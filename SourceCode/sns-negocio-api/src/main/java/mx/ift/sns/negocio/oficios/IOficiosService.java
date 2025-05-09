package mx.ift.sns.negocio.oficios;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.modelo.filtros.FiltroBusquedaPlantillas;
import mx.ift.sns.modelo.oficios.Oficio;
import mx.ift.sns.modelo.oficios.OficioBlob;
import mx.ift.sns.modelo.oficios.Plantilla;
import mx.ift.sns.modelo.oficios.TipoDestinatario;
import mx.ift.sns.modelo.solicitud.Solicitud;
import mx.ift.sns.modelo.solicitud.TipoSolicitud;

/**
 * Interfaz de negocio para Generación de Oficios.
 */
public interface IOficiosService {

    /**
     * Persiste un objeto Oficio en la tabla Oficios.
     * @param pOficio Oficio
     * @return Oficio
     */
    Oficio saveOficio(Oficio pOficio);

    /**
     * Genera un nuevo oficio en base a una plantilla con la información de la solicitud.
     * @param pParametros Parámetros de creación del oficio.
     * @return Oficio oficio creado
     * @throws Exception en caso de error
     */
    Oficio crearOficio(ParametrosOficio pParametros) throws Exception;

    /**
     * Actualiza un oficio existente con la información de la solicitud.
     * @param pParametros Parámetros de actualización del oficio.
     * @return oficio Oficio con el documento actualizado.
     * @throws Exception en caso de error
     */
    Oficio actualizarOficio(ParametrosOficio pParametros) throws Exception;

    /**
     * Almacena una plantilla en base de datos con la información del tipo de solicitud asociada.
     * @param pTipoSolicitud Tipo de solicitud que requerirá la plantilla.
     * @param pTipoDestinatario Tipo de destinatario para el oficio.
     * @param pPlantillaSerializada Documento serializado de la plantilla.
     * @param pDescripcion Descripción de la plantilla.
     * @throws Exception en caso de error
     */
    void guardarPlantilla(TipoSolicitud pTipoSolicitud,
            TipoDestinatario pTipoDestinatario, byte[] pPlantillaSerializada, String pDescripcion) throws Exception;

    /**
     * Recupera un oficio almacenado en BBDD.
     * @param pIdOficio Identificador de Oficio
     * @return Oficio según su ID
     */
    Oficio getOficio(BigDecimal pIdOficio);

    /**
     * Recupera un oficio en función de su Solicitud y Tipo de Destinatario.
     * @param pSolicitud Solicitud a la que pertenece
     * @param pCdgTipoDestinatario Tipo de Destinatario del oficio
     * @return Oficio
     */
    Oficio getOficio(Solicitud pSolicitud, String pCdgTipoDestinatario);

    /**
     * Recupera una plantilla de la base de datos de Plantillas. <b>Método generado para pruebas JUnit</b>
     * @param pTipoSolicitud Tipo de Solicitud
     * @param pTipoDestinatario Tipo de Destinatario del Oficio
     * @return Bytes del documento de Plantilla
     * @throws Exception En el caso de ocurrir un error se lanza la excepción
     */
    byte[] descargarPlantilla(TipoSolicitud pTipoSolicitud, TipoDestinatario pTipoDestinatario) throws Exception;

    /**
     * Elimina un registro de Plantilla.
     * @param pTipoSolicitud Tipo de Solicitud
     * @param pTipoDestinatario Tipo de Destinatario del Oficio
     * @throws Exception En el caso de ocurrir un error se lanza la excepción
     */
    void removePlantilla(TipoSolicitud pTipoSolicitud, TipoDestinatario pTipoDestinatario) throws Exception;

    /**
     * Almacena una nueva plantilla en el catálogo de plantillas.
     * @param pPlantilla Plantilla
     * @return Plantilla
     */
    Plantilla savePlantilla(Plantilla pPlantilla);

    /**
     * Recupera una plantilla del catálogo de plantillas.
     * @param pTipoSolicitud Tipo de solicitud.
     * @param pTipoDestinatario Tipo de destinatario del oficio.
     * @return Objeto plantilla con el documento serializado.
     */
    Plantilla getPlantilla(TipoSolicitud pTipoSolicitud, TipoDestinatario pTipoDestinatario);

    /**
     * Elimina un registro de Plantilla.
     * @param pPlantilla Plantilla
     */
    void removePlantilla(Plantilla pPlantilla);

    /**
     * Recupera el catálogo de tipos de destinatario.
     * @return List
     */
    List<TipoDestinatario> findAllTiposDestinatario();

    /**
     * Recupera un Tipo Destinatario por su código.
     * @param pCdgDestinatario Código de Tipo de Destinatario
     * @return TipoDestinatario
     */
    TipoDestinatario getTipoDestinatarioByCdg(String pCdgDestinatario);

    /**
     * Recupera el número de oficios existentes en base de datos.
     * @return número de oficios existentes en base de datos.
     */
    int getOficiosCount();

    /**
     * Recupera un oficio por su número de oficio.
     * @param numOficio Número de Oficio.
     * @param pTipoDestinatario Tipo de Destinatario del oficio
     * @return Oficio
     */
    Oficio getOficioByNumOficio(String numOficio, TipoDestinatario pTipoDestinatario);

    /**
     * Recupera la instancia de base de datos del objeto OficioBlob solicitado.
     * @param pOficioBlobId Identificador del objeto OficioBlob
     * @return Objeto OficioBlob almacenado en bbdd.
     */
    OficioBlob getOficioBlob(BigDecimal pOficioBlobId);

    /**
     * Persiste un objeto OficioBlob en base de datos asignándole un id.
     * @param pOficioBlob objeto OficioBlob nuevo.
     * @return Objeto OficioBlob persistido.
     */
    OficioBlob saveOficioBlob(OficioBlob pOficioBlob);

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
     * Genera el fichero excel serializado con la exportación de la consulta del catálogo de plantillas.
     * @param pListaPlantillas Lista de Plantillas
     * @return Fichero Excel Serializado.
     * @throws Exception en caso de error.
     */
    byte[] getExportConsultaCatalogoPlantillas(List<Plantilla> pListaPlantillas) throws Exception;

    /**
     * Comprueba si un numero de oficio ya existe.
     * @param numeroOficio oficio
     * @return boolean
     */
    boolean existeNumeroOficio(String numeroOficio);

    /**
     * Recupera los oficios de una solicitud.
     * @param idSolicitud idSolicitud
     * @return lista de oficios
     */
    List<Oficio> getOficiosBySolicitud(BigDecimal idSolicitud);

}
