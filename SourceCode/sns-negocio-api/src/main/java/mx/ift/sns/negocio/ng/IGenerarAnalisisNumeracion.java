package mx.ift.sns.negocio.ng;

import java.util.List;

import mx.ift.sns.modelo.ng.SolicitudAsignacion;
import mx.ift.sns.modelo.nng.NumeracionSolicitadaNng;
import mx.ift.sns.modelo.pst.Proveedor;

/**
 * Interfaz del servicio de Generar Analisis Numeración.
 */
public interface IGenerarAnalisisNumeracion {

    /**
     * Genera el analis de la numeracion geográfica de una solicitud de asignacion NG.
     * @param solicitud asignacion
     * @return byte[] xls file
     * @throws Exception error
     */
    byte[] generarAnalisisNumeracion(SolicitudAsignacion solicitud) throws Exception;

    /**
     * Genera el analis de la numeracion no geográfica de un proveedor.
     * @param proveedor Proveedor
     * @param numeracionesSolicitadas numeraciones
     * @return byte[] xls file
     * @throws Exception error
     */
    byte[] generarAnalisisNumeracionNng(Proveedor proveedor, List<NumeracionSolicitadaNng> numeracionesSolicitadas);

}
