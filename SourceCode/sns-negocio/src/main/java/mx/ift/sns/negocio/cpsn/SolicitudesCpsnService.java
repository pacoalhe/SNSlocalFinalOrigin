package mx.ift.sns.negocio.cpsn;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import mx.ift.sns.dao.cpsn.ISolicitudAsignacionCpsnDao;
import mx.ift.sns.dao.cpsn.ISolicitudCesionCPSNDao;
import mx.ift.sns.dao.cpsn.ISolicitudCpsnDao;
import mx.ift.sns.dao.cpsn.ISolicitudLiberacionCpsnDao;
import mx.ift.sns.dao.solicitud.IEstadoSolicitudDao;
import mx.ift.sns.dao.solicitud.ITipoSolicitudDao;
import mx.ift.sns.modelo.cps.EstatusAsignacionCps;
import mx.ift.sns.modelo.cpsn.CesionSolicitadaCPSN;
import mx.ift.sns.modelo.cpsn.CodigoCPSN;
import mx.ift.sns.modelo.cpsn.EstatusCPSN;
import mx.ift.sns.modelo.cpsn.LiberacionSolicitadaCpsn;
import mx.ift.sns.modelo.cpsn.NumeracionAsignadaCpsn;
import mx.ift.sns.modelo.cpsn.SolicitudAsignacionCpsn;
import mx.ift.sns.modelo.cpsn.SolicitudCesionCPSN;
import mx.ift.sns.modelo.cpsn.SolicitudLiberacionCpsn;
import mx.ift.sns.modelo.cpsn.TipoBloqueCPSN;
import mx.ift.sns.modelo.cpsn.VConsultaGenericaCpsn;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudesCpsn;
import mx.ift.sns.modelo.solicitud.EstadoCesionSolicitada;
import mx.ift.sns.modelo.solicitud.EstadoLiberacionSolicitada;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.modelo.solicitud.TipoSolicitud;
import mx.ift.sns.negocio.IBitacoraService;
import mx.ift.sns.negocio.PeticionCancelacion;
import mx.ift.sns.utils.date.FechasUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación del servicio de Soliciutdes para Codigos CPS Nacionales.
 */
@Stateless(name = "SolicitudesCpsnService", mappedName = "SolicitudesCpsnService")
@Remote(ISolicitudesCpsnService.class)
public class SolicitudesCpsnService implements ISolicitudesCpsnService {

    /** Logger de la clase . */
    private static final Logger LOGGER = LoggerFactory.getLogger(SolicitudesCpsnService.class);

    /** DAO de EstadoSolicitud. */
    @Inject
    private IEstadoSolicitudDao estadoSolicitudDao;

    /** DAO Solicitudes de Asignacion CPSN. */
    @Inject
    private ISolicitudAsignacionCpsnDao solicitudAsigDao;

    /** DAO Solicitudes de Liberación CPSN. */
    @Inject
    private ISolicitudLiberacionCpsnDao solicitudLibDao;

    /** DAO Solicitudes de Cesión. */
    @Inject
    private ISolicitudCesionCPSNDao solicitudCesionCPSNDao;

    /** DAO Solicitudes de Liberacion. */
    @Inject
    private ISolicitudLiberacionCpsnDao solicitudLiberacionCPSNDao;

    /** DAO Solicitudes de CPSN. */
    @Inject
    private ISolicitudCpsnDao solicitudCpsnDao;

    /** Servicio de Códigos CPSN. */
    @EJB
    private ICodigoCPSNService cpsnService;

    /** Servicio de Bitácora. */
    @EJB
    private IBitacoraService bitacoraService;

    /** DAO de Tipos de solicitud. */
    @Inject
    private ITipoSolicitudDao tipoSolicitudDao;

