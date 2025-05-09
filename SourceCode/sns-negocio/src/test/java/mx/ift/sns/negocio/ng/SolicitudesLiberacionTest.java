package mx.ift.sns.negocio.ng;

import mx.ift.sns.context.TestCaseContext;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Casos unitarios para pruebas de Solicitudes de Liberación.
 */
public class SolicitudesLiberacionTest extends TestCaseContext {

    // /** Logger de la clase. */
    // private static final Logger LOGGER = LoggerFactory.getLogger(SolicitudesLiberacionTest.class);
    //
    // /** Serie libre. */
    // private Serie serieLibre = null;
    //
    // /** Rango de una serie. */
    // private RangoSerie rango = null;
    //
    // /** Filtro de Búsqueda de Series. */
    // private static FiltroBusquedaSeries filtroSeriesLibres = null;
    //
    // /** Filtro de Búsqueda de Ocupadas. */
    // private static FiltroBusquedaSeries filtroSeriesOcupadas = null;
    //
    // /** Solicitud de Liberación genérica sin Id. */
    // private static SolicitudLiberacionNg defaultSolLib = null;
    //
    // /** Solicitud de Liberación. */
    // private static SolicitudLiberacionNg solLib = null;
    //
    // /** Generados de números aleatorios. */
    // private static Random random = null;
    //
    // /** Lista de rangos temporales no liberados en las pruebas. */
    // private ArrayList<RangoSerie> listaRangosTemporales = new ArrayList<RangoSerie>();

    /** Constructor, inicializa la clase de la que se hereda. */
    public SolicitudesLiberacionTest() {
        // IMPORTANTE: Es necesario llamar al constructor de la clase
        // 'TestCaseContext' para que se inicialice el Entorno
        super();
    }

    /**
     * Método ejecutado antes de todos los test de la clase. Prepara las variables comunes a los test.
     */
    @BeforeClass
    public static void inicializarParametros() {
        // // Filtros
        // filtroSeriesLibres = new FiltroBusquedaSeries();
        // filtroSeriesLibres.setSerieLibre(true);
        // filtroSeriesLibres.setUsarPaginacion(true);
        // filtroSeriesLibres.setResultadosPagina(5);
        // filtroSeriesLibres.setNumeroPagina(0);
        // filtroSeriesOcupadas = new FiltroBusquedaSeries();
        // filtroSeriesOcupadas.setSerieLibre(false);
        // filtroSeriesOcupadas.setUsarPaginacion(true);
        // filtroSeriesOcupadas.setResultadosPagina(5);
        // filtroSeriesOcupadas.setNumeroPagina(0);
        //
        // // Generador de números aleatorios
        // random = new Random();
        //
        // // Solicitud de Liberación.
        // defaultSolLib = new SolicitudLiberacionNg();
        // defaultSolLib.setComentarios("Pruebas JUNIT - SolicitudesLiberacionTest");
        // defaultSolLib.setFechaIniPruebas(new Date());
        // defaultSolLib.setFechaIniUtilizacion(new Date());
        // defaultSolLib.setFechaSolicitud(new Date());
        // defaultSolLib.setReferencia("123456789");
        //
        // EstadoSolicitud estSol = new EstadoSolicitud();
        // estSol.setCodigo(EstadoSolicitud.SOLICITUD_EN_TRAMITE);
        // defaultSolLib.setEstadoSolicitud(estSol);
        //
        // Proveedor proveedor = new Proveedor();
        // proveedor.setId(new BigDecimal(55));
        // defaultSolLib.setProveedorSolicitante(proveedor);
    }

    /**
     * Método ejecutado después de todos los test de la clase. Libera las variables y recursos comunes a los test
     */
    @AfterClass
    public static void liberarParametros() {
    }

