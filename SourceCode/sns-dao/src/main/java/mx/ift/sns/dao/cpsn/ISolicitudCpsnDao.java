package mx.ift.sns.dao.cpsn;

import java.util.List;

import mx.ift.sns.modelo.cpsn.VConsultaGenericaCpsn;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudesCpsn;

/**
 * Interfaz DAO para Solicitud de Liberación de CPSN.
 * @author X53490DE
 */
public interface ISolicitudCpsnDao {

    /**
     * Método que busca las solicitudes de códigos cpsn según los filtros.
     * @param filtros a cumplir
     * @return listado de solicitudes
     */
    List<VConsultaGenericaCpsn> findAllSolicitudes(FiltroBusquedaSolicitudesCpsn filtros);

    /**
     * Método que cuenta las solicitudes de códigos cpsn según los filtros.
     * @param filtros a cumplir
     * @return número de solicitudes
     */
    Integer findAllSolicitudesCount(FiltroBusquedaSolicitudesCpsn filtros);

    /**
     * Método que busca todas las solicitudes CPSN sin filtro.
     * @return listado de solicitudes.
     */
    List<VConsultaGenericaCpsn> findAllSolicitudesCpsn();
}
