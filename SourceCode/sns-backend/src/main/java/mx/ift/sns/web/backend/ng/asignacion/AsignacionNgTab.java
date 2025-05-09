package mx.ift.sns.web.backend.ng.asignacion;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mx.ift.sns.modelo.ng.NumeracionAsignada;
import mx.ift.sns.modelo.ng.NumeracionSolicitada;
import mx.ift.sns.modelo.ng.RangoSerie;
import mx.ift.sns.modelo.ng.RangoSeriePK;
import mx.ift.sns.modelo.ng.SolicitudAsignacion;
import mx.ift.sns.modelo.series.EstadoRango;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.negocio.exceptions.RegistroModificadoException;
import mx.ift.sns.negocio.ng.INumeracionGeograficaService;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.components.TabWizard;
import mx.ift.sns.web.backend.components.Wizard;

import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pestaña de asignaciones.
 */
public class AsignacionNgTab extends TabWizard {

    /** Serial uid. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AsignacionNgTab.class);

    /** Id de mensajes de error. */
    private static final String MSG_ID = "MSG_Asignacion";

    /** Facade de Servicios de Numeración Geográfica. */
    private INumeracionGeograficaService ngService;

    /** Campo solicitud asignación. */
    private SolicitudAsignacion solicitud;

    /** Numeracion a Asignar. */
    private List<NumeracionAsignada> numeracionAsignadaList;

    /** Total registros. */
    private Integer totalRegistros;

    /** Total numeros asignados de la solicitud. */
    private Integer totalNumerosAsignados;

    /** Indica si se han asignado los rangos solicitados. */
    private boolean asignado = false;

    /** Se encarga de guardar el número de registros por página al cargar la misma. */
    private int registroPorPagina;

    /**
     * Constructor de la clase de respaldo para el tab/pestaña de 'Asignación'.
     * @param pBeanPadre Referencia a la clase Wizard que instancia este Tab
     * @param pNgService Facade de Numeración Geográfica
     */
    public AsignacionNgTab(Wizard pBeanPadre, INumeracionGeograficaService pNgService) {
        try {
            // Facade de Numeración Geográfica.
            this.ngService = pNgService;

            // Asociamos el Wizard padre a la pestaña que reprsenta este Tab
            setWizard(pBeanPadre);

            // Inicializaciones.
            numeracionAsignadaList = new ArrayList<NumeracionAsignada>();
            totalRegistros = new Integer(0);
            totalNumerosAsignados = new Integer(0);

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
            this.setSummaryError(Errores.getDescripcionError(Errores.ERROR_0004));
        }
    }

