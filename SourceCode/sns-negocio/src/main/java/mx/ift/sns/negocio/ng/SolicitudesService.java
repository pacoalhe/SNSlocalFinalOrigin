package mx.ift.sns.negocio.ng;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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

import mx.ift.sns.dao.ng.IAbnDao;
import mx.ift.sns.dao.ng.INirDao;
import mx.ift.sns.dao.ng.INumeracionAsignadaDAO;
import mx.ift.sns.dao.ng.INumeracionRedistribuidaDAO;
import mx.ift.sns.dao.ng.INumeracionSolicitadaDAO;
import mx.ift.sns.dao.ng.IPoblacionAbnDao;
import mx.ift.sns.dao.ng.ISolicitudAsignacionDao;
import mx.ift.sns.dao.ng.ISolicitudCesionNgDao;
import mx.ift.sns.dao.ng.ISolicitudConsolidacionDao;
import mx.ift.sns.dao.ng.ISolicitudLiberacionNgDao;
import mx.ift.sns.dao.ng.ISolicitudLineasActivasDao;
import mx.ift.sns.dao.ng.ISolicitudRedistribucionNgDao;
import mx.ift.sns.dao.solicitud.ISolicitudDao;
import mx.ift.sns.dao.solicitud.ITipoSolicitudDao;
import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.abn.EstadoAbn;
import mx.ift.sns.modelo.abn.PoblacionAbn;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSolicitudes;
import mx.ift.sns.modelo.ng.AbnConsolidar;
import mx.ift.sns.modelo.ng.Cesion;
import mx.ift.sns.modelo.ng.CesionSolicitadaNg;
import mx.ift.sns.modelo.ng.EstadoAbnConsolidar;
import mx.ift.sns.modelo.ng.Liberacion;
import mx.ift.sns.modelo.ng.LiberacionSolicitadaNg;
import mx.ift.sns.modelo.ng.NirConsolidar;
import mx.ift.sns.modelo.ng.NumeracionRedistribuida;
import mx.ift.sns.modelo.ng.NumeracionSolicitada;
import mx.ift.sns.modelo.ng.PoblacionConsolidar;
import mx.ift.sns.modelo.ng.RangoSerie;
import mx.ift.sns.modelo.ng.RedistribucionSolicitadaNg;
import mx.ift.sns.modelo.ng.SolicitudAsignacion;
import mx.ift.sns.modelo.ng.SolicitudCesionNg;
import mx.ift.sns.modelo.ng.SolicitudConsolidacion;
import mx.ift.sns.modelo.ng.SolicitudLiberacionNg;
import mx.ift.sns.modelo.ng.SolicitudLineasActivas;
import mx.ift.sns.modelo.ng.SolicitudRedistribucionNg;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.series.EstadoRango;
import mx.ift.sns.modelo.series.Nir;
import mx.ift.sns.modelo.solicitud.EstadoCesionSolicitada;
import mx.ift.sns.modelo.solicitud.EstadoLiberacionSolicitada;
import mx.ift.sns.modelo.solicitud.EstadoRedistribucionSolicitada;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.modelo.solicitud.Solicitud;
import mx.ift.sns.modelo.solicitud.TipoSolicitud;
import mx.ift.sns.negocio.IBitacoraService;
import mx.ift.sns.negocio.PeticionCancelacion;
import mx.ift.sns.negocio.oficios.IOficiosService;
import mx.ift.sns.negocio.ot.IOrganizacionTerritorialService;
import mx.ift.sns.negocio.psts.IProveedoresService;
import mx.ift.sns.negocio.usu.IUsuariosService;
import mx.ift.sns.utils.date.FechasUtils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación métodos de servicio para Solicitudes.
 */
@Stateless(name = "SolicitudesService", mappedName = "SolicitudesService")
@Remote(ISolicitudesService.class)
public class SolicitudesService implements ISolicitudesService {

    /** Constante del tamaño de bloque al aplicar cesiones */
    private static final int BLOQUE = 100;

    /** Logger de la clase . */
    private static final Logger LOGGER = LoggerFactory.getLogger(SolicitudesService.class);

    /** Inyeccion del propio servicio para no perder la transaccionalidad en llamadas a ametodos propios. */
    @EJB
    private ISolicitudesService solicitudesService;

    /** Servicio series. */
    @EJB
    private ISeriesService seriesService;

    /** Servicio de Proveedores. */
    @EJB
    private IProveedoresService proveedoresService;

    /** Servicio de Oficios. */
    @EJB
    private IOficiosService oficiosService;

    /** DAO Solicitudes Genéricas. */
    @Inject
    private ISolicitudDao solicitudDao;

    /** DAO Solicitudes de Asignacion. */
    @Inject
    private ISolicitudAsignacionDao solicitudAsigDao;

    /** DAO Solicitudes de Cesion. */
    @Inject
    private ISolicitudCesionNgDao solicitudCesionDao;

    /** DAO de Solicitudes de Liberacion. */
    @Inject
    private ISolicitudLiberacionNgDao solicitudLiberacionDao;

    /** DAO de Soliciudes de Redistribución. */
    @Inject
    private ISolicitudRedistribucionNgDao solicitudRedistribucionDao;

    /** DAO de Solicitudes de Consolidación. */
    @Inject
    private ISolicitudConsolidacionDao solicitudConsolidacionDao;

    /** DAO de Solicitudes de Lineas Activas. */
    @Inject
    private ISolicitudLineasActivasDao solicitudLineasActivasDao;

    /** Dao NIr. */
    @Inject
    private INirDao nirDao;

    /** Servicio organizacion territorial. */
    @EJB
    private IOrganizacionTerritorialService otService;

    /** Dao Abn. */
    @Inject
    private IAbnDao abnDao;

    /** Dao PoblacionAbn. */
    @Inject
    private IPoblacionAbnDao poblacionAbnDao;

    /** Dao para Tipos de Solicitud. */
    @Inject
    private ITipoSolicitudDao tipoSolicitudDao;

    /** Dao para Numeracion Asignada. */
    @Inject
    private INumeracionAsignadaDAO numAsignadaDao;

    /** Dao para Numeracion Solcitada. */
    @Inject
    private INumeracionSolicitadaDAO numSolcitadaDao;

    /** Dao de Numeracion Redistribuida. */
    @Inject
    private INumeracionRedistribuidaDAO numRedistDao;

    /** Servicio de usuarios. */
    @EJB(mappedName = "UsuariosService")
    private IUsuariosService usuariosService;

