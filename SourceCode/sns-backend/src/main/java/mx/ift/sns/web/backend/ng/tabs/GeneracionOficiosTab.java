package mx.ift.sns.web.backend.ng.tabs;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import mx.ift.sns.modelo.config.Parametro;
import mx.ift.sns.modelo.cpsi.SolicitudAsignacionCpsi;
import mx.ift.sns.modelo.cpsi.SolicitudCesionCpsi;
import mx.ift.sns.modelo.cpsi.SolicitudCpsiUit;
import mx.ift.sns.modelo.cpsi.SolicitudLiberacionCpsi;
import mx.ift.sns.modelo.cpsn.SolicitudAsignacionCpsn;
import mx.ift.sns.modelo.cpsn.SolicitudCesionCPSN;
import mx.ift.sns.modelo.cpsn.SolicitudLiberacionCpsn;
import mx.ift.sns.modelo.ng.RangoSerie;
import mx.ift.sns.modelo.ng.SolicitudAsignacion;
import mx.ift.sns.modelo.ng.SolicitudCesionNg;
import mx.ift.sns.modelo.ng.SolicitudLiberacionNg;
import mx.ift.sns.modelo.ng.SolicitudRedistribucionNg;
import mx.ift.sns.modelo.nng.RangoSerieNng;
import mx.ift.sns.modelo.nng.SolicitudAsignacionNng;
import mx.ift.sns.modelo.nng.SolicitudCesionNng;
import mx.ift.sns.modelo.nng.SolicitudLiberacionNng;
import mx.ift.sns.modelo.nng.SolicitudRedistribucionNng;
import mx.ift.sns.modelo.oficios.Oficio;
import mx.ift.sns.modelo.oficios.OficioBlob;
import mx.ift.sns.modelo.oficios.TipoDestinatario;
import mx.ift.sns.modelo.oficios.TipoRol;
import mx.ift.sns.modelo.series.EstadoRango;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.modelo.solicitud.Solicitud;
import mx.ift.sns.modelo.solicitud.TipoSolicitud;
import mx.ift.sns.negocio.exceptions.RegistroModificadoException;
import mx.ift.sns.negocio.oficios.IOficiosFacade;
import mx.ift.sns.negocio.oficios.ParametrosOficio;
import mx.ift.sns.utils.date.FechasUtils;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.components.TabWizard;
import mx.ift.sns.web.backend.components.Wizard;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase de respaldo para el Tab/Pestaña de 'Oficios'.
 */
public class GeneracionOficiosTab extends TabWizard {

    /** Serializacion. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(GeneracionOficiosTab.class);

    /** Identificador del componente de mensajes JSF. */
    private static final String MSG_ID = "MSG_GenerarOficio";

    /** Servicio de Numeración Geográfica. */
    private IOficiosFacade oficiosFacade;

    /** Solicitud con la información del oficio. */
    private Solicitud solicitud;

    /** Fecha de Oficio. */
    private Date fechaOficio;

    /** Prefijo del número de oficio. */
    private String prefijo;

    /** Prefijo por defecto del Oficio. */
    private String prefijoDefault = "";

    /** Número de oficio. */
    private String numOficio;

    /** Año de oficio. */
    private String anno;

    /** Fecha de Inicio de Utilización. */
    private Date fechaIniUtilizacion;

    /** Lista de tipos de destinatario. */
    private List<TipoDestinatario> listaDestinatarios;

    /** Tipo de Destinatario Seleccionado. */
    private TipoDestinatario destinatarioSeleccionado;

    /** Indica si el botón de generar oficio está disponible. */
    private boolean generarDisponible = false;

    /** Indica si el botón de descargar oficio está disponible. */
    private boolean descargaDisponible = false;

    /** Indica si el botón de cargar oficio está disponible. */
    private boolean cargaDisponible = false;

    /** Indica si el botón de actualizar oficio está disponible. */
    private boolean actualizacionDisponible = false;

    /** Fichero seleccionado para actualizar. */
    private UploadedFile ficheroSubido = null;

    /** Oficio generado. */
    private Oficio oficioGenerado = null;

    /** Comentarios Adicionales para los Documentos. */
    private String comentarios = "";

    /** Dias para aplicacion de la solicitud. */
    private int diasAplicacion = 20;

    /** Tamaño máximo de fichero para plantillas. */
    private String plantillaMaxSize = "";

    /** Tipo de Rol seleccionado para Solicitudes de Cesión. */
    private TipoRol tipoRolSeleccionado;

    /** Indica si el combo de TipoRol está habilitado. */
    private boolean tipoRolHabilitado = false;

    /** Lista de tipos de rol disponibles. */
    private List<TipoRol> listaTiposRol;

    /** Lista de tipos de oficio disponibles para el tipo de destinatario seleccionado. */
    private List<TipoDestinatario> listaOficiosDisponibles;

    /** Oficio seleccionado para generar. */
    private TipoDestinatario oficioSeleccionado;

    /** Ruta del fichero seleccionado para actualizar la plantilla. */
    private String pathFichero = "";

    /** Número de oficio del PST Solicitante. */
    private String numOficioPstSolicitante = "";

    /** Indica que el oficio generado tiene como destinatario el Proveedor Solicitante o el resto de Pst's. */
    private boolean oficioParaPsts = true;

    /** Indica que el oficio en curso es un trámite de Asignación. */
    private boolean oficioAsignacion = false;

    /** Indica que el oficio generado tiene como destinatario el Proveedor Solicitante. */
    private boolean oficioPstSolicitante = false;

    /** Contenedor de tipos de destinatario. */
    private HashMap<String, TipoDestinatario> tiposDest;

    /** Indica que es un trámite solicitado por IFT. */
    private boolean oficioIFT = false;

