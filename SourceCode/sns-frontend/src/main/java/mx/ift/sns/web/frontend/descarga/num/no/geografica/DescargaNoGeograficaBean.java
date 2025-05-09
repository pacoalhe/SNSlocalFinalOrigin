package mx.ift.sns.web.frontend.descarga.num.no.geografica;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import mx.ift.sns.modelo.nng.ClaveServicio;
import mx.ift.sns.modelo.pnn.Plan;
import mx.ift.sns.modelo.pnn.TipoPlan;
import mx.ift.sns.negocio.IConsultaPublicaFacade;
import mx.ift.sns.web.frontend.common.MensajesFrontBean;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controla la descarga de planes de numeración no geográfica.
 * @author X51461MO
 */
@ManagedBean(name = "descargaNoGeograficaBean")
@ViewScoped
public class DescargaNoGeograficaBean implements Serializable {

    /** Servicio de Busqueda Publica. */
    @EJB(mappedName = "ConsultaPublicaFacade")
    private IConsultaPublicaFacade ngPublicService;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DescargaNoGeograficaBean.class);

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador del componente de mensajes JSF. */
    private static final String MSG_ID = "MSG_ConsultaFront";

    /**
     * Listado con las claves de servicio.
     */
    private List<ClaveServicio> clavesServicio;

    /**
     * Plan para la descarga.
     */
    private Plan planDescarga;

    /** Listado de planes de numeración no goegráfica públicos, visibles en la web y con archivo generado. */
    private List<Plan> listaPlanesDescarga;

    // /////////////////////////////////POSTCONSTRUCT//////////////////////////////
    /**
     * Postconstructor.
     */
    @PostConstruct
    public void init() {
        // Listado de claves de servicio
        try {
            List<Plan> listaPlanesAux = new ArrayList<Plan>();
            Plan planAux = null;
            List<ClaveServicio> listadoClavesActivasWeb = ngPublicService.findAllClaveServicioActivasWeb();
            for (ClaveServicio claveServicio : listadoClavesActivasWeb) {
                planAux = ngPublicService.getPlanByTipoAndClaveServicio(TipoPlan.TIPO_PLAN_NNG_PUBLICO,
                        claveServicio.getCodigo());
                if (planAux != null) {
                    listaPlanesAux.add(ngPublicService.getPlanByTipoAndClaveServicio(TipoPlan.TIPO_PLAN_NNG_PUBLICO,
                            claveServicio.getCodigo()));
                }
            }
            this.setListaPlanesDescarga(listaPlanesAux);
        } catch (Exception e) {
            LOGGER.debug("Error inesperado: " + e.getMessage());
            MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado");
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("PostConstruc Descarga No Geogáfica: está activado");
        }
    }

    // ///////////////////////////MÉTODOS////////////////////////////////////
    /**
     * Método invocado al pulsar sobre el botón 'Descargar'. Realiza la descarga del fichero correspondiente.
     * @param clave ClaveServicio
     * @return StreamedContent Fichero a Descargar
     */
    public StreamedContent descargarPlanNng(ClaveServicio clave) {
        try {
            this.setPlanDescarga(ngPublicService.getPlanByTipoAndClaveServicio(TipoPlan.TIPO_PLAN_NNG_PUBLICO,
                    clave.getCodigo()));
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("clave del archivo de descarga {}", clave.getCodigo());
            }
            if (this.planDescarga != null) {
                InputStream stream = new ByteArrayInputStream(planDescarga.getFichero());
                StringBuffer docName = new StringBuffer();
                docName.append(planDescarga.getNombre());
                // Zip Mime Type
                String wordMimeType = "multipart/x-zip";
                StreamedContent downFile = new DefaultStreamedContent(stream, wordMimeType, docName.toString());
                MensajesFrontBean.addInfoMsg(MSG_ID, "Plan descargado correctamente");
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Archivo descargado correctamente");
                }
                return downFile;
            } else {
                MensajesFrontBean.addErrorMsg(MSG_ID, "No existe el plan seleccionado");
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("No existe achivo para la descarga");
                }
            }

        } catch (Exception e) {
            LOGGER.debug("Error inesperado: " + e.getMessage());
            MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado");
        }
        return null;
    }

    /**
     * Obtiene la fecha de generación del plan público de numeración no geográfica listo para descargar.
     * @param fechaGeneracion Date
     * @return String
     */
    public String getFormatoFechaPlanNNGGenerado(Date fechaGeneracion) {
        String fechaPlanPublicoNNGString = "";
        try {
            SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
            fechaPlanPublicoNNGString = formatoFecha.format(fechaGeneracion);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("La fecha de generación del plan público es: {}", fechaPlanPublicoNNGString);
            }

        } catch (Exception e) {
            LOGGER.error("Error inesperado al obtener la fecha  de generación del plan público" + e.getMessage());
            MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado");
        }
        return fechaPlanPublicoNNGString;
    }

    /**
     * Plan para la descarga.
     * @return the planDescarga
     */
    public Plan getPlanDescarga() {
        return planDescarga;
    }

    /**
     * Plan para la descarga.
     * @param planDescarga the planDescarga to set
     */
    public void setPlanDescarga(Plan planDescarga) {
        this.planDescarga = planDescarga;
    }

    /**
     * Listado con las claves de servicio.
     * @return the clavesServicio
     */
    public List<ClaveServicio> getClavesServicio() {
        return clavesServicio;
    }

    /**
     * Listado con las claves de servicio.
     * @param clavesServicio the clavesServicio to set
     */
    public void setClavesServicio(List<ClaveServicio> clavesServicio) {
        this.clavesServicio = clavesServicio;
    }

    /**
     * Listado de planes de numeración no goegráfica públicos, visibles en la web y con archivo generado.
     * @return the listaPlanesDescarga
     */
    public List<Plan> getListaPlanesDescarga() {
        return listaPlanesDescarga;
    }

    /**
     * Listado de planes de numeración no goegráfica públicos, visibles en la web y con archivo generado.
     * @param listaPlanesDescarga the listaPlanesDescarga to set
     */
    public void setListaPlanesDescarga(List<Plan> listaPlanesDescarga) {
        this.listaPlanesDescarga = listaPlanesDescarga;
    }

}
