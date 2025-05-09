package mx.ift.sns.modelo.filtros;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.ng.EstadoAbnConsolidar;
import mx.ift.sns.modelo.nng.ClaveServicio;
import mx.ift.sns.modelo.nng.TipoAsignacion;
import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.Poblacion;
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
public class FiltroBusquedaSolicitudes implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;
    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(FiltroBusquedaSolicitudes.class);
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
    /** Referencia de la solicitud. */
    private String referenciaSolicitud;
    /** Indica si se ha de utilizar paginación en la búsqueda. */
    private boolean usarPaginacion = false;
    /** Usando paginación, indica en el número de bloque. */
    private int numeroPagina = 0;
    /** Usando paginación, indica el número de resultados por bloque. */
    private int resultadosPagina = 10;
    /** Fecha de Asignación de Solicitud. */
    private Date fechaUtilizacion;
    /** Fecha de solicitud. */
    private Date fechaSolicitud;
    /** Rango inicio fecha Asignación. */
    private Date fechaAsignacionDesde;
    /** Rango final fecha Asignación. */
    private Date fechaAsignacionHasta;
    /** Rango inicio fecha Liberación. */
    private Date fechaLiberacionDesde;
    /** Rango final fecha Liberación. */
    private Date fechaLiberacionHasta;
    /** Rango inicio fecha de Implementación. */
    private Date fechaImplementacionDesde;
    /** Rango inicio final de Implementación. */
    private Date fechaImplementacionHasta;
    /** Estado de solicitud. */
    private EstadoSolicitud estado;
    /** Estado de Liberaciones Solicitadas. */
    private EstadoLiberacionSolicitada estatusLibSol;
    /** Estado de Cesiones Solicitadas. */
    private EstadoCesionSolicitada estatusCesSol;
    /** Identificador de Numeración de la región. */
    private BigDecimal idNir;
    /** Identificador de la serie. */
    private BigDecimal sna;
    /** Rango inicio fchSolicitud. */
    private Date numIniciofchSolicitud;
    /** Rango final fchSolicitud. */
    private Date numFinalfchSolicitud;
    /** Rango inicio fchIniUtilizacion. */
    private Date numIniciofchIniUtilizacion;
    /** Rango final fchIniUtilizacion. **/
    private Date numFinalfchIniUtilizacion;
    /** Rango inicio FechaAsignacion. */
    private Date numInicioFechaAsignacion;
    /** Rango final FechaAsignacion. **/
    private Date numFinalFechaAsignacion;
    /** Rango inicio fchIniImplementacion. */
    private Date numInicioFechImpl;
    /** Rango final fchFinImplementacion. **/
    private Date numFinalFechImpl;
    /** abnEntrega. **/
    private String abnEntrega;
    /** abnRecibe. **/
    private String abnRecibe;
    /** Fecha de consolidacion. */
    private Date fechaConsolidacion;
    /** fechaIniConsolidacion. **/
    private Date fechaIniConsolidacion;
    /** fechaFinConsolidacion. **/
    private Date fechaFinConsolidacion;
    /** Rango inicio Fecha Autorización. */
    private Date fechaAutorizacionDesde;
    /** Rango final Fecha Redistribución. */
    private Date fechaAutorizacionHasta;
    /** Rango inicio Fecha Redistribución. */
    private Date fechaRedistribucionDesde;
    /** Rango final Fecha Autorización. */
    private Date fechaRedistribucionHasta;
    /** Estado de Estado. */
    private Estado estadoMun;
    /** Estado de Municipio. */
    private Municipio municipio;
    /** Estado de Poblacion. */
    private Poblacion poblacion;
    /** Tipo de trámite. */
    private TipoSolicitud tipoSolicitud;
    /** Formateador de fechas. */
    private SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy");
    /** Número de oficio del PST Solicitante. */
    private String oficioPstSolicitante;
    /** Estado de Abn a Consolidar. */
    private EstadoAbnConsolidar estatusAbnConso;
    /** Identificador de la Clave de Servicio. */
    private BigDecimal idClaveServicio;
    /** Tipo de asignación. */
    private TipoAsignacion tipoAsignacion;
    /** Cliente. */
    private String cliente;
    /** Rango inicio FechaAsignacion. */
    private Date fechaIniTramitacion;
    /** Rango final FechaAsignacion. **/
    private Date fechaFinTramitacion;
    /** Clave de servicio. */
    private ClaveServicio claveServicio;

    /** Constructor, por defect vacío. */
    public FiltroBusquedaSolicitudes() {
    }

    /**
     * Genera una lista con los filtros que se han definido.
     * @return Lista de objetos FiltroBusqueda con la información solicitada
     */
    public ArrayList<FiltroBusqueda> getListaFiltrosSolicitudCesionNg() {

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
                filtro.setCampo("fechaCesion", "cl");
                filtro.setValor(formateador.parse(formateador.format(fechaImplementacionDesde)), Date.class);
                filtro.setFechaDesde(true);
                filtros.add(filtro);
            } catch (ParseException e) {
                LOGGER.error("Fecha incorrecta: " + numIniciofchSolicitud.toString());
            }
        }

        if (fechaImplementacionHasta != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("fechaCesion", "cl");
            filtro.setFechaHasta(true);

            Calendar calendarInstance = Calendar.getInstance();
            calendarInstance.setTime(fechaImplementacionHasta);
            calendarInstance.add(Calendar.DAY_OF_MONTH, 1);

            filtro.setValor(calendarInstance.getTime(), Date.class);
            filtros.add(filtro);
        }

        if (idNir != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("idNir", "cl");
            filtro.setValor(idNir, BigDecimal.class);
            filtros.add(filtro);
        }

        if (sna != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("sna", "cl");
            filtro.setValor(sna, BigDecimal.class);
            filtros.add(filtro);
        }

        if (estatusCesSol != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("estado", "cl");
            filtro.setValor(estatusCesSol, EstadoCesionSolicitada.class);
            filtros.add(filtro);
        }

        if (poblacion != null) {
            // Búsqueda sobre tabla NG_CESION_SOLICITADA -> cl
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("poblacion", "cl");
            filtro.setValor(poblacion, Poblacion.class);
            filtros.add(filtro);
        }

        return filtros;
    }

    /**
     * Genera una lista con los filtros que se han definido.
     * @return Lista de objetos FiltroBusqueda con la información solicitada
     */
    public ArrayList<FiltroBusqueda> getListaFiltrosSolicitudCesionNng() {

        ArrayList<FiltroBusqueda> filtros = new ArrayList<FiltroBusqueda>();

        // Búsqueda sobre tabla NNG_SOLICITUD_CESION -> sl
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

        // Búsqueda sobre tabla NNG_CESION_SOLICITADA -> cs
        if (fechaImplementacionDesde != null) {
            try {
                FiltroBusqueda filtro = new FiltroBusqueda();
                filtro.setCampo("fechaCesion", "cl");
                filtro.setValor(formateador.parse(formateador.format(fechaImplementacionDesde)), Date.class);
                filtro.setFechaDesde(true);
                filtros.add(filtro);
            } catch (ParseException e) {
                LOGGER.error("Fecha incorrecta: " + numIniciofchSolicitud.toString());
            }
        }

        if (fechaImplementacionHasta != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("fechaCesion", "cl");
            filtro.setFechaHasta(true);

            Calendar calendarInstance = Calendar.getInstance();
            calendarInstance.setTime(fechaImplementacionHasta);
            calendarInstance.add(Calendar.DAY_OF_MONTH, 1);

            filtro.setValor(calendarInstance.getTime(), Date.class);
            filtros.add(filtro);
        }

        if (idClaveServicio != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("idClaveServicio", "cl");
            filtro.setValor(idClaveServicio, BigDecimal.class);
            filtros.add(filtro);
        }

        if (sna != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("sna", "cl");
            filtro.setValor(sna, BigDecimal.class);
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
    public ArrayList<FiltroBusqueda> getListaFiltrosSolicitudAsignacionNg() throws Exception {

        ArrayList<FiltroBusqueda> filtros = new ArrayList<FiltroBusqueda>();

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

        if (proveedor != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("proveedorSolicitante", "sl");
            filtro.setValor(proveedor, Proveedor.class);
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

        if (numInicioFechaAsignacion != null) {
            try {
                FiltroBusqueda filtro = new FiltroBusqueda();
                filtro.setCampo("fechaAsignacion", "sl");
                filtro.setValor(formateador.parse(formateador.format(numInicioFechaAsignacion)), Date.class);
                filtro.setFechaDesde(true);
                filtros.add(filtro);
            } catch (ParseException e) {
                LOGGER.error("Fecha incorrecta: " + numIniciofchSolicitud.toString());
            }
        }

        if (numFinalFechaAsignacion != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("fechaAsignacion", "sl");
            filtro.setFechaHasta(true);

            Calendar calendarInstance = Calendar.getInstance();
            calendarInstance.setTime(numFinalFechaAsignacion);
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

        // Búsqueda sobre tabla NG_NUM_SOLICITADA -> ns
        if (poblacion != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("poblacion", "ns");
            filtro.setValor(poblacion, Poblacion.class);
            filtros.add(filtro);
        }

        return filtros;
    }

    /**
     * Genera una lista con los filtros que se han definido.
     * @return Lista de pares (clave, valor)
     * @throws Exception exception
     */
    public ArrayList<FiltroBusqueda> getListaFiltrosSolicitudAsignacionNng() throws Exception {

        ArrayList<FiltroBusqueda> filtros = new ArrayList<FiltroBusqueda>();

        if (consecutivo != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("id", "sl");
            filtro.setValor(new BigDecimal(consecutivo), BigDecimal.class);
            filtros.add(filtro);
        }

        if (proveedor != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("proveedorSolicitante", "sl");
            filtro.setValor(proveedor, Proveedor.class);
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

        if (numInicioFechaAsignacion != null) {
            try {
                FiltroBusqueda filtro = new FiltroBusqueda();
                filtro.setCampo("fechaAsignacion", "sl");
                filtro.setValor(formateador.parse(formateador.format(numInicioFechaAsignacion)), Date.class);
                filtro.setFechaDesde(true);
                filtros.add(filtro);
            } catch (ParseException e) {
                LOGGER.error("Fecha incorrecta: " + numIniciofchSolicitud.toString());
            }
        }

        if (numFinalFechaAsignacion != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("fechaAsignacion", "sl");
            filtro.setFechaHasta(true);

            Calendar calendarInstance = Calendar.getInstance();
            calendarInstance.setTime(numFinalFechaAsignacion);
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

        if (tipoAsignacion != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("tipoAsignacion", "sl");
            filtro.setValor(tipoAsignacion, TipoAsignacion.class);
            filtros.add(filtro);
        }

        // Búsqueda sobre tabla NNG_NUM_SOLICITADA -> ns
        if (poblacion != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("poblacion", "ns");
            filtro.setValor(poblacion, Poblacion.class);
            filtros.add(filtro);
        }

        if (cliente != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("cliente", "ns");
            filtro.setValor(cliente, String.class);
            filtros.add(filtro);
        }

        if (claveServicio != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("claveServicio", "ns");
            filtro.setValor(claveServicio, ClaveServicio.class);
            filtros.add(filtro);
        }

        if (sna != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("sna", "ns");
            filtro.setValor(sna, BigDecimal.class);
            filtros.add(filtro);
        }

        return filtros;
    }

    /**
     * Genera una lista con los filtros que se han definido.
     * @return Lista de objetos FiltroBusqueda con la información solicitada
     */
    public ArrayList<FiltroBusqueda> getListaFiltrosSolicitudLiberacionNg() {

        ArrayList<FiltroBusqueda> filtros = new ArrayList<FiltroBusqueda>();

        // Búsqueda sobre tabla NG_SOLICITUD_LIBERACION -> sl
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

        if (proveedor != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("proveedorSolicitante", "sl");
            filtro.setValor(proveedor, Proveedor.class);
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

        if (oficioPstSolicitante != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("numOficioSolicitante", "sl");
            filtro.setValor(oficioPstSolicitante, String.class);
            filtros.add(filtro);
        }

        // Búsqueda sobre tabla NG_LIBERACION_SOLICITADA -> ls
        if (fechaLiberacionDesde != null) {
            try {
                FiltroBusqueda filtro = new FiltroBusqueda();
                filtro.setCampo("fechaLiberacion", "ls");
                filtro.setValor(formateador.parse(formateador.format(fechaLiberacionDesde)), Date.class);
                filtro.setFechaDesde(true);
                filtros.add(filtro);
            } catch (ParseException e) {
                LOGGER.error("Fecha incorrecta: " + numIniciofchSolicitud.toString());
            }
        }

        if (fechaLiberacionHasta != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("fechaLiberacion", "ls");
            filtro.setFechaHasta(true);

            Calendar calendarInstance = Calendar.getInstance();
            calendarInstance.setTime(fechaLiberacionHasta);
            calendarInstance.add(Calendar.DAY_OF_MONTH, 1);

            filtro.setValor(calendarInstance.getTime(), Date.class);
            filtros.add(filtro);
        }

        if (idNir != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("idNir", "ls");
            filtro.setValor(idNir, BigDecimal.class);
            filtros.add(filtro);
        }

        if (sna != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("sna", "ls");
            filtro.setValor(sna, BigDecimal.class);
            filtros.add(filtro);
        }

        if (estatusLibSol != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("estado", "ls");
            filtro.setValor(estatusLibSol, EstadoLiberacionSolicitada.class);
            filtros.add(filtro);
        }

        if (poblacion != null) {
            // Búsqueda sobre tabla NG_LIBERACION_SOLICITADA -> ls
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("poblacion", "ls");
            filtro.setValor(poblacion, Poblacion.class);
            filtros.add(filtro);
        }

        return filtros;
    }

    /**
     * Genera una lista con los filtros que se han definido.
     * @return Lista de objetos FiltroBusqueda con la información solicitada
     */
    public ArrayList<FiltroBusqueda> getListaFiltrosSolicitudLiberacionNng() {

        ArrayList<FiltroBusqueda> filtros = new ArrayList<FiltroBusqueda>();

        // Búsqueda sobre tabla NNG_SOLICITUD_LIBERACION -> sl
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

        if (proveedor != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("proveedorSolicitante", "sl");
            filtro.setValor(proveedor, Proveedor.class);
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

        if (oficioPstSolicitante != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("numOficioSolicitante", "sl");
            filtro.setValor(oficioPstSolicitante, String.class);
            filtros.add(filtro);
        }

        // Búsqueda sobre tabla NNG_LIBERACION_SOLICITADA -> ls
        if (fechaLiberacionDesde != null) {
            try {
                FiltroBusqueda filtro = new FiltroBusqueda();
                filtro.setCampo("fechaLiberacion", "ls");
                filtro.setValor(formateador.parse(formateador.format(fechaLiberacionDesde)), Date.class);
                filtro.setFechaDesde(true);
                filtros.add(filtro);
            } catch (ParseException e) {
                LOGGER.error("Fecha incorrecta: " + numIniciofchSolicitud.toString());
            }
        }

        if (fechaLiberacionHasta != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("fechaLiberacion", "ls");
            filtro.setFechaHasta(true);

            Calendar calendarInstance = Calendar.getInstance();
            calendarInstance.setTime(fechaLiberacionHasta);
            calendarInstance.add(Calendar.DAY_OF_MONTH, 1);

            filtro.setValor(calendarInstance.getTime(), Date.class);
            filtros.add(filtro);
        }

        if (idClaveServicio != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("idClaveServicio", "ls");
            filtro.setValor(idClaveServicio, BigDecimal.class);
            filtros.add(filtro);
        }

        if (sna != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("sna", "ls");
            filtro.setValor(sna, BigDecimal.class);
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
    public ArrayList<FiltroBusqueda> getListaFiltrosSolicitudRedistribucionNg() {

        ArrayList<FiltroBusqueda> filtros = new ArrayList<FiltroBusqueda>();

        // Búsqueda sobre la tabla NG_SOLICITUD_REDISTRIBUCION -> sl
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

        if (proveedor != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("proveedorSolicitante", "sl");
            filtro.setValor(proveedor, Proveedor.class);
            filtros.add(filtro);
        }

        if (fechaRedistribucionDesde != null) {
            try {
                FiltroBusqueda filtro = new FiltroBusqueda();
                filtro.setCampo("fechaAsignacion", "sl");
                filtro.setValor(formateador.parse(formateador.format(fechaRedistribucionDesde)), Date.class);
                filtro.setFechaDesde(true);
                filtros.add(filtro);
            } catch (ParseException e) {
                LOGGER.error("Fecha incorrecta: " + fechaRedistribucionDesde.toString());
            }
        }

        if (fechaRedistribucionHasta != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("fechaAsignacion", "sl");
            filtro.setFechaHasta(true);

            Calendar calendarInstance = Calendar.getInstance();
            calendarInstance.setTime(fechaRedistribucionHasta);
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

        // Búsqueda sobre tabla NG_REDISTRIBUCION_SOL -> rs
        if (idNir != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("idNir", "rs");
            filtro.setValor(idNir, BigDecimal.class);
            filtros.add(filtro);
        }

        if (sna != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("sna", "rs");
            filtro.setValor(sna, BigDecimal.class);
            filtros.add(filtro);
        }

        if (poblacion != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("poblacion", "rs");
            filtro.setValor(poblacion, Poblacion.class);
            filtros.add(filtro);
        }

        return filtros;
    }

    /**
     * Genera una lista con los filtros que se han definido.
     * @return Lista de objetos FiltroBusqueda con la información solicitada
     */
    public ArrayList<FiltroBusqueda> getListaFiltrosSolicitudRedistribucionNng() {

        ArrayList<FiltroBusqueda> filtros = new ArrayList<FiltroBusqueda>();

        // Búsqueda sobre la tabla NNG_SOLICITUD_REDISTRIBUCION -> sl
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

        if (proveedor != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("proveedorSolicitante", "sl");
            filtro.setValor(proveedor, Proveedor.class);
            filtros.add(filtro);
        }

        if (fechaRedistribucionDesde != null) {
            try {
                FiltroBusqueda filtro = new FiltroBusqueda();
                filtro.setCampo("fechaAsignacion", "sl");
                filtro.setValor(formateador.parse(formateador.format(fechaRedistribucionDesde)), Date.class);
                filtro.setFechaDesde(true);
                filtros.add(filtro);
            } catch (ParseException e) {
                LOGGER.error("Fecha incorrecta: " + fechaRedistribucionDesde.toString());
            }
        }

        if (fechaRedistribucionHasta != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("fechaAsignacion", "sl");
            filtro.setFechaHasta(true);

            Calendar calendarInstance = Calendar.getInstance();
            calendarInstance.setTime(fechaRedistribucionHasta);
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

        // Búsqueda sobre tabla NNG_REDISTRIBUCION_SOL -> rs
        if (idClaveServicio != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("idClaveServicio", "rs");
            filtro.setValor(idClaveServicio, BigDecimal.class);
            filtros.add(filtro);
        }

        if (sna != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("sna", "rs");
            filtro.setValor(sna, BigDecimal.class);
            filtros.add(filtro);
        }

        return filtros;
    }

    /**
     * Genera una lista con los filtros que se han definido para la consulta de consolidaciones.
     * @return Lista de pares (clave, valor)
     */
    public ArrayList<FiltroBusqueda> getListaFiltrosSolicitudConsolidacion() {

        ArrayList<FiltroBusqueda> filtros = new ArrayList<FiltroBusqueda>();

        // EstadoAbn estadoAbn = new EstadoAbn();
        // estadoAbn.setCodigo(EstadoAbn.INACTIVO);
        //
        // FiltroBusqueda filtroInicialEnt = new FiltroBusqueda();
        // filtroInicialEnt.setCampo("estadoAbn", "ea");
        // filtroInicialEnt.setValor(estadoAbn, EstadoAbn.class);
        // filtros.add(filtroInicialEnt);
        //
        // filtroInicialEnt.setDistinto(true);
        //
        // FiltroBusqueda filtroInicialRec = new FiltroBusqueda();
        // filtroInicialRec.setCampo("estadoAbn", "ear");
        // filtroInicialRec.setValor(estadoAbn, EstadoAbn.class);
        // filtros.add(filtroInicialRec);
        //
        // filtroInicialRec.setDistinto(true);

        // Búsqueda sobre tabla SOL_SOLICITUD -> sl
        if (consecutivo != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("id", "sl");
            filtro.setValor(Long.parseLong(consecutivo), Long.class);
            filtros.add(filtro);
        }

        if (estado != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("estadoSolicitud", "sl");
            filtro.setValor(estado, EstadoSolicitud.class);
            filtros.add(filtro);
        }

        if (poblacion != null) {
            Abn abn = new Abn();
            abn.setCodigoAbn(poblacion.getAbn().getCodigoAbn());
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("abnEntrega", "sl");
            filtro.setValor(abn, BigDecimal.class);
            filtro.setCampoSecundario("abnRecibe", "sl");
            filtro.setValorSecundario(abn, BigDecimal.class);
            filtros.add(filtro);
            filtro.setPoblacion(true);
        }

        if (estadoMun != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("estado", "pa");
            filtro.setValor(estadoMun, Estado.class);
            filtros.add(filtro);
            filtro.setEstadoMun(true);
        }

        if (municipio != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("municipio", "pa");
            filtro.setValor(municipio, Municipio.class);
            filtros.add(filtro);
            filtro.setMunicipio(true);
        }

        if (abnEntrega != null && !abnEntrega.isEmpty()) {
            Abn abn = new Abn();
            abn.setCodigoAbn(new BigDecimal(abnEntrega));
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("abnEntrega", "sl");
            filtro.setValor(abn, BigDecimal.class);
            filtros.add(filtro);
        }

        if (abnRecibe != null && !abnRecibe.isEmpty()) {
            Abn abn = new Abn();
            abn.setCodigoAbn(new BigDecimal(abnRecibe));
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("abnRecibe", "sl");
            filtro.setValor(abn, BigDecimal.class);
            filtros.add(filtro);
        }

        // Búsqueda sobre tabla NG_ABN_CONSOLIDAR -> ac
        if (estatusAbnConso != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("estado", "ac");
            filtro.setValor(estatusAbnConso, EstadoAbnConsolidar.class);
            filtros.add(filtro);
        }

        if (fechaIniConsolidacion != null) {
            try {
                FiltroBusqueda filtro = new FiltroBusqueda();
                filtro.setCampo("fechaConsolidacion", "ac");
                filtro.setValor(formateador.parse(formateador.format(fechaIniConsolidacion)), Date.class);
                filtro.setFechaDesde(true);
                filtros.add(filtro);
            } catch (ParseException e) {
                LOGGER.error("Fecha incorrecta: " + fechaIniConsolidacion.toString());
            }
        }

        if (fechaFinConsolidacion != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("fechaConsolidacion", "ac");
            filtro.setFechaHasta(true);

            Calendar calendarInstance = Calendar.getInstance();
            calendarInstance.setTime(fechaFinConsolidacion);
            calendarInstance.add(Calendar.DAY_OF_MONTH, 1);

            filtro.setValor(calendarInstance.getTime(), Date.class);
            filtros.add(filtro);
        }

        return filtros;
    }

    /**
     * Genera una lista con los filtros que se han definido para la consulta genérica de solicitudes.
     * @return Lista de pares (clave, valor)
     * @throws Exception exception
     */
    public ArrayList<FiltroBusqueda> getListaFiltrosConsultaGenerica() {
        ArrayList<FiltroBusqueda> filtros = new ArrayList<FiltroBusqueda>();

        // Búsqueda sobre la vista V_SOLICITUD_POBLACION
        if (consecutivo != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("solicitud", "vsp");
            filtro.setValor(Long.parseLong(consecutivo), Long.class);
            filtros.add(filtro);
        }
        if (proveedor != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("proveedor", "vsp");
            filtro.setValor(proveedor, Proveedor.class);
            filtros.add(filtro);
        }

        if (numIniciofchSolicitud != null) {
            try {
                FiltroBusqueda filtro = new FiltroBusqueda();
                filtro.setCampo("desde-fechaSolicitud", "vsp");
                filtro.setValor(formateador.parse(formateador.format(numIniciofchSolicitud)), Date.class);
                filtro.setFechaDesde(true);
                filtros.add(filtro);
            } catch (ParseException e) {
                LOGGER.error("Fecha incorrecta: " + numIniciofchSolicitud.toString());
            }
        }

        if (numFinalfchSolicitud != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("hasta-fechaSolicitud", "vsp");
            filtro.setFechaHasta(true);

            Calendar calendarInstance = Calendar.getInstance();
            calendarInstance.setTime(numFinalfchSolicitud);
            calendarInstance.add(Calendar.DAY_OF_MONTH, 1);

            filtro.setValor(calendarInstance.getTime(), Date.class);
            filtros.add(filtro);
        }

        if (numInicioFechaAsignacion != null) {
            try {
                FiltroBusqueda filtro = new FiltroBusqueda();
                filtro.setCampo("desde-fechaAsignacion", "vsp");
                filtro.setValor(formateador.parse(formateador.format(numInicioFechaAsignacion)), Date.class);
                filtro.setFechaDesde(true);
                filtros.add(filtro);
            } catch (ParseException e) {
                LOGGER.error("Fecha incorrecta: " + numIniciofchSolicitud.toString());
            }
        }

        if (numFinalFechaAsignacion != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("hasta-fechaAsignacion", "vsp");
            filtro.setFechaHasta(true);

            Calendar calendarInstance = Calendar.getInstance();
            calendarInstance.setTime(numFinalFechaAsignacion);
            calendarInstance.add(Calendar.DAY_OF_MONTH, 1);

            filtro.setValor(calendarInstance.getTime(), Date.class);
            filtros.add(filtro);
        }
        if (estado != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("estatus", "vsp");
            filtro.setValor(estado, EstadoSolicitud.class);
            filtros.add(filtro);
        }
        if (tipoSolicitud != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("tipoSolicitud", "vsp");
            filtro.setValor(tipoSolicitud, TipoSolicitud.class);
            filtros.add(filtro);
        }

        if (poblacion != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("inegi", "vsp");
            filtro.setValor(poblacion.getInegi(), String.class);
            filtros.add(filtro);

        }

        if (estadoMun != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("codEstado", "vsp");
            filtro.setValor(estadoMun.getCodEstado(), String.class);
            filtros.add(filtro);

        }

        if (municipio != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("codMunicipio", "vsp");
            filtro.setValor(municipio.getId().getCodMunicipio(), String.class);
            filtros.add(filtro);

        }

        if (oficioPstSolicitante != null) {
            // Número de Oficio
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("numOficio", "vsp");
            filtro.setValor(oficioPstSolicitante, String.class);
            filtros.add(filtro);
        }

        return filtros;
    }

    /**
     * Genera una lista con los filtros que se han definido para la consulta genérica de solicitudes NNG.
     * @return Lista de pares (clave, valor)
     * @throws Exception exception
     */
    public ArrayList<FiltroBusqueda> getListaFiltrosConsultaGenericaNng() {
        ArrayList<FiltroBusqueda> filtros = new ArrayList<FiltroBusqueda>();

        // Búsqueda sobre la tabla SOL_SOLICITUD
        if (consecutivo != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("id", "ss");
            filtro.setValor(Long.parseLong(consecutivo), Long.class);
            filtros.add(filtro);
        }

        if (proveedor != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("proveedorSolicitante", "ss");
            filtro.setValor(proveedor, Proveedor.class);
            filtros.add(filtro);
        }

        if (numIniciofchSolicitud != null) {
            try {
                FiltroBusqueda filtro = new FiltroBusqueda();
                filtro.setCampo("fechaSolicitud", "ss");
                filtro.setValor(formateador.parse(formateador.format(numIniciofchSolicitud)), Date.class);
                filtro.setFechaDesde(true);
                filtros.add(filtro);
            } catch (ParseException e) {
                LOGGER.error("Fecha incorrecta: " + numIniciofchSolicitud.toString());
            }
        }

        if (numFinalfchSolicitud != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("fechaSolicitud", "ss");
            filtro.setFechaHasta(true);

            Calendar calendarInstance = Calendar.getInstance();
            calendarInstance.setTime(numFinalfchSolicitud);
            calendarInstance.add(Calendar.DAY_OF_MONTH, 1);

            filtro.setValor(calendarInstance.getTime(), Date.class);
            filtros.add(filtro);
        }

        if (fechaIniTramitacion != null) {
            try {
                FiltroBusqueda filtro = new FiltroBusqueda();
                filtro.setCampo("fechaAsignacion", "ss");
                filtro.setValor(formateador.parse(formateador.format(fechaIniTramitacion)), Date.class);
                filtro.setFechaDesde(true);
                filtros.add(filtro);
            } catch (ParseException e) {
                LOGGER.error("Fecha incorrecta: " + fechaIniTramitacion.toString());
            }
        }

        if (fechaFinTramitacion != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("fechaAsignacion", "ss");
            filtro.setFechaHasta(true);

            Calendar calendarInstance = Calendar.getInstance();
            calendarInstance.setTime(fechaFinTramitacion);
            calendarInstance.add(Calendar.DAY_OF_MONTH, 1);

            filtro.setValor(calendarInstance.getTime(), Date.class);
            filtros.add(filtro);
        }

        if (estado != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("estadoSolicitud", "ss");
            filtro.setValor(estado, EstadoSolicitud.class);
            filtros.add(filtro);
        }

        if (oficioPstSolicitante != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("numOficioSolicitante", "ss");
            filtro.setValor(oficioPstSolicitante, String.class);
            filtros.add(filtro);
        }

        if (tipoSolicitud != null) {
            FiltroBusqueda filtro = new FiltroBusqueda();
            filtro.setCampo("tipoSolicitud", "ss");
            filtro.setValor(tipoSolicitud, TipoSolicitud.class);
            filtro.setFiltroTipoSolicitud(true);
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
        referenciaSolicitud = null;
        fechaUtilizacion = null;
        fechaSolicitud = null;
        estado = null;
        idNir = null;
        sna = null;
        numIniciofchSolicitud = null;
        numFinalfchSolicitud = null;
        numIniciofchIniUtilizacion = null;
        numFinalfchIniUtilizacion = null;
        numInicioFechaAsignacion = null;
        numFinalFechaAsignacion = null;
        abnEntrega = null;
        abnRecibe = null;
        fechaConsolidacion = null;
        fechaIniConsolidacion = null;
        fechaFinConsolidacion = null;
        estadoMun = null;
        municipio = null;
        tipoSolicitud = null;
        fechaAutorizacionDesde = null;
        fechaAutorizacionHasta = null;
        fechaAsignacionHasta = null;
        fechaAsignacionDesde = null;
        fechaRedistribucionDesde = null;
        fechaRedistribucionHasta = null;
        oficioPstSolicitante = null;
        idClaveServicio = null;
        tipoAsignacion = null;
        cliente = null;
        fechaIniTramitacion = null;
        fechaFinTramitacion = null;
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
        if (fechaSolicitud != null) {
            trace.append("fechaSolicitud").append(": ").append(fechaSolicitud).append(", ");
        }
        if (numIniciofchSolicitud != null) {
            trace.append("numIniciofchSolicitud").append(": ").append(numIniciofchSolicitud).append(", ");
        }
        if (numFinalfchSolicitud != null) {
            trace.append("numFinalfchSolicitud").append(": ").append(numFinalfchSolicitud).append(", ");
        }
        if (fchIniUtilizacion != null) {
            trace.append("fchIniUtilizacion").append(": ").append(fchIniUtilizacion).append(", ");
        }
        if (fchIniPruebas != null) {
            trace.append("fchIniPruebas").append(": ").append(fchIniPruebas).append(", ");
        }
        if (fechaUtilizacion != null) {
            trace.append("fechaUtilizacion").append(": ").append(fechaUtilizacion).append(", ");
        }
        if (numIniciofchIniUtilizacion != null) {
            trace.append("numIniciofchIniUtilizacion").append(": ").append(numIniciofchIniUtilizacion).append(", ");
        }
        if (numFinalfchIniUtilizacion != null) {
            trace.append("numFinalfchIniUtilizacion").append(": ").append(numFinalfchIniUtilizacion).append(", ");
        }
        if (fechaAsignacionDesde != null) {
            trace.append("fechaAsignacionDesde").append(": ").append(fechaAsignacionDesde).append(", ");
        }
        if (fechaAsignacionHasta != null) {
            trace.append("fechaAsignacionHasta").append(": ").append(fechaAsignacionHasta).append(", ");
        }
        if (numInicioFechaAsignacion != null) {
            trace.append("numInicioFechaAsignacion").append(": ").append(numInicioFechaAsignacion).append(", ");
        }
        if (numFinalFechaAsignacion != null) {
            trace.append("numFinalFechaAsignacion").append(": ").append(numFinalFechaAsignacion).append(", ");
        }
        if (fechaAutorizacionDesde != null) {
            trace.append("fechaAutorizacionDesde").append(": ").append(fechaAutorizacionDesde).append(", ");
        }
        if (fechaAutorizacionHasta != null) {
            trace.append("fechaAutorizacionHasta").append(": ").append(fechaAutorizacionHasta).append(", ");
        }
        if (fechaConsolidacion != null) {
            trace.append("fechaConsolidacion").append(": ").append(fechaConsolidacion).append(", ");
        }
        if (fechaIniConsolidacion != null) {
            trace.append("fechaIniConsolidacion").append(": ").append(fechaIniConsolidacion).append(", ");
        }
        if (fechaFinConsolidacion != null) {
            trace.append("fechaFinConsolidacion").append(": ").append(fechaFinConsolidacion).append(", ");
        }
        if (fechaImplementacionDesde != null) {
            trace.append("fechaImplementacionDesde").append(": ").append(fechaImplementacionDesde).append(", ");
        }
        if (fechaImplementacionHasta != null) {
            trace.append("fechaImplementacionHasta").append(": ").append(fechaImplementacionHasta).append(", ");
        }
        if (fechaLiberacionDesde != null) {
            trace.append("fechaLiberacionDesde").append(": ").append(fechaLiberacionDesde).append(", ");
        }
        if (fechaLiberacionHasta != null) {
            trace.append("fechaLiberacionHasta").append(": ").append(fechaLiberacionHasta).append(", ");
        }
        if (fechaRedistribucionDesde != null) {
            trace.append("fechaRedistribucionDesde").append(": ").append(fechaRedistribucionDesde).append(", ");
        }
        if (fechaRedistribucionHasta != null) {
            trace.append("fechaRedistribucionHasta").append(": ").append(fechaRedistribucionHasta).append(", ");
        }
        if (oficioPstSolicitante != null) {
            trace.append("oficioPstSolicitante").append(": ").append(oficioPstSolicitante).append(", ");
        }
        if (estado != null) {
            trace.append("estado").append(": ").append(estado.getCodigo()).append(", ");
        }
        if (abnEntrega != null) {
            trace.append("abnEntrega").append(": ").append(abnEntrega).append(", ");
        }
        if (abnRecibe != null) {
            trace.append("abnRecibe").append(": ").append(abnRecibe).append(", ");
        }
        if (estadoMun != null) {
            trace.append("estadoMun").append(": ").append(estadoMun).append(", ");
        }
        if (municipio != null) {
            trace.append("municipio").append(": ").append(municipio).append(", ");
        }
        if (poblacion != null) {
            trace.append("poblacion").append(": ").append(poblacion).append(", ");
        }
        if (idNir != null) {
            trace.append("idNir").append(": ").append(idNir).append(", ");
        }
        if (idClaveServicio != null) {
            trace.append("idClaveServicio").append(": ").append(idClaveServicio).append(", ");
        }
        if (sna != null) {
            trace.append("sna").append(": ").append(sna).append(", ");
        }
        if (fechaIniTramitacion != null) {
            trace.append("fechaIniTramitacion").append(": ").append(fechaIniTramitacion).append(", ");
        }
        if (fechaFinTramitacion != null) {
            trace.append("fechaFinTramitacion").append(": ").append(fechaFinTramitacion).append(", ");
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
     * Fecha de solicitud.
     * @return fechaSolicitud fechaSolicitud
     **/
    public Date getFechaSolicitud() {
        return fechaSolicitud;
    }

    /**
     * Fecha de solicitud.
     * @param fechaSolicitud fechaSolicitud
     **/
    public void setFechaSolicitud(Date fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
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
     * Fecha de Asignación de Solicitud.
     * @return fechaUtilizacion fechaUtilizacion
     **/
    public Date getFechaUtilizacion() {
        return fechaUtilizacion;
    }

    /**
     * Fecha de Asignación de Solicitud.
     * @param fechaUtilizacion fechaUtilizacion
     **/
    public void setFechaUtilizacion(Date fechaUtilizacion) {
        this.fechaUtilizacion = fechaUtilizacion;
    }

    /**
     * @return the pstCesionario
     */
    public Proveedor getPstCesionario() {
        return pstCesionario;
    }

    /**
     * @return the fchIniUtilizacion
     */
    public Date getFchIniUtilizacion() {
        return fchIniUtilizacion;
    }

    /**
     * @return the fechaImplementacion
     */
    public Date getFchIniPruebas() {
        return fchIniPruebas;
    }

    /**
     * @param pstCesionario the pstCesionario to set
     */
    public void setPstCesionario(Proveedor pstCesionario) {
        this.pstCesionario = pstCesionario;
    }

    /**
     * @param fchIniUtilizacion the fchIniUtilizacion to set
     */
    public void setFchIniUtilizacion(Date fchIniUtilizacion) {
        this.fchIniUtilizacion = fchIniUtilizacion;
    }

    /**
     * @param fchIniPruebas the fchIniPruebas to set
     */
    public void setFchIniPruebas(Date fchIniPruebas) {
        this.fchIniPruebas = fchIniPruebas;
    }

    /** @return numIniciofchSolicitud */
    public Date getNumIniciofchSolicitud() {
        return numIniciofchSolicitud;
    }

    /** @param numIniciofchSolicitud */
    public void setNumIniciofchSolicitud(Date numIniciofchSolicitud) {
        this.numIniciofchSolicitud = numIniciofchSolicitud;
    }

    /** @return numFinalfchSolicitud */
    public Date getNumFinalfchSolicitud() {
        return numFinalfchSolicitud;
    }

    /** @param numFinalfchSolicitud */
    public void setNumFinalfchSolicitud(Date numFinalfchSolicitud) {
        this.numFinalfchSolicitud = numFinalfchSolicitud;
    }

    /** @return numIniciofchIniUtilizacion */
    public Date getNumIniciofchIniUtilizacion() {
        return numIniciofchIniUtilizacion;
    }

    /** @param numIniciofchIniUtilizacion */
    public void setNumIniciofchIniUtilizacion(Date numIniciofchIniUtilizacion) {
        this.numIniciofchIniUtilizacion = numIniciofchIniUtilizacion;
    }

    /** @return numFinalfchIniUtilizacion */
    public Date getNumFinalfchIniUtilizacion() {
        return numFinalfchIniUtilizacion;
    }

    /** @param numFinalfchIniUtilizacion */
    public void setNumFinalfchIniUtilizacion(Date numFinalfchIniUtilizacion) {
        this.numFinalfchIniUtilizacion = numFinalfchIniUtilizacion;
    }

    /**
     * abnEntrega.
     * @return the abnEntrega
     */
    public String getAbnEntrega() {
        return abnEntrega;
    }

    /**
     * abnEntrega.
     * @param abnEntrega the abnEntrega to set
     */
    public void setAbnEntrega(String abnEntrega) {
        this.abnEntrega = abnEntrega;
    }

    /**
     * abnRecibe.
     * @return the abnRecibe
     */
    public String getAbnRecibe() {
        return abnRecibe;
    }

    /**
     * abnRecibe.
     * @param abnRecibe the abnRecibe to set
     */
    public void setAbnRecibe(String abnRecibe) {
        this.abnRecibe = abnRecibe;
    }

    /**
     * Fecha de consolidacion.
     * @return the fechaConsolidacion
     */
    public Date getFechaConsolidacion() {
        return fechaConsolidacion;
    }

    /**
     * Fecha de consolidacion.
     * @param fechaConsolidacion the fechaConsolidacion to set
     */
    public void setFechaConsolidacion(Date fechaConsolidacion) {
        this.fechaConsolidacion = fechaConsolidacion;
    }

    /**
     * fechaIniConsolidacion.
     * @return the fechaIniConsolidacion
     */
    public Date getFechaIniConsolidacion() {
        return fechaIniConsolidacion;
    }

    /**
     * fechaIniConsolidacion.
     * @param fechaIniConsolidacion the fechaIniConsolidacion to set
     */
    public void setFechaIniConsolidacion(Date fechaIniConsolidacion) {
        this.fechaIniConsolidacion = fechaIniConsolidacion;
    }

    /**
     * fechaFinConsolidacion.
     * @return the fechaFinConsolidacion
     */
    public Date getFechaFinConsolidacion() {
        return fechaFinConsolidacion;
    }

    /**
     * fechaFinConsolidacion.
     * @param fechaFinConsolidacion the fechaFinConsolidacion to set
     */
    public void setFechaFinConsolidacion(Date fechaFinConsolidacion) {
        this.fechaFinConsolidacion = fechaFinConsolidacion;
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

    /**
     * Estado de Poblacion.
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
     * Rango inicio FechaAsignacion.
     * @return the numInicioFechaAsignacion
     */
    public Date getNumInicioFechaAsignacion() {
        return numInicioFechaAsignacion;
    }

    /**
     * Rango inicio FechaAsignacion.
     * @param numInicioFechaAsignacion the numInicioFechaAsignacion to set
     */
    public void setNumInicioFechaAsignacion(Date numInicioFechaAsignacion) {
        this.numInicioFechaAsignacion = numInicioFechaAsignacion;
    }

    /**
     * Rango final FechaAsignacion.
     * @return the numFinalFechaAsignacion
     */
    public Date getNumFinalFechaAsignacion() {
        return numFinalFechaAsignacion;
    }

    /**
     * Rango final FechaAsignacion.
     * @param numFinalFechaAsignacion the numFinalFechaAsignacion to set
     */
    public void setNumFinalFechaAsignacion(Date numFinalFechaAsignacion) {
        this.numFinalFechaAsignacion = numFinalFechaAsignacion;
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
     * identificador de referenciaSolicitud.
     * @return referenciaSolicitud
     */
    public String getReferenciaSolicitud() {
        return referenciaSolicitud;
    }

    /**
     * identificador de referenciaSolicitud.
     * @param referenciaSolicitud String
     */
    public void setReferenciaSolicitud(String referenciaSolicitud) {
        this.referenciaSolicitud = referenciaSolicitud;
    }

    /**
     * Identificador de Numeración de la región.
     * @return BigDecimal
     */
    public BigDecimal getIdNir() {
        return idNir;
    }

    /**
     * Identificador de Numeración de la región.
     * @param idNir BigDecimal
     */
    public void setIdNir(BigDecimal idNir) {
        this.idNir = idNir;
    }

    /**
     * Identificador de la serie.
     * @return BigDecimal
     */
    public BigDecimal getSna() {
        return sna;
    }

    /**
     * Identificador de la serie.
     * @param sna BigDecimal
     */
    public void setSna(BigDecimal sna) {
        this.sna = sna;
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
     * Rango inicio fchIniImplementacion.
     * @return the numInicioFechImpl
     */
    public Date getNumInicioFechImpl() {
        return numInicioFechImpl;
    }

    /**
     * Rango inicio fchIniImplementacion.
     * @param numInicioFechImpl the numInicioFechImpl to set
     */
    public void setNumInicioFechImpl(Date numInicioFechImpl) {
        this.numInicioFechImpl = numInicioFechImpl;
    }

    /**
     * Rango final fchFinImplementacion.
     * @return the numFinalFechImpl
     */
    public Date getNumFinalFechImpl() {
        return numFinalFechImpl;
    }

    /**
     * Rango final fchFinImplementacion.
     * @param numFinalFechImpl the numFinalFechImpl to set
     */
    public void setNumFinalFechImpl(Date numFinalFechImpl) {
        this.numFinalFechImpl = numFinalFechImpl;
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
     * Rango inicio Fecha Autorización.
     * @return Date
     */
    public Date getFechaAutorizacionDesde() {
        return fechaAutorizacionDesde;
    }

    /**
     * Rango inicio Fecha Autorización.
     * @param fechaAutorizacionDesde Date
     */
    public void setFechaAutorizacionDesde(Date fechaAutorizacionDesde) {
        this.fechaAutorizacionDesde = fechaAutorizacionDesde;
    }

    /**
     * Rango final Fecha Autorización.
     * @return Date
     */
    public Date getFechaAutorizacionHasta() {
        return fechaAutorizacionHasta;
    }

    /**
     * Rango final Fecha Autorización.
     * @param fechaAutorizacionHasta Date
     */
    public void setFechaAutorizacionHasta(Date fechaAutorizacionHasta) {
        this.fechaAutorizacionHasta = fechaAutorizacionHasta;
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
     * Rango inicio fecha Liberación.
     * @return Date
     */
    public Date getFechaLiberacionDesde() {
        return fechaLiberacionDesde;
    }

    /**
     * Rango inicio fecha Liberación.
     * @param fechaLiberacionDesde Date
     */
    public void setFechaLiberacionDesde(Date fechaLiberacionDesde) {
        this.fechaLiberacionDesde = fechaLiberacionDesde;
    }

    /**
     * Rango final fecha Liberación.
     * @return Date
     */
    public Date getFechaLiberacionHasta() {
        return fechaLiberacionHasta;
    }

    /**
     * Rango final fecha Liberación.
     * @param fechaLiberacionHasta Date
     */
    public void setFechaLiberacionHasta(Date fechaLiberacionHasta) {
        this.fechaLiberacionHasta = fechaLiberacionHasta;
    }

    /**
     * Rango Inicial fecha Redistribución.
     * @return Date
     */
    public Date getFechaRedistribucionDesde() {
        return fechaRedistribucionDesde;
    }

    /**
     * Rango Inicial fecha Redistribución.
     * @param fechaRedistribucionDesde Date
     */
    public void setFechaRedistribucionDesde(Date fechaRedistribucionDesde) {
        this.fechaRedistribucionDesde = fechaRedistribucionDesde;
    }

    /**
     * Rango Final fecha Redistribución.
     * @return Date
     */
    public Date getFechaRedistribucionHasta() {
        return fechaRedistribucionHasta;
    }

    /**
     * Rango Final fecha Redistribución.
     * @param fechaRedistribucionHasta Date
     */
    public void setFechaRedistribucionHasta(Date fechaRedistribucionHasta) {
        this.fechaRedistribucionHasta = fechaRedistribucionHasta;
    }

    /**
     * Estatus de las Liberaciones Solicitadas asociadas a la Solicitud.
     * @return EstadoLiberacionSolicitada
     */
    public EstadoLiberacionSolicitada getEstatusLibSol() {
        return estatusLibSol;
    }

    /**
     * Estatus de las Liberaciones Solicitadas asociadas a la Solicitud.
     * @param estatusLibSol EstadoLiberacionSolicitada
     */
    public void setEstatusLibSol(EstadoLiberacionSolicitada estatusLibSol) {
        this.estatusLibSol = estatusLibSol;
    }

    /**
     * Estatus de las Cesiones Solicitadas asociadas a la Solicitud.
     * @return EstadoCesionSolicitada
     */
    public EstadoCesionSolicitada getEstatusCesSol() {
        return estatusCesSol;
    }

    /**
     * Estatus de las Cesiones Solicitadas asociadas a la Solicitud.
     * @param estatusCesSol EstadoCesionSolicitada
     */
    public void setEstatusCesSol(EstadoCesionSolicitada estatusCesSol) {
        this.estatusCesSol = estatusCesSol;
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
     * Estado de Abn a Consolidar.
     * @return the estatusAbnConso
     */
    public EstadoAbnConsolidar getEstatusAbnConso() {
        return estatusAbnConso;
    }

    /**
     * Estado de Abn a Consolidar.
     * @param estatusAbnConso the estatusAbnConso to set
     */
    public void setEstatusAbnConso(EstadoAbnConsolidar estatusAbnConso) {
        this.estatusAbnConso = estatusAbnConso;
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
     * Tipo de asignación.
     * @return the tipoAsignacion
     */
    public TipoAsignacion getTipoAsignacion() {
        return tipoAsignacion;
    }

    /**
     * Tipo de asignación.
     * @param tipoAsignacion the tipoAsignacion to set
     */
    public void setTipoAsignacion(TipoAsignacion tipoAsignacion) {
        this.tipoAsignacion = tipoAsignacion;
    }

    /**
     * Cliente de la numeración.
     * @return String
     */
    public String getCliente() {
        return cliente;
    }

    /**
     * Cliente de la numeración.
     * @param cliente String
     */
    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    /**
     * Rango inicio FechaAsignacion.
     * @return the fechaIniTramitacion
     */
    public Date getFechaIniTramitacion() {
        return fechaIniTramitacion;
    }

    /**
     * Rango inicio FechaAsignacion.
     * @param fechaIniTramitacion the fechaIniTramitacion to set
     */
    public void setFechaIniTramitacion(Date fechaIniTramitacion) {
        this.fechaIniTramitacion = fechaIniTramitacion;
    }

    /**
     * Rango final FechaAsignacion.
     * @return the fechaFinTramitacion
     */
    public Date getFechaFinTramitacion() {
        return fechaFinTramitacion;
    }

    /**
     * Rango final FechaAsignacion.
     * @param fechaFinTramitacion the fechaFinTramitacion to set
     */
    public void setFechaFinTramitacion(Date fechaFinTramitacion) {
        this.fechaFinTramitacion = fechaFinTramitacion;
    }

    /**
     * Identificador de la clave de servicio.
     * @return BigDecimal
     */
    public BigDecimal getIdClaveServicio() {
        return idClaveServicio;
    }

    /**
     * Identificador de la clave de servicio.
     * @param idClaveServicio BigDecimal
     */
    public void setIdClaveServicio(BigDecimal idClaveServicio) {
        this.idClaveServicio = idClaveServicio;
    }

    /**
     * Clave de servicio.
     * @return the claveServicio
     */
    public ClaveServicio getClaveServicio() {
        return claveServicio;
    }

    /**
     * Clave de servicio.
     * @param claveServicio the claveServicio to set
     */
    public void setClaveServicio(ClaveServicio claveServicio) {
        this.claveServicio = claveServicio;
    }
}
