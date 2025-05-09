package mx.ift.sns.web.backend.ng.asignacion;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;

import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.abn.AbnCentral;
import mx.ift.sns.modelo.central.Central;
import mx.ift.sns.modelo.central.CentralesRelacion;
import mx.ift.sns.modelo.ng.NumeracionSolicitada;
import mx.ift.sns.modelo.ng.SolicitudAsignacion;
import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.pst.TipoModalidad;
import mx.ift.sns.modelo.pst.TipoProveedor;
import mx.ift.sns.modelo.pst.TipoRed;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;
import mx.ift.sns.negocio.exceptions.RegistroModificadoException;
import mx.ift.sns.negocio.ng.INumeracionGeograficaService;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;
import mx.ift.sns.web.backend.components.TabWizard;
import mx.ift.sns.web.backend.components.Wizard;
import mx.ift.sns.web.backend.selectablemodels.NumeracionSeleccionadaNgModel;

import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Asignacion de Numeracion.
 */
public class NumeracionAsignacionNgTab extends TabWizard {

    /** Serial uid. */
    private static final long serialVersionUID = -6977772487921944690L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(NumeracionAsignacionNgTab.class);

    /** Servicio de numeracion geografica. */
    private INumeracionGeograficaService ngService;

    /** Campo solicitud asignación. */
    private SolicitudAsignacion solicitud;

    /** Indicador guardado. */
    private boolean guardado;

    /** Lista que contiene los posibles tipo de red. */
    private List<TipoRed> listaTiposRed;

    /** Lista que contiene los posibles tipo de modalidad. */
    private List<TipoModalidad> listaTiposModalidad;

    /** Lista de estados. */
    private List<Estado> listaEstados;

    /** Lista de municipios. */
    private List<Municipio> listaMunicipios;

    /** Lista de poblaciones. */
    private List<Poblacion> listaPoblaciones;

    /** Numeración solicitada. */
    private NumeracionSolicitada numeracionSolicitada;

    /** Seleccion multiple tabla resumen. */
    private List<NumeracionSolicitada> selectedNumeracionSolicitada;

    /** Data model tabla resumen. */
    private NumeracionSeleccionadaNgModel numeracionesSolicitadas;

    /** Representanción de la clave censal. */
    private String claveCensal;

    /** Representanción del codigo del ABN. */
    private String abn;

    /** Lista de arrendatarios. */
    private List<Proveedor> listaArrendatarios;

    /** Lista de concesionarios. */
    private List<Proveedor> listaConcesionarios;

    /** IDO seleccionado. */
    private String ido;

    /** Total fijo. Actualizar al dar añadir. */
    private long totalFijo;

    /** Total movil. Actualizar al dar añadir. */
    private long totalMovil;

    /** Total cpp. Actualizar al dar añadir. */
    private long totalCpp;

    /** Total mpp. Actualizar al dar añadir. */
    private long totalMpp;

    /** Total numeraciones. Actualizar al dar añadir. */
    private long totalNum;

    /** Total registros. Actualizar al dar añadir. */
    private long totalReg;

    /* Datos ubicación */

    /** Estado. */
    private Estado estado;

    /** Municipio. */
    private Municipio municipio;

    /** Poblacion. */
    private Poblacion poblacion;

    /** Escenario asignacion. */
    private int tipoEscenarioAsignacion;

    /** Habilita boton guardar. */
    private boolean habilitarGuardar = false;

    /** Habilita boton guardar cambios. */
    private boolean habilitarEditar = false;

    /** Se encarga de guardar el número de registros por página al cargar la misma. */
    private int registroPorPagina;

    /**
     * Constructor para cargar los datos genereales.
     * @param pBeanPadre bean del padre
     * @param ngService servicio
     * @throws Exception excepcion
     */
    public NumeracionAsignacionNgTab(Wizard pBeanPadre, INumeracionGeograficaService ngService) {

        LOGGER.debug("NumeracionAsignacionTab");

        guardado = false;
        setWizard(pBeanPadre);
        this.ngService = ngService;

        listaTiposRed = new ArrayList<TipoRed>();

        listaArrendatarios = new ArrayList<Proveedor>();
        listaConcesionarios = new ArrayList<Proveedor>();

        try {
            listaTiposModalidad = this.ngService.findAllTiposModalidad();

            listaEstados = this.ngService.findEstados();
        } catch (Exception e) {
            LOGGER.error("error", e);
            MensajesBean.addErrorMsg("MSG_Numeracion", Errores.ERROR_0004);
        }
        listaMunicipios = new ArrayList<Municipio>();
        listaPoblaciones = new ArrayList<Poblacion>();

        Estado estado = new Estado();

        Municipio municipio = new Municipio();
        municipio.setEstado(estado);

        Poblacion poblacion = new Poblacion();
        poblacion.setMunicipio(municipio);

        Abn abn = new Abn();
        poblacion.setAbn(abn);

        numeracionSolicitada = new NumeracionSolicitada();
        numeracionSolicitada.setPoblacion(poblacion);

        selectedNumeracionSolicitada = new ArrayList<NumeracionSolicitada>();

        totalCpp = 0;
        totalFijo = 0;
        totalMovil = 0;
        totalMpp = 0;
        totalNum = 0;
        totalReg = 0;

        habilitarGuardar = false;
        habilitarEditar = false;
        guardado = false;
    }

    @Override
    public void resetTab() {

        LOGGER.debug("");

        solicitud = new SolicitudAsignacion();
        numeracionSolicitada = new NumeracionSolicitada();
        estado = null;
        municipio = null;
        poblacion = null;
        claveCensal = null;
        abn = null;
        listaTiposRed = new ArrayList<TipoRed>();
        selectedNumeracionSolicitada = new ArrayList<NumeracionSolicitada>();

        listaMunicipios = new ArrayList<Municipio>();
        listaPoblaciones = new ArrayList<Poblacion>();

        listaArrendatarios = new ArrayList<Proveedor>();
        listaConcesionarios = new ArrayList<Proveedor>();

        this.setSummaryError(null);

        habilitarEditar = false;
    }

