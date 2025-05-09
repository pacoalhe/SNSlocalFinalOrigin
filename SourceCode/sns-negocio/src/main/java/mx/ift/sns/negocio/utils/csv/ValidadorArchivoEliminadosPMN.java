package mx.ift.sns.negocio.utils.csv;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVReader;

import mx.ift.sns.negocio.ng.model.ResultadoValidacionArrendamiento;
import mx.ift.sns.negocio.ng.model.ResultadoValidacionCSV;
import mx.ift.sns.negocio.pnn.ProcesarRegistrosElimindosPlanMaestro;
import mx.ift.sns.utils.date.FechasUtils;

/**
 * Validador de archivos csv.
 */

public class ValidadorArchivoEliminadosPMN {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidadorArchivoEliminadosPMN.class);

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
     * 
     * @param headers cabeceras
     * @return true si es correcto, false eoc
     * @throws IOException error leyendo
     */
    public boolean checkHeader(String[] headers) throws IOException {

	return true;
    }

    /**
     * Valida una fila del archivo.
     * 
     * @param valores d
     */
    public void checkFila(String[] valores) {

    }

    /**
     * Valida el fichero de rangos de numeracion del pst arrendador.
     * 
     * @param fileName nombre del archivo
     * @return res resultado de la validacion
     * @throws Exception error
     */
    public ResultadoValidacionCSV validar(String fileName) throws Exception {

	LOGGER.debug("fichero {}", fileName);

	totalFilas = 0;

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

	try {

	    res.setError(ResultadoValidacionArrendamiento.VALIDACION_OK);
	    FileReader freader = null;
	    freader = new FileReader(fileName);
	    cvsReader = new CSVReader(freader, DELIMITADOR, '"', 1);
	    List<String[]> allRows = cvsReader.readAll();
	    LOGGER.info("-----------------Tamaño del fichero csvdeleted " + allRows.size());

	    int cpus = Runtime.getRuntime().availableProcessors();

	    if (allRows.size() > 0) {
		LOGGER.info("<--Inicia el recorido del csv {}", FechasUtils.getActualDate());
		ExecutorService executorServiceCancelados = Executors.newFixedThreadPool(cpus);
		for (String[] row : allRows) {
		    Runnable worker = new ProcesarRegistrosElimindosPlanMaestro(row);
		    executorServiceCancelados.execute(worker);
		    // checkFila(row);

		}
		executorServiceCancelados.shutdown();
		while (!executorServiceCancelados.isTerminated()) {
		}
		LOGGER.info("<---Finished all threads---->");
		LOGGER.info("<--finaliza el recorido del csv {}", FechasUtils.getActualDate());
	    } else {
		/* fichero vacio. */
		LOGGER.debug("fichero vacio");
		res.setError(ResultadoValidacionArrendamiento.ERROR_FICHERO_VACIO);
	    }

	} catch (Exception e) {
	    /* fichero incorrecto. */
	    res.setError(ResultadoValidacionArrendamiento.ERROR_FICHERO);

	    LOGGER.error("Error validando archivo {}", fileName, e);
	} finally {
	    if (cvsReader != null) {
		cvsReader.close();
	    }
	}

	LOGGER.debug("fin validacion {} filas={} res={}", fileName, totalFilas, res.getError());

	return res;
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
