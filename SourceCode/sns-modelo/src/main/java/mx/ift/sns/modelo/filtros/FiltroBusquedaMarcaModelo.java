package mx.ift.sns.modelo.filtros;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;

/** Filtro concreto para búsqueda. Contiene información específica de campo, tabla, etc. */
public class FiltroBusquedaMarcaModelo implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Identificador de código de área. */
    private BigDecimal idMarca;

    /** Identificador de código de región. */
    private BigDecimal idModelo;

    /** Indica si se ha de utilizar paginación en la búsqueda. */
    private boolean usarPaginacion = true;

    /** Usando paginación, indica en el número de bloque. */
    private int numeroPagina = 0;

    /** Usando paginación, indica el número de resultados por bloque. */
    private int resultadosPagina = 10;

    /**
     * Genera una lista con los filtros que se han definido sobre Rangos de Serie.
     * @return ArrayList<FiltroBusqueda>
     */
    public ArrayList<FiltroBusqueda> getListaFiltros() {

        ArrayList<FiltroBusqueda> filtros = new ArrayList<FiltroBusqueda>();

        if (idModelo != null) {

            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("id", "m");
            filtro.setValor(idModelo, BigDecimal.class);
            filtros.add(filtro);
        } else if (idMarca != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("marca.id", "m");
            filtro.setValor(idMarca, BigDecimal.class);
            filtros.add(filtro);

        }

        return filtros;
    }

    /** Limpia los filtros. */
    public void clear() {
        idMarca = null;
        idModelo = null;
        usarPaginacion = true;
        numeroPagina = 0;
    }

    @Override
    public String toString() {
        StringBuffer trace = new StringBuffer();
        trace.append("usarPaginacion").append(": ").append(usarPaginacion).append(", ");
        if (usarPaginacion) {
            trace.append("numeroPagina").append(": ").append(numeroPagina).append(", ");
            trace.append("resultadosPagina").append(": ").append(resultadosPagina).append(", ");
        }
        if (idModelo != null) {
            trace.append("idModelo").append(": ").append(idModelo).append(", ");
        }
        if (idMarca != null) {
            trace.append("idMarca").append(": ").append(idMarca).append(", ");
        }

        return trace.toString();
    }

    /**
     * Identificador de código de área.
     * @return the idMarca
     */
    public BigDecimal getIdMarca() {
        return idMarca;
    }

    /**
     * Identificador de código de área.
     * @param idMarca the idMarca to set
     */
    public void setIdMarca(BigDecimal idMarca) {
        this.idMarca = idMarca;
    }

    /**
     * Identificador de código de región.
     * @return the idModelo
     */
    public BigDecimal getIdModelo() {
        return idModelo;
    }

    /**
     * Identificador de código de región.
     * @param idModelo the idModelo to set
     */
    public void setIdModelo(BigDecimal idModelo) {
        this.idModelo = idModelo;
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

    // GETTERS & SETTERS

}
