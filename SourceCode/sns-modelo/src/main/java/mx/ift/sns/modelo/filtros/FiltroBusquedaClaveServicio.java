package mx.ift.sns.modelo.filtros;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;

import mx.ift.sns.modelo.central.Estatus;

/**
 * Clase auxiliar para búsqueda de claves de servicio. Contiene los filtros que negocio enviará a los DAOS para que
 * construya las querys. Cada variable asignada se considerará un filtro a satisfacer.
 */
public class FiltroBusquedaClaveServicio implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    // private static final Logger LOGGER = LoggerFactory.getLogger(FiltroBusquedaClaveServicio.class);

    /** Constante para el alias de la tabla de proveedores. */
    private static final String FILTRO_ALIAS_TABLA = "cs";
    /** Constante para el campo nombre. */
    private static final String FILTRO_CODIGO = "codigo";
    /** Constante para el campo tipo proveedor. */
    private static final String FILTRO_ESTATUS = "estatus";

    /** Código de la clave de servicio. */
    private BigDecimal codigo;

    /** Estatus de la Clave de Servicio. */
    private Estatus estatus;

    /** Indica si se ha de utilizar paginación en la búsqueda. */
    private boolean usarPaginacion = false;

    /** Usando paginación, indica en el número de bloque. */
    private int numeroPagina = 0;

    /** Usando paginación, indica el número de resultados por bloque. */
    private int resultadosPagina = 10;

    /** Flag de precarga de datos al entrar en el catalogo. */
    private boolean preCarga;

    /** Constructor, por defect vacío. */
    public FiltroBusquedaClaveServicio() {
    }

    /**
     * Genera una lista con los filtros que se han definido.
     * @return Lista de objetos FiltroBusqueda con la información solicitada
     */
    public ArrayList<FiltroBusqueda> getFiltrosClaveServicio() {

        ArrayList<FiltroBusqueda> filtros = new ArrayList<FiltroBusqueda>();

        // Búsqueda sobre tabla CAT_CLAVE_SERVICIO con alias claveServicio
        if (codigo != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo(FILTRO_CODIGO, FILTRO_ALIAS_TABLA);
            filtro.setValor(codigo, BigDecimal.class);
            filtros.add(filtro);
        }

        if (estatus != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo(FILTRO_ESTATUS, FILTRO_ALIAS_TABLA);
            filtro.setValor(estatus, Estatus.class);
            filtros.add(filtro);
        }

        return filtros;
    }

    /** Limpia los filtros. */
    public void clear() {
        codigo = null;
        estatus = null;
        usarPaginacion = false;
        numeroPagina = 0;
        resultadosPagina = 0;
        preCarga = false;
    }

    /**
     * codigo.
     * @return the codigo
     */
    public BigDecimal getCodigo() {
        return codigo;
    }

    /**
     * codigo.
     * @param codigo the codigo to set
     */
    public void setCodigo(BigDecimal codigo) {
        this.codigo = codigo;
    }

    /**
     * Estatus de la Clave de Servicio.
     * @return the estatus
     */
    public Estatus getEstatus() {
        return estatus;
    }

    /**
     * Estatus de la Clave de Servicio.
     * @param estatus the estatus to set
     */
    public void setEstatus(Estatus estatus) {
        this.estatus = estatus;
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
     * Flag de precarga de datos al entrar en el catalogo.
     * @return the preCarga
     */
    public boolean isPreCarga() {
        return preCarga;
    }

    /**
     * Flag de precarga de datos al entrar en el catalogo.
     * @param preCarga the preCarga to set
     */
    public void setPreCarga(boolean preCarga) {
        this.preCarga = preCarga;
    }

}
