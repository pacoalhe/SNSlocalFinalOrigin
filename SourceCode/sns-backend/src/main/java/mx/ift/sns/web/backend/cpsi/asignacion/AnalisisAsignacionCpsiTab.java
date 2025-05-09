package mx.ift.sns.web.backend.cpsi.asignacion;

import java.util.ArrayList;
import java.util.List;

import mx.ift.sns.modelo.cps.EstatusAsignacionCps;
import mx.ift.sns.modelo.cpsi.CodigoCPSI;
import mx.ift.sns.modelo.cpsi.CpsiAsignado;
import mx.ift.sns.modelo.cpsi.EstatusCPSI;
import mx.ift.sns.modelo.cpsi.Linea1EstudioCPSI;
import mx.ift.sns.modelo.cpsi.SolicitudAsignacionCpsi;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.negocio.PeticionCancelacion;
import mx.ift.sns.negocio.cpsi.ICodigoCPSIFacade;
import mx.ift.sns.negocio.exceptions.RegistroModificadoException;
import mx.ift.sns.utils.date.FechasUtils;
import mx.ift.sns.web.backend.ac.cpsi.ConsultarCodigosCPSIBean;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.components.TabWizard;
import mx.ift.sns.web.backend.components.Wizard;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Clase de soporte para la pestaña de 'Análisis' de Solicitudes de Asignación CPSI. */
public class AnalisisAsignacionCpsiTab extends TabWizard {

    /** Logger de la clase. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AnalisisAsignacionCpsiTab.class);

    /** Id del campo de errores. */
    private static final String MSG_ID = "MSG_AnalisisCPSI";

    /** Información de la petición de Solicitud. */
    private SolicitudAsignacionCpsi solicitud;

    /** Servicio de Códigos Nacionales. */
    private ICodigoCPSIFacade codigoCpsiFacade;

    /** Listado de la tabla de resúmen de análisis de CPSI de Proveedor. */
    private List<Linea1EstudioCPSI> listadoDetalleAnalisisPst;

    /** Listado de Códigos CPSI. */
    private List<CodigoCPSI> listadoCodigos;

    /** Listado de Códigos CPSI seleccionados. */
    private List<CodigoCPSI> codigosSeleccionados;

    /** Lista de Asignaciones Solicitadas. */
    private List<CpsiAsignado> listaAsignaciones;

    /** CPSI Asignado seleccionado. */
    private CpsiAsignado cpsiAsignadoSelecciondo;

    /** Indica si el boton de seleccionar está habilitado. */
    private boolean seleccionarHabilitado = false;

    // /** Bean asociado al Catálogo de CPSI. */
    // private ConsultarCodigosCPSIBean catalogoCpsiBean;

    /** Indica el número de códigos solicitados por el usuario. */
    private String codigosSolicitados = "1";

