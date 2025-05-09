package mx.ift.sns.web.backend.reporteador;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import mx.ift.sns.modelo.filtros.FiltroReporteadorNG;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.reporteador.ElementoAgrupador;
import mx.ift.sns.negocio.IReporteadorFacade;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Clase de soporte para el reporteador de NG por fechas. */
@ManagedBean(name = "reporteadorNGTramitesBean")
@ViewScoped
public class ReporteadorNGTramitesBean implements Serializable {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ReporteadorNGTramitesBean.class);

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Id del campo de errores. */
    private static final String MSG_ID = "MSG_BuscadorNGTramites";

    /** Facade de servicios Administración de catálogo. */
    @EJB(mappedName = "ReporteadorFacade")
    private IReporteadorFacade reporteadorFacade;

    /** Pst seleccionada. */
    private Proveedor pstSeleccionada;

    /** Fecha hasta. */
    private Date fchHasta;

    /** Fecha desde. */
    private Date fchDesde;

    /** Codigo del tipo de agrupación. */
    private String agruparPor;

    /** Lista de psts disponibles para seleccionar. */
    private List<Proveedor> listaPst;

    /** Filtros de búsqueda de numeraciones NG activas/asignadas. */
    private FiltroReporteadorNG filtros;

    /** Check de agrupación por meses. */
    private boolean mes;

    /** Check de agrupación por años. */
    private boolean anyo;

    /**
     * Iniciamos la pantalla cargando los combos.
     * @throws Exception error en inicio
     **/
    @PostConstruct
    public void init() throws Exception {

        LOGGER.debug("");

        try {
            pstSeleccionada = null;
            fchDesde = null;
            fchHasta = null;
            agruparPor = "";
            mes = false;
            anyo = false;

            // Catálogo de Proveedores
            listaPst = reporteadorFacade.findAllProveedoresActivos();
            // Filtros de búsqueda.
            filtros = new FiltroReporteadorNG();

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
        fchDesde = null;
        fchHasta = null;
        agruparPor = "";
        mes = false;
        anyo = false;

    }

    /** Click en el mes. */
    public void checkMes() {
        if (mes) {
            anyo = false;
            agruparPor = ElementoAgrupador.COD_MES;
        } else {
            agruparPor = "";
        }
    }

    /** Click en el anyo. */
    public void checkAnyo() {
        if (anyo) {
            mes = false;
            agruparPor = ElementoAgrupador.COD_ANYO;
        } else {
            agruparPor = "";
        }
    }

    /**
     * Método que realiza el export a excel de los resultados de la consulta.
     * @return excel
     * @throws Exception Exception
     */
    public StreamedContent getExportarDatos() {
        StreamedContent fichero = null;
        boolean hayError = false;
        try {
            LOGGER.debug("");
            // Viene con el primer dia del mes seleccionado
            // Transformamos al primer dia del mes siguiente
            if (null == pstSeleccionada) {
                MensajesBean.addErrorMsg(MSG_ID, "PST es obligatorio", "");
                hayError = true;
            }
            if (null == fchHasta || null == fchHasta) {
                MensajesBean.addErrorMsg(MSG_ID, "El rango de fechas es obligatorio", "");
                hayError = true;
            } else if (fchHasta.before(fchDesde)) {
                MensajesBean.addErrorMsg(MSG_ID, "Fecha Hasta no puede ser menor a la Fecha Desde", "");
                hayError = true;
            }
            if (!hayError) {
                filtros.setPstSeleccionada(this.pstSeleccionada);
                filtros.setFechaInicio(fchDesde);
                filtros.setFechaFin(getFirstDayNextMonth(fchHasta));
                ElementoAgrupador primeraAgrupacion = new ElementoAgrupador();
                primeraAgrupacion.setCodigo(agruparPor);
                filtros.setPrimeraAgrupacion(primeraAgrupacion);

                InputStream stream = new ByteArrayInputStream(
                        reporteadorFacade.getReporteNGTramites(filtros));
                String docName = "Trámites Numeración Geográfica";

                docName = docName.concat(".xlsx");

                stream.close();

                LOGGER.debug("docname {}", docName);
                return new DefaultStreamedContent(stream,
                        "application/vnd.ms-excel", docName);

            }
        } catch (Exception e) {
            LOGGER.debug("Error inesperado: " + e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
        return fichero;
    }

    /**
     * Recupera el primer dia del mes siguiente.
     * @param fechaFin fecha
     * @return mes siguinete
     */
    private Date getFirstDayNextMonth(Date fechaFin) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fechaFin);
        calendar.add(Calendar.MONTH, 1); // Mes siguiente
        return calendar.getTime();

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

    /**
     * Fecha hasta.
     * @return Date
     */
    public Date getFchHasta() {
        return fchHasta;
    }

    /**
     * Fecha hasta.
     * @param fchHasta Date
     */
    public void setFchHasta(Date fchHasta) {
        this.fchHasta = fchHasta;
    }

    /**
     * Fecha desde.
     * @return Date
     */
    public Date getFchDesde() {
        return fchDesde;
    }

    /**
     * Fecha desde.
     * @param fchDesde Date
     */
    public void setFchDesde(Date fchDesde) {
        this.fchDesde = fchDesde;
    }

    /**
     * Codigo del tipo de agrupación.
     * @return String
     */
    public String getAgruparPor() {
        return agruparPor;
    }

    /**
     * Codigo del tipo de agrupación.
     * @param agruparPor String
     */
    public void setAgruparPor(String agruparPor) {
        this.agruparPor = agruparPor;
    }

    /**
     * Filtros de búsqueda de numeraciones NG activas/asignadas.
     * @return FiltroReporteadorNG
     */
    public FiltroReporteadorNG getFiltros() {
        return filtros;
    }

    /**
     * Filtros de búsqueda de numeraciones NG activas/asignadas.
     * @param filtros FiltroReporteadorNG
     */
    public void setFiltros(FiltroReporteadorNG filtros) {
        this.filtros = filtros;
    }

    /**
     * Check de agrupación por meses.
     * @return boolean
     */
    public boolean isMes() {
        return mes;
    }

    /**
     * Check de agrupación por meses.
     * @param mes boolean
     */
    public void setMes(boolean mes) {
        this.mes = mes;
    }

    /**
     * Check de agrupación por años.
     * @return boolean
     */
    public boolean isAnyo() {
        return anyo;
    }

    /**
     * Check de agrupación por años.
     * @param anyo boolean
     */
    public void setAnyo(boolean anyo) {
        this.anyo = anyo;
    }

}
