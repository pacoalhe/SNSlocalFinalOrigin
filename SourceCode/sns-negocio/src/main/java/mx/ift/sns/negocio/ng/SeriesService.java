package mx.ift.sns.negocio.ng;

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
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import mx.ift.sns.dao.ng.IEstadoRangoDao;
import mx.ift.sns.dao.ng.IHistoricoSerieDAO;
import mx.ift.sns.dao.ng.INirDao;
import mx.ift.sns.dao.ng.INumeracionAsignadaDAO;
import mx.ift.sns.dao.ng.IParametroDao;
import mx.ift.sns.dao.ng.IRangoSerieDao;
import mx.ift.sns.dao.ng.ISerieArrendadaDAO;
import mx.ift.sns.dao.ng.ISerieDao;
import mx.ift.sns.dao.nng.IClaveServicioDao;
import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.central.Estatus;
import mx.ift.sns.modelo.filtros.FiltroBusquedaHistoricoSeries;
import mx.ift.sns.modelo.filtros.FiltroBusquedaRangos;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSeries;
import mx.ift.sns.modelo.filtros.FiltroReporteadorNG;
import mx.ift.sns.modelo.ng.Cesion;
import mx.ift.sns.modelo.ng.CesionSolicitadaNg;
import mx.ift.sns.modelo.ng.DetalleRangoAsignadoNg;
import mx.ift.sns.modelo.ng.DetalleReporteAbd;
import mx.ift.sns.modelo.ng.Liberacion;
import mx.ift.sns.modelo.ng.LiberacionSolicitadaNg;
import mx.ift.sns.modelo.ng.NumeracionRedistribuida;
import mx.ift.sns.modelo.ng.NumeracionSolicitada;
import mx.ift.sns.modelo.ng.RangoSerie;
import mx.ift.sns.modelo.ng.RangoSeriePK;
import mx.ift.sns.modelo.ng.RedistribucionSolicitadaNg;
import mx.ift.sns.modelo.ng.Serie;
import mx.ift.sns.modelo.ng.SeriePK;
import mx.ift.sns.modelo.nng.ClaveServicio;
import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.EstadoNumeracion;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.NirNumeracion;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.ot.PoblacionNumeracion;
import mx.ift.sns.modelo.pnn.DetallePlanAbdPresuscripcion;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.pst.ProveedorConvenio;
import mx.ift.sns.modelo.pst.TipoProveedor;
import mx.ift.sns.modelo.reporteabd.SerieArrendada;
import mx.ift.sns.modelo.reporteabd.SerieArrendadaPadre;
import mx.ift.sns.modelo.reporteador.ElementoAgrupador;
import mx.ift.sns.modelo.reporteador.NGReporteador;
import mx.ift.sns.modelo.series.EstadoRango;
import mx.ift.sns.modelo.series.HistoricoSerie;
import mx.ift.sns.modelo.series.IRangoSerie;
import mx.ift.sns.modelo.series.Nir;
import mx.ift.sns.modelo.series.VCatalogoSerie;
import mx.ift.sns.modelo.solicitud.Solicitud;
import mx.ift.sns.negocio.IBitacoraService;
import mx.ift.sns.negocio.ac.ExportConsultaCatalogoSerie;
import mx.ift.sns.negocio.num.model.Numero;
import mx.ift.sns.negocio.psts.IProveedoresService;
import mx.ift.sns.negocio.usu.IUsuariosService;
import mx.ift.sns.negocio.utils.excel.ExportarExcel;
import mx.ift.sns.utils.date.FechasUtils;
import mx.ift.sns.utils.series.RangosSeriesUtil;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generador de plan de numeracion de ABD Presuscripcion.
 */
@Stateless
@Remote(ISeriesService.class)
public class SeriesService implements ISeriesService {

    /** Logger de la clase . */
    private static final Logger LOGGER = LoggerFactory.getLogger(SeriesService.class);

    /** Longitud de la numeracion nacional. */
    public static final int LENGTH_NUM = 10;

    /** Longitud de la numeracion local. */
    public static final int MAX_LENGTH_NUM_LOCAL = 8;

    /** Longitud de la numeracion local. */
    public static final int MIN_LENGTH_NUM_LOCAL = 7;

    /** Servicio de Proveedores. */
    @EJB
    private IProveedoresService pstService;

    /** Servicio de usuarios. */
    @EJB(mappedName = "UsuariosService")
    private IUsuariosService usuariosService;

    /** DAO Rangos Serie. */
    @Inject
    private IRangoSerieDao rangoSerieDAO;

    /** DAO de parametros. */
    @Inject
    private IParametroDao parametroDao;

    /** DAO de Series Numéricas. */
    @Inject
    private ISerieDao serieDAO;

    /** DAO de Estado Rango. */
    @Inject
    private IEstadoRangoDao estadoRangoDao;

    /** DAO de numeracion asignada. */
    @Inject
    private INumeracionAsignadaDAO numeracionAsignadaDAO;

    /** DAO Nir. */
    @Inject
    private INirDao nirDAO;

    /** DAO Clave Servicio. */
    @Inject
    private IClaveServicioDao claveServicioDao;

    /** DAO de numeracion asignada. */
    @Inject
    private ISerieArrendadaDAO serieArrendadaDAO;

    /** DAO de historico de series ng. */
    @Inject
    private IHistoricoSerieDAO historicoSerieDAO;

    /** Servicio de Bitácora. */
    @EJB
    private IBitacoraService bitacoraService;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<Serie> findSeriesPst(Proveedor pst, Abn abn) {
        return serieDAO.findSeriesPst(pst, abn);
    }

    @Override
    public Serie getSerie(BigDecimal pIdSna, BigDecimal pIdNir) throws Exception {
        return serieDAO.getSerie(pIdSna, pIdNir);
    }

    @Override
    public int getMaxMinSerieFromNir(int pCdgNir, boolean pMaxValue) {
        return serieDAO.getMaxMinSerieFromNir(pCdgNir, pMaxValue);
    }

    @Override
    public boolean existeRangoSerie(BigDecimal pIdNir, BigDecimal pSna,
            String pNumInicial, Proveedor pAsignatario) {
        return rangoSerieDAO.existeRangoSerie(pIdNir, pSna, pNumInicial, pAsignatario);
    }