    /**
     * Constructor de la clase de respaldo para el tab/pestaña de 'Análisis'.
     * @param pWizard Wizard
     * @param pCodigoCpsiFacade Facade de Servicios de CPSI.
     * @param pCatalogoCpsiBean Bean asociado al Catálogo de CPSI.
     */
    public AnalisisAsignacionCpsiTab(Wizard pWizard, ICodigoCPSIFacade pCodigoCpsiFacade,
            ConsultarCodigosCPSIBean pCatalogoCpsiBean) {
        try {
            // Asociamos el Wizard padre a la pestaña que reprsenta este Tab
            setWizard(pWizard);
            setIdMensajes(MSG_ID);

            // Asociamos la solicitud que usaremos en todo el Wizard
            solicitud = (SolicitudAsignacionCpsi) getWizard().getSolicitud();

            // Asociamos el Facade de servicios
            codigoCpsiFacade = pCodigoCpsiFacade;

            // Asociamos el Bean de Catálogo de CPSI.
            // catalogoCpsiBean = pCatalogoCpsiBean;

            // Inicializaciones
            listadoCodigos = new ArrayList<CodigoCPSI>();
            listaAsignaciones = new ArrayList<CpsiAsignado>();
            codigosSeleccionados = new ArrayList<CodigoCPSI>();

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    @Override
    public void resetTab() {
        this.listadoDetalleAnalisisPst = null;
        this.solicitud = null;
        this.listadoCodigos.clear();
        this.codigosSeleccionados.clear();
        this.listaAsignaciones.clear();
        this.seleccionarHabilitado = false;
        this.codigosSolicitados = "1";
    }

    /**
     * Guarda los cambios realizados hasta el momento en el trámite.
     * @return 'True' si se ha podido guardar correctamente.
     */
    private boolean guardarCambios() {
        try {
            // Lista de Asignaciones Solicitadas a eliminar permanentemente
            ArrayList<CpsiAsignado> asigSolEliminar = new ArrayList<CpsiAsignado>();

            // Eliminamos de la solicitud las Asignaciones que se han eliminado de la tabla de resumen.
            for (CpsiAsignado cpsiAsig : solicitud.getCpsiAsignados()) {
                if (cpsiAsig.getEstatus().getCodigo().equals(EstatusAsignacionCps.PENDIENTE)) {
                    if ((!arrayContains(listaAsignaciones, cpsiAsig.getIdCpsi().intValue()))) {
                        // No podemos eliminar una asignación solicitada de la lista mientras estamos
                        // recorriendo la misma lista (ConcurrentModificationException).
                        // Por lo tanto la añadimos a otra lista para eliminarla al salir del bucle.
                        asigSolEliminar.add(cpsiAsig);
                    }
                }
            }

            // Eliminamos de la solicitud los CpsiAsignado que se han eliminado de la tabla de resumen.
            for (CpsiAsignado cpsiAsig : asigSolEliminar) {
                // Al tener marcadas las liberacionesSolicitadas como @PrivateOwned en JPA se eliminan automáticamente
                // cuando se guardan los cambios en la solicitud ya que indica que nadie más las utiliza.
                solicitud.removeCpsiAsignado(cpsiAsig);
            }

            // Agregamos las asignaciones que se han añadido
            for (CpsiAsignado cpsiAsig : listaAsignaciones) {
                if (!arrayContains(solicitud.getCpsiAsignados(), cpsiAsig.getIdCpsi().intValue())) {
                    solicitud.addCpsiAsignado(cpsiAsig);
                }
            }

            // Agreamos valores de asignación por defecto
            if (solicitud.getFechaAsignacion() == null) {
                solicitud.setFechaAsignacion(FechasUtils.getFechaHoy());
                solicitud.setDiasAplicacion(20); // 20 Por Defecto
                solicitud.setFechaIniUtilizacion(FechasUtils.calculaFecha(
                        solicitud.getFechaAsignacion(), solicitud.getDiasAplicacion(), 0, 0));
            }

            // Agregamos la cantidad de códigos solicitados por el usuario. El validador de enteros de la parte
            // Client-Side se encarga de que lleguen valores correctos.
            if (!StringUtils.isEmpty(codigosSolicitados)) {
                solicitud.setNumCpsiSolicitados(Integer.parseInt(codigosSolicitados));
            } else {
                solicitud.setNumCpsiSolicitados(0);
            }

            // Guardamos los cambios y generamos los nuevos registros
            solicitud = codigoCpsiFacade.saveSolicitudAsignacion(solicitud);

            // Actualizamos la solicitud para todos los tabs
            getWizard().setSolicitud(solicitud);

            // Una vez persistidas las asignaciones rehacemos la tabla de asignaciones para que todas
            // tengan el id ya generado
            listaAsignaciones.clear();
            listaAsignaciones.addAll(solicitud.getCpsiAsignados());
            return true;

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

    @Override
    public void actualizaCampos() {
        // Reseteamos el Tab por si se va y viene desde los botones de navegación del wizard.
        this.resetTab();

        // La solicitud del Wizard ha cambiado de instancia desde que se generó en
        // el constructor. Es necesario actualizar la referecnia en el tab.
        solicitud = (SolicitudAsignacionCpsi) getWizard().getSolicitud();

        // Actualizamos las solicitudes pendientes
        listaAsignaciones.clear();
        if (solicitud.getCpsiAsignados() != null) {
            listaAsignaciones.addAll(solicitud.getCpsiAsignados());
        }

        // Cantidad de codigos solicitados.
        if (solicitud.getNumCpsiSolicitados() != null) {
            codigosSolicitados = String.valueOf(solicitud.getNumCpsiSolicitados());
        } else {
            codigosSolicitados = "1";
        }
    }

    /** Método invocado al pulsar el botón de 'Generar Análisis'. */
    public void generarAnalisis() {
        try {
            // Controlamos si es nulo para cuando se genera el análisis automáticamente al cerrar el catálogo.
            if (!StringUtils.isEmpty(codigosSolicitados) && (!codigosSolicitados.equals("0"))) {
                // Estudio de CPSI del Proveedor.
                Linea1EstudioCPSI estudioCpsi =
                        codigoCpsiFacade.getEstudioCpsiProveedor(solicitud.getProveedorSolicitante());

                listadoDetalleAnalisisPst = new ArrayList<Linea1EstudioCPSI>(1);
                listadoDetalleAnalisisPst.add(estudioCpsi);

                // Listado de Códigos CPSI (los asignados “A” al PST de la solicitud y todos los que no estén asignados)
                listadoCodigos = codigoCpsiFacade.findAllCodigosCPSIForAnalisis(solicitud.getProveedorSolicitante());
                codigosSeleccionados.clear();
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Método invocado al pulsar el botón de 'Catálogo CPSI'. */
    public void abrirCatalogo() {
        try {
            // Preseleccionamos el Proveedor Solicitante en la búsqueda de códigos al abrir el catálogo.
            // catalogoCpsiBean.setProveedorSeleccionado(this.solicitud.getProveedorSolicitante());
            // catalogoCpsiBean.realizarBusqueda();

            // Mostramos la modal. No podemos usar el oncomplete en el componente JSF ya que el validator de
            // TXT_Cantidad impide que se actualice el valor de 'codigosSolicitados' y el botón no se deshabilita.
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('MDL_CatalogoCpsiPst').show()");
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Método invocado al pulsar el botón de 'Selección'. */
    public void seleccionCodigos() {
        try {
            for (CodigoCPSI codigo : codigosSeleccionados) {
                if (!arrayContains(listaAsignaciones, codigo.getId().intValue())) {
                    listaAsignaciones.add(this.getCpsiAsignadoFromCodigoCpsi(codigo));
                }
            }
            // codigosSeleccionados.clear();
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Método invocado al pulsar el botón de Eliminar' en la tabla de Asignaciones. */
    public void eliminarAsignacion() {
        try {
            PeticionCancelacion checkCancelacion = codigoCpsiFacade.cancelAsignacion(cpsiAsignadoSelecciondo, true);
            if (checkCancelacion.isCancelacionPosible()) {

                if (cpsiAsignadoSelecciondo.getEstatus().getCodigo().equals(EstatusAsignacionCps.ASIGNADO)) {
                    // Si la Asignación ya se había ejecutado marcamos la asignación como cancelada.
                    EstatusAsignacionCps estatusCancelada = new EstatusAsignacionCps();
                    estatusCancelada.setCodigo(EstatusAsignacionCps.CANCELADO);
                    cpsiAsignadoSelecciondo.setEstatus(estatusCancelada);
                } else {
                    // Si la asignación estaba como "Pendiente" directamente se elimina.
                    listaAsignaciones.remove(cpsiAsignadoSelecciondo);
                }

                // Actualizamos la solicitud
                this.guardarCambios();

                if (solicitud.getEstadoSolicitud().getCodigo().equals(EstadoSolicitud.SOLICITUD_TERMINADA)) {
                    // Si todas las asignaciones están canceladas el trámite pasa a estar cancelado también.
                    boolean asignacionesCanceladas = true;
                    for (CpsiAsignado cpsiAsig : solicitud.getCpsiAsignados()) {
                        asignacionesCanceladas = asignacionesCanceladas
                                && (cpsiAsig.getEstatus().getCodigo().equals(EstatusAsignacionCps.CANCELADO));
                    }

                    // Cambiamos el estado de la solicitud
                    if (asignacionesCanceladas) {
                        EstadoSolicitud statusCancelada = new EstadoSolicitud();
                        statusCancelada.setCodigo(EstadoSolicitud.SOLICITUD_CANCELADA);
                        solicitud.setEstadoSolicitud(statusCancelada);

                        MensajesBean.addInfoMsg(MSG_ID, "Se han cancelado todas las asignaciones."
                                + " El trámite pasa a estado 'Cancelado'", "");

                        // Guardamos los cambios
                        solicitud = codigoCpsiFacade.saveSolicitudAsignacion(solicitud);
                        getWizard().setSolicitud(solicitud);
                    }
                }
            } else {
                MensajesBean.addErrorMsg(MSG_ID, checkCancelacion.getMensajeError(), "");
            }
        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0015);
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Método invocado al seleccionar un código de la tabla de CPSI. */
    public void seleccionarCodigo() {
        try {
            if (!codigosSeleccionados.isEmpty()) {
                seleccionarHabilitado = true;
                for (CodigoCPSI codigo : codigosSeleccionados) {
                    if (!codigo.getEstatus().getId().equals(EstatusCPSI.LIBRE)) {
                        seleccionarHabilitado = false;
                        MensajesBean.addErrorMsg(MSG_ID,
                                MensajesBean.getTextoResource("asignacion.cpsi.tab.analisis.error.estatus"), "");
                        break;
                    }
                }
            } else {
                seleccionarHabilitado = false;
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    @Override
    public boolean isAvanzar() {
        boolean resultado = true;
        if (this.isTabHabilitado()) {
            // Continuamos siempre y cuando haya asignaciones por procesar
            if (listaAsignaciones.isEmpty()) {
                MensajesBean.addErrorMsg(MSG_ID,
                        MensajesBean.getTextoResource("asignacion.cpsi.tab.analisis.error.almenosUna"), "");
                resultado = false;
            } else {
                resultado = this.guardarCambios();
            }
        } else {
            resultado = true;
        }

        if (!resultado) {
            // Al no permitirse avanzar se muestra en el Wizard el mensaje indicado por el TabWizard
            this.setSummaryError(MensajesBean.getTextoResource("error.avanzar"));
        }
        return resultado;
    }

    /**
     * Crea un objeto CpsiAsignado con la información de un código CPSI.
     * @param pCodigo Informaciónd el Código CPSI.
     * @return objeto CpsiAsignado con la información de un código CPSI.
     */
    private CpsiAsignado getCpsiAsignadoFromCodigoCpsi(CodigoCPSI pCodigo) {
        CpsiAsignado ca = new CpsiAsignado();
        ca.setBinario(pCodigo.getBinario());
        ca.setIdCpsi(pCodigo.getId());
        ca.setSolicitudAsignacion(this.solicitud);

        // Estatus
        EstatusAsignacionCps eacps = new EstatusAsignacionCps();
        eacps.setCodigo(EstatusAsignacionCps.PENDIENTE);
        eacps.setDescripcion(EstatusAsignacionCps.TXT_PENDIENTE);
        ca.setEstatus(eacps);

        // Cargamos la variable 'Formato Decimal'
        ca.getFormatoDecimal();

        return ca;
    }

    /**
     * Indica si se se ha de habilitar el botón de 'Guardar'.
     * @return boolean
     */
    public boolean isSalvarHabilitado() {
        return (!listaAsignaciones.isEmpty());
    }

    /**
     * Indica si una lista contiene un CPSI Asignado sin usar los métos Equals de Object.
     * @param list Lista de objetos CpsiAsignado
     * @param pCpsiId Identificador de código CPSI.
     * @return True si existe un objeto CpsiAsignado con el Id del CPSI esta en la lista
     * @throws Exception en caso de error
     */
    private boolean arrayContains(List<CpsiAsignado> list, int pCpsiId) throws Exception {

        // Cuando se guardan los cambios los CpsiAsignados tienen otra instancia diferente dada
        // por JPA y, por lo tanto, no se pueden comparar con las almacenadas en la lista interna. Es necesario
        // comparar por los campos clave para saber si nos estamos refiriendo a la misma.

        for (CpsiAsignado cpsiAsig : list) {
            if (cpsiAsig.getIdCpsi().intValue() == pCpsiId) {
                return true;
            }
        }
        return false;
    }

    /**
     * Devuelve el número de códigos seleccionados en la tabla de códigos.
     * @return int
     */
    public int getCantidadSeleccionada() {
        return (codigosSeleccionados.size());
    }

    // /**
    // * Indica si el valor de cantidad de numeración solicitada es correcto.
    // * @return True si el valor de cantidad de numeración solicitada es correcto.
    // */
    // public boolean isCantidadCorrecta() {
    // return ((!StringUtils.isEmpty(codigosSolicitados)) && (Integer.parseInt(codigosSolicitados) > 0));
    // }

    // GETTERS & SETTERS

    /**
     * Indica si se se ha de habilitar el botón de 'Seleccionar'.
     * @return boolean
     */
    public boolean isSeleccionarHabilitado() {
        return seleccionarHabilitado;
    }

    /**
     * Información de la petición de Solicitud.
     * @return SolicitudAsignacionCpsi
     */
    public SolicitudAsignacionCpsi getSolicitud() {
        return solicitud;
    }

    /**
     * Listado de la tabla de resúmen de análisis de CPSI de Proveedor.
     * @return List
     */
    public List<Linea1EstudioCPSI> getListadoDetalleAnalisisPst() {
        return listadoDetalleAnalisisPst;
    }

    /**
     * Listado de Códigos CPSI.
     * @return List
     */
    public List<CodigoCPSI> getListadoCodigos() {
        return listadoCodigos;
    }

    /**
     * Listado de Códigos CPSI seleccionados.
     * @return List
     */
    public List<CodigoCPSI> getCodigosSeleccionados() {
        return codigosSeleccionados;
    }

    /**
     * Listado de Códigos CPSI seleccionados.
     * @param codigosSeleccionados List
     */
    public void setCodigosSeleccionados(List<CodigoCPSI> codigosSeleccionados) {
        this.codigosSeleccionados = codigosSeleccionados;
    }

    /**
     * Lista de Asignaciones Solicitadas.
     * @return List
     */
    public List<CpsiAsignado> getListaAsignaciones() {
        return listaAsignaciones;
    }

    /**
     * CPSI Asignado seleccionado.
     * @return CpsiAsignado
     */
    public CpsiAsignado getCpsiAsignadoSelecciondo() {
        return cpsiAsignadoSelecciondo;
    }

    /**
     * CPSI Asignado seleccionado.
     * @param cpsiAsignadoSelecciondo CpsiAsignado
     */
    public void setCpsiAsignadoSelecciondo(CpsiAsignado cpsiAsignadoSelecciondo) {
        this.cpsiAsignadoSelecciondo = cpsiAsignadoSelecciondo;
    }

    /**
     * Indica el número de códigos solicitados por el usuario.
     * @return String
     */
    public String getCodigosSolicitados() {
        return codigosSolicitados;
    }

    /**
     * Indica el número de códigos solicitados por el usuario.
     * @param codigosSolicitados String
     */
    public void setCodigosSolicitados(String codigosSolicitados) {
        this.codigosSolicitados = codigosSolicitados;
    }
}
