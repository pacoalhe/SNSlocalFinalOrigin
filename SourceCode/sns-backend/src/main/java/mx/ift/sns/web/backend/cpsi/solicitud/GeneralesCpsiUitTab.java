package mx.ift.sns.web.backend.cpsi.solicitud;

import java.util.Date;

import mx.ift.sns.modelo.cpsi.SolicitudCpsiUit;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.modelo.solicitud.TipoSolicitud;
import mx.ift.sns.negocio.cpsi.ICodigoCPSIFacade;
import mx.ift.sns.negocio.exceptions.RegistroModificadoException;
import mx.ift.sns.web.backend.ApplicationException;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.components.TabWizard;
import mx.ift.sns.web.backend.components.Wizard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Clase de soporte para la pestaña de 'Generales' de Solicitudes de códigos CPSI a la UIT. */
public class GeneralesCpsiUitTab extends TabWizard {

    /** Logger de la clase. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(GeneralesCpsiUitTab.class);

    /** Id del campo de errores. */
    private static final String MSG_ID = "MSG_GeneralesCPSI";

    /** Información de la petición de Solicitud. */
    private SolicitudCpsiUit solicitud;

    /** Servicio de Códigos Nacionales. */
    private ICodigoCPSIFacade codigoCpsiFacade;

    /** Indica si se se ha de habilitar el botón de 'Guardar'. */
    private boolean salvarHabilitado = false;

    /**
     * Constructor de la clase de respaldo para el tab/pestaña de 'Generales'.
     * @param pWizard Wizard
     * @param pCodigoCpsiFacade Facade de Servicios de CPSI.
     */
    public GeneralesCpsiUitTab(Wizard pWizard, ICodigoCPSIFacade pCodigoCpsiFacade) {

        try {
            // Asociamos el Wizard padre a la pestaña que reprsenta este Tab
            setWizard(pWizard);
            setIdMensajes(MSG_ID);

            // Asociamos la solicitud que usaremos en todo el Wizard
            solicitud = (SolicitudCpsiUit) getWizard().getSolicitud();

            // Asociamos el Facade de servicios
            codigoCpsiFacade = pCodigoCpsiFacade;

            salvarHabilitado = false;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new ApplicationException();
        }
    }

    @Override
    public boolean isAvanzar() {
        boolean r = false;
        if (isTabHabilitado()) {
            if (isGuardado() || solicitud.getId() != null) {
                r = validaGeneral();
            } else {
                this.setSummaryError(Errores.getDescripcionError(Errores.ERROR_0008));
                r = false;
            }
        } else {
            // No se puede tocar el tab, dejamos pasar a la siguiente
            // pestaña.
            r = true;
        }

        return r;
    }

    @Override
    public void actualizaCampos() {
        try {
            // Actualizamos la Solicitud
            solicitud = (SolicitudCpsiUit) getWizard().getSolicitud();

            // Habilitamos el Botón de Guardar
            this.habilitarSalvarBoton();

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.getDescripcionError(Errores.ERROR_0004), "");
        }
    }

    @Override
    public void resetTab() {
        solicitud = null;
        salvarHabilitado = false;
    }

    /** Habilita el boton de guardar. */
    public void habilitarSalvarBoton() {
        salvarHabilitado = false;
        if (solicitud.getFechaSolicitud() != null) {
            if (solicitud.getNumCpsiSolicitados() != null) {
                if (solicitud.getNumCpsiSolicitados() > 0) {
                    salvarHabilitado = true;
                }
            }
        }
    }

    /**
     * Guarda los cambios realizados hasta el momento en el trámite.
     * @return 'True' si se ha podido guardar correctamente.
     */
    private boolean guardarCambios() {
        try {
            if (validaGeneral()) {
                // Solicitud en trámite
                EstadoSolicitud estado = new EstadoSolicitud();
                estado.setCodigo(EstadoSolicitud.SOLICITUD_EN_TRAMITE);
                solicitud.setEstadoSolicitud(estado);

                // Tipo de Solicitud de Cesión
                if (solicitud.getTipoSolicitud() == null) {
                    TipoSolicitud tipoSol = new TipoSolicitud();
                    tipoSol.setCdg(TipoSolicitud.SOLICITUD_CPSI_UIT);
                    solicitud.setTipoSolicitud(tipoSol);
                }

                // Guardamos la Solicitud
                if (solicitud.getNumCpsiSolicitados() == null) {
                    solicitud.setNumCpsiSolicitados(new Integer(0));
                }
                solicitud = codigoCpsiFacade.saveSolicitudCpsiUit(solicitud);

                // Actualizamos la nueva instancia en el Wizard padre.
                getWizard().setSolicitud(solicitud);
                setGuardado(true);
                return true;
            } else {
                return false;
            }
        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.getDescripcionError(Errores.ERROR_0015), "");
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.getDescripcionError(Errores.ERROR_0004), "");
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

    /**
     * Valida los datos del formulario.
     * @return true/false
     */
    private boolean validaGeneral() {

        boolean fechaCorrecta = true;
        boolean campoReferenciaInformado = true;
        Date fechaHoy = new Date();

        if (solicitud.getFechaSolicitud() != null) {
            if (solicitud.getFechaSolicitud().after(fechaHoy)) {
                MensajesBean.addErrorMsg(MSG_ID,
                        MensajesBean.getTextoResource("manual.generales.error.fechasol.menor"), "");
                fechaCorrecta = false;
            }
        } else {
            MensajesBean.addErrorMsg(MSG_ID,
                    MensajesBean.getTextoResource("manual.generales.error.fechasol.required"), "");
            fechaCorrecta = false;
        }

        if (solicitud.getReferencia().trim().isEmpty()) {
            if (solicitud.getReferencia().matches("\\s+")) {
                MensajesBean.addErrorMsg(MSG_ID,
                        MensajesBean.getTextoResource("manual.generales.error.referencia.no.blanco"), "");
                campoReferenciaInformado = false;
            }
        }
        return (fechaCorrecta && campoReferenciaInformado);
    }

    // GETTERS & SETTERS

    /**
     * Información de la solicitud.
     * @return SolicitudCpsiUit
     */
    public SolicitudCpsiUit getSolicitud() {
        return solicitud;
    }

    /**
     * Indicador de habilitar el botón de 'Guardar'.
     * @return boolean
     */
    public boolean isSalvarHabilitado() {
        return salvarHabilitado;
    }

    /**
     * Indicador de habilitar el botón de 'Guardar'.
     * @param salvarHabilitado boolean
     */
    public void setSalvarHabilitado(boolean salvarHabilitado) {
        this.salvarHabilitado = salvarHabilitado;
    }

}
