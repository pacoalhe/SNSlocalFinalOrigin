package mx.ift.sns.dao.oficios;

import java.util.List;

import mx.ift.sns.dao.IBaseDAO;
import mx.ift.sns.modelo.filtros.FiltroBusquedaPlantillas;
import mx.ift.sns.modelo.oficios.Plantilla;
import mx.ift.sns.modelo.oficios.TipoDestinatario;
import mx.ift.sns.modelo.solicitud.TipoSolicitud;

/**
 * Interfaz de definición de los métodos para base de datos para la generación de Plantillas.
 */
public interface IPlantillaDao extends IBaseDAO<Plantilla> {

    /**
     * Recupera todos las plantillas almacenadas.
     * @return List
     */
    List<Plantilla> findAllPlantillas();

    /**
     * Recupera todos las plantillas almacenadas según los criterios dados.
     * @param pFiltros Filtros de Búsqueda y paginación.
     * @return List
     */
    List<Plantilla> findAllPlantillas(FiltroBusquedaPlantillas pFiltros);

    /**
     * Recupera el número de plantillas almacenadas según los criterios dados.
     * @param pFiltros Filtros de Búsqueda y paginación.
     * @return número de plantillas almacenadas según los criterios dados.
     */
    int findAllPlantillasCount(FiltroBusquedaPlantillas pFiltros);

    /**
     * Recupera una plantilla del catálogo de plantillas.
     * @param pTipoSolicitud Tipo de solicitud.
     * @param pTipoDestinatario Tipo de destinatario del oficio.
     * @return Objeto plantilla con el documento serializado.
     */
    Plantilla getPlantilla(TipoSolicitud pTipoSolicitud, TipoDestinatario pTipoDestinatario);

}
