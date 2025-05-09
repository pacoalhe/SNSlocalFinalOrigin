package mx.ift.sns.web.backend.cpsi.asignacion;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mx.ift.sns.modelo.cps.EstatusAsignacionCps;
import mx.ift.sns.modelo.cpsi.CodigoCPSI;
import mx.ift.sns.modelo.cpsi.CpsiAsignado;
import mx.ift.sns.modelo.cpsi.EstatusCPSI;
import mx.ift.sns.modelo.cpsi.SolicitudAsignacionCpsi;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.negocio.cpsi.ICodigoCPSIFacade;
import mx.ift.sns.negocio.exceptions.RegistroModificadoException;
import mx.ift.sns.utils.date.FechasUtils;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.components.TabWizard;
import mx.ift.sns.web.backend.components.Wizard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Clase de soporte para la pestaña de 'Asignación' de Solicitudes de Asignación CPSI. */
public class AsignacionCpsiTab extends TabWizard {

    /** Logger de la clase. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AsignacionCpsiTab.class);

    /** Id del campo de errores. */
    private static final String MSG_ID = "MSG_AsignacionCPSI";

    /** Información de la petición de Solicitud. */
    private SolicitudAsignacionCpsi solicitud;

    /** Servicio de Códigos Nacionales. */
    private ICodigoCPSIFacade codigoCpsiFacade;

    /** Lista de Asignaciones Solicitadas. */
    private List<CpsiAsignado> listaAsignacionesSolicitadas;

    /** Indica si los códigos CPSI han sido asignados. */
    private boolean codigosAsignados = false;

    /** Número de días para fija la fecha de inicio de utilización. */
    private String diasAplicacion = "0";

