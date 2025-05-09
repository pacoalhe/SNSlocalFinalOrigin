package mx.ift.sns.dao.nng;

import java.util.List;

import mx.ift.sns.dao.IBaseDAO;
import mx.ift.sns.modelo.nng.ClaveServicio;
import mx.ift.sns.modelo.nng.NumeracionAsignadaNng;
import mx.ift.sns.modelo.nng.NumeracionSolicitadaNng;
import mx.ift.sns.modelo.nng.SolicitudAsignacionNng;
import mx.ift.sns.modelo.pst.Proveedor;

/**
 * Interfaz del DAO de numeracion asignada.
 * @author X36155QU
 */
public interface INumeracionAsignadaNngDao extends IBaseDAO<NumeracionAsignadaNng> {

    /**
     * Recupera un historico numeraciones asignadas por clave de servicio.
     * @param proveedor proveedor
     * @param listaClaves claves
     * @return historico
     */
    List<Object[]> findAllNumeracionAsignadaByPstGroupByClave(Proveedor proveedor, List<ClaveServicio> listaClaves);

    /**
     * Obtiene las numeraciones asignadas en una clave de servicio por año.
     * @param clave servicio
     * @param proveedor solicitante
     * @return lista
     */
    List<Object[]> findAllNumeracionAsignadaByClaveGroupByAnio(String clave, Proveedor proveedor);

    /**
     * Obtiene las numeraciones asignadas por la solicitud de asignacion.
     * @param solicitud asignacion
     * @return numeraciones asignadas
     */
    List<NumeracionAsignadaNng> findAllNumeracionAsignadaBySolicitud(SolicitudAsignacionNng solicitud);

    /**
     * Obtiene las numeraciones asignadas asociadas a una numeración solicitada.
     * @param numSol numeracion solicitada
     * @return numeraciones asignadas
     */
    List<NumeracionAsignadaNng> findAllNumeracionAsignadaBySolicitada(NumeracionSolicitadaNng numSol);

}
