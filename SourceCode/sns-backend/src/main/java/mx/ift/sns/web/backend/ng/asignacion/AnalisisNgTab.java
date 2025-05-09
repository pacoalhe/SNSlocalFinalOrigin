package mx.ift.sns.web.backend.ng.asignacion;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.faces.event.ActionEvent;

import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.filtros.FiltroBusquedaSeries;
import mx.ift.sns.modelo.ng.NumeracionAsignada;
import mx.ift.sns.modelo.ng.NumeracionSolicitada;
import mx.ift.sns.modelo.ng.RangoSerie;
import mx.ift.sns.modelo.ng.RangoSeriePK;
import mx.ift.sns.modelo.ng.SolicitudAsignacion;
import mx.ift.sns.modelo.pst.TipoProveedor;
import mx.ift.sns.modelo.series.EstadoRango;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.negocio.exceptions.RegistroModificadoException;
import mx.ift.sns.negocio.ng.INumeracionGeograficaService;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.components.TabWizard;
import mx.ift.sns.web.backend.components.Wizard;
import mx.ift.sns.web.backend.lazymodels.NumDisponibleLazyModel;
import mx.ift.sns.web.backend.lazymodels.multiseleccion.MultiSelectionOnLazyModelManager;
import mx.ift.sns.web.backend.ng.asignacion.manual.analisis.NumeracionDisponible;
import mx.ift.sns.web.backend.ng.asignacion.manual.analisis.SugerenciaNumeracion;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleSelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase que implementa la pestaña de Analisis.
 */
public class AnalisisNgTab extends TabWizard {

