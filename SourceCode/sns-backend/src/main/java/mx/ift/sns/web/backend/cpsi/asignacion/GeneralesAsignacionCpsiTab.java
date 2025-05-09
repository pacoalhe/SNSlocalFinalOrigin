package mx.ift.sns.web.backend.cpsi.asignacion;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mx.ift.sns.modelo.cpsi.SolicitudAsignacionCpsi;
import mx.ift.sns.modelo.pst.Contacto;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.pst.TipoContacto;
import mx.ift.sns.modelo.pst.TipoProveedor;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.modelo.solicitud.TipoSolicitud;
import mx.ift.sns.negocio.cpsi.ICodigoCPSIFacade;
import mx.ift.sns.negocio.exceptions.RegistroModificadoException;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.components.TabWizard;
import mx.ift.sns.web.backend.components.Wizard;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Clase de soporte para la pestaña de 'Generales' de Solicitudes de Asignación CPSI. */
public class GeneralesAsignacionCpsiTab extends TabWizard {

    /** Logger de la clase. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(GeneralesAsignacionCpsiTab.class);

    /** Id del campo de errores. */
    private static final String MSG_ID = "MSG_GeneralesCPSI";

    /** Información de la petición de Solicitud. */
    private SolicitudAsignacionCpsi solicitud;

    /** Servicio de Códigos Nacionales. */
    private ICodigoCPSIFacade codigoCpsiFacade;

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

    /** Indica si existen movimientos para el trámite. */
    private boolean solicitudIniciada = false;

    /**
     * Constructor de la clase de respaldo para el tab/pestaña de 'Generales'.
     * @param pWizard Wizard
     * @param pCodigoCpsiFacade Facade de Servicios de CPSI.
     */
    public GeneralesAsignacionCpsiTab(Wizard pWizard, ICodigoCPSIFacade pCodigoCpsiFacade) {
        // Asociamos el Wizard padre a la pestaña que reprsenta este Tab
        setWizard(pWizard);
        setIdMensajes(MSG_ID);

        // Asociamos la solicitud que usaremos en todo el Wizard
        solicitud = (SolicitudAsignacionCpsi) getWizard().getSolicitud();

        // Asociamos el Facade de servicios
        codigoCpsiFacade = pCodigoCpsiFacade;
    }

    @Override
    public void cargaValoresIniciales() {
        try {
            // Cargamos el combo de proveedores
            listaProveedores = codigoCpsiFacade.findAllProveedores();

            // Iniciamos valores
            listaRepresentantes = new ArrayList<>(1);
            listaSuplentes = new ArrayList<>(1);
            expandirPST = false;
            expandirRepresentante = false;
            expandirSuplente = false;
            salvarHabilitado = false;
            pstComercializadora = false;
            pstConcesionario = false;
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
            this.setSummaryError(Errores.getDescripcionError(Errores.ERROR_0004));
        }
    }