    /** Asigna las numeraciones solicitadas. */
    public void asignarAction() {
        try {
            EstadoRango estadoRango = ngService.getEstadoRangoByCodigo(EstadoRango.ASIGNADO);

            for (NumeracionAsignada numeracionAsignada : numeracionAsignadaList) {
                int indexNumAsig = solicitud.getNumeracionAsignadas().indexOf(numeracionAsignada);
                solicitud.getNumeracionAsignadas().get(indexNumAsig).setEstatus(estadoRango);

                RangoSerie rango = ngService.getRangoSerie(numeracionAsignada.getNir().getId(),
                        numeracionAsignada.getSna(), numeracionAsignada.getInicioRango(),
                        numeracionAsignada.getSolicitudAsignacion().getProveedorSolicitante());

                if (rango != null) {
                    int indexRango = solicitud.getRangos().indexOf(rango);
                    if (rango.getEstadoRango().getCodigo().equals(EstadoRango.PENDIENTE)) {
                        // Actualizamos rango asignando
                        solicitud.getRangos().get(indexRango).setEstadoRango(estadoRango);
                        solicitud.getRangos().get(indexRango).setFechaAsignacion(new Date());
                        solicitud.getRangos().get(indexRango).setConsecutivoAsignacion(solicitud.getId());
                    }
                } else {

                    RangoSeriePK idRangoSerie = new RangoSeriePK();
                    idRangoSerie.setSna(numeracionAsignada.getSna());
                    idRangoSerie.setIdNir(numeracionAsignada.getNir().getId());

                    RangoSerie rangoSerieSeleccionada = new RangoSerie();
                    rangoSerieSeleccionada.setId(idRangoSerie);
                    NumeracionSolicitada selectNumeracion = numeracionAsignada.getNumeracionSolicitada();

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
                    rangoSerieSeleccionada.setNumInicio(numeracionAsignada.getInicioRango());
                    rangoSerieSeleccionada.setNumFinal(numeracionAsignada.getFinRango());
                    rangoSerieSeleccionada.setEstadoRango(estadoRango);

                    // Agregamos al rango a la solicitud para que se persista al guardar.
                    solicitud.addRango(rangoSerieSeleccionada);

                }

            }
            EstadoSolicitud estadoSolicitud = new EstadoSolicitud();
            estadoSolicitud.setCodigo(EstadoSolicitud.SOLICITUD_TERMINADA);
            solicitud.setEstadoSolicitud(estadoSolicitud);
            solicitud.setFechaAsignacion(new Date());

            // Actualizamos la solicitud del Wizard
            solicitud = ngService.saveSolicitudAsignacion(solicitud);
            this.getWizard().setSolicitud(solicitud);

            // Habilitamos el botón de guardar e indicamos que ya se pueden generar los oficios.
            asignado = true;

            // Total de Numeración asignada a la solicitud.
            totalNumerosAsignados = ngService.getTotalNumAsignadaSolicitud(solicitud.getId()).intValue();

            MensajesBean.addInfoMsg(MSG_ID, "Se ha asignado correctamente la numeracion solicitada", "");

        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0015);
            asignado = false;
        } catch (Exception e) {
            asignado = false;
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Carga la lista de numeraciones que se van a asignar.
     * @throws Exception error
     */
    private void actualizarRangosSolicitados() throws Exception {

        List<String> avisosRangosCambiados = new ArrayList<String>();
        numeracionAsignadaList.clear();
        for (NumeracionAsignada numAsignada : solicitud.getNumeracionAsignadas()) {

            if (!numAsignada.getEstatus().getCodigo().equals(EstadoRango.ASIGNADO)) {
                // Comprobamos que la numeracion tiene un rango correspondiente o si la numeracion esta libre
                RangoSerie rango = ngService.getRangoSerie(numAsignada.getNir().getId(),
                        numAsignada.getSna(), numAsignada.getInicioRango(),
                        numAsignada.getSolicitudAsignacion().getProveedorSolicitante());
                if (rango != null
                        && numAsignada.getSolicitudAsignacion().getId().compareTo(rango.getSolicitud().getId()) == 0) {
                    numeracionAsignadaList.add(numAsignada);
                } else if (rango == null
                        && ngService.isRangoLibre(numAsignada.getNir().getId(), numAsignada.getSna(),
                                numAsignada.getInicioRango(), numAsignada.getFinRango())) {
                    // Si la numeracion esta libre tambien es correcta
                    numeracionAsignadaList.add(numAsignada);
                } else {
                    // Si la numeracion no es correcta avisamos
                    StringBuffer sbAviso = new StringBuffer();
                    sbAviso.append(" NIR: ").append(numAsignada.getNir().getCodigo());
                    sbAviso.append(", SNA: ").append(numAsignada.getSna());
                    sbAviso.append(", Inicio: ").append(numAsignada.getInicioRango());
                    sbAviso.append(", Final: ").append(numAsignada.getFinRango()).append("<br>");
                    avisosRangosCambiados.add(sbAviso.toString());
                }
            }

        }

        if (!avisosRangosCambiados.isEmpty()) {
            StringBuffer sbAviso = new StringBuffer();
            sbAviso.append(
                    "Las siguientes numeraciones han sido cedidas o redistribuidas y no se pueden reasignar:<br>");
            for (String aviso : avisosRangosCambiados) {
                sbAviso.append(aviso);
            }
            MensajesBean.addErrorMsg(MSG_ID, sbAviso.toString(), "");
        }

        totalRegistros = solicitud.getNumeracionAsignadas().size();
        totalNumerosAsignados = ngService.getTotalNumAsignadaSolicitud(solicitud.getId()).intValue();

    }

    @Override
    public boolean isAvanzar() {
        try {

            if (asignado) {
                return true;
            } else {
                MensajesBean.addErrorMsg(MSG_ID,
                        "Por favor, asigne la numeración solicitada antes de ir al siguiente paso.", "");
                return false;
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
            return false;
        }
    }

    @Override
    public void actualizaCampos() {
        try {
            // Reseteamos las variables del formulario
            this.resetTab();

            // Actualizamos la solicitud con la última versión.
            this.solicitud = (SolicitudAsignacion) getWizard().getSolicitud();

            // Actualizamos la tabla de Análisis
            this.actualizarRangosSolicitados();

            // Marca de Numeración asignada.
            asignado = solicitud.getEstadoSolicitud().getCodigo().equals(EstadoSolicitud.SOLICITUD_TERMINADA);

            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.execute("PF('numeracionAsignada_wid').clearFilters()");

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    @Override
    public void resetTab() {
        numeracionAsignadaList.clear();
        totalRegistros = new Integer(0);
        totalNumerosAsignados = new Integer(0);
        asignado = false;
    }

    // GETTERS & SETTERS

    /**
     * Obtiene Tabla resumen.
     * @return numeracionAsignadaList
     */
    public List<NumeracionAsignada> getNumeracionAsignadaList() {
        return numeracionAsignadaList;
    }

    /**
     * Obtiene Total registros.
     * @return the totalRegistros
     */
    public Integer getTotalRegistros() {
        return totalRegistros;
    }

    /**
     * Obtiene Total numeros asignados de la solicitud.
     * @return totalNumerosAsignados
     */
    public Integer getTotalNumerosAsignados() {
        return totalNumerosAsignados;
    }

    /**
     * Indica si se han asignado los rangos solicitados..
     * @return boolean
     */
    public boolean isAsignado() {
        return asignado;
    }

    /**
     * Campo solicitud asignación.
     * @return SolicitudAsignacion
     */
    public SolicitudAsignacion getSolicitud() {
        return solicitud;
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
}
