package mx.ift.sns.web.backend.nng.liberacion;

import java.util.ArrayList;
import java.util.List;

import mx.ift.sns.modelo.nng.SolicitudLiberacionNng;
import mx.ift.sns.modelo.pst.Contacto;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.pst.TipoContacto;
import mx.ift.sns.modelo.pst.TipoProveedor;
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

/** Clase de soporte para la pestaña de 'Generales' para el trámite de Liberaciones NNG en los Wizard. */
public class GeneralesLiberacionNngTab extends TabWizard {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(GeneralesLiberacionNngTab.class);

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Id del campo de errores. */
    private static final String MSG_ID = "MSG_GeneralesLiberacion";

    /** Servicio de Numeración Geográfica. */
    private INumeracionNoGeograficaFacade nngFacade;

    /** Lista de proveedores disponibles para seleccionar. */
    private List<Proveedor> listaProveedores;

    /** Información de la petición de Solicitud. */
    private SolicitudLiberacionNng solicitud;

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

    /** Indica si se ha de mostrar el CheckBox de Liberación IFT. */
    private boolean mostrarLiberacionIft = true;

    /** Indica si es una liberación solicitada por IFT. */
    private boolean liberacionIft = false;

    /** Indica si existen movimientos para el trámite. */
    private boolean solicitudIniciada = false;

    /**
     * Constructor de la clase de respaldo para el tab/pestaña de 'Generales'.
     * @param pWizard Referencia a la clase Wizard que instancia este Tab
     * @param pNngFacade Facade de Servicios de Numeración No Geográfica
     */
    public GeneralesLiberacionNngTab(Wizard pWizard, INumeracionNoGeograficaFacade pNngFacade) {
        // Asociamos el Wizard padre a la pestaña que reprsenta este Tab
        setWizard(pWizard);
        setIdMensajes(MSG_ID);

        // Asociamos la solicitud que usaremos en todo el Wizard
        solicitud = (SolicitudLiberacionNng) getWizard().getSolicitud();

        // Asociamos el Facade de Servicios.
        nngFacade = pNngFacade;
    }

    @Override
    public void cargaValoresIniciales() {
        try {
            // Inicializaciones
            listaRepresentantes = new ArrayList<Contacto>(1);
            listaSuplentes = new ArrayList<Contacto>(1);

            // Catálogo de Proveedores
            listaProveedores = nngFacade.findAllProveedoresActivos();

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
            expandirPST = false;
            expandirRepresentante = false;
            expandirSuplente = false;
            salvarHabilitado = false;
            pstComercializadora = false;
            pstConcesionario = false;
            listaRepresentantes.clear();
            listaSuplentes.clear();
            liberacionIft = false;
            setGuardado(false);
            solicitudIniciada = false;
        }
    }

