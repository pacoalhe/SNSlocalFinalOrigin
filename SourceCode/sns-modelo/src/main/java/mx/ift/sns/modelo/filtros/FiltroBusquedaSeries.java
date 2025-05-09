package mx.ift.sns.modelo.filtros;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Map.Entry;

import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.pst.TipoModalidad;
import mx.ift.sns.modelo.pst.TipoRed;

/**
 * Clase auxiliar para búsqueda de series. Contiene los filtros que negocio enviará a los DAOS para que construya las
 * querys. Cada variable asignada se considerará un filtro a satisfacer.
 */
public class FiltroBusquedaSeries implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Identificador de código de área. */
    private BigDecimal idAbn;

    /** Identificador de código de región. */
    private BigDecimal idNir;

    /** Identificador de código de región. */
    private Integer cdgNir;

    /** Identificador de numeración. */
    private BigDecimal idSna;

    /** Clave de servicio. */
    private BigDecimal clave;

    /** Ocupacion. */
    private BigDecimal ocupacion;

    /** Disponible. */
    private BigDecimal disponible;

    /** Indicador de serie libre. */
    private boolean serieLibre = false;

    /** Proveedor arrendatario de los rangos pertenecientes a la serie. */
    private Proveedor arrendatario;

    /** Proveedor concesionario de los rangos pertenecientes a la serie. */
    private Proveedor concesionario;

    /** Proveedor asignatario de los rangos pertenecientes a la serie. */
    private Proveedor asignatario;

    /** Indica que se van a buscar serie de distinto asignatario. */
    private boolean otroAsignatario = false;

    /** Indica si se ha de utilizar paginación en la búsqueda. */
    private boolean usarPaginacion = false;

    /** Indica si se ha de buscar series con rangos disponibles. */
    private boolean rangosDisponibles = false;

    /** Usando paginación, indica en el número de bloque. */
    private int numeroPagina = 0;

    /** Usando paginación, indica el número de resultados por bloque. */
    private int resultadosPagina = 10;

    /** Tipo de Red asociada al Rango. */
    private TipoRed tipoRed;

    /** Tipo de Modalidad asociada al Rango para redes móviles. */
    private TipoModalidad tipoModalidad;

    /** Tipo de ordenacion por NIR. */
    private String nirOrder;

    /** Tipo de ordenacion por clave. */
    private String claveOrder;

    /** Tipo de ordenacion por SNA. */
    private String snaOrder;

    /** estado de la serie. */
    private String estatus;

    /** variable que indica si hay o no filtros. */
    private boolean sinFiltros;

    /**
     * Genera una lista con los filtros que se han definido sobre la tabla de Series.
     * @return Lista de pares (clave, valor)
     */
    public ArrayList<Entry<String, Object>> getFiltrosSerie() {

        ArrayList<Entry<String, Object>> filtros = new ArrayList<Entry<String, Object>>();

        if (idAbn != null) {
            Abn abn = new Abn();
            abn.setCodigoAbn(idAbn);
            Entry<String, Object> entry = new SimpleEntry<String, Object>("nir.abn", abn);
            filtros.add(entry);
        }

        if (idNir != null) {
            Entry<String, Object> entry = new SimpleEntry<String, Object>("id.idNir", idNir);
            filtros.add(entry);
        }

        if (cdgNir != null) {
            Entry<String, Object> entry = new SimpleEntry<String, Object>("nir.codigo", cdgNir);
            filtros.add(entry);
        }

        if (idSna != null) {
            Entry<String, Object> entry = new SimpleEntry<String, Object>("id.sna", idSna);
            filtros.add(entry);
        }

        if (clave != null) {
            Entry<String, Object> entry = new SimpleEntry<String, Object>("id.idClaveServicio", clave);
            filtros.add(entry);
        }

        if (ocupacion != null) {
            Entry<String, Object> entry = new SimpleEntry<String, Object>("ocupacion", ocupacion);
            filtros.add(entry);
        }

        if (disponible != null) {
            Entry<String, Object> entry = new SimpleEntry<String, Object>("disponible", disponible);
            filtros.add(entry);
        }

        return filtros;
    }

    /**
     * Genera una lista con los filtros que se han definido sobre la tabla de Rangos.
     * @return Lista de pares (clave, valor)
     */
    public ArrayList<Entry<String, Object>> getFiltrosRango() {

        ArrayList<Entry<String, Object>> filtros = new ArrayList<Entry<String, Object>>();

        if (asignatario != null) {
            Entry<String, Object> entry = new SimpleEntry<String, Object>("asignatario", asignatario);
            filtros.add(entry);
        }

        if (arrendatario != null) {
            Entry<String, Object> entry = new SimpleEntry<String, Object>("arrendatario", arrendatario);
            filtros.add(entry);
        }

        if (concesionario != null) {
            Entry<String, Object> entry = new SimpleEntry<String, Object>("concesionario", concesionario);
            filtros.add(entry);
        }

        if (tipoRed != null) {
            Entry<String, Object> entry = new SimpleEntry<String, Object>("tipoRed", tipoRed);
            filtros.add(entry);
        }

        if (tipoModalidad != null) {
            Entry<String, Object> entry = new SimpleEntry<String, Object>("tipoModalidad", tipoModalidad);
            filtros.add(entry);
        }

        return filtros;
    }

    /**
     * Genera una lista con los filtros que se han definido.
     * @return Lista de objetos FiltroBusqueda con la información solicitada
     */
    public ArrayList<FiltroBusqueda> getListaFiltrosCatalogoSerie() {

        ArrayList<FiltroBusqueda> filtros = new ArrayList<FiltroBusqueda>();

        // Búsqueda sobre la vista V_CATALOGO_SERIE
        if (idAbn != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("idAbn", "vcs");
            filtro.setValor(String.valueOf(idAbn), String.class);
            filtros.add(filtro);
        }

        if (idSna != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("idSna", "vcs");
            filtro.setValor(String.valueOf(idSna), String.class);
            filtros.add(filtro);
        }

        if (idNir != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("cdgNir", "vcs");
            filtro.setValor(String.valueOf(idNir), String.class);
            filtros.add(filtro);
        }

        if (estatus != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("nombreEstatus", "vcs");
            filtro.setValor(estatus, String.class);
            filtros.add(filtro);
        }

        return filtros;
    }

    /** Limpia los filtros. */
    public void clear() {
        idAbn = null;
        idSna = null;
        idNir = null;
        cdgNir = null;
        serieLibre = false;
        otroAsignatario = false;
        asignatario = null;
        concesionario = null;
        arrendatario = null;
        usarPaginacion = false;
        numeroPagina = 0;
        resultadosPagina = 0;
        tipoModalidad = null;
        tipoRed = null;
        estatus = null;
        disponible = null;
        ocupacion = null;
    }

    @Override
    public String toString() {
        StringBuffer trace = new StringBuffer();
        trace.append("idAbn").append(": ").append(idAbn).append(", ");
        trace.append("idSna").append(": ").append(idSna).append(", ");
        trace.append("idNir").append(": ").append(idNir).append(", ");
        trace.append("cdgNir").append(": ").append(cdgNir).append(", ");
        trace.append("serieLibre").append(": ").append(serieLibre).append(", ");
        trace.append("asignatario").append(": ").append(asignatario).append(", ");
        trace.append("concesionario").append(": ").append(concesionario).append(", ");
        trace.append("arrendatario").append(": ").append(arrendatario).append(", ");
        trace.append("usarPaginacion").append(": ").append(usarPaginacion).append(", ");
        trace.append("numeroPagina").append(": ").append(numeroPagina).append(", ");
        trace.append("resultadosPagina").append(": ").append(resultadosPagina).append(", ");
        trace.append("tipoModalidad").append(": ").append(tipoModalidad).append(", ");
        trace.append("tipoRed").append(": ").append(tipoRed);
        trace.append("estatus").append(": ").append(estatus);
        return trace.toString();
    }

    // GETTERS & SETTERS

    /**
     * Identificador de código de área.
     * @return BigDecimal
     */
    public BigDecimal getIdAbn() {
        return idAbn;
    }

    /**
     * Identificador de código de área.
     * @param idAbn BigDecimal
     */
    public void setIdAbn(BigDecimal idAbn) {
        this.idAbn = idAbn;
    }

    /**
     * Identificador de numeración.
     * @return BigDecimal
     */
    public BigDecimal getIdSna() {
        return idSna;
    }

    /**
     * Identificador de numeración.
     * @param idSna BigDecimal
     */
    public void setIdSna(BigDecimal idSna) {
        this.idSna = idSna;
    }

    /**
     * Indicador de serie libre.
     * @return True si la serie no tiene rangos.
     */
    public boolean isSerieLibre() {
        return serieLibre;
    }

    /**
     * Indicador de serie libre.
     * @param serieLibre True si la serie no tiene rangos.
     */
    public void setSerieLibre(boolean serieLibre) {
        this.serieLibre = serieLibre;
    }

    /**
     * Proveedor arrendatario de los rangos pertenecientes a la serie.
     * @return Proveedor arrendatario
     */
    public Proveedor getArrendatario() {
        return arrendatario;
    }

    /**
     * Proveedor arrendatario de los rangos pertenecientes a la serie.
     * @param arrendatario Proveedor arrendatario
     */
    public void setArrendatario(Proveedor arrendatario) {
        this.arrendatario = arrendatario;
    }

    /**
     * Proveedor concesionario de los rangos pertenecientes a la serie.
     * @return Proveedor concesionario
     */
    public Proveedor getConcesionario() {
        return concesionario;
    }

    /**
     * Proveedor concesionario de los rangos pertenecientes a la serie.
     * @param concesionario Proveedor concesionario
     */
    public void setConcesionario(Proveedor concesionario) {
        this.concesionario = concesionario;
    }

    /**
     * Proveedor asignatario de los rangos pertenecientes a la serie.
     * @return Proveedor asignatario
     */
    public Proveedor getAsignatario() {
        return asignatario;
    }

    /**
     * Proveedor asignatario de los rangos pertenecientes a la serie.
     * @param asignatario Proveedor asignatario
     */
    public void setAsignatario(Proveedor asignatario) {
        this.asignatario = asignatario;
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
     * @return numeroPagina int
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

    /**
     * Tipo de Red asociada al Rango.
     * @return TipoRed
     */
    public TipoRed getTipoRed() {
        return tipoRed;
    }

    /**
     * Tipo de Red asociada al Rango.
     * @param tipoRed TipoRed
     */
    public void setTipoRed(TipoRed tipoRed) {
        this.tipoRed = tipoRed;
    }

    /**
     * Tipo de Modalidad asociada al Rango para redes móviles.
     * @return TipoModalidad
     */
    public TipoModalidad getTipoModalidad() {
        return tipoModalidad;
    }

    /**
     * Tipo de Modalidad asociada al Rango para redes móviles.
     * @param tipoModalidad TipoModalidad
     */
    public void setTipoModalidad(TipoModalidad tipoModalidad) {
        this.tipoModalidad = tipoModalidad;
    }

    /**
     * Identificador de código de región.
     * @return BigDecimal
     */
    public BigDecimal getIdNir() {
        return idNir;
    }

    /**
     * Identificador de código de región.
     * @param idNir BigDecimal
     */
    public void setIdNir(BigDecimal idNir) {
        this.idNir = idNir;
    }

    /**
     * Identificador de código de región.
     * @return the cdgNir
     */
    public Integer getCdgNir() {
        return cdgNir;
    }

    /**
     * Identificador de código de región.
     * @param cdgNir the cdgNir to set
     */
    public void setCdgNir(Integer cdgNir) {
        this.cdgNir = cdgNir;
    }

    /**
     * Tipo de ordenacion por NIR.
     * @return the nirOrder
     */
    public String getNirOrder() {
        return nirOrder;
    }

    /**
     * Tipo de ordenacion por NIR.
     * @param nirOrder the nirOrder to set
     */
    public void setNirOrder(String nirOrder) {
        this.nirOrder = nirOrder;
    }

    /**
     * Tipo de ordenacion por SNA.
     * @return the snaOrder
     */
    public String getSnaOrder() {
        return snaOrder;
    }

    /**
     * Tipo de ordenacion por SNA.
     * @param snaOrder the snaOrder to set
     */
    public void setSnaOrder(String snaOrder) {
        this.snaOrder = snaOrder;
    }

    /**
     * estado de la serie.
     * @return the estatus
     */
    public String getEstatus() {
        return estatus;
    }

    /**
     * estado de la serie.
     * @param estatus the estatus to set
     */
    public void setEstatus(String estatus) {
        this.estatus = estatus;
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
     * Clave de servicio.
     * @return the clave
     */
    public BigDecimal getClave() {
        return clave;
    }

    /**
     * Clave de servicio.
     * @param clave the clave to set
     */
    public void setClave(BigDecimal clave) {
        this.clave = clave;
    }

    /**
     * Indica que se van a buscar serie de distinto asignatario.
     * @return the otroAsignatario
     */
    public boolean isOtroAsignatario() {
        return otroAsignatario;
    }

    /**
     * Indica que se van a buscar serie de distinto asignatario.
     * @param otroAsignatario the otroAsignatario to set
     */
    public void setOtroAsignatario(boolean otroAsignatario) {
        this.otroAsignatario = otroAsignatario;
    }

    /**
     * Indica si se ha de buscar series con rangos disponibles.
     * @return the rangosDisponibles
     */
    public boolean isRangosDisponibles() {
        return rangosDisponibles;
    }

    /**
     * Indica si se ha de buscar series con rangos disponibles.
     * @param rangosDisponibles the rangosDisponibles to set
     */
    public void setRangosDisponibles(boolean rangosDisponibles) {
        this.rangosDisponibles = rangosDisponibles;
    }

    /**
     * Tipo de ordenacion por clave.
     * @return the claveOrder
     */
    public String getClaveOrder() {
        return claveOrder;
    }

    /**
     * Tipo de ordenacion por clave.
     * @param claveOrder the claveOrder to set
     */
    public void setClaveOrder(String claveOrder) {
        this.claveOrder = claveOrder;
    }

    /**
     * @return the ocupacion
     */
    public BigDecimal getOcupacion() {
        return ocupacion;
    }

    /**
     * @param ocupacion the ocupacion to set
     */
    public void setOcupacion(BigDecimal ocupacion) {
        this.ocupacion = ocupacion;
    }

    /**
     * @return the disponible
     */
    public BigDecimal getDisponible() {
        return disponible;
    }

    /**
     * @param disponible the disponible to set
     */
    public void setDisponible(BigDecimal disponible) {
        this.disponible = disponible;
    }

}
