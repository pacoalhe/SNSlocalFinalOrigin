package mx.ift.sns.modelo.filtros;

import java.io.Serializable;
import java.util.ArrayList;

import mx.ift.sns.modelo.oficios.TipoDestinatario;
import mx.ift.sns.modelo.solicitud.TipoSolicitud;

/**
 * Clase auxiliar para búsqueda de plantillas. Contiene los filtros que negocio enviará a los DAOS para que construya
 * las querys. Cada variable asignada se considerará un filtro a satisfacer.
 */
public class FiltroBusquedaPlantillas implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    // private static final Logger LOGGER = LoggerFactory.getLogger(FiltroBusquedaPlantillas.class);

    /** Tipo de destinatario. */
    private TipoDestinatario tipoDestinatario;

    /** Tipo de Solicitud. */
    private TipoSolicitud tipoSolicitud;

    /** Indica si se ha de utilizar paginación en la búsqueda. */
    private boolean usarPaginacion = false;

    /** Usando paginación, indica en el número de bloque. */
    private int numeroPagina = 0;

    /** Usando paginación, indica el número de resultados por bloque. */
    private int resultadosPagina = 0;

    /** Constructor, por defect vacío. */
    public FiltroBusquedaPlantillas() {
    }

    /**
     * Genera una lista con los filtros que se han definido.
     * @return Lista de objetos FiltroBusqueda con la información solicitada
     */
    public ArrayList<FiltroBusqueda> getListaFiltros() {

        ArrayList<FiltroBusqueda> filtros = new ArrayList<FiltroBusqueda>();

        if (tipoDestinatario != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("tipoDestinatario", "");
            filtro.setValor(tipoDestinatario, TipoDestinatario.class);
            filtros.add(filtro);
        }

        if (tipoSolicitud != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("tipoSolicitud", "");
            filtro.setValor(tipoSolicitud, TipoSolicitud.class);
            filtros.add(filtro);
        }

        return filtros;
    }

    /** Limpia los filtros. */
    public void clear() {
        this.tipoDestinatario = null;
        this.tipoSolicitud = null;
        this.usarPaginacion = false;
        this.numeroPagina = 0;
        this.resultadosPagina = 0;
    }

    @Override
    public String toString() {
        StringBuilder trace = new StringBuilder();
        if (tipoSolicitud != null) {
            trace.append("tipoSolicitud").append(": ").append(tipoSolicitud.getDescripcion()).append(", ");
        }
        if (tipoDestinatario != null) {
            trace.append("tipoDestinatario").append(": ").append(tipoDestinatario.getDescripcion()).append(", ");
        }
        trace.append("usarPaginacion").append(": ").append(usarPaginacion).append(", ");
        if (usarPaginacion) {
            trace.append("numeroPagina").append(": ").append(numeroPagina).append(", ");
            trace.append("resultadosPagina").append(": ").append(resultadosPagina);
        }
        return trace.toString();
    }

    // GETTERS & SETTERS

    /**
     * Tipo de destinatario.
     * @return TipoDestinatario
     */
    public TipoDestinatario getTipoDestinatario() {
        return tipoDestinatario;
    }

    /**
     * Tipo de destinatario.
     * @param tipoDestinatario TipoDestinatario
     */
    public void setTipoDestinatario(TipoDestinatario tipoDestinatario) {
        this.tipoDestinatario = tipoDestinatario;
    }

    /**
     * Tipo de Solicitud.
     * @return TipoSolicitud
     */
    public TipoSolicitud getTipoSolicitud() {
        return tipoSolicitud;
    }

    /**
     * Tipo de Solicitud.
     * @param tipoSolicitud TipoSolicitud
     */
    public void setTipoSolicitud(TipoSolicitud tipoSolicitud) {
        this.tipoSolicitud = tipoSolicitud;
    }

    /**
     * Indica si se ha de utilizar paginación en la búsqueda.
     * @return boolean
     */
    public boolean isUsarPaginacion() {
        return usarPaginacion;
    }

    /**
     * Indica si se ha de utilizar paginación en la búsqueda.
     * @param usarPaginacion boolean
     */
    public void setUsarPaginacion(boolean usarPaginacion) {
        this.usarPaginacion = usarPaginacion;
    }

    /**
     * Usando paginación, indica en el número de bloque.
     * @return int
     */
    public int getNumeroPagina() {
        return numeroPagina;
    }

    /**
     * Usando paginación, indica en el número de bloque.
     * @param numeroPagina int
     */
    public void setNumeroPagina(int numeroPagina) {
        this.numeroPagina = numeroPagina;
    }

    /**
     * Usando paginación, indica el número de resultados por bloque.
     * @return int
     */
    public int getResultadosPagina() {
        return resultadosPagina;
    }

    /**
     * Usando paginación, indica el número de resultados por bloque.
     * @param resultadosPagina int
     */
    public void setResultadosPagina(int resultadosPagina) {
        this.resultadosPagina = resultadosPagina;
    }
}
