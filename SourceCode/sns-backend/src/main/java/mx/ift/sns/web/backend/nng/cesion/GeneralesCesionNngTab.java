package mx.ift.sns.web.backend.nng.cesion;

import mx.ift.sns.modelo.nng.SolicitudCesionNng;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.modelo.solicitud.TipoSolicitud;
import mx.ift.sns.negocio.exceptions.RegistroModificadoException;
import mx.ift.sns.negocio.nng.INumeracionNoGeograficaFacade;
import mx.ift.sns.utils.date.FechasUtils;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.components.TabWizard;
import mx.ift.sns.web.backend.components.Wizard;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Clase de soporte para la pestaña de 'GeneralesCesiones' en los Wizard. */
public class GeneralesCesionNngTab extends TabWizard {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(GeneralesCesionNngTab.class);

    /** Identificador del componente de mensajes JSF. */
    private static final String MSG_ID = "MSG_GeneralesCesiones";

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Servicio de Numeración Geográfica. */
    private INumeracionNoGeograficaFacade nngFacade;

    /** Información de la petición de Solicitud. */
    private SolicitudCesionNng solicitud;

    /** Indica si se se ha de habilitar el botón de 'Guardar'. */
    private boolean salvarHabilitado = false;

    /**
     * Constructor de la clase de respaldo para el tab/pestaña de 'GeneralesCesiones'.
     * @param pWizard Referencia a la clase Wizard que instancia este Tab
     * @param pNngFacade Facade de Servicios de Numeración No Geográfica
     */
    public GeneralesCesionNngTab(Wizard pWizard, INumeracionNoGeograficaFacade pNngFacade) {
        try {
            // Asociamos el Wizard padre a la pestaña que reprsenta este Tab
            setWizard(pWizard);

            // Asociamos la solicitud que usaremos en todo el Wizard
            solicitud = (SolicitudCesionNng) getWizard().getSolicitud();

            // Asociamos el Facade
            nngFacade = pNngFacade;

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            this.setSummaryError(Errores.getDescripcionError(Errores.ERROR_0004));
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Método invocado al procesar el formulario. */
    public void habilitarSalvarBoton() {
        salvarHabilitado = (solicitud.getFechaSolicitud() != null) && (!StringUtils.isEmpty(solicitud.getReferencia()));
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
                    tipoSol.setCdg(TipoSolicitud.CESION_DERECHOS_NNG);
                    solicitud.setTipoSolicitud(tipoSol);
                }

                // Guardamos la Solicitud
                solicitud = nngFacade.saveSolicitudCesion((SolicitudCesionNng) solicitud);

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

            // Si ha pasado todas las validaciones
            return true;

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
            return false;
        }
    }

    @Override
    public boolean isAvanzar() {

        boolean r = false;

        // Si el tab no está habilitado para edición permitimos pasar
        // directamente a la siguiente pestaña.
        if (isTabHabilitado()) {
            // if (guardarCambios()) {
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

    @Override
    public void actualizaCampos() {
        solicitud = (SolicitudCesionNng) getWizard().getSolicitud();
        this.habilitarSalvarBoton();
    }

    @Override
    public void resetTab() {
        salvarHabilitado = false;
        setGuardado(false);
    }

    // GETTERS & SETTERS

    /**
     * Indica si se se ha de habilitar el botón de 'Guardar'.
     * @return True si el bótón esta habilitado
     */
    public boolean isSalvarHabilitado() {
        return salvarHabilitado;
    }

    /**
     * Información de Solicitud.
     * @return Solicitud
     */
    public SolicitudCesionNng getSolicitud() {
        return solicitud;
    }

}
