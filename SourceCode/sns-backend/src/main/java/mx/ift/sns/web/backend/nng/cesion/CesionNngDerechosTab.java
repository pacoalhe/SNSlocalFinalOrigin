package mx.ift.sns.web.backend.nng.cesion;

import java.util.ArrayList;
import java.util.List;

import mx.ift.sns.modelo.nng.SolicitudCesionNng;
import mx.ift.sns.modelo.pst.Contacto;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.pst.TipoContacto;
import mx.ift.sns.modelo.pst.TipoProveedor;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.negocio.exceptions.RegistroModificadoException;
import mx.ift.sns.negocio.nng.INumeracionNoGeograficaFacade;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.components.TabWizard;
import mx.ift.sns.web.backend.components.Wizard;
import mx.ift.sns.web.backend.ng.cesion.CesionNgDerechosTab;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Clase de soporte para la pestaña de 'CesionDerechos' en los Wizard. */
public class CesionNngDerechosTab extends TabWizard {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CesionNgDerechosTab.class);

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador del componente de mensajes JSF. */
    private static final String MSG_ID = "MSG_CesionDerechos";

    /** Facade de Servicios de Numeración No Geográfica. */
    private INumeracionNoGeograficaFacade nngFacade;

    /** Información de la petición de Solicitud de Cesión NNG. */
    private SolicitudCesionNng solicitud;

    /** Indica si se se ha de habilitar el botón de 'Guardar'. */
    private boolean salvarHabilitado = false;

    /** Listado de Proveedores Cedentes. */
    private List<Proveedor> listaProveedoresCedentes;

    /** Listado de Proveedores Cesionarios. */
    private List<Proveedor> listaProveedoresCesionarios;

    /** Muestra los campos de identificación de un Proveedor de Tipo 'Comercializadora'. */
    private boolean pstComercializadora = true; // Mostramos todos los campos xq hay 2 composite de info del proveedor

    /** Muestra los campos de identificación de un Proveedor de Tipo 'Concesionario'. */
    private boolean pstConcesionario = true; // Mostramos todos los campos xq hay 2 composite de info del proveedor

    /** Lista de Representantes legales asociados al Proveedor Cedente. */
    private List<Contacto> listaRepresentantesLegalesPstCedente;

    /** Lista de Representantes suplentes asociados al Proveedor Cedente. */
    private List<Contacto> listaRepresentantesSuplentesPstCedente;

    /** Lista de Representantes legales asociados al Proveedor Cesionario. */
    private List<Contacto> listaRepresentantesLegalesPstCesionario;

    /** Lista de Representantes suplentes asociados al Proveedor Cesionario. */
    private List<Contacto> listaRepresentantesSuplentesPstCesionario;

    /** Indica si se ha de expandir el panel de información de Proveedor Cedente. */
    private boolean expandirPstCedente = false;

    /** Indica si se ha de expandir el panel de información de Represenatante Legal del Proveedor Cedente. */
    private boolean expandirRepLegalPstCedente = false;

    /** Indica si se ha de expandir el panel de información de Represenatante Suplente del Proveedor Cedente. */
    private boolean expandirRepSuplPstCedente = false;

    /** Indica si se ha de expandir el panel de información de Proveedor Cesionario. */
    private boolean expandirPstCesionario = false;

    /** Indica si se ha de expandir el panel de información de Represenatante Legal del Proveedor Cesionario. */
    private boolean expandirRepLegalPstCesionario = false;

    /** Indica si se ha de expandir el panel de información de Represenatante Suplente del Proveedor Cesionario. */
    private boolean expandirRepSuplPstCesionario = false;

    /**
     * Constructor de la clase de respaldo para el tab/pestaña de 'CesionDerechos'.
     * @param pWizard Referencia a la clase Wizard que instancia este Tab
     * @param pNngFacade Facade de Servicios de Numeración No Geográfica.
     */
    public CesionNngDerechosTab(Wizard pWizard, INumeracionNoGeograficaFacade pNngFacade) {
        // Asociamos el Wizard padre a la pestaña que reprsenta este Tab
        setWizard(pWizard);

        // Asociamos la solicitud que usaremos en todo el Wizard
        solicitud = (SolicitudCesionNng) getWizard().getSolicitud();

        // Asociamos el Facade de Servicios
        nngFacade = pNngFacade;
    }

    @Override
    public void cargaValoresIniciales() {
        try {
            // Inicializaciones para cargar los campos de Faces sin errores
            listaProveedoresCedentes = nngFacade.findAllProveedores();
            listaProveedoresCesionarios = new ArrayList<>(1);
            listaRepresentantesLegalesPstCedente = new ArrayList<>(1);
            listaRepresentantesLegalesPstCesionario = new ArrayList<>(1);
            listaRepresentantesSuplentesPstCedente = new ArrayList<>(1);
            listaRepresentantesSuplentesPstCesionario = new ArrayList<>(1);
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
            this.setSummaryError(Errores.getDescripcionError(Errores.ERROR_0004));
        }
    }

    @Override
    public void resetTab() {
        if (this.isIniciado()) {
            expandirPstCedente = false;
            expandirPstCesionario = false;
            expandirRepLegalPstCedente = false;
            expandirRepLegalPstCesionario = false;
            expandirRepSuplPstCedente = false;
            expandirRepSuplPstCesionario = false;
            salvarHabilitado = false;
            listaProveedoresCesionarios.clear();
            listaRepresentantesLegalesPstCedente.clear();
            listaRepresentantesLegalesPstCesionario.clear();
            listaRepresentantesSuplentesPstCedente.clear();
            listaRepresentantesSuplentesPstCesionario.clear();
        }
    }

    /** Método invocado al seleccionar un proveedor en el combo de 'Proveedor Cedente'. */
    public void seleccionPstCedente() {
        try {
            // El Proveedor Solicitante de la solicitud actúa como Proveedor Cedente.
            if (solicitud.getProveedorSolicitante() != null) {
                listaRepresentantesLegalesPstCedente = nngFacade.getRepresentantesLegales(
                        TipoContacto.REPRESENTANTE_LEGAL, solicitud.getProveedorSolicitante().getId());
                listaRepresentantesSuplentesPstCedente = nngFacade.getRepresentantesLegales(
                        TipoContacto.OTROS_REPRESENTANTES_LEGALES, solicitud.getProveedorSolicitante().getId());

                // Proveedores Cesionarios Disponibles
                listaProveedoresCesionarios = nngFacade.findAllCesionarios(solicitud.getProveedorSolicitante());

                // No existen Proveedores Cesionarios disponibles para el Cedente
                if (listaProveedoresCesionarios.isEmpty()) {
                    MensajesBean.addErrorMsg("MSG_CesionDerechos",
                            MensajesBean.getTextoResource("cesion.errores.sinCesionarios"), "");
                }

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Encontrados " + listaProveedoresCesionarios.size() + " posibles cesionarios para "
                            + solicitud.getProveedorSolicitante().getNombre());
                }

            } else {
                listaRepresentantesLegalesPstCedente = new ArrayList<Contacto>(1);
                listaRepresentantesSuplentesPstCedente = new ArrayList<Contacto>(1);
            }
            // Obligamos a seleccionar de nuevo los representantes
            solicitud.setRepresentanteLegal(null);
            solicitud.setRepresentanteSuplente(null);
            expandirRepLegalPstCedente = false;
            expandirRepSuplPstCedente = false;

            // Mostramos la info del proveedor si procede
            expandirPstCedente = (solicitud.getProveedorSolicitante() != null);

            // Obligamos a seleccionar de nuevo toda la información de cesionario ya que ha cambiado el cedente
            solicitud.setProveedorCesionario(null);
            solicitud.setRepresentanteLegalCesionario(null);
            solicitud.setRepresentanteSuplenteCesionario(null);
            expandirPstCesionario = false;
            expandirRepLegalPstCesionario = false;
            expandirRepSuplPstCesionario = false;

            // Refrescamos el botón de guardar
            this.habilitarSalvarBoton();
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Método invocado al seleccionar un proveedor en el combo de 'Proveedor Cesionario'. */
    public void seleccionPstCesionario() {
        try {
            if (solicitud.getProveedorCesionario() != null) {
                listaRepresentantesLegalesPstCesionario = nngFacade.getRepresentantesLegales(
                        TipoContacto.REPRESENTANTE_LEGAL, solicitud.getProveedorCesionario().getId());
                listaRepresentantesSuplentesPstCesionario = nngFacade.getRepresentantesLegales(
                        TipoContacto.OTROS_REPRESENTANTES_LEGALES, solicitud.getProveedorCesionario().getId());
            } else {
                listaRepresentantesLegalesPstCesionario = new ArrayList<Contacto>(1);
                listaRepresentantesSuplentesPstCesionario = new ArrayList<Contacto>(1);
            }
            // Obligamos a seleccionar de nuevo los representantes
            solicitud.setRepresentanteLegalCesionario(null);
            solicitud.setRepresentanteSuplenteCesionario(null);
            expandirRepLegalPstCesionario = false;
            expandirRepSuplPstCesionario = false;

            // Mostramos la info del proveedor si procede
            expandirPstCesionario = (solicitud.getProveedorCesionario() != null);

            // Refrescamos el botón de guardar
            this.habilitarSalvarBoton();
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Método invocado al seleccionar un representante en el combo de 'Representante Legal Pst Cedente'. */
    public void seleccionRepLegalPstCedente() {
        expandirRepLegalPstCedente = (solicitud.getRepresentanteLegal() != null);
        this.habilitarSalvarBoton();
    }

    /** Método invocado al seleccionar un representante en el combo de 'Representante Suplente Pst Cedente'. */
    public void seleccionRepSuplPstCedente() {
        expandirRepSuplPstCedente = (solicitud.getRepresentanteSuplente() != null);
    }

    /** Método invocado al seleccionar un representante en el combo de 'Representante Legal Pst Cesionario'. */
    public void seleccionRepLegalPstCesionario() {
        expandirRepLegalPstCesionario = (solicitud.getRepresentanteLegalCesionario() != null);
        this.habilitarSalvarBoton();
    }

    /** Método invocado al seleccionar un representante en el combo de 'Representante Suplente Pst Cesionario'. */
    public void seleccionRepSuplPstCesionario() {
        expandirRepSuplPstCesionario = (solicitud.getRepresentanteSuplenteCesionario() != null);
    }

    @Override
    public boolean isAvanzar() {
        // Si el tab no está habilitado para edición permitimos pasar
        // directamente a la siguiente pestaña.
        boolean resultado = false;
        if (this.isTabHabilitado()) {
            if (isValido()) {
                resultado = guardarCambios();
            } else {
                resultado = false;
            }
        } else {
            // No se puede tocar el tab, dejamos pasar a la siguiente
            // pestaña.
            resultado = true;
        }

        if (!resultado) {
            // Al no permitirse avanzar se muestra en el Wizard el mensaje indicado por el TabWizard
            this.setSummaryError(MensajesBean.getTextoResource("error.avanzar"));
        }
        return resultado;
    }

    @Override
    public void actualizaCampos() {
        try {
            // La solicitud del Wizard ha cambiado de instancia desde que se generó en
            // el constructor. Es necesario actualizar la referencia en el tab.
            solicitud = (SolicitudCesionNng) getWizard().getSolicitud();

            // Recargamos la lista de representantes del PST Cedente
            if (solicitud.getProveedorSolicitante() != null) {
                listaRepresentantesLegalesPstCedente = nngFacade.getRepresentantesLegales(
                        TipoContacto.REPRESENTANTE_LEGAL, solicitud.getProveedorSolicitante().getId());
                listaRepresentantesSuplentesPstCedente = nngFacade.getRepresentantesLegales(
                        TipoContacto.OTROS_REPRESENTANTES_LEGALES, solicitud.getProveedorSolicitante().getId());
            }

            // Mostramos la info de los proveedores seleccionados si procede
            expandirPstCedente = (solicitud.getProveedorSolicitante() != null);
            expandirPstCesionario = (solicitud.getProveedorCesionario() != null);

            // Si existe proveedor cedente solicitado cargamos el combo de cesionarios acordes
            if (solicitud.getProveedorSolicitante() != null) {
                // Proveedores Cesionarios Disponibles
                listaProveedoresCesionarios = nngFacade.findAllCesionarios(solicitud.getProveedorSolicitante());
                listaRepresentantesLegalesPstCesionario = nngFacade.getRepresentantesLegales(
                        TipoContacto.REPRESENTANTE_LEGAL, solicitud.getProveedorCesionario().getId());
                listaRepresentantesSuplentesPstCesionario = nngFacade.getRepresentantesLegales(
                        TipoContacto.OTROS_REPRESENTANTES_LEGALES, solicitud.getProveedorCesionario().getId());
            }

            // Si existen movimientos en el trámite no permitimos modificar los Proveedores
            if (!solicitud.getEstadoSolicitud().getCodigo().equals(EstadoSolicitud.SOLICITUD_CANCELADA)) {
                this.setTabHabilitado(solicitud.getCesionesSolicitadas().isEmpty());
            }

            // Habilitamos el botón de guardar
            this.habilitarSalvarBoton();

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Guarda los cambios realizados hasta el momento en el trámite.
     * @return 'True' si se ha podido guardar correctamente.
     */
    private boolean guardarCambios() {
        try {
            // Guardamos los cambios
            solicitud = nngFacade.saveSolicitudCesion(solicitud);

            // Actualizamos la solicitud para todos los tabs
            getWizard().setSolicitud(solicitud);
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

    /**
     * Valida la información introducida en el formulario.
     * @return 'True' si las validaciones son correctas.
     */
    private boolean isValido() {
        try {
            // PST Cedente y PST Cesionario son obligatorios
            if (solicitud.getProveedorSolicitante() == null || solicitud.getProveedorCesionario() == null) {
                MensajesBean.addErrorMsg(MSG_ID, "Es necesario seleccionar Cedente y Cesionario.", "");
                return false;
            }

            if (solicitud.getProveedorCesionario().getTipoProveedor().getCdg().equals(TipoProveedor.CONCESIONARIO)) {
                // El Cesionario Concesionario ha de tener BCD
                if (solicitud.getProveedorCesionario().getBcd() == null) {
                    MensajesBean.addErrorMsg(MSG_ID, "El Cesionario Concesionario ha de tener BCD.", "");
                    return false;
                }
            }

            // Todas las validaciones de proveedor cesionario se realizan en la query de búsqueda
            // de proveedores cesionarios en función del proveedor cedente.

            return true;
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
            return false;
        }
    }

    /** Habilita el botón de Guardar al introducir una referencia de solicitud. */
    public void habilitarSalvarBoton() {
        // Cedente, Cesionario y representantes legales obligatorios.
        salvarHabilitado = (
                (solicitud.getProveedorSolicitante() != null)
                        && solicitud.getProveedorCesionario() != null
                        && solicitud.getRepresentanteLegal() != null
                        && solicitud.getRepresentanteLegalCesionario() != null);
    }

    /**
     * Método invocado al salir de la modal de Edición de Proveedores para actualizar el formulario con los cambios.
     */
    public void actualizarEdicionProveedor() {
        try {
            // Se actualiza la lista de Proveedores Cedentes
            listaProveedoresCedentes = nngFacade.findAllProveedores();

            if (solicitud.getProveedorSolicitante() != null) {
                // Actualizamos el Proveedor Cedente Seleccionado
                int index = listaProveedoresCedentes.indexOf(solicitud.getProveedorSolicitante());
                solicitud.setProveedorSolicitante(listaProveedoresCedentes.get(index));

                // Se actualiza la Lista de Reprensentantes del Proveedor Cedente
                listaRepresentantesLegalesPstCedente = nngFacade.getRepresentantesLegales(
                        TipoContacto.REPRESENTANTE_LEGAL, solicitud.getProveedorSolicitante().getId());

                listaRepresentantesSuplentesPstCedente = nngFacade.getRepresentantesLegales(
                        TipoContacto.OTROS_REPRESENTANTES_LEGALES, solicitud.getProveedorSolicitante().getId());

                // Actualizamos el representante Legal seleccionado del Proveedor Cedente
                if (solicitud.getRepresentanteLegal() != null) {
                    index = listaRepresentantesLegalesPstCedente.indexOf(solicitud.getRepresentanteLegal());
                    solicitud.setRepresentanteLegal(listaRepresentantesLegalesPstCedente.get(index));
                }

                // Actualizamos el representante Suplente seleccionado del Proveedor Cedente
                if (solicitud.getRepresentanteSuplente() != null) {
                    index = listaRepresentantesSuplentesPstCedente.indexOf(solicitud.getRepresentanteSuplente());
                    solicitud.setRepresentanteSuplente(listaRepresentantesSuplentesPstCedente.get(index));
                }
            }

            if (solicitud.getProveedorCesionario() != null) {
                // Proveedores Cesionarios Disponibles
                listaProveedoresCesionarios = nngFacade.findAllCesionarios(solicitud.getProveedorSolicitante());

                // Actualizamos el Proveedor Cesionario Seleccionado
                int index = listaProveedoresCesionarios.indexOf(solicitud.getProveedorCesionario());
                solicitud.setProveedorCesionario(listaProveedoresCesionarios.get(index));

                // Se actualiza la Lista de Reprensentantes del Proveedor Cedente
                listaRepresentantesLegalesPstCesionario = nngFacade.getRepresentantesLegales(
                        TipoContacto.REPRESENTANTE_LEGAL, solicitud.getProveedorCesionario().getId());

                listaRepresentantesSuplentesPstCesionario = nngFacade.getRepresentantesLegales(
                        TipoContacto.OTROS_REPRESENTANTES_LEGALES, solicitud.getProveedorCesionario().getId());

                // Actualizamos el representante Legal seleccionado del Proveedor Cedente
                if (solicitud.getRepresentanteLegalCesionario() != null) {
                    index = listaRepresentantesLegalesPstCesionario
                            .indexOf(solicitud.getRepresentanteLegalCesionario());
                    solicitud.setRepresentanteLegalCesionario(listaRepresentantesLegalesPstCesionario.get(index));
                }

                // Actualizamos el representante Suplente seleccionado del Proveedor Cedente
                if (solicitud.getRepresentanteSuplenteCesionario() != null) {
                    index = listaRepresentantesSuplentesPstCesionario
                            .indexOf(solicitud.getRepresentanteSuplenteCesionario());
                    solicitud.setRepresentanteSuplenteCesionario(listaRepresentantesSuplentesPstCesionario.get(index));
                }
            }

            MensajesBean.addInfoMsg(MSG_ID, "Información de Proveedor y Representantes Legales actualizada", "");

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    // GETTERS & SETTERS

    /**
     * Información de la solicitud de cesión.
     * @return SolicitudCesionNng
     */
    public SolicitudCesionNng getSolicitud() {
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
     * Indica si se se ha de habilitar el botón de 'Guardar'.
     * @param salvarHabilitado boolean
     */
    public void setSalvarHabilitado(boolean salvarHabilitado) {
        this.salvarHabilitado = salvarHabilitado;
    }

    /**
     * Listado de Proveedores Cedentes.
     * @return List
     */
    public List<Proveedor> getListaProveedoresCedentes() {
        return listaProveedoresCedentes;
    }

    /**
     * Listado de Proveedores Cesionarios.
     * @return List
     */
    public List<Proveedor> getListaProveedoresCesionarios() {
        return listaProveedoresCesionarios;
    }

    /**
     * Muestra los campos de identificación de un Proveedor de Tipo 'Comercializadora'.
     * @return boolean
     */
    public boolean isPstComercializadora() {
        return pstComercializadora;
    }

    /**
     * Muestra los campos de identificación de un Proveedor de Tipo 'Concesionario'.
     * @return boolean
     */
    public boolean isPstConcesionario() {
        return pstConcesionario;
    }

    /**
     * Indica si se ha de expandir el panel de información de Proveedor Cedente.
     * @return boolean
     */
    public boolean isExpandirPstCedente() {
        return expandirPstCedente;
    }

    /**
     * Indica si se ha de expandir el panel de información de Proveedor Cedente.
     * @param expandirPstCedente boolean
     */
    public void setExpandirPstCedente(boolean expandirPstCedente) {
        this.expandirPstCedente = expandirPstCedente;
    }

    /**
     * Indica si se ha de expandir el panel de información de Represenatante Legal del Proveedor Cedente.
     * @return boolean
     */
    public boolean isExpandirRepLegalPstCedente() {
        return expandirRepLegalPstCedente;
    }

    /**
     * Indica si se ha de expandir el panel de información de Represenatante Legal del Proveedor Cedente.
     * @param expandirRepLegalPstCedente boolean
     */
    public void setExpandirRepLegalPstCedente(boolean expandirRepLegalPstCedente) {
        this.expandirRepLegalPstCedente = expandirRepLegalPstCedente;
    }

    /**
     * Indica si se ha de expandir el panel de información de Represenatante Suplente del Proveedor Cedente.
     * @return boolean
     */
    public boolean isExpandirRepSuplPstCedente() {
        return expandirRepSuplPstCedente;
    }

    /**
     * Indica si se ha de expandir el panel de información de Represenatante Suplente del Proveedor Cedente.
     * @param expandirRepSuplPstCedente boolean
     */
    public void setExpandirRepSuplPstCedente(boolean expandirRepSuplPstCedente) {
        this.expandirRepSuplPstCedente = expandirRepSuplPstCedente;
    }

    /**
     * Indica si se ha de expandir el panel de información de Proveedor Cesionario.
     * @return boolean
     */
    public boolean isExpandirPstCesionario() {
        return expandirPstCesionario;
    }

    /**
     * Indica si se ha de expandir el panel de información de Proveedor Cesionario.
     * @param expandirPstCesionario boolean
     */
    public void setExpandirPstCesionario(boolean expandirPstCesionario) {
        this.expandirPstCesionario = expandirPstCesionario;
    }

    /**
     * Indica si se ha de expandir el panel de información de Represenatante Legal del Proveedor Cesionario.
     * @return boolean
     */
    public boolean isExpandirRepLegalPstCesionario() {
        return expandirRepLegalPstCesionario;
    }

    /**
     * Indica si se ha de expandir el panel de información de Represenatante Legal del Proveedor Cesionario.
     * @param expandirRepLegalPstCesionario boolean
     */
    public void setExpandirRepLegalPstCesionario(boolean expandirRepLegalPstCesionario) {
        this.expandirRepLegalPstCesionario = expandirRepLegalPstCesionario;
    }

    /**
     * Indica si se ha de expandir el panel de información de Represenatante Suplente del Proveedor Cesionario.
     * @return boolean
     */
    public boolean isExpandirRepSuplPstCesionario() {
        return expandirRepSuplPstCesionario;
    }

    /**
     * Indica si se ha de expandir el panel de información de Represenatante Suplente del Proveedor Cesionario.
     * @param expandirRepSuplPstCesionario boolean
     */
    public void setExpandirRepSuplPstCesionario(boolean expandirRepSuplPstCesionario) {
        this.expandirRepSuplPstCesionario = expandirRepSuplPstCesionario;
    }

    /**
     * Lista de Representantes legales asociados al Proveedor Cedente.
     * @return List
     */
    public List<Contacto> getListaRepresentantesLegalesPstCedente() {
        return listaRepresentantesLegalesPstCedente;
    }

    /**
     * Lista de Representantes suplentes asociados al Proveedor Cedente.
     * @return List
     */
    public List<Contacto> getListaRepresentantesSuplentesPstCedente() {
        return listaRepresentantesSuplentesPstCedente;
    }

    /**
     * Lista de Representantes legales asociados al Proveedor Cesionario.
     * @return List
     */
    public List<Contacto> getListaRepresentantesLegalesPstCesionario() {
        return listaRepresentantesLegalesPstCesionario;
    }

    /**
     * Lista de Representantes suplentes asociados al Proveedor Cesionario.
     * @return List
     */
    public List<Contacto> getListaRepresentantesSuplentesPstCesionario() {
        return listaRepresentantesSuplentesPstCesionario;
    }
}