    /**
     * Método que válida si hay numeración solicitada.
     * @return boolean valida
     */
    public boolean isValido() {
        return solicitud.getNumeracionSolicitadas().size() > 0;
    }

    /**
     * Boton de salvar numeracion solicitada.
     * @return true/false
     */
    public boolean guardar() {
        try {
            // Guardamos la relacion con el abn y central de origen
            for (int i = 0; i < solicitud.getNumeracionSolicitadas().size(); i++) {
                if (solicitud.getNumeracionSolicitadas().get(i).getPoblacion().getAbn() != null) {
                    // Guardamos la relacion con el abn y central de origen
                    AbnCentral abnCentral = new AbnCentral();
                    abnCentral.setAbn(solicitud.getNumeracionSolicitadas().get(i).getPoblacion().getAbn());
                    abnCentral.setCentral(solicitud.getNumeracionSolicitadas().get(i).getCentralOrigen());
                    ngService.saveAbnCentral(abnCentral);
                }
                // Guardamos la relacion con la central de origen y destino
                CentralesRelacion centralesRelacion = new CentralesRelacion();
                centralesRelacion.setCentralDestino(solicitud.getNumeracionSolicitadas().get(i).getCentralDestino());
                centralesRelacion.setCentralOrigen(solicitud.getNumeracionSolicitadas().get(i).getCentralOrigen());
                ngService.saveCentralesRelacion(centralesRelacion);
            }

            // Limpiamos los rangos de la solicitud que puedan haber venido de otras pestañas para que no se dupliquen
            // solicitud.setRangos(new ArrayList<RangoSerie>());

            // Guardamos la solicitud y recargamos los datos de la pestaña
            solicitud = ngService.saveSolicitudAsignacion(solicitud);
            this.getWizard().setSolicitud(solicitud);

            numeracionesSolicitadas = new NumeracionSeleccionadaNgModel(solicitud.getNumeracionSolicitadas());

            StringBuilder sBuilder = new StringBuilder();
            sBuilder.append(MensajesBean.getTextoResource("manual.generales.exito.guardar")).append(" ");
            sBuilder.append(solicitud.getId());
            MensajesBean.addInfoMsg("MSG_Numeracion", sBuilder.toString(), "");

            guardado = true;
            habilitarEditar = false;
            limpiarDatosNumeracion();
            selectedNumeracionSolicitada = new ArrayList<NumeracionSolicitada>();
        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg("MSG_Numeracion", Errores.ERROR_0015);
            return false;
        } catch (Exception e) {
            LOGGER.error("error", e);

            MensajesBean.addErrorMsg("MSG_Numeracion", MensajesBean.getTextoResource("manual.generales.error.guardar"),
                    "");
            guardado = false;
        }

        return guardado;
    }

    @Override
    public String getMensajeError() {
        return MensajesBean.getTextoResource("manual.generales.necesario.guardar");
    }

    @Override
    public boolean isRetroceder() {
        getWizard().setSolicitud(solicitud);
        return super.isRetroceder();
    }

    @Override
    public boolean isAvanzar() {
        boolean res = false;
        if (this.isTabHabilitado()) {
            if (isValido()) {
                if (guardado) {
                    return true;
                } else {
                    this.setSummaryError(Errores.getDescripcionError(Errores.ERROR_0008));
                    return false;
                }
            } else {
                this.setSummaryError("No hay numeraciones seleccionadas.");
                return false;
            }
        } else {
            res = true;
        }
        return res;
    }

    @Override
    public void actualizaCampos() {

        LOGGER.debug("");
        solicitud = (SolicitudAsignacion) getWizard().getSolicitud();
        try {
            // Carga centrales destino

            if (this.solicitud.getProveedorSolicitante() != null) {
                listaTiposRed.clear();
                if (this.solicitud.getProveedorSolicitante().getTipoRed().getCdg().equals(TipoRed.AMBAS)) {
                    listaTiposRed = this.ngService.findAllTiposRed();
                } else {
                    listaTiposRed.add(this.solicitud.getProveedorSolicitante().getTipoRed());
                }
            }

            if (this.solicitud.getProveedorSolicitante().getTipoProveedor().getCdg().
                    equals(TipoProveedor.CONCESIONARIO)) {
                tipoEscenarioAsignacion = 1;

            } else if (this.solicitud.getProveedorSolicitante().getTipoProveedor().getCdg().
                    equals(TipoProveedor.COMERCIALIZADORA)) {
                tipoEscenarioAsignacion = 2;
            } else {
                tipoEscenarioAsignacion = 3;

                numeracionSolicitada.setIdoPnn(this.solicitud.getProveedorSolicitante().getIdo());
            }

            // Cargamos los combos
            TipoProveedor tipoProveedor = new TipoProveedor();
            tipoProveedor.setCdg(this.solicitud.getProveedorSolicitante().getTipoProveedor().getCdg());

            // Cargamos la tabla de numeraciones solicitadas
            List<NumeracionSolicitada> numsSolicitadasAux = new ArrayList<NumeracionSolicitada>();

            numsSolicitadasAux = ngService.getNumeracionesSolicitadas(solicitud.getId());

            solicitud.setNumeracionSolicitadas(numsSolicitadasAux);

            numeracionesSolicitadas = new NumeracionSeleccionadaNgModel(solicitud.getNumeracionSolicitadas());

            // Si la solicitud ya tiene numeraciones solicitadas las guardamos con el IDO/IDA seteado.
            if (numeracionesSolicitadas.getRowCount() > 0) {
                solicitud = ngService.saveSolicitudAsignacion(solicitud);
                this.getWizard().setSolicitud(solicitud);
                habilitarGuardar = true;
                guardado = true;
            }

            // Cargamos los totales de la tabla
            sumTotales(solicitud.getNumeracionSolicitadas());

            totalReg = solicitud.getNumeracionSolicitadas().size();
        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg("MSG_Numeracion", Errores.ERROR_0015);
        } catch (Exception e) {
            LOGGER.error("Se ha producido un error al cargar la pestaña de numeración: " + e);
            MensajesBean.addErrorMsg("MSG_Numeracion", Errores.ERROR_0004);
        }

    }