    /**
     * Constructor de la clase de respaldo para el tab/pestaña de 'Oficios'.
     * @param pWizard Referencia a la clase Wizard que instancia este Tab
     * @param pOficiosFacade Facade de métodos para generación de oficios.
     */
    public GeneracionOficiosTab(Wizard pWizard, IOficiosFacade pOficiosFacade) {
        // Asociamos el Wizard padre a la pestaña que reprsenta este Tab
        setWizard(pWizard);

        // Asociamos la solicitud que usaremos en todo el Wizard. Es posible que
        // la referencia del Wizzard padre cambie en función de los pasos previos,
        // por eso hay que actualizar la referencia al llegar a éste tab usando
        // el método actualizaCampos();
        solicitud = getWizard().getSolicitud();

        // Asociamos el Servicio
        oficiosFacade = pOficiosFacade;
    }

    @Override
    public void cargaValoresIniciales() {
        try {
            // Número de Oficio
            numOficio = "";
            prefijo = oficiosFacade.getParamByName(Parametro.PREFIJO);
            prefijoDefault = new String(prefijo);
            anno = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));

            // Precargamos los tipos de destinatario para evitar llamadas repetitivas a BBDD
            tiposDest = new HashMap<String, TipoDestinatario>(4);
            tiposDest.put(TipoDestinatario.PST_SOLICITANTE,
                    oficiosFacade.getTipoDestinatarioByCdg(TipoDestinatario.PST_SOLICITANTE));
            tiposDest.put(TipoDestinatario.RESTO_PST,
                    oficiosFacade.getTipoDestinatarioByCdg(TipoDestinatario.RESTO_PST));
            tiposDest.put(TipoDestinatario.ACTA_CIRCUNSTANCIADA,
                    oficiosFacade.getTipoDestinatarioByCdg(TipoDestinatario.ACTA_CIRCUNSTANCIADA));
            tiposDest.put(TipoDestinatario.CEDULA_NOTIFICACION,
                    oficiosFacade.getTipoDestinatarioByCdg(TipoDestinatario.CEDULA_NOTIFICACION));

            // Lista de Destinatarios
            listaDestinatarios = new ArrayList<TipoDestinatario>(2);
            listaDestinatarios.add(tiposDest.get(TipoDestinatario.PST_SOLICITANTE));
            listaDestinatarios.add(tiposDest.get(TipoDestinatario.RESTO_PST));

            // Lista de Tipos de Rol
            listaTiposRol = new ArrayList<TipoRol>(2);
            listaTiposRol.add(new TipoRol(TipoRol.TIPO_ROL_CEDENTE));
            listaTiposRol.add(new TipoRol(TipoRol.TIPO_ROL_CESIONARIO));

            // Lista de Oficios Disponibles.
            listaOficiosDisponibles = new ArrayList<TipoDestinatario>(3);