    /** Servicio de Bitácora. */
    @EJB(mappedName = "BitacoraService")
    private IBitacoraService bitacoraService;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudAsignacion saveSolicitudAsignacion(SolicitudAsignacion solicitudAsignacion) throws Exception {
        if (solicitudAsignacion.getTipoSolicitud() == null) {
            solicitudAsignacion.setTipoSolicitud(tipoSolicitudDao.getTipoSolicitudById(TipoSolicitud.ASIGNACION));
        }
        return solicitudAsigDao.saveOrUpdate(solicitudAsignacion);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudCesionNg saveSolicitudCesion(SolicitudCesionNg pSolicitud) throws Exception {
        return solicitudCesionDao.saveOrUpdate(pSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudLiberacionNg saveSolicitudLiberacion(SolicitudLiberacionNg pSolicitud) throws Exception {
        return solicitudLiberacionDao.saveOrUpdate(pSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudConsolidacion saveSolicitudConsolidacion(SolicitudConsolidacion pSolicitud) throws Exception {
        return solicitudConsolidacionDao.saveOrUpdate(pSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public SolicitudLiberacionNg applyLiberacionesSolicitadas(SolicitudLiberacionNg pSolicitud) throws Exception {

        // Parseamos las fechas para eliminar las horas y minutos
        Date fHoy = FechasUtils.getFechaHoy();

        // Estado para las liberaciones que se ejecuten.
        EstadoLiberacionSolicitada els = new EstadoLiberacionSolicitada();
        els.setCodigo(EstadoLiberacionSolicitada.LIBERADO);

        boolean todoLiberado = true;
        List<Liberacion> liberaciones = new ArrayList<Liberacion>();

        // Agrupamos las liberaciones por rango
        HashMap<String, List<LiberacionSolicitadaNg>> rangosByLiberacion =
                new HashMap<String, List<LiberacionSolicitadaNg>>(1);

        for (LiberacionSolicitadaNg libSol : pSolicitud.getLiberacionesSolicitadas()) {
            // Ignoramos las liberaciones que ya se hayan ejecutado.
            if (libSol.getEstado().getCodigo().equals(EstadoLiberacionSolicitada.PENDIENTE)) {
                // Recuperamos la Información del Rango al que se ha solicitado Liberar
                RangoSerie rangoOriginal = seriesService.getRangoSerieByFraccion(
                        libSol.getIdNir(),
                        libSol.getSna(),
                        libSol.getNumInicio(),
                        libSol.getNumFinal(),
                        libSol.getProveedorCesionario());

                String key = rangoOriginal.getIdentificadorRango();
                if (!rangosByLiberacion.containsKey(key)) {
                    rangosByLiberacion.put(key, new ArrayList<LiberacionSolicitadaNg>());
                }
                rangosByLiberacion.get(key).add(libSol);
            }
        }

        // Si solo existe una liberación sobre una rango y los límites del rango coinciden, es que se
        // quiere ceder el rango completo. Si no, se quieren realizar fracciones sobre rangos.

        for (String rangoKey : rangosByLiberacion.keySet()) {
            if (rangosByLiberacion.get(rangoKey).size() == 1) {
                LiberacionSolicitadaNg libSol = rangosByLiberacion.get(rangoKey).get(0);
                Date fImplementacion = FechasUtils.parseFecha(libSol.getFechaLiberacion());

                // Si los valores inicial y final coinciden es que se quiere liberar la serie completa
                if (libSol.getFraccionamientoRango().equals("N")) {
                    // Liberación de rango completo
                    List<Liberacion> liberacionRango = seriesService.liberarRangoCompleto(libSol,
                            fHoy.equals(fImplementacion));

                    // Si se devuelven liberaciones es que se ha liberado la serie.
                    boolean serieLiberada = (!liberacionRango.isEmpty());
                    if (serieLiberada) {
                        libSol.setEstado(els);
                    }

                    liberaciones.addAll(liberacionRango);
                    todoLiberado = todoLiberado && serieLiberada;
                } else {
                    // Liberación de las fracciones en estado 'Afectado' del rango.
                    List<Liberacion> liberacionesRango =
                            seriesService.liberarFraccionesRango(rangosByLiberacion.get(rangoKey),
                                    fHoy.equals(fImplementacion));

                    // Si se devuelven liberaciones es que se ha liberado la serie.
                    boolean rangoFraccionado = (!liberacionesRango.isEmpty());
                    if (rangoFraccionado) {
                        libSol.setEstado(els);
                    }

                    liberaciones.addAll(liberacionesRango);
                    todoLiberado = todoLiberado && rangoFraccionado;
                }
            } else {
                // Si existen varias liberaciones significa que queremos fraccionar el rango

                // Las liberaciones vienen agrupadas por el rango, por lo que todas tienen los
                // mismos valores a excepción del número inicial y número final.
                // Para obtener fecha de implementación nos vale cualquiera de la lista.
                LiberacionSolicitadaNg infoLibSol = rangosByLiberacion.get(rangoKey).get(0);
                Date fImplementacion = FechasUtils.parseFecha(infoLibSol.getFechaLiberacion());

                // Liberación de las fracciones en estado 'Afectado' del rango.
                List<Liberacion> liberacionesRango = seriesService.liberarFraccionesRango(
                        rangosByLiberacion.get(rangoKey),
                        fHoy.equals(fImplementacion));

                // Si se devuelven liberaciones es que se ha liberado la serie.
                boolean rangoFraccionado = (!liberacionesRango.isEmpty());
                if (rangoFraccionado) {
                    for (LiberacionSolicitadaNg libSol : rangosByLiberacion.get(rangoKey)) {
                        libSol.setEstado(els);
                    }
                }

                liberaciones.addAll(liberacionesRango);
                todoLiberado = todoLiberado && rangoFraccionado;
            }
        }

        // Comprobamos si se han efectuado todas las liberaciones
        if (!rangosByLiberacion.isEmpty()) {

            // Añadimos las liberaciones creadas a la solicitud
            for (Liberacion lib : liberaciones) {
                pSolicitud.addLiberacion(lib);
            }

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
        EstadoLiberacionSolicitada statusLibSol = new EstadoLiberacionSolicitada();
        statusLibSol.setCodigo(EstadoLiberacionSolicitada.PENDIENTE);

        // Filtros de búsqueda
        FiltroBusquedaSolicitudes filtroSolicitudes = new FiltroBusquedaSolicitudes();
        filtroSolicitudes.setEstado(estatusSol);
        filtroSolicitudes.setEstatusLibSol(statusLibSol);

        List<SolicitudLiberacionNg> solicitudes = this.findAllSolicitudesLiberacion(filtroSolicitudes);
        for (SolicitudLiberacionNg solicitud : solicitudes) {
            if (!solicitud.getLiberacionesSolicitadas().isEmpty()) {
                // Capturamos la excepción aquí para que se ejecuten el resto de liberaciones pendientes.
                try {
                    bitacoraService.add(solicitud,
                            "Comprobando Liberaciones NG pendientes de Solicitud " + solicitud.getId());
                    this.applyLiberacionesSolicitadas(solicitud);
                } catch (Exception e) {
                    LOGGER.error("Error ejecutando Liberaciones NG pendientes.", e);
                }
            }
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudRedistribucionNg applyRedistribucionesSolicitadas(SolicitudRedistribucionNg pSolicitud)
            throws Exception {

        // Estado Redistribuido
        EstadoRedistribucionSolicitada ers = new EstadoRedistribucionSolicitada();
        ers.setCodigo(EstadoRedistribucionSolicitada.REDISTRIBUIDO);

        // Agrupamos las redistribuciones de fracciones por rango
        HashMap<String, List<RedistribucionSolicitadaNg>> fraccionesByRedist =
                new HashMap<String, List<RedistribucionSolicitadaNg>>(1);

        // Lista de Redistribuciones aplicadas
        List<NumeracionRedistribuida> numsDistribuidas =
                new ArrayList<NumeracionRedistribuida>(pSolicitud.getRedistribucionesSolicitadas().size());

        // Diferenciamos entre fracciones y rangos completos
        for (RedistribucionSolicitadaNg redSol : pSolicitud.getRedistribucionesSolicitadas()) {
            if (redSol.getEstado().getCodigo().equals(EstadoRedistribucionSolicitada.PENDIENTE)) {
                if (redSol.getFraccionamientoRango().equals("S")) {
                    // Si se trata de una fracción primero hay que crearla. Para ello agrupamos todas las fracciones por
                    // rango primero.
                    RangoSerie rangoOriginal = seriesService.getRangoSerieByFraccion(
                            redSol.getIdNir(),
                            redSol.getSna(),
                            redSol.getNumInicio(),
                            redSol.getNumFinal(),
                            redSol.getProveedorSolicitante());

                    String key = rangoOriginal.getId().getId().toString();
                    if (!fraccionesByRedist.containsKey(key)) {
                        fraccionesByRedist.put(key, new ArrayList<RedistribucionSolicitadaNg>());
                    }
                    fraccionesByRedist.get(key).add(redSol);
                } else {
                    // Si es un rango completo basta con actualizar los campos. Se comprueba que el rango
                    // exista y tenga el estatus correcto antes de pasar a redistribuir.
                    RangoSerie rangoCompleto = seriesService.getRangoSerie(
                            redSol.getIdNir(),
                            redSol.getSna(),
                            redSol.getNumInicio(),
                            redSol.getProveedorSolicitante());

                    numsDistribuidas.add(seriesService.redistribuirRango(rangoCompleto, redSol));
                }
            }
        }

        // Redistribuciones de Fracciones
        if (!fraccionesByRedist.isEmpty()) {
            for (List<RedistribucionSolicitadaNg> listaFracciones : fraccionesByRedist.values()) {
                numsDistribuidas.addAll(seriesService.fraccionarRangoFromRedistribuciones(listaFracciones));
            }
        }

        // Si todo ha ido bien actualizamos los estados de las Redistribuciones Solicitadas
        for (RedistribucionSolicitadaNg redSol : pSolicitud.getRedistribucionesSolicitadas()) {
            if (redSol.getEstado().getCodigo().equals(EstadoRedistribucionSolicitada.PENDIENTE)) {
                redSol.setEstado(ers);
            }
        }

        // Si existen cambios actualizamos la solicitud
        if (!numsDistribuidas.isEmpty()) {
            EstadoSolicitud es = new EstadoSolicitud();
            es.setCodigo(EstadoSolicitud.SOLICITUD_TERMINADA);
            pSolicitud.setEstadoSolicitud(es);

            // Añadimos las redistribuciones creadas a la solicitud
            for (NumeracionRedistribuida numRed : numsDistribuidas) {
                pSolicitud.addNumeracionRedistribuida(numRed);
            }

            // Guardamos los cambios
            pSolicitud.setFechaAsignacion(new Date());
            return this.saveSolicitudRedistribucion(pSolicitud);
        } else {
            return pSolicitud;
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public SolicitudCesionNg applyCesionesSolicitadas(SolicitudCesionNg pSolicitud) throws Exception {
        // Calculamos el numero de bloques
        int bloques = (pSolicitud.getCesionesSolicitadas().size() / BLOQUE) + 1;
        for (int i = 0; i < bloques; i++) {
            LOGGER.debug("Inicio bloque {}", i);
            // Obtenemos el indice de la ultima cesion solicitada que vamos a procesar en este bucle
            int ultima = (i + 1) == bloques ? pSolicitud.getCesionesSolicitadas().size() : BLOQUE * (i + 1);

            pSolicitud = solicitudesService.procesarCesionesSolicitadas(
                    new ArrayList<CesionSolicitadaNg>(pSolicitud.getCesionesSolicitadas().subList(i * BLOQUE, ultima)),
                    pSolicitud);

            LOGGER.debug("Fin bloque {}", i);
        }

        return pSolicitud;

    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public SolicitudCesionNg procesarCesionesSolicitadas(List<CesionSolicitadaNg> cesionesSolicitadas,
            SolicitudCesionNg pSolicitud) throws Exception {

        // Parseamos las fechas para eliminar las horas y minutos
        Date fHoy = FechasUtils.getFechaHoy();

        // Estado para las cesiones que se ejecuten.
        EstadoCesionSolicitada ecs = new EstadoCesionSolicitada();
        ecs.setCodigo(EstadoCesionSolicitada.CEDIDO);

        List<Cesion> cesiones = new ArrayList<Cesion>();

        // Agrupamos las cesiones por rango
        HashMap<RangoSerie, List<CesionSolicitadaNg>> rangosByCesion =
                new HashMap<RangoSerie, List<CesionSolicitadaNg>>(1);
        
        boolean todoCedido = true;
        for (CesionSolicitadaNg cesSol : cesionesSolicitadas) {
            // Ignoramos las cesiones que ya se hayan ejecutado.
            if (cesSol.getEstado().getCodigo().equals(EstadoCesionSolicitada.PENDIENTE)) {

                // Recuperamos la Información del Rango al que se ha solicitado ceder
                RangoSerie rangoOriginal = seriesService.getRangoSerieByFraccion(
                        cesSol.getIdNir(),
                        cesSol.getSna(),
                        cesSol.getNumInicio(),
                        cesSol.getNumFinal(),
                        cesSol.getProveedorCedente());

                // String key = rangoOriginal.getIdentificadorRango();
                if (!rangosByCesion.containsKey(rangoOriginal)) {
                    rangosByCesion.put(rangoOriginal, new ArrayList<CesionSolicitadaNg>());
                }
                rangosByCesion.get(rangoOriginal).add(cesSol);

            }
        }

        // Si solo existe una cesión sobre una rango y los límites del rango coinciden, es que se
        // quiere ceder el rango completo. Si no, se quieren realizar fracciones sobre rangos.

        for (RangoSerie rangoKey : rangosByCesion.keySet()) {

            if (rangosByCesion.get(rangoKey).size() == 1) {
                CesionSolicitadaNg cesSol = rangosByCesion.get(rangoKey).get(0);
                Date fImplementacion = FechasUtils.parseFecha(cesSol.getFechaCesion());

                // Si los valores inicial y final coinciden es que se quiere ceder el rango completo
                if (cesSol.getFraccionamientoRango().equals("N")) {
                    // Cesión de rango completo
                    List<Cesion> cesionRango = seriesService
                            .cederRangoCompleto(cesSol, fHoy.equals(fImplementacion));

                    // Si se devuelven Objetos Cesión es que se han aplicado las cesiones.
                    // En este caso, al ser solo 1 rango, si no esta vacío el array es que se ha ejecutado.
                    boolean rangoCedido = (!cesionRango.isEmpty());
                    if (rangoCedido) {
                        cesSol.setEstado(ecs);
                        pSolicitud.getCesionesSolicitadas().set(pSolicitud.getCesionesSolicitadas().indexOf(cesSol),
                                cesSol);
                    }

                    cesiones.addAll(cesionRango);
                    todoCedido=todoCedido && rangoCedido;
                    

                } else {
                    // Cesión de las fracciones en estado 'Afectado' del rango.
                    List<Cesion> cesionesRango = seriesService.cederFraccionesRango(rangoKey,
                            rangosByCesion.get(rangoKey),
                            fHoy.equals(fImplementacion));

                    // Si se devuelven Objetos Cesión es que se han aplicado las cesiones.
                    // En este caso, al ser solo 1 rango, si no esta vacío el array es que se ha ejecutado.
                    boolean rangoFraccionado = (!cesionesRango.isEmpty());
                    if (rangoFraccionado) {
                        cesSol.setEstado(ecs);
                        pSolicitud.getCesionesSolicitadas().set(pSolicitud.getCesionesSolicitadas().indexOf(cesSol),
                                cesSol);
                    }

                    cesiones.addAll(cesionesRango);
                    todoCedido=todoCedido && rangoFraccionado;

                }
            } else {
                // Si existen varias cesiones para un mismo rango significa que queremos fraccionar el rango

                // Las cesiones vienen agrupadas por el rango, por lo que todas tienen los
                // mismos valores a excepción del número inicial y número final.
                // Para obtener fecha de implementación nos vale cualquiera de la lista.
                CesionSolicitadaNg infoCesSol = rangosByCesion.get(rangoKey).get(0);
                Date fImplementacion = FechasUtils.parseFecha(infoCesSol.getFechaCesion());

                // Cesión de las fracciones en estado 'Afectado' del rango.
                List<Cesion> cesionesRango = seriesService
                        .cederFraccionesRango(rangoKey, rangosByCesion.get(rangoKey), fHoy.equals(fImplementacion));

                // Si se devuelven Objetos Cesión es que se han aplicado las cesiones.
                boolean rangoFraccionado = (!cesionesRango.isEmpty());
                if (rangoFraccionado) {
                    for (CesionSolicitadaNg cesSol : rangosByCesion.get(rangoKey)) {
                        cesSol.setEstado(ecs);
                        pSolicitud.getCesionesSolicitadas().set(pSolicitud.getCesionesSolicitadas().indexOf(cesSol),
                                cesSol);
                    }
                }

                cesiones.addAll(cesionesRango);

            }

        }

        // Añadimos las cesiones creadas a la solicitud
        List<Cesion> cesionesToAplicar=new ArrayList<Cesion>();
        for (Cesion ces : cesiones) {
            pSolicitud.addCesion(ces,cesionesToAplicar);
        }

        EstadoSolicitud es = new EstadoSolicitud();
        // Comprobamos si se han efectuado todas las cesiones
        
                    
        
        //if (pSolicitud.getCesionesSolicitadas().size() == pSolicitud.getCesionesAplicadas().size()) {
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
        return solicitudCesionDao.saveOrUpdate(pSolicitud);

    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
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

        List<SolicitudCesionNg> solicitudes = this.findAllSolicitudesCesion(filtroSolicitudes);
        for (SolicitudCesionNg solicitud : solicitudes) {
            if (solicitud.getCesionesSolicitadas() != null && !solicitud.getCesionesSolicitadas().isEmpty()) {
                // Capturamos la excepción aquí para que se ejecuten el resto de cesiones pendientes.
                try {
                    bitacoraService.add(solicitud,
                            "Comprobando Cesiones NG pendientes de Solicitud " + solicitud.getId());
                    this.applyCesionesSolicitadas(solicitud);
                } catch (Exception e) {
                    LOGGER.error("Error ejecutando Cesiones NG pendientes.", e);
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

            List<SolicitudCesionNg> solicitudes = this.findAllSolicitudesCesion(filtroSolicitudes);
            for (SolicitudCesionNg solicitud : solicitudes) {
                for (CesionSolicitadaNg cesSol : solicitud.getCesionesSolicitadas()) {
                    if (!cesSol.getEstado().getCodigo().equals(EstadoCesionSolicitada.CEDIDO)) {
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
        EstadoLiberacionSolicitada statusLibSol = new EstadoLiberacionSolicitada();
        statusLibSol.setCodigo(EstadoLiberacionSolicitada.PENDIENTE);

        // Filtros de búsqueda
        FiltroBusquedaSolicitudes filtroSolicitudes = new FiltroBusquedaSolicitudes();
        filtroSolicitudes.setEstado(estatusSol);
        filtroSolicitudes.setEstatusLibSol(statusLibSol);
        filtroSolicitudes.setFechaLiberacionHasta(FechasUtils.calculaFecha(FechasUtils.getFechaHoy(), 2, 0, 0));

        try {
            // Parseamos las fechas para eliminar las horas y minutos
            Date fHoy = FechasUtils.getFechaHoy();

            // Agrupamos las Solicitudes por fecha
            HashMap<String, Integer> tramitesByFecha = new HashMap<String, Integer>(1);
            ArrayList<String> tramitesFallidos = new ArrayList<String>();
            StringBuffer key;

            List<SolicitudLiberacionNg> solicitudes = this.findAllSolicitudesLiberacion(filtroSolicitudes);
            for (SolicitudLiberacionNg solicitud : solicitudes) {
                for (LiberacionSolicitadaNg libSol : solicitud.getLiberacionesSolicitadas()) {
                    if (!libSol.getEstado().getCodigo().equals(EstadoLiberacionSolicitada.LIBERADO)) {
                        // Si se ha sobrepasado la fecha de liberación es que hay algún problema para en la liberación.
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
    public SolicitudCesionNg getSolicitudCesionById(BigDecimal pConsecutivo) {
        return solicitudCesionDao.getSolicitudCesionById(pConsecutivo);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudCesionNg getSolicitudCesionEagerLoad(SolicitudCesionNg pSolicitud) throws Exception {
        return solicitudCesionDao.getSolicitudCesionEagerLoad(pSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public PeticionCancelacion cancelSolicitudCesion(SolicitudCesionNg pSolicitud) throws Exception {

        // Cargamos las cesiones de la solicitud y comprobamos que se puedan cancelar.
        SolicitudCesionNg solicitud = this.getSolicitudCesionEagerLoad(pSolicitud);
        List<String> avisosCancelacion = new ArrayList<String>(solicitud.getCesionesSolicitadas().size());
        boolean isCancelacionPosible = true;
        for (CesionSolicitadaNg cesSol : solicitud.getCesionesSolicitadas()) {
            PeticionCancelacion checkCancelacionCes = this.checkCancelCesion(cesSol);
            if (!checkCancelacionCes.isCancelacionPosible()) {
                avisosCancelacion.add(checkCancelacionCes.getMensajeError());
            }
            isCancelacionPosible = isCancelacionPosible && checkCancelacionCes.isCancelacionPosible();
        }

        PeticionCancelacion checkCancelacion = new PeticionCancelacion();
        if (isCancelacionPosible) {
            for (CesionSolicitadaNg cesSol : solicitud.getCesionesSolicitadas()) {
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
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public PeticionCancelacion cancelSolicitudLiberacion(SolicitudLiberacionNg pSolicitud) throws Exception {

        // Cargamos las liberaciones de la solicitud y comprobamos que se puedan cancelar.
        SolicitudLiberacionNg solicitud = this.getSolicitudLiberacionEagerLoad(pSolicitud);
        List<String> avisosCancelacion = new ArrayList<String>(solicitud.getLiberacionesSolicitadas().size());
        boolean isCancelacionPosible = true;
        for (LiberacionSolicitadaNg libSol : solicitud.getLiberacionesSolicitadas()) {
            PeticionCancelacion checkCancelacionLib = this.checkCancelLiberacion(libSol);
            if (!checkCancelacionLib.isCancelacionPosible()) {
                avisosCancelacion.add(checkCancelacionLib.getMensajeError());
            }
            isCancelacionPosible = isCancelacionPosible && checkCancelacionLib.isCancelacionPosible();
        }

        PeticionCancelacion checkCancelacion = new PeticionCancelacion();
        if (isCancelacionPosible) {
            for (LiberacionSolicitadaNg libSol : solicitud.getLiberacionesSolicitadas()) {
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

    /**
     * Comprueba si es posible la cancelación de una liberación.
     * @param pLibSol Información de la liberación.
     * @return Objeto PeticionCancelacion con la información del proceso.
     * @throws Exception En caso de error.
     */
    private PeticionCancelacion checkCancelLiberacion(LiberacionSolicitadaNg pLibSol) throws Exception {
        PeticionCancelacion checkCancelacion = new PeticionCancelacion();
        checkCancelacion.setCancelacionPosible(true);

        if (pLibSol.getEstado().getCodigo().equals(EstadoLiberacionSolicitada.PENDIENTE)) {
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
            sbAviso.append("ABN: ").append(pLibSol.getIdAbn()).append(", ");
            sbAviso.append("Nir: ").append(pLibSol.getCdgNir()).append(", ");
            sbAviso.append("Serie: ").append(pLibSol.getSna()).append(", ");
            sbAviso.append("N.Inicio: ").append(pLibSol.getNumInicio()).append(". ");
            sbAviso.append("La fecha de implementación ya se ha cumplido: ");
            sbAviso.append(FechasUtils.fechaToString(pLibSol.getFechaLiberacion()));
            checkCancelacion.setCancelacionPosible(false);
            checkCancelacion.setMensajeError(sbAviso.toString());
        }
        return checkCancelacion;
    }

    @Override
    public PeticionCancelacion cancelLiberacion(LiberacionSolicitadaNg pLibSol, boolean pUseCheck) throws Exception {
        PeticionCancelacion checkCancelacion = null;
        if (pUseCheck) {
            checkCancelacion = this.checkCancelLiberacion(pLibSol);
        } else {
            checkCancelacion = new PeticionCancelacion();
            checkCancelacion.setCancelacionPosible(true);
        }

        if (checkCancelacion.isCancelacionPosible()) {
            RangoSerie rango = null;
            if (pLibSol.getFraccionamientoRango().equals("S")) {
                rango = seriesService.getRangoSerieByFraccion(
                        pLibSol.getIdNir(), pLibSol.getSna(),
                        pLibSol.getNumInicio(), pLibSol.getNumFinal(),
                        pLibSol.getProveedorCesionario());
            } else {
                rango = seriesService.getRangoSerie(
                        pLibSol.getIdNir(), pLibSol.getSna(),
                        pLibSol.getNumInicio(), pLibSol.getProveedorCesionario());
            }

            // Comprobamos que el rango siga existiendo para poder reestablecerlo. Si no
            // existe permitimos la cancelación igualmente para poder eliminar la liberación solicitada.
            if (rango != null) {
                if (!rango.getEstadoRango().getCodigo().equals(EstadoRango.ASIGNADO)) {
                    // Estado Asignado para Rangos
                    EstadoRango statusAsignado = new EstadoRango();
                    statusAsignado.setCodigo(EstadoRango.ASIGNADO);

                    Solicitud solicitudAnterior = new Solicitud();
                    solicitudAnterior.setId(pLibSol.getConsecutivoAsignacion());
                    rango.setSolicitud(solicitudAnterior);
                    rango.setEstadoRango(statusAsignado);
                    seriesService.saveRangoSerie(rango);
                }

                bitacoraService.add("Rango reestablecido: " + rango.toString());
            }

            // Estado Cancelada para la liberación. Se actualiza al guardar la solicitud.
            EstadoLiberacionSolicitada statusLibSolCancelada = new EstadoLiberacionSolicitada();
            statusLibSolCancelada.setCodigo(EstadoLiberacionSolicitada.CANCELADO);
            pLibSol.setEstado(statusLibSolCancelada);
        }

        return checkCancelacion;
    }

    /**
     * Comprueba si es posible la cancelación de una cesión..
     * @param pCesSol Información de la cesión.
     * @return Objeto PeticionCancelacion con la información del proceso.
     * @throws Exception En caso de error.
     */
    private PeticionCancelacion checkCancelCesion(CesionSolicitadaNg pCesSol) throws Exception {
        PeticionCancelacion checkCancelacion = new PeticionCancelacion();
        checkCancelacion.setCancelacionPosible(true);

        if (pCesSol.getEstado().getCodigo().equals(EstadoCesionSolicitada.PENDIENTE)) {
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
            sbAviso.append("ABN: ").append(pCesSol.getIdAbn()).append(", ");
            sbAviso.append("Nir: ").append(pCesSol.getCdgNir()).append(", ");
            sbAviso.append("Serie: ").append(pCesSol.getSna()).append(", ");
            sbAviso.append("N.Inicio: ").append(pCesSol.getNumInicio()).append(". ");
            sbAviso.append("La fecha de implementación ya se ha cumplido: ");
            sbAviso.append(FechasUtils.fechaToString(pCesSol.getFechaCesion()));
            checkCancelacion.setCancelacionPosible(false);
            checkCancelacion.setMensajeError(sbAviso.toString());
        }
        return checkCancelacion;
    }

    @Override
    public PeticionCancelacion cancelCesion(CesionSolicitadaNg pCesSol, boolean pUseCheck) throws Exception {
        PeticionCancelacion checkCancelacion = null;
        if (pUseCheck) {
            checkCancelacion = this.checkCancelCesion(pCesSol);
        } else {
            checkCancelacion = new PeticionCancelacion();
            checkCancelacion.setCancelacionPosible(true);
        }

        if (checkCancelacion.isCancelacionPosible()) {
            RangoSerie rango = null;
            if (pCesSol.getFraccionamientoRango().equals("S")) {
                rango = seriesService.getRangoSerieByFraccion(
                        pCesSol.getIdNir(), pCesSol.getSna(),
                        pCesSol.getNumInicio(), pCesSol.getNumFinal(),
                        pCesSol.getProveedorCedente());
            } else {
                rango = seriesService.getRangoSerie(
                        pCesSol.getIdNir(), pCesSol.getSna(),
                        pCesSol.getNumInicio(), pCesSol.getProveedorCedente());
            }

            // Comprobamos que el rango siga existiendo para poder reestablecerlo. Si no
            // existe permitimos la cancelación igualmente para poder eliminar la cesión solicitada.
            if (rango != null) {
                if (!rango.getEstadoRango().getCodigo().equals(EstadoRango.ASIGNADO)) {
                    // Estado Asignado para Rangos
                    EstadoRango statusAsignado = new EstadoRango();
                    statusAsignado.setCodigo(EstadoRango.ASIGNADO);

                    Solicitud solicitudAnterior = new Solicitud();
                    solicitudAnterior.setId(pCesSol.getConsecutivoAsignacion());
                    rango.setSolicitud(solicitudAnterior);
                    rango.setEstadoRango(statusAsignado);
                    seriesService.saveRangoSerie(rango);
                }

                bitacoraService.add("Rango reestablecido: " + rango.toString());
            }

            // Estado Cancelada para la cesión. Se actualiza al guardar la solicitud.
            EstadoCesionSolicitada statusCancelada = new EstadoCesionSolicitada();
            statusCancelada.setCodigo(EstadoCesionSolicitada.CANCELADO);
            pCesSol.setEstado(statusCancelada);
        }

        return checkCancelacion;
    }

    @Override
    public PeticionCancelacion cancelRedistribucion(RedistribucionSolicitadaNg pRedSol, boolean pUseCheck)
            throws Exception {

        List<RangoSerie> rangosEliminar = new ArrayList<RangoSerie>();

        // Para cancelaciones de solicitudes completas el check se hace en el método cancelSolicitudRedistribucion.
        // En el caso de querer cancelar solamente una RedistribucionSolicitadaNg y no el trámite entonces sí es
        // necesario validar.
        PeticionCancelacion checkCancelacion = null;
        if (pUseCheck) {
            // Comprobamos si la cancelación es posible
            checkCancelacion = this.checkCancelacionRedistribucion(pRedSol);
        } else {
            checkCancelacion = new PeticionCancelacion();
            checkCancelacion.setCancelacionPosible(true);
        }

        if (checkCancelacion.isCancelacionPosible()) {
            // El rango redistribuido existe o no se hubieran pasado las validaciones
            RangoSerie rangoRedist = seriesService.getRangoSerie(
                    pRedSol.getIdNir(), pRedSol.getSna(), pRedSol.getNumInicio(), pRedSol.getProveedorSolicitante());

            // Recomponemos el fraccionamiento del rango si existía.
            boolean rangoFraccionado = (!StringUtils.isEmpty(pRedSol.getFraccionamientoRango())
                    && pRedSol.getFraccionamientoRango().equals("S"));

            if (rangoFraccionado) {
                // Recomponemos el fraccionamiento. En el método cancelSolicitudRedistribucion() se comprueba
                // que existan los rangos del fraccionamiento.
                RangoSerie rangoOriginal = seriesService.getRangoSerieOriginalByFraccion(
                        rangoRedist, pRedSol.getSolicitudRedistribucion().getId(),
                        pRedSol.getProveedorSolicitante());

                // Eliminamos el rango anterior al ser parte de la fracción
                if (rangoRedist.getNumInicioAsInt() != rangoOriginal.getNumInicioAsInt()) {
                    RangoSerie rangoAnterior = seriesService.getRangoSerie(
                            rangoOriginal.getSerie().getNir().getId(),
                            rangoOriginal.getSerie().getId().getSna(),
                            rangoOriginal.getNumInicio(), rangoOriginal.getAsignatario());

                    rangosEliminar.add(rangoAnterior);
                }

                // Eliminamos el rango posterior al ser parte de la fracción
                if (rangoRedist.getNumFinalAsInt() != rangoOriginal.getNumFinalAsInt()) {
                    // Inicio del Rango Posterior
                    String numInicialRPost = StringUtils.leftPad(
                            String.valueOf((rangoRedist.getNumFinalAsInt() + 1)), 4, '0');

                    RangoSerie rangoPosterior = seriesService.getRangoSerie(
                            rangoOriginal.getSerie().getNir().getId(),
                            rangoOriginal.getSerie().getId().getSna(),
                            numInicialRPost, rangoOriginal.getAsignatario());

                    rangosEliminar.add(rangoPosterior);
                }

                // Solicitud Anterior
                Solicitud solicitudAnterior = new Solicitud();
                solicitudAnterior.setId(pRedSol.getConsecutivoAsignacion());
                rangoOriginal.setSolicitud(solicitudAnterior);

                seriesService.saveRangoSerie(rangoOriginal);
                bitacoraService.add("Rango reestablecido: " + rangoOriginal.toString());

                // Eliminamos el rango redistribuido
                rangosEliminar.add(rangoRedist);

            } else {
                // El rango se redistribuyó sin fraccionamiento. Reestablecemos la información
                // original del rango. En el método cancelSolicitudRedistribucion() se comprueba
                // que el rango existe.

                if (pRedSol.getNumeracionesRedistribuidas().isEmpty()) {
                    // Las numeraciones redistribuidas se agregan directamente a la solicitud, no a la redistribución
                    // solicitada. Si no se ha hecho un getSolicitudXXXEagerLoad previamente la lista de numeraciones
                    // redistribuidas de las redistribuciones solicitadas estará vacía. Se da el caso, por ejemplo, al
                    // generar una redistribución nueva, ir a la pantalla de oficios, volver a atrás y cancelar la
                    // redistribución.
                    List<NumeracionRedistribuida> numeracionesRedis =
                            numRedistDao.getNumeracionRedistribuidaById(rangoRedist.getSolicitud().getId());
                    pRedSol.getNumeracionesRedistribuidas().addAll(numeracionesRedis);
                }

                // Solamente existe una NumeracionRedistribuida por RedistribucionSolicitadaNg con
                // la información original del Rango antes de la redistribución.
                NumeracionRedistribuida numRed = pRedSol.getNumeracionesRedistribuidas().get(0);
                rangoRedist.setConcesionario(numRed.getConcesionario());
                rangoRedist.setArrendatario(numRed.getArrendatario());
                rangoRedist.setNumInicio(numRed.getInicioRango());
                rangoRedist.setNumFinal(numRed.getFinRango());
                rangoRedist.setCentralDestino(numRed.getCentralDestino());
                rangoRedist.setCentralOrigen(numRed.getCentralOrigen());
                rangoRedist.setPoblacion(numRed.getPoblacion());
                rangoRedist.setTipoModalidad(numRed.getTipoModalidad());
                rangoRedist.setTipoRed(numRed.getTipoRed());

                // Reestablecemos la solicitud anterior que tuviese el rango
                Solicitud solicitudAnterior = new Solicitud();
                solicitudAnterior.setId(pRedSol.getConsecutivoAsignacion());
                rangoRedist.setSolicitud(solicitudAnterior);

                seriesService.saveRangoSerie(rangoRedist);
                bitacoraService.add("Rango reestablecido: " + rangoRedist.toString());
            }
        }

        // Si todo ha ido bien actualizamos el estado de la redistribución solicitada.
        if (checkCancelacion.isCancelacionPosible()) {

            // Rangos de fracciones que se han reestablecido
            for (RangoSerie rango : rangosEliminar) {
                seriesService.removeRangoSerie(rango);
                bitacoraService.add("Rango/Fracción descartada: " + rango.toString());
            }

            EstadoRedistribucionSolicitada ers = new EstadoRedistribucionSolicitada();
            ers.setCodigo(EstadoRedistribucionSolicitada.CANCELADO);
            pRedSol.setEstado(ers);

            // Se hace un Cascade.ALL sobre la solicitud, por lo que los cambios se aplican
            // al guardar la solicitud. No es necesario guardar la Redistribución solicitada.
            // pRedSol = this.saveRedistribucionSolicitada(pRedSol);
        }

        return checkCancelacion;
    }

    @Override
    public PeticionCancelacion cancelSolicitudRedistribucion(SolicitudRedistribucionNg pSolicitud) throws Exception {
        SolicitudRedistribucionNg solicitud = this.getSolicitudRedistribucionEagerLoad(pSolicitud);
        PeticionCancelacion checkCancelacion = null;
        for (RedistribucionSolicitadaNg redSol : solicitud.getRedistribucionesSolicitadas()) {
            checkCancelacion = this.checkCancelacionRedistribucion(redSol);
            if (!checkCancelacion.isCancelacionPosible()) {
                // Si alguna cancelación no es posible abortamos la cacelación completa del trámite
                break;
            }
        }

        // Si la solicitud no tiene redistribuciones la Petición de Cancelación en éste punto
        // es null. La instanciamos para que se cambie el estado de la solicitud.
        if (checkCancelacion == null) {
            checkCancelacion = new PeticionCancelacion();
            checkCancelacion.setCancelacionPosible(true);
        }

        // Si las cancelaciones son posibles aplicamos los cambios
        if (checkCancelacion.isCancelacionPosible()) {
            for (RedistribucionSolicitadaNg redSol : solicitud.getRedistribucionesSolicitadas()) {
                // Ejecutamos las cancelaciones sin volver a chequear
                checkCancelacion = this.cancelRedistribucion(redSol, false);
            }

            // Cambiamos el estado de la solicitud
            EstadoSolicitud statusCancelada = new EstadoSolicitud();
            statusCancelada.setCodigo(EstadoSolicitud.SOLICITUD_CANCELADA);
            solicitud.setEstadoSolicitud(statusCancelada);
            this.saveSolicitudRedistribucion(solicitud);
        }

        return checkCancelacion;
    }

    /**
     * Comprueba si es posible realizar la cancelación de un trámite de Redistribución.
     * @param pRedSol Información de la RedistribucionSolicitadaNg
     * @return PeticionCancelacion con la información del chequeo.
     * @throws Exception en caso de error.
     */
    private PeticionCancelacion checkCancelacionRedistribucion(RedistribucionSolicitadaNg pRedSol) throws Exception {
        // Para cancelar una RedistribucionSolicitadaNg comprobamos:
        // 1.- Que la numeración redistribuida siga existiendo
        // 2.- Que la numeración redistribuida no haya tenido un trámite posterior
        // 3.- Si existe fraccionamiento, que las fracciones sigan existiendo
        // 4.- Si existe fraccionamiento, que las fracciones no hayan tenido un trámite posterior
        // 5.- Que exista el consecutivo de la solicitud anterior del rango.
        PeticionCancelacion checkCancelacion = new PeticionCancelacion();
        checkCancelacion.setCancelacionPosible(true);

        if (seriesService.existeRangoSerie(
                pRedSol.getIdNir(), pRedSol.getSna(), pRedSol.getNumInicio(), pRedSol.getProveedorSolicitante())) {

            // Rango Redistribuido
            RangoSerie rangoRedist = seriesService.getRangoSerie(
                    pRedSol.getIdNir(), pRedSol.getSna(), pRedSol.getNumInicio(), pRedSol.getProveedorSolicitante());

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
                if (pRedSol.getConsecutivoAsignacion() == null) {
                    StringBuffer sbError = new StringBuffer();
                    sbError.append("La siguiente numeración redistribuida no dispone ");
                    sbError.append("de información del trámite de asignación que la generó: ").append(rangoRedist);
                    checkCancelacion.setMensajeError(sbError.toString());
                    checkCancelacion.setCancelacionPosible(false);
                } else {
                    // Recomponemos el fraccionamiento del rango si existía.
                    boolean rangoFraccionado = (!StringUtils.isEmpty(pRedSol.getFraccionamientoRango())
                            && pRedSol.getFraccionamientoRango().equals("S"));

                    if (rangoFraccionado) {
                        RangoSerie rangoOriginal = seriesService.getRangoSerieOriginalByFraccion(
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
                    }
                }
            }
        } else {
            // El rango redistribuido ya no pertence al Solicitante.
            StringBuffer sbError = new StringBuffer();
            sbError.append("El rango redistribuido con nir: ").append(pRedSol.getIdNir());
            sbError.append(", ").append("serie: ").append(pRedSol.getSna()).append(", ");
            sbError.append("inicio: ").append(pRedSol.getNumInicio()).append(" ya no pertenece al Solicitante");
            checkCancelacion.setMensajeError(sbError.toString());
            checkCancelacion.setCancelacionPosible(false);
        }

        return checkCancelacion;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudLiberacionNg getSolicitudLiberacionEagerLoad(SolicitudLiberacionNg pSolicitud) throws Exception {
        return solicitudLiberacionDao.getSolicitudLiberacionEagerLoad(pSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public SolicitudLiberacionNg getSolicitudLiberacionById(BigDecimal pConsecutivo) {
        return solicitudLiberacionDao.getSolicitudLiberacionById(pConsecutivo);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<SolicitudLiberacionNg> findAllSolicitudesLiberacion() {
        return solicitudLiberacionDao.findAllSolicitudesLiberacion();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<SolicitudLiberacionNg> findAllSolicitudesLiberacion(FiltroBusquedaSolicitudes pFiltrosSolicitud) {
        return solicitudLiberacionDao.findAllSolicitudesLiberacion(pFiltrosSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public int findAllSolicitudesLiberacionCount(FiltroBusquedaSolicitudes pFiltrosSolicitud) {
        return solicitudLiberacionDao.findAllSolicitudesLiberacionCount(pFiltrosSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<SolicitudCesionNg> findAllSolicitudesCesion() {
        return solicitudCesionDao.findAllSolicitudesCesion();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<SolicitudCesionNg> findAllSolicitudesCesion(FiltroBusquedaSolicitudes pFiltrosSolicitud) {
        return solicitudCesionDao.findAllSolicitudesCesion(pFiltrosSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public int findAllSolicitudesCesionCount(FiltroBusquedaSolicitudes pFiltrosSolicitud) {
        return solicitudCesionDao.findAllSolicitudesCesionCount(pFiltrosSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public SolicitudConsolidacion applyConsolidacionesSolicitadas(SolicitudConsolidacion pSolicitud) throws Exception {
        SimpleDateFormat formateador;
        Date fecha;
        Date fechaActual;
        Date fechaConso;
        String fechaSoli;
        String fechaSistema;
        List<AbnConsolidar> listaAbnConsolidar;
        int contador = 0;

        formateador = new SimpleDateFormat("dd/MM/yyyy");
        Abn abn = null;

        // pasa todo de un Abn al otro
        // y si están todas las conso en estado CONSOLIDADO se pone la solicitud TERMINADA
        // Hacer está comprobación para dar de baja el ABN o no RN034

        listaAbnConsolidar = pSolicitud.getAbnsConsolidados();

        for (AbnConsolidar abnConsolidar : listaAbnConsolidar) {
            fecha = new Date();
            fechaSistema = formateador.format(fecha);
            fechaActual = formateador.parse(fechaSistema);
            fechaSoli = formateador.format(abnConsolidar.getFechaConsolidacion());
            fechaConso = formateador.parse(fechaSoli);
            if (fechaActual.equals(fechaConso)
                    && abnConsolidar.getEstado().getCodigo().equals(EstadoAbnConsolidar.EN_TRAMITE)) {
                // Si la fecha del día en curso es igual a la fecha de consolidación y está en estado EN TRAMITE

                // Se busca en la tabla NIR_CONSOLIDAR los Nirs a consolidar
                LOGGER.debug("Se busca en la tabla NIR_CONSOLIDAR los Nirs pertenecientes al AbnConsolidar");
                // List<NirConsolidar> listaNir = nirConsolidarDao.findNirConsolidarById(abnConsolidar.getId());
                if (abnConsolidar.getNirConsolidar() != null && !abnConsolidar.getNirConsolidar().isEmpty()) {
                    // Si lo encuentra, se modifica

                    Nir nirAux = new Nir();
                    for (NirConsolidar nirConso : abnConsolidar.getNirConsolidar()) {
                        nirAux = nirConso.getNir();
                        nirAux.setAbn(pSolicitud.getAbnRecibe());

                        // Se hace la Auditoría del registro
                        nirAux.updateAuditableValues(usuariosService.getCurrentUser());

                        nirDao.saveOrUpdate(nirAux);
                    }
                }

                // Se busca en la tabla POBLACION_CONSOLIDAR las poblaciones a consolidar
                LOGGER.debug("Se busca en la tabla POBLACION_CONSOLIDAR "
                        + "las poblaciones pertenecientes al AbnConsolidar");
                // List<PoblacionConsolidar> listaPoblacionConsolidar = poblacionConsolidarDao
                // .findPoblacionConsolidarById(abnConsolidar.getId());
                if (abnConsolidar.getPoblacionConsolidar() != null
                        && !abnConsolidar.getPoblacionConsolidar().isEmpty()) {
                    for (PoblacionConsolidar poblacionConso : abnConsolidar.getPoblacionConsolidar()) {
                        poblacionConso.getPoblacionAbn().setAbn(pSolicitud.getAbnRecibe());
                        poblacionAbnDao.saveOrUpdate(poblacionConso.getPoblacionAbn());
                    }
                }

                EstadoAbnConsolidar estado = new EstadoAbnConsolidar();
                estado.setCodigo(EstadoAbnConsolidar.CONSOLIDADO);
                abnConsolidar.setEstado(estado);
                // abnConsolidarDao.saveAbnConsolidar(abnConsolidar);
            }
            // else if (abnConsolidar.getEstado().getCodigo().equals(EstadoAbnConsolidar.CONSOLIDADO)) {
            // // Si la fechas no son iguales se debe lanzar un proceso Batch para que haga la consolidación
            // throw new Exception("El Abn a consolidar seleccionado ya está consolidado");
            // }
        }
        pSolicitud = this.saveSolicitudConsolidacion(pSolicitud);

        // Se recorre la lista de Abn a consolidar y se comprueba si están todos consolidados o no.
        for (AbnConsolidar abnConsolidar : listaAbnConsolidar) {
            if (!abnConsolidar.getEstado().getCodigo().equals(EstadoAbnConsolidar.EN_TRAMITE)) {
                contador = contador + 1;
            }
        }

        if (listaAbnConsolidar.size() == contador) {
            // RN036
            // Se cambia el estado del Abn a ACTIVO
            abn = abnDao.getAbnById(pSolicitud.getAbnEntrega().getCodigoAbn());
            EstadoAbn estadoAbn = new EstadoAbn();
            estadoAbn.setCodigo(EstadoAbn.ACTIVO);
            abn.setEstadoAbn(estadoAbn);
            // Se hace la Auditoría del registro
            abn.updateAuditableValues(usuariosService.getCurrentUser());

            abnDao.saveOrUpdate(abn);

            // Se cambia el estado de la solicitud a TERMINADA
            EstadoSolicitud estadoSol = new EstadoSolicitud();
            estadoSol.setCodigo(EstadoSolicitud.SOLICITUD_TERMINADA);
            pSolicitud.setEstadoSolicitud(estadoSol);
            pSolicitud = this.saveSolicitudConsolidacion(pSolicitud);
        }

        // RN034
        // El ABN no tiene más poblaciones por consolidar, se pasa a estado INACTIVO
        List<Poblacion> listaPoblacion = poblacionAbnDao.findAllPoblacionesByAbn(pSolicitud.getAbnEntrega());
        if (listaPoblacion.isEmpty()) {
            // Si la consolidación es total el ABN se pasa a INACTIVO
            // abn = abnDao.getAbnById(pSolicitud.getAbnEntrega().getCodigoAbn());
            abn = pSolicitud.getAbnEntrega();
            EstadoAbn estadoAbn = new EstadoAbn();
            estadoAbn.setCodigo(EstadoAbn.INACTIVO);
            abn.setEstadoAbn(estadoAbn);
            // Se hace la Auditoría del registro
            abn.updateAuditableValues(usuariosService.getCurrentUser());

            abnDao.saveOrUpdate(abn);
        }

        return pSolicitud;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void applyConsolidacionesPendientes() throws Exception {

        // Solicitudes de consolidación en Trámite
        EstadoSolicitud estatusSol = new EstadoSolicitud();
        estatusSol.setCodigo(EstadoSolicitud.SOLICITUD_EN_TRAMITE);

        // Abn a Consolidar Pendientes
        EstadoAbnConsolidar statusConSol = new EstadoAbnConsolidar();
        statusConSol.setCodigo(EstadoAbnConsolidar.EN_TRAMITE);

        // Filtros de búsqueda
        FiltroBusquedaSolicitudes filtroSolicitudes = new FiltroBusquedaSolicitudes();
        filtroSolicitudes.setEstado(estatusSol);
        filtroSolicitudes.setEstatusAbnConso(statusConSol);

        List<SolicitudConsolidacion> solicitudes = this.findAllSolicitudesConsolidacion(filtroSolicitudes);
        for (SolicitudConsolidacion solicitud : solicitudes) {
            if (solicitud.getAbnsConsolidados() != null && !solicitud.getAbnsConsolidados().isEmpty()) {
                // Capturamos la excepción aquí para que se ejecuten el resto de consolidaciones pendientes.
                try {
                    bitacoraService.add(solicitud,
                            "Comprobando Consolidaciones NG pendientes de Solicitud " + solicitud.getId());
                    this.applyConsolidacionesSolicitadas(solicitud);
                } catch (Exception e) {
                    LOGGER.error("Error ejecutando Consolidaciones pendientes.", e);
                }
            }
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<SolicitudConsolidacion> findAllSolicitudesConsolidacion() {
        return solicitudConsolidacionDao.findAllSolicitudesConsolidacion();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<SolicitudConsolidacion> findAllSolicitudesConsolidacion(FiltroBusquedaSolicitudes pFiltrosSolicitud) {
        return solicitudConsolidacionDao.findAllSolicitudesConsolidacion(pFiltrosSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public int findAllSolicitudesConsolidacionCount(FiltroBusquedaSolicitudes pFiltrosSolicitud) {
        return solicitudConsolidacionDao.findAllSolicitudesConsolidacionCount(pFiltrosSolicitud);
    }

    // @Override
    // @Deprecated
    // public SolicitudCesionNg fusionarProveedores(SolicitudCesionNg pSolicitud, boolean pInmediata) throws Exception {
    //
    // // Comprobamos la fecha de Implementación
    // if (!pInmediata) {
    // // Parseamos las fechas para eliminar las horas y minutos
    // SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    // Date fImplementacion = sdf.parse(sdf.format(pSolicitud.getCesionesSolicitadas().get(0).getFechaCesion()));
    //
    // Calendar calendar = Calendar.getInstance();
    // calendar.add(Calendar.DAY_OF_MONTH, +1);
    // Date fMannana = sdf.parse(sdf.format(calendar.getTime()));
    //
    // // Solo se aplica la liberación si se ha llegado a la fecha de implementación o un día antes si
    // // se invoca desde el planificador
    // if (!(fImplementacion.equals(fMannana) || fImplementacion.before(fMannana))) {
    // if (LOGGER.isDebugEnabled()) {
    // LOGGER.debug("No es posible aplicar la fusión de proveedores. "
    // + "no se ha cumplido la fecha de implementación: " + sdf.format(fImplementacion));
    // }
    // return pSolicitud;
    // }
    // }
    //
    // // Proveedores de la Cesión
    // Proveedor pstCedente = pSolicitud.getProveedorSolicitante();
    // Proveedor pstCesionario = pSolicitud.getProveedorCesionario();
    //
    // // Modificamos el tipo de servicio del proveedor cesionario para que soporte el servicio del cedente.
    // if (!pstCesionario.getTipoRed().getCdg().equals(TipoRed.AMBAS)) {
    // // Si el cesionario no soporta ambos tipos de red hemos de ver si soporta el mismo tipo del cedente.
    // if (!pstCedente.getTipoRed().getCdg().equals(pstCesionario.getTipoRed().getCdg())) {
    // // El cesionario no soporta el tipo de red del cedente. Hemos de cambiar al cesionario al
    // // tipo de red "Ambas".
    // TipoRed tipoRedAmbas = new TipoRed();
    // tipoRedAmbas.setCdg(TipoRed.AMBAS);
    //
    // pstCesionario.setTipoRedOriginal(pstCesionario.getTipoRed());
    // pstCesionario.setTipoRed(tipoRedAmbas);
    // }
    // }
    //
    // EstadoCesionSolicitada estadoCedido = new EstadoCesionSolicitada();
    // estadoCedido.setCodigo(EstadoCesionSolicitada.CEDIDO);
    //
    // // Modificamos el Asignatario de toda la numeración del cedente
    // ArrayList<Cesion> cesiones = new ArrayList<Cesion>(pSolicitud.getCesionesSolicitadas().size());
    // for (CesionSolicitadaNg cesSol : pSolicitud.getCesionesSolicitadas()) {
    // // Actualizamos el Rango (IDA, Asignatario y Solicitd)
    // Cesion cesion = seriesService.cederRangoEnFusion(cesSol);
    // cesiones.add(cesion);
    //
    // // Actualizamos la Cesión Solicitada
    // cesSol.setEstado(estadoCedido);
    // this.saveCesionSolicitada(cesSol);
    // }
    //
    // // Actualizar el Proveedor Cedente a estado Inactivo
    // Estatus estatusInactivo = new Estatus();
    // estatusInactivo.setCdg(Estatus.INACTIVO);
    // pstCedente.setEstatus(estatusInactivo);
    // proveedoresService.updateProveedor(pstCedente);
    //
    // // Comprobamos si se han efectuado todas las cesiones
    // EstadoSolicitud es = new EstadoSolicitud();
    // es.setCodigo(EstadoSolicitud.SOLICITUD_TERMINADA);
    // pSolicitud.setEstadoSolicitud(es);
    //
    // // Añadimos las cesiones creadas a la solicitud
    // for (Cesion ces : cesiones) {
    // pSolicitud.addCesion(ces);
    // }
    //
    // // Guardamos los cambios
    // return this.saveSolicitudCesion(pSolicitud);
    // }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudConsolidacion applyAbnConsolidar(List<PoblacionAbn> listaPoblacion, List<Nir> listaNir,
            Date fechaConsolidacion, SolicitudConsolidacion solicitud) throws Exception {

        AbnConsolidar abnConsolidar;

        if (solicitud.getAbnsConsolidados().isEmpty()) {

            abnConsolidar = new AbnConsolidar();

            EstadoAbnConsolidar estadoConso = new EstadoAbnConsolidar();
            estadoConso.setCodigo(EstadoAbnConsolidar.EN_TRAMITE);
            abnConsolidar.setEstado(estadoConso);
            abnConsolidar.setPresuscripcion(solicitud.getAbnEntrega().getPresuscripcion());
            abnConsolidar.setFechaConsolidacion(fechaConsolidacion);
            abnConsolidar.setSolicitudConsolidacion(solicitud);

            // Recorrer la lista de poblaciones a consolidar y crear los registros necesarios en la tabla
            // NG_POBLACION_CONSOLIDAR
            List<PoblacionConsolidar> listaPoblacionConso = new ArrayList<PoblacionConsolidar>();
            for (PoblacionAbn poblacion : listaPoblacion) {
                PoblacionConsolidar pobConso = new PoblacionConsolidar();
                pobConso.setAbnConsolidar(abnConsolidar);
                pobConso.setPoblacionAbn(poblacion);
                listaPoblacionConso.add(pobConso);
            }
            abnConsolidar.setPoblacionConsolidar(listaPoblacionConso);

            // Recorrer la lista de nir's a consolidar y crear los registros necesarios en la tabla NG_NIR_CONSOLIDAR
            List<NirConsolidar> listaNirConso = new ArrayList<NirConsolidar>();
            for (Nir nir : listaNir) {
                NirConsolidar nirConso = new NirConsolidar();
                nirConso.setAbnConsolidar(abnConsolidar);
                nirConso.setNir(nir);
                listaNirConso.add(nirConso);
            }
            abnConsolidar.setNirConsolidar(listaNirConso);
        } else {
            abnConsolidar = solicitud.getAbnsConsolidados().get(0);
            abnConsolidar.setFechaConsolidacion(fechaConsolidacion);
        }

        // Se guarda en BD el AbnConsolidar
        // abnConsolidado = abnConsolidarDao.saveAbnConsolidar(abnConsolidar);
        solicitud.getAbnsConsolidados().add(abnConsolidar);

        // Se realiza la consolidación del Abn
        // Llamada al método que realiza las consolidaciones
        this.applyConsolidacionesSolicitadas(solicitud);

        return solicitud;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudConsolidacion getSolicitudConsolidacionById(BigDecimal pConsecutivo) {
        return solicitudConsolidacionDao.getSolicitudConsolidacionById(pConsecutivo);
    }

    @Override
    public TipoSolicitud getTipoSolicitudById(BigDecimal pConsecutivo) {
        return solicitudDao.getTipoSolicitudById(pConsecutivo);
    }

    @Override
    public SolicitudAsignacion getSolicitudAsignacionEagerLoad(SolicitudAsignacion pSolicitud) throws Exception {

        pSolicitud.setNumeracionAsignadas(numAsignadaDao.findAllNumeracionAsignadaBySolicitud(pSolicitud));
        pSolicitud.setNumeracionSolicitadas(numSolcitadaDao.getNumSolicitada(pSolicitud.getId()));
        pSolicitud.setRangos(seriesService.findAllRangosBySolicitud(pSolicitud));
        pSolicitud.setOficios(oficiosService.getOficiosBySolicitud(pSolicitud.getId()));
        return pSolicitud;
    }

    @Override
    public SolicitudAsignacion getSolicitudAsignacionById(BigDecimal pConsecutivo) {
        return solicitudAsigDao.getSolicitudAsignacionById(pConsecutivo);
    }

    @Override
    public SolicitudLineasActivas saveSolicitudLineasActivas(SolicitudLineasActivas solicitudLineasActivas) {
        return solicitudLineasActivasDao.saveSolicitudLineasActivas(solicitudLineasActivas);
    }

    @Override
    public List<SolicitudRedistribucionNg> findAllSolicitudesRedistribucion() {
        return solicitudRedistribucionDao.findAllSolicitudesRedistribucion();
    }

    @Override
    public List<SolicitudRedistribucionNg> findAllSolicitudesRedistribucion(
            FiltroBusquedaSolicitudes pFiltrosSolicitud) {
        return solicitudRedistribucionDao.findAllSolicitudesRedistribucion(pFiltrosSolicitud);
    }

    @Override
    public int findAllSolicitudesRedistribucionCount(FiltroBusquedaSolicitudes pFiltrosSolicitud) {
        return solicitudRedistribucionDao.findAllSolicitudesRedistribucionCount(pFiltrosSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudRedistribucionNg saveSolicitudRedistribucion(SolicitudRedistribucionNg pSolicitud) {
        return solicitudRedistribucionDao.saveOrUpdate(pSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public SolicitudRedistribucionNg getSolicitudRedistribucionById(BigDecimal pConsecutivo) {
        return solicitudRedistribucionDao.getSolicitudRedistribucionById(pConsecutivo);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SolicitudRedistribucionNg getSolicitudRedistribucionEagerLoad(SolicitudRedistribucionNg pSolicitud) {
        return solicitudRedistribucionDao.getSolicitudRedistribucionEagerLoad(pSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<TipoSolicitud> findAllTiposSolicitud() {
        return tipoSolicitudDao.findAllTiposSolicitudNG();
    }

    @Override
    public TipoSolicitud getTipoSolicitudById(Integer idTipoSolicitud) {
        return tipoSolicitudDao.getTipoSolicitudById(idTipoSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<Solicitud> findAllSolicitudes(FiltroBusquedaSolicitudes pFiltrosSolicitud)
            throws Exception {
        return solicitudDao.findAllSolicitudes(pFiltrosSolicitud);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public int findAllSolicitudesCount(FiltroBusquedaSolicitudes pFiltrosSolicitud) throws Exception {
        return solicitudDao.findAllSolicitudesCount(pFiltrosSolicitud);
    }

    @Override
    public boolean existNumeracionAsignadaBySolicita(NumeracionSolicitada numeracionSolicitada) {

        return numAsignadaDao.existNumeracionAsignadaBySolicita(numeracionSolicitada);
    }

    @Override
    public boolean isSolicitudPendieteByNir(BigDecimal idNir) {
        return solicitudDao.isSolicitudPendieteByNir(idNir);
    }

}