    /**
     * Autocomplete del combo de centrales de origen.
     * @param query nombre central
     * @return lista central
     * @throws Exception error
     */
    public List<Central> completeCentralOrigen(String query) throws Exception {

        List<Central> result = new ArrayList<Central>();

        if (tipoEscenarioAsignacion == 1) {
            result = ngService.findAllCentralesOrigenByName(query, solicitud.getProveedorSolicitante(), null);
        }
        if (tipoEscenarioAsignacion == 2) {
            result = ngService.findAllCentralesOrigenByName(query, numeracionSolicitada.getConcesionario(),
                    numeracionSolicitada.getArrendatario());
        }
        if (tipoEscenarioAsignacion == 3) {
            result = ngService.findAllCentralesOrigenByName(query, numeracionSolicitada.getConcesionario(),
                    solicitud.getProveedorSolicitante());
        }

        return result;

    }

    /**
     * Autocomplete del combo de centrales destino.
     * @param query nombre central
     * @return lista central
     * @throws Exception error
     */
    public List<Central> completeCentralDestino(String query) throws Exception {

        List<Central> result = new ArrayList<Central>();

        if(!query.isEmpty() || query.compareTo("") != 0)
            result = ngService.findAllCentralesByName(query);

        return result;

    }

    /**
     * Suma de los totales de la tabla.
     * @param numeracionSolicitadas List<NumeracionSolicitada>
     */
    private void sumTotales(List<NumeracionSolicitada> numeracionSolicitadas) {
        LOGGER.debug("");
        totalCpp = 0;
        totalFijo = 0;
        totalMovil = 0;
        totalMpp = 0;
        totalNum = 0;
        totalReg = 0;

        totalReg = numeracionSolicitadas.size();

        for (int i = 0; i < numeracionSolicitadas.size(); i++) {
            NumeracionSolicitada dato = numeracionSolicitadas.get(i);
            if (dato.getTipoRed().getCdg().equals(TipoRed.MOVIL)) {
                totalMovil = totalMovil + dato.getCantSolicitada().longValue();
                if (dato.getTipoModalidad().getCdg().equals(TipoModalidad.MPP)) {
                    totalMpp = totalMpp + dato.getCantSolicitada().longValue();
                } else if (dato.getTipoModalidad().getCdg().equals(TipoModalidad.CPP)) {
                    totalCpp = totalCpp + dato.getCantSolicitada().longValue();
                }
            } else if (dato.getTipoRed().getCdg().equals(TipoRed.FIJA)) {
                totalFijo = totalFijo + dato.getCantSolicitada().longValue();
            }

            totalNum += dato.getCantSolicitada().longValue();
        }

    }

    /**
     * Rellena la lista de municipios por Estado seleccionado.
     * @throws Exception excepcion
     */
    public void habilitarMunicipio() {
        LOGGER.debug("");

        try {
            if (estado != null) {
                listaMunicipios = ngService.findMunicipiosByEstado(estado.getCodEstado());
            } else {
                listaMunicipios = new ArrayList<Municipio>();
            }
        } catch (Exception e) {
            LOGGER.error("error", e);
            MensajesBean.addErrorMsg("MSG_Numeracion", Errores.ERROR_0004);
        }

        municipio = null;

        listaPoblaciones = new ArrayList<Poblacion>();
        poblacion = null;
        claveCensal = null;
        abn = null;

    }

    /**
     * Limpia los datos de ubicacion.
     */
    private void limpiarUbicacion() {

        listaEstados = new ArrayList<Estado>();
        estado = null;

        listaMunicipios = new ArrayList<Municipio>();
        municipio = null;

        listaPoblaciones = new ArrayList<Poblacion>();
        poblacion = null;

        claveCensal = null;
        abn = null;

    }

    /**
     * Rellena la lista de poblaciones por estado y municipio.
     * @throws Exception excepcion
     */
    public void habilitarPoblacion() {
        LOGGER.debug("");

        try {
            if (municipio != null) {
                listaPoblaciones = ngService.findAllPoblaciones(
                        estado.getCodEstado(), municipio.getId().getCodMunicipio());
            } else {
                listaPoblaciones = new ArrayList<Poblacion>();

            }
        } catch (Exception e) {
            LOGGER.error("error", e);
            MensajesBean.addErrorMsg("MSG_Numeracion", Errores.ERROR_0004);
        }
        poblacion = null;
        claveCensal = null;
        abn = null;

    }

    /**
     * Genera la clave censal y el abn de la numeracion solicitada.
     */
    public void generaClave() {

        LOGGER.debug("");

        try {
            numeracionSolicitada.setPoblacion(poblacion);

            claveCensal = numeracionSolicitada.getPoblacion().getInegi();

            abn = numeracionSolicitada.getPoblacion().getAbn().getCodigoAbn().toString();
        } catch (Exception e) {
            limpiarUbicacion();
            LOGGER.error(e.getMessage());
            MensajesBean
                    .addErrorMsg("MSG_Numeracion",
                            "Se ha encontrado un problema al cargar la ubicación por la población.", "");
        }

    }

    /**
     * Se obtienen el estado, municipio y poblacion al cambiar el ABN.
     */

    public void abnChange() {
        LOGGER.debug("");

        try {
            if (!abn.matches("-?\\d+(\\.\\d+)?")) {
                MensajesBean.addErrorMsg("MSG_Numeracion", "El ABN tiene que ser un valor numerico", "");
            } else {
                Poblacion poblacionAux = ngService.getPoblacionAnclaByCodigoAbn(new BigDecimal(abn));
                if (poblacionAux != null) {

                    estado = poblacionAux.getMunicipio().getEstado();
                    municipio = poblacionAux.getMunicipio();
                    poblacion = poblacionAux;

                    if (!listaEstados.contains(estado)) {
                        listaEstados.add(estado);
                    }

                    if (!listaMunicipios.contains(municipio)) {
                        listaMunicipios.add(municipio);
                    }

                    if (!listaPoblaciones.contains(poblacion)) {
                        listaPoblaciones.add(poblacion);
                    }

                    claveCensal = poblacion.getInegi();

                    numeracionSolicitada.setPoblacion(poblacion);
                } else {
                    estado = null;

                    listaMunicipios = new ArrayList<Municipio>();
                    municipio = null;

                    listaPoblaciones = new ArrayList<Poblacion>();
                    poblacion = null;

                    claveCensal = null;
                    MensajesBean.addErrorMsg("MSG_Numeracion",
                            "No existe ubicación para el ABN introducido", "");
                }
            }

        } catch (Exception e) {
            limpiarUbicacion();
            LOGGER.error(e.getMessage());
            MensajesBean.addErrorMsg("MSG_Numeracion",
                    "Se ha encontrado un problema al cargar la ubicación por el ABN ", "");
        }
    }

