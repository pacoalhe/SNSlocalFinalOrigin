package mx.ift.sns.web.backend.components;

import java.io.Serializable;

/**
 * Proporciona variables y lógica común para las clases que se utilicen en los Backing Bean asociados a los Wizard de
 * Primefaces. <b> Ésta clase ha de ser extendida por las clases que estén asociadas a una pestaña contenida en un
 * Wizard.</b>
 * @see Wizard
 */
public abstract class TabWizard implements Serializable {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** id del tab. */
    private String id;

    /** Id de mensajes de error del tab. */
    private String idMensajes = null;

    /** Mensaje de error para el atributo 'Detail' en los componentes p:message de JSF. */
    private String mensajeError;

    /** Mensaje de error para el atributo 'Summary' en los componentes p:message de JSF. */
    private String summaryError;

    /** Wizard contenedor. */
    private Wizard wizard;

    /** Habilita o deshabilita los campos de la pestaña asociada. */
    private boolean tabHabilitado = true;

    /** Tab guardado. */
    private boolean guardado = false;

    /** Datos modificados, hay que guardar. */
    private boolean modificado = false;

    /** Indica que el Tab ha cargado los valores iniciales. */
    private boolean iniciado = false;

    // Métodos obligatorios a redefinir en cada Tab.

    /**
     * Indica si el formulario asociado al paso esta completado y se puede continuar.
     * @return 'true' si las validaciones del paso son correctas
     */
    public abstract boolean isAvanzar();

    /**
     * Método que resfreca el tab antes de obtener el foco.
     */
    public abstract void actualizaCampos();

    /**
     * Método que limpia el valor de las variables y campos en los tabs.
     */
    public abstract void resetTab();

    // Métodos opcionales a redefinir en cada Tab.

    /**
     * Indica si es posible retroceder al tab anterior en el flujo de pasos.
     * @return 'true' si es posible retroceder al paso anterior.
     */
    public boolean isRetroceder() {
        return true;
    }

    /**
     * Realiza una carga de valores iniciales bajo demanda.
     */
    public void cargaValoresIniciales() {
        this.setIniciado(true);
    }

    // GETTERS & SETTERS

    /**
     * Set del wizard contenedor.
     * @param wizard contenedor.
     */
    public void setWizard(Wizard wizard) {
        this.wizard = wizard;
    }

    /**
     * Devuelve el wizard al que pertenece el tab.
     * @return Wizard
     */
    public Wizard getWizard() {
        return wizard;
    }

    /**
     * Mensaje de error para el atributo 'Detail' en los componentes p:message de JSF.
     * @param mensajeError String
     */
    public void setMensajeError(String mensajeError) {
        this.mensajeError = mensajeError;
    }

    /**
     * Mensaje de error para el atributo 'Detail' en los componentes p:message de JSF.
     * @return String
     */
    public String getMensajeError() {
        return mensajeError;
    }

    /**
     * Mensaje de error para el atributo 'Summary' en los componentes p:message de JSF.
     * @return String
     */
    public String getSummaryError() {
        return summaryError;
    }

    /**
     * Mensaje de error para el atributo 'Summary' en los componentes p:message de JSF.
     * @param summaryError String
     */
    public void setSummaryError(String summaryError) {
        this.summaryError = summaryError;
    }

    /**
     * id del tab.
     * @return String
     */
    public String getId() {
        return id;
    }

    /**
     * id del tab.
     * @param id String
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Habilita o deshabilita los campos de la pestaña asociada.
     * @return True si se pueden editar los campos.
     */
    public boolean isTabHabilitado() {
        return tabHabilitado;
    }

    /**
     * Habilita o deshabilita los campos de la pestaña asociada.
     * @param tabHabilitado boolean
     */
    public void setTabHabilitado(boolean tabHabilitado) {
        this.tabHabilitado = tabHabilitado;
    }

    /**
     * Tab guardado.
     * @return boolean
     */
    public boolean isGuardado() {
        return guardado;
    }

    /**
     * Tab guardado.
     * @param guardado boolean
     */
    public void setGuardado(boolean guardado) {
        this.guardado = guardado;
    }

    /**
     * Datos modificados, hay que guardar.
     * @return boolean
     */
    public boolean isModificado() {
        return modificado;
    }

    /**
     * Datos modificados, hay que guardar.
     * @param modificado boolean
     */
    public void setModificado(boolean modificado) {
        this.modificado = modificado;
    }

    /**
     * Id de mensajes de error del tab.
     * @return String
     */
    public String getIdMensajes() {
        return idMensajes;
    }

    /**
     * Id de mensajes de error del tab.
     * @param idMensajes String
     */
    public void setIdMensajes(String idMensajes) {
        this.idMensajes = idMensajes;
    }

    /**
     * Indica que el Tab ha cargado los valores iniciales.
     * @return 'True' si se han cargado los valores iniciarles del Tab.
     */
    public boolean isIniciado() {
        return iniciado;
    }

    /**
     * Indica que el Tab ha cargado los valores iniciales.
     * @param iniciado 'True' si se han cargado los valores iniciarles del Tab.
     */
    public void setIniciado(boolean iniciado) {
        this.iniciado = iniciado;
    }

}