    /**
     * Método ejecutado antes de cada test de la clase. Prepara las variables de un test concreto.
     */
    @Before
    public void setUp() {
        // // Servicio de Rangos y Series
        // try {
        // ISeriesService seriesService = (ISeriesService) getEjbBean(SeriesService.class);
        // INumeracionGeograficaService ngService =
        // (INumeracionGeograficaService) getEjbBean(NumeracionGeograficaService.class);
        //
        // List<Serie> seriesLibres = seriesService.findAllSeries(filtroSeriesLibres);
        // List<Serie> seriesOcupadas = seriesService.findAllSeries(filtroSeriesOcupadas);
        //
        // if (!seriesLibres.isEmpty()) {
        // // Serie Libre para pruebas
        // LOGGER.debug("Se han encontrado " + seriesLibres.size() + " series libres.");
        // int randomNum = random.nextInt(seriesLibres.size());
        // serieLibre = seriesLibres.get(randomNum);
        //
        // // Series ocupadas para pruebas
        // randomNum = random.nextInt(seriesOcupadas.size());
        // Serie serieOcupada = seriesOcupadas.get(randomNum);
        //
        // // Rango de la serie ocupada
        // randomNum = random.nextInt(serieOcupada.getRangos().size());
        // RangoSerie rangoExistente = serieOcupada.getRangos().get(randomNum);
        //
        // // Solicitud de Liberación
        // solLib = ngService.saveSolicitudLiberacion(defaultSolLib);
        //
        // // Estatus Asignado para los rangos Ficticios
        // EstadoRango statusAsignado = ngService.getEstadoRangoByCodigo(EstadoRango.ASIGNADO);
        //
        // // Rango ficticio
        // rango = (RangoSerie) RangosSeriesUtil.crearRangoFicticio(500, 999, rangoExistente, statusAsignado);
        // rango.setSerie(serieLibre);
        // rango.setSolicitud(solLib);
        // rango.getId().setIdNir(serieLibre.getId().getIdNir());
        // rango.getId().setSna(serieLibre.getId().getSna());
        //
        // LOGGER.debug("Serie seleccionada: " + serieLibre.toString());
        // LOGGER.debug("Rango ficticio: " + rango.toString());
        // } else {
        // LOGGER.debug("No se han encontrado series libres para realizar las pruebas");
        // }
        // } catch (Exception e) {
        // LOGGER.debug("No se han podido inicializar las variables: " + e.getMessage());
        // }
    }

    /**
     * Método ejecutado después de cada test de la clase. Libera las variables y recursos de un test concreto.
     */
    @After
    public void tearDown() {
        // Servicio de Rangos y Series
        // try {
        // ISeriesService seriesService = (ISeriesService) getEjbBean(SeriesService.class);
        // INumeracionGeograficaService ngService =
        // (INumeracionGeograficaService) getEjbBean(NumeracionGeograficaService.class);
        //
        // // Eliminamos los rangos de pruebas asociados a la serie libre
        // if (!listaRangosTemporales.isEmpty()) {
        // for (RangoSerie rangoTemporal : listaRangosTemporales) {
        // seriesService.removeRangoSerie(rangoTemporal);
        // }
        // listaRangosTemporales.clear();
        // }
        //
        // // Eliminamos la solicitud de pruebas y todas sus dependencias
        // SolicitudLiberacionNg solLibEager = ngService.getSolicitudLiberacionEagerLoad(solLib);
        // if (solLibEager != null) {
        // // Dependencias marcadas como CascadetType.ALL para borrado en cascada
        // // ngService.removeSolicitudLiberacionCascade(solLibEager);
        // // ngService.removeSolicitudLiberacionCascadeForced(solLibEager);
        // // ngService.removeSolicitudLiberacion(solLibEager);
        // }
        //
        // } catch (Exception e) {
        // LOGGER.debug("No se han podido eliminar las variables: " + e.getMessage());
        // }
    }

