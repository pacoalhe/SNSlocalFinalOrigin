package mx.ift.sns.dao.ng;

import java.util.List;

import mx.ift.sns.modelo.ng.NumeracionAsignada;
import mx.ift.sns.modelo.ng.NumeracionSolicitada;
import mx.ift.sns.modelo.ng.Serie;
import mx.ift.sns.modelo.ng.SolicitudAsignacion;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.series.Nir;

/**
 * DAO de acceso a NumeracionSolicitada.
 */
public interface INumeracionAsignadaDAO {

    /**
     * Busca la numeracion solicitada que coincide con los datos indicados.
     * @param nir NIR
     * @param serie SNA
     * @param inicioRango inicio
     * @param finRango fin
     * @param arrendador proveedor arrendador
     * @return numeracion si existe
     */
    NumeracionAsignada findNumeracionArrendada(Nir nir, Serie serie, String inicioRango, String finRango,
            Proveedor arrendador);

    /**
     * Obtiene todas las numeracion asigandas de una solicitud.
     * @param pSolicitud solicitud
     * @return numeraciones asignadas
     */
    List<NumeracionAsignada> findAllNumeracionAsignadaBySolicitud(SolicitudAsignacion pSolicitud);

    /**
     * Comprueba si una numeracion solicitada tiene numeraciones asignadas asociadas
     * @param numeracionSolicitada numeracionSolicitada
     * @return true/false
     */
    boolean existNumeracionAsignadaBySolicita(NumeracionSolicitada numeracionSolicitada);

}
