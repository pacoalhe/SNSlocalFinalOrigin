package mx.ift.sns.negocio.ng;

import mx.ift.sns.context.TestCaseContext;

import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * Casos unitarios para pruebas del serivio de Oficios.
 */
public class OficiosServiceTest extends TestCaseContext {

    // /** Logger de la clase . */
    // private static final Logger LOGGER = LoggerFactory.getLogger(OficiosServiceTest.class);
    //
    // /** Solicitud. */
    // private static SolicitudLiberacionNg solicitud;
    //
    // /** Tipo de Destinatario. */
    // private static TipoDestinatario tipoDestinatario;
    //
    // /** Servicio para la generación de oficios. */
    // private static String numOficio = "";

    /** Constructor, inicializa la clase de la que se hereda. */
    public OficiosServiceTest() {
        // IMPORTANTE: Es necesario llamar al constructor de la clase
        // 'TestCaseContext' para que se inicialice el Entorno
        super();
    }

    /** Método ejecutado antes de todos los test de clase. Prepara las variables comunes a los test. */
    @BeforeClass
    public static void inicializarParametros() {
        // // Valores por defecto para todas las pruebas de oficios
        // LOGGER.info("Setting Up Parameters:");
        //
        // // Solicitud
        // TipoSolicitud tipoSolicitud = new TipoSolicitud();
        // tipoSolicitud.setCdg(TipoSolicitud.LIBERACION);
        //
        // EstadoSolicitud estatus = new EstadoSolicitud();
        // estatus.setCodigo(EstadoSolicitud.SOLICITUD_EN_TRAMITE);
        //
        // // Proveedor existente en la BBDD
        // Proveedor proveedor = new Proveedor();
        // proveedor.setId(new BigDecimal(55));
        //
        // solicitud = new SolicitudLiberacionNg();
        // solicitud.setTipoSolicitud(tipoSolicitud);
        // solicitud.setEstadoSolicitud(estatus);
        // solicitud.setId(new BigDecimal(100));
        // solicitud.setFechaIniPruebas(new Date());
        // solicitud.setFechaIniUtilizacion(new Date());
        // solicitud.setFechaSolicitud(new Date());
        // solicitud.setProveedorSolicitante(proveedor);
        // solicitud.setReferencia("123456789");
        //
        // // Tipo de Destinatario ficticio.
        // tipoDestinatario = new TipoDestinatario();
        // tipoDestinatario.setCdg(TipoDestinatario.PST_SOLICITANTE);
        //
        // // Numero de Oficio ficticio.
        // numOficio = "SDC/YHTF/FRDC/00100/" + String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        //
        // LOGGER.info("Parameter 'solicitud'  : " + solicitud.toString());
        // LOGGER.info("Parameter 'destinatario' : " + tipoDestinatario);
        // LOGGER.info("Parameter 'numOficio'    : " + numOficio);
    }

    /** Método ejecutado después de todos los test de clase. Libera las variables y recursos comunes a los test */
    @AfterClass
    public static void liberarParametros() {
    }

