package mx.ift.sns.negocio.cpsi;

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

import mx.ift.sns.dao.cpsi.ISolicitudAsignacionCpsiDao;
import mx.ift.sns.dao.cpsi.ISolicitudCesionCpsiDao;
import mx.ift.sns.dao.cpsi.ISolicitudCpsiDao;
import mx.ift.sns.dao.cpsi.ISolicitudCpsiUitDao;
import mx.ift.sns.dao.cpsi.ISolicitudLiberacionCpsiDao;
import mx.ift.sns.dao.solicitud.IEstadoSolicitudDao;
import mx.ift.sns.dao.solicitud.ITipoSolicitudDao;
import mx.ift.sns.modelo.cps.EstatusAsignacionCps;
import mx.ift.sns.modelo.cpsi.CesionSolicitadaCpsi;
import mx.ift.sns.modelo.cpsi.CodigoCPSI;
import mx.ift.sns.modelo.cpsi.CpsiAsignado;
import mx.ift.sns.modelo.cpsi.CpsiUitEntregado;
import mx.ift.sns.modelo.cpsi.EstatusCPSI;
import mx.ift.sns.modelo.cpsi.EstatusSolicitudCpsiUit;
import mx.ift.sns.modelo.cpsi.LiberacionSolicitadaCpsi;
import mx.ift.sns.modelo.cpsi.SolicitudAsignacionCpsi;
import mx.ift.sns.modelo.cpsi.SolicitudCesionCpsi;
import mx.ift.sns.modelo.cpsi.SolicitudCpsiUit;
import mx.ift.sns.modelo.cpsi.SolicitudLiberacionCpsi;
import mx.ift.sns.modelo.cpsi.VConsultaGenericaCpsi;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudesCPSI;
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
 * Implementación del servicio de Soliciutdes para Codigos CPS Internacionales.
 */
@Stateless(name = "SolicitudesCpsiService", mappedName = "SolicitudesCpsiService")
@Remote(ISolicitudesCpsiService.class)
public class SolicitudesCpsiService implements ISolicitudesCpsiService {

    /** Logger de la clase . */
    private static final Logger LOGGER = LoggerFactory.getLogger(SolicitudesCpsiService.class);

    /** Servicio de Códigos CPSI. */
    @EJB
    private ICodigoCPSIService codigoCpsiService;

    /** Servicio de Bitácora. */
    @EJB
    private IBitacoraService bitacoraService;

    /** DAO de EstadoSolicitud. */
    @Inject
    private IEstadoSolicitudDao estadoSolicitudDao;

    /** DAO De Solicitud de Liberación. */
    @Inject
    private ISolicitudLiberacionCpsiDao solicitudLibDao;

    /** DAO De Solicitud de Cesión. */
    @Inject
    private ISolicitudCesionCpsiDao solicitudCesDao;

    /** DAO De Solicitud de Asignación. */
    @Inject
    private ISolicitudAsignacionCpsiDao solicitudAsigDao;

    /** DAO Solicitudes de CPSI. */
    @Inject
    private ISolicitudCpsiDao solicitudCpsiDao;

    /** DAO de Tipos de solicitud. */
    @Inject
    private ITipoSolicitudDao tipoSolicitudDao;

    /** DAO De Solicitud de códigos a la UIT. */
    @Inject
    private ISolicitudCpsiUitDao solicitudCpsiUitDao;