    /**
     * Crea un objeto LiberacionSolicitadaNg en base a la información de un Rango.
     * @param pRango Rango con la información de la liberación.
     * @param pFechaLiberacion Fecha de Liberación del Rango
     * @return LiberacionSolicitadaNg
     * @throws Exception en caso de error.
     */
    // private LiberacionSolicitadaNg getLiberacionSolicitadaFromRango(RangoSerie pRango, Date pFechaLiberacion)
    // throws Exception {
    // LiberacionSolicitadaNg ls = new LiberacionSolicitadaNg();
    //
    // // Liberacion Solicitada
    // ls.setId(null); // Hasta que no se guarda no se obtiene un Id
    // ls.setIdNir(pRango.getSerie().getNir().getId());
    // ls.setSna(pRango.getSerie().getId().getSna());
    // ls.setCentralDestino(pRango.getCentralDestino());
    // ls.setCentralOrigen(pRango.getCentralOrigen());
    // ls.setNumInicio(pRango.getNumInicio().toString());
    // ls.setNumFinal(pRango.getNumFinal().toString());
    // ls.setPoblacion(pRango.getPoblacion());
    // ls.setProveedorCesionario(pRango.getAsignatario());
    // ls.setSolicitudLiberacion(solLib);
    // ls.setTipoModalidad(pRango.getTipoModalidad());
    // ls.setTipoRed(pRango.getTipoRed());
    // ls.setFechaLiberacion(pFechaLiberacion);
    //
    // // Estado Reservado para su posterior liberación.
    // EstadoLiberacionSolicitada els = new EstadoLiberacionSolicitada();
    // els.setCodigo(EstadoLiberacionSolicitada.PENDIENTE);
    // ls.setEstado(els);
    //
    // return ls;
    // }

    /** Test para el Servicio 'NumeracionGeograficaService.applyLiberacionesSolicitadas()'. */
    @Test
    public void liberarRangoExistenteFechaActual() {
        // LOGGER.info("Caso de Test: 'liberarRangoExistente'");
        // try {
        // ISeriesService seriesService = (ISeriesService) getEjbBean(SeriesService.class);
        // INumeracionGeograficaService ngService =
        // (INumeracionGeograficaService) getEjbBean(NumeracionGeograficaService.class);
        //
        // if (serieLibre == null || rango == null) {
        // Assert.fail("No hay información de serie o rango para realizar las pruebas");
        // } else {
        // // Agregamos el rango ficticio para poder liberarlo.
        // RangoSerie rangoGuardado = seriesService.saveRangoSerie(rango);
        //
        // // Liberación solicitada
        // LiberacionSolicitadaNg libSol = getLiberacionSolicitadaFromRango(rangoGuardado, new Date());
        // solLib.addLiberacionSolicitada(libSol);
        //
        // // Liberamos el rango
        // solLib = ngService.applyLiberacionesSolicitadas(solLib);
        //
        // // Comprobaciones Liberaciones Generadas
        // Assert.assertEquals(1, solLib.getLiberaciones().size());
        // Liberacion lib = solLib.getLiberaciones().get(0);
        // Assert.assertEquals(rango.getNumInicio(), lib.getInicioRango());
        // Assert.assertEquals(rango.getNumFinal(), lib.getFinRango());
        //
        // // Comprobaciones Liberaciones Solicitadas Generadas
        // Assert.assertEquals(1, solLib.getLiberacionesSolicitadas().size());
        // LiberacionSolicitadaNg libSolGen = solLib.getLiberacionesSolicitadas().get(0);
        // Assert.assertEquals(rango.getNumInicio(), libSolGen.getNumInicio());
        // Assert.assertEquals(rango.getNumFinal(), libSolGen.getNumFinal());
        // Assert.assertEquals(EstadoLiberacionSolicitada.LIBERADO, libSolGen.getEstado().getCodigo());
        //
        // // Comprobamos que no existe el rango, debería saltar una excepción
        // Assert.assertFalse(seriesService.existeRangoSerie(
        // rango.getId().getIdNir(),
        // rango.getId().getSna(),
        // rango.getNumInicio(),
        // rango.getAsignatario()));
        // }
        // } catch (Exception e) {
        // Assert.fail(e.getMessage());
        // }
    }

