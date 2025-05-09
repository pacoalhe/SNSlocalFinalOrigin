package mx.ift.sns.negocio.nng;

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

import mx.ift.sns.dao.nng.INumeracionAsignadaNngDao;
import mx.ift.sns.dao.nng.INumeracionSolicitadaNngDao;
import mx.ift.sns.dao.nng.ISolicitudAsignacionNngDao;
import mx.ift.sns.dao.nng.ISolicitudCesionNngDao;
import mx.ift.sns.dao.nng.ISolicitudLiberacionNngDao;
import mx.ift.sns.dao.nng.ISolicitudLineasActivasNngDao;
import mx.ift.sns.dao.nng.ISolicitudRedistribucionNngDao;
import mx.ift.sns.dao.nng.ITipoAsignacionDao;
import mx.ift.sns.dao.solicitud.IEstadoSolicitudDao;
import mx.ift.sns.dao.solicitud.ISolicitudDao;
import mx.ift.sns.dao.solicitud.ITipoSolicitudDao;
import mx.ift.sns.modelo.filtros.FiltroBusquedaHistoricoRangos;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudes;
import mx.ift.sns.modelo.nng.CesionSolicitadaNng;
import mx.ift.sns.modelo.nng.HistoricoRangoSerieNng;
import mx.ift.sns.modelo.nng.LiberacionSolicitadaNng;
import mx.ift.sns.modelo.nng.NumeracionAsignadaNng;
import mx.ift.sns.modelo.nng.NumeracionSolicitadaNng;
import mx.ift.sns.modelo.nng.RangoSerieNng;
import mx.ift.sns.modelo.nng.RedistribucionSolicitadaNng;
import mx.ift.sns.modelo.nng.SolicitudAsignacionNng;
import mx.ift.sns.modelo.nng.SolicitudCesionNng;
import mx.ift.sns.modelo.nng.SolicitudLiberacionNng;
import mx.ift.sns.modelo.nng.SolicitudLineasActivasNng;
import mx.ift.sns.modelo.nng.SolicitudRedistribucionNng;
import mx.ift.sns.modelo.nng.TipoAsignacion;
import mx.ift.sns.modelo.series.EstadoRango;
import mx.ift.sns.modelo.solicitud.EstadoCesionSolicitada;
import mx.ift.sns.modelo.solicitud.EstadoLiberacionSolicitada;
import mx.ift.sns.modelo.solicitud.EstadoRedistribucionSolicitada;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.modelo.solicitud.Solicitud;
import mx.ift.sns.modelo.solicitud.TipoSolicitud;
import mx.ift.sns.negocio.IBitacoraService;
import mx.ift.sns.negocio.PeticionCancelacion;
import mx.ift.sns.negocio.psts.IProveedoresService;
import mx.ift.sns.utils.date.FechasUtils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación de los métodos del Servicio de Solicitudes de Numeración No Geográfica.
 */
@Stateless(name = "SolicitudesNngService", mappedName = "SolicitudesNngService")
@Remote(ISolicitudesNngService.class)
public class SolicitudesNngService implements ISolicitudesNngService {

    /** Logger de la clase . */
    private static final Logger LOGGER = LoggerFactory.getLogger(SolicitudesNngService.class);

    /** Servicio de Series NNG. */
    @EJB
    private ISeriesNngService seriesNngService;

    /** Servicio de Proveedores NNG. */
    @EJB
    private IProveedoresService proveedoresService;

    /** Servicio de Bitácora. */
    @EJB
    private IBitacoraService bitacoraService;

    /** DAO de Solicitudes de Cesión NNG. */
    @Inject
    private ISolicitudCesionNngDao solicitudCesionNngDao;

    /** DAO de Solicitudes de Asignación NNG. */
    @Inject
    private ISolicitudAsignacionNngDao solicitudAsignacionNngDao;

    /** DAO de Solicitudes de Asignación NNG. */
    @Inject
    private ISolicitudLineasActivasNngDao solicitudLinActNngDao;

    /** DAO de Numeracion Solicitada NNG. */
    @Inject
    private INumeracionSolicitadaNngDao numeracionSolicitadaNngDao;

    /** DAO de Numeracion Asignada NNG. */
    @Inject
    private INumeracionAsignadaNngDao numeracionAsignadaNngDao;

    /** DAO de EstadoSolicitud. */
    @Inject
    private IEstadoSolicitudDao estadoSolicitudDao;

    /** DAO de TipoAsignacion. */
    @Inject
    private ITipoAsignacionDao tipoAsignacionDao;

    /** DAO de Solicitudes de Liberación NNG. */
    @Inject
    private ISolicitudLiberacionNngDao solicitudLiberacionNngDao;

    /** DAO de Solicitudes de Redistribución NNG. */
    @Inject
    private ISolicitudRedistribucionNngDao solicitudRedistNngDao;

    /** DAO de Solicitud NNG. */
    @Inject
    private ISolicitudDao solicitudDao;

    /** DAO de Tipo Solicitud NNG. */
    @Inject
    private ITipoSolicitudDao tipoSolicitudDao;

    /********************************************************************************************/

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudCesionNng saveSolicitudCesion(SolicitudCesionNng pSolicitudCesion) {
        return solicitudCesionNngDao.saveOrUpdate(pSolicitudCesion);
    }

    @Override
    public SolicitudCesionNng getSolicitudCesionById(BigDecimal pConsecutivo) {
        return solicitudCesionNngDao.getSolicitudCesionById(pConsecutivo);
    }

    @Override
    public SolicitudCesionNng getSolicitudCesionEagerLoad(SolicitudCesionNng pSolicitud) {
        return solicitudCesionNngDao.getSolicitudCesionEagerLoad(pSolicitud);
    }

    @Override
    public List<SolicitudCesionNng> findAllSolicitudesCesion() {
        return solicitudCesionNngDao.findAllSolicitudesCesion();
    }

    @Override
    public List<SolicitudCesionNng> findAllSolicitudesCesion(FiltroBusquedaSolicitudes pFiltrosSolicitud) {
        return solicitudCesionNngDao.findAllSolicitudesCesion(pFiltrosSolicitud);
    }

    @Override
    public int findAllSolicitudesCesionCount(FiltroBusquedaSolicitudes pFiltrosSolicitud) {
        return solicitudCesionNngDao.findAllSolicitudesCesionCount(pFiltrosSolicitud);
    }

    @Override
    public List<EstadoSolicitud> findAllEstadosSolicitud() {
        return estadoSolicitudDao.findAllEstadosSolicitud();
    }

    @Override
    public TipoAsignacion getTipoAsignacionById(String cdg) {
        return tipoAsignacionDao.getTipoAsignacionById(cdg);
    }

