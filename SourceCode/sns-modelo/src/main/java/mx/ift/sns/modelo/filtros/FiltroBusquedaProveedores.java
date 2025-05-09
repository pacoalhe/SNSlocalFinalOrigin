package mx.ift.sns.modelo.filtros;

import java.io.Serializable;
import java.util.ArrayList;

import mx.ift.sns.modelo.central.Estatus;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.pst.TipoProveedor;
import mx.ift.sns.modelo.pst.TipoRed;
import mx.ift.sns.modelo.pst.TipoServicio;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;

/**
 * Clase auxiliar para búsqueda de proveedores. Contiene los filtros que negocio enviará a los DAOS para que construya
 * las querys. Cada variable asignada se considerará un filtro a satisfacer.
 */
public class FiltroBusquedaProveedores implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    // private static final Logger LOGGER = LoggerFactory.getLogger(FiltroBusquedaProveedores.class);

    /** Constante para el alias de la tabla de proveedores. */
    private static final String FILTRO_ALIAS_TABLA = "pst";
    /** Constante para el campo nombre. */
    private static final String FILTRO_NOMBRE = "nombre";
    /** Constante para el campo tipo Servicio. */
    private static final String FILTRO_TIPO_SERVICIO = "tipoServicio";
    /** Constante para el campo tipo proveedor. */
    private static final String FILTRO_TIPO_PST = "tipoProveedor";
    /** Constante para el campo tipo red. */
    private static final String FILTRO_TIPO_RED = "tipoRed";
    /** Constante para el campo estatus. */
    private static final String FILTRO_ESTADO = "estatus";

    /** Proveedor de servicios PST. */
    private Proveedor proveedor;

    /** Tipo de Servicio. */
    private TipoServicio tipoServicio;

    /** Tipo de PST. */
    private TipoProveedor tipoProveedor;

    /** Tipo de Red. */
    private TipoRed tipoRed;

    /** Estado del PST. */
    private Estatus estado;

    /** Indica si se ha de utilizar paginación en la búsqueda. */
    private boolean usarPaginacion = false;

    /** Usando paginación, indica en el número de bloque. */
    private int numeroPagina = 0;

    /** Usando paginación, indica el número de resultados por bloque. */
    private int resultadosPagina = 10;

    /** Flag de precarga de datos al entrar en el catalogo. */
    private boolean preCarga;

    /** Constructor, por defect vacío. */
    public FiltroBusquedaProveedores() {
    }

    /**
     * Genera una lista con los filtros que se han definido.
     * @return Lista de objetos FiltroBusqueda con la información solicitada
     */
    public ArrayList<FiltroBusqueda> getFiltrosProveedor() {

        ArrayList<FiltroBusqueda> filtros = new ArrayList<FiltroBusqueda>();

        // Búsqueda sobre tabla CAT_PST -> pst
        if (proveedor != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo(FILTRO_NOMBRE, FILTRO_ALIAS_TABLA);
            filtro.setValor(proveedor.getNombre(), String.class);
            filtros.add(filtro);
        }

        if (tipoServicio != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo(FILTRO_TIPO_SERVICIO, FILTRO_ALIAS_TABLA);
            filtro.setValor(tipoServicio, TipoServicio.class);
            filtros.add(filtro);
        }

        if (tipoProveedor != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo(FILTRO_TIPO_PST, FILTRO_ALIAS_TABLA);
            filtro.setValor(tipoProveedor, TipoProveedor.class);
            filtros.add(filtro);
        }

        if (tipoRed != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo(FILTRO_TIPO_RED, FILTRO_ALIAS_TABLA);
            filtro.setValor(tipoRed, TipoRed.class);
            filtros.add(filtro);
        }

        if (estado != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo(FILTRO_ESTADO, FILTRO_ALIAS_TABLA);
            filtro.setValor(estado, EstadoSolicitud.class);
            filtros.add(filtro);
        }

        return filtros;
    }

    /** Limpia los filtros. */
    public void clear() {
        proveedor = null;
        tipoServicio = null;
        tipoProveedor = null;
        tipoRed = null;
        estado = null;
        usarPaginacion = false;
        numeroPagina = 0;
        resultadosPagina = 0;
        preCarga = false;
    }

    @Override
    public String toString() {
        StringBuilder trace = new StringBuilder();
        if (proveedor != null) {
            trace.append("Proveedor").append(": ").append(proveedor.getNombre()).append(", ");
        }
        if (tipoServicio != null) {
            trace.append("TipoServicio").append(": ").append(tipoServicio.getCdg()).append(", ");
        }
        if (tipoProveedor != null) {
            trace.append("TipoProveedor").append(": ").append(tipoProveedor.getCdg()).append(", ");
        }
        if (tipoRed != null) {
            trace.append("TipoRed").append(": ").append(tipoRed.getCdg()).append(", ");
        }
        if (estado != null) {
            trace.append("Estado").append(": ").append(estado.getCdg()).append(", ");
        }

        return trace.toString();
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
     * Tipo de Servicio.
     * @return the tipoServicio
     */
    public TipoServicio getTipoServicio() {
        return tipoServicio;
    }

    /**
     * Tipo de Servicio.
     * @param tipoServicio the tipoServicio to set
     */
    public void setTipoServicio(TipoServicio tipoServicio) {
        this.tipoServicio = tipoServicio;
    }

    /**
     * Tipo de PST.
     * @return the tipoProveedor
     */
    public TipoProveedor getTipoProveedor() {
        return tipoProveedor;
    }

    /**
     * Tipo de PST.
     * @param tipoProveedor the tipoProveedor to set
     */
    public void setTipoProveedor(TipoProveedor tipoProveedor) {
        this.tipoProveedor = tipoProveedor;
    }

    /**
     * Tipo de Red.
     * @return the tipoRed
     */
    public TipoRed getTipoRed() {
        return tipoRed;
    }

    /**
     * Tipo de Red.
     * @param tipoRed the tipoRed to set
     */
    public void setTipoRed(TipoRed tipoRed) {
        this.tipoRed = tipoRed;
    }

    /**
     * Estado del PST.
     * @return the estado
     */
    public Estatus getEstado() {
        return estado;
    }

    /**
     * Estado del PST.
     * @param estado the estado to set
     */
    public void setEstado(Estatus estado) {
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
