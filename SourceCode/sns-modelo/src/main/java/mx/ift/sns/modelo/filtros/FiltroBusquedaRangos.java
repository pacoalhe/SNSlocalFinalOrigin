package mx.ift.sns.modelo.filtros;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.central.Central;
import mx.ift.sns.modelo.central.Estatus;
import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.pst.TipoModalidad;
import mx.ift.sns.modelo.pst.TipoRed;
import mx.ift.sns.modelo.series.EstadoRango;

/** Filtros de búsqueda para Rangos (Numeración Geográfica y Numeración No Geográfica. */
public class FiltroBusquedaRangos implements Serializable, Cloneable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Ordenación de campos ascendente. */
    public static final String ORDEN_ASC = "ASC";

    /** Ordenación de campos descendente. */
    public static final String ORDEN_DESC = "DESC";

    /** Identificador de código de área. */
    private BigDecimal idAbn;

    /** Identificador de código de región. */
    private BigDecimal idNir;

    /** Identificador de numeración. */
    private BigDecimal idSna;

    /** Proveedor arrendatario de los rangos pertenecientes a la serie. */
    private Proveedor arrendatario;

    /** Proveedor concesionario de los rangos pertenecientes a la serie. */
    private Proveedor concesionario;

    /** Proveedor asignatario de los rangos pertenecientes a la serie. */
    private Proveedor asignatario;

    /** Indica si se ha de utilizar paginación en la búsqueda. */
    private boolean usarPaginacion = false;

    /** Usando paginación, indica en el número de bloque. */
    private int numeroPagina = 0;

    /** Usando paginación, indica el número de resultados por bloque. */
    private int resultadosPagina = 10;

    /** Tipo de Red asociada al Rango. */
    private TipoRed tipoRed;

    /** Tipo de Modalidad asociada al Rango para redes móviles. */
    private TipoModalidad tipoModalidad;

    /** Central Origen. */
    private Central centralOrigen;

    /** Central Destino. */
    private Central centralDestino;

    /** Poblacion asociada al rango. */
    private Poblacion poblacion;

    /** Municipio asociado a la población del Rango. */
    private Municipio municipio;

    /** Municipio Estado (OT) a la población del Rango. */
    private Estado estadoOt;

    /** Identificador de la solicitud que asignó / modificó la numeracionón. */
    private BigDecimal idSolicitud;

    /** Inicio del Rango. */
    private String numInicio;

    /** Final del Rango. */
    private String numFinal;

    /** Estatus Rango. */
    private EstadoRango estatusRango;

    /** IDO Planes de Numeración. */
    private BigDecimal idoPnn;

    /** IDA Planes de Numeración. */
    private BigDecimal idaPnn;

    /** Clave de Servicio de Numeración No Geográfica. */
    private BigDecimal claveServicio;

    /** Indica los campos para ordenar y el tipo de ordenación. */
    private HashMap<String, String> ordenCampos = new HashMap<String, String>(1);

    /**
     * Genera una lista con los filtros que se han definido sobre Rangos de Serie tanto de Numeración Geográfica como de
     * Numeración No Geográfica.
     * @return ArrayList<FiltroBusqueda>
     */
    public ArrayList<FiltroBusqueda> getListaFiltros() {

        ArrayList<FiltroBusqueda> filtros = new ArrayList<FiltroBusqueda>();

        // Primer Filtro: Estado de la Serie Activa
        Estatus estadoSerie = new Estatus();
        estadoSerie.setCdg(Estatus.ACTIVO);

        // Filtros de la tabla de Series (CAT_SERIE y CAT_SERIE_NG) -> sSerie
        FiltroBusqueda filtroSerie = new FiltroBusqueda();
        filtroSerie.setCampo("estatus", "sSerie");
        filtroSerie.setValor(estadoSerie, Estatus.class);
        filtros.add(filtroSerie);

        // Filtros de la tabla NIR (ABN_NIR) -> sNir (Solo Numeración Geográfica)
        if (idAbn != null) {
            Abn abn = new Abn();
            abn.setCodigoAbn(idAbn);

            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("abn", "sNir");
            filtro.setValor(abn, Abn.class);
            filtros.add(filtro);
        }

        // Filtros de la tabla de Series (NG_RANGO_SERIE y NNG_RANGO_SERIE) -> sRango
        if (idNir != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("id.idNir", "sRango");
            filtro.setValor(idNir, BigDecimal.class);
            filtros.add(filtro);
        }
        if (idSna != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("id.sna", "sRango");
            filtro.setValor(idSna, BigDecimal.class);
            filtros.add(filtro);
        }
        if (claveServicio != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("id.idClaveServicio", "sRango");
            filtro.setValor(claveServicio, BigDecimal.class);
            filtros.add(filtro);
        }
        if (centralOrigen != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("centralOrigen", "sRango");
            filtro.setValor(centralOrigen, Central.class);
            filtros.add(filtro);
        }
        if (centralDestino != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("centralDestino", "sRango");
            filtro.setValor(centralDestino, Central.class);
            filtros.add(filtro);
        }
        if (asignatario != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("asignatario", "sRango");
            filtro.setValor(asignatario, Proveedor.class);
            filtros.add(filtro);
        }
        if (arrendatario != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("arrendatario", "sRango");
            filtro.setValor(arrendatario, Proveedor.class);
            filtros.add(filtro);
        }
        if (concesionario != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("concesionario", "sRango");
            filtro.setValor(concesionario, Proveedor.class);
            filtros.add(filtro);
        }
        if (tipoRed != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("tipoRed", "sRango");
            filtro.setValor(tipoRed, TipoRed.class);
            filtros.add(filtro);
        }
        if (tipoModalidad != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("tipoModalidad", "sRango");
            filtro.setValor(tipoModalidad, TipoModalidad.class);
            filtros.add(filtro);
        }
        if (poblacion != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("poblacion", "sRango");
            filtro.setValor(poblacion, Poblacion.class);
            filtros.add(filtro);
        }
        if (municipio != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("poblacion.municipio", "sRango");
            filtro.setValor(municipio, Municipio.class);
            filtros.add(filtro);
        }
        if (estadoOt != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("poblacion.municipio.estado", "sRango");
            filtro.setValor(estadoOt, Estado.class);
            filtros.add(filtro);
        }
        if (idSolicitud != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("solicitud.id", "sRango");
            filtro.setValor(idSolicitud, BigDecimal.class);
            filtros.add(filtro);
        }
        if (numInicio != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("numInicio", "sRango");
            filtro.setValor(numInicio, String.class);
            filtros.add(filtro);
        }
        if (numFinal != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("numFinal", "sRango");
            filtro.setValor(numFinal, String.class);
            filtros.add(filtro);
        }
        if (estatusRango != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("estatus", "sRango");
            filtro.setValor(estatusRango, EstadoRango.class);
            filtros.add(filtro);
        }
        if (idoPnn != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("idoPnn", "sRango");
            filtro.setValor(idoPnn, BigDecimal.class);
            filtros.add(filtro);
        }
        if (idaPnn != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("idaPnn", "sRango");
            filtro.setValor(idaPnn, BigDecimal.class);
            filtros.add(filtro);
        }

        return filtros;
    }

    /** Limpia los filtros. */
    public void clear() {
        idAbn = null;
        idSna = null;
        idNir = null;
        asignatario = null;
        concesionario = null;
        arrendatario = null;
        usarPaginacion = false;
        numeroPagina = 0;
        resultadosPagina = 0;
        tipoModalidad = null;
        tipoRed = null;
        centralOrigen = null;
        centralDestino = null;
        poblacion = null;
        numFinal = null;
        numInicio = null;
        estatusRango = null;
        idoPnn = null;
        idaPnn = null;
        claveServicio = null;
        ordenCampos.clear();
    }

    @Override
    public String toString() {
        StringBuffer trace = new StringBuffer();
        trace.append("usarPaginacion").append(": ").append(usarPaginacion).append(", ");
        if (usarPaginacion) {
            trace.append("numeroPagina").append(": ").append(numeroPagina).append(", ");
            trace.append("resultadosPagina").append(": ").append(resultadosPagina).append(", ");
        }
        if (idAbn != null) {
            trace.append("idAbn").append(": ").append(idAbn).append(", ");
        }
        if (idSna != null) {
            trace.append("idSna").append(": ").append(idSna).append(", ");
        }
        if (claveServicio != null) {
            trace.append("claveServicio").append(": ").append(claveServicio).append(", ");
        }
        if (asignatario != null) {
            trace.append("asignatario").append(": ").append(asignatario).append(", ");
        }
        if (concesionario != null) {
            trace.append("concesionario").append(": ").append(concesionario).append(", ");
        }
        if (arrendatario != null) {
            trace.append("arrendatario").append(": ").append(arrendatario).append(", ");
        }
        if (estatusRango != null) {
            trace.append("estatusRango").append(": ").append(estatusRango.getCodigo()).append(", ");
        }
        if (tipoModalidad != null) {
            trace.append("tipoModalidad").append(": ").append(tipoModalidad).append(", ");
        }
        if (tipoRed != null) {
            trace.append("tipoRed").append(": ").append(tipoRed).append(", ");
        }
        if (centralOrigen != null) {
            trace.append("centralOrigen").append(": ").append(centralOrigen).append(", ");
        }
        if (centralDestino != null) {
            trace.append("centralDestino").append(": ").append(centralDestino).append(", ");
        }
        if (poblacion != null) {
            trace.append("poblacion").append(": ").append(poblacion).append(", ");
        }
        if (municipio != null) {
            trace.append("municipio").append(": ").append(municipio).append(", ");
        }
        if (estadoOt != null) {
            trace.append("estado (OT)").append(": ").append(estadoOt).append(", ");
        }
        if (idoPnn != null) {
            trace.append("idoPnn").append(": ").append(idoPnn).append(", ");
        }
        if (idaPnn != null) {
            trace.append("idaPnn").append(": ").append(idaPnn).append(", ");
        }
        if (numInicio != null) {
            trace.append("numInicio").append(": ").append(numInicio).append(", ");
        }
        if (numFinal != null) {
            trace.append("numFinal").append(": ").append(numFinal);
        }

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
     * Identificador de centralOrigen.
     * @return centralOrigen
     */
    public Central getCentralOrigen() {
        return centralOrigen;
    }

    /**
     * Identificador de centralOrigen.
     * @param centralOrigen Central
     */
    public void setCentralOrigen(Central centralOrigen) {
        this.centralOrigen = centralOrigen;
    }

    /**
     * Identificador de centralDestino.
     * @return centralDestino
     */
    public Central getCentralDestino() {
        return centralDestino;
    }

    /**
     * Identificador de centralDestino.
     * @param centralDestino Central
     */
    public void setCentralDestino(Central centralDestino) {
        this.centralDestino = centralDestino;
    }

    /**
     * Población asociada al rango.
     * @return Poblacion
     */
    public Poblacion getPoblacion() {
        return poblacion;
    }

    /**
     * Población asociada al rango.
     * @param poblacion Poblacion
     */
    public void setPoblacion(Poblacion poblacion) {
        this.poblacion = poblacion;
    }

    /**
     * Identificador de la solicitud que asignó / modificó la numeracionón.
     * @return BigDecimal
     */
    public BigDecimal getIdSolicitud() {
        return idSolicitud;
    }

    /**
     * Identificador de la solicitud que asignó / modificó la numeracionón.
     * @param idSolicitud BigDecimal
     */
    public void setIdSolicitud(BigDecimal idSolicitud) {
        this.idSolicitud = idSolicitud;
    }

    /**
     * Indica cómo se ha de ordenar el resultado de la búsqueda.
     * @param pCampo Campo a ordenar
     * @param pTipoOrden Ascendente o Descendente
     * @see FiltroBusquedaRangos.ORDEN_ASC
     * @see FiltroBusquedaRangos.ORDEN_DESC
     */
    public void setOrdernarPor(String pCampo, String pTipoOrden) {
        ordenCampos.put(pCampo, pTipoOrden);
    }

    /**
     * Indica los campos para ordenar y el tipo de ordenación.
     * @return HashMap<String, String>
     * @see FiltroBusquedaRangos.ORDEN_ASC
     * @see FiltroBusquedaRangos.ORDEN_DESC
     */
    public HashMap<String, String> getOrdenCampos() {
        return ordenCampos;
    }

    /**
     * Inicio del Rango.
     * @return String
     */
    public String getNumInicio() {
        return numInicio;
    }

    /**
     * Inicio del Rango.
     * @param numInicio String
     */
    public void setNumInicio(String numInicio) {
        this.numInicio = numInicio;
    }

    /**
     * Final del Rango.
     * @return String
     */
    public String getNumFinal() {
        return numFinal;
    }

    /**
     * Final del Rango.
     * @param numFinal String
     */
    public void setNumFinal(String numFinal) {
        this.numFinal = numFinal;
    }

    /**
     * Municipio asociado a la población del Rango.
     * @return Municipio
     */
    public Municipio getMunicipio() {
        return municipio;
    }

    /**
     * Municipio asociado a la población del Rango.
     * @param municipio Municipio
     */
    public void setMunicipio(Municipio municipio) {
        this.municipio = municipio;
    }

    /**
     * Estado (OT) asociado a la población del Rango.
     * @return Estado
     */
    public Estado getEstadoOt() {
        return estadoOt;
    }

    /**
     * Estado (OT) asociado a la población del Rango.
     * @param estadoOt Estado
     */
    public void setEstadoOt(Estado estadoOt) {
        this.estadoOt = estadoOt;
    }

    /**
     * Estatus del Rango.
     * @return EstadoRango
     */
    public EstadoRango getEstatusRango() {
        return estatusRango;
    }

    /**
     * Estatus del Rango.
     * @param estatusRango EstadoRango
     */
    public void setEstatusRango(EstadoRango estatusRango) {
        this.estatusRango = estatusRango;
    }

    /**
     * IDO Planes de Numeración.
     * @return BigDecimal
     */
    public BigDecimal getIdoPnn() {
        return idoPnn;
    }

    /**
     * IDO Planes de Numeración.
     * @param idoPnn BigDecimal
     */
    public void setIdoPnn(BigDecimal idoPnn) {
        this.idoPnn = idoPnn;
    }

    /**
     * IDA Planes de Numeración.
     * @return BigDecimal
     */
    public BigDecimal getIdaPnn() {
        return idaPnn;
    }

    /**
     * IDA Planes de Numeración.
     * @param idaPnn BigDecimal
     */
    public void setIdaPnn(BigDecimal idaPnn) {
        this.idaPnn = idaPnn;
    }

    /**
     * Clave de Servicio de Numeración No Geográfica.
     * @return BigDecimal
     */
    public BigDecimal getClaveServicio() {
        return claveServicio;
    }

    /**
     * Clave de Servicio de Numeración No Geográfica.
     * @param claveServicio BigDecimal
     */
    public void setClaveServicio(BigDecimal claveServicio) {
        this.claveServicio = claveServicio;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
