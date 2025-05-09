package mx.ift.sns.web.backend.ng.redistribucion;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import mx.ift.sns.modelo.central.Central;
import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.pst.TipoModalidad;
import mx.ift.sns.modelo.pst.TipoRed;
import mx.ift.sns.negocio.ng.INumeracionGeograficaService;
import mx.ift.sns.web.backend.common.Errores;
import mx.ift.sns.web.backend.common.MensajesBean;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Almacena información del formulario de Redistribución de Numeración. */
public class DatosFormRedistribucion implements Serializable {

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DatosFormRedistribucion.class);

    /** Identificador del componente de mensajes JSF. */
    private static final String MSG_ID = "MSG_Redistribuciones";

    /** Facade de Servicios de Numeración Geográfica. */
    private INumeracionGeograficaService ngService;

    /** Tipo de Red seleccionada para la búsqueda. */
    private TipoRed tipoRed;

    /** Catálogo de tipos de red. */
    private List<TipoRed> listaTiposRed;

    /** Tipo de Modalidad seleccionada para la búsqueda. */
    private TipoModalidad tipoModalidad;

    /** Catálogo de tipos de modalidad. */
    private List<TipoModalidad> listaTiposModalidad;

    /** Representanción de la clave censal. */
    private String claveCensal;

    /** Identificador de código de área. */
    private String abnUbicacion;

    /** Organitazión Territorial - Estado. */
    private Estado estado;

    /** Organitazión Territorial - Lista de estados. */
    private List<Estado> listaEstados;

    /** Organitazión Territorial - Lista de municipios. */
    private List<Municipio> listaMunicipios;

    /** Organitazión Territorial - Municipio. */
    private Municipio municipio;

    /** Organitazión Territorial - Lista de poblaciones. */
    private List<Poblacion> listaPoblaciones;

    /** Organitazión Territorial - Poblacion. */
    private Poblacion poblacion;

    /** Listado de Proveedores Concesionarios. */
    private List<Proveedor> listaConcesionarios;

    /** Proveedor Concesionario Seleccionado. */
    private Proveedor pstConcesionario;

    /** Listado de Proveedores Arrendatarios. */
    private List<Proveedor> listaArrendatarios;

    /** Proveedor Arrendatario Seleccionado. */
    private Proveedor pstArrendatario;

    /** Central de origen seleccionada. */
    private Central centralOrigen;

    /** Central de destino seleccionada. */
    private Central centralDestino;

    /**
     * Constructor, asocia el servicio de Numeración Geográfica e inicializa las listas.
     * @param pNgService Servicio de Numeración Geográfica
     * @throws Exception En caso de error de inicialización.
     */
    public DatosFormRedistribucion(INumeracionGeograficaService pNgService) throws Exception {
        // Servicio de Numeración geográfica instanciado
        ngService = pNgService;

        // Inicializaciones las listas con valores fijos
        listaEstados = ngService.findEstados();

        // Listas instanciadas vacías para evitar errores JSF
        listaTiposModalidad = new ArrayList<TipoModalidad>();
        listaMunicipios = new ArrayList<Municipio>();
        listaPoblaciones = new ArrayList<Poblacion>();
        listaConcesionarios = new ArrayList<Proveedor>();

        listaArrendatarios = new ArrayList<Proveedor>();
        listaTiposRed = new ArrayList<TipoRed>();
    }

    /** Resetea los valores almacenados. */
    public void clear() {
        tipoRed = null;
        tipoModalidad = null;
        claveCensal = null;
        abnUbicacion = null;
        estado = null;
        municipio = null;
        poblacion = null;
        pstConcesionario = null;
        pstArrendatario = null;
        centralOrigen = null;
        centralDestino = null;

        listaTiposModalidad.clear();
        listaMunicipios.clear();
        listaPoblaciones.clear();
        // listaConcesionarios.clear();
        // listaCentralesOrigen.clear();
        // listaArrendatarios.clear();
        // listaTiposRed.clear();
    }

    /** Método invocado al seleccionar un tipo de red en el combo. */
    public void seleccionTipoRed() {
        try {
            // Tipos de Modalidad en función del tipo de red
            if (tipoRed != null) {
                String red = tipoRed.getCdg();
                if (red.equals(TipoRed.MOVIL) || red.equals(TipoRed.AMBAS)) {
                    listaTiposModalidad = ngService.findAllTiposModalidad();
                } else {
                    listaTiposModalidad.clear();
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Método invocado al modificar la clave censal. */
    public void cambioClaveCensal() {
        try {
            if (!StringUtils.isBlank(claveCensal)) {
                // if (!claveCensal.matches("-?\\d+(\\.\\d+)?")) {
                // MensajesBean.addErrorMsg(MSG_ID, "La clave censal tiene que ser un valor numerico", "");
                //
                // } else {

                // listaEstados.clear();
                listaMunicipios.clear();
                listaPoblaciones.clear();

                Poblacion poblacionAux = ngService.findPoblacionById(claveCensal);
                if (poblacionAux != null) {
                    estado = poblacionAux.getMunicipio().getEstado();
                    municipio = poblacionAux.getMunicipio();
                    poblacion = poblacionAux;

                    // listaEstados.add(estado);
                    listaMunicipios.add(municipio);
                    listaPoblaciones.add(poblacion);
                    if (poblacion.getAbn() != null) {
                        abnUbicacion = poblacion.getAbn().getCodigoAbn().toString();
                    } else {
                        abnUbicacion = null;
                        MensajesBean.addWarningMsg(MSG_ID, "La población seleccionada no tiene ABN asociado", "");
                    }
                } else {
                    this.limpiarDatosUbicacion();
                    MensajesBean.addErrorMsg(MSG_ID, "No existe ubicación para la clave censal introducida", "");
                }
                // }
            } else {
                this.limpiarDatosUbicacion();
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Método invocado al modificar el campo ABN de Ubicación. */
    public void cambioAbnUbicacion() {
        try {
            if (!StringUtils.isEmpty(abnUbicacion)) {
                // listaEstados.clear();
                listaMunicipios.clear();
                listaPoblaciones.clear();

                Poblacion poblacionAux = ngService.getPoblacionAnclaByCodigoAbn(new BigDecimal(abnUbicacion));
                if (poblacionAux != null) {
                    estado = poblacionAux.getMunicipio().getEstado();
                    municipio = poblacionAux.getMunicipio();
                    poblacion = poblacionAux;

                    // listaEstados.add(estado);
                    listaMunicipios.add(municipio);
                    listaPoblaciones.add(poblacion);
                    claveCensal = poblacion.getInegi();
                } else {
                    this.limpiarDatosUbicacion();
                    MensajesBean.addErrorMsg(MSG_ID, "No existe ubicación para el ABN introducido", "");
                }
            } else {
                this.limpiarDatosUbicacion();
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Método invocado al modificar la selección de Municipio. */
    public void habilitarMunicipio() {
        try {
            if (estado != null) {
                listaMunicipios = ngService.findMunicipiosByEstado(estado.getCodEstado());
            } else {
                listaMunicipios.clear();
            }
            listaPoblaciones.clear();
            poblacion = null;
            claveCensal = null;
            abnUbicacion = null;
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Método invocado al modificar la selección de Población. */
    public void habilitarPoblacion() {
        try {
            if (municipio != null) {
                listaPoblaciones = ngService.findAllPoblaciones(
                        estado.getCodEstado(), municipio.getId().getCodMunicipio());
            } else {
                listaPoblaciones.clear();
            }
            poblacion = null;
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        }
    }

    /** Genera la clave censal y el abn de la numeracion solicitada. */
    public void generaClave() {
        if (poblacion != null) {
            claveCensal = poblacion.getInegi();
            if (poblacion.getAbn() != null) {
                abnUbicacion = poblacion.getAbn().getCodigoAbn().toString();
            } else {
                abnUbicacion = null;
                MensajesBean.addWarningMsg(MSG_ID, "La población seleccionada no tiene ABN asociado", "");
            }
        } else {
            claveCensal = null;
            abnUbicacion = null;
        }
    }

    /** Limpia los campos relacionados con la ubicación. */
    private void limpiarDatosUbicacion() {
        abnUbicacion = null;
        claveCensal = null;
        estado = null;
        municipio = null;
        poblacion = null;

        // try {
        // listaEstados = ngService.findEstados();
        // } catch (Exception e) {
        // LOGGER.debug("Error inesperado: " + e.getMessage());
        // MensajesBean.addErrorMsg(MSG_ID, Errores.ERROR_0004);
        // }
    }

    /**
     * Indica si se han seleccionado campos para redistribuir.
     * @return True si hay algún campo informado.
     */
    public boolean hayInfoRedistribucion() {
        boolean hayDatos = (tipoRed != null
                || tipoModalidad != null
                || poblacion != null
                || pstConcesionario != null
                || pstArrendatario != null
                || centralOrigen != null
                || centralDestino != null);

        return hayDatos;
    }

    /**
     * Actualiza los valores de Ubucicación en base a una Población.
     * @param pPoblacion Poblacion
     * @throws Exception en caso de error
     */
    public void actualizarUbicacionFromPoblacion(Poblacion pPoblacion) throws Exception {
        if (pPoblacion != null) {
            // Estado
            estado = pPoblacion.getMunicipio().getEstado();

            // Municipio
            listaMunicipios = ngService.findMunicipiosByEstado(estado.getCodEstado());
            municipio = pPoblacion.getMunicipio();

            // Población
            listaPoblaciones = ngService.findAllPoblaciones(
                    estado.getCodEstado(), municipio.getId().getCodMunicipio());
            poblacion = pPoblacion;
            claveCensal = poblacion.getInegi();

            // ABN
            if (poblacion.getAbn() != null) {
                abnUbicacion = poblacion.getAbn().getCodigoAbn().toString();
            } else {
                abnUbicacion = null;
                MensajesBean.addWarningMsg(MSG_ID, "La población seleccionada no tiene ABN asociado", "");
            }
        }
    }

    // GETTERS & SETTERS

    /**
     * Tipo de Red seleccionada para la búsqueda.
     * @return TipoRed
     */
    public TipoRed getTipoRed() {
        return tipoRed;
    }

    /**
     * Tipo de Red seleccionada para la búsqueda.
     * @param tipoRed TipoRed
     */
    public void setTipoRed(TipoRed tipoRed) {
        this.tipoRed = tipoRed;
    }

    /**
     * Catálogo de tipos de red.
     * @return List<TipoRed>
     */
    public List<TipoRed> getListaTiposRed() {
        return listaTiposRed;
    }

    /**
     * Catálogo de tipos de red.
     * @param listaTiposRed List<TipoRed>
     */
    public void setListaTiposRed(List<TipoRed> listaTiposRed) {
        this.listaTiposRed = listaTiposRed;
    }

    /**
     * Tipo de Modalidad seleccionada para la búsqueda.
     * @return TipoModalidad
     */
    public TipoModalidad getTipoModalidad() {
        return tipoModalidad;
    }

    /**
     * Tipo de Modalidad seleccionada para la búsqueda.
     * @param tipoModalidad TipoModalidad
     */
    public void setTipoModalidad(TipoModalidad tipoModalidad) {
        this.tipoModalidad = tipoModalidad;
    }

    /**
     * Catálogo de tipos de modalidad.
     * @return List<TipoModalidad>
     */
    public List<TipoModalidad> getListaTiposModalidad() {
        return listaTiposModalidad;
    }

    /**
     * Catálogo de tipos de modalidad.
     * @param listaTiposModalidad List<TipoModalidad>
     */
    public void setListaTiposModalidad(List<TipoModalidad> listaTiposModalidad) {
        this.listaTiposModalidad = listaTiposModalidad;
    }

    /**
     * Representanción de la clave censal.
     * @return String
     */
    public String getClaveCensal() {
        return claveCensal;
    }

    /**
     * Representanción de la clave censal.
     * @param claveCensal String
     */
    public void setClaveCensal(String claveCensal) {
        this.claveCensal = claveCensal;
    }

    /**
     * Identificador de código de área.
     * @return String
     */
    public String getAbnUbicacion() {
        return abnUbicacion;
    }

    /**
     * Identificador de código de área.
     * @param abnUbicacion String
     */
    public void setAbnUbicacion(String abnUbicacion) {
        this.abnUbicacion = abnUbicacion;
    }

    /**
     * Organitazión Territorial - Estado.
     * @return Estado
     */
    public Estado getEstado() {
        return estado;
    }

    /**
     * Organitazión Territorial - Estado.
     * @param estado Estado
     */
    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    /**
     * Organitazión Territorial - Lista de estados.
     * @return List<Estado>
     */
    public List<Estado> getListaEstados() {
        return listaEstados;
    }

    /**
     * Organitazión Territorial - Lista de estados.
     * @param listaEstados List<Estado>
     */
    public void setListaEstados(List<Estado> listaEstados) {
        this.listaEstados = listaEstados;
    }

    /**
     * Organitazión Territorial - Lista de municipios.
     * @return List<Municipio>
     */
    public List<Municipio> getListaMunicipios() {
        return listaMunicipios;
    }

    /**
     * Organitazión Territorial - Lista de municipios.
     * @param listaMunicipios List<Municipio>
     */
    public void setListaMunicipios(List<Municipio> listaMunicipios) {
        this.listaMunicipios = listaMunicipios;
    }

    /**
     * Organitazión Territorial - Municipio.
     * @return Municipio
     */
    public Municipio getMunicipio() {
        return municipio;
    }

    /**
     * Organitazión Territorial - Municipio.
     * @param municipio Municipio
     */
    public void setMunicipio(Municipio municipio) {
        this.municipio = municipio;
    }

    /**
     * Organitazión Territorial - Lista de poblaciones.
     * @return List<Poblacion>
     */
    public List<Poblacion> getListaPoblaciones() {
        return listaPoblaciones;
    }

    /**
     * Organitazión Territorial - Lista de poblaciones.
     * @param listaPoblaciones List<Poblacion>
     */
    public void setListaPoblaciones(List<Poblacion> listaPoblaciones) {
        this.listaPoblaciones = listaPoblaciones;
    }

    /**
     * Organitazión Territorial - Poblacion.
     * @return Poblacion
     */
    public Poblacion getPoblacion() {
        return poblacion;
    }

    /**
     * Organitazión Territorial - Poblacion.
     * @param poblacion Poblacion
     */
    public void setPoblacion(Poblacion poblacion) {
        this.poblacion = poblacion;
    }

    /**
     * Listado de Proveedores Concesionarios.
     * @return List<Proveedor>
     */
    public List<Proveedor> getListaConcesionarios() {
        return listaConcesionarios;
    }

    /**
     * Listado de Proveedores Concesionarios.
     * @param listaConcesionarios List<Proveedor>
     */
    public void setListaConcesionarios(List<Proveedor> listaConcesionarios) {
        this.listaConcesionarios = listaConcesionarios;
    }

    /**
     * Proveedor Concesionario Seleccionado.
     * @return Proveedor
     */
    public Proveedor getPstConcesionario() {
        return pstConcesionario;
    }

    /**
     * Proveedor Concesionario Seleccionado.
     * @param pstConcesionario Proveedor
     */
    public void setPstConcesionario(Proveedor pstConcesionario) {
        this.pstConcesionario = pstConcesionario;
    }

    /**
     * Listado de Proveedores Arrendatarios.
     * @return List<Proveedor>
     */
    public List<Proveedor> getListaArrendatarios() {
        return listaArrendatarios;
    }

    /**
     * Listado de Proveedores Arrendatarios.
     * @param listaArrendatarios List<Proveedor>
     */
    public void setListaArrendatarios(List<Proveedor> listaArrendatarios) {
        this.listaArrendatarios = listaArrendatarios;
    }

    /**
     * Proveedor Arrendatario Seleccionado.
     * @return Proveedor
     */
    public Proveedor getPstArrendatario() {
        return pstArrendatario;
    }

    /**
     * Proveedor Arrendatario Seleccionado.
     * @param pstArrendatario Proveedor
     */
    public void setPstArrendatario(Proveedor pstArrendatario) {
        this.pstArrendatario = pstArrendatario;
    }

    /**
     * Central de origen seleccionada.
     * @return Central
     */
    public Central getCentralOrigen() {
        return centralOrigen;
    }

    /**
     * Central de origen seleccionada.
     * @param centralOrigen Central
     */
    public void setCentralOrigen(Central centralOrigen) {
        this.centralOrigen = centralOrigen;
    }

    /**
     * Central de destino seleccionada.
     * @return Central
     */
    public Central getCentralDestino() {
        return centralDestino;
    }

    /**
     * Central de destino seleccionada.
     * @param centralDestino Central
     */
    public void setCentralDestino(Central centralDestino) {
        this.centralDestino = centralDestino;
    }

    /**
     * Facade de Servicios de Numeración Geográfica.
     * @return INumeracionGeograficaService
     */
    public INumeracionGeograficaService getNgService() {
        return ngService;
    }

    /**
     * Facade de Servicios de Numeración Geográfica.
     * @param ngService INumeracionGeograficaService
     */
    public void setNgService(INumeracionGeograficaService ngService) {
        this.ngService = ngService;
    }

}
