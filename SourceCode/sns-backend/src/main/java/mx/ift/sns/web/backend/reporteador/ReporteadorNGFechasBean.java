package mx.ift.sns.web.backend.reporteador;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import mx.ift.sns.modelo.filtros.FiltroReporteadorNG;
import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.Poblacion;
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
@ManagedBean(name = "reporteadorNGFechasBean")
@SessionScoped
public class ReporteadorNGFechasBean implements Serializable {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ReporteadorNGFechasBean.class);

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Id del campo de errores. */
    private static final String MSG_ID = "MSG_BuscadorNGFecha";

    /** Hilo que genera el reporte. */
    private Thread thread;

    /** Facade de servicios Administración de catálogo. */
    @EJB(mappedName = "ReporteadorFacade")
    private IReporteadorFacade reporteadorFacade;

    /** Existencia de petición de reporte. */
    private boolean existePeticion = false;

    /** Fin de la generación del reporte. */
    private boolean acabado = false;

    /** Reporte. */
    private byte[] file;

    /** Pst seleccionada. */
    private Proveedor pstSeleccionada;

    /** Estado seleccionado. */
    private Estado estadoSeleccionado;

    /** Municipio seleccionado. */
    private Municipio municipioSeleccionado;

    /** Poblacion seleccionado. */
    private Poblacion poblacionSeleccionada;

    /** ABN seleccionado. */
    private Integer abnSeleccionada;

    /** Fecha hasta. */
    private Date fchHasta;

    /** Fecha desde. */
    private Date fchDesde;

    /** Tipo de Agrupación. */
    private String agruparPor;

    /** Lista de psts disponibles para seleccionar. */
    private List<Proveedor> listaPst;

    /** Lista de estados disponibles para seleccionar. */
    private List<Estado> listaEstado;

    /** Lista de municipios disponibles para seleccionar. */
    private List<Municipio> listaMunicipio;

    /** Lista de poblaciones disponibles para seleccionar. */
    private List<Poblacion> listaPoblacion;

    /** Filtros de búsqueda de numeraciones NG activas/asignadas. */
    private FiltroReporteadorNG filtros;

    /**
     * Iniciamos la pantalla cargando los combos.
     * @throws Exception error en inicio
     **/
    @PostConstruct
    public void init() throws Exception {

        LOGGER.debug("");

        try {
            pstSeleccionada = null;
            estadoSeleccionado = null;
            municipioSeleccionado = null;
            poblacionSeleccionada = null;
            fchDesde = null;
            fchHasta = null;
            abnSeleccionada = null;
            agruparPor = "M";

            listaMunicipio = new ArrayList<Municipio>(1);
            listaPoblacion = new ArrayList<Poblacion>(1);
            // Catálogo de Proveedores
            listaPst = reporteadorFacade.findAllProveedoresActivos();
            listaEstado = reporteadorFacade.findAllEstados();
            // Filtros de búsqueda.
            filtros = new FiltroReporteadorNG();

        } catch (Exception ex) {
            LOGGER.error("error", ex);
        }
    }

    /**
     * Evalua si el usuario tine algun reporte pendiente.
     */
    public void evaluaPeticion() {
        if (existePeticion) {
            if (FacesContext.getCurrentInstance().getMessageList().isEmpty()) {
                MensajesBean.addInfoMsg(MSG_ID, "Existe un reporte en curso", "");
                MensajesBean.addInfoMsg(MSG_ID,
                        "Pulse el botón Actualizar estado para conocer la situación del proceso",
                        "");

            }
        }
    }

    /**
     * Reseta los valores.
     */
    public void resetPantalla() {

        LOGGER.debug("");

        pstSeleccionada = null;
        estadoSeleccionado = null;
        municipioSeleccionado = null;
        poblacionSeleccionada = null;
        fchDesde = null;
        fchHasta = null;
        listaMunicipio = null;
        listaPoblacion = null;
        abnSeleccionada = null;
        agruparPor = "M";

        if (thread != null) {
            thread.interrupt();
            // thread.stop();
        }
        file = null;
        acabado = false;
        existePeticion = false;
    }

    /** Finaliza una exportación pendiente. */
    public void limpiaExportacion() {

        LOGGER.debug("");

        if (thread != null) {
            // thread.stop();
            thread.interrupt();
        }
        file = null;
        acabado = false;
        existePeticion = false;
    }

    /** Método invocado al seleccionar un estado del combo de estados. */
    public void seleccionEstado() {

        LOGGER.debug("");

        try {
            if (estadoSeleccionado != null) {
                // Municipios
                listaMunicipio = reporteadorFacade.findMunicipiosByEstado(estadoSeleccionado.getCodEstado());
                municipioSeleccionado = null;
                poblacionSeleccionada = null;
                listaPoblacion = new ArrayList<Poblacion>(1);
            } else {
                // Inicialización de los desplegables de municipios y poblaciones
                listaMunicipio = new ArrayList<Municipio>(1);
                listaPoblacion = new ArrayList<Poblacion>(1);
            }
        } catch (Exception ex) {
            LOGGER.error("error", ex);
        }
    }

    /** Método invocado al seleccionar un municipio del combo de municipios. */
    public void seleccionMunicipio() {

        LOGGER.debug("");

        try {
            if (municipioSeleccionado != null && estadoSeleccionado != null) {
                // Poblaciones
                listaPoblacion = reporteadorFacade.findAllPoblaciones(estadoSeleccionado.getCodEstado(),
                        municipioSeleccionado.getId().getCodMunicipio());
                poblacionSeleccionada = null;
            } else {
                listaPoblacion = null;
            }
        } catch (Exception ex) {
            LOGGER.error("error", ex);
        }
    }

    /**
     * Devuelve el reporte generado.
     * @return reporte
     */
    public StreamedContent descargarFichero() {
        StreamedContent fichero = null;
        try {
            LOGGER.info("");
            InputStream stream = new ByteArrayInputStream(file);
            String docName = "Numeraciones Geográficas Activas/Asignadas";

            docName = docName.concat(".xlsx");

            stream.close();
            file = null;
            acabado = true;
            existePeticion = false;

            LOGGER.info("docname {}", docName);
            return new DefaultStreamedContent(stream,
                    "application/vnd.ms-excel", docName);

        } catch (Exception e) {
            LOGGER.info("Error inesperado: " + e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
        return fichero;
    }

    /**
     * Método que realiza el export a excel de los resultados de la consulta.
     */
    public void initReport() {
        try {
            LOGGER.info("initReport");
            // Viene con el primer dia del mes seleccionado
            // Transformamos al primer dia del mes siguiente

            if (null == fchDesde || null == fchHasta) {
                MensajesBean.addErrorMsg(MSG_ID, "El rango de fechas es obligatorio", "");
            } else if (fchHasta.before(fchDesde)) {
                MensajesBean.addErrorMsg(MSG_ID, "Fecha Hasta no puede ser menor a la Fecha Desde", "");
            } else {
                filtros.setEstadoSeleccionado(this.estadoSeleccionado);
                filtros.setMunicipioSeleccionado(this.municipioSeleccionado);
                filtros.setPoblacionSeleccionada(this.poblacionSeleccionada);
                filtros.setAbnSeleccionado(this.abnSeleccionada);
                filtros.setPstSeleccionada(this.pstSeleccionada);
                filtros.setFechaInicio(fchDesde);
                filtros.setFechaFin(this.getFirstDayNextMonth(fchHasta));
                ElementoAgrupador primeraAgrupacion = new ElementoAgrupador();
                primeraAgrupacion.setCodigo(agruparPor);
                filtros.setPrimeraAgrupacion(primeraAgrupacion);
                existePeticion = true;

                ThreadReport runnableJob = new ThreadReport();
                runnableJob.setReporteBean(this);
                runnableJob.setReporteadorFacade(reporteadorFacade);
                thread = new Thread(runnableJob);
                thread.start();
                MensajesBean.addInfoMsg(MSG_ID, "Se ha iniciado la generación del reporte", "");
                MensajesBean.addInfoMsg(MSG_ID,
                        "Pulse el botón Actualizar estado para conocer la situación del proceso",
                        "");
            }
        } catch (Exception e) {
            LOGGER.info("Error inesperado: " + e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Evalua el estado del proceso de reporte.
     */
    public void isReportRunning() {
        if (!thread.isAlive()) {
            MensajesBean.addInfoMsg(MSG_ID, "Pulse Descargar fichero para obtener el reporte", "");
            acabado = true;
        } else {
            MensajesBean.addInfoMsg(MSG_ID, "Reporte en proceso..", "");
        }
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
     * Estado seleccionado.
     * @return Estado
     */
    public Estado getEstadoSeleccionado() {
        return estadoSeleccionado;
    }

    /**
     * Estado seleccionado.
     * @param estadoSeleccionado Estado
     */
    public void setEstadoSeleccionado(Estado estadoSeleccionado) {
        this.estadoSeleccionado = estadoSeleccionado;
    }

    /**
     * Municipio seleccionado.
     * @return Municipio
     */
    public Municipio getMunicipioSeleccionado() {
        return municipioSeleccionado;
    }

    /**
     * Municipio seleccionado.
     * @param municipioSeleccionado Municipio
     */
    public void setMunicipioSeleccionado(Municipio municipioSeleccionado) {
        this.municipioSeleccionado = municipioSeleccionado;
    }

    /**
     * Poblacion seleccionado.
     * @return Poblacion
     */
    public Poblacion getPoblacionSeleccionada() {
        return poblacionSeleccionada;
    }

    /**
     * Poblacion seleccionado.
     * @param poblacionSeleccionada Poblacion
     */
    public void setPoblacionSeleccionada(Poblacion poblacionSeleccionada) {
        this.poblacionSeleccionada = poblacionSeleccionada;
    }

    /**
     * ABN seleccionado.
     * @return Integer
     */
    public Integer getAbnSeleccionada() {
        return abnSeleccionada;
    }

    /**
     * ABN seleccionado.
     * @param abnSeleccionada Integer
     */
    public void setAbnSeleccionada(Integer abnSeleccionada) {
        this.abnSeleccionada = abnSeleccionada;
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
     * Lista de estados disponibles para seleccionar.
     * @return List<Estado>
     */
    public List<Estado> getListaEstado() {
        return listaEstado;
    }

    /**
     * Lista de estados disponibles para seleccionar.
     * @param listaEstado List<Estado>
     */
    public void setListaEstado(List<Estado> listaEstado) {
        this.listaEstado = listaEstado;
    }

    /**
     * Lista de municipios disponibles para seleccionar.
     * @return List<Municipio>
     */
    public List<Municipio> getListaMunicipio() {
        return listaMunicipio;
    }

    /**
     * Lista de municipios disponibles para seleccionar.
     * @param listaMunicipio List<Municipio>
     */
    public void setListaMunicipio(List<Municipio> listaMunicipio) {
        this.listaMunicipio = listaMunicipio;
    }

    /**
     * Lista de poblaciones disponibles para seleccionar.
     * @return List<Poblacion>
     */
    public List<Poblacion> getListaPoblacion() {
        return listaPoblacion;
    }

    /**
     * Lista de poblaciones disponibles para seleccionar.
     * @param listaPoblacion List<Poblacion>
     */
    public void setListaPoblacion(List<Poblacion> listaPoblacion) {
        this.listaPoblacion = listaPoblacion;
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
     * Tipo de Agrupación.
     * @return String
     */
    public String getAgruparPor() {
        return agruparPor;
    }

    /**
     * Tipo de Agrupación.
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
     * @return the existePeticion
     */
    public boolean isExistePeticion() {
        return existePeticion;
    }

    /**
     * @param existePeticion the existePeticion to set
     */
    public void setExistePeticion(boolean existePeticion) {
        this.existePeticion = existePeticion;
    }

    /**
     * @return acabado
     */
    public boolean isAcabado() {
        return acabado;
    }

    /**
     * @param acabado acabado
     */
    public void setAcabado(boolean acabado) {
        this.acabado = acabado;
    }

    /**
     * @return the file
     */
    public byte[] getFile() {
        return file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(byte[] file) {
        this.file = file;
    }

}
