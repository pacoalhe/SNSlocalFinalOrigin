package mx.ift.sns.modelo.filtros;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;

import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.Poblacion;

/**
 * Clase auxiliar para búsqueda de poblaciones. Contiene los filtros que negocio enviará a los DAOS para que construya
 * las querys. Cada variable asignada se considerará un filtro a satisfacer.
 */
public class FiltroBusquedaPoblaciones implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    // private static final Logger LOGGER = LoggerFactory.getLogger(FiltroBusquedaPoblaciones.class);

    /** Constante para el alias de la tabla de proveedores. */
    private static final String FILTRO_ALIAS_TABLA_POBLACION = "p";
    /** Constante para el alias de la tabla de PoblacionAbn. */
    private static final String FILTRO_ALIAS_TABLA_POBLACION_ABN = "pAbn";
    /** Constante para el campo estado. */
    private static final String FILTRO_ESTADO = "municipio.id.codEstado";
    /** Constante para el campo municipio. */
    private static final String FILTRO_MUNICIPIO = "municipio.id.codMunicipio";
    /** Constante para el campo ABN. */
    private static final String FILTRO_ABN = "abn.codigoAbn";
    /** Constante para el campo población. */
    private static final String FILTRO_POBLACION = "cdgPoblacion";
    /** Constante para el campo estatus. */
    // private static final String FILTRO_ESTATUS = "estatus";

    /** Estado. */
    private Estado estado;

    /** Municipio. */
    private Municipio municipio;

    /** Identificador de código de área. */
    private Integer idAbn;

    /** Poblacion. */
    private Poblacion poblacion;

    /** Indica si se ha de utilizar paginación en la búsqueda. */
    private boolean usarPaginacion = false;

    /** Usando paginación, indica en el número de bloque. */
    private int numeroPagina = 0;

    /** Usando paginación, indica el número de resultados por bloque. */
    private int resultadosPagina = 10;

    /** Constructor, por defect vacío. */
    public FiltroBusquedaPoblaciones() {
    }

    /**
     * Genera una lista con los filtros que se han definido.
     * @return Lista de objetos FiltroBusqueda con la información solicitada
     */
    public ArrayList<FiltroBusqueda> getFiltrosPoblacion() {

        ArrayList<FiltroBusqueda> filtros = new ArrayList<FiltroBusqueda>();

        // Búsqueda sobre tabla CAT_POBLACION -> po
        if (estado != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo(FILTRO_ESTADO, FILTRO_ALIAS_TABLA_POBLACION);
            filtro.setValor(estado.getCodEstado(), String.class);
            filtros.add(filtro);
        }
        if (municipio != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo(FILTRO_MUNICIPIO, FILTRO_ALIAS_TABLA_POBLACION);
            filtro.setValor(municipio.getId().getCodMunicipio(), String.class);
            filtros.add(filtro);
        }
        if (idAbn != null) {
            Abn abnAux = new Abn();
            abnAux.setCodigoAbn(new BigDecimal(idAbn));
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo(FILTRO_ABN, FILTRO_ALIAS_TABLA_POBLACION_ABN);
            filtro.setValor(abnAux.getCodigoAbn(), BigDecimal.class);
            filtros.add(filtro);
        }
        if (poblacion != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo(FILTRO_POBLACION, FILTRO_ALIAS_TABLA_POBLACION);
            filtro.setValor(poblacion.getCdgPoblacion(), String.class);
            filtros.add(filtro);
        }

        return filtros;
    }

    /** Limpia los filtros. */
    public void clear() {
        estado = null;
        municipio = null;
        idAbn = null;
        poblacion = null;

        usarPaginacion = false;
        numeroPagina = 0;
        resultadosPagina = 0;
    }

    @Override
    public String toString() {
        StringBuilder trace = new StringBuilder();
        if (estado != null) {
            trace.append("Estado").append(": ").append(estado.getCodEstado()).append(", ");
        }
        if (municipio != null) {
            trace.append("Municipio").append(": ").append(municipio.getId()).append(", ");
        }
        if (idAbn != null) {
            trace.append("idAbn").append(": ").append(idAbn).append(", ");
        }
        if (poblacion != null) {
            trace.append("Poblacion").append(": ").append(poblacion).append(", ");
        }
        return trace.toString();
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
     * Identificador de código de área.
     * @return the idAbn
     */
    public Integer getIdAbn() {
        return idAbn;
    }

    /**
     * Identificador de código de área.
     * @param idAbn the idAbn to set
     */
    public void setIdAbn(Integer idAbn) {
        this.idAbn = idAbn;
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

}