    /** Test para el Servicio 'NumeracionGeograficaService.applyLiberacionesSolicitadas()'. */
    @Test
    public void liberarRangoExistenteFechaDiferida() {
        // LOGGER.info("Caso de Test: 'liberarRangoExistenteFechaDiferida'");
        // try {
        // ISeriesService seriesService = (ISeriesService) getEjbBean(SeriesService.class);
        // INumeracionGeograficaService ngService =
        // (INumeracionGeograficaService) getEjbBean(NumeracionGeograficaService.class);
        //
        // if (serieLibre == null || rango == null) {
        // Assert.fail("No hay información de serie o rango para realizar las pruebas");
        // } else {
        // // Agregamos el rango ficticio para poder liberarlo.
        // RangoSerie rangoGuardado = seriesService.saveRangoSerie(rango);
        // listaRangosTemporales.add(rangoGuardado);
        //
        // // Fecha posterior al día actual
        // Calendar calendar = Calendar.getInstance();
        // calendar.add(Calendar.DAY_OF_MONTH, 1);
        //
        // // Liberación solicitada
        // LiberacionSolicitadaNg libSol = getLiberacionSolicitadaFromRango(rangoGuardado, calendar.getTime());
        // solLib.addLiberacionSolicitada(libSol);
        //
        // // Liberamos el rango
        // solLib = ngService.applyLiberacionesSolicitadas(solLib);
        //
        // // Comprobaciones Liberaciones Generadas
        // Assert.assertEquals(0, solLib.getLiberaciones().size());
        //
        // // Comprobaciones Liberaciones Solicitadas Generadas
        // Assert.assertEquals(1, solLib.getLiberacionesSolicitadas().size());
        // LiberacionSolicitadaNg libSolGen = solLib.getLiberacionesSolicitadas().get(0);
        // Assert.assertEquals(rango.getNumInicio(), libSolGen.getNumInicio());
        // Assert.assertEquals(rango.getNumFinal(), libSolGen.getNumFinal());
        // Assert.assertEquals(EstadoLiberacionSolicitada.PENDIENTE, libSolGen.getEstado().getCodigo());
        //
        // // Comprobamos que el rango aún existe y está pendiente de liberar
        // RangoSerie rangoPendiente = seriesService.getRangoSerie(
        // rango.getId().getIdNir(),
        // rango.getId().getSna(),
        // rango.getNumInicio(),
        // rango.getAsignatario());
        //
        // Assert.assertNotNull(rangoPendiente);
        // Assert.assertEquals(EstadoRango.RESERVADO, rangoPendiente.getEstadoRango().getCodigo());
        // }
        // } catch (Exception e) {
        // Assert.fail(e.getMessage());
        // }
    }

    /** Test para el Servicio 'NumeracionGeograficaService.applyLiberacionesSolicitadas()'. */
    @Test
    public void liberarRangoInexistente() {
        // LOGGER.info("Caso de Test: 'liberarRangoInexistente'");
        // try {
        // INumeracionGeograficaService ngService =
        // (INumeracionGeograficaService) getEjbBean(NumeracionGeograficaService.class);
        //
        // if (serieLibre == null || rango == null) {
        // Assert.fail("No hay información de serie o rango para realizar las pruebas");
        // } else {
        //
        // // Liberación solicitada
        // LiberacionSolicitadaNg libSol = getLiberacionSolicitadaFromRango(rango, new Date());
        // solLib.addLiberacionSolicitada(libSol);
        //
        // try {
        // // Liberamos el rango inexistente
        // solLib = ngService.applyLiberacionesSolicitadas(solLib);
        // } catch (Exception e) {
        // // Controlamos la excepción de NoResultException, que es la esperada.
        // if (e.getCause().getClass().equals(EJBTransactionRolledbackException.class)) {
        // if (e.getCause().getCause().getClass().equals(EJBTransactionRolledbackException.class)) {
        // if (!e.getCause().getCause().getCause().getClass().equals(NoResultException.class)) {
        // Assert.fail(e.getMessage());
        // }
        // } else {
        // Assert.fail(e.getMessage());
        // }
        // } else {
        // Assert.fail(e.getMessage());
        // }
        // }
        // }
        // } catch (Exception e) {
        // Assert.fail(e.getMessage());
        // }
    }

