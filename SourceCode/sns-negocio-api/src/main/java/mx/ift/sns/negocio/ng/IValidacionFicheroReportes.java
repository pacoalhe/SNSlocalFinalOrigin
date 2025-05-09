package mx.ift.sns.negocio.ng;

import java.io.File;

import mx.ift.sns.negocio.ng.model.RetornoProcesaFicheroReportes;
import mx.ift.sns.negocio.nng.RetornoProcesaFicheroReportesNng;

/**
 * Interfaz.
 * @author X36155QU
 */
public interface IValidacionFicheroReportes {

    /**
     * Método que procesa un fichero de reporte de lineas activas.
     * @param fichero fichero
     * @return RetornoProcesaFichero RetornoProcesaFichero
     * @throws Exception Exception
     */
    RetornoProcesaFicheroReportes validarLineasActivas(File fichero) throws Exception;

    /**
     * Valida el reporte de lineas activas detallado.
     * @param fichero File
     * @return RetornoProcesaFicheroReportes
     * @throws Exception error
     */
    RetornoProcesaFicheroReportes validarFicheroLineasActivasDet(File fichero) throws Exception;

    /**
     * Valida el reporte de lineas activas arrendatario.
     * @param fichero File
     * @return RetornoProcesaFicheroReportes
     * @throws Exception error
     */
    RetornoProcesaFicheroReportes validarFicheroLineasArrendatario(File fichero) throws Exception;

    /**
     * Valida el reporte de lineas arrendadas.
     * @param fichero file
     * @return RetornoProcesaFicheroReportes
     * @throws Exception error
     */
    RetornoProcesaFicheroReportes validarFicheroLineasArrendada(File fichero) throws Exception;

    /**
     * Método que procesa un fichero de reporte de lineas activas no geografica.
     * @param fichero fichero
     * @return RetornoProcesaFichero RetornoProcesaFichero
     * @throws Exception Exception
     */
    RetornoProcesaFicheroReportesNng validarLineasActivasNng(File fichero) throws Exception;

    /**
     * Método que procesa un fichero de reporte de lineas activas no geografica.
     * @param fichero fichero
     * @return RetornoProcesaFichero RetornoProcesaFichero
     * @throws Exception Exception
     */
    RetornoProcesaFicheroReportesNng validarLineasActivasDetNng(File fichero) throws Exception;

    /**
     * Valida un reporte de lineas activa de arrendatario.
     * @param fichero fichero
     * @return reportes
     * @throws Exception error
     */
    RetornoProcesaFicheroReportesNng validarFicheroLineasArrendatarioNng(File fichero) throws Exception;

    /**
     * Valida un reporte de lineas activa de arrendador.
     * @param fichero fichero
     * @return reportes
     * @throws Exception error
     */
    RetornoProcesaFicheroReportesNng validarFicheroLineasArrendadaNng(File fichero) throws Exception;

}
