package mx.ift.sns.negocio.utils.csv;


import com.opencsv.CSVReader;
import mx.ift.sns.modelo.port.NumeroCancelado;
import mx.ift.sns.negocio.ng.model.ResultadoValidacionArrendamiento;
import mx.ift.sns.negocio.ng.model.ResultadoValidacionCSV;
import mx.ift.sns.negocio.port.CanceladosDAO;
import mx.ift.sns.negocio.port.NumeroCanceladoRequestDTO;
import mx.ift.sns.negocio.port.NumeroRequestDTO;
import mx.ift.sns.negocio.port.ProcesarRegistrosCancelados;
import mx.ift.sns.utils.date.FechasUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Validador de archivos csv.
 */

@Stateless
public class ValidadorArchivoDeletedCSV2 {

    //FJAH 26052025
    public ValidadorArchivoDeletedCSV2() {
        // Constructor vacío requerido por EJB
    }

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
    public NumeroCanceladoRequestDTO checkFila(String[] valores) {
        // portId,portType,action,numberFrom,numberTo,isMpp,rida,rcr,dida,dcr,actionDate
        if (valores.length >= 1) {

            NumeroCanceladoRequestDTO num = new NumeroCanceladoRequestDTO();
            num.setPortId(StringUtils.trim(valores[0]));
            num.setPortType(StringUtils.trim(valores[1]).charAt(0));
            num.setAction(StringUtils.trim(valores[2]));
            num.setNumberFrom(StringUtils.trim(valores[3]));
            num.setNumberTo(StringUtils.trim(valores[4]));
            num.setIsMpp(StringUtils.trim(valores[5]).charAt(0));
            num.setRida(new BigDecimal(StringUtils.trim(valores[6])));
            num.setRcr(new BigDecimal(StringUtils.trim(valores[7])));
            num.setDida(!StringUtils.trim(valores[8]).equals("null") ? new BigDecimal(StringUtils.trim(valores[8])) : null);
            num.setDcr(!StringUtils.trim(valores[9]).equals("null") ? new BigDecimal(StringUtils.trim(valores[9])) : null);
            num.setActionDate(StringUtils.trim(valores[10]));
            num.setAssigneeCr(new BigDecimal(StringUtils.trim(valores[12])));
            num.setAssigneeIda(new BigDecimal(StringUtils.trim(valores[11])));

            return (num);
        }
        return null;
    }

    /**
     * Valida el fichero de rangos de numeracion del pst arrendador.
     * @param fileName nombre del archivo
     * @return res resultado de la validacion
     * @throws Exception error
     */
    //FJAH 26052025 Refactorizacion
    public Collection<NumeroCanceladoRequestDTO> validar(String fileName) throws Exception {
        LOGGER.debug("fichero {}", fileName);
        totalFilas = 0;

        File f1 = new File(fileName);
        if (StringUtils.isEmpty(fileName) || !f1.exists()) {
            LOGGER.debug("nombre fichero vacio o no existe");
            res.setError(ResultadoValidacionArrendamiento.ERROR_FICHERO);
            return Collections.emptyList();
        }

        try (FileReader freader = new FileReader(fileName);
             CSVReader cvsReader = new CSVReader(freader, DELIMITADOR,'"',1)) {

            res.setError(ResultadoValidacionArrendamiento.VALIDACION_OK);
            List<String[]> allRows = cvsReader.readAll();
            LOGGER.debug("---------------------------tamaño del fichero {}", allRows.size());

            Collection<NumeroCanceladoRequestDTO> list = new ArrayList<>();
            if (!allRows.isEmpty()) {
                for (String[] row : allRows) {
                    NumeroCanceladoRequestDTO dto = checkFila(row);
                    if (dto != null) list.add(dto); // Evita nulos
                }
                LOGGER.info("<---Finished all threads---->");
            } else {
                LOGGER.debug("fichero vacio");
                res.setError(ResultadoValidacionArrendamiento.ERROR_FICHERO_VACIO);
            }
            LOGGER.debug("fin validacion {} filas={} res={}", fileName, totalFilas, res.getError());
            return list;
        } catch (Exception e) {
            res.setError(ResultadoValidacionArrendamiento.ERROR_FICHERO);
            LOGGER.error("Error validando archivo {}", fileName, e);
            return Collections.emptyList();
        }
    }
    /*
    public Collection<NumeroCanceladoRequestDTO> validar(String fileName) throws Exception {

        LOGGER.debug("fichero {}", fileName);

        totalFilas = 0;

        if (StringUtils.isEmpty(fileName)) {
            LOGGER.debug("nombre fichero vacio");
            res.setError(ResultadoValidacionArrendamiento.ERROR_FICHERO);

            return null;
        }

        File f1 = new File(fileName);
        if (!f1.exists()) {
            LOGGER.debug("fichero no existe");
            res.setError(ResultadoValidacionArrendamiento.ERROR_FICHERO);
            return null;
        }

        try {

            res.setError(ResultadoValidacionArrendamiento.VALIDACION_OK);
            FileReader freader = null;
            freader = new FileReader(fileName);
            cvsReader = new CSVReader(freader, DELIMITADOR,'"',1);
            List<String[]> allRows = cvsReader.readAll();
            LOGGER.info("-----------------Tamaño del fichero csvdeleted "+ allRows.size() );

            if (allRows.size() > 0) {
                LOGGER.info("<--Inicia el recorido del csv {}", FechasUtils.getActualDate());
                Collection<NumeroCanceladoRequestDTO> list = new ArrayList<NumeroCanceladoRequestDTO>();
                for (String[] row : allRows) {
                    list.add(checkFila(row));
                }
                LOGGER.info("<---Finished all threads---->");
                LOGGER.info("<--finaliza el recorido del csv {}",FechasUtils.getActualDate());
                return list;
            }
            else
            {
                // fichero vacio. //
                LOGGER.debug("fichero vacio");
                res.setError(ResultadoValidacionArrendamiento.ERROR_FICHERO_VACIO);
            }

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

        return null;
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