    /**
     * Se obtienen el estado, municipio y poblacion al cambiar la clave censal.
     */

    public void claveCensalChange() {

        LOGGER.debug("");

        try {

            if (!claveCensal.matches("-?\\d+(\\.\\d+)?")) {
                MensajesBean.addErrorMsg("MSG_Numeracion", "La clave censal tiene que ser un valor numerico", "");
            } else {
                Poblacion poblacionAux = ngService.findPoblacionById(claveCensal);
                if (poblacionAux != null) {
                    estado = poblacionAux.getMunicipio().getEstado();
                    municipio = poblacionAux.getMunicipio();
                    poblacion = poblacionAux;

                    if (!listaEstados.contains(estado)) {
                        listaEstados.add(estado);
                    }

                    if (!listaMunicipios.contains(municipio)) {
                        listaMunicipios.add(municipio);
                    }

                    if (!listaPoblaciones.contains(poblacion)) {
                        listaPoblaciones.add(poblacion);
                    }

                    abn = poblacion.getAbn().getCodigoAbn().toString();
                    numeracionSolicitada.setPoblacion(poblacion);

                } else {
                    estado = null;

                    listaMunicipios = new ArrayList<Municipio>();
                    municipio = null;

                    listaPoblaciones = new ArrayList<Poblacion>();
                    poblacion = null;

                    abn = null;
                    MensajesBean.addErrorMsg("MSG_Numeracion",
                            "No existe ubicación para la clave censal introducida", "");
                }
            }

        } catch (Exception e) {
            limpiarUbicacion();
            LOGGER.error(e.getMessage());
            MensajesBean.addErrorMsg("MSG_Numeracion",
                    "Se ha encontrado un problema al cargar la ubicación por la clave censal", "");
        }

    }

    /**
     * Agrega una numeracion seleccionada a la tabla Resumen.
     */
    public void agregarAction() {
        LOGGER.debug("");

        if (validarNumeracion()) {

            if (tipoEscenarioAsignacion == 1) {
                numeracionSolicitada.setIdoPnn(solicitud.getProveedorSolicitante().getIdo());
                numeracionSolicitada.setIdaPnn(solicitud.getProveedorSolicitante().getIdo());
            } else if (tipoEscenarioAsignacion == 2) {
                numeracionSolicitada.setIdoPnn(numeracionSolicitada.getConcesionario().getIdo());
                numeracionSolicitada.setIdaPnn(solicitud.getProveedorSolicitante().getIda());

            } else if (tipoEscenarioAsignacion == 3) {
                numeracionSolicitada.setIdaPnn(solicitud.getProveedorSolicitante().getIdo());
                if (numeracionSolicitada.getConcesionario() == null) {
                    numeracionSolicitada.setIdoPnn(solicitud.getProveedorSolicitante().getIdo());
                }
            }

            numeracionSolicitada.setCantAsignada(new BigDecimal(0));

            numeracionSolicitada.setSolicitudAsignacion(solicitud);
            NumeracionSolicitada numSolicitudAux = new NumeracionSolicitada();
            numSolicitudAux = numeracionSolicitada;
            numSolicitudAux.setId(null);

            solicitud.getNumeracionSolicitadas().add(numSolicitudAux);
            EstadoSolicitud estadoSolicitud = new EstadoSolicitud();
            estadoSolicitud.setCodigo(EstadoSolicitud.SOLICITUD_EN_TRAMITE);
            solicitud.setEstadoSolicitud(estadoSolicitud);

            numeracionesSolicitadas = new NumeracionSeleccionadaNgModel(solicitud.getNumeracionSolicitadas());
            sumTotales(solicitud.getNumeracionSolicitadas());

            limpiarDatosNumeracion();

            habilitarGuardar = true;
            habilitarEditar = false;

            guardado = false;
        }

    }

    /**
     * Metodo encargado de limpiar los filtros de edicion de la numeracion.
     */
    private void limpiarDatosNumeracion() {

        LOGGER.debug("");

        // Limpiamos los datos
        numeracionSolicitada = new NumeracionSolicitada();
        if (tipoEscenarioAsignacion == 3) {
            numeracionSolicitada.setIdoPnn(this.solicitud.getProveedorSolicitante().getIdo());
        }
        estado = null;
        municipio = null;
        poblacion = null;
        claveCensal = null;
        abn = null;

        selectedNumeracionSolicitada = new ArrayList<NumeracionSolicitada>();

        listaMunicipios = new ArrayList<Municipio>();
        listaPoblaciones = new ArrayList<Poblacion>();

        listaConcesionarios = new ArrayList<Proveedor>();
        listaArrendatarios = new ArrayList<Proveedor>();

    }