    /** Test para el Servicio 'NumeracionGeograficaService.applyLiberacionesSolicitadas()'. */
    @Test
    public void fraccionarRangoExistenteFechaActual() {
        // LOGGER.info("Caso de Test: 'fraccionarRangoExistenteFechaActual'");
        // try {
        // ISeriesService seriesService = (ISeriesService) getEjbBean(SeriesService.class);
        // INumeracionGeograficaService ngService =
        // (INumeracionGeograficaService) getEjbBean(NumeracionGeograficaService.class);
        //
        // if (serieLibre == null || rango == null) {
        // Assert.fail("No hay información de serie o rango para realizar las pruebas");
        // } else {
        // // Agregamos el rango ficticio para poder liberarlo.
        // RangoSerie rangoGuardado = seriesService.saveRangoSerie(rango);
        //
        // // Liberación, 100 números a partir del 600
        // RangoSerie liberacion = (RangoSerie) rangoGuardado.clone();
        // liberacion.setNumInicio("0600");
        // liberacion.setNumFinal("0699");
        // liberacion.setSolicitud(rangoGuardado.getSolicitud());
        //
        // // Liberación solicitada
        // LiberacionSolicitadaNg libSol = getLiberacionSolicitadaFromRango(liberacion, new Date());
        // solLib.addLiberacionSolicitada(libSol);
        //
        // // Fraccionamos el rango
        // solLib = ngService.applyLiberacionesSolicitadas(solLib);
        //
        // // Comprobaciones Liberaciones Generadas
        // Assert.assertEquals(1, solLib.getLiberaciones().size());
        // Liberacion lib = solLib.getLiberaciones().get(0);
        // Assert.assertEquals(liberacion.getNumInicio(), lib.getInicioRango());
        // Assert.assertEquals(liberacion.getNumFinal(), lib.getFinRango());
        //
        // // Comprobaciones Liberaciones Solicitadas Generadas
        // Assert.assertEquals(1, solLib.getLiberacionesSolicitadas().size());
        // LiberacionSolicitadaNg libSolGen = solLib.getLiberacionesSolicitadas().get(0);
        // Assert.assertEquals(liberacion.getNumInicio(), libSolGen.getNumInicio());
        // Assert.assertEquals(liberacion.getNumFinal(), libSolGen.getNumFinal());
        // Assert.assertEquals(EstadoLiberacionSolicitada.LIBERADO, libSolGen.getEstado().getCodigo());
        //
        // // Comprobamos que no existe el rango, debería saltar una excepción
        // RangoSerie rangoLiberado = null;
        // try {
        // rangoLiberado = seriesService.getRangoSerieByFraccion(
        // rango.getId().getIdNir(),
        // rango.getId().getSna(),
        // rango.getNumInicio(),
        // rango.getNumFinal(),
        // rango.getAsignatario());
        // } catch (Exception e) {
        // // Controlamos la excepción de NoResultException, que es la esperada.
        // if (e.getCause().getClass().equals(EJBTransactionRolledbackException.class)) {
        // if (!e.getCause().getCause().getClass().equals(NoResultException.class)) {
        // Assert.fail(e.getMessage());
        // }
        // } else {
        // Assert.fail(e.getMessage());
        // }
        // }
        //
        // // Comprobamos que existen las fracciones
        // RangoSerie fraccionA = seriesService.getRangoSerieByFraccion(
        // liberacion.getId().getIdNir(),
        // liberacion.getId().getSna(),
        // "0500", "0599",
        // rango.getAsignatario());
        //
        // Assert.assertNotNull(fraccionA);
        // listaRangosTemporales.add(fraccionA);
        // Assert.assertEquals(fraccionA.getNumInicioAsInt(), 500);
        // Assert.assertEquals(fraccionA.getNumFinalAsInt(), 599);
        //
        // RangoSerie fraccionB = seriesService.getRangoSerieByFraccion(
        // liberacion.getId().getIdNir(),
        // liberacion.getId().getSna(),
        // "0700", "0999",
        // rango.getAsignatario());
        //
        // Assert.assertNotNull(fraccionB);
        // listaRangosTemporales.add(fraccionB);
        // Assert.assertEquals(fraccionB.getNumInicioAsInt(), 700);
        // Assert.assertEquals(fraccionB.getNumFinalAsInt(), 999);
        // }
        // } catch (Exception e) {
        // Assert.fail(e.getMessage());
        // }
    }

