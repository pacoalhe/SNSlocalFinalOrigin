package mx.ift.sns.web.backend.ac.poblaciones;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.abn.PoblacionAbn;
import mx.ift.sns.modelo.central.Estatus;
import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.MunicipioPK;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.negocio.IAdminCatalogosFacade;
import mx.ift.sns.negocio.exceptions.RegistroModificadoException;
import mx.ift.sns.negocio.ot.IOrganizacionTerritorialService;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** ManagedBean. */
@ManagedBean(name = "nuevaPoblacionBean")
@ViewScoped
public class NuevaPoblacionBean implements Serializable {

    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(NuevaPoblacionBean.class);

    /** Servicio de Administracion de catalogos. */
    @EJB(mappedName = "AdminCatalogosFacade")
    private IAdminCatalogosFacade adminCatFacadeService;

    /** Servicio de oganización territorial. */
    @EJB(mappedName = "OrganizacionTerritorialService")
    private IOrganizacionTerritorialService otservice;

    /** ID mensaje. */
    private static final String MSG_ID = "MSG_NuevaPoblacion";

    /*
     * Campos del formulario
     */
    /** Lista de estados. */
    private List<Estado> listaEstados;

    /** Lista de municipios. */
    private List<Municipio> listaMunicipios;

    /** Lista de nuevas poblaciones. */
    private List<Poblacion> listaPoblaciones;

    /** Lista de nuevas poblacionesAbn. */
    private List<PoblacionAbn> listaPoblacionesAbn;

    /** Estado. */
    private Estado estadoSeleccionado;

    /** Municipio. */
    private Municipio municipioSeleccionado;

    /** ABN. */
    private String abnSeleccionado;

    /** Código poblacion. */
    private String codigoPoblacion;

    /** Nombre poblacion. */
    private String nombrePoblacion;

    /** Estatus. */
    private Estatus estatusSeleccionado;