    // /** Test para el Servicio 'GeneracionOficioService.crearOficio()'. */
    // @Test
    // @Ignore
    // public void crearOficioConPlantillaExistente() {
    // LOGGER.info("Caso de Test: 'crearOficioConPlantillaExistente'");
    // IOficiosService goService = null;
    // INumeracionGeograficaService ngService = null;
    // SolicitudLiberacionNg solLib = null;
    // Oficio result = null;
    // try {
    // // Servicio
    // goService = (IOficiosService) getEjbBean(OficiosService.class);
    // ngService = (INumeracionGeograficaService) getEjbBean(NumeracionGeograficaService.class);
    //
    // // Primero creamos una Solicitud que albergue el Oficio
    // solLib = ngService.saveSolicitudLiberacion(solicitud);
    //
    // // Guardamos la plantilla serializada en BBDD
    // byte[] plantillaExpected = goService.descargarPlantilla(solLib.getTipoSolicitud(), tipoDestinatario);
    //
    // // Oficio esperado
    // Oficio expected = new Oficio();
    // expected.setSolicitud(solLib);
    // expected.setTipoDestinatario(tipoDestinatario);
    // expected.setNumOficio(numOficio);
    // expected.setFechaOficio(new Date());
    // expected.setDocumento(plantillaExpected);
    //
    // // Oficio creado
    // result = goService.crearOficio(numOficio, solLib, tipoDestinatario, new Date(), "");
    //
    // // Validaciones
    // Assert.assertEquals(expected.getTipoDestinatario().getCdg(), result.getTipoDestinatario().getCdg());
    // Assert.assertEquals(expected.getSolicitud().getId(), result.getSolicitud().getId());
    // Assert.assertEquals(expected.getNumOficio(), result.getNumOficio());
    // Assert.assertNotNull(result.getFechaOficio());
    // Assert.assertArrayEquals(expected.getDocumento(), result.getDocumento());
    //
    // } catch (Exception ex) {
    // Assert.fail(ex.getMessage());
    // } finally {
    // // Tareas de limpieza
    // if (goService != null && ngService != null) {
    // // Eliminamos el oficio de pruebas generado
    // try {
    // goService.eliminarOficio(result);
    // } catch (Exception ex) {
    // }
    // // Eliminamos la Solicitud de Pruebas
    // try {
    // ngService.removeSolicitudLiberacion(solLib);
    // } catch (Exception ex) {
    // }
    // }
    // }
    // }

    // /** Test para el Servicio 'GeneracionOficioService.crearOficio()'. */
    // @Test(expected = java.lang.AssertionError.class)
    // @Ignore
    // public void crearOficioConPlantillaInexistente() {
    // LOGGER.info("Caso de Test: 'crearOficioConPlantillaInexistente'");
    // try {
    // // Servicio
    // IOficiosService goService = (IOficiosService) getEjbBean(OficiosService.class);
    //
    // // Oficio esperado
    // Oficio expected = new Oficio();
    // expected.setSolicitud(solicitud);
    // expected.setTipoDestinatario(tipoDestinatario);
    // expected.setNumOficio(numOficio);
    // expected.setFechaOficio(new Date());
    // expected.setDocumento(null);
    //
    // TipoDestinatario tipoInexistente = new TipoDestinatario();
    // tipoInexistente.setCdg(TipoDestinatario.JUNIT_TEST);
    //
    // // Oficio creado
    // goService.crearOficio(numOficio, solicitud, tipoInexistente, new Date(), "");
    //
    // } catch (Exception ex) {
    // Assert.fail(ex.getMessage());
    // }
    // }

