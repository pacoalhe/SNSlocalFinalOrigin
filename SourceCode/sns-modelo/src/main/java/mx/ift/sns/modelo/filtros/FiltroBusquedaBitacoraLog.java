package mx.ift.sns.modelo.filtros;

import java.io.Serializable;
import java.util.Date;

import mx.ift.sns.modelo.usu.Usuario;

/**
 * Filtro de búsqueda de bitacora log.
 */
public class FiltroBusquedaBitacoraLog implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Primer Resultado. */
    private int first = -1;

    /** Tamaño de la busqueda. */
    private int pageSize = -1;

    /** Fecha de inicio. */
    private Date fechaInicio;

    /** Fecha fin. */
    private Date fechaFin;

    /** Usaurio de busqueda. */
    private Usuario usuario;

    /** Ordenacion del filtro. */
    private OrdenFiltro ordenFiltro;

    /**
     * Limpia los filtros.
     */
    public void clear() {
        fechaFin = null;
        fechaInicio = null;
        usuario = null;
        ordenFiltro = OrdenFiltro.NINGUNO;
        first = -1;
        pageSize = -1;
    }

    @Override
    public String toString() {
        StringBuffer trace = new StringBuffer();
        return trace.toString();
    }

    /**
     * Indica si el filtro esta vacio.
     * @return true si lo esta
     */
    public boolean isEmpty() {
        if ((fechaFin == null) && (fechaInicio == null) && (usuario != null)) {
            return true;
        }

        return false;
    }

    /**
     * Primer Resultado.
     * @return the first
     */
    public int getFirst() {
        return first;
    }

    /**
     * Primer Resultado.
     * @param first the first to set
     */
    public void setFirst(int first) {
        this.first = first;
    }

    /**
     * Tamaño de la busqueda.
     * @return the pageSize
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * Tamaño de la busqueda.
     * @param pageSize the pageSize to set
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
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
     * Ordenacion del filtro.
     * @return the ordenFiltro
     */
    public OrdenFiltro getOrdenFiltro() {
        return ordenFiltro;
    }

    /**
     * Ordenacion del filtro.
     * @param ordenFiltro the ordenFiltro to set
     */
    public void setOrdenFiltro(OrdenFiltro ordenFiltro) {
        this.ordenFiltro = ordenFiltro;
    }

    /**
     * Usaurio de busqueda.
     * @return the usuario
     */
    public Usuario getUsuario() {
        return usuario;
    }

    /**
     * Usaurio de busqueda.
     * @param usuario the usuario to set
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
