package mx.ift.sns.modelo.filtros;

import java.io.Serializable;
import java.util.Date;

import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.reporteador.ElementoAgrupador;

/**
 * Clase auxiliar para búsqueda de NG para los reportes.
 */
public class FiltroReporteadorNG implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** fechaInicio. */
    private Date fechaInicio;

    /** fechaFin. */
    private Date fechaFin;

    /** Pst seleccionada. */
    private Proveedor pstSeleccionada;

    /** Estado seleccionado. */
    private Estado estadoSeleccionado;

    /** Municipio seleccionado. */
    private Municipio municipioSeleccionado;

    /** Poblacion seleccionado. */
    private Poblacion poblacionSeleccionada;

    /** ABN seleccionado. */
    private Integer abnSeleccionado;

    /** Elemento de agrupación 1 seleccionado. */
    private ElementoAgrupador primeraAgrupacion;

    /** Elemento de agrupación 2 seleccionado. */
    private ElementoAgrupador segundaAgrupacion;

    /** Tipo de solicitud seleccionada. */
    private String tipoSolicitudSeleccionada;

    /** Limpia los filtros. */
    public void clear() {
        fechaInicio = null;
        fechaFin = null;
        pstSeleccionada = null;
        estadoSeleccionado = null;
        municipioSeleccionado = null;
        poblacionSeleccionada = null;
        abnSeleccionado = null;
        primeraAgrupacion = null;
        segundaAgrupacion = null;
    }

    @Override
    public String toString() {
        StringBuilder trace = new StringBuilder();
        if (pstSeleccionada != null) {
            trace.append("pstSeleccionada").append(": ").append(pstSeleccionada).append(", ");
        }
        if (estadoSeleccionado != null) {
            trace.append("estadoSeleccionado").append(": ").append(estadoSeleccionado).append(", ");
        }
        if (municipioSeleccionado != null) {
            trace.append("municipioSeleccionado").append(": ").append(municipioSeleccionado).append(", ");
        }
        if (poblacionSeleccionada != null) {
            trace.append("poblacionSeleccionada").append(": ").append(poblacionSeleccionada).append(", ");
        }
        if (abnSeleccionado != null) {
            trace.append("abnSeleccionado").append(": ").append(abnSeleccionado).append(", ");
        }
        if (fechaInicio != null) {
            trace.append("fechaInicio").append(": ").append(fechaInicio).append(", ");
        }
        if (fechaFin != null) {
            trace.append("fechaFin").append(": ").append(fechaFin).append(", ");
        }

        if (primeraAgrupacion != null) {
            trace.append("primeraAgrupacion").append(": ").append(primeraAgrupacion).append(", ");
        }
        if (segundaAgrupacion != null) {
            trace.append("segundaAgrupacion").append(": ").append(segundaAgrupacion).append(", ");
        }
        return trace.toString();
    }

    /**
     * fechaFin.
     * @return the fechaFin
     */
    public Date getFechaFin() {
        return fechaFin;
    }

    /**
     * fechaFin.
     * @param fechaFin the fechaFin to set
     */
    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    /**
     * Pst seleccionada.
     * @return the pstSeleccionada
     */
    public Proveedor getPstSeleccionada() {
        return pstSeleccionada;
    }

    /**
     * Pst seleccionada.
     * @param pstSeleccionada the pstSeleccionada to set
     */
    public void setPstSeleccionada(Proveedor pstSeleccionada) {
        this.pstSeleccionada = pstSeleccionada;
    }

    /**
     * Estado seleccionado.
     * @return the estadoSeleccionado
     */
    public Estado getEstadoSeleccionado() {
        return estadoSeleccionado;
    }

    /**
     * Estado seleccionado.
     * @param estadoSeleccionado the estadoSeleccionado to set
     */
    public void setEstadoSeleccionado(Estado estadoSeleccionado) {
        this.estadoSeleccionado = estadoSeleccionado;
    }

    /**
     * Municipio seleccionado.
     * @return the municipioSeleccionado
     */
    public Municipio getMunicipioSeleccionado() {
        return municipioSeleccionado;
    }

    /**
     * Municipio seleccionado.
     * @param municipioSeleccionado the municipioSeleccionado to set
     */
    public void setMunicipioSeleccionado(Municipio municipioSeleccionado) {
        this.municipioSeleccionado = municipioSeleccionado;
    }

    /**
     * Poblacion seleccionado.
     * @return the poblacionSeleccionada
     */
    public Poblacion getPoblacionSeleccionada() {
        return poblacionSeleccionada;
    }

    /**
     * Poblacion seleccionado.
     * @param poblacionSeleccionada the poblacionSeleccionada to set
     */
    public void setPoblacionSeleccionada(Poblacion poblacionSeleccionada) {
        this.poblacionSeleccionada = poblacionSeleccionada;
    }

    /**
     * ABN seleccionado.
     * @return the abnSeleccionado
     */
    public Integer getAbnSeleccionado() {
        return abnSeleccionado;
    }

    /**
     * ABN seleccionado.
     * @param abnSeleccionado the abnSeleccionado to set
     */
    public void setAbnSeleccionado(Integer abnSeleccionado) {
        this.abnSeleccionado = abnSeleccionado;
    }

    /**
     * fechaInicio.
     * @return the fechaInicio
     */
    public Date getFechaInicio() {
        return fechaInicio;
    }

    /**
     * fechaInicio.
     * @param fechaInicio the fechaInicio to set
     */
    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    /**
     * Elemento de agrupación 1 seleccionado.
     * @return the primeraAgrupacion
     */
    public ElementoAgrupador getPrimeraAgrupacion() {
        return primeraAgrupacion;
    }

    /**
     * Elemento de agrupación 1 seleccionado.
     * @param primeraAgrupacion the primeraAgrupacion to set
     */
    public void setPrimeraAgrupacion(ElementoAgrupador primeraAgrupacion) {
        this.primeraAgrupacion = primeraAgrupacion;
    }

    /**
     * Elemento de agrupación 2 seleccionado.
     * @return the segundaAgrupacion
     */
    public ElementoAgrupador getSegundaAgrupacion() {
        return segundaAgrupacion;
    }

    /**
     * Elemento de agrupación 2 seleccionado.
     * @param segundaAgrupacion the segundaAgrupacion to set
     */
    public void setSegundaAgrupacion(ElementoAgrupador segundaAgrupacion) {
        this.segundaAgrupacion = segundaAgrupacion;
    }

    /**
     * Tipo de solicitud seleccionada.
     * @return the tipoSolicitudSeleccionada
     */
    public String getTipoSolicitudSeleccionada() {
        return tipoSolicitudSeleccionada;
    }

    /**
     * Tipo de solicitud seleccionada.
     * @param tipoSolicitudSeleccionada the tipoSolicitudSeleccionada to set
     */
    public void setTipoSolicitudSeleccionada(String tipoSolicitudSeleccionada) {
        this.tipoSolicitudSeleccionada = tipoSolicitudSeleccionada;
    }

}
