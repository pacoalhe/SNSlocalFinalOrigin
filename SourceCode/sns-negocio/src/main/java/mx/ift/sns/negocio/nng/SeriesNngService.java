package mx.ift.sns.negocio.nng;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import mx.ift.sns.dao.ng.IEstadoRangoDao;
import mx.ift.sns.dao.ng.IParametroDao;
import mx.ift.sns.dao.nng.IHistoricoRangoSerieNngDao;
import mx.ift.sns.dao.nng.IHistoricoSerieNngDAO;
import mx.ift.sns.dao.nng.IRangoSerieNngDao;
import mx.ift.sns.dao.nng.ISerieNngDao;
import mx.ift.sns.modelo.filtros.FiltroBusquedaHistoricoRangos;
import mx.ift.sns.modelo.filtros.FiltroBusquedaHistoricoSeries;
import mx.ift.sns.modelo.filtros.FiltroBusquedaRangos;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSeries;
import mx.ift.sns.modelo.nng.CesionSolicitadaNng;
import mx.ift.sns.modelo.nng.ClaveServicio;
import mx.ift.sns.modelo.nng.DetalleRangoAsignadoNng;
import mx.ift.sns.modelo.nng.HistoricoRangoSerieNng;
import mx.ift.sns.modelo.nng.LiberacionSolicitadaNng;
import mx.ift.sns.modelo.nng.RangoSerieNng;
import mx.ift.sns.modelo.nng.RangoSerieNngPK;
import mx.ift.sns.modelo.nng.RedistribucionSolicitadaNng;
import mx.ift.sns.modelo.nng.SerieNng;
import mx.ift.sns.modelo.nng.SerieNngPK;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.pst.TipoProveedor;
import mx.ift.sns.modelo.series.EstadoRango;
import mx.ift.sns.modelo.series.HistoricoSerieNng;
import mx.ift.sns.modelo.series.IRangoSerie;
import mx.ift.sns.modelo.solicitud.EstadoLiberacionSolicitada;
import mx.ift.sns.negocio.IBitacoraService;
import mx.ift.sns.negocio.utils.excel.ExportarExcel;
import mx.ift.sns.utils.date.FechasUtils;
import mx.ift.sns.utils.series.RangosSeriesUtil;

/**
 * Implementación de los métodos del Servicio de Series de Numeración No Geográfica.
 */
@Stateless(name = "SeriesNngService", mappedName = "SeriesNngService")
@Remote(ISeriesNngService.class)
public class SeriesNngService implements ISeriesNngService {

    /** Logger de la clase . */
    // private static final Logger LOGGER = LoggerFactory.getLogger(SeriesNngService.class);

    /** DAO de parametros. */
    @Inject
    private IParametroDao parametroDao;

    /** DAO De RangosNng. */
    @Inject
    private IRangoSerieNngDao rangoSerieNngDao;

    /** DAO De EstadoRango. */
    @Inject
    private IEstadoRangoDao estadoRangoDao;

    /** DAO De Series NNG. */
    @Inject
    private ISerieNngDao serieNngDao;

    /** DAO Historico de Rangos NNG. */
    @Inject
    private IHistoricoRangoSerieNngDao histRangosNngDao;

    /** DAO Historico de Series NNG. */
    @Inject
    private IHistoricoSerieNngDAO histSerieNngDao;

    /** Servicio de Bitácora. */
    @EJB
    private IBitacoraService bitacoraService;

    /********************************************************************************************/

    @Override
    public List<RangoSerieNng> findAllRangos(FiltroBusquedaRangos pFiltros) {
        return rangoSerieNngDao.findAllRangos(pFiltros);
    }

    @Override
    public int findAllRangosCount(FiltroBusquedaRangos pFiltros) {
        return rangoSerieNngDao.findAllRangosCount(pFiltros);
    }

    @Override
    public RangoSerieNng getRangoSerie(BigDecimal pClaveServicio, BigDecimal pSna, String pNumInicial,
            Proveedor pAsignatario) {
        return rangoSerieNngDao.getRangoSerie(pClaveServicio, pSna, pNumInicial, pAsignatario);
    }

    @Override
    public RangoSerieNng getRangoSerieByFraccion(BigDecimal pClaveServicio, BigDecimal pSna, String pNumInicial,
            String pNumFinal, Proveedor pAsignatario) {
        return rangoSerieNngDao.getRangoSerieByFraccion(pClaveServicio, pSna, pNumInicial, pNumFinal, pAsignatario);
    }

    @Override
    public EstadoRango getEstadoRangoByCodigo(String codigo) {
        return estadoRangoDao.getEstadoRangoByCodigo(codigo);
    }

    @Override
    public Integer findAllSeriesCount(FiltroBusquedaSeries filtros) {
        return serieNngDao.findAllSeriesCount(filtros);
    }

