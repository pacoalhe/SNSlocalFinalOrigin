package mx.ift.sns.web.frontend.areas;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.series.Nir;
import mx.ift.sns.negocio.IConfiguracionFacade;
import mx.ift.sns.negocio.IConsultaPublicaFacade;
import mx.ift.sns.web.frontend.common.MensajesFrontBean;
import mx.ift.sns.web.frontend.util.PaginatorUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Muestra la tabla de las poblaciones con numeración asignada de un estado y busca la inforamción relacionada.
 * @author X51461MO
 */
@ManagedBean(name = "poblacionEstadoBean")
@ViewScoped
public class PoblacionEstadoBean implements Serializable {

    /** Servicio de Busqueda Publica. */
    @EJB(mappedName = "ConsultaPublicaFacade")
    private IConsultaPublicaFacade ngPublicService;

    /** Servicio de configuración Publica. */
    @EJB(mappedName = "ConfiguracionFacade")
    private IConfiguracionFacade ngConfiguracionService;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(PoblacionEstadoBean.class);

    /** Identificador del componente de mensajes JSF. */
    private static final String MSG_ID = "MSG_ConsultaFront";

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /**
     * Listado de poblaciones con numeración asignada por estado.
     */
    private List<Poblacion> poblacionEstado;
    /**
     * String con los distintos número de registros por página. Por defecto: 5,10,15,20,15.
     */
    private String numeroRegistros = "5, 10, 15, 20, 25";

    // /////////////////////////////////////MÉTODOS////////////////////////////////////////
    /**
     * Setea las poblaciones y sus nir.
     * @param poblacionEstado List<Poblacion>
     */
    public void setDetallePoblacionEstado(List<Poblacion> poblacionEstado) {
        try {
            int numeroRegistro = PaginatorUtil.getRegistrosPorPagina(ngConfiguracionService);
            PaginatorUtil.resetPaginacion("FORM_pobEstado:TBL_pobEst", numeroRegistro);
            this.setNumeroRegistros(PaginatorUtil.getGruposRegistrosPorPagina(numeroRegistro));
            this.setPoblacionEstado(poblacionEstado);
        } catch (Exception e) {
            LOGGER.error("Error inesperado" + e.getMessage());
            MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado");
        }

    }

    /**
     * Devuelve la lista de nirs por población.
     * @param pob Poblacion
     * @return List<Nir>
     */
    public List<Nir> nirPoblacion(Poblacion pob) {
        List<Nir> listaNirPoblacion = new ArrayList<Nir>();
        try {
            listaNirPoblacion.addAll(ngPublicService.findAllNirByPoblacion(pob));
        } catch (Exception e) {
            LOGGER.error("Error inesperado al buscar los nir de una población." + e.getMessage());
            MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado");
        }
        return listaNirPoblacion;
    }

    // ///////////////////////////GETTERS Y SETTERS/////////////////////////////////

    /**
     * Listado de poblaciones con numeración asignada por estado.
     * @return the poblacionEstado
     */
    public List<Poblacion> getPoblacionEstado() {
        return poblacionEstado;
    }

    /**
     * Listado de poblaciones con numeración asignada por estado.
     * @param poblacionEstado the poblacionEstado to set
     */
    public void setPoblacionEstado(List<Poblacion> poblacionEstado) {
        this.poblacionEstado = poblacionEstado;
    }

    /**
     * String con los distintos número de registros por página. Por defecto: 5,10,15,20,15
     * @return the numeroRegistros
     */
    public String getNumeroRegistros() {
        return numeroRegistros;
    }

    /**
     * String con los distintos número de registros por página. Por defecto: 5,10,15,20,15
     * @param numeroRegistros the numeroRegistros to set
     */
    public void setNumeroRegistros(String numeroRegistros) {
        this.numeroRegistros = numeroRegistros;
    }

}
