package mx.ift.sns.web.backend.cpsi.liberacion;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mx.ift.sns.modelo.cpsi.CodigoCPSI;
import mx.ift.sns.modelo.cpsi.EstatusCPSI;
import mx.ift.sns.modelo.cpsi.LiberacionSolicitadaCpsi;
import mx.ift.sns.modelo.cpsi.SolicitudLiberacionCpsi;
import mx.ift.sns.modelo.filtros.FiltroBusquedaCodigosCPSI;
import mx.ift.sns.modelo.solicitud.EstadoLiberacionSolicitada;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.negocio.PeticionCancelacion;
import mx.ift.sns.negocio.cpsi.ICodigoCPSIFacade;
import mx.ift.sns.negocio.exceptions.RegistroModificadoException;
import mx.ift.sns.utils.date.FechasUtils;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.components.TabWizard;
import mx.ift.sns.web.backend.components.Wizard;

import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Clase de soporte para la pestaña de 'Liberaciones' para el trámite de Liberaciones CPSI en los Wizard. */
public class LiberacionesCpsiTab extends TabWizard {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(LiberacionesCpsiTab.class);

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Id del campo de errores. */
    private static final String MSG_ID = "MSG_LiberacionesCPSI";

    /** Facade de Servicios para Códigos de Puntos de Señalización Internacional. */
    private ICodigoCPSIFacade cpsiFacade;

    /** Información de la petición de Solicitud. */
    private SolicitudLiberacionCpsi solicitud;

    /** Indica si se se ha de habilitar el botón de 'Guardar'. */
    private boolean salvarHabilitado = false;

    /** Fecha de implementación de la liberación. */
    private Date fechaImplementacion;

    /** Filtro de búsqueda de codigos CPS internacionales. */
    private FiltroBusquedaCodigosCPSI filtros;

    /** Listado de códigos internacionales. */
    private List<CodigoCPSI> listadoCodigosCPSI;

    /** Listado de Códigos CPSI seleccionados. */
    private List<CodigoCPSI> codigosCPSISeleccionados;

    /** Indica si la implementación es inmediata o programada. */
    private boolean implementacionInmediata = true;

    /** Lista de liberaciones CPSI solicitadas. */
    private List<LiberacionSolicitadaCpsi> listaLiberaciones;

    /** Tipo de Periodo de Reserva (Días / Meses). */
    private String tipoPeriodoReserva;

    /** Tipo de Fecha de Implementación (Calendario / Días / Meses). */
    private String tipoPeriodoImplementacion;

    /** Periodo de reserva de la numeración. */
    private String periodoReserva = "0";

    /** Días / meses / fecha indicada para la implementación programada. */
    private String periodoImplementacion = "0";

    /** Fecha de Finalización del Perido de Reserva. */
    private Date fechaFinReserva;

    /** Indica si la fecha de implementación se selecciona usando el calendario. */
    private boolean fechaImplCalendario = true;

    /** Liberación seleccionada en la tabla de Liberaciones Solicitadas. */
    private LiberacionSolicitadaCpsi libSolSeleccionada;

    /**
     * Constructor de la clase de respaldo para el tab/pestaña de 'Liberaciones'.
     * @param pWizard Referencia a la clase Wizard que instancia este Tab
     * @param pCpsiFacade Facade de Servicios de CPSI
     */
    public LiberacionesCpsiTab(Wizard pWizard, ICodigoCPSIFacade pCpsiFacade) {

        // Asociamos el Wizard padre a la pestaña que reprsenta este Tab
        setWizard(pWizard);

        // Asociamos la solicitud que usaremos en todo el Wizard. Es posible que
        // la referencia del Wizzard padre cambie en función de los pasos previos,
        // por eso hay que actualizar la referencia al llegar a éste tab usando
        // el método actualizaCampos();
        solicitud = (SolicitudLiberacionCpsi) getWizard().getSolicitud();

        // Asociamos el Servicio
        this.cpsiFacade = pCpsiFacade;
    }

