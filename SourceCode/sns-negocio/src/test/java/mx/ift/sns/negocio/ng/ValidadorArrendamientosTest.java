package mx.ift.sns.negocio.ng;

import java.io.File;

import mx.ift.sns.context.TestCaseContext;
import mx.ift.sns.negocio.ng.model.ResultadoValidacionArrendamiento;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Casos unitarios para pruebas de generacion de plan de numeracion de ABD Presuscripcion.
 */
public class ValidadorArrendamientosTest extends TestCaseContext {

    /** Logger de la clase . */
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidadorArrendamientosTest.class);

    /** SErvicio. */
    private static IValidadorArchivosPNNABD servicio;

    /** path. */
    private String path;

    /**
     * Constructor, inicializa la clase de la que se hereda.
     * @throws Exception error
     */
    public ValidadorArrendamientosTest() throws Exception {
        // IMPORTANTE: Es necesario llamar al constructor de la clase
        // 'TestCaseContext' para que se inicialice el Entorno
        super();
        servicio = (IValidadorArchivosPNNABD) getEjbBean(ValidadorArchivosPNNABD.class);

        File f = new File("./");
        LOGGER.info("path=" + f.getAbsolutePath());

        path = f.getAbsolutePath() + "/";
    }

    /**
     * @throws Exception error
     */
    @Ignore
    @Test
    public void testFicherosNoValidos1() throws Exception {
        LOGGER.info("init");

        ResultadoValidacionArrendamiento res = servicio.validar("", "");

        Assert.assertTrue(res.isError());
        LOGGER.info("fin");
    }

    /**
     * @throws Exception error
     */
    @Ignore
    @Test
    public void testFicherosNoValidos2() throws Exception {
        LOGGER.info("init");

        ResultadoValidacionArrendamiento res = servicio.validar("dfdf", "");

        Assert.assertTrue(res.isError());
        LOGGER.info("fin");
    }

    /**
     * @throws Exception error
     */
    @Ignore
    @Test
    public void testFicherosNoValidos3() throws Exception {
        LOGGER.info("init");

        ResultadoValidacionArrendamiento res = servicio
                .validar(path + "src/test/resources/ng/Arrendador20141114.csv", "");

        Assert.assertTrue(res.isError());
        LOGGER.info("fin");
    }

    /**
     * @throws Exception error
     */
    @Ignore
    @Test
    public void testFicherosValidos() throws Exception {
        LOGGER.info("init");

        ResultadoValidacionArrendamiento res = servicio.validar(path
                + "src/test/resources/ng/Arrendador20141114.csv",
                path + "src/test/resources/ng/Arrendador20141114.csv");

        Assert.assertTrue(res.isError());

        LOGGER.info("lista de errores {}", res.getListaRangosNoAsignados().size());

        for (int i = 0; i < res.getListaRangosNoAsignados().size(); i++) {
            LOGGER.info("{}", res.getListaRangosNoAsignados().get(i));
        }

        LOGGER.info("fin");
    }

    /**
     * @throws Exception error
     */
    @Ignore
    @Test
    public void testFicheroCabeceraIncorrecta1() throws Exception {
        LOGGER.info("init");

        ResultadoValidacionArrendamiento res =
                servicio.validar(path + "src/test/resources/ng/cabecera_incorrecta1.csv",
                        path + "src/test/resources/ng/Arrendador20141114.csv");

        LOGGER.debug("ee {}", res.isError());
        Assert.assertTrue(res.isError());

        LOGGER.info("fin");
    }

    /**
     * @throws Exception error
     */
    @Ignore
    @Test
    public void testFicheroCabeceraIncorrecta2() throws Exception {
        LOGGER.info("init");

        ResultadoValidacionArrendamiento res = servicio.validar(path
                + "src/test/resources/ng/cabecera_incorrecta2.csv",
                path + "src/test/resources/ng/Arrendador20141114.csv");

        Assert.assertTrue(res.isError());

        LOGGER.info("fin");
    }

    /**
     * @throws Exception error
     */
    @Ignore
    @Test
    public void testFicherosValidos2() throws Exception {
        LOGGER.info("init");

        ResultadoValidacionArrendamiento res = servicio.validar(path + "src/test/resources/ng/arrendador_2.csv",
                path + "src/test/resources/ng/Arrendador20141114.csv");

        Assert.assertTrue(res.isError());

        LOGGER.info("lista de errores {}", res.getListaRangosNoAsignados().size());

        for (int i = 0; i < res.getListaRangosNoAsignados().size(); i++) {
            LOGGER.info("{}", res.getListaRangosNoAsignados().get(i));
        }

        LOGGER.info("fin");
    }

    /**
     * @throws Exception error
     */
    @Ignore
    @Test
    public void testFicherosValidos3() throws Exception {
        LOGGER.info("init");

        ResultadoValidacionArrendamiento res = servicio.validar(path + "src/test/resources/ng/arrendador_3.csv",
                path + "src/test/resources/ng/Arrendador20141114.csv");

        Assert.assertTrue(res.isError());

        LOGGER.info("lista de errores {}", res.getListaRangosNoAsignados().size());

        for (int i = 0; i < res.getListaRangosNoAsignados().size(); i++) {
            LOGGER.info("{}", res.getListaRangosNoAsignados().get(i));
        }

        LOGGER.info("fin");
    }

    /**
     * @throws Exception error
     */
    @Ignore
    @Test
    public void testFicherosValidos4() throws Exception {
        LOGGER.info("init");

        ResultadoValidacionArrendamiento res = servicio.validar(path + "src/test/resources/ng/arrendatario4.csv",
                path + "src/test/resources/ng/arrendador_4.csv");

        Assert.assertFalse(res.isError());

        LOGGER.info("lista de errores {}", res.getListaRangosNoAsignados().size());

        for (int i = 0; i < res.getListaRangosNoAsignados().size(); i++) {
            LOGGER.info("{}", res.getListaRangosNoAsignados().get(i));
        }

        LOGGER.info("fin");
    }

    /**
     * @throws Exception error
     */
    @Ignore
    @Test
    public void testFicherosValidos5() throws Exception {
        LOGGER.info("init");

        ResultadoValidacionArrendamiento res = servicio.validar(path + "src/test/resources/ng/arrendatario2.csv",
                path + "src/test/resources/ng/arrendador_2.csv");

        Assert.assertFalse(res.isError());

        LOGGER.info("lista de errores {}", res.getListaRangosNoAsignados().size());

        for (int i = 0; i < res.getListaRangosNoAsignados().size(); i++) {
            LOGGER.info("{}", res.getListaRangosNoAsignados().get(i));
        }

        LOGGER.info("fin");
    }

    /**
     * @throws Exception error
     */
    @Ignore
    @Test
    public void testFicherosValidos6() throws Exception {
        LOGGER.info("init");

        ResultadoValidacionArrendamiento res = servicio.validar(path + "src/test/resources/ng/arrendatario4.csv",
                path + "src/test/resources/ng/arrendador_6.csv");

        Assert.assertFalse(res.isError());

        LOGGER.info("lista de errores {}", res.getListaRangosNoAsignados().size());

        for (int i = 0; i < res.getListaRangosNoAsignados().size(); i++) {
            LOGGER.info("{}", res.getListaRangosNoAsignados().get(i));
        }

        LOGGER.info("fin");
    }

    /**
     * @throws Exception error
     */
    @Ignore
    @Test
    public void testFicherosValidos7() throws Exception {
        LOGGER.info("init");

        ResultadoValidacionArrendamiento res = servicio.validar(path + "src/test/resources/ng/arrendatario4.csv",
                path + "src/test/resources/ng/arrendador_7.csv");

        imprimeErrores(res);

        LOGGER.info("fin");

        // Assert.assertFalse(res.isError());
    }

    public void imprimeErrores(ResultadoValidacionArrendamiento res) {

        LOGGER.info("errores no asignados {}", res.getListaRangosNoAsignados().size());
        for (int i = 0; i < res.getListaRangosNoAsignados().size(); i++) {
            LOGGER.info("{}", res.getListaRangosNoAsignados().get(i));
        }

        LOGGER.info("errores comparacion {}", res.getListaErroresComparacion().size());

        for (int i = 0; i < res.getListaErroresComparacion().size(); i++) {
            LOGGER.info("{}", res.getListaErroresComparacion().get(i));
        }

        LOGGER.info("fin");
    }
}