    // /** Test para el Servicio 'GeneracionOficioService.actualizarOficio()'. */
    // @Test
    // @Ignore
    // public void actualizarOficioExistente() {
    // LOGGER.info("Caso de Test: 'actualizarPlantillaOficioExistente'");
    // IOficiosService goService = null;
    // INumeracionGeograficaService ngService = null;
    // SolicitudLiberacionNg solLib = null;
    // Oficio result = null;
    // try {
    // // Servicio
    // goService = (IOficiosService) getEjbBean(OficiosService.class);
    // ngService = (INumeracionGeograficaService) getEjbBean(NumeracionGeograficaService.class);
    //
    // // Documento para Actualizar
    // InputStream stream = this.getClass().getResourceAsStream("/ng/Machote-oficio-word.docx");
    // if (stream == null) {
    // Assert.fail("Resorce '/resources/ng/Machote-oficio-word.docx' not found");
    // }
    // byte[] documentoExpected = this.serializarFichero(stream);
    //
    // // Primero creamos una Solicitud que albergue el Oficio
    // solLib = ngService.saveSolicitudLiberacion(solicitud);
    //
    // // Creamos un oficio con la plantilla del documento por defecto
    // result = goService.crearOficio(numOficio, solLib, tipoDestinatario, new Date(), "");
    //
    // // Actualizamos el oficio con el nuevo documento
    // result.setDocumento(documentoExpected);
    // result.setFechaActualizacion(new Date());
    // goService.actualizarOficio(result, "");
    //
    // // Oficio esperado
    // Oficio expected = new Oficio();
    // expected.setSolicitud(solLib);
    // expected.setTipoDestinatario(tipoDestinatario);
    // expected.setNumOficio(numOficio);
    // expected.setFechaOficio(new Date());
    // expected.setDocumento(documentoExpected);
    // expected.setFechaActualizacion(new Date());
    //
    // // Validaciones
    // Assert.assertEquals(expected.getTipoDestinatario().getCdg(), result.getTipoDestinatario().getCdg());
    // Assert.assertEquals(expected.getSolicitud().getId(), result.getSolicitud().getId());
    // Assert.assertEquals(expected.getNumOficio(), result.getNumOficio());
    // Assert.assertNotNull(result.getFechaOficio());
    // Assert.assertNotNull(result.getFechaActualizacion());
    // Assert.assertArrayEquals(expected.getDocumento(), result.getDocumento());
    //
    // } catch (Exception ex) {
    // Assert.fail(ex.getMessage());
    // } finally {
    // // Tareas de limpieza
    // if (goService != null && ngService != null) {
    // // Eliminamos el oficio de pruebas generado
    // try {
    // goService.eliminarOficio(result);
    // } catch (Exception ex) {
    // }
    // // Eliminamos la Solicitud de Pruebas
    // try {
    // ngService.removeSolicitudLiberacion(solLib);
    // } catch (Exception ex) {
    // }
    // }
    // }
    // }

    // /**
    // * Serializa un InputStream transformándolo en un byte[].
    // * @param pFlujo Objeto de Entrada
    // * @return byte[]
    // * @throws Exception En caso de error
    // */
    // private byte[] serializarFichero(InputStream pFlujo) throws Exception {
    // ByteArrayOutputStream baos = new ByteArrayOutputStream();
    // byte[] buffer = new byte[1024];
    // int length;
    //
    // try {
    // while ((length = pFlujo.read(buffer)) != -1) {
    // baos.write(buffer, 0, length);
    // }
    // } finally {
    // if (pFlujo != null) {
    // pFlujo.close();
    // }
    // }
    // return baos.toByteArray();
    // }

