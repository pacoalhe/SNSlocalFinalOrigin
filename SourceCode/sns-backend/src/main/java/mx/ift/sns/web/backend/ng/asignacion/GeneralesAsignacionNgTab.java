package mx.ift.sns.web.backend.ng.asignacion;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import mx.ift.sns.modelo.lineas.ReporteLineasActivas;
import mx.ift.sns.modelo.ng.SolicitudAsignacion;
import mx.ift.sns.modelo.pst.Contacto;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.pst.TipoContacto;
import mx.ift.sns.modelo.pst.TipoProveedor;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.modelo.solicitud.TipoSolicitud;
import mx.ift.sns.negocio.exceptions.RegistroModificadoException;
import mx.ift.sns.negocio.ng.INumeracionGeograficaService;
import mx.ift.sns.utils.date.FechasUtils;
import mx.ift.sns.web.backend.ApplicationException;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.components.TabWizard;
import mx.ift.sns.web.backend.components.Wizard;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Clase de soporte para la pestaña de 'Generales' en los Wizard. */
public class GeneralesAsignacionNgTab extends TabWizard {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(GeneralesAsignacionNgTab.class);

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Id del campo de errores. */
    private static final String MSG_ID = "MSG_Generales";

    /** Servicio de Numeración Geográfica. */
    private INumeracionGeograficaService ngService;

    /** Lista de proveedores disponibles para seleccionar. */
    private List<Proveedor> listaProveedores;

    /** Información de la petición de Solicitud. */
    private SolicitudAsignacion solicitud;

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

    /** Indica si existen movimientos para el trámite. */
    private boolean solicitudIniciada = false;

    /**
     * Constructor de la clase de respaldo para el tab/pestaña de 'Generales'.
     * @param pWizard Referencia a la clase Wizard que instancia este Tab
     * @param pNgService Servicio de Numeración Geográfica
     * @throws Exception error
     */
    public GeneralesAsignacionNgTab(Wizard pWizard, INumeracionGeograficaService pNgService) throws Exception {
        try {
            // Asociamos el Wizard padre a la pestaña que reprsenta este Tab
            setWizard(pWizard);
            setIdMensajes(MSG_ID);

            solicitud = (SolicitudAsignacion) pWizard.getSolicitud();

            // Asociamos el Servicio
            ngService = pNgService;

            // Inicializaciones
            listaRepresentantes = new ArrayList<Contacto>(1);
            listaSuplentes = new ArrayList<Contacto>(1);

            // Catálogo de Proveedores
            listaProveedores = ngService.findAllProveedoresActivos();

            expandirPST = false;
            expandirRepresentante = false;
            expandirSuplente = false;
            salvarHabilitado = false;
            pstComercializadora = false;
            pstConcesionario = false;
        } catch (Exception e) {
            throw new ApplicationException();
        }
    }

    @Override
    public void resetTab() {
        expandirPST = false;
        expandirRepresentante = false;
        expandirSuplente = false;
        salvarHabilitado = false;
        pstComercializadora = false;
        pstConcesionario = false;
        listaRepresentantes = null;
        listaSuplentes = null;
        solicitud = new SolicitudAsignacion();
        setGuardado(false);
        solicitudIniciada = false;
    }