    @Override
    public List<TipoAsignacion> findAllTipoAsignacion() {
        return tipoAsignacionDao.findAllTipoAsignacion();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<SolicitudAsignacionNng> findAllSolicitudesAsignacion(FiltroBusquedaSolicitudes filtros)
            throws Exception {
        return solicitudAsignacionNngDao.findAllSolicitudesAsignacion(filtros);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Integer findAllSolicitudesAsignacionCount(FiltroBusquedaSolicitudes filtros) throws Exception {
        return solicitudAsignacionNngDao.findAllSolicitudesAsignacionCount(filtros);

    }

    @Override
    public List<String> findAllClientesBySolicitud(Solicitud sol) {
        return numeracionSolicitadaNngDao.findAllClientesBySolicitud(sol);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudAsignacionNng saveSolicitudAsignacion(SolicitudAsignacionNng solicitud) {
        if (solicitud.getTipoSolicitud() == null) {

            solicitud.setTipoSolicitud(tipoSolicitudDao.getTipoSolicitudById(TipoSolicitud.ASIGNACION_NNG));
        }
        return solicitudAsignacionNngDao.saveOrUpdate(solicitud);
    }

    @Override
    public SolicitudAsignacionNng getSolicitudAsignacionEagerLoad(SolicitudAsignacionNng pSolicitud) {

        return solicitudAsignacionNngDao.getSolicitudAsignacionEagerLoad(pSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public PeticionCancelacion cancelSolicitudCesion(SolicitudCesionNng pSolicitud) throws Exception {

        // Cargamos las cesiones de la solicitud y comprobamos que se puedan cancelar.
        SolicitudCesionNng solicitud = this.getSolicitudCesionEagerLoad(pSolicitud);
        List<String> avisosCancelacion = new ArrayList<String>(solicitud.getCesionesSolicitadas().size());
        boolean isCancelacionPosible = true;
        for (CesionSolicitadaNng cesSol : solicitud.getCesionesSolicitadas()) {
            PeticionCancelacion checkCancelacionCes = this.checkCancelCesion(cesSol);
            if (!checkCancelacionCes.isCancelacionPosible()) {
                avisosCancelacion.add(checkCancelacionCes.getMensajeError());
            }
            isCancelacionPosible = isCancelacionPosible && checkCancelacionCes.isCancelacionPosible();
        }

        PeticionCancelacion checkCancelacion = new PeticionCancelacion();
        if (isCancelacionPosible) {
            for (CesionSolicitadaNng cesSol : solicitud.getCesionesSolicitadas()) {
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
            sbAvisos.append("No ha sido posible cancelar la solicitud:<br>");
            for (String aviso : avisosCancelacion) {
                sbAvisos.append(aviso).append("<br>");
            }
            checkCancelacion.setCancelacionPosible(false);
            checkCancelacion.setMensajeError(sbAvisos.toString());
        }

        return checkCancelacion;
    }

    @Override
    public PeticionCancelacion cancelCesion(CesionSolicitadaNng pCesSol, boolean pUseCheck) throws Exception {
        PeticionCancelacion checkCancelacion = null;
        if (pUseCheck) {
            checkCancelacion = this.checkCancelCesion(pCesSol);
        } else {
            checkCancelacion = new PeticionCancelacion();
            checkCancelacion.setCancelacionPosible(true);
        }

        if (checkCancelacion.isCancelacionPosible()) {
            RangoSerieNng rango = null;
            if (pCesSol.getFraccionamientoRango().equals("S")) {
                rango = seriesNngService.getRangoSerieByFraccion(
                        pCesSol.getIdClaveServicio(), pCesSol.getSna(),
                        pCesSol.getNumInicio(), pCesSol.getNumFinal(),
                        pCesSol.getProveedorCedente());
            } else {
                rango = seriesNngService.getRangoSerie(
                        pCesSol.getIdClaveServicio(), pCesSol.getSna(),
                        pCesSol.getNumInicio(), pCesSol.getProveedorCedente());
            }

            // Comprobamos que el rango siga existiendo para poder reestablecerlo. Si no
            // existe permitimos la cancelación igualmente para poder eliminar la cesión solicitada.
            if (rango != null) {
                if (!rango.getEstatus().getCodigo().equals(EstadoRango.ASIGNADO)) {
                    // Estado Asignado para Rangos
                    EstadoRango statusAsignado = new EstadoRango();
                    statusAsignado.setCodigo(EstadoRango.ASIGNADO);

                    Solicitud solicitudAnterior = new Solicitud();
                    solicitudAnterior.setId(pCesSol.getConsecutivoAsignacion());
                    rango.setSolicitud(solicitudAnterior);
                    rango.setEstatus(statusAsignado);
                    seriesNngService.saveRangoSerie(rango);
                }

                bitacoraService.add("Rango reestablecido: " + rango.toString());
            }

            // Estado Cancelada para la cesión. Se actualiza al guardar la solicitud.
            EstadoCesionSolicitada statusCancelada = new EstadoCesionSolicitada();
            statusCancelada.setCodigo(EstadoCesionSolicitada.CANCELADO);
            pCesSol.setEstatus(statusCancelada);
        }

        return checkCancelacion;
    }

    /**
     * Comprueba si es posible la cancelación de una cesión..
     * @param pCesSol Información de la cesión.
     * @return Objeto PeticionCancelacion con la información del proceso.
     * @throws Exception En caso de error.
     */
    private PeticionCancelacion checkCancelCesion(CesionSolicitadaNng pCesSol) throws Exception {
        PeticionCancelacion checkCancelacion = new PeticionCancelacion();
        checkCancelacion.setCancelacionPosible(true);

        if (pCesSol.getEstatus().getCodigo().equals(EstadoCesionSolicitada.PENDIENTE)) {
            // Comprobamos que exista el Consecutivo de la Solicitud que creó el rango para poder reasignárselo.
            // Es necesario comprobarlo ya que puede haber rangos mal migrados que no dispongan de ese dato y
            // fallaría la cancelación
            if (pCesSol.getConsecutivoAsignacion() == null) {
                StringBuffer sbError = new StringBuffer();
                sbError.append("La siguiente numeración pendiente de cesión no dispone ");
                sbError.append("de información del trámite de asignación que la generó: ").append(pCesSol);
                checkCancelacion.setMensajeError(sbError.toString());
                checkCancelacion.setCancelacionPosible(false);
            }
        } else {
            StringBuffer sbAviso = new StringBuffer();
            sbAviso.append("No es posible cancelar la cesión del rango con ");
            sbAviso.append("Serie: ").append(pCesSol.getSna()).append(" y ");
            sbAviso.append("N.Inicio: ").append(pCesSol.getNumInicio()).append(". ");
            sbAviso.append("La fecha de implementación ya se ha cumplido: ");
            sbAviso.append(FechasUtils.fechaToString(pCesSol.getFechaCesion()));
            checkCancelacion.setCancelacionPosible(false);
            checkCancelacion.setMensajeError(sbAviso.toString());
        }
        return checkCancelacion;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public SolicitudCesionNng applyCesionesSolicitadas(SolicitudCesionNng pSolicitud) throws Exception {

        // Parseamos las fechas para eliminar las horas y minutos
        Date fHoy = FechasUtils.getFechaHoy();

        // Estado para las cesiones que se ejecuten.
        EstadoCesionSolicitada ecs = new EstadoCesionSolicitada();
        ecs.setCodigo(EstadoCesionSolicitada.CEDIDO);

        boolean todoCedido = true;

        // Agrupamos las cesiones por rango
        HashMap<String, List<CesionSolicitadaNng>> rangosByCesion = new HashMap<String, List<CesionSolicitadaNng>>(1);

        for (CesionSolicitadaNng cesSol : pSolicitud.getCesionesSolicitadas()) {
            // Ignoramos las cesiones que ya se hayan ejecutado.
            if (cesSol.getEstatus().getCodigo().equals(EstadoCesionSolicitada.PENDIENTE)) {
                // Recuperamos la Información del Rango al que se ha solicitado ceder
                RangoSerieNng rangoOriginal = seriesNngService.getRangoSerieByFraccion(
                        cesSol.getIdClaveServicio(),
                        cesSol.getSna(),
                        cesSol.getNumInicio(),
                        cesSol.getNumFinal(),
                        cesSol.getProveedorCedente());

                String key = rangoOriginal.getIdentificadorRango();
                if (!rangosByCesion.containsKey(key)) {
                    rangosByCesion.put(key, new ArrayList<CesionSolicitadaNng>());
                }
                rangosByCesion.get(key).add(cesSol);
            }
        }

        // Si solo existe una cesión sobre un rango y los límites del rango coinciden, es que se
        // quiere ceder el rango completo. Si no, se quieren realizar fracciones sobre rangos.

        for (String rangoKey : rangosByCesion.keySet()) {
            if (rangosByCesion.get(rangoKey).size() == 1) {
                CesionSolicitadaNng cesSol = rangosByCesion.get(rangoKey).get(0);

                // Eliminamos minutos y segudos de la fecha de cesión.
                Date fImplementacion = FechasUtils.parseFecha(cesSol.getFechaCesion());

                // Cesión del Rango Completo
                if (cesSol.getFraccionamientoRango().equals("N")) {
                    if (seriesNngService.cederRangoCompleto(cesSol, fHoy.equals(fImplementacion))) {
                        cesSol.setEstatus(ecs);
                    } else {
                        todoCedido = false;
                    }
                } else {
                    // Cesión de las fracciones en estado 'Afectado' del rango.
                    if (seriesNngService.cederFraccionesRango(
                            rangosByCesion.get(rangoKey), fHoy.equals(fImplementacion))) {
                        cesSol.setEstatus(ecs);
                    } else {
                        todoCedido = false;
                    }
                }
            } else {
                // Si existen varias cesiones para un mismo rango significa que queremos fraccionar el rango

                // Las cesiones vienen agrupadas por el rango, por lo que todas tienen los
                // mismos valores a excepción del número inicial y número final.
                // Para obtener fecha de implementación nos vale cualquiera de la lista.
                CesionSolicitadaNng infoCesSol = rangosByCesion.get(rangoKey).get(0);

                // Eliminamos minutos y segudos de la fecha de cesión.
                Date fImplementacion = FechasUtils.parseFecha(infoCesSol.getFechaCesion());

                // Cesión de las fracciones en estado 'Afectado' del rango.
                if (seriesNngService.cederFraccionesRango(
                        rangosByCesion.get(rangoKey), fHoy.equals(fImplementacion))) {
                    for (CesionSolicitadaNng cesSol : rangosByCesion.get(rangoKey)) {
                        cesSol.setEstatus(ecs);
                    }
                } else {
                    todoCedido = false;
                }
            }
        }

        // Comprobamos si se han efectuado todas las cesiones
        if (!rangosByCesion.isEmpty()) {
            EstadoSolicitud es = new EstadoSolicitud();
            if (todoCedido) {
                // Marcamos la solcitud como Terminada.
                es.setCodigo(EstadoSolicitud.SOLICITUD_TERMINADA);
                pSolicitud.setFechaAsignacion(new Date());
            } else {
                // Marcamos la solcitud como En Trámite (por si antes estaba terminada)
                es.setCodigo(EstadoSolicitud.SOLICITUD_EN_TRAMITE);
                pSolicitud.setFechaAsignacion(null);
            }
            pSolicitud.setEstadoSolicitud(es);

            // Guardamos los cambios. Los objetos CesionSolicitadaNg y Cesion se guardan en Cascada.
            return this.saveSolicitudCesion(pSolicitud);

        } else {
            if (pSolicitud.getEstadoSolicitud().getCodigo().equals(EstadoSolicitud.SOLICITUD_EN_TRAMITE)) {

                // Para solicitudes con todas las cesiones realizadas que estén en Trámite se ha de
                // actualizar el estado. Se da éste caso cuando se eliminan todas las cesiones pendientes
                // y solo quedan las cesiones cedidas o canceladas..

                EstadoSolicitud es = new EstadoSolicitud();
                es.setCodigo(EstadoSolicitud.SOLICITUD_TERMINADA);
                pSolicitud.setEstadoSolicitud(es);
                pSolicitud.setFechaAsignacion(new Date());
                return this.saveSolicitudCesion(pSolicitud);
            } else {
                // Si no han habido cambios no hace falta actualizar la solicitud.
                return pSolicitud;
            }
        }
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
        FiltroBusquedaSolicitudes filtroSolicitudes = new FiltroBusquedaSolicitudes();
        filtroSolicitudes.setEstado(estatusSol);
        filtroSolicitudes.setEstatusCesSol(statusCesSol);

        List<SolicitudCesionNng> solicitudes = this.findAllSolicitudesCesion(filtroSolicitudes);
        for (SolicitudCesionNng solicitud : solicitudes) {
            if (solicitud.getCesionesSolicitadas() != null && !solicitud.getCesionesSolicitadas().isEmpty()) {
                try {
                    bitacoraService.add(solicitud,
                            "Comprobando Cesiones NNG pendientes de Solicitud " + solicitud.getId());
                    this.applyCesionesSolicitadas(solicitud);
                } catch (Exception e) {
                    LOGGER.error("Error ejecutando Cesiones NNG pendientes.", e);
                }
            }
        }
    }

    @Override
    public List<String> getNotificacionesCesionesPendientes() {

        // Solicitudes en Trámite
        EstadoSolicitud estatusSol = new EstadoSolicitud();
        estatusSol.setCodigo(EstadoSolicitud.SOLICITUD_EN_TRAMITE);

        // Cesiones Solicitadas Pendientes
        EstadoCesionSolicitada statusCesSol = new EstadoCesionSolicitada();
        statusCesSol.setCodigo(EstadoCesionSolicitada.PENDIENTE);

        // Filtros de búsqueda
        FiltroBusquedaSolicitudes filtroSolicitudes = new FiltroBusquedaSolicitudes();
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

            List<SolicitudCesionNng> solicitudes = this.findAllSolicitudesCesion(filtroSolicitudes);
            for (SolicitudCesionNng solicitud : solicitudes) {
                for (CesionSolicitadaNng cesSol : solicitud.getCesionesSolicitadas()) {
                    if (!cesSol.getEstatus().getCodigo().equals(EstadoCesionSolicitada.CEDIDO)) {
                        // Si se ha sobrepasado la fecha de cesión es que hay algún problema para en la liberación.
                        if (fHoy.after(cesSol.getFechaCesion())) {
                            if (!tramitesFallidos.contains(cesSol.getSolicitudCesion().getId().toString())) {
                                tramitesFallidos.add(cesSol.getSolicitudCesion().getId().toString());
                            }
                        } else {
                            // Las notificaciones se generan a partir de 2 días antes de la fecha de implementación
                            if (fHoy.compareTo(FechasUtils.getFechaNotificacion(cesSol.getFechaCesion())) >= 0) {
                                key = new StringBuffer();
                                key.append(FechasUtils.fechaToString(cesSol.getFechaCesion()));
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
    public List<String> getNotificacionesLiberacionesPendientes() {

        // Solicitudes en Trámite
        EstadoSolicitud estatusSol = new EstadoSolicitud();
        estatusSol.setCodigo(EstadoSolicitud.SOLICITUD_EN_TRAMITE);

        // Liberaciones Solicitadas Reservadas (Pendientes)
        EstadoLiberacionSolicitada statusPendiente = new EstadoLiberacionSolicitada();
        statusPendiente.setCodigo(EstadoLiberacionSolicitada.PENDIENTE);

        // Liberaciones Solicitadas Reservadas (En Periodo de Reserva)
        EstadoLiberacionSolicitada statusReserva = new EstadoLiberacionSolicitada();
        statusReserva.setCodigo(EstadoLiberacionSolicitada.PERIODO_RESERVA);

        // Filtros de búsqueda
        FiltroBusquedaSolicitudes filtroSolicitudes = new FiltroBusquedaSolicitudes();
        filtroSolicitudes.setEstado(estatusSol);
        filtroSolicitudes.setFechaLiberacionHasta(FechasUtils.calculaFecha(FechasUtils.getFechaHoy(), 2, 0, 0));

        try {
            // Parseamos las fechas para eliminar las horas y minutos
            Date fHoy = FechasUtils.getFechaHoy();

            // Agrupamos las Solicitudes por fecha
            HashMap<String, Integer> tramitesByFecha = new HashMap<String, Integer>(1);
            ArrayList<String> tramitesFallidos = new ArrayList<String>();
            StringBuffer key;

            // Buscamos Solicitudes con LiberacionesSolicitadas en estado Pendiente y en Periodo de Reserva
            List<SolicitudLiberacionNng> solicitudes = new ArrayList<SolicitudLiberacionNng>();
            filtroSolicitudes.setEstatusLibSol(statusReserva);
            solicitudes.addAll(this.findAllSolicitudesLiberacion(filtroSolicitudes));
            filtroSolicitudes.setEstatusLibSol(statusPendiente);
            solicitudes.addAll(this.findAllSolicitudesLiberacion(filtroSolicitudes));

            for (SolicitudLiberacionNng solicitud : solicitudes) {
                for (LiberacionSolicitadaNng libSol : solicitud.getLiberacionesSolicitadas()) {

                    // Liberaciones pendientes de implementar.
                    if (libSol.getEstatus().getCodigo().equals(EstadoLiberacionSolicitada.PENDIENTE)) {
                        // Si se ha sobrepasado la fecha de implementación es que ha habido algún problema.
                        if (fHoy.after(libSol.getFechaLiberacion())) {
                            if (!tramitesFallidos.contains(libSol.getSolicitudLiberacion().getId().toString())) {
                                tramitesFallidos.add(libSol.getSolicitudLiberacion().getId().toString());
                            }
                        } else {
                            // Las notificaciones se generan a partir de 2 días antes de la fecha de implementación
                            if (fHoy.compareTo(FechasUtils.getFechaNotificacion(libSol.getFechaLiberacion())) >= 0) {
                                key = new StringBuffer();
                                key.append(FechasUtils.fechaToString(libSol.getFechaLiberacion()));
                                key.append("-").append(solicitud.getId());
                                if (!tramitesByFecha.containsKey(key.toString())) {
                                    tramitesByFecha.put(key.toString(), new Integer(0));
                                }
                                int oneMore = (tramitesByFecha.get(key.toString())).intValue() + 1;
                                tramitesByFecha.put(key.toString(), new Integer(oneMore));
                            }
                        }
                    }

                    // Liberaciones en periodo de reserva.
                    if (libSol.getEstatus().getCodigo().equals(EstadoLiberacionSolicitada.PERIODO_RESERVA)) {
                        // Si se ha sobrepasado la fecha del periodo de fin de reserva es que hay algún problema
                        // para liberar
                        if (fHoy.after(libSol.getFechaFinReserva())) {
                            if (!tramitesFallidos.contains(libSol.getSolicitudLiberacion().getId().toString())) {
                                tramitesFallidos.add(libSol.getSolicitudLiberacion().getId().toString());
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
    public List<Solicitud> findAllSolicitudes(FiltroBusquedaSolicitudes pFiltrosSolicitud)
            throws Exception {
        return solicitudDao.findAllSolicitudesNng(pFiltrosSolicitud);
    }

    @Override
    public int findAllSolicitudesCount(FiltroBusquedaSolicitudes pFiltrosSolicitud) throws Exception {
        return solicitudDao.findAllSolicitudesNngCount(pFiltrosSolicitud);
    }

    @Override
    public List<TipoSolicitud> findAllTiposSolicitud() {
        return tipoSolicitudDao.findAllTiposSolicitudNNG();
    }

    @Override
    public List<SolicitudLiberacionNng> findAllSolicitudesLiberacion(FiltroBusquedaSolicitudes pFiltrosSolicitud) {
        return solicitudLiberacionNngDao.findAllSolicitudesLiberacion(pFiltrosSolicitud);
    }

    @Override
    public int findAllSolicitudesLiberacionCount(FiltroBusquedaSolicitudes pFiltrosSolicitud) {
        return solicitudLiberacionNngDao.findAllSolicitudesLiberacionCount(pFiltrosSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudLiberacionNng saveSolicitudLiberacion(SolicitudLiberacionNng pSolicitud) {
        return solicitudLiberacionNngDao.saveOrUpdate(pSolicitud);
    }

    @Override
    public SolicitudLiberacionNng getSolicitudLiberacionById(BigDecimal pConsecutivo) {
        return solicitudLiberacionNngDao.getSolicitudLiberacionById(pConsecutivo);
    }

    @Override
    public SolicitudLiberacionNng getSolicitudLiberacionEagerLoad(SolicitudLiberacionNng pSolicitud) {
        return solicitudLiberacionNngDao.getSolicitudLiberacionEagerLoad(pSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public PeticionCancelacion cancelSolicitudLiberacion(SolicitudLiberacionNng pSolicitud) throws Exception {

        // Cargamos las liberaciones de la solicitud y comprobamos que se puedan cancelar.
        SolicitudLiberacionNng solicitud = this.getSolicitudLiberacionEagerLoad(pSolicitud);
        List<String> avisosCancelacion = new ArrayList<String>(solicitud.getLiberacionesSolicitadas().size());
        boolean isCancelacionPosible = true;
        for (LiberacionSolicitadaNng libSol : solicitud.getLiberacionesSolicitadas()) {
            PeticionCancelacion checkCancelacionLib = this.checkCancelLiberacion(libSol);
            if (!checkCancelacionLib.isCancelacionPosible()) {
                avisosCancelacion.add(checkCancelacionLib.getMensajeError());
            }
            isCancelacionPosible = isCancelacionPosible && checkCancelacionLib.isCancelacionPosible();
        }

        PeticionCancelacion checkCancelacion = new PeticionCancelacion();
        if (isCancelacionPosible) {
            for (LiberacionSolicitadaNng libSol : solicitud.getLiberacionesSolicitadas()) {
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
            sbAvisos.append("No ha sido posible cancelar la solicitud:<br>");
            for (String aviso : avisosCancelacion) {
                sbAvisos.append(aviso).append("<br>");
            }
            checkCancelacion.setCancelacionPosible(false);
            checkCancelacion.setMensajeError(sbAvisos.toString());
        }

        return checkCancelacion;
    }

    @Override
    public PeticionCancelacion cancelLiberacion(LiberacionSolicitadaNng pLibSol, boolean pUseCheck) throws Exception {
        PeticionCancelacion checkCancelacion = null;
        if (pUseCheck) {
            checkCancelacion = this.checkCancelLiberacion(pLibSol);
        } else {
            checkCancelacion = new PeticionCancelacion();
            checkCancelacion.setCancelacionPosible(true);
        }

        if (checkCancelacion.isCancelacionPosible()) {
            RangoSerieNng rango = null;
            if (pLibSol.getFraccionamientoRango().equals("S")) {
                rango = seriesNngService.getRangoSerieByFraccion(
                        pLibSol.getIdClaveServicio(), pLibSol.getSna(),
                        pLibSol.getNumInicio(), pLibSol.getNumFinal(),
                        pLibSol.getProveedorCesionario());
            } else {
                rango = seriesNngService.getRangoSerie(
                        pLibSol.getIdClaveServicio(), pLibSol.getSna(),
                        pLibSol.getNumInicio(), pLibSol.getProveedorCesionario());
            }

            // Comprobamos que el rango siga existiendo para poder reestablecerlo. Si no
            // existe permitimos la cancelación igualmente para poder eliminar la liberación solicitada.
            if (rango != null) {
                if (!rango.getEstatus().getCodigo().equals(EstadoRango.ASIGNADO)) {
                    // Estado Asignado para Rangos
                    EstadoRango statusAsignado = new EstadoRango();
                    statusAsignado.setCodigo(EstadoRango.ASIGNADO);

                    Solicitud solicitudAnterior = new Solicitud();
                    solicitudAnterior.setId(pLibSol.getConsecutivoAsignacion());
                    rango.setSolicitud(solicitudAnterior);
                    rango.setEstatus(statusAsignado);
                    rango.setFechaFinReserva(null);
                    seriesNngService.saveRangoSerie(rango);
                }

                bitacoraService.add("Rango reestablecido: " + rango.toString());
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
    private PeticionCancelacion checkCancelLiberacion(LiberacionSolicitadaNng pLibSol) throws Exception {
        PeticionCancelacion checkCancelacion = new PeticionCancelacion();
        checkCancelacion.setCancelacionPosible(true);

        if (pLibSol.getEstatus().getCodigo().equals(EstadoLiberacionSolicitada.PENDIENTE)
                || pLibSol.getEstatus().getCodigo().equals(EstadoLiberacionSolicitada.PERIODO_RESERVA)) {
            // Comprobamos que exista el Consecutivo de la Solicitud que creó el rango para poder reasignárselo.
            // Es necesario comprobarlo ya que puede haber rangos mal migrados que no dispongan de ese dato y
            // fallaría la cancelación
            if (pLibSol.getConsecutivoAsignacion() == null) {
                StringBuffer sbError = new StringBuffer();
                sbError.append("La siguiente numeración pendiente de liberación no dispone ");
                sbError.append("de información del trámite de asignación que la generó: ").append(pLibSol);
                checkCancelacion.setMensajeError(sbError.toString());
                checkCancelacion.setCancelacionPosible(false);
            }
        } else {
            StringBuffer sbAviso = new StringBuffer();
            sbAviso.append("No es posible cancelar la liberación del rango con ");
            sbAviso.append("Clave Serv.: ").append(pLibSol.getIdClaveServicio()).append(", ");
            sbAviso.append("Serie: ").append(pLibSol.getSna()).append(", ");
            sbAviso.append("N.Inicio: ").append(pLibSol.getNumInicio()).append(". ");
            sbAviso.append("La fecha de implementación (");
            sbAviso.append(FechasUtils.fechaToString(pLibSol.getFechaLiberacion()));
            sbAviso.append(") y periodo de reserva (");
            sbAviso.append(FechasUtils.fechaToString(pLibSol.getFechaFinReserva()));
            sbAviso.append(") ya se han cumplido.");
            checkCancelacion.setCancelacionPosible(false);
            checkCancelacion.setMensajeError(sbAviso.toString());
        }
        return checkCancelacion;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public SolicitudLiberacionNng applyLiberacionesSolicitadas(SolicitudLiberacionNng pSolicitud) throws Exception {

        // Parseamos las fechas para eliminar las horas y minutos
        Date fHoy = FechasUtils.getFechaHoy();

        EstadoLiberacionSolicitada estatusLiberado = new EstadoLiberacionSolicitada();
        estatusLiberado.setCodigo(EstadoLiberacionSolicitada.LIBERADO);

        EstadoLiberacionSolicitada estatusReserva = new EstadoLiberacionSolicitada();
        estatusReserva.setCodigo(EstadoLiberacionSolicitada.PERIODO_RESERVA);

        // Agrupamos las liberaciones por rango
        HashMap<String, List<LiberacionSolicitadaNng>> rangosByLiberacion =
                new HashMap<String, List<LiberacionSolicitadaNng>>(1);

        boolean todoLiberado = true;

        for (LiberacionSolicitadaNng libSol : pSolicitud.getLiberacionesSolicitadas()) {
            // Ignoramos las liberaciones que ya se hayan ejecutado.
            if (libSol.getEstatus().getCodigo().equals(EstadoLiberacionSolicitada.PENDIENTE)
                    || libSol.getEstatus().getCodigo().equals(EstadoLiberacionSolicitada.PERIODO_RESERVA)) {
                // Recuperamos la Información del Rango al que se ha solicitado Liberar
                RangoSerieNng rangoOriginal = seriesNngService.getRangoSerieByFraccion(
                        libSol.getIdClaveServicio(),
                        libSol.getSna(),
                        libSol.getNumInicio(),
                        libSol.getNumFinal(),
                        libSol.getProveedorCesionario());

                String key = rangoOriginal.getIdentificadorRango();
                if (!rangosByLiberacion.containsKey(key)) {
                    rangosByLiberacion.put(key, new ArrayList<LiberacionSolicitadaNng>());
                }
                rangosByLiberacion.get(key).add(libSol);
            }
        }

        // Si solo existe una liberación sobre una rango y los límites del rango coinciden, es que se
        // quiere ceder el rango completo. Si no, se quieren realizar fracciones sobre rangos.
        for (String rangoKey : rangosByLiberacion.keySet()) {
            if (rangosByLiberacion.get(rangoKey).size() == 1) {
                LiberacionSolicitadaNng libSol = rangosByLiberacion.get(rangoKey).get(0);
                Date fImplementacion = FechasUtils.parseFecha(libSol.getFechaLiberacion());

                String resultado;
                if (libSol.getFraccionamientoRango().equals("N")) {
                    // Liberación de rango completo
                    resultado = seriesNngService.liberarRangoCompleto(libSol, fHoy.equals(fImplementacion));
                } else {
                    // Liberación de las fracciones en estado 'Afectado' del rango.
                    resultado = seriesNngService.liberarFraccionesRango(
                            rangosByLiberacion.get(rangoKey), fHoy.equals(fImplementacion));
                }

                todoLiberado = todoLiberado && resultado.equals(EstadoLiberacionSolicitada.LIBERADO);

                // Marcamos el nuevo estado de la liberación
                if (resultado.equals(EstadoLiberacionSolicitada.LIBERADO)) {
                    libSol.setEstatus(estatusLiberado);
                } else if (resultado.equals(EstadoLiberacionSolicitada.PERIODO_RESERVA)) {
                    libSol.setEstatus(estatusReserva);
                }

            } else {
                // Si existen varias liberaciones significa que queremos fraccionar el rango

                // Las liberaciones vienen agrupadas por el rango, por lo que todas tienen los
                // mismos valores a excepción del número inicial y número final.
                // Para obtener fecha de implementación nos vale cualquiera de la lista.
                LiberacionSolicitadaNng infoLibSol = rangosByLiberacion.get(rangoKey).get(0);
                Date fImplementacion = FechasUtils.parseFecha(infoLibSol.getFechaLiberacion());

                // Liberación de las fracciones en estado 'Afectado' del rango.
                String resultado = seriesNngService.liberarFraccionesRango(
                        rangosByLiberacion.get(rangoKey), fHoy.equals(fImplementacion));

                todoLiberado = todoLiberado && resultado.equals(EstadoLiberacionSolicitada.LIBERADO);

                // Marcamos el nuevo estado de las liberacies
                if (resultado.equals(EstadoLiberacionSolicitada.LIBERADO)) {
                    for (LiberacionSolicitadaNng liberacion : rangosByLiberacion.get(rangoKey)) {
                        liberacion.setEstatus(estatusLiberado);
                    }
                } else if (resultado.equals(EstadoLiberacionSolicitada.PERIODO_RESERVA)) {
                    for (LiberacionSolicitadaNng liberacion : rangosByLiberacion.get(rangoKey)) {
                        liberacion.setEstatus(estatusReserva);
                    }
                }
            }
        }

        // Comprobamos si se han efectuado todas las liberaciones
        if (!rangosByLiberacion.isEmpty()) {

            EstadoSolicitud es = new EstadoSolicitud();
            if (todoLiberado) {
                // Marcamos la solcitud como Terminada.
                es.setCodigo(EstadoSolicitud.SOLICITUD_TERMINADA);
                pSolicitud.setFechaAsignacion(new Date());
            } else {
                // Marcamos la solcitud como En Trámite (por si antes estaba terminada)
                es.setCodigo(EstadoSolicitud.SOLICITUD_EN_TRAMITE);
                pSolicitud.setFechaAsignacion(null);
            }
            pSolicitud.setEstadoSolicitud(es);

            // Guardamos los cambios
            return this.saveSolicitudLiberacion(pSolicitud);

        } else {
            if (pSolicitud.getEstadoSolicitud().getCodigo().equals(EstadoSolicitud.SOLICITUD_EN_TRAMITE)) {

                // Para solicitudes con todas las liberaciones realizadas que estén en Trámite se ha de
                // actualizar el estado. Se da éste caso cuando se eliminan todas las liberaciones pendientes
                // y solo quedan las liberaciones liberadas o canceladas

                EstadoSolicitud es = new EstadoSolicitud();
                es.setCodigo(EstadoSolicitud.SOLICITUD_TERMINADA);
                pSolicitud.setEstadoSolicitud(es);
                pSolicitud.setFechaAsignacion(new Date());
                return this.saveSolicitudLiberacion(pSolicitud);
            } else {
                // Si no han habido cambios no hace falta actualizar la solicitud.
                return pSolicitud;
            }
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void applyLiberacionesPendientes() throws Exception {

        // Solicitudes en Trámite
        EstadoSolicitud estatusSol = new EstadoSolicitud();
        estatusSol.setCodigo(EstadoSolicitud.SOLICITUD_EN_TRAMITE);

        // Liberaciones Solicitadas Reservadas (Pendientes)
        EstadoLiberacionSolicitada statusPendiente = new EstadoLiberacionSolicitada();
        statusPendiente.setCodigo(EstadoLiberacionSolicitada.PENDIENTE);

        // Liberaciones Solicitadas Reservadas (En Periodo de Reserva)
        EstadoLiberacionSolicitada statusReserva = new EstadoLiberacionSolicitada();
        statusReserva.setCodigo(EstadoLiberacionSolicitada.PERIODO_RESERVA);

        // Filtros de búsqueda
        FiltroBusquedaSolicitudes filtroSolicitudes = new FiltroBusquedaSolicitudes();
        filtroSolicitudes.setEstado(estatusSol);

        // Buscamos Solicitudes con LiberacionesSolicitadas en estado Pendiente y en Periodo de Reserva
        List<SolicitudLiberacionNng> solicitudes = new ArrayList<SolicitudLiberacionNng>();
        filtroSolicitudes.setEstatusLibSol(statusReserva);
        solicitudes.addAll(this.findAllSolicitudesLiberacion(filtroSolicitudes));
        filtroSolicitudes.setEstatusLibSol(statusPendiente);
        solicitudes.addAll(this.findAllSolicitudesLiberacion(filtroSolicitudes));

        for (SolicitudLiberacionNng solicitud : solicitudes) {
            if (!solicitud.getLiberacionesSolicitadas().isEmpty()) {
                try {
                    bitacoraService.add(solicitud,
                            "Comprobando Liberaciones NNG pendientes de Solicitud " + solicitud.getId());
                    this.applyLiberacionesSolicitadas(solicitud);
                } catch (Exception e) {
                    LOGGER.error("Error ejecutando Liberaciones NNG pendientes.", e);
                }
            }
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<SolicitudRedistribucionNng> findAllSolicitudesRedistribucion(
            FiltroBusquedaSolicitudes pFiltrosSolicitud) {
        return solicitudRedistNngDao.findAllSolicitudesRedistribucion(pFiltrosSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public int findAllSolicitudesRedistribucionCount(FiltroBusquedaSolicitudes pFiltrosSolicitud) {
        return solicitudRedistNngDao.findAllSolicitudesRedistribucionCount(pFiltrosSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudRedistribucionNng saveSolicitudRedistribucion(SolicitudRedistribucionNng pSolicitud) {
        return solicitudRedistNngDao.saveOrUpdate(pSolicitud);
    }

    @Override
    public SolicitudRedistribucionNng getSolicitudRedistribucionById(BigDecimal pConsecutivo) {
        return solicitudRedistNngDao.getSolicitudRedistribucionById(pConsecutivo);
    }

    @Override
    public SolicitudRedistribucionNng getSolicitudRedistribucionEagerLoad(SolicitudRedistribucionNng pSolicitud) {
        return solicitudRedistNngDao.getSolicitudRedistribucionEagerLoad(pSolicitud);
    }

    @Override
    public boolean isNumeracionSolicitadaWithRangos(NumeracionSolicitadaNng numeracionSolicitada) {

        return numeracionSolicitadaNngDao.isNumeracionSolicitadaWithRangos(numeracionSolicitada);

    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudRedistribucionNng applyRedistribucionesSolicitadas(SolicitudRedistribucionNng pSolicitud)
            throws Exception {

        // Estado Redistribuido
        EstadoRedistribucionSolicitada ers = new EstadoRedistribucionSolicitada();
        ers.setCodigo(EstadoRedistribucionSolicitada.REDISTRIBUIDO);

        // Agrupamos las redistribuciones de fracciones por rango
        HashMap<String, List<RedistribucionSolicitadaNng>> fraccionesByRedist =
                new HashMap<String, List<RedistribucionSolicitadaNng>>(1);

        // Diferenciamos entre fracciones y rangos completos
        for (RedistribucionSolicitadaNng redSol : pSolicitud.getRedistribucionesSolicitadas()) {
            if (redSol.getEstatus().getCodigo().equals(EstadoRedistribucionSolicitada.PENDIENTE)) {
                if (redSol.getFraccionamientoRango().equals("S")) {
                    // Si se trata de una fracción primero hay que crearla. Para ello agrupamos todas las fracciones por
                    // rango primero.
                    RangoSerieNng rangoOriginal = seriesNngService.getRangoSerieByFraccion(
                            redSol.getIdClaveServicio(),
                            redSol.getSna(),
                            redSol.getNumInicio(),
                            redSol.getNumFinal(),
                            redSol.getProveedorSolicitante());

                    String key = rangoOriginal.getIdentificadorRango();
                    if (!fraccionesByRedist.containsKey(key)) {
                        fraccionesByRedist.put(key, new ArrayList<RedistribucionSolicitadaNng>());
                    }
                    fraccionesByRedist.get(key).add(redSol);
                } else {
                    // Si es un rango completo basta con actualizar los campos. Se comprueba que el rango
                    // exista y tenga el estatus correcto antes de pasar a redistribuir.
                    RangoSerieNng rangoCompleto = seriesNngService.getRangoSerie(
                            redSol.getIdClaveServicio(),
                            redSol.getSna(),
                            redSol.getNumInicio(),
                            redSol.getProveedorSolicitante());

                    // Redistribuimos el Rango
                    seriesNngService.redistribuirRango(rangoCompleto, redSol);
                }
            }
        }

        // Redistribuciones de Fracciones
        if (!fraccionesByRedist.isEmpty()) {
            for (List<RedistribucionSolicitadaNng> listaFracciones : fraccionesByRedist.values()) {
                seriesNngService.redistribuirFraccionesRango(listaFracciones);
            }
        }

        // Si todo ha ido bien actualizamos los estados de las Redistribuciones Solicitadas
        boolean nuevasRedist = false;
        for (RedistribucionSolicitadaNng redSol : pSolicitud.getRedistribucionesSolicitadas()) {
            if (redSol.getEstatus().getCodigo().equals(EstadoRedistribucionSolicitada.PENDIENTE)) {
                redSol.setEstatus(ers);
                nuevasRedist = true;
            }
        }

        // Es posible que se haya cancelado la solicitud directamente cancelando sus redistribuciones individuales.
        boolean solicitudCancelada = pSolicitud.getEstadoSolicitud().getCodigo()
                .equals(EstadoSolicitud.SOLICITUD_CANCELADA);

        if (solicitudCancelada && (!nuevasRedist)) {
            return pSolicitud;
        } else {
            EstadoSolicitud es = new EstadoSolicitud();
            es.setCodigo(EstadoSolicitud.SOLICITUD_TERMINADA);
            pSolicitud.setEstadoSolicitud(es);

            // Guardamos los cambios
            pSolicitud.setFechaAsignacion(new Date());
            return this.saveSolicitudRedistribucion(pSolicitud);
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public PeticionCancelacion cancelRedistribucion(RedistribucionSolicitadaNng pRedSol, boolean pUseCheck)
            throws Exception {

        List<RangoSerieNng> rangosEliminar = new ArrayList<RangoSerieNng>();

        // Para cancelaciones de solicitudes completas el check se hace en el método cancelSolicitudRedistribucion.
        // En el caso de querer cancelar solamente una RedistribucionSolicitadaNng y no el trámite entonces sí es
        // necesario validar.
        PeticionCancelacion checkCancelacion = null;
        if (pUseCheck) {
            // Comprobamos si la cancelación es posible
            checkCancelacion = this.checkCancelRedistribucion(pRedSol);
        } else {
            checkCancelacion = new PeticionCancelacion();
            checkCancelacion.setCancelacionPosible(true);
        }

        if (checkCancelacion.isCancelacionPosible()) {
            // El rango redistribuido existe o no se hubieran pasado las validaciones
            RangoSerieNng rangoRedist = seriesNngService.getRangoSerie(
                    pRedSol.getIdClaveServicio(),
                    pRedSol.getSna(),
                    pRedSol.getNumInicio(),
                    pRedSol.getProveedorSolicitante());

            // Recomponemos el fraccionamiento del rango si existía.
            boolean rangoFraccionado = pRedSol.getFraccionamientoRango().equals("S");

            if (rangoFraccionado) {
                // Recomponemos el fraccionamiento. En el método cancelSolicitudRedistribucion() se comprueba
                // que existan los rangos del fraccionamiento.
                RangoSerieNng rangoOriginal = seriesNngService.getRangoSerieOriginalByFraccion(
                        rangoRedist, pRedSol.getSolicitudRedistribucion().getId(),
                        pRedSol.getProveedorSolicitante());

                // Eliminamos el rango anterior al ser parte de la fracción
                if (rangoRedist.getNumInicioAsInt() != rangoOriginal.getNumInicioAsInt()) {
                    RangoSerieNng rangoAnterior = seriesNngService.getRangoSerie(
                            rangoOriginal.getClaveServicio().getCodigo(),
                            rangoOriginal.getSerie().getId().getSna(),
                            rangoOriginal.getNumInicio(), rangoOriginal.getAsignatario());

                    rangosEliminar.add(rangoAnterior);
                }

                // Eliminamos el rango posterior al ser parte de la fracción
                if (rangoRedist.getNumFinalAsInt() != rangoOriginal.getNumFinalAsInt()) {
                    // Inicio del Rango Posterior
                    String numInicialRPost = StringUtils.leftPad(
                            String.valueOf((rangoRedist.getNumFinalAsInt() + 1)), 4, '0');

                    RangoSerieNng rangoPosterior = seriesNngService.getRangoSerie(
                            rangoOriginal.getClaveServicio().getCodigo(),
                            rangoOriginal.getSerie().getId().getSna(),
                            numInicialRPost, rangoOriginal.getAsignatario());

                    rangosEliminar.add(rangoPosterior);
                }

                // Solicitud Anterior
                Solicitud solicitudAnterior = new Solicitud();
                solicitudAnterior.setId(pRedSol.getConsecutivoAsignacion());
                rangoOriginal.setSolicitud(solicitudAnterior);

                seriesNngService.saveRangoSerie(rangoOriginal);
                bitacoraService.add("Rango reestablecido: " + rangoOriginal.toString());

                // Eliminamos el rango redistribuido
                rangosEliminar.add(rangoRedist);

            } else {
                // El rango se redistribuyó sin fraccionamiento. Reestablecemos la información
                // original del rango. En el método cancelSolicitudRedistribucion() se comprueba
                // que el rango existe.

                // Filtros para recuperar el estado anterior del rango. Buscamos el movimiento anterior
                // al creado por la redistribución.
                FiltroBusquedaHistoricoRangos filtros = new FiltroBusquedaHistoricoRangos();
                filtros.setUsarPaginacion(true);
                filtros.setNumeroPagina(0);
                filtros.setResultadosPagina(5);
                filtros.setIdClaveServicio(pRedSol.getIdClaveServicio());
                filtros.setSna(pRedSol.getSna());
                filtros.setNumInicio(pRedSol.getNumInicio());
                filtros.setNumFinal(pRedSol.getNumFinal());
                filtros.setIdSolicitudDistinct(pRedSol.getSolicitudRedistribucion().getId());
                filtros.setIdPstAsignatario(pRedSol.getProveedorSolicitante().getId());
                filtros.setFechaHistoricoHasta(pRedSol.getSolicitudRedistribucion().getFechaAsignacion());
                filtros.setOrderType(FiltroBusquedaHistoricoRangos.ORDEN_DESC);

                // En el método check se comprueba que existan movimientos para poder cancelar.
                List<HistoricoRangoSerieNng> listaMovs = seriesNngService.findAllHistoricActionsFromRangos(filtros);

                // La lista viene ordenada por ID's DESC. El primer elemento de la lista se corresponde con el
                // último movimiento del rango afectado antes de la redistribución, por lo que tiene los valores
                // exactos para devolver el rango al estado inmediatamente anterior.
                HistoricoRangoSerieNng hrsnng = listaMovs.get(0);

                if (hrsnng.getIdPstConcesionario() != null) {
                    rangoRedist.setConcesionario(proveedoresService.getProveedorById(hrsnng.getIdPstConcesionario()));
                } else {
                    rangoRedist.setConcesionario(null);
                }

                if (hrsnng.getIdPstArrendatario() != null) {
                    rangoRedist.setArrendatario(proveedoresService.getProveedorById(hrsnng.getIdPstArrendatario()));
                } else {
                    rangoRedist.setArrendatario(null);
                }

                rangoRedist.setCliente(hrsnng.getCliente());
                rangoRedist.setNumInicio(hrsnng.getNumInicio());
                rangoRedist.setNumFinal(hrsnng.getNumFinal());

                // Reestablecemos la solicitud anterior que tuviese el rango
                Solicitud solicitudAnterior = new Solicitud();
                solicitudAnterior.setId(pRedSol.getConsecutivoAsignacion());
                rangoRedist.setSolicitud(solicitudAnterior);

                seriesNngService.saveRangoSerie(rangoRedist);
                bitacoraService.add("Rango reestablecido: " + rangoRedist.toString());

            }
        }

        // Si todo ha ido bien actualizamos el estado de la redistribución solicitada.
        if (checkCancelacion.isCancelacionPosible()) {

            // Rangos de fracciones que se han reestablecido
            for (RangoSerieNng rango : rangosEliminar) {
                seriesNngService.removeRangoSerie(rango);
                bitacoraService.add("Rango/Fracción descartada: " + rango.toString());
            }

            // Se hace un Cascade.ALL sobre la solicitud, por lo que los cambios se aplican
            // al guardar la solicitud.
            EstadoRedistribucionSolicitada ers = new EstadoRedistribucionSolicitada();
            ers.setCodigo(EstadoRedistribucionSolicitada.CANCELADO);
            pRedSol.setEstatus(ers);
        }

        return checkCancelacion;
    }

    /**
     * Comprueba si es posible realizar la cancelación de un trámite de Redistribución.
     * @param pRedSol Información de la RedistribucionSolicitadaNng
     * @return PeticionCancelacion con la información del chequeo.
     * @throws Exception en caso de error.
     */
    private PeticionCancelacion checkCancelRedistribucion(RedistribucionSolicitadaNng pRedSol) throws Exception {
        // Para cancelar una RedistribucionSolicitadaNng comprobamos:
        // 1.- Que la numeración redistribuida siga existiendo
        // 2.- Que la numeración redistribuida no haya tenido un trámite posterior
        // 3.- Si existe fraccionamiento, que las fracciones sigan existiendo
        // 4.- Si existe fraccionamiento, que las fracciones no hayan tenido un trámite posterior
        // 5.- Si no existe fraccionamiento, que existan movimientos en el histórico de rangos.
        // 6.- Que exista el consecutivo de la solicitud anterior del rango.
        PeticionCancelacion checkCancelacion = new PeticionCancelacion();
        checkCancelacion.setCancelacionPosible(true);

        // Recuperamos el rango si sigue existiendo.
        RangoSerieNng rangoRedist = seriesNngService.getRangoSerie(
                pRedSol.getIdClaveServicio(),
                pRedSol.getSna(),
                pRedSol.getNumInicio(),
                pRedSol.getProveedorSolicitante());

        if (rangoRedist != null) {
            if (rangoRedist.getSolicitud().getId().intValue()
                != pRedSol.getSolicitudRedistribucion().getId().intValue()) {
                StringBuffer sbMensaje = new StringBuffer();
                sbMensaje.append("El trámite ").append(rangoRedist.getSolicitud().getId());
                sbMensaje.append(" es posterior a la redistribución y afecta a la misma numeración.");
                checkCancelacion.setMensajeError(sbMensaje.toString());
                checkCancelacion.setCancelacionPosible(false);
            } else {
                // Comprobamos que exista el Consecutivo de la Solicitud que creó el rango para poder reasignárselo.
                // Es necesario comprobarlo ya que puede haber rangos mal migrados que no dispongan de ese dato y
                // fallaría la cancelación
                if (rangoRedist.getConsecutivoAsignacion() == null) {
                    StringBuffer sbError = new StringBuffer();
                    sbError.append("La siguiente numeración redistribuida no dispone ");
                    sbError.append("de información del trámite de asignación que la generó: ").append(rangoRedist);
                    checkCancelacion.setMensajeError(sbError.toString());
                    checkCancelacion.setCancelacionPosible(false);
                } else {
                    // Recomponemos el fraccionamiento del rango si existía.
                    if (pRedSol.getFraccionamientoRango().equals("S")) {
                        RangoSerieNng rangoOriginal = seriesNngService.getRangoSerieOriginalByFraccion(
                                rangoRedist, pRedSol.getSolicitudRedistribucion().getId(),
                                pRedSol.getProveedorSolicitante());

                        if (rangoOriginal != null) {
                            // Existe fraccionamiento sobre el rango cedido
                            if (rangoOriginal.getSolicitud().getId().intValue()
                            != pRedSol.getSolicitudRedistribucion().getId().intValue()) {
                                StringBuffer sbMensaje = new StringBuffer();
                                sbMensaje.append("El trámite ").append(rangoOriginal.getSolicitud().getId());
                                sbMensaje.append(" es posterior a la redistribución y afecta a la misma numeración.");
                                checkCancelacion.setMensajeError(sbMensaje.toString());
                                checkCancelacion.setCancelacionPosible(false);
                            }
                        } else {
                            // Si el rangoOriginal es nulo significa que no se han encontrado las distintas
                            // fracciones del rango que se redistribuyó. Pueden hacerse liberado despúes
                            // del trámite por lo que ya no es posibe cancelar el trámite.
                            StringBuffer sbError = new StringBuffer();
                            sbError.append("Las fracciones derivadas de la redistribución del rango: ").append(
                                    rangoRedist);
                            sbError.append(" ya no se encuentran en la numeración del Proveedor Solicitante.");
                            checkCancelacion.setMensajeError(sbError.toString());
                            checkCancelacion.setCancelacionPosible(false);
                        }
                    } else {
                        // Para rehacer una redistribución de un rango no fraccionado usamos el histórico de movimientos
                        // sobre rangos. Para ello, tenemos que comprobar que exista almenos un movimiento previo al
                        // realizado por la redistribución.

                        // Filtros para recuperar el estado anterior del rango. Buscamos el movimiento anterior
                        // al creado por la redistribución.
                        FiltroBusquedaHistoricoRangos filtros = new FiltroBusquedaHistoricoRangos();
                        filtros.setIdClaveServicio(pRedSol.getIdClaveServicio());
                        filtros.setSna(pRedSol.getSna());
                        filtros.setNumInicio(pRedSol.getNumInicio());
                        filtros.setNumFinal(pRedSol.getNumFinal());
                        filtros.setIdSolicitudDistinct(pRedSol.getSolicitudRedistribucion().getId());
                        filtros.setIdPstAsignatario(pRedSol.getProveedorSolicitante().getId());
                        // Ha de existir fecha de asignación ya que el trámite está terminado.
                        filtros.setFechaHistoricoHasta(pRedSol.getSolicitudRedistribucion().getFechaAsignacion());
                        int movsCount = seriesNngService.findAllHistoricActionsFromRangosCount(filtros);

                        // Para poder cancelar la redistribución han de existir al menos el movimiento de asignación del
                        // rango.
                        if (movsCount < 1) {
                            StringBuffer sbError = new StringBuffer();
                            sbError.append("El rango redistribuido con clave de servicio: ").append(
                                    pRedSol.getIdClaveServicio());
                            sbError.append(", ").append("serie: ").append(pRedSol.getSna()).append(", ");
                            sbError.append("inicio: ").append(pRedSol.getNumInicio())
                                    .append(" no tiene información de asignación en el histórico de movimientos.");
                            checkCancelacion.setMensajeError(sbError.toString());
                            checkCancelacion.setCancelacionPosible(false);
                        }
                    }
                }
            }
        } else {
            // El rango redistribuido ya no pertence al Solicitante.
            StringBuffer sbError = new StringBuffer();
            sbError.append("El rango redistribuido con clave de servicio: ").append(pRedSol.getIdClaveServicio());
            sbError.append(", ").append("serie: ").append(pRedSol.getSna()).append(", ");
            sbError.append("inicio: ").append(pRedSol.getNumInicio()).append(" ya no pertenece al Solicitante");
            checkCancelacion.setMensajeError(sbError.toString());
            checkCancelacion.setCancelacionPosible(false);
        }

        return checkCancelacion;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public PeticionCancelacion cancelSolicitudRedistribucion(SolicitudRedistribucionNng pSolicitud) throws Exception {

        // Cargamos las redistribuciones de la solicitud y comprobamos que se puedan cancelar.
        SolicitudRedistribucionNng solicitud = this.getSolicitudRedistribucionEagerLoad(pSolicitud);
        List<String> avisosCancelacion = new ArrayList<String>(solicitud.getRedistribucionesSolicitadas().size());
        boolean isCancelacionPosible = true;
        for (RedistribucionSolicitadaNng redSol : solicitud.getRedistribucionesSolicitadas()) {
            PeticionCancelacion checkCancelacionCes = this.checkCancelRedistribucion(redSol);
            if (!checkCancelacionCes.isCancelacionPosible()) {
                avisosCancelacion.add(checkCancelacionCes.getMensajeError());
            }
            isCancelacionPosible = isCancelacionPosible && checkCancelacionCes.isCancelacionPosible();
        }

        PeticionCancelacion checkCancelacion = new PeticionCancelacion();
        if (isCancelacionPosible) {
            for (RedistribucionSolicitadaNng redSol : solicitud.getRedistribucionesSolicitadas()) {
                this.cancelRedistribucion(redSol, false);
            }

            // Cambiamos el status de la Solicitud
            EstadoSolicitud statusSolCancelada = new EstadoSolicitud();
            statusSolCancelada.setCodigo(EstadoSolicitud.SOLICITUD_CANCELADA);
            solicitud.setEstadoSolicitud(statusSolCancelada);
            this.saveSolicitudRedistribucion(solicitud);

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
    public boolean isSolicitudWithRangos(Solicitud solicitud) {
        return solicitudDao.isSolicitudWithRangos(solicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudLineasActivasNng saveSolicitudLineasActivas(SolicitudLineasActivasNng solicitud) {
        return solicitudLinActNngDao.saveSolicitudLineasActivas(solicitud);
    }

    @Override
    public List<NumeracionAsignadaNng> findAllNumeracionAsignadaBySolicitud(SolicitudAsignacionNng solicitud) {
        return numeracionAsignadaNngDao.findAllNumeracionAsignadaBySolicitud(solicitud);
    }

}
