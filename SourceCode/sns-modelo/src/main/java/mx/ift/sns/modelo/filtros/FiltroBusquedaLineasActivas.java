package mx.ift.sns.modelo.filtros;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

import mx.ift.sns.modelo.nng.ClaveServicio;
import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.pst.Proveedor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase auxiliar para búsqueda de solicitudes genérica. Contiene los filtros que negocio enviará a los DAOS para que
 * construya las querys. Cada variable asignada se considerará un filtro a satisfacer.
 */
public class FiltroBusquedaLineasActivas implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(FiltroBusquedaLineasActivas.class);

    /** Proveedor de servicios PST. */
    private Proveedor pst;

    /** ABN. */
    private String abn;

    /** Inegi. */
    private String claveCensal;

    /** Estado. */
    private Estado estado;

    /** Municipio. */
    private Municipio municipio;

    /** Indica si se ha de utilizar paginación en la búsqueda. */
    private Poblacion poblacion;

    /** Fecha inicial del reporte. */
    private Date fechaInicial;

    /** Fecha final del reporte. */
    private Date fechaFinal;

    /** Indica si se ha de utilizar paginación en la búsqueda. */
    private boolean usarPaginacion = false;

    /** Usando paginación, indica en el número de bloque. */
    private int numeroPagina = 0;

    /** Usando paginación, indica el número de resultados por bloque. */
    private int resultadosPagina = 10;

    /** Consecutivo, entero de 9 dígitos (String) en la tabla. */
    private String consecutivo;

    /** Clave de servicio. */
    private ClaveServicio claveServicio;

    /** La consulta es de tipo historico. */
    private Boolean historico;

    /**
     * Genera una lista con los filtros que se han definido para NG.
     * @return Lista de objetos FiltroBusqueda con la información solicitada
     */
    public ArrayList<FiltroBusqueda> getListaFiltrosNg() {
        ArrayList<FiltroBusqueda> filtros = new ArrayList<FiltroBusqueda>();

        if (pst != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("proveedor", "d");
            filtro.setValor(pst, Proveedor.class);
            filtros.add(filtro);
        }

        if (abn != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("codigoAbn", "pabn.abn");
            filtro.setValor(new BigDecimal(abn), BigDecimal.class);
            filtros.add(filtro);
        }

        if (claveCensal != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("inegi", "d.poblacion");
            filtro.setValor(claveCensal, String.class);
            filtros.add(filtro);
        }

        if (estado != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("estado", "d.poblacion.municipio");
            filtro.setValor(estado, Estado.class);
            filtros.add(filtro);
        }

        if (municipio != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("municipio", "d.poblacion");
            filtro.setValor(municipio, Municipio.class);
            filtros.add(filtro);
        }

        if (poblacion != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("poblacion", "d");
            filtro.setValor(poblacion, Poblacion.class);
            filtros.add(filtro);
        }

        if (fechaInicial != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("fechaInicial", "d");
            filtro.setValor(fechaInicial, Date.class);
            filtro.setFechaDesde(true);
            filtros.add(filtro);
        }

        if (fechaFinal != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("fechaFinal", "d");
            filtro.setValor(fechaFinal, Date.class);
            filtro.setFechaHasta(true);
            filtros.add(filtro);
        }

        if (consecutivo != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("consecutivo", "d");
            filtro.setValor(new BigDecimal(consecutivo), BigDecimal.class);
            filtros.add(filtro);
        }
        return filtros;
    }

    /**
     * Genera una lista con los filtros que se han definido para NNG.
     * @return Lista de objetos FiltroBusqueda con la información solicitada
     */
    public ArrayList<FiltroBusqueda> getListaFiltrosNng() {
        ArrayList<FiltroBusqueda> filtros = new ArrayList<FiltroBusqueda>();

        if (pst != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("proveedor", "d");
            filtro.setValor(pst, Proveedor.class);
            filtros.add(filtro);
        }

        if (fechaInicial != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("fechaInicial", "d");
            filtro.setValor(fechaInicial, Date.class);
            filtro.setFechaDesde(true);
            filtros.add(filtro);
        }

        if (fechaFinal != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("fechaFinal", "d");
            filtro.setValor(fechaFinal, Date.class);
            filtro.setFechaHasta(true);
            filtros.add(filtro);
        }

        if (consecutivo != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("consecutivo", "d");
            filtro.setValor(new BigDecimal(consecutivo), BigDecimal.class);
            filtros.add(filtro);
        }

        if (claveServicio != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("claveServicio", "d");
            filtro.setValor(claveServicio, ClaveServicio.class);
            filtros.add(filtro);
        }
        return filtros;
    }

    /**
     * Limpia los filtros.
     */
    public void clear() {

        LOGGER.debug("");

        pst = null;
        abn = null;
        estado = null;
        municipio = null;
        poblacion = null;
        fechaInicial = null;
        fechaFinal = null;
        claveCensal = null;
        consecutivo = null;
        claveServicio = null;
    }

    /**
     * Devuelve tru si no hay filtros activos.
     * @return true false
     */
    public boolean isEmpty() {
        return (pst == null) && (abn == null) && (estado == null)
                && (municipio == null) && (poblacion == null) && (fechaInicial == null)
                && (fechaFinal == null) && (claveCensal == null) && (consecutivo == null) && (claveServicio == null);
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();

        if (pst != null) {
            b.append("pst=");
            b.append(pst);
        }

        if (abn != null) {
            b.append(" abn=");
            b.append(abn);
        }

        if (estado != null) {
            b.append(" estado=");
            b.append(estado);
        }

        if (municipio != null) {
            b.append(" municipio=");
            b.append(municipio);
        }

        if (poblacion != null) {
            b.append(" poblacion=");
            b.append(poblacion);
        }

        if (fechaInicial != null) {
            b.append(" fechaInicial=");
            b.append(fechaInicial);
        }

        if (fechaInicial != null) {
            b.append(" fechaInicial=");
            b.append(fechaInicial);
        }

        return b.toString();
    }

    /**
     * Obtiene Proveedor de servicios PST.
     * @return pst
     */
    public Proveedor getPst() {
        return pst;
    }

    /**
     * Proveedor de servicios PST.
     * @param pstCesionario pstCesionario to set
     */
    public void setPst(Proveedor pstCesionario) {
        this.pst = pstCesionario;
    }

    /**
     * obtiene ABN.
     * @return abn
     */
    public String getAbn() {
        return abn;
    }

    /**
     * Carga obtiene ABN.
     * @param abn abn to set
     */
    public void setAbn(String abn) {
        this.abn = abn;
    }

    /**
     * Obtiene Inegi.
     * @return claveCensal
     */
    public String getClaveCensal() {
        return claveCensal;
    }

    /**
     * Carga Inegi.
     * @param claveCensal claveCensal to set
     */
    public void setClaveCensal(String claveCensal) {
        this.claveCensal = claveCensal;
    }

    /**
     * Obtiene estado.
     * @return estado
     */
    public Estado getEstado() {
        return estado;
    }

    /**
     * Carga estado.
     * @param estado estado to set
     */
    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    /**
     * Obtiene municipio.
     * @return municipio
     */
    public Municipio getMunicipio() {
        return municipio;
    }

    /**
     * Carga municipio.
     * @param municipio municipio to set
     */
    public void setMunicipio(Municipio municipio) {
        this.municipio = municipio;
    }

    /**
     * obtiene poblacion.
     * @return poblacion
     */
    public Poblacion getPoblacion() {
        return poblacion;
    }

    /**
     * carga poblacion.
     * @param poblacion poblacion to set
     */
    public void setPoblacion(Poblacion poblacion) {
        this.poblacion = poblacion;
    }

    /**
     * obtiene rango Fecha inicial del reporte.
     * @return fechaInicial
     */
    public Date getFechaInicial() {
        return fechaInicial;
    }

    /**
     * Carga rango Fecha inicial del reporte.
     * @param fechaInicial fechaInicial to set
     */
    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    /**
     * carga Fecha final del reporte.
     * @return fechaFinal
     */
    public Date getFechaFinal() {
        return fechaFinal;
    }

    /**
     * @param fechaFinal fechaFinal to set
     */
    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
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
     * Obtiene Consecutivo.
     * @return consecutivo
     */
    public String getConsecutivo() {
        return consecutivo;
    }

    /**
     * Carga Consecutivo.
     * @param consecutivo consecutivo to set
     */
    public void setConsecutivo(String consecutivo) {
        this.consecutivo = consecutivo;
    }

    /**
     * obtiene Clave de servicio.
     * @return claveServicio
     */
    public ClaveServicio getClaveServicio() {
        return claveServicio;
    }

    /**
     * carga Clave de servicio.
     * @param claveServicio claveServicio to set
     */
    public void setClaveServicio(ClaveServicio claveServicio) {
        this.claveServicio = claveServicio;
    }

    /**
     * Indica que la consulta es de tipo historico.
     * @return historico
     */
    public Boolean isHistorico() {
        return historico;
    }

    /**
     * Carga true si la consulta es de tipo historico.
     * @param historico historico to set
     */
    public void setHistorico(Boolean historico) {
        this.historico = historico;
    }

}
