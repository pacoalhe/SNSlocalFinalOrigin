package mx.ift.sns.modelo.filtros;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;

import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.pst.Proveedor;

/**
 * Clase auxiliar para búsqueda de solicitudes genérica. Contiene los filtros que negocio enviará a los DAOS para que
 * construya las querys. Cada variable asignada se considerará un filtro a satisfacer.
 */
public class FiltroBusquedaCentrales implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    // private static final Logger LOGGER = LoggerFactory.getLogger(FiltroBusquedaCentrales.class);

    /** Indica si se ha de utilizar paginación en la búsqueda. */
    private boolean usarPaginacion = false;

    /** Usando paginación, indica en el número de bloque. */
    private int numeroPagina = 0;

    /** Usando paginación, indica el número de resultados por bloque. */
    private int resultadosPagina = 10;

    /** Nombre de la central. */
    private String nombre;

    /** Proveedor de servicios PST. */
    private Proveedor proveedor;

    /** abn. **/
    private BigDecimal abns;

    /** Estado de Poblacion. */
    private Poblacion poblacion;

    /** variable que indica si hay o no filtros. */
    private boolean sinFiltros;

    /** Estado de Estado. */
    private Estado estadoMun;

    /** Estado de Municipio. */
    private Municipio municipio;

    /** Constructor, por defect vacío. */
    public FiltroBusquedaCentrales() {
    }

    /**
     * Genera una lista con los filtros que se han definido.
     * @return Lista de objetos FiltroBusqueda con la información solicitada
     */
    public ArrayList<FiltroBusqueda> getListaFiltros() {

        ArrayList<FiltroBusqueda> filtros = new ArrayList<FiltroBusqueda>();

        // Búsqueda sobre la vista V_CATALOGO_CENTRAL
        if (nombre != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("nombre", "vcc");
            filtro.setValor(nombre, String.class);
            filtros.add(filtro);
        }

        if (poblacion != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("inegi", "vcc");
            filtro.setValor(poblacion.getInegi(), String.class);
            filtros.add(filtro);

        }

        if (estadoMun != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("codEstado", "vcc");
            filtro.setValor(estadoMun.getCodEstado(), String.class);
            filtros.add(filtro);

        }

        if (municipio != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("codMunicipio", "vcc");
            filtro.setValor(municipio.getId().getCodMunicipio(), String.class);
            filtros.add(filtro);

        }

        if (proveedor != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("proveedor", "vcc");
            filtro.setValor(proveedor.getId().toString(), String.class);
            filtros.add(filtro);
        }

        if (abns != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("abns", "vcc");
            filtro.setValor(abns, BigDecimal.class);
            filtros.add(filtro);
        }

        return filtros;
    }

    /** Limpia los filtros. */
    public void clear() {
        nombre = null;
        poblacion = null;
        proveedor = null;
        abns = null;
        estadoMun = null;
        municipio = null;
    }

    @Override
    public String toString() {
        StringBuilder trace = new StringBuilder();
        if (nombre != null) {
            trace.append("nombre").append(": ").append(nombre).append(", ");
        }
        if (proveedor != null) {
            trace.append("proveedor").append(": ").append(proveedor).append(", ");
        }
        if (poblacion != null) {
            trace.append("poblacion").append(": ").append(poblacion).append(", ");
        }
        if (estadoMun != null) {
            trace.append("estadoMun").append(": ").append(estadoMun).append(", ");
        }
        if (municipio != null) {
            trace.append("municipio").append(": ").append(municipio).append(", ");
        }
        if (abns != null) {
            trace.append("abnCentrales").append(": ").append(abns).append(", ");
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
     * Nombre de la central.
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Nombre de la central.
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Proveedor de servicios PST.
     * @return the proveedor
     */
    public Proveedor getProveedor() {
        return proveedor;
    }

    /**
     * Proveedor de servicios PST.
     * @param proveedor the proveedor to set
     */
    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    /**
     * abn.
     * @return the abns
     */
    public BigDecimal getAbns() {
        return abns;
    }

    /**
     * abn.
     * @param abns the abns to set
     */
    public void setAbns(BigDecimal abns) {
        this.abns = abns;
    }

    /**
     * @return the poblacion
     */
    public Poblacion getPoblacion() {
        return poblacion;
    }

    /**
     * Estado de Poblacion.
     * @param poblacion the poblacion to set
     */
    public void setPoblacion(Poblacion poblacion) {
        this.poblacion = poblacion;
    }

    /**
     * Estado de Poblacion.
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
     * Indica si se ha de utilizar paginación en la búsqueda.
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
     * Usando paginación, indica en el número de bloque.
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
     * variable que indica si hay o no filtros.
     * @return the sinFiltros
     */
    public boolean isSinFiltros() {
        return sinFiltros;
    }

    /**
     * variable que indica si hay o no filtros.
     * @param sinFiltros the sinFiltros to set
     */
    public void setSinFiltros(boolean sinFiltros) {
        this.sinFiltros = sinFiltros;
    }

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

    /**
     * Estado de Municipio.
     * @return the municipio
     */
    public Municipio getMunicipio() {
        return municipio;
    }

    /**
     * Estado de Municipio.
     * @param municipio the municipio to set
     */
    public void setMunicipio(Municipio municipio) {
        this.municipio = municipio;
    }
}
