package mx.ift.sns.modelo.filtros;

import java.io.Serializable;
import java.util.ArrayList;

import mx.ift.sns.modelo.ot.Estado;

/**
 * Clase auxiliar para búsqueda de solicitudes genérica. Contiene los filtros que negocio enviará a los DAOS para que
 * construya las querys. Cada variable asignada se considerará un filtro a satisfacer.
 */
public class FiltroBusquedaEstados implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    // private static final Logger LOGGER = LoggerFactory.getLogger(FiltroBusquedaEstados.class);

    /** Indica si se ha de utilizar paginación en la búsqueda. */
    private boolean usarPaginacion = false;

    /** Usando paginación, indica en el número de bloque. */
    private int numeroPagina = 0;

    /** Usando paginación, indica el número de resultados por bloque. */
    private int resultadosPagina = 10;

    /** Estado de Estado. */
    private Estado estadoMun;

    /** Constructor, por defect vacío. */
    public FiltroBusquedaEstados() {
    }

    /**
     * Genera una lista con los filtros que se han definido.
     * @return Lista de objetos FiltroBusqueda con la información solicitada
     */
    public ArrayList<FiltroBusqueda> getListaFiltros() {

        ArrayList<FiltroBusqueda> filtros = new ArrayList<FiltroBusqueda>();

        if (estadoMun != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("estadoSolicitud", "e");
            filtro.setValor(estadoMun, Estado.class);
            filtros.add(filtro);
        }

        return filtros;
    }

    /** Limpia los filtros. */
    public void clear() {
        estadoMun = null;
    }

    @Override
    public String toString() {
        StringBuilder trace = new StringBuilder();
        if (estadoMun != null) {
            trace.append("estadoMun").append(": ").append(estadoMun).append(", ");
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
     * Estado de Estado.
     * @return the estadoMun
     */
    public Estado getEstadoMun() {
        return estadoMun;
    }

    /**
     * Estado de Estado.
     * @param estadoMun the estadoMun to set
     */
    public void setEstadoMun(Estado estadoMun) {
        this.estadoMun = estadoMun;
    }

}