    /**
     * Valida una numeracion seleccionada.
     * @return true/false
     */
    private boolean validarNumeracion() {
        LOGGER.debug("");

        boolean valido = true;

        if (numeracionSolicitada.getTipoRed() == null) {
            MensajesBean.addErrorMsg("MSG_Numeracion", "El campo 'Tipo red' es obligatorio", "");
            valido = false;
        } else if (numeracionSolicitada.getTipoRed().getCdg().equals(TipoRed.MOVIL)
                && numeracionSolicitada.getTipoModalidad() == null) {

            MensajesBean.addErrorMsg("MSG_Numeracion", "El campo 'Tipo modalidad' es obligatorio", "");
            valido = false;

        }
        if (numeracionSolicitada.getCantSolicitada() == null) {
            MensajesBean.addErrorMsg("MSG_Numeracion", "El campo 'Cantidad numeraciones' es obligatorio", "");
            valido = false;
        }
        if (claveCensal == null) {
            MensajesBean.addErrorMsg("MSG_Numeracion", "El campo 'Clave censal' es obligatorio", "");
            valido = false;
        }
        if (abn == null) {
            MensajesBean.addErrorMsg("MSG_Numeracion", "El campo 'ABN' es obligatorio", "");
            valido = false;
        }
        if (estado == null) {
            MensajesBean.addErrorMsg("MSG_Numeracion", "El campo 'Estado' es obligatorio", "");
            valido = false;
        }
        if (municipio == null) {
            MensajesBean.addErrorMsg("MSG_Numeracion", "El campo 'Municipio' es obligatorio", "");
            valido = false;
        }
        if (poblacion == null) {
            MensajesBean.addErrorMsg("MSG_Numeracion", "El campo 'Poblacion' es obligatorio", "");
            valido = false;
        }
        if (numeracionSolicitada.getCentralOrigen() == null) {
            MensajesBean.addErrorMsg("MSG_Numeracion", "La central de origen es obligatoria", "");
            valido = false;
        } else if (numeracionSolicitada.getCentralOrigen().getId() == null) {
            MensajesBean.addErrorMsg("MSG_Numeracion", "La central de origen no es correcta", "");
            valido = false;
        }
        if (numeracionSolicitada.getCentralDestino() == null) {
            MensajesBean.addErrorMsg("MSG_Numeracion", "La central de destino es obligatoria", "");
            valido = false;
        } else if (numeracionSolicitada.getCentralDestino().getId() == null) {
            MensajesBean.addErrorMsg("MSG_Numeracion", "La central de destino no es correcta", "");
            valido = false;
        }
        if (tipoEscenarioAsignacion == 2 && numeracionSolicitada.getConcesionario() == null) {
            MensajesBean.addErrorMsg("MSG_Numeracion", "El campo 'Concesionario' es obligatorio", "");
            valido = false;
        }

        return valido && !numeracionRepetida();
    }

    /**
     * Comprueba si una numeracion solicitada esta repetida.
     * @return true/false
     */
    private boolean numeracionRepetida() {
        LOGGER.debug("");

        String rowKey = (String) numeracionesSolicitadas.getRowKey(numeracionSolicitada);
        if (numeracionesSolicitadas.getRowData(rowKey) != null) {
            MensajesBean.addErrorMsg("MSG_Numeracion",
                    "La numeración solicitada esta repetida. Por favor, seleccione otros datos", "");
            return true;
        }

        return false;
    }

    /**
     * Elimina una numeracion seleccionada a la tabla Resumen.
     * @param actionEvent ActionEvent
     * @throws Exception error
     */
    @SuppressWarnings("unchecked")
    public void eliminarAction(ActionEvent actionEvent) {

        LOGGER.debug("");

        try {
            for (int i = 0; i < selectedNumeracionSolicitada.size(); i++) {
                int index = ((List<NumeracionSolicitada>) numeracionesSolicitadas.getWrappedData())
                        .indexOf(selectedNumeracionSolicitada.get(i));
                if (!tieneRangos(selectedNumeracionSolicitada.get(i))) {

                    solicitud.getNumeracionSolicitadas().remove(index);
                    guardado = false;
                } else {
                    MensajesBean
                            .addErrorMsg(
                                    "MSG_Numeracion",
                                    "No se puede eliminar la numeración solicitada de la fila " + String.valueOf(index)
                                            + " porqué ya tiene rangos de series asociados "
                                            + "en estado pendiente o asignado", "");
                }
            }

            EstadoSolicitud estadoSolicitud = new EstadoSolicitud();
            estadoSolicitud.setCodigo(EstadoSolicitud.SOLICITUD_EN_TRAMITE);
            solicitud.setEstadoSolicitud(estadoSolicitud);

            solicitud = ngService.saveSolicitudAsignacion(solicitud);
            this.getWizard().setSolicitud(solicitud);

            numeracionesSolicitadas = new NumeracionSeleccionadaNgModel(solicitud.getNumeracionSolicitadas());

            sumTotales(solicitud.getNumeracionSolicitadas());

            if (numeracionesSolicitadas.getRowCount() == 0) {
                habilitarGuardar = false;
            }

            limpiarDatosNumeracion();
            habilitarEditar = false;

        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg("MSG_Numeracion", Errores.ERROR_0015);

        } catch (Exception e) {
            LOGGER.error("error", e);

            MensajesBean.addErrorMsg("MSG_Numeracion", MensajesBean.getTextoResource("manual.generales.error.guardar"),
                    "");
            guardado = false;
        }

    }

    /**
     * Comprueba si una numeracion solicitada tiene numeraciones asignadas.
     * @param numeracionSolicitada numeracionSolicitada
     * @return true/false
     */
    private boolean tieneRangos(NumeracionSolicitada numeracionSolicitada) {

        return ngService.existNumeracionAsignadaBySolicita(numeracionSolicitada);

    }

