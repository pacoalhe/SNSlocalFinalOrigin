package mx.ift.sns.modelo.filtros;

import java.io.Serializable;

/** Filtro concreto para búsqueda. Contiene información específica de campo, tabla, etc. */
public class FiltroBusqueda implements Serializable {

    /** Serialziación. */
    private static final long serialVersionUID = 1L;

    /** Variable de la entidad JPA. */
    private String campo;

    /** Valor de búsqueda para el campo. */
    private Object valor;

    /** Variable de la entidad JPA. */
    private String campoSecundario;

    /** Valor de búsqueda para el campo. */
    private Object valorSecundario;

    /** Clase del tipo de valor. */
    @SuppressWarnings("rawtypes")
    private Class clase;

    /** Prefijo para el campo. */
    private String prefijo;

    /** Indica si se trata de un campo 'Fecha Desde'. */
    private boolean fechaDesde = false;

    /** Indica si se trata de un campo 'Fecha Hasta'. */
    private boolean fechaHasta = false;

    /** Variable booleana a comprobar en la query. */
    private boolean distinto = false;

    /** Variable booleana a comprobar en la query. */
    private boolean like = false;

    /** Variable booleana a comprobar en la query. */
    private boolean poblacion = false;

    /** Variable booleana a comprobar en la query. */
    private boolean filtroTipoSolicitud = false;

    /** Variable booleana a comprobar en la query. */
    private boolean estadoMun = false;

    /** Variable booleana a comprobar en la query. */
    private boolean municipio = false;

    // private boolean solicitudAsig = false;

    /**
     * Indica el campo de la entidad JPA y el prefijo utilizado para la tabla.
     * @param pCampo Nombre de la variable de la entidad JPA
     * @param pPrefijo Nombre del prefijo utilizado para la tabla en las Querys
     */
    public void setCampo(String pCampo, String pPrefijo) {
        campo = pCampo;
        prefijo = pPrefijo;
    }

    /**
     * Indica el campo de la entidad JPA y el prefijo utilizado para la tabla.
     * @param pCampo Nombre de la variable de la entidad JPA
     * @param pPrefijo Nombre del prefijo utilizado para la tabla en las Querys
     */
    public void setCampoSecundario(String pCampo, String pPrefijo) {
        campoSecundario = pCampo;
        prefijo = pPrefijo;
    }

    /**
     * Asocia un valor para la búsqueda e indica su clase.
     * @param pValor Valor a buscar
     * @param pClase Clase del objeto que representa el valor a buscar
     */
    @SuppressWarnings("rawtypes")
    public void setValor(Object pValor, Class pClase) {
        valor = pValor;
        clase = pClase;
    }

    /**
     * Asocia un valor para la búsqueda e indica su clase.
     * @param pValor Valor a buscar
     * @param pClase Clase del objeto que representa el valor a buscar
     */
    @SuppressWarnings("rawtypes")
    public void setValorSecundario(Object pValor, Class pClase) {
        valorSecundario = pValor;
        clase = pClase;
    }

    /**
     * Nombre de la variable de la entidad JPA.
     * @return String
     */
    public String getCampo() {
        return campo;
    }

    /**
     * Nombre de la variable de la entidad JPA.
     * @return String
     */
    public String getCampoSecundario() {
        return campoSecundario;
    }

    /**
     * Valor a buscar.
     * @return Object
     */
    public Object getValor() {
        return valor;
    }

    /**
     * Valor a buscar.
     * @return Object
     */
    public Object getValorSecundario() {
        return valorSecundario;
    }

    /**
     * Clase del objeto que representa el valor a buscar.
     * @return Class
     */
    @SuppressWarnings("rawtypes")
    public Class getClase() {
        return clase;
    }

    /**
     * Nombre del prefijo utilizado para la tabla en las Querys.
     * @return String
     */
    public String getPrefijo() {
        return prefijo;
    }

    /**
     * Indica si se trata de un campo 'Fecha Desde'.
     * @return boolean
     */
    public boolean isFechaDesde() {
        return fechaDesde;
    }

    /**
     * Indica si se trata de un campo 'Fecha Desde'.
     * @param fechaDesde boolean
     */
    public void setFechaDesde(boolean fechaDesde) {
        this.fechaDesde = fechaDesde;
    }

    /**
     * Indica si se trata de un campo 'Fecha Hasta'.
     * @return boolean
     */
    public boolean isFechaHasta() {
        return fechaHasta;
    }

    /**
     * Indica si se trata de un campo 'Fecha Hasta'.
     * @param fechaHasta boolean
     */
    public void setFechaHasta(boolean fechaHasta) {
        this.fechaHasta = fechaHasta;
    }

    /**
     * Variable booleana a comprobar en la query.
     * @return the distinto
     */
    public boolean isDistinto() {
        return distinto;
    }

    /**
     * Variable booleana a comprobar en la query.
     * @param distinto the distinto to set
     */
    public void setDistinto(boolean distinto) {
        this.distinto = distinto;
    }

    /**
     * Variable booleana a comprobar en la query.
     * @return the poblacion
     */
    public boolean isPoblacion() {
        return poblacion;
    }

    /**
     * Variable booleana a comprobar en la query.
     * @param poblacion the poblacion to set
     */
    public void setPoblacion(boolean poblacion) {
        this.poblacion = poblacion;
    }

    /**
     * Variable booleana a comprobar en la query.
     * @return the filtroTipoSolicitud
     */
    public boolean isFiltroTipoSolicitud() {
        return filtroTipoSolicitud;
    }

    /**
     * Variable booleana a comprobar en la query.
     * @param filtroTipoSolicitud the filtroTipoSolicitud to set
     */
    public void setFiltroTipoSolicitud(boolean filtroTipoSolicitud) {
        this.filtroTipoSolicitud = filtroTipoSolicitud;
    }

    /**
     * Variable booleana a comprobar en la query.
     * @return the estadoMun
     */
    public boolean isEstadoMun() {
        return estadoMun;
    }

    /**
     * Variable booleana a comprobar en la query.
     * @param estadoMun the estadoMun to set
     */
    public void setEstadoMun(boolean estadoMun) {
        this.estadoMun = estadoMun;
    }

    /**
     * Variable booleana a comprobar en la query.
     * @return the municipio
     */
    public boolean isMunicipio() {
        return municipio;
    }

    /**
     * Variable booleana a comprobar en la query.
     * @param municipio the municipio to set
     */
    public void setMunicipio(boolean municipio) {
        this.municipio = municipio;
    }

    /**
     * Variable booleana a comprobar en la query.
     * @return the like
     */
    public boolean isLike() {
        return like;
    }

    /**
     * Variable booleana a comprobar en la query.
     * @param like the like to set
     */
    public void setLike(boolean like) {
        this.like = like;
    }

}
