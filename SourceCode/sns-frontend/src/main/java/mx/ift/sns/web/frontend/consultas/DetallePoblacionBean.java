package mx.ift.sns.web.frontend.consultas;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.MunicipioPK;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.series.Nir;
import mx.ift.sns.negocio.IConfiguracionFacade;
import mx.ift.sns.negocio.IConsultaPublicaFacade;
import mx.ift.sns.web.frontend.common.MensajesFrontBean;
import mx.ift.sns.web.frontend.util.PaginatorUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bean que controla el dialog del detalle de la poblacion.
 * @author Daniel
 */
@ManagedBean(name = "detallePoblacionBean")
@ViewScoped
public class DetallePoblacionBean implements Serializable {

    /** Servicio de Busqueda Publica. */
    @EJB(mappedName = "ConsultaPublicaFacade")
    private IConsultaPublicaFacade ngPublicService;

    /** Servicio de configuración Publica. */
    @EJB(mappedName = "ConfiguracionFacade")
    private IConfiguracionFacade ngConfiguracionService;

    /** Identificador del componente de mensajes JSF. */
    private static final String MSG_ID = "MSG_ConsultaFront";

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DetallePoblacionBean.class);

    /** Serial ID. */
    private static final long serialVersionUID = 1L;

    /**
     * Poblacion de la que se muestra la info.
     */
    private Poblacion poblacionDetalle;

    /**
     * Lista de concesionarios del abn.
     */
    private List<Proveedor> concesionariosAbn;

    /**
     * Lista de concesionarios de la poblacion.
     */
    private List<Proveedor> concesionariosPoblacion;

    /**
     * Lista de poblaciones del abn.
     */
    private List<Poblacion> poblacionesAbn;

    /**
     * Lista de municipios del abn.
     */
    private List<Municipio> municipiosNir;

    /**
     * Numeracion asignada de la poblacion.
     */
    private BigDecimal numeracionAsignadaPoblacion;

    /**
     * Numeracion asignada del abn.
     */
    private BigDecimal numeracionAsignadaAbn;

    /**
     * Poblacion con mayor numeracion asignada.
     */
    private Poblacion poblacionMaxNumAsignada;

    /**
     * Abn de la poblacion detalle.
     */
    private Abn poblacionDetalleAbn;
    /**
     * Muestra la tabla de poblaciones .
     */
    private boolean paginatedResultsPoblacionesActive;
    /**
     * Muestra la tabla de municipios .
     */
    private boolean paginatedResultsMunicipiosActive;
    /**
     * Muestra la tabla de concesionarios .
     */
    private boolean paginatedResultsConcesionariosActive;
    /**
     * Muestra la tabla de proveedores de una población.
     */
    private boolean paginatedResultsProveedoresActive;

    /** Campo poblacion de un numero consultado. */
    private Poblacion poblacionNumero;

    /**
     * Lista de prestadores de servicio de una poblacion.
     */
    private List<Proveedor> prestadoresServicioPoblacionNumero;

    /**
     * Presusbscripcion.
     */
    private String presuscripcion;

    /**
     * Numeración asignada abn en formato.
     */
    private String numAsignadaAbnFormat;

    /**
     * Numeración asignada población en formato.
     */
    private String numAsignadaPobFormat;
    /**
     * Abn Municipio.
     */
    private Abn abnMunicipio;
    /**
     * Abn proveedor.
     */
    private Abn abnProveedor;
    /**
     * Abn Población.
     */
    private Abn abnPoblacion;
    /**
     * Proveedor por nir.
     */
    private List<Proveedor> proveedoresNir;
    /**
     * Poblaciones con numeración asignada nir.
     */
    private List<Poblacion> poblacionNumeracionAsignadaNir;
    /**
     * Nir consultado.
     */
    private Nir nirConsultado;

    /**
     * Numeración asignada nir en formato String.
     */
    private String numeracionNirFormat;

    /** Número de digitos según longitud nir. */
    private Integer numeroDigitos;

    /**
     * String con los distintos número de registros por página. Por defecto: 5,10,15,20,15
     */
    private String numeroRegistros = "5, 10, 15, 20, 25";

    // ////////////////////////////////////////////MÉTODOS///////////////////////////////////////////
    /**
     * Setea la poblacion detalle y su info.
     * @param poblacionDetalle Poblacion.
     */
    public void setDetallePoblacionData(Poblacion poblacionDetalle) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Población consultada {}", poblacionDetalle.getNombre());
        }
        try {
            this.poblacionDetalle = poblacionDetalle;
            this.setPoblacionDetalleAbn(poblacionDetalle.getAbn());
            if (poblacionDetalle.getAbn().getPresuscripcion() != null) {
                if (poblacionDetalle.getAbn().getPresuscripcion().compareTo("P") == 0) {
                    this.setPresuscripcion("Sí");
                } else {
                    this.setPresuscripcion("No");
                }
            } else {
                this.setPresuscripcion("No");
            }
            this.setConcesionariosPoblacion(ngPublicService.findAllPrestadoresServicioBy(null, null,
                    this.poblacionDetalle,
                    null, null));
            this.setNumeracionAsignadaPoblacion(ngPublicService.getTotalNumRangosAsignadosByPoblacion("", "", null,
                    poblacionDetalle));
            this.setNumAsignadaPobFormat(this.formatoNumeracionAsignada(this.numeracionAsignadaPoblacion));
            this.setPoblacionMaxNumAsignada(ngPublicService
                    .getPoblacionWithMaxNumAsignadaByAbn(this.poblacionDetalleAbn));
        } catch (Exception e) {
            LOGGER.error("Error de carga de datos", e);
            MensajesFrontBean.addErrorMsg(MSG_ID, "Error en la búsqueda de datos en la BBDD");
        }
    }

    /**
     * Activa la tabla de proveedoresPoblacion y setea la lista de proveedoresPoblacion.
     * @param poblacion Poblacion
     */
    public void activateAndSetProveedor(Poblacion poblacion) {
        try {
            int numeroRegistro = PaginatorUtil.getRegistrosPorPagina(ngConfiguracionService);
            // PaginatorUtil.resetPaginacion("FORM_detallePC:DT_provPC", numeroRegistro);
            PaginatorUtil.resetPaginacion(":FORM_detalleNIR:DT_proveedorPoblacion", numeroRegistro);
            this.setNumeroRegistros(PaginatorUtil.getGruposRegistrosPorPagina(numeroRegistro));
            this.setConcesionariosPoblacion(ngPublicService.findAllPrestadoresServicioBy(
                    null, null, poblacion, null, null));
            this.setPaginatedResultsConcesionariosActive(false);
            this.setPaginatedResultsMunicipiosActive(false);
            this.setPaginatedResultsPoblacionesActive(false);
            this.setPaginatedResultsProveedoresActive(true);
        } catch (Exception e) {
            LOGGER.error("Error inesperado en la carga de datos de proveedores", e);
            MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado");
        }
    }

    /**
     * Activa la tabla poblaciones y setea el listado de poblaciones por Abn.
     * @param nirPoblacion Nir
     */
    public void activateAndSetPoblacion(Nir nirPoblacion) {
        try {
            int numeroRegistro = PaginatorUtil.getRegistrosPorPagina(ngConfiguracionService);
            // PaginatorUtil.resetPaginacion("FORM_detallePC:TBL_poblacionesAbnPC", numeroRegistro);
            PaginatorUtil.resetPaginacion(":FORM_detalleNIR:CTM_poblacionesNir:TBL_poblacionesNir", numeroRegistro);
            // PaginatorUtil.resetPaginacion("FORM_detalleP:TBL_poblacionesAbn", numeroRegistro);
            this.setNumeroRegistros(PaginatorUtil.getGruposRegistrosPorPagina(numeroRegistro));
            this.setPoblacionesAbn(ngPublicService.findALLPoblacionesNumeracionAsignadaByNir(nirPoblacion));
            this.setNirConsultado(nirPoblacion);
            this.setPaginatedResultsConcesionariosActive(false);
            this.setPaginatedResultsMunicipiosActive(false);
            this.setPaginatedResultsPoblacionesActive(true);
            this.setPaginatedResultsProveedoresActive(false);
        } catch (Exception e) {
            LOGGER.error("Error inesperado", e);
            MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado al cargar los datos de población");
        }
    }

    /**
     * Activa la tabla municipios y setea el listado de poblaciones por Abn.
     * @param abnMunicipio Abn
     * @param nir Nir
     */
    public void activateAndSetMunicipio(Abn abnMunicipio, Nir nir) {
        try {
            int numeroRegistro = PaginatorUtil.getRegistrosPorPagina(ngConfiguracionService);
            PaginatorUtil.resetPaginacion(":FORM_detalleNIR:CTM_municipiosNir:TBL_municipiosNir", numeroRegistro);
            // PaginatorUtil.resetPaginacion("FORM_detallePC:TBL_municipiosAbnPC", numeroRegistro);
            // PaginatorUtil.resetPaginacion("FORM_detalleP:TBL_municipiosAbn", numeroRegistro);
            this.setNumeroRegistros(PaginatorUtil.getGruposRegistrosPorPagina(numeroRegistro));

            if (abnMunicipio != null && nir != null) {
                this.setAbnMunicipio(abnMunicipio);
                this.setNirConsultado(nir);
                this.setPoblacionesAbn(ngPublicService.findAllPoblacionesByAbn(abnMunicipio));
                this.setMunicipiosNir(ngPublicService.findAllMunicipiosByNir(nir, true));
                this.setPaginatedResultsConcesionariosActive(false);
                this.setPaginatedResultsPoblacionesActive(false);
                this.setPaginatedResultsMunicipiosActive(true);
                this.setPaginatedResultsProveedoresActive(false);
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado al cargar los datos de municipio", e);
            MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado");
        }
    }

    /**
     * Activa la tabla concesionarios y setea el listado de concesionarios por Abn.
     * @param abnPoblacion Abn
     * @param nirConsultado Nir
     */
    public void activateAndSetConcesionario(Abn abnPoblacion, Nir nirConsultado) {
        try {
            int numeroRegistro = PaginatorUtil.getRegistrosPorPagina(ngConfiguracionService);
            // PaginatorUtil.resetPaginacion("FORM_detallePC:TBL_concesionariosAbnPC", numeroRegistro);
            PaginatorUtil
                    .resetPaginacion(":FORM_detalleNIR:CTM_proveedoresNir:TBL_proveedoresNir", numeroRegistro);
            // PaginatorUtil.resetPaginacion("FORM_detalleP:TBL_concesionariosAbn", numeroRegistro);
            this.setNumeroRegistros(PaginatorUtil.getGruposRegistrosPorPagina(numeroRegistro));

            this.setNirConsultado(nirConsultado);
            this.setProveedoresNir(ngPublicService.findAllPrestadoresServicioBy(nirConsultado, null, null, null, null));
            this.setAbnProveedor(abnPoblacion);
            this.setPaginatedResultsPoblacionesActive(false);
            this.setPaginatedResultsMunicipiosActive(false);
            this.setPaginatedResultsConcesionariosActive(true);
            this.setPaginatedResultsProveedoresActive(false);
        } catch (Exception e) {
            LOGGER.error("Error inesperado al cargar los datos del proveedor", e);
            MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado");
        }
    }

    /**
     * Activa la tabla concesionarios y setea el listado de concesionarios por Nir.
     * @param nir Nir
     */
    public void activateAndSetConcesionarioPoblacion(Nir nir) {
        try {
            int numeroRegistro = PaginatorUtil.getRegistrosPorPagina(ngConfiguracionService);
            PaginatorUtil.resetPaginacion(":FORM_detalleNIR:CTM_proveedoresNir:TBL_proveedoresNir", numeroRegistro);
            this.setNumeroRegistros(PaginatorUtil.getGruposRegistrosPorPagina(numeroRegistro));

            this.setProveedoresNir(ngPublicService.findAllPrestadoresServicioBy(
                    nir, null, null, null, null));
            this.setNirConsultado(nir);
            this.setPaginatedResultsPoblacionesActive(false);
            this.setPaginatedResultsMunicipiosActive(false);
            this.setPaginatedResultsConcesionariosActive(true);
            this.setPaginatedResultsProveedoresActive(false);
        } catch (Exception e) {
            LOGGER.error("Error inesperado al cargar los datos de proveedor", e);
            MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado");
        }
    }

    /**
     * Setea la lista de proveedores para una PoblacionNumeracion.
     * @param poblacionNumero Poblacion
     */
    public void setPrestadorNumero(Poblacion poblacionNumero) {
        try {
            this.setPrestadoresServicioPoblacionNumero(ngPublicService
                    .findAllPrestadoresServicioBy(null, null, poblacionNumero, null, null));
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("La poblacion seteada es: {}", poblacionNumero);
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado al cargar los datos de proveedor", e);
            MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado");
        }
    }

    /**
     * Convierte a formato numero USA un número.
     * @param num BigDecimal
     * @return String
     */
    public String formatoNumeracionAsignada(BigDecimal num) {
        NumberFormat numFormato;
        String numeroStr = "";
        try {
            numFormato = NumberFormat.getNumberInstance(Locale.US);
            numeroStr = numFormato.format(num);
        } catch (Exception e) {
            LOGGER.error("Error inesperado al dar formato a la numeración asignada" + e.getMessage());
            MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado");
        }
        return numeroStr;
    }

    /**
     * Crea la clave inegi del municipio.
     * @param idMunicipio MunicipioPK
     * @return String
     */
    public String createInegiMunicipio(MunicipioPK idMunicipio) {
        String codEstado = "";
        String codMunicipio = "";
        String inegiMunicipio = "";
        try {
            if (idMunicipio != null) {
                codEstado = idMunicipio.getCodEstado();
                codMunicipio = idMunicipio.getCodMunicipio();
                codEstado = (codEstado.length() == 1) ? "0" + codEstado : codEstado;
                if (codMunicipio.length() == 1) {
                    codMunicipio = "00" + codMunicipio;
                } else if (codMunicipio.length() == 2) {
                    codMunicipio = "0" + codMunicipio;
                }
                inegiMunicipio = codEstado + codMunicipio;
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado al generar el formato del inegi municipio" + e.getMessage());
            MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado");
        }
        return inegiMunicipio;

    }

    /**
     * Devuelve las poblaciones con numeración asignada por nir.
     * @param nir Nir
     * @return List<Poblacion>
     */
    public List<Poblacion> getPoblacionesNumeracionAsignadaNir(Nir nir) {
        try {
            this.setPoblacionNumeracionAsignadaNir(ngPublicService.findALLPoblacionesNumeracionAsignadaByNir(nir));
        } catch (Exception e) {
            LOGGER.error("Error inesperado al cargar datos de poblaciones con numeración asignada asociados a un nir",
                    e.getMessage());
            MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado");
        }
        return this.getPoblacionNumeracionAsignadaNir();
    }

    /**
     * Devuelve los proveedors con numeración asignada por nir.
     * @param nir Nir
     * @return List<Proveedor>
     */
    public List<Proveedor> getProveedoresNumeracionAsignadaNir(Nir nir) {
        try {
            this.setProveedoresNir(ngPublicService.findAllPrestadoresServicioBy(nir, null, null, null, null));
        } catch (Exception e) {
            LOGGER.error("Error inesperado al cargar datos de proveedores con numeración asignada asociados a un nir",
                    e.getMessage());
            MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado");
        }
        return this.getProveedoresNir();
    }

    /**
     * Devuelve la nuemeración asignada a un nir en formato string USA.
     * @param nir Nir
     * @return int
     */
    public String getNumeracionNir(Nir nir) {
        try {
            int numeracionNir = (ngPublicService.findNumeracionesAsignadasNir(nir));
            BigDecimal bg = new BigDecimal(numeracionNir);
            this.setNumeracionNirFormat(this.formatoNumeracionAsignada(bg));
        } catch (Exception e) {
            LOGGER.error("Error inesperado al cargar datos de  numeración asignada asociada a un nir" + e.getMessage());
            MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado");
        }
        return this.getNumeracionNirFormat();
    }

    /**
     * Método que setea los municipios pertenecientes aun nir para mostrar en las tabla cuantos son.
     * @param nir Nir
     * @return List<Municipio>
     */
    public List<Municipio> getMunicipiosByNir(Nir nir) {
        try {
            this.setMunicipiosNir(ngPublicService.findAllMunicipiosByNir(nir, true));
        } catch (Exception e) {
            LOGGER.error("Error inesperado al cargar datos de municipios asociados a un nir" + e.getMessage());
            MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado");
        }
        return this.getMunicipiosNir();
    }

    /**
     * Activa la tabla municipios y setea el listado de poblaciones por Abn.
     * @param poblacion Poblacion
     * @param nir Nir
     */
    public void activateAndSetMunicipioNirPoblacion(Poblacion poblacion, Nir nir) {
        try {

            this.setAbnMunicipio(ngPublicService.getAbnByPoblacion(poblacion));
            this.setNirConsultado(nir);
            this.setPoblacionesAbn(ngPublicService.findAllPoblacionesByAbn(this.getAbnMunicipio()));
            this.setMunicipiosNir(ngPublicService.findAllMunicipiosByNir(nir, true));
            this.setPaginatedResultsConcesionariosActive(false);
            this.setPaginatedResultsPoblacionesActive(false);
            this.setPaginatedResultsMunicipiosActive(true);
        } catch (Exception e) {
            LOGGER.error("Error inesperado al cargar datos" + e.getMessage());
            MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado");
        }
    }

    /**
     * Devuelve el número de dígitos según el nir.
     * @param nir Nir
     * @return Integer
     */
    public Integer getDigitos(Nir nir) {
        try {
            String codigoNir = String.valueOf(nir.getCodigo());
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Nir: {} y length {}", codigoNir, codigoNir.length());
            }
            switch (codigoNir.length()) {
            case 1:
                this.setNumeroDigitos(9);
                break;
            case 2:
            //    this.setNumeroDigitos(8);
                this.setNumeroDigitos(10);
                break;
            case 3:
           //     this.setNumeroDigitos(7);
                this.setNumeroDigitos(10);
                break;

            }
        } catch (Exception e) {
            LOGGER.error("Error al setear el número de dígitos del numero interno" + e.getMessage());
        }
        return this.getNumeroDigitos();
    }

    // /////////////////////////////////////////GETTERS Y SETTERS////////////////////////////////////////
    /**
     * Numeración asignada nir en formato String.
     * @return the numeracionNirFormat
     */
    public String getNumeracionNirFormat() {
        return numeracionNirFormat;
    }

    /**
     * Numeración asignada nir en formato String.
     * @param numeracionNirFormat the numeracionNirFormat to set
     */
    public void setNumeracionNirFormat(String numeracionNirFormat) {
        this.numeracionNirFormat = numeracionNirFormat;
    }

    /**
     * Poblaciones con numeración asignada nir.
     * @return the poblacionNumeracionAsignadaNir
     */
    public List<Poblacion> getPoblacionNumeracionAsignadaNir() {
        return poblacionNumeracionAsignadaNir;
    }

    /**
     * Poblaciones con numeración asignada nir.
     * @param poblacionNumeracionAsignadaNir the poblacionNumeracionAsignadaNir to set
     */
    public void setPoblacionNumeracionAsignadaNir(List<Poblacion> poblacionNumeracionAsignadaNir) {
        this.poblacionNumeracionAsignadaNir = poblacionNumeracionAsignadaNir;
    }

    /**
     * Nir consultado.
     * @return the nirConsultado
     */
    public Nir getNirConsultado() {
        return nirConsultado;
    }

    /**
     * Nir consultado.
     * @param nirConsultado the nirConsultado to set
     */
    public void setNirConsultado(Nir nirConsultado) {
        this.nirConsultado = nirConsultado;
    }

    /**
     * Proveedor por nir.
     * @return the proveedoresNir
     */
    public List<Proveedor> getProveedoresNir() {
        return proveedoresNir;
    }

    /**
     * Proveedor por nir.
     * @param proveedoresNir the proveedoresNir to set
     */
    public void setProveedoresNir(List<Proveedor> proveedoresNir) {
        this.proveedoresNir = proveedoresNir;
    }

    /**
     * Abn proveedor.
     * @return the abnProveedor
     */
    public Abn getAbnProveedor() {
        return abnProveedor;
    }

    /**
     * Abn proveedor.
     * @param abnProveedor the abnProveedor to set
     */
    public void setAbnProveedor(Abn abnProveedor) {
        this.abnProveedor = abnProveedor;
    }

    /**
     * Abn Población.
     * @return the abnPoblacion
     */
    public Abn getAbnPoblacion() {
        return abnPoblacion;
    }

    /**
     * Abn Población.
     * @param abnPoblacion the abnPoblacion to set
     */
    public void setAbnPoblacion(Abn abnPoblacion) {
        this.abnPoblacion = abnPoblacion;
    }

    /**
     * Abn Municipio.
     * @return the abnMunicipio
     */
    public Abn getAbnMunicipio() {
        return abnMunicipio;
    }

    /**
     * Abn Municipio.
     * @param abnMunicipio the abnMunicipio to set
     */
    public void setAbnMunicipio(Abn abnMunicipio) {
        this.abnMunicipio = abnMunicipio;
    }

    /**
     * Numeración asignada población en formato.
     * @return the numAsignadaPobFormat
     */
    public String getNumAsignadaPobFormat() {
        return numAsignadaPobFormat;
    }

    /**
     * Numeración asignada población en formato.
     * @param numAsignadaPobFormat the numAsignadaPobFormat to set
     */
    public void setNumAsignadaPobFormat(String numAsignadaPobFormat) {
        this.numAsignadaPobFormat = numAsignadaPobFormat;
    }

    /**
     * Numeración asignada abn en formato.
     * @return the numAsignadaAbnFormat
     */
    public String getNumAsignadaAbnFormat() {
        return numAsignadaAbnFormat;
    }

    /**
     * Numeración asignada abn en formato.
     * @param numAsignadaAbnFormat the numAsignadaAbn to set
     */
    public void setNumAsignadaAbnFormat(String numAsignadaAbnFormat) {
        this.numAsignadaAbnFormat = numAsignadaAbnFormat;
    }

    /**
     * Presusbscripcion.
     * @return the presuscripcion
     */
    public String getPresuscripcion() {
        return presuscripcion;
    }

    /**
     * Presusbscripcion.
     * @param presuscripcion the presuscripcion to set
     */
    public void setPresuscripcion(String presuscripcion) {
        this.presuscripcion = presuscripcion;
    }

    /**
     * Muestra la tabla de proveedores de una población.
     * @return the paginatedResultsProveedoresActive
     */
    public boolean isPaginatedResultsProveedoresActive() {
        return paginatedResultsProveedoresActive;
    }

    /**
     * Muestra la tabla de proveedores de una población.
     * @param paginatedResultsProveedoresActive the paginatedResultsProveedoresActive to set
     */
    public void setPaginatedResultsProveedoresActive(boolean paginatedResultsProveedoresActive) {
        this.paginatedResultsProveedoresActive = paginatedResultsProveedoresActive;
    }

    /**
     * Lista de prestadores de servicio de una poblacion.
     * @return the prestadoresServicioPoblacionNumero
     */
    public List<Proveedor> getPrestadoresServicioPoblacionNumero() {
        return prestadoresServicioPoblacionNumero;
    }

    /**
     * Lista de prestadores de servicio de una poblacion.
     * @param prestadoresServicioPoblacionNumero the prestadoresServicioPoblacionNumero to set
     */
    public void setPrestadoresServicioPoblacionNumero(List<Proveedor> prestadoresServicioPoblacionNumero) {
        this.prestadoresServicioPoblacionNumero = prestadoresServicioPoblacionNumero;
    }

    /**
     * Campo poblacion de un numero consultado.
     * @return the poblacionNumero
     */
    public Poblacion getPoblacionNumero() {
        return poblacionNumero;
    }

    /**
     * Campo poblacion de un numero consultado.
     * @param poblacionNumero the poblacionNumero to set
     */
    public void setPoblacionNumero(Poblacion poblacionNumero) {
        this.poblacionNumero = poblacionNumero;
    }

    /**
     * Poblacion de la que se muestra la info.
     * @return the poblacionDetalle
     */
    public Poblacion getPoblacionDetalle() {
        return poblacionDetalle;
    }

    /**
     * Poblacion de la que se muestra la info.
     * @param poblacionDetalle the poblacionDetalle to set
     */
    public void setPoblacionDetalle(Poblacion poblacionDetalle) {
        this.poblacionDetalle = poblacionDetalle;
    }

    /**
     * Lista de concesionarios del abn.
     * @return the concesionariosAbn
     */
    public List<Proveedor> getConcesionariosAbn() {
        return concesionariosAbn;
    }

    /**
     * Lista de concesionarios del abn.
     * @param concesionariosAbn the concesionariosAbn to set
     */
    public void setConcesionariosAbn(List<Proveedor> concesionariosAbn) {
        this.concesionariosAbn = concesionariosAbn;
    }

    /**
     * Lista de concesionarios de la poblacion.
     * @return the concesionariosPoblacion
     */
    public List<Proveedor> getConcesionariosPoblacion() {
        return concesionariosPoblacion;
    }

    /**
     * Lista de concesionarios de la poblacion.
     * @param concesionariosPoblacion the concesionariosPoblacion to set
     */
    public void setConcesionariosPoblacion(List<Proveedor> concesionariosPoblacion) {
        this.concesionariosPoblacion = concesionariosPoblacion;
    }

    /**
     * Lista de poblaciones del abn.
     * @return the poblacionesAbn
     */
    public List<Poblacion> getPoblacionesAbn() {
        return poblacionesAbn;
    }

    /**
     * Lista de poblaciones del abn.
     * @param poblacionesAbn the poblacionesAbn to set
     */
    public void setPoblacionesAbn(List<Poblacion> poblacionesAbn) {

        this.poblacionesAbn = poblacionesAbn;
    }

    /**
     * Lista de municipios del abn.
     * @return the municipiosNir
     */
    public List<Municipio> getMunicipiosNir() {
        return municipiosNir;
    }

    /**
     * Lista de municipios del abn.
     * @param municipiosNir the municipiosNir to set
     */
    public void setMunicipiosNir(List<Municipio> municipiosNir) {
        this.municipiosNir = municipiosNir;
    }

    /**
     * Numeracion asignada de la poblacion.
     * @return the numeracionAsignadaPoblacion
     */
    public BigDecimal getNumeracionAsignadaPoblacion() {
        return numeracionAsignadaPoblacion;
    }

    /**
     * Numeracion asignada de la poblacion.
     * @param numeracionAsignadaPoblacion the numeracionAsignadaPoblacion to set
     */
    public void setNumeracionAsignadaPoblacion(BigDecimal numeracionAsignadaPoblacion) {
        this.numeracionAsignadaPoblacion = numeracionAsignadaPoblacion;
    }

    /**
     * Numeracion asignada del abn.
     * @return the numeracionAsignadaAbn
     */
    public BigDecimal getNumeracionAsignadaAbn() {
        return numeracionAsignadaAbn;
    }

    /**
     * Numeracion asignada del abn.
     * @param numeracionAsignadaAbn the numeracionAsignadaAbn to set
     */
    public void setNumeracionAsignadaAbn(BigDecimal numeracionAsignadaAbn) {
        this.numeracionAsignadaAbn = numeracionAsignadaAbn;
    }

    /**
     * Poblacion con mayor numeracion asignada.
     * @return the poblacionMaxNumAsignada
     */
    public Poblacion getPoblacionMaxNumAsignada() {
        return poblacionMaxNumAsignada;
    }

    /**
     * Poblacion con mayor numeracion asignada.
     * @param poblacionMaxNumAsignada the poblacionMaxNumAsignada to set
     */
    public void setPoblacionMaxNumAsignada(Poblacion poblacionMaxNumAsignada) {
        this.poblacionMaxNumAsignada = poblacionMaxNumAsignada;
    }

    /**
     * Abn de la poblacion detalle.
     * @return the poblacionDetalleAbn
     */
    public Abn getPoblacionDetalleAbn() {
        return poblacionDetalleAbn;
    }

    /**
     * Abn de la poblacion detalle.
     * @param poblacionDetalleAbn the poblacionDetalleAbn to set
     */
    public void setPoblacionDetalleAbn(Abn poblacionDetalleAbn) {
        this.poblacionDetalleAbn = poblacionDetalleAbn;
    }

    /**
     * Muestra la tabla de poblaciones .
     * @return the paginatedResultsPoblacionesActive
     */
    public boolean isPaginatedResultsPoblacionesActive() {
        return paginatedResultsPoblacionesActive;
    }

    /**
     * Muestra la tabla de poblaciones .
     * @param paginatedResultsPoblacionesActive the paginatedResultsPoblacionesActive to set
     */
    public void setPaginatedResultsPoblacionesActive(boolean paginatedResultsPoblacionesActive) {
        this.paginatedResultsPoblacionesActive = paginatedResultsPoblacionesActive;
    }

    /**
     * Muestra la tabla de municipios .
     * @return the paginatedResultsMunicipiosActive
     */
    public boolean isPaginatedResultsMunicipiosActive() {
        return paginatedResultsMunicipiosActive;
    }

    /**
     * Muestra la tabla de municipios .
     * @param paginatedResultsMunicipiosActive the paginatedResultsMunicipiosActive to set
     */
    public void setPaginatedResultsMunicipiosActive(boolean paginatedResultsMunicipiosActive) {
        this.paginatedResultsMunicipiosActive = paginatedResultsMunicipiosActive;
    }

    /**
     * Muestra la tabla de concesionarios .
     * @return the paginatedResultsConcesionariosActive
     */
    public boolean isPaginatedResultsConcesionariosActive() {
        return paginatedResultsConcesionariosActive;
    }

    /**
     * Muestra la tabla de concesionarios .
     * @param paginatedResultsConcesionariosActive the paginatedResultsConcesionariosActive to set
     */
    public void setPaginatedResultsConcesionariosActive(boolean paginatedResultsConcesionariosActive) {
        this.paginatedResultsConcesionariosActive = paginatedResultsConcesionariosActive;
    }

    /**
     * Número de digitos según longitud nir.
     * @return the numeroDigitos
     */
    public Integer getNumeroDigitos() {
        return numeroDigitos;
    }

    /**
     * Número de digitos según longitud nir.
     * @param numeroDigitos the numeroDigitos to set
     */
    public void setNumeroDigitos(Integer numeroDigitos) {
        this.numeroDigitos = numeroDigitos;
    }

    /**
     * String con los distintos número de registros por página. Por defecto: 5,10,15,20,15
     * @return the numeroRegistros
     */
    public String getNumeroRegistros() {
        return numeroRegistros;
    }

    /**
     * String con los distintos número de registros por página. Por defecto: 5,10,15,20,15
     * @param numeroRegistros the numeroRegistros to set
     */
    public void setNumeroRegistros(String numeroRegistros) {
        this.numeroRegistros = numeroRegistros;
    }

}