    /** Serial uid. */
    private static final long serialVersionUID = 3100597867527841411L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AnalisisNgTab.class);

    /** ID mensaje. */
    private static final String MSG_ID = "MSG_analisis";

    /** Render si el PST solicitante es comercializador. */
    private boolean renderColumnasComercializadora = false;

    /** Render si se selecciona una serie libre. */
    private boolean renderSerieSelected = false;

    /** Render si se selecciona una serie ocupada. */
    private boolean renderSerieOcupada = false;

    /** boolean de la accion de analizar. */
    private boolean analizado = false;

    /** Cantidad a asignar. */
    private Long cantidadAsignar;

    /** Cantidad por asignar. */
    private Integer cantidadPorAsignar;

    /** Cantidad solicitada. */
    private Integer cantidadSolicitada;

    /** Numeracion inicial. */
    private Integer numeracionInicial;

    /** Numeracion final. */
    private Integer numeracionFinal;

    /** Cantidad series asignada al ABN. */
    private Integer cantSeriesAsignadaAbn;

    /** Total series en ABN. */
    private Integer totalSeriesAbn;

    /** Porcentaja ocupacion serie. */
    private Float porcentajeOcupacionSerie;

    /** Numeracion asignada en ABN. */
    private Integer cantNumeracionAsignadaAbn;

    /** Total numeracion en ABN. */
    private Integer totalNumeracionAbn;

    /** Porcentaje ocupacion numeracion. */
    private Float porcentajeOcupacionNumeracion;

    /** Lista numeraciones disponibles por PST. */
    private NumDisponibleLazyModel lineasDisponiblesPSTList;

    /** Lista numeraciones disponibles por otros PST. */
    private NumDisponibleLazyModel lineasDisponiblesNoPSTList;

    /** Lista numeraciones disponibles por series libres. */
    private NumDisponibleLazyModel lineasDisponiblesSeriesList;

    /** Lista rangos seleccionados. */
    private List<RangoSerie> numeracionAsignadaSerieList;

    /** Lista sugerencia de rangos seleccionados. */
    private List<SugerenciaNumeracion> sugerenciaNumeracionList;

    /** Numeración solicitada de resumen. */
    private NumeracionSolicitada selectResumenNumeracion;

    /** Numeración solicitada de seleccion. */
    private NumeracionSolicitada selectNumeracion;

    /** Seleccion Numeración ocupada. */
    private NumeracionDisponible selectSerieOcupada;

    /** Seleccion Numeración por PST. */
    private NumeracionDisponible selectNumDispoPST;

    /** Seleccion Numeración por otro PST. */
    private NumeracionDisponible selectNumDispoNoPST;

    /** Serie libre. */
    private MultiSelectionOnLazyModelManager<NumeracionDisponible> selectSerieLibre;

    /** Sugerencia numeracion seleccionada. */
    private SugerenciaNumeracion sugerenciaNumeracionSelect;

    /** Seleccion multiple en la tabla de numeraciones seleccionada. */
    private List<NumeracionAsignada> selectedNumeracionSeleccionada;

    /** Servicio de numeracion geografica. */
    private INumeracionGeograficaService ngService;

    /** Solicitud de asignacion. */
    private SolicitudAsignacion solicitud;

    /** Lista de rangos cambiados. */
    private List<String> avisosRangosCambiados = new ArrayList<String>();

    /** Se encarga de guardar el número de registros por página al cargar la misma. */
    private int registroPorPagina;

    /** Lista de Numeraciones Asignadas Filtradas en la tabla de Numeraciones Seleccionadas. */
    private List<NumeracionAsignada> listNumAsigFiltradas;

    /**
     * Constructor para cargar los datos genereales.
     * @param pBeanPadre beanPadre
     * @param proovedorService servicio
     */
    public AnalisisNgTab(Wizard pBeanPadre, INumeracionGeograficaService proovedorService) {
        try {
            setWizard(pBeanPadre);

            cantidadAsignar = new Long(0);
            cantidadPorAsignar = new Integer(0);
            cantidadSolicitada = new Integer(0);

            this.ngService = proovedorService;

            lineasDisponiblesPSTList = new NumDisponibleLazyModel();
            lineasDisponiblesNoPSTList = new NumDisponibleLazyModel();
            lineasDisponiblesSeriesList = new NumDisponibleLazyModel();

            selectSerieLibre = new MultiSelectionOnLazyModelManager<NumeracionDisponible>();
            lineasDisponiblesSeriesList.setMultiSelectionManager(selectSerieLibre);
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
            this.setSummaryError(Errores.getDescripcionError(Errores.ERROR_0004));
        }
    }

    /****************************************************************
     * OPERACIONES SELECCION EN TABLAS
     ****************************************************************/

    /**
     * Acciones al seleccionar una numeracion seleccionada.
     */
    public void numeracionSeleccionadaSelect() {

        try {
            avisosRangosCambiados = new ArrayList<String>();
            for (NumeracionAsignada numAsignada : selectedNumeracionSeleccionada) {
                // Comprobamos que la numeracion tiene un rango correspondiente o si la numeracion esta libre
                RangoSerie rango = ngService.getRangoSerie(numAsignada.getNir().getId(),
                        numAsignada.getSna(), numAsignada.getInicioRango(),
                        numAsignada.getSolicitudAsignacion().getProveedorSolicitante());

                if (!(rango != null && numAsignada.getSolicitudAsignacion().getId()
                        .compareTo(rango.getSolicitud().getId()) == 0)
                        && !(rango == null && ngService.isRangoLibre(numAsignada.getNir().getId(), numAsignada.getSna(),
                                numAsignada.getInicioRango(), numAsignada.getFinRango()))) {
                    // Si la numeracion no es correcta avisamos
                    StringBuffer sbAviso = new StringBuffer();
                    sbAviso.append(" NIR: ").append(numAsignada.getNir().getCodigo());
                    sbAviso.append(", SNA: ").append(numAsignada.getSna());
                    sbAviso.append(", Inicio: ").append(numAsignada.getInicioRango());
                    sbAviso.append(", Final: ").append(numAsignada.getFinRango());
                    avisosRangosCambiados.add(sbAviso.toString());
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Evento que sucede al seleccionar una registro de la tabla.
     * @param event evento
     */
    public void numeracionOnRowSelect(SelectEvent event) {

        selectResumenNumeracion = (NumeracionSolicitada) event.getObject();
        selectNumeracion = selectResumenNumeracion;

        cantidadSolicitada = selectNumeracion.getCantSolicitada().intValue();
        cantidadPorAsignar = cantidadSolicitada - selectResumenNumeracion.getCantAsignada().intValue();

        renderSerieSelected = false;
        renderSerieOcupada = false;
        analizado = false;
    }

    /**
     * Eveto al seleccionar una numeracion disponible del PST.
     * @param event evento
     */
    public void seriePstOnRowSelect(SelectEvent event) {

        lineasDisponiblesSeriesList.setEjecutar(false);
        lineasDisponiblesNoPSTList.setEjecutar(false);

        selectSerieOcupada = (NumeracionDisponible) event.getObject();

        seleccionarSerieOcupada();

        selectNumDispoNoPST = null;
        selectSerieLibre.clear();
    }

    /**
     * Selecciona una serie en la tabla de numeracion disponible por otros PST.
     * @param event SelectEvent
     */
    public void serieOtroPstOnRowSelect(SelectEvent event) {

        lineasDisponiblesSeriesList.setEjecutar(false);
        lineasDisponiblesPSTList.setEjecutar(false);

        selectSerieOcupada = (NumeracionDisponible) event.getObject();

        seleccionarSerieOcupada();

        selectNumDispoPST = null;
        selectSerieLibre.clear();
    }

    /**
     * Acciones al seleccionar una Serie Libre.
     */
    private void serieOnRowSelect() {

        lineasDisponiblesNoPSTList.setEjecutar(false);
        lineasDisponiblesPSTList.setEjecutar(false);

        selectNumDispoPST = null;
        selectNumDispoNoPST = null;

        renderSerieSelected = true;
        renderSerieOcupada = false;
    }

    /**
     * Método invocado al seleccionar todas las filas de la tabla Series Libres mediante el checkbox de cabecera.
     * @param event ToggleSelectEvent
     */
    public void seleccionPaginaSerieLibre(ToggleSelectEvent event) {
        try {
            // Actualizamos la selección múltiple en paginación
            int pagina = ((DataTable) event.getSource()).getPage();
            selectSerieLibre.toggleSelecction(pagina, event.isSelected());

            serieOnRowSelect();

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Método invocado al seleccionar una fila de la tabla de Series Libres.
     * @param event SelectEvent
     */
    public void seleccionSerieLibre(SelectEvent event) {
        try {
            // Actualizamos la selección múltiple en paginación
            NumeracionDisponible serieLibre = (NumeracionDisponible) event.getObject();
            int pagina = ((DataTable) event.getSource()).getPage();
            selectSerieLibre.updateSelection(serieLibre, pagina, true);

            serieOnRowSelect();

            if (numeracionAsignadaSerieList != null) {
                numeracionAsignadaSerieList.clear();
            }

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Método invocado al deseleccionar una fila de la tabla de Series Libres.
     * @param event SelectEvent
     */
    public void deSeleccionSerieLibre(UnselectEvent event) {
        try {
            // Actualizamos la selección múltiple en paginación
            NumeracionDisponible serieLibre = (NumeracionDisponible) event.getObject();
            int pagina = ((DataTable) event.getSource()).getPage();
            selectSerieLibre.updateSelection(serieLibre, pagina, false);

            serieOnRowSelect();

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Selecciona un rango de la tabla de sugerencias.
     * @param event SelectEvent
     * @throws Exception Exception
     */
    public void sugerenciaOnRowSelect(SelectEvent event) throws Exception {

        lineasDisponiblesSeriesList.setEjecutar(true);
        lineasDisponiblesNoPSTList.setEjecutar(true);
        lineasDisponiblesPSTList.setEjecutar(true);

        try {
            // Añadimos la sugerencia a la tabla de rangos seleccionados
            sugerenciaNumeracionSelect = (SugerenciaNumeracion) event.getObject();
            addNumeracionSeleccionada(sugerenciaNumeracionSelect.getNumInicial().intValue(), sugerenciaNumeracionSelect
                    .getNumFinal().intValue(), selectSerieOcupada);

            EstadoSolicitud estadoSolicitud = new EstadoSolicitud();
            estadoSolicitud.setCodigo(EstadoSolicitud.SOLICITUD_EN_TRAMITE);
            // Guardamos y actualizamos las tablas
            solicitud.setEstadoSolicitud(estadoSolicitud);

            guardarSeleccionAction();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        } finally {
            analizarAction();
            seleccionarSerieOcupada();
            cantidadAsignar = (long) 0;
        }

    }

    /****************************************************************
     * ACCIONES DE BOTON
     ****************************************************************/

    /**
     * Botón de analizar el cual carga los datos de las tablas.
     */
    public void analizarAction() {

        lineasDisponiblesSeriesList.setEjecutar(true);
        lineasDisponiblesNoPSTList.setEjecutar(true);
        lineasDisponiblesPSTList.setEjecutar(true);

        // Validamos la cantidad a asignar
        boolean error = false;
        if (!analizado) {
            // Se elimina los elementos seleccionados si los hubiese
            selectNumDispoPST = null;
            selectNumDispoNoPST = null;
            selectSerieLibre.clear();

            // Se quita la renderización de la numeración asignada a la serie seleccionada
            renderSerieSelected = false;
            renderSerieOcupada = false;

            if (!(cantidadAsignar instanceof Long) || (cantidadAsignar < 1)) {
                MensajesBean.addErrorMsg(MSG_ID, "Cantidad a asignar inválida", "");
                error = true;

            } else if ((cantidadAsignar > cantidadSolicitada || cantidadAsignar > cantidadPorAsignar)) {
                MensajesBean.addErrorMsg(MSG_ID, "No se pueden asignar mas numeraciones que las solicitadas", "");
                error = true;
            }
        }

        if (!error) {

            Abn abn = new Abn();
            abn = selectNumeracion.getPoblacion().getAbn();

            if (abn != null) {
                // Construimos las tablas de numericion disponiple
                try {

                    FiltroBusquedaSeries filtrosOcupadosPst = new FiltroBusquedaSeries();
                    FiltroBusquedaSeries filtrosOcupadosOtroPst = new FiltroBusquedaSeries();

                    filtrosOcupadosPst.clear();
                    filtrosOcupadosOtroPst.clear();

                    filtrosOcupadosPst.setIdAbn(abn.getCodigoAbn());
                    filtrosOcupadosPst.setSerieLibre(false);

                    filtrosOcupadosOtroPst.setIdAbn(abn.getCodigoAbn());
                    filtrosOcupadosOtroPst.setSerieLibre(false);

                    // Numeración disponible por PST solicitante
                    filtrosOcupadosPst.setAsignatario(solicitud.getProveedorSolicitante());
                    lineasDisponiblesPSTList = new NumDisponibleLazyModel();
                    lineasDisponiblesPSTList.setService(ngService);
                    lineasDisponiblesPSTList.setFiltros(filtrosOcupadosPst);

                    // Numeración disponible por otros PST
                    filtrosOcupadosOtroPst.setAsignatario(solicitud.getProveedorSolicitante());
                    filtrosOcupadosOtroPst.setOtroAsignatario(true);
                    lineasDisponiblesNoPSTList = new NumDisponibleLazyModel();
                    lineasDisponiblesNoPSTList.setService(ngService);
                    lineasDisponiblesNoPSTList.setFiltros(filtrosOcupadosOtroPst);

                    FiltroBusquedaSeries filtrosLibres = new FiltroBusquedaSeries();
                    FiltroBusquedaSeries filtrosOcupados = new FiltroBusquedaSeries();

                    filtrosLibres.clear();
                    filtrosOcupados.clear();

                    filtrosLibres.setIdAbn(abn.getCodigoAbn());
                    filtrosLibres.setSerieLibre(true);

                    filtrosOcupados.setIdAbn(abn.getCodigoAbn());
                    filtrosOcupados.setSerieLibre(false);

                    // Numeracion disponible por serie
                    selectSerieLibre = new MultiSelectionOnLazyModelManager<NumeracionDisponible>();
                    lineasDisponiblesSeriesList = new NumDisponibleLazyModel();
                    lineasDisponiblesSeriesList.setMultiSelectionManager(selectSerieLibre);
                    lineasDisponiblesSeriesList.setService(ngService);
                    lineasDisponiblesSeriesList.setFiltros(filtrosLibres);

                    numeracionInicial = new Integer(0);
                    numeracionFinal = (int) (numeracionInicial + cantidadAsignar - 1);
                    if (numeracionFinal < 0) {
                        numeracionFinal = 0;
                    }

                    /* Calculamos los porcentajes */

                    cantSeriesAsignadaAbn = ngService.findAllSeriesRangosCount(filtrosOcupados);
                    totalSeriesAbn = cantSeriesAsignadaAbn + ngService.findAllSeriesRangosCount(filtrosLibres);
                    porcentajeOcupacionSerie = new Float(0);
                    if (totalSeriesAbn > 0) {
                        porcentajeOcupacionSerie = (cantSeriesAsignadaAbn.floatValue()
                                / totalSeriesAbn.floatValue()) * 100;
                    }
                    porcentajeOcupacionSerie = (float) (Math.rint(porcentajeOcupacionSerie * 1000000) / 1000000);

                    cantNumeracionAsignadaAbn = ngService.getTotalNumOcupadaAbn(abn).intValue();
                    totalNumeracionAbn = totalSeriesAbn * 10000;
                    porcentajeOcupacionNumeracion = new Float(0);
                    if (totalNumeracionAbn > 0) {
                        porcentajeOcupacionNumeracion = (cantNumeracionAsignadaAbn.floatValue() / totalNumeracionAbn
                                .floatValue()) * 100;
                    }
                    porcentajeOcupacionNumeracion =
                            (float) (Math.rint(porcentajeOcupacionNumeracion * 1000000) / 1000000);

                    analizado = true;

                    if (totalSeriesAbn == 0) {
                        renderSerieOcupada = false;

                        analizado = false;
                        MensajesBean.addErrorMsg(MSG_ID,
                                "No se han encontrado numeraciones disponibles para el ABN seleccionado", "");

                    } else {
                        // Limpiamos los filtros de la tabla
                        RequestContext requestContext = RequestContext.getCurrentInstance();
                        requestContext.execute("PF('wid_detalleSeries').clearFilters()");
                    }

                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                    MensajesBean.addErrorMsg(MSG_ID,
                            "Ha ocurrido un error al cargar las tablas de numeracion disponible",
                            "");
                }
            } else {
                // Si la poblacion de la numeracion no tiene ABN lo informamos
                MensajesBean.addErrorMsg(MSG_ID,
                        "La poblacion de la numeración seleccionada no tiene ABN asignado", "");
            }
        }
    }

    /**
     * Crea la tabla de sugerencias de numeración.
     * @param actionEvent ActionEvent
     */
    public void sugerirAction(ActionEvent actionEvent) {

        lineasDisponiblesSeriesList.setEjecutar(false);
        lineasDisponiblesNoPSTList.setEjecutar(false);
        lineasDisponiblesPSTList.setEjecutar(false);

        boolean error = false;

        if (!(cantidadAsignar instanceof Long) || (cantidadAsignar < 1)) {
            MensajesBean.addErrorMsg(MSG_ID, "Cantidad a asignar inválida", "");
            error = true;
        } else if ((cantidadAsignar > cantidadSolicitada || cantidadAsignar > cantidadPorAsignar)) {
            MensajesBean.addErrorMsg(MSG_ID, "No se pueden asignar mas numeraciones que las solicitadas", "");
            error = true;
        }

        sugerenciaNumeracionList = new ArrayList<SugerenciaNumeracion>();

        Integer numInicial = new Integer(0);

        if (!error) {
            // Cargamos la tabla sugerencias
            for (int k = 0; k < numeracionAsignadaSerieList.size(); k++) {

                if (((numInicial + cantidadAsignar - 1) < Integer.parseInt(numeracionAsignadaSerieList.get(k)
                        .getNumInicio())) && ((numInicial + cantidadAsignar) < 10000)) {
                    SugerenciaNumeracion sugerencia = new SugerenciaNumeracion();
                    sugerencia.setNumInicial(new BigDecimal(numInicial));
                    sugerencia.setNumFinal(new BigDecimal(numInicial + cantidadAsignar - 1));
                    sugerenciaNumeracionList.add(sugerencia);
                }
                numInicial = Integer.parseInt(numeracionAsignadaSerieList.get(k).getNumFinal()) + 1;
            }
            if ((numInicial + cantidadAsignar - 1) < 10000) {
                SugerenciaNumeracion sugerencia = new SugerenciaNumeracion();
                sugerencia.setNumInicial(new BigDecimal(numInicial));
                sugerencia.setNumFinal(new BigDecimal(numInicial + cantidadAsignar - 1));
                sugerenciaNumeracionList.add(sugerencia);
            }
        }
    }

    /**
     * Accion que selecciona un rango de los disponibles.
     * @param actionEvent ActionEvent
     * @throws Exception Exception
     */
    public void seleccionarAction(ActionEvent actionEvent) {

        lineasDisponiblesSeriesList.setEjecutar(true);
        lineasDisponiblesNoPSTList.setEjecutar(true);
        lineasDisponiblesPSTList.setEjecutar(true);

        boolean error = false;

        if (!renderSerieOcupada && selectSerieLibre.getRegistrosSeleccionados().isEmpty()) {
            MensajesBean.addErrorMsg(MSG_ID, "Tiene que seleccionar una serie", "");
            error = true;
        } else {

            if (!(cantidadAsignar instanceof Long) || (cantidadAsignar < 1)) {
                MensajesBean.addErrorMsg(MSG_ID, "Cantidad a asignar inválida", "");
                error = true;

            } else if ((cantidadAsignar > cantidadSolicitada || cantidadAsignar > cantidadPorAsignar)) {
                MensajesBean.addErrorMsg(MSG_ID, "No se pueden asignar mas numeraciones que las solicitadas", "");
                error = true;
            }

            if (!(numeracionInicial instanceof Integer) || (numeracionInicial < 0)) {
                MensajesBean.addErrorMsg("MSG_numeracionInicial", "Numeración inicial inválida", "");
                error = true;
            }
        }
        if (!error) {
            try {

                boolean rangoIncompatible = false;
                if (renderSerieOcupada) {

                    // Comprobamos que el rango seleccionado no es incompatible a ningún rango ya existente en la serie
                    // seleccionada.
                    for (int i = 0; i < numeracionAsignadaSerieList.size(); i++) {
                        RangoSerie rango = numeracionAsignadaSerieList.get(i);
                        if ((numeracionInicial >= rango.getNumInicioAsInt() && numeracionInicial <= rango
                                .getNumFinalAsInt())
                                || (numeracionFinal >= rango.getNumInicioAsInt() && numeracionFinal <= rango
                                        .getNumFinalAsInt())
                                || (numeracionInicial < rango.getNumInicioAsInt() && numeracionFinal > rango
                                        .getNumFinalAsInt())) {

                            rangoIncompatible = true;
                            break;
                        }
                    }
                    if (!rangoIncompatible && (numeracionInicial + cantidadAsignar) <= 10000) {
                        addNumeracionSeleccionada(numeracionInicial, numeracionFinal, selectSerieOcupada);
                        renderSerieSelected = false;
                    } else {
                        rangoIncompatible = true;
                    }
                } else {
                    if (selectSerieLibre.getRegistrosSeleccionados().size() == 1
                            && (numeracionInicial + cantidadAsignar) <= 10000) {
                        if (numeracionFinal < 10000) {
                            addNumeracionSeleccionada(numeracionInicial, numeracionFinal, selectSerieLibre
                                    .getRegistrosSeleccionados().get(0));
                        } else {
                            addNumeracionSeleccionada(numeracionInicial, 9999, selectSerieLibre
                                    .getRegistrosSeleccionados().get(0));
                        }

                        renderSerieSelected = false;

                    } else if (selectSerieLibre.getRegistrosSeleccionados().size() > 1) {

                        Integer asignado = 0;
                        int i = 0;
                        while (asignado < cantidadAsignar && i < selectSerieLibre.getRegistrosSeleccionados().size()) {
                            if (asignado + 10000 < cantidadAsignar) {
                                addNumeracionSeleccionada(0, 9999, selectSerieLibre.getRegistrosSeleccionados().get(i));
                            } else {
                                addNumeracionSeleccionada(0, (int) (cantidadAsignar - asignado - 1), selectSerieLibre
                                        .getRegistrosSeleccionados().get(i));
                            }
                            asignado += 10000;
                            i++;
                        }
                        renderSerieSelected = false;
                    } else {
                        rangoIncompatible = true;
                    }
                }
                if (!rangoIncompatible) {
                    EstadoSolicitud estadoSolicitud = new EstadoSolicitud();
                    estadoSolicitud.setCodigo(EstadoSolicitud.SOLICITUD_EN_TRAMITE);
                    // Guardamos y actualizamos las tablas
                    solicitud.setEstadoSolicitud(estadoSolicitud);

                    guardarSeleccionAction();

                    cantidadAsignar = (long) 0;

                } else {
                    MensajesBean.addErrorMsg("MSG_numeracionInicial", "No es posible seleccionar el rango", "");
                }

            } catch (NumberFormatException e) {
                LOGGER.error("error", e);
                MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
            } catch (Exception e) {
                LOGGER.error("error", e);
            } finally {
                analizarAction();
                if (renderSerieOcupada) {
                    seleccionarSerieOcupada();
                }
            }
        }
    }

    /**
     * Regresa una numeracion seleccionada.
     * @param actionEvent ActionEvent
     */
    public void regresarSeleccionAction(ActionEvent actionEvent) {

        lineasDisponiblesSeriesList.setEjecutar(true);
        lineasDisponiblesNoPSTList.setEjecutar(true);
        lineasDisponiblesPSTList.setEjecutar(true);

        try {
            if (!selectedNumeracionSeleccionada.isEmpty() && avisosRangosCambiados.isEmpty()) {

                // Recorrido por los registros seleccionados de la tabla 'Numeración Seleccionada'
                for (NumeracionAsignada numSeleccionada : selectedNumeracionSeleccionada) {
                    // NumeracionSolicitada de la Solicitud

                    int index = solicitud.getNumeracionSolicitadas().indexOf(numSeleccionada.getNumeracionSolicitada());
                    RangoSerie numeracion = ngService.getRangoSerie(numSeleccionada.getNir().getId(),
                            numSeleccionada.getSna(), numSeleccionada.getInicioRango(),
                            numSeleccionada.getSolicitudAsignacion().getProveedorSolicitante());

                    int cantAsignada = solicitud.getNumeracionSolicitadas().get(index).getCantAsignada().intValue();
                    cantAsignada = cantAsignada
                            - (Integer.parseInt(numSeleccionada.getFinRango())
                                    - Integer.parseInt(numSeleccionada.getInicioRango()) + 1);
                    // Cantidad Asignada
                    solicitud.getNumeracionSolicitadas().get(index).setCantAsignada(new BigDecimal(cantAsignada));

                    // Eliminamos la numeración solicitada de la solicitud.
                    if (numeracion != null) {
                        // Eliminamos la numeración asignada de base de datos.
                        solicitud.removeRango(numeracion);
                        ngService.removeRangoSerie(numeracion);
                    }
                    solicitud.getNumeracionAsignadas().remove(numSeleccionada);

                }

                // Recalculamos los input de cantidad
                cantidadAsignar = (long) 0;
                if (selectResumenNumeracion != null) {
                    cantidadSolicitada = selectResumenNumeracion.getCantSolicitada().intValue();
                    cantidadPorAsignar = cantidadSolicitada - selectResumenNumeracion.getCantAsignada().intValue();
                }
                // Grabamos la solicitud con los cambios en las numeraciones solicitadas
                EstadoSolicitud estadoSolicitud = new EstadoSolicitud();
                estadoSolicitud.setCodigo(EstadoSolicitud.SOLICITUD_EN_TRAMITE);
                solicitud.setEstadoSolicitud(estadoSolicitud);
                this.guardarSeleccionAction();

            } else {
                if (avisosRangosCambiados.isEmpty()) {
                    MensajesBean.addErrorMsg(MSG_ID,
                            "Es necesario seleccionar almenos una numeración del resumen de numeraciones", "");
                } else {
                    StringBuffer sbAviso = new StringBuffer();
                    sbAviso.append("Las siguientes numeraciones han sido cedidas o redistribuidas ");
                    sbAviso.append("y no se pueden regresar: ");
                    MensajesBean.addErrorMsg(MSG_ID, sbAviso.toString(), "");
                    for (String aviso : avisosRangosCambiados) {
                        MensajesBean.addErrorMsg(MSG_ID, aviso, "");
                    }
                }
            }
        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0015);

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);

        } finally {
            // Actualizamos las tablas
            if (analizado) {
                analizarAction();
                if (renderSerieOcupada) {
                    seleccionarSerieOcupada();
                }
            }
        }
    }

    /**
     * Guarda la seleccion de rangos.
     * @return boolean
     */
    private boolean guardarSeleccionAction() {

        lineasDisponiblesSeriesList.setEjecutar(false);
        lineasDisponiblesNoPSTList.setEjecutar(false);
        lineasDisponiblesPSTList.setEjecutar(false);

        try {
            // Grabamos la solicitud con los cambios en las numeraciones solicitadas
            solicitud = ngService.saveSolicitudAsignacion(solicitud);
            this.getWizard().setSolicitud(solicitud);
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
        if (this.guardarSeleccionAction()) {
            // Mensaje de información al usuario.
            StringBuffer sBuf = new StringBuffer();
            sBuf.append(MensajesBean.getTextoResource("manual.generales.exito.guardar")).append(" ");
            sBuf.append(solicitud.getId());
            MensajesBean.addInfoMsg(MSG_ID, sBuf.toString(), "");
        }
    }

    /****************************************************************
     * ACCIONES DE INPUT TEXT
     ****************************************************************/

    /**
     * Accion al cambiar la numeración inicial.
     */
    public void numeracionInicialKeyUp() {
        lineasDisponiblesSeriesList.setEjecutar(false);
        lineasDisponiblesNoPSTList.setEjecutar(false);
        lineasDisponiblesPSTList.setEjecutar(false);

        if (numeracionInicial != null) {
            numeracionFinal = (int) (numeracionInicial + cantidadAsignar - 1);
        } else {
            numeracionFinal = 0;
        }

        if (numeracionFinal < 0) {
            numeracionFinal = 0;
        }
    }

    /**
     * Metodo privado con el que agregamos la numeracion seleccionada.
     * @param numInicial numInicial
     * @param numFinal numFinal
     * @param numSelect numSelect
     * @throws Exception Exception
     */
    private void addNumeracionSeleccionada(Integer numInicial,
            Integer numFinal, NumeracionDisponible numSelect) throws Exception {

        RangoSeriePK idRangoSerie = new RangoSeriePK();
        idRangoSerie.setSna(numSelect.getSerie().getId().getSna());
        idRangoSerie.setIdNir(numSelect.getSerie().getId().getIdNir());

        RangoSerie rangoSerieSeleccionada = new RangoSerie();
        rangoSerieSeleccionada.setId(idRangoSerie);
        rangoSerieSeleccionada.setSerie(numSelect.getSerie());
        rangoSerieSeleccionada.setPoblacion(selectNumeracion.getPoblacion());
        rangoSerieSeleccionada.setConcesionario(selectNumeracion.getConcesionario());
        rangoSerieSeleccionada.setArrendatario(selectNumeracion.getArrendatario());
        rangoSerieSeleccionada.setAsignatario(solicitud.getProveedorSolicitante());
        rangoSerieSeleccionada.setCentralOrigen(selectNumeracion.getCentralOrigen());
        rangoSerieSeleccionada.setCentralDestino(selectNumeracion.getCentralDestino());
        rangoSerieSeleccionada.setTipoModalidad(selectNumeracion.getTipoModalidad());
        rangoSerieSeleccionada.setTipoRed(selectNumeracion.getTipoRed());
        rangoSerieSeleccionada.setPoblacion(selectNumeracion.getPoblacion());
        rangoSerieSeleccionada.setSolicitud(solicitud);
        rangoSerieSeleccionada.setIdaPnn(selectNumeracion.getIdaPnn());
        rangoSerieSeleccionada.setIdoPnn(selectNumeracion.getIdoPnn());
        rangoSerieSeleccionada.setNumSolicitada(selectNumeracion);
        rangoSerieSeleccionada.setNumInicio(String.format("%04d", numInicial));
        rangoSerieSeleccionada.setNumFinal(String.format("%04d", numFinal));

        EstadoRango estadoRango = new EstadoRango();
        estadoRango.setCodigo(EstadoRango.PENDIENTE);
        rangoSerieSeleccionada.setEstadoRango(estadoRango);

        NumeracionAsignada numeracionSeleccionada = new NumeracionAsignada(rangoSerieSeleccionada);

        cantidadPorAsignar = cantidadPorAsignar - (numFinal - numInicial + 1);
        selectResumenNumeracion.setCantAsignada(new BigDecimal(cantidadSolicitada - cantidadPorAsignar));

        solicitud.addNumeracionAsignada(numeracionSeleccionada);

        // Agregamos al rango a la solicitud para que se persista al guardar.
        solicitud.addRango(rangoSerieSeleccionada);
    }

    /**
     * Accion al seleccionar una serie ocupada.
     */
    private void seleccionarSerieOcupada() {

        numeracionAsignadaSerieList = new ArrayList<RangoSerie>();

        List<RangoSerie> numeracionAsignadaSerieListAux = ngService.findNumeracionesAsiganadasSerie(
                selectSerieOcupada.getSerie().getNir().getAbn()
                , selectSerieOcupada.getSerie().getNir(), selectSerieOcupada.getSerie().getId().getSna());

        for (int i = 0; i < numeracionAsignadaSerieListAux.size(); i++) {
            RangoSerie numeracionAsignada = new RangoSerie();
            numeracionAsignada = numeracionAsignadaSerieListAux.get(i);

            numeracionAsignadaSerieList.add(numeracionAsignada);
        }

        renderSerieSelected = true;
        renderSerieOcupada = true;
    }

    // Resto de métodos
    @Override
    public void resetTab() {
        cantidadAsignar = new Long(0);
        cantidadPorAsignar = new Integer(0);
        cantidadSolicitada = new Integer(0);

        selectSerieLibre.clear();

        selectResumenNumeracion = null;
        selectNumeracion = null;

        renderColumnasComercializadora = Boolean.FALSE;
        renderSerieSelected = Boolean.FALSE;
        renderSerieOcupada = Boolean.FALSE;

        analizado = false;

        lineasDisponiblesPSTList = new NumDisponibleLazyModel();
        lineasDisponiblesNoPSTList = new NumDisponibleLazyModel();
        lineasDisponiblesSeriesList = new NumDisponibleLazyModel();
    }

    @Override
    public boolean isAvanzar() {
        boolean resultado = true;

        if (solicitud.getNumeracionAsignadas().isEmpty()) {
            this.setSummaryError(Errores.getDescripcionError(Errores.ERROR_0011));
            resultado = false;
        } else {
            // Guardamos los cambios que hayan podido quedar pendientes.
            this.guardarSeleccionAction();
            resultado = true;
        }

        return resultado;
    }

    @Override
    public void actualizaCampos() {
        try {
            // Reseteamos las variables del formulario
            // this.resetTab();

            // Actualizamos la solicitud con la última versión.
            this.solicitud = (SolicitudAsignacion) getWizard().getSolicitud();

            // Columnas específicas de Comercializador o Ambos
            if (solicitud.getProveedorSolicitante().getTipoProveedor().getCdg().equals(TipoProveedor.COMERCIALIZADORA)
                    || solicitud.getProveedorSolicitante().getTipoProveedor().getCdg().equals(TipoProveedor.AMBOS)) {
                renderColumnasComercializadora = true;
            }

            // Actualizamos la tabla de Análisis
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.execute("PF('wid_numeracionSeleccionada').clearFilters()");
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Getter del para el fileDownload.
     * @return the analisisNumeracion
     * @throws Exception error
     */
    public StreamedContent getAnalisisNumeracion() throws Exception {

        lineasDisponiblesSeriesList.setEjecutar(false);
        lineasDisponiblesNoPSTList.setEjecutar(false);
        lineasDisponiblesPSTList.setEjecutar(false);

        InputStream stream = ngService.generarAnalisisNumeracion(solicitud);

        String docName = "ANALISIS_NUMERACION";
        docName = docName.concat("_").concat(solicitud.getProveedorSolicitante().getNombreCorto());

        Calendar fechaActual = Calendar.getInstance();
        docName = docName.concat("_").concat(Integer.toString(fechaActual.get(Calendar.DAY_OF_MONTH)))
                .concat(Integer.toString(fechaActual.get(Calendar.MONTH) + 1))
                .concat(Integer.toString(fechaActual.get(Calendar.YEAR)));

        docName = docName.concat(".xlsx");

        stream.close();

        LOGGER.debug("docname {}", docName);
        return new DefaultStreamedContent(stream,
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", docName);
    }

    /*********************************************************************
     * PROPIEDADES PARA LA PARTE VIEW DE JSF
     *********************************************************************/

    /**
     * Obtiene Porcentaje ocupacion numeracion.
     * @return porcentajeOcupacionNumeracion
     */
    public Float getPorcentajeOcupacionNumeracion() {
        return porcentajeOcupacionNumeracion;
    }

    /**
     * Obtiene Render si el PST solicitante es comercializador.
     * @return renderColumnasComercializadora
     */
    public boolean isRenderColumnasComercializadora() {
        return renderColumnasComercializadora;
    }

    /**
     * Obtiene Render si se selecciona una serie libre.
     * @return the renderSerieSelected
     */
    public boolean isRenderSerieSelected() {
        return renderSerieSelected;
    }

    /**
     * Obtiene Render si se selecciona una serie ocupada.
     * @return the renderSerieOcupada
     */
    public boolean isRenderSerieOcupada() {
        return renderSerieOcupada;
    }

    /**
     * Obtiene Cantidad a asignar.
     * @return cantidadAsignar
     */
    public Long getCantidadAsignar() {
        return cantidadAsignar;
    }

    /**
     * Carga Cantidad a asignar.
     * @param cantidadAsignar cantidadAsignar to set
     */
    public void setCantidadAsignar(Long cantidadAsignar) {
        this.cantidadAsignar = cantidadAsignar;
    }

    /**
     * Obtiene Cantidad por asignar.
     * @return the cantidadPorAsignar
     */
    public Integer getCantidadPorAsignar() {
        return cantidadPorAsignar;
    }

    /**
     * Carga Cantidad por asignar.
     * @param cantidadPorAsignar cantidadPorAsignar to set
     */
    public void setCantidadPorAsignar(Integer cantidadPorAsignar) {
        this.cantidadPorAsignar = cantidadPorAsignar;
    }

    /**
     * Obtiene Cantidad solicitada.
     * @return cantidadSolicitada
     */
    public Integer getCantidadSolicitada() {
        return cantidadSolicitada;
    }

    /**
     * Carga Cantidad solicitada.
     * @param cantidadSolicitada cantidadSolicitada to set
     */
    public void setCantidadSolicitada(Integer cantidadSolicitada) {
        this.cantidadSolicitada = cantidadSolicitada;
    }

    /**
     * Obtiene Numeracion inicial.
     * @return numeracionInicial
     */
    public Integer getNumeracionInicial() {
        return numeracionInicial;
    }

    /**
     * Carga Numeracion inicial.
     * @param numeracionInicial numeracionInicial to set
     */
    public void setNumeracionInicial(Integer numeracionInicial) {
        this.numeracionInicial = numeracionInicial;
    }

    /**
     * Obtiene Numeracion final.
     * @return numeracionFinal
     */
    public Integer getNumeracionFinal() {
        return numeracionFinal;
    }

    /**
     * Carga Numeracion final.
     * @param numeracionFinal numeracionFinal to set
     */
    public void setNumeracionFinal(Integer numeracionFinal) {
        this.numeracionFinal = numeracionFinal;
    }

    /**
     * Obtiene Cantidad series asignada al ABN.
     * @return the cantSeriesAsignadaAbn
     */
    public Integer getCantSeriesAsignadaAbn() {
        return cantSeriesAsignadaAbn;
    }

    /**
     * Obtiene Total series en ABN.
     * @return totalSeriesAbn
     */
    public Integer getTotalSeriesAbn() {
        return totalSeriesAbn;
    }

    /**
     * Obtiene Numeracion asignada en ABN.
     * @return cantNumeracionAsignadaAbn
     */
    public Integer getCantNumeracionAsignadaAbn() {
        return cantNumeracionAsignadaAbn;
    }

    /**
     * Obtiene Total numeracion en ABN.
     * @return totalNumeracionAbn
     */
    public Integer getTotalNumeracionAbn() {
        return totalNumeracionAbn;
    }

    /**
     * Obtiene Lista numeraciones disponibles por PST.
     * @return lineasDisponiblesPSTList
     */
    public NumDisponibleLazyModel getLineasDisponiblesPSTList() {
        return lineasDisponiblesPSTList;
    }

    /**
     * Carga Lista numeraciones disponibles por PST.
     * @param lineasDisponiblesPSTList lineasDisponiblesPSTList to set
     */
    public void setLineasDisponiblesPSTList(NumDisponibleLazyModel lineasDisponiblesPSTList) {
        this.lineasDisponiblesPSTList = lineasDisponiblesPSTList;
    }

    /**
     * Obtiene Lista numeraciones disponibles por otros PST.
     * @return lineasDisponiblesNoPSTList
     */
    public NumDisponibleLazyModel getLineasDisponiblesNoPSTList() {
        return lineasDisponiblesNoPSTList;
    }

    /**
     * Carga Lista numeraciones disponibles por otros PST.
     * @param lineasDisponiblesNoPSTList the lineasDisponiblesNoPSTList to set
     */
    public void setLineasDisponiblesNoPSTList(NumDisponibleLazyModel lineasDisponiblesNoPSTList) {
        this.lineasDisponiblesNoPSTList = lineasDisponiblesNoPSTList;
    }

    /**
     * Obtiene Lista numeraciones disponibles por series libres.
     * @return the lineasDisponiblesSeriesList
     */
    public NumDisponibleLazyModel getLineasDisponiblesSeriesList() {
        return lineasDisponiblesSeriesList;
    }

    /**
     * Carga Lista numeraciones disponibles por series libres.
     * @param lineasDisponiblesSeriesList lineasDisponiblesSeriesList to set
     */
    public void setLineasDisponiblesSeriesList(NumDisponibleLazyModel lineasDisponiblesSeriesList) {
        this.lineasDisponiblesSeriesList = lineasDisponiblesSeriesList;
    }

    /**
     * Obtiene Lista rangos seleccionados.
     * @return numeracionAsignadaSerieList
     */
    public List<RangoSerie> getNumeracionAsignadaSerieList() {
        return numeracionAsignadaSerieList;
    }

    /**
     * Carga Lista rangos seleccionados.
     * @param numeracionAsignadaSerieList numeracionAsignadaSerieList to set
     */
    public void setNumeracionAsignadaSerieList(List<RangoSerie> numeracionAsignadaSerieList) {
        this.numeracionAsignadaSerieList = numeracionAsignadaSerieList;
    }

    /**
     * Obtiene Lista sugerencia de rangos seleccionados.
     * @return sugerenciaNumeracionList
     */
    public List<SugerenciaNumeracion> getSugerenciaNumeracionList() {
        return sugerenciaNumeracionList;
    }

    /**
     * Carga Lista sugerencia de rangos seleccionados.
     * @param sugerenciaNumeracionList sugerenciaNumeracionList to set
     */
    public void setSugerenciaNumeracionList(List<SugerenciaNumeracion> sugerenciaNumeracionList) {
        this.sugerenciaNumeracionList = sugerenciaNumeracionList;
    }

    /**
     * Obtiene Numeración solicitada de resumen.
     * @return selectResumenNumeracion
     */
    public NumeracionSolicitada getSelectResumenNumeracion() {
        return selectResumenNumeracion;
    }

    /**
     * Carga Numeración solicitada de resumen.
     * @param selectResumenNumeracion selectResumenNumeracion to set
     */
    public void setSelectResumenNumeracion(NumeracionSolicitada selectResumenNumeracion) {
        this.selectResumenNumeracion = selectResumenNumeracion;
    }

    /**
     * Obtiene Numeración solicitada de seleccion.
     * @return the selectNumeracion
     */
    public NumeracionSolicitada getSelectNumeracion() {
        return selectNumeracion;
    }

    /**
     * Carga Numeración solicitada de seleccion.
     * @param selectNumeracion selectNumeracion to set
     */
    public void setSelectNumeracion(NumeracionSolicitada selectNumeracion) {
        this.selectNumeracion = selectNumeracion;
    }

    /**
     * Obtiene Seleccion Numeración ocupada.
     * @return selectSerieOcupada
     */
    public NumeracionDisponible getSelectSerieOcupada() {
        return selectSerieOcupada;
    }

    /**
     * Carga Seleccion Numeración ocupada.
     * @param selectSerieOcupada selectSerieOcupada to set
     */
    public void setSelectSerieOcupada(NumeracionDisponible selectSerieOcupada) {
        this.selectSerieOcupada = selectSerieOcupada;
    }

    /**
     * Obtiene Seleccion Numeración por PST.
     * @return selectNumDispoPST
     */
    public NumeracionDisponible getSelectNumDispoPST() {
        return selectNumDispoPST;
    }

    /**
     * Carga Seleccion Numeración por PST.
     * @param selectNumDispoPST selectNumDispoPST to set
     */
    public void setSelectNumDispoPST(NumeracionDisponible selectNumDispoPST) {
        this.selectNumDispoPST = selectNumDispoPST;
    }

    /**
     * obtiene Seleccion Numeración por otro PST.
     * @return the selectNumDispoNoPST
     */
    public NumeracionDisponible getSelectNumDispoNoPST() {
        return selectNumDispoNoPST;
    }

    /**
     * Carga Seleccion Numeración por otro PST.
     * @param selectNumDispoNoPST the selectNumDispoNoPST to set
     */
    public void setSelectNumDispoNoPST(NumeracionDisponible selectNumDispoNoPST) {
        this.selectNumDispoNoPST = selectNumDispoNoPST;
    }

    /**
     * Obtiene Serie libre.
     * @return selectSerieLibre
     */
    public MultiSelectionOnLazyModelManager<NumeracionDisponible> getSelectSerieLibre() {
        return selectSerieLibre;
    }

    /**
     * Carga Serie libre.
     * @param selectSerieLibre selectSerieLibre to set
     */
    public void setSelectSerieLibre(MultiSelectionOnLazyModelManager<NumeracionDisponible> selectSerieLibre) {
        this.selectSerieLibre = selectSerieLibre;
    }

    /**
     * Obtiene Sugerencia numeracion seleccionada.
     * @return sugerenciaNumeracionSelect
     */
    public SugerenciaNumeracion getSugerenciaNumeracionSelect() {
        return sugerenciaNumeracionSelect;
    }

    /**
     * Carga Sugerencia numeracion seleccionada.
     * @param sugerenciaNumeracionSelect sugerenciaNumeracionSelect to set
     */
    public void setSugerenciaNumeracionSelect(SugerenciaNumeracion sugerenciaNumeracionSelect) {
        this.sugerenciaNumeracionSelect = sugerenciaNumeracionSelect;
    }

    /**
     * Obtiene Seleccion multiple en la tabla de numeraciones seleccionada.
     * @return selectedNumeracionSeleccionada
     */
    public List<NumeracionAsignada> getSelectedNumeracionSeleccionada() {
        return selectedNumeracionSeleccionada;
    }

    /**
     * Carga Seleccion multiple en la tabla de numeraciones seleccionada.
     * @param selectedNumeracionSeleccionada selectedNumeracionSeleccionada to set
     */
    public void setSelectedNumeracionSeleccionada(List<NumeracionAsignada> selectedNumeracionSeleccionada) {
        this.selectedNumeracionSeleccionada = selectedNumeracionSeleccionada;
    }

    /**
     * Obtiene Solicitud de asignacion.
     * @return solicitud
     */
    public SolicitudAsignacion getSolicitud() {
        return solicitud;
    }

    /**
     * Obtiene Porcentaja ocupacion serie.
     * @return the porcentajeOcupacionSerie
     */
    public Float getPorcentajeOcupacionSerie() {
        return porcentajeOcupacionSerie;
    }

    /**
     * Obtiene boolean de la accion de analizar.
     * @return analizado
     */
    public boolean isAnalizado() {
        return analizado;
    }

    /**
     * @return the registroPorPagina
     */
    public int getRegistroPorPagina() {
        return registroPorPagina;
    }

    /**
     * @param registroPorPagina the registroPorPagina to set
     */
    public void setRegistroPorPagina(int registroPorPagina) {
        this.registroPorPagina = registroPorPagina;
    }

    /**
     * Lista de Numeraciones Asignadas Filtradas en la tabla de Numeraciones Seleccionadas.
     * @return List
     */
    public List<NumeracionAsignada> getListNumAsigFiltradas() {
        return listNumAsigFiltradas;
    }

    /**
     * Lista de Numeraciones Asignadas Filtradas en la tabla de Numeraciones Seleccionadas.
     * @param listNumAsigFiltradas List
     */
    public void setListNumAsigFiltradas(List<NumeracionAsignada> listNumAsigFiltradas) {
        this.listNumAsigFiltradas = listNumAsigFiltradas;
    }

}