    @Override
    public void cargaValoresIniciales() {
        try {
            // Inicializaciones
            filtros = new FiltroBusquedaCodigosCPSI();
            codigosCPSISeleccionados = new ArrayList<CodigoCPSI>();
            listaLiberaciones = new ArrayList<LiberacionSolicitadaCpsi>();

            // Fecha de Implementación y cuarentena
            fechaImplementacion = new Date();
            tipoPeriodoImplementacion = "2"; // Calendario
            periodoImplementacion = "0";
            fechaFinReserva = new Date();
            periodoReserva = "0";
            tipoPeriodoReserva = "0"; // Días
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
            this.setSummaryError(Errores.getDescripcionError(Errores.ERROR_0004));
        }
    }

    @Override
    public void resetTab() {
        if (this.isIniciado()) {
            this.codigosCPSISeleccionados.clear();
            this.fechaImplementacion = new Date();
            this.implementacionInmediata = true;
            this.listadoCodigosCPSI = null;
            this.listaLiberaciones.clear();
            this.salvarHabilitado = false;
            this.solicitud = null;
            this.periodoReserva = "0";
            this.tipoPeriodoReserva = "0"; // Días
            this.fechaFinReserva = new Date();
            this.periodoImplementacion = "0";
            this.tipoPeriodoImplementacion = "2"; // Calendario
            this.fechaImplCalendario = true;
            this.libSolSeleccionada = null;
        }
    }