    /**
     * Se carga la numeracion seleccionada.
     * @param event SelectEvent
     */
    public void onRowSelect(SelectEvent event) {

        LOGGER.debug("");

        try {
            if (selectedNumeracionSolicitada.size() == 1) {
                NumeracionSolicitada numeracionSolicitadaAux = (NumeracionSolicitada) event.getObject();
                if (numeracionSolicitadaAux.getPoblacion().getAbn() != null) {
                    estado = numeracionSolicitadaAux.getPoblacion().getMunicipio().getEstado();
                    municipio = numeracionSolicitadaAux.getPoblacion().getMunicipio();
                    poblacion = numeracionSolicitadaAux.getPoblacion();
                    if (!listaEstados.contains(estado)) {
                        listaEstados.add(estado);
                    }
                    if (!listaMunicipios.contains(municipio)) {
                        listaMunicipios.add(municipio);
                    }
                    if (!listaPoblaciones.contains(poblacion)) {
                        listaPoblaciones.add(poblacion);
                    }
                    claveCensal = poblacion.getInegi();
                    abn = poblacion.getAbn().getCodigoAbn().toString();
                    // Seteamos los datos seleccionados
                    numeracionSolicitada.setConcesionario(numeracionSolicitadaAux.getConcesionario());
                    numeracionSolicitada.setArrendatario(numeracionSolicitadaAux.getArrendatario());
                    numeracionSolicitada.setCantSolicitada(numeracionSolicitadaAux.getCantSolicitada());
                    numeracionSolicitada.setCentralDestino(numeracionSolicitadaAux.getCentralDestino());
                    numeracionSolicitada.setCentralOrigen(numeracionSolicitadaAux.getCentralOrigen());
                    numeracionSolicitada.setPoblacion(numeracionSolicitadaAux.getPoblacion());
                    numeracionSolicitada.setIdaPnn(numeracionSolicitadaAux.getIdaPnn());
                    numeracionSolicitada.setIdoPnn(numeracionSolicitadaAux.getIdoPnn());
                    numeracionSolicitada.setTipoRed(numeracionSolicitadaAux.getTipoRed());
                    numeracionSolicitada.setTipoModalidad(numeracionSolicitadaAux.getTipoModalidad());
                    TipoProveedor tipoProveedor = new TipoProveedor();
                    tipoProveedor.setCdg(this.solicitud.getProveedorSolicitante().getTipoProveedor().getCdg());
                    listaArrendatarios = ngService.findAllProveedoresByServicioArrendar(tipoProveedor,
                            numeracionSolicitada.getTipoRed(), this.solicitud.getProveedorSolicitante().getId());
                    listaConcesionarios = ngService.findAllConcesionariosByComercializador(
                            numeracionSolicitada.getTipoRed(), solicitud.getProveedorSolicitante());
                    habilitarEditar = true;
                } else {
                    // Si la poblacion de la numeracion no tiene ABN lo informamos
                    MensajesBean.addErrorMsg("MSG_Numeracion",
                            "La poblacion de la numeración seleccionada no tiene ABN asignado",
                            "");
                }
            } else {
                // Limpiamos los datos
                numeracionSolicitada = new NumeracionSolicitada();
                estado = null;
                municipio = null;
                poblacion = null;
                claveCensal = null;
                abn = null;

                listaMunicipios = new ArrayList<Municipio>();
                listaPoblaciones = new ArrayList<Poblacion>();
                habilitarEditar = false;
            }
        } catch (Exception e) {
            LOGGER.error("error", e);
            MensajesBean.addErrorMsg("MSG_Numeracion", Errores.ERROR_0004);
        }
    }

    /**
     * Se carga la numeracion seleccionada.
     */
    public void onRowUnSelect() {

        LOGGER.debug("");

        try {
            if (selectedNumeracionSolicitada.size() == 1) {
                NumeracionSolicitada numeracionSolicitadaAux = selectedNumeracionSolicitada.get(0);
                if (numeracionSolicitadaAux.getPoblacion().getAbn() != null) {
                    estado = numeracionSolicitadaAux.getPoblacion().getMunicipio().getEstado();
                    municipio = numeracionSolicitadaAux.getPoblacion().getMunicipio();
                    poblacion = numeracionSolicitadaAux.getPoblacion();
                    if (!listaEstados.contains(estado)) {
                        listaEstados.add(estado);
                    }
                    if (!listaMunicipios.contains(municipio)) {
                        listaMunicipios.add(municipio);
                    }
                    if (!listaPoblaciones.contains(poblacion)) {
                        listaPoblaciones.add(poblacion);
                    }
                    claveCensal = poblacion.getInegi();
                    abn = poblacion.getAbn().getCodigoAbn().toString();
                    // Seteamos los datos seleccionados
                    numeracionSolicitada.setConcesionario(numeracionSolicitadaAux.getConcesionario());
                    numeracionSolicitada.setArrendatario(numeracionSolicitadaAux.getArrendatario());
                    numeracionSolicitada.setCantSolicitada(numeracionSolicitadaAux.getCantSolicitada());
                    numeracionSolicitada.setCentralDestino(numeracionSolicitadaAux.getCentralDestino());
                    numeracionSolicitada.setCentralOrigen(numeracionSolicitadaAux.getCentralOrigen());
                    numeracionSolicitada.setPoblacion(numeracionSolicitadaAux.getPoblacion());
                    numeracionSolicitada.setIdaPnn(numeracionSolicitadaAux.getIdaPnn());
                    numeracionSolicitada.setIdoPnn(numeracionSolicitadaAux.getIdoPnn());
                    numeracionSolicitada.setTipoRed(numeracionSolicitadaAux.getTipoRed());
                    numeracionSolicitada.setTipoModalidad(numeracionSolicitadaAux.getTipoModalidad());
                    TipoProveedor tipoProveedor = new TipoProveedor();
                    tipoProveedor.setCdg(this.solicitud.getProveedorSolicitante().getTipoProveedor().getCdg());
                    listaArrendatarios = ngService.findAllProveedoresByServicioArrendar(tipoProveedor,
                            numeracionSolicitada.getTipoRed(), this.solicitud.getProveedorSolicitante().getId());
                    listaConcesionarios = ngService.findAllConcesionariosByComercializador(
                            numeracionSolicitada.getTipoRed(), solicitud.getProveedorSolicitante());
                    habilitarEditar = true;
                } else {
                    // Si la poblacion de la numeracion no tiene ABN lo informamos
                    MensajesBean.addErrorMsg("MSG_Numeracion",
                            "La poblacion de la numeración seleccionada no tiene ABN asignado",
                            "");
                }
            } else {
                // Limpiamos los datos
                numeracionSolicitada = new NumeracionSolicitada();
                estado = null;
                municipio = null;
                poblacion = null;
                claveCensal = null;
                abn = null;

                listaMunicipios = new ArrayList<Municipio>();
                listaPoblaciones = new ArrayList<Poblacion>();
                habilitarEditar = false;
            }
        } catch (Exception e) {
            LOGGER.error("error", e);
            MensajesBean.addErrorMsg("MSG_Numeracion", Errores.ERROR_0004);
        }
    }

