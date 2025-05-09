package mx.ift.sns.web.backend.nng.asignacion;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mx.ift.sns.modelo.nng.SolicitudAsignacionNng;
import mx.ift.sns.modelo.nng.TipoAsignacion;
import mx.ift.sns.modelo.pst.Contacto;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.pst.TipoContacto;
import mx.ift.sns.modelo.pst.TipoProveedor;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.negocio.exceptions.RegistroModificadoException;
import mx.ift.sns.negocio.nng.INumeracionNoGeograficaFacade;
import mx.ift.sns.web.backend.ApplicationException;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.components.TabWizard;
import mx.ift.sns.web.backend.components.Wizard;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase que implementa la pestaña de Datos Generales de una asignacion NNG.
 * @author X36155QU
 */
public class GeneralesAsignacionNngTab extends TabWizard {

    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(GeneralesAsignacionNngTab.class);

    /** Id del campo de errores. */
    private static final String MSG_ID = "MSG_Generales";

    /** Servicio de Numeración No Geográfica. */
    private INumeracionNoGeograficaFacade nngFacade;

    /** Solicitud de asignación. */
    private SolicitudAsignacionNng solicitud;

    /** Lista de proveedores disponibles para seleccionar. */
    private List<Proveedor> listaProveedores;

    /** Lista de representantes legales del proveedor. */
    private List<Contacto> listaRepresentantes;

    /** Lista de representantes suplentes del proveedor. */
    private List<Contacto> listaSuplentes;

    /** Indica si se ha de expandir el panel de información de Proveedor. */
    private boolean expandirPST = false;

    /** Indica si se ha de expandir el panel de información de Represenatante Legal. */
    private boolean expandirRepresentante = false;

    /** Indica si se ha de expandir el panel de información del Represenatante suplente. */
    private boolean expandirSuplente = false;

    /** Indica si se se ha de habilitar el botón de 'Guardar'. */
    private boolean salvarHabilitado = false;

    /** Muestra los campos de identificación de un Proveedor de Tipo 'Comercializadora'. */
    private boolean pstComercializadora = false;

    /** Muestra los campos de identificación de un Proveedor de Tipo 'Concesionario'. */
    private boolean pstConcesionario = false;

    /** Indica si se puede modificar el tipo de asignacion. */
    private boolean modificarTipo = true;

    /** Lista de tipos de asignacion. */
    private List<TipoAsignacion> listaTipoAsignacion;

    /** Indica si existen movimientos para el trámite. */
    private boolean solicitudIniciada = false;

    /**
     * Constructor de la clase de respaldo para el tab/pestaña de 'Generales'.
     * @param pWizard Wizard
     * @param facade INumeracionNoGeograficaFacade
     */
    public GeneralesAsignacionNngTab(Wizard pWizard, INumeracionNoGeograficaFacade facade) {
        try {
            // Asociamos el Wizard padre a la pestaña que reprsenta este Tab
            setWizard(pWizard);
            setIdMensajes(MSG_ID);

            // Asociamos la solicitud que usaremos en todo el Wizard
            solicitud = (SolicitudAsignacionNng) getWizard().getSolicitud();

            // Asociamos el Servicio
            nngFacade = facade;

            // Iniciamos valores
            listaRepresentantes = new ArrayList<Contacto>(1);
            listaSuplentes = new ArrayList<Contacto>(1);

            // Cargamos el combo de proveedores
            listaProveedores = nngFacade.findAllProveedores();

            // Cargamos el combo de tipos de asigancion
            listaTipoAsignacion = nngFacade.findAllTipoAsignacion();

            expandirPST = false;
            expandirRepresentante = false;
            expandirSuplente = false;
            salvarHabilitado = false;
            pstComercializadora = false;
            pstConcesionario = false;

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new ApplicationException();
        }
    }

    @Override
    public boolean isAvanzar() {

        boolean r = false;

        // Si el tab no está habilitado para edición permitimos pasar
        // directamente a la siguiente pestaña.
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

        LOGGER.debug("r={}", r);

        return r;
    }

