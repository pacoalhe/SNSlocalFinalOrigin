package mx.ift.sns.web.frontend.areas;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.*;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.persistence.NoResultException;
import javax.persistence.criteria.Order;

import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.ot.*;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.series.Nir;
import mx.ift.sns.negocio.IConsultaPublicaFacade;
import mx.ift.sns.negocio.usu.IUsuariosService;
import mx.ift.sns.web.frontend.common.MensajesFrontBean;
import mx.ift.sns.web.frontend.consultas.ConsultaPublicaMarcacionBean;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import mx.ift.sns.web.frontend.areas.Areas;


/**
 * Bean que controla la vista principal de áreas geográficas de numeración.
 * @author X51461MO
 */
@ManagedBean(name = "areasGeoNumeracionMainBean")
@ViewScoped
public class AreasGeoNumeracionMainBean implements Serializable {

    /** Servicio de Busqueda Publica. */
    @EJB(mappedName = "ConsultaPublicaFacade")
    private IConsultaPublicaFacade ngPublicService;

    /** Servicio de usuarios. */
    @EJB(mappedName = "UsuariosService")
    private IUsuariosService usuariosService;

    /** Bean detallePoblacionBean. */
    @ManagedProperty(value = "#{consultaPublicaMarcacionBean}")
    private ConsultaPublicaMarcacionBean consultaPublicaMarcacionBean;

    /** Refactorización para el uso de la lista de proveedores por zona */
    @ManagedProperty(value = "#{dialogInfoBean}")
    private DialogInfoBean dialogInfoBean;

    public void setDialogInfoBean(DialogInfoBean dialogInfoBean) {
        this.dialogInfoBean = dialogInfoBean;
    }