    /** Método invocado al seleccionar un proveedor del combo de proveedores. */
    public void seleccionProveedor() {
        try {
            if (solicitud.getProveedorSolicitante() != null) {

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
                pstComercializadora = false;
                pstConcesionario = false;
                salvarHabilitado = false;
                expandirPST = false;
            }

            // Bloque de información de representante
            expandirRepresentante = false;
            expandirSuplente = false;

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
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

    /** Habilita el botón de Guardar. */
    public void habilitarSalvarBoton() {
        salvarHabilitado = false;
        if (solicitud.getProveedorSolicitante() != null
                && solicitud.getFechaSolicitud() != null
                && solicitud.getReferencia() != null
                && solicitud.getRepresentanteLegal() != null) {

            if (!solicitud.getReferencia().trim().equals("")) {
                salvarHabilitado = true;
            }
        }
    }

    /**
     * Método que válida generales.
     * @return boolean valida
     */
    public boolean isValido() {
        try {
            // Provedor Concesionario/Ambos ha de tener ABC
            if (!solicitud.getProveedorSolicitante().getTipoProveedor().getCdg()
                    .equals(TipoProveedor.COMERCIALIZADORA)) {
                if (solicitud.getProveedorSolicitante().getBcd() == null) {
                    MensajesBean.addErrorMsg(MSG_ID,
                            MensajesBean.getTextoResource("manual.generales.error.pst.bcd"), "");
                    return false;
                }
            }

            // Si el PST comercializadora no tiene IDA o no tiene un convenio con un PST concesionario que tenga ABC,
            // el SNS no debe permitir seleccionarlo para el trámite
            if (solicitud.getProveedorSolicitante().getTipoProveedor().getCdg()
                    .equals(TipoProveedor.COMERCIALIZADORA)) {
                if (solicitud.getProveedorSolicitante().getIda() == null) {
                    MensajesBean.addErrorMsg(MSG_ID,
                            MensajesBean.getTextoResource("manual.generales.error.pst.ida"), "");
                    return false;
                }

                if (!nngFacade.existeConcesionarioConvenioConBcd(solicitud.getProveedorSolicitante())) {
                    MensajesBean.addErrorMsg(MSG_ID,
                            MensajesBean.getTextoResource("manual.generales.error.pstComer.convenioBCD"), "");
                    return false;
                }
            }

            // Fecha de Solicitud requerida.
            if (solicitud.getFechaSolicitud() == null) {
                MensajesBean.addErrorMsg(MSG_ID,
                        MensajesBean.getTextoResource("manual.generales.error.fechasol.required"), "");
                return false;
            }

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

    /**
     * Guarda los cambios realizados hasta el momento en el trámite.
     * @return 'True' si se ha podido guardar correctamente.
     */
    private boolean guardarCambios() {
        if (isValido()) {
            try {
                // Solicitud en trámite
                EstadoSolicitud estado = new EstadoSolicitud();
                estado.setCodigo(EstadoSolicitud.SOLICITUD_EN_TRAMITE);
                solicitud.setEstadoSolicitud(estado);

                if (solicitud.getTipoSolicitud() == null) {
                    TipoSolicitud tipoSol = new TipoSolicitud();
                    tipoSol.setCdg(TipoSolicitud.LIBERACION_NNG);
                    solicitud.setTipoSolicitud(tipoSol);
                }

                // Guardamos la Solicitud
                solicitud.setLiberacionIft(liberacionIft);
                solicitud = nngFacade.saveSolicitudLiberacion(solicitud);

                // Actualizamos la nueva instancia en el Wizard padre.
                getWizard().setSolicitud(solicitud);
                setGuardado(true);
                return true;

            } catch (RegistroModificadoException rme) {
                MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0015);
                return false;
            } catch (Exception e) {
                LOGGER.error("Error inesperado", e);
                MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
                return false;
            }
        } else {
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
        try {
            solicitud = (SolicitudLiberacionNng) getWizard().getSolicitud();
            if (solicitud.isLiberacionIft() != null) {
                liberacionIft = solicitud.isLiberacionIft();
            }

            if (solicitud.getProveedorSolicitante() != null) { // Representantes
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

            } else {
                pstComercializadora = false;
                pstConcesionario = false;
                salvarHabilitado = false;
                expandirPST = false;
            }

            // Deshabilitamos el combo de selección de proveedor si ya se han grabado movimientos.
            if (solicitud.getLiberacionesSolicitadas() != null) {
                solicitudIniciada = (!solicitud.getLiberacionesSolicitadas().isEmpty());
            }

            // Habilitamos el botón de guardar.
            this.habilitarSalvarBoton();

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
            listaProveedores = nngFacade.findAllProveedoresActivos();

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

    // GETTERS & SETTERS

    /**
     * Lista de proveedores disponibles para seleccionar.
     * @return Catálogo de proveedores
     */
    public List<Proveedor> getListaProveedores() {
        return listaProveedores;
    }

    /**
     * Información de la petición de Solicitud.
     * @return Petición de solicitud genérica
     */
    public SolicitudLiberacionNng getSolicitud() {
        return solicitud;
    }

    /**
     * Lista de representantes legales del proveedor.
     * @return Catálogo de contactos asociado al Proveedor
     */
    public List<Contacto> getListaRepresentantes() {
        return listaRepresentantes;
    }

    /**
     * Lista de representantes suplentes del proveedor.
     * @return Catálogo de contactos asociado al Proveedor
     */
    public List<Contacto> getListaSuplentes() {
        return listaSuplentes;
    }

    /**
     * Indica si se ha de expandir el panel de información de Proveedor.
     * @return True si el bloque ha de estar expandido
     */
    public boolean isExpandirPST() {
        return expandirPST;
    }

    /**
     * Indica si se ha de expandir el panel de información de Proveedor.
     * @param expandirPST True si el bloque ha de estar expandido
     */
    public void setExpandirPST(boolean expandirPST) {
        this.expandirPST = expandirPST;
    }

    /**
     * Indica si se ha de expandir el panel de información de Represenatante Legal.
     * @return True si el bloque ha de estar expandido
     */
    public boolean isExpandirRepresentante() {
        return expandirRepresentante;
    }

    /**
     * Indica si se ha de expandir el panel de información de Represenatante Legal.
     * @param expandirRepresentante True si el bloque ha de estar expandido
     */
    public void setExpandirRepresentante(boolean expandirRepresentante) {
        this.expandirRepresentante = expandirRepresentante;
    }

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
     * Muestra los campos de identificación de un Proveedor de Tipo 'Comercializadora'.
     * @return True se se han de mostrar la información IDA del Proveedor
     */
    public boolean isPstComercializadora() {
        return pstComercializadora;
    }

    /**
     * Muestra los campos de identificación de un Proveedor de Tipo 'Comercializadora'.
     * @param pstComercializadora True se se han de mostrar la información IDA del Proveedor
     */
    public void setPstComercializadora(boolean pstComercializadora) {
        this.pstComercializadora = pstComercializadora;
    }

    /**
     * Muestra los campos de identificación de un Proveedor de Tipo 'Concesionario'.
     * @return True se se han de mostrar la información IDO, ABC, BCD del Proveedor
     */
    public boolean isPstConcesionario() {
        return pstConcesionario;
    }

    /**
     * Muestra los campos de identificación de un Proveedor de Tipo 'Concesionario'.
     * @param pstConcesionario True se se han de mostrar la información IDO, ABC, BCD del Proveedor
     */
    public void setPstConcesionario(boolean pstConcesionario) {
        this.pstConcesionario = pstConcesionario;
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
     * Indica si se ha de mostrar el CheckBox de Liberación IFT.
     * @return boolean
     */
    public boolean isMostrarLiberacionIft() {
        return mostrarLiberacionIft;
    }

    /**
     * Indica si es una liberación solicitada por IFT.
     * @return boolean
     */
    public boolean isLiberacionIft() {
        return liberacionIft;
    }

    /**
     * Indica si es una liberación solicitada por IFT.
     * @param liberacionIft boolean
     */
    public void setLiberacionIft(boolean liberacionIft) {
        this.liberacionIft = liberacionIft;
    }

    /**
     * Indica si existen movimientos para el trámite.
     * @return the solicitudIniciada
     */
    public boolean isSolicitudIniciada() {
        return solicitudIniciada;
    }

}
