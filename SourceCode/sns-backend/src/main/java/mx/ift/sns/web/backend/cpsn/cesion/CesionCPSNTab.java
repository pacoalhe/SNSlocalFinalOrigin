package mx.ift.sns.web.backend.cpsn.cesion;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mx.ift.sns.modelo.cpsn.CesionSolicitadaCPSN;
import mx.ift.sns.modelo.cpsn.CodigoCPSN;
import mx.ift.sns.modelo.cpsn.EstatusCPSN;
import mx.ift.sns.modelo.cpsn.SolicitudCesionCPSN;
import mx.ift.sns.modelo.cpsn.TipoBloqueCPSN;
import mx.ift.sns.modelo.filtros.FiltroBusquedaCodigosCPSN;
import mx.ift.sns.modelo.solicitud.EstadoCesionSolicitada;
import mx.ift.sns.modelo.solicitud.EstadoLiberacionSolicitada;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.negocio.PeticionCancelacion;
import mx.ift.sns.negocio.cpsn.ICodigoCPSNFacade;
import mx.ift.sns.negocio.exceptions.RegistroModificadoException;
import mx.ift.sns.utils.date.FechasUtils;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.components.TabWizard;
import mx.ift.sns.web.backend.components.Wizard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Clase de soporte para la pestaña de 'Cesión CPSN' en los Wizard. */
public class CesionCPSNTab extends TabWizard {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CesionCPSNTab.class);

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador del componente de mensajes JSF. */
    private static final String MSG_ID = "MSG_CesionCPSN";

    /** Servicio de CPSN. */
    private ICodigoCPSNFacade cpsnFacade;

    /** Información de la petición de Solicitud. */
    private SolicitudCesionCPSN solicitud;

    /** Cesion CPSN Solicitada seleccionada de la tabla de Cesiones. */
    private CesionSolicitadaCPSN cesSolSeleccionada;

    /** Tipo de bloque de la búsqueda. */
    private TipoBloqueCPSN tipoBloqueSeleccionado;

    /** Listado de tipos de bloque CPSN. */
    private List<TipoBloqueCPSN> listadoTiposBloque;

    /** Indica si se se ha de habilitar el botón de 'Guardar'. */
    private boolean salvarHabilitado = false;

    /** Fecha de implementación de la cesión. */
    private Date fechaImplementacion;

    /** Filtro de búsqueda de codigos CPS Nacionales. */
    private FiltroBusquedaCodigosCPSN filtros;

    /** Listado de códigos nacionales. */
    private List<CodigoCPSN> listadoCodigosCPSN;

    /** Listado de Códigos CPSN seleccionados. */
    private List<CodigoCPSN> codigosCPSNSeleccionados;

    /** Lista de Cesiones CPSN solicitadas por el Cedente para el Cesionario. */
    private List<CesionSolicitadaCPSN> listaCesionesCPSN = null;

    /** Indica si la implementación es inmediata o programada. */
    private boolean implementacionInmediata = true;

    /** Tipo de Fecha de Implementación (Calendario / Días / Meses). */
    private String tipoPeriodoImplementacion;

    /** Días / meses / fecha indicada para la implementación programada. */
    private String periodoImplementacion = "0";

    /** Indica si la fecha de implementación se selecciona usando el calendario. */
    private boolean fechaImplCalendario = true;

    /**
     * Constructor de la clase de respaldo para el tab/pestaña de 'Cesión CPSN'.
     * @param pWizard Referencia a la clase Wizard que instancia este Tab
     * @param cpsnFacade Servicio de Códigos de Señalización nacional
     */
    public CesionCPSNTab(Wizard pWizard, ICodigoCPSNFacade cpsnFacade) {
        // Asociamos el Wizard padre a la pestaña que reprsenta este Tab
        setWizard(pWizard);

        // Asociamos la solicitud que usaremos en todo el Wizard
        solicitud = (SolicitudCesionCPSN) getWizard().getSolicitud();

        // Asociamos el Servicio
        this.cpsnFacade = cpsnFacade;
    }

    @Override
    public void cargaValoresIniciales() {
        try {
            // Inicializaciones
            tipoBloqueSeleccionado = null;
            listadoTiposBloque = cpsnFacade.findAllTiposBloqueCPSN();
            filtros = new FiltroBusquedaCodigosCPSN();
            listaCesionesCPSN = new ArrayList<>();
            listadoCodigosCPSN = new ArrayList<>();
            codigosCPSNSeleccionados = new ArrayList<>();

            // Fecha de Implementación y cuarentena
            fechaImplementacion = new Date();
            tipoPeriodoImplementacion = "2"; // Calendario
            periodoImplementacion = "0";
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
            this.setSummaryError(Errores.getDescripcionError(Errores.ERROR_0004));
        }
    }

    @Override
    public void resetTab() {
        if (this.isIniciado()) {
            this.limpiarBusqueda();
            this.listaCesionesCPSN.clear();
            this.codigosCPSNSeleccionados.clear();
            this.fechaImplementacion = new Date();
            this.implementacionInmediata = true;
            this.salvarHabilitado = false;
            this.solicitud = null;
            this.tipoBloqueSeleccionado = null;
            this.periodoImplementacion = "0";
            this.tipoPeriodoImplementacion = "2"; // Calendario
            this.fechaImplCalendario = true;
            this.cesSolSeleccionada = null;
        }
    }

    /** Método invocado al pulsar el botón 'buscar' sobre el buscador de Series. */
    public void realizarBusqueda() {
        try {
            crearFiltros();
            listadoCodigosCPSN = cpsnFacade.findCodigosCPSN(filtros);
            codigosCPSNSeleccionados.clear();
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Método invocado al pulsar el botón 'limpiar' sobre el buscador de CPSN. */
    public void limpiarBusqueda() {
        tipoBloqueSeleccionado = null;
        codigosCPSNSeleccionados.clear();
        listadoCodigosCPSN.clear();
    }

    /** Creación de los filtros de búsqueda. */
    private void crearFiltros() {
        // Resetamos los filtros
        filtros.clear();

        // Filtro del proveedor
        filtros.setProveedor(solicitud.getProveedorSolicitante());
        filtros.setEstatusCPSN(new EstatusCPSN(EstatusCPSN.ASIGNADO));

        // Filtro de tipo de bloque
        if (tipoBloqueSeleccionado != null) {
            filtros.setTipoBloqueCPSN(tipoBloqueSeleccionado);
        }
    }

    @Override
    public boolean isAvanzar() {
        boolean resultado = true;
        if (this.isTabHabilitado()) {
            // Continuamos siempre y cuando haya cesiones por procesar
            if (listaCesionesCPSN.isEmpty()) {
                MensajesBean.addErrorMsg(MSG_ID, MensajesBean.getTextoResource("cesion.errores.almenosUna"), "");
                resultado = false;
            } else {
                resultado = (this.guardarCambios() && this.aplicarCesiones());
            }
        } else {
            resultado = true;
        }

        if (!resultado) {
            // Al no permitirse avanzar se muestra en el Wizard el mensaje indicado por el TabWizard
            this.setSummaryError(MensajesBean.getTextoResource("error.avanzar"));
        }
        return resultado;
    }

    /**
     * Aplica las cesiones seleccionadas por el usuario.
     * @return True si se han aplicado correctamente las cesiones.
     */
    private boolean aplicarCesiones() {
        try {
            if (validarCodigosACeder()) {
                // Aplicamos la cesiones solicitadas.
                SolicitudCesionCPSN solCes = cpsnFacade.applyCesionesSolicitadas(solicitud);

                // Asociamos la nueva instancia de solicitud al Wizard
                this.getWizard().setSolicitud(solCes);

                // Continuamos a la siguiente pestaña
                return true;
            }
        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0015);
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }

        return false;
    }

    /**
     * Comprueba que los códigos seleccionados para ceder existen y tienen estatus disponible.
     * @return True si se pueden aplicar las cesiones.
     * @throws Exception En caso de error.
     */
    private boolean validarCodigosACeder() throws Exception {

        // Antes de aplicar las liberaciones comprobamos que todos los CPSN sigan
        // asignados al Proveedor y con un estatus válido.
        List<String> avisosEstatus = new ArrayList<String>();

        int cpsnModificados = 0;
        for (CesionSolicitadaCPSN cesSol : solicitud.getCesionesSolicitadasCPSN()) {
            if (cesSol.getEstatus().getCodigo().equals(EstadoLiberacionSolicitada.PENDIENTE)) {

                CodigoCPSN cpsn = cpsnFacade.getCodigoCpsn(
                        cesSol.getTipoBloqueCpsn().getId(),
                        cesSol.getIdCpsn(),
                        cesSol.getSolicitudCesionCPSN().getProveedorSolicitante());

                // Si esta libre o asignado a un proveedor diferente no será prosible encontrarlo.
                if (cpsn != null) {
                    // Comprobamos si sigue en estatus asignado.
                    if (!cpsn.getEstatusCPSN().getId().equals(EstatusCPSN.ASIGNADO)) {
                        // Si no está asignado puede estar planificado para una cesión posterior
                        if (!cpsn.getEstatusCPSN().getId().equals(EstatusCPSN.PLANIFICADO)) {
                            StringBuffer sbAviso = new StringBuffer();
                            sbAviso.append("Tipo bloque: ").append(cpsn.getTipoBloqueCPSN().getDescripcion());
                            sbAviso.append(", Binario: ").append(cpsn.getBinario());
                            if (cpsn.getTipoBloqueCPSN().getId().equals(TipoBloqueCPSN.INDIVIDUAL)) {
                                sbAviso.append(", Dec. Total: ").append(cpsn.getDecimalTotal());
                            } else {
                                sbAviso.append(", Dec. Red: ").append(cpsn.getDecimalRed());
                                sbAviso.append(", Dec. Desde: ").append(cpsn.getDecimalDesde());
                                sbAviso.append(", Dec. Hasta: ").append(cpsn.getDecimalHasta());
                            }
                            sbAviso.append(", Estatus: ").append(cpsn.getEstatusCPSN().getDescripcion());
                            sbAviso.append("<br>");
                            avisosEstatus.add(sbAviso.toString());
                        }
                    }
                } else {
                    ++cpsnModificados;
                }
            }
        }

        // Avisos de CPSNs que ya no existen
        if (cpsnModificados != 0) {
            StringBuffer sbAviso = new StringBuffer();
            sbAviso.append("Se han encontrado ").append(cpsnModificados);
            sbAviso.append(" que han sido eliminados o modificados. Revise las cesiones solicitadas.<br>");
            MensajesBean.addErrorMsg(MSG_ID, sbAviso.toString(), "");
        }

        // Avisos de CPSN que ya no tienen estatus 'Asignado'
        if (!avisosEstatus.isEmpty()) {
            StringBuffer sbAviso = new StringBuffer();
            sbAviso.append("Los siguientes CPSN han cambiado de estatus:<br>");
            for (String aviso : avisosEstatus) {
                sbAviso.append(aviso);
            }
            MensajesBean.addErrorMsg(MSG_ID, sbAviso.toString(), "");
        }

        // Si no hay avisos, continuamos con la Cesión
        return (avisosEstatus.isEmpty() && (cpsnModificados == 0));
    }

    /**
     * Guarda los cambios realizados hasta el momento en el trámite.
     * @return 'True' si se ha podido guardar correctamente.
     */
    private boolean guardarCambios() {
        // Lista de Cesiones Solicitadas a eliminar permanentemente
        ArrayList<CesionSolicitadaCPSN> cesSolEliminar = new ArrayList<CesionSolicitadaCPSN>();

        try {
            // Eliminamos las cesiones que se han deseleccionado
            for (CesionSolicitadaCPSN cesSol : solicitud.getCesionesSolicitadasCPSN()) {
                // Ignoramos las cesiones que ya se hayan ejecutado
                if (cesSol.getEstatus().getCodigo().equals(EstadoCesionSolicitada.PENDIENTE)) {
                    if ((!arrayContains(listaCesionesCPSN, cesSol.getIdCpsn().intValue()))) {
                        // No podemos eliminar una cesion solicitada de la lista mientras estamos
                        // recorriendo la misma lista (ConcurrentModificationException).
                        // Por lo tanto la añadimos a otra lista para eliminarla al salir del bucle.
                        cesSolEliminar.add(cesSol);
                    }
                }
            }

            // Eliminamos de la solicitud las CesionesSolicitadas que se han eliminado de la tabla de Cesiones.
            for (CesionSolicitadaCPSN cesSol : cesSolEliminar) {
                // Al tener marcadas las cesionesSolicitadas como @PrivateOwned en JPA se eliminan automáticamente
                // cuando se guardan los cambios en la solicitud ya que indica que nadie más las utiliza.
                solicitud.removeCesionSolicitada(cesSol);
            }

            // Agregamos las cesiones que se han añadido
            for (CesionSolicitadaCPSN cesSol : listaCesionesCPSN) {
                if (!arrayContains(solicitud.getCesionesSolicitadasCPSN(), cesSol.getIdCpsn().intValue())) {
                    solicitud.addCesionSolicitada(cesSol);
                }
            }

            // Guardamos los cambios y generamos los nuevos registros
            solicitud = cpsnFacade.saveSolicitudCesion(solicitud);

            // Actualizamos la solicitud para todos los tabs
            getWizard().setSolicitud(solicitud);

            // Una vez persistidas las CesionesSolicitadas rehacemos la tabla de cesiones para que todas
            // tengan el id ya generado
            listaCesionesCPSN.clear();
            listaCesionesCPSN.addAll(solicitud.getCesionesSolicitadasCPSN());

            return true;

        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0015);
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
        return false;
    }

    /**
     * Método invocado al pulsar sobre el botón 'Guardar'.
     */
    public void guardarCambiosManual() {
        // Guardamos los cambios realizados en la solicitud.
        if (this.guardarCambios()) {
            // Mensaje de información al usuario.
            StringBuffer sBuf = new StringBuffer();
            sBuf.append(MensajesBean.getTextoResource("manual.generales.exito.guardar")).append(" ");
            sBuf.append(solicitud.getId());
            MensajesBean.addInfoMsg(MSG_ID, sBuf.toString(), "");
        }
    }

    @Override
    public void actualizaCampos() {
        // Reseteamos el Tab por si se va y viene desde los botones de navegación del wizard.
        this.resetTab();

        // La solicitud del Wizard ha cambiado de instancia desde que se generó en
        // el constructor. Es necesario actualizar la referecnia en el tab.
        solicitud = (SolicitudCesionCPSN) getWizard().getSolicitud();

        // Actualizamos las solicitudes pendientes
        listaCesionesCPSN.clear();
        if (solicitud.getCesionesSolicitadasCPSN() != null) {
            listaCesionesCPSN.addAll(solicitud.getCesionesSolicitadasCPSN());
        }

        // Habilitamos el botón de guardar siempre y cuando haya cesiones solicitadas.
        salvarHabilitado = (!listaCesionesCPSN.isEmpty());
    }

    /** Método invocado al pulsar sobre el botón 'Eliminar' en la tabla de Cesiones seleccionadas. */
    public void eliminarCesion() {
        try {
            PeticionCancelacion checkCancelacion = cpsnFacade.cancelCesion(cesSolSeleccionada, true);
            if (checkCancelacion.isCancelacionPosible()) {
                listaCesionesCPSN.remove(cesSolSeleccionada);
                this.guardarCambios();

                if (solicitud.getEstadoSolicitud().getCodigo().equals(EstadoSolicitud.SOLICITUD_TERMINADA)) {
                    // RN082: Si todas las cesiones están canceladas el trámite pasa a estar
                    // cancelado también.
                    boolean cesionesCanceladas = true;
                    for (CesionSolicitadaCPSN cesSol : solicitud.getCesionesSolicitadasCPSN()) {
                        cesionesCanceladas = cesionesCanceladas
                                && (cesSol.getEstatus().getCodigo().equals(EstadoCesionSolicitada.CANCELADO));
                    }

                    // Cambiamos el estado de la solicitud
                    if (cesionesCanceladas) {
                        EstadoSolicitud statusCancelada = new EstadoSolicitud();
                        statusCancelada.setCodigo(EstadoSolicitud.SOLICITUD_CANCELADA);
                        solicitud.setEstadoSolicitud(statusCancelada);

                        MensajesBean.addInfoMsg(MSG_ID, "Se han cancelado todas las cesiones."
                                + " El trámite pasa a estado 'Cancelado'", "");

                        // Guardamos los cambios
                        solicitud = cpsnFacade.saveSolicitudCesion(solicitud);
                        getWizard().setSolicitud(solicitud);
                    }
                }
            } else {
                MensajesBean.addErrorMsg(MSG_ID, checkCancelacion.getMensajeError(), "");
            }

            // Acabamos de eliminar una cesión solicitada de la tabla de resúmen. Habilitamos
            // el botón de guardar para salvar los cambios.
            salvarHabilitado = true;

        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0015);
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Método invocado al pulsar sobre el botón de implementación inmediata. */
    public void inicializarFechaImplementacion() {
        if (implementacionInmediata) {
            fechaImplementacion = FechasUtils.getFechaHoy();
            tipoPeriodoImplementacion = "2"; // Calendario
            periodoImplementacion = "0";
            fechaImplCalendario = false;
        } else {
            // La fecha de implementación programada siempre es a partir de mañana.
            fechaImplementacion = FechasUtils.calculaFecha(1, 0, 0);
            tipoPeriodoImplementacion = "2"; // Calendario
            periodoImplementacion = "1";
            fechaImplCalendario = true;
        }
    }

    /** Método invocado al pulsar sobre fecha de implementación. */
    public void actualizarFechaImplementacion() {
        // Las validaciones client-side nos aseguran que llegue un valor entero positivo
        int intPeriodo = Integer.parseInt(this.periodoImplementacion);
        if (intPeriodo == 0) {
            implementacionInmediata = false;
            inicializarFechaImplementacion();
        } else if (intPeriodo > 0) {
            if (tipoPeriodoImplementacion.equals("0")) {
                // Implementación indicada en días
                fechaImplementacion = FechasUtils.calculaFecha(intPeriodo, 0, 0);
                fechaImplCalendario = false;
            } else if (tipoPeriodoImplementacion.equals("1")) {
                // Implementación indicada en meses
                fechaImplementacion = FechasUtils.calculaFecha(0, intPeriodo, 0);
                fechaImplCalendario = false;
            } else {
                // Implementación indicada en el calendario
                fechaImplCalendario = true;
            }
        } else {
            MensajesBean.addErrorMsg(MSG_ID, "El periodo de implementación indicado no es válido.", "");
            fechaImplementacion = FechasUtils.calculaFecha(1, 0, 0);
            tipoPeriodoImplementacion = "2"; // Calendario
            periodoImplementacion = "1";
            fechaImplCalendario = true;
        }
    }

    /** Valida la fecha de Implementación. */
    public void validarFechaImplementacion() {
        try {
            // La fecha de implementación programada siempre es a partir de mañana.
            Date fManana = FechasUtils.calculaFecha(1, 0, 0);

            if (fechaImplementacion == null) {
                fechaImplementacion = fManana;
            } else if (fechaImplementacion.before(fManana)) {
                MensajesBean.addErrorMsg(MSG_ID,
                        "La fecha de implementación programada no puede ser igual o inferior al día actual.", "");
                fechaImplementacion = fManana;
            }

            // Actualizamos los días comprendidos entre hoy y la fecha de implementación.
            int intPeriodo = (int) FechasUtils.getDiasEntre(FechasUtils.getFechaHoy(), fechaImplementacion);
            periodoImplementacion = String.valueOf(intPeriodo);

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Método invocado al pulsar el botón 'Agregar'. */
    public void agregarCesion() {
        try {
            if (validarCodigosSeleccionados()) {
                for (CodigoCPSN cpsn : codigosCPSNSeleccionados) {
                    listaCesionesCPSN.add(getCesionSolicitadaFromCPSN(cpsn));
                }

                this.inicializarFechaImplementacion();
            }

            // Habilitamos el botón de guardar siempre y cuando haya cesiones solicitadas.
            salvarHabilitado = (!listaCesionesCPSN.isEmpty());

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Realiza las validaciones sobre los códigos seleccionados antes de agregar las liberaciones solicitadas.
     * @return True si se han pasado todas las validaciones.
     * @throws Exception en caso de error
     */
    private boolean validarCodigosSeleccionados() throws Exception {

        // Comprueba si se han seleccionado cpsns para ceder.
        if (codigosCPSNSeleccionados.isEmpty()) {
            MensajesBean.addErrorMsg(MSG_ID, "Es necesario seleccionar al menos un CPSN", "");
            return false;
        }

        List<CodigoCPSN> cpsnsEnCesion = new ArrayList<CodigoCPSN>();
        List<CodigoCPSN> cpsnsEstatusNoValido = new ArrayList<CodigoCPSN>();
        for (CodigoCPSN cpsn : codigosCPSNSeleccionados) {
            if (!cpsn.getEstatusCPSN().getId().equals(EstatusCPSN.ASIGNADO)) {
                // Comprueba que el estatus del CPSN sea correcto para cesión
                cpsnsEstatusNoValido.add(cpsn);
            } else {
                // Comprueba si se han seleccionado cpsns ya elegidos para ceder
                if (arrayContains(listaCesionesCPSN, cpsn.getId().intValue())) {
                    cpsnsEnCesion.add(cpsn);
                }
            }
        }

        if (!cpsnsEnCesion.isEmpty()) {
            StringBuffer sb = new StringBuffer();
            sb.append("No es posible agregar los CPSN seleccionados.<br>");
            sb.append("Lo siguientes CPSN ya existen para cesión:<br>");
            for (CodigoCPSN cpsn : cpsnsEnCesion) {
                sb.append("Tipo bloque: ").append(cpsn.getTipoBloqueCPSN().getDescripcion());
                sb.append(", Binario: ").append(cpsn.getBinario());
                if (cpsn.getTipoBloqueCPSN().getId().equals(TipoBloqueCPSN.INDIVIDUAL)) {
                    sb.append(", Dec. Total: ").append(cpsn.getDecimalTotal()).append("<br>");
                } else {
                    sb.append(", Dec. Red: ").append(cpsn.getDecimalRed());
                    sb.append(", Dec. Desde: ").append(cpsn.getDecimalDesde());
                    sb.append(", Dec. Hasta: ").append(cpsn.getDecimalHasta()).append("<br>");
                }
            }
            MensajesBean.addErrorMsg(MSG_ID, sb.toString(), "");
            return false;
        }

        if (!cpsnsEstatusNoValido.isEmpty()) {
            StringBuffer sb = new StringBuffer();
            if (!cpsnsEnCesion.isEmpty()) {
                sb.append("<br>");
            } else {
                sb.append("No es posible agregar los CPSN seleccionados.<br>");
            }
            sb.append("Lo siguientes CPSN tienen un estatus no permitido para cesión:<br>");
            for (CodigoCPSN cpsn : cpsnsEstatusNoValido) {
                sb.append("Tipo bloque: ").append(cpsn.getTipoBloqueCPSN().getDescripcion());
                if (cpsn.getTipoBloqueCPSN().getId().equals(TipoBloqueCPSN.INDIVIDUAL)) {
                    sb.append(", Dec. Total: ").append(cpsn.getDecimalTotal());
                } else {
                    sb.append(", Binario: ").append(cpsn.getBinario());
                    sb.append(", Dec. Red: ").append(cpsn.getDecimalRed());
                    sb.append(", Dec. Desde: ").append(cpsn.getDecimalDesde());
                    sb.append(", Dec. Hasta: ").append(cpsn.getDecimalHasta());
                }
                sb.append(", Estatus: ").append(cpsn.getEstatusCPSN().getDescripcion()).append("<br>");
            }
            MensajesBean.addErrorMsg(MSG_ID, sb.toString(), "");
            return false;
        }

        return true;
    }

    /**
     * Indica si una lista contiene una CesionSolicitadaCPSN sin usar los métos Equals de Object.
     * @param list Lista de objetos CesionSolicitadaCPSN
     * @param pCpsnId Identificador de código CPSN.
     * @return True si la CesionSolicitadaCPSN esta en la lista
     * @throws Exception en caso de error
     */
    private boolean arrayContains(List<CesionSolicitadaCPSN> list, int pCpsnId) throws Exception {

        // Cuando se guardan los cambios las CesionesSolicitadas tienen otra instancia diferente dada
        // por JPA y, por lo tanto, no se pueden comparar con las almacenadas en la lista interna. Es necesario
        // comparar por los campos clave para saber si nos estamos refiriendo a la misma.

        for (CesionSolicitadaCPSN cesSol : list) {
            if (cesSol.getIdCpsn().intValue() == pCpsnId) {
                return true;
            }
        }
        return false;
    }

    /**
     * Crea un objeto CesionSolicitadaCPSN en base a la información de un CPSN.
     * @param pCpsn CPSN con la información de la cesión.
     * @return CesionSolicitadaCPSN
     * @throws Exception en caso de error.
     */
    private CesionSolicitadaCPSN getCesionSolicitadaFromCPSN(CodigoCPSN pCpsn) throws Exception {
        CesionSolicitadaCPSN cscpsn = new CesionSolicitadaCPSN();
        cscpsn.setSolicitudCesionCPSN(solicitud);
        cscpsn.setFechaImplementacion(FechasUtils.parseFecha(fechaImplementacion));
        cscpsn.setIdCpsn(pCpsn.getId());
        cscpsn.setBinario(pCpsn.getBinario());
        cscpsn.setTipoBloqueCpsn(pCpsn.getTipoBloqueCPSN());
        cscpsn.setDecimalTotal(pCpsn.getDecimalTotal());
        cscpsn.setDecimalDesde(pCpsn.getDecimalDesde());
        cscpsn.setDecimalHasta(pCpsn.getDecimalHasta());
        cscpsn.setDecimalRed(pCpsn.getDecimalRed());

        StringBuilder sbImplementacion = new StringBuilder();
        sbImplementacion.append(periodoImplementacion).append(" ");
        if (tipoPeriodoImplementacion.equals("0") || tipoPeriodoImplementacion.equals("2")) {
            sbImplementacion.append("Día/s");
        } else {
            sbImplementacion.append("Mes/s");
        }
        cscpsn.setPeriodoImplementacion(sbImplementacion.toString());

        EstadoCesionSolicitada ecs = new EstadoCesionSolicitada();
        ecs.setCodigo(EstadoCesionSolicitada.PENDIENTE);
        ecs.setDescripcion(EstadoCesionSolicitada.TXT_PENDIENTE);
        cscpsn.setEstatus(ecs);

        return cscpsn;
    }

    /**
     * Información de la petición de Solicitud.
     * @return SolicitudCesionCPSN
     */
    public SolicitudCesionCPSN getSolicitud() {
        return solicitud;
    }

    /**
     * Cesion CPSN Solicitada seleccionada de la tabla de Cesiones.
     * @return CesionSolicitadaCPSN
     */
    public CesionSolicitadaCPSN getCesSolSeleccionada() {
        return cesSolSeleccionada;
    }

    /**
     * Cesion CPSN Solicitada seleccionada de la tabla de Cesiones.
     * @param cesSolSeleccionada CesionSolicitadaCPSN
     */
    public void setCesSolSeleccionada(CesionSolicitadaCPSN cesSolSeleccionada) {
        this.cesSolSeleccionada = cesSolSeleccionada;
    }

    /**
     * Tipo de bloque de la búsqueda.
     * @return TipoBloqueCPSN
     */
    public TipoBloqueCPSN getTipoBloqueSeleccionado() {
        return tipoBloqueSeleccionado;
    }

    /**
     * Tipo de bloque de la búsqueda.
     * @param tipoBloqueSeleccionado TipoBloqueCPSN
     */
    public void setTipoBloqueSeleccionado(TipoBloqueCPSN tipoBloqueSeleccionado) {
        this.tipoBloqueSeleccionado = tipoBloqueSeleccionado;
    }

    /**
     * Listado de tipos de bloque CPSN.
     * @return List<TipoBloqueCPSN>
     */
    public List<TipoBloqueCPSN> getListadoTiposBloque() {
        return listadoTiposBloque;
    }

    /**
     * Indica si se se ha de habilitar el botón de 'Guardar'.
     * @return boolean
     */
    public boolean isSalvarHabilitado() {
        return salvarHabilitado;
    }

    /**
     * Indica si se se ha de habilitar el botón de 'Guardar'.
     * @param salvarHabilitado boolean
     */
    public void setSalvarHabilitado(boolean salvarHabilitado) {
        this.salvarHabilitado = salvarHabilitado;
    }

    /**
     * Indica si se se ha de habilitar el botón de 'Guardar'.
     * @return Date
     */
    public Date getFechaImplementacion() {
        return fechaImplementacion;
    }

    /**
     * Indica si se se ha de habilitar el botón de 'Guardar'.
     * @param fechaImplementacion Date
     */
    public void setFechaImplementacion(Date fechaImplementacion) {
        this.fechaImplementacion = fechaImplementacion;
    }

    /**
     * Filtro de búsqueda de codigos CPS Nacionales.
     * @return FiltroBusquedaCodigosCPSN
     */
    public FiltroBusquedaCodigosCPSN getFiltros() {
        return filtros;
    }

    /**
     * Filtro de búsqueda de codigos CPS Nacionales.
     * @param filtros FiltroBusquedaCodigosCPSN
     */
    public void setFiltros(FiltroBusquedaCodigosCPSN filtros) {
        this.filtros = filtros;
    }

    /**
     * Listado de códigos nacionales.
     * @return List<CodigoCPSN>
     */
    public List<CodigoCPSN> getListadoCodigosCPSN() {
        return listadoCodigosCPSN;
    }

    /**
     * Listado de códigos nacionales.
     * @param listadoCodigosCPSN List<CodigoCPSN>
     */
    public void setListadoCodigosCPSN(List<CodigoCPSN> listadoCodigosCPSN) {
        this.listadoCodigosCPSN = listadoCodigosCPSN;
    }

    /**
     * Listado de Códigos CPSN seleccionados.
     * @return List<CodigoCPSN>
     */
    public List<CodigoCPSN> getCodigosCPSNSeleccionados() {
        return codigosCPSNSeleccionados;
    }

    /**
     * Listado de Códigos CPSN seleccionados.
     * @param codigosCPSNSeleccionados List<CodigoCPSN>
     */
    public void setCodigosCPSNSeleccionados(List<CodigoCPSN> codigosCPSNSeleccionados) {
        this.codigosCPSNSeleccionados = codigosCPSNSeleccionados;
    }

    /**
     * Lista de Cesiones CPSN solicitadas por el Cedente para el Cesionario.
     * @return List<CesionSolicitadaCPSN>
     */
    public List<CesionSolicitadaCPSN> getListaCesionesCPSN() {
        return listaCesionesCPSN;
    }

    /**
     * Lista de Cesiones CPSN solicitadas por el Cedente para el Cesionario.
     * @param listaCesionesCPSN List<CesionSolicitadaCPSN>
     */
    public void setListaCesionesCPSN(List<CesionSolicitadaCPSN> listaCesionesCPSN) {
        this.listaCesionesCPSN = listaCesionesCPSN;
    }

    /**
     * Indica si la implementación es inmediata o programada.
     * @return boolean
     */
    public boolean isImplementacionInmediata() {
        return implementacionInmediata;
    }

    /**
     * Indica si la implementación es inmediata o programada.
     * @param implementacionInmediata boolean
     */
    public void setImplementacionInmediata(boolean implementacionInmediata) {
        this.implementacionInmediata = implementacionInmediata;
    }

    /**
     * Tipo de Fecha de Implementación (Calendario / Días / Meses).
     * @return String
     */
    public String getTipoPeriodoImplementacion() {
        return tipoPeriodoImplementacion;
    }

    /**
     * Tipo de Fecha de Implementación (Calendario / Días / Meses).
     * @param tipoPeriodoImplementacion String
     */
    public void setTipoPeriodoImplementacion(String tipoPeriodoImplementacion) {
        this.tipoPeriodoImplementacion = tipoPeriodoImplementacion;
    }

    /**
     * Días / meses / fecha indicada para la implementación programada.
     * @return String
     */
    public String getPeriodoImplementacion() {
        return periodoImplementacion;
    }

    /**
     * Días / meses / fecha indicada para la implementación programada.
     * @param periodoImplementacion String
     */
    public void setPeriodoImplementacion(String periodoImplementacion) {
        this.periodoImplementacion = periodoImplementacion;
    }

    /**
     * Indica si la fecha de implementación se selecciona usando el calendario.
     * @return boolean
     */
    public boolean isFechaImplCalendario() {
        return fechaImplCalendario;
    }

    /**
     * Indica si la fecha de implementación se selecciona usando el calendario.
     * @param fechaImplCalendario boolean
     */
    public void setFechaImplCalendario(boolean fechaImplCalendario) {
        this.fechaImplCalendario = fechaImplCalendario;
    }

}
