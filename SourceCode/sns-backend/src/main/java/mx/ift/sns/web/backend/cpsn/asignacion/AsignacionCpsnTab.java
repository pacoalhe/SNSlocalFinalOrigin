package mx.ift.sns.web.backend.cpsn.asignacion;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mx.ift.sns.modelo.cps.EstatusAsignacionCps;
import mx.ift.sns.modelo.cpsn.CodigoCPSN;
import mx.ift.sns.modelo.cpsn.EstatusCPSN;
import mx.ift.sns.modelo.cpsn.EstudioEquipoCPSN;
import mx.ift.sns.modelo.cpsn.NumeracionAsignadaCpsn;
import mx.ift.sns.modelo.cpsn.NumeracionSolicitadaCpsn;
import mx.ift.sns.modelo.cpsn.SolicitudAsignacionCpsn;
import mx.ift.sns.modelo.cpsn.TipoBloqueCPSN;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.negocio.cpsn.ICodigoCPSNFacade;
import mx.ift.sns.negocio.exceptions.RegistroModificadoException;
import mx.ift.sns.utils.date.FechasUtils;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.components.TabWizard;
import mx.ift.sns.web.backend.components.Wizard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Clase de soporte para la pestaña de 'Asignación' de Solicitudes de Asignación CPSN. */
public class AsignacionCpsnTab extends TabWizard {

    /** Serial uid. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AsignacionCpsnTab.class);

    /** Id de mensajes de error. */
    private static final String MSG_ID = "MSG_AsignacionCPSN";

    /** Servicio de Códigos de CPSN. */
    private ICodigoCPSNFacade cpsnFacade;

    /** Campo solicitud asignación. */
    private SolicitudAsignacionCpsn solicitud;

    /** Datos del estudio de equipos de señalización. */
    private List<EstudioEquipoCPSN> listadoCantidades;

    /** Indica si los códigos CPSN han sido asignados. */
    private boolean bloquesAsignados = false;

    /** Número de días para fija la fecha de inicio de utilización. */
    private String diasAplicacion = "0";

    /**
     * Constructor de la clase de respaldo para el tab/pestaña de 'Asignación'.
     * @param pBeanPadre Referencia a la clase Wizard que instancia este Tab
     * @param pCpsnFacade Facade de Codigos CPSN
     * @throws Exception En caso de error.
     */
    public AsignacionCpsnTab(Wizard pBeanPadre, ICodigoCPSNFacade pCpsnFacade) {

        // Asociamos el Wizard padre a la pestaña que reprsenta este Tab
        setWizard(pBeanPadre);
        setIdMensajes(MSG_ID);

        // Asociamos la solicitud que usaremos en todo el Wizard
        solicitud = (SolicitudAsignacionCpsn) getWizard().getSolicitud();

        // Asociamos el Facade de servicios
        cpsnFacade = pCpsnFacade;

        listadoCantidades = new ArrayList<EstudioEquipoCPSN>();
    }

    @Override
    public void resetTab() {
        this.solicitud = null;
        this.bloquesAsignados = false;
        this.listadoCantidades = new ArrayList<EstudioEquipoCPSN>();
        this.diasAplicacion = "0";
    }