    /**
     * Iniciamos las listas de estado y region.
     */
    @PostConstruct
    public void init() {
        // Inicialización del desplegable de municipios y lista de poblaciones a dar de alta
        listaMunicipios = new ArrayList<Municipio>(1);
        listaPoblaciones = new ArrayList<Poblacion>(1);
        // Inicializa la lista de poblacionesAbn
        listaPoblacionesAbn = new ArrayList<PoblacionAbn>(1);

        try {
            // Listado de estados
            listaEstados = adminCatFacadeService.findAllEstados();
        } catch (Exception e) {
            LOGGER.error("error", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /**
     * Método que realiza la validación de los datos del formulario de alta de la población.
     * @return boolean valido
     */
    private boolean validarDatosFormulario() {
        boolean valido = true;
        try {
            // Estado
            if (this.estadoSeleccionado == null) {
                MensajesBean.addErrorMsg(MSG_ID,
                        MensajesBean.getTextoResource("catalogo.poblaciones.valida.seleccion.estado.error"), "");
                valido = false;
            }
            // Municipio: si viene informado se carga en el objeto poblacion con el estado
            if (this.estadoSeleccionado != null && this.municipioSeleccionado == null) {
                MensajesBean.addErrorMsg(MSG_ID,
                        MensajesBean.getTextoResource("catalogo.poblaciones.valida.seleccion.municipio.error"), "");
                valido = false;
            } else {
                Municipio nuevoMunicipio = new Municipio();
                nuevoMunicipio.setId(new MunicipioPK());
                nuevoMunicipio.getId().setCodEstado(this.municipioSeleccionado.getEstado().getCodEstado());
                nuevoMunicipio.setEstado(this.municipioSeleccionado.getEstado());
                nuevoMunicipio.getId().setCodMunicipio(this.municipioSeleccionado.getId().getCodMunicipio());
                nuevoMunicipio.setNombre(this.municipioSeleccionado.getNombre());
            }

            // Código población: si viene informado se carga en el objeto poblacion
            // Validamos si ya existe el código de población informada para ese estado y municipio
            if (!StringUtils.isEmpty(this.codigoPoblacion)) {
                if (StringUtils.isNumeric(this.codigoPoblacion)) {
                    if (StringUtils.length(this.codigoPoblacion) <= 4) {
                        if (this.estadoSeleccionado != null && this.municipioSeleccionado != null) {
                            Poblacion poblacionExiste = adminCatFacadeService.findPoblacionById(componerINEGI(
                                    this.estadoSeleccionado,
                                    this.municipioSeleccionado, this.codigoPoblacion));
                            if (null != poblacionExiste && null != poblacionExiste.getCdgPoblacion()) {
                                MensajesBean.addErrorMsg(MSG_ID,
                                        MensajesBean.getTextoResource("catalogo.poblaciones.valida.inegi.existe.error"),
                                        "");
                                valido = false;
                            }
                        }
                    } else {
                        MensajesBean.addErrorMsg(MSG_ID,
                                MensajesBean
                                        .getTextoResource("catalogo.poblaciones.valida.codigo.poblacion.formato.error"),
                                "");
                        valido = false;
                    }
                } else {
                    MensajesBean.addErrorMsg(MSG_ID,
                            MensajesBean
                                    .getTextoResource("catalogo.poblaciones.valida.codigo.poblacion.numerico.error"),
                            "");
                    valido = false;
                }
            } else {
                MensajesBean.addErrorMsg(MSG_ID,
                        MensajesBean.getTextoResource("catalogo.poblaciones.valida.codigo.poblacion.obligatorio.error"),
                        "");
                valido = false;
            }

            // Nombre población: si viene informado se carga en el objeto poblacion
            if (null == this.nombrePoblacion || "".equals(this.nombrePoblacion.trim())) {
                MensajesBean.addErrorMsg(MSG_ID,
                        MensajesBean.getTextoResource("catalogo.poblaciones.valida.nombre.poblacion.error"), "");
                valido = false;
            }
            // ABN: si viene informado se carga en el objeto poblacion
            // Validación Código de ABN disponible.

            if (!StringUtils.isEmpty(this.abnSeleccionado)) {
                if (StringUtils.isNumeric(this.abnSeleccionado)) {
                    if (StringUtils.length(this.abnSeleccionado) <= 3) {
                        // Validamos si el abn informado existe en el sistema
                        Abn abnAux = otservice.getAbnById(new BigDecimal(this.abnSeleccionado));
                        if (abnAux == null) {
                            MensajesBean.addErrorMsg(MSG_ID,
                                    MensajesBean.getTextoResource("catalogo.poblaciones.valida.abn.no.existe.error"),
                                    "");
                            valido = false;
                        }
                    } else {
                        MensajesBean.addErrorMsg(MSG_ID,
                                MensajesBean.getTextoResource("catalogo.poblaciones.valida.abn.formato.error"), "");
                        valido = false;
                    }
                } else {
                    MensajesBean.addErrorMsg(MSG_ID,
                            MensajesBean.getTextoResource("catalogo.poblaciones.valida.abn.numerico.error"), "");
                    valido = false;
                }
            } else {
                MensajesBean.addErrorMsg(MSG_ID,
                        MensajesBean.getTextoResource("catalogo.poblaciones.valida.abn.obligatorio.error"), "");
                valido = false;
            }
        } catch (Exception e) {
            MensajesBean.addErrorMsg(MSG_ID, "Error: " + e.getMessage(), "");
            valido = false;
        }
        return valido;
    }

    /**
     * Método para cargar el objeto Población a guardar .
     * @return poblacionCargada
     */
    public Poblacion cargaValues() {
        Poblacion poblacionCargada = new Poblacion();
        try {

            Municipio nuevoMunicipio = new Municipio();
            nuevoMunicipio.setId(new MunicipioPK());
            nuevoMunicipio.getId().setCodEstado(this.municipioSeleccionado.getEstado().getCodEstado());
            nuevoMunicipio.setEstado(this.municipioSeleccionado.getEstado());
            nuevoMunicipio.getId().setCodMunicipio(this.municipioSeleccionado.getId().getCodMunicipio());
            nuevoMunicipio.setNombre(this.municipioSeleccionado.getNombre());
            poblacionCargada.setMunicipio(nuevoMunicipio);
            poblacionCargada.setCdgPoblacion(this.codigoPoblacion);
            poblacionCargada.setInegi(componerINEGI(this.estadoSeleccionado, this.municipioSeleccionado,
                    this.codigoPoblacion));
            poblacionCargada.setNombre(this.nombrePoblacion.toUpperCase());
            Abn abnAux;
            abnAux = otservice.getAbnById(new BigDecimal(this.abnSeleccionado));
            poblacionCargada.setAbn(abnAux);
            // Se pone el estado de la población a ACTIVO
            Estatus estatus = new Estatus();
            estatus.setCdg(Estatus.ACTIVO);
            poblacionCargada.setEstatus(estatus);

            // Se carga la entidad de PoblacionAbn
            PoblacionAbn poblacionAbnAux = new PoblacionAbn();
            poblacionAbnAux.setAbn(abnAux);
            poblacionAbnAux.setInegi(poblacionCargada);
            listaPoblacionesAbn.clear();
            listaPoblacionesAbn.add(poblacionAbnAux);
            poblacionCargada.setPoblacionAbn(poblacionAbnAux);
            poblacionCargada.setPoblacionesAbn(listaPoblacionesAbn);

        } catch (Exception e) {
            LOGGER.error("Se ha producido un error al cargar los datos de la población: " + e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, "Error: " + e.getMessage());
        }
        return poblacionCargada;
    }

    /** Método invocado al seleccionar un estado del combo de estados. */
    public void seleccionEstado() {
        try {
            if (estadoSeleccionado != null) {
                // Municipios del estado seleccionado
                listaMunicipios = adminCatFacadeService
                        .findMunicipiosByEstado(estadoSeleccionado.getCodEstado());
                municipioSeleccionado = null;
            }
        } catch (Exception ex) {
            LOGGER.error("error", ex);
        }
    }

    /**
     * Función para RN073: composición el código INEGI.
     * @param estado Estado
     * @param municipio municipio
     * @param codPoblacion String poblacion
     * @return String codigo inegi
     */
    public String componerINEGI(Estado estado, Municipio municipio, String codPoblacion) {
        String inegi = "";
        try {
            if (null != estado && null != estado.getCodEstado()
                    && null != municipio && null != municipio.getId()
                    && !StringUtils.isEmpty(codPoblacion)) {
                if (isNumeric(estado.getCodEstado())
                        && isNumeric(municipio.getId().getCodMunicipio())
                        && isNumeric(codPoblacion)) {
                    inegi = String.format("%02d", Integer.parseInt(estado.getCodEstado()))
                            .concat(String.format("%03d", Integer.parseInt(municipio.getId().getCodMunicipio()))
                                    .concat(String.format("%04d", Integer.parseInt(codPoblacion))));
                }
            }
        } catch (Exception e) {
            MensajesBean.addErrorMsg(MSG_ID, MensajesBean.getTextoResource("catalogo.poblaciones.compone.inegi.error"),
                    "");
        }
        return inegi;
    }

    /**
     * Validacion de población ya existente.
     * @param poblacion población a agregar a la lista
     * @return existencia de la población
     */
    private boolean poblacionRepetida(Poblacion poblacion) {
        String rowKey = (String) poblacion.getInegi();
        if (null != listaPoblaciones) {
            for (int i = 0; i < listaPoblaciones.size(); i++) {
                Poblacion poblacionAux = listaPoblaciones.get(i);
                if (rowKey.equalsIgnoreCase(poblacionAux.getInegi())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Agrega una población nueva a la lista.
     */
    public void agregarPoblacionLista() {
        try {
            listaPoblacionesAbn = new ArrayList<PoblacionAbn>(1);
            if (this.validarDatosFormulario()) {
                Poblacion poblacionCargada = cargaValues();
                if (!poblacionRepetida(poblacionCargada)) {
                    PoblacionAbn poblacionAbnAux = new PoblacionAbn();
                    poblacionAbnAux.setAbn(poblacionCargada.getAbn());
                    poblacionAbnAux.setInegi(poblacionCargada);
                    poblacionCargada.setPoblacionAbn(poblacionAbnAux);
                    listaPoblacionesAbn.add(poblacionAbnAux);
                    poblacionCargada.setPoblacionesAbn(listaPoblacionesAbn);
                    listaPoblaciones.add(poblacionCargada);
                } else {
                    MensajesBean.addErrorMsg(MSG_ID,
                            MensajesBean.getTextoResource("catalogo.poblaciones.agregar.poblacion.error"), "");
                }
            }
        } catch (Exception e) {
            LOGGER.error("Se ha producido un error al cargar los datos de la población en la lista: " + e.getMessage());
            MensajesBean.addErrorMsg(MSG_ID, "Error: " + e.getMessage(), "");
        }
    }

    /**
     * Elimina una población de la tabla.
     * @param dato poblacion
     */
    public void eliminarPoblacionLista(Poblacion dato) {
        listaPoblaciones.remove(dato);
    }

    /**
     * Guarda las nuevas poblaciones.
     */
    public void guardarListaNuevasPoblaciones() {
        try {
            boolean valido = true;
            if (null != listaPoblaciones && !listaPoblaciones.isEmpty()) {
                String nuevaLinea = "<br>";
                StringBuilder sbf = new StringBuilder("Se han guardado las siguientes poblaciones:");
                sbf.append(nuevaLinea);

                for (Poblacion dato : listaPoblaciones) {
                    Poblacion poblacionExiste = adminCatFacadeService.findPoblacionById(dato.getInegi());
                    if (null != poblacionExiste) {
                        valido = false;
                    }

                    sbf.append("Para el estado ").append(dato.getMunicipio().getEstado().getNombre())
                            .append(" con codigo ")
                            .append(dato.getMunicipio().getEstado().getCodEstado()).append(" y el municipio ")
                            .append(dato.getMunicipio().getNombre())
                            .append(" con codigo ").append(dato.getMunicipio().getId())
                            .append(" se ha creado la población ")
                            .append(dato.getNombre())
                            .append(" con codigo ").append(dato.getCdgPoblacion()).append(nuevaLinea);
                }
                if (valido) {
                    adminCatFacadeService.guardaPoblaciones(listaPoblaciones);
                    MensajesBean.addInfoMsg(MSG_ID, sbf.toString(), "");
                } else {
                    MensajesBean
                            .addErrorMsg(MSG_ID, MensajesBean.getTextoResource(
                                    "catalogo.poblaciones.guardar.lista.poblacion.existe.error"), "");
                }
            } else {
                MensajesBean.addErrorMsg(MSG_ID,
                        MensajesBean.getTextoResource("catalogo.poblaciones.guardar.lista.sin.poblaciones.error"), "");
            }

        } catch (RegistroModificadoException rme) {
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0015);
        } catch (Exception e) {
            MensajesBean.addErrorMsg(MSG_ID,
                    MensajesBean.getTextoResource("catalogo.poblaciones.guardar.lista.poblaciones.error"), "");
        }
    }

    /**
     * Restea los datos.
     */
    public void resetCampos() {
        this.estadoSeleccionado = null;
        this.municipioSeleccionado = null;
        this.abnSeleccionado = null;
        this.estatusSeleccionado = null;
        this.codigoPoblacion = null;
        this.nombrePoblacion = null;
        this.listaMunicipios = null;
        this.listaPoblaciones.clear();
        this.listaPoblacionesAbn.clear();
    }

    /**
     * Método para saber si es numerica la cadena.
     * @param cadena cadena de entrada.
     * @return boolean true/false.
     */
    private static boolean isNumeric(String cadena) {
        return cadena.matches("\\d*");
    }

    /**
     * Lista de nuevas poblaciones.
     * @return List<Poblacion>
     */
    public List<Poblacion> getListaPoblaciones() {
        return listaPoblaciones;
    }

    /**
     * Lista de nuevas poblaciones.
     * @param listaPoblaciones List<Poblacion>
     */
    public void setListaPoblaciones(List<Poblacion> listaPoblaciones) {
        this.listaPoblaciones = listaPoblaciones;
    }

    /**
     * Lista de estados.
     * @return List<Estado>
     */
    public List<Estado> getListaEstados() {
        return listaEstados;
    }

    /**
     * Lista de estados.
     * @param listaEstados List<Estado>
     */
    public void setListaEstados(List<Estado> listaEstados) {
        this.listaEstados = listaEstados;
    }

    /**
     * Lista de municipios.
     * @return List<Municipio>
     */
    public List<Municipio> getListaMunicipios() {
        return listaMunicipios;
    }

    /**
     * Lista de municipios.
     * @param listaMunicipios List<Municipio>
     */
    public void setListaMunicipios(List<Municipio> listaMunicipios) {
        this.listaMunicipios = listaMunicipios;
    }

    /**
     * Estado.
     * @return Estado
     */
    public Estado getEstadoSeleccionado() {
        return estadoSeleccionado;
    }

    /**
     * Estado.
     * @param estadoSeleccionado Estado
     */
    public void setEstadoSeleccionado(Estado estadoSeleccionado) {
        this.estadoSeleccionado = estadoSeleccionado;
    }

    /**
     * Municipio.
     * @return Municipio
     */
    public Municipio getMunicipioSeleccionado() {
        return municipioSeleccionado;
    }

    /**
     * Municipio.
     * @param municipioSeleccionado Municipio
     */
    public void setMunicipioSeleccionado(Municipio municipioSeleccionado) {
        this.municipioSeleccionado = municipioSeleccionado;
    }

    /**
     * Código poblacion.
     * @return String
     */
    public String getCodigoPoblacion() {
        return codigoPoblacion;
    }

    /**
     * Código poblacion.
     * @param codigoPoblacion String
     */
    public void setCodigoPoblacion(String codigoPoblacion) {
        this.codigoPoblacion = codigoPoblacion;
    }

    /**
     * Nombre poblacion.
     * @return String
     */
    public String getNombrePoblacion() {
        return nombrePoblacion;
    }

    /**
     * Nombre poblacion.
     * @param nombrePoblacion String
     */
    public void setNombrePoblacion(String nombrePoblacion) {
        this.nombrePoblacion = nombrePoblacion;
    }

    /**
     * ABN.
     * @return String
     */
    public String getAbnSeleccionado() {
        return abnSeleccionado;
    }

    /**
     * ABN.
     * @param abnSeleccionado String
     */
    public void setAbnSeleccionado(String abnSeleccionado) {
        this.abnSeleccionado = abnSeleccionado;
    }

    /**
     * Lista de nuevas poblacionesAbn.
     * @return List<PoblacionAbn>
     */
    public List<PoblacionAbn> getListaPoblacionesAbn() {
        return listaPoblacionesAbn;
    }

    /**
     * Lista de nuevas poblacionesAbn.
     * @param listaPoblacionesAbn List<PoblacionAbn>
     */
    public void setListaPoblacionesAbn(List<PoblacionAbn> listaPoblacionesAbn) {
        this.listaPoblacionesAbn = listaPoblacionesAbn;
    }

    /**
     * Asocia el servicio que se usarán en las búsquedas.
     * @param otservice IOrganizacionTerritorialService
     */
    public void setOtService(IOrganizacionTerritorialService otservice) {
        this.otservice = otservice;
    }

    /**
     * Estatus.
     * @return Estatus
     */
    public Estatus getEstatusSeleccionado() {
        return estatusSeleccionado;
    }

    /**
     * Estatus.
     * @param estatusSeleccionado Estatus
     */
    public void setEstatusSeleccionado(Estatus estatusSeleccionado) {
        this.estatusSeleccionado = estatusSeleccionado;
    }

}
