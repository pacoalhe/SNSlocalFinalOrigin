package mx.ift.sns.web.backend.ng.lineasactivas;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import mx.ift.sns.negocio.ng.INumeracionGeograficaService;
import mx.ift.sns.web.backend.components.Wizard;

/**
 * Wizrad de carga de lineas activas.
 */
@ManagedBean(name = "cargaLineasActivasBean")
@ViewScoped
public class CargaLineasActivasWizard extends Wizard implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Llamada a la clase de negocio. */
    @EJB(mappedName = "NumeracionGeograficaService")
    private INumeracionGeograficaService ngService;

    /** Pestaña Cargar Archivo Lineas Activas . */
    private CargarArchivoLineasActivasNgTab tabCargaArchivo;

    /** Tamaño máximo permitido para los ficheros de lineas activas. */
    private String maxTamFichero;

    /**
     * Constructor.
     */
    public CargaLineasActivasWizard() {
        setWizardMessagesId("wiz_cargaLineasActivas");
    }

    /**
     * PostConstruct.
     * @throws Exception error en init
     */
    @PostConstruct
    public void init() throws Exception {

        maxTamFichero = ngService.getParamByName("maxTamFicheroLineasActivas");

        tabCargaArchivo = new CargarArchivoLineasActivasNgTab(this, ngService);
        tabCargaArchivo.setId("Carga Lineas Activas - TabCargaArchivo");
        getPasosWizard().add(tabCargaArchivo);
    }

    @Override
    public void resetTabs() {
        super.resetTabs();
    }

    /**
     * Pastaña para Cargar Archivo Lineas Activas.
     * @return tabCargaArchivo
     */
    public CargarArchivoLineasActivasNgTab getTabCargaArchivo() {
        return tabCargaArchivo;
    }

    /**
     * Pastaña para Cargar Archivo Lineas Activas.
     * @param tabCargaArchivo CargarArchivoLineasActivasNngTab
     */
    public void setTabCargaArchivo(CargarArchivoLineasActivasNgTab tabCargaArchivo) {
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