    @Override
    public void actualizaCampos() {
        try {
            solicitud = (SolicitudAsignacionNng) getWizard().getSolicitud();

            // Representantes
            if (solicitud.getProveedorSolicitante() != null) {
                listaRepresentantes = nngFacade.getRepresentantesLegales(
                        TipoContacto.REPRESENTANTE_LEGAL, solicitud.getProveedorSolicitante().getId());
                listaSuplentes = nngFacade.getRepresentantesLegales(
                        TipoContacto.OTROS_REPRESENTANTES_LEGALES, solicitud.getProveedorSolicitante().getId());

                // Referencia de Solicitud
                salvarHabilitado = (!StringUtils.isEmpty(this.solicitud.getReferencia()));

                // Tipo de Proveedor
                String tipoProveedor = solicitud.getProveedorSolicitante().getTipoProveedor().getCdg();
                pstComercializadora = (tipoProveedor.equals(TipoProveedor.COMERCIALIZADORA));
                pstConcesionario = ((tipoProveedor.equals(TipoProveedor.CONCESIONARIO))
                        || (tipoProveedor.equals(TipoProveedor.AMBOS)));

                // Bloque de información de Proveedor
                expandirPST = true;

                modificarTipo = solicitud.getNumeracionesSolicitadas().isEmpty()
                        || solicitud.getTipoAsignacion() == null;

            } else {
                pstComercializadora = false;
                pstConcesionario = false;
                salvarHabilitado = false;
                expandirPST = false;
            }

            // Deshabilitamos el combo de selección de proveedor si ya se han grabado movimientos.
            if (solicitud.getNumeracionesSolicitadas() != null) {
                solicitudIniciada = (!solicitud.getNumeracionesSolicitadas().isEmpty());
            }
            // Habilitamos el botón de guardar.
            this.habilitarSalvarBoton();

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    @Override
    public void resetTab() {
        solicitud = new SolicitudAsignacionNng();
        setGuardado(false);
        listaRepresentantes = new ArrayList<Contacto>(1);
        listaSuplentes = new ArrayList<Contacto>(1);
        expandirPST = false;
        expandirRepresentante = false;
        expandirSuplente = false;
        salvarHabilitado = false;
        pstComercializadora = false;
        pstConcesionario = false;
        modificarTipo = true;
        solicitudIniciada = false;
    }

    /**
     * Método invocado al salir de la modal de Edición de Proveedores para actualizar el formulario con los cambios.
     */
    public void actualizarEdicionProveedor() {
        try {
            // Se actualiza la lista de proveedores
            listaProveedores = nngFacade.findAllProveedores();

            if (solicitud.getProveedorSolicitante() != null) {
                // Actualizamos el Proveedor Seleccionado
                int index = listaProveedores.indexOf(solicitud.getProveedorSolicitante());
                solicitud.setProveedorSolicitante(listaProveedores.get(index));

                // Se actualiza la Lista de Reprensentantes
                listaRepresentantes = nngFacade.getRepresentantesLegales(
                        TipoContacto.REPRESENTANTE_LEGAL, solicitud.getProveedorSolicitante().getId());

                listaSuplentes = nngFacade.getRepresentantesLegales(
                        TipoContacto.OTROS_REPRESENTANTES_LEGALES, solicitud.getProveedorSolicitante().getId());

                // Actualizamos el representante Legal seleccionado
                if (solicitud.getRepresentanteLegal() != null) {
                    index = listaRepresentantes.indexOf(solicitud.getRepresentanteLegal());
                    solicitud.setRepresentanteLegal(listaRepresentantes.get(index));
                }

                // Actualizamos el representante Suplente seleccionado
                if (solicitud.getRepresentanteSuplente() != null) {
                    index = listaSuplentes.indexOf(solicitud.getRepresentanteSuplente());
                    solicitud.setRepresentanteSuplente(listaSuplentes.get(index));
                }
            }

            MensajesBean.addInfoMsg(MSG_ID, "Información de Proveedor y Representantes Legales actualizada", "");

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Acciones al seleccionar el proveedor.
     */
    public void seleccionProveedor() {
        if (validaProveedor()) {
            // Representantes
            listaRepresentantes = nngFacade.getRepresentantesLegales(
                    TipoContacto.REPRESENTANTE_LEGAL, solicitud.getProveedorSolicitante().getId());
            listaSuplentes = nngFacade.getRepresentantesLegales(
                    TipoContacto.OTROS_REPRESENTANTES_LEGALES, solicitud.getProveedorSolicitante().getId());

            // Tipo de Proveedor
            String tipoProveedor = solicitud.getProveedorSolicitante().getTipoProveedor().getCdg();
            pstComercializadora = (tipoProveedor.equals(TipoProveedor.COMERCIALIZADORA));
            pstConcesionario = ((tipoProveedor.equals(TipoProveedor.CONCESIONARIO))
                    || (tipoProveedor.equals(TipoProveedor.AMBOS)));

            // Bloque de información de Proveedor
            expandirPST = true;
            solicitud.setRepresentanteLegal(null);
            solicitud.setRepresentanteSuplente(null);

            habilitarSalvarBoton();
        } else {
            solicitud.setProveedorSolicitante(null);
        }
    }

    /** Método invocado al seleccionar un tipo de representante del combo de representantes legales. */
    public void seleccionRepresentante() {
        expandirRepresentante = (solicitud.getRepresentanteLegal() != null);
        expandirPST = false;
        habilitarSalvarBoton();

    }

    /** Método invocado al seleccionar un representante del combo de otros representantes. */
    public void seleccionSuplente() {
        expandirSuplente = (solicitud.getRepresentanteSuplente() != null);
        expandirPST = false;
        expandirRepresentante = false;
    }

    /**
     * Habilita el boton de guardar.
     */
    public void habilitarSalvarBoton() {
        salvarHabilitado = false;
        if (solicitud.getProveedorSolicitante() != null && solicitud.getFechaSolicitud() != null
                && solicitud.getReferencia() != null && solicitud.getTipoAsignacion() != null
                && solicitud.getRepresentanteLegal() != null) {
            if (!solicitud.getReferencia().trim().equals("")) {
                salvarHabilitado = true;
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
                if (solicitud.getEstadoSolicitud() == null) {
                    // Solicitud en trámite
                    EstadoSolicitud estado = new EstadoSolicitud();
                    estado.setCodigo(EstadoSolicitud.SOLICITUD_EN_TRAMITE);
                    solicitud.setEstadoSolicitud(estado);
                }
                solicitud = nngFacade.saveSolicitudAsignacion(solicitud);

                // Actualizamos la nueva instancia en el Wizard padre.
                getWizard().setSolicitud(solicitud);
                setGuardado(true);

                return true;
            } else {
                return false;
            }
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
     * Valida los datos de la pestaña.
     * @return true/false
     */
    private boolean validaGeneral() {

        boolean fechaCorrecta = true;
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

        return validaProveedor() && fechaCorrecta;
    }

    /**
     * Valida el proveedor seleccionado.
     * @return boolean
     */
    private boolean validaProveedor() {

        boolean proveedorCorrecto = true;

        if (solicitud.getProveedorSolicitante() != null) {
            if (solicitud.getProveedorSolicitante().getTipoProveedor().getCdg().equals(TipoProveedor.CONCESIONARIO)) {
                if (solicitud.getProveedorSolicitante().getBcd() == null) {
                    proveedorCorrecto = false;
                    MensajesBean.addErrorMsg(MSG_ID, "El proveedor seleccionado no tiene BCD", "");
                }
            } else if (solicitud.getProveedorSolicitante().getTipoProveedor().getCdg()
                    .equals(TipoProveedor.COMERCIALIZADORA)) {
                if (solicitud.getProveedorSolicitante().getIda() == null
                        || !nngFacade.existeConcesionarioConvenioConBcd(solicitud.getProveedorSolicitante())) {
                    MensajesBean.addErrorMsg(MSG_ID,
                            "El PST seleccionado no tiene IDA o no tiene un convenio con un concesionario con BCD", "");
                }
            } else if (solicitud.getProveedorSolicitante().getTipoProveedor().getCdg().equals(TipoProveedor.AMBOS)) {
                if (solicitud.getProveedorSolicitante().getBcd() == null) {
                    proveedorCorrecto = false;
                    MensajesBean.addErrorMsg(MSG_ID, "El proveedor seleccionado no tiene BCD", "");
                }
            }
        } else {
            proveedorCorrecto = false;
        }
        return proveedorCorrecto;

    }

    /**
     * @return the nngFacade
     */
    public INumeracionNoGeograficaFacade getNngFacade() {
        return nngFacade;
    }

    /**
     * @param nngFacade the nngFacade to set
     */
    public void setNngFacade(INumeracionNoGeograficaFacade nngFacade) {
        this.nngFacade = nngFacade;
    }

    /**
     * @return the listaProveedores
     */
    public List<Proveedor> getListaProveedores() {
        return listaProveedores;
    }

    /**
     * @param listaProveedores the listaProveedores to set
     */
    public void setListaProveedores(List<Proveedor> listaProveedores) {
        this.listaProveedores = listaProveedores;
    }

    /**
     * @return the listaRepresentantes
     */
    public List<Contacto> getListaRepresentantes() {
        return listaRepresentantes;
    }

    /**
     * @param listaRepresentantes the listaRepresentantes to set
     */
    public void setListaRepresentantes(List<Contacto> listaRepresentantes) {
        this.listaRepresentantes = listaRepresentantes;
    }

    /**
     * @return the solicitud
     */
    public SolicitudAsignacionNng getSolicitud() {
        return solicitud;
    }

    /**
     * @param solicitud the solicitud to set
     */
    public void setSolicitud(SolicitudAsignacionNng solicitud) {
        this.solicitud = solicitud;
    }

    /**
     * @return the listaSuplentes
     */
    public List<Contacto> getListaSuplentes() {
        return listaSuplentes;
    }

    /**
     * @param listaSuplentes the listaSuplentes to set
     */
    public void setListaSuplentes(List<Contacto> listaSuplentes) {
        this.listaSuplentes = listaSuplentes;
    }

    /**
     * @return the expandirPST
     */
    public boolean isExpandirPST() {
        return expandirPST;
    }

    /**
     * @param expandirPST the expandirPST to set
     */
    public void setExpandirPST(boolean expandirPST) {
        this.expandirPST = expandirPST;
    }

    /**
     * @return the expandirRepresentante
     */
    public boolean isExpandirRepresentante() {
        return expandirRepresentante;
    }

    /**
     * @param expandirRepresentante the expandirRepresentante to set
     */
    public void setExpandirRepresentante(boolean expandirRepresentante) {
        this.expandirRepresentante = expandirRepresentante;
    }

    /**
     * @return the expandirSuplente
     */
    public boolean isExpandirSuplente() {
        return expandirSuplente;
    }

    /**
     * @param expandirSuplente the expandirSuplente to set
     */
    public void setExpandirSuplente(boolean expandirSuplente) {
        this.expandirSuplente = expandirSuplente;
    }

    /**
     * @return the salvarHabilitado
     */
    public boolean isSalvarHabilitado() {
        return salvarHabilitado;
    }

    /**
     * @param salvarHabilitado the salvarHabilitado to set
     */
    public void setSalvarHabilitado(boolean salvarHabilitado) {
        this.salvarHabilitado = salvarHabilitado;
    }

    /**
     * @return the pstComercializadora
     */
    public boolean isPstComercializadora() {
        return pstComercializadora;
    }

    /**
     * @param pstComercializadora the pstComercializadora to set
     */
    public void setPstComercializadora(boolean pstComercializadora) {
        this.pstComercializadora = pstComercializadora;
    }

    /**
     * @return the pstConcesionario
     */
    public boolean isPstConcesionario() {
        return pstConcesionario;
    }

    /**
     * @param pstConcesionario the pstConcesionario to set
     */
    public void setPstConcesionario(boolean pstConcesionario) {
        this.pstConcesionario = pstConcesionario;
    }

    /**
     * @return the listaTipoAsignacion
     */
    public List<TipoAsignacion> getListaTipoAsignacion() {
        return listaTipoAsignacion;
    }

    /**
     * @param listaTipoAsignacion the listaTipoAsignacion to set
     */
    public void setListaTipoAsignacion(List<TipoAsignacion> listaTipoAsignacion) {
        this.listaTipoAsignacion = listaTipoAsignacion;
    }

    /**
     * @return the modificarTipo
     */
    public boolean isModificarTipo() {
        return modificarTipo;
    }

    /**
     * @param modificarTipo the modificarTipo to set
     */
    public void setModificarTipo(boolean modificarTipo) {
        this.modificarTipo = modificarTipo;
    }

    /**
     * Indica si existen movimientos para el trámite.
     * @return the solicitudIniciada
     */
    public boolean isSolicitudIniciada() {
        return solicitudIniciada;
    }

}
