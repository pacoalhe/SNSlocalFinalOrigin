package mx.ift.sns.modelo.filtros;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.pst.TipoModalidad;
import mx.ift.sns.modelo.pst.TipoRed;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Filtro de búsqueda de series.
 */
public class FiltroBusquedaHistoricoSeries implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(FiltroBusquedaHistoricoSeries.class);

    /** Formateador de fechas. */
    private SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy");

    /** Indica si se ha de utilizar paginación en la búsqueda. */
    private boolean usarPaginacion = false;

    /** Usando paginación, indica en el número de bloque. */
    private int numeroPagina = 0;

    /** Usando paginación, indica el número de resultados por bloque. */
    private int resultadosPagina = 10;

    /** Identificador de código de área. */
    private String idAbn;

    /** Identificador de código de región. */
    private String idNir;

    /** Código de región. */
    private String cdgNir;

    /** Identificador de numeración. */
    private String idSna;

    /** Numero inicial. */
    private String numIni;

    /** Numero final. */
    private String numFin;

    /** Estado. */
    private Estado estado;

    /** Municipio. */
    private Municipio municipio;

    /** Poblacion. */
    private Poblacion poblacion;

    /** Tipo de Red asociada al Rango. */
    private TipoRed tipoRed;

    /** Tipo de Modalidad asociada al Rango para redes móviles. */
    private TipoModalidad tipoModalidad;

    /** Clave de servicio. */
    private String claveServicio;

    /** Fecha de inicio. */
    private Date fechaInicio;

    /** Fecha fin. */
    private Date fechaFin;

    /**
     * Crea la lista de filtros dinamica.
     * @return lista filtros.
     */
    public ArrayList<FiltroBusqueda> getListaHitoricoSeriesNng() {
        ArrayList<FiltroBusqueda> filtros = new ArrayList<FiltroBusqueda>();

        if (!StringUtils.isEmpty(claveServicio)) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("claveServicio", "hs");
            filtro.setValor(new BigDecimal(claveServicio), BigDecimal.class);
            filtros.add(filtro);
        }

        if (!StringUtils.isEmpty(numIni)) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("numInicial", "hs");
            filtro.setValor(numIni, String.class);
            filtros.add(filtro);
        }

        if (!StringUtils.isEmpty(numFin)) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("numFinal", "hs");
            filtro.setValor(numFin, String.class);
            filtros.add(filtro);
        }

        if (!StringUtils.isEmpty(idSna)) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("serie", "hs");
            filtro.setValor(new BigDecimal(idSna), BigDecimal.class);
            filtros.add(filtro);
        }

        if (fechaInicio != null) {

            try {
                FiltroBusqueda filtro = new FiltroBusqueda();
                filtro.setCampo("fechaTramite", "hs");
                filtro.setValor(formateador.parse(formateador.format(fechaInicio)), Date.class);
                filtro.setFechaDesde(true);
                filtros.add(filtro);
            } catch (ParseException e) {
                LOGGER.error("Fecha incorrecta: " + fechaInicio.toString());
            }

        }

        if (fechaFin != null) {

            try {
                FiltroBusqueda filtro = new FiltroBusqueda();
                filtro.setCampo("fechaTramite", "hs");
                filtro.setValor(formateador.parse(formateador.format(fechaFin)), Date.class);
                filtro.setFechaHasta(true);
                filtros.add(filtro);
            } catch (ParseException e) {
                LOGGER.error("Fecha incorrecta: " + fechaFin.toString());
            }

        }

        return filtros;
    }

    /**
     * Limpia los filtros.
     */
    public void clear() {
        idAbn = null;
        idNir = null;
        cdgNir = null;
        idSna = null;
        numIni = null;
        numFin = null;
        estado = null;
        municipio = null;
        poblacion = null;
        tipoModalidad = null;
        tipoRed = null;
        fechaFin = null;
        fechaInicio = null;
        claveServicio = null;
    }

    @Override
    public String toString() {
        StringBuffer trace = new StringBuffer();
        trace.append("idAbn").append(": ").append(idAbn).append(", ");
        trace.append("idSna").append(": ").append(idSna).append(", ");
        trace.append("idNir").append(": ").append(idNir).append(", ");

        trace.append("tipoModalidad").append(": ").append(tipoModalidad).append(", ");
        trace.append("tipoRed").append(": ").append(tipoRed);
        return trace.toString();
    }

    /**
     * Identificador de numeración.
     * @return BigDecimal
     */
    public String getIdSna() {
        return idSna;
    }

    /**
     * Identificador de numeración.
     * @param idSna BigDecimal
     */
    public void setIdSna(String idSna) {
        this.idSna = idSna;
    }

    /**
     * Tipo de Red asociada al Rango.
     * @return TipoRed
     */
    public TipoRed getTipoRed() {
        return tipoRed;
    }

    /**
     * Tipo de Red asociada al Rango.
     * @param tipoRed TipoRed
     */
    public void setTipoRed(TipoRed tipoRed) {
        this.tipoRed = tipoRed;
    }

    /**
     * Tipo de Modalidad asociada al Rango para redes móviles.
     * @return TipoModalidad
     */
    public TipoModalidad getTipoModalidad() {
        return tipoModalidad;
    }

    /**
     * Tipo de Modalidad asociada al Rango para redes móviles.
     * @param tipoModalidad TipoModalidad
     */
    public void setTipoModalidad(TipoModalidad tipoModalidad) {
        this.tipoModalidad = tipoModalidad;
    }

    /**
     * Fecha de inicio.
     * @return the fechaInicio
     */
    public Date getFechaInicio() {
        return fechaInicio;
    }

    /**
     * Fecha de inicio.
     * @param fechaInicio the fechaInicio to set
     */
    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    /**
     * Fecha fin.
     * @return the fechaFin
     */
    public Date getFechaFin() {
        return fechaFin;
    }

    /**
     * Fecha fin.
     * @param fechaFin the fechaFin to set
     */
    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    /**
     * Poblacion.
     * @return the poblacion
     */
    public Poblacion getPoblacion() {
        return poblacion;
    }

    /**
     * Poblacion.
     * @param poblacion the poblacion to set
     */
    public void setPoblacion(Poblacion poblacion) {
        this.poblacion = poblacion;
    }

    /**
     * Municipio.
     * @return the municipio
     */
    public Municipio getMunicipio() {
        return municipio;
    }

    /**
     * Municipio.
     * @param municipio the municipio to set
     */
    public void setMunicipio(Municipio municipio) {
        this.municipio = municipio;
    }

    /**
     * Estado.
     * @return the estado
     */
    public Estado getEstado() {
        return estado;
    }

    /**
     * Estado.
     * @param estado the estado to set
     */
    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    /**
     * Numero inicial.
     * @return the numIni
     */
    public String getNumIni() {
        return numIni;
    }

    /**
     * Numero inicial.
     * @param numIni the numIni to set
     */
    public void setNumIni(String numIni) {
        this.numIni = numIni;
    }

    /**
     * Numero final.
     * @return the numFin
     */
    public String getNumFin() {
        return numFin;
    }

    /**
     * Numero final.
     * @param numFin the numFin to set
     */
    public void setNumFin(String numFin) {
        this.numFin = numFin;
    }

    /**
     * Identificador de código de área.
     * @param idAbn the idAbn to set
     */
    public void setIdAbn(String idAbn) {
        this.idAbn = idAbn;
    }

    /**
     * Identificador de código de región.
     * @param idNir the idNir to set
     */
    public void setIdNir(String idNir) {
        this.idNir = idNir;
    }

    /**
     * Identificador de código de área.
     * @return the idAbn
     */
    public String getIdAbn() {
        return idAbn;
    }

    /**
     * Identificador de código de región.
     * @return the idNir
     */
    public String getIdNir() {
        return idNir;
    }

    /**
     * Código de región.
     * @return the cdgNir
     */
    public String getCdgNir() {
        return cdgNir;
    }

    /**
     * Código de región.
     * @param cdgNir the cdgNir to set
     */
    public void setCdgNir(String cdgNir) {
        this.cdgNir = cdgNir;
    }

    /**
     * Clave de servicio.
     * @return the claveServicio
     */
    public String getClaveServicio() {
        return claveServicio;
    }

    /**
     * Clave de servicio.
     * @param claveServicio the claveServicio to set
     */
    public void setClaveServicio(String claveServicio) {
        this.claveServicio = claveServicio;
    }

    /**
     * Formateador de fechas.
     * @return the formateador
     */
    public SimpleDateFormat getFormateador() {
        return formateador;
    }

    /**
     * Formateador de fechas.
     * @param formateador the formateador to set
     */
    public void setFormateador(SimpleDateFormat formateador) {
        this.formateador = formateador;
    }

    /**
     * Indica si se ha de utilizar paginación en la búsqueda.
     * @return the usarPaginacion
     */
    public boolean isUsarPaginacion() {
        return usarPaginacion;
    }

    /**
     * Indica si se ha de utilizar paginación en la búsqueda.
     * @param usarPaginacion the usarPaginacion to set
     */
    public void setUsarPaginacion(boolean usarPaginacion) {
        this.usarPaginacion = usarPaginacion;
    }

    /**
     * Usando paginación, indica en el número de bloque.
     * @return the numeroPagina
     */
    public int getNumeroPagina() {
        return numeroPagina;
    }

    /**
     * Usando paginación, indica en el número de bloque.
     * @param numeroPagina the numeroPagina to set
     */
    public void setNumeroPagina(int numeroPagina) {
        this.numeroPagina = numeroPagina;
    }

    /**
     * Usando paginación, indica el número de resultados por bloque.
     * @return the resultadosPagina
     */
    public int getResultadosPagina() {
        return resultadosPagina;
    }

    /**
     * Usando paginación, indica el número de resultados por bloque.
     * @param resultadosPagina the resultadosPagina to set
     */
    public void setResultadosPagina(int resultadosPagina) {
        this.resultadosPagina = resultadosPagina;
    }

    /**
     * Indica si el filtro esta vacio.
     * @return true si lo esta
     */
    public boolean isEmpty() {
        if ((idAbn == null) && (idNir == null) && (cdgNir == null) && (idSna == null) && (numIni == null)
                && (numFin == null) && (estado == null) && (municipio == null)
                && (poblacion == null) && (tipoModalidad == null) && (tipoRed == null)
                && (fechaFin == null) && (fechaInicio == null) && claveServicio == null) {
            return true;
        }

        return false;
    }

}