    @Override
    public List<SerieNng> findAllSeries(FiltroBusquedaSeries filtros) {
        return serieNngDao.findAllSeries(filtros);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public RangoSerieNng saveRangoSerie(RangoSerieNng pRangoSerieNng) {
        if (pRangoSerieNng.getId() == null) {
            pRangoSerieNng.setId(new RangoSerieNngPK());
            pRangoSerieNng.getId().setIdClaveServicio(pRangoSerieNng.getClaveServicio().getCodigo());
            pRangoSerieNng.getId().setSna(pRangoSerieNng.getSna());
        }
        return rangoSerieNngDao.saveOrUpdate(pRangoSerieNng);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void removeRangoSerie(RangoSerieNng pRangoSerieNng) {
        rangoSerieNngDao.delete(pRangoSerieNng);
    }

    @Override
    public Integer getTotalOcupacionSerie(SerieNng serie) {
        return rangoSerieNngDao.getTotalOcupacionSerie(serie);
    }

    @Override
    public List<SerieNng> findRandomSeriesLibreByClaveServicio(ClaveServicio clave, int n) {
        return serieNngDao.findRandomSeriesLibreByClaveServicio(clave, n);
    }

    @Override
    public SerieNng getRandomSerieOcupadaByClaveServicio(ClaveServicio clave, BigDecimal cantidad) {
        return serieNngDao.getRandomSerieOcupadaByClaveServicio(clave, cantidad);
    }

    @Override
    public RangoSerieNng getRandomRangoByClaveServicio(ClaveServicio clave, int cantidad) {
        return rangoSerieNngDao.getRandomRangoByClaveServicio(clave, cantidad);
    }

    @Override
    public boolean cederRangoCompleto(CesionSolicitadaNng pCesionSolicitada, boolean pInmediata) throws Exception {

        // StringBuilder sbTraza = new StringBuilder();
        // sbTraza.append("Cesión de Rango NNG Completo.");
        // sbTraza.append(", Solicitud: ").append(pCesionSolicitada.getSolicitudCesion().getId());
        // sbTraza.append(", Rango con Clave Serv.: ");
        // sbTraza.append(pCesionSolicitada.getIdClaveServicio());
        // sbTraza.append(", Sna: ").append(pCesionSolicitada.getSna());
        // sbTraza.append(", Num.Ini: ").append(pCesionSolicitada.getNumInicio());
        // sbTraza.append(", Fecha Implementación: ");
        // sbTraza.append(FechasUtils.fechaToString(pCesionSolicitada.getFechaCesion()));
        // sbTraza.append(" (ejecución planificador: ");
        // sbTraza.append(FechasUtils.fechaToString(
        // FechasUtils.getFechaImplementacionReal(pCesionSolicitada.getFechaCesion()),
        // "dd/MM/yyyy HH:mm:ss"));
        // sbTraza.append("), Ejecución Inmediata: ").append(pInmediata);

        Date fHoy = new Date();
        boolean fImplementacionCumplida =
                (fHoy.compareTo(FechasUtils.getFechaImplementacionReal(pCesionSolicitada.getFechaCesion())) >= 0);

        // sbTraza.append(", Fecha Implementación Cumplida: ").append(fImplementacionCumplida);
        // LOGGER.info(sbTraza.toString());
        // bitacoraService.add(pCesionSolicitada.getSolicitudCesion(), sbTraza.toString());

        // Rango asignado al proveedor cedente que se quiere ceder
        RangoSerieNng rango = this.getRangoSerie(
                pCesionSolicitada.getIdClaveServicio(),
                pCesionSolicitada.getSna(),
                pCesionSolicitada.getNumInicio(),
                pCesionSolicitada.getProveedorCedente());

        // Si no se ha cumplido la fecha de implementación no se hace nada.
        if ((!pInmediata) && (!fImplementacionCumplida)) {
            // Estado Reservado para Rangos con fecha de implementación no cumplida.
            EstadoRango statusReservado = new EstadoRango();
            statusReservado.setCodigo(EstadoRango.RESERVADO);

            rango.setEstatus(statusReservado);
            rango.setSolicitud(pCesionSolicitada.getSolicitudCesion());
            this.saveRangoSerie(rango);

            // Trazas de Bitácora.
            bitacoraService.add(pCesionSolicitada.getSolicitudCesion(), "Rango reservado: " + rango.toString());

            return false;
        }

        // Cedemos el rango teniendo en cuenta la naturaleza del proveedor
        return cederRango(rango, pCesionSolicitada);
    }

    @Override
    public boolean cederFraccionesRango(List<CesionSolicitadaNng> pCesionesSolicitadas, boolean pInmediata)
            throws Exception {

        // Las cesiones vienen agrupadas por el rango, por lo que todas tienen los
        // mismos valores a excepción del número inicial y número final. Para obtener
        // la información del rango original o fecha de implementación nos vale cualquiera
        // de la lista.
        CesionSolicitadaNng cesSol = pCesionesSolicitadas.get(0);

        // StringBuilder sbTraza = new StringBuilder();
        // sbTraza.append("Cesión de Rango NNG Fraccionado.");
        // sbTraza.append(", Solicitud: ").append(cesSol.getSolicitudCesion().getId());
        // sbTraza.append(", Rango con Clave Serv.: ");
        // sbTraza.append(cesSol.getIdClaveServicio());
        // sbTraza.append(", Sna: ").append(cesSol.getSna());
        // sbTraza.append(", Fecha Implementación: ");
        // sbTraza.append(FechasUtils.fechaToString(cesSol.getFechaCesion()));
        // sbTraza.append(" (ejecución planificador: ");
        // sbTraza.append(FechasUtils.fechaToString(
        // FechasUtils.getFechaImplementacionReal(cesSol.getFechaCesion()), "dd/MM/yyyy HH:mm:ss"));
        // sbTraza.append("), Ejecución Inmediata: ").append(pInmediata);

        Date fHoy = new Date();
        boolean fImplementacionCumplida =
                (fHoy.compareTo(FechasUtils.getFechaImplementacionReal(cesSol.getFechaCesion())) >= 0);

        // sbTraza.append(", Fecha Implementación Cumplida: ").append(fImplementacionCumplida);
        // LOGGER.info(sbTraza.toString());
        // bitacoraService.add(cesSol.getSolicitudCesion(), sbTraza.toString());

        // Lista peticiones de fraccionamiento.
        List<IRangoSerie> fraccionesBySolicitud = this.getFraccionesByCesionesSolicitadas(pCesionesSolicitadas);

        // La primera fracción de la lista es la que tiene el número de Inicio del
        // rango que queremos fraccionar. Es necesario eliminar primer el Rango
        // para luego guardar las fracciones nuevas.
        RangoSerieNng rangoOriginal = this.getRangoSerieByFraccion(
                cesSol.getIdClaveServicio(),
                cesSol.getSna(),
                ((RangoSerieNng) fraccionesBySolicitud.get(0)).getNumInicio(),
                ((RangoSerieNng) fraccionesBySolicitud.get(0)).getNumFinal(),
                cesSol.getProveedorCedente());

        // Si no se ha cumplido la fecha de implementación no se hace nada.
        if ((!pInmediata) && (!fImplementacionCumplida)) {
            // Estado Reservado para Rangos con fecha de implementación no cumplida.
            EstadoRango statusReservado = new EstadoRango();
            statusReservado.setCodigo(EstadoRango.RESERVADO);

            rangoOriginal.setEstatus(statusReservado);
            rangoOriginal.setSolicitud(cesSol.getSolicitudCesion());
            this.saveRangoSerie(rangoOriginal);

            // Trazas de Bitácora.
            bitacoraService.add(cesSol.getSolicitudCesion(), "Rango reservado: " + cesSol.toString());

            return false;
        }

        // Cambiamos la solicitud del rango original para que cree los nuevos rangos asociados
        // a la nueva solicitud de cesión.
        rangoOriginal.setSolicitud(cesSol.getSolicitudCesion());

        // Estatus Asignado para los rangos Ficticios
        EstadoRango statusAsignado = this.getEstadoRangoByCodigo(EstadoRango.ASIGNADO);

        // Fracciones finales
        List<IRangoSerie> fraccionesIRango = RangosSeriesUtil.getFracciones(
                fraccionesBySolicitud, rangoOriginal, statusAsignado);
        List<RangoSerieNng> fracciones = RangosSeriesUtil.castListaRangoSerieNNG(fraccionesIRango);

        // Eliminamos el rango original y guardamos las fracciones
        this.removeRangoSerie(rangoOriginal);

        // Trazas de Bitácora.
        bitacoraService.add(cesSol.getSolicitudCesion(),
                "Rango eliminado para fraccionar: " + rangoOriginal.toString());

        // Almacenamos las reservas y cesiones
        for (RangoSerieNng fraccion : fracciones) {
            if (fraccion.getEstatus().getCodigo().equals(EstadoRango.AFECTADO)) {
                // Los rangos afectados son los que se quieren ceder.
                // Cedemos el rango teniendo en cuenta la naturaleza del proveedor
                cederRango(fraccion, cesSol);
            } else {
                // Los rangos no afectados han de mantener la info del rango original
                this.saveRangoSerie(fraccion);

                // Trazas de Bitácora.
                bitacoraService.add(cesSol.getSolicitudCesion(), "Rango actualizado: " + rangoOriginal.toString());
            }
        }

        return true;
    }

    /**
     * Aplica una cesión sobre un rango o fracción con la información de la cesión solicitada.
     * @param pRango Rango o Fracción a ceder.
     * @param pCesionSolicitada Información de la solicitud.
     * @return boolean 'True' si se ha podido ceder el rango
     * @throws Exception en caso de error.
     */
    private boolean cederRango(RangoSerieNng pRango, CesionSolicitadaNng pCesionSolicitada) throws Exception {

        Proveedor pst = pCesionSolicitada.getProveedorCesionario();

        // Diferenciamos entre Proveedor Cesionario Concesionario o Comercialziadora
        if (pst.getTipoProveedor().getCdg().equals(TipoProveedor.COMERCIALIZADORA)) {

            // Cesión Comercializadora - Comercializadora: El ABC de enrutamiento será el valor del ABC del
            // concesionario con quien se haya elegido para convenio.
            pRango.setAbc(pCesionSolicitada.getConvenioCesionario().getProveedorConcesionario().getAbc());

        } else if (pst.getTipoProveedor().getCdg().equals(TipoProveedor.CONCESIONARIO)) {

            // Cesión Comercializadora - Concesionario: ABC e BCD tendrán el mismo valor y será
            // el del ABC del concesionario (cesionario).
            pRango.setAbc(pCesionSolicitada.getProveedorCesionario().getAbc());
            pRango.setBcd(pCesionSolicitada.getProveedorCesionario().getAbc());

        } else {
            // Cesión Comercializadora - Ambos
            pRango.setBcd(pCesionSolicitada.getProveedorCesionario().getAbc());

            // Se puede utilizar el ABC Propio
            if (pCesionSolicitada.getUsarAbcCesionario().equals("S")) {
                pRango.setAbc(pCesionSolicitada.getProveedorCesionario().getAbc());
            }

            // Se puede utilizar el ABC de un Concesionario con el que se tenga convenio
            if (pCesionSolicitada.getUsarConvenioCesionario().equals("S")) {
                pRango.setAbc(pCesionSolicitada.getConvenioCesionario().getProveedorConcesionario().getAbc());
            }
        }

        // Cambiamos el estado a Asignado
        EstadoRango estadoAsignado = new EstadoRango();
        estadoAsignado.setCodigo(EstadoRango.ASIGNADO);
        pRango.setEstatus(estadoAsignado);

        // El rango tendrá el Identificador de la solicitud que lo cedió
        pRango.setSolicitud(pCesionSolicitada.getSolicitudCesion());

        // Cambiamos el Asignatario del Rango por el Id del Cesionario
        // El Cesionario es el que va a utilizar el rango a partir de ahora.
        pRango.setAsignatario(pCesionSolicitada.getProveedorCesionario());

        // Guardamos los cambios del Rango cedido
        this.saveRangoSerie(pRango);

        StringBuffer traza = new StringBuffer();
        traza.append("Rango Cedido de: ").append(pCesionSolicitada.getProveedorCedente().getNombreCorto());
        traza.append(" a ").append(pCesionSolicitada.getProveedorCesionario().getNombreCorto());

        // Trazas de Bitácora.
        bitacoraService.add(pCesionSolicitada.getSolicitudCesion(), traza.toString());

        return true;
    }

    /**
     * Crea una lista de Fracciones/Rangos en función de las peticiones de Cesión.
     * @param pCesiones Lista de cesiones solicitudas sobre un mismo rango.
     * @return lista de Fracciones/Rangos en función de las peticiones de cesión
     * @throws Exception en caso de error.
     */
    private List<IRangoSerie> getFraccionesByCesionesSolicitadas(List<CesionSolicitadaNng> pCesiones)
            throws Exception {

        // Listado de Fracciones contenidas en las Cesiones
        List<IRangoSerie> listaFracciones = new ArrayList<IRangoSerie>(pCesiones.size());

        EstadoRango estadoReservado = new EstadoRango();
        estadoReservado.setCodigo(EstadoRango.AFECTADO);

        // Recuperamos el Rango Original. Todas las cesiones de la lista
        // son referentes al mismo rango.
        RangoSerieNng rangoOriginal = this.getRangoSerieByFraccion(
                pCesiones.get(0).getIdClaveServicio(),
                pCesiones.get(0).getSna(),
                pCesiones.get(0).getNumInicio(),
                pCesiones.get(0).getNumFinal(),
                pCesiones.get(0).getProveedorCedente());

        // Creamos los rangos con la información de la Cesión Solicitada
        for (CesionSolicitadaNng cesSol : pCesiones) {

            // Info de la PK
            RangoSerieNngPK rangoPk = new RangoSerieNngPK();
            rangoPk.setIdClaveServicio(cesSol.getIdClaveServicio());
            rangoPk.setSna(cesSol.getSna());

            // Info de Rango
            RangoSerieNng rango = new RangoSerieNng();
            rango.setId(rangoPk);
            rango.setEstatus(estadoReservado);
            rango.setAbc(rangoOriginal.getAbc());
            rango.setBcd(rangoOriginal.getBcd());
            rango.setNumInicio(cesSol.getNumInicio());
            rango.setNumFinal(cesSol.getNumFinal());
            rango.setObservaciones(rangoOriginal.getObservaciones());
            rango.setAsignatario(rangoOriginal.getAsignatario());
            rango.setConcesionario(rangoOriginal.getConcesionario());
            rango.setArrendatario(rangoOriginal.getArrendatario());
            rango.setRentado(rangoOriginal.getRentado());
            rango.setSerie(this.getSerie(cesSol.getIdClaveServicio(), cesSol.getSna()));
            rango.setSolicitud(cesSol.getSolicitudCesion());
            rango.setFechaAsignacion(rangoOriginal.getFechaAsignacion());
            rango.setConsecutivoAsignacion(rangoOriginal.getConsecutivoAsignacion());
            rango.setOficioAsignacion(rangoOriginal.getOficioAsignacion());
            listaFracciones.add(rango);
        }

        return listaFracciones;
    }

    @Override
    public SerieNng getSerie(BigDecimal pIdClaveServicio, BigDecimal pSna) {
        return serieNngDao.getSerie(pIdClaveServicio, pSna);
    }

    @Override
    public Integer getTotalNumeracionAsignadaByClaveServicio(ClaveServicio clave) {
        return rangoSerieNngDao.getTotalNumeracionAsignadaByClaveServicio(clave);
    }

    @Override
    public String liberarRangoCompleto(LiberacionSolicitadaNng pLibSol, boolean pInmediata) throws Exception {
        // StringBuilder sbTraza = new StringBuilder();
        // sbTraza.append("Liberando Rango NNG Completo.");
        // sbTraza.append(", Solicitud: ").append(pLibSol.getSolicitudLiberacion().getId());
        // sbTraza.append(", Rango con Clave Serv.: ");
        // sbTraza.append(pLibSol.getIdClaveServicio());
        // sbTraza.append(", Sna: ").append(pLibSol.getSna());
        // sbTraza.append(", Fecha Implementación: ");
        // sbTraza.append(FechasUtils.fechaToString(pLibSol.getFechaLiberacion()));
        // sbTraza.append(" (ejecución planificador: ");
        // sbTraza.append(FechasUtils.fechaToString(
        // FechasUtils.getFechaImplementacionReal(pLibSol.getFechaLiberacion()), "dd/MM/yyyy HH:mm:ss"));
        // sbTraza.append("), Fecha Fin P.Reserva: ");
        // sbTraza.append(FechasUtils.fechaToString(pLibSol.getFechaFinReserva()));
        // sbTraza.append(" (ejecución planificador: ");
        // sbTraza.append(FechasUtils.fechaToString(
        // FechasUtils.getFechaImplementacionReal(pLibSol.getFechaFinReserva()), "dd/MM/yyyy HH:mm:ss"));
        // sbTraza.append("), Ejecución Inmediata: ").append(pInmediata);

        // Tenemos en cuenta los minutos.
        Date fHoy = new Date();
        boolean fFinReservaCumplida =
                (fHoy.compareTo(FechasUtils.getFechaImplementacionReal(pLibSol.getFechaFinReserva())) >= 0);

        boolean fImplementacionCumplida =
                (fHoy.compareTo(FechasUtils.getFechaImplementacionReal(pLibSol.getFechaLiberacion())) >= 0);

        // sbTraza.append(", Fecha Implementación Cumplida: ").append(fImplementacionCumplida);
        // sbTraza.append(", Fecha Fin Reserva Cumplida: ").append(fFinReservaCumplida);
        // LOGGER.info(sbTraza.toString());
        // bitacoraService.add(pLibSol.getSolicitudLiberacion(), sbTraza.toString());

        // El rango nunca debería ser nulo ya que queda reservado.
        RangoSerieNng rango = this.getRangoSerie(
                pLibSol.getIdClaveServicio(),
                pLibSol.getSna(),
                pLibSol.getNumInicio(),
                pLibSol.getProveedorCesionario());

        // Si no se ha cumplido la fecha de implementación no se hace nada.
        if ((!pInmediata) && (!fImplementacionCumplida)) {
            // Estado Reservado para Rangos con fecha de implementación no cumplida.
            EstadoRango statusReservado = new EstadoRango();
            statusReservado.setCodigo(EstadoRango.RESERVADO);

            rango.setEstatus(statusReservado);
            rango.setSolicitud(pLibSol.getSolicitudLiberacion());
            rango.setFechaFinReserva(pLibSol.getFechaFinReserva());
            this.saveRangoSerie(rango);

            // Trazas de Bitácora.
            bitacoraService.add(pLibSol.getSolicitudLiberacion(), "Rango reservado: " + rango.toString());

            return EstadoLiberacionSolicitada.PENDIENTE;
        }

        // Control de Periodo de Reserva
        if (fFinReservaCumplida) {
            // Eliminamos el Rango original.
            this.removeRangoSerie(rango);

            // Trazas de Bitácora.
            bitacoraService.add(pLibSol.getSolicitudLiberacion(), "Rango liberado: " + rango.toString());

            return EstadoLiberacionSolicitada.LIBERADO;

        } else {
            // Fecha de implementación cumplida pero periodo de reserva no.
            EstadoRango estatusReserva = new EstadoRango();
            estatusReserva.setCodigo(EstadoRango.PERIODO_RESERVA);
            rango.setEstatus(estatusReserva);
            rango.setSolicitud(pLibSol.getSolicitudLiberacion());
            rango.setFechaFinReserva(pLibSol.getFechaFinReserva());
            this.saveRangoSerie(rango);

            // Trazas de Bitácora.
            bitacoraService.add(pLibSol.getSolicitudLiberacion(), "Rango en periodo de reserva: " + rango.toString());

            return EstadoLiberacionSolicitada.PERIODO_RESERVA;
        }
    }

    /**
     * Crea una lista de Fracciones/Rangos en función de las peticiones de Liberación.
     * @param pLiberaciones Lista de solicitudes de liberación sobre un mismo rango.
     * @return lista de Fracciones/Rangos en función de las peticiones de Liberación
     * @throws Exception en caso de error.
     */
    private List<IRangoSerie> getFraccionesByLiberacionesSolicitadas(
            List<LiberacionSolicitadaNng> pLiberaciones) throws Exception {

        // Listado de Fracciones contenidas en las Liberaciones
        List<IRangoSerie> listaFracciones = new ArrayList<IRangoSerie>(pLiberaciones.size());

        EstadoRango estadoReservado = new EstadoRango();
        estadoReservado.setCodigo(EstadoRango.AFECTADO);

        // Recuperamos el Rango Original. Todas las liberaciones de la lista
        // son referentes al mismo rango.
        RangoSerieNng rangoOriginal = this.getRangoSerieByFraccion(
                pLiberaciones.get(0).getIdClaveServicio(),
                pLiberaciones.get(0).getSna(),
                pLiberaciones.get(0).getNumInicio(),
                pLiberaciones.get(0).getNumFinal(),
                pLiberaciones.get(0).getProveedorCesionario());

        // Creamos los rangos con la información de la Liberación Solicitada
        for (LiberacionSolicitadaNng libSol : pLiberaciones) {

            // Info de la PK
            RangoSerieNngPK rangoPk = new RangoSerieNngPK();
            rangoPk.setIdClaveServicio(libSol.getIdClaveServicio());
            rangoPk.setSna(libSol.getSna());

            // Info de Rango
            RangoSerieNng rango = new RangoSerieNng();
            rango.setId(rangoPk);
            rango.setEstatus(estadoReservado);
            rango.setAbc(rangoOriginal.getAbc());
            rango.setBcd(rangoOriginal.getBcd());
            rango.setNumInicio(libSol.getNumInicio());
            rango.setNumFinal(libSol.getNumFinal());
            rango.setNumeracionSolicitada(rangoOriginal.getNumeracionSolicitada());
            rango.setObservaciones(rangoOriginal.getObservaciones());

            // Debería ser el mismo que rangoOriginal.getAsignatario()
            rango.setAsignatario(libSol.getProveedorCesionario());

            rango.setConcesionario(rangoOriginal.getConcesionario());
            rango.setArrendatario(rangoOriginal.getArrendatario());
            rango.setRentado(rangoOriginal.getRentado());
            rango.setSerie(this.getSerie(libSol.getIdClaveServicio(), libSol.getSna()));
            rango.setSolicitud(libSol.getSolicitudLiberacion());
            rango.setCliente(libSol.getCliente());
            rango.setFechaAsignacion(rangoOriginal.getFechaAsignacion());
            rango.setConsecutivoAsignacion(rangoOriginal.getConsecutivoAsignacion());
            rango.setOficioAsignacion(rangoOriginal.getOficioAsignacion());

            listaFracciones.add(rango);
        }

        return listaFracciones;
    }

    @Override
    public String liberarFraccionesRango(List<LiberacionSolicitadaNng> pLiberaciones, boolean pInmediata)
            throws Exception {

        // Las liberaciones vienen agrupadas por el rango, por lo que todas tienen los
        // mismos valores a excepción del número inicial y número final. Para obtener
        // la información del rango original o fecha de implementación nos vale cualquiera
        // de la lista.
        LiberacionSolicitadaNng libSol = pLiberaciones.get(0);

        // StringBuilder sbTraza = new StringBuilder();
        // sbTraza.append("Liberando Rango NNG Fraccionado.");
        // sbTraza.append(", Solicitud: ").append(libSol.getSolicitudLiberacion().getId());
        // sbTraza.append(", Rango con Clave Serv.: ");
        // sbTraza.append(libSol.getIdClaveServicio());
        // sbTraza.append(", Sna: ").append(libSol.getSna());
        // sbTraza.append(", Fecha Implementación: ");
        // sbTraza.append(FechasUtils.fechaToString(libSol.getFechaLiberacion()));
        // sbTraza.append(" (ejecución planificador: ");
        // sbTraza.append(FechasUtils.fechaToString(
        // FechasUtils.getFechaImplementacionReal(libSol.getFechaLiberacion()), "dd/MM/yyyy HH:mm:ss"));
        // sbTraza.append("), Fecha Fin P.Reserva: ");
        // sbTraza.append(FechasUtils.fechaToString(libSol.getFechaFinReserva()));
        // sbTraza.append(" (ejecución planificador: ");
        // sbTraza.append(FechasUtils.fechaToString(
        // FechasUtils.getFechaImplementacionReal(libSol.getFechaFinReserva()), "dd/MM/yyyy HH:mm:ss"));
        // sbTraza.append("), Ejecución Inmediata: ").append(pInmediata);

        // Tenemos en cuenta los minutos.
        Date fHoy = new Date();
        boolean fFinReservaCumplida =
                (fHoy.compareTo(FechasUtils.getFechaImplementacionReal(libSol.getFechaFinReserva())) >= 0);

        boolean fImplementacionCumplida =
                (fHoy.compareTo(FechasUtils.getFechaImplementacionReal(libSol.getFechaLiberacion())) >= 0);

        // sbTraza.append(", Fecha Implementación Cumplida: ").append(fImplementacionCumplida);
        // sbTraza.append(", Fecha Fin Reserva Cumplida: ").append(fFinReservaCumplida);
        // LOGGER.info(sbTraza.toString());
        // bitacoraService.add(libSol.getSolicitudLiberacion(), sbTraza.toString());

        // Lista peticiones de fraccionamiento.
        List<IRangoSerie> fraccionesBySolicitud = this.getFraccionesByLiberacionesSolicitadas(pLiberaciones);

        // La primera fracción de la lista es la que tiene el número de Inicio del
        // rango que queremos fraccionar. Es necesario eliminar primer el Rango
        // para luego guardar las fracciones nuevas.
        RangoSerieNng rangoOriginal = this.getRangoSerieByFraccion(
                libSol.getIdClaveServicio(),
                libSol.getSna(),
                ((RangoSerieNng) fraccionesBySolicitud.get(0)).getNumInicio(),
                ((RangoSerieNng) fraccionesBySolicitud.get(0)).getNumFinal(),
                libSol.getProveedorCesionario());

        // Si no se ha cumplido la fecha de implementación no se hace nada.
        if ((!pInmediata) && (!fImplementacionCumplida)) {
            // Estado Reservado para Rangos con fecha de implementación no cumplida.
            EstadoRango statusReservado = new EstadoRango();
            statusReservado.setCodigo(EstadoRango.RESERVADO);

            rangoOriginal.setEstatus(statusReservado);
            rangoOriginal.setSolicitud(libSol.getSolicitudLiberacion());
            rangoOriginal.setFechaFinReserva(libSol.getFechaFinReserva());
            this.saveRangoSerie(rangoOriginal);

            // Trazas de Bitácora.
            bitacoraService.add(libSol.getSolicitudLiberacion(), "Rango reservado: " + rangoOriginal.toString());

            return EstadoLiberacionSolicitada.PENDIENTE;
        }

        if (fFinReservaCumplida) {
            // Cambiamos la solicitud del rango original para que cree los nuevos rangos asociados
            // a la nueva solicitud de liberación.
            rangoOriginal.setSolicitud(libSol.getSolicitudLiberacion());

            // Fracciones finales
            EstadoRango statusAsignado = this.getEstadoRangoByCodigo(EstadoRango.ASIGNADO);
            List<IRangoSerie> fraccionesIRango =
                    RangosSeriesUtil.getFracciones(fraccionesBySolicitud, rangoOriginal, statusAsignado);

            List<RangoSerieNng> fracciones = RangosSeriesUtil.castListaRangoSerieNNG(fraccionesIRango);

            // Eliminamos el rango original y guardamos las fracciones
            this.removeRangoSerie(rangoOriginal);
            bitacoraService.add(libSol.getSolicitudLiberacion(), "Rango liberado: " + rangoOriginal.toString());

            // Almacenamos las reservas y liberaciones
            for (RangoSerieNng fraccion : fracciones) {
                if (fraccion.getEstatus().getCodigo().equals(EstadoRango.AFECTADO)) {
                    // Los rangos afectados son los que se quieren liberar. Se ignoran.
                    bitacoraService.add(libSol.getSolicitudLiberacion(), "Rango eliminado: " + fraccion.toString());
                } else {
                    // Al terminar la liberación los rangos quedan en estado Asignado
                    this.saveRangoSerie(fraccion);
                    bitacoraService.add(libSol.getSolicitudLiberacion(), "Fracción creada: " + fraccion.toString());
                }
            }

            return EstadoLiberacionSolicitada.LIBERADO;
        } else {
            // Estatus Perido de Reserva.
            EstadoRango estatusReserva = new EstadoRango();
            estatusReserva.setCodigo(EstadoRango.PERIODO_RESERVA);
            rangoOriginal.setEstatus(estatusReserva);
            rangoOriginal.setSolicitud(libSol.getSolicitudLiberacion());
            rangoOriginal.setFechaFinReserva(libSol.getFechaFinReserva());
            this.saveRangoSerie(rangoOriginal);

            bitacoraService.add(libSol.getSolicitudLiberacion(),
                    "Rango en periodo de reserva: " + rangoOriginal.toString());

            return EstadoLiberacionSolicitada.PERIODO_RESERVA;
        }
    }

    @Override
    public SerieNng getSerieById(SerieNngPK id) {
        return serieNngDao.getSerie(id.getIdClaveServicio(), id.getSna());
    }

    @Override
    public List<RangoSerieNng> validateRango(BigDecimal clave, BigDecimal sna,
            String numeroInicial, String numeroFinal) {
        return rangoSerieNngDao.validateRango(clave, sna, numeroInicial, numeroFinal);
    }

    @Override
    public RangoSerieNng getRangoSeriePertenece(BigDecimal pClaveServicio, BigDecimal pSna, String pNumeracion) {
        return rangoSerieNngDao.getRangoSeriePertenece(pClaveServicio, pSna, pNumeracion);
    }

    @Override
    public void redistribuirRango(RangoSerieNng pRango, RedistribucionSolicitadaNng pRedistSol) {

        // Si algún campo de RedistribucionSolicitadaNng está a nulo es que se quiere eliminar
        // ese valor o directamente no existía.

        StringBuffer sbUpdate = new StringBuffer();
        sbUpdate.append("Redistribución idRango: ").append(pRango.getId().getId()).append(". ");

        sbUpdate.append("Cliente: ").append(pRango.getCliente());
        sbUpdate.append(" -> ").append(pRedistSol.getCliente()).append("; ");
        pRango.setCliente(pRedistSol.getCliente());

        if (pRango.getArrendatario() != null) {
            sbUpdate.append("Arrendatario: ").append(pRango.getArrendatario().getNombreCorto());
        } else {
            sbUpdate.append("Arrendatario: null");
        }
        if (pRedistSol.getProveedorArrendatario() != null) {
            sbUpdate.append(" -> ").append(pRedistSol.getProveedorArrendatario().getNombreCorto()).append("; ");
        } else {
            sbUpdate.append(" -> null; ");
        }
        pRango.setArrendatario(pRedistSol.getProveedorArrendatario());

        if (pRango.getConcesionario() != null) {
            sbUpdate.append("Concesionario: ").append(pRango.getConcesionario().getNombreCorto());
        } else {
            sbUpdate.append("Concesionario: null");
        }
        if (pRedistSol.getProveedorConcesionario() != null) {
            sbUpdate.append(" -> ").append(pRedistSol.getProveedorConcesionario().getNombreCorto()).append("; ");
        } else {
            sbUpdate.append(" -> null; ");
        }
        pRango.setConcesionario(pRedistSol.getProveedorConcesionario());

        sbUpdate.append("ABC: ").append(pRango.getAbcAsString()).append(" -> ");
        sbUpdate.append(pRedistSol.getAbc()).append("; ");
        pRango.setAbc(pRedistSol.getAbc());

        // Guardamos la información actualizada
        sbUpdate.append("Solicitud: ").append(pRango.getSolicitud().getId());
        sbUpdate.append(" -> ").append(pRedistSol.getSolicitudRedistribucion().getId()).append("; ");
        pRango.setSolicitud(pRedistSol.getSolicitudRedistribucion());

        // Cambiamos el estado a Asignado
        EstadoRango estadoAsignado = new EstadoRango();
        estadoAsignado.setCodigo(EstadoRango.ASIGNADO);
        pRango.setEstatus(estadoAsignado);
        this.saveRangoSerie(pRango);

        // Trazas de Bitácora.
        bitacoraService.add(pRedistSol.getSolicitudRedistribucion(), sbUpdate.toString());
    }

    @Override
    public void redistribuirFraccionesRango(List<RedistribucionSolicitadaNng> pRedistribuciones) throws Exception {

        // Status Afectado de Rango
        EstadoRango statusAfectado = new EstadoRango();
        statusAfectado.setCodigo(EstadoRango.AFECTADO);

        // Las Fracciones vienen agrupadas por el rango, por lo que todas tienen los
        // mismos valores a excepción del número inicial y número final. Para obtener
        // la información del rango original nos vale cualquiera
        // de la lista.
        RedistribucionSolicitadaNng redSol = pRedistribuciones.get(0);
        RangoSerieNng rangoOriginal = this.getRangoSerieByFraccion(
                redSol.getIdClaveServicio(),
                redSol.getSna(),
                redSol.getNumInicio(),
                redSol.getNumFinal(),
                redSol.getProveedorSolicitante());

        // Fracciones Afectadas
        List<IRangoSerie> fraccionesBySolicitud = new ArrayList<IRangoSerie>(pRedistribuciones.size());
        for (RedistribucionSolicitadaNng redSolFraccion : pRedistribuciones) {
            RangoSerieNng rangoCloned = (RangoSerieNng) rangoOriginal.clone();
            rangoCloned.setSolicitud(redSolFraccion.getSolicitudRedistribucion());
            rangoCloned.setNumInicio(redSolFraccion.getNumInicio());
            rangoCloned.setNumFinal(redSolFraccion.getNumFinal());
            rangoCloned.setEstatus(statusAfectado);
            fraccionesBySolicitud.add(rangoCloned);
        }

        // Lista de fracciones derivadas del rango original
        EstadoRango statusAsignado = this.getEstadoRangoByCodigo(EstadoRango.ASIGNADO);
        List<IRangoSerie> fraccionesIRango =
                RangosSeriesUtil.getFracciones(fraccionesBySolicitud, rangoOriginal, statusAsignado);

        List<RangoSerieNng> fracciones = RangosSeriesUtil.castListaRangoSerieNNG(fraccionesIRango);

        // Eliminamos el rango original y guardamos las fracciones
        this.removeRangoSerie(rangoOriginal);

        // Guardamos los cambios
        for (RangoSerieNng fraccion : fracciones) {
            if (fraccion.getEstatus().getCodigo().equals(EstadoRango.AFECTADO)) {
                // Los rangos afectados son los que se quieren redistribuir
                this.redistribuirRango(fraccion, redSol);
            } else {
                // El resto de rangos son las fracciones que quedan en la numeración original
                fraccion.setSolicitud(redSol.getSolicitudRedistribucion());
                this.saveRangoSerie(fraccion);
                bitacoraService.add(redSol.getSolicitudRedistribucion(), "Fracción creada: " + fraccion.toString());
            }
        }
    }

    @Override
    public RangoSerieNng getRangoSerieOriginalByFraccion(RangoSerieNng pFraccion, BigDecimal pIdSolicitud,
            Proveedor pAsignatarioInicial) throws Exception {

        // Cuando se realiza una fracción, todos los rangos que se generan llevan el identificador de la solicitud
        // que está modificando la numeración.
        FiltroBusquedaRangos fbr = new FiltroBusquedaRangos();
        fbr.setIdSolicitud(pIdSolicitud);
        fbr.setIdSna(pFraccion.getSerie().getId().getSna());
        fbr.setClaveServicio(pFraccion.getClaveServicio().getCodigo());
        fbr.setAsignatario(pAsignatarioInicial);

        // Lista de posibles rangos adyacentes de la fracción seleccionada
        List<RangoSerieNng> rangosAdyacentes = this.findAllRangos(fbr);

        String numInicio = null;
        String numFinal = null;
        RangoSerieNng rangoInfo = null;
        for (RangoSerieNng rango : rangosAdyacentes) {
            if ((rango.getNumFinalAsInt() + 1) == pFraccion.getNumInicioAsInt()) {
                // Rango anterior
                numInicio = rango.getNumInicio();
                rangoInfo = rango;
            }

            if ((rango.getNumInicioAsInt() - 1) == pFraccion.getNumFinalAsInt()) {
                // Rango posterior
                numFinal = rango.getNumFinal();
                rangoInfo = rango;
            }
        }

        if (rangoInfo != null) {
            // Información de Asignatario, etc.
            RangoSerieNng rangoOriginal = (RangoSerieNng) rangoInfo.clone();

            // Se necesita la solicitud para los métodos de cancelación.
            rangoOriginal.setSolicitud(rangoInfo.getSolicitud());

            // Número inicial de la numeración.
            if (numInicio != null) {
                rangoOriginal.setNumInicio(numInicio);
            } else {
                rangoOriginal.setNumInicio(pFraccion.getNumInicio());
            }

            // Número final de la numeración
            if (numFinal != null) {
                rangoOriginal.setNumFinal(numFinal);
            } else {
                rangoOriginal.setNumFinal(pFraccion.getNumFinal());
            }

            return rangoOriginal;
        } else {
            return null;
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<HistoricoRangoSerieNng> findAllHistoricActionsFromRangos(FiltroBusquedaHistoricoRangos pFiltros) {
        return histRangosNngDao.findAllHistoricActions(pFiltros);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public int findAllHistoricActionsFromRangosCount(FiltroBusquedaHistoricoRangos pFiltros) {
        return histRangosNngDao.findAllHistoricActionsCount(pFiltros);
    }

    @Override
    public Integer getNumeracionAsignadaSerie(ClaveServicio claveServicio, Proveedor proveedor) {
        return rangoSerieNngDao.getNumeracionAsignadaSerie(claveServicio.getCodigo().toString(), proveedor);
    }

    @Override
    public Integer getNumeracionAsignadaRango(ClaveServicio claveServicio, Proveedor proveedor) {
        return rangoSerieNngDao.getNumeracionAsignadaRango(claveServicio.getCodigo().toString(), proveedor);
    }

    @Override
    public Integer getNumeracionAsignadaEspecifica(ClaveServicio claveServicio, Proveedor proveedor) {
        return rangoSerieNngDao.getNumeracionAsignadaEspecifica(claveServicio.getCodigo().toString(), proveedor);
    }

    @Override
    public List<HistoricoSerieNng> findAllHistoricoSeries(FiltroBusquedaHistoricoSeries pFiltros) {
        return histSerieNngDao.findAllHistoricoSeries(pFiltros);
    }

    @Override
    public int findAllHistoricoSeriesCount(FiltroBusquedaHistoricoSeries pFiltros) {
        return histSerieNngDao.findAllHistoricoSeriesCount(pFiltros);
    }

    @Override
    public Integer getTotalNumeracionAsignadaBySolicitud(BigDecimal id) {
        return rangoSerieNngDao.getTotalNumeracionAsignadaBySolicitud(id);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public boolean existeNumeracionAsignadaAlPst(Proveedor proveedor) {
        return rangoSerieNngDao.existeNumeracionAsignadaAlPst(proveedor);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public int findAllRangosAsignadosEspecificoCount() {
        return rangoSerieNngDao.findAllRangosAsignadosEspecificoCount();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<RangoSerieNng> findAllRangosAsignadosEspecifico(int numPag, int tamPag) {
        return rangoSerieNngDao.findAllRangosAsignadosEspecifico(numPag, tamPag);

    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<DetalleRangoAsignadoNng> findAllRangosAsignadosEspecificoD(int numPag, int tamPag) {
        return rangoSerieNngDao.findAllRangosAsignadosEspecificoD(numPag, tamPag);

    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public int findAllRangosAsignadosCount() {
        return rangoSerieNngDao.findAllRangosAsignadosCount();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<RangoSerieNng> findAllRangosAsignados(int numPag, int tamPag) {
        return rangoSerieNngDao.findAllRangosAsignados(numPag, tamPag);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<DetalleRangoAsignadoNng> findAllRangosAsignadosD(int numPag, int tamPag) {
        return rangoSerieNngDao.findAllRangosAsignadosD(numPag, tamPag);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<DetalleRangoAsignadoNng> findAllRangosAsignadosD(BigDecimal clave, int numPag, int tamPag) {
        return rangoSerieNngDao.findAllRangosAsignadosD(clave, numPag, tamPag);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<ClaveServicio> findAllClaveServicioAsignadas() {
        return rangoSerieNngDao.findAllClaveServicioAsignadas();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public int findAllRangosAsignadosByClaveCount(ClaveServicio clave) {
        return rangoSerieNngDao.findAllRangosAsignadosByClaveCount(clave);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<RangoSerieNng> findAllRangosByClaveAsignados(ClaveServicio clave, int numPag, int tamPag) {
        return rangoSerieNngDao.findAllRangosByClaveAsignados(clave, numPag, tamPag);
    }

    @Override
    public byte[] getExportHistoricoSeries(FiltroBusquedaHistoricoSeries filtro) throws Exception {
        filtro.setUsarPaginacion(false);

        List<HistoricoSerieNng> listahistorico = histSerieNngDao.findAllHistoricoSeries(filtro);

        ExportHistoricoSerieNng exhs = new ExportHistoricoSerieNng(listahistorico);
        ExportarExcel export = new ExportarExcel(parametroDao);
        if (export.getTamMaxExportacion() >= listahistorico.size()) {
            return export.generarReporteExcel("Historico", exhs);
        } else {
            return export.generarReporteExcelHistoricoSeriesNng("Historico", exhs, listahistorico);
        }
    }

}
