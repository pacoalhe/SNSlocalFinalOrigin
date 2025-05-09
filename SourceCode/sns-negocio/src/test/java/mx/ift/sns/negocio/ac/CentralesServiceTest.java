package mx.ift.sns.negocio.ac;

import mx.ift.sns.context.TestCaseContext;
import mx.ift.sns.modelo.central.Central;
import mx.ift.sns.modelo.central.Estatus;
import mx.ift.sns.modelo.central.Marca;
import mx.ift.sns.modelo.central.Modelo;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.negocio.centrales.CentralesService;
import mx.ift.sns.negocio.centrales.ICentralesService;
import mx.ift.sns.negocio.psts.IProveedoresService;
import mx.ift.sns.negocio.psts.ProveedoresService;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Casos unitarios para pruebas del servicio de centrales.
 */
public class CentralesServiceTest extends TestCaseContext {

    /** Logger de la clase . */
    private static final Logger LOGGER = LoggerFactory.getLogger(CentralesServiceTest.class);

    /** DAO de centrales. */
    private static ICentralesService centralService;

    /** DAO de proveedores. */
    private static IProveedoresService pstService;

    public CentralesServiceTest() throws Exception {
        super();
        centralService = (ICentralesService) getEjbBean(CentralesService.class);
        pstService = (IProveedoresService) getEjbBean(ProveedoresService.class);
    }

    /**
     * Comprobamos central.
     * @throws Exception
     */
    @Ignore
    @Test
    public void comprobarCentralTest() {
        try {
            Central central = new Central();
            central.setNombre("LA HUERTA");
            central.setProveedor(pstService.getProveedorByNombre("TELEFONOS DE MEXICO, S.A.B. DE C.V."));
            central.setLatitud("20°49'13\"");
            central.setLongitud("100°49'43\"");

            central = centralService.comprobarCentral(central);

            if (central.getId() != null) {
                LOGGER.info("Hay centrales:" + central.toString());
            } else {
                LOGGER.info("No hay centrales:" + central.toString());
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

    }
    
    @Test
    public void createCentralTest(){
    	
    	 try {
             Central central = new Central();
             central.setNombre("IECISA Central Test 1");
             central.setProveedor(pstService.getProveedorByNombre("TELEFONOS DE MEXICO, S.A.B. DE C.V."));
             central.setLatitud("20°49'13\"");
             central.setLongitud("100°49'43\"");
             Poblacion poblacion=new Poblacion();
             poblacion.setInegi("151180031");
             central.setPoblacion(poblacion);
             central.setEstatus(new Estatus());
             central.getEstatus().setCdg(Estatus.ACTIVO);
             
             Marca marca = new Marca();
             marca.setNombre("IECISA Marca");
             marca.setEstatus(new Estatus());
             marca.getEstatus().setCdg(Estatus.ACTIVO);
                         
             Modelo modelo = new Modelo();
             modelo.setTipoModelo("IECISA Modelo");
             modelo.setDescripcion("IECISA Modelo");
             modelo.setEstatus(new Estatus());
             modelo.getEstatus().setCdg(Estatus.ACTIVO);
            
             modelo.setMarca(marca);
             central.setModelo(modelo);
             
             central = centralService.saveCentralFromAsignacion(central);

             
         } catch (Exception e) {
             LOGGER.error(e.getMessage());
         }
    	
    }

}
