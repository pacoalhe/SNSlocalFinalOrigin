package mx.ift.sns.web.backend.cpsi.cesion;

import mx.ift.sns.modelo.cpsi.SolicitudCesionCpsi;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.modelo.solicitud.TipoSolicitud;
import mx.ift.sns.negocio.cpsi.ICodigoCPSIFacade;
import mx.ift.sns.negocio.exceptions.RegistroModificadoException;
import mx.ift.sns.utils.date.FechasUtils;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.components.TabWizard;
import mx.ift.sns.web.backend.components.Wizard;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Clase de soporte para la pestaña de 'GeneralesCesiones' en los Wizard. */
public class GeneralesCesionCpsiTab extends TabWizard {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(GeneralesCesionCpsiTab.class);

    /** Identificador del componente de mensajes JSF. */
    private static final String MSG_ID = "MSG_GeneralesCesiones";

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Información de la petición de Solicitud. */
    private SolicitudCesionCpsi solicitud;

    /** Servicio de Códigos Nacionales. */
    private ICodigoCPSIFacade codigoCpsiFacade;

    /** Indica si se se ha de habilitar el botón de 'Guardar'. */
    private boolean salvarHabilitado = false;

    /**
     * Constructor de la clase de respaldo para el tab/pestaña de 'GeneralesCesionesCPSN'.
     * @param pWizard Referencia a la clase Wizard que instancia este Tab
     * @param codigoCpsiFacade servicio de códigos
     */
    public GeneralesCesionCpsiTab(Wizard pWizard, ICodigoCPSIFacade codigoCpsiFacade) {
        try {
            setWizard(pWizard);
            solicitud = (SolicitudCesionCpsi) getWizard().getSolicitud();
            this.codigoCpsiFacade = codigoCpsiFacade;
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
            this.setSummaryError(Errores.getDescripcionError(Errores.ERROR_0004));
        }
    }

    @Override
    public boolean isAvanzar() {
        boolean r = false;
        if (isTabHabilitado()) {
            if (isGuardado() || solicitud.getId() != null) {
                r = isValido();
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

    /**
     * Método que válida generales.
     * @return boolean valida
     */
    public boolean isValido() {
        try {
            // Fecha de solicitud debe ser igual o menor a la fecha del día en curso
            if (solicitud.getFechaSolicitud().after(FechasUtils.getFechaHoy())) {
                MensajesBean.addErrorMsg(MSG_ID,
                        MensajesBean.getTextoResource("manual.generales.error.fechasol.menor"), "");
                return false;
            }
            // Referencia Solicitud no puede estar vacío
            if (solicitud.getReferencia() == null || solicitud.getReferencia().trim().isEmpty()) {
                MensajesBean.addErrorMsg(MSG_ID,
                        MensajesBean.getTextoResource("manual.generales.error.referencia.required"), "");
                return false;
            }

            // Si ha pasado todas las validaciones
            return true;

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
            return false;
        }
    }

    /**
     * Guarda los cambios realizados hasta el momento en el trámite.
     * @return 'True' si se ha podido guardar correctamente.
     */
    private boolean guardarCambios() {
        try {
            if (isValido()) {
                // Solicitud en trámite
                EstadoSolicitud estado = new EstadoSolicitud();
                estado.setCodigo(EstadoSolicitud.SOLICITUD_EN_TRAMITE);
                solicitud.setEstadoSolicitud(estado);

                // Tipo de Solicitud de Cesión
                if (solicitud.getTipoSolicitud() == null) {
                    TipoSolicitud tipoSol = new TipoSolicitud();
                    tipoSol.setCdg(TipoSolicitud.CESION_CPSI);
                    solicitud.setTipoSolicitud(tipoSol);
                }

                // Guardamos la Solicitud
                solicitud = codigoCpsiFacade.saveSolicitudCesion(solicitud);

                // Actualizamos la nueva instancia en el Wizard padre.
                getWizard().setSolicitud(solicitud);

                setGuardado(true);
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
        solicitud = (SolicitudCesionCpsi) getWizard().getSolicitud();
        this.habilitarSalvarBoton();
    }

    /** Método invocado al procesar el formulario. */
    public void habilitarSalvarBoton() {
        salvarHabilitado = (solicitud.getFechaSolicitud() != null) && (!StringUtils.isEmpty(solicitud.getReferencia()));
    }

    @Override
    public void resetTab() {
        setGuardado(false);
        salvarHabilitado = false;
    }

    // GETTERS & SETTERS

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

}