    @Override
    public boolean isAvanzar() {
        boolean resultado = true;
        if (this.isTabHabilitado()) {
            // Continuamos siempre y cuando haya liberaciones por procesar
            if (listaLiberaciones.isEmpty()) {
                MensajesBean.addErrorMsg(MSG_ID, MensajesBean.getTextoResource("liberacion.almenosUna"), "");
                resultado = false;
            } else {
                resultado = (this.guardarCambios() && this.aplicarLiberaciones());
            }
        } else {
            // El tab no se puede editar, permitimos pasar directamente a la siguiente
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
        // Reseteamos el Tab por si se va y viene desde los botones de navegación del wizard.
        this.resetTab();

        // La solicitud del Wizard ha cambiado de instancia desde que se generó en
        // el constructor. Es necesario actualizar la referecnia en el tab.
        solicitud = (SolicitudLiberacionCpsi) getWizard().getSolicitud();

        // Actualizamos las solicitudes pendientes
        listaLiberaciones.clear();
        if (solicitud.getLiberacionesSolicitadas() != null) {
            listaLiberaciones.addAll(solicitud.getLiberacionesSolicitadas());
        }

        // Habilitamos el botón de guardar siempre y cuando haya liberaciones solicitadas.
        salvarHabilitado = (!listaLiberaciones.isEmpty());

        // Cargamos la lista de Códigos CPSI asignados al PST.
        this.realizarBusqueda();
    }

    /** Método invocado al pulsar el botón 'buscar' sobre el buscador de CPSI. */
    public void realizarBusqueda() {
        try {
            // Reiniciamos los filtros
            filtros.clear();

            // Buscamos siempre por el Asignatario que es el poseedor del CPSI
            filtros.setProveedor(this.solicitud.getProveedorSolicitante());
            filtros.setEstatusCPSI(new EstatusCPSI(EstatusCPSI.ASIGNADO));

            // Búsqueda de CPSI.
            listadoCodigosCPSI = cpsiFacade.findAllCodigosCPSI(filtros);
            codigosCPSISeleccionados.clear();
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    // /** Método invocado al pulsar el botón 'limpiar' sobre el buscador de CPSI. */
    // public void limpiarBusqueda() {
    // listadoCodigosCPSI = null;
    // codigosCPSISeleccionados.clear();
    // }

    /** Método invocado al pulsar sobre el botón de implementación inmediata. */
    public void inicializarFechaImplementacion() {
        if (implementacionInmediata) {
            fechaImplementacion = FechasUtils.getFechaHoy();
            tipoPeriodoImplementacion = "2"; // Calendario
            periodoImplementacion = "0";
            fechaImplCalendario = false;
        } else {
            // La fecha de implementación programada siempre es a partir de mañana.
            fechaImplementacion = FechasUtils.calculaFecha(1, 0, 0);
            tipoPeriodoImplementacion = "2"; // Calendario
            periodoImplementacion = "1";
            fechaImplCalendario = true;
        }

        // Actualizamos las fechas del Periodo de Cuarentena.
        actualizarFechaFinReserva();
    }

    /** Método invocado al pulsar sobre fecha de implementación. */
    public void actualizarFechaImplementacion() {
        // Las validaciones client-side nos aseguran que llegue un valor entero positivo
        int intPeriodo = Integer.parseInt(this.periodoImplementacion);
        if (intPeriodo == 0) {
            implementacionInmediata = false;
            inicializarFechaImplementacion();
        } else if (intPeriodo > 0) {
            if (tipoPeriodoImplementacion.equals("0")) {
                // Implementación indicada en días
                fechaImplementacion = FechasUtils.calculaFecha(intPeriodo, 0, 0);
                fechaImplCalendario = false;
            } else if (tipoPeriodoImplementacion.equals("1")) {
                // Implementación indicada en meses
                fechaImplementacion = FechasUtils.calculaFecha(0, intPeriodo, 0);
                fechaImplCalendario = false;
            } else {
                // Implementación indicada en el calendario
                fechaImplCalendario = true;
            }
        } else {
            MensajesBean.addErrorMsg(MSG_ID, "El periodo de implementación indicado no es válido.", "");
            fechaImplementacion = FechasUtils.calculaFecha(1, 0, 0);
            tipoPeriodoImplementacion = "2"; // Calendario
            periodoImplementacion = "1";
            fechaImplCalendario = true;
        }

        // Actualizamos las fechas del Periodo de Reserva.
        actualizarFechaFinReserva();
    }

    /** Valida la fecha de Implementación. */
    public void validarFechaImplementacion() {
        try {
            // La fecha de implementación programada siempre es a partir de mañana.
            Date fManana = FechasUtils.calculaFecha(1, 0, 0);

            if (fechaImplementacion == null) {
                fechaImplementacion = fManana;
            } else if (fechaImplementacion.before(fManana)) {
                MensajesBean.addErrorMsg(MSG_ID,
                        "La fecha de implementación programada no puede ser igual o inferior al día actual.", "");
                fechaImplementacion = fManana;
            }

            // Actualizamos los días comprendidos entre hoy y la fecha de implementación.
            int intPeriodo = (int) FechasUtils.getDiasEntre(FechasUtils.getFechaHoy(), fechaImplementacion);
            periodoImplementacion = String.valueOf(intPeriodo);

            // Actualizamos las fechas del Periodo de Reserva.
            this.actualizarFechaFinReserva();
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Método invocado al pulsar sobre fecha de implementación. */
    public void actualizarFechaFinReserva() {
        // Las validaciones client-side nos aseguran que llegue un valor entero positivo
        int intReserva = Integer.parseInt(this.periodoReserva);
        if (intReserva == 0) {
            fechaFinReserva = fechaImplementacion;
        } else if (intReserva > 0) {
            if (tipoPeriodoReserva.equals("0")) {
                // Perido de reserva indicado en días.
                fechaFinReserva = FechasUtils.calculaFecha(fechaImplementacion, intReserva, 0, 0);
            } else {
                // Perido de reserva indicado en meses.
                fechaFinReserva = FechasUtils.calculaFecha(fechaImplementacion, 0, intReserva, 0);
            }
        } else {
            MensajesBean.addErrorMsg(MSG_ID, "El periodo de cuarentena indicado no es válido.", "");
            fechaFinReserva = fechaImplementacion;
            periodoReserva = "0";
        }
    }

    /** Resetea los campos del periodo de reserva. */
    private void limpiarPeriodoReserva() {
        periodoReserva = "0";
        tipoPeriodoReserva = "0";
        this.actualizarFechaFinReserva();
    }

    /**
     * Método invocado al pulsar sobre el botón de 'Agregar' liberación.
     * @param pCheckCuarentena Indica si se ha de confirmar la cuarentena de los rangos seleccionados
     */
    public void agregarLiberacion(boolean pCheckCuarentena) {
        try {
            // Validación de rango arrendado.
            boolean todoOk = !(pCheckCuarentena && confirmarCuarentena());

            if (todoOk && validarCodigosSeleccionados()) {
                for (CodigoCPSI cpsi : codigosCPSISeleccionados) {
                    listaLiberaciones.add(this.getLiberacionSolicitadaFromCpsi(cpsi));
                }
                this.inicializarFechaImplementacion();
                this.limpiarPeriodoReserva();
            }

            // Habilitamos el botón de guardar siempre y cuando haya liberaciones solicitadas.
            salvarHabilitado = (!listaLiberaciones.isEmpty());

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Para varios cpsi seleccionados, muestra la modal de confirmación.
     * @return True si se ha de mostrar la modal de confirmación de cuarentena.
     */
    private boolean confirmarCuarentena() {

        if ((!codigosCPSISeleccionados.isEmpty()) && (codigosCPSISeleccionados.size() > 1)) {
            // Mostramos la modal.
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('DLG_ConfirmCuarentena').show()");
            return true;
        }

        return false;
    }

    /**
     * Realiza las validaciones sobre los códigos seleccionados antes de agregar las liberaciones solicitadas.
     * @return True si se han pasado todas las validaciones.
     * @throws Exception en caso de error
     */
    private boolean validarCodigosSeleccionados() throws Exception {

        // Comprueba si se han seleccionado cpsis para liberar.
        if (codigosCPSISeleccionados.isEmpty()) {
            MensajesBean.addErrorMsg(MSG_ID, "Es necesario seleccionar almenos un CPSI", "");
            return false;
        }

        List<CodigoCPSI> cpsisEnLiberacion = new ArrayList<CodigoCPSI>();
        List<CodigoCPSI> cpsisEstatusNoValido = new ArrayList<CodigoCPSI>();
        for (CodigoCPSI cpsi : codigosCPSISeleccionados) {
            if (!cpsi.getEstatus().getId().equals(EstatusCPSI.ASIGNADO)) {
                // Comprueba que el estatus del CPSI sea correcto para liberación
                cpsisEstatusNoValido.add(cpsi);
            } else {
                // Comprueba si se han seleccionado cpsis ya elegidos para liberar
                if (arrayContains(listaLiberaciones, cpsi.getId().intValue())) {
                    cpsisEnLiberacion.add(cpsi);
                }
            }
        }

        if (!cpsisEnLiberacion.isEmpty()) {
            StringBuffer sb = new StringBuffer();
            sb.append("No es posible agregar los CPSI seleccionados.<br>");
            sb.append("Lo siguientes CPSI ya existen para liberación:<br>");
            for (CodigoCPSI cpsi : cpsisEnLiberacion) {
                sb.append("Binario: ").append(cpsi.getBinario());
                sb.append(", Decimal: ").append(cpsi.getDecimalTotal());
                sb.append(", Formato Dec.: ").append(cpsi.getFormatoDecimal()).append("<br>");
            }
            MensajesBean.addErrorMsg(MSG_ID, sb.toString(), "");
            return false;
        }

        if (!cpsisEstatusNoValido.isEmpty()) {
            StringBuffer sb = new StringBuffer();
            if (!cpsisEnLiberacion.isEmpty()) {
                sb.append("<br>");
            } else {
                sb.append("No es posible agregar los CPSI seleccionados.<br>");
            }
            sb.append("Lo siguientes CPSI tienen un estatus no permitido para liberación:<br>");
            for (CodigoCPSI cpsi : cpsisEstatusNoValido) {
                sb.append("Binario: ").append(cpsi.getBinario());
                sb.append(", Decimal: ").append(cpsi.getDecimalTotal());
                sb.append(", Formato Dec.: ").append(cpsi.getFormatoDecimal()).append("<br>");
                sb.append(", Estatus: ").append(cpsi.getEstatus().getDescripcion()).append("<br>");
            }
            MensajesBean.addErrorMsg(MSG_ID, sb.toString(), "");
            return false;
        }

        return true;
    }

    /**
     * Guarda los cambios realizados hasta el momento en el trámite.
     * @return 'True' si se ha podido guardar correctamente.
     */
    private boolean guardarCambios() {

        // Lista de Liberaciones Solicitadas a eliminar permanentemente
        ArrayList<LiberacionSolicitadaCpsi> libSolEliminar = new ArrayList<LiberacionSolicitadaCpsi>();

        try {
            // Eliminamos las liberaciones que se han deseleccionado
            for (LiberacionSolicitadaCpsi libsol : solicitud.getLiberacionesSolicitadas()) {
                // Ignoramos las liberaciones que ya se hayan ejecutado
                if (libsol.getEstatus().getCodigo().equals(EstadoLiberacionSolicitada.PENDIENTE)) {
                    if ((!arrayContains(listaLiberaciones, libsol.getIdCpsi().intValue()))) {
                        // No podemos eliminar una liberación solicitada de la lista mientras estamos
                        // recorriendo la misma lista (ConcurrentModificationException).
                        // Por lo tanto la añadimos a otra lista para eliminarla al salir del bucle.
                        libSolEliminar.add(libsol);
                    }
                }
            }

            // Eliminamos de la solicitud las LiberacionesSolicitadas que se han eliminado de la tabla de Liberaciones.
            for (LiberacionSolicitadaCpsi libsol : libSolEliminar) {
                // Al tener marcadas las liberacionesSolicitadas como @PrivateOwned en JPA se eliminan automáticamente
                // cuando se guardan los cambios en la solicitud ya que indica que nadie más las utiliza.
                solicitud.removeLiberacionSolicitada(libsol);
            }

            // Agregamos las liberaciones que se han añadido
            for (LiberacionSolicitadaCpsi libSol : listaLiberaciones) {
                if (!arrayContains(solicitud.getLiberacionesSolicitadas(), libSol.getIdCpsi().intValue())) {
                    solicitud.addLiberacionSolicitada(libSol);
                }
            }

            // Guardamos los cambios y generamos los nuevos registros
            solicitud = cpsiFacade.saveSolicitudLiberacion(solicitud);

            // Actualizamos la solicitud para todos los tabs
            getWizard().setSolicitud(solicitud);

            // Una vez persistidas las LiberacionesSolicitadas rehacemos la tabla de liberaciones para que todas
            // tengan el id ya generado
            listaLiberaciones.clear();
            listaLiberaciones.addAll(solicitud.getLiberacionesSolicitadas());

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

    /**
     * Aplica las liberaciones seleccionadas por el usuario.
     * @return True si se han aplicado correctamente las liberaciones.
     */
    private boolean aplicarLiberaciones() {
        try {
            if (validarCodigosALiberar()) {
                // Aplicamos la liberaciones solicitadas y reserva de rangos si es necesario.
                SolicitudLiberacionCpsi solLib = cpsiFacade.applyLiberacionesSolicitadas(solicitud);

                // Asociamos la nueva instancia de solicitud al Wizard
                this.getWizard().setSolicitud(solLib);

                // Continuamos a la siguiente pestaña
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
     * Comprueba que los códigos seleccionados para liberar existen y tienen estatus disponible.
     * @return True si se pueden aplicar las liberaciones.
     * @throws Exception En caso de error.
     */
    private boolean validarCodigosALiberar() throws Exception {

        // Antes de aplicar las liberaciones comprobamos que todos los CPSI sigan
        // asignados al Proveedor y con un estatus válido.
        List<String> avisosEstatus = new ArrayList<String>();

        int cpsiModificados = 0;
        for (LiberacionSolicitadaCpsi libSol : solicitud.getLiberacionesSolicitadas()) {
            if (libSol.getEstatus().getCodigo().equals(EstadoLiberacionSolicitada.PENDIENTE)) {

                CodigoCPSI cpsi = cpsiFacade.getCodigoCpsi(
                        libSol.getIdCpsi(),
                        libSol.getSolicitudLiberacion().getProveedorSolicitante());

                // Si esta libre o asignado a un proveedor diferente no será prosible encontrarlo.
                if (cpsi != null) {
                    // Comprobamos si sigue en estatus asignado.
                    if (!cpsi.getEstatus().getId().equals(EstatusCPSI.ASIGNADO)) {
                        // Si no está asignado puede estar planificado para una liberación posterior
                        if (!cpsi.getEstatus().getId().equals(EstatusCPSI.PLANIFICADO)) {
                            StringBuffer sbAviso = new StringBuffer();
                            sbAviso.append(", Binario: ").append(cpsi.getBinario());
                            sbAviso.append(", Decimal: ").append(cpsi.getDecimalTotal());
                            sbAviso.append(", Formato Dec.: ").append(cpsi.getFormatoDecimal());
                            sbAviso.append(", Estatus: ").append(cpsi.getEstatus().getDescripcion()).append("<br>");
                            avisosEstatus.add(sbAviso.toString());
                        }
                    }
                } else {
                    ++cpsiModificados;
                }
            }
        }

        // Avisos de CPSIs que ya no existen
        if (cpsiModificados != 0) {
            StringBuffer sbAviso = new StringBuffer();
            sbAviso.append("Se han encontrado ").append(cpsiModificados);
            sbAviso.append(" que han sido eliminados o modificados. Revise las liberaciones solicitadas.<br>");
            MensajesBean.addErrorMsg(MSG_ID, sbAviso.toString(), "");
        }

        // Avisos de CPSI que ya no tienen estatus 'Asignado'
        if (!avisosEstatus.isEmpty()) {
            StringBuffer sbAviso = new StringBuffer();
            sbAviso.append("Los siguientes CPSI han cambiado de estatus:<br>");
            for (String aviso : avisosEstatus) {
                sbAviso.append(aviso);
            }
            MensajesBean.addErrorMsg(MSG_ID, sbAviso.toString(), "");
        }

        // Si no hay avisos, continuamos con la liberación
        return (avisosEstatus.isEmpty() && (cpsiModificados == 0));
    }

    /**
     * Indica si una lista contiene una LiberacionSolicitadaNng sin usar los métos Equals de Object.
     * @param list Lista de objetos LiberacionSolicitadaNng
     * @param pCpsiId Identificador de código CPSI.
     * @return True si la LiberacionSolicitadaNng esta en la lista
     * @throws Exception en caso de error
     */
    private boolean arrayContains(List<LiberacionSolicitadaCpsi> list, int pCpsiId) throws Exception {

        // Cuando se guardan los cambios las LiberacionesSolicitadas tienen otra instancia diferente dada
        // por JPA y, por lo tanto, no se pueden comparar con las almacenadas en la lista interna. Es necesario
        // comparar por los campos clave para saber si nos estamos refiriendo a la misma.

        for (LiberacionSolicitadaCpsi libSol : list) {
            if (libSol.getIdCpsi().intValue() == pCpsiId) {
                return true;
            }
        }
        return false;
    }

    /**
     * Crea un objeto LiberacionSolicitadaCpsi en base a la información de un CodigoCPSI dado.
     * @param pCpsi Información el código de puntos de señalización.
     * @return LiberacionSolicitadaCpsi con la información del CPSI.
     * @throws Exception en caso de error.
     */
    private LiberacionSolicitadaCpsi getLiberacionSolicitadaFromCpsi(CodigoCPSI pCpsi) throws Exception {
        LiberacionSolicitadaCpsi lscpsi = new LiberacionSolicitadaCpsi();
        lscpsi.setSolicitudLiberacion(solicitud);
        lscpsi.setFechaImplementacion(FechasUtils.parseFecha(fechaImplementacion));
        lscpsi.setFechaFinCuarentena(FechasUtils.parseFecha(fechaFinReserva));
        lscpsi.setIdCpsi(pCpsi.getId());
        lscpsi.setBinario(pCpsi.getBinario());
        lscpsi.setDecimal(pCpsi.getDecimalTotal());
        lscpsi.setFormatoDecimal(pCpsi.getFormatoDecimal());

        StringBuilder sbImplementacion = new StringBuilder();
        sbImplementacion.append(periodoImplementacion).append(" ");
        if (tipoPeriodoImplementacion.equals("0") || tipoPeriodoImplementacion.equals("2")) {
            sbImplementacion.append("Día/s");
        } else {
            sbImplementacion.append("Mes/s");
        }
        lscpsi.setPeriodoImplementacion(sbImplementacion.toString());

        StringBuilder sbCuarentena = new StringBuilder();
        sbCuarentena.append(periodoReserva).append(" ");
        if (tipoPeriodoReserva.equals("0")) {
            sbCuarentena.append("Día/s");
        } else {
            sbCuarentena.append("Mes/s");
        }
        lscpsi.setPeriodoCuarentena(sbCuarentena.toString());

        // Estado pendiente para su posterior liberación.
        EstadoLiberacionSolicitada els = new EstadoLiberacionSolicitada();
        els.setCodigo(EstadoLiberacionSolicitada.PENDIENTE);
        els.setDescripcion(EstadoLiberacionSolicitada.TXT_PENDIENTE);
        lscpsi.setEstatus(els);
        return lscpsi;
    }

    /** Método invocado al pulsar sobre el botón 'Eliminar' en la tabla de Liberaciones seleccionadas. */
    public void eliminarLiberacion() {
        try {
            PeticionCancelacion checkCancelacion = cpsiFacade.cancelLiberacion(libSolSeleccionada, true);
            if (checkCancelacion.isCancelacionPosible()) {

                if (libSolSeleccionada.getEstatus().getCodigo().equals(EstadoLiberacionSolicitada.LIBERADO)) {
                    // Si la liberación ya se había ejecutado pero aún no se ha cumplido la cuarentena
                    // marcamos la liberación como cancelada.
                    EstadoLiberacionSolicitada estatusLibSol = new EstadoLiberacionSolicitada();
                    estatusLibSol.setCodigo(EstadoLiberacionSolicitada.CANCELADO);
                    libSolSeleccionada.setEstatus(estatusLibSol);
                } else {
                    // Si la liberación estaba como "Pendiente" directamente se elimina.
                    listaLiberaciones.remove(libSolSeleccionada);
                }

                // Actualizamos la solicitud
                this.guardarCambios();

                // Actualizamos la tabla de CPSI asignados para que se vuelva a mostrar el Código.
                this.realizarBusqueda();

                if (solicitud.getEstadoSolicitud().getCodigo().equals(EstadoSolicitud.SOLICITUD_TERMINADA)) {
                    // RN088: Si todas las liberaciones están canceladas el trámite pasa a estar
                    // cancelado también.
                    boolean liberacionesCanceladas = true;
                    for (LiberacionSolicitadaCpsi libSol : solicitud.getLiberacionesSolicitadas()) {
                        liberacionesCanceladas = liberacionesCanceladas
                                && (libSol.getEstatus().getCodigo().equals(
                                        EstadoLiberacionSolicitada.CANCELADO));
                    }

                    // Cambiamos el estado de la solicitud
                    if (liberacionesCanceladas) {
                        EstadoSolicitud statusCancelada = new EstadoSolicitud();
                        statusCancelada.setCodigo(EstadoSolicitud.SOLICITUD_CANCELADA);
                        solicitud.setEstadoSolicitud(statusCancelada);

                        MensajesBean.addInfoMsg(MSG_ID,
                                "Se han cancelado todas las liberaciones."
                                        + " El trámite pasa a estado 'Cancelado'", "");

                        // Guardamos los cambios
                        solicitud = cpsiFacade.saveSolicitudLiberacion(solicitud);
                        getWizard().setSolicitud(solicitud);
                    }
                }
            } else {
                MensajesBean.addErrorMsg(MSG_ID, checkCancelacion.getMensajeError(), "");
            }

            // Acabamos de eliminar una liberación solicitada de la tabla de resúmen. Habilitamos
            // el botón de guardar para salvar los cambios.
            salvarHabilitado = true;

        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0015);
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    // GETTERS & SETTERS

    /**
     * Fecha de implementación de la liberación.
     * @return Date
     */
    public Date getFechaImplementacion() {
        return fechaImplementacion;
    }

    /**
     * Fecha de implementación de la liberación.
     * @param fechaImplementacion Date
     */
    public void setFechaImplementacion(Date fechaImplementacion) {
        this.fechaImplementacion = fechaImplementacion;
    }

    /**
     * Listado de Códigos CPSI seleccionados.
     * @return List
     */
    public List<CodigoCPSI> getCodigosCPSISeleccionados() {
        return codigosCPSISeleccionados;
    }

    /**
     * Listado de Códigos CPSI seleccionados.
     * @param codigosCPSISeleccionados List
     */
    public void setCodigosCPSISeleccionados(List<CodigoCPSI> codigosCPSISeleccionados) {
        this.codigosCPSISeleccionados = codigosCPSISeleccionados;
    }

    /**
     * Indica si la implementación es inmediata o programada.
     * @return boolean
     */
    public boolean isImplementacionInmediata() {
        return implementacionInmediata;
    }

    /**
     * Indica si la implementación es inmediata o programada.
     * @param implementacionInmediata boolean
     */
    public void setImplementacionInmediata(boolean implementacionInmediata) {
        this.implementacionInmediata = implementacionInmediata;
    }

    /**
     * Indica si se se ha de habilitar el botón de 'Guardar'.
     * @return boolean
     */
    public boolean isSalvarHabilitado() {
        return salvarHabilitado;
    }

    /**
     * Listado de códigos nacionales.
     * @return List
     */
    public List<CodigoCPSI> getListadoCodigosCPSI() {
        return listadoCodigosCPSI;
    }

    /**
     * Lista de liberaciones CPSI solicitadas.
     * @return List
     */
    public List<LiberacionSolicitadaCpsi> getListaLiberaciones() {
        return listaLiberaciones;
    }

    /**
     * Perido de reserva de la numeración.
     * @return String
     */
    public String getPeriodoReserva() {
        return periodoReserva;
    }

    /**
     * Perido de reserva de la numeración.
     * @param periodoReserva String
     */
    public void setPeriodoReserva(String periodoReserva) {
        this.periodoReserva = periodoReserva;
    }

    /**
     * Tipo de Periodo de Reserva (Días / Meses).
     * @return String
     */
    public String getTipoPeriodoReserva() {
        return tipoPeriodoReserva;
    }

    /**
     * Tipo de Periodo de Reserva (Días / Meses).
     * @param tipoPeriodoReserva String
     */
    public void setTipoPeriodoReserva(String tipoPeriodoReserva) {
        this.tipoPeriodoReserva = tipoPeriodoReserva;
    }

    /**
     * Fecha de Finalización del Perido de Reserva.
     * @return Date
     */
    public Date getFechaFinReserva() {
        return fechaFinReserva;
    }

    /**
     * Tipo de Fecha de Implementación (Calendario / Días / Meses).
     * @return String
     */
    public String getTipoPeriodoImplementacion() {
        return tipoPeriodoImplementacion;
    }

    /**
     * Tipo de Fecha de Implementación (Calendario / Días / Meses).
     * @param tipoPeriodoImplementacion String
     */
    public void setTipoPeriodoImplementacion(String tipoPeriodoImplementacion) {
        this.tipoPeriodoImplementacion = tipoPeriodoImplementacion;
    }

    /**
     * Días / meses / fecha indicada para la implementación programada.
     * @return String
     */
    public String getPeriodoImplementacion() {
        return periodoImplementacion;
    }

    /**
     * Días / meses / fecha indicada para la implementación programada.
     * @param periodoImplementacion String
     */
    public void setPeriodoImplementacion(String periodoImplementacion) {
        this.periodoImplementacion = periodoImplementacion;
    }

    /**
     * Indica si la fecha de implementación se selecciona usando el calendario.
     * @return boolean
     */
    public boolean isFechaImplCalendario() {
        return fechaImplCalendario;
    }

    /**
     * Liberación seleccionada en la tabla de Liberaciones Solicitadas.
     * @return LiberacionSolicitadaCpsi
     */
    public LiberacionSolicitadaCpsi getLibSolSeleccionada() {
        return libSolSeleccionada;
    }

    /**
     * Liberación seleccionada en la tabla de Liberaciones Solicitadas.
     * @param libSolSeleccionada LiberacionSolicitadaCpsi
     */
    public void setLibSolSeleccionada(LiberacionSolicitadaCpsi libSolSeleccionada) {
        this.libSolSeleccionada = libSolSeleccionada;
    }

}