    /**
     * Edita una numeracion solicitada de la tabla resumen.
     */
    public void editarAction() {

        LOGGER.debug("");

        NumeracionSolicitada editNumSolicitada = selectedNumeracionSolicitada.get(0);

        if (!tieneRangos(editNumSolicitada)) {
            if (validarNumeracion()) {

                int index = solicitud.getNumeracionSolicitadas().indexOf(editNumSolicitada);

                if (tipoEscenarioAsignacion == 1) {
                    numeracionSolicitada.setIdoPnn(solicitud.getProveedorSolicitante().getIdo());
                    numeracionSolicitada.setIdaPnn(solicitud.getProveedorSolicitante().getIdo());
                } else if (tipoEscenarioAsignacion == 2) {
                    numeracionSolicitada.setIdoPnn(numeracionSolicitada.getConcesionario().getIdo());
                    numeracionSolicitada.setIdaPnn(solicitud.getProveedorSolicitante().getIda());
                } else if (tipoEscenarioAsignacion == 3) {
                    numeracionSolicitada.setIdaPnn(solicitud.getProveedorSolicitante().getIdo());
                }
                numeracionSolicitada.setPoblacion(poblacion);
                // Seteamos los datos cambiados
                editNumSolicitada.setConcesionario(numeracionSolicitada.getConcesionario());
                editNumSolicitada.setArrendatario(numeracionSolicitada.getArrendatario());
                editNumSolicitada.setCantSolicitada(numeracionSolicitada.getCantSolicitada());
                editNumSolicitada.setCentralDestino(numeracionSolicitada.getCentralDestino());
                editNumSolicitada.setCentralOrigen(numeracionSolicitada.getCentralOrigen());
                editNumSolicitada.setPoblacion(numeracionSolicitada.getPoblacion());
                editNumSolicitada.setIdaPnn(numeracionSolicitada.getIdaPnn());
                editNumSolicitada.setIdoPnn(numeracionSolicitada.getIdoPnn());
                editNumSolicitada.setTipoRed(numeracionSolicitada.getTipoRed());
                editNumSolicitada.setTipoModalidad(numeracionSolicitada.getTipoModalidad());
                solicitud.getNumeracionSolicitadas().set(index, editNumSolicitada);

                EstadoSolicitud estadoSolicitud = new EstadoSolicitud();
                estadoSolicitud.setCodigo(EstadoSolicitud.SOLICITUD_EN_TRAMITE);
                solicitud.setEstadoSolicitud(estadoSolicitud);

                numeracionesSolicitadas = new NumeracionSeleccionadaNgModel(solicitud.getNumeracionSolicitadas());
            }

            sumTotales(solicitud.getNumeracionSolicitadas());
            guardado = false;

        } else {
            MensajesBean
                    .addErrorMsg(
                            "MSG_Numeracion",
                            "No se puede modificar la numeración solicitada porqué ya tiene rangos de series asociados "
                                    + "en estado pendiente o asignado", "");
        }

    }

    /**
     * Se encarga de obtener los proveedores a los que es posible arrendar numeraciones por tipo de servicio.
     */
    public void actualizarListadoArrendar() {

        LOGGER.debug("");

        try {
            TipoProveedor tipoProveedor = new TipoProveedor();
            tipoProveedor.setCdg(this.solicitud.getProveedorSolicitante().getTipoProveedor().getCdg());

            if (null == numeracionSolicitada.getTipoRed()) {
                listaArrendatarios = new ArrayList<Proveedor>();
                listaConcesionarios = new ArrayList<Proveedor>();
            } else {
                listaArrendatarios = ngService.findAllProveedoresByServicioArrendar(tipoProveedor,
                        numeracionSolicitada.getTipoRed(), this.solicitud.getProveedorSolicitante().getId());
                listaConcesionarios = ngService.findAllConcesionariosByComercializador(
                        numeracionSolicitada.getTipoRed(), solicitud.getProveedorSolicitante());
            }
        } catch (Exception e) {
            LOGGER.error("error", e);
            MensajesBean.addErrorMsg("MSG_Numeracion", Errores.ERROR_0004);
        }
    }

    /**
     * Obtiene Campo solicitud asignación.
     * @return solicitud
     */
    public SolicitudAsignacion getSolicitud() {
        return solicitud;
    }

    /**
     * Indica si tramite guardado.
     * @return guardado
     */
    @Override
    public boolean isGuardado() {
        return guardado;
    }

    /**
     * Obtiene Lista que contiene los posibles tipo de red.
     * @return listaTiposRed
     */
    public List<TipoRed> getListaTiposRed() {
        return listaTiposRed;
    }

    /**
     * Carga Lista que contiene los posibles tipo de red.
     * @param listaTiposRed listaTiposRed to set
     */
    public void setListaTiposRed(List<TipoRed> listaTiposRed) {
        this.listaTiposRed = listaTiposRed;
    }

    /**
     * Obtiene Lista que contiene los posibles tipo de modalidad.
     * @return listaTiposModalidad
     */
    public List<TipoModalidad> getListaTiposModalidad() {
        return listaTiposModalidad;
    }

    /**
     * Carga Lista que contiene los posibles tipo de modalidad.
     * @param listaTiposModalidad listaTiposModalidad to set
     */
    public void setListaTiposModalidad(List<TipoModalidad> listaTiposModalidad) {
        this.listaTiposModalidad = listaTiposModalidad;
    }

    /**
     * Obtiene Lista de estados.
     * @return listaEstados
     */
    public List<Estado> getListaEstados() {
        return listaEstados;
    }

    /**
     * carga Lista de estados.
     * @param listaEstados listaEstados to set
     */
    public void setListaEstados(List<Estado> listaEstados) {
        this.listaEstados = listaEstados;
    }

    /**
     * Obtiene Lista de municipios.
     * @return listaMunicipios
     */
    public List<Municipio> getListaMunicipios() {
        return listaMunicipios;
    }

    /**
     * Carga Lista de municipios.
     * @param listaMunicipios listaMunicipios to set
     */
    public void setListaMunicipios(List<Municipio> listaMunicipios) {
        this.listaMunicipios = listaMunicipios;
    }

