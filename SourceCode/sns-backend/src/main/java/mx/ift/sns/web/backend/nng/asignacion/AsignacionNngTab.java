package mx.ift.sns.web.backend.nng.asignacion;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import mx.ift.sns.modelo.nng.NumeracionAsignadaNng;
import mx.ift.sns.modelo.nng.NumeracionSolicitadaNng;
import mx.ift.sns.modelo.nng.RangoSerieNng;
import mx.ift.sns.modelo.nng.RangoSerieNngPK;
import mx.ift.sns.modelo.nng.SolicitudAsignacionNng;
import mx.ift.sns.modelo.nng.TipoAsignacion;
import mx.ift.sns.modelo.pst.TipoProveedor;
import mx.ift.sns.modelo.series.EstadoRango;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.negocio.exceptions.RegistroModificadoException;
import mx.ift.sns.negocio.nng.INumeracionNoGeograficaFacade;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.components.TabWizard;
import mx.ift.sns.web.backend.components.Wizard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pestaña de asignaciones.
 */
public class AsignacionNngTab extends TabWizard {

    /** Serial uid. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AsignacionNngTab.class);

    /** Id de mensajes de error. */
    private static final String MSG_ID = "MSG_Asignacion";

    /** Facade de Servicios de Numeración No Geográfica. */
    private INumeracionNoGeograficaFacade nngFacade;

    /** Campo solicitud asignación. */
    private SolicitudAsignacionNng solicitud;

    /** Numeraciones para asignar. */
    private List<NumeracionAsignadaNng> numeracionParaAsignarList;

    /** Tabla numeracion por asignar. */
    private List<NumeracionAsignadaNng> numeracionPorAsignarList;

    /** Indica si se han asignado los rangos solicitados. */
    private boolean asignado = false;

    /** Proveedor Solicitante es Concesionario. */
    private boolean pstConcesionario;

    /** Proveedor Solicitante es Comercializador. */
    private boolean pstComercializador;

    /** Proveedor Solicitante es de ampos tipos. */
    private boolean pstAmbos;

    /** Solicitud es de tipo Especifica. */
    private boolean especifica;

    /** Estado del rango. */
    private EstadoRango estadoRango;

    /** Se encarga de guardar el número de registros por página al cargar la misma. */
    private int registroPorPagina;

    /**
     * Constructor de la clase de respaldo para el tab/pestaña de 'Asignación'.
     * @param pBeanPadre Referencia a la clase Wizard que instancia este Tab
     * @param pNngFacade Facade de Numeración no Geográfica
     */
    public AsignacionNngTab(Wizard pBeanPadre, INumeracionNoGeograficaFacade pNngFacade) {
        try {
            // Facade de Numeración No Geográfica.
            this.nngFacade = pNngFacade;

            // Asociamos el Wizard padre a la pestaña que reprsenta este Tab
            setWizard(pBeanPadre);

            // Inicializaciones.
            numeracionPorAsignarList = new ArrayList<NumeracionAsignadaNng>();
            numeracionParaAsignarList = new ArrayList<NumeracionAsignadaNng>();
            estadoRango = nngFacade.getEstadoRangoByCodigo(EstadoRango.ASIGNADO);
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
            this.setSummaryError(Errores.getDescripcionError(Errores.ERROR_0004));
        }
    }

    private int findNumeracionInList(NumeracionSolicitadaNng numeracionSolicitada){
        int index = 0;
        for( NumeracionSolicitadaNng numeracionSolicitadaNng : solicitud.getNumeracionesSolicitadas() ){

            if( numeracionSolicitadaNng.getId().compareTo(numeracionSolicitada.getId()) == 0 ){
                return index;
            }
            index++;
        }

        return -1;
    }