            // Tamaño máximo de fichero para plantillas.
            plantillaMaxSize = oficiosFacade.getParamByName(Parametro.FICH_OFICIO_SIZE);
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
            this.setSummaryError(Errores.getDescripcionError(Errores.ERROR_0004));
        }
    }

    @Override
    public void resetTab() {
        if (this.isIniciado()) {
            generarDisponible = false;
            descargaDisponible = false;
            cargaDisponible = false;
            actualizacionDisponible = false;
            numOficio = "";
            prefijo = new String(prefijoDefault);
            ficheroSubido = null;
            comentarios = "";
            tipoRolSeleccionado = null;
            tipoRolHabilitado = false;
            pathFichero = "";
            oficioGenerado = null;
            oficioSeleccionado = null;
            numOficioPstSolicitante = "";
            oficioParaPsts = true;
            oficioAsignacion = false;
            listaOficiosDisponibles.clear();
            destinatarioSeleccionado = null;
            oficioIFT = false;
            fechaOficio = null;
            diasAplicacion = 20;
        }
    }

    @Override
    public void actualizaCampos() {
        try {
            // Reseteamos todos los campos por si se avanza y retrodcede desde otras pestañas.
            this.resetTab();

            // La solicitud del Wizard ha cambiado de instancia desde que se generó en
            // el constructor. Es necesario actualizar la referencia en el tab.
            solicitud = getWizard().getSolicitud();

            // Cargamos el número de oficio del PST Solicitante si ya está generado.
            if (!StringUtils.isEmpty(solicitud.getNumOficioSolicitante())) {
                numOficioPstSolicitante = solicitud.getNumOficioSolicitante();
            }

            // Días de aplicación para trámite de Asignación NG y NNG
            oficioAsignacion = (solicitud.getTipoSolicitud().getCdg().equals(TipoSolicitud.ASIGNACION)
                    || solicitud.getTipoSolicitud().getCdg().equals(TipoSolicitud.ASIGNACION_NNG));

            // Trámite de Liberación solicitado por IFT.
            // Para los trámites solicitados por ITF solo se debe permitir generar oficio para el Resto de PST's.
            if (solicitud.getTipoSolicitud().getCdg().equals(TipoSolicitud.LIBERACION_NNG)) {
                oficioIFT = ((SolicitudLiberacionNng) solicitud).isLiberacionIft();
            } else if (solicitud.getTipoSolicitud().getCdg().equals(TipoSolicitud.LIBERACION_CPSN)) {
                oficioIFT = ((SolicitudLiberacionCpsn) solicitud).isLiberacionIft();
            } else if (solicitud.getTipoSolicitud().getCdg().equals(TipoSolicitud.LIBERACION_CPSI)) {
                oficioIFT = ((SolicitudLiberacionCpsi) solicitud).isLiberacionIft();
            } else {
                oficioIFT = false;
            }

            // Es posible que la solicitud se haya cancelado al eliminar cesiones, liberaciones, etc individuales
            // y luego se pulse en siguiente. En éstos casos la solicitud llega como cancelada por lo que hay
            // que inhabilitar el tab.
            this.setTabHabilitado(!solicitud.getEstadoSolicitud().getCodigo()
                    .equals(EstadoSolicitud.SOLICITUD_CANCELADA));

            // Actualizamos los días de aplicación y Fecha de Inicio de Utilización de
            // solicitudes de asignación NG y NNG.
            if (oficioAsignacion) {
                this.actualizarDiasAplicacion();
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    @Override
    public boolean isAvanzar() {
        this.setSummaryError("No es posible avanzar, se ha terminado el trámite");
        return false;
    }

    /** Método invocado al seleccionar una opción en el combo 'Dirigido a'. */
    public void seleccionDestinatario() {
        try {
            if (destinatarioSeleccionado != null) {
                listaOficiosDisponibles.clear();

                if (oficioIFT) {
                    // Para Trámites solicitados por ITF solo se permite generar oficios para el Resto de PST's
                    if (destinatarioSeleccionado.getCdg().equals(TipoDestinatario.RESTO_PST)) {
                        listaOficiosDisponibles.add(tiposDest.get(TipoDestinatario.RESTO_PST));
                    }
                    tipoRolHabilitado = false;
                } else {
                    // Las solicitudes de cesión tienen un tratamiento diferente.
                    boolean solicituCesion = (solicitud.getTipoSolicitud().getCdg()
                            .equals(TipoSolicitud.CESION_DERECHOS)
                            || solicitud.getTipoSolicitud().getCdg().equals(TipoSolicitud.CESION_DERECHOS_NNG)
                            || solicitud.getTipoSolicitud().getCdg().equals(TipoSolicitud.CESION_CPSI)
                            || solicitud.getTipoSolicitud().getCdg().equals(TipoSolicitud.CESION_CPSN));

                    // Las solicitudes UIT tienen un tratamiento diferente.
                    boolean solicitudUit = this.solicitud.getTipoSolicitud().getCdg()
                            .equals(TipoSolicitud.SOLICITUD_CPSI_UIT);

                    if (destinatarioSeleccionado.getCdg().equals(TipoDestinatario.PST_SOLICITANTE)) {
                        if (solicituCesion) {
                            // Diferenciamos por el tipo de rol seleccionado para solicitudes de cesión
                            if (tipoRolSeleccionado == null) {
                                listaOficiosDisponibles.add(tiposDest.get(TipoDestinatario.PST_SOLICITANTE));
                            }
                            listaOficiosDisponibles.add(tiposDest.get(TipoDestinatario.ACTA_CIRCUNSTANCIADA));
                            listaOficiosDisponibles.add(tiposDest.get(TipoDestinatario.CEDULA_NOTIFICACION));
                        } else if (solicitudUit) {
                            listaOficiosDisponibles.add(tiposDest.get(TipoDestinatario.PST_SOLICITANTE));
                        } else {
                            listaOficiosDisponibles.add(tiposDest.get(TipoDestinatario.PST_SOLICITANTE));
                            listaOficiosDisponibles.add(tiposDest.get(TipoDestinatario.ACTA_CIRCUNSTANCIADA));
                            listaOficiosDisponibles.add(tiposDest.get(TipoDestinatario.CEDULA_NOTIFICACION));
                        }
                    } else if (destinatarioSeleccionado.getCdg().equals(TipoDestinatario.RESTO_PST)) {
                        if (!solicitudUit) {
                            listaOficiosDisponibles.add(tiposDest.get(TipoDestinatario.RESTO_PST));
                        }
                    } else {
                        listaOficiosDisponibles.clear();
                    }

                    // Actualizamos el Combo de Tipo de Rol
                    tipoRolHabilitado = (destinatarioSeleccionado.getCdg().equals(TipoDestinatario.PST_SOLICITANTE)
                            && solicituCesion);
                }
            } else {
                // Bloqueamos las acciones de generación de oficio.
                listaOficiosDisponibles.clear();
                tipoRolHabilitado = false;
            }

            // Reseteamos siempre el oficio seleccionado para obligar a seleccionarlo de nuevo.
            oficioSeleccionado = null;
            this.seleccionOficio();

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Método invocado al seleccionar una opción en el combo 'Tipo Rol a'. */
    public void seleccionRol() {
        try {
            listaOficiosDisponibles.clear();

            if (tipoRolSeleccionado == null) {
                listaOficiosDisponibles.add(tiposDest.get(TipoDestinatario.PST_SOLICITANTE));
            }

            listaOficiosDisponibles.add(tiposDest.get(TipoDestinatario.ACTA_CIRCUNSTANCIADA));
            listaOficiosDisponibles.add(tiposDest.get(TipoDestinatario.CEDULA_NOTIFICACION));

            // Reseteamos siempre el oficio seleccionado para obligar a seleccionarlo de nuevo.
            oficioSeleccionado = null;
            this.seleccionOficio();

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Método invocado al seleccionar una opción en el combo 'Oficio a generar'. */
    public void seleccionOficio() {
        try {
            if (oficioSeleccionado != null) {
                // Tipo de Oficio Seleccionado
                oficioParaPsts = ((oficioSeleccionado.getCdg().equals(TipoDestinatario.PST_SOLICITANTE))
                        || oficioSeleccionado.getCdg().equals(TipoDestinatario.RESTO_PST));

                // Oficio específico para Proveedor Solicitante
                oficioPstSolicitante = (oficioSeleccionado.getCdg().equals(TipoDestinatario.PST_SOLICITANTE));

                // Cargamos el Oficio de BBDD
                oficioGenerado = oficiosFacade.getOficio(solicitud, oficioSeleccionado.getCdg());

                if (oficioGenerado != null) {
                    fechaOficio = oficioGenerado.getFechaOficio();
                    numOficio = procesarNumeroOficio(oficioGenerado.getNumOficio());
                } else {
                    fechaOficio = null;
                    if (oficioSeleccionado.getCdg().equals(TipoDestinatario.RESTO_PST)
                            && (!StringUtils.isEmpty(numOficioPstSolicitante))) {
                        // Para el Resto de PST's el número de oficio se rellena automáticamente.
                        int numOficioInt = Integer.valueOf(this.procesarNumeroOficio(numOficioPstSolicitante));
                        numOficio = String.valueOf(numOficioInt + 1);
                    } else {
                        numOficio = "";
                    }
                }

                // El oficio para el proveedor Solicitante siempre se crea primero ya que el Numero de Oficio
                // de proveedor solicitante es requerido y tiene que estar completado.
                boolean oficioPstGenerado = false;
                if (oficioIFT) {
                    // La excepción son los trámites solicitados por ITF, que no requieren Oficio de PST Solicitante.
                    oficioPstGenerado = true;
                } else {
                    if (oficioSeleccionado.getCdg().equals(TipoDestinatario.PST_SOLICITANTE)) {
                        // Si se va a crear el oficio del PST Solicitante habilitamos las opciones.
                        oficioPstGenerado = true;
                    } else {
                        oficioPstGenerado = !StringUtils.isEmpty(numOficioPstSolicitante);
                    }
                }

                // Actualizamos las acciones disponibles.
                descargaDisponible = (oficioGenerado != null) && oficioPstGenerado;
                actualizacionDisponible = (oficioGenerado != null) && oficioPstGenerado;

                // Botón de generar
                generarDisponible = oficioPstGenerado;

                if (!oficioPstGenerado) {
                    MensajesBean.addErrorMsg(MSG_ID,
                            "Es necesario crear primero el oficio para el Proveedor Solicitante.", "");
                }
            } else {
                generarDisponible = false;
                descargaDisponible = false;
                actualizacionDisponible = false;
                fechaOficio = null;
                numOficio = "";
                oficioGenerado = null;
                oficioParaPsts = false;
                oficioPstSolicitante = false;
            }

            // Forzamos que vuelva a elegir un fichero para actualizar
            ficheroSubido = null;

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Método invocado al pulsar sobre el botón 'Generar Oficio'. */
    public void generarOficio() {
        try {
            if (oficioSeleccionado != null) {
                if (LOGGER.isDebugEnabled()) {
                    StringBuffer sbTrace = new StringBuffer("Generación de Oficio.");
                    sbTrace.append(" Tipo Solicitud: ").append(solicitud.getTipoSolicitud().getDescripcion());
                    sbTrace.append(" Tipo Destinatario: ").append(oficioSeleccionado.getDescripcion());
                    LOGGER.debug(sbTrace.toString());
                }

                boolean oficioGeneradoOk = false;
                if (oficioSeleccionado.getCdg().equals(TipoDestinatario.PST_SOLICITANTE)) {
                    oficioGeneradoOk = this.generarOficioPsts();
                    if (oficioGeneradoOk) {
                        // Actualizamos el número de oficio del PST Solicitante para uso en el resto de oficios.
                        numOficioPstSolicitante = oficioGenerado.getNumOficio();
                    }
                } else if (oficioSeleccionado.getCdg().equals(TipoDestinatario.RESTO_PST)) {
                    oficioGeneradoOk = this.generarOficioPsts();
                } else if (oficioSeleccionado.getCdg().equals(TipoDestinatario.CEDULA_NOTIFICACION)) {
                    oficioGeneradoOk = this.generarOficioNoPsts();
                } else if (oficioSeleccionado.getCdg().equals(TipoDestinatario.ACTA_CIRCUNSTANCIADA)) {
                    oficioGeneradoOk = this.generarOficioNoPsts();
                }

                if (oficioGeneradoOk) {
                    // Al guardar la solicitud se persiste el oficio generado.
                    if (solicitud.getTipoSolicitud().getCdg().equals(TipoSolicitud.ASIGNACION)) {
                        if (oficioSeleccionado.getCdg().equals(TipoDestinatario.PST_SOLICITANTE)) {
                            for (RangoSerie rangoAsignado : solicitud.getRangos()) {
                                if (rangoAsignado.getEstadoRango().getCodigo().equals(EstadoRango.ASIGNADO)) {
                                    // Los cambios se registran al guardar la solicitud.
                                    rangoAsignado.setOficioAsignacion(solicitud.getNumOficioSolicitante());
                                }
                            }
                        }
                        this.solicitud = oficiosFacade.saveSolicitudAsignacion((SolicitudAsignacion) solicitud);
                    } else if (solicitud.getTipoSolicitud().getCdg().equals(TipoSolicitud.ASIGNACION_NNG)) {
                        if (oficioSeleccionado.getCdg().equals(TipoDestinatario.PST_SOLICITANTE)) {
                            for (RangoSerieNng rangoAsignado : solicitud.getRangosNng()) {
                                if (rangoAsignado.getEstatus().getCodigo().equals(EstadoRango.ASIGNADO)) {
                                    // Los cambios se registran al guardar la solicitud.
                                    rangoAsignado.setOficioAsignacion(solicitud.getNumOficioSolicitante());
                                }
                            }
                        }
                        this.solicitud = oficiosFacade.saveSolicitudAsignacion((SolicitudAsignacionNng) solicitud);
                    } else if (solicitud.getTipoSolicitud().getCdg().equals(TipoSolicitud.CESION_DERECHOS)) {
                        this.solicitud = oficiosFacade.saveSolicitudCesion((SolicitudCesionNg) solicitud);

                    } else if (solicitud.getTipoSolicitud().getCdg().equals(TipoSolicitud.CESION_DERECHOS_NNG)) {
                        this.solicitud = oficiosFacade.saveSolicitudCesion((SolicitudCesionNng) solicitud);

                    } else if (solicitud.getTipoSolicitud().getCdg().equals(TipoSolicitud.LIBERACION)) {
                        this.solicitud = oficiosFacade.saveSolicitudLiberacion((SolicitudLiberacionNg) solicitud);

                    } else if (solicitud.getTipoSolicitud().getCdg().equals(TipoSolicitud.LIBERACION_NNG)) {
                        this.solicitud = oficiosFacade.saveSolicitudLiberacion((SolicitudLiberacionNng) solicitud);

                    } else if (solicitud.getTipoSolicitud().getCdg().equals(TipoSolicitud.REDISTRIBUCION)) {
                        this.solicitud = oficiosFacade
                                .saveSolicitudRedistribucion((SolicitudRedistribucionNg) solicitud);

                    } else if (solicitud.getTipoSolicitud().getCdg().equals(TipoSolicitud.REDISTRIBUCION_NNG)) {
                        this.solicitud = oficiosFacade
                                .saveSolicitudRedistribucion((SolicitudRedistribucionNng) solicitud);

                    } else if (solicitud.getTipoSolicitud().getCdg().equals(TipoSolicitud.ASIGNACION_CPSN)) {
                        this.solicitud = oficiosFacade.saveSolicitudAsignacion((SolicitudAsignacionCpsn) solicitud);

                    } else if (solicitud.getTipoSolicitud().getCdg().equals(TipoSolicitud.CESION_CPSN)) {
                        this.solicitud = oficiosFacade.saveSolicitudCesion((SolicitudCesionCPSN) solicitud);

                    } else if (solicitud.getTipoSolicitud().getCdg().equals(TipoSolicitud.LIBERACION_CPSN)) {
                        this.solicitud = oficiosFacade.saveSolicitudLiberacion((SolicitudLiberacionCpsn) solicitud);

                    } else if (solicitud.getTipoSolicitud().getCdg().equals(TipoSolicitud.ASIGNACION_CPSI)) {
                        this.solicitud = oficiosFacade.saveSolicitudAsignacion((SolicitudAsignacionCpsi) solicitud);

                    } else if (solicitud.getTipoSolicitud().getCdg().equals(TipoSolicitud.CESION_CPSI)) {
                        this.solicitud = oficiosFacade.saveSolicitudCesion((SolicitudCesionCpsi) solicitud);

                    } else if (solicitud.getTipoSolicitud().getCdg().equals(TipoSolicitud.LIBERACION_CPSI)) {
                        this.solicitud = oficiosFacade.saveSolicitudLiberacion((SolicitudLiberacionCpsi) solicitud);

                    } else if (solicitud.getTipoSolicitud().getCdg().equals(TipoSolicitud.SOLICITUD_CPSI_UIT)) {
                        this.solicitud = oficiosFacade.saveSolicitudCpsiUit((SolicitudCpsiUit) solicitud);
                    }

                    // Actualizamos la solicitud del Wizard
                    this.getWizard().setSolicitud(solicitud);

                    // Actualizamos los botones del formulario y la instancia del oficio.
                    this.seleccionOficio();
                }
            }
        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0015);
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Método invocado al pulsar sobre el botón 'Descargar Oficio'.
     * @return StreamedContent Fichero a Descargar
     */
    public StreamedContent getDescargarOficio() {
        try {
            if (oficioSeleccionado != null) {
                if (LOGGER.isDebugEnabled()) {
                    StringBuffer sbTrace = new StringBuffer("Descarga de Oficio.");
                    sbTrace.append(" Tipo Solicitud: ").append(solicitud.getTipoSolicitud().getDescripcion())
                            .append(", ");
                    sbTrace.append(" Tipo Destinatario: ").append(oficioSeleccionado.getDescripcion()).append(", ");
                    sbTrace.append(" Solicitud: ").append(solicitud.getId());
                    LOGGER.debug(sbTrace.toString());
                }

                // Recuperamos el Blob del oficio.
                OficioBlob oficioBlob = oficiosFacade.getOficioBlob(oficioGenerado.getIdOficioBlob());
                InputStream stream = new ByteArrayInputStream(oficioBlob.getDocumento());

                // Nombre del Documento Word
                StringBuffer docName = new StringBuffer();
                if (oficioSeleccionado.getCdg().equals(TipoDestinatario.PST_SOLICITANTE)) {
                    docName.append("Oficio_").append(oficioGenerado.getNumOficio()).append(".docx");
                } else if (oficioSeleccionado.getCdg().equals(TipoDestinatario.RESTO_PST)) {
                    docName.append("Oficio_").append(oficioGenerado.getNumOficio()).append(".docx");
                } else if (oficioSeleccionado.getCdg().equals(TipoDestinatario.CEDULA_NOTIFICACION)) {
                    docName.append("Cédula_").append(oficioGenerado.getNumOficio()).append(".docx");
                } else if (oficioSeleccionado.getCdg().equals(TipoDestinatario.ACTA_CIRCUNSTANCIADA)) {
                    docName.append("Acta_").append(oficioGenerado.getNumOficio()).append(".docx");
                }

                // Word Mime Type
                String wordMimeType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
                StreamedContent downFile = new DefaultStreamedContent(stream, wordMimeType, docName.toString());

                return downFile;
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
        return null;
    }

    /**
     * Fichero seleccionado para actualizar el oficio.
     * @param pEvent FileUploadEvent
     */
    public void cargarFichero(FileUploadEvent pEvent) {
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Fichero seleccionado: " + pEvent.getFile().getFileName());
            }

            // El chequeo se hace en el View mendiante métodos de PrimeFaces
            ficheroSubido = (pEvent.getFile());

            // Informaciónd el fichero cargado.
            StringBuilder sbFile = new StringBuilder();
            sbFile.append(ficheroSubido.getFileName()).append(" (").append(ficheroSubido.getSize());
            sbFile.append(" bytes)");
            pathFichero = sbFile.toString();

            // Fichero cargado, se puede actualizar
            MensajesBean.addInfoMsg(MSG_ID, "Fichero cargado correctamente", "");
            actualizacionDisponible = true;

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
            ficheroSubido = null;
            pathFichero = "";
        }
    }

    /** Método invocado al pulsar sobre el botón 'Guardar'. */
    public void actualizarOficio() {
        try {
            if (ficheroSubido != null) {
                if (LOGGER.isDebugEnabled()) {
                    StringBuffer sbTrace = new StringBuffer("Actualizar Oficio.");
                    sbTrace.append(" Tipo Solicitud: ").append(solicitud.getTipoSolicitud().getDescripcion());
                    sbTrace.append(" Tipo Destinatario: ").append(oficioSeleccionado.getDescripcion());
                    sbTrace.append(" Solicitud: ").append(solicitud.getId());
                    LOGGER.debug(sbTrace.toString());
                }

                // Actualizamos el Documento
                OficioBlob oficioBlob = oficiosFacade.getOficioBlob(oficioGenerado.getIdOficioBlob());
                oficioBlob.setDocumento(ficheroSubido.getContents());
                oficiosFacade.saveOficioBlob(oficioBlob);

                // Actualizamos el Oficio
                oficioGenerado.setFechaActualizacion(new Date());
                oficioGenerado = oficiosFacade.saveOficio(oficioGenerado);
                MensajesBean.addInfoMsg(MSG_ID, "Oficio " + oficioGenerado.getNumOficio() + " actualizado", "");

                // Actualizamos los componentes del Interfaz.
                cargaDisponible = false;
                ficheroSubido = null;
                pathFichero = "";
                generarDisponible = true;
                descargaDisponible = true;
                actualizacionDisponible = true;
            } else {
                MensajesBean.addErrorMsg(MSG_ID, "Es necesario seleccionar un documento para actualizar", "");
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Dado un Número de Oficio Compuesto lo parsea e inicializa las variables del tab.
     * @param pNumeroOficio Número de Oficio
     * @return Devuelve el número de Oficio sin prefijo ni año
     */
    private String procesarNumeroOficio(String pNumeroOficio) {
        try {
            // Número de Oficio
            String[] numOfiParts = pNumeroOficio.split("/");
            anno = numOfiParts[numOfiParts.length - 1];
            String num = numOfiParts[numOfiParts.length - 2];

            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < (numOfiParts.length - 2); i++) {
                sb.append(numOfiParts[i]).append("/");
            }
            sb.replace(sb.length() - 1, sb.length(), "");

            prefijo = sb.toString();
            return num;
        } catch (Exception ex) {
            return "";
        }
    }

    /**
     * Genera el Oficio para el Proveedor Solicitante y el Resto de Proveedores.
     * @return True si se ha generado/actualizado el oficio correctamente.
     * @throws Exception en caso de error.
     */
    public boolean generarOficioPsts() throws Exception {
        // Resultado del proceso.
        boolean generarOk = false;
        ParametrosOficio params = new ParametrosOficio();

        Oficio oficioPrevio = oficiosFacade.getOficio(this.solicitud, oficioSeleccionado.getCdg());
        if (oficioPrevio == null) {
            // Número de Oficio Completo
            StringBuffer sbNumOfi = new StringBuffer();
            sbNumOfi.append(prefijo).append("/").append(numOficio).append("/").append(anno);

            // Comprobamos que no exista el número de oficio para otros trámites
            if (oficiosFacade.existeNumeroOficio(sbNumOfi.toString())) {
                MensajesBean.addErrorMsg(MSG_ID, "El número de oficio " + sbNumOfi.toString()
                        + " ya existe para otro trámite.", "");
            } else {
                params.setNumOficio(sbNumOfi.toString());
                params.setSolicitud(solicitud);
                params.setTipoDestinatario(oficioSeleccionado);
                params.setFechaOficio(fechaOficio);
                params.setTextoAdicional(comentarios);
                params.setTipoRol(tipoRolSeleccionado);

                // Creamos el Oficio
                oficioGenerado = oficiosFacade.crearOficio(params);
                MensajesBean.addInfoMsg(MSG_ID,
                        oficioSeleccionado.getDescripcion() + " generado: " + oficioGenerado.getNumOficio(), "");

                // Agregamos el oficio a la solicitud. El oficio y el blob se guardan al guardar la solicitud.
                solicitud.addOficio(oficioGenerado);
                if (oficioSeleccionado.getCdg().equals(TipoDestinatario.PST_SOLICITANTE)) {
                    solicitud.setNumOficioSolicitante(oficioGenerado.getNumOficio());
                }
                generarOk = true;
            }
        } else {
            params.setSolicitud(solicitud);
            params.setOficio(oficioPrevio);
            params.setNumOficio(oficioPrevio.getNumOficio());
            params.setTextoAdicional(comentarios);
            params.setTipoRol(tipoRolSeleccionado);
            params.setTipoDestinatario(oficioPrevio.getTipoDestinatario());
            params.setFechaOficio(oficioPrevio.getFechaOficio());

            // Actualizamos el oficio
            oficioGenerado = oficiosFacade.actualizarOficio(params);
            MensajesBean.addInfoMsg(MSG_ID,
                    oficioSeleccionado.getDescripcion() + " actualizado: " + oficioGenerado.getNumOficio(), "");
            generarOk = true;
        }

        return generarOk;
    }

    /**
     * Genera el Oficio de Cédula de Notificación.
     * @return True si se ha generado/actualizado el oficio correctamente.
     * @throws Exception en caso de error.
     */
    public boolean generarOficioNoPsts() throws Exception {
        ParametrosOficio params = new ParametrosOficio();

        // El número de oficio reflejado en la cédula es el del Oficio del PST Solicitante.
        // Solo se permite generar la cédula si existe Oficio para el PST Soliciante.

        Oficio oficioPrevio = oficiosFacade.getOficio(this.getSolicitud(), oficioSeleccionado.getCdg());
        if (oficioPrevio != null) {
            params.setSolicitud(solicitud);
            params.setOficio(oficioPrevio);
            params.setNumOficio(oficioPrevio.getNumOficio());
            params.setTextoAdicional(comentarios);
            params.setTipoRol(tipoRolSeleccionado);
            params.setTipoDestinatario(oficioPrevio.getTipoDestinatario());
            params.setFechaOficio(oficioPrevio.getFechaOficio());

            // Actualizamos el Oficio
            oficioGenerado = oficiosFacade.actualizarOficio(params);
            MensajesBean.addInfoMsg(MSG_ID,
                    oficioSeleccionado.getDescripcion() + " actualizado: " + oficioGenerado.getNumOficio(), "");

        } else {
            params.setNumOficio(numOficioPstSolicitante);
            params.setSolicitud(solicitud);
            params.setTipoDestinatario(oficioSeleccionado);
            params.setTextoAdicional(comentarios);
            params.setTipoRol(tipoRolSeleccionado);

            // El Oficio del PST Solicitante debe existir o no se hubiese podido seleccionar la generación de cédula o
            // acta.
            Oficio oficioPstSol = oficiosFacade.getOficio(this.getSolicitud(), TipoDestinatario.PST_SOLICITANTE);
            params.setFechaOficio(oficioPstSol.getFechaOficio());

            // Creamos el Oficio. Utilizamos el Número de Oficio del PST Solicitante.
            oficioGenerado = oficiosFacade.crearOficio(params);

            // Agregamos el oficio a la solicitud
            solicitud.addOficio(oficioGenerado);
            MensajesBean.addInfoMsg(MSG_ID,
                    oficioSeleccionado.getDescripcion() + " generado: " + oficioGenerado.getNumOficio(), "");
        }

        // Solo se permite generar el Acta y Cédula si existe oficio del PST Solicitante. Si no, no se llega
        // nunca a éste método.
        return true;
    }

    /** Método invocado al pulsar sobre el botón 'Actualizar'. */
    public void habilitarActualizacion() {
        generarDisponible = false;
        descargaDisponible = false;
        actualizacionDisponible = false;
        cargaDisponible = true;
    }

    /** Recalcula la fecha de inicio de utilizacion. */
    public void diasAplicacionChange() {
        // Los días de aplicación solo afectan a solicitudes de Asignación NG y NNG.
        if (oficioAsignacion) {
            if (fechaOficio != null) {
                solicitud.setFechaIniUtilizacion(FechasUtils.calculaFecha(fechaOficio, diasAplicacion, 0, 0));
            } else {
                MensajesBean.addErrorMsg(MSG_ID, "Es necesario selecionar una fecha de oficio", "");
            }
        }
    }

    /**
     * Actualiza las variables de días de aplicación y fecha de inicio de utilización de la solicitud y el formulario.
     */
    private void actualizarDiasAplicacion() {

        // El campo de Fecha de Inicio de Utilización insertado por el usuario en la pestaña de generales
        // se pierde y se sustituye por el calculado entre la fecha de oficio + días de aplicación.
        this.solicitud.setFechaIniUtilizacion(null);
        this.setDiasAplicacion(20);

        if (!StringUtils.isEmpty(solicitud.getNumOficioSolicitante())) {
            // Si existe número de oficio entonces ya existe fecha de inicio de utilización
            Oficio oficioAsigPstSol = oficiosFacade.getOficio(this.solicitud, TipoDestinatario.PST_SOLICITANTE);
            if (oficioAsigPstSol != null) {
                solicitud.setFechaIniUtilizacion(
                        FechasUtils.calculaFecha(oficioAsigPstSol.getFechaOficio(), diasAplicacion, 0, 0));
            }
        }
    }

    /* GETTES & SETTERS */

    /**
     * Indica si la descarga de la plantilla esta disponible o no.
     * @return True si está habilitado
     */
    public boolean isDescargaDisponible() {
        return descargaDisponible;
    }

    /**
     * Indica si la descarga de la plantilla esta disponible o no.
     * @param descargaDisponible True si está habilitado
     */
    public void setDescargaDisponible(boolean descargaDisponible) {
        this.descargaDisponible = descargaDisponible;
    }

    /**
     * Indica si es el botón de actualización de oficio esta habilitado o no.
     * @return True si está habilitado
     */
    public boolean isActualizacionDisponible() {
        return actualizacionDisponible;
    }

    /**
     * Indica si es el botón de actualización de oficio esta habilitado o no.
     * @param actualizacionDisponible True si está habilitado
     */
    public void setActualizacionDisponible(boolean actualizacionDisponible) {
        this.actualizacionDisponible = actualizacionDisponible;
    }

    /**
     * Indica si la plantilla esta lista para ser cargada.
     * @return True si está habilitado
     */
    public boolean isCargaDisponible() {
        return cargaDisponible;
    }

    /**
     * Indica si la plantilla esta lista para ser cargada.
     * @param cargaDisponible True si está habilitado
     */
    public void setCargaDisponible(boolean cargaDisponible) {
        this.cargaDisponible = cargaDisponible;
    }

    /**
     * Indica si el botón 'Generar' esta habilitado o no.
     * @return True si está habilitado
     */
    public boolean isGenerarDisponible() {
        return generarDisponible;
    }

    /**
     * Indica si el botón 'Generar' esta habilitado o no.
     * @param generarDisponible True si está habilitado
     */
    public void setGenerarDisponible(boolean generarDisponible) {
        this.generarDisponible = generarDisponible;
    }

    /**
     * Fecha de Oficio.
     * @return Date
     */
    public Date getFechaOficio() {
        return fechaOficio;
    }

    /**
     * Fecha de Oficio.
     * @param fechaOficio Date
     */
    public void setFechaOficio(Date fechaOficio) {
        this.fechaOficio = fechaOficio;
    }

    /**
     * Prefijo del Oficio.
     * @return String
     */
    public String getPrefijo() {
        return prefijo;
    }

    /**
     * Prefijo del Oficio.
     * @param prefijo String
     */
    public void setPrefijo(String prefijo) {
        this.prefijo = prefijo;
    }

    /**
     * Número de Oficio Proveedor Solicitante.
     * @return String
     */
    public String getNumOficio() {
        return numOficio;
    }

    /**
     * Número de Oficio Proveedor Solicitante.
     * @param numOficio String
     */
    public void setNumOficio(String numOficio) {
        this.numOficio = numOficio;
    }

    /**
     * Año de Oficio.
     * @return String
     */
    public String getAnno() {
        return anno;
    }

    /**
     * Año de Oficio.
     * @param anno String
     */
    public void setAnno(String anno) {
        this.anno = anno;
    }

    /**
     * Fecha de Inicio de Utilización.
     * @return Date
     */
    public Date getFechaIniUtilizacion() {
        return fechaIniUtilizacion;
    }

    /**
     * Lista de destinatarios.
     * @return List
     */
    public List<TipoDestinatario> getListaDestinatarios() {
        return listaDestinatarios;
    }

    /**
     * Destinatario seleccionado para la generación de documentos.
     * @return TipoDestinatario
     */
    public TipoDestinatario getDestinatarioSeleccionado() {
        return destinatarioSeleccionado;
    }

    /**
     * Destinatario seleccionado para la generación de documentos.
     * @param destinatarioSeleccionado TipoDestinatario
     */
    public void setDestinatarioSeleccionado(TipoDestinatario destinatarioSeleccionado) {
        this.destinatarioSeleccionado = destinatarioSeleccionado;
    }

    /**
     * Solicitud.
     * @return Solicitud
     */
    public Solicitud getSolicitud() {
        return solicitud;
    }

    /**
     * Solicitud.
     * @param solicitud Solicitud
     */
    public void setSolicitud(Solicitud solicitud) {
        this.solicitud = solicitud;
    }

    /**
     * Comentarios Adicionales para los Documentos.
     * @return String
     */
    public String getComentarios() {
        return comentarios;
    }

    /**
     * Comentarios Adicionales para los Documentos.
     * @param comentarios String
     */
    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    /**
     * @return the diasAplicacion
     */
    public int getDiasAplicacion() {
        return diasAplicacion;
    }

    /**
     * @param diasAplicacion the diasAplicacion to set
     */
    public void setDiasAplicacion(int diasAplicacion) {
        this.diasAplicacion = diasAplicacion;
    }

    /**
     * Tamaño máximo de fichero para plantillas.
     * @return String
     */
    public String getPlantillaMaxSize() {
        return plantillaMaxSize;
    }

    /**
     * Tipo de Rol seleccionado para Solicitudes de Cesión.
     * @return TipoRol
     */
    public TipoRol getTipoRolSeleccionado() {
        return tipoRolSeleccionado;
    }

    /**
     * Tipo de Rol seleccionado para Solicitudes de Cesión.
     * @param tipoRolSeleccionado TipoRol
     */
    public void setTipoRolSeleccionado(TipoRol tipoRolSeleccionado) {
        this.tipoRolSeleccionado = tipoRolSeleccionado;
    }

    /**
     * Indica si el combo de TipoRol está habilitado.
     * @return boolean
     */
    public boolean isTipoRolHabilitado() {
        return tipoRolHabilitado;
    }

    /**
     * Indica si el combo de TipoRol está habilitado.
     * @param tipoRolHabilitado boolean
     */
    public void setTipoRolHabilitado(boolean tipoRolHabilitado) {
        this.tipoRolHabilitado = tipoRolHabilitado;
    }

    /**
     * Lista de tipos de rol disponibles.
     * @return List
     */
    public List<TipoRol> getListaTiposRol() {
        return listaTiposRol;
    }

    /**
     * Lista de tipos de oficio disponibles para el tipo de destinatario seleccionado.
     * @return List<TipoDestinatario>
     */
    public List<TipoDestinatario> getListaOficiosDisponibles() {
        return listaOficiosDisponibles;
    }

    /**
     * Oficio seleccionado para generar.
     * @return TipoDestinatario
     */
    public TipoDestinatario getOficioSeleccionado() {
        return oficioSeleccionado;
    }

    /**
     * Oficio seleccionado para generar.
     * @param oficioSeleccionado TipoDestinatario
     */
    public void setOficioSeleccionado(TipoDestinatario oficioSeleccionado) {
        this.oficioSeleccionado = oficioSeleccionado;
    }

    /**
     * Ruta del fichero seleccionado para actualizar la plantilla.
     * @return String
     */
    public String getPathFichero() {
        return pathFichero;
    }

    /**
     * Ruta del fichero seleccionado para actualizar la plantilla.
     * @param pathFichero the pathFichero to set
     */
    public void setPathFichero(String pathFichero) {
        this.pathFichero = pathFichero;
    }

    /**
     * Indica que el oficio generado tiene como destinatario el Proveedor Solicitante o el resto de Pst's.
     * @return boolean
     */
    public boolean isOficioParaPsts() {
        return oficioParaPsts;
    }

    /**
     * Indica que el oficio en curso es un trámite de Asignación.
     * @return boolean
     */
    public boolean isOficioAsignacion() {
        return oficioAsignacion;
    }

    /**
     * Indica que el oficio generado tiene como destinatario el Proveedor Solicitante.
     * @return boolean
     */
    public boolean isOficioPstSolicitante() {
        return oficioPstSolicitante;
    }

}
