package mx.ift.sns.negocio.oficios;

import java.math.BigDecimal;
import java.util.List;

import mx.ift.sns.modelo.cpsi.SolicitudAsignacionCpsi;
import mx.ift.sns.modelo.cpsi.SolicitudCesionCpsi;
import mx.ift.sns.modelo.cpsi.SolicitudCpsiUit;
import mx.ift.sns.modelo.cpsi.SolicitudLiberacionCpsi;
import mx.ift.sns.modelo.cpsn.SolicitudAsignacionCpsn;
import mx.ift.sns.modelo.cpsn.SolicitudCesionCPSN;
import mx.ift.sns.modelo.cpsn.SolicitudLiberacionCpsn;
import mx.ift.sns.modelo.ng.SolicitudAsignacion;
import mx.ift.sns.modelo.ng.SolicitudCesionNg;
import mx.ift.sns.modelo.ng.SolicitudLiberacionNg;
import mx.ift.sns.modelo.ng.SolicitudRedistribucionNg;
import mx.ift.sns.modelo.nng.SolicitudAsignacionNng;
import mx.ift.sns.modelo.nng.SolicitudCesionNng;
import mx.ift.sns.modelo.nng.SolicitudLiberacionNng;
import mx.ift.sns.modelo.nng.SolicitudRedistribucionNng;
import mx.ift.sns.modelo.oficios.Oficio;
import mx.ift.sns.modelo.oficios.OficioBlob;
import mx.ift.sns.modelo.oficios.TipoDestinatario;
import mx.ift.sns.modelo.solicitud.Solicitud;

/**
 * Facade de métodos comunes para la generación de oficios.
 * @author X53490DE
 */
public interface IOficiosFacade {

    /**
     * Devuelve el valor de un parametro por su nombre.
     * @param name nombre
     * @return valor
     */
    String getParamByName(String name);

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
     * Recupera un oficio en función de su Solicitud y Tipo de Destinatario.
     * @param pSolicitud Solicitud a la que pertenece
     * @param pCdgTipoDestinatario Tipo de Destinatario del oficio
     * @return Oficio
     */
    Oficio getOficio(Solicitud pSolicitud, String pCdgTipoDestinatario);

    /**
     * Persiste un objeto Oficio en la tabla Oficios.
     * @param pOficio Oficio
     * @return Oficio
     */
    Oficio saveOficio(Oficio pOficio);

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
     * Recupera un oficio por su número de oficio.
     * @param numOficio Número de Oficio.
     * @param pTipoDestinatario Tipo de Destinatario del oficio
     * @return Oficio
     */
    Oficio getOficioByNumOficio(String numOficio, TipoDestinatario pTipoDestinatario);

    /**
     * Comprueba si un numero de oficio ya existe.
     * @param numeroOficio oficio
     * @return boolean
     */
    boolean existeNumeroOficio(String numeroOficio);

    /**
     * Méotodo que salva una solicitud asignación.
     * @param solicitudAsignacion solicitud a crear
     * @return solicitudasignación salvada
     * @throws Exception error al crear solicitud
     */
    SolicitudAsignacion saveSolicitudAsignacion(SolicitudAsignacion solicitudAsignacion) throws Exception;

    /**
     * Méotodo que salva una solicitud cesión.
     * @param solicitudCesion solicitud a crear
     * @return solicitudcesión salvada
     * @throws Exception error al crear solicitud
     */
    SolicitudCesionNg saveSolicitudCesion(SolicitudCesionNg solicitudCesion) throws Exception;

    /**
     * Méotodo que salva una solicitud asignación.
     * @param pSolicitud solicitud a salvar
     * @return solicitud persistida
     * @throws Exception error al guardar
     */
    SolicitudLiberacionNg saveSolicitudLiberacion(SolicitudLiberacionNg pSolicitud) throws Exception;

    /**
     * Méotodo que salva una solicitud redistribución.
     * @param pSolicitud solicitudRedistribucion a crear
     * @return solicitudRedistribución salvada
     */
    SolicitudRedistribucionNg saveSolicitudRedistribucion(SolicitudRedistribucionNg pSolicitud);

    /**
     * Persiste / Mergea un objeto SolicitudCesionNng.
     * @param pSolicitudCesion Objeto a almacenar.
     * @return SolicitudCesionNng persistido / mergeado.
     */
    SolicitudCesionNng saveSolicitudCesion(SolicitudCesionNng pSolicitudCesion);

    /**
     * Persiste / Mergea un objeto SolicitudAsignacionNng.
     * @param pSolicitud Objeto a almacenar.
     * @return SolicitudAsignacionNng persistido / mergeado.
     */
    SolicitudAsignacionNng saveSolicitudAsignacion(SolicitudAsignacionNng pSolicitud);

    /**
     * Persiste / Mergea un objeto SolicitudLiberacionNng.
     * @param pSolicitud Objeto a almacenar.
     * @return SolicitudLiberacionNng persistido / mergeado.
     */
    SolicitudLiberacionNng saveSolicitudLiberacion(SolicitudLiberacionNng pSolicitud);

    /**
     * Méotodo que salva una solicitud redistribución.
     * @param pSolicitud solicitudRedistribucion a crear
     * @return SolicitudRedistribucionNng
     */
    SolicitudRedistribucionNng saveSolicitudRedistribucion(SolicitudRedistribucionNng pSolicitud);

    /**
     * Méotodo que salva una solicitud liberación de CPSN.
     * @param pSolicitud solicitud a salvar
     * @return SolicitudLiberacionCpsn
     */
    SolicitudLiberacionCpsn saveSolicitudLiberacion(SolicitudLiberacionCpsn pSolicitud);

    /**
     * Méotodo que salva una solicitud asignación de CPSN.
     * @param solicitudAsignacion solicitud a crear
     * @return SolicitudAsignacionCpsn salvada
     * @throws Exception error al crear solicitud
     */
    SolicitudAsignacionCpsn saveSolicitudAsignacion(SolicitudAsignacionCpsn solicitudAsignacion) throws Exception;

    /**
     * Méotodo que salva una solicitud cesión de CPSN.
     * @param solicitudCesion solicitud a crear
     * @return solicitudcesión salvada
     * @throws Exception error al crear solicitud
     */
    SolicitudCesionCPSN saveSolicitudCesion(SolicitudCesionCPSN solicitudCesion) throws Exception;

    /**
     * Méotodo que salva una solicitud asignación.
     * @param pSolicitud solicitud a salvar
     * @return SolicitudAsignacionCpsi
     */
    SolicitudAsignacionCpsi saveSolicitudAsignacion(SolicitudAsignacionCpsi pSolicitud);

    /**
     * Méotodo que salva una solicitud de cesión CSPI.
     * @param pSolicitud solicitud a salvar
     * @return solicitud persistida
     */
    SolicitudCesionCpsi saveSolicitudCesion(SolicitudCesionCpsi pSolicitud);

    /**
     * Méotodo que salva una solicitud liberación de CPSI.
     * @param pSolicitud solicitud a salvar
     * @return SolicitudLiberacionCpsi
     */
    SolicitudLiberacionCpsi saveSolicitudLiberacion(SolicitudLiberacionCpsi pSolicitud);

    /**
     * Método que salva una solicitud de códigos CPSI a la UIT.
     * @param pSolicitud solicitud a salvar.
     * @return SolicitudCpsiUit
     */
    SolicitudCpsiUit saveSolicitudCpsiUit(SolicitudCpsiUit pSolicitud);

}
