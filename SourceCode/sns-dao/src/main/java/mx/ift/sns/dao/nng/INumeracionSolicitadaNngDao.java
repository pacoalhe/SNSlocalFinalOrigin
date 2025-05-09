package mx.ift.sns.dao.nng;

import java.util.List;

import mx.ift.sns.dao.IBaseDAO;
import mx.ift.sns.modelo.nng.NumeracionSolicitadaNng;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.solicitud.Solicitud;

/**
 * Interfaz del DAO de NumeracionSolicitadaNng.
 * @author X36155QU
 */
public interface INumeracionSolicitadaNngDao extends IBaseDAO<NumeracionSolicitadaNng> {

    /**
     * Obtiene todo a los que que se ha solicitado asignación de numeración por solicitud de asignación.
     * @param sol solicitud
     * @return lista
     */
    List<String> findAllClientesBySolicitud(Solicitud sol);

    /**
     * Comprueba si una numeracion solicitada tiene rango asociados.
     * @param numeracionSolicitada NumeracionSolicitadaNng
     * @return true/false
     */
    boolean isNumeracionSolicitadaWithRangos(NumeracionSolicitadaNng numeracionSolicitada);

    /**
     * Obtiene la numeraciones asignadas en una clave de servicio por año.
     * @param clave servicio
     * @param proveedor solicitante
     * @return totales
     */
    Integer getTotalNumeracionesSolicitadasByClave(String clave, Proveedor proveedor);

}