    /********************************************************************************************/

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<EstadoSolicitud> findAllEstadosSolicitud() {
        return estadoSolicitudDao.findAllEstadosSolicitud();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<SolicitudLiberacionCpsi> findAllSolicitudesLiberacion(FiltroBusquedaSolicitudesCPSI pFiltrosSolicitud) {
        return solicitudLibDao.findAllSolicitudesLiberacion(pFiltrosSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public int findAllSolicitudesLiberacionCount(FiltroBusquedaSolicitudesCPSI pFiltrosSolicitud) {
        return solicitudLibDao.findAllSolicitudesLiberacionCount(pFiltrosSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudLiberacionCpsi saveSolicitudLiberacion(SolicitudLiberacionCpsi pSolicitud) {
        return solicitudLibDao.saveOrUpdate(pSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public SolicitudLiberacionCpsi getSolicitudLiberacionById(BigDecimal pConsecutivo) {
        return solicitudLibDao.getSolicitudLiberacionById(pConsecutivo);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudLiberacionCpsi getSolicitudLiberacionEagerLoad(SolicitudLiberacionCpsi pSolicitud) {
        return solicitudLibDao.getSolicitudLiberacionEagerLoad(pSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public SolicitudLiberacionCpsi applyLiberacionesSolicitadas(SolicitudLiberacionCpsi pSolicitud) throws Exception {

        // Parseamos las fechas para eliminar las horas y minutos
        Date fHoy = FechasUtils.getFechaHoy();

        EstadoLiberacionSolicitada estatusLiberado = new EstadoLiberacionSolicitada();
        estatusLiberado.setCodigo(EstadoLiberacionSolicitada.LIBERADO);

        boolean todoLiberado = true;
        boolean liberacionesPendientes = false;
        for (LiberacionSolicitadaCpsi libSol : pSolicitud.getLiberacionesSolicitadas()) {
            // Ignoramos las liberaciones que ya se hayan ejecutado.
            if (libSol.getEstatus().getCodigo().equals(EstadoLiberacionSolicitada.PENDIENTE)) {
                // Fecha de Implementación
                Date fImplementacion = FechasUtils.parseFecha(libSol.getFechaImplementacion());

                // Liberación
                String resultado = codigoCpsiService.liberarCpsi(libSol, fHoy.equals(fImplementacion));
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
        FiltroBusquedaSolicitudesCPSI filtroSolicitudes = new FiltroBusquedaSolicitudesCPSI();
        filtroSolicitudes.setEstado(estatusSol);
        filtroSolicitudes.setEstatusLibSol(statusLibSol);

        List<SolicitudLiberacionCpsi> solicitudes = this.findAllSolicitudesLiberacion(filtroSolicitudes);
        for (SolicitudLiberacionCpsi solicitud : solicitudes) {
            if (!solicitud.getLiberacionesSolicitadas().isEmpty()) {
                try {
                    bitacoraService.add(solicitud,
                            "Comprobando Liberaciones CPSI pendientes de Solicitud " + solicitud.getId());

                    // Hacemos una validación previa de que aún existen los códigos CPSI
                    boolean codigosOk = true;
                    for (LiberacionSolicitadaCpsi libSol : solicitud.getLiberacionesSolicitadas()) {
                        if (libSol.getEstatus().getCodigo().equals(EstadoLiberacionSolicitada.PENDIENTE)) {
                            CodigoCPSI cpsi = codigoCpsiService.getCodigoCpsi(
                                    libSol.getIdCpsi(), libSol.getSolicitudLiberacion().getProveedorSolicitante());

                            if (cpsi == null) {
                                StringBuilder sbTraza = new StringBuilder();
                                sbTraza.append("Código CPSI no encontrado. ");
                                sbTraza.append("Id: ").append(libSol.getIdCpsi()).append(" ");
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
                    LOGGER.error("Error ejecutando Liberaciones de CPSI pendientes.", e);
                }
            }
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public PeticionCancelacion cancelSolicitudLiberacion(SolicitudLiberacionCpsi pSolicitud) throws Exception {

        // Cargamos las liberaciones de la solicitud y comprobamos que se puedan cancelar.
        SolicitudLiberacionCpsi solicitud = this.getSolicitudLiberacionEagerLoad(pSolicitud);
        List<String> avisosCancelacion = new ArrayList<String>(solicitud.getLiberacionesSolicitadas().size());
        boolean isCancelacionPosible = true;
        for (LiberacionSolicitadaCpsi libSol : solicitud.getLiberacionesSolicitadas()) {
            PeticionCancelacion checkCancelacionLib = this.checkCancelLiberacion(libSol);
            if (!checkCancelacionLib.isCancelacionPosible()) {
                avisosCancelacion.add(checkCancelacionLib.getMensajeError());
            }
            isCancelacionPosible = isCancelacionPosible && checkCancelacionLib.isCancelacionPosible();
        }

        // Si todas las liberaciones se pueden efectuar procedemos.
        PeticionCancelacion checkCancelacion = new PeticionCancelacion();
        if (isCancelacionPosible) {
            for (LiberacionSolicitadaCpsi libSol : solicitud.getLiberacionesSolicitadas()) {
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
    public PeticionCancelacion cancelLiberacion(LiberacionSolicitadaCpsi pLibSol, boolean pUseCheck) throws Exception {
        PeticionCancelacion checkCancelacion = null;
        if (pUseCheck) {
            checkCancelacion = this.checkCancelLiberacion(pLibSol);
        } else {
            checkCancelacion = new PeticionCancelacion();
            checkCancelacion.setCancelacionPosible(true);
        }

        if (checkCancelacion.isCancelacionPosible()) {

            // Comprobamos si el CPSI sigue existiendo tal y como estaba cuando se creó la liberación solicitada.
            CodigoCPSI cpsi = codigoCpsiService.getCodigoCpsi(pLibSol.getIdCpsi(), null);

            boolean cpsiSinModificar = ((cpsi != null) // Si cpsi es nulo no se evalúa el resto.
                    && ((cpsi.getEstatus().getId().equals(EstatusCPSI.CUARENTENA)
                    || cpsi.getEstatus().getId().equals(EstatusCPSI.PLANIFICADO))));

            StringBuilder sbBitacora = new StringBuilder();
            if (cpsiSinModificar) {
                // Reestablecemos el CPSI como asignado al Proveedor Solicitante.
                EstatusCPSI status = new EstatusCPSI();
                status.setId(EstatusCPSI.ASIGNADO);

                cpsi.setEstatus(status);
                cpsi.setFechaFinCuarentena(null);
                cpsi.setProveedor(pLibSol.getSolicitudLiberacion().getProveedorSolicitante());
                codigoCpsiService.saveCodigoCPSI(cpsi);

                sbBitacora.append("Cancelada Liberación de CPSI. ").append(cpsi);
                sbBitacora.append("El CPSI ha sido reasignado al Proveedor ");
                sbBitacora.append(pLibSol.getSolicitudLiberacion().getProveedorSolicitante().getNombre());
                bitacoraService.add(pLibSol.getSolicitudLiberacion(), sbBitacora.toString());
            } else {
                sbBitacora.append("Cancelada Liberación de CPSI. ");
                sbBitacora.append("CPSI = {").append("Id: ").append(pLibSol.getIdCpsi());
                sbBitacora.append(", Binario: ").append(cpsi.getBinario());
                sbBitacora.append(", Decimal: ").append(cpsi.getDecimalTotal());
                sbBitacora.append(", Formato Dec.: ").append(cpsi.getFormatoDecimal());
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
    private PeticionCancelacion checkCancelLiberacion(LiberacionSolicitadaCpsi pLibSol) throws Exception {
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
            sbAviso.append("No es posible cancelar la liberación del CPSI con ");
            sbAviso.append("Binario: ").append(pLibSol.getBinario());
            sbAviso.append(", Decimal: ").append(pLibSol.getDecimal());
            sbAviso.append(", Formato Dec.: ").append(pLibSol.getFormatoDecimal());
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
        FiltroBusquedaSolicitudesCPSI filtroSolicitudes = new FiltroBusquedaSolicitudesCPSI();
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

            List<SolicitudLiberacionCpsi> solicitudes = this.findAllSolicitudesLiberacion(filtroSolicitudes);
            for (SolicitudLiberacionCpsi solicitud : solicitudes) {
                for (LiberacionSolicitadaCpsi libSol : solicitud.getLiberacionesSolicitadas()) {
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
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<SolicitudCesionCpsi> findAllSolicitudesCesion(FiltroBusquedaSolicitudesCPSI pFiltrosSolicitud) {
        return solicitudCesDao.findAllSolicitudesCesion(pFiltrosSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public int findAllSolicitudesCesionCount(FiltroBusquedaSolicitudesCPSI pFiltrosSolicitud) {
        return solicitudCesDao.findAllSolicitudesCesionCount(pFiltrosSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public SolicitudCesionCpsi getSolicitudCesionById(BigDecimal pConsecutivo) {
        return solicitudCesDao.getSolicitudCesionById(pConsecutivo);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudCesionCpsi saveSolicitudCesion(SolicitudCesionCpsi pSolicitud) {
        return solicitudCesDao.saveOrUpdate(pSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudCesionCpsi getSolicitudCesionEagerLoad(SolicitudCesionCpsi pSolicitud) {
        return solicitudCesDao.getSolicitudCesionEagerLoad(pSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public SolicitudCesionCpsi applyCesionesSolicitadas(SolicitudCesionCpsi pSolicitud) throws Exception {

        // Parseamos las fechas para eliminar las horas y minutos
        Date fHoy = FechasUtils.getFechaHoy();

        EstadoCesionSolicitada estatusCedido = new EstadoCesionSolicitada();
        estatusCedido.setCodigo(EstadoCesionSolicitada.CEDIDO);

        boolean todoCedido = true;
        boolean cesionesPendientes = false;
        for (CesionSolicitadaCpsi cesSol : pSolicitud.getCesionesSolicitadas()) {
            // Ignoramos las cesiones que ya se hayan ejecutado.
            if (cesSol.getEstatus().getCodigo().equals(EstadoCesionSolicitada.PENDIENTE)) {
                // Fecha de Implementación
                Date fImplementacion = FechasUtils.parseFecha(cesSol.getFechaImplementacion());

                // Cesión
                String resultado = codigoCpsiService.cederCpsi(cesSol, fHoy.equals(fImplementacion));
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
        FiltroBusquedaSolicitudesCPSI filtroSolicitudes = new FiltroBusquedaSolicitudesCPSI();
        filtroSolicitudes.setEstado(estatusSol);
        filtroSolicitudes.setEstatusCesSol(statusCesSol);

        List<SolicitudCesionCpsi> solicitudes = this.findAllSolicitudesCesion(filtroSolicitudes);
        for (SolicitudCesionCpsi solicitud : solicitudes) {
            if (solicitud.getCesionesSolicitadas() != null && !solicitud.getCesionesSolicitadas().isEmpty()) {
                try {
                    bitacoraService.add(solicitud,
                            "Comprobando Cesiones CPSI pendientes de Solicitud " + solicitud.getId());

                    // Hacemos una validación previa de que aún existen los códigos CPSI
                    boolean codigosOk = true;
                    for (CesionSolicitadaCpsi cesSol : solicitud.getCesionesSolicitadas()) {
                        if (cesSol.getEstatus().getCodigo().equals(EstadoCesionSolicitada.PENDIENTE)) {
                            CodigoCPSI cpsi = codigoCpsiService.getCodigoCpsi(
                                    cesSol.getIdCpsi(),
                                    cesSol.getSolicitudCesion().getProveedorSolicitante());

                            if (cpsi == null) {
                                StringBuilder sbTraza = new StringBuilder();
                                sbTraza.append("Código CPSI no encontrado. ");
                                sbTraza.append("Id: ").append(cesSol.getIdCpsi()).append(" ");
                                sbTraza.append("Pst: ").append(cesSol.getSolicitudCesion().getProveedorSolicitante());
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
                    LOGGER.error("error ejecutando Cesiones de CPSI pendientes.", e);
                }
            }
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public PeticionCancelacion cancelSolicitudCesion(SolicitudCesionCpsi pSolicitud) throws Exception {
        // Cargamos las cesiones de la solicitud y comprobamos que se puedan cancelar.
        SolicitudCesionCpsi solicitud = this.getSolicitudCesionEagerLoad(pSolicitud);
        List<String> avisosCancelacion = new ArrayList<String>(solicitud.getCesionesSolicitadas().size());
        boolean isCancelacionPosible = true;
        for (CesionSolicitadaCpsi cesSol : solicitud.getCesionesSolicitadas()) {
            PeticionCancelacion checkCancelacionCes = this.checkCancelCesion(cesSol);
            if (!checkCancelacionCes.isCancelacionPosible()) {
                avisosCancelacion.add(checkCancelacionCes.getMensajeError());
            }
            isCancelacionPosible = isCancelacionPosible && checkCancelacionCes.isCancelacionPosible();
        }

        PeticionCancelacion checkCancelacion = new PeticionCancelacion();
        if (isCancelacionPosible) {
            for (CesionSolicitadaCpsi cesSol : solicitud.getCesionesSolicitadas()) {
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
    public PeticionCancelacion cancelCesion(CesionSolicitadaCpsi pCesSol, boolean pUseCheck) throws Exception {
        PeticionCancelacion checkCancelacion = null;
        if (pUseCheck) {
            checkCancelacion = this.checkCancelCesion(pCesSol);
        } else {
            checkCancelacion = new PeticionCancelacion();
            checkCancelacion.setCancelacionPosible(true);
        }

        // Solamente se permiten hacer cancelaciones de cesiones que no se han realizado. Por lo tanto,
        // para cancelar una cesión solamente es necesario cancelar el trámite (la cesión solicitada).
        // El CPSI se queda como está.
        // Si el CPSI ha sido modificado se permite cancelar igualmente ya que el CPSI no se ve afectado.

        if (checkCancelacion.isCancelacionPosible()) {
            // Comprobamos que el CPSI no se haya asignado a otro proveedor o desasignado.
            CodigoCPSI cpsi = codigoCpsiService.getCodigoCpsi(
                    pCesSol.getIdCpsi(),
                    pCesSol.getSolicitudCesion().getProveedorSolicitante());

            if (cpsi != null) {
                // Si el CPSI no ha sido modificado lo actualizamos como Asignado (por si estaba reservado)
                EstatusCPSI statusAsignado = new EstatusCPSI(EstatusCPSI.ASIGNADO);
                cpsi.setEstatus(statusAsignado);
                cpsi.setFechaFinCuarentena(null);

                codigoCpsiService.saveCodigoCPSI(cpsi);
            }

            // Traza de Bitácora
            StringBuilder sbBitacora = new StringBuilder();
            sbBitacora.append("Cancelada cesión de CPSI ").append(pCesSol.getIdCpsi());
            bitacoraService.add(pCesSol.getSolicitudCesion(), sbBitacora.toString());

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
    private PeticionCancelacion checkCancelCesion(CesionSolicitadaCpsi pCesSol) throws Exception {
        PeticionCancelacion checkCancelacion = new PeticionCancelacion();
        if (pCesSol.getEstatus().getCodigo().equals(EstadoCesionSolicitada.PENDIENTE)) {
            checkCancelacion.setCancelacionPosible(true);
        } else {
            StringBuffer sbAviso = new StringBuffer();
            sbAviso.append("No es posible cancelar la cesión del CPSI con ");
            sbAviso.append("Binario: ").append(pCesSol.getBinario());
            sbAviso.append(", Decimal: ").append(pCesSol.getIdCpsi());
            sbAviso.append(", Formato Dec.: ").append(pCesSol.getFormatoDecimal());
            sbAviso.append(". La fecha de implementación (");
            sbAviso.append(FechasUtils.fechaToString(pCesSol.getFechaImplementacion()));
            sbAviso.append(") ya se han cumplido.");

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
        FiltroBusquedaSolicitudesCPSI filtroSolicitudes = new FiltroBusquedaSolicitudesCPSI();
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

            List<SolicitudCesionCpsi> solicitudes = this.findAllSolicitudesCesion(filtroSolicitudes);
            for (SolicitudCesionCpsi solicitud : solicitudes) {
                for (CesionSolicitadaCpsi cesSol : solicitud.getCesionesSolicitadas()) {
                    if (cesSol.getEstatus().getCodigo().equals(EstadoCesionSolicitada.PENDIENTE)) {
                        // Si se ha sobrepasado la fecha de cesión es que hay algún problema para en la cesión.
                        if (fHoy.after(cesSol.getFechaImplementacion())) {
                            if (!tramitesFallidos.contains(cesSol.getSolicitudCesion().getId().toString())) {
                                tramitesFallidos.add(cesSol.getSolicitudCesion().getId().toString());
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
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<SolicitudAsignacionCpsi> findAllSolicitudesAsignacion(FiltroBusquedaSolicitudesCPSI pFiltrosSolicitud) {
        return solicitudAsigDao.findAllSolicitudesAsignacion(pFiltrosSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public int findAllSolicitudesAsignacionCount(FiltroBusquedaSolicitudesCPSI pFiltrosSolicitud) {
        return solicitudAsigDao.findAllSolicitudesAsignacionCount(pFiltrosSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudAsignacionCpsi saveSolicitudAsignacion(SolicitudAsignacionCpsi pSolicitud) {
        return solicitudAsigDao.saveOrUpdate(pSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public SolicitudAsignacionCpsi getSolicitudAsignacionById(BigDecimal pConsecutivo) {
        return solicitudAsigDao.getSolicitudAsignacionById(pConsecutivo);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudAsignacionCpsi getSolicitudAsignacionEagerLoad(SolicitudAsignacionCpsi pSolicitud) {
        return solicitudAsigDao.getSolicitudAsignacionEagerLoad(pSolicitud);
    }

    @Override
    public List<TipoSolicitud> findAllTiposSolicitud() {
        return tipoSolicitudDao.findAllTiposSolicitudCpsi();
    }

    @Override
    public List<DetalleConsultaGenerica> findAllSolicitudes(FiltroBusquedaSolicitudesCPSI filtros) {
        List<DetalleConsultaGenerica> listadoConsultaGenerica = new ArrayList<DetalleConsultaGenerica>();
        List<VConsultaGenericaCpsi> resultados = solicitudCpsiDao.findAllSolicitudes(filtros);
        int posDetalle = -1;

        for (VConsultaGenericaCpsi data : resultados) {
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
    public Integer findAllSolicitudesCount(FiltroBusquedaSolicitudesCPSI filtros) {
        return solicitudCpsiDao.findAllSolicitudesCount(filtros);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudAsignacionCpsi applyAsignacionesSolicitadas(SolicitudAsignacionCpsi pSolicitud) throws Exception {
        EstatusAsignacionCps statusAsignado = new EstatusAsignacionCps();
        statusAsignado.setCodigo(EstatusAsignacionCps.ASIGNADO);

        // Asignamos todos los CPSI al Solicitante. Los CPSI que llegan deberían estar en estado Libre.
        boolean asignacionesPendientes = false;
        for (CpsiAsignado cpsiAsig : pSolicitud.getCpsiAsignados()) {
            if (cpsiAsig.getEstatus().getCodigo().equals(EstatusAsignacionCps.PENDIENTE)) {
                codigoCpsiService.asignarCpsi(cpsiAsig, pSolicitud.getProveedorSolicitante());
                cpsiAsig.setEstatus(statusAsignado);

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
    public PeticionCancelacion cancelSolicitudAsignacion(SolicitudAsignacionCpsi pSolicitud) throws Exception {
        // Cargamos las asignaciones de la solicitud y comprobamos que se puedan cancelar.
        SolicitudAsignacionCpsi solicitud = this.getSolicitudAsignacionEagerLoad(pSolicitud);
        List<String> avisosCancelacion = new ArrayList<String>(solicitud.getCpsiAsignados().size());
        boolean isCancelacionPosible = true;
        for (CpsiAsignado cpsiAsig : solicitud.getCpsiAsignados()) {
            PeticionCancelacion checkCancelacionAsig = this.checkCancelAsignacion(cpsiAsig);
            if (!checkCancelacionAsig.isCancelacionPosible()) {
                avisosCancelacion.add(checkCancelacionAsig.getMensajeError());
            }
            isCancelacionPosible = isCancelacionPosible && checkCancelacionAsig.isCancelacionPosible();
        }

        PeticionCancelacion checkCancelacion = new PeticionCancelacion();
        if (isCancelacionPosible) {
            for (CpsiAsignado cpsiAsig : solicitud.getCpsiAsignados()) {
                this.cancelAsignacion(cpsiAsig, false);
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
    public PeticionCancelacion cancelAsignacion(CpsiAsignado pCpsiAsig, boolean pUseCheck) throws Exception {
        PeticionCancelacion checkCancelacion = null;
        if (pUseCheck) {
            checkCancelacion = this.checkCancelAsignacion(pCpsiAsig);
        } else {
            checkCancelacion = new PeticionCancelacion();
            checkCancelacion.setCancelacionPosible(true);
        }

        if (checkCancelacion.isCancelacionPosible()) {
            if (pCpsiAsig.getEstatus().getCodigo().equals(EstatusAsignacionCps.ASIGNADO)) {
                // En el método check se comprueba que el CPSI existe.
                CodigoCPSI cpsi = codigoCpsiService.getCodigoCpsi(
                        pCpsiAsig.getIdCpsi(),
                        pCpsiAsig.getSolicitudAsignacion().getProveedorSolicitante());

                EstatusCPSI statusLibre = new EstatusCPSI(EstatusCPSI.LIBRE);
                cpsi.setEstatus(statusLibre);
                cpsi.setFechaFinCuarentena(null);
                cpsi.setProveedor(null);

                codigoCpsiService.saveCodigoCPSI(cpsi);
            }

            // Las canceladas se ignoran.
            if (!pCpsiAsig.getEstatus().getCodigo().equals(EstatusAsignacionCps.CANCELADO)) {
                // Traza de Bitácora
                StringBuilder sbBitacora = new StringBuilder();
                sbBitacora.append("Cancelada asignación de CPSI ").append(pCpsiAsig.getIdCpsi());
                bitacoraService.add(pCpsiAsig.getSolicitudAsignacion(), sbBitacora.toString());

                // Estado Cancelada para la cesión. Se actualiza al guardar la solicitud.
                EstatusAsignacionCps statusCancelada = new EstatusAsignacionCps();
                statusCancelada.setCodigo(EstatusAsignacionCps.CANCELADO);
                pCpsiAsig.setEstatus(statusCancelada);
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
    private PeticionCancelacion checkCancelAsignacion(CpsiAsignado pAsigSol) throws Exception {
        PeticionCancelacion checkCancelacion = new PeticionCancelacion();
        checkCancelacion.setCancelacionPosible(true);

        // Las Asignaciones en estado Pendiente o cancelada no se revisan.
        if (pAsigSol.getEstatus().getCodigo().equals(EstatusAsignacionCps.ASIGNADO)) {
            // Solo es posible cancelar una asignación si el CPSI sigue asignado al Proveedor Solicitante.
            CodigoCPSI cpsi = codigoCpsiService.getCodigoCpsi(
                    pAsigSol.getIdCpsi(),
                    pAsigSol.getSolicitudAsignacion().getProveedorSolicitante());

            if (cpsi == null) {
                StringBuffer sbAviso = new StringBuffer();
                sbAviso.append("No es posible cancelar la asignación del CPSI con ");
                sbAviso.append("Binario: ").append(pAsigSol.getBinario());
                sbAviso.append(", Decimal: ").append(pAsigSol.getIdCpsi());
                sbAviso.append(", Formato Dec.: ").append(pAsigSol.getFormatoDecimal());
                sbAviso.append(". El CPSI ha sido liberado o reasignado a otro Proveedor.");

                checkCancelacion.setMensajeError(sbAviso.toString());
                checkCancelacion.setCancelacionPosible(false);
            }
        }

        return checkCancelacion;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudCpsiUit saveSolicitudCpsiUit(SolicitudCpsiUit pSolicitud) {
        return solicitudCpsiUitDao.saveOrUpdate(pSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public int findAllSolicitudesCpsiUitCount(FiltroBusquedaSolicitudesCPSI pFiltrosSolicitud) {
        return solicitudCpsiUitDao.findAllSolicitudesCpsiUitCount(pFiltrosSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<SolicitudCpsiUit> findAllSolicitudesCpsiUit(FiltroBusquedaSolicitudesCPSI pFiltrosSolicitud) {
        return solicitudCpsiUitDao.findAllSolicitudesCpsiUit(pFiltrosSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudCpsiUit getSolicitudCpsiUitEagerLoad(SolicitudCpsiUit pSolicitud) {
        return solicitudCpsiUitDao.getSolicitudCpsiUitEagerLoad(pSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public SolicitudCpsiUit getSolicitudCpsiUitById(BigDecimal pConsecutivo) {
        return solicitudCpsiUitDao.getSolicitudCpsiUitById(pConsecutivo);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public int findSolicitudCpsiUitByCodAndEstatusCount(FiltroBusquedaSolicitudesCPSI filtro) {
        return solicitudCpsiUitDao.findSolicitudCpsiUitByCodAndEstatusCount(filtro);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public PeticionCancelacion cancelSolicitud(CpsiUitEntregado pCpsiUitEnt, boolean pUseCheck) throws Exception {
        PeticionCancelacion checkCancelacion = null;
        if (pUseCheck) {
            checkCancelacion = this.checkCancelSolicitud(pCpsiUitEnt);
        } else {
            checkCancelacion = new PeticionCancelacion();
            checkCancelacion.setCancelacionPosible(true);
        }

        if (checkCancelacion.isCancelacionPosible()) {
            if (pCpsiUitEnt.getEstatus().getCodigo().equals(EstatusSolicitudCpsiUit.ENTREGADO)) {
                // En el método check se comprueba que el CPSI existe.
                CodigoCPSI cpsi = codigoCpsiService.getCodigoCpsi(
                        pCpsiUitEnt.getIdCpsi(), null);

                boolean estaLibre = false;
                for (int i = cpsi.getId().intValue(); i <= cpsi.getId().intValue() + 7; i++) {
                    CodigoCPSI cpsiSiguiente = codigoCpsiService.getCodigoCpsi(
                            new BigDecimal(i), null);
                    if (cpsiSiguiente.getEstatus().getId().equals(EstatusCPSI.LIBRE)) {
                        estaLibre = true;
                    } else {
                        estaLibre = false;
                        break;
                    }
                }
                if (estaLibre) {
                    for (int i = cpsi.getId().intValue(); i <= cpsi.getId().intValue() + 7; i++) {
                        CodigoCPSI cpsiSiguiente = codigoCpsiService.getCodigoCpsi(
                                new BigDecimal(i), null);
                        codigoCpsiService.removeCodigoCpsi(cpsiSiguiente);
                    }
                } else {
                    StringBuffer sbAviso = new StringBuffer();
                    sbAviso.append("No es posible cancelar la solicitud de códigos CPSI a la UIT,"
                            + " ya que hay códigos que no estan Libres ");

                    checkCancelacion.setMensajeError(sbAviso.toString());
                    checkCancelacion.setCancelacionPosible(false);
                }
            }

            // Las canceladas se ignoran.
            if (!pCpsiUitEnt.getEstatus().getCodigo().equals(EstatusSolicitudCpsiUit.CANCELADO)) {
                // Traza de Bitácora
                StringBuilder sbBitacora = new StringBuilder();
                sbBitacora.append("Cancelada la solicitud de códigos CPSI ").append(pCpsiUitEnt.getIdCpsi());
                bitacoraService.add(pCpsiUitEnt.getSolicitudUit(), sbBitacora.toString());

                // Estado Cancelada para la cesión. Se actualiza al guardar la solicitud.
                EstatusSolicitudCpsiUit statusCancelada = new EstatusSolicitudCpsiUit();
                statusCancelada.setCodigo(EstatusSolicitudCpsiUit.CANCELADO);
                pCpsiUitEnt.setEstatus(statusCancelada);
            }
        }

        return checkCancelacion;
    }

    /**
     * Comprueba si es posible la cancelación de una solicitud.
     * @param pEntSol Información de la asignación.
     * @return Objeto PeticionCancelacion con la información del proceso.
     * @throws Exception En caso de error.
     */
    private PeticionCancelacion checkCancelSolicitud(CpsiUitEntregado pEntSol) throws Exception {
        PeticionCancelacion checkCancelacion = new PeticionCancelacion();
        checkCancelacion.setCancelacionPosible(true);

        // Las Solicitudes en estado Pendiente o cancelada no se revisan.
        if (pEntSol.getEstatus().getCodigo().equals(EstatusSolicitudCpsiUit.ENTREGADO)) {
            // Solo es posible cancelar una asignación si el CPSI sigue asignado al Proveedor Solicitante.
            CodigoCPSI cpsi = codigoCpsiService.getCodigoCpsi(
                    pEntSol.getIdCpsi(),
                    null);

            if (cpsi == null) {
                StringBuffer sbAviso = new StringBuffer();
                sbAviso.append("No es posible cancelar la solicitud de códigos CPSI a la UIT con ");
                sbAviso.append("Binario: ").append(pEntSol.getBinario());
                sbAviso.append(", Decimal: ").append(pEntSol.getIdCpsi());
                sbAviso.append(", Formato Dec.: ").append(pEntSol.getFormatoDecimal());
                sbAviso.append(". El CPSI ha sido asignado.");

                checkCancelacion.setMensajeError(sbAviso.toString());
                checkCancelacion.setCancelacionPosible(false);
            }
        }

        return checkCancelacion;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudCpsiUit applySolicitudCpsiUit(SolicitudCpsiUit pSolicitud) throws Exception {

        EstatusSolicitudCpsiUit estatusEntregado = new EstatusSolicitudCpsiUit();
        estatusEntregado.setCodigo(EstatusSolicitudCpsiUit.ENTREGADO);

        for (CpsiUitEntregado entSol : pSolicitud.getCpsiUitEntregado()) {
            if (entSol.getEstatus().getCodigo().equals(EstadoLiberacionSolicitada.PENDIENTE)) {

                EstatusCPSI estadoCpsi = new EstatusCPSI();
                estadoCpsi.setId(EstatusCPSI.LIBRE);

                int contador = 0;

                // Creación de los códigos CPSI solicitados
                // A partir del id del cpsi se tienen que crear todos los bloques individuales (desde el 0 al 7)
                for (int i = 0; i < 8; i++) {
                    CodigoCPSI codigo = new CodigoCPSI();
                    codigo.setId(new BigDecimal(entSol.getIdCpsi().intValue() + i));
                    codigo.setEstatus(estadoCpsi);
                    codigoCpsiService.saveCodigoCPSI(codigo);
                    contador = contador + 1;
                }

                if (contador == 8) {
                    entSol.setEstatus(estatusEntregado);
                }
            }
        }

        // Es posible que se haya cancelado la solicitud directamente cancelando sus solicitudes.
        boolean solicitudCancelada = pSolicitud.getEstadoSolicitud().getCodigo()
                .equals(EstadoSolicitud.SOLICITUD_CANCELADA);

        EstadoSolicitud es = new EstadoSolicitud();

        if (solicitudCancelada) {
            // Es posible que se haya cancelado la solicitud directamente cancelando sus solicitudes.
            return pSolicitud;
        } else {
            // Marcamos la solcitud como Terminada.
            es.setCodigo(EstadoSolicitud.SOLICITUD_TERMINADA);
            pSolicitud.setFechaAsignacion(new Date());
        }

        pSolicitud.setEstadoSolicitud(es);

        // Guardamos los cambios
        return this.saveSolicitudCpsiUit(pSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public PeticionCancelacion cancelSolicitud(SolicitudCpsiUit pSolicitud) throws Exception {
        // Cargamos las asignaciones de la solicitud y comprobamos que se puedan cancelar.
        SolicitudCpsiUit solicitud = this.getSolicitudCpsiUitEagerLoad(pSolicitud);
        List<String> avisosCancelacion = new ArrayList<String>(solicitud.getCpsiUitEntregado().size());
        boolean isCancelacionPosible = true;
        for (CpsiUitEntregado cpsiEnt : solicitud.getCpsiUitEntregado()) {
            PeticionCancelacion checkCancelacionSol = this.checkCancelSolicitud(cpsiEnt);
            if (!checkCancelacionSol.isCancelacionPosible()) {
                avisosCancelacion.add(checkCancelacionSol.getMensajeError());
            }
            isCancelacionPosible = isCancelacionPosible && checkCancelacionSol.isCancelacionPosible();
        }

        PeticionCancelacion checkCancelacion = new PeticionCancelacion();
        if (isCancelacionPosible) {
            for (CpsiUitEntregado cpsiEnt : solicitud.getCpsiUitEntregado()) {
                this.cancelSolicitud(cpsiEnt, false);
            }

            // Cambiamos el status de la Solicitud
            EstadoSolicitud statusSolCancelada = new EstadoSolicitud();
            statusSolCancelada.setCodigo(EstadoSolicitud.SOLICITUD_CANCELADA);
            solicitud.setEstadoSolicitud(statusSolCancelada);
            this.saveSolicitudCpsiUit(solicitud);

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
}