    /**
     * obtiene Lista de poblaciones.
     * @return listaPoblaciones
     */
    public List<Poblacion> getListaPoblaciones() {
        return listaPoblaciones;
    }

    /**
     * Carga Lista de poblaciones.
     * @param listaPoblaciones listaPoblaciones to set
     */
    public void setListaPoblaciones(List<Poblacion> listaPoblaciones) {
        this.listaPoblaciones = listaPoblaciones;
    }

    /**
     * Obtiene Numeración solicitada.
     * @return numeracionSolicitada
     */
    public NumeracionSolicitada getNumeracionSolicitada() {
        return numeracionSolicitada;
    }

    /**
     * Carga Numeración solicitada.
     * @param numeracionSolicitada numeracionSolicitada to set
     */
    public void setNumeracionSolicitada(NumeracionSolicitada numeracionSolicitada) {
        this.numeracionSolicitada = numeracionSolicitada;
    }

    /**
     * Obtiene Seleccion multiple tabla resumen.
     * @return selectedNumeracionSolicitada
     */
    public List<NumeracionSolicitada> getSelectedNumeracionSolicitada() {
        return selectedNumeracionSolicitada;
    }

    /**
     * Carga Seleccion multiple tabla resumen.
     * @param selectedNumeracionSolicitada selectedNumeracionSolicitada to set
     */
    public void setSelectedNumeracionSolicitada(List<NumeracionSolicitada> selectedNumeracionSolicitada) {
        this.selectedNumeracionSolicitada = selectedNumeracionSolicitada;
    }

    /**
     * Obtiene Data model tabla resumen.
     * @return numeracionesSolicitadas
     */
    public NumeracionSeleccionadaNgModel getNumeracionesSolicitadas() {
        return numeracionesSolicitadas;
    }

    /**
     * Carga Data model tabla resumen.
     * @param numeracionesSolicitadas numeracionesSolicitadas to set
     */
    public void setNumeracionesSolicitadas(NumeracionSeleccionadaNgModel numeracionesSolicitadas) {
        this.numeracionesSolicitadas = numeracionesSolicitadas;
    }

    /**
     * Obtiene representanción de la clave censal.
     * @return claveCensal
     */
    public String getClaveCensal() {
        return claveCensal;
    }

    /**
     * Carga representanción de la clave censal.
     * @param claveCensal claveCensal to set
     */
    public void setClaveCensal(String claveCensal) {
        this.claveCensal = claveCensal;
    }

    /**
     * Obtiene Representanción del codigo del ABN.
     * @return abn
     */
    public String getAbn() {
        return abn;
    }

    /**
     * Carga Representanción del codigo del ABN.
     * @param abn abn to set
     */
    public void setAbn(String abn) {
        this.abn = abn;
    }

    /**
     * Obtiene Lista de arrendatarios.
     * @return listaArrendatarios
     */
    public List<Proveedor> getListaArrendatarios() {
        return listaArrendatarios;
    }

    /**
     * Carga Lista de arrendatarios.
     * @param listaArrendatarios listaArrendatarios to set
     */
    public void setListaArrendatarios(List<Proveedor> listaArrendatarios) {
        this.listaArrendatarios = listaArrendatarios;
    }

    /**
     * Obtiene Lista de concesionarios.
     * @return listaConcesionarios
     */
    public List<Proveedor> getListaConcesionarios() {
        return listaConcesionarios;
    }

    /**
     * Carga Lista de concesionarios.
     * @param listaConcesionarios listaConcesionarios to set
     */
    public void setListaConcesionarios(List<Proveedor> listaConcesionarios) {
        this.listaConcesionarios = listaConcesionarios;
    }

    /**
     * Obtiene IDO seleccionado.
     * @return ido
     */
    public String getIdo() {
        return ido;
    }

    /**
     * Carga IDO seleccionado.
     * @param ido ido to set
     */
    public void setIdo(String ido) {
        this.ido = ido;
    }

    /**
     * Obtiene Total fijo.
     * @return totalFijo
     */
    public long getTotalFijo() {
        return totalFijo;
    }

    /**
     * Obtiene total Movil.
     * @return totalMovil
     */
    public long getTotalMovil() {
        return totalMovil;
    }

    /**
     * obtiene total Cpp.
     * @return totalCpp
     */
    public long getTotalCpp() {
        return totalCpp;
    }

    /**
     * Obtiene total Mpp.
     * @return totalMpp
     */
    public long getTotalMpp() {
        return totalMpp;
    }

    /**
     * Obtiene Total numeraciones.
     * @return totalNum
     */
    public long getTotalNum() {
        return totalNum;
    }

    /**
     * Obtiene Total registros.
     * @return totalReg
     */
    public long getTotalReg() {
        return totalReg;
    }

    /**
     * Obtiene estado.
     * @return estado
     */
    public Estado getEstado() {
        return estado;
    }

    /**
     * Carga estado.
     * @param estado estado to set
     */
    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    /**
     * Obtiene municipio.
     * @return municipio
     */
    public Municipio getMunicipio() {
        return municipio;
    }

    /**
     * Carga municipio.
     * @param municipio municipio to set
     */
    public void setMunicipio(Municipio municipio) {
        this.municipio = municipio;
    }

    /**
     * Obtiene poblacion.
     * @return poblacion
     */
    public Poblacion getPoblacion() {
        return poblacion;
    }

    /**
     * Obiene poblacion.
     * @param poblacion poblacion to set
     */
    public void setPoblacion(Poblacion poblacion) {
        this.poblacion = poblacion;
    }

    /**
     * Obtiene el tipo de Escenario de asignacion.
     * @return tipoEscenarioAsignacion
     */
    public int getTipoEscenarioAsignacion() {
        return tipoEscenarioAsignacion;
    }

    /**
     * Indica si habilita guardar.
     * @return habilitarGuardar
     */
    public boolean isHabilitarGuardar() {
        return habilitarGuardar;
    }

    /**
     * Indica si habilita editar.
     * @return habilitarEditar
     */
    public boolean isHabilitarEditar() {
        return habilitarEditar;
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
