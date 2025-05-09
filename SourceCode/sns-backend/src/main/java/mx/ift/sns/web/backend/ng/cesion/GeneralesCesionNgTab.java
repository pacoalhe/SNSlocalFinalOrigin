package mx.ift.sns.web.backend.ng.cesion;

import mx.ift.sns.modelo.ng.SolicitudCesionNg;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.modelo.solicitud.TipoSolicitud;
import mx.ift.sns.negocio.exceptions.RegistroModificadoException;
import mx.ift.sns.negocio.ng.INumeracionGeograficaService;
import mx.ift.sns.utils.date.FechasUtils;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.components.TabWizard;
import mx.ift.sns.web.backend.components.Wizard;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Clase de soporte para la pestaña de 'GeneralesCesiones' en los Wizard. */
public class GeneralesCesionNgTab extends TabWizard {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(GeneralesCesionNgTab.class);

    /** Identificador del componente de mensajes JSF. */
    private static final String MSG_ID = "MSG_GeneralesCesiones";

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Servicio de Numeración Geográfica. */
    private INumeracionGeograficaService ngService;

    /** Información de la petición de Solicitud. */
    private SolicitudCesionNg solicitud;

    /** Indica si se se ha de habilitar el botón de 'Guardar'. */
    private boolean salvarHabilitado = false;

    // /** Indica si estamos editando una solicitud existente. */
    // private boolean editandoSolicitud = false;

    /** Fecha Inicial de la Solicitud. */
    // private Date fechaSolicitudInicial = null;

    /**
     * Constructor de la clase de respaldo para el tab/pestaña de 'GeneralesCesiones'.
     * @param pWizard Referencia a la clase Wizard que instancia este Tab
     * @param pNgService Servicio de Numeración Geográfica
     */
    public GeneralesCesionNgTab(Wizard pWizard, INumeracionGeograficaService pNgService) {
        try {
            // Asociamos el Wizard padre a la pestaña que reprsenta este Tab
            setWizard(pWizard);

            // Asociamos la solicitud que usaremos en todo el Wizard
            solicitud = (SolicitudCesionNg) getWizard().getSolicitud();

            // Asociamos el Servicio
            ngService = pNgService;

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
            this.setSummaryError(Errores.getDescripcionError(Errores.ERROR_0004));
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
            // // Si existía una fecha inicial guardada comprobamos si se ha mantenido
            // if (fechaSolicitudInicial != null && solicitud.getFechaSolicitud() != null) {
            // if (solicitud.getFechaSolicitud().before(fechaSolicitudInicial)) {
            // MensajesBean.addErrorMsg(MSG_ID, "No es posible retrasar la fecha de una solicitud en trámite.");
            // return false;
            // }
            // }

            if (isValido()) {
                // Solicitud en trámite
                EstadoSolicitud estado = new EstadoSolicitud();
                estado.setCodigo(EstadoSolicitud.SOLICITUD_EN_TRAMITE);
                solicitud.setEstadoSolicitud(estado);

                // Tipo de Solicitud de Cesión
                if (solicitud.getTipoSolicitud() == null) {
                    TipoSolicitud tipoSol = new TipoSolicitud();
                    tipoSol.setCdg(TipoSolicitud.CESION_DERECHOS);
                    solicitud.setTipoSolicitud(tipoSol);
                }

                // Guardamos la Solicitud
                solicitud = ngService.saveSolicitudCesion((SolicitudCesionNg) solicitud);

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
            // // Si existía una fecha inicial guardada comprobamos si se ha mantenido
            // boolean fechaModificada = false;
            // if (fechaSolicitudInicial != null && solicitud.getFechaSolicitud() != null) {
            // fechaModificada = !(fechaSolicitudInicial.equals(solicitud.getFechaSolicitud()));
            // }

            // // Validación en función de si estamos editando o no.
            // if (editandoSolicitud && solicitud.getFechaSolicitud() != null && !fechaModificada) {
            // // La fecha ya se introdujo al crear la solicitud
            // return true;
            // } else {
            // // La fecha no puede ser anterior al día actual
            // Date fechaHoy = sdf.parse(sdf.format(new Date()));
            // if (solicitud.getFechaSolicitud().before(fechaHoy)) {
            // MensajesBean.addErrorMsg(MSG_ID, "Fecha solicitud no puede ser menor a la fecha de hoy");
            // return false;
            // }
            // }

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
                setSummaryError(Errores.getDescripcionError(Errores.ERROR_0008));
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
        solicitud = (SolicitudCesionNg) getWizard().getSolicitud();
        this.habilitarSalvarBoton();

        // // Estamos en editando una solicitud existente. Evita algunas validaciones.
        // editandoSolicitud = (solicitud.getEstadoSolicitud() != null
        // && solicitud.getEstadoSolicitud().getCodigo().equals(EstadoSolicitud.SOLICITUD_EN_TRAMITE));

        // Guardamos la fecha Inicial de la Solicitud
        // fechaSolicitudInicial = solicitud.getFechaSolicitud();
    }

    @Override
    public void resetTab() {
        salvarHabilitado = false;
        setGuardado(false);
        // editandoSolicitud = false;
        // fechaSolicitudInicial = null;
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
     * Indica si se se ha de habilitar el botón de 'Guardar'.
     * @param salvarHabilitado True si el bótón esta habilitado
     */
    public void setSalvarHabilitado(boolean salvarHabilitado) {
        this.salvarHabilitado = salvarHabilitado;
    }

    /**
     * Información de Solicitud.
     * @return Solicitud
     */
    public SolicitudCesionNg getSolicitud() {
        return solicitud;
    }
}
