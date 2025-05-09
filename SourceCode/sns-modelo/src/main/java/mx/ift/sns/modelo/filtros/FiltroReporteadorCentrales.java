package mx.ift.sns.modelo.filtros;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;

import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.pst.Proveedor;

/**
 * Clase auxiliar para búsqueda de Centrales para los reportes.
 */
public class FiltroReporteadorCentrales implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

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

    /** Indica si se ha de utilizar paginación en la búsqueda. */
    private boolean usarPaginacion = false;

    /** Usando paginación, indica en el número de bloque. */
    private int numeroPagina = 0;

    /** Usando paginación, indica el número de resultados por bloque. */
    private int resultadosPagina = 50000;

    /** Limpia los filtros. */
    public void clear() {
        pstSeleccionada = null;
        estadoSeleccionado = null;
        municipioSeleccionado = null;
        poblacionSeleccionada = null;
        abnSeleccionado = null;
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
        return trace.toString();
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
     * Lista de filtros para búsqueda del reporteador de centrales.
     * @return ArrayList<FiltroBusqueda>
     */
    public ArrayList<FiltroBusqueda> getListaFiltros() {

        ArrayList<FiltroBusqueda> filtros = new ArrayList<FiltroBusqueda>();

        // Búsqueda sobre la vista REPORTE_CENTRAL_VM
        if (pstSeleccionada != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("idPst", "rc");
            filtro.setValor(pstSeleccionada.getId(), BigDecimal.class);
            filtros.add(filtro);
        }

        if (poblacionSeleccionada != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("idInegi", "rc");
            filtro.setValor(poblacionSeleccionada.getInegi(), String.class);
            filtros.add(filtro);

        }

        if (municipioSeleccionado != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("idMunicipio", "rc");
            filtro.setValor(municipioSeleccionado.getId().getCodMunicipio(), String.class);
            filtros.add(filtro);

        }

        if (estadoSeleccionado != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("idEstado", "rc");
            filtro.setValor(estadoSeleccionado.getCodEstado(), String.class);
            filtros.add(filtro);

        }

        if (abnSeleccionado != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("idAbn", "rc");
            filtro.setValor(abnSeleccionado, BigDecimal.class);
            filtros.add(filtro);
        }

        return filtros;
    }

}