    /** Test para el Servicio 'NumeracionGeograficaService.applyLiberacionesSolicitadas()'. */
    @Test
    public void fraccionarRangoExistenteFechaDiferida() {
        // LOGGER.info("Caso de Test: 'fraccionarRangoExistenteFechaDiferida'");
        // try {
        // ISeriesService seriesService = (ISeriesService) getEjbBean(SeriesService.class);
        // INumeracionGeograficaService ngService =
        // (INumeracionGeograficaService) getEjbBean(NumeracionGeograficaService.class);
        //
        // if (serieLibre == null || rango == null) {
        // Assert.fail("No hay información de serie o rango para realizar las pruebas");
        // } else {
        // // Agregamos el rango ficticio para poder liberarlo.
        // RangoSerie rangoGuardado = seriesService.saveRangoSerie(rango);
        // listaRangosTemporales.add(rangoGuardado);
        //
        // // Fracción, 100 números a partir del 600
        // RangoSerie liberacion = (RangoSerie) rangoGuardado.clone();
        // liberacion.setNumInicio("0600");
        // liberacion.setNumFinal("0699");
        // liberacion.setSolicitud(rangoGuardado.getSolicitud());
        //
        // // Fecha posterior al día actual
        // Calendar calendar = Calendar.getInstance();
        // calendar.add(Calendar.DAY_OF_MONTH, 1);
        //
        // // Liberación solicitada
        // LiberacionSolicitadaNg libSol = getLiberacionSolicitadaFromRango(liberacion, calendar.getTime());
        // solLib.addLiberacionSolicitada(libSol);
        //
        // // Fraccionamos el rango
        // solLib = ngService.applyLiberacionesSolicitadas(solLib);
        //
        // // Comprobaciones Liberaciones Generadas
        // Assert.assertEquals(0, solLib.getLiberaciones().size());
        //
        // // Comprobaciones Liberaciones Solicitadas Generadas
        // Assert.assertEquals(1, solLib.getLiberacionesSolicitadas().size());
        // LiberacionSolicitadaNg libSolGen = solLib.getLiberacionesSolicitadas().get(0);
        // Assert.assertEquals(liberacion.getNumInicio(), libSolGen.getNumInicio());
        // Assert.assertEquals(liberacion.getNumFinal(), libSolGen.getNumFinal());
        // Assert.assertEquals(EstadoLiberacionSolicitada.PENDIENTE, libSolGen.getEstado().getCodigo());
        //
        // // Comprobamos que existe el rango original aún
        // RangoSerie rangoPendiente = seriesService.getRangoSerie(
        // rango.getId().getIdNir(),
        // rango.getId().getSna(),
        // rango.getNumInicio(),
        // rango.getAsignatario());
        //
        // Assert.assertNotNull(rangoPendiente);
        // Assert.assertEquals(rangoPendiente.getNumInicioAsInt(), 500);
        // Assert.assertEquals(rangoPendiente.getNumFinalAsInt(), 999);
        // Assert.assertEquals(EstadoRango.RESERVADO, rangoPendiente.getEstadoRango().getCodigo());
        // }
        // } catch (Exception e) {
        // Assert.fail(e.getMessage());
        // }
    }

