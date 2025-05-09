package mx.ift.sns.web.backend.cpsi.cesion;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mx.ift.sns.modelo.cpsi.CesionSolicitadaCpsi;
import mx.ift.sns.modelo.cpsi.CodigoCPSI;
import mx.ift.sns.modelo.cpsi.EstatusCPSI;
import mx.ift.sns.modelo.cpsi.SolicitudCesionCpsi;
import mx.ift.sns.modelo.filtros.FiltroBusquedaCodigosCPSI;
import mx.ift.sns.modelo.solicitud.EstadoCesionSolicitada;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.negocio.PeticionCancelacion;
import mx.ift.sns.negocio.cpsi.ICodigoCPSIFacade;
import mx.ift.sns.negocio.exceptions.RegistroModificadoException;
import mx.ift.sns.utils.date.FechasUtils;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.components.TabWizard;
import mx.ift.sns.web.backend.components.Wizard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Clase de soporte para la pestaña de 'Cesión' en los Wizard. */
public class CesionCpsiTab extends TabWizard {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CesionCpsiTab.class);

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador del componente de mensajes JSF. */
    private static final String MSG_ID = "MSG_CesionCPSI";

    /** Facade de Servicios de CPSI. */
    private ICodigoCPSIFacade cpsiFacade;

    /** Información de la petición de Solicitud. */
    private SolicitudCesionCpsi solicitud;

    /** Cesion CPSI Solicitada seleccionada de la tabla de Cesiones. */
    private CesionSolicitadaCpsi cesSolSeleccionada;

    /** Indica si se se ha de habilitar el botón de 'Guardar'. */
    private boolean salvarHabilitado = false;

    /** Fecha de implementación de la cesión. */
    private Date fechaImplementacion;

    /** Filtro de búsqueda de codigos CPS Nacionales. */
    private FiltroBusquedaCodigosCPSI filtros;

    /** Listado de códigos internacionales. */
    private List<CodigoCPSI> listadoCodigos;

    /** Listado de Códigos CPSI seleccionados. */
    private List<CodigoCPSI> codigosSeleccionados;

    /** Lista de Cesiones CPSI solicitadas por el Cedente para el Cesionario. */
    private List<CesionSolicitadaCpsi> listaCesiones = null;

    /** Indica si la implementación es inmediata o programada. */
    private boolean implementacionInmediata = true;

    // /** Indica si estamos actualizando los campos al editar la solicitud. */
    // private boolean actualizandoCampos = false;

    /** Tipo de Fecha de Implementación (Calendario / Días / Meses). */
    private String tipoPeriodoImplementacion;

    /** Días / meses / fecha indicada para la implementación programada. */
    private String periodoImplementacion = "0";

    /** Indica si la fecha de implementación se selecciona usando el calendario. */
    private boolean fechaImplCalendario = true;

    /**
     * Constructor de la clase de respaldo para el tab/pestaña de 'Cesión'.
     * @param pWizard Referencia a la clase Wizard que instancia este Tab
     * @param pCodigoCpsiFacade Facade de Servicios de Códigos de Señalización Internacional.
     */
    public CesionCpsiTab(Wizard pWizard, ICodigoCPSIFacade pCodigoCpsiFacade) {
        // Asociamos el Wizard padre a la pestaña que reprsenta este Tab
        setWizard(pWizard);

        // Asociamos la solicitud que usaremos en todo el Wizard
        solicitud = (SolicitudCesionCpsi) getWizard().getSolicitud();

        // Asociamos el Servicio
        this.cpsiFacade = pCodigoCpsiFacade;
    }

    @Override
    public void cargaValoresIniciales() {
        try {
            // Inicializaciones
            filtros = new FiltroBusquedaCodigosCPSI();
            listaCesiones = new ArrayList<CesionSolicitadaCpsi>();
            codigosSeleccionados = new ArrayList<CodigoCPSI>();

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
            this.listaCesiones.clear();
            this.codigosSeleccionados.clear();
            this.fechaImplementacion = new Date();
            this.implementacionInmediata = true;
            this.salvarHabilitado = false;
            this.solicitud = null;
            this.periodoImplementacion = "0";
            this.tipoPeriodoImplementacion = "2"; // Calendario
            this.fechaImplCalendario = true;
            this.cesSolSeleccionada = null;
        }
    }

    /** Método invocado al pulsar el botón 'buscar' sobre el buscador de Series. */
    public void realizarBusqueda() {
        try {
            // Resetamos los filtros
            filtros.clear();

            // Filtro del proveedor
            filtros.setProveedor(solicitud.getProveedorSolicitante());
            filtros.setEstatusCPSI(new EstatusCPSI(EstatusCPSI.ASIGNADO));

            listadoCodigos = cpsiFacade.findAllCodigosCPSI(filtros);
            codigosSeleccionados.clear();
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    @Override
    public boolean isAvanzar() {
        boolean resultado = true;
        if (this.isTabHabilitado()) {
            // Continuamos siempre y cuando haya cesiones por procesar
            if (listaCesiones.isEmpty()) {
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
                SolicitudCesionCpsi solCes = cpsiFacade.applyCesionesSolicitadas(solicitud);

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

        // Antes de aplicar las cesiones comprobamos que todos los CPSI sigan
        // asignados al Proveedor y con un estatus válido.
        List<String> avisosEstatus = new ArrayList<String>();

        int cpsiModificados = 0;
        for (CesionSolicitadaCpsi cesSol : solicitud.getCesionesSolicitadas()) {
            if (cesSol.getEstatus().getCodigo().equals(EstadoCesionSolicitada.PENDIENTE)) {

                CodigoCPSI cpsi = cpsiFacade.getCodigoCpsi(
                        cesSol.getIdCpsi(),
                        cesSol.getSolicitudCesion().getProveedorSolicitante());

                // Si esta libre o asignado a un proveedor diferente no será prosible encontrarlo.
                if (cpsi != null) {
                    // Comprobamos si sigue en estatus asignado.
                    if (!cpsi.getEstatus().getId().equals(EstatusCPSI.ASIGNADO)) {
                        // Si no está asignado puede estar planificado para una cesión posterior
                        if (!cpsi.getEstatus().getId().equals(EstatusCPSI.PLANIFICADO)) {
                            StringBuffer sbAviso = new StringBuffer();
                            sbAviso.append("Binario: ").append(cpsi.getBinario());
                            sbAviso.append(", Decimal: ").append(cpsi.getDecimalTotal());
                            sbAviso.append(", Formato Dec.: ").append(cpsi.getFormatoDecimal());
                            sbAviso.append(", Estatus: ").append(cpsi.getEstatus().getDescripcion()).append("<br>");
                            avisosEstatus.add(sbAviso.toString());
                        }
                    }
                } else {
                    ++cpsiModificados;
                }
            }
        }

        // Avisos de CPSIs que ya no existen
        if (cpsiModificados != 0) {
            StringBuffer sbAviso = new StringBuffer();
            sbAviso.append("Se han encontrado ").append(cpsiModificados);
            sbAviso.append(" que han sido eliminados o modificados. Revise las cesiones solicitadas.<br>");
            MensajesBean.addErrorMsg(MSG_ID, sbAviso.toString(), "");
        }

        // Avisos de CPSI que ya no tienen estatus 'Asignado'
        if (!avisosEstatus.isEmpty()) {
            StringBuffer sbAviso = new StringBuffer();
            sbAviso.append("Los siguientes CPSI han cambiado de estatus:<br>");
            for (String aviso : avisosEstatus) {
                sbAviso.append(aviso);
            }
            MensajesBean.addErrorMsg(MSG_ID, sbAviso.toString(), "");
        }

        // Si no hay avisos, continuamos con la cesión
        return (avisosEstatus.isEmpty() && (cpsiModificados == 0));
    }

    /**
     * Guarda los cambios realizados hasta el momento en el trámite.
     * @return 'True' si se ha podido guardar correctamente.
     */
    private boolean guardarCambios() {
        // Lista de Cesiones Solicitadas a eliminar permanentemente
        ArrayList<CesionSolicitadaCpsi> cesSolEliminar = new ArrayList<CesionSolicitadaCpsi>();

        try {
            // Eliminamos las cesiones que se han deseleccionado
            for (CesionSolicitadaCpsi cesSol : solicitud.getCesionesSolicitadas()) {
                // Ignoramos las cesiones que ya se hayan ejecutado
                if (cesSol.getEstatus().getCodigo().equals(EstadoCesionSolicitada.PENDIENTE)) {
                    if ((!arrayContains(listaCesiones, cesSol.getIdCpsi().intValue()))) {
                        // No podemos eliminar una cesion solicitada de la lista mientras estamos
                        // recorriendo la misma lista (ConcurrentModificationException).
                        // Por lo tanto la añadimos a otra lista para eliminarla al salir del bucle.
                        cesSolEliminar.add(cesSol);
                    }
                }
            }

            // Eliminamos de la solicitud las CesionesSolicitadas que se han eliminado de la tabla de Cesiones.
            for (CesionSolicitadaCpsi cesSol : cesSolEliminar) {
                // Al tener marcadas las cesionesSolicitadas como @PrivateOwned en JPA se eliminan automáticamente
                // cuando se guardan los cambios en la solicitud ya que indica que nadie más las utiliza.
                solicitud.removeCesionSolicitada(cesSol);
            }

            // Agregamos las cesiones que se han añadido
            for (CesionSolicitadaCpsi cesSol : listaCesiones) {
                if (!arrayContains(solicitud.getCesionesSolicitadas(), cesSol.getIdCpsi().intValue())) {
                    solicitud.addCesionSolicitada(cesSol);
                }
            }

            // Guardamos los cambios y generamos los nuevos registros
            solicitud = cpsiFacade.saveSolicitudCesion(solicitud);

            // Actualizamos la solicitud para todos los tabs
            getWizard().setSolicitud(solicitud);

            // Una vez persistidas las CesionesSolicitadas rehacemos la tabla de cesiones para que todas
            // tengan el id ya generado
            listaCesiones.clear();
            listaCesiones.addAll(solicitud.getCesionesSolicitadas());

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
        solicitud = (SolicitudCesionCpsi) getWizard().getSolicitud();

        // Actualizamos las solicitudes pendientes
        listaCesiones.clear();
        if (solicitud.getCesionesSolicitadas() != null) {
            listaCesiones.addAll(solicitud.getCesionesSolicitadas());
        }

        // Habilitamos el botón de guardar siempre y cuando haya cesiones solicitadas.
        salvarHabilitado = (!listaCesiones.isEmpty());
        // Realizamos la búsqueda.
        this.realizarBusqueda();
    }

    /** Método invocado al pulsar sobre el botón 'Eliminar' en la tabla de Cesiones seleccionadas. */
    public void eliminarCesion() {
        try {
            PeticionCancelacion checkCancelacion = cpsiFacade.cancelCesion(cesSolSeleccionada, true);
            if (checkCancelacion.isCancelacionPosible()) {
                listaCesiones.remove(cesSolSeleccionada);
                this.guardarCambios();

                if (solicitud.getEstadoSolicitud().getCodigo().equals(EstadoSolicitud.SOLICITUD_TERMINADA)) {
                    // RN082: Si todas las cesiones están canceladas el trámite pasa a estar
                    // cancelado también.
                    boolean cesionesCanceladas = true;
                    for (CesionSolicitadaCpsi cesSol : solicitud.getCesionesSolicitadas()) {
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
                        solicitud = cpsiFacade.saveSolicitudCesion(solicitud);
                        getWizard().setSolicitud(solicitud);
                    }
                }
            } else {
                MensajesBean.addErrorMsg(MSG_ID, checkCancelacion.getMensajeError(), "");
            }

            // Acabamos de eliminar una cesión solicitada de la tabla de resúmen. Habilitamos
            // el botón de guardar para salvar los cambios.
            salvarHabilitado = true;
            // Realizamos la búsqueda para que se muestren los datos actualizados
            this.realizarBusqueda();
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
                for (CodigoCPSI cpsi : codigosSeleccionados) {
                    listaCesiones.add(getCesionSolicitadaFromCPSI(cpsi));
                }

                this.inicializarFechaImplementacion();
            }

            // Habilitamos el botón de guardar siempre y cuando haya cesiones solicitadas.
            salvarHabilitado = (!listaCesiones.isEmpty());

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

        // Comprueba si se han seleccionado cpsis para ceder.
        if (codigosSeleccionados.isEmpty()) {
            MensajesBean.addErrorMsg(MSG_ID, "Es necesario seleccionar al menos un CPSI", "");
            return false;
        }

        List<CodigoCPSI> cpsisEnCesion = new ArrayList<CodigoCPSI>();
        List<CodigoCPSI> cpsisEstatusNoValido = new ArrayList<CodigoCPSI>();
        for (CodigoCPSI cpsi : codigosSeleccionados) {
            if (!cpsi.getEstatus().getId().equals(EstatusCPSI.ASIGNADO)) {
                // Comprueba que el estatus del CPSI sea correcto para cesión
                cpsisEstatusNoValido.add(cpsi);
            } else {
                // Comprueba si se han seleccionado CPSIs ya elegidos para ceder
                if (arrayContains(listaCesiones, cpsi.getId().intValue())) {
                    cpsisEnCesion.add(cpsi);
                }
            }
        }

        if (!cpsisEnCesion.isEmpty()) {
            StringBuffer sb = new StringBuffer();
            sb.append("No es posible agregar los CPSI seleccionados.<br>");
            sb.append("Lo siguientes CPSI ya existen para cesión:<br>");
            for (CodigoCPSI cpsi : cpsisEnCesion) {
                sb.append("Binario: ").append(cpsi.getBinario());
                sb.append(", Decimal: ").append(cpsi.getDecimalTotal());
                sb.append(", Formato Dec.: ").append(cpsi.getFormatoDecimal()).append("<br>");
            }
            MensajesBean.addErrorMsg(MSG_ID, sb.toString(), "");
            return false;
        }

        if (!cpsisEstatusNoValido.isEmpty()) {
            StringBuffer sb = new StringBuffer();
            if (!cpsisEnCesion.isEmpty()) {
                sb.append("<br>");
            } else {
                sb.append("No es posible agregar los CPSI seleccionados.<br>");
            }
            sb.append("Lo siguientes CPSI tienen un estatus no permitido para cesión:<br>");
            for (CodigoCPSI cpsi : cpsisEstatusNoValido) {
                sb.append("Binario: ").append(cpsi.getBinario());
                sb.append(", Decimal: ").append(cpsi.getDecimalTotal());
                sb.append(", Formato Dec.: ").append(cpsi.getFormatoDecimal()).append("<br>");
                sb.append(", Estatus: ").append(cpsi.getEstatus().getDescripcion()).append("<br>");
            }
            MensajesBean.addErrorMsg(MSG_ID, sb.toString(), "");
            return false;
        }

        return true;
    }

    /**
     * Indica si una lista contiene una CesionSolicitadaCPSN sin usar los métos Equals de Object.
     * @param list Lista de objetos CesionSolicitadaCpsi
     * @param pCpsiId Identificador de código CPSI.
     * @return True si la CesionSolicitadaCpsi esta en la lista
     * @throws Exception en caso de error
     */
    private boolean arrayContains(List<CesionSolicitadaCpsi> list, int pCpsiId) throws Exception {

        // Cuando se guardan los cambios las CesionesSolicitadas tienen otra instancia diferente dada
        // por JPA y, por lo tanto, no se pueden comparar con las almacenadas en la lista interna. Es necesario
        // comparar por los campos clave para saber si nos estamos refiriendo a la misma.

        for (CesionSolicitadaCpsi cesSol : list) {
            if (cesSol.getIdCpsi().intValue() == pCpsiId) {
                return true;
            }
        }
        return false;
    }

    /**
     * Crea un objeto CesionSolicitadaCpsi en base a la información de un CPSI.
     * @param pCpsi CPSI con la información de la cesión.
     * @return CesionSolicitadaCpsi
     * @throws Exception en caso de error.
     */
    private CesionSolicitadaCpsi getCesionSolicitadaFromCPSI(CodigoCPSI pCpsi) throws Exception {
        CesionSolicitadaCpsi cscpsi = new CesionSolicitadaCpsi();
        cscpsi.setSolicitudCesion(solicitud);
        cscpsi.setFechaImplementacion(FechasUtils.parseFecha(fechaImplementacion));
        cscpsi.setIdCpsi(pCpsi.getId());
        cscpsi.setBinario(pCpsi.getBinario());
        cscpsi.setFormatoDecimal(pCpsi.getFormatoDecimal());

        StringBuilder sbImplementacion = new StringBuilder();
        sbImplementacion.append(periodoImplementacion).append(" ");
        if (tipoPeriodoImplementacion.equals("0") || tipoPeriodoImplementacion.equals("2")) {
            sbImplementacion.append("Día/s");
        } else {
            sbImplementacion.append("Mes/s");
        }
        cscpsi.setPeriodoImplementacion(sbImplementacion.toString());

        EstadoCesionSolicitada ecs = new EstadoCesionSolicitada();
        ecs.setCodigo(EstadoCesionSolicitada.PENDIENTE);
        ecs.setDescripcion(EstadoCesionSolicitada.TXT_PENDIENTE);
        cscpsi.setEstatus(ecs);

        return cscpsi;
    }

    // GETTERS & SETTERS

    /**
     * Cesion CPSI Solicitada seleccionada de la tabla de Cesiones.
     * @return CesionSolicitadaCpsi
     */
    public CesionSolicitadaCpsi getCesSolSeleccionada() {
        return cesSolSeleccionada;
    }

    /**
     * Cesion CPSI Solicitada seleccionada de la tabla de Cesiones.
     * @param cesSolSeleccionada CesionSolicitadaCpsi
     */
    public void setCesSolSeleccionada(CesionSolicitadaCpsi cesSolSeleccionada) {
        this.cesSolSeleccionada = cesSolSeleccionada;
    }

    /**
     * Fecha de implementación de la cesión.
     * @return Date
     */
    public Date getFechaImplementacion() {
        return fechaImplementacion;
    }

    /**
     * Fecha de implementación de la cesión.
     * @param fechaImplementacion Date
     */
    public void setFechaImplementacion(Date fechaImplementacion) {
        this.fechaImplementacion = fechaImplementacion;
    }

    /**
     * Listado de Códigos CPSI seleccionados.
     * @return List
     */
    public List<CodigoCPSI> getCodigosSeleccionados() {
        return codigosSeleccionados;
    }

    /**
     * Listado de Códigos CPSI seleccionados.
     * @param codigosSeleccionados List
     */
    public void setCodigosSeleccionados(List<CodigoCPSI> codigosSeleccionados) {
        this.codigosSeleccionados = codigosSeleccionados;
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
     * @return int
     */
    public String getPeriodoImplementacion() {
        return periodoImplementacion;
    }

    /**
     * Días / meses / fecha indicada para la implementación programada.
     * @param periodoImplementacion int
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

    /**
     * Información de la petición de Solicitud.
     * @return SolicitudCesionCpsi
     */
    public SolicitudCesionCpsi getSolicitud() {
        return solicitud;
    }

    /**
     * Indica si se se ha de habilitar el botón de 'Guardar'.
     * @return boolean
     */
    public boolean isSalvarHabilitado() {
        return salvarHabilitado;
    }

    /**
     * Listado de códigos internacionales.
     * @return List
     */
    public List<CodigoCPSI> getListadoCodigos() {
        return listadoCodigos;
    }

    /**
     * Lista de Cesiones CPSI solicitadas por el Cedente para el Cesionario.
     * @return List
     */
    public List<CesionSolicitadaCpsi> getListaCesiones() {
        return listaCesiones;
    }

}