    /**
     * Constructor de la clase de respaldo para el tab/pestaña de 'Análisis'.
     * @param pWizard Wizard
     * @param pCodigoCpsiFacade Facade de Servicios de CPSI.
     */
    public AsignacionCpsiTab(Wizard pWizard, ICodigoCPSIFacade pCodigoCpsiFacade) {
        try {
            // Asociamos el Wizard padre a la pestaña que reprsenta este Tab
            setWizard(pWizard);
            setIdMensajes(MSG_ID);

            // Asociamos la solicitud que usaremos en todo el Wizard
            solicitud = (SolicitudAsignacionCpsi) getWizard().getSolicitud();

            // Asociamos el Facade de servicios
            codigoCpsiFacade = pCodigoCpsiFacade;

            // Inicializaciones
            listaAsignacionesSolicitadas = new ArrayList<CpsiAsignado>();

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    @Override
    public void resetTab() {
        this.listaAsignacionesSolicitadas.clear();
        this.solicitud = null;
        this.codigosAsignados = false;
        this.diasAplicacion = "0";
    }

    @Override
    public void actualizaCampos() {
        // La solicitud del Wizard ha cambiado de instancia desde que se generó en
        // el constructor. Es necesario actualizar la referecnia en el tab.
        solicitud = (SolicitudAsignacionCpsi) getWizard().getSolicitud();

        if (solicitud.getDiasAplicacion() != null) {
            diasAplicacion = String.valueOf(solicitud.getDiasAplicacion());
        } else {
            diasAplicacion = "0";
        }

        // Actualizamos las solicitudes pendientes
        listaAsignacionesSolicitadas.clear();
        if (solicitud.getCpsiAsignados() != null) {
            listaAsignacionesSolicitadas.addAll(solicitud.getCpsiAsignados());

            // Comprobamos si existen códigos asignados pendientes.
            if (!solicitud.getEstadoSolicitud().getCodigo().equals(EstadoSolicitud.SOLICITUD_CANCELADA)) {
                codigosAsignados = true;
                for (CpsiAsignado cpsiAsig : solicitud.getCpsiAsignados()) {
                    if (cpsiAsig.getEstatus().getCodigo().equals(EstatusAsignacionCps.PENDIENTE)) {
                        codigosAsignados = false;
                        break;
                    }
                }
            }
        }
    }

    /** Método invocado al pulsar el botón de 'Asignar'. */
    public void asignarCodigos() {
        try {
            if (validaCodigos()) {
                // Asignamos los códigos seleccionados.
                solicitud = codigoCpsiFacade.applyAsignacionesSolicitadas(solicitud);

                // Actualizamos la solicitud para todos los tabs
                getWizard().setSolicitud(solicitud);

                // Deshabilitamos el botón de 'Asignar'
                codigosAsignados = true;

                // Actualizamos la tabla de Asignaciones con el nuevo estado.
                listaAsignacionesSolicitadas.clear();
                listaAsignacionesSolicitadas.addAll(solicitud.getCpsiAsignados());
            }
        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0015);
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Comprueba que los códigos seleccionados para asignar sigan estando disponibles.
     * @return True si los códigos son válidos para asignación.
     * @throws Exception en caso de error.
     */
    private boolean validaCodigos() throws Exception {
        // Dado que la pestaña de análisis tiene acceso al catálogo, se puede dar el caso de haber seleccionado
        // codigos y posteriormente haberlos reservado desde el catálogo antes de haber sido asignados.
        ArrayList<String> avisos = new ArrayList<>(solicitud.getCpsiAsignados().size());
        for (CpsiAsignado cpsiAsig : solicitud.getCpsiAsignados()) {

            // Proveedor nulo para buscar todos los códigos asignados o no.
            CodigoCPSI cpsi = codigoCpsiFacade.getCodigoCpsi(cpsiAsig.getIdCpsi(), null);

            if (cpsi == null) {
                StringBuilder sbAviso = new StringBuilder();
                sbAviso.append("Binario: ").append(cpsiAsig.getBinario());
                sbAviso.append(", Decimal: ").append(cpsiAsig.getIdCpsi());
                sbAviso.append(", Formato Dec.: ").append(cpsiAsig.getFormatoDecimal());
                avisos.add(sbAviso.toString());
            }

            // El CPSI existe libre o asignado al proveedor solicitante
            if (cpsi != null) {
                // Comprobamos que el CPSI esté ya asignado al Solicitante o esté libre para poder ser asignado.
                // Si el Código está asignado a otro Proveedor (por un trámite posterior) se ignora también.
                if (!cpsi.getEstatus().getId().equals(EstatusCPSI.LIBRE)) {
                    if (!cpsi.getEstatus().getId().equals(EstatusCPSI.ASIGNADO)) {
                        // Estado no permitido.
                        StringBuilder sbAviso = new StringBuilder();
                        sbAviso.append("Binario: ").append(cpsiAsig.getBinario());
                        sbAviso.append(", Decimal: ").append(cpsiAsig.getIdCpsi());
                        sbAviso.append(", Formato Dec.: ").append(cpsiAsig.getFormatoDecimal());
                        sbAviso.append(", Estatus: ").append(cpsi.getEstatus().getDescripcion());
                        avisos.add(sbAviso.toString());
                    } else {
                        // Si está ya asignado, comprobamos que se corresponda con la asignación realizada por ésta
                        // solicitud.
                        if (!cpsi.getProveedor().equals(solicitud.getProveedorSolicitante())) {
                            // Estado no permitido.
                            StringBuilder sbAviso = new StringBuilder();
                            sbAviso.append("Binario: ").append(cpsiAsig.getBinario());
                            sbAviso.append(", Decimal: ").append(cpsiAsig.getIdCpsi());
                            sbAviso.append(", Formato Dec.: ").append(cpsiAsig.getFormatoDecimal());
                            sbAviso.append(", Estatus: ").append(cpsi.getEstatus().getDescripcion());
                            sbAviso.append(", Proveedor: ").append(cpsi.getProveedor().getNombre());
                            avisos.add(sbAviso.toString());
                        }
                    }
                }
            }
        }

        if (!avisos.isEmpty()) {
            StringBuilder sbAvisos = new StringBuilder();
            sbAvisos.append("Los siguientes códigos han sido modificados y no pueden ser asignados:<br>");
            for (String aviso : avisos) {
                sbAvisos.append(aviso).append("<br>");
            }
            MensajesBean.addErrorMsg(MSG_ID, sbAvisos.toString(), "");
        }

        return avisos.isEmpty();
    }

    /**
     * Guarda los cambios realizados hasta el momento en el trámite.
     * @return 'True' si se ha podido guardar correctamente.
     */
    private boolean guardarCambios() {
        try {
            // Días de aplicación.
            solicitud.setDiasAplicacion(Integer.parseInt(this.diasAplicacion));

            // Guardamos los cambios
            solicitud = codigoCpsiFacade.saveSolicitudAsignacion(solicitud);

            // Actualizamos la solicitud para todos los tabs
            getWizard().setSolicitud(solicitud);

            // Actualizamos el estado de las Asignaciones.
            listaAsignacionesSolicitadas.clear();
            listaAsignacionesSolicitadas.addAll(solicitud.getCpsiAsignados());

            return true;

        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0015);
            return false;
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
            return false;
        }
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
    public boolean isAvanzar() {
        boolean resultado = true;
        if (this.isTabHabilitado()) {
            resultado = codigosAsignados;
            if (!codigosAsignados) {
                MensajesBean.addErrorMsg(MSG_ID, "Es necesario confirmar la asignación de códigos solicitados", "");
            } else {
                resultado = resultado && this.guardarCambios();
            }
        }

        if (!resultado) {
            // Al no permitirse avanzar se muestra en el Wizard el mensaje indicado por el TabWizard
            this.setSummaryError(MensajesBean.getTextoResource("error.avanzar"));
        }
        return resultado;
    }

    /** Método invocado al modificar el campo 'Fecha de Asignación'. */
    public void validarFechaAsignacion() {
        Date fHoy = FechasUtils.getFechaHoy();
        if (solicitud.getFechaAsignacion() != null) {
            if (solicitud.getFechaAsignacion().after(fHoy)) {
                MensajesBean.addErrorMsg(MSG_ID, "La fecha de Asignación no puede ser superior al día actual", "");
                solicitud.setFechaAsignacion(fHoy);
            }
            // Actualizamos los días de aplicación y la fecha de inicio de utilización.
            this.diasAplicacionChange();
        } else {
            MensajesBean.addErrorMsg(MSG_ID, "La fecha de Asignación no puede ser nula", "");
            solicitud.setFechaAsignacion(fHoy);
        }
    }

    /** Método invocado al modificar el campo 'Fecha de Inicio de Utilización'. */
    public void validarFechaInicioUtilizacion() {
        if (solicitud.getFechaAsignacion() != null) {
            if (solicitud.getFechaIniUtilizacion() != null) {
                if (solicitud.getFechaIniUtilizacion().before(solicitud.getFechaAsignacion())) {
                    MensajesBean.addErrorMsg(MSG_ID,
                            "La fecha de inicio de utilización no puede ser inferior a la fecha de asignación.", "");
                    this.diasAplicacionChange();
                } else {
                    int diasEntre = (int) FechasUtils.getDiasEntre(
                            solicitud.getFechaAsignacion(),
                            solicitud.getFechaIniUtilizacion());
                    solicitud.setDiasAplicacion(diasEntre);
                }
            } else {
                // Ponemos la fecha de inicio por defecto en función de la fecha de asignación.
                this.diasAplicacionChange();
            }
        } else {
            MensajesBean.addErrorMsg(MSG_ID, "La fecha de Asignación no puede ser nula", "");
            this.diasAplicacionChange();
        }
    }

    /** Recalcula la fecha de inicio de utilización. */
    public void diasAplicacionChange() {
        if (solicitud.getFechaAsignacion() != null) {
            // Las validaciones client-side nos aseguran que llegue un valor numérico positivo.
            int diasAplicacion = Integer.parseInt(this.diasAplicacion);

            solicitud.setFechaIniUtilizacion(FechasUtils.calculaFecha(
                    solicitud.getFechaAsignacion(), diasAplicacion, 0, 0));
        }
    }

    // GETTERS & SETTERS

    /**
     * Lista de Asignaciones Solicitadas.
     * @return List
     */
    public List<CpsiAsignado> getListaAsignacionesSolicitadas() {
        return listaAsignacionesSolicitadas;
    }

    /**
     * Indica si los códigos CPSI han sido asignados.
     * @return boolean
     */
    public boolean isCodigosAsignados() {
        return codigosAsignados;
    }

    /**
     * Información de la petición de Solicitud.
     * @return SolicitudAsignacionCpsi
     */
    public SolicitudAsignacionCpsi getSolicitud() {
        return solicitud;
    }

    /**
     * Número de días para fija la fecha de inicio de utilización.
     * @return String
     */
    public String getDiasAplicacion() {
        return diasAplicacion;
    }

    /**
     * Número de días para fija la fecha de inicio de utilización.
     * @param diasAplicacion String
     */
    public void setDiasAplicacion(String diasAplicacion) {
        this.diasAplicacion = diasAplicacion;
    }

}