    @Override
    public RangoSerie getRangoSerie(BigDecimal pIdNir, BigDecimal pSna,
            String pNumInicial, Proveedor pAsignatario) throws Exception {
        return rangoSerieDAO.getRangoSerie(pIdNir, pSna, pNumInicial, pAsignatario);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void removeRangoSerie(RangoSerie pRangoSerie) throws Exception {
        rangoSerieDAO.delete(pRangoSerie);
    }

    @Override
    public RangoSerie getRangoSerieByFraccion(BigDecimal pIdNir, BigDecimal pSna,
            String pNumInicial, String pNumFinal, Proveedor pAsignatario) throws Exception {
        return rangoSerieDAO.getRangoSerieByFraccion(pIdNir, pSna, pNumInicial, pNumFinal, pAsignatario);
    }

    @Override
    public List<Serie> findSeriesOtrosPsts(Proveedor pst, Abn abn) throws Exception {
        return serieDAO.findSeriesOtrosPsts(pst, abn);
    }

    @Override
    public List<Serie> findSeriesLibres(Abn abn) throws Exception {
        return serieDAO.findSeriesLibres(abn);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<Serie> findAllSeries(FiltroBusquedaSeries pFiltros) throws Exception {
        return serieDAO.findAllSeries(pFiltros);
    }

    @Override
    public List<VCatalogoSerie> findAllCatalogoSeries(FiltroBusquedaSeries pFiltros) throws Exception {
        return serieDAO.findAllCatalogoSeries(pFiltros);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public int findAllSeriesRangosCount(FiltroBusquedaSeries pFiltros) throws Exception {
        return serieDAO.findAllSeriesCount(pFiltros);
    }

    @Override
    public int findAllCatalogoSeriesCount(FiltroBusquedaSeries pFiltros) throws Exception {
        return serieDAO.findAllCatalogoSeriesCount(pFiltros);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<RangoSerie> findAllRangos(FiltroBusquedaRangos pFiltros) throws Exception {
        return rangoSerieDAO.findAllRangos(pFiltros);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public int findAllRangosCount(FiltroBusquedaRangos pFiltros) throws Exception {
        return rangoSerieDAO.findAllRangosCount(pFiltros);
    }

    @Override
    public List<Liberacion> liberarRangoCompleto(LiberacionSolicitadaNg pLibSol, boolean pInmediata)
            throws Exception {

        // Lista de liberaciones generadas
        List<Liberacion> liberaciones = new ArrayList<Liberacion>();

        // Tenemos en cuenta los minutos.
        Date fHoy = new Date();
        boolean fImplementacionCumplida =
                (fHoy.compareTo(FechasUtils.getFechaImplementacionReal(pLibSol.getFechaLiberacion())) >= 0);

        RangoSerie rango = this.getRangoSerie(
                pLibSol.getIdNir(),
                pLibSol.getSna(),
                pLibSol.getNumInicio(),
                pLibSol.getProveedorCesionario());

        // Si no se ha cumplido la fecha de implementación no se hace nada.
        if ((!pInmediata) && (!fImplementacionCumplida)) {
            // Estado Reservado para Rangos con fecha de implementación no cumplida.
            EstadoRango statusReservado = new EstadoRango();
            statusReservado.setCodigo(EstadoRango.RESERVADO);

            rango.setEstadoRango(statusReservado);
            rango.setSolicitud(pLibSol.getSolicitudLiberacion());
            this.saveRangoSerie(rango);
            bitacoraService.add(pLibSol.getSolicitudLiberacion(), "Rango reservado: " + rango.toString());

            // Devolvemos la lista en blanco.
            return liberaciones;
        }

        // Creamos una nueva Liberación con la información de la solicitud
        Liberacion liberacion = new Liberacion();
        liberacion.setInicioRango(pLibSol.getNumInicio());
        liberacion.setFinRango(pLibSol.getNumFinal());
        liberacion.setSolicitudLiberacion(pLibSol.getSolicitudLiberacion());
        liberacion.setLiberacionSolicitada(pLibSol);
        liberaciones.add(liberacion);

        // Eliminamos el Rango original.
        this.removeRangoSerie(rango);
        bitacoraService.add(pLibSol.getSolicitudLiberacion(), "Rango liberado: " + rango.toString());

        return liberaciones;
    }

    @Override
    public List<Cesion> cederRangoCompleto(CesionSolicitadaNg pCesionSolicitada, boolean pInmediata) throws Exception {

        // Tenemos en cuenta los minutos.
        Date fHoy = new Date();
        boolean fImplementacionCumplida =
                (fHoy.compareTo(FechasUtils.getFechaImplementacionReal(pCesionSolicitada.getFechaCesion())) >= 0);

        // Lista de cesiones generadas
        List<Cesion> cesiones = new ArrayList<Cesion>();

        // Rango asignado al proveedor cedente que se quiere ceder
        RangoSerie rango = this.getRangoSerie(
                pCesionSolicitada.getIdNir(),
                pCesionSolicitada.getSna(),
                pCesionSolicitada.getNumInicio(),
                pCesionSolicitada.getProveedorCedente());

        // Si no se ha cumplido la fecha de implementación no se hace nada.
        if ((!pInmediata) && (!fImplementacionCumplida)) {
            // Estado Reservado para Rangos con fecha de implementación no cumplida.
            EstadoRango statusReservado = new EstadoRango();
            statusReservado.setCodigo(EstadoRango.RESERVADO);

            rango.setEstadoRango(statusReservado);
            rango.setSolicitud(pCesionSolicitada.getSolicitudCesion());
            this.saveRangoSerie(rango);
            bitacoraService.add(pCesionSolicitada.getSolicitudCesion(), "Rango reservado: " + rango.toString());

            // Devolvemos la lista en blanco.
            return cesiones;
        }

        // Cedemos el rango teniendo en cuenta la naturaleza del proveedor
        Cesion cesion = cederRango(rango, pCesionSolicitada);
        cesiones.add(cesion);

        return cesiones;
    }

    @Override
    public List<Cesion> cederFraccionesRango(RangoSerie rangoOriginal, List<CesionSolicitadaNg> pCesionesSolicitadas,
            boolean pInmediata)
            throws Exception {

        // Lista de cesiones generadas
        List<Cesion> cesiones = new ArrayList<Cesion>();

        // Las cesiones vienen agrupadas por el rango, por lo que todas tienen los
        // mismos valores a excepción del número inicial y número final. Para obtener
        // la información de la fecha de implementación nos vale cualquiera
        // de la lista.
        CesionSolicitadaNg cesSol = pCesionesSolicitadas.get(0);

        // Tenemos en cuenta los minutos.
        Date fHoy = new Date();
        boolean fImplementacionCumplida =
                (fHoy.compareTo(FechasUtils.getFechaImplementacionReal(cesSol.getFechaCesion())) >= 0);

        // Lista peticiones de fraccionamiento.
        List<IRangoSerie> fraccionesBySolicitud = this.getFraccionesByCesionesSolicitadas(pCesionesSolicitadas);

        // La primera fracción de la lista es la que tiene el número de Inicio del
        // rango que queremos fraccionar. Es necesario eliminar primer el Rango
        // para luego guardar las fracciones nuevas.
        // RangoSerie rangoOriginal = this.getRangoSerieByFraccion(
        // cesSol.getIdNir(),
        // cesSol.getSna(),
        // ((RangoSerie) fraccionesBySolicitud.get(0)).getNumInicio(),
        // ((RangoSerie) fraccionesBySolicitud.get(0)).getNumFinal(),
        // cesSol.getProveedorCedente());

        // Si no se ha cumplido la fecha de implementación no se hace nada.
        if ((!pInmediata) && (!fImplementacionCumplida)) {
            // Estado Reservado para Rangos con fecha de implementación no cumplida.
            EstadoRango statusReservado = new EstadoRango();
            statusReservado.setCodigo(EstadoRango.RESERVADO);

            rangoOriginal.setEstadoRango(statusReservado);
            rangoOriginal.setSolicitud(cesSol.getSolicitudCesion());
            this.saveRangoSerie(rangoOriginal);
            bitacoraService.add(cesSol.getSolicitudCesion(), "Rango reservado: " + cesSol.toString());

            // Devolvemos la lista en blanco.
            return cesiones;
        }

        // Cambiamos la solicitud del rango original para que cree los nuevos rangos asociados
        // a la nueva solicitud de cesión.
        rangoOriginal.setSolicitud(cesSol.getSolicitudCesion());

        // Fracciones finales
        EstadoRango statusAsignado = this.getEstadoRangoByCodigo(EstadoRango.ASIGNADO);
        List<IRangoSerie> fraccionesIRango =
                RangosSeriesUtil.getFracciones(fraccionesBySolicitud, rangoOriginal, statusAsignado);

        List<RangoSerie> fracciones = RangosSeriesUtil.castListaRangoSerieNG(fraccionesIRango);

        // Eliminamos el rango original y guardamos las fracciones
        this.removeRangoSerie(rangoOriginal);
        bitacoraService.add(cesSol.getSolicitudCesion(), "Rango eliminado: " + rangoOriginal.toString());

        // Almacenamos las reservas y liberaciones
        for (RangoSerie fraccion : fracciones) {
            if (fraccion.getEstadoRango().getCodigo().equals(EstadoRango.AFECTADO)) {
                // Los rangos afectados son los que se quieren ceder.
                // Cedemos el rango teniendo en cuenta la naturaleza del proveedor
                Cesion cesion = cederRango(fraccion, cesSol);
                cesiones.add(cesion);
            } else {
                // Los rangos no afectados han de mantener la info del rango original
                this.saveRangoSerie(fraccion);
                bitacoraService.add(cesSol.getSolicitudCesion(), "Rango actualizado: " + rangoOriginal.toString());
            }
        }

        return cesiones;
    }

    /**
     * Aplica una cesión sobre un rango o fracción con la información de la cesión solicitada.
     * @param pRango Rango o Fracción a ceder.
     * @param pCesionSolicitada Información de la solicitud.
     * @return Objeto Cesion con la informaciónd de la cesión aplicada.
     * @throws Exception en caso de error.
     */
    private Cesion cederRango(RangoSerie pRango, CesionSolicitadaNg pCesionSolicitada) throws Exception {

        Proveedor pst = pCesionSolicitada.getProveedorCesionario();

        // Diferenciamos entre Proveedor Cesionario Concesionario o Comercialziadora
        if (pst.getTipoProveedor().getCdg().equals(TipoProveedor.COMERCIALIZADORA)) {

            if (pCesionSolicitada.getProveedorCesionario() != null) {
                // Cesión Comercializadora - Comercializadora
                pRango.setIdaPnn(pCesionSolicitada.getProveedorCesionario().getIda());
                if (pCesionSolicitada.getConvenioCesionario().getProveedorConcesionario() != null) {
                    pRango.setIdoPnn(pCesionSolicitada.getConvenioCesionario().getProveedorConcesionario().getIdo());
                }
            }

        } else if (pst.getTipoProveedor().getCdg().equals(TipoProveedor.CONCESIONARIO)) {

            if (pCesionSolicitada.getProveedorCesionario() != null) {
                // Cesión Comercializadora - Concesionario
                // Ido e Ida de Rango igual al Ido del Proveedor Cesionario
                pRango.setIdaPnn(pCesionSolicitada.getProveedorCesionario().getIdo());
                pRango.setIdoPnn(pCesionSolicitada.getProveedorCesionario().getIdo());
            }

        } else {
            if (pCesionSolicitada.getProveedorCesionario() != null) {
                // Cesión Comercializadora - Ambos
                pRango.setIdaPnn(pCesionSolicitada.getProveedorCesionario().getIdo());
            }

            if (pCesionSolicitada.getUsarIdoCesionario() != null) {
                // Se puede utilizar el IDO Propio
                if (pCesionSolicitada.getUsarIdoCesionario().equals("S")) {
                    if (pCesionSolicitada.getProveedorCesionario() != null) {
                        pRango.setIdoPnn(pCesionSolicitada.getProveedorCesionario().getIdo());
                    }
                }
            }

            if (pCesionSolicitada.getUsarConvenioConcesionario() != null) {
                // Se puede utilizar el IDO de un Concesionario con el que se tenga convenio
                if (pCesionSolicitada.getUsarConvenioConcesionario().equals("S")) {
                    if (pCesionSolicitada.getConvenioCesionario() != null
                            && pCesionSolicitada.getConvenioCesionario().getProveedorConcesionario() != null) {
                        pRango.setIdoPnn(
                                pCesionSolicitada.getConvenioCesionario().getProveedorConcesionario().getIdo());
                    }
                }
            }

        }

        // Cambiamos el estado a Asignado
        EstadoRango estadoAsignado = new EstadoRango();
        estadoAsignado.setCodigo(EstadoRango.ASIGNADO);
        pRango.setEstadoRango(estadoAsignado);

        // El rango tendrá el Identificador de la solicitud que lo cedió
        pRango.setSolicitud(pCesionSolicitada.getSolicitudCesion());

        // Cambiamos el Asignatario del Rango por el Id del Cesionario
        // El Cesionario es el que va a utilizar el rango a partir de ahora.
        pRango.setAsignatario(pCesionSolicitada.getProveedorCesionario());

        // En las cesiones no existe arrendamiento.
        pRango.setArrendatario(null);

        // Guardamos los cambios del Rango cedido
        this.saveRangoSerie(pRango);

        StringBuffer traza = new StringBuffer();
        traza.append("Rango Cedido de: ").append(pCesionSolicitada.getProveedorCedente().getNombreCorto());
        traza.append(" a ").append(pCesionSolicitada.getProveedorCesionario().getNombreCorto());

        bitacoraService.add(pCesionSolicitada.getSolicitudCesion(), traza.toString());

        // Cesión creada
        Cesion cesion = new Cesion();
        cesion.setCesionSolicitada(pCesionSolicitada);
        cesion.setInicioRango(pCesionSolicitada.getNumInicio());
        cesion.setFinRango(pCesionSolicitada.getNumFinal());
        cesion.setSolicitudCesion(pCesionSolicitada.getSolicitudCesion());

        return cesion;
    }

    /**
     * Crea una lista de Fracciones/Rangos en función de las peticiones de Cesión.
     * @param pCesiones Lista de cesiones solicitudas sobre un mismo rango.
     * @return lista de Fracciones/Rangos en función de las peticiones de cesión
     * @throws Exception en caso de error.
     */
    private List<IRangoSerie> getFraccionesByCesionesSolicitadas(List<CesionSolicitadaNg> pCesiones) throws Exception {

        // Listado de Fracciones contenidas en las Cesiones
        List<IRangoSerie> listaFracciones = new ArrayList<IRangoSerie>(pCesiones.size());

        EstadoRango estadoReservado = new EstadoRango();
        estadoReservado.setCodigo(EstadoRango.AFECTADO);

        // Recuperamos el Rango Original. Todas las cesiones de la lista
        // son referentes al mismo rango.
        RangoSerie rangoOriginal = this.getRangoSerieByFraccion(
                pCesiones.get(0).getIdNir(),
                pCesiones.get(0).getSna(),
                pCesiones.get(0).getNumInicio(),
                pCesiones.get(0).getNumFinal(),
                pCesiones.get(0).getProveedorCedente());

        // Creamos los rangos con la información de la Cesión Solicitada
        for (CesionSolicitadaNg cesSol : pCesiones) {

            // Info de la PK
            RangoSeriePK rangoPk = new RangoSeriePK();
            rangoPk.setIdNir(cesSol.getIdNir());
            rangoPk.setSna(cesSol.getSna());

            // Info de Rango
            RangoSerie rango = new RangoSerie();
            rango.setId(rangoPk);
            rango.setCentralDestino(cesSol.getCentralDestino());
            rango.setCentralOrigen(cesSol.getCentralOrigen());
            rango.setEstadoRango(estadoReservado);
            rango.setIdaPnn(rangoOriginal.getIdaPnn());
            rango.setIdoPnn(rangoOriginal.getIdoPnn());
            rango.setNumInicio(cesSol.getNumInicio());
            rango.setNumFinal(cesSol.getNumFinal());
            rango.setNumSolicitada(rangoOriginal.getNumSolicitada());
            rango.setObservaciones(rangoOriginal.getObservaciones());
            rango.setPoblacion(cesSol.getPoblacion());
            rango.setAsignatario(rangoOriginal.getAsignatario());
            rango.setConcesionario(rangoOriginal.getConcesionario());
            rango.setArrendatario(rangoOriginal.getArrendatario());
            rango.setRentado(rangoOriginal.getRentado());
            rango.setSerie(this.getSerie(cesSol.getSna(), cesSol.getIdNir()));
            rango.setSolicitud(cesSol.getSolicitudCesion());
            rango.setTipoModalidad(cesSol.getTipoModalidad());
            rango.setTipoRed(cesSol.getTipoRed());
            rango.setFechaAsignacion(rangoOriginal.getFechaAsignacion());
            rango.setConsecutivoAsignacion(rangoOriginal.getConsecutivoAsignacion());
            rango.setOficioAsignacion(rangoOriginal.getOficioAsignacion());

            listaFracciones.add(rango);
        }

        return listaFracciones;
    }

    /**
     * Crea una lista de Fracciones/Rangos en función de las peticiones de Liberación.
     * @param pLiberaciones Lista de solicitudes de liberación sobre un mismo rango.
     * @return lista de Fracciones/Rangos en función de las peticiones de Liberación
     * @throws Exception en caso de error.
     */
    private List<IRangoSerie> getFraccionesByLiberacionesSolicitadas(
            List<LiberacionSolicitadaNg> pLiberaciones) throws Exception {

        // Listado de Fracciones contenidas en las Liberaciones
        List<IRangoSerie> listaFracciones = new ArrayList<IRangoSerie>(pLiberaciones.size());

        EstadoRango estadoReservado = new EstadoRango();
        estadoReservado.setCodigo(EstadoRango.AFECTADO);

        // Recuperamos el Rango Original. Todas las liberaciones de la lista
        // son referentes al mismo rango.
        RangoSerie rangoOriginal = this.getRangoSerieByFraccion(
                pLiberaciones.get(0).getIdNir(),
                pLiberaciones.get(0).getSna(),
                pLiberaciones.get(0).getNumInicio(),
                pLiberaciones.get(0).getNumFinal(),
                pLiberaciones.get(0).getProveedorCesionario());

        // Creamos los rangos con la información de la Liberación Solicitada
        for (LiberacionSolicitadaNg libSol : pLiberaciones) {

            // Info de la PK
            RangoSeriePK rangoPk = new RangoSeriePK();
            rangoPk.setIdNir(libSol.getIdNir());
            rangoPk.setSna(libSol.getSna());

            // Info de Rango
            RangoSerie rango = new RangoSerie();
            rango.setId(rangoPk);
            rango.setCentralDestino(libSol.getCentralDestino());
            rango.setCentralOrigen(libSol.getCentralOrigen());
            rango.setEstadoRango(estadoReservado);
            rango.setIdaPnn(rangoOriginal.getIdaPnn());
            rango.setIdoPnn(rangoOriginal.getIdoPnn());
            rango.setNumInicio(libSol.getNumInicio());
            rango.setNumFinal(libSol.getNumFinal());
            rango.setNumSolicitada(rangoOriginal.getNumSolicitada());
            rango.setObservaciones(rangoOriginal.getObservaciones());
            rango.setPoblacion(libSol.getPoblacion());

            // Debería ser el mismo que rangoOriginal.getAsignatario()
            rango.setAsignatario(libSol.getProveedorCesionario());

            rango.setConcesionario(rangoOriginal.getConcesionario());
            rango.setArrendatario(rangoOriginal.getArrendatario());
            rango.setRentado(rangoOriginal.getRentado());
            rango.setSerie(this.getSerie(libSol.getSna(), libSol.getIdNir()));
            rango.setSolicitud(libSol.getSolicitudLiberacion());
            rango.setTipoModalidad(libSol.getTipoModalidad());
            rango.setTipoRed(libSol.getTipoRed());
            rango.setFechaAsignacion(rangoOriginal.getFechaAsignacion());
            rango.setConsecutivoAsignacion(rangoOriginal.getConsecutivoAsignacion());
            rango.setOficioAsignacion(rangoOriginal.getOficioAsignacion());

            listaFracciones.add(rango);
        }

        return listaFracciones;
    }

    @Override
    public List<Liberacion> liberarFraccionesRango(List<LiberacionSolicitadaNg> pLiberaciones,
            boolean pInmediata) throws Exception {

        // Lista de liberaciones generadas
        List<Liberacion> liberaciones = new ArrayList<Liberacion>();

        // Las liberaciones vienen agrupadas por el rango, por lo que todas tienen los
        // mismos valores a excepción del número inicial y número final. Para obtener
        // la información del rango original o fecha de implementación nos vale cualquiera
        // de la lista.
        LiberacionSolicitadaNg libSol = pLiberaciones.get(0);

        // Tenemos en cuenta los minutos.
        Date fHoy = new Date();
        boolean fImplementacionCumplida =
                (fHoy.compareTo(FechasUtils.getFechaImplementacionReal(libSol.getFechaLiberacion())) >= 0);

        // Lista peticiones de fraccionamiento.
        List<IRangoSerie> fraccionesBySolicitud = this.getFraccionesByLiberacionesSolicitadas(pLiberaciones);

        // La primera fracción de la lista es la que tiene el número de Inicio del
        // rango que queremos fraccionar. Es necesario eliminar primer el Rango
        // para luego guardar las fracciones nuevas.
        RangoSerie rangoOriginal = this.getRangoSerieByFraccion(
                libSol.getIdNir(),
                libSol.getSna(),
                ((RangoSerie) fraccionesBySolicitud.get(0)).getNumInicio(),
                ((RangoSerie) fraccionesBySolicitud.get(0)).getNumFinal(),
                libSol.getProveedorCesionario());

        // Si no se ha cumplido la fecha de implementación no se hace nada.
        if ((!pInmediata) && (!fImplementacionCumplida)) {
            // Estado Reservado para Rangos con fecha de implementación no cumplida.
            EstadoRango statusReservado = new EstadoRango();
            statusReservado.setCodigo(EstadoRango.RESERVADO);

            rangoOriginal.setEstadoRango(statusReservado);
            rangoOriginal.setSolicitud(libSol.getSolicitudLiberacion());
            this.saveRangoSerie(rangoOriginal);
            bitacoraService.add(libSol.getSolicitudLiberacion(), "Rango reservado: " + rangoOriginal.toString());

            // Devolvemos la lista en blanco.
            return liberaciones;
        }

        // Cambiamos la solicitud del rango original para que cree los nuevos rangos asociados
        // a la nueva solicitud de liberación.
        rangoOriginal.setSolicitud(libSol.getSolicitudLiberacion());

        // Fracciones finales
        EstadoRango statusAsignado = this.getEstadoRangoByCodigo(EstadoRango.ASIGNADO);
        List<IRangoSerie> fraccionesIRango =
                RangosSeriesUtil.getFracciones(fraccionesBySolicitud, rangoOriginal, statusAsignado);

        List<RangoSerie> fracciones = RangosSeriesUtil.castListaRangoSerieNG(fraccionesIRango);

        // Eliminamos el rango original y guardamos las fracciones
        this.removeRangoSerie(rangoOriginal);

        // Almacenamos las reservas y liberaciones
        for (RangoSerie fraccion : fracciones) {
            if (fraccion.getEstadoRango().getCodigo().equals(EstadoRango.AFECTADO)) {
                // Los rangos afectados son los que se quieren liberar. Se ignoran.
                bitacoraService.add(libSol.getSolicitudLiberacion(), "Rango eliminado: " + rangoOriginal.toString());

                // Creamos una nueva Liberación con la información del rango liberado
                Liberacion liberacion = new Liberacion();
                liberacion.setInicioRango(fraccion.getNumInicio());
                liberacion.setFinRango(fraccion.getNumFinal());
                liberacion.setSolicitudLiberacion(libSol.getSolicitudLiberacion());
                liberacion.setLiberacionSolicitada(libSol);
                liberaciones.add(liberacion);

            } else {
                // Al terminar la liberación los rangos quedan en estado Asignado
                this.saveRangoSerie(fraccion);
                bitacoraService.add(libSol.getSolicitudLiberacion(), "Fracción creada: " + fraccion.toString());
            }
        }

        return liberaciones;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public BigDecimal getTotalNumOcupadaSerie(SeriePK id) throws Exception {
        return rangoSerieDAO.getTotalNumOcupadaSerie(id);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<RangoSerie> findNumeracionesAsiganadasSerie(Abn abn, Nir nir, BigDecimal sna) {
        return rangoSerieDAO.findNumeracionesAsignadasSerie(abn, nir, sna);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<RangoSerie> findNumeracionesSeleccionadasSinAsignar(BigDecimal codSol) throws Exception {
        List<RangoSerie> list = rangoSerieDAO.findRangoSerieBySolicitudSinAsignar(codSol);
        return list;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<RangoSerie> findNumeracionesSeleccionadas(BigDecimal codSol) throws Exception {
        return rangoSerieDAO.findRangoSerieBySolicitud(codSol);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public BigDecimal getTotalNumAsignadaSolicitud(BigDecimal codSol) throws Exception {
        return rangoSerieDAO.getTotalNumAsignadaSolicitud(codSol);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public BigDecimal getTotalNumOcupadaAbn(Abn abn) throws Exception {
        return rangoSerieDAO.getTotalNumOcupadaAbn(abn);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public EstadoRango getEstadoRangoByCodigo(String codigo) {
        return estadoRangoDao.getEstadoRangoByCodigo(codigo);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public RangoSerie saveRangoSerie(RangoSerie rangoSerie) throws Exception {
        return rangoSerieDAO.saveOrUpdate(rangoSerie);
    }

    /**
     * Función que parse el numero nacional.
     * @param numeracion String
     * @return Numero
     */
    @Deprecated
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private Numero parseNumeracionNacional(String numeracion) {
        if (StringUtils.isEmpty(numeracion)) {
            throw new IllegalArgumentException("La numeracion no puede ser nula.");
        }

        if (numeracion.length() > LENGTH_NUM) {
            throw new IllegalArgumentException("La numeracion no puede tener mas de " + LENGTH_NUM + " digitos.");
        }

        if (numeracion.length() < LENGTH_NUM) {
            while (numeracion.length() < LENGTH_NUM) {
                numeracion = "0" + numeracion;
            }
        }

        Numero num = new Numero();
        String nirS;
        String snaS;
        String numS;

        if (numeracion.startsWith("55")
                || numeracion.startsWith("33")
                || numeracion.startsWith("81")) {
            /* ejemplo 22 2213 9000 */
            nirS = numeracion.substring(0, 2);
            snaS = numeracion.substring(2, 6);
            numS = numeracion.substring(6, 10);

        } else {

            /* ejemplo 222 213 9000 */
            nirS = numeracion.substring(0, 3);
            snaS = numeracion.substring(3, 6);
            numS = numeracion.substring(6, 10);
        }

        num.setCodigoNir(nirS);
        num.setSna(snaS);

        LOGGER.debug("numeracion '{}' long= {} codigo nir '{}' sna '{}' num '{}'", numeracion, numeracion.length(),
                nirS, snaS, numS);

        Nir nir = null;
        Serie serie = null;
        RangoSerie rango = null;
        try {

            nir = nirDAO.getNirByCodigo(Integer.parseInt(nirS));
            LOGGER.debug("{}", nir);

            if (nir != null) {
                serie = serieDAO.getSerie(new BigDecimal(snaS), nir.getId());
                LOGGER.debug("{}", serie);

                if (serie != null) {
                    rango = rangoSerieDAO.getRangoPerteneceNumeracion(nir, serie.getId().getSna(), numS);
                    LOGGER.debug("{}", rango);
                }
            }
        } catch (NoResultException e) {
            LOGGER.debug(" no encontrada");

        } catch (NonUniqueResultException e) {
            LOGGER.debug(" no es unica");
        } catch (Exception e) {
            LOGGER.debug("esta no deberia ser", e);
        }

        num.setNumero(numeracion);
        num.setNir(nir);
        num.setSerie(serie);
        num.setNumeroInterno(numS);
        num.setLocal(false);
        num.setNacional(true);

        LOGGER.debug("{}", num);
        return num;
    }

    /**
     * Parsea un numnero local de 7 digitos.
     * @param numeracion numeracion
     * @return Numero
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Deprecated
    private Numero parseNumeracionLocal(String numeracion) {

        Numero num = new Numero();
        String snaS = null;
        String numS = null;

        /* ejemplo 213 9000 */
        /* ejemplo 2131 9000 */

        if (numeracion.length() == MIN_LENGTH_NUM_LOCAL) {

            snaS = numeracion.substring(0, 3);
            numS = numeracion.substring(3, 7);

        } else if (numeracion.length() == MAX_LENGTH_NUM_LOCAL) {

            snaS = numeracion.substring(0, 4);
            numS = numeracion.substring(4, 8);

        }

        num.setCodigoNir(null);
        num.setSna(snaS);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("numeracion '{}' long= {} sna '{}' num '{}'", numeracion, numeracion.length(), snaS, numS);
        }

        Serie serie = new Serie();
        SeriePK idSerie = new SeriePK();

        idSerie.setSna(new BigDecimal(snaS));
        serie.setId(idSerie);

        num.setNumero(numeracion);
        num.setNir(null);
        num.setSerie(serie);
        num.setNumeroInterno(numS);

        num.setLocal(true);
        num.setNacional(false);

        LOGGER.debug("{}", num);
        return num;
    }

    /**
     * @param numeracion numeración
     * @return Numero
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Deprecated
    public Numero parseNumeracion(String numeracion) {
        if (StringUtils.isEmpty(numeracion)) {
            throw new IllegalArgumentException("La numeracion no puede ser nula.");
        }

        if (numeracion.length() > LENGTH_NUM) {
            throw new IllegalArgumentException("La numeracion no puede tener mas de " + LENGTH_NUM + " digitos.");
        }

        if (numeracion.length() == LENGTH_NUM) {
            return parseNumeracionNacional(numeracion);
        } else if ((numeracion.length() >= MIN_LENGTH_NUM_LOCAL) && (numeracion.length() <= MAX_LENGTH_NUM_LOCAL)) {
            return parseNumeracionLocal(numeracion);
        }

        throw new IllegalArgumentException("La numeracion incorrecta.");
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public boolean pertenecenMismaSerie(Numero n0, Numero n1) {

        boolean res = (n0.getNir().getId().equals(n1.getNir().getId()))
                && (n0.getSerie().getId().getSna().equals(n1.getSerie().getId().getSna()));

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("n0 {} n1 {} res {}", n0, n1, res);
        }

        return res;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public boolean existeNumeracion(Numero num) {
        boolean res = false;

        if ((num != null) && (num.getNir() != null) && (num.getSerie() != null)) {
            res = true;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("num {} res={}", num, res);
        }

        return res;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public boolean estaArrendadoRango(Numero from, Numero to, Proveedor arrendador) {

        boolean res = false;

        if (pertenecenMismaSerie(from, to)) {
            try {
                numeracionAsignadaDAO.findNumeracionArrendada(from.getNir(), from.getSerie(), from.getNumeroInterno(),
                        to.getNumeroInterno(), arrendador);
            } catch (NoResultException e) {
                res = false;
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("from {} to {} arrendador {} res {}", from, to, arrendador, res);
        }

        return res;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public boolean estaArrendadoRango(RangoSerie rango) {

        boolean res = (rango.getAsignatario() != null) && (rango.getArrendatario() != null);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("estaarrendado rango {} res {}", rango, res);
        }

        return res;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public boolean hayArrendamiento(RangoSerie rango, Proveedor arrendador, Proveedor arrendatario) {

        boolean res = (rango != null)
                && (rango.getAsignatario() != null)
                && (rango.getArrendatario() != null)
                && (rango.getAsignatario().equals(arrendador))
                && rango.getArrendatario().equals(arrendatario);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("hayArrendamiento rango {} arrendador {} arrendatario {} res {}",
                    rango, arrendador.getId(), arrendatario.getId(), res);
        }

        return res;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public RangoSerie getRangoPertenece(Nir nir, BigDecimal sna, String numInicio, String numFinal) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("{} sna {} numInicio {} numFinal {}", nir, sna, numInicio, numFinal);
        }

        return rangoSerieDAO.getRangoPertenece(nir, sna, numInicio, numFinal);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public RangoSerie getRangoPertenece(Numero numero) {
        return rangoSerieDAO.getRangoPerteneceNumeracion(numero.getNir(), numero.getSerie().getId().getSna(),
                numero.getNumeroInterno());
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void create(SerieArrendada serie) {
        serieArrendadaDAO.create(serie);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteAllSeriesArrendadas() {
        serieArrendadaDAO.deleteAll();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<SerieArrendada> findSeriesArrendadas(int first, int pageSize) {
        return serieArrendadaDAO.findAll(first, pageSize);
    }

    @Override
    public List<NumeracionRedistribuida> fraccionarRangoFromRedistribuciones(
            List<RedistribucionSolicitadaNg> pRedistribuciones) throws Exception {

        // Status Afectado de Rango
        EstadoRango statusAfectado = new EstadoRango();
        statusAfectado.setCodigo(EstadoRango.AFECTADO);

        // Lista de Numeraciones Redistribuidas generadas
        List<NumeracionRedistribuida> numeraciones = new ArrayList<NumeracionRedistribuida>(pRedistribuciones.size());

        // Las Fracciones vienen agrupadas por el rango, por lo que todas tienen los
        // mismos valores a excepción del número inicial y número final. Para obtener
        // la información del rango original nos vale cualquiera
        // de la lista.
        RedistribucionSolicitadaNg redSol = pRedistribuciones.get(0);
        RangoSerie rangoOriginal = this.getRangoSerieByFraccion(
                redSol.getIdNir(),
                redSol.getSna(),
                redSol.getNumInicio(),
                redSol.getNumFinal(),
                redSol.getProveedorSolicitante());

        // Fracciones Afectadas
        List<IRangoSerie> fraccionesBySolicitud = new ArrayList<IRangoSerie>(pRedistribuciones.size());
        for (RedistribucionSolicitadaNg redSolFraccion : pRedistribuciones) {
            RangoSerie rangoCloned = (RangoSerie) rangoOriginal.clone();
            rangoCloned.setSolicitud(redSolFraccion.getSolicitudRedistribucion());
            rangoCloned.setNumInicio(redSolFraccion.getNumInicio());
            rangoCloned.setNumFinal(redSolFraccion.getNumFinal());
            rangoCloned.setEstadoRango(statusAfectado);
            fraccionesBySolicitud.add(rangoCloned);
        }

        // Lista de fracciones derivadas del rango original
        EstadoRango statusAsignado = this.getEstadoRangoByCodigo(EstadoRango.ASIGNADO);
        List<IRangoSerie> fraccionesIRango =
                RangosSeriesUtil.getFracciones(fraccionesBySolicitud, rangoOriginal, statusAsignado);

        List<RangoSerie> fracciones = RangosSeriesUtil.castListaRangoSerieNG(fraccionesIRango);

        // Eliminamos el rango original y guardamos las fracciones
        this.removeRangoSerie(rangoOriginal);

        // Guardamos los cambios
        for (RangoSerie fraccion : fracciones) {
            if (fraccion.getEstadoRango().getCodigo().equals(EstadoRango.AFECTADO)) {
                // Los rangos afectados son los que se quieren redistribuir
                numeraciones.add(this.redistribuirRango(fraccion, redSol));
            } else {
                // El resto de rangos son las fracciones que quedan en la numeración original
                fraccion.setSolicitud(redSol.getSolicitudRedistribucion());
                this.saveRangoSerie(fraccion);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Fracción creada: " + fraccion.toString());
                }
                bitacoraService.add(redSol.getSolicitudRedistribucion(), "Fracción creada: " + fraccion.toString());
            }
        }

        return numeraciones;
    }

    @Override
    public NumeracionRedistribuida redistribuirRango(RangoSerie pRango, RedistribucionSolicitadaNg pRedSol)
            throws Exception {

        // Objeto NumeracionRedistribuida con la información original del rango para posibles cancelaciones.
        NumeracionRedistribuida numRed = new NumeracionRedistribuida();
        numRed.setRedistribucionSolicitada(pRedSol);
        numRed.setSolicitudRedistribucion(pRedSol.getSolicitudRedistribucion());
        numRed.setInicioRango(pRango.getNumInicio());
        numRed.setFinRango(pRango.getNumFinal());
        numRed.setConcesionario(pRango.getConcesionario());
        numRed.setArrendatario(pRango.getArrendatario());
        numRed.setCentralDestino(pRango.getCentralDestino());
        numRed.setCentralOrigen(pRango.getCentralOrigen());
        numRed.setPoblacion(pRango.getPoblacion());
        //numRed.setTipoModalidad(pRedSol.getTipoModalidad());
        numRed.setTipoModalidad(pRango.getTipoModalidad());
//        if(pRedSol.getTipoRed() != null) {
//        	numRed.setTipoRed(pRedSol.getTipoRed());
//        }else {
//        	numRed.setTipoRed(pRango.getTipoRed());
//        }
        numRed.setTipoRed(pRango.getTipoRed());

        StringBuffer sbUpdate = new StringBuffer();
        sbUpdate.append("Id Rango: ").append(pRango.getId().getId()).append(". ");

        if (pRedSol.getCentralDestino() != null) {
            if (pRango.getCentralDestino() != null) {
                sbUpdate.append("Central Destino: ").append(pRango.getCentralDestino().getNombre());
            } else {
                sbUpdate.append("Central Destino: null");
            }
            sbUpdate.append(" -> ").append(pRedSol.getCentralDestino().getNombre()).append("; ");
            pRango.setCentralDestino(pRedSol.getCentralDestino());
        }

        if (pRedSol.getCentralOrigen() != null) {
            if (pRango.getCentralOrigen() != null) {
                sbUpdate.append("Central Origen: ").append(pRango.getCentralOrigen().getNombre());
            } else {
                sbUpdate.append("Central Origen: null");
            }
            sbUpdate.append(" -> ").append(pRedSol.getCentralOrigen().getNombre()).append("; ");
            pRango.setCentralOrigen(pRedSol.getCentralOrigen());
        }

        if (pRedSol.getProveedorArrendatario() != null) {
            if (pRango.getArrendatario() != null) {
                sbUpdate.append("Arrendatario: ").append(pRango.getArrendatario().getNombreCorto());
            } else {
                sbUpdate.append("Arrendatario: null");
            }
            sbUpdate.append(" -> ").append(pRedSol.getProveedorArrendatario().getNombreCorto()).append("; ");
            pRango.setArrendatario(pRedSol.getProveedorArrendatario());
        }

        if (pRedSol.getProveedorConcesionario() != null) {
            if (pRango.getConcesionario() != null) {
                sbUpdate.append("Concesionario: ").append(pRango.getConcesionario().getNombreCorto());
            } else {
                sbUpdate.append("Concesionario: null");
            }
            sbUpdate.append(" -> ").append(pRedSol.getProveedorConcesionario().getNombreCorto()).append("; ");
            pRango.setConcesionario(pRedSol.getProveedorConcesionario());
        }

        if (pRedSol.getTipoRed() != null) {
            if (pRango.getTipoRed() != null) {
                sbUpdate.append("Tipo de Red: ").append(pRango.getTipoRed().getDescripcion());
            } else {
                sbUpdate.append("Tipo de Red: null");
            }
            sbUpdate.append(" -> ").append(pRedSol.getTipoRed().getDescripcion()).append("; ");
            pRango.setTipoRed(pRedSol.getTipoRed());
        }

        if (pRedSol.getTipoModalidad() != null) {
            if (pRango.getTipoModalidad() != null) {
                sbUpdate.append("Tipo de Modalidad: ").append(pRango.getTipoModalidad().getDescripcion());
            } else {
                sbUpdate.append("Tipo de Modalidad: null");
            }
            sbUpdate.append(" -> ").append(pRedSol.getTipoModalidad().getDescripcion()).append("; ");
            pRango.setTipoModalidad(pRedSol.getTipoModalidad());
        }
        
        pRango.setTipoModalidad(pRedSol.getTipoModalidad());

        if (pRedSol.getPoblacion() != null) {
            if (pRango.getPoblacion() != null) {
                sbUpdate.append("Población: ").append(pRango.getPoblacion().getNombre());
            } else {
                sbUpdate.append("Población: null");
            }
            sbUpdate.append(" -> ").append(pRedSol.getPoblacion().getNombre()).append("; ");
            pRango.setPoblacion(pRedSol.getPoblacion());
        }

        sbUpdate.append("IDO: ").append(pRango.getIdoPnn()).append(" -> ");
        sbUpdate.append(pRedSol.getIdoPnn()).append("; ");
        pRango.setIdoPnn(pRedSol.getIdoPnn());

        // Guardamos la información actualizada
        sbUpdate.append("Solicitud: ").append(pRango.getSolicitud().getId());
        sbUpdate.append(" -> ").append(pRedSol.getSolicitudRedistribucion().getId()).append("; ");
        pRango.setSolicitud(pRedSol.getSolicitudRedistribucion());

        // Cambiamos el estado a Asignado
        EstadoRango estadoAsignado = new EstadoRango();
        estadoAsignado.setCodigo(EstadoRango.ASIGNADO);
        pRango.setEstadoRango(estadoAsignado);
        this.saveRangoSerie(pRango);

        bitacoraService.add(pRedSol.getSolicitudRedistribucion(), sbUpdate.toString());

        // Agregamos la numeración redistribuida a la RedistribucionSolicitadaNg
        pRedSol.addNumeracionRedistribuida(numRed);

        return numRed;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public int getSeriesArrendadasCount() {
        return serieArrendadaDAO.getSeriesArrendadasCount();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public boolean isSerieCompleta(RangoSerie rango) {
        return rango.getNumInicio().equals(RangoSerie.NUM_MIN)
                && rango.getNumFinal().equals(RangoSerie.NUM_MAX);
    }

    @Override
    public BigDecimal getTotalRangosAsignadosByPst(String tipoRed, String tipoModalidad, Proveedor proveedor) {
        return rangoSerieDAO.getTotalRangosAsignadosByPst(tipoRed, tipoModalidad, proveedor);
    }

    @Override
    public BigDecimal getTotalNumRangosAsignadosByPoblacion(String tipoRed, String tipoModalidad, Proveedor proveedor,
            Poblacion poblacion) {
        return rangoSerieDAO.getTotalNumRangosAsignadosByPoblacion(tipoRed, tipoModalidad, proveedor,
                poblacion.getInegi());
    }

    @Override
    public Poblacion getPoblacionWithMaxNumAsignadaByAbn(Abn abn) {
        return rangoSerieDAO.getPoblacionWithMaxNumAsignadaByAbn(abn);
    }

    @Override
    public BigDecimal getTotalNumRangosAsignadosByAbn(
            String tipoRed, String tipoModalidad, Abn abn, Proveedor proveedor) {
        return rangoSerieDAO.getTotalNumRangosAsignadosByAbn(tipoRed, tipoModalidad, abn.getCodigoAbn(), proveedor);
    }

    @Override
    public List<HistoricoSerie> findHistoricoSeries(FiltroBusquedaHistoricoSeries filtro, int first, int pagesize) {
        return historicoSerieDAO.findHistoricoSeries(filtro, first, pagesize);
    }

    @Override
    public int findHistoricoSeriesCount(FiltroBusquedaHistoricoSeries filtro) {
        return historicoSerieDAO.findHistoricoSeriesCount(filtro);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<NGReporteador> findHistoricoSeries(FiltroReporteadorNG filtro) throws Exception {
        List<NGReporteador> listaSolicitadas = historicoSerieDAO.findHistoricoSeries(filtro);
        List<NGReporteador> listaCedidas = historicoSerieDAO.findHistoricoSeriesCedidas(filtro);
        return getListaResultado(listaSolicitadas, listaCedidas, filtro);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<NGReporteador> findHistoricoSeriesAsignadas(FiltroReporteadorNG filtro) {
        return historicoSerieDAO.findHistoricoSeriesTotalesAsignadas(filtro);
    }

    @Override
    public List<RangoSerie> findAllRangosByNumSol(NumeracionSolicitada numeracionSolicitada) {

        return rangoSerieDAO.findAllRangosByNumSol(numeracionSolicitada);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public boolean isValidoNir(Integer nir) {
        boolean res = false;
        if ((nir < Nir.MIN_NIR || nir > Nir.MAX_NIR)) {
            res = false;
        } else if ((nir >= 10) && (nir <= 99)) {
            res = true;
        } else if ((nir >= 100) && (nir <= 999)) {
            res = true;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("nir {} res={}", nir, res);
        }

        return res;
    }

    @Override
    public boolean isValidoNir(String nir) {
        boolean res = false;

        if (StringUtils.isEmpty(nir)) {
            res = false;
        } else if ((nir.length() == 3) && (nir.charAt(0) == '0')) {
            res = false;
        } else if ((nir.length() == 2) && (nir.charAt(0) == '0')) {
            res = false;
        } else {
            try {
                Integer n = Integer.parseInt(nir);
                res = isValidoNir(n);
            } catch (NumberFormatException e) {
                res = false;
            }

        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("nir {} res={}", nir, res);
        }

        return res;
    }

    @Override
    public boolean isValidoSNA(Integer sna) {
        boolean res = false;
        if ((sna < Serie.MIN_SERIE || sna > Serie.MAX_SERIE)) {
            res = false;
        } else {
            res = true;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("sna {} res={}", sna, res);
        }

        return res;
    }

    @Override
    public boolean isValidoSNA(String sna) {
        boolean res = false;

        if (StringUtils.isEmpty(sna)) {
            res = false;
        } else if ((sna.length() == 4) && (sna.charAt(0) == '0')) {
            res = false;
        } else if ((sna.length() == 3) && (sna.charAt(0) == '0')) {
            res = false;
        } else {
            try {
                Integer n = Integer.parseInt(sna);
                res = isValidoSNA(n);
            } catch (NumberFormatException e) {
                res = false;
            }

        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("sna {} res={}", sna, res);
        }

        return res;
    }

    @Override
    public boolean tieneSerieCompleta(BigDecimal idNir, BigDecimal sna, Proveedor pst) {
        return true;
    }

    @Override
    public RangoSerie getRangoSerieOriginalByFraccion(RangoSerie pFraccion, BigDecimal pIdSolicitud,
            Proveedor pAsignatarioInicial) throws Exception {

        // Cuando se realiza una fracción, todos los rangos que se generan llevan el identificador de la solicitud
        // que está modificando la numeración.
        FiltroBusquedaRangos fbr = new FiltroBusquedaRangos();
        fbr.setIdSolicitud(pIdSolicitud);
        fbr.setIdSna(pFraccion.getSerie().getId().getSna());
        fbr.setIdAbn(pFraccion.getSerie().getNir().getAbn().getCodigoAbn());
        fbr.setIdNir(pFraccion.getSerie().getNir().getId());
        fbr.setAsignatario(pAsignatarioInicial);

        // Lista de posibles rangos adyacentes de la fracción seleccionada
        List<RangoSerie> rangosAdyacentes = this.findAllRangos(fbr);

        String numInicio = null;
        String numFinal = null;
        RangoSerie rangoInfo = null;
        for (RangoSerie rango : rangosAdyacentes) {
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
            // Información de Asignatario, Red, etc.
            RangoSerie rangoOriginal = (RangoSerie) rangoInfo.clone();

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
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public BigDecimal getNumRangosAsignados() {
        return rangoSerieDAO.getNumRangosAsignados();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<RangoSerie> getRangosAsignados(int num, int pageSize) {
        return rangoSerieDAO.getRangosAsignados(num, pageSize);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<DetalleRangoAsignadoNg> getRangosAsignadosD(int num, int pageSize) {
        return rangoSerieDAO.getRangosAsignadosD(num, pageSize);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<Nir> findNirsNumeroLocal(Numero numeroLocal) {
        return rangoSerieDAO.findNirsNumeroLocal(numeroLocal.getSerie().getId().getSna(),
                numeroLocal.getNumeroInterno());
    }

    @Override
    public Integer getTotalNumRangosAsignadosByMunicipio(Municipio municipio) {
        return rangoSerieDAO.getTotalNumRangosAsignadosByMunicipio(municipio);
    }

    @Override
    public Integer getTotalNumRangosAsignadosByEstado(Estado estado) {
        return rangoSerieDAO.getTotalNumRangosAsignadosByEstado(estado);
    }

    @Override
    public List<EstadoRango> findAllEstadosRango() {
        return estadoRangoDao.findAllEstadosRango();
    }

    @Override
    public int getNirsByPoblacion(Poblacion poblacion) {
        return rangoSerieDAO.getNirsByPoblacion(poblacion);
    }

    @Override
    public List<Nir> findAllNirsByPoblacion(Poblacion poblacion) {
        return rangoSerieDAO.findAllNirsByPoblacion(poblacion);
    }

    @Override
    public List<PoblacionNumeracion> getPoblacionesNumeracionByProveedor(Proveedor proveedorServ) {
        return rangoSerieDAO.getPoblacionesNumeracionByProveedor(proveedorServ);
    }

    @Override
    public List<EstadoNumeracion> getEstadosNumeracionByProveedor(Proveedor proveedorServ) {
        return rangoSerieDAO.getEstadosNumeracionByProveedor(proveedorServ);
    }

    @Override
    public List<NirNumeracion> getNirsNumeracionByProveedor(Proveedor proveedorServ) {
        return rangoSerieDAO.getNirsNumeracionByProveedor(proveedorServ);
    }

    @Override
    public BigDecimal getNumeracionPoblacionByProveedor(Proveedor proveedorServ, Poblacion poblacion) {
        return rangoSerieDAO.getNumeracionPoblacionByProveedor(proveedorServ, poblacion);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ClaveServicio getClaveServicioByCodigo(BigDecimal pCodigo) {
        return claveServicioDao.getClaveServicioByCodigo(pCodigo);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Nir getNirById(BigDecimal pdIdNir) throws Exception {
        return nirDAO.getNir(pdIdNir);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public boolean existeNumeracionAsignadaAlPstByConvenio(ProveedorConvenio proveedorConvenio) {
        return rangoSerieDAO.existeNumeracionAsignadaAlPstByConvenio(proveedorConvenio);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public boolean existeNumeracionAsignadaAlPst(Proveedor proveedor) {
        return rangoSerieDAO.existeNumeracionAsignadaAlPst(proveedor);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Serie saveSerie(Serie pSerie) {

        // Se hace la Auditoría del registro
        pSerie.updateAuditableValues(usuariosService.getCurrentUser());

        return serieDAO.saveOrUpdate(pSerie);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Serie createSerie(Nir pNir, BigDecimal pSna) {
        Serie serie = null;
        try {
            // Estatus Activo
            Estatus estatusActivo = new Estatus();
            estatusActivo.setCdg(Estatus.ACTIVO);

            Serie serieNueva = new Serie();
            serieNueva.setEstatus(estatusActivo);
            serieNueva.setNir(pNir);

            SeriePK idSerie = new SeriePK();
            idSerie.setIdNir(pNir.getId());
            idSerie.setSna(pSna);
            serieNueva.setId(idSerie);

            serie = this.saveSerie(serieNueva);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Serie {} creada para el cdgNir {}", pSna, pNir.getCodigo());
            }
        } catch (Exception e) {
            LOGGER.error("Error al guardar la serie " + e.getMessage());
        }
        return serie;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public int desactivarSeriesByNir(BigDecimal pIdNir) {
        return serieDAO.desactivarSeriesByNir(pIdNir);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public int activarSeriesByNir(BigDecimal pIdNir, int pInicioSerie, int pFinalSerie) {
        return serieDAO.activarSeriesByNir(pIdNir, pInicioSerie, pFinalSerie);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public byte[] getExportConsultaCatalogoSeries(FiltroBusquedaSeries pfiltro) throws Exception {
        pfiltro.setUsarPaginacion(false);
        List<VCatalogoSerie> listado = serieDAO.findAllCatalogoSeries(pfiltro);
        ExportConsultaCatalogoSerie excca = new ExportConsultaCatalogoSerie(listado);
        ExportarExcel export = new ExportarExcel(parametroDao);
        if (export.getTamMaxExportacion() >= listado.size()) {
            return export.generarReporteExcel("Catálogo de Series", excca);
        } else {
            return export.generarReporteExcelSeries("Catálogo de Series", excca, listado);
        }

    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public boolean existSerieWithNirAbn(String abn, String nir) {
        return serieDAO.existSerieWithNirAbn(abn, nir);
    }

    @Override
    public List<Object[]> findAllSeriesOcupadas(FiltroBusquedaSeries pFiltros) {
        return serieDAO.findAllSeriesOcupadas(pFiltros);
    }

    @Override
    public List<Object[]> findTotalesTramites(FiltroReporteadorNG filtro) {
        return historicoSerieDAO.getTotalesTramitesPorFechas(filtro);
    }

    @Override
    public Integer getTotalNumeracionAginadaProveedor(BigDecimal idPst) {
        return rangoSerieDAO.getTotalNumeracionAginadaProveedor(idPst);
    }

    @Override
    public Nir getNirByCodigo(int cdgNir) {
        return nirDAO.getNirByCodigo(cdgNir);
    }

    @Override
    public List<PoblacionNumeracion>
            findALLPoblacionesNumeracionByProveedorEstado(Proveedor proveedorServ, Estado estado) {
        return rangoSerieDAO.findALLPoblacionesNumeracionByProveedorEstado(proveedorServ, estado);
    }

    @Override
    public List<PoblacionNumeracion>
            findALLPoblacionesNumeracionByProveedorNir(Proveedor proveedorServ, Nir nir) {
        return rangoSerieDAO.findALLPoblacionesNumeracionByProveedorNir(proveedorServ, nir);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public int findAllRangosAsignadosFijosCount() {

        return rangoSerieDAO.findAllRangosAsignadosFijosCount();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<RangoSerie> findAllRangosAsignadosFijos(int numPage, int pageSize) {

        return rangoSerieDAO.findAllRangosAsignadosFijos(numPage, pageSize);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<DetallePlanAbdPresuscripcion> findAllRangosAsignadosFijosD(int numPage, int pageSize) {

        return rangoSerieDAO.findAllRangosAsignadosFijosD(numPage, pageSize);
    }

    @Override
    public List<Proveedor> findAllPrestadoresServicioBy(Nir nir, Abn abn, Poblacion poblacion, Municipio municipio,
            Estado estado) {
        return rangoSerieDAO.findAllPrestadoresServicioBy(nir, abn, poblacion, municipio, estado);
    }

    @Override
    public List<Poblacion> findALLPoblacionesNumeracionAsignadaByNir(Nir nir) {
        return rangoSerieDAO.findALLPoblacionesNumeracionAsignadaByNir(nir);
    }

    @Override
    public int findNumeracionesAsignadasNir(Nir nir) {
        return rangoSerieDAO.findNumeracionesAsignadasNir(nir);
    }

    @Override
    public List<Poblacion> findALLPoblacionesNumeracionAsignadaByMunicipio(Municipio municipio) {
        return rangoSerieDAO.findALLPoblacionesNumeracionAsignadaByMunicipio(municipio);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String actualizaSerie(BigDecimal serieOriginal, BigDecimal nirOriginal, BigDecimal serieNueva,
            BigDecimal usuario) {
        return serieDAO.actualizaSerie(serieOriginal, nirOriginal, serieNueva, usuario);
    }

    @Override
    public List<NGReporteador> getListaResultado(List<NGReporteador> listaSolicitadas, List<NGReporteador> listaCedidas,
            FiltroReporteadorNG filtro)
            throws Exception {
        int contCedidas = 0;
        int contSolicitadas = 0;
        // Búsqueda en la lista de Cedidas las numeraciones a adicionar a las que vienen de Solicitadas
        if (listaSolicitadas != null && listaCedidas != null) {
            for (NGReporteador listCedidasData : listaCedidas) {
                contSolicitadas = 0;
                for (NGReporteador listSolicitadasData : listaSolicitadas) {
                    boolean esIgual = false;
                    if (filtro.getPrimeraAgrupacion() != null) {
                        if (filtro.getPrimeraAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_PST)) {
                            if (listSolicitadasData.getIdPst().equals(listCedidasData.getIdPst())) {
                                esIgual = true;
                            }
                        } else if (filtro.getPrimeraAgrupacion().getCodigo()
                                .equalsIgnoreCase(ElementoAgrupador.COD_ESTADO)) {
                            if (listSolicitadasData.getEstado().equals(listCedidasData.getEstado())) {
                                esIgual = true;
                            }
                        } else if (filtro.getPrimeraAgrupacion().getCodigo()
                                .equalsIgnoreCase(ElementoAgrupador.COD_MUNICIPIO)) {
                            if (listSolicitadasData.getMunicipio().equals(listCedidasData.getMunicipio())) {
                                esIgual = true;
                            }
                        } else if (filtro.getPrimeraAgrupacion().getCodigo()
                                .equalsIgnoreCase(ElementoAgrupador.COD_POBLACION)) {
                            if (listSolicitadasData.getDescPoblacion().equals(listCedidasData.getDescPoblacion())) {
                                esIgual = true;
                            }
                        } else if (filtro.getPrimeraAgrupacion().getCodigo().
                                equalsIgnoreCase(ElementoAgrupador.COD_ABN)) {
                            if (listSolicitadasData.getAbn().equals(listCedidasData.getAbn())) {
                                esIgual = true;
                            }
                        }
                        if (filtro.getSegundaAgrupacion() != null) {
                            if (filtro.getSegundaAgrupacion().getCodigo().equalsIgnoreCase(ElementoAgrupador.COD_PST)) {
                                if (listSolicitadasData.getIdPst().equals(listCedidasData.getIdPst())) {
                                    esIgual = true;
                                } else {
                                    esIgual = false;
                                }
                            } else if (filtro.getSegundaAgrupacion().getCodigo()
                                    .equalsIgnoreCase(ElementoAgrupador.COD_ESTADO)) {
                                if (listSolicitadasData.getEstado().equals(listCedidasData.getEstado())) {
                                    esIgual = true;
                                } else {
                                    esIgual = false;
                                }
                            } else if (filtro.getSegundaAgrupacion().getCodigo()
                                    .equalsIgnoreCase(ElementoAgrupador.COD_MUNICIPIO)) {
                                if (listSolicitadasData.getMunicipio().equals(listCedidasData.getMunicipio())) {
                                    esIgual = true;
                                } else {
                                    esIgual = false;
                                }
                            } else if (filtro.getSegundaAgrupacion().getCodigo()
                                    .equalsIgnoreCase(ElementoAgrupador.COD_POBLACION)) {
                                if (listSolicitadasData.getDescPoblacion().equals(listCedidasData.getDescPoblacion())) {
                                    esIgual = true;
                                } else {
                                    esIgual = false;
                                }
                            } else if (filtro.getSegundaAgrupacion().getCodigo()
                                    .equalsIgnoreCase(ElementoAgrupador.COD_ABN)) {
                                if (listSolicitadasData.getAbn().equals(listCedidasData.getAbn())) {
                                    esIgual = true;
                                } else {
                                    esIgual = false;
                                }
                            }
                        }
                    } else {
                        if (null != filtro.getPstSeleccionada()) {
                            if (listSolicitadasData.getIdPst().equals(
                                    listCedidasData.getIdPst().equals(filtro.getPstSeleccionada()))) {
                                esIgual = true;
                            } else {
                                esIgual = false;
                            }
                        }
                        if (null != filtro.getEstadoSeleccionado()) {
                            if (listSolicitadasData.getEstado().equals(
                                    listCedidasData.getEstado().equals(filtro.getEstadoSeleccionado()))) {
                                esIgual = true;
                            } else {
                                esIgual = false;
                            }
                        }
                        if (null != filtro.getMunicipioSeleccionado()) {
                            if (listSolicitadasData.getMunicipio().equals(
                                    listCedidasData.getMunicipio().equals(filtro.getMunicipioSeleccionado()))) {
                                esIgual = true;
                            } else {
                                esIgual = false;
                            }
                        }
                        if (null != filtro.getPoblacionSeleccionada()) {
                            if (listSolicitadasData.getDescPoblacion().equals(
                                    listCedidasData.getDescPoblacion().equals(filtro.getPoblacionSeleccionada()))) {
                                esIgual = true;
                            } else {
                                esIgual = false;
                            }
                        }
                        if (null != filtro.getAbnSeleccionado()) {
                            if (listSolicitadasData.getAbn().equals(
                                    listCedidasData.getAbn().equals(filtro.getAbnSeleccionado()))) {
                                esIgual = true;
                            } else {
                                esIgual = false;
                            }
                        }
                    }
                    if (esIgual) {
                        if (null != listaSolicitadas.get(contSolicitadas).getAsignadasFijas()
                                && null != listaCedidas.get(contCedidas).getAsignadasFijas()) {
                            listaSolicitadas.get(contSolicitadas).setAsignadasFijas(
                                    listaSolicitadas.get(contSolicitadas).getAsignadasFijas()
                                            + listaCedidas.get(contCedidas).getAsignadasFijas());
                            listaSolicitadas.get(contSolicitadas).setTotalAsignadas(
                                    listaSolicitadas.get(contSolicitadas).getTotalAsignadas()
                                            + listaCedidas.get(contCedidas).getAsignadasFijas());
                        }
                        if (null != listaSolicitadas.get(contSolicitadas).getAsignadasMovilesCPP()
                                && null != listaCedidas.get(contCedidas).getAsignadasMovilesCPP()) {
                            listaSolicitadas.get(contSolicitadas).setAsignadasMovilesCPP(
                                    listaSolicitadas.get(contSolicitadas).getAsignadasMovilesCPP()
                                            + listaCedidas.get(contCedidas).getAsignadasMovilesCPP());
                            listaSolicitadas.get(contSolicitadas).setTotalAsignadas(
                                    listaSolicitadas.get(contSolicitadas).getTotalAsignadas()
                                            + listaCedidas.get(contCedidas).getAsignadasMovilesCPP());
                        }
                        if (null != listaSolicitadas.get(contSolicitadas).getAsignadasMovilesMPP()
                                && null != listaCedidas.get(contCedidas).getAsignadasMovilesMPP()) {
                            listaSolicitadas.get(contSolicitadas).setAsignadasMovilesMPP(
                                    listaSolicitadas.get(contSolicitadas).getAsignadasMovilesMPP()
                                            + listaCedidas.get(contCedidas).getAsignadasMovilesMPP());
                            listaSolicitadas.get(contSolicitadas).setTotalAsignadas(
                                    listaSolicitadas.get(contSolicitadas).getTotalAsignadas()
                                            + listaCedidas.get(contCedidas).getAsignadasMovilesMPP());
                        }
                    }
                    contSolicitadas++;
                }
                contCedidas++;
            }
        }
        return listaSolicitadas;
    }

    @Override
    public byte[] getExportHistoricoSeries(FiltroBusquedaHistoricoSeries filtro) throws Exception {
        filtro.setUsarPaginacion(false);

        int total = historicoSerieDAO.findHistoricoSeriesCount(filtro);
        List<HistoricoSerie> listahistorico = historicoSerieDAO.findHistoricoSeries(filtro, 0, total);

        ExportHistoricoSerieNg exhs = new ExportHistoricoSerieNg(listahistorico);
        ExportarExcel export = new ExportarExcel(parametroDao);
        if (export.getTamMaxExportacion() >= listahistorico.size()) {
            return export.generarReporteExcel("Historico", exhs);
        } else {
            return export.generarReporteExcelHistoricoSeriesNg("Historico", exhs, listahistorico);
        }
    }

    @Override
    public List<RangoSerie> findAllRangosBySolicitud(Solicitud pSolicitud) {

        return rangoSerieDAO.findAllRangosBySolicitud(pSolicitud);
    }

    @Override
    public boolean isRangoLibre(BigDecimal nir, BigDecimal sna, String inicioRango, String finRango) {

        return rangoSerieDAO.isRangoLibre(nir, sna, inicioRango, finRango);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<SerieArrendadaPadre> findSeriesArrendadasPadre(int first, int pageSize) {
        return serieArrendadaDAO.findAllPadres(first, pageSize);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public int findSeriesArrendadasPadreCount() {
        return serieArrendadaDAO.findAllPadresCount();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Long getDetalleReporteAbdCount() {
        return rangoSerieDAO.getDetalleReporteAbdCount();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public List<DetalleReporteAbd> getDetalleReporteAbd(int first, int pageSize) {
        return rangoSerieDAO.getDetalleReporteAbd(first, pageSize);
    }

    @Override
    public void generarPnnAux() {
        rangoSerieDAO.generarPnnAux();
    }

    @Override
    public boolean isRangosPentientesByNir(Nir nir) {
        return rangoSerieDAO.isRangosPentientesByNir(nir);
    }
}