    /** Identificador del componente de mensajes JSF. */
    private static final String MSG_ID = "MSG_ConsultaFront";

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AreasGeoNumeracionMainBean.class);

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Campo poblacion buscada. */
    private Poblacion poblacion;

    /** Lista de poblaciones. */
    private List<Poblacion> poblaciones;

    /** Flag comprobacion input poblacion activado. */
    private boolean populationInputActivated = true;

    /** Flag comprobacion boton formulario activado. */
    private boolean formButtonActivated = false;

    /** Flag para renderizado de tabla poblacion. */
    private boolean populationsTableActivated = false;
    /**
     * Path de la imagen general de estados.
     */
    private String path = "/img/mapas/estados.gif";

    /** Path de la imagen de zona
     *
     */

    private String pathMapaZona;

    public String getPathMapaZona() {
        return pathMapaZona;
    }


    /** Flag para renderizado de la tabla estado. */
    private boolean estateTable = false;

    /** Flag para renderizado de lista desplegable de búsqueda de estado y mapas de estado. */
    private boolean mapAndListActivsted = false;

    /** Lista de concesionarios que prestan servicio en un estado. */
    private List<Proveedor> concesionariosEstado;

    /** Número de concesionarios que prestan servicio en un estado. */
    private Integer numeroConcesionariosEstado;

    /** Estado. */
    private Estado estado;

    /** Estado seleccionado en la lista desplegable. */
    //FJAH 06.06.2025 Refactorizacion a Zona
    //private String estadoSelected;

    /** Municipio seleccionado en la lista desplegable. */
    private String municipioSelected;

    //FJAH 05.06.2025 Refactorización para region -> zona
    /** Lista de todos los Estados. */
    //private List<Estado> listaEstado;

    /**
     * Lista de municipos por estado.
     */
    private List<Municipio> municipiosEstado;
    /**
     * Municipio.
     */
    private Municipio municipio;

    /**
     * Flag para mostras o no el mapa.
     */
    private boolean activateMap = true;

    /**
     * Código de estado.
     */
    private String codEstado;

    /**
     * Número total de poblaciones con numeracion por estado.
     */
    private List<Poblacion> poblacionesNumeracionEstado;

    /**
     * Lista de ABN por estado.
     */
    private List<Abn> abnInEstado;
    
  
    
    
    @SuppressWarnings("unchecked")
	public List comparaABN() {
    	
    	
        System.out.println("Lista Sin Orden:");

        Collection<Abn> collection = abnInEstado;
        for (int i=0; i<abnInEstado.size(); i++)
        	System.out.println("ID : " + abnInEstado.get(i).getCodigoAbn() + " NOMBRE: " + abnInEstado.get(i).getNombre() );

        
      //	Version 8
      //  	abnInEstado.sort(Comparator.comparing(Abn::getNombre).thenComparing(Abn::getCodigoAbn));
 
        Collections.sort(abnInEstado, new comparaAbn());
        
        System.out.println("Lista Ordenada:");
        for (int i=0; i<abnInEstado.size(); i++)
        	System.out.println("ID : " + abnInEstado.get(i).getCodigoAbn() + " NOMBRE: " + abnInEstado.get(i).getNombre() );

        
        
   	/*
    	Collections.sort(abnInEstado, new Comparator() {
    		
    		public int compare(Object o1, Object o2) {
    			
    			String x1 = ((Abn) o1).getNombre();
    			String x2 = ((Abn) o2).getNombre();
    			int sComp = x1.compareTo(x2);
    			
    			 if (sComp != 0) {
    	               return sComp;
     	            } 
 	               System.out.println("Orden : " + sComp);

    			 
  //  			 BigDecimal x3 = ((Abn) o1).getCodigoAbn();
  //  			 BigDecimal x4 = ((Abn) o2).getCodigoAbn();
    	            
  //  			 return x3.compareTo(x4);
    			
    		}});
  */  		
        return abnInEstado;

    }
    
    
    
    
    
    /**
     * Número de ABN por estado.
     */
    private Integer numeroAbnEstado;
    /**
     * Código Abn.
     */
    private BigDecimal numeroAbn;
    /**
     * Lista de Nirs de un Abn.
     */
    private List<Nir> listaNirAbn;
    /**
     * Flag para el componente de lista desplegable de NIR/Abn.
     */
    private boolean listNirActivated = false;
    /**
     * Flag para el componente de la lista desplegable de Nir/Municipio.
     */
    private boolean listNirMunicipioActivated = false;
    /**
     * Flag para activar la lista desplegable de ABN.
     */
    private boolean listAbnActivated = true;
    /**
     * Flag para activar la lista desplegable de Municipio.
     */
    private boolean listMunicipioActivated = false;
    /**
     * Lista con los Abn de un municipio.
     */
    private List<Abn> listAbnMunicipio;
    /**
     * Lista con los Nir de un municipio.
     */
    private List<Nir> listNirMunicipio;
    /**
     * Lista de poblaciones de un municipio.
     */
    private List<Poblacion> poblacionesMunicipio;
    /**
     * Lista de proveedores de un municipio.
     */
    private List<Proveedor> proveedorMunicipio;

    /**
     * LISTA consesionariosZona
     */
    private List<Proveedor> concesionariosZona;


    /**
     * Numeración total asignada a un muninicipio.
     */
    private Integer numeracionAsignadaMunicipio;
    /**
     * Valor de clave inegi = Código estado + Código municipio.
     */
    private String inegiMunicipio;

    /**
     * Actica el botón búsqueda ABN.
     */
    private boolean btnBusquedaABN = false;
    /**
     * Activa el botón búsqueda de municipio.
     */
    private boolean btnBusquedaMunicipio = true;
    /**
     * Flag para activar el boton de limpiar1.
     */
    private boolean btnClean1 = false;
    /**
     * Flag para activar el boton de limpiar2.
     */
    private boolean btnClean2 = true;
    /**
     * Flag para activar el botón submit 1.
     */
    private boolean submit1Activate = true;
    /**
     * Flag para activar el botón submit 2.
     */
    private boolean submit2Activate = true;
    /**
     * Nir de la lista.
     */
    private String nirSelected;
    /**
     * Numeración asignada en un estado en formato número.
     */
    private String numeracionEstadoFormat;
    /**
     * Numeración asignada en un mumnicipio en formato número.
     */
    private String numeracionMunicipioFormat;

    /**
     * Lista de áreas del mapa general de México.
     */
    private List<Areas> listaAreas;
    /**
     * Lista de áreas de los mapas de detalle estado.
     * private List<EstadoArea> listaAreasNir;
     */
    private List<Areas> listaAreasNir;
    /**
     * Path de la ruta de la imagen de los mapas de detalle estado.
     */
    private String pathMapaNir;
    /**
     * id del link generado para vicular los áreas de los mapas de detalle estado.
     */
    private int numeroLink = 0;
    /**
     * Lista de NIR por estado.
     */
    private List<Nir> nirsEstado;

    //FJAH 06.06.2025 refactorizacion para zonas
    private Region region; // Zona seleccionada
    private List<Region> listaRegiones; // Combo de zonas

    // --- Agregados por Zona ---
    private List<Municipio> municipiosZona;
    private Set<Proveedor> empresasZona;
    private BigDecimal numeracionZona;
    private String clavesInegiZona;
    private int cantidadMunicipiosZona;
    private int cantidadEmpresasZona;

    // Hardcodeo zona -> estados (ID_REGION -> List<ID_ESTADO>)
    private static final Map<Integer, List<Integer>> estadosPorRegion = new HashMap<>();
    static {
        estadosPorRegion.put(1, Arrays.asList(1, 2, 3, 4)); // FJAH 06.06.2025
        estadosPorRegion.put(2, Arrays.asList(5, 6, 7));
        estadosPorRegion.put(3, Arrays.asList(8, 9, 10, 11));
        estadosPorRegion.put(4, Arrays.asList(12, 13, 14));
        estadosPorRegion.put(5, Arrays.asList(15, 16, 17, 18));
        estadosPorRegion.put(6, Arrays.asList(19, 20, 21));
        estadosPorRegion.put(7, Arrays.asList(22, 23, 24, 25));
        estadosPorRegion.put(8, Arrays.asList(26, 27, 28));
        estadosPorRegion.put(9, Arrays.asList(29, 30, 31, 32));
        // FJAH 06.06.2025 Hardcodeo ejemplo, pon los IDs reales aquí
    }

    private Integer totalMunicipiosZona;


    // --- Getters y setters solo para zonas/agregados ---

    public Integer getTotalMunicipiosZona() {
        return totalMunicipiosZona;
    }

    public Region getRegion() { return region; }
    public void setRegion(Region region) { this.region = region; }
    public List<Region> getListaRegiones() { return listaRegiones; }
    public void setListaRegiones(List<Region> listaRegiones) { this.listaRegiones = listaRegiones; }
    public List<Municipio> getMunicipiosZona() { return municipiosZona; }
    public Set<Proveedor> getEmpresasZona() { return empresasZona; }
    public String getNumeracionZona() { return numeracionZona != null ? numeracionZona.toPlainString() : "0"; }
    public String getClavesInegiZona() { return clavesInegiZona; }
    public int getCantidadMunicipiosZona() { return cantidadMunicipiosZona; }
    public int getCantidadEmpresasZona() { return cantidadEmpresasZona; }

    private BigDecimal idRegionSelected;

    public BigDecimal getIdRegionSelected() { return idRegionSelected; }
    public void setIdRegionSelected(BigDecimal idRegionSelected) { this.idRegionSelected = idRegionSelected; }

    public List<Proveedor> getConcesionariosZona() {
        return concesionariosZona;
    }

    public void setConcesionariosZona(List<Proveedor> concesionariosZona) {
        this.concesionariosZona = concesionariosZona;
    }

    public Integer getNumeroConcesionariosZona() {
        return (concesionariosZona != null ? concesionariosZona.size() : 0);
    }

    private List<Municipio> municipiosZonaTmp = new ArrayList<>();

    public List<Municipio> getMunicipiosZonaTmp() {
        return municipiosZonaTmp;
    }

    public void setMunicipiosZonaTmp(List<Municipio> municipiosZonaTmp) {
        this.municipiosZonaTmp = municipiosZonaTmp;
    }






    // //////////////////////POSTCONSTRUCT////////////////////////////
    /**
     * Postconstructor.
     */
    //FJAH 06.06.2025 refactorizacion para zonas
    @PostConstruct
    public void init() {
        //listaRegiones = ngPublicService.getRegiones();
        List<Region> todasLasRegiones = ngPublicService.getRegiones();
        listaRegiones = new ArrayList<Region>();

        for (Region r : todasLasRegiones) {
            if (!"Zona 1".equalsIgnoreCase(r.getDescripcion())) {
                listaRegiones.add(r);
            }
        }

        region = null;

        // NUEVO: cargar zonas vectorizadas
        Areas areas = new Areas();
        this.setListaAreas(areas.getListaZonas());
        this.setListaAreasNir(areas.generaListaZonas());

        LOGGER.info("Zonas cargadas: " + this.listaAreas.size());

        limpiarDatosZona();
    }

    /*
    @PostConstruct
    public void init() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("PostConstruc: está activado");
        }
        try {
            Areas areas = new Areas();
            this.setListaAreas(areas.getListaAreasEstados());
            //FJAH 05.06.2025 Refactorización para region -> zona
            //setListaEstado(ngPublicService.findEstados());
            listaRegiones = ngPublicService.getRegiones(); // Método nuevo llena la lista con regiones
            region = null;
            RequestContext.getCurrentInstance().update("P_containerVista2");
        } catch (Exception e) {
            LOGGER.error("Error al cargar las zonas de la lista o del mapa principal" + e.getMessage());
            MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado al cargar las zonas en el mapa o la lista");
        }

    }

     */

    // /////////////////////////////MÉTODOS//////////////////////////////////////////////

    // FJAH 10.06.2025 refactorización para zonas y visibilidad de la tabla
    public void onChangeRegion() {
        System.out.println("== INICIA onChangeRegion ==");
        LOGGER.info("Listener ejecutado ONCHANGE REGION, id=" + idRegionSelected);
        limpiarDatosZona();

        if (idRegionSelected == null) {
            this.setEstateTable(false);
            return;
        }

        // Buscar región seleccionada
        this.region = null;
        for (Region reg : listaRegiones) {
            if (reg.getIdRegion().toString().equals(idRegionSelected.toString())) {
                this.region = reg;
                break;
            }
        }

        if (this.region == null) {
            this.setEstateTable(false);
            return;
        }

        LOGGER.debug("== INICIA carga de datos agregados ==");

        Set<Municipio> municipiosZonaTmp = new HashSet<>();
        Set<Proveedor> empresasZonaTmp = new HashSet<>();

        // Municipios por zona (nueva lógica) y lista de muncipios para descargar archivo excel.
        try {
            LOGGER.debug("== Carga de Municipios por Zona desde NIR ==");

            Integer zona = region.getIdRegion().intValue();

            // Hardcode para cantidadMunicipiosZona
            switch (zona) {
                case 2: this.cantidadMunicipiosZona = 444; break;
                case 3: this.cantidadMunicipiosZona = 141; break;
                case 4: this.cantidadMunicipiosZona = 251; break;
                case 5: this.cantidadMunicipiosZona = 78; break;
                case 6: this.cantidadMunicipiosZona = 37; break;
                case 7: this.cantidadMunicipiosZona = 177; break;
                case 8: this.cantidadMunicipiosZona = 141; break;
                case 9: this.cantidadMunicipiosZona = 529; break;
                default: this.cantidadMunicipiosZona = 0; break;
            }

            //List<Municipio> municipiosZona = ngPublicService.findMunicipiosByZona(zona);
            //Long total = ngPublicService.getTotalMunicipiosByZona(zona);

            //if (municipiosZona != null) {
            //    municipiosZonaTmp.clear(); // Limpiar antes por si es recarga
            //    municipiosZonaTmp.addAll(municipiosZona);
            //}

            //totalMunicipiosZona = total != null ? total.intValue() : 0;
            totalMunicipiosZona = this.cantidadMunicipiosZona;

            // Validación opcional
            //if (municipiosZonaTmp.size() != totalMunicipiosZona) {
            //    LOGGER.warn("¡Inconsistencia! Lista trae " + municipiosZonaTmp.size() +
            //            " pero el total reportado es " + totalMunicipiosZona);
            //}

        } catch (Exception e) {
            LOGGER.error("Error al obtener municipios por zona " + region.getIdRegion() + ": " + e.getMessage(), e);
        }

        // Carga de Proveedores por zona
        try {
            LOGGER.debug("== Carga de Proveedores ==");
            List<Proveedor> proveedoresZona = ngPublicService.findAllPrestadoresServicioByZona(region.getIdRegion().intValue());
            if (proveedoresZona != null) empresasZonaTmp.addAll(proveedoresZona);
        } catch (Exception e) {
            LOGGER.error("Error al obtener Proveedores de la zona " + region.getIdRegion() + ": " + e.getMessage());
            numeracionZona = BigDecimal.ZERO;
        }

        // Carga de numeración por zona
        try {
            LOGGER.debug("== Carga de Numeración ==");
            Integer total = ngPublicService.getTotalNumeracionAsignadaPorZona(region.getIdRegion().intValue());
            numeracionZona = new BigDecimal(total != null ? total : 0);
        } catch (Exception e) {
            LOGGER.error("Error al obtener numeración de la zona " + region.getIdRegion() + ": " + e.getMessage());
            numeracionZona = BigDecimal.ZERO;
        }

        //this.municipiosZona = new ArrayList<>(municipiosZonaTmp);
        this.empresasZona = empresasZonaTmp;
        this.concesionariosZona = new ArrayList<>(empresasZonaTmp);
        this.numeracionZona = numeracionZona;

        this.clavesInegiZona = ""; // Reservado por si luego se requiere
        //this.cantidadMunicipiosZona = this.municipiosZona.size();
        this.cantidadEmpresasZona = this.empresasZona.size();

        String rutaImagenZona = "/img/mapas/" + region.getIdRegion() + ".gif";
        this.setPathMapaNir(rutaImagenZona);
        LOGGER.debug("Imagen asignada para zona: " + rutaImagenZona);

        this.setEstateTable(true);
        this.setActivateMap(false);

        // Consola depuración
        System.out.println("municipiosZona: " + this.cantidadMunicipiosZona);
        System.out.println("empresasZona: " + this.empresasZona.size());
        System.out.println("numeracionZona: " + numeracionZona);
        System.out.println("estateTable: " + estateTable);
    }

    private void limpiarDatosZona() {
        this.municipiosZonaTmp = new ArrayList<>();
        this.empresasZona = new HashSet<>();
        this.numeracionZona = BigDecimal.ZERO;
        this.clavesInegiZona = "";
        this.cantidadMunicipiosZona = 0;
        this.cantidadEmpresasZona = 0;
    }

    /** Comprueba si algun campo esta relleno y deshabilita o habilita inputs y boton. */

    public void checkFormStatus() {
        try {
            if (poblacion != null && poblacion.getNombre() != null) {
                if (StringUtils.isNotEmpty(poblacion.getNombre())) {
                    setPopulationInputActivated(true);
                    setFormButtonActivated(true);
                }

            }
            if ((poblacion != null && poblacion.getNombre() != null
                    && poblacion.getNombre().equals("") || poblacion != null
                    && poblacion.getNombre() == null)) {
                setPopulationInputActivated(true);
                setPopulationsTableActivated(false);
                setPopulationsTableActivated(false);
                setFormButtonActivated(false);
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado" + e.getMessage());
            MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado");
        }

    }

    /**
     * Autocompleta la lista de poblaciones.
     * @param query String
     * @return List<String>
     */
    public List<String> completeText(String query) {
        List<String> poblacionesNameAux = new ArrayList<String>(1);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("ngPublicService:" + ngPublicService);
        }

        try {
            poblacionesNameAux = ngPublicService.findAllPoblacionesNameLikeNombre(query);

        } catch (NoResultException e) {
            LOGGER.error("error inesperado" + e.getMessage());
        } catch (Exception e) {
            LOGGER.error("error inspeserado" + e.getMessage());
        }

        return poblacionesNameAux;
    }

    /**
     * Metodo que realiza distintos tipos de busqueda dependiendo del input activo.
     */
    public void publicSearch() {
        try {
            if (this.isPopulationInputActivated()) {
                this.setActivateMap(false);
                this.setFormButtonActivated(false);
                this.setPopulationInputActivated(false);
                if (poblacion.getNombre().length() > 4) {
                    setPoblaciones(ngPublicService.findAllPoblacionesLikeNombre(poblacion.getNombre()));
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("El listado de poblaciones capturado es: {}", this.poblaciones);
                    }
                } else {
                    MensajesFrontBean.addInfoMsg("MSG_ConsultaFront",
                            "El campo 'consecutivo' ha de tener un formato máximo de 9 dígitos", "");
                }
                setPopulationsTableActivated(true);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("flag tabla poblaciones: {}", this.populationsTableActivated);
                }

            }
        } catch (Exception e) {
            LOGGER.error("Error insperado" + e.getMessage());
            MensajesFrontBean.addErrorMsg(MSG_ID, "Error insperado");
        }
    }

    /** Metodo que resetea el formulario y activa los inputs. */

    public void resetSearch() {
        try {
            this.setPopulationInputActivated(true);
            this.setFormButtonActivated(false);
            this.setPopulationsTableActivated(false);
            this.setActivateMap(true);
            this.setEstateTable(false);
            this.poblacion.setNombre("");
            //this.setEstadoSelected("");
            this.region = null; // FJAH 06.06.2025 Limpia la zona seleccionada
        } catch (Exception e) {
            LOGGER.error("Error insperado" + e.getMessage());
            MensajesFrontBean.addErrorMsg(MSG_ID, "Error insperado");
        }
    }

    /**
     * Método que setea la información del estado seleccionado en la lista desplegable y muestra la información general
     * del estado seleccionado.
     */
    /* FJAH 06.06.2025 Refactorizar porla Zona
    public void searchEstado() {
        try {
            this.cleanAndReset();
            if (this.estadoSelected != null) {
                String idEstado = this.estadoSelected;
                this.setEstado(ngPublicService.findEstadoById(idEstado));
                this.setPathMapaNir("/img/mapas/" + idEstado + ".gif");
                this.creaMapaNir(this.getEstado());
                this.setEstateTable(true);
                this.setActivateMap(false);
                this.setMapAndListActivsted(false);
                if (this.estado != null && (this.estado.getCodEstado().length() == 1)) {
                    this.setCodEstado("0" + idEstado);
                } else {
                    this.setCodEstado(idEstado);
                }
                BigDecimal bg = new BigDecimal(ngPublicService
                        .getTotalNumeracionAsignadaByEstado(this.estado));
                this.setNumeracionEstadoFormat(this.formatoNumeracionAsignada(bg));
                this.setPoblacionesNumeracionEstado(ngPublicService.findAllPoblacionesEstadoNumeracion(this.estado));
                this.setConcesionariosEstado(ngPublicService.findAllPrestadoresServicioBy(null, null, null, null,
                        this.estado));
                this.setNumeroConcesionariosEstado(this.concesionariosEstado.size());
                this.setMunicipiosEstado(ngPublicService.findMunicipiosByEstado(idEstado));
                this.setAbnInEstado(ngPublicService.findAbnInEstado(idEstado));
                this.setNumeroAbnEstado(this.abnInEstado.size());
                this.setNirsEstado(ngPublicService.findAllNirByEstado(this.estado));
                this.setListaNirAbn(null);
                this.setListNirMunicipio(null);
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado de carga de datos al buscar por estado." + e.getMessage());
            MensajesFrontBean.addErrorMsg(MSG_ID, "Error de de base de datos.");
        }
    }
     */

    /**
     * Método que setea la información del estado seleccionado en el mapa principal y muestra la información general del
     * estado seleccionado.
     * @param idEstado String
     */
    public void searchEstadoMap(String idEstado) {
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Estado id: {}", idEstado);
            }
            if (idEstado != null) {

                this.setPathMapaNir("/img/mapas/" + idEstado + ".gif");
                this.setEstado(ngPublicService.findEstadoById(idEstado));
                this.creaMapaNir(this.getEstado());
                this.setEstateTable(true);
                this.setActivateMap(false);
                this.setMapAndListActivsted(false);
                if (this.estado != null && (this.estado.getCodEstado().length() == 1)) {
                    this.setCodEstado("0" + idEstado);
                } else {
                    this.setCodEstado(idEstado);
                }
                BigDecimal bg = new BigDecimal(ngPublicService
                        .getTotalNumeracionAsignadaByEstado(this.estado));
                this.setNumeracionEstadoFormat(this.formatoNumeracionAsignada(bg));
                this.setPoblacionesNumeracionEstado(ngPublicService.findAllPoblacionesEstadoNumeracion(this.estado));

                this.setConcesionariosEstado(ngPublicService.findAllPrestadoresServicioBy(null, null, null, null,
                        this.estado));
                this.setNumeroConcesionariosEstado(this.concesionariosEstado.size());
                this.setMunicipiosEstado(ngPublicService.findMunicipiosByEstado(idEstado));
                this.setAbnInEstado(ngPublicService.findAbnInEstado(idEstado));
                this.setNumeroAbnEstado(this.abnInEstado.size());
                this.setNirsEstado(ngPublicService.findAllNirByEstado(this.estado));
                this.setListNirMunicipio(null);
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado al cargar la información del área seleccionada." + e.getMessage());
            MensajesFrontBean.addErrorMsg(MSG_ID, "Error de de base de datos.");
        }

    }

    /**
     * Búsca los Nir del abn seleccionado en la lista desplegable del método de búsqueda por Abn y activa la lista
     * desplegable de NIR.
     */
    public void searchNirAbn() {
        try {
            this.setListNirActivated(true);
            this.setListMunicipioActivated(false);
            if (this.numeroAbn != null) {
                this.setListaNirAbn(ngPublicService.findAllNirByAbn(this.numeroAbn));
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado al cargar la lista de nirs asociados a un Abn" + e.getMessage());
        }

    }

    /**
     * Búsca los Nir del municipio seleccionado en la lista desplegable del método de búsqueda por municipio y activa la
     * lista desplegable de NIR.
     */
    public void searchNirMunicipio() {
        try {
            this.setListNirMunicipioActivated(true);
            this.setListAbnActivated(false);
            MunicipioPK idm = new MunicipioPK();
            idm.setCodEstado(this.estado.getCodEstado());
            if (this.municipioSelected != null) {
                idm.setCodMunicipio(this.municipioSelected);

                this.setMunicipio(ngPublicService.findMunicipioById(idm));
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("El municipio seleccionado es {},{}", this.municipio);
                }

                this.setListNirMunicipio(ngPublicService.finAllNirInMunicipio(this.municipioSelected,
                        this.estado.getCodEstado()));

                this.setListAbnMunicipio(ngPublicService.findAbnInMunicipio(this.municipioSelected,
                        this.estado.getCodEstado()));
                this.setPoblacionesMunicipio(ngPublicService
                        .findALLPoblacionesNumeracionAsignadaByMunicipio(this.municipio));
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("se han seteado {} poblaciones para el municipio", this.poblacionesMunicipio.size());
                }
                this.setProveedorMunicipio(ngPublicService.findAllPrestadoresServicioBy(null, null, null,
                        this.municipio, null));
                this.setNumeracionAsignadaMunicipio(ngPublicService
                        .getTotalNumRangosAsignadosByMunicipio(this.municipio));
                BigDecimal bg = new BigDecimal(this.numeracionAsignadaMunicipio);
                this.setNumeracionMunicipioFormat(this.formatoNumeracionAsignada(bg));
                String codEstado = "";
                String codMunicipio = "";
                codEstado = (this.estado.getCodEstado().length() == 1) ? "0" + this.estado.getCodEstado() : this.estado
                        .getCodEstado();
                if (this.municipioSelected.length() == 1) {
                    codMunicipio = "00" + this.municipioSelected;
                } else if (this.municipioSelected.length() == 2) {
                    codMunicipio = "0" + this.municipioSelected;
                } else {
                    codMunicipio = this.municipioSelected;
                }

                this.setInegiMunicipio(codEstado + codMunicipio);
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado al cargar la información del área seleccionada." + e.getMessage());
            MensajesFrontBean.addErrorMsg(MSG_ID, "Error de de base de datos.");
        }
    }

    /** Activa la búsqueda por municipio. */
    public void activeBusquedaMunicipio() {
        this.cleanAbn();
        this.setBtnBusquedaABN(true);
        this.setBtnBusquedaMunicipio(false);
        this.setListMunicipioActivated(true);
        this.setListAbnActivated(false);
        this.setListNirActivated(false);
        this.setListNirMunicipioActivated(false);
        this.setBtnClean1(true);
        this.setBtnClean2(false);
        this.setSubmit1Activate(true);
        this.setSubmit2Activate(true);
    }

    /** Activa la búsqueda por abn. */
    public void activeBusquedaAbn() {
        this.setBtnBusquedaMunicipio(true);
        this.setBtnBusquedaABN(false);
        this.setListAbnActivated(true);
        this.setListNirActivated(false);
        this.setListNirMunicipioActivated(false);
        this.setListMunicipioActivated(false);
        this.clean();
        this.setBtnClean1(false);
        this.setBtnClean2(true);
        this.setSubmit1Activate(true);
        this.setSubmit2Activate(true);
    }

    /** Limpia las listas deplegables de la búsqueda. */
    public void clean() {
        try {
            this.setNumeroAbn(null);
            this.setMunicipioSelected(null);
            this.setListNirMunicipio(null);
            this.setNirSelected(null);
        } catch (Exception e) {
            LOGGER.error("Error inesperado" + e.getMessage());
            MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado");
        }

    }

    /** Limpia las listas deplegables de la búsqueda y activa la de abn por defecto. */
    public void cleanAndReset() {
        this.clean();
        this.setBtnClean1(false);
        this.setBtnClean2(true);
        this.setSubmit1Activate(true);
        this.setSubmit2Activate(true);
        this.setBtnBusquedaABN(false);
        this.setBtnBusquedaMunicipio(true);
        this.setListAbnActivated(true);
        this.setListNirMunicipioActivated(false);
        this.setListNirActivated(false);
        this.setListMunicipioActivated(false);

    }

    /** Limpia las listas deplegables de la búsqueda por abn. */
    public void cleanAbn() {
        this.clean();
        this.setListNirMunicipioActivated(false);
        this.setListNirActivated(false);
        this.setBtnClean1(false);
        this.setSubmit1Activate(true);
        this.setSubmit2Activate(true);

    }

    /** Limpia las listas deplegables de la búsqueda por municipio. */
    public void cleanMunicipio() {
        this.clean();
        this.setListNirMunicipioActivated(false);
        this.setListNirActivated(false);
        this.setBtnClean2(false);
        this.setSubmit1Activate(true);
        this.setSubmit2Activate(true);

    }

    /**
     * Convierte a formato número US un número.
     * @param num BigDecimal
     * @return String
     */
    public String formatoNumeracionAsignada(BigDecimal num) {
        String numeroStr = "";
        try {
            NumberFormat numFormato = NumberFormat.getNumberInstance(Locale.US);
            numeroStr = numFormato.format(num);
        } catch (Exception e) {
            LOGGER.error("Error inesperado al dar formato a la numeración asignada" + e.getMessage());
            MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado");
        }
        return numeroStr;
    }

    /**
     * Método que muestra la búsqueda por estados.
     * @param estado Estado
     */

    public void viewMaps(Estado estado) {
        if (estado != null) {
            try {
                this.setEstado(estado);
                String idEstado = this.estado.getCodEstado();
                this.setPathMapaNir("/img/mapas/" + idEstado + ".gif");
                this.creaMapaNir(this.getEstado());
                if (this.estado != null && (idEstado.length() == 1)) {
                    this.setCodEstado("0" + idEstado);
                } else {
                    this.setCodEstado(idEstado);
                }
                this.setPathMapaNir("/img/mapas/" + idEstado + ".gif");
                this.creaMapaNir(this.getEstado());
                BigDecimal bg = new BigDecimal(ngPublicService
                        .getTotalNumeracionAsignadaByEstado(this.estado));
                this.setNumeracionEstadoFormat(this.formatoNumeracionAsignada(bg));
                this.setPoblacionesNumeracionEstado(ngPublicService.findAllPoblacionesEstadoNumeracion(this.estado));
                this.setConcesionariosEstado(ngPublicService.findAllConcesionariosByEstado(this.estado));
                this.setNumeroConcesionariosEstado(this.concesionariosEstado.size());
                this.setMunicipiosEstado(ngPublicService.findMunicipiosByEstado(idEstado));
                this.setAbnInEstado(ngPublicService.findAbnInEstado(idEstado));
                this.setNumeroAbnEstado(this.abnInEstado.size());
                this.setEstateTable(true);

                consultaPublicaMarcacionBean.resetSearch();
                consultaPublicaMarcacionBean.setConsultaActivada(false);
            } catch (Exception e) {
                LOGGER.error("Error en la búsqueda de información en la base de datos." + e.getMessage());
                MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado");
            }
        }

    }

    /**
     * Genera la lista de areas del mapa de detalle estado.
     * @param estado Estado
     */
    private void creaMapaNir(Estado estado) {
        this.listaAreasNir = new ArrayList<>(); // limpia la lista sin error
    }
    /*
    private void creaMapaNir(Estado estado) {
        try {
            this.setListaAreasNir(ngPublicService.findAllAreaEstadoByEstado(estado));
        } catch (Exception e) {
            LOGGER.error("Error al crear las áreas en el segundo mapa" + e.getMessage());
            MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado");
        }
    }
     */

    /**
     * Genera un lista de identificadores para los links necesarios en mapa de detalle estado.
     * @param size int
     * @return Integer
     */
    public Integer generaIdLink(int size) {
        try {
            this.setNumeroLink(this.getNumeroLink() + 1);
            if (this.numeroLink == size) {
                this.setNumeroLink(0);
                return size - 1;
            }
        } catch (Exception e) {
            LOGGER.error("Error al generar los links de la áreas del segundo mapa" + e.getMessage());
            MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado");

        }
        return this.getNumeroLink() - 1;

    }

    // ////////////////////////////////GETTER Y SETTERS/////////////////////////////////////
    /**
     * Método que vuelve al mapa general de estados desde la vista del mapa de nirs al pulsar en el botón volver.
     */
    public void volver() {
        this.estateTable = false;
        this.activateMap = true;
        //FJAH 06.06.2025 Refactorización por la Zona
        this.region = null; // Limpia la zona seleccionada en vez del estado
        this.idRegionSelected = null; // Esto limpia el combo
    }

    /**
     * Flag para activar el boton de limpiar1.
     * @return the btnClean1
     */
    public boolean isBtnClean1() {
        return btnClean1;
    }

    /**
     * Flag para activar el boton de limpiar1.
     * @param btnClean1 the btnClean1 to set
     */
    public void setBtnClean1(boolean btnClean1) {
        this.btnClean1 = btnClean1;
    }

    /**
     * id del link generado para vicular los áreas de los mapas de detalle estado.
     * @return the numeroLink
     */
    public Integer getNumeroLink() {
        return numeroLink;
    }

    /**
     * id del link generado para vicular los áreas de los mapas de detalle estado.
     * @param numeroLink the numeroLink to set
     */
    public void setNumeroLink(Integer numeroLink) {
        this.numeroLink = numeroLink;
    }

    /**
     * Path de la ruta de la imagen de los mapas de detalle estado.
     * @return the pathMapaNir
     */
    public String getPathMapaNir() {
        return pathMapaNir;
    }

    /**
     * Path de la ruta de la imagen de los mapas de detalle estado.
     * @param pathMapaNir the pathMapaNir to set
     */
    public void setPathMapaNir(String pathMapaNir) {
        this.pathMapaNir = pathMapaNir;
    }

    /**
     * Lista de áreas de los mapas de detalle estado.
     * @return the listaAreasNir
     */
    //public List<EstadoArea> getListaAreasNir() {
    public List<Areas> getListaAreasNir() {
        return listaAreasNir;
    }

    /**
     * Lista de áreas de los mapas de detalle estado.
     * @param listaAreasNir the listaAreasNir to set
     */
    //public void setListaAreasNir(List<EstadoArea> listaAreasNir) {
    public void setListaAreasNir(List<Areas> listaAreasNir) {
        this.listaAreasNir = listaAreasNir;
    }

    /**
     * Lista de áreas del mapa general de México.
     * @return the listaAreas
     */
    public List<Areas> getListaAreas() {
        return listaAreas;
    }

    /**
     * Lista de áreas del mapa general de México.
     * @param listaAreas the listaAreas to set
     */
    public void setListaAreas(List<Areas> listaAreas) {
        this.listaAreas = listaAreas;
    }

    /**
     * Bean ConsultaPublicaMarcacionBean.
     * @return the consultaPublicaMarcacionBean
     */
    public ConsultaPublicaMarcacionBean getConsultaPublicaMarcacionBean() {
        return consultaPublicaMarcacionBean;
    }

    /**
     * Bean ConsultaPublicaMarcacionBean.
     * @param consultaPublicaMarcacionBean the consultaPublicaMarcacionBean to set
     */
    public void setConsultaPublicaMarcacionBean(ConsultaPublicaMarcacionBean consultaPublicaMarcacionBean) {
        this.consultaPublicaMarcacionBean = consultaPublicaMarcacionBean;
    }

    /**
     * Path de la imagen general de estados.
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * Path de la imagen general de estados.
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Numeración asignada en un mumnicipio en formato número.
     * @return the numeracionMunicipioFormat
     */
    public String getNumeracionMunicipioFormat() {
        return numeracionMunicipioFormat;
    }

    /**
     * Numeración asignada en un mumnicipio en formato número.
     * @param numeracionMunicipioFormat the numeracionMunicipioFormat to set
     */
    public void setNumeracionMunicipioFormat(String numeracionMunicipioFormat) {
        this.numeracionMunicipioFormat = numeracionMunicipioFormat;
    }

    /**
     * Numeración asignada en un estado en formato número.
     * @return the numeracionEstadoFormat
     */
    public String getNumeracionEstadoFormat() {
        return numeracionEstadoFormat;
    }

    /**
     * Numeración asignada en un estado en formato número.
     * @param numeracionEstadoFormat the numeracionEstadoFormat to set
     */
    public void setNumeracionEstadoFormat(String numeracionEstadoFormat) {
        this.numeracionEstadoFormat = numeracionEstadoFormat;
    }

    /**
     * Número total de poblaciones con numeracion por estado.
     * @return the poblacionesNumeracionEstado
     */
    public List<Poblacion> getPoblacionesNumeracionEstado() {
        return poblacionesNumeracionEstado;
    }

    /**
     * Número total de poblaciones con numeracion por estado.
     * @param poblacionesNumeracionEstado the poblacionesNumeracionEstado to set
     */
    public void setPoblacionesNumeracionEstado(List<Poblacion> poblacionesNumeracionEstado) {
        this.poblacionesNumeracionEstado = poblacionesNumeracionEstado;
    }

    /**
     * Nir de la lista.
     * @return the nirSelected
     */
    public String getNirSelected() {
        return nirSelected;
    }

    /**
     * Nir de la lista.
     * @param nirSelected the nirSelected to set
     */
    public void setNirSelected(String nirSelected) {
        this.nirSelected = nirSelected;
    }

    /**
     * Flag para activar el botón submit 1.
     * @return the submit1Activate
     */
    public boolean isSubmit1Activate() {
        return submit1Activate;
    }

    /**
     * Flag para activar el botón submit 1.
     * @param submit1Activate the submit1Activate to set
     */
    public void setSubmit1Activate(boolean submit1Activate) {
        this.submit1Activate = submit1Activate;
    }

    /**
     * Flag para activar el botón submit 2.
     * @return the submit2Activate
     */
    public boolean isSubmit2Activate() {
        return submit2Activate;
    }

    /**
     * Flag para activar el botón submit 2.
     * @param submit2Activate the submit2Activate to set
     */
    public void setSubmit2Activate(boolean submit2Activate) {
        this.submit2Activate = submit2Activate;
    }

    /**
     * Flag para activar el boton de limpiar2.
     * @return the btnClean2
     */
    public boolean isBtnClean2() {
        return btnClean2;
    }

    /**
     * Flag para activar el boton de limpiar2.
     * @param btnClean2 the btnClean2 to set
     */
    public void setBtnClean2(boolean btnClean2) {
        this.btnClean2 = btnClean2;
    }

    /**
     * Actica el botón búsqueda ABN.
     * @return the btnBisquedaABN
     */
    public boolean isBtnBusquedaABN() {
        return btnBusquedaABN;
    }

    /**
     * Actica el botón búsqueda ABN.
     * @param btnBusquedaABN the btnBusquedaABN to set
     */
    public void setBtnBusquedaABN(boolean btnBusquedaABN) {
        this.btnBusquedaABN = btnBusquedaABN;
    }

    /**
     * Activa el botón búsqueda de municipio.
     * @return the btnBusquedaMunicipio
     */
    public boolean isBtnBusquedaMunicipio() {
        return btnBusquedaMunicipio;
    }

    /**
     * Activa el botón búsqueda de municipio.
     * @param btnBusquedaMunicipio the btnBusquedaMunicipio to set
     */
    public void setBtnBusquedaMunicipio(boolean btnBusquedaMunicipio) {
        this.btnBusquedaMunicipio = btnBusquedaMunicipio;
    }

    /**
     * Flag para activar la lista desplegable de ABN.
     * @return the listAbnActivated
     */
    public boolean isListAbnActivated() {
        return listAbnActivated;
    }

    /**
     * Flag para activar la lista desplegable de ABN.
     * @param listAbnActivated the listAbnActivated to set
     */
    public void setListAbnActivated(boolean listAbnActivated) {
        this.listAbnActivated = listAbnActivated;
    }

    /**
     * Flag para activar la lista desplegable de Municipio.
     * @return the listMunicipioActivated
     */
    public boolean isListMunicipioActivated() {
        return listMunicipioActivated;
    }

    /**
     * Flag para activar la lista desplegable de Municipio.
     * @param listMunicipioActivated the listMunicipioActivated to set
     */
    public void setListMunicipioActivated(boolean listMunicipioActivated) {
        this.listMunicipioActivated = listMunicipioActivated;
    }

    /**
     * Valor de clave inegi = Código estado + Código municipio.
     * @return the inegiMunicipio
     */
    public String getInegiMunicipio() {
        return inegiMunicipio;
    }

    /**
     * Valor de clave inegi = Código estado + Código municipio.
     * @param inegiMunicipio the inegiMunicipio to set
     */
    public void setInegiMunicipio(String inegiMunicipio) {
        this.inegiMunicipio = inegiMunicipio;
    }

    /**
     * @return the poblacionesMunicipio
     */
    public List<Poblacion> getPoblacionesMunicipio() {
        return poblacionesMunicipio;
    }

    /**
     * @param poblacionesMunicipio the poblacionesMunicipio to set
     */
    public void setPoblacionesMunicipio(List<Poblacion> poblacionesMunicipio) {
        this.poblacionesMunicipio = poblacionesMunicipio;
    }

    /**
     * municipio.
     * @return the municipio
     */
    public Municipio getMunicipio() {
        return municipio;
    }

    /**
     * municipio.
     * @param municipio the municipio to set
     */
    public void setMunicipio(Municipio municipio) {
        this.municipio = municipio;
    }

    /**
     * Campo poblacion buscada.
     * @return the poblacion
     */
    public Poblacion getPoblacion() {
        return poblacion;
    }

    /**
     * Campo poblacion buscada.
     * @param poblacion the poblacion to set
     */
    public void setPoblacion(Poblacion poblacion) {
        this.poblacion = poblacion;
    }

    /**
     * @return the poblaciones
     */
    public List<Poblacion> getPoblaciones() {
        return poblaciones;
    }

    /**
     * @param poblaciones the poblaciones to set
     */
    public void setPoblaciones(List<Poblacion> poblaciones) {
        this.poblaciones = poblaciones;
    }

    /**
     * Flag comprobacion input poblacion activado.
     * @return the populationInputActivated
     */
    public boolean isPopulationInputActivated() {
        return populationInputActivated;
    }

    /**
     * Flag comprobacion input poblacion activado.
     * @param populationInputActivated the populationInputActivated to set
     */
    public void setPopulationInputActivated(boolean populationInputActivated) {
        this.populationInputActivated = populationInputActivated;
    }

    /**
     * Flag comprobacion boton formulario activado.
     * @return the formButtonActivated
     */
    public boolean isFormButtonActivated() {
        return formButtonActivated;
    }

    /**
     * Flag comprobacion boton formulario activado.
     * @param formButtonActivated the formButtonActivated to set
     */
    public void setFormButtonActivated(boolean formButtonActivated) {
        this.formButtonActivated = formButtonActivated;
    }

    /**
     * Flag para renderizado de tabla poblacion.
     * @return the populationsTableActivated
     */
    public boolean isPopulationsTableActivated() {
        return populationsTableActivated;
    }

    /**
     * Flag para renderizado de tabla poblacion.
     * @param populationsTableActivated the populationsTableActivated to set
     */
    public void setPopulationsTableActivated(boolean populationsTableActivated) {
        this.populationsTableActivated = populationsTableActivated;
    }

    /**
     * Estado.
     * @return the estado
     */
    public Estado getEstado() {
        return estado;
    }

    /**
     * Estado.
     * @param estado the estado to set
     */
    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    /**
     * Lista de todos los Estados.
     * @return the listaEstado
     */
    //FJAH 06.06.2025 Refactorización para region -> zona
    //public List<Estado> getListaEstado() { return listaEstado; }

    /**
     * Lista de todos los Estados.
     * @param listaRegiones the listaEstado to set
     */
    //FJAH 05.06.2025 Refactorización para region -> zona
    //public void setListaEstado(List<Estado> listaEstado) { this.listaEstado = listaEstado; }

    /**
     * Estado seleccionado en la lista desplegable.
     * @return the estadoSelected
     */
    //FJAH 06.06.2025 Refactorizacion a Zona
    //public String getEstadoSelected() { return estadoSelected; }


    /**
     * Estado seleccionado en la lista desplegable.
     * @param estadoSelected the estadoSelected to set
     */
    //FJAH 06.06.2025 Refactorizacion a Zona
    //public void setEstadoSelected(String estadoSelected) { this.estadoSelected = estadoSelected; }

    /**
     * Flag para mostras o no el mapa.
     * @return the activateMap
     */
    public boolean isActivateMap() {
        return activateMap;
    }

    /**
     * Flag para mostras o no el mapa.
     * @param activateMap the activateMap to set
     */
    public void setActivateMap(boolean activateMap) {
        this.activateMap = activateMap;
    }

    /**
     * Código de estado.
     * @return the codEstado
     */
    public String getCodEstado() {
        return codEstado;
    }

    /**
     * Código de estado.
     * @param codEstado the codEstado to set
     */
    public void setCodEstado(String codEstado) {
        this.codEstado = codEstado;
    }

    /**
     * Flag para renderizado de la tabla estado.
     * @return the estateTable
     */
    public boolean isEstateTable() {
        return estateTable;
    }

    /**
     * Flag para renderizado de la tabla estado.
     * @param estateTable the estateTable to set
     */
    public void setEstateTable(boolean estateTable) {
        this.estateTable = estateTable;
    }

    /**
     * Flag para renderizado de lista desplegable de búsqueda de estado y mapas de estado.
     * @return the mapAndListActivsted
     */
    public boolean isMapAndListActivsted() {
        return mapAndListActivsted;
    }

    /**
     * Flag para renderizado de lista desplegable de búsqueda de estado y mapas de estado.
     * @param mapAndListActivsted the mapAndListActivsted to set
     */
    public void setMapAndListActivsted(boolean mapAndListActivsted) {
        this.mapAndListActivsted = mapAndListActivsted;
    }

    /**
     * Lista de concesionarios que prestan servicio en un estado.
     * @return the concesionariosEstado
     */
    public List<Proveedor> getConcesionariosEstado() {
        return concesionariosEstado;
    }

    /**
     * Lista de concesionarios que prestan servicio en un estado.
     * @param concesionariosEstado the concesionariosEstado to set
     */
    public void setConcesionariosEstado(List<Proveedor> concesionariosEstado) {
        this.concesionariosEstado = concesionariosEstado;
    }

    /**
     * Número de concesionarios que prestan servicio en un estado.
     * @return the numeroConcesionariosEstado
     */
    public Integer getNumeroConcesionariosEstado() {
        return numeroConcesionariosEstado;
    }

    /**
     * Número de concesionarios que prestan servicio en un estado.
     * @param numeroConcesionariosEstado the numeroConcesionariosEstado to set
     */
    public void setNumeroConcesionariosEstado(Integer numeroConcesionariosEstado) {
        this.numeroConcesionariosEstado = numeroConcesionariosEstado;
    }

    /**
     * Municipio seleccionado en la lista desplegable.
     * @return the municipioSelected
     */
    public String getMunicipioSelected() {
        return municipioSelected;
    }

    /**
     * Municipio seleccionado en la lista desplegable.
     * @param municipioSelected the minicipioSelected to set
     */
    public void setMunicipioSelected(String municipioSelected) {
        this.municipioSelected = municipioSelected;
    }

    /**
     * Lista de municipos por estado.
     * @return the municipiosEstado
     */
    public List<Municipio> getMunicipiosEstado() {
        return municipiosEstado;
    }

    /**
     * Lista de municipos por estado.
     * @param municipiosEstado the municipiosEstado to set
     */
    public void setMunicipiosEstado(List<Municipio> municipiosEstado) {
        this.municipiosEstado = municipiosEstado;
    }

    /**
     * Lista de ABN por estado.
     * @return the abnInEstado
     */
    public List<Abn> getAbnInEstado() {
        return abnInEstado;
    }

    /**
     * Lista de ABN por estado.
     * @param abnInEstado the abnInEstado to set
     */
    public void setAbnInEstado(List<Abn> abnInEstado) {
        this.abnInEstado = abnInEstado;
    }

    /**
     * Número de ABN por estado.
     * @return the numeroAbnEstado
     */
    public Integer getNumeroAbnEstado() {
        return numeroAbnEstado;
    }

    /**
     * Número de ABN por estado.
     * @param numeroAbnEstado the numeroAbnEstado to set
     */
    public void setNumeroAbnEstado(Integer numeroAbnEstado) {
        this.numeroAbnEstado = numeroAbnEstado;
    }

    /**
     * Código Abn.
     * @return the numeroAbn
     */
    public BigDecimal getNumeroAbn() {
        return numeroAbn;
    }

    /**
     * Código Abn.
     * @param numeroAbn the numeroAbn to set
     */
    public void setNumeroAbn(BigDecimal numeroAbn) {
        this.numeroAbn = numeroAbn;
    }

    /**
     * Lista de Nirs de un Abn.
     * @return the listaNirAbn
     */
    public List<Nir> getListaNirAbn() {
        return listaNirAbn;
    }

    /**
     * Lista de Nirs de un Abn.
     * @param listaNirAbn the listaNirAbn to set
     */
    public void setListaNirAbn(List<Nir> listaNirAbn) {
        this.listaNirAbn = listaNirAbn;
    }

    /**
     * Flag para el componente de lista desplegable de NIR/Abn.
     * @return the listNirActivated
     */
    public boolean isListNirActivated() {
        return listNirActivated;
    }

    /**
     * Flag para el componente de lista desplegable de NIR/Abn.
     * @param listNirActivated the listNirActivated to set
     */
    public void setListNirActivated(boolean listNirActivated) {
        this.listNirActivated = listNirActivated;
    }

    /**
     * Flag para el componente de la lista desplegable de Nir/Municipio.
     * @return the listNirMunicipioActivated
     */
    public boolean isListNirMunicipioActivated() {
        return listNirMunicipioActivated;
    }

    /**
     * Flag para el componente de la lista desplegable de Nir/Municipio.
     * @param listNirMunicipioActivated the listNirMunicipioActivated to set
     */
    public void setListNirMunicipioActivated(boolean listNirMunicipioActivated) {
        this.listNirMunicipioActivated = listNirMunicipioActivated;
    }

    /**
     * Lista con los Abn de un municipio.
     * @return the listAbnMunicipio
     */
    public List<Abn> getListAbnMunicipio() {
        return listAbnMunicipio;
    }

    /**
     * Lista con los Abn de un municipio.
     * @param listAbnMunicipio the listAbnMunicipio to set
     */
    public void setListAbnMunicipio(List<Abn> listAbnMunicipio) {
        this.listAbnMunicipio = listAbnMunicipio;
    }

    /**
     * Lista con los Nir de un municipio.
     * @return the listNirMunicipio
     */
    public List<Nir> getListNirMunicipio() {
        return listNirMunicipio;
    }

    /**
     * Lista con los Nir de un municipio.
     * @param listNirMunicipio the listNirMunicipio to set
     */
    public void setListNirMunicipio(List<Nir> listNirMunicipio) {
        this.listNirMunicipio = listNirMunicipio;
    }

    /**
     * Lista de proveedores de un municipio.
     * @return the proveedorMunicipio
     */
    public List<Proveedor> getProveedorMunicipio() {
        return proveedorMunicipio;
    }

    /**
     * Lista de proveedores de un municipio.
     * @param proveedorMunicipio the proveedorMunicipio to set
     */
    public void setProveedorMunicipio(List<Proveedor> proveedorMunicipio) {
        this.proveedorMunicipio = proveedorMunicipio;
    }

    /**
     * Numeración total asignada a un muninicipio.
     * @return the numeracionAsignadaMunicipio
     */
    public Integer getNumeracionAsignadaMunicipio() {
        return numeracionAsignadaMunicipio;
    }

    /**
     * Numeración total asignada a un muninicipio.
     * @param numeracionAsignadaMunicipio the numeracionAsignadaMunicipio to set
     */
    public void setNumeracionAsignadaMunicipio(Integer numeracionAsignadaMunicipio) {
        this.numeracionAsignadaMunicipio = numeracionAsignadaMunicipio;
    }

    /**
     * Lista de NIR por estado.
     * @return the nirsEstado
     */
    public List<Nir> getNirsEstado() {
        return nirsEstado;
    }

    /**
     * Lista de NIR por estado.
     * @param nirsEstado the nirsEstado to set
     */
    public void setNirsEstado(List<Nir> nirsEstado) {
        this.nirsEstado = nirsEstado;
    }

    /** Refactorización FJAH 17.06.2025
     *
     */
    public void abrirDialogoConProveedoresZona() {
        LOGGER.debug("== Ejecutando abrirDialogoConProveedoresZona ==");
        if (dialogInfoBean != null) {
            LOGGER.debug("dialogInfoBean OK, enviando lista");
            dialogInfoBean.setAndActivatedProveedorEstado(this.concesionariosZona);
            dialogInfoBean.setTablaProvEstadoActivated(true);
            dialogInfoBean.setTablaProvAbnActivated(false); // por si acaso
            dialogInfoBean.setTableMunicipioActivated(false); // por si acaso
            LOGGER.debug("   → Modal preparado con " + this.concesionariosZona.size() + " elementos");
        } else {LOGGER.warn("dialogInfoBean es NULL");}
    }

    /**
     * FJAH 27.06.2025
     * Descarga de archivo Excel de municipios por zona
     */
    public StreamedContent getArchivoMunicipios() {
        try {
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet("Municipios");

            // Encabezados
            HSSFRow header = sheet.createRow(0);
            header.createCell(0).setCellValue("Estado");
            header.createCell(1).setCellValue("Municipio");
            header.createCell(2).setCellValue("Clave INEGI");

            int rowIndex = 1;

            for (Municipio m : municipiosZonaTmp) {
                HSSFRow row = sheet.createRow(rowIndex++);

                // Estado (usamos el código del estado)
                row.createCell(0).setCellValue(m.getId().getCodEstado());

                // Nombre del municipio
                row.createCell(1).setCellValue(m.getNombre());

                // Clave INEGI de 5 dígitos
                row.createCell(2).setCellValue(m.getClaveInegi5());
            }

            // Convertir a stream
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            workbook.close();

            return new DefaultStreamedContent(
                    new ByteArrayInputStream(out.toByteArray()),
                    "application/vnd.ms-excel",
                    "municipios_zona_" + region.getIdRegion() + ".xls"
            );

        } catch (Exception e) {
            LOGGER.error("Error generando archivo Excel de municipios: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * FJAH 26.06.2025
     * Busqueda a traves del selector de maá de zonas.
     */
    public void searchZonaMap(String idZona) {
        try {
            LOGGER.debug("Zona seleccionada: {}", idZona);
            limpiarDatosZona();

            if (idZona != null && idZona.startsWith("ZONA_")) {
                // Extraer el número y convertirlo a Integer
                String zonaStr = idZona.replace("ZONA_", "");
                int idRegion = Integer.parseInt(zonaStr);

                // Buscar región en listaRegiones
                for (Region reg : listaRegiones) {
                    if (reg.getIdRegion().intValue() == idRegion) {
                        this.region = reg;
                        this.idRegionSelected = reg.getIdRegion();
                        break;
                    }
                }

                if (region != null) {
                    onChangeRegion(); // Reutiliza lógica de carga de datos
                } else {
                    LOGGER.error("No se encontró la región con ID {}", idRegion);
                    MensajesFrontBean.addErrorMsg(MSG_ID, "Zona no encontrada.");
                }
            }

        } catch (Exception e) {
            LOGGER.error("Error inesperado al seleccionar zona desde el mapa: " + e.getMessage(), e);
            MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado al cargar zona.");
        }
    }


}