    /** Test para el Servicio 'NumeracionGeograficaService.applyLiberacionesSolicitadas()'. */
    @Test
    public void fraccionarRangoExistenteFechaActualPrimerSegmento() {
        // LOGGER.info("Caso de Test: 'fraccionarRangoExistenteFechaActualPrimerSegmento'");
        // try {
        // ISeriesService seriesService = (ISeriesService) getEjbBean(SeriesService.class);
        // INumeracionGeograficaService ngService =
        // (INumeracionGeograficaService) getEjbBean(NumeracionGeograficaService.class);
        //
        // if (serieLibre == null || rango == null) {
        // Assert.fail("No hay información de serie o rango para realizar las pruebas");
        // } else {
        // // Agregamos el rango ficticio para poder liberarlo.
        // RangoSerie rangoGuardado = seriesService.saveRangoSerie(rango);
        //
        // // Liberamos 200 números a partir del inicio del Rango
        // RangoSerie liberacion = (RangoSerie) rangoGuardado.clone();
        // liberacion.setNumInicio("0500");
        // liberacion.setNumFinal("0699");
        // liberacion.setSolicitud(rangoGuardado.getSolicitud());
        //
        // // Liberación solicitada
        // LiberacionSolicitadaNg libSol = getLiberacionSolicitadaFromRango(liberacion, new Date());
        // solLib.addLiberacionSolicitada(libSol);
        //
        // // Fraccionamos el rango
        // solLib = ngService.applyLiberacionesSolicitadas(solLib);
        //
        // // Comprobaciones Liberaciones Generadas
        // Assert.assertEquals(1, solLib.getLiberaciones().size());
        // Liberacion lib = solLib.getLiberaciones().get(0);
        // Assert.assertEquals(liberacion.getNumInicio(), lib.getInicioRango());
        // Assert.assertEquals(liberacion.getNumFinal(), lib.getFinRango());
        //
        // // Comprobaciones Liberaciones Solicitadas Generadas
        // Assert.assertEquals(1, solLib.getLiberacionesSolicitadas().size());
        // LiberacionSolicitadaNg libSolGen = solLib.getLiberacionesSolicitadas().get(0);
        // Assert.assertEquals(liberacion.getNumInicio(), libSolGen.getNumInicio());
        // Assert.assertEquals(liberacion.getNumFinal(), libSolGen.getNumFinal());
        // Assert.assertEquals(EstadoLiberacionSolicitada.LIBERADO, libSolGen.getEstado().getCodigo());
        //
        // // Comprobamos que no existe el rango, debería saltar una excepción
        // RangoSerie rangoLiberado = null;
        // try {
        // rangoLiberado = seriesService.getRangoSerieByFraccion(
        // rango.getId().getIdNir(),
        // rango.getId().getSna(),
        // rango.getNumInicio(),
        // rango.getNumFinal(),
        // rango.getAsignatario());
        // } catch (Exception e) {
        // // Controlamos la excepción de NoResultException, que es la esperada.
        // if (e.getCause().getClass().equals(EJBTransactionRolledbackException.class)) {
        // if (!e.getCause().getCause().getClass().equals(NoResultException.class)) {
        // Assert.fail(e.getMessage());
        // }
        // } else {
        // Assert.fail(e.getMessage());
        // }
        // }
        //
        // Assert.assertNull(rangoLiberado);
        //
        // // Comprobamos que existe la fracción resultante de la liberación
        // RangoSerie fraccion = seriesService.getRangoSerieByFraccion(
        // liberacion.getId().getIdNir(),
        // liberacion.getId().getSna(),
        // "0700", "0999",
        // rango.getAsignatario());
        //
        // Assert.assertNotNull(fraccion);
        // listaRangosTemporales.add(fraccion);
        // Assert.assertEquals(fraccion.getNumInicioAsInt(), 700);
        // Assert.assertEquals(fraccion.getNumFinalAsInt(), 999);
        // }
        // } catch (Exception e) {
        // Assert.fail(e.getMessage());
        // }
    }

