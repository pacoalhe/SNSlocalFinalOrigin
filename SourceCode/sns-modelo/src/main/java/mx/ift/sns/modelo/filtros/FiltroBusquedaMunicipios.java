package mx.ift.sns.modelo.filtros;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;

import mx.ift.sns.modelo.central.Estatus;
import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.Region;

/**
 * Clase auxiliar para búsqueda de municipios. Contiene los filtros que negocio enviará a los DAOS para que construya
 * las querys. Cada variable asignada se considerará un filtro a satisfacer.
 * @author X36155QU
 */
public class FiltroBusquedaMunicipios implements Serializable {

    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    /** Estado. */
    private Estado estado;

    /** Codigo municipio. */
    private String codMunicipio;

    /** Nombre municipio. */
    private String nombre;

    /** Region celular. */
    private Region regionCelular;

    /** Region PCS. */
    private Region regionPcs;

    /** Estado del municipio. */
    private Estatus estatus;

    /** Indica si se ha de utilizar paginación en la búsqueda. */
    private boolean usarPaginacion = true;

    /** Usando paginación, indica en el número de bloque. */
    private int numeroPagina = 0;

    /** Usando paginación, indica el número de resultados por bloque. */
    private int resultadosPagina = 5;

    /**
     * Genera una lista con los filtros que se han definido.
     * @return Lista de objetos FiltroBusqueda con la información solicitada
     */
    public ArrayList<FiltroBusqueda> getListaFiltros() {

        ArrayList<FiltroBusqueda> filtros = new ArrayList<FiltroBusqueda>();

        // Búsqueda sobre tabla CAT_MUNICIPIOS -> m

        if (estado != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("estado", "m");
            filtro.setValor(estado, Estado.class);
            filtros.add(filtro);
        }

        if (codMunicipio != null) {
            if (!codMunicipio.equals("")) {
                FiltroBusqueda filtro = new FiltroBusqueda();
                filtro.setCampo("codMunicipio", "m.id");
                filtro.setValor(codMunicipio, String.class);
                filtros.add(filtro);
            }
        }

        if (nombre != null) {
            if (!nombre.equals("")) {
                FiltroBusqueda filtro = new FiltroBusqueda();
                filtro.setCampo("nombre", "m");
                filtro.setValor(nombre, String.class);
                filtros.add(filtro);
            }
        }

        if (regionCelular != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("regionCelular", "m");
            filtro.setValor(regionCelular, Region.class);
            filtros.add(filtro);
        }

        if (regionPcs != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("regionPcs", "m");
            filtro.setValor(regionPcs, Region.class);
            filtros.add(filtro);
        }

        if (estatus != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("estatus", "m");
            filtro.setValor(estatus, Estatus.class);
            filtros.add(filtro);
        }

        return filtros;
    }

    /**
     * Genera una lista con los filtros que se han definido para la exportacion de municipios.
     * @return Lista de objetos FiltroBusqueda con la información solicitada
     */
    public ArrayList<FiltroBusqueda> getListaFiltrosExport() {

        ArrayList<FiltroBusqueda> filtros = new ArrayList<FiltroBusqueda>();

        // Búsqueda sobre vista V_EXP_MUNICIPIO -> e

        if (estado != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("idEstado", "e");
            filtro.setValor(estado.getCodEstado(), String.class);
            filtros.add(filtro);
        }

        if (codMunicipio != null) {
            if (!codMunicipio.equals("")) {
                FiltroBusqueda filtro = new FiltroBusqueda();
                filtro.setCampo("idMunicipio", "e");
                filtro.setValor(codMunicipio, String.class);
                filtros.add(filtro);
            }
        }

        if (nombre != null) {
            if (!nombre.equals("")) {
                FiltroBusqueda filtro = new FiltroBusqueda();
                filtro.setCampo("nombreMunicipio", "e");
                filtro.setValor(nombre, String.class);
                filtros.add(filtro);
            }
        }

        if (regionCelular != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("regionCelular", "e");
            filtro.setValor(regionCelular.getIdRegion(), BigDecimal.class);
            filtros.add(filtro);
        }

        if (regionPcs != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("regionPcs", "e");
            filtro.setValor(regionPcs.getIdRegion(), BigDecimal.class);
            filtros.add(filtro);
        }

        return filtros;
    }

    /** Limpia los filtros. */
    public void clear() {
        estado = null;
        codMunicipio = null;
        nombre = null;
        regionCelular = null;
        regionPcs = null;

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
     * Codigo municipio.
     * @return the codMunicipio
     */
    public String getCodMunicipio() {
        return codMunicipio;
    }

    /**
     * Codigo municipio.
     * @param codMunicipio the codMunicipio to set
     */
    public void setCodMunicipio(String codMunicipio) {
        this.codMunicipio = codMunicipio;
    }

    /**
     * Nombre municipio.
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Nombre municipio.
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Region celular.
     * @return the regionCelular
     */
    public Region getRegionCelular() {
        return regionCelular;
    }

    /**
     * Region celular.
     * @param regionCelular the regionCelular to set
     */
    public void setRegionCelular(Region regionCelular) {
        this.regionCelular = regionCelular;
    }

    /**
     * Region PCS.
     * @return the regionPcs
     */
    public Region getRegionPcs() {
        return regionPcs;
    }

    /**
     * Region PCS.
     * @param regionPcs the regionPcs to set
     */
    public void setRegionPcs(Region regionPcs) {
        this.regionPcs = regionPcs;
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
     * Usando paginación, indica en el número de bloque..
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

    @Override
    public String toString() {
        return "FiltroBusquedaMunicipios [estado=" + estado + ", codMunicipio=" + codMunicipio + ", nombre=" + nombre
                + ", regionCelular=" + regionCelular + ", regionPcs=" + regionPcs + "]";
    }

    /**Estado del municipio.
     * @return the estatus
     */
    public Estatus getEstatus() {
        return estatus;
    }

    /**Estado del municipio.
     * @param estatus the estatus to set
     */
    public void setEstatus(Estatus estatus) {
        this.estatus = estatus;
    }

}