    @Override
    public void resetTab() {
        if (this.isIniciado()) {
            solicitud = null;
            this.setGuardado(false);
            listaRepresentantes.clear();
            listaSuplentes.clear();
            expandirPST = false;
            expandirRepresentante = false;
            expandirSuplente = false;
            salvarHabilitado = false;
            pstComercializadora = false;
            pstConcesionario = false;
            solicitudIniciada = false;
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
            solicitud = (SolicitudAsignacionCpsi) getWizard().getSolicitud();

            // Habilitamos el Botón de Guardar
            this.habilitarSalvarBoton();

            // Representantes
            if (solicitud.getProveedorSolicitante() != null) {
                listaRepresentantes = codigoCpsiFacade.getRepresentantesLegales(
                        TipoContacto.REPRESENTANTE_LEGAL, solicitud.getProveedorSolicitante().getId());
                listaSuplentes = codigoCpsiFacade.getRepresentantesLegales(
                        TipoContacto.OTROS_REPRESENTANTES_LEGALES, solicitud.getProveedorSolicitante().getId());

                // Tipo de Proveedor
                String tipoProveedor = solicitud.getProveedorSolicitante().getTipoProveedor().getCdg();
                pstComercializadora = (tipoProveedor.equals(TipoProveedor.COMERCIALIZADORA));
                pstConcesionario = ((tipoProveedor.equals(TipoProveedor.CONCESIONARIO))
                        || (tipoProveedor.equals(TipoProveedor.AMBOS)));

                // Bloque de información de Proveedor
                expandirPST = true;

            } else {
                pstComercializadora = false;
                pstConcesionario = false;
                salvarHabilitado = false;
                expandirPST = false;
            }

            // Deshabilitamos el combo de selección de proveedor si ya se han grabado movimientos.
            if (solicitud.getCpsiAsignados() != null) {
                solicitudIniciada = (!solicitud.getCpsiAsignados().isEmpty());
            }

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Método invocado al salir de la modal de Edición de Proveedores para actualizar el formulario con los cambios.
     */
    public void actualizarEdicionProveedor() {
        try {
            // Se actualiza la lista de proveedores
            listaProveedores = codigoCpsiFacade.findAllProveedores();

            if (solicitud.getProveedorSolicitante() != null) {
                // Actualizamos el Proveedor Seleccionado
                int index = listaProveedores.indexOf(solicitud.getProveedorSolicitante());
                solicitud.setProveedorSolicitante(listaProveedores.get(index));

                // Se actualiza la Lista de Reprensentantes
                listaRepresentantes = codigoCpsiFacade.getRepresentantesLegales(
                        TipoContacto.REPRESENTANTE_LEGAL, solicitud.getProveedorSolicitante().getId());

                listaSuplentes = codigoCpsiFacade.getRepresentantesLegales(
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

    /** Acciones al seleccionar el proveedor. */
    public void seleccionProveedor() {
        // Representantes
        listaRepresentantes = codigoCpsiFacade.getRepresentantesLegales(
                TipoContacto.REPRESENTANTE_LEGAL, solicitud.getProveedorSolicitante().getId());
        listaSuplentes = codigoCpsiFacade.getRepresentantesLegales(
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

    /** Habilita el boton de guardar. */
    public void habilitarSalvarBoton() {
        salvarHabilitado = false;
        if (solicitud.getProveedorSolicitante() != null && solicitud.getFechaSolicitud() != null
                && solicitud.getReferencia() != null && solicitud.getRepresentanteLegal() != null) {
            if (!StringUtils.isEmpty(this.solicitud.getReferencia())) {
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
                // Solicitud en trámite
                EstadoSolicitud estado = new EstadoSolicitud();
                estado.setCodigo(EstadoSolicitud.SOLICITUD_EN_TRAMITE);
                solicitud.setEstadoSolicitud(estado);

                // Tipo de Solicitud de Cesión
                if (solicitud.getTipoSolicitud() == null) {
                    TipoSolicitud tipoSol = new TipoSolicitud();
                    tipoSol.setCdg(TipoSolicitud.ASIGNACION_CPSI);
                    solicitud.setTipoSolicitud(tipoSol);
                }

                // Guardamos la Solicitud
                if (solicitud.getNumCpsiSolicitados() == null) {
                    solicitud.setNumCpsiSolicitados(new Integer(0));
                }
                solicitud = codigoCpsiFacade.saveSolicitudAsignacion(solicitud);

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

        if (solicitud.getReferencia() == null || solicitud.getReferencia().trim().isEmpty()) {
            MensajesBean.addErrorMsg(MSG_ID,
                    MensajesBean.getTextoResource("manual.generales.error.referencia.required"), "");
            campoReferenciaInformado = false;
        }
        return (fechaCorrecta && campoReferenciaInformado);
    }

    // GETTERS & SETTERS

    /**
     * Indica si se ha de expandir el panel de información de Proveedor.
     * @return boolean
     */
    public boolean isExpandirPST() {
        return expandirPST;
    }

    /**
     * Indica si se ha de expandir el panel de información de Proveedor.
     * @param expandirPST boolean
     */
    public void setExpandirPST(boolean expandirPST) {
        this.expandirPST = expandirPST;
    }

    /**
     * Indica si se ha de expandir el panel de información de Represenatante Legal.
     * @return boolean
     */
    public boolean isExpandirRepresentante() {
        return expandirRepresentante;
    }

    /**
     * Indica si se ha de expandir el panel de información de Represenatante Legal.
     * @param expandirRepresentante boolean
     */
    public void setExpandirRepresentante(boolean expandirRepresentante) {
        this.expandirRepresentante = expandirRepresentante;
    }

    /**
     * Indica si se ha de expandir el panel de información del Represenatante suplente.
     * @return boolean
     */
    public boolean isExpandirSuplente() {
        return expandirSuplente;
    }

    /**
     * Indica si se ha de expandir el panel de información del Represenatante suplente.
     * @param expandirSuplente boolean
     */
    public void setExpandirSuplente(boolean expandirSuplente) {
        this.expandirSuplente = expandirSuplente;
    }

    /**
     * Información de la petición de Solicitud.
     * @return SolicitudAsignacionCpsi
     */
    public SolicitudAsignacionCpsi getSolicitud() {
        return solicitud;
    }

    /**
     * Lista de proveedores disponibles para seleccionar.
     * @return List<Proveedor>
     */
    public List<Proveedor> getListaProveedores() {
        return listaProveedores;
    }

    /**
     * Lista de representantes legales del proveedor.
     * @return List<Contacto>
     */
    public List<Contacto> getListaRepresentantes() {
        return listaRepresentantes;
    }

    /**
     * Lista de representantes suplentes del proveedor.
     * @return List<Contacto>
     */
    public List<Contacto> getListaSuplentes() {
        return listaSuplentes;
    }

    /**
     * Indica si se se ha de habilitar el botón de 'Guardar'.
     * @return boolean
     */
    public boolean isSalvarHabilitado() {
        return salvarHabilitado;
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
     * Indica si existen movimientos para el trámite.
     * @return the solicitudIniciada
     */
    public boolean isSolicitudIniciada() {
        return solicitudIniciada;
    }

}