    /** Método invocado al seleccionar un proveedor del combo de proveedores. */
    public void seleccionProveedor() {
        try {
            if (solicitud.getProveedorSolicitante() != null) {
                // Comprobamos la fecha del ultimo reporte del Proveedor
                Calendar fechaHoy = Calendar.getInstance();
                fechaHoy.add(Calendar.MONTH, -2);

                ReporteLineasActivas ultimoReporte =
                        ngService.getLastReporteLineasActiva(solicitud.getProveedorSolicitante());

                if (ultimoReporte != null) {
                    Calendar fechaReporte = Calendar.getInstance();
                    fechaReporte.setTime(ultimoReporte.getSolicitudLineasActivas().getFechaSolicitud());
                    if (fechaReporte.before(fechaHoy)) {
                        MensajesBean.addWarningMsg(MSG_ID,
                                "Último reporte de lineas activas de "
                                        + solicitud.getProveedorSolicitante().getNombre()
                                        + " hecho el " + fechaReporte.get(Calendar.DAY_OF_MONTH) + "/"
                                        + (fechaReporte.get(Calendar.MONTH) + 1) + "/"
                                        + fechaReporte.get(Calendar.YEAR), "");
                    }
                } else {
                    MensajesBean.addWarningMsg(MSG_ID,
                            "El proveedor seleccionado no ha realizado ningun reporte de lineas activas todavía.", "");
                }

                // Representantes
                listaRepresentantes = ngService.getRepresentantesLegales(
                        TipoContacto.REPRESENTANTE_LEGAL, solicitud.getProveedorSolicitante().getId());
                listaSuplentes = ngService.getRepresentantesLegales(
                        TipoContacto.OTROS_REPRESENTANTES_LEGALES, solicitud.getProveedorSolicitante().getId());

                habilitarSalvarBoton();

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

        } catch (Exception ex) {
            LOGGER.error("error", ex);

        }
    }

    /** Método invocado al seleccionar un tipo de representante del combo de representantes legales. */
    public void seleccionRepresentante() {
        expandirRepresentante = (solicitud.getRepresentanteLegal() != null);
        expandirPST = false;
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
        if (solicitud.getProveedorSolicitante() != null && solicitud.getFechaSolicitud() != null
                && solicitud.getReferencia() != null) {
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
            Date fechaHoy = FechasUtils.getFechaHoy();

            boolean datosPnn = true;
            if (!solicitud.getProveedorSolicitante().getTipoProveedor().getCdg()
                    .equals(TipoProveedor.COMERCIALIZADORA)) {
                if (solicitud.getProveedorSolicitante().getIdo() == null) {
                    MensajesBean.addErrorMsg(MSG_ID,
                            MensajesBean.getTextoResource("manual.generales.error.pst.ido"), "");
                    datosPnn = false;
                }
            } else {
                if (solicitud.getProveedorSolicitante().getIda() == null) {
                    MensajesBean.addErrorMsg(MSG_ID,
                            MensajesBean.getTextoResource("manual.generales.error.pst.ida"), "");
                    datosPnn = false;
                }
            }
            if (!datosPnn) {
                LOGGER.debug("no valido");
                return false;
            }
            if (solicitud.getFechaSolicitud() != null) {
                if (solicitud.getFechaSolicitud().after(fechaHoy)) {
                    MensajesBean.addErrorMsg(MSG_ID,
                            MensajesBean.getTextoResource("manual.generales.error.fechasol.menor"), "");
                    return false;
                }
            } else {
                MensajesBean.addErrorMsg(MSG_ID,
                        MensajesBean.getTextoResource("manual.generales.error.fechasol.required"), "");
                return false;
            }

            if ((solicitud.getFechaIniPruebas() != null) && (solicitud.getFechaIniPruebas().before(fechaHoy))) {
                MensajesBean.addErrorMsg(MSG_ID,
                        MensajesBean.getTextoResource("manual.generales.error.fechainipru.mayor"), "");
                return false;
            }

            if ((solicitud.getFechaIniUtilizacion() != null)
                    && (solicitud.getFechaIniPruebas() != null)
                    && (solicitud.getFechaIniPruebas().after(solicitud.getFechaIniUtilizacion()))) {

                MensajesBean.addErrorMsg(MSG_ID,
                        MensajesBean.getTextoResource("manual.generales.error.fechainiutil.mayor"), "");
                return false;
            }

            LOGGER.debug("valido");
            // Si ha pasado todas las validaciones
            return true;

        } catch (Exception e) {
            LOGGER.error("Se ha producido un error al validar generales: ", e);
            MensajesBean.addErrorMsg(MSG_ID, MensajesBean.getTextoResource("manual.generales.error.valida"), "");
            return false;
        }
    }

    /**
     * Boton de salvar datos del provvedor.
     * @return boolean valida
     */
    public boolean guardarCambios() {
        if (isValido()) {
            try {
                // Solicitud en trámite
                EstadoSolicitud estado = new EstadoSolicitud();
                estado.setCodigo(EstadoSolicitud.SOLICITUD_EN_TRAMITE);
                solicitud.setEstadoSolicitud(estado);

                // Tipo de Solicitud de Asignación
                if (solicitud.getTipoSolicitud() == null) {
                    TipoSolicitud tipoSol = new TipoSolicitud();
                    tipoSol.setCdg(TipoSolicitud.ASIGNACION);
                    solicitud.setTipoSolicitud(tipoSol);
                }

                // Guardamos la Solicitud
                solicitud = ngService.saveSolicitudAsignacion(solicitud);

                // Actualizamos la nueva instancia en el Wizard padre.
                getWizard().setSolicitud(solicitud);

                StringBuilder sBuilder = new StringBuilder();
                sBuilder.append(MensajesBean.getTextoResource("manual.generales.exito.guardar")).append(" ");
                sBuilder.append(solicitud.getId());
                MensajesBean.addInfoMsg(MSG_ID, sBuilder.toString(), "");

                setGuardado(true);
                return true;
            } catch (RegistroModificadoException rme) {
                MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0015);
                return false;
            } catch (Exception e) {
                LOGGER.error("Se ha producido un error al guardar generales ", e);
                MensajesBean
                        .addErrorMsg(MSG_ID, MensajesBean.getTextoResource("manual.generales.error.guardar"), "");
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean isAvanzar() {

        boolean r = false;

        // Si el tab no está habilitado para edición permitimos pasar
        // directamente a la siguiente pestaña.
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

        LOGGER.debug("r={}", r);

        return r;
    }

    @Override
    public void actualizaCampos() {
        solicitud = (SolicitudAsignacion) getWizard().getSolicitud();
        try {

            if (solicitud.getProveedorSolicitante() != null) { // Representantes
                listaRepresentantes = ngService.getRepresentantesLegales(
                        TipoContacto.REPRESENTANTE_LEGAL, solicitud.getProveedorSolicitante().getId());
                listaSuplentes = ngService.getRepresentantesLegales(
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
            if (solicitud.getNumeracionSolicitadas() != null) {
                solicitudIniciada = (!solicitud.getNumeracionSolicitadas().isEmpty());
            }

            // Habilitamos el botón de guardar.
            this.habilitarSalvarBoton();

        } catch (Exception e) {
            LOGGER.error("Se ha producido un error al cargar la solicitud", e);
            MensajesBean
                    .addErrorMsg(MSG_ID, "Se ha producido un error al cargar la solicitud", "");
        }
    }

    /**
     * Método invocado al salir de la modal de Edición de Proveedores para actualizar el formulario con los cambios.
     */
    public void actualizarEdicionProveedor() {
        try {
            // Se actualiza la lista de proveedores
            listaProveedores = ngService.findAllProveedoresActivos();

            if (solicitud.getProveedorSolicitante() != null) {
                // Actualizamos el Proveedor Seleccionado
                int index = listaProveedores.indexOf(solicitud.getProveedorSolicitante());
                solicitud.setProveedorSolicitante(listaProveedores.get(index));

                // Se actualiza la Lista de Reprensentantes
                listaRepresentantes = ngService.getRepresentantesLegales(
                        TipoContacto.REPRESENTANTE_LEGAL, solicitud.getProveedorSolicitante().getId());

                listaSuplentes = ngService.getRepresentantesLegales(
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
    public SolicitudAsignacion getSolicitud() {
        return solicitud;
    }

    /**
     * Información de la petición de Solicitud.
     * @param solicitud the solicitud to set
     */
    public void setSolicitud(SolicitudAsignacion solicitud) {
        this.solicitud = solicitud;
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
