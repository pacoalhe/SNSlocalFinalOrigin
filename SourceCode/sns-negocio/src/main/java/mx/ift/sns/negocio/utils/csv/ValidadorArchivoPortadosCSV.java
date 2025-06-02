package mx.ift.sns.negocio.utils.csv;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


import mx.ift.sns.modelo.port.NumeroPortado;
import mx.ift.sns.negocio.ng.model.ResultadoValidacionArrendamiento;
import mx.ift.sns.negocio.ng.model.ResultadoValidacionCSV;
import mx.ift.sns.negocio.port.PortadosDAO;
import mx.ift.sns.negocio.port.ProcesarRegistrosCancelados;
import mx.ift.sns.negocio.port.ProcesarRegistrosPortados;
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
public class ValidadorArchivoPortadosCSV {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidadorArchivoPortadosCSV.class);

    //FJAH 26052025
    public ValidadorArchivoPortadosCSV() {
        // Constructor vacío requerido por EJB
    }

    final int BATCH_SIZE = 5000;

    //FJAH 26052025
    @EJB
    private PortadosDAO portadosDAO;

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
    private ProcesarRegistrosPortados procesadorPortados;

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

    //FJAH 26052025 Refactorización del metodo
    public ResultadoValidacionCSV validar(String fileName) throws Exception {
        LOGGER.debug("fichero {}", fileName);

        int successCount = 0;
        String actionDateLote = null; // O Timestamp si ya lo conviertes ahí mismo

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

        try (FileReader freader = new FileReader(fileName)) {
            res.setError(ResultadoValidacionArrendamiento.VALIDACION_OK);
            cvsReader = new CSVReader(freader, DELIMITADOR, '"', 1);

            List<String[]> allRows = cvsReader.readAll();
            LOGGER.debug("---------------------------tamaño del fichero csvportados " + allRows.size());

            if (!allRows.isEmpty()) {
                LOGGER.info("<--Inicia el recorrido del csv {}", FechasUtils.getActualDate());

                List<NumeroPortado> batch = new ArrayList<>();

                for (String[] row : allRows) {
                    NumeroPortado n = mapeaRowANumeroPortado(row);

                    if (actionDateLote == null && n.getActionDate() != null) {
                        actionDateLote = String.valueOf(n.getActionDate());
                    }

                    batch.add(n);
                    if (batch.size() == BATCH_SIZE) {
                        int procesados = portadosDAO.upsertBatch(batch);
                        LOGGER.info("Batch de {} procesados (insert/update)", procesados);
                        batch.clear();
                    }
                }
                // Procesa los registros restantes si no llegan al tamaño de batch
                if (!batch.isEmpty()) {
                    int procesados = portadosDAO.upsertBatch(batch);
                    LOGGER.info("Batch final de {} procesados (insert/update)", procesados);
                }

                LOGGER.info("<---Batch processing terminado---->");
                LOGGER.info("Procesados exitosamente: {}", successCount);

            } else {
                LOGGER.debug("fichero vacio");
                res.setError(ResultadoValidacionArrendamiento.ERROR_FICHERO_VACIO);
            }
        } finally {
            if (cvsReader != null) {
                cvsReader.close();
            }
        }

        res.setActionDateLote(actionDateLote);
        LOGGER.info("actionDateLote que se setea en resultado: {}", actionDateLote);
        LOGGER.info("VALOR DE res.getActionDateLote(actionDateLote) SETEADO: {}", res.getActionDateLote());

        LOGGER.debug("fin validacion {} filas={} res={}", fileName, totalFilas, res.getError());
        LOGGER.info("RESUMEN FINAL PORTADOS: Exitosos = {}", successCount);

        return res;
    }

    // Mapear fila CSV a NumeroPortado
    private NumeroPortado mapeaRowANumeroPortado(String[] valores) {
        NumeroPortado num = new NumeroPortado();
        num.setPortId(StringUtils.trim(valores[0]));
        num.setPortType(StringUtils.trim(valores[1]));
        num.setAction(StringUtils.trim(valores[2]));
        num.setNumberFrom(StringUtils.trim(valores[3]));
        num.setNumberTo(StringUtils.trim(valores[4]));
        num.setIsMpp(StringUtils.trim(valores[5]));
        num.setRida(new BigDecimal(valores[6]));
        num.setRcr(new BigDecimal(valores[7]));
        num.setDida(!valores[8].equals("null") ? new BigDecimal(valores[8]) : null);
        num.setDcr(!valores[9].equals("null") ? new BigDecimal(valores[9]) : null);
        num.setActionDate(StringUtils.trim(valores[10]));
        return num;
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

        try (FileReader freader = new FileReader(fileName)) {

            res.setError(ResultadoValidacionArrendamiento.VALIDACION_OK);
            //FileReader freader = null;
            //freader = new FileReader(fileName);
            cvsReader = new CSVReader(freader, DELIMITADOR,'"',1);

            List<String[]> allRows = cvsReader.readAll();
            LOGGER.debug("---------------------------tamaño del fichero csvportados "+allRows.size());
            //int cpus = Runtime.getRuntime().availableProcessors();

            if (!allRows.isEmpty()) {
                LOGGER.info("<--Inicia el recorido del csv {}", FechasUtils.getActualDate());

                /*
                List<Future<Boolean>> futures = new ArrayList<>();
                for (String[] row : allRows) {
                    futures.add(procesadorPortados.procesarAsync(row));
                }

                 */
/*
                for (final String[] row : allRows) {
                    futures.add(executor.submit(new Callable<Boolean>() {
                        @Override
                        public Boolean call() {
                            try {
                                // Aquí llamas tu método asíncrono y esperas el resultado
                                return procesadorPortados.procesarAsync(row).get();
                            } catch (Exception e) {
                                LOGGER.error("Error procesando registro", e);
                                return false;
                            }
                        }
                    }));
                }

                // Espera a que todos terminen (importante)
                for (Future<Boolean> f : futures) {
                    try {
                        Boolean ok = f.get();
                        if (Boolean.TRUE.equals(ok)) {
                            successCount++;
                        } else {
                            failCount++;
                        }
                        //f.get(); // espera a que termine, puedes manejar el resultado
                    } catch (Exception e) {
                        failCount++;
                        LOGGER.error("Error esperando procesamiento asíncrono", e);
                    }
                }
                LOGGER.info("<---Finished all threads---->");
                LOGGER.info("Procesados exitosamente: {} / Fallidos: {}", successCount, failCount);
            } else {
                LOGGER.debug("fichero vacio");
                res.setError(ResultadoValidacionArrendamiento.ERROR_FICHERO_VACIO);
            }

            /*
            if (allRows.size() > 0) {

                //ExecutorService executorServicePortados=Executors.newFixedThreadPool(cpus);
                for (String[] row : allRows) {
                    //FJAH 24052025
                    //Runnable worker=new ProcesarRegistrosPortados(row);
                    //executorServicePortados.execute(worker);
                    //checkFila(row);
                    //executorServicePortados.execute(worker);
                    procesadorPortados.procesarAsync(row);

                }
                //FJAH 24052025
                //executorServicePortados.shutdown();
                //executorServicePortados.awaitTermination(30, TimeUnit.MINUTES);
                //while (!executorServicePortados.isTerminated()) {}
                LOGGER.info("<---Finished all threads---->");

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
 */
    /*

        } finally {
            executor.shutdown();
            if (cvsReader != null) {
                cvsReader.close();
            }
        }

        LOGGER.debug("fin validacion {} filas={} res={}", fileName, totalFilas, res.getError());

        // Resumen al resultado si lo requieres
        LOGGER.info("RESUMEN FINAL PORTADOS: Exitosos = {}, Fallidos = {}", successCount, failCount);

        return res;
    }

     */

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
