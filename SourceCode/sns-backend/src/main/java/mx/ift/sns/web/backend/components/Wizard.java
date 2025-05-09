package mx.ift.sns.web.backend.components;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import mx.ift.sns.modelo.solicitud.Solicitud;
import mx.ift.sns.web.backend.ApplicationException;
import mx.ift.sns.web.backend.common.MensajesBean;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.FlowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase base que han de heredar los bean que estén asociados a un Wizard de primesfaces para controlar el flujo de
 * movimiento entre pestañas.
 */
public abstract class Wizard implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 3611163943075398482L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Wizard.class);

    /** Lista de pasos para completar el Wizzard. */
    private List<TabWizard> pasosWizard = new ArrayList<TabWizard>();

    /** Lista de id de los pasos dados en el Wizzard. */
    private List<String> pasosDados = new ArrayList<String>();

    /** Índice para movernos por los pasos incluidos en la lista. */
    private int numPaso = 0;

    /** Titulo del wizard. */
    private String titulo;

    /** Indica si nos movemos hacia atrás o adelante en el wizard. */
    private boolean flujoAtras = false;

    /** Referencia al componente de p:messages del wizard general. */
    private String wizardMessagesId = null;

    /** Solicitud asociada al Wizard. Cada bean que hereda del wizard instancia el tipo de solicitud que necesite. */
    private Solicitud solicitud;

    /**
     * Método invocado por el p:wizzard para avanzar al siguiente paso si es posible.
     * @param event FlowEvent
     * @return Identificador del siguiente tab si es posible continuar o el nombre del tab actual si no lo es.
     */
    public String siguientePaso(FlowEvent event) {

        // Si no es posible continuar nos quedamos en la pestaña actual.
        String pasoSiguiente = event.getOldStep();

        try {
            if (!pasosWizard.isEmpty() && (pasosWizard.size() > numPaso)) {

                // Control de atrás-adelante repetido sobre dos tabs
                if (numPaso > 0) {
                    flujoAtras = pasosDados.get(numPaso - 1).equals(event.getNewStep());
                } else {
                    flujoAtras = false;
                }

                if (flujoAtras) {
                    TabWizard paso = pasosWizard.get(numPaso);
                    if (paso.isRetroceder()) {
                        pasosDados.remove(numPaso - 1);
                        numPaso--;

                        if (numPaso >= 0) {
                            pasosWizard.get(numPaso).actualizaCampos();
                        }
                        event.getOldStep();
                        pasoSiguiente = event.getNewStep();
                    } else {
                        boolean mensaje = false;
                        if (!StringUtils.isEmpty(paso.getMensajeError())) {
                            MensajesBean.addErrorMsg(wizardMessagesId, paso.getMensajeError());
                            mensaje = true;
                        }

                        if (!StringUtils.isEmpty(paso.getSummaryError())) {
                            MensajesBean.addErrorMsg(wizardMessagesId, paso.getSummaryError(), "");
                            mensaje = true;
                        }

                        if (!mensaje) {
                            MensajesBean.addErrorMsg(wizardMessagesId, "No es posible continuar", "");
                        }
                    }
                } else {
                    TabWizard paso = pasosWizard.get(numPaso);
                    if (paso.isAvanzar()) {
                        pasosDados.add(event.getOldStep());
                        numPaso++;
                        event.getOldStep();
                        pasoSiguiente = event.getNewStep();
                        if (pasosWizard.size() > numPaso) {
                            TabWizard siguientePaso = pasosWizard.get(numPaso);
                            // Carga de variables incial del Tab.
                            if (!siguientePaso.isIniciado()) {
                                siguientePaso.cargaValoresIniciales();
                                siguientePaso.setIniciado(true);
                            }
                            siguientePaso.actualizaCampos();
                        }
                    } else {
                        boolean mensaje = false;
                        if (!StringUtils.isEmpty(paso.getMensajeError())) {
                            MensajesBean.addErrorMsg(wizardMessagesId, paso.getMensajeError());
                            mensaje = true;
                        }

                        if (!StringUtils.isEmpty(paso.getSummaryError())) {
                            MensajesBean.addErrorMsg(wizardMessagesId, paso.getSummaryError(), "");
                            mensaje = true;
                        }

                        if (!mensaje) {
                            MensajesBean.addErrorMsg(wizardMessagesId, "No es posible continuar", "");
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error en el flujo de tabs: ", e);
            pasoSiguiente = event.getOldStep();
            throw new ApplicationException();
        }

        return pasoSiguiente;
    }

    /** Invoca al método resetTab() de cada paso para limpiar variables. */
    public void resetTabs() {
        try {
            for (TabWizard paso : pasosWizard) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Limpiando variables, paso: " + paso.getId());
                }
                paso.resetTab();
            }
            pasosDados = new ArrayList<String>();
        } catch (Exception e) {
            LOGGER.error("Error en la limpieza de tabs: ", e);
            throw new ApplicationException();
        }
    }

    /** Invoca al método actualizaCampos() de cada paso para actualizar variables. */
    public void actualizaCamposTabs() {
        try {
            for (TabWizard paso : pasosWizard) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Actualizando campos, paso: " + paso.getId());
                }
                paso.actualizaCampos();
            }
        } catch (Exception e) {
            LOGGER.error("Error en la actualización de campos de tabs: ", e);
            throw new ApplicationException();
        }
    }

    /**
     * Comprueba si el wizard puede pasar al paso siguiente.
     * @return true si puede
     */
    public boolean isAvanzar() {
        boolean r = false;
        if (numPaso > 0) {
            TabWizard tab = pasosWizard.get(numPaso);
            r = tab.isAvanzar();
        }

        LOGGER.debug("r={}", r);

        return r;
    }

    /**
     * Comprueba si el wizard puede pasar al paso anterior.
     * @return true si puede
     */
    public boolean isRetroceder() {
        boolean r = false;
        if (numPaso > 0) {
            TabWizard tab = pasosWizard.get(numPaso);
            r = tab.isRetroceder();
        }

        LOGGER.debug("r={}", r);

        return r;
    }

    /**
     * Restablece a cero el contador de pasos.
     */
    public void resetNumPaso() {
        numPaso = 0;
        flujoAtras = false;
    }

    /**
     * Solicitud asociada al Wizard.
     * @return Instancia de Solicitud hija creada en el bean que hereda de éste wizard
     */
    public Solicitud getSolicitud() {
        return solicitud;
    }

    /**
     * @return List<TabWizard>
     */
    public List<TabWizard> getPasosWizard() {
        return pasosWizard;
    }

    /**
     * @param pasosWizard List<TabWizard>
     */
    public void setPasosWizard(List<TabWizard> pasosWizard) {
        this.pasosWizard = pasosWizard;
    }

    /**
     * @param solicitud Solicitud
     */
    public void setSolicitud(Solicitud solicitud) {
        this.solicitud = solicitud;
    }

    /**
     * @return String
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * @param titulo String
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * @return String
     */
    public String getWizardMessagesId() {
        return wizardMessagesId;
    }

    /**
     * @param wizardMessagesId String
     */
    public void setWizardMessagesId(String wizardMessagesId) {
        this.wizardMessagesId = wizardMessagesId;
    }

    /**
     * @return List<String>
     */
    public List<String> getPasosDados() {
        return pasosDados;
    }

    /**
     * @param pasosDados List<String>
     */
    public void setPasosDados(List<String> pasosDados) {
        this.pasosDados = pasosDados;
    }
}