    /**
     * Guarda los cambios realizados hasta el momento en el trámite.
     * @return 'True' si se ha podido guardar correctamente.
     */
    private boolean asignarAction() {
        try {
            Calendar fechaAsignacion = Calendar.getInstance();

            for (NumeracionAsignadaNng numeracionAsignada : numeracionParaAsignarList) {
                //int indexNumSol = solicitud.getNumeracionesSolicitadas().indexOf(numeracionAsignada.getNumeracionSolicitada());
                NumeracionSolicitadaNng numSol = solicitud.getNumeracionesSolicitadas().get(
                        findNumeracionInList(numeracionAsignada.getNumeracionSolicitada())
                );
                int indexNumAsig = numSol.getNumeracionesAsignadas().indexOf(numeracionAsignada);
                numSol.getNumeracionesAsignadas().get(indexNumAsig).setEstatus(estadoRango);

                // Obtenemos el rango asocioado a la numeracion asignada
                RangoSerieNng rango = nngFacade.getRangoSerie(numeracionAsignada.getClaveServicio().getCodigo(),
                        numeracionAsignada.getSna(), numeracionAsignada.getInicioRango(),
                        solicitud.getProveedorSolicitante());

                if (rango != null) {
                    int indexRango = solicitud.getRangosNng().indexOf(rango);
                    if (rango.getEstatus().getCodigo().equals(EstadoRango.PENDIENTE)) {
                        // Actualizamos rango asignando
                        solicitud.getRangosNng().get(indexRango).setEstatus(estadoRango);
                        solicitud.getRangosNng().get(indexRango).setFechaAsignacion(new Date());
                        solicitud.getRangosNng().get(indexRango).setConsecutivoAsignacion(solicitud.getId());
                    }
                } else {
                    // Si no existe el rango es que se ha liberado por lo que creamos uno para reasignarlo
                    RangoSerieNngPK idRangoSerie = new RangoSerieNngPK();
                    idRangoSerie.setSna(numeracionAsignada.getSna());
                    idRangoSerie.setIdClaveServicio(numeracionAsignada.getClaveServicio().getCodigo());

                    RangoSerieNng rangoReasignado = new RangoSerieNng();
                    rangoReasignado.setId(idRangoSerie);
                    NumeracionSolicitadaNng selectNumeracion = numeracionAsignada.getNumeracionSolicitada();

                    rangoReasignado.setConcesionario(selectNumeracion.getConcesionario());
                    rangoReasignado.setArrendatario(selectNumeracion.getArrendatario());
                    rangoReasignado.setAsignatario(solicitud.getProveedorSolicitante());
                    rangoReasignado.setCliente(selectNumeracion.getCliente());
                    rangoReasignado.setSolicitud(solicitud);
                    rangoReasignado.setAbc(selectNumeracion.getAbc());
                    rangoReasignado.setBcd(selectNumeracion.getBcd());
                    rangoReasignado.setNumeracionSolicitada(selectNumeracion);
                    rangoReasignado.setNumInicio(numeracionAsignada.getInicioRango());
                    rangoReasignado.setNumFinal(numeracionAsignada.getFinRango());
                    rangoReasignado.setEstatus(estadoRango);

                    // Agregamos al rango a la solicitud para que se persista al guardar.
                    solicitud.addRangoNng(rangoReasignado);

                }

            }

            EstadoSolicitud estadoSolicitud = new EstadoSolicitud();
            estadoSolicitud.setCodigo(EstadoSolicitud.SOLICITUD_TERMINADA);
            solicitud.setEstadoSolicitud(estadoSolicitud);
            solicitud.setFechaAsignacion(fechaAsignacion.getTime());

            // Actualizamos la solicitud del Wizard
            solicitud = nngFacade.saveSolicitudAsignacion(solicitud);
            this.getWizard().setSolicitud(solicitud);

            // Habilitamos el botón de guardar e indicamos que ya se pueden generar los oficios.
            asignado = true;

            MensajesBean.addInfoMsg(MSG_ID, "Se ha asignado correctamente la numeracion solicitada", "");
        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0015);
            asignado = false;
        } catch (Exception e) {
            asignado = false;
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        } finally {
            numeracionPorAsignarList = nngFacade.findAllNumeracionAsignadaBySolicitud(solicitud);
        }

