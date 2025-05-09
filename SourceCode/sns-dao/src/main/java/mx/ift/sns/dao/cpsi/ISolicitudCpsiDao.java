package mx.ift.sns.dao.cpsi;

import java.util.List;

import mx.ift.sns.modelo.cpsi.VConsultaGenericaCpsi;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudesCPSI;

/**
 * Interfaz DAO para Solicitud genérica de cpsi.
 * @author X23016PE
 */
public interface ISolicitudCpsiDao {

    /**
     * Método que busca las solicitudes de códigos cpsi según los filtros.
     * @param filtros a cumplir
     * @return listado de solicitudes
     */
    List<VConsultaGenericaCpsi> findAllSolicitudes(FiltroBusquedaSolicitudesCPSI filtros);

    /**
     * Método que cuenta las solicitudes de códigos cpsi según los filtros.
     * @param filtros a cumplir
     * @return número de solicitudes
     */
    Integer findAllSolicitudesCount(FiltroBusquedaSolicitudesCPSI filtros);

    /**
     * Método que busca todas las solicitudes cpsi sin filtro.
     * @return listado de solicitudes.
     */
    List<VConsultaGenericaCpsi> findAllSolicitudesCpsi();
}