    /** Test para el Servicio 'NumeracionGeograficaService.applyLiberacionesSolicitadas()'. */
    @Test
    public void fraccionarRangoExistenteFechaActualUltimoSegmento() {
        // LOGGER.info("Caso de Test: 'fraccionarRangoExistenteFechaActualUltimoSegmento'");
        // try {
        // ISeriesService seriesService = (ISeriesService) getEjbBean(SeriesService.class);
        // INumeracionGeograficaService ngService =
        // (INumeracionGeograficaService) getEjbBean(NumeracionGeograficaService.class);
        //
        // if (serieLibre == null || rango == null) {
        // Assert.fail("No hay información de serie o rango para realizar las pruebas");
        // } else {
        // // Agregamos el rango ficticio para poder liberarlo.
        // RangoSerie rangoGuardado = seriesService.saveRangoSerie(rango);
        //
        // // Liberamos 100 números hasta completar el final de rango
        // RangoSerie liberacion = (RangoSerie) rangoGuardado.clone();
        // liberacion.setNumInicio("0800");
        // liberacion.setNumFinal("0999");
        // liberacion.setSolicitud(rangoGuardado.getSolicitud());
        //
        // // Liberación solicitada
        // LiberacionSolicitadaNg libSol = getLiberacionSolicitadaFromRango(liberacion, new Date());
        // solLib.addLiberacionSolicitada(libSol);
        //
        // // Fraccionamos el rango
        // solLib = ngService.applyLiberacionesSolicitadas(solLib);
        //
        // // Comprobaciones Liberaciones Generadas
        // Assert.assertEquals(1, solLib.getLiberaciones().size());
        // Liberacion lib = solLib.getLiberaciones().get(0);
        // Assert.assertEquals(liberacion.getNumInicio(), lib.getInicioRango());
        // Assert.assertEquals(liberacion.getNumFinal(), lib.getFinRango());
        //
        // // Comprobaciones Liberaciones Solicitadas Generadas
        // Assert.assertEquals(1, solLib.getLiberacionesSolicitadas().size());
        // LiberacionSolicitadaNg libSolGen = solLib.getLiberacionesSolicitadas().get(0);
        // Assert.assertEquals(liberacion.getNumInicio(), libSolGen.getNumInicio());
        // Assert.assertEquals(liberacion.getNumFinal(), libSolGen.getNumFinal());
        // Assert.assertEquals(EstadoLiberacionSolicitada.LIBERADO, libSolGen.getEstado().getCodigo());
        //
        // // Comprobamos que no existe el rango, debería saltar una excepción
        // RangoSerie rangoLiberado = null;
        // try {
        // rangoLiberado = seriesService.getRangoSerieByFraccion(
        // rango.getId().getIdNir(),
        // rango.getId().getSna(),
        // rango.getNumInicio(),
        // rango.getNumFinal(),
        // rango.getAsignatario());
        // } catch (Exception e) {
        // // Controlamos la excepción de NoResultException, que es la esperada.
        // if (e.getCause().getClass().equals(EJBTransactionRolledbackException.class)) {
        // if (!e.getCause().getCause().getClass().equals(NoResultException.class)) {
        // Assert.fail(e.getMessage());
        // }
        // } else {
        // Assert.fail(e.getMessage());
        // }
        // }
        //
        // Assert.assertNull(rangoLiberado);
        //
        // // Comprobamos que existe la fracción resultante de la liberación
        // RangoSerie fraccion = seriesService.getRangoSerieByFraccion(
        // liberacion.getId().getIdNir(),
        // liberacion.getId().getSna(),
        // "0500", "0799",
        // rango.getAsignatario());
        //
        // Assert.assertNotNull(fraccion);
        // listaRangosTemporales.add(fraccion);
        // Assert.assertEquals(fraccion.getNumInicioAsInt(), 500);
        // Assert.assertEquals(fraccion.getNumFinalAsInt(), 799);
        // }
        // } catch (Exception e) {
        // Assert.fail(e.getMessage());
        // }
    }

    /** Test para el Servicio 'NumeracionGeograficaService.applyLiberacionesSolicitadas()'. */
    @Test
    public void fraccionarRangoInexistente() {
        // LOGGER.info("Caso de Test: 'fraccionarRangoInexistente'");
        // try {
        // INumeracionGeograficaService ngService =
        // (INumeracionGeograficaService) getEjbBean(NumeracionGeograficaService.class);
        //
        // if (serieLibre == null || rango == null) {
        // Assert.fail("No hay información de serie o rango para realizar las pruebas");
        // } else {
        //
        // // Liberación solicitada
        // LiberacionSolicitadaNg libSol = getLiberacionSolicitadaFromRango(rango, new Date());
        // solLib.addLiberacionSolicitada(libSol);
        //
        // try {
        // // Liberamos el rango inexistente
        // solLib = ngService.applyLiberacionesSolicitadas(solLib);
        // } catch (Exception e) {
        // // Controlamos la excepción de NoResultException, que es la esperada.
        // if (e.getCause().getClass().equals(EJBTransactionRolledbackException.class)) {
        // if (e.getCause().getCause().getClass().equals(EJBTransactionRolledbackException.class)) {
        // if (!e.getCause().getCause().getCause().getClass().equals(NoResultException.class)) {
        // Assert.fail(e.getMessage());
        // }
        // } else {
        // Assert.fail(e.getMessage());
        // }
        // } else {
        // Assert.fail(e.getMessage());
        // }
        // }
        // }
        // } catch (Exception e) {
        // Assert.fail(e.getMessage());
        // }
    }
}