    // /** Agrega Plantillas. */
    // @Test
    // public void agregaPlantillas() {
    // byte[] expected = null;
    // InputStream stream = null;
    // try {
    // // Servicio
    // IOficiosService goService = (IOficiosService) getEjbBean(OficiosService.class);
    // INumeracionGeograficaService ngService =
    // (INumeracionGeograficaService) getEjbBean(NumeracionGeograficaService.class);
    //
    // TipoSolicitud tipoSolicitudAsignacion = new TipoSolicitud();
    // tipoSolicitudAsignacion.setCdg(TipoSolicitud.ASIGNACION);
    //
    // TipoSolicitud tipoSolicitudRedistribucion = new TipoSolicitud();
    // tipoSolicitudRedistribucion.setCdg(TipoSolicitud.REDISTRIBUCION);
    //
    // TipoSolicitud tipoSolicitudLiberacion = new TipoSolicitud();
    // tipoSolicitudLiberacion.setCdg(TipoSolicitud.LIBERACION);
    //
    // TipoSolicitud tipoSolicitudCesion = new TipoSolicitud();
    // tipoSolicitudCesion.setCdg(TipoSolicitud.CESION_DERECHOS);
    //
    // TipoDestinatario tipoCedula = ngService.getTipoDestinatarioByCdg(TipoDestinatario.CEDULA_NOTIFICACION);
    // TipoDestinatario tipoActa = ngService.getTipoDestinatarioByCdg(TipoDestinatario.ACTA_CIRCUNSTANCIADA);
    // TipoDestinatario tipoPstSol = ngService.getTipoDestinatarioByCdg(TipoDestinatario.PST_SOLICITANTE);
    // TipoDestinatario tipoOtrosPsts = ngService.getTipoDestinatarioByCdg(TipoDestinatario.RESTO_PST);
    //
    // // Plantillas Solicitud de Asignacion
    // stream = this.getClass().getResourceAsStream("/ng/1_Asignacion_Solicitante.docx");
    // if (stream == null) {
    // Assert.fail("Resorce '/ng/1_Asignacion_Solicitante.docx not found");
    // }
    // expected = this.serializarFichero(stream);
    // goService.guardarPlantilla(tipoSolicitudAsignacion, tipoPstSol, expected,
    // "Asignación Numeración Geográfica PST Solicitante");
    //
    // stream = this.getClass().getResourceAsStream("/ng/22_Asignacion_Otros.docx");
    // if (stream == null) {
    // Assert.fail("Resorce '/ng/22_Asignacion_Otros.docx not found");
    // }
    // expected = this.serializarFichero(stream);
    // goService.guardarPlantilla(tipoSolicitudAsignacion, tipoOtrosPsts, expected,
    // "Asignación Numeración Geográfica Resto PSTs");
    //
    // stream = this.getClass().getResourceAsStream("/ng/49_Cedula.docx");
    // if (stream == null) {
    // Assert.fail("Resorce '/ng/49_Cedula.docx not found");
    // }
    // expected = this.serializarFichero(stream);
    // goService.guardarPlantilla(tipoSolicitudAsignacion, tipoCedula, expected,
    // "Asignación Numeración Geográfica Cédula Notificación");
    //
    // stream = this.getClass().getResourceAsStream("/ng/50_Acta.docx");
    // if (stream == null) {
    // Assert.fail("Resorce '/ng/50_Acta.docx not found");
    // }
    // expected = this.serializarFichero(stream);
    // goService.guardarPlantilla(tipoSolicitudAsignacion, tipoActa, expected,
    // "Asignación Numeración Acta Circunstanciada");
    //
    // // Plantillas Solicitud de Cesión
    // stream = this.getClass().getResourceAsStream("/ng/47-Cesion_Solicitante.docx");
    // if (stream == null) {
    // Assert.fail("Resorce '/ng/47-Cesion_Solicitante.docx not found");
    // }
    // expected = this.serializarFichero(stream);
    // goService.guardarPlantilla(tipoSolicitudCesion, tipoPstSol, expected,
    // "Cesión Derechos Numeración Geográfica PST Solicitante");
    //
    // stream = this.getClass().getResourceAsStream("/ng/48-Cesion_Otros.docx");
    // if (stream == null) {
    // Assert.fail("Resorce '/ng/48-Cesion_Otros.docx not found");
    // }
    // expected = this.serializarFichero(stream);
    // goService.guardarPlantilla(tipoSolicitudCesion, tipoOtrosPsts, expected,
    // "Cesión Derechos Numeración Geográfica Resto PSTs");
    //
    // stream = this.getClass().getResourceAsStream("/ng/49_Cedula_Cesion.docx");
    // if (stream == null) {
    // Assert.fail("Resorce '/ng/49_Cedula_Cesion.docx not found");
    // }
    // expected = this.serializarFichero(stream);
    // goService.guardarPlantilla(tipoSolicitudCesion, tipoCedula, expected,
    // "Cesión Derechos Numeración Geográfica Cédula Notificación");
    //
    // stream = this.getClass().getResourceAsStream("/ng/50_Acta_Cesion.docx");
    // if (stream == null) {
    // Assert.fail("Resorce '/ng/50_Acta_Cesion.docx not found");
    // }
    // expected = this.serializarFichero(stream);
    // goService.guardarPlantilla(tipoSolicitudCesion, tipoActa, expected,
    // "Cesión Derechos Numeración Geográfica Acta Circunstanciada");
    //
    // // Plantillas Solicitud de Liberación
    // stream = this.getClass().getResourceAsStream("/ng/45_Liberacion_Solicitante.docx");
    // if (stream == null) {
    // Assert.fail("Resorce '/ng/45_Liberacion_Solicitante.docx not found");
    // }
    // expected = this.serializarFichero(stream);
    // goService.guardarPlantilla(tipoSolicitudLiberacion, tipoPstSol, expected,
    // "Liberación Numeración Geográfica PST Solicitante");
    //
    // stream = this.getClass().getResourceAsStream("/ng/46-Liberacion_Otros.docx");
    // if (stream == null) {
    // Assert.fail("Resorce '/ng/46-Liberacion_Otros.docx not found");
    // }
    // expected = this.serializarFichero(stream);
    // goService.guardarPlantilla(tipoSolicitudLiberacion, tipoOtrosPsts, expected,
    // "Liberación Numeración Geográfica Resto PSTs");
    //
    // stream = this.getClass().getResourceAsStream("/ng/49_Cedula.docx");
    // if (stream == null) {
    // Assert.fail("Resorce '/ng/49_Cedula.docx not found");
    // }
    // expected = this.serializarFichero(stream);
    // goService.guardarPlantilla(tipoSolicitudLiberacion, tipoCedula, expected,
    // "Liberación Numeración Geográfica Cédula Notificación");
    //
    // stream = this.getClass().getResourceAsStream("/ng/50_Acta.docx");
    // if (stream == null) {
    // Assert.fail("Resorce '/ng/50_Acta.docx not found");
    // }
    // expected = this.serializarFichero(stream);
    // goService.guardarPlantilla(tipoSolicitudLiberacion, tipoActa, expected,
    // "Liberación Numeración Geográfica Acta Circunstanciada");
    //
    // // Plantillas Solicitud de Redistribución
    // stream = this.getClass().getResourceAsStream("/ng/7-Redistribucion_Solicitante.docx");
    // if (stream == null) {
    // Assert.fail("Resorce '/ng/7-Redistribucion_Solicitante.docx not found");
    // }
    // expected = this.serializarFichero(stream);
    // goService.guardarPlantilla(tipoSolicitudRedistribucion, tipoPstSol, expected,
    // "Redistribución Numeración Geográfica PST Solicitante");
    //
    // stream = this.getClass().getResourceAsStream("/ng/8-Redistribucion_Otros.docx");
    // if (stream == null) {
    // Assert.fail("Resorce '/ng/8-Redistribucion_Otros.docx not found");
    // }
    // expected = this.serializarFichero(stream);
    // goService.guardarPlantilla(tipoSolicitudRedistribucion, tipoOtrosPsts, expected,
    // "Redistribución Numeración Geográfica Resto PSTs");
    //
    // stream = this.getClass().getResourceAsStream("/ng/49_Cedula.docx");
    // if (stream == null) {
    // Assert.fail("Resorce '/ng/49_Cedula.docx not found");
    // }
    // expected = this.serializarFichero(stream);
    // goService.guardarPlantilla(tipoSolicitudRedistribucion, tipoCedula, expected,
    // "Redistribución Numeración Geográfica Cédula Notificación");
    //
    // stream = this.getClass().getResourceAsStream("/ng/50_Acta.docx");
    // if (stream == null) {
    // Assert.fail("Resorce '/ng/50_Acta.docx not found");
    // }
    // expected = this.serializarFichero(stream);
    // goService.guardarPlantilla(tipoSolicitudRedistribucion, tipoActa, expected,
    // "Redistribución Numeración Geográfica Acta Circunstanciada");
    //
    // } catch (Exception ex) {
    // Assert.fail(ex.getMessage());
    // }
    // }
}
