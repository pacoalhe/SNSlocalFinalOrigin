package mx.ift.sns.negocio.ng;

import mx.ift.sns.negocio.ng.model.ResultadoValidacionArrendamiento;

/**
 * Generador de plan de numeracion de ABD Presuscripcion.
 */
public interface IPlanNumeracionABDService {

    /**
     * Método encargado de procesar la generación de planes ABD de los archivos de arrendador y arrendatario.
     * @param arrendadorUrl ruta del fichero de arrendador
     * @param arrendatarioUrl ruta fichero de arrendatario
     * @param arrendadorNombre nombre del fichero de arrendador
     * @param arrendatarioNombre nombre del fichero de arrendatario
     * @return ResultadoValidacionArrendamienton resultado
     */
    ResultadoValidacionArrendamiento procesarFicherosAbd(String arrendadorUrl, String arrendatarioUrl,
            String arrendadorNombre, String arrendatarioNombre);

    /** Genera tablas reporte ABD. */
    void generarRegistrosAbd();
}
