package mx.ift.sns.negocio.ng;

import java.io.File;

import mx.ift.sns.negocio.ng.model.RetornoProcesaFicheroAsignacion;

/**
 * Interfaz.
 */
public interface IValidacionFicheroAsignacionAutomatica {

    /**
     * Método que procesa un fichero.
     * @param fichero fichero
     * @return RetornoProcesaFichero RetornoProcesaFichero
     * @throws Exception Exception
     */
    RetornoProcesaFicheroAsignacion validar(File fichero) throws Exception;

}
