package mx.ift.sns.modelo.filtros;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

/**
 * Filtros de búsqueda para movimientos históricos sobre Rangos de Numeración Geográfica y Numeración No Geográfica.
 */
public class FiltroBusquedaHistoricoRangos implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Ordenación de campos ascendente. */
    public static final String ORDEN_ASC = "ASC";

    /** Ordenación de campos descendente. */
    public static final String ORDEN_DESC = "DESC";

    /** Indica si se ha de utilizar paginación en la búsqueda. */
    private boolean usarPaginacion = false;

    /** Usando paginación, indica en el número de bloque. */
    private int numeroPagina = 0;

    /** Usando paginación, indica el número de resultados por bloque. */
    private int resultadosPagina = 10;

    /** Identificador de código ABC. */
    private BigDecimal abc;

    /** Identificador de código BCD. */
    private BigDecimal bcd;

    /** Tipo de acción sobre la tabla RangoSerieNng. */
    private String accion;

    /** Fecha de generación del registro de histórico. */
    private Date fechaHistorico;

    /** Fecha de generación del registro de histórico. */
    private Date fechaHistoricoDesde;

    /** Fecha de generación del registro de histórico. */
    private Date fechaHistoricoHasta;

    /** Identificador de la Clave de Servicio. */
    private BigDecimal idClaveServicio;

    /** Identificador de la Serie a la que pertenece al rango modificado. */
    private BigDecimal sna;

    /** Identificador del Estatus del Rango. */
    private String idEstatusRango;

    /** Identificador de la Numeración Solicitada que generó la numeración. */
    private BigDecimal idNumeracionSolicitada;

    /** Identificador del RangoSerieNng asociado al movimiento del histórico. */
    private BigDecimal idRangoSerie;

    /** Identificador del Proveedor Asignatario de la numeración. */
    private BigDecimal idPstAsignatario;

    /** Identificador de la Solicitud que modificó la numeración. */
    private BigDecimal idSolicitud;

    /** Identificador de la Solicitud que modificó la numeración. */
    private BigDecimal idSolicitudDistinct;

    /** Identificador de la Solicitud de Asignación que generó la numeración. */
    private BigDecimal idSolicitudAsignacion;

    /** Inicio del Rango. */
    private String numInicio;

    /** Final del Rango. */
    private String numFinal;

    /** Tipo de Ordenación. */
    private String orderType;

    /**
     * Genera la lista de filtros definidos sobre los movimientos de Rangos Serie (NG y NNG).
     * @return ArrayList<FiltroBusqueda>
     */
    public ArrayList<FiltroBusqueda> getListaFiltros() {

        ArrayList<FiltroBusqueda> filtros = new ArrayList<FiltroBusqueda>();

        if (idClaveServicio != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("idClaveServicio", "");
            filtro.setValor(idClaveServicio, BigDecimal.class);
            filtros.add(filtro);
        }
        if (abc != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("abc", "");
            filtro.setValor(abc, BigDecimal.class);
            filtros.add(filtro);
        }
        if (bcd != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("bcd", "");
            filtro.setValor(bcd, BigDecimal.class);
            filtros.add(filtro);
        }
        if (accion != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("accion", "");
            filtro.setValor(accion, String.class);
            filtros.add(filtro);
        }
        if (fechaHistorico != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("fechaHistorico", "");
            filtro.setValor(fechaHistorico, Date.class);
            filtros.add(filtro);
        }
        if (fechaHistoricoDesde != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("fechaHistorico", "");
            filtro.setValor(fechaHistoricoDesde, Date.class);
            filtro.setFechaDesde(true);
            filtros.add(filtro);
        }
        if (fechaHistoricoHasta != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("fechaHistorico", "");
            filtro.setValor(fechaHistoricoHasta, Date.class);
            filtro.setFechaHasta(true);
            filtros.add(filtro);
        }
        if (idEstatusRango != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("idEstatusRango", "");
            filtro.setValor(idEstatusRango, String.class);
            filtros.add(filtro);
        }
        if (idNumeracionSolicitada != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("idNumeracionSolicitada", "");
            filtro.setValor(idNumeracionSolicitada, BigDecimal.class);
            filtros.add(filtro);
        }
        if (idPstAsignatario != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("idPstAsignatario", "");
            filtro.setValor(idPstAsignatario, BigDecimal.class);
            filtros.add(filtro);
        }
        if (idRangoSerie != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("idRangoSerie", "");
            filtro.setValor(idRangoSerie, BigDecimal.class);
            filtros.add(filtro);
        }
        if (idSolicitud != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("idSolicitud", "");
            filtro.setValor(idSolicitud, BigDecimal.class);
            filtros.add(filtro);
        }
        if (idSolicitudDistinct != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("idSolicitud", "");
            filtro.setValor(idSolicitudDistinct, BigDecimal.class);
            filtro.setDistinto(true);
            filtros.add(filtro);
        }
        if (idSolicitudAsignacion != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("idSolicitudAsignacion", "");
            filtro.setValor(idSolicitudAsignacion, BigDecimal.class);
            filtros.add(filtro);
        }
        if (sna != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("sna", "");
            filtro.setValor(sna, BigDecimal.class);
            filtros.add(filtro);
        }
        if (numFinal != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("numFinal", "");
            filtro.setValor(numFinal, String.class);
            filtros.add(filtro);
        }
        if (numInicio != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("numInicio", "");
            filtro.setValor(numInicio, String.class);
            filtros.add(filtro);
        }

        return filtros;
    }

    /** Limpia los filtros. */
    public void clear() {
        this.usarPaginacion = false;
        this.numeroPagina = 0;
        this.resultadosPagina = 0;
        this.abc = null;
        this.accion = null;
        this.bcd = null;
        this.fechaHistorico = null;
        this.idClaveServicio = null;
        this.idEstatusRango = null;
        this.idNumeracionSolicitada = null;
        this.idPstAsignatario = null;
        this.idRangoSerie = null;
        this.idSolicitud = null;
        this.idSolicitudAsignacion = null;
        this.sna = null;
        this.numFinal = null;
        this.numInicio = null;
        this.fechaHistoricoDesde = null;
        this.fechaHistoricoHasta = null;
        this.idSolicitudDistinct = null;
        this.orderType = null;
    }

    @Override
    public String toString() {
        StringBuffer trace = new StringBuffer();
        trace.append("usarPaginacion").append(": ").append(usarPaginacion).append(", ");
        if (usarPaginacion) {
            trace.append("numeroPagina").append(": ").append(numeroPagina).append(", ");
            trace.append("resultadosPagina").append(": ").append(resultadosPagina).append(", ");
        }
        if (abc != null) {
            trace.append("abc").append(": ").append(abc).append(", ");
        }
        if (accion != null) {
            trace.append("accion").append(": ").append(accion).append(", ");
        }
        if (bcd != null) {
            trace.append("bcd").append(": ").append(bcd).append(", ");
        }
        if (fechaHistorico != null) {
            trace.append("fechaHistorico").append(": ").append(fechaHistorico).append(", ");
        }
        if (fechaHistoricoDesde != null) {
            trace.append("fechaHistoricoDesde").append(": ").append(fechaHistoricoDesde).append(", ");
        }
        if (fechaHistoricoHasta != null) {
            trace.append("fechaHistoricoHasta").append(": ").append(fechaHistoricoHasta).append(", ");
        }
        if (idClaveServicio != null) {
            trace.append("idClaveServicio").append(": ").append(idClaveServicio).append(", ");
        }
        if (idEstatusRango != null) {
            trace.append("idEstatusRango").append(": ").append(idEstatusRango).append(", ");
        }
        if (idNumeracionSolicitada != null) {
            trace.append("idNumeracionSolicitada").append(": ").append(idNumeracionSolicitada).append(", ");
        }
        if (idPstAsignatario != null) {
            trace.append("idPstAsignatario").append(": ").append(idPstAsignatario).append(", ");
        }
        if (idRangoSerie != null) {
            trace.append("idRangoSerie").append(": ").append(idRangoSerie).append(", ");
        }
        if (idSolicitud != null) {
            trace.append("idSolicitud").append(": ").append(idSolicitud).append(", ");
        }
        if (idSolicitudDistinct != null) {
            trace.append("idSolicitudDistinct").append(": ").append(idSolicitudDistinct).append(", ");
        }
        if (idSolicitudAsignacion != null) {
            trace.append("idSolicitudAsignacion").append(": ").append(idSolicitudAsignacion).append(", ");
        }
        if (numInicio != null) {
            trace.append("numInicio").append(": ").append(numInicio).append(", ");
        }
        if (numFinal != null) {
            trace.append("numFinal").append(": ").append(numFinal).append(", ");
        }
        if (sna != null) {
            trace.append("sna").append(": ").append(sna).append(", ");
        }
        if (orderType != null) {
            trace.append("orderType").append(": ").append(ORDEN_ASC);
        } else {
            trace.append("orderType").append(": ").append(ORDEN_DESC);
        }
        return trace.toString();
    }

    // GETTERS & SETTERS

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
     * @param numeroPagina int
     */
    public void setNumeroPagina(int numeroPagina) {
        this.numeroPagina = numeroPagina;
    }

    /**
     * Usando paginación, indica en el número de bloque.
     * @return getNumeroPagina
     */
    public int getNumeroPagina() {
        return numeroPagina;
    }

    /**
     * Usando paginación, indica el número de resultados por bloque.
     * @param resultadosPagina int
     */
    public void setResultadosPagina(int resultadosPagina) {
        this.resultadosPagina = resultadosPagina;
    }

    /**
     * Usando paginación, indica el número de resultados por bloque.
     * @return int
     */
    public int getResultadosPagina() {
        return resultadosPagina;
    }

    /**
     * Identificador de código ABC.
     * @param abc BigDecimal
     */
    public void setAbc(BigDecimal abc) {
        this.abc = abc;
    }

    /**
     * Identificador de código BCD.
     * @param bcd BigDecimal
     */
    public void setBcd(BigDecimal bcd) {
        this.bcd = bcd;
    }

    /**
     * Tipo de acción sobre la tabla RangoSerieNng.
     * @param accion String
     */
    public void setAccion(String accion) {
        this.accion = accion;
    }

    /**
     * Fecha de generación del registro de histórico.
     * @param fechaHistorico Date
     */
    public void setFechaHistorico(Date fechaHistorico) {
        this.fechaHistorico = fechaHistorico;
    }

    /**
     * Identificador de la Clave de Servicio.
     * @param idClaveServicio BigDecimal
     */
    public void setIdClaveServicio(BigDecimal idClaveServicio) {
        this.idClaveServicio = idClaveServicio;
    }

    /**
     * Identificador de la Serie a la que pertenece al rango modificado.
     * @param sna BigDecimal
     */
    public void setSna(BigDecimal sna) {
        this.sna = sna;
    }

    /**
     * Identificador del Estatus del Rango.
     * @param idEstatusRango String
     */
    public void setIdEstatusRango(String idEstatusRango) {
        this.idEstatusRango = idEstatusRango;
    }

    /**
     * Identificador de la Numeración Solicitada que generó la numeración.
     * @param idNumeracionSolicitada BigDecimal
     */
    public void setIdNumeracionSolicitada(BigDecimal idNumeracionSolicitada) {
        this.idNumeracionSolicitada = idNumeracionSolicitada;
    }

    /**
     * Identificador del RangoSerieNng asociado al movimiento del histórico.
     * @param idRangoSerie BigDecimal
     */
    public void setIdRangoSerie(BigDecimal idRangoSerie) {
        this.idRangoSerie = idRangoSerie;
    }

    /**
     * Identificador del Proveedor Asignatario de la numeración.
     * @param idPstAsignatario BigDecimal
     */
    public void setIdPstAsignatario(BigDecimal idPstAsignatario) {
        this.idPstAsignatario = idPstAsignatario;
    }

    /**
     * Identificador de la Solicitud que modificó la numeración.
     * @param idSolicitud BigDecimal
     */
    public void setIdSolicitud(BigDecimal idSolicitud) {
        this.idSolicitud = idSolicitud;
    }

    /**
     * Identificador de la Solicitud de Asignación que generó la numeración.
     * @param idSolicitudAsignacion BigDecimal
     */
    public void setIdSolicitudAsignacion(BigDecimal idSolicitudAsignacion) {
        this.idSolicitudAsignacion = idSolicitudAsignacion;
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
     * @param numFinal String
     */
    public void setNumFinal(String numFinal) {
        this.numFinal = numFinal;
    }

    /**
     * Fecha de generación del registro de histórico.
     * @param fechaHistoricoDesde Date
     */
    public void setFechaHistoricoDesde(Date fechaHistoricoDesde) {
        this.fechaHistoricoDesde = fechaHistoricoDesde;
    }

    /**
     * Fecha de generación del registro de histórico.
     * @param fechaHistoricoHasta Date
     */
    public void setFechaHistoricoHasta(Date fechaHistoricoHasta) {
        this.fechaHistoricoHasta = fechaHistoricoHasta;
    }

    /**
     * Identificador de la Solicitud que modificó la numeración. Filtro "notEqual".
     * @param idSolicitudDistinct BigDecimal
     */
    public void setIdSolicitudDistinct(BigDecimal idSolicitudDistinct) {
        this.idSolicitudDistinct = idSolicitudDistinct;
    }

    /**
     * Indica el tipo de ordenación de los resultados.
     * @see FiltroBusquedaRangos.ORDEN_ASC
     * @see FiltroBusquedaRangos.ORDEN_DESC
     * @return String
     */
    public String getOrderType() {
        return orderType;
    }

    /**
     * Indica el tipo de ordenación de los resultados.
     * @see FiltroBusquedaRangos.ORDEN_ASC
     * @see FiltroBusquedaRangos.ORDEN_DESC
     * @param orderType String
     */
    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

}
