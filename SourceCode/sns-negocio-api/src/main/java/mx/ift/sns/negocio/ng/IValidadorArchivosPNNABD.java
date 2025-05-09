package mx.ift.sns.negocio.ng;

import mx.ift.sns.negocio.ng.model.ResultadoValidacionArrendamiento;

/**
 * Generador de plan de numeracion de ABD Presuscripcion.
 */
public interface IValidadorArchivosPNNABD {

    /**
     * Generar el plan de ABD Presuscripcion si es posible.
     * @param ficheroArrendatario fichero PST arrendatario
     * @param ficheroArrendador fichero PST arrendador
     * @return res resultado de la validacion
     */
    ResultadoValidacionArrendamiento validar(String ficheroArrendatario, String ficheroArrendador);

    /**
     * Genera tablas reporte ABD.
     */
    void generarTablas();
}