    /********************************************************************************************/

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<EstadoSolicitud> findAllEstadosSolicitud() {
        return estadoSolicitudDao.findAllEstadosSolicitud();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public int findAllSolicitudesAsignacionCount(FiltroBusquedaSolicitudesCpsn pFiltrosSolicitud) throws Exception {
        return solicitudAsigDao.findAllSolicitudesAsignacionCount(pFiltrosSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<SolicitudAsignacionCpsn> findAllSolicitudesAsignacion(FiltroBusquedaSolicitudesCpsn pFiltrosSolicitud)
            throws Exception {
        return solicitudAsigDao.findAllSolicitudesAsignacion(pFiltrosSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudAsignacionCpsn saveSolicitudAsignacion(SolicitudAsignacionCpsn solicitudAsignacion)
            throws Exception {
        return solicitudAsigDao.saveOrUpdate(solicitudAsignacion);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudAsignacionCpsn getSolicitudAsignacionEagerLoad(SolicitudAsignacionCpsn pSolicitud)
            throws Exception {
        return solicitudAsigDao.getSolicitudAsignacionEagerLoad(pSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudAsignacionCpsn getSolicitudAsignacionById(BigDecimal pConsecutivo) {
        return solicitudAsigDao.getSolicitudAsignacionById(pConsecutivo);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<SolicitudLiberacionCpsn> findAllSolicitudesLiberacion(FiltroBusquedaSolicitudesCpsn pFiltrosSolicitud) {
        return solicitudLibDao.findAllSolicitudesLiberacion(pFiltrosSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public int findAllSolicitudesLiberacionCount(FiltroBusquedaSolicitudesCpsn pFiltrosSolicitud) {
        return solicitudLibDao.findAllSolicitudesLiberacionCount(pFiltrosSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudLiberacionCpsn saveSolicitudLiberacion(SolicitudLiberacionCpsn pSolicitud) {
        return solicitudLibDao.saveOrUpdate(pSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public SolicitudLiberacionCpsn getSolicitudLiberacionById(BigDecimal pConsecutivo) {
        return solicitudLibDao.getSolicitudLiberacionById(pConsecutivo);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudLiberacionCpsn getSolicitudLiberacionEagerLoad(SolicitudLiberacionCpsn pSolicitud) {
        return solicitudLibDao.getSolicitudLiberacionEagerLoad(pSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public SolicitudLiberacionCpsn applyLiberacionesSolicitadas(SolicitudLiberacionCpsn pSolicitud) throws Exception {

        // Parseamos las fechas para eliminar las horas y minutos
        Date fHoy = FechasUtils.getFechaHoy();

        EstadoLiberacionSolicitada estatusLiberado = new EstadoLiberacionSolicitada();
        estatusLiberado.setCodigo(EstadoLiberacionSolicitada.LIBERADO);

        boolean todoLiberado = true;
        boolean liberacionesPendientes = false;
        for (LiberacionSolicitadaCpsn libSol : pSolicitud.getLiberacionesSolicitadas()) {
            // Ignoramos las liberaciones que ya se hayan ejecutado.
            if (libSol.getEstatus().getCodigo().equals(EstadoLiberacionSolicitada.PENDIENTE)) {
                // Fecha de Implementación
                Date fImplementacion = FechasUtils.parseFecha(libSol.getFechaImplementacion());

                // Liberación
                String resultado = cpsnService.liberarCpsn(libSol, fHoy.equals(fImplementacion));
                todoLiberado = todoLiberado && resultado.equals(EstadoLiberacionSolicitada.LIBERADO);

                if (resultado.equals(EstadoLiberacionSolicitada.LIBERADO)) {
                    libSol.setEstatus(estatusLiberado);
                }

                // Indica que se han ejecutado liberaciones.
                liberacionesPendientes = true;
            }
        }

        // Es posible que se haya cancelado la solicitud directamente cancelando sus liberaciones individuales.
        boolean solicitudCancelada = pSolicitud.getEstadoSolicitud().getCodigo()
                .equals(EstadoSolicitud.SOLICITUD_CANCELADA);

        EstadoSolicitud es = new EstadoSolicitud();
        if (todoLiberado) {
            if (solicitudCancelada && (!liberacionesPendientes)) {
                // Es posible que se haya cancelado la solicitud directamente cancelando sus liberaciones individuales.
                return pSolicitud;
            } else {
                // Marcamos la solcitud como Terminada.
                es.setCodigo(EstadoSolicitud.SOLICITUD_TERMINADA);
                pSolicitud.setFechaAsignacion(new Date());
            }
        } else {
            // Marcamos la solicitud como En Trámite (por si antes estaba terminada)
            es.setCodigo(EstadoSolicitud.SOLICITUD_EN_TRAMITE);
            pSolicitud.setFechaAsignacion(null);
        }
        pSolicitud.setEstadoSolicitud(es);

        // Guardamos los cambios
        return this.saveSolicitudLiberacion(pSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void applyLiberacionesPendientes() throws Exception {

        // Solicitudes en Trámite
        EstadoSolicitud estatusSol = new EstadoSolicitud();
        estatusSol.setCodigo(EstadoSolicitud.SOLICITUD_EN_TRAMITE);

        // Liberaciones Solicitadas Reservadas (Pendientes)
        EstadoLiberacionSolicitada statusLibSol = new EstadoLiberacionSolicitada();
        statusLibSol.setCodigo(EstadoLiberacionSolicitada.PENDIENTE);

        // Filtros de búsqueda
        FiltroBusquedaSolicitudesCpsn filtroSolicitudes = new FiltroBusquedaSolicitudesCpsn();
        filtroSolicitudes.setEstado(estatusSol);
        filtroSolicitudes.setEstatusLibSol(statusLibSol);

        List<SolicitudLiberacionCpsn> solicitudes = this.findAllSolicitudesLiberacion(filtroSolicitudes);
        for (SolicitudLiberacionCpsn solicitud : solicitudes) {
            if (!solicitud.getLiberacionesSolicitadas().isEmpty()) {
                try {
                    bitacoraService.add(solicitud,
                            "Comprobando Liberaciones CPSN pendientes de Solicitud " + solicitud.getId());

                    // Hacemos una validación previa de que aún existen los códigos CPSN
                    boolean codigosOk = true;
                    for (LiberacionSolicitadaCpsn libSol : solicitud.getLiberacionesSolicitadas()) {
                        if (libSol.getEstatus().getCodigo().equals(EstadoLiberacionSolicitada.PENDIENTE)) {
                            CodigoCPSN cpsn = cpsnService.getCodigoCpsn(
                                    libSol.getTipoBloqueCpsn().getId(),
                                    libSol.getIdCpsn(),
                                    libSol.getSolicitudLiberacion().getProveedorSolicitante());

                            if (cpsn == null) {
                                StringBuilder sbTraza = new StringBuilder();
                                sbTraza.append("Código CPSN no encontrado. ");
                                sbTraza.append("Id: ").append(libSol.getIdCpsn()).append(" ");
                                sbTraza.append("Bloque: ").append(libSol.getTipoBloqueCpsn().getId()).append(" ");
                                sbTraza.append("Pst: ");
                                sbTraza.append(libSol.getSolicitudLiberacion().getProveedorSolicitante());
                                bitacoraService.add(solicitud, sbTraza.toString());
                                codigosOk = false;
                                break;
                            }
                        }
                    }
                    if (codigosOk) {
                        this.applyLiberacionesSolicitadas(solicitud);
                    }
                } catch (Exception e) {
                    LOGGER.error("Error ejecutando Liberaciones de CPSN pendientes.", e);
                }
            }
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public PeticionCancelacion cancelSolicitudLiberacion(SolicitudLiberacionCpsn pSolicitud) throws Exception {

        // Cargamos las liberaciones de la solicitud y comprobamos que se puedan cancelar.
        SolicitudLiberacionCpsn solicitud = this.getSolicitudLiberacionEagerLoad(pSolicitud);
        List<String> avisosCancelacion = new ArrayList<String>(solicitud.getLiberacionesSolicitadas().size());
        boolean isCancelacionPosible = true;
        for (LiberacionSolicitadaCpsn libSol : solicitud.getLiberacionesSolicitadas()) {
            PeticionCancelacion checkCancelacionLib = this.checkCancelLiberacion(libSol);
            if (!checkCancelacionLib.isCancelacionPosible()) {
                avisosCancelacion.add(checkCancelacionLib.getMensajeError());
            }
            isCancelacionPosible = isCancelacionPosible && checkCancelacionLib.isCancelacionPosible();
        }

        // Si todas las liberaciones se pueden efectuar procedemos.
        PeticionCancelacion checkCancelacion = new PeticionCancelacion();
        if (isCancelacionPosible) {
            for (LiberacionSolicitadaCpsn libSol : solicitud.getLiberacionesSolicitadas()) {
                this.cancelLiberacion(libSol, false);
            }

            // Cambiamos el status de la Solicitud
            EstadoSolicitud statusSolCancelada = new EstadoSolicitud();
            statusSolCancelada.setCodigo(EstadoSolicitud.SOLICITUD_CANCELADA);
            solicitud.setEstadoSolicitud(statusSolCancelada);
            saveSolicitudLiberacion(solicitud);

            // Cancelación efectudada
            checkCancelacion.setCancelacionPosible(true);
        } else {
            StringBuffer sbAvisos = new StringBuffer();
            for (String aviso : avisosCancelacion) {
                sbAvisos.append(aviso).append("<br>");
            }
            checkCancelacion.setCancelacionPosible(false);
            checkCancelacion.setMensajeError(sbAvisos.toString());
        }

        return checkCancelacion;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public PeticionCancelacion cancelLiberacion(LiberacionSolicitadaCpsn pLibSol, boolean pUseCheck) throws Exception {
        PeticionCancelacion checkCancelacion = null;
        if (pUseCheck) {
            checkCancelacion = this.checkCancelLiberacion(pLibSol);
        } else {
            checkCancelacion = new PeticionCancelacion();
            checkCancelacion.setCancelacionPosible(true);
        }

        if (checkCancelacion.isCancelacionPosible()) {

            // Comprobamos si el CPSN sigue existiendo tal y como estaba cuando se creó la liberación solicitada.
            CodigoCPSN cpsn = cpsnService.getCodigoCpsn(pLibSol.getTipoBloqueCpsn().getId(), pLibSol.getIdCpsn(), null);

            boolean cpsnSinModificar = ((cpsn != null) // Si cpsn es nulo no se evalúa el resto.
                    && ((cpsn.getEstatusCPSN().getId().equals(EstatusCPSN.CUARENTENA)
                    || cpsn.getEstatusCPSN().getId().equals(EstatusCPSN.PLANIFICADO))));

            StringBuilder sbBitacora = new StringBuilder();
            if (cpsnSinModificar) {
                // Reestablecemos el CPSN como asignado al Proveedor Solicitante.
                EstatusCPSN status = new EstatusCPSN();
                status.setId(EstatusCPSN.ASIGNADO);

                cpsn.setEstatusCPSN(status);
                cpsn.setFechaCuarentena(null);
                cpsn.setProveedor(pLibSol.getSolicitudLiberacion().getProveedorSolicitante());
                cpsnService.saveCodigoCpsn(cpsn);

                sbBitacora.append("Cancelada Liberación de CPSN. ").append(cpsn);
                sbBitacora.append("El CPSN ha sido reasignado al Proveedor ");
                sbBitacora.append(pLibSol.getSolicitudLiberacion().getProveedorSolicitante().getNombre());
                bitacoraService.add(pLibSol.getSolicitudLiberacion(), sbBitacora.toString());
            } else {
                sbBitacora.append("Cancelada Liberación de CPSN. ");
                sbBitacora.append("CPSN = {").append("Id: ").append(pLibSol.getIdCpsn());
                sbBitacora.append(", Tipo bloque: ").append(pLibSol.getTipoBloqueCpsn().getDescripcion());
                sbBitacora.append(", Binario: ").append(pLibSol.getBinario());
                if (pLibSol.getTipoBloqueCpsn().getId().equals(TipoBloqueCPSN.INDIVIDUAL)) {
                    sbBitacora.append(", Dec. Total: ").append(pLibSol.getDecimalTotal());
                } else {
                    sbBitacora.append(", Dec. Red: ").append(pLibSol.getDecimalRed());
                    sbBitacora.append(", Dec. Desde: ").append(pLibSol.getDecimalDesde());
                    sbBitacora.append(", Dec. Hasta: ").append(pLibSol.getDecimalHasta());
                }
                sbBitacora.append("}");
                bitacoraService.add(pLibSol.getSolicitudLiberacion(), sbBitacora.toString());
            }

            // Estado Cancelada para la liberación. Se actualiza al guardar la solicitud.
            EstadoLiberacionSolicitada statusLibSolCancelada = new EstadoLiberacionSolicitada();
            statusLibSolCancelada.setCodigo(EstadoLiberacionSolicitada.CANCELADO);
            pLibSol.setEstatus(statusLibSolCancelada);
        }

        return checkCancelacion;
    }

    /**
     * Comprueba si es posible la cancelación de una liberación.
     * @param pLibSol Información de la liberación.
     * @return Objeto PeticionCancelacion con la información del proceso.
     * @throws Exception En caso de error.
     */
    private PeticionCancelacion checkCancelLiberacion(LiberacionSolicitadaCpsn pLibSol) throws Exception {
        PeticionCancelacion checkCancelacion = new PeticionCancelacion();

        if (pLibSol.getEstatus().getCodigo().equals(EstadoLiberacionSolicitada.PENDIENTE)) {
            checkCancelacion.setCancelacionPosible(true);
        } else {
            // Si la liberación se ha cumplido pero el periodo de reserva aún está vigente es posible
            // realizar la cancelación.
            checkCancelacion.setCancelacionPosible(FechasUtils.getFechaHoy().before(pLibSol.getFechaFinCuarentena()));
        }

        if (!checkCancelacion.isCancelacionPosible()) {
            StringBuffer sbAviso = new StringBuffer();
            sbAviso.append("No es posible cancelar la liberación del CPSN con ");
            sbAviso.append("Tipo bloque: ").append(pLibSol.getTipoBloqueCpsn().getDescripcion());
            sbAviso.append(", Binario: ").append(pLibSol.getBinario());
            if (pLibSol.getTipoBloqueCpsn().getId().equals(TipoBloqueCPSN.INDIVIDUAL)) {
                sbAviso.append(", Dec. Total: ").append(pLibSol.getDecimalTotal());
            } else {
                sbAviso.append(", Dec. Red: ").append(pLibSol.getDecimalRed());
            }
            sbAviso.append(". La fecha de implementación (");
            sbAviso.append(FechasUtils.fechaToString(pLibSol.getFechaImplementacion()));
            sbAviso.append(") y periodo de cuarentena (");
            sbAviso.append(FechasUtils.fechaToString(pLibSol.getFechaFinCuarentena()));
            sbAviso.append(") ya se han cumplido.");

            checkCancelacion.setMensajeError(sbAviso.toString());
        }

        return checkCancelacion;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<String> getNotificacionesLiberacionesPendientes() {

        // Solicitudes en Trámite
        EstadoSolicitud estatusSol = new EstadoSolicitud();
        estatusSol.setCodigo(EstadoSolicitud.SOLICITUD_EN_TRAMITE);

        // Liberaciones Solicitadas Reservadas (Pendientes)
        EstadoLiberacionSolicitada statusLibSol = new EstadoLiberacionSolicitada();
        statusLibSol.setCodigo(EstadoLiberacionSolicitada.PENDIENTE);

        // Filtros de búsqueda
        FiltroBusquedaSolicitudesCpsn filtroSolicitudes = new FiltroBusquedaSolicitudesCpsn();
        filtroSolicitudes.setEstado(estatusSol);
        filtroSolicitudes.setEstatusLibSol(statusLibSol);
        filtroSolicitudes.setFechaImplementacionHasta(FechasUtils.calculaFecha(FechasUtils.getFechaHoy(), 2, 0, 0));

        try {
            // Parseamos las fechas para eliminar las horas y minutos
            Date fHoy = FechasUtils.getFechaHoy();

            // Agrupamos las Solicitudes por fecha
            HashMap<String, Integer> tramitesByFecha = new HashMap<String, Integer>(1);
            ArrayList<String> tramitesFallidos = new ArrayList<String>();
            StringBuffer key;

            List<SolicitudLiberacionCpsn> solicitudes = this.findAllSolicitudesLiberacion(filtroSolicitudes);
            for (SolicitudLiberacionCpsn solicitud : solicitudes) {
                for (LiberacionSolicitadaCpsn libSol : solicitud.getLiberacionesSolicitadas()) {
                    if (libSol.getEstatus().getCodigo().equals(EstadoLiberacionSolicitada.PENDIENTE)) {
                        // Si se ha sobrepasado la fecha de liberación es que hay algún problema para en la liberación.
                        if (fHoy.after(libSol.getFechaImplementacion())) {
                            if (!tramitesFallidos.contains(libSol.getSolicitudLiberacion().getId().toString())) {
                                tramitesFallidos.add(libSol.getSolicitudLiberacion().getId().toString());
                            }
                        } else {
                            // Las notificaciones se generan a partir de 2 días antes de la fecha de implementación
                            if (fHoy.compareTo(FechasUtils.getFechaNotificacion(
                                    libSol.getFechaImplementacion())) >= 0) {
                                key = new StringBuffer();
                                key.append(FechasUtils.fechaToString(libSol.getFechaImplementacion()));
                                key.append("-").append(solicitud.getId());
                                if (!tramitesByFecha.containsKey(key.toString())) {
                                    tramitesByFecha.put(key.toString(), new Integer(0));
                                }
                                int oneMore = (tramitesByFecha.get(key.toString())).intValue() + 1;
                                tramitesByFecha.put(key.toString(), new Integer(oneMore));
                            }
                        }
                    }
                }
            }

            // Generamos las notificaciones
            List<String> notificaciones = new ArrayList<String>();
            StringBuffer notificacion = null;
            for (String item : tramitesByFecha.keySet()) {
                // Info de Trámite y Fecha
                String[] keyInfo = item.split("-");
                Integer count = tramitesByFecha.get(item);

                // Notificación
                notificacion = new StringBuffer();
                notificacion.append("<br/>");
                notificacion.append("La Solicitud ").append(keyInfo[1]).append(" tiene ");
                if (count > 1) {
                    notificacion.append(count).append(" liberaciones programadas ");
                } else {
                    notificacion.append(count).append(" liberación programada ");
                }

                notificacion.append(" con fecha ").append(keyInfo[0]);
                notificaciones.add(notificacion.toString());
            }

            for (String item : tramitesFallidos) {
                notificacion = new StringBuffer();
                notificacion.append("<br/>");
                notificacion.append("La Solicitud ").append(item);
                notificacion.append(" no ha podido ser ejecutada. Revise el trámite.");
                notificaciones.add(notificacion.toString());
            }

            return notificaciones;

        } catch (Exception e) {
            LOGGER.error("Error inesperado: " + e.getMessage());
            return new ArrayList<String>();
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public int findAllSolicitudesCesionCount(FiltroBusquedaSolicitudesCpsn pFiltrosSolicitud) {
        return solicitudCesionCPSNDao.findAllSolicitudesCesionCPSNCount(pFiltrosSolicitud);
    }

    @Override
    public List<SolicitudCesionCPSN> findAllSolicitudesCesion(FiltroBusquedaSolicitudesCpsn pFiltrosSolicitud) {
        return solicitudCesionCPSNDao.findAllSolicitudesCesionCPSN(pFiltrosSolicitud);
    }

    @Override
    public SolicitudCesionCPSN getSolicitudCesionById(BigDecimal pConsecutivo) {
        return solicitudCesionCPSNDao.getSolicitudCesionCPSNById(pConsecutivo);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudCesionCPSN saveSolicitudCesion(SolicitudCesionCPSN pSolicitud) throws Exception {
        return solicitudCesionCPSNDao.saveOrUpdate(pSolicitud);
    }

    @Override
    public SolicitudCesionCPSN getSolicitudCesionEagerLoad(SolicitudCesionCPSN pSolicitud) throws Exception {
        return solicitudCesionCPSNDao.getSolicitudCesionCPSNEagerLoad(pSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public SolicitudCesionCPSN applyCesionesSolicitadas(SolicitudCesionCPSN pSolicitud) throws Exception {

        // Parseamos las fechas para eliminar las horas y minutos
        Date fHoy = FechasUtils.getFechaHoy();

        EstadoCesionSolicitada estatusCedido = new EstadoCesionSolicitada();
        estatusCedido.setCodigo(EstadoCesionSolicitada.CEDIDO);

        boolean todoCedido = true;
        boolean cesionesPendientes = false;
        for (CesionSolicitadaCPSN cesSol : pSolicitud.getCesionesSolicitadasCPSN()) {
            // Ignoramos las cesiones que ya se hayan ejecutado.
            if (cesSol.getEstatus().getCodigo().equals(EstadoCesionSolicitada.PENDIENTE)) {
                // Fecha de Implementación
                Date fImplementacion = FechasUtils.parseFecha(cesSol.getFechaImplementacion());

                // Cesión
                String resultado = cpsnService.cederCPSN(cesSol, fHoy.equals(fImplementacion));
                todoCedido = todoCedido && resultado.equals(EstadoCesionSolicitada.CEDIDO);

                if (resultado.equals(EstadoCesionSolicitada.CEDIDO)) {
                    cesSol.setEstatus(estatusCedido);
                }

                // Indicamos que había cesiones pendientes
                cesionesPendientes = true;
            }
        }

        // Es posible que se haya cancelado la solicitud directamente cancelando sus cesiones individuales.
        boolean solicitudCancelada = pSolicitud.getEstadoSolicitud().getCodigo()
                .equals(EstadoSolicitud.SOLICITUD_CANCELADA);

        EstadoSolicitud es = new EstadoSolicitud();
        if (todoCedido) {
            if (solicitudCancelada && (!cesionesPendientes)) {
                // Es posible que se haya cancelado la solicitud directamente cancelando sus cesiones individuales.
                return pSolicitud;
            } else {
                // Marcamos la solcitud como Terminada.
                es.setCodigo(EstadoSolicitud.SOLICITUD_TERMINADA);
                pSolicitud.setFechaAsignacion(new Date());
            }
        } else {
            // Marcamos la solicitud como En Trámite (por si antes estaba terminada)
            es.setCodigo(EstadoSolicitud.SOLICITUD_EN_TRAMITE);
            pSolicitud.setFechaAsignacion(null);
        }
        pSolicitud.setEstadoSolicitud(es);

        // Guardamos los cambios
        return this.saveSolicitudCesion(pSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void applyCesionesPendientes() throws Exception {

        // Solicitudes en Trámite
        EstadoSolicitud estatusSol = new EstadoSolicitud();
        estatusSol.setCodigo(EstadoSolicitud.SOLICITUD_EN_TRAMITE);

        // Cesiones Solicitadas Pendientes
        EstadoCesionSolicitada statusCesSol = new EstadoCesionSolicitada();
        statusCesSol.setCodigo(EstadoCesionSolicitada.PENDIENTE);

        // Filtros de búsqueda
        FiltroBusquedaSolicitudesCpsn filtroSolicitudes = new FiltroBusquedaSolicitudesCpsn();
        filtroSolicitudes.setEstado(estatusSol);
        filtroSolicitudes.setEstatusCesSol(statusCesSol);

        List<SolicitudCesionCPSN> solicitudes = this.findAllSolicitudesCesion(filtroSolicitudes);
        for (SolicitudCesionCPSN solicitud : solicitudes) {
            if (solicitud.getCesionesSolicitadasCPSN() != null && !solicitud.getCesionesSolicitadasCPSN().isEmpty()) {
                try {
                    bitacoraService.add(solicitud,
                            "Comprobando Cesiones CPSN pendientes de Solicitud " + solicitud.getId());

                    // Hacemos una validación previa de que aún existen los códigos CPSN
                    boolean codigosOk = true;
                    for (CesionSolicitadaCPSN cesSol : solicitud.getCesionesSolicitadasCPSN()) {
                        if (cesSol.getEstatus().getCodigo().equals(EstadoCesionSolicitada.PENDIENTE)) {
                            CodigoCPSN cpsn = cpsnService.getCodigoCpsn(
                                    cesSol.getTipoBloqueCpsn().getId(),
                                    cesSol.getIdCpsn(),
                                    cesSol.getSolicitudCesionCPSN().getProveedorSolicitante());

                            if (cpsn == null) {
                                StringBuilder sbTraza = new StringBuilder();
                                sbTraza.append("Código CPSN no encontrado. ");
                                sbTraza.append("Id: ").append(cesSol.getIdCpsn()).append(" ");
                                sbTraza.append("Bloque: ").append(cesSol.getTipoBloqueCpsn().getId()).append(" ");
                                sbTraza.append("Pst: ");
                                sbTraza.append(cesSol.getSolicitudCesionCPSN().getProveedorSolicitante());
                                bitacoraService.add(solicitud, sbTraza.toString());
                                codigosOk = false;
                                break;
                            }
                        }
                    }
                    if (codigosOk) {
                        this.applyCesionesSolicitadas(solicitud);
                    }
                } catch (Exception e) {
                    LOGGER.error("error ejecutando Cesiones de CPSN pendientes.", e);
                }
            }
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public PeticionCancelacion cancelSolicitudCesion(SolicitudCesionCPSN pSolicitud) throws Exception {
        // Cargamos las cesiones de la solicitud y comprobamos que se puedan cancelar.
        SolicitudCesionCPSN solicitud = this.getSolicitudCesionEagerLoad(pSolicitud);
        List<String> avisosCancelacion = new ArrayList<String>(solicitud.getCesionesSolicitadasCPSN().size());
        boolean isCancelacionPosible = true;
        for (CesionSolicitadaCPSN cesSol : solicitud.getCesionesSolicitadasCPSN()) {
            PeticionCancelacion checkCancelacionCes = this.checkCancelCesion(cesSol);
            if (!checkCancelacionCes.isCancelacionPosible()) {
                avisosCancelacion.add(checkCancelacionCes.getMensajeError());
            }
            isCancelacionPosible = isCancelacionPosible && checkCancelacionCes.isCancelacionPosible();
        }

        PeticionCancelacion checkCancelacion = new PeticionCancelacion();
        if (isCancelacionPosible) {
            for (CesionSolicitadaCPSN cesSol : solicitud.getCesionesSolicitadasCPSN()) {
                this.cancelCesion(cesSol, false);
            }

            // Cambiamos el status de la Solicitud
            EstadoSolicitud statusSolCancelada = new EstadoSolicitud();
            statusSolCancelada.setCodigo(EstadoSolicitud.SOLICITUD_CANCELADA);
            solicitud.setEstadoSolicitud(statusSolCancelada);
            saveSolicitudCesion(solicitud);

            // Cancelación efectudada
            checkCancelacion.setCancelacionPosible(true);
        } else {
            StringBuffer sbAvisos = new StringBuffer();
            for (String aviso : avisosCancelacion) {
                sbAvisos.append(aviso).append("<br>");
            }
            checkCancelacion.setCancelacionPosible(false);
            checkCancelacion.setMensajeError(sbAvisos.toString());
        }

        return checkCancelacion;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public PeticionCancelacion cancelCesion(CesionSolicitadaCPSN pCesSol, boolean pUseCheck) throws Exception {
        PeticionCancelacion checkCancelacion = null;
        if (pUseCheck) {
            checkCancelacion = this.checkCancelCesion(pCesSol);
        } else {
            checkCancelacion = new PeticionCancelacion();
            checkCancelacion.setCancelacionPosible(true);
        }

        // Solamente se permiten hacer cancelaciones de cesiones que no se han realizado. Por lo tanto,
        // para cancelar una cesión solamente es necesario cancelar el trámite (la cesión solicitada).
        // El CPSN se queda como está.

        // Si el CPSN ha sido agrupado o modificado se permite cancelar igualmente ya que el CPSN no se
        // ve afectado.

        if (checkCancelacion.isCancelacionPosible()) {
            // Comprobamos que el CPSN no se haya agrupado o desagrupado
            CodigoCPSN cpsn = cpsnService.getCodigoCpsn(
                    pCesSol.getTipoBloqueCpsn().getId(),
                    pCesSol.getIdCpsn(),
                    pCesSol.getSolicitudCesionCPSN().getProveedorSolicitante());

            if (cpsn != null) {
                // Si el CPSN no ha sido modificado lo actualizamos como Asignado (por si estaba reservado)
                EstatusCPSN statusAsignado = new EstatusCPSN(EstatusCPSN.ASIGNADO);
                cpsn.setEstatusCPSN(statusAsignado);
                cpsn.setFechaCuarentena(null);

                cpsnService.saveCodigoCpsn(cpsn);
            }

            // Traza de Bitácora
            StringBuilder sbBitacora = new StringBuilder();
            sbBitacora.append("Cancelada cesión de CPSN ").append(pCesSol.getIdCpsn());
            bitacoraService.add(pCesSol.getSolicitudCesionCPSN(), sbBitacora.toString());

            // Estado Cancelada para la cesión. Se actualiza al guardar la solicitud.
            EstadoCesionSolicitada statusCancelada = new EstadoCesionSolicitada();
            statusCancelada.setCodigo(EstadoCesionSolicitada.CANCELADO);
            pCesSol.setEstatus(statusCancelada);
        }

        return checkCancelacion;
    }

    /**
     * Comprueba si es posible la cancelación de una cesión.
     * @param pCesSol Información de la cesión.
     * @return Objeto PeticionCancelacion con la información del proceso.
     * @throws Exception En caso de error.
     */
    private PeticionCancelacion checkCancelCesion(CesionSolicitadaCPSN pCesSol) throws Exception {
        PeticionCancelacion checkCancelacion = new PeticionCancelacion();
        if (pCesSol.getEstatus().getCodigo().equals(EstadoCesionSolicitada.PENDIENTE)) {
            checkCancelacion.setCancelacionPosible(true);
        } else {
            StringBuffer sbAviso = new StringBuffer();
            sbAviso.append("No es posible cancelar la cesión del CPSN con ");
            sbAviso.append("Tipo bloque: ").append(pCesSol.getTipoBloqueCpsn().getDescripcion());
            sbAviso.append(", Binario: ").append(pCesSol.getBinario());
            if (pCesSol.getTipoBloqueCpsn().getId().equals(TipoBloqueCPSN.INDIVIDUAL)) {
                sbAviso.append(", Dec. Total: ").append(pCesSol.getDecimalTotal());
            } else {
                sbAviso.append(", Dec. Red: ").append(pCesSol.getDecimalRed());
            }
            sbAviso.append(". La fecha de implementación ya se ha cumplido: ");
            sbAviso.append(FechasUtils.fechaToString(pCesSol.getFechaImplementacion()));

            checkCancelacion.setCancelacionPosible(false);
            checkCancelacion.setMensajeError(sbAviso.toString());
        }
        return checkCancelacion;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<String> getNotificacionesCesionesPendientes() {

        // Solicitudes en Trámite
        EstadoSolicitud estatusSol = new EstadoSolicitud();
        estatusSol.setCodigo(EstadoSolicitud.SOLICITUD_EN_TRAMITE);

        // Cesiones Solicitadas Reservadas (Pendientes)
        EstadoCesionSolicitada statusCesSol = new EstadoCesionSolicitada();
        statusCesSol.setCodigo(EstadoCesionSolicitada.PENDIENTE);

        // Filtros de búsqueda
        FiltroBusquedaSolicitudesCpsn filtroSolicitudes = new FiltroBusquedaSolicitudesCpsn();
        filtroSolicitudes.setEstado(estatusSol);
        filtroSolicitudes.setEstatusCesSol(statusCesSol);
        filtroSolicitudes.setFechaImplementacionHasta(FechasUtils.calculaFecha(FechasUtils.getFechaHoy(), 2, 0, 0));

        try {
            // Parseamos las fechas para eliminar las horas y minutos
            Date fHoy = FechasUtils.getFechaHoy();

            // Agrupamos las Solicitudes por fecha
            HashMap<String, Integer> tramitesByFecha = new HashMap<String, Integer>(1);
            ArrayList<String> tramitesFallidos = new ArrayList<String>();
            StringBuffer key;

            List<SolicitudCesionCPSN> solicitudes = this.findAllSolicitudesCesion(filtroSolicitudes);
            for (SolicitudCesionCPSN solicitud : solicitudes) {
                for (CesionSolicitadaCPSN cesSol : solicitud.getCesionesSolicitadasCPSN()) {
                    if (cesSol.getEstatus().getCodigo().equals(EstadoCesionSolicitada.PENDIENTE)) {
                        // Si se ha sobrepasado la fecha de cesión es que hay algún problema para en la cesión.
                        if (fHoy.after(cesSol.getFechaImplementacion())) {
                            if (!tramitesFallidos.contains(cesSol.getSolicitudCesionCPSN().getId().toString())) {
                                tramitesFallidos.add(cesSol.getSolicitudCesionCPSN().getId().toString());
                            }
                        } else {
                            // Las notificaciones se generan a partir de 2 días antes de la fecha de implementación
                            if (fHoy.compareTo(FechasUtils.getFechaNotificacion(
                                    cesSol.getFechaImplementacion())) >= 0) {
                                key = new StringBuffer();
                                key.append(FechasUtils.fechaToString(cesSol.getFechaImplementacion()));
                                key.append("-").append(solicitud.getId());
                                if (!tramitesByFecha.containsKey(key.toString())) {
                                    tramitesByFecha.put(key.toString(), new Integer(0));
                                }
                                int oneMore = (tramitesByFecha.get(key.toString())).intValue() + 1;
                                tramitesByFecha.put(key.toString(), new Integer(oneMore));
                            }
                        }
                    }
                }
            }

            // Generamos las notificaciones
            List<String> notificaciones = new ArrayList<String>();
            StringBuffer notificacion = null;
            for (String item : tramitesByFecha.keySet()) {
                // Info de Trámite y Fecha
                String[] keyInfo = item.split("-");
                Integer count = tramitesByFecha.get(item);

                // Notificación
                notificacion = new StringBuffer();
                notificacion.append("<br/>");
                notificacion.append("La Solicitud ").append(keyInfo[1]).append(" tiene ");
                if (count > 1) {
                    notificacion.append(count).append(" cesiones programadas ");
                } else {
                    notificacion.append(count).append(" cesión programada ");
                }

                notificacion.append(" con fecha ").append(keyInfo[0]);
                notificaciones.add(notificacion.toString());
            }

            for (String item : tramitesFallidos) {
                notificacion = new StringBuffer();
                notificacion.append("<br/>");
                notificacion.append("La Solicitud ").append(item);
                notificacion.append(" no ha podido ser ejecutada. Revise el trámite.");
                notificaciones.add(notificacion.toString());
            }

            return notificaciones;

        } catch (Exception e) {
            LOGGER.error("Error inesperado: " + e.getMessage());
            return new ArrayList<String>();
        }
    }

    @Override
    public boolean cpsnEnTramitePendiente(Integer cpsnMin, Integer cpsnMax) {
        EstadoCesionSolicitada estatusCp = new EstadoCesionSolicitada();
        estatusCp.setCodigo(EstadoCesionSolicitada.PENDIENTE);
        EstadoLiberacionSolicitada estatusLp = new EstadoLiberacionSolicitada();
        estatusLp.setCodigo(EstadoLiberacionSolicitada.PENDIENTE);
        EstadoLiberacionSolicitada estatusLr = new EstadoLiberacionSolicitada();
        estatusLr.setCodigo(EstadoLiberacionSolicitada.PERIODO_RESERVA);

        if (solicitudCesionCPSNDao.cpsnEnCesionPendiente(cpsnMin, cpsnMax, estatusCp, FechasUtils.getFechaHoy())) {
            return true;
        } else if (solicitudLiberacionCPSNDao.cpsnEnLiberacionPendiente(cpsnMin, cpsnMax, estatusLp, estatusLr,
                FechasUtils.getFechaHoy())) {
            return true;
        }

        return false;
    }

    @Override
    public List<DetalleConsultaGenerica> findAllSolicitudes(FiltroBusquedaSolicitudesCpsn filtros) {
        List<DetalleConsultaGenerica> listadoConsultaGenerica = new ArrayList<DetalleConsultaGenerica>();
        List<VConsultaGenericaCpsn> resultados = solicitudCpsnDao.findAllSolicitudes(filtros);
        int posDetalle = -1;

        for (VConsultaGenericaCpsn data : resultados) {
            if (posDetalle == -1) {
                listadoConsultaGenerica.add(new DetalleConsultaGenerica(data));
                posDetalle++;
            } else {
                if (listadoConsultaGenerica.get(posDetalle).getConsecutivo().equals(data.getConsecutivo())) {
                    listadoConsultaGenerica.get(posDetalle).addFechaImplementacion(data);
                } else {
                    listadoConsultaGenerica.add(new DetalleConsultaGenerica(data));
                    posDetalle++;
                }
            }
        }

        return listadoConsultaGenerica;
    }

    @Override
    public Integer findAllSolicitudesCount(FiltroBusquedaSolicitudesCpsn filtros) {
        return solicitudCpsnDao.findAllSolicitudesCount(filtros);
    }

    @Override
    public List<TipoSolicitud> findAllTiposSolicitud() {
        return tipoSolicitudDao.findAllTiposSolicitudCpsn();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudAsignacionCpsn applyAsignacionesSolicitadas(SolicitudAsignacionCpsn pSolicitud) throws Exception {
        EstatusAsignacionCps statusAsignado = new EstatusAsignacionCps();
        statusAsignado.setCodigo(EstatusAsignacionCps.ASIGNADO);

        // Asignamos todos los CPSI al Solicitante. Los CPSI que llegan deberían estar en estado Libre.
        boolean asignacionesPendientes = false;
        for (NumeracionAsignadaCpsn cpsnAsig : pSolicitud.getNumeracionAsignadas()) {
            if (cpsnAsig.getEstatus().getCodigo().equals(EstatusAsignacionCps.PENDIENTE)) {
                cpsnService.asignarCpsn(cpsnAsig, pSolicitud.getProveedorSolicitante());
                cpsnAsig.setEstatus(statusAsignado);

                // Indicamos que existían asignaciones pendientes de realizar.
                asignacionesPendientes = true;
            }
        }

        // Es posible que se haya cancelado la solicitud directamente cancelando sus asignaciones individuales.
        boolean solicitudCancelada = pSolicitud.getEstadoSolicitud().getCodigo()
                .equals(EstadoSolicitud.SOLICITUD_CANCELADA);

        if (solicitudCancelada && (!asignacionesPendientes)) {
            return pSolicitud;
        } else {
            // Marcamos la solcitud como Terminada.
            EstadoSolicitud es = new EstadoSolicitud();
            es.setCodigo(EstadoSolicitud.SOLICITUD_TERMINADA);
            pSolicitud.setEstadoSolicitud(es);

            // Guardamos los cambios
            return this.saveSolicitudAsignacion(pSolicitud);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public PeticionCancelacion cancelSolicitudAsignacion(SolicitudAsignacionCpsn pSolicitud) throws Exception {
        // Cargamos las asignaciones de la solicitud y comprobamos que se puedan cancelar.
        SolicitudAsignacionCpsn solicitud = this.getSolicitudAsignacionEagerLoad(pSolicitud);
        List<String> avisosCancelacion = new ArrayList<String>(solicitud.getNumeracionAsignadas().size());
        boolean isCancelacionPosible = true;
        for (NumeracionAsignadaCpsn cpsnAsig : solicitud.getNumeracionAsignadas()) {
            PeticionCancelacion checkCancelacionAsig = this.checkCancelAsignacion(cpsnAsig);
            if (!checkCancelacionAsig.isCancelacionPosible()) {
                avisosCancelacion.add(checkCancelacionAsig.getMensajeError());
            }
            isCancelacionPosible = isCancelacionPosible && checkCancelacionAsig.isCancelacionPosible();
        }

        PeticionCancelacion checkCancelacion = new PeticionCancelacion();
        if (isCancelacionPosible) {
            for (NumeracionAsignadaCpsn cpsnAsig : solicitud.getNumeracionAsignadas()) {
                this.cancelAsignacion(cpsnAsig, false);
            }

            // Cambiamos el status de la Solicitud
            EstadoSolicitud statusSolCancelada = new EstadoSolicitud();
            statusSolCancelada.setCodigo(EstadoSolicitud.SOLICITUD_CANCELADA);
            solicitud.setEstadoSolicitud(statusSolCancelada);
            this.saveSolicitudAsignacion(solicitud);

            // Cancelación efectudada
            checkCancelacion.setCancelacionPosible(true);
        } else {
            StringBuffer sbAvisos = new StringBuffer();
            for (String aviso : avisosCancelacion) {
                sbAvisos.append(aviso).append("<br>");
            }
            checkCancelacion.setCancelacionPosible(false);
            checkCancelacion.setMensajeError(sbAvisos.toString());
        }

        return checkCancelacion;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public PeticionCancelacion cancelAsignacion(NumeracionAsignadaCpsn pCpsnAsig, boolean pUseCheck) throws Exception {
        PeticionCancelacion checkCancelacion = null;
        if (pUseCheck) {
            checkCancelacion = this.checkCancelAsignacion(pCpsnAsig);
        } else {
            checkCancelacion = new PeticionCancelacion();
            checkCancelacion.setCancelacionPosible(true);
        }

        if (checkCancelacion.isCancelacionPosible()) {
            if (pCpsnAsig.getEstatus().getCodigo().equals(EstatusAsignacionCps.ASIGNADO)) {
                // En el método check se comprueba que el CPSI existe.
                CodigoCPSN cpsn = cpsnService.getCodigoCpsn(pCpsnAsig.getTipoBloqueCpsn().getId(),
                        pCpsnAsig.getIdCpsn(), pCpsnAsig.getSolicitudAsignacion().getProveedorSolicitante());

                EstatusCPSN statusLibre = new EstatusCPSN(EstatusCPSN.LIBRE);
                cpsn.setEstatusCPSN(statusLibre);
                cpsn.setFechaCuarentena(null);
                cpsn.setProveedor(null);

                cpsnService.saveCodigoCpsn(cpsn);
            }

            // Las canceladas se ignoran.
            if (!pCpsnAsig.getEstatus().getCodigo().equals(EstatusAsignacionCps.CANCELADO)) {
                // Traza de Bitácora
                StringBuilder sbBitacora = new StringBuilder();
                sbBitacora.append("Cancelada asignación de CPSN ").append(pCpsnAsig.getIdCpsn());
                bitacoraService.add(pCpsnAsig.getSolicitudAsignacion(), sbBitacora.toString());

                // Estado Cancelada para la cesión. Se actualiza al guardar la solicitud.
                EstatusAsignacionCps statusCancelada = new EstatusAsignacionCps();
                statusCancelada.setCodigo(EstatusAsignacionCps.CANCELADO);
                pCpsnAsig.setEstatus(statusCancelada);
            }
        }

        return checkCancelacion;
    }

    /**
     * Comprueba si es posible la cancelación de una asignación.
     * @param pAsigSol Información de la asignación.
     * @return Objeto PeticionCancelacion con la información del proceso.
     * @throws Exception En caso de error.
     */
    private PeticionCancelacion checkCancelAsignacion(NumeracionAsignadaCpsn pAsigSol) throws Exception {
        PeticionCancelacion checkCancelacion = new PeticionCancelacion();
        checkCancelacion.setCancelacionPosible(true);

        // Las Asignaciones en estado Pendiente o cancelada no se revisan.
        if (pAsigSol.getEstatus().getCodigo().equals(EstatusAsignacionCps.ASIGNADO)) {
            // Solo es posible cancelar una asignación si el CPSI sigue asignado al Proveedor Solicitante.
            CodigoCPSN cpsn = cpsnService.getCodigoCpsn(pAsigSol.getTipoBloqueCpsn().getId(), pAsigSol.getIdCpsn(),
                    pAsigSol.getSolicitudAsignacion().getProveedorSolicitante());
            if (cpsn == null) {
                StringBuffer sbAviso = new StringBuffer();
                sbAviso.append("No es posible cancelar la asignación del CPSN con ");
                sbAviso.append("Binario: ").append(pAsigSol.getBinario());
                sbAviso.append(", Decimal: ").append(pAsigSol.getIdCpsn());
                sbAviso.append(". El CPSN ha sido liberado o reasignado a otro Proveedor.");

                checkCancelacion.setMensajeError(sbAviso.toString());
                checkCancelacion.setCancelacionPosible(false);
            } else {
                checkCancelacion.setCancelacionPosible(true);
            }
        }

        return checkCancelacion;
    }

}
