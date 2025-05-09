package mx.ift.sns.modelo.filtros;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.solicitud.EstadoCesionSolicitada;
import mx.ift.sns.modelo.solicitud.EstadoLiberacionSolicitada;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.modelo.solicitud.TipoSolicitud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase auxiliar para búsqueda de solicitudes genérica. Contiene los filtros que negocio enviará a los DAOS para que
 * construya las querys. Cada variable asignada se considerará un filtro a satisfacer.
 */
public class FiltroBusquedaSolicitudesCpsn implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(FiltroBusquedaSolicitudesCpsn.class);

    /** Consecutivo, entero de 9 dígitos (String) en la tabla. */
    private String consecutivo;

    /** Proveedor de servicios PST cesionario. */
    private Proveedor pstCesionario;

    /** Proveedor de servicios PST. */
    private Proveedor proveedor;

    /** Fecha de Autorización de Solicitud cesión. */
    private Date fchIniUtilizacion;

    /** Fecha de Implementación de Solicitud cesión. */
    private Date fchIniPruebas;

    /** Indica si se ha de utilizar paginación en la búsqueda. */
    private boolean usarPaginacion = false;

    /** Usando paginación, indica en el número de bloque. */
    private int numeroPagina = 0;

    /** Usando paginación, indica el número de resultados por bloque. */
    private int resultadosPagina = 10;

    /** Rango inicio fecha Asignación. */
    private Date fechaAsignacionDesde;

    /** Rango final fecha Asignación. */
    private Date fechaAsignacionHasta;

    /** Rango inicio fecha de Implementación. */
    private Date fechaImplementacionDesde;

    /** Rango inicio final de Implementación. */
    private Date fechaImplementacionHasta;

    /** Estado de solicitud. */
    private EstadoSolicitud estado;

    /** Rango inicio fchSolicitud. */
    private Date fechaSolicitudDesde;

    /** Rango final fchSolicitud. */
    private Date fechaSolicitudHasta;

    /** Tipo de trámite. */
    private TipoSolicitud tipoSolicitud;

    /** Formateador de fechas. */
    private SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy");

    /** Número de oficio del PST Solicitante. */
    private String oficioPstSolicitante;

    /** Referencia de la solicitud. */
    private String referenciaSolicitud;

    /** Rango inicio fchSolicitud. */
    private Date numIniciofchSolicitud;

    /** Rango final fchSolicitud. */
    private Date numFinalfchSolicitud;

    /** Estado de Cesiones Solicitadas. */
    private EstadoCesionSolicitada estatusCesSol;

    /** Estado de Liberaciones Solicitadas. */
    private EstadoLiberacionSolicitada estatusLibSol;

    /** Constructor, por defect vacío. */
    public FiltroBusquedaSolicitudesCpsn() {
    }

    /**
     * Genera una lista con los filtros que se han definido.
     * @return Lista de objetos FiltroBusqueda con la información solicitada
     */
    public ArrayList<FiltroBusqueda> getListaFiltrosSolicitudCesionCpsn() {
        ArrayList<FiltroBusqueda> filtros = new ArrayList<FiltroBusqueda>();

        // Búsqueda sobre tabla NG_SOLICITUD_CESION -> sl
        if (referenciaSolicitud != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("referencia", "sl");
            filtro.setValor(referenciaSolicitud, String.class);
            filtros.add(filtro);
        }

        if (consecutivo != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("id", "sl");
            filtro.setValor(Long.parseLong(consecutivo), Long.class);
            filtros.add(filtro);
        }

        if (numIniciofchSolicitud != null) {
            try {
                FiltroBusqueda filtro = new FiltroBusqueda();
                filtro.setCampo("fechaSolicitud", "sl");
                filtro.setValor(formateador.parse(formateador.format(numIniciofchSolicitud)), Date.class);
                filtro.setFechaDesde(true);
                filtros.add(filtro);
            } catch (ParseException e) {
                LOGGER.error("Fecha incorrecta: " + numIniciofchSolicitud.toString());
            }
        }

        if (numFinalfchSolicitud != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("fechaSolicitud", "sl");
            filtro.setFechaHasta(true);

            Calendar calendarInstance = Calendar.getInstance();
            calendarInstance.setTime(numFinalfchSolicitud);
            calendarInstance.add(Calendar.DAY_OF_MONTH, 1);

            filtro.setValor(calendarInstance.getTime(), Date.class);
            filtros.add(filtro);
        }

        if (estado != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("estadoSolicitud", "sl");
            filtro.setValor(estado, EstadoSolicitud.class);
            filtros.add(filtro);
        }

        if (proveedor != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("proveedorSolicitante", "sl");
            filtro.setValor(proveedor, Proveedor.class);
            filtros.add(filtro);
        }

        if (pstCesionario != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("proveedorCesionario", "sl");
            filtro.setValor(pstCesionario, Proveedor.class);
            filtros.add(filtro);
        }

        if (oficioPstSolicitante != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("numOficioSolicitante", "sl");
            filtro.setValor(oficioPstSolicitante, String.class);
            filtros.add(filtro);
        }

        // Búsqueda sobre tabla NG_CESION_SOLICITADA -> cs
        if (fechaImplementacionDesde != null) {
            try {
                FiltroBusqueda filtro = new FiltroBusqueda();
                filtro.setCampo("fechaImplementacion", "cl");
                filtro.setValor(formateador.parse(formateador.format(fechaImplementacionDesde)), Date.class);
                filtro.setFechaDesde(true);
                filtros.add(filtro);
            } catch (ParseException e) {
                LOGGER.error("Fecha incorrecta: " + numIniciofchSolicitud.toString());
            }
        }

        if (fechaImplementacionHasta != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("fechaImplementacion", "cl");
            filtro.setFechaHasta(true);

            Calendar calendarInstance = Calendar.getInstance();
            calendarInstance.setTime(fechaImplementacionHasta);
            calendarInstance.add(Calendar.DAY_OF_MONTH, 1);

            filtro.setValor(calendarInstance.getTime(), Date.class);
            filtros.add(filtro);
        }

        if (estatusCesSol != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("estatus", "cl");
            filtro.setValor(estatusCesSol, EstadoCesionSolicitada.class);
            filtros.add(filtro);
        }

        return filtros;
    }

    /**
     * Genera una lista con los filtros que se han definido.
     * @return Lista de pares (clave, valor)
     * @throws Exception exception
     */
    public ArrayList<FiltroBusqueda> getListaFiltrosSolicitudAsignacion() {

        ArrayList<FiltroBusqueda> filtros = new ArrayList<FiltroBusqueda>();

        if (consecutivo != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("id", "sl");
            filtro.setValor(Long.parseLong(consecutivo), Long.class);
            filtros.add(filtro);
        }

        if (proveedor != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("proveedorSolicitante", "sl");
            filtro.setValor(proveedor, Proveedor.class);
            filtros.add(filtro);
        }

        if (fechaSolicitudDesde != null) {
            try {
                FiltroBusqueda filtro = new FiltroBusqueda();
                filtro.setCampo("fechaSolicitud", "sl");
                filtro.setValor(formateador.parse(formateador.format(fechaSolicitudDesde)), Date.class);
                filtro.setFechaDesde(true);
                filtros.add(filtro);
            } catch (ParseException e) {
                LOGGER.error("Fecha incorrecta: " + fechaSolicitudDesde.toString());
            }
        }

        if (fechaSolicitudHasta != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("fechaSolicitud", "sl");
            filtro.setFechaHasta(true);

            Calendar calendarInstance = Calendar.getInstance();
            calendarInstance.setTime(fechaSolicitudHasta);
            calendarInstance.add(Calendar.DAY_OF_MONTH, 1);

            filtro.setValor(calendarInstance.getTime(), Date.class);
            filtros.add(filtro);
        }

        if (fechaAsignacionDesde != null) {
            try {
                FiltroBusqueda filtro = new FiltroBusqueda();
                filtro.setCampo("fechaAsignacion", "sl");
                filtro.setValor(formateador.parse(formateador.format(fechaAsignacionDesde)), Date.class);
                filtro.setFechaDesde(true);
                filtros.add(filtro);
            } catch (ParseException e) {
                LOGGER.error("Fecha incorrecta: " + fechaAsignacionDesde.toString());
            }
        }

        if (fechaAsignacionHasta != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("fechaAsignacion", "sl");
            filtro.setFechaHasta(true);

            Calendar calendarInstance = Calendar.getInstance();
            calendarInstance.setTime(fechaAsignacionHasta);
            calendarInstance.add(Calendar.DAY_OF_MONTH, 1);

            filtro.setValor(calendarInstance.getTime(), Date.class);
            filtros.add(filtro);
        }

        if (estado != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("estadoSolicitud", "sl");
            filtro.setValor(estado, EstadoSolicitud.class);
            filtros.add(filtro);
        }

        if (oficioPstSolicitante != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("numOficioSolicitante", "sl");
            filtro.setValor(oficioPstSolicitante, String.class);
            filtros.add(filtro);
        }

        return filtros;
    }

    /**
     * Genera una lista con los filtros que se han definido.
     * @return Lista de objetos FiltroBusqueda con la información solicitada
     */
    public ArrayList<FiltroBusqueda> getListaFiltrosSolicitudLiberacionCpsn() {

        ArrayList<FiltroBusqueda> filtros = new ArrayList<FiltroBusqueda>();

        // Tabla SOL_SOLICITUD / CPSN_SOLICITUD_LIBERACION
        if (consecutivo != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("id", "sl");
            filtro.setValor(Long.parseLong(consecutivo), Long.class);
            filtros.add(filtro);
        }

        if (proveedor != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("proveedorSolicitante", "sl");
            filtro.setValor(proveedor, Proveedor.class);
            filtros.add(filtro);
        }

        if (fechaSolicitudDesde != null) {
            try {
                FiltroBusqueda filtro = new FiltroBusqueda();
                filtro.setCampo("fechaSolicitud", "sl");
                filtro.setValor(formateador.parse(formateador.format(fechaSolicitudDesde)), Date.class);
                filtro.setFechaDesde(true);
                filtros.add(filtro);
            } catch (ParseException e) {
                LOGGER.error("Fecha incorrecta: " + fechaSolicitudDesde.toString());
            }
        }

        if (fechaSolicitudHasta != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("fechaSolicitud", "sl");
            filtro.setFechaHasta(true);

            Calendar calendarInstance = Calendar.getInstance();
            calendarInstance.setTime(fechaSolicitudHasta);
            calendarInstance.add(Calendar.DAY_OF_MONTH, 1);

            filtro.setValor(calendarInstance.getTime(), Date.class);
            filtros.add(filtro);
        }

        if (estado != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("estadoSolicitud", "sl");
            filtro.setValor(estado, EstadoSolicitud.class);
            filtros.add(filtro);
        }

        if (oficioPstSolicitante != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("numOficioSolicitante", "sl");
            filtro.setValor(oficioPstSolicitante, String.class);
            filtros.add(filtro);
        }

        // Tabla CPSN_LIBERACION_SOLICITADA
        if (fechaImplementacionDesde != null) {
            try {
                FiltroBusqueda filtro = new FiltroBusqueda();
                filtro.setCampo("fechaImplementacion", "ls");
                filtro.setValor(formateador.parse(formateador.format(fechaImplementacionDesde)), Date.class);
                filtro.setFechaDesde(true);
                filtros.add(filtro);
            } catch (ParseException e) {
                LOGGER.error("Fecha incorrecta: " + fechaImplementacionDesde.toString());
            }
        }

        if (fechaImplementacionHasta != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("fechaImplementacion", "ls");
            filtro.setFechaHasta(true);

            Calendar calendarInstance = Calendar.getInstance();
            calendarInstance.setTime(fechaImplementacionHasta);
            calendarInstance.add(Calendar.DAY_OF_MONTH, 1);

            filtro.setValor(calendarInstance.getTime(), Date.class);
            filtros.add(filtro);
        }

        if (estatusLibSol != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("estatus", "ls");
            filtro.setValor(estatusLibSol, EstadoLiberacionSolicitada.class);
            filtros.add(filtro);
        }

        return filtros;
    }

    /**
     * Genera una lista con los filtros que se han definido.
     * @return Lista de objetos FiltroBusqueda con la información solicitada
     */
    public ArrayList<FiltroBusqueda> getListaFiltrosConsultaGenericaCpsn() {
        ArrayList<FiltroBusqueda> filtros = new ArrayList<FiltroBusqueda>();

        if (consecutivo != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("consecutivo", "sl");
            filtro.setValor(Long.parseLong(consecutivo), Long.class);
            filtros.add(filtro);
        }

        if (numIniciofchSolicitud != null) {
            try {
                FiltroBusqueda filtro = new FiltroBusqueda();
                filtro.setCampo("fechaSolicitud", "sl");
                filtro.setValor(formateador.parse(formateador.format(numIniciofchSolicitud)), Date.class);
                filtro.setFechaDesde(true);
                filtros.add(filtro);
            } catch (ParseException e) {
                LOGGER.error("Fecha incorrecta: " + numIniciofchSolicitud.toString());
            }
        }

        if (numFinalfchSolicitud != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("fechaSolicitud", "sl");
            filtro.setFechaHasta(true);

            Calendar calendarInstance = Calendar.getInstance();
            calendarInstance.setTime(numFinalfchSolicitud);
            calendarInstance.add(Calendar.DAY_OF_MONTH, 1);

            filtro.setValor(calendarInstance.getTime(), Date.class);
            filtros.add(filtro);
        }

        if (estado != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("estatusSolicitud", "sl");
            filtro.setValor(estado, EstadoSolicitud.class);
            filtros.add(filtro);
        }

        if (proveedor != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("idPstOrigen", "sl");
            filtro.setValor(proveedor.getId(), BigDecimal.class);
            filtros.add(filtro);
        }

        if (pstCesionario != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("idPstDestino", "sl");
            filtro.setValor(pstCesionario.getId(), BigDecimal.class);
            filtros.add(filtro);
        }

        if (oficioPstSolicitante != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("numOficioSolicitante", "sl");
            filtro.setValor(oficioPstSolicitante, String.class);
            filtros.add(filtro);
        }

        // Búsqueda sobre tabla NG_CESION_SOLICITADA -> cs
        if (fechaImplementacionDesde != null) {
            try {
                FiltroBusqueda filtro = new FiltroBusqueda();
                filtro.setCampo("fechaImplementacion", "sl");
                filtro.setValor(formateador.parse(formateador.format(fechaImplementacionDesde)), Date.class);
                filtro.setFechaDesde(true);
                filtros.add(filtro);
            } catch (ParseException e) {
                LOGGER.error("Fecha incorrecta: " + numIniciofchSolicitud.toString());
            }
        }

        if (fechaImplementacionHasta != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("fechaImplementacion", "sl");
            filtro.setFechaHasta(true);

            Calendar calendarInstance = Calendar.getInstance();
            calendarInstance.setTime(fechaImplementacionHasta);
            calendarInstance.add(Calendar.DAY_OF_MONTH, 1);

            filtro.setValor(calendarInstance.getTime(), Date.class);
            filtros.add(filtro);
        }

        if (tipoSolicitud != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("idTipoSolicitud", "sl");
            filtro.setValor(tipoSolicitud.getCdg(), Integer.class);
            filtros.add(filtro);
        }

        return filtros;
    }

    /** Limpia los filtros. */
    public void clear() {
        consecutivo = null;
        pstCesionario = null;
        proveedor = null;
        usarPaginacion = false;
        numeroPagina = 0;
        resultadosPagina = 0;
        fchIniUtilizacion = null;
        fchIniPruebas = null;
        estado = null;
        tipoSolicitud = null;
        fechaAsignacionHasta = null;
        fechaAsignacionDesde = null;
        oficioPstSolicitante = null;
    }

    @Override
    public String toString() {
        StringBuilder trace = new StringBuilder();
        if (consecutivo != null) {
            trace.append("consecutivo").append(": ").append(consecutivo).append(", ");
        }
        if (proveedor != null) {
            trace.append("proveedor").append(": ").append(proveedor).append(", ");
        }
        if (pstCesionario != null) {
            trace.append("pstCesionario").append(": ").append(pstCesionario).append(", ");
        }
        if (fechaSolicitudDesde != null) {
            trace.append("fechaSolicitudDesde").append(": ").append(fechaSolicitudDesde).append(", ");
        }
        if (fechaSolicitudHasta != null) {
            trace.append("fechaSolicitudHasta").append(": ").append(fechaSolicitudHasta).append(", ");
        }
        if (fchIniUtilizacion != null) {
            trace.append("fchIniUtilizacion").append(": ").append(fchIniUtilizacion).append(", ");
        }
        if (fchIniPruebas != null) {
            trace.append("fchIniPruebas").append(": ").append(fchIniPruebas).append(", ");
        }
        if (fechaAsignacionDesde != null) {
            trace.append("fechaAsignacionDesde").append(": ").append(fechaAsignacionDesde).append(", ");
        }
        if (fechaAsignacionHasta != null) {
            trace.append("fechaAsignacionHasta").append(": ").append(fechaAsignacionHasta).append(", ");
        }
        if (fechaImplementacionDesde != null) {
            trace.append("fechaImplementacionDesde").append(": ").append(fechaImplementacionDesde).append(", ");
        }
        if (fechaImplementacionHasta != null) {
            trace.append("fechaImplementacionHasta").append(": ").append(fechaImplementacionHasta).append(", ");
        }
        if (oficioPstSolicitante != null) {
            trace.append("oficioPstSolicitante").append(": ").append(oficioPstSolicitante).append(", ");
        }
        if (estado != null) {
            trace.append("estado").append(": ").append(estado.getCodigo()).append(", ");
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
     * Consecutivo, entero de 9 dígitos.
     * @return consecutivo consecutivo
     */
    public String getConsecutivo() {
        return consecutivo;
    }

    /**
     * Consecutivo, entero de 9 dígitos.
     * @param consecutivo consecutivo
     */
    public void setConsecutivo(String consecutivo) {
        this.consecutivo = consecutivo;
    }

    /**
     * Proveedor de servicios PST.
     * @return proveedor proveedor
     */
    public Proveedor getProveedor() {
        return proveedor;
    }

    /**
     * Proveedor de servicios PST.
     * @param proveedor proveedor
     */
    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    /**
     * Estado de solicitud.
     * @return estado estado
     **/
    public EstadoSolicitud getEstado() {
        return estado;
    }

    /**
     * Estado de solicitud.
     * @param estado estado
     **/
    public void setEstado(EstadoSolicitud estado) {
        this.estado = estado;
    }

    /**
     * Proveedor de servicios PST cesionario.
     * @return the pstCesionario
     */
    public Proveedor getPstCesionario() {
        return pstCesionario;
    }

    /**
     * Fecha de Implementación de Solicitud cesión.
     * @return the fchIniUtilizacion
     */
    public Date getFchIniUtilizacion() {
        return fchIniUtilizacion;
    }

    /**
     * Fecha de Implementación de Solicitud cesión.
     * @return the fechaImplementacion
     */
    public Date getFchIniPruebas() {
        return fchIniPruebas;
    }

    /**
     * Proveedor de servicios PST cesionario.
     * @param pstCesionario the pstCesionario to set
     */
    public void setPstCesionario(Proveedor pstCesionario) {
        this.pstCesionario = pstCesionario;
    }

    /**
     * Fecha de Implementación de Solicitud cesión.
     * @param fchIniUtilizacion the fchIniUtilizacion to set
     */
    public void setFchIniUtilizacion(Date fchIniUtilizacion) {
        this.fchIniUtilizacion = fchIniUtilizacion;
    }

    /**
     * Fecha de Implementación de Solicitud cesión.
     * @param fchIniPruebas the fchIniPruebas to set
     */
    public void setFchIniPruebas(Date fchIniPruebas) {
        this.fchIniPruebas = fchIniPruebas;
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
     * Formateador de fechas.
     * @return the formateador
     */
    public SimpleDateFormat getFormateador() {
        return formateador;
    }

    /**
     * Formateador de fechas.
     * @param formateador the formateador to set
     */
    public void setFormateador(SimpleDateFormat formateador) {
        this.formateador = formateador;
    }

    /**
     * Tipo de trámite.
     * @return the tipoSolicitud
     */
    public TipoSolicitud getTipoSolicitud() {
        return tipoSolicitud;
    }

    /**
     * Tipo de trámite.
     * @param tipoSolicitud the tipoSolicitud to set
     */
    public void setTipoSolicitud(TipoSolicitud tipoSolicitud) {
        this.tipoSolicitud = tipoSolicitud;
    }

    /**
     * Rango inicio fecha Asingación.
     * @return Date
     */
    public Date getFechaAsignacionDesde() {
        return fechaAsignacionDesde;
    }

    /**
     * Rango inicio fecha Asingación.
     * @param fechaAsignacionDesde Date
     */
    public void setFechaAsignacionDesde(Date fechaAsignacionDesde) {
        this.fechaAsignacionDesde = fechaAsignacionDesde;
    }

    /**
     * Rango final fecha Asingación.
     * @return Date
     */
    public Date getFechaAsignacionHasta() {
        return fechaAsignacionHasta;
    }

    /**
     * Rango final fecha Asingación.
     * @param fechaAsignacionHasta Date
     */
    public void setFechaAsignacionHasta(Date fechaAsignacionHasta) {
        this.fechaAsignacionHasta = fechaAsignacionHasta;
    }

    /**
     * Número de oficio del PST Solicitante.
     * @return String
     */
    public String getOficioPstSolicitante() {
        return oficioPstSolicitante;
    }

    /**
     * Número de oficio del PST Solicitante.
     * @param oficioPstSolicitante String
     */
    public void setOficioPstSolicitante(String oficioPstSolicitante) {
        this.oficioPstSolicitante = oficioPstSolicitante;
    }

    /**
     * Rango inicio fecha de Implementación.
     * @return the fechaImplementacionDesde
     */
    public Date getFechaImplementacionDesde() {
        return fechaImplementacionDesde;
    }

    /**
     * Rango inicio fecha de Implementación.
     * @param fechaImplementacionDesde the fechaImplementacionDesde to set
     */
    public void setFechaImplementacionDesde(Date fechaImplementacionDesde) {
        this.fechaImplementacionDesde = fechaImplementacionDesde;
    }

    /**
     * Rango inicio final de Implementación.
     * @return the fechaImplementacionHasta
     */
    public Date getFechaImplementacionHasta() {
        return fechaImplementacionHasta;
    }

    /**
     * Rango inicio final de Implementación.
     * @param fechaImplementacionHasta the fechaImplementacionHasta to set
     */
    public void setFechaImplementacionHasta(Date fechaImplementacionHasta) {
        this.fechaImplementacionHasta = fechaImplementacionHasta;
    }

    /**
     * Rango inicio fchSolicitud.
     * @return the fechaSolicitudDesde
     */
    public Date getFechaSolicitudDesde() {
        return fechaSolicitudDesde;
    }

    /**
     * Rango inicio fchSolicitud.
     * @param fechaSolicitudDesde the fechaSolicitudDesde to set
     */
    public void setFechaSolicitudDesde(Date fechaSolicitudDesde) {
        this.fechaSolicitudDesde = fechaSolicitudDesde;
    }

    /**
     * Rango final fchSolicitud.
     * @return the fechaSolicitudHasta
     */
    public Date getFechaSolicitudHasta() {
        return fechaSolicitudHasta;
    }

    /**
     * Rango final fchSolicitud.
     * @param fechaSolicitudHasta the fechaSolicitudHasta to set
     */
    public void setFechaSolicitudHasta(Date fechaSolicitudHasta) {
        this.fechaSolicitudHasta = fechaSolicitudHasta;
    }

    /**
     * Referencia de la solicitud.
     * @return the referenciaSolicitud
     */
    public String getReferenciaSolicitud() {
        return referenciaSolicitud;
    }

    /**
     * Referencia de la solicitud.
     * @param referenciaSolicitud the referenciaSolicitud to set
     */
    public void setReferenciaSolicitud(String referenciaSolicitud) {
        this.referenciaSolicitud = referenciaSolicitud;
    }

    /**
     * Rango inicio fchSolicitud.
     * @return the numIniciofchSolicitud
     */
    public Date getNumIniciofchSolicitud() {
        return numIniciofchSolicitud;
    }

    /**
     * Rango inicio fchSolicitud.
     * @param numIniciofchSolicitud the numIniciofchSolicitud to set
     */
    public void setNumIniciofchSolicitud(Date numIniciofchSolicitud) {
        this.numIniciofchSolicitud = numIniciofchSolicitud;
    }

    /**
     * Rango final fchSolicitud.
     * @return the numFinalfchSolicitud
     */
    public Date getNumFinalfchSolicitud() {
        return numFinalfchSolicitud;
    }

    /**
     * Rango final fchSolicitud.
     * @param numFinalfchSolicitud the numFinalfchSolicitud to set
     */
    public void setNumFinalfchSolicitud(Date numFinalfchSolicitud) {
        this.numFinalfchSolicitud = numFinalfchSolicitud;
    }

    /**
     * Estado de Liberaciones Solicitadas.
     * @return the estatusLibSol
     */
    public EstadoLiberacionSolicitada getEstatusLibSol() {
        return estatusLibSol;
    }

    /**
     * Estado de Liberaciones Solicitadas.
     * @param estatusLibSol the estatusLibSol to set
     */
    public void setEstatusLibSol(EstadoLiberacionSolicitada estatusLibSol) {
        this.estatusLibSol = estatusLibSol;
    }

    /**
     * Estado de Cesiones Solicitadas.
     * @return the estatusCesSol
     */
    public EstadoCesionSolicitada getEstatusCesSol() {
        return estatusCesSol;
    }

    /**
     * Estado de Cesiones Solicitadas.
     * @param estatusCesSol the estatusCesSol to set
     */
    public void setEstatusCesSol(EstadoCesionSolicitada estatusCesSol) {
        this.estatusCesSol = estatusCesSol;
    }

}