        return asignado;
    }

    /**
     * Método invocado al pulsar sobre el botón 'Guardar'.
     */
    public void guardarCambiosManual() {
        // Guardamos los cambios realizados en la solicitud.
        if (this.asignarAction()) {
            // Mensaje de información al usuario.
            StringBuffer sBuf = new StringBuffer();
            sBuf.append(MensajesBean.getTextoResource("manual.generales.exito.guardar")).append(" ");
            sBuf.append(solicitud.getId());
            MensajesBean.addInfoMsg(MSG_ID, sBuf.toString(), "");
        }
    }

    @Override
    public boolean isAvanzar() {
        if (asignado) {
            return true;
        } else {
            MensajesBean.addErrorMsg(MSG_ID,
                    "Por favor, asigne la numeración solicitada antes de ir al siguiente paso.", "");
            return false;
        }
    }

    @Override
    public void actualizaCampos() {
        try {
            // Cargamos las dependencias Lazy (Asignaciones Solicitadas, Aplicadas y Oficios)
            SolicitudAsignacionNng solicitudLoaded = nngFacade
                    .getSolicitudAsignacionEagerLoad((SolicitudAsignacionNng) getWizard().getSolicitud());

            // Asociamos la solicitud cargada en al Wizard
            this.solicitud = solicitudLoaded;
            getWizard().setSolicitud(solicitud);

            numeracionPorAsignarList = nngFacade.findAllNumeracionAsignadaBySolicitud(solicitud);

            // Iniciamos renderizaciones
            especifica = solicitud.getTipoAsignacion().getCdg().equals(TipoAsignacion.ESPECIFICA);
            pstComercializador = solicitud.getProveedorSolicitante().getTipoProveedor().getCdg()
                    .equals(TipoProveedor.COMERCIALIZADORA);
            pstAmbos = solicitud.getProveedorSolicitante().getTipoProveedor().getCdg().equals(TipoProveedor.AMBOS);
            pstConcesionario = solicitud.getProveedorSolicitante().getTipoProveedor().getCdg()
                    .equals(TipoProveedor.CONCESIONARIO);

            asignado = solicitud.getEstadoSolicitud().getCodigo().equals(EstadoSolicitud.SOLICITUD_TERMINADA);
            cargarListaAsignacion();

        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }
    /**
     * Carga lista de asignación.
     */
    private void cargarListaAsignacion() {
        List<String> avisosRangosCambiados = new ArrayList<String>();
        numeracionParaAsignarList.clear();
        for (NumeracionSolicitadaNng numSolicitada : solicitud.getNumeracionesSolicitadas()) {
            for (NumeracionAsignadaNng numAsignada : numSolicitada.getNumeracionesAsignadas()) {

                // Comprobamos que la numeracion tiene un rango correspondiente o si la numeracion esta libre
                RangoSerieNng rango = nngFacade.getRangoSerie(numAsignada.getClaveServicio().getCodigo(),
                        numAsignada.getSna(), numAsignada.getInicioRango(),
                        solicitud.getProveedorSolicitante());
                if (rango != null && solicitud.getId().compareTo(rango.getSolicitud().getId()) == 0) {
                    numeracionParaAsignarList.add(numAsignada);
                } else if (rango == null
                        && nngFacade.validateRango(numAsignada.getClaveServicio().getCodigo(),
                                numAsignada.getSna(), numAsignada.getInicioRango(),
                                numAsignada.getFinRango()).isEmpty()) {
                    // Si la numeracion esta libre tambien es correcta
                    numeracionParaAsignarList.add(numAsignada);
                } else {
                    // Si la numeracion no es correcta avisamos
                    StringBuffer sbAviso = new StringBuffer();
                    sbAviso.append(" NIR: ").append(numAsignada.getClaveServicio().getCodigo());
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

    }

    @Override
    public void resetTab() {
        numeracionPorAsignarList = new ArrayList<NumeracionAsignadaNng>();
        asignado = false;
    }

    @Override
    public boolean isRetroceder() {
        // Al retroceder reseteamos la variable asignado para que se vuelva a habilitar
        // el botón de "Asignar"
        asignado = false;
        return super.isRetroceder();
    }

    /**
     * Indica si se han asignado los rangos solicitados.
     * @return boolean
     */
    public boolean isAsignado() {
        return asignado;
    }

    /**
     * Tabla numeracion por asignar.
     * @return numeracionPorAsignarList
     */
    public List<NumeracionAsignadaNng> getNumeracionPorAsignarList() {
        return numeracionPorAsignarList;
    }

    /**
     * Tabla numeracion por asignar.
     * @param numeracionPorAsignarList numeracionPorAsignarList to set
     */
    public void setNumeracionPorAsignarList(List<NumeracionAsignadaNng> numeracionPorAsignarList) {
        this.numeracionPorAsignarList = numeracionPorAsignarList;
    }

    /**
     * Campo solicitud asignación.
     * @return solicitud
     */
    public SolicitudAsignacionNng getSolicitud() {
        return solicitud;
    }

    /**
     * Indica si se han asignado los rangos solicitados.
     * @param asignado asignado to set
     */
    public void setAsignado(boolean asignado) {
        this.asignado = asignado;
    }

    /**
     * Proveedor Solicitante es Concesionario.
     * @return pstConcesionario
     */
    public boolean isPstConcesionario() {
        return pstConcesionario;
    }

    /**
     * Proveedor Solicitante es Concesionario.
     * @param pstConcesionario pstConcesionario to set
     */
    public void setPstConcesionario(boolean pstConcesionario) {
        this.pstConcesionario = pstConcesionario;
    }
    
    /**
     * @return pstComercializador
     */
    public boolean isPstComercializador() {
        return pstComercializador;
    }

    /**
     * Proveedor Solicitante es Comercializador.
     * @param pstComercializador pstComercializador to set
     */
    public void setPstComercializador(boolean pstComercializador) {
        this.pstComercializador = pstComercializador;
    }
    
    /**
     * Proveedor Solicitante es de ampos tipos.
     * @return pstAmbos
     */
    public boolean isPstAmbos() {
        return pstAmbos;
    }

    /**
     * Proveedor Solicitante es de ampos tipos.
     * @param pstAmbos pstAmbos to set
     */
    public void setPstAmbos(boolean pstAmbos) {
        this.pstAmbos = pstAmbos;
    }

    /**
     * Solicitud es de tipo Especifica.
     * @return especifica
     */
    public boolean isEspecifica() {
        return especifica;
    }

    /**
     * Solicitud es de tipo Especifica.
     * @param especifica especifica to set
     */
    public void setEspecifica(boolean especifica) {
        this.especifica = especifica;
    }

    /**
     * @return the numeracionParaAsignarList
     */
    public List<NumeracionAsignadaNng> getNumeracionParaAsignarList() {
        return numeracionParaAsignarList;
    }

    /**
     * @param numeracionParaAsignarList the numeracionParaAsignarList to set
     */
    public void setNumeracionParaAsignarList(List<NumeracionAsignadaNng> numeracionParaAsignarList) {
        this.numeracionParaAsignarList = numeracionParaAsignarList;
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