    /** Método invocado al pulsar el botón de 'Asignar'. */
    public void asignarBloques() {
        try {
            if (validaCodigos()) {
                // Asignamos los códigos seleccionados.
                solicitud = cpsnFacade.applyAsignacionesSolicitadas(solicitud);

                // Actualizamos la solicitud para todos los tabs
                getWizard().setSolicitud(solicitud);

                // Deshabilitamos el botón de 'Asignar'
                bloquesAsignados = true;
            }
        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.getDescripcionError(Errores.ERROR_0015), "");
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.getDescripcionError(Errores.ERROR_0004), "");
        }
    }

    /**
     * Comprueba que los códigos seleccionados para asignar sigan estando disponibles.
     * @return True si los códigos son válidos para asignación.
     * @throws Exception en caso de error.
     */
    private boolean validaCodigos() throws Exception {
        // Dado que la pestaña de análisis tiene acceso al catálogo, se puede dar el caso de haber seleccionado
        // codigos y posteriormente haberlos reservado desde el catálogo antes de haber sido asignados.
        ArrayList<String> avisos = new ArrayList<>(solicitud.getNumeracionAsignadas().size());
        for (NumeracionAsignadaCpsn cpsnAsig : solicitud.getNumeracionAsignadas()) {

            // Proveedor nulo por que se supone que están libres.
            CodigoCPSN cpsn = cpsnFacade.getCodigoCpsn(
                    cpsnAsig.getTipoBloqueCpsn().getId(), cpsnAsig.getIdCpsn(), null);

            if (cpsn == null) {
                StringBuilder sbAviso = new StringBuilder();
                sbAviso.append("Binario: ").append(cpsnAsig.getBinario());
                sbAviso.append(", Decimal: ").append(cpsnAsig.getIdCpsn());
                avisos.add(sbAviso.toString());
            }

            // El CPSN existe libre o asignado al proveedor solicitante
            if (cpsn != null) {
                // Comprobamos que el CPSN esté ya asignado al Solicitante o esté libre para poder ser asignado.
                // Si el Código está asignado a otro Proveedor (por un trámite posterior) se ignora también.
                if (!cpsn.getEstatusCPSN().getId().equals(EstatusCPSN.LIBRE)) {
                    if (!cpsn.getEstatusCPSN().getId().equals(EstatusCPSN.ASIGNADO)) {
                        // Estado no permitido.
                        StringBuilder sbAviso = new StringBuilder();
                        sbAviso.append("Binario: ").append(cpsnAsig.getBinario());
                        sbAviso.append(", Decimal: ").append(cpsnAsig.getIdCpsn());
                        sbAviso.append(", Estatus: ").append(cpsn.getEstatusCPSN().getDescripcion());
                        avisos.add(sbAviso.toString());
                    } else {
                        // Estado no permitido.
                        StringBuilder sbAviso = new StringBuilder();
                        sbAviso.append("Binario: ").append(cpsnAsig.getBinario());
                        sbAviso.append(", Decimal: ").append(cpsnAsig.getIdCpsn());
                        sbAviso.append(", Estatus: ").append(cpsn.getEstatusCPSN().getDescripcion());
                        sbAviso.append(", Proveedor: ").append(cpsn.getProveedor().getNombre());
                        avisos.add(sbAviso.toString());
                    }
                }
            }
        }

        if (!avisos.isEmpty()) {
            StringBuilder sbAvisos = new StringBuilder();
            sbAvisos.append("Los siguientes códigos han sido modificados y no pueden ser asignados:<br>");
            for (String aviso : avisos) {
                sbAvisos.append(aviso).append("<br>");
            }
            MensajesBean.addErrorMsg(MSG_ID, sbAvisos.toString(), "");
        }

        return avisos.isEmpty();
    }

    /** Método invocado al modificar el campo 'Fecha de Asignación'. */
    public void validarFechaAsignacion() {
        Date fHoy = FechasUtils.getFechaHoy();
        if (solicitud.getFechaAsignacion() != null) {
            if (solicitud.getFechaAsignacion().after(fHoy)) {
                MensajesBean.addErrorMsg(MSG_ID, "La fecha de Asignación no puede ser superior al día actual", "");
                solicitud.setFechaAsignacion(fHoy);
            }
            // Actualizamos los días de aplicación y la fecha de inicio de utilización.
            this.diasAplicacionChange();
        } else {
            MensajesBean.addErrorMsg(MSG_ID, "La fecha de Asignación no puede ser nula", "");
            solicitud.setFechaAsignacion(fHoy);
        }
    }

    /** Recalcula la fecha de inicio de utilización. */
    public void diasAplicacionChange() {
        if (solicitud.getFechaAsignacion() != null) {
            // Las validaciones client-side nos aseguran que llegue un valor numérico positivo.
            int diasAplicacion = Integer.parseInt(this.diasAplicacion);

            solicitud.setFechaIniUtilizacion(FechasUtils.calculaFecha(
                    solicitud.getFechaAsignacion(), diasAplicacion, 0, 0));
        }
    }

    /** Método invocado al modificar el campo 'Fecha de Inicio de Utilización'. */
    public void validarFechaInicioUtilizacion() {
        if (solicitud.getFechaAsignacion() != null) {
            if (solicitud.getFechaIniUtilizacion() != null) {
                if (solicitud.getFechaIniUtilizacion().before(solicitud.getFechaAsignacion())) {
                    MensajesBean.addErrorMsg(MSG_ID,
                            "La fecha de inicio de utilización no puede ser inferior a la fecha de asignación.", "");
                    this.diasAplicacionChange();
                } else {
                    int diasEntre = (int) FechasUtils.getDiasEntre(
                            solicitud.getFechaAsignacion(),
                            solicitud.getFechaIniUtilizacion());
                    solicitud.setDiasAplicacion(diasEntre);
                }
            } else {
                // Ponemos la fecha de inicio por defecto en función de la fecha de asignación.
                this.diasAplicacionChange();
            }
        } else {
            MensajesBean.addErrorMsg(MSG_ID, "La fecha de Asignación no puede ser nula", "");
            this.diasAplicacionChange();
        }
    }

    @Override
    public boolean isAvanzar() {
        boolean resultado = true;
        if (this.isTabHabilitado()) {
            resultado = bloquesAsignados;
            if (!bloquesAsignados) {
                MensajesBean.addErrorMsg(MSG_ID, "Es necesario confirmar la asignación de bloques solicitados", "");
            } else {
                resultado = resultado && this.guardarCambios();
            }
        }

        if (!resultado) {
            // Al no permitirse avanzar se muestra en el Wizard el mensaje indicado por el TabWizard
            setMensajeError("No es posible continuar");
        }
        return resultado;
    }

    @Override
    public void actualizaCampos() {
        try {
            // Asociamos la solicitud que usaremos en todo el Wizard
            solicitud = (SolicitudAsignacionCpsn) getWizard().getSolicitud();

            if (solicitud.getDiasAplicacion() != null) {
                diasAplicacion = String.valueOf(solicitud.getDiasAplicacion());
            } else {
                diasAplicacion = "0";
            }

            if (solicitud.getNumeracionAsignadas() != null) {
                // Comprobamos si existen códigos asignados pendientes.
                if (!solicitud.getEstadoSolicitud().getCodigo().equals(EstadoSolicitud.SOLICITUD_CANCELADA)) {
                    bloquesAsignados = true;
                    for (NumeracionAsignadaCpsn cpsnAsig : solicitud.getNumeracionAsignadas()) {
                        if (cpsnAsig.getEstatus().getCodigo().equals(EstatusAsignacionCps.PENDIENTE)) {
                            bloquesAsignados = false;
                            break;
                        }
                    }
                }
            }

            listadoCantidades = new ArrayList<EstudioEquipoCPSN>();
            EstudioEquipoCPSN tipoBloque = null;
            int cantSoli2048 = 0;
            int cantSoli128 = 0;
            int cantSoli8 = 0;
            int cantSoliIndv = 0;
            int cantSelec2048 = 0;
            int cantSelec128 = 0;
            int cantSelec8 = 0;
            int cantSelecIndv = 0;
            boolean existeBloq2048 = false;
            boolean existeBloq128 = false;
            boolean existeBloq8 = false;
            boolean existeBloqIndv = false;

            for (NumeracionSolicitadaCpsn numSoli : solicitud.getNumeracionSolicitadas()) {
                if (numSoli.getTipoBloque().getId().equals(TipoBloqueCPSN.BLOQUE_2048)) {
                    cantSoli2048 = cantSoli2048 + numSoli.getCantSolicitada().intValue();
                    cantSelec2048 = cantidadSeleccionada(numSoli.getTipoBloque().getId());
                    existeBloq2048 = true;
                } else if (numSoli.getTipoBloque().getId().equals(TipoBloqueCPSN.BLOQUE_128)) {
                    cantSoli128 = cantSoli128 + numSoli.getCantSolicitada().intValue();
                    cantSelec128 = cantidadSeleccionada(numSoli.getTipoBloque().getId());
                    existeBloq128 = true;
                } else if (numSoli.getTipoBloque().getId().equals(TipoBloqueCPSN.BLOQUE_8)) {
                    cantSoli8 = cantSoli8 + numSoli.getCantSolicitada().intValue();
                    cantSelec8 = cantidadSeleccionada(numSoli.getTipoBloque().getId());
                    existeBloq8 = true;
                } else if (numSoli.getTipoBloque().getId().equals(TipoBloqueCPSN.INDIVIDUAL)) {
                    cantSoliIndv = cantSoliIndv + numSoli.getCantSolicitada().intValue();
                    cantSelecIndv = cantidadSeleccionada(numSoli.getTipoBloque().getId());
                    existeBloqIndv = true;
                }
            }

            if (existeBloq2048) {
                tipoBloque = new EstudioEquipoCPSN();
                tipoBloque.setTipoBloque(TipoBloqueCPSN.TXT_BLOQUE_2048);
                tipoBloque.setCantidadSolicitada(cantSoli2048);
                tipoBloque.setCantidadSeleccionada(cantSelec2048);
                listadoCantidades.add(tipoBloque);
            }
            if (existeBloq128) {
                tipoBloque = new EstudioEquipoCPSN();
                tipoBloque.setTipoBloque(TipoBloqueCPSN.TXT_BLOQUE_128);
                tipoBloque.setCantidadSolicitada(cantSoli128);
                tipoBloque.setCantidadSeleccionada(cantSelec128);
                listadoCantidades.add(tipoBloque);
            }
            if (existeBloq8) {
                tipoBloque = new EstudioEquipoCPSN();
                tipoBloque.setTipoBloque(TipoBloqueCPSN.TXT_BLOQUE_8);
                tipoBloque.setCantidadSolicitada(cantSoli8);
                tipoBloque.setCantidadSeleccionada(cantSelec8);
                listadoCantidades.add(tipoBloque);
            }
            if (existeBloqIndv) {
                tipoBloque = new EstudioEquipoCPSN();
                tipoBloque.setTipoBloque(TipoBloqueCPSN.TXT_BLOQUE_INDIVIDUAL);
                tipoBloque.setCantidadSolicitada(cantSoliIndv);
                tipoBloque.setCantidadSeleccionada(cantSelecIndv);
                listadoCantidades.add(tipoBloque);
            }

        } catch (Exception e) {
            MensajesBean.addErrorMsg(MSG_ID, "Ha ocurrido un error al cargar la pestaña de asignacion");
        }
    }

    /**
     * Guarda los cambios realizados hasta el momento en el trámite.
     * @return 'True' si se ha podido guardar correctamente.
     */
    private boolean guardarCambios() {
        try {
            // Días de aplicación.
            solicitud.setDiasAplicacion(Integer.parseInt(this.diasAplicacion));

            // Guardamos los cambios
            solicitud = cpsnFacade.saveSolicitudAsignacion(solicitud);

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
     * Método para obtener las cantidades seleccionadas dependiendo del tipo de bloque.
     * @param tipoBloque tipoBloque
     * @return int
     */
    private int cantidadSeleccionada(String tipoBloque) {
        int cantidad = 0;
        for (NumeracionAsignadaCpsn numAsig : solicitud.getNumeracionAsignadas()) {
            if (numAsig.getTipoBloqueCpsn().getId().equals(tipoBloque)) {
                cantidad = cantidad + 1;
            }
        }
        return cantidad;
    }

    /**
     * Información de la solicitud.
     * @return SolicitudAsignacionCpsn
     */
    public SolicitudAsignacionCpsn getSolicitud() {
        return solicitud;
    }

    /**
     * Identificador de los datos del estudio de equipos de señalización.
     * @return List<EstudioEquipoCPSN>
     */
    public List<EstudioEquipoCPSN> getListadoCantidades() {
        return listadoCantidades;
    }

    /**
     * Identificador de los datos del estudio de equipos de señalización.
     * @param listadoCantidades List<EstudioEquipoCPSN>
     */
    public void setListadoCantidades(List<EstudioEquipoCPSN> listadoCantidades) {
        this.listadoCantidades = listadoCantidades;
    }

    /**
     * Indica si los códigos CPSN han sido asignados.
     * @return boolean
     */
    public boolean isBloquesAsignados() {
        return bloquesAsignados;
    }

    /**
     * Indica si los códigos CPSN han sido asignados.
     * @param bloquesAsignados boolean
     */
    public void setBloquesAsignados(boolean bloquesAsignados) {
        this.bloquesAsignados = bloquesAsignados;
    }

    /**
     * Número de días para fija la fecha de inicio de utilización.
     * @return String
     */
    public String getDiasAplicacion() {
        return diasAplicacion;
    }

    /**
     * Número de días para fija la fecha de inicio de utilización.
     * @param diasAplicacion String
     */
    public void setDiasAplicacion(String diasAplicacion) {
        this.diasAplicacion = diasAplicacion;
    }

}
