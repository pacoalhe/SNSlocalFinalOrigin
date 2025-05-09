package mx.ift.sns.web.backend.reporteador;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.negocio.IReporteadorFacade;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Clase de soporte para el reporteador de NNG. */
@ManagedBean(name = "reporteadorNNGBean")
@ViewScoped
public class ReporteadorNNGBean implements Serializable {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ReporteadorNNGBean.class);

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Id del campo de errores. */
    private static final String MSG_ID = "MSG_BuscadorNNG";

    /** Facade de servicios Administración de catálogo. */
    @EJB(mappedName = "ReporteadorFacade")
    private IReporteadorFacade reporteadorFacade;

    /** Pst seleccionada. */
    private Proveedor pstSeleccionada;

    /** Lista de psts disponibles para seleccionar. */
    private List<Proveedor> listaPst;

    /**
     * Iniciamos la pantalla cargando los combos.
     * @throws Exception error en inicio
     **/
    @PostConstruct
    public void init() throws Exception {

        LOGGER.debug("");

        try {
            pstSeleccionada = null;

            // Catálogo de Proveedores
            listaPst = reporteadorFacade.findAllProveedoresActivos();

        } catch (Exception ex) {
            LOGGER.error("error", ex);
        }
    }

    /**
     * Reseta los valores.
     */
    public void resetPantalla() {

        LOGGER.debug("");

        pstSeleccionada = null;

    }

    /**
     * Método que realiza el export a excel de los resultados de la consulta.
     * @return excel
     * @throws Exception Exception
     */
    public StreamedContent getExportarDatos() {
        StreamedContent fichero = null;
        // boolean hayError = false;
        try {
            LOGGER.debug("");

            InputStream stream = new ByteArrayInputStream(
                    reporteadorFacade.getReporteNNG(pstSeleccionada));
            String docName = "Numeración No Geográfica";

            docName = docName.concat(".xlsx");

            stream.close();

            LOGGER.debug("docname {}", docName);
            return new DefaultStreamedContent(stream,
                    "application/vnd.ms-excel", docName);

        } catch (Exception e) {
            LOGGER.debug("Error inesperado: " + e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
        return fichero;
    }

    /**
     * Pst seleccionada.
     * @return Proveedor
     */
    public Proveedor getPstSeleccionada() {
        return pstSeleccionada;
    }

    /**
     * Pst seleccionada.
     * @param pstSeleccionada Proveedor
     */
    public void setPstSeleccionada(Proveedor pstSeleccionada) {
        this.pstSeleccionada = pstSeleccionada;
    }

    /**
     * Lista de psts disponibles para seleccionar.
     * @return List<Proveedor>
     */
    public List<Proveedor> getListaPst() {
        return listaPst;
    }

    /**
     * Lista de psts disponibles para seleccionar.
     * @param listaPst List<Proveedor>
     */
    public void setListaPst(List<Proveedor> listaPst) {
        this.listaPst = listaPst;
    }

}
