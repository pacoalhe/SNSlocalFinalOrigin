package mx.ift.sns.dao.ng;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.dao.IBaseDAO;
import mx.ift.sns.modelo.ng.NumeracionSolicitada;
import mx.ift.sns.modelo.ng.SolicitudAsignacion;
import mx.ift.sns.modelo.pst.Proveedor;

/**
 * Interfaz de definición de los métodos para base de numeraciones solicitadas.
 */
public interface INumeracionSolicitadaDAO extends IBaseDAO<NumeracionSolicitada> {

    /**
     * Recupera las numeraciones solicitadas de una solicitud.
     * @param codSol Codigo de solicitud
     * @return Lista de numeraciones solicitadas
     */
    List<NumeracionSolicitada> getNumSolicitada(BigDecimal codSol);

    /**
     * Obtiene el total de numeraciones solicitadas por un PST por poblacion.
     * @param tipoRed tipoRed
     * @param tipoModalidad tipoModalidad
     * @param poblacion poblacion
     * @param proveedor proveedor
     * @return total
     */
    BigDecimal getTotalNumSolicitadasByPoblacion(String tipoRed, String tipoModalidad, String poblacion,
            Proveedor proveedor);

    /**
     * Obtiene el total de numeraciones solicitadas por un PST por ABN.
     * @param tipoRed tipoRed
     * @param tipoModalidad tipoModalidad
     * @param abn abn
     * @param proveedor proveedor
     * @return total
     */
    BigDecimal getTotalNumSolicitadasByAbn(String tipoRed, String tipoModalidad, BigDecimal abn,
            Proveedor proveedor);

    /**
     * Obtiene la numeraciones asignadas en una poblacion por año.
     * @param poblacion inege
     * @param proveedor solicitante
     * @return totales
     */
    List<Object[]> findAllNumeracionAsignadaByPoblacionGroupByAnio(String poblacion, Proveedor proveedor);

    /**
     * Obtiene la numeraciones asignadas en un ABN por año.
     * @param abn abn
     * @param proveedor solicitante
     * @return totales
     */
    List<Object[]> findAllNumeracionAsignadaByAbnGroupByAnio(BigDecimal abn, Proveedor proveedor);

    /**
     * Recupera un historico numeraciones solicitada por población.
     * @param solicitud asignacion
     * @return historicos
     */
    List<Object[]> findAllNumSolicitadasBySolicitudGroupByPoblacion(SolicitudAsignacion solicitud);

    /**
     * Recupera un historico numeraciones solicitada por ABN.
     * @param solicitud asignacion
     * @return historicos
     */
    List<Object[]> findAllNumSolicitadasBySolicitudGroupByAbn(SolicitudAsignacion solicitud);

}
