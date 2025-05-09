package mx.ift.sns.negocio.ng;

import mx.ift.sns.context.TestCaseContext;

import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * Casos unitarios para pruebas del sericio de Consultas.
 */
public class GeneracionConsultaSolicitudesServiceTest extends TestCaseContext {

    // /** // Se inicializa a false, pare comprobar que se llama al metodo inicializa(). */
    // private static boolean valor = false;
    //
    // /** Logger de la clase. */
    // private static final Logger LOGGER = LoggerFactory.getLogger(GeneracionConsultaSolicitudesServiceTest.class);
    //
    // /** consecutivo. */
    // private static String consecutivo;
    //
    // /** número de oficio. */
    // private static String referenciaSolicitud;
    //
    // /** Combo proveedor. */
    // private static Proveedor proveedorSeleccionado;
    // /** Combo estado. */
    // private static EstadoSolicitud estadoSeleccionado;
    //
    // /** campo fecha Solicitud. */
    // private static Date fchSolicitud;
    // /** campo fecha Asignación. */
    // private static Date fchIniUtilizacion;
    //
    // /** lista proveedor. **/
    // private static List<Proveedor> listaProveedores;
    // /** lista estados. **/
    // private static List<EstadoSolicitud> listaEstados;
    // /** lista asignacion. **/
    // private static List<SolicitudAsignacion> listaAsignacion;
    //
    // public List<SolicitudAsignacion> getListaAsignacion() {
    // return listaAsignacion;
    // }
    //
    // public void setListaAsignacion(List<SolicitudAsignacion> listaAsignacion) {
    // this.listaAsignacion = listaAsignacion;
    // }

    /** Constructor, inicializa la clase de la que se hereda. */
    public GeneracionConsultaSolicitudesServiceTest() {
        // IMPORTANTE: Es necesario llamar al constructor de la clase
        // 'TestCaseContext' para que se inicialice el Entorno
        super();
    }

    /**
     * Método ejecutado antes de todos los test de clase. Prepara las variables comunes a los test
     * @throws Exception ex
     */
    @BeforeClass
    public static void inicializarParametros() throws Exception {
        // // Valores por defecto para todas las pruebas de oficios
        // LOGGER.info("Setting Up Parameters:");
        // FiltroBusquedaSolicitudes filtros = new FiltroBusquedaSolicitudes();
        //
        // INumeracionGeograficaService numService =
        // (INumeracionGeograficaService) getEjbBean(NumeracionGeograficaService.class);
        //
        // listaAsignacion = numService.findAllSolicitudesAsignacion(filtros);
        // listaProveedores = numService.findAllProveedoresActivos();
        // listaEstados = numService.findAllEstadosSolicitud();
        // valor = true;
        //
        // SimpleDateFormat formatoDelTexto = new SimpleDateFormat("yyyy/MM/dd");
        //
        // String strfchIniUtilizacion = "2007/12/2005";
        // String strfchSolicitud = "10/03/2012";
        //
        // fchIniUtilizacion = formatoDelTexto.parse(strfchIniUtilizacion);
        // fchSolicitud = formatoDelTexto.parse(strfchSolicitud);
        // consecutivo = "123456789";
        // referenciaSolicitud = "CFT/D04/AGIT/DGPFT/3665/00" +
        // String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        //
        // LOGGER.info("Parameter 'consecutivo': " + consecutivo);
        // LOGGER.info("Parameter 'numOficio'  : " + referenciaSolicitud);
        //
    }

    /** Método ejecutado después de todos los test de clase. Libera las variables y recursos comunes a los test */
    @AfterClass
    public static void liberarParametros() {

    }

    // /**
    // * Método para buscar consultas.
    // * @throws Exception
    // **/
    // @Test
    // public void buscarConsultas() throws Exception {
    // long consulta = 0;
    // INumeracionGeograficaService service;
    //
    // try {
    //
    // LOGGER.info("Caso de Test: 'buscarConsultas'");
    //
    // // Este test falla si no se ha llamado a inicializar()
    // assertEquals(valor, true);
    // // Servicio
    //
    // FiltroBusquedaSolicitudes filtros = new FiltroBusquedaSolicitudes();
    //
    // filtros.setConsecutivo(consecutivo);
    // filtros.setFechaSolicitud(fchSolicitud);
    // filtros.setFechaUtilizacion(fchIniUtilizacion);
    // filtros.setReferenciaSolicitud(referenciaSolicitud);
    //
    // assertEquals(filtros.getConsecutivo(), consecutivo);
    // assertEquals(filtros.getFechaSolicitud(), fchSolicitud);
    // assertEquals(filtros.getFechaUtilizacion(), fchIniUtilizacion);
    // assertEquals(filtros.getReferenciaSolicitud(), referenciaSolicitud);
    //
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }

    // @Test
    // public void ComprobarListas() throws Exception {
    //
    // /* comento esto pq no funciona */
    // // assertFalse(listaAsignacion.isEmpty());
    // // assertFalse(listaEstados.isEmpty());
    // // assertFalse(listaProveedores.isEmpty());
    //
    // }
}
