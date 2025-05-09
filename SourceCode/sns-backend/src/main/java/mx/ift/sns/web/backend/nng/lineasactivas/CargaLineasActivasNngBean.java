package mx.ift.sns.web.backend.nng.lineasactivas;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import mx.ift.sns.negocio.nng.INumeracionNoGeograficaFacade;
import mx.ift.sns.web.backend.components.Wizard;

/**
 * Wizrad de carga de lineas activas.
 */
@ManagedBean(name = "cargaLineasActivasNngBean")
@ViewScoped
public class CargaLineasActivasNngBean extends Wizard implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Llamada a la clase de negocio. */
    @EJB(mappedName = "NumeracionNoGeograficaFacade")
    private INumeracionNoGeograficaFacade nnFacade;

    /** Pastaña para Cargar Archivo Lineas Activas. */
    private CargarArchivoLineasActivasNngTab tabCargaArchivo;

    /** Tamaño máximo permitido para los ficheros de lineas activas. */
    private String maxTamFichero;

    /**
     * Constructor.
     */
    public CargaLineasActivasNngBean() {
        setWizardMessagesId("wiz_cargaLineasActivas");
    }

    /**
     * PostConstruct.
     * @throws Exception error en init
     */
    @PostConstruct
    public void init() throws Exception {

        maxTamFichero = nnFacade.getParamByName("maxTamFicheroLineasActivas");

        tabCargaArchivo = new CargarArchivoLineasActivasNngTab(this, nnFacade);
        tabCargaArchivo.setId("Carga Lineas Activas - TabCargaArchivo");
        getPasosWizard().add(tabCargaArchivo);
    }

    @Override
    public void resetTabs() {
        tabCargaArchivo.resetTab();
    }

    /**
     * Pastaña para Cargar Archivo Lineas Activas.
     * @return tabCargaArchivo
     */
    public CargarArchivoLineasActivasNngTab getTabCargaArchivo() {
        return tabCargaArchivo;
    }

    /**
     * Pastaña para Cargar Archivo Lineas Activas.
     * @param tabCargaArchivo CargarArchivoLineasActivasNngTab
     */
    public void setTabCargaArchivo(CargarArchivoLineasActivasNngTab tabCargaArchivo) {
        this.tabCargaArchivo = tabCargaArchivo;
    }

    /**
     * Tamaño máximo permitido para los ficheros de lineas activas.
     * @return maxTamFichero
     */
    public String getMaxTamFichero() {
        return maxTamFichero;
    }

    /**
     * Tamaño máximo permitido para los ficheros de lineas activas.
     * @param maxTamFichero String
     */
    public void setMaxTamFichero(String maxTamFichero) {
        this.maxTamFichero = maxTamFichero;
    }
}
