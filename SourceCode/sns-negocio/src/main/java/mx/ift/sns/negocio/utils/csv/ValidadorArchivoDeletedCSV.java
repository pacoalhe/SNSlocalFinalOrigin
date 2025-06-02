package mx.ift.sns.negocio.utils.csv;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


import mx.ift.sns.modelo.port.NumeroCancelado;
import mx.ift.sns.negocio.ng.model.ResultadoValidacionArrendamiento;
import mx.ift.sns.negocio.ng.model.ResultadoValidacionCSV;
import mx.ift.sns.negocio.port.CanceladosDAO;
import mx.ift.sns.negocio.port.ProcesarRegistrosCancelados;
import mx.ift.sns.utils.date.FechasUtils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVReader;

import javax.ejb.EJB;
import javax.ejb.Stateless;


/**
 * Validador de archivos csv.
 */

@Stateless
public class ValidadorArchivoDeletedCSV {

    //FJAH 26052025
    public ValidadorArchivoDeletedCSV() {
        // Constructor vacío requerido por EJB
    }

    final int BATCH_SIZE = 2500;

    @EJB
    private CanceladosDAO canceladosDAO;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidadorArchivoDeletedCSV.class);

    /** Delimitador del fichero CSV. */
    private static final char DELIMITADOR = ',';

    /** Lector de ficheros en formato CSV. */
    private CSVReader cvsReader = null;

    /** Resultado de la validacion. */
    private ResultadoValidacionCSV res = new ResultadoValidacionCSV();

    /** Indica si se ha encontrado un error. */
    private boolean hayError = false;

    /** Indica si se ha de terminar la lectura de las filas. */
    private boolean terminar = false;

    /** Numero total de filas del archivo, sin la cabecera. */
    private int totalFilas = 0;

    //FJAH 24052025
    @EJB
    private ProcesarRegistrosCancelados procesadorCancelados;


    /**
     * Comprueba la cabecera del fichero.
     * @param headers cabeceras
     * @return true si es correcto, false eoc
     * @throws IOException error leyendo
     */
    public boolean checkHeader(String[] headers) throws IOException {

        return true;
    }

    /**
     * Valida una fila del archivo.
     * @param valores d
     */
    public void checkFila(String[] valores) {

    }

    /**
     * Valida el fichero de rangos de numeracion del pst arrendador.
     * @param fileName nombre del archivo
     * @return res resultado de la validacion
     * @throws Exception error
     */

    //FJAH 26052025 Refactorizacion por lotes
    public ResultadoValidacionCSV validar(String fileName) throws Exception {
        LOGGER.debug("fichero {}", fileName);

        ResultadoValidacionCSV res = new ResultadoValidacionCSV();
        int totalFilas = 0;
        int successCount = 0;
        int failCount = 0;
        //final int BATCH_SIZE = 2500;
        List<NumeroCancelado> lote = new ArrayList<>(BATCH_SIZE);

        if (StringUtils.isEmpty(fileName)) {
            LOGGER.debug("nombre fichero vacio");
            res.setError(ResultadoValidacionArrendamiento.ERROR_FICHERO);
            return res;
        }

        File f1 = new File(fileName);
        if (!f1.exists()) {
            LOGGER.debug("fichero no existe");
            res.setError(ResultadoValidacionArrendamiento.ERROR_FICHERO);
            return res;
        }

        try (FileReader freader = new FileReader(fileName);
             CSVReader cvsReader = new CSVReader(freader, DELIMITADOR, '"', 1)) {

            res.setError(ResultadoValidacionArrendamiento.VALIDACION_OK);

            List<String[]> allRows = cvsReader.readAll();
            LOGGER.info("-----------------Tamaño del fichero csvdeleted {}", allRows.size());

            if (!allRows.isEmpty()) {
                LOGGER.info("<--Inicia el recorrido del csv {}", FechasUtils.getActualDate());

                for (String[] row : allRows) {
                    totalFilas++;
                    // Validación básica
                    if (row == null || row.length < 13) {
                        LOGGER.error("Fila incompleta (esperado 13 columnas, recibido: {}). Fila omitida: {}", (row == null ? 0 : row.length), Arrays.toString(row));
                        failCount++;
                        continue;
                    }
                    // Mapeo según tu método checkFila/mapRowANumeroCancelado
                    NumeroCancelado num = mapRowANumeroCancelado(row);
                    if (num != null) {
                        lote.add(num);
                    }
                    // Cuando el lote llega a 500, procesa
                    if (lote.size() == BATCH_SIZE) {
                        int procesados = canceladosDAO.procesarCanceladosBatch(lote);
                        LOGGER.info("Batch de {} procesados (delete/insert)", procesados);
                        successCount += procesados;
                        lote.clear();
                    }
                }
                // Procesa el último lote pendiente
                if (!lote.isEmpty()) {
                    int procesados = canceladosDAO.procesarCanceladosBatch(lote);
                    LOGGER.info("Batch final de {} procesados (delete/insert)", procesados);
                    successCount += procesados;
                    lote.clear();
                }
            } else {
                LOGGER.debug("fichero vacio");
                res.setError(ResultadoValidacionArrendamiento.ERROR_FICHERO_VACIO);
            }
        } catch (Exception e) {
            res.setError(ResultadoValidacionArrendamiento.ERROR_FICHERO);
            LOGGER.error("Error validando archivo {}", fileName, e);
        }

        LOGGER.info("RESUMEN FINAL CANCELADOS: Exitosos = {}, Fallidos = {}, Total filas = {}", successCount, failCount, totalFilas);

        return res;
    }

    private NumeroCancelado mapRowANumeroCancelado(String[] valores) {
        try {
            if (valores == null || valores.length < 13) {
                return null;
            }
            NumeroCancelado num = new NumeroCancelado();
            num.setPortId(StringUtils.trimToEmpty(valores[0]));
            num.setPortType(StringUtils.trimToEmpty(valores[1]));
            num.setAction(StringUtils.trimToEmpty(valores[2]));
            num.setNumberFrom(StringUtils.trimToEmpty(valores[3]));
            num.setNumberTo(StringUtils.trimToEmpty(valores[4]));
            num.setIsMpp(StringUtils.defaultIfBlank(StringUtils.trim(valores[5]), "N"));
            num.setRida(parseBigDecimalSafe(valores[6]));
            num.setRcr(parseBigDecimalSafe(valores[7]));
            num.setDida(parseBigDecimalSafe(valores[8]));
            num.setDcr(parseBigDecimalSafe(valores[9]));
            String dateStr = StringUtils.trimToNull(valores[10]);
            try {
                if (dateStr != null) {
                    SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                    Date parsedDate = sourceFormat.parse(dateStr);
                    num.setActionDate(new java.sql.Timestamp(parsedDate.getTime()));
                } else {
                    num.setActionDate(new java.sql.Timestamp(System.currentTimeMillis()));
                }
            } catch (Exception e) {
                num.setActionDate(new java.sql.Timestamp(System.currentTimeMillis()));
            }
            num.setAssigneeIda(parseBigDecimalSafe(valores[11]));
            num.setAssigneeCr(parseBigDecimalSafe(valores[12]));
            return num;
        } catch (Exception ex) {
            LOGGER.error("Error mapeando fila: {}", Arrays.toString(valores), ex);
            return null;
        }
    }

    private BigDecimal parseBigDecimalSafe(String s) {
        try {
            if (s == null || s.trim().isEmpty() || "null".equalsIgnoreCase(s.trim())) return null;
            return new BigDecimal(s.trim());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @return the hayError
     */
    public boolean isHayError() {
        return hayError;
    }

    /**
     * @param hayError the hayError to set
     */
    public void setHayError(boolean hayError) {
        this.hayError = hayError;
    }

    /**
     * @return the res
     */
    public ResultadoValidacionCSV getRes() {
        return res;
    }

    /**
     * @param res the res to set
     */
    public void setRes(ResultadoValidacionCSV res) {
        this.res = res;
    }

    /**
     * @return the totalFilas
     */
    public int getTotalFilas() {
        return totalFilas;
    }

    /**
     * @param totalFilas the totalFilas to set
     */
    public void setTotalFilas(int totalFilas) {
        this.totalFilas = totalFilas;
    }

    /**
     * @return the terminar
     */
    public boolean isTerminar() {
        return terminar;
    }

    /**
     * @param terminar the terminar to set
     */
    public void setTerminar(boolean terminar) {
        this.terminar = terminar;
    }
}

/*
public ResultadoValidacionCSV validar(String fileName) throws Exception {

        LOGGER.debug("fichero {}", fileName);

        totalFilas = 0;
        int successCount = 0;
        int failCount = 0;
        int POOL_SIZE = 8; // O ajústalo según tu infra

        if (StringUtils.isEmpty(fileName)) {
            LOGGER.debug("nombre fichero vacio");
            res.setError(ResultadoValidacionArrendamiento.ERROR_FICHERO);

            return res;
        }

        File f1 = new File(fileName);
        if (!f1.exists()) {
            LOGGER.debug("fichero no existe");
            res.setError(ResultadoValidacionArrendamiento.ERROR_FICHERO);
            return res;
        }

        ExecutorService executor = Executors.newFixedThreadPool(POOL_SIZE);
        List<Future<Boolean>> futures = new ArrayList<>();

        try (FileReader freader = new FileReader(fileName)){

            res.setError(ResultadoValidacionArrendamiento.VALIDACION_OK);
            //FileReader freader = null;
            //freader = new FileReader(fileName);
            cvsReader = new CSVReader(freader, DELIMITADOR,'"',1);
            List<String[]> allRows = cvsReader.readAll();
            LOGGER.info("-----------------Tamaño del fichero csvdeleted "+ allRows.size() );

            //int cpus = Runtime.getRuntime().availableProcessors();

            if (!allRows.isEmpty()) {
                LOGGER.info("<--Inicia el recorrido del csv {}", FechasUtils.getActualDate());

                for (final String[] row : allRows) {
                    futures.add(executor.submit(new Callable<Boolean>() {
                        @Override
                        public Boolean call() {
                            try {
                                // Aquí llamas tu método asíncrono y esperas el resultado
                                return procesadorCancelados.procesarAsync(row).get();
                            } catch (Exception e) {
                                LOGGER.error("Error procesando registro", e);
                                return false;
                            }
                        }
                    }));
                }

                /*
                List<Future<Boolean>> futures = new ArrayList<>();
                for (String[] row : allRows) {
                    futures.add(procesadorCancelados.procesarAsync(row));
                }
                 */
/*
// Espera a que todos terminen (importante)
                for (Future<Boolean> f : futures) {
        try {
        Boolean ok = f.get(); // Espera a que termine
        if (Boolean.TRUE.equals(ok)) {
        successCount++;
        } else {
        failCount++;
        }
        //f.get(); // espera a que termine, puedes manejar el resultado
        } catch (Exception e) {
        LOGGER.error("Error esperando procesamiento asíncrono", e);
        LOGGER.info("Procesados exitosamente: {} / Fallidos: {}", successCount, failCount);
        }
        }
        LOGGER.info("<---Finished all threads---->");
        } else {
        LOGGER.debug("fichero vacio");
        res.setError(ResultadoValidacionArrendamiento.ERROR_FICHERO_VACIO);
        }

            /*
            if (allRows.size() > 0) {
            	LOGGER.info("<--Inicia el recorido del csv {}",FechasUtils.getActualDate());
            	//ExecutorService executorServiceCancelados=Executors.newFixedThreadPool(cpus);
				for (String[] row : allRows) {
                    //FJAH 24052025
					//Runnable worker=new ProcesarRegistrosCancelados(row);
					//executorServiceCancelados.execute(worker);
					//checkFila(row);
                    procesadorCancelados.procesarAsync(row);

				}
                //FJAH 24052025
				//executorServiceCancelados.shutdown();
				//while (!executorServiceCancelados.isTerminated()) {}
				LOGGER.info("<---Finished all threads---->");
				LOGGER.info("<--finaliza el recorido del csv {}",FechasUtils.getActualDate());
                }
            else
            {
                // fichero vacio. //
                LOGGER.debug("fichero vacio");
                res.setError(ResultadoValidacionArrendamiento.ERROR_FICHERO_VACIO);
            }

             */

/*
        } catch (Exception e) {
        // fichero incorrecto. //
        res.setError(ResultadoValidacionArrendamiento.ERROR_FICHERO);

        LOGGER.error("Error validando archivo {}", fileName, e);
        } finally {
        if (cvsReader != null) {
        cvsReader.close();
        }
        }

        LOGGER.debug("fin validacion {} filas={} res={}", fileName, totalFilas, res.getError());
        LOGGER.info("RESUMEN FINAL CANCELADOS: Exitosos = {}, Fallidos = {}", successCount, failCount);

        return res;
        }

 */


