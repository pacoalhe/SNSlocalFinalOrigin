package mx.ift.sns.web.frontend.consultas;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.*;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.persistence.NoResultException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.ng.RangoSerie;
import mx.ift.sns.modelo.ot.Estado;
import mx.ift.sns.modelo.ot.EstadoNumeracion;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.MunicipioPK;
import mx.ift.sns.modelo.ot.NirNumeracion;
import mx.ift.sns.modelo.ot.Poblacion;
import mx.ift.sns.modelo.ot.PoblacionNumeracion;
import mx.ift.sns.modelo.pnn.PlanMaestroDetalle;
import mx.ift.sns.modelo.port.NumeroPortado;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.pst.TipoRed;
import mx.ift.sns.modelo.series.Nir;
import mx.ift.sns.negocio.IConfiguracionFacade;
import mx.ift.sns.negocio.IConsultaPublicaFacade;
import mx.ift.sns.negocio.num.model.Numero;
import mx.ift.sns.negocio.pnn.IPlanMaestroService;
import mx.ift.sns.web.frontend.common.MensajesFrontBean;
import mx.ift.sns.web.frontend.util.PaginatorUtil;
import mx.ift.sns.negocio.pnn.IPlanNumeracionService;

/** Bean que controla el formulario de consultas publicas. */
@ManagedBean(name = "consultaPublicaMarcacionBean")
@ViewScoped
public class ConsultaPublicaMarcacionBean implements Serializable {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsultaPublicaMarcacionBean.class);

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador del componente de mensajes JSF. */
    private static final String MSG_ID = "MSG_ConsultaFront";

    /** Identificador del tipo de numeración a consultar. */
    private static final String NUMERACION_GEOGRAFICA = "NG";

    /**
     * Action Reverse for ported numbers this constant is saved in cat_parametro
     * table
     */
    private static final String ACTION_REVERSE = "port.action.reverse";

    /** Bean detallePoblacionBean. */
    @ManagedProperty(value = "#{detallePoblacionBean}")
    private DetallePoblacionBean detallePoblacionBean;

    /** Servicio de Busqueda Publica. */
    @EJB(mappedName = "ConsultaPublicaFacade")
    private IConsultaPublicaFacade ngPublicService;

    /** Servicio de configuración Publica. */
    @EJB(mappedName = "ConfiguracionFacade")
    private IConfiguracionFacade ngConfiguracionService;

    @EJB(mappedName = "PlanMaestroService")
    private IPlanMaestroService planService;

	/** FJAH */
	@EJB(mappedName = "PlanNumeracionService")
	private IPlanNumeracionService planNumeracionService;

    /** Campo poblacion buscada. */
    private Poblacion poblacion;

    /** Campo poblacion de un numero consultado. */
    private Poblacion poblacionNumero;

    /** Lista de prestadores de servicio de una poblacion. */
    private List<Proveedor> prestadoresServicioPoblacionNumero;

    /** Lista de poblaciones. */
    private List<Poblacion> poblaciones;

    /** Campo numero nacional. */
    private String nationalNumber = "";

    /** Campo numero local. */
    private String localNumber = "";

    /** Campo nir. */
    private String codigoNir = "";

    /** Flag comprobacion input poblacion activado. */
    private boolean populationInputActivated = true;

    /** Flag comprobacion input numero nacional activado. */
    private boolean nationalNumberInputActivated = true;

    /** Flag comprobacion input numero local activado. */
    private boolean localNumberInputActivated = true;

    /** Flag comprobacion input nir local activado. */
    private boolean nirInputActivated = true;

    /** Flag comprobacion boton formulario activado. */
    private boolean formButtonActivated = false;

    /** Flag para renderizado de tabla poblacion. */
    private boolean populationsTableActivated = false;

    /** Flag para renderizado de tabla servicio local. */
    private boolean localServiceTableActivated = false;

    /** Flag para renderizado de informacion del numero. */
    private boolean numberInfoTableActivated = false;

    /** Flag para renderizado de tabla de informacion de mexico. */
    private boolean mexicoInfoTableActivated = false;

    /** Flag para renderizado de combo nirs numero local. */
    private boolean comboNumeroLocalNirsActivated = false;

    /** Abn. */
    private Abn abn;

    /** Numero consultado. **/
    private Numero numeroConsultado;

    /** Prestador del servicio del numero consultado. **/
    private Proveedor prestadorNumero;

    /** Estado. */
    private Estado estado;

    /** Lista de municipios del abn. */
    private List<Municipio> municipiosNir;

    /** Poblacion con mayor numeracion asignada. */
    private Poblacion poblacionAbnMaxNumAsignada;

    /** Lista de nirs de un numero local. */
    private List<Nir> nirsNumeroLocal;

    /** Nir numero local seleccionado. */
    private String nirNumeroLocalSelected;

    /** Lista de poblaciones y numeracion de un proveedor de servicio. */
    private List<PoblacionNumeracion> poblacionesNumeracionProveedor;

    /** Lista de poblaciones y numeracion de un proveedor de servicio. */
    private List<EstadoNumeracion> estadosNumeracionProveedor;

    /** Lista de nirs y numeracion de un proveedor de servicio. */
    private List<NirNumeracion> nirsNumeracionProveedor;

    /**
     * Flag para renderizado de tabla poblaciones donde el proveedor tiene
     * numeración.
     */
    private boolean poblacionesProveedorTabla = false;

    /**
     * Flag para renderizado de tabla estados donde el proveedor tiene numeración.
     */
    private boolean estadosProveedorTabla = false;

    /** Flag para renderizado de tabla nirs donde el proveedor tiene numeración. */
    private boolean nirsProveedorTabla = false;

    /** Muestra la tabla de poblaciones . */
    private boolean paginatedResultsPoblacionesActive;

    /** Muestra la tabla de municipios . */
    private boolean paginatedResultsMunicipiosActive;

    /** Muestra la tabla de concesionarios . */
    private boolean paginatedResultsConcesionariosActive;

    /** Numeración asignada por población en formato String. */
    private String numeracionAsignadaPobFormat;

    /** Numeración asignada por proveedor. */
    private Integer numeracionAsignadaProveedor;

    /** Presusbscripcion. */
    private String presuscripcion;

    /** PoblacionNumeracion por proveedor y estado. */
    private List<PoblacionNumeracion> poblacionNumeracionProveedorEstado;

    /** Estado con numeración asignada de un proveedor. */
    private Estado estadoProveedor;

    /** Nirs por poblacion. */
    private List<Nir> nirsPoblacion;

    /** Nirs por área de numeracion. */
    private List<Nir> nirsAbn;

    /** Modalidad y tipo de red del numero consultado. */
    private String tipoModalidadRed;

    /** Flag que activa la consulta numérica. */
    private boolean consultaActivada = true;

    /** Nir de búsqueda por nir. */
    private Nir nir;

    /** Municipio seleccionado en el detalle. */
    private Municipio municipio;

    /** Flag de activación tablas info municipio. */
    private boolean municipioInfoTableActivated = false;

    /** Lista de nirs de un municipio. */
    private List<Nir> nirsMunicipio;

    /** Numeración asignada municipio en formato. */
    private String numeracionAsignadaMunicipioFormat;

    /** Proveedores por municipio. */
    private List<Proveedor> proveedoresMunicipio;

    /** Inegi municipio. */
    private String inegiMunicipio;

    /** Número de digitos según longitud nir. */
    private Integer numeroDigitos;

    /** Proveedores Nir. */
    private List<Proveedor> proveedoresNir;

    /** Poblaciones con numeración asignada nir. */
    private List<Poblacion> poblacionesNumeracionAsignadaNir;

    /** Numeración asignada nir en formato USA. */
    private String numeracionNirFormat;

    /** Poblaciones Nir. */
    private List<Poblacion> poblacionesNir;

    /** Flag de activación tabla poblaciones sin nir especificado. */
    private boolean activaTablaPoblacionesNoNir = false;

    /**
     * String con los distintos número de registros por página. Por defecto:
     * 5,10,15,20,15
     */
    private String numeroRegistros = "5, 10, 15, 20, 25";

    /** Obtendrá el valor del nir respecto al número consultado */
    private int nirNumConsultado;

    /** Obtendrá el valor del número sin el nir respecto al número consultado */
    private String numConsultadoSinNir;

	/**
	 * FJAH 30.06.2025
	 * Declaración de variables para la obtención de cantidades
	 */
	private int cantidadMunicipiosZona;
	private int cantidadEmpresasZona;
	private BigDecimal numeracionZona;
	private String zona = "";

	private List<mx.ift.sns.modelo.pst.Proveedor> proveedoresZona = new ArrayList<>();


	// //////////////////////////////POSTCONSTRUCT//////////////////
    /**
     * Postconstructor.
     */
    @PostConstruct
    public void init() {
	this.poblacion = new Poblacion();
	if (LOGGER.isDebugEnabled()) {
	    LOGGER.debug("Postconstruct consulta numeracion: esta activado");
	}
    }

    /**
     * Método que realiza el autocompletado de la lista desplegable en el input de
     * búsqueda por población.
     * 
     * @param query String
     * @return List<String>
     */
    public List<String> completeText(String query) {
	if (LOGGER.isDebugEnabled()) {
	    LOGGER.debug("ngPublicService:" + ngPublicService);
	}
	List<String> poblacionesNameAux = new ArrayList<String>(1);
	try {
	    poblacionesNameAux = ngPublicService.findAllPoblacionesNameLikeNombre(query);
	    Collections.sort(poblacionesNameAux);
	} catch (NoResultException e) {
	    LOGGER.error("Error al buscar todas las poblaciones" + e.getMessage());
	    //MensajesFrontBean.addErrorMsg(MSG_ID, "Error insperado"); //FJAH
	} catch (Exception e) {
	    LOGGER.error("Error al buscar todas las poblaciones" + e.getMessage());
	    //MensajesFrontBean.addErrorMsg(MSG_ID, "Error insperado"); //FJAH
	}
	return poblacionesNameAux;
    }

    /**
     * Metodo que resetea el formulario y activa los inputs.
     */
    public void resetSearch() {
	try {
	    this.setNationalNumberInputActivated(true);
	    this.setLocalNumberInputActivated(true);
	    this.setPopulationInputActivated(true);
	    this.setNirInputActivated(true);
	    this.setFormButtonActivated(false);
	    this.setActivaTablaPoblacionesNoNir(false);
	    this.setPopulationsTableActivated(false);
	    this.setLocalServiceTableActivated(false);
	    this.setNumberInfoTableActivated(false);
	    this.setPopulationsTableActivated(false);
	    this.setMexicoInfoTableActivated(false);
	    this.setComboNumeroLocalNirsActivated(false);
	    this.poblacion.setNombre("");
	    this.setLocalNumber("");
	    this.setNationalNumber("");
	    this.setCodigoNir("");
	    this.setNirNumeroLocalSelected("");
	    this.setMunicipioInfoTableActivated(false);
		this.setZona("");
		this.setNir(null);
	} catch (Exception e) {
	    LOGGER.error("Error al limpiar todo" + e.getMessage());
	    //MensajesFrontBean.addErrorMsg(MSG_ID, "Error insperado"); //FJAH
	}
    }

    /**
     * Comprueba si algun campo esta relleno y deshabilita o habilita inputs y
     * boton.
     */
    public void checkFormStatus() {
	try {
	    if (poblacion != null && poblacion.getNombre() != null) {
		if (StringUtils.isNotEmpty(poblacion.getNombre())) {
		    setNationalNumberInputActivated(false);
		    setLocalNumberInputActivated(false);
		    setNirInputActivated(false);
		    setPopulationInputActivated(true);
		    setFormButtonActivated(true);
		}
	    }
	    if (!getNationalNumber().equals("")) {
		setPopulationInputActivated(false);
		setLocalNumberInputActivated(false);
		setNirInputActivated(false);
		if (getNationalNumber().length() >= 10) {
		    setNationalNumberInputActivated(true);
		    setFormButtonActivated(true);
		} else {
		    setFormButtonActivated(false);
		}
	    }
	    if (!getLocalNumber().equals("")) {
		setNationalNumberInputActivated(false);
		setPopulationInputActivated(false);
		setNirInputActivated(false);
		if (getLocalNumber().length() >= 7) {
		    setLocalNumberInputActivated(true);
		    setFormButtonActivated(true);
		} else {
		    setFormButtonActivated(false);
		}
	    }
	    if (!getCodigoNir().equals("")) {
		setNationalNumberInputActivated(false);
		setLocalNumberInputActivated(false);
		setPopulationInputActivated(false);
		if (getCodigoNir().length() == 1) {
		    setNirInputActivated(true);
		    setFormButtonActivated(true);
		} else {
		    setFormButtonActivated(false);
		}
	    }
	    if ((poblacion != null && poblacion.getNombre() != null && poblacion.getNombre().equals("")
		    || poblacion != null && poblacion.getNombre() == null) && getNationalNumber().equals("")
		    && getLocalNumber().equals("") && getCodigoNir().equals("")) {
		setNationalNumberInputActivated(true);
		setLocalNumberInputActivated(true);
		setPopulationInputActivated(true);
		setNirInputActivated(true);
		setPopulationsTableActivated(false);
		setLocalServiceTableActivated(false);
		setNumberInfoTableActivated(false);
		setPopulationsTableActivated(false);
		setMexicoInfoTableActivated(false);
		this.setActivaTablaPoblacionesNoNir(false);
		setFormButtonActivated(false);
	    }
	} catch (Exception e) {
	    LOGGER.error("Error insesperado" + e.getMessage());
	    //MensajesFrontBean.addErrorMsg(MSG_ID, "Error insperado"); //FJAH
	}
    }

    /** Busca informacion de un numero local despues de seleccionar el nir. */
    public void searchLocalNumberWithNir() {
	try {
	    if (this.nirNumeroLocalSelected != null && this.localNumber != null) {
		String nationalNumberAux = this.nirNumeroLocalSelected + this.localNumber;
		parseAndSearchNationalNumber(nationalNumberAux);
		setFormButtonActivated(false);
		setLocalNumberInputActivated(false);
		if (LOGGER.isDebugEnabled()) {
		    LOGGER.debug("Número Nacional = {}, nir seleccionado = {}", nationalNumberAux,
			    this.nirNumeroLocalSelected);
		}
	    } else {
		if (LOGGER.isDebugEnabled()) {
		    LOGGER.debug("Se ha perdido la información seteada");
		}
	    }
	} catch (Exception e) {
	    LOGGER.error("Error insesperado de carga de datos al buscar por número local" + e.getMessage());
	    //MensajesFrontBean.addErrorMsg(MSG_ID, "Error insperado"); //FJAH
	}
    }

    /**
     * Método que realiza distintos tipos de busqueda dependiendo del input activo.
     */
    public void publicSearch() {
	try {
	    String regex = "[0-9]+";
	    if (this.getLocalNumberInputActivated() == true) {
		this.setFormButtonActivated(false);
		this.setLocalNumberInputActivated(false);
		if (this.localNumber.matches(regex)) {
		    Numero numeroLocal = ngPublicService.parseNumeracion(this.localNumber);
		    if (numeroLocal != null) {
			this.setNirsNumeroLocal(ngPublicService.findNirsNumeroLocal(numeroLocal));
			List<Nir> nirsAux = this.nirsNumeroLocal;
			if (nirsAux.size() > 0) {
			    setComboNumeroLocalNirsActivated(true);
			} else {
			    MensajesFrontBean.addErrorMsg(MSG_ID, "No existen NIR asociados al número introducido");
			}
		    }
		} else {
		    MensajesFrontBean.addErrorMsg(MSG_ID, "Introduzca sólo caracteres del 0 al 9");
		}
	    }
	    if (this.getNationalNumberInputActivated() == true) {
		this.setFormButtonActivated(false);
		this.setNationalNumberInputActivated(false);
		this.setNirInputActivated(false);
		if (this.nationalNumber.matches(regex)) {
		    parseAndSearchNationalNumber(this.nationalNumber);
		} else {
		    MensajesFrontBean.addErrorMsg(MSG_ID, "Introduzca sólo caracteres del 0 al 9");
		}
	    }
		// --------------------------------------
		// FJAH 30.06.2025 - Nuevo flujo por zona
		// Cuando el input activo es por zona (antes NIR),
		// se usa directamente el valor de `zona` en vez de `codigoNir`
		// y se invoca al nuevo método `searchZona()`
		// --------------------------------------
		if (this.getNirInputActivated() == true) {
			String regexZona = "[2-9]+";
			this.setFormButtonActivated(false);
			this.setNirInputActivated(false);

			if(this.codigoNir.matches(regexZona)) {
				this.setZona(this.codigoNir); // usar zona
				this.searchZona();

				// Activamos el panel solo si hubo resultado
				this.setLocalServiceTableActivated(true);
				/*
				if (this.nir != null) {
					this.setLocalServiceTableActivated(true);
				} else {
					this.setLocalServiceTableActivated(false);
					MensajesFrontBean.addErrorMsg(MSG_ID, "Zona no encontrada");
				}

				 */
			} else {
				MensajesFrontBean.addErrorMsg(MSG_ID, "Introduzca sólo caracteres del 2 al 9");
			}
		}
		/*
		LOGGER.debug("== Validando zona: [{}]", this.zona);
		if (this.getNirInputActivated() == true) {
			String regexZona = "[2-9]+";
			this.setFormButtonActivated(false);
			this.setNirInputActivated(false);

			if (this.zona != null && this.zona.matches(regexZona)) {
				this.searchZona();
			} else {
				MensajesFrontBean.addErrorMsg(MSG_ID, "Debe ingresar una zona válida (2 a 9)");
			}
		}
		 */

	    if (this.getPopulationInputActivated() == true) {
		int numeroRegistro = PaginatorUtil.getRegistrosPorPagina(ngConfiguracionService);
		PaginatorUtil.resetPaginacion("FORM_myform:TBL_populationsTable", numeroRegistro);
		this.setNumeroRegistros(PaginatorUtil.getGruposRegistrosPorPagina(numeroRegistro));
		this.setFormButtonActivated(false);
		this.setPopulationInputActivated(false);
		// Se hizo el Split ya que para ubicar la poblacion
		if (this.poblacion.getNombre().split("; ")[0].length() > 4) {
		    Municipio municipio = ngPublicService.findMunicipioByNombreAndEstado(
			    poblacion.getNombre().split("; ")[1], poblacion.getNombre().split("; ")[2]);
		    this.setPoblaciones(ngPublicService.findPoblacionByNombreAndMunicipioAndEstado(
			    poblacion.getNombre().split("; ")[0], municipio));
		} else {
		    MensajesFrontBean.addInfoMsg("MSG_ConsultaFront",
			    "El campo 'consecutivo' ha de tener un formato máximo de 9 dígitos", "");
		}
		this.setPopulationsTableActivated(true);
	    }
	} catch (Exception e) {
	    LOGGER.error("Error insesperado al buscar por alguno de los cuatro inputs" + e.getMessage());
	    //MensajesFrontBean.addErrorMsg(MSG_ID, "Error insperado"); //FJAH
	}
    }

    /**
     * Parsea y busca los datos para un numero nacional de 10 cifras.
     * 
     * @param nationalNumber numero nacional.
     */
    private void parseAndSearchNationalNumber(String nationalNumber) {
	try {
	    this.setNumeroConsultado(ngPublicService.parseNumeracion(nationalNumber));
	    // Buscar por nationalNumber en números portados para comprobar si es portado.
	    // Setear a true si es así.
	    if (this.numeroConsultado != null) {
		if (numeroConsultado.getRango() != null) {
		    RangoSerie rango = numeroConsultado.getRango();
		    this.getTipoRed(rango);
			/**
			 * FJAH 30.06.2025. 10.07.2025
			 * Refactorización
			 */
			this.setNir(ngPublicService.getNirByCodigo(Integer.parseInt(numeroConsultado.getCodigoNir())));
			if (this.nir == null) {
				LOGGER.error("No se pudo obtener el NIR para el código: {}", numeroConsultado.getCodigoNir());
				MensajesFrontBean.addErrorMsg(MSG_ID, "Número no encontrado");
				return;
			}
			/*
		    this.setPoblacionNumero(rango.getPoblacion());
		    this.setPrestadoresServicioPoblacionNumero(
			    ngPublicService.findAllPrestadoresServicioBy(null, null, this.poblacionNumero, null, null));
		    this.setAbn(ngPublicService.getAbnByCodigoNir(numeroConsultado.getCodigoNir()));
		    this.setNirsAbn(this.getAbn().getNirs());
		    this.setNirsPoblacion(ngPublicService.findAllNirByPoblacion(this.poblacionNumero));
		    this.setNir(ngPublicService.getNirByCodigo(Integer.valueOf(numeroConsultado.getCodigoNir())));
		    this.chequeaNumeroDigitos(numeroConsultado.getCodigoNir());
		    if (this.abn.getPresuscripcion() != null) {
			if (this.abn.getPresuscripcion().compareTo("P") == 0) {
			    this.setPresuscripcion("Sí");
			} else {
			    this.setPresuscripcion("No");
			}
		    } else {
			this.setPresuscripcion("No");
		    }
			 */

			/*
		    if (this.getNir() != null && this.getAbn() != null) {
			this.setProveedoresNir(
				ngPublicService.findAllPrestadoresServicioBy(this.getNir(), null, null, null, null));
			this.setPoblacionesNir(
				ngPublicService.findALLPoblacionesNumeracionAsignadaByNir(this.getNir()));
			this.setMunicipiosNir(ngPublicService.findAllMunicipiosByNir(this.getNir(), true));
			BigDecimal bg = new BigDecimal(ngPublicService.findNumeracionesAsignadasNir(this.getNir()));
			this.setNumeracionNirFormat(this.formatoNumeracionAsignada(bg));
			this.setNumeracionAsignadaPobFormat((this.formatoNumeracionAsignada(ngPublicService
				.getTotalNumRangosAsignadosByPoblacion("", "", null, this.poblacionNumero))));
			this.setPoblacionAbnMaxNumAsignada(
				ngPublicService.getPoblacionWithMaxNumAsignadaByAbn(this.getAbn()));
		    }
			 */
			// ========== INICIO BLOQUE DE REEMPLAZO ==========
			Integer zonaInt = this.nir.getZona(); // zona real desde el NIR
			// Municipios en duro
			/*
			switch (zonaInt) {
				case 2: this.setCantidadMunicipiosZona(444); break;
				case 3: this.setCantidadMunicipiosZona(172); break;
				case 4: this.setCantidadMunicipiosZona(280); break;
				case 5: this.setCantidadMunicipiosZona(78); break;
				case 6: this.setCantidadMunicipiosZona(204); break;
				case 7: this.setCantidadMunicipiosZona(358); break;
				case 8: this.setCantidadMunicipiosZona(140); break;
				case 9: this.setCantidadMunicipiosZona(800); break;
				default: this.setCantidadMunicipiosZona(0); break;
			}
			 */

			// === LÓGICA REAL ACTIVADA ===
			Set<Municipio> municipiosZonaTmp = new HashSet<>();
			List<Municipio> municipiosZonaRaw = ngPublicService.findMunicipiosByZona(zonaInt);

			if (municipiosZonaRaw != null) {
				municipiosZonaTmp.clear();
				municipiosZonaTmp.addAll(municipiosZonaRaw);
			}

			// Asignamos a atributos y calculamos total
			this.setCantidadMunicipiosZona(municipiosZonaTmp.size());

			if (this.cantidadMunicipiosZona == 0) {
				LOGGER.warn("No se encontraron municipios para la zona " + zonaInt);
			}


			// Carga de proveedores por zona
			List<Proveedor> proveedoresZona = new ArrayList<>();
			try {
				List<?> resultadoGenerico = ngPublicService.findAllPrestadoresServicioByZona(zonaInt);
				List<Proveedor> listaFinal = new ArrayList<>();

				if (resultadoGenerico != null) {
					for (Object obj : resultadoGenerico) {
						if (obj instanceof Proveedor) {
							listaFinal.add((Proveedor) obj);
						}
					}
				}
				this.setProveedoresZona(listaFinal);
				this.setCantidadEmpresasZona(listaFinal.size());
			} catch (Exception e) {
				LOGGER.error("Error al obtener Proveedores de la zona " + zonaInt + ": " + e.getMessage(), e);
				this.setProveedoresZona(new ArrayList<Proveedor>());
				this.setCantidadEmpresasZona(0);
			}
			// Numeración asignada desde BD
			Integer totalNumeracion = ngPublicService.getTotalNumeracionAsignadaPorZona(zonaInt);
			this.setNumeracionZona(new BigDecimal(totalNumeracion != null ? totalNumeracion : 0));

			// Setear para compatibilidad con la IG
			this.setProveedoresNir(this.proveedoresZona);
			this.setMunicipiosNir(mockMunicipios(this.cantidadMunicipiosZona));
			this.setNumeracionNirFormat(this.numeracionZona.toPlainString());
			// ========== FIN BLOQUE DE REEMPLAZO ==========

		    // Aqui se llama método para quitar el NIR al número consultado
		    getNumSinNir(this.numeroConsultado.getNumero());
		    this.findProveedorPrestador(rango, nationalNumber);
		    this.setLocalServiceTableActivated(true);
		    this.setMexicoInfoTableActivated(true);
		    this.setNumberInfoTableActivated(true);
		    this.setActivaTablaPoblacionesNoNir(false);
		} else {
		    MensajesFrontBean.addErrorMsg(MSG_ID, "Número no encontrado");
		    this.setPopulationsTableActivated(false);
		    this.setLocalServiceTableActivated(false);
		    this.setNumberInfoTableActivated(false);
		    this.setPopulationsTableActivated(false);
		    this.setMexicoInfoTableActivated(false);
		    this.setComboNumeroLocalNirsActivated(false);
		    this.setActivaTablaPoblacionesNoNir(false);
		}
	    }
	} catch (Exception e) {
	    LOGGER.error("Error insesperado en el parseo del numero nacional o local" + e.getMessage());
	    MensajesFrontBean.addErrorMsg(MSG_ID, "Numero no encontrado");
	    this.setPopulationsTableActivated(false);
	    this.setLocalServiceTableActivated(false);
	    this.setNumberInfoTableActivated(false);
	    this.setPopulationsTableActivated(false);
	    this.setMexicoInfoTableActivated(false);
	    this.setComboNumeroLocalNirsActivated(false);
	    this.setActivaTablaPoblacionesNoNir(false);
	}
    }

	private List<Municipio> mockMunicipios(int cantidad) {
		List<Municipio> municipios = new ArrayList<>();
		for (int i = 0; i < cantidad; i++) {
			Municipio m = new Municipio();
			m.setNombre("Municipio " + (i + 1));
			municipios.add(m);
		}
		return municipios;
	}

	/**
     * Chequea si existe el código NIR introducido y setea los datos
     * correspondientes relacionados.
     */
    private void searchNir() {
	try {
	    if (ngPublicService.existsNir(this.codigoNir)) {
		this.setNir(ngPublicService.getNirByCodigo(Integer.valueOf(this.codigoNir)));
		this.setAbn(ngPublicService.getAbnByCodigoNir(this.codigoNir));
		this.setNirsAbn(ngPublicService.findAllNirByAbn(this.abn.getCodigoAbn()));
		this.chequeaNumeroDigitos(this.codigoNir);
		if (this.abn.getPresuscripcion() != null) {
		    if (this.abn.getPresuscripcion().compareTo("P") == 0) {
			this.setPresuscripcion("Sí");
		    } else {
			this.setPresuscripcion("No");
		    }
		} else {
		    this.setPresuscripcion("No");
		}
		if (this.getNir() != null && this.getAbn() != null) {
		    this.setProveedoresNir(
			    ngPublicService.findAllPrestadoresServicioBy(this.getNir(), null, null, null, null));
		    this.setPoblacionesNir(ngPublicService.findALLPoblacionesNumeracionAsignadaByNir(this.getNir()));
		    this.setMunicipiosNir(ngPublicService.findAllMunicipiosByNir(this.getNir(), true));
		    BigDecimal bg = new BigDecimal(ngPublicService.findNumeracionesAsignadasNir(this.getNir()));
		    this.setNumeracionNirFormat(this.formatoNumeracionAsignada(bg));
		    this.setPoblacionAbnMaxNumAsignada(
			    ngPublicService.getPoblacionWithMaxNumAsignadaByAbn(this.getAbn()));
		}
		this.setLocalServiceTableActivated(true);
		this.setFormButtonActivated(false);
		this.setNirInputActivated(false);
	    } else {
		MensajesFrontBean.addErrorMsg(MSG_ID, "El número introducido no se corresponde con ningún NIR");
	    }
	    this.setActivaTablaPoblacionesNoNir(false);
	} catch (Exception e) {
	    LOGGER.error("Error insesperado en la carga de datos al buscar por nir" + e.getMessage());
	    MensajesFrontBean.addErrorMsg(MSG_ID, "Error en la búsqueda del nir");
	}
    }

	/**
	 * FJAH 30.06.2025
	 * Refactorización método zona (municipios en duro, proveedores y numeración reales)
	 */
	public void searchZona() {
		try {
			LOGGER.debug(">> Buscando datos por zona [{}]", this.zona);

			if (this.zona == null || this.zona.trim().isEmpty()) {
				MensajesFrontBean.addErrorMsg(MSG_ID, "Debe ingresar una zona válida");
				return;
			}

			Integer zonaInt = Integer.parseInt(this.zona);

			// ----------------------------
			// Municipios en duro
			/*
			switch (zonaInt) {
				case 2: this.setCantidadMunicipiosZona(444); break;
				case 3: this.setCantidadMunicipiosZona(172); break;
				case 4: this.setCantidadMunicipiosZona(280); break;
				case 5: this.setCantidadMunicipiosZona(78); break;
				case 6: this.setCantidadMunicipiosZona(204); break;
				case 7: this.setCantidadMunicipiosZona(358); break;
				case 8: this.setCantidadMunicipiosZona(140); break;
				case 9: this.setCantidadMunicipiosZona(800); break;
				default: this.setCantidadMunicipiosZona(0); break;
			}
			 */


			// === LÓGICA REAL ACTIVADA ===
			Set<Municipio> municipiosZonaTmp = new HashSet<>();
			List<Municipio> municipiosZonaRaw = ngPublicService.findMunicipiosByZona(zonaInt);

			if (municipiosZonaRaw != null) {
				municipiosZonaTmp.clear();
				municipiosZonaTmp.addAll(municipiosZonaRaw);
			}

			// Asignamos a atributos y calculamos total
			this.setCantidadMunicipiosZona(municipiosZonaTmp.size());

			if (this.cantidadMunicipiosZona == 0) {
				LOGGER.warn("No se encontraron municipios para la zona " + zonaInt);
			}

			// ----------------------------
			// Proveedores desde BD
			// Carga de Proveedores por zona
			// Carga de Proveedores por zona
			List<Proveedor> proveedoresZona = new ArrayList<>();
			try {
				LOGGER.debug("== Carga de Proveedores ==");
				List<?> resultadoGenerico = ngPublicService.findAllPrestadoresServicioByZona(zonaInt);
				List<Proveedor> listaFinal = new ArrayList<>();

				if (resultadoGenerico != null) {
					for (Object obj : resultadoGenerico) {
						if (obj instanceof Proveedor) {
							listaFinal.add((Proveedor) obj);
						}
					}
				}




				this.setProveedoresZona(listaFinal);
				this.setCantidadEmpresasZona(listaFinal.size());

			} catch (Exception e) {
				LOGGER.error("Error al obtener Proveedores de la zona " + zonaInt + ": " + e.getMessage(), e);
				this.setProveedoresZona(new ArrayList<Proveedor>());
				this.setCantidadEmpresasZona(0);
			}

			// ----------------------------
			// Numeración asignada desde BD
			Integer totalNumeracion = ngPublicService.getTotalNumeracionAsignadaPorZona(zonaInt);
			this.setNumeracionZona(new BigDecimal(totalNumeracion != null ? totalNumeracion : 0));

			// ----------------------------
			// Activar panel IG (nuevo)
			this.setLocalServiceTableActivated(true);

			LOGGER.debug(">> Zona [{}]: municipios={}, empresas={}, numeracion={}",
					zonaInt, this.cantidadMunicipiosZona, this.cantidadEmpresasZona, this.numeracionZona);

			// Mapear variables reales del xhtml
			List<Municipio> municipiosMock = new ArrayList<>();
			for (int i = 0; i < this.cantidadMunicipiosZona; i++) {
				Municipio m = new Municipio();
				m.setNombre("Municipio " + (i + 1));
				municipiosMock.add(m);
			}
			this.setMunicipiosNir(municipiosMock); // ahora sí mostrará el .size()

			this.setProveedoresNir(this.proveedoresZona); // lo que ya cargaste de BD

			this.setNumeracionNirFormat(this.numeracionZona.toPlainString()); // formato esperado por el xhtml

		} catch (Exception e) {
			LOGGER.error("Error al buscar por zona: {}", e.getMessage(), e);
			MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado al buscar por zona");
			this.setActivaTablaPoblacionesNoNir(false);
		}
	}

	/**
	 * FJAH 01.07.2025
	 */
	public String getNumeracionZonaFormato() {
		if (numeracionZona != null) {
			NumberFormat nf = NumberFormat.getInstance(new Locale("es", "MX"));
			nf.setGroupingUsed(true);
			nf.setMaximumFractionDigits(0);
			return nf.format(numeracionZona);
		} else {
			return "0";
		}
	}

	public void cargarProveedoresZonaParaDialogo() {
		try {
			LOGGER.debug("== Carga rápida de Proveedores desde el .xhtml ==");

			if (this.zona == null || this.zona.trim().isEmpty()) {
				LOGGER.warn("Zona vacía, no se cargan proveedores.");
				this.proveedoresZona = new ArrayList<>();
				this.cantidadEmpresasZona = 0;
				return;
			}

			Integer zonaInt = Integer.parseInt(this.zona);
			List<?> resultadoGenerico = ngPublicService.findAllPrestadoresServicioByZona(zonaInt);
			List<Proveedor> listaFinal = new ArrayList<>();

			if (resultadoGenerico != null) {
				for (Object obj : resultadoGenerico) {
					if (obj instanceof Proveedor) {
						listaFinal.add((Proveedor) obj);
					}
				}
			}

			this.proveedoresZona = listaFinal;
			this.cantidadEmpresasZona = listaFinal.size();

		} catch (Exception e) {
			LOGGER.error("Error al cargar proveedores desde diálogo: " + e.getMessage(), e);
			this.proveedoresZona = new ArrayList<>();
			this.cantidadEmpresasZona = 0;
		}
	}

	/**
     * Setea el nir y busca los datos relacionados.
     * 
     * @param nir String
     */
    public void setAndSearchNir(String nir) {
	try {
	    if (ngPublicService.existsNir(nir)) {
		if (LOGGER.isDebugEnabled()) {
		    LOGGER.debug("El NIR {} ha sido encontrado", nir);
		}
		setAbn(ngPublicService.getAbnByCodigoNir(nir));
		this.setNir(ngPublicService.getNirByCodigo(Integer.valueOf(nir)));
		this.setProveedoresNir(ngPublicService.findAllPrestadoresServicioBy(this.nir, null, null, null, null));
		this.setCodigoNir(nir);
		this.chequeaNumeroDigitos(nir);
		if (this.abn.getPresuscripcion() != null) {
		    if (this.abn.getPresuscripcion().compareTo("P") == 0) {
			this.setPresuscripcion("Sí");
		    } else {
			this.setPresuscripcion("No");
		    }
		} else {
		    this.setPresuscripcion("No");
		}
		this.setPoblacionesNumeracionAsignadaNir(
			ngPublicService.findALLPoblacionesNumeracionAsignadaByNir(this.getNir()));
		this.setMunicipiosNir(ngPublicService.findAllMunicipiosByNir(this.getNir(), true));
		BigDecimal bg = new BigDecimal(ngPublicService.findNumeracionesAsignadasNir(this.getNir()));
		this.setNumeracionNirFormat(this.formatoNumeracionAsignada(bg));
		this.setPoblacionAbnMaxNumAsignada(ngPublicService.getPoblacionWithMaxNumAsignadaByAbn(this.getAbn()));
	    } else {
		if (LOGGER.isDebugEnabled()) {
		    LOGGER.debug("El NIR {} no corresponde a ninguno existente", this.codigoNir);
		}
		MensajesFrontBean.addErrorMsg(MSG_ID, "El número introducido no se corresponde con ningún NIR");
	    }
	} catch (Exception e) {
	    LOGGER.error("Error insesperado en la carga de datos" + e.getMessage());
	    MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado");
	}
    }

	/**
	 * FJAH 16042025-05052025
	 * Refactorización del proveedor
	 * Busca el proveedor a partir de un rangoSerie.
	 *
	 * @param rango            RangoSerie
	 * @param numeroConsultado Strings
	 */
	private void findProveedorPrestador(RangoSerie rango, String numeroConsultado) {
		Boolean numeroPortadoBool = false;
		try {
			//LOGGER.info("===> findProveedorPrestador ejecutado para número " + numeroConsultado);

			NumeroPortado numeroPortado = ngPublicService.findNumeroPortado(numeroConsultado);
			String action = ngPublicService.getActionPorted(ACTION_REVERSE);

			//LOGGER.info("Se realiza la busqueda del numero en el plan maestro ******");

			//LOGGER.info("===> Resultado findNumeroPortado: " + (numeroPortado != null ? "Sí" : "No"));
			if (numeroPortado != null) {
				LOGGER.info("    RIDA: " + numeroPortado.getRida() + ", DIDA: " + numeroPortado.getDida() + ", ACTION: " + numeroPortado.getAction());
			}

			PlanMaestroDetalle planEncontrado = planNumeracionService.getDetalleNumeroConsultaPublica(
					Long.valueOf(numeroConsultado), Long.valueOf(numeroConsultado));
			//LOGGER.info("===> Resultado planService.getPlanMaestroDetalle: " + (planEncontrado != null ? "Sí" : "No"));

			boolean numIni = false;
			boolean numFin = false;

			Nir nir = ngPublicService.getNirById(rango.getId().getIdNir());

			if (nir != null && rango != null && rango.getId() != null) {
				int codigo = nir.getCodigo();
				BigDecimal sna = rango.getId().getSna();
				String numInicio = rango.getNumInicio();
				String numFinal = rango.getNumFinal();

				if (sna != null && numInicio != null && numFinal != null) {
					Long numeroIni = Long.valueOf(codigo + sna.toString() + numInicio);
					Long numeroFinal = Long.valueOf(codigo + sna.toString() + numFinal);

					Long numeroLong = Long.valueOf(numeroConsultado != null ? numeroConsultado : "0");
					numIni = numeroIni <= numeroLong;
					numFin = numeroFinal >= numeroLong;
				}
			}

			//LOGGER.info("===> Dentro de rango? " + (numIni && numFin));

			if (numeroPortado != null || (planEncontrado != null && numIni && numFin)) {
				List<Proveedor> pstIDOListAux = new ArrayList<>();
				String actionPorted = "";

				if (numeroPortado != null) {
					numeroPortadoBool = true;
					actionPorted = numeroPortado.getAction();

					//LOGGER.info("===> Buscando PST por RIDA de Portados...");
					pstIDOListAux = ngPublicService.getProveedorByIDO(numeroPortado.getRida());

					if (pstIDOListAux.isEmpty()) {
						if ("REVERSE".equalsIgnoreCase(actionPorted)) {
							//LOGGER.info("===> Acción REVERSE detectada. Intentando búsqueda por IDA en IDO...");
							pstIDOListAux = ngPublicService.getProveedorByIDO(numeroPortado.getDida());

							if (!pstIDOListAux.isEmpty()) {
								LOGGER.info("===> Proveedor encontrado por IDA como IDO en acción REVERSE.");
							}
						}

						if (pstIDOListAux.isEmpty()) {
							//LOGGER.info("No se encontró por IDO. Buscando por IDA: {}", numeroPortado.getRida());
							pstIDOListAux = ngPublicService.getProveedorByIDA(numeroPortado.getRida());

							if (pstIDOListAux.isEmpty()) {
								//LOGGER.info("No se encontró por RIDA. Buscando por DIDA como IDA: {}", numeroPortado.getDida());
								pstIDOListAux = ngPublicService.getProveedorByIDA(numeroPortado.getDida());

								if (pstIDOListAux.isEmpty()) {
									//LOGGER.info("No se encontró por DIDA en IDA. Buscando por DIDA como IDO: {}", numeroPortado.getDida());
									pstIDOListAux = ngPublicService.getProveedorByIDO(numeroPortado.getDida());
								}
							}
						}
					}
				} else if (planEncontrado != null && !numeroPortadoBool) {
					pstIDOListAux = ngPublicService.getProveedorByIDA(BigDecimal.valueOf(planEncontrado.getIda()));

					if (pstIDOListAux.isEmpty()) {
						pstIDOListAux = ngPublicService.getProveedorByIDO(BigDecimal.valueOf(planEncontrado.getIda()));

						if (pstIDOListAux.isEmpty()) {
							pstIDOListAux = ngPublicService.getProveedorByIDO(BigDecimal.valueOf(planEncontrado.getIdo()));
						}
					}
				}

				if (!pstIDOListAux.isEmpty()) {
					for (Proveedor pst : pstIDOListAux) {
						if (pstIDOListAux.size() == 1 || (pst.getConsultaPublicaSns() != null
								&& pst.getConsultaPublicaSns().equals(NUMERACION_GEOGRAFICA))) {
							this.setPrestadorNumero(pst);
						}
					}
				} else {
					this.setPrestadorNumero(null);
					LOGGER.info("===> No se encontró proveedor actual en CAT_PST para RIDA/DIDA. Se mantiene valor anterior o se deja en null.");
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_WARN,
									"Proveedor no encontrado",
									"Proveedor actual no registrado en el catálogo"));
				}

			} else {
				Proveedor proveedorAux = rango.getArrendatario();
				if (proveedorAux == null) {
					proveedorAux = rango.getAsignatario();
				}
				this.setPrestadorNumero(proveedorAux);
			}

			if (this.prestadorNumero != null) {
				this.setNumeracionAsignadaProveedor(ngPublicService.getTotalNumeracionAginadaProveedor(this.prestadorNumero.getId()));
			}

		} catch (Exception e) {
			LOGGER.info("===> [ERROR] Excepción en findProveedorPrestador: " + e.getMessage());
			LOGGER.info("Error inesperado en la carga de datos" + e.getMessage());
		}
	}

	/*
    private void findProveedorPrestador(RangoSerie rango, String numeroConsultado) {
	Boolean numeroPortadoBool = false;
	try {
	    // Si existe numero portado, buscamos el pst por IDO, si no devuelve nada
	    // buscamos por IDA.
	    NumeroPortado numeroPortado = ngPublicService.findNumeroPortado(numeroConsultado);
	    // Obtenemos el valor para port.action.reverse desde la tb cat_parametro
	    String action = ngPublicService.getActionPorted(ACTION_REVERSE);

	    LOGGER.info("Se realiza la busqueda del numero en el plan maestro ******");
	    PlanMaestroDetalle planEncontrado = planService.getPlanMaestroDetalle(Long.valueOf(numeroConsultado),
		    Long.valueOf(numeroConsultado));

	    LOGGER.info("Se obtiene el resultado de la busqueda del numero en el plan maestro "
		    + planEncontrado.toString() != null ? planEncontrado.toString() : "");

	    Nir nirNumero = ngPublicService.getNirById(rango.getId().getIdNir());
	    Long numeroIni = Long
		    .valueOf(nirNumero.getCodigo() + rango.getId().getSna().toString() + rango.getNumInicio());
	    Long numeroFinal = Long
		    .valueOf(nirNumero.getCodigo() + rango.getId().getSna().toString() + rango.getNumFinal());

	    boolean numIni = numeroIni <= Long.valueOf(numeroConsultado != null ? numeroConsultado : "0");
	    boolean numFin = numeroFinal >= Long.valueOf(numeroConsultado != null ? numeroConsultado : "0");

	    if (numeroPortado != null || (planEncontrado != null && numIni && numFin)) {

		List<Proveedor> pstIDOListAux = new ArrayList<Proveedor>();
		String actionPorted = "";

		if (numeroPortado != null) {

		    numeroPortadoBool = true;

		    actionPorted = numeroPortado.getAction();

		    // Se agrega esta condicion para tener en cuenta los procesos de reverse en
		    // portabilidad.
		    if (actionPorted.equals(action)) {
				pstIDOListAux = ngPublicService.getProveedorByIDO(numeroPortado.getRida());

		    } else {

				pstIDOListAux = ngPublicService.getProveedorByIDO(numeroPortado.getRida());
		    }

		} else if (planEncontrado != null && !numeroPortadoBool) {

		    LOGGER.info("Se busca el PST con IDA " + planEncontrado.getIda());
		    pstIDOListAux = ngPublicService.getProveedorByIDA(BigDecimal.valueOf(planEncontrado.getIda()));

		    LOGGER.info(
			    "Se encontraron " + pstIDOListAux.size() + " PST(s) con IDA " + planEncontrado.getIda());

		    if (pstIDOListAux.isEmpty()) {
			LOGGER.info("Se busca el PST como IDO debido a que no se localizó como IDA "
				+ planEncontrado.getIda());
			pstIDOListAux = ngPublicService.getProveedorByIDO(BigDecimal.valueOf(planEncontrado.getIda()));

			LOGGER.info("Se encontraron " + pstIDOListAux.size() + " PST(s) con IDA "
				+ planEncontrado.getIda());

			if (pstIDOListAux.isEmpty()) {

			    LOGGER.info("Se busca el PST con IDO debido a que no se localizó el IDA "
				    + planEncontrado.getIdo());
			    pstIDOListAux = ngPublicService
				    .getProveedorByIDO(BigDecimal.valueOf(planEncontrado.getIdo()));
			}
		    }

		}

		if (pstIDOListAux.size() >= 1) {
		    // Se recorre la lista en caso de existir más de un PST con un mismo código de
		    // identificación
		    LOGGER.info(
			    "No se encontro información en portabilidad o en el plan maestro, se busca internamente en el SNS");
		    for (Proveedor pst : pstIDOListAux) {
			LOGGER.info("Se asocia el PST localizado al número búscado");
			if (pstIDOListAux.size() == 1 || (pst.getConsultaPublicaSns() != null
				&& pst.getConsultaPublicaSns().equals(NUMERACION_GEOGRAFICA)))
			    this.setPrestadorNumero(pst);
		    }
		} else {
		    if (numeroPortadoBool) {
			List<Proveedor> pstIDAListAux = ngPublicService.getProveedorByIDA(numeroPortado.getRida());
			if (pstIDAListAux.size() >= 1) {
			    // Se recorre la lista en caso de existir más de un PST con un mismo código de
			    // identificación
			    for (Proveedor pst : pstIDAListAux) {
				if (pstIDAListAux.size() == 1 || (pst.getConsultaPublicaSns() != null
					&& pst.getConsultaPublicaSns().equals(NUMERACION_GEOGRAFICA)))
				    this.setPrestadorNumero(pst);
			    }
			} else {
			    LOGGER.error(
				    "No se ha definido un proveedor principal en la tabla CAT_PST para el RIDA: {}",
				    numeroPortado.getRida());
			    this.setPrestadorNumero(null);
			}
		    }
		}

	    } else {
		// Si no es un numero portado buscamos en rango serie.
		Proveedor proveedorAux = rango.getArrendatario();
		// si no existe arrendatario el prestador sera el asignatario
		if (proveedorAux == null) {
		    proveedorAux = rango.getAsignatario();
		}
		this.setPrestadorNumero(proveedorAux);
	    }
	    if (this.prestadorNumero != null) {
		this.setNumeracionAsignadaProveedor(
			ngPublicService.getTotalNumeracionAginadaProveedor(this.prestadorNumero.getId()));
	    }

	} catch (Exception e) {
	    LOGGER.error("Error insesperado en la carga de datos" + e.getMessage());
	    MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado");
	}
    }

	 */

    /**
     * Obtiene las poblaciones y la numeracion asignada del proveedor del numero
     * consultado.
     */
    public void getPoblacionesNumeracion() {
	try {
	    this.setPoblacionesNumeracionProveedor(
		    ngPublicService.getPoblacionesNumeracionByProveedor(this.prestadorNumero));
	    this.poblacionesProveedorTabla = true;
	    this.estadosProveedorTabla = false;
	    this.nirsProveedorTabla = false;
	} catch (Exception e) {
	    LOGGER.error("Error insesperado en la carga de datos" + e.getMessage());
	    //MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado"); //FJAH
	}
    }

    /**
     * Obtiene el detalle de poblacion de una seleccionada.
     * 
     * @param pst    Proveedor
     * @param estado Estado
     */
    public void getInfoPoblacionProveedorEstado(Proveedor pst, Estado estado) {
	try {
	    this.setEstadoProveedor(estado);
	    this.setPoblacionNumeracionProveedorEstado(
		    ngPublicService.findALLPoblacionesNumeracionByProveedorEstado(pst, estado));
	    this.poblacionesProveedorTabla = true;
	    this.estadosProveedorTabla = false;
	    this.nirsProveedorTabla = false;
	    if (LOGGER.isDebugEnabled()) {
		LOGGER.debug("Estado {} y Proveedor {} y lista {}", pst, estado,
			this.poblacionNumeracionProveedorEstado);
	    }
	} catch (Exception e) {
	    LOGGER.error("Error insesperado en la carga de datos" + e.getMessage());
	    //MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado"); //FJAH
	}
    }

    /**
     * Obtiene los estados y poblaciones de la numeracion asignada del proveedor del
     * numero consultado.
     */
    public void getEstadosNumeracion() {
	try {
	    this.setEstadosNumeracionProveedor(ngPublicService.getEstadosNumeracionByProveedor(this.prestadorNumero));
	    this.poblacionesProveedorTabla = false;
	    this.estadosProveedorTabla = true;
	    this.nirsProveedorTabla = false;
	} catch (Exception e) {
	    LOGGER.error("Error insesperado en la carga de datos" + e.getMessage());
	    //MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado"); //FJAH
	}
    }

    /**
     * Obtiene los nirs y la numeracion asignada del proveedor del numero
     * consultado.
     */
    public void getNirsNumeracion() {
	try {
	    this.setNirsNumeracionProveedor(ngPublicService.getNirsNumeracionByProveedor(this.prestadorNumero));
	    this.poblacionesProveedorTabla = false;
	    this.estadosProveedorTabla = false;
	    this.nirsProveedorTabla = true;
	} catch (Exception e) {
	    LOGGER.error("Error insesperado en la carga de datos" + e.getMessage());
	    //MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado"); //FJAH
	}
    }

    /**
     * @param table tabla a activar.
     */
    public void activateResultTable(String table) {
	try {
	    this.setPaginatedResultsPoblacionesActive(false);
	    this.setPaginatedResultsMunicipiosActive(false);
	    this.setPaginatedResultsConcesionariosActive(false);
	    if (StringUtils.equals(table, "poblaciones")) {
		this.setPaginatedResultsPoblacionesActive(true);
	    }
	    if (StringUtils.equals(table, "municipios")) {
		this.setPaginatedResultsMunicipiosActive(true);
	    }
	    if (StringUtils.equals(table, "concesionarios")) {
		this.setPaginatedResultsConcesionariosActive(true);
	    }
	} catch (Exception e) {
	    LOGGER.error("Error insesperado" + e.getMessage());
	    //MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado"); //FJAH
	}
    }

    /**
     * Convierte a formato numero USA un número.
     * 
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
	    LOGGER.error("Error insesperado al dar formato a la numeración asignada" + e.getMessage());
	    //MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado"); //FJAH
	}
	return numeroStr;
    }

    /**
     * Devuelve el tipo de red de un numero local o nacional.
     * 
     * @param rs RangoSerie
     */
    private void getTipoRed(RangoSerie rs) {
	try {
	    String resultado = "";
	    String modalidad = "";
	    String tipoRed = "";
	    tipoRed = rs.getTipoRed().getCdg();
	    if (tipoRed.compareTo(TipoRed.AMBAS) == 0) {
		tipoRed = "Fija y Móvil";
	    } else if (tipoRed.compareTo(TipoRed.FIJA) == 0) {
		tipoRed = "Fija";
	    } else if (tipoRed.compareTo(TipoRed.MOVIL) == 0) {
		tipoRed = "Móvil";
	    } else if (tipoRed.compareTo(TipoRed.DESCONOCIDA) == 0) {
		tipoRed = "Desconocida";
	    }
	    if (rs.getTipoModalidad() != null) {
		modalidad = "-" + rs.getTipoModalidad().getCdg();
	    } else {
		modalidad = "";
	    }
	    resultado = tipoRed + modalidad;
	    this.setTipoModalidadRed(resultado);
	} catch (Exception e) {
	    LOGGER.error("Error insesperado al devolver el tipo de red de la numeración consultada" + e.getMessage());
	    //MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado"); //FJAH
	}
    }

    /**
     * Busca y setea los nirs de una población.
     * 
     * @param poblacion Poblacion
     * @return List<Nir>
     */
    public List<Nir> buscaNirsPoblacion(Poblacion poblacion) {
	try {
	    this.setNirsPoblacion(ngPublicService.findAllNirByPoblacion(poblacion));
	} catch (Exception e) {
	    LOGGER.error("Error insesperado de carga de datos" + e.getMessage());
	    //MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado"); //FJAH
	}
	return this.nirsPoblacion;
    }

    /**
     * Obtiene el detalle de poblacion de una seleccionada.
     * 
     * @param pst Proveedor
     * @param nir Nir
     */
    public void getInfoNirProveedor(Proveedor pst, Nir nir) {
	try {
	    if (nir != null) {
		this.setPoblacionNumeracionProveedorEstado(
			ngPublicService.findALLPoblacionesNumeracionByProveedorNir(pst, nir));
	    }
	    this.poblacionesProveedorTabla = true;
	    this.estadosProveedorTabla = false;
	    this.nirsProveedorTabla = false;
	    if (LOGGER.isDebugEnabled()) {
		LOGGER.debug("Estado {} y Proveedor {} y lista {}", pst, estado,
			this.poblacionNumeracionProveedorEstado);
	    }
	} catch (Exception e) {
	    LOGGER.error("Error insesperado de carga de datos" + e.getMessage());
	    //MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado"); //FJAH
	}
    }

    /**
     * Setea la información ligada a una población seleccionada o un nir
     * seleccionado.
     * 
     * @param poblacion Poblacion
     * @param nir       Nir
     * @param tablaNir  boolean
     */
    public void infoPoblacion(Poblacion poblacion, Nir nir, boolean tablaNir) {
	try {
	    if (LOGGER.isDebugEnabled()) {
		LOGGER.debug("Bucando para poblacion {} y nir {}", poblacion, nir);
	    }
	    Abn abn = null;
	    this.setActivaTablaPoblacionesNoNir(false);
	    this.setNumberInfoTableActivated(false);
	    this.setMexicoInfoTableActivated(true);
	    this.setLocalServiceTableActivated(true);
	    this.setPopulationsTableActivated(false);
	    this.setMunicipioInfoTableActivated(false);
	    if (!tablaNir) {
		abn = ngPublicService.getAbnByPoblacion(poblacion);
		this.setPoblacionNumero(poblacion);
		this.setNirsPoblacion(ngPublicService.findAllNirByPoblacion(poblacion));
		this.setNumeracionAsignadaPobFormat(this.formatoNumeracionAsignada(
			ngPublicService.getTotalNumRangosAsignadosByPoblacion("", "", null, poblacion)));
	    } else if (tablaNir) {
		abn = ngPublicService.getAbnByCodigoNir(String.valueOf(nir.getCodigo()));
		this.setNumberInfoTableActivated(false);
		this.setLocalServiceTableActivated(true);
		this.setMexicoInfoTableActivated(false);
	    }
	    this.setPrestadoresServicioPoblacionNumero(
		    ngPublicService.findAllPrestadoresServicioBy(null, null, this.getPoblacionNumero(), null, null));
	    this.setNir(nir);
	    if (this.getNir() != null && abn != null) {
		this.setNirsAbn(ngPublicService.findAllNirByAbn(abn.getCodigoAbn()));
		this.setPoblacionAbnMaxNumAsignada(ngPublicService.getPoblacionWithMaxNumAsignadaByAbn(abn));
		this.setPoblacionesNir(ngPublicService.findALLPoblacionesNumeracionAsignadaByNir(this.getNir()));
		this.setMunicipiosNir(ngPublicService.findAllMunicipiosByNir(this.getNir(), true));
		this.setProveedoresNir(
			ngPublicService.findAllPrestadoresServicioBy(this.getNir(), null, null, null, null));
		BigDecimal bg = new BigDecimal(ngPublicService.findNumeracionesAsignadasNir(this.getNir()));
		this.setNumeracionNirFormat(this.formatoNumeracionAsignada(bg));
		if (abn.getPresuscripcion() != null) {
		    if (abn.getPresuscripcion().compareTo("P") == 0) {
			this.setPresuscripcion("Sí");
		    } else {
			this.setPresuscripcion("No");
		    }
		} else {
		    this.setPresuscripcion("No");
		}
	    }
	} catch (Exception e) {
	    LOGGER.error("Error insesperado de carga de datos de la población seleccionada" + e.getMessage());
	    //MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado"); //FJAH
	}
    }

    /**
     * Setea la información ligada a un municipio seleccionado.
     * 
     * @param municipio    Municipio
     * @param abnMunicipio Abn
     * @param nir          Nir
     */
    public void infoMunicipio(Municipio municipio, Abn abnMunicipio, Nir nir) {
	try {
	    this.setActivaTablaPoblacionesNoNir(false);
	    this.setNumberInfoTableActivated(false);
	    this.setMunicipioInfoTableActivated(true);
	    this.setLocalServiceTableActivated(false);
	    this.setMexicoInfoTableActivated(false);
	    this.setComboNumeroLocalNirsActivated(false);
	    this.setPopulationsTableActivated(false);
	    this.setAbn(abnMunicipio);
	    this.setMunicipio(municipio);
	    this.setNir(nir);
	    setNirsMunicipio(ngPublicService.finAllNirInMunicipio(municipio.getId().getCodMunicipio(),
		    municipio.getEstado().getCodEstado()));
	    BigDecimal bg = new BigDecimal(ngPublicService.getTotalNumRangosAsignadosByMunicipio(municipio));
	    this.setNumeracionAsignadaMunicipioFormat(this.formatoNumeracionAsignada(bg));
	    this.setNirsAbn(ngPublicService.findAllNirByAbn(abnMunicipio.getCodigoAbn()));
	    this.setProveedoresMunicipio(ngPublicService.findAllConcesionariosByMunicipio(municipio));
	    this.setInegiMunicipio(this.createInegiMunicipio(municipio.getId()));
	    if (this.getNir() != null && abnMunicipio != null) {
		this.setPoblacionAbnMaxNumAsignada(ngPublicService.getPoblacionWithMaxNumAsignadaByAbn(abnMunicipio));
		this.setPoblacionesNir(ngPublicService.findALLPoblacionesNumeracionAsignadaByNir(this.getNir()));
		this.setMunicipiosNir(ngPublicService.findAllMunicipiosByNir(this.getNir(), true));
		this.setProveedoresNir(
			ngPublicService.findAllPrestadoresServicioBy(this.getNir(), null, null, null, null));
		BigDecimal bgNir = new BigDecimal(ngPublicService.findNumeracionesAsignadasNir(this.getNir()));
		this.setNumeracionNirFormat(this.formatoNumeracionAsignada(bgNir));
		if (abnMunicipio.getPresuscripcion() != null) {
		    if (abnMunicipio.getPresuscripcion().compareTo("P") == 0) {
			this.setPresuscripcion("Sí");
		    } else {
			this.setPresuscripcion("No");
		    }
		} else {
		    this.setPresuscripcion("No");
		}
	    }
	} catch (Exception e) {
	    LOGGER.error("Error insesperado de carga de datos de la población seleccionada" + e.getMessage());
	    //MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado"); //FJAH
	}
    }

    /**
     * Crea la clave inegi del municipio.
     * 
     * @param idMunicipio MunicipioPK
     * @return String
     */
    public String createInegiMunicipio(MunicipioPK idMunicipio) {
	String inegiMunicipio = "";
	LOGGER.debug("pkmunicipio {}", idMunicipio);
	try {
	    if (idMunicipio != null) {
		String codEstado = idMunicipio.getCodEstado();
		String codMunicipio = idMunicipio.getCodMunicipio();
		codEstado = (codEstado.length() == 1) ? "0" + codEstado : codEstado;
		if (codMunicipio.length() == 1) {
		    codMunicipio = "00" + codMunicipio;
		} else if (codMunicipio.length() == 2) {
		    codMunicipio = "0" + codMunicipio;
		}
		inegiMunicipio = codEstado + codMunicipio;
	    }
	} catch (Exception e) {
	    LOGGER.error("Error al crear el inegio del municipio" + e.getMessage());
	    //MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado"); //FJAH
	}
	return inegiMunicipio;
    }

    /**
     * Devuelve el número de dígitos según el nir.
     * 
     * @param codigoNir String
     */
    private void chequeaNumeroDigitos(String codigoNir) {
	if (LOGGER.isDebugEnabled()) {
	    LOGGER.debug("Nir: {} y length {}", codigoNir, codigoNir.length());
	}
	switch (codigoNir.length()) {
	case 1:
	    this.setNumeroDigitos(9);
	    break;
	case 2:
	    this.setNumeroDigitos(10);
	    // this.setNumeroDigitos(8);
	    break;
	case 3:
	    this.setNumeroDigitos(10);
	    // this.setNumeroDigitos(7);
	    break;
	}
    }

    /**
     * Devuelve el número de dígitos según el nir.
     * 
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
		this.setNumeroDigitos(8);
		break;
	    case 3:
		this.setNumeroDigitos(7);
		break;
	    }
	} catch (Exception e) {
	    LOGGER.error("Error al setear el número de dígitos del numero interno" + e.getMessage());
	}
	return this.getNumeroDigitos();
    }

    /**
     * Setea la información ligada a una población seleccionada o un nir
     * seleccionado.
     * 
     * @param nir Nir
     */
    public void infoNir(Nir nir) {
	try {
	    Abn abn = null;
	    this.setActivaTablaPoblacionesNoNir(false);
	    this.setNumberInfoTableActivated(false);
	    this.setMexicoInfoTableActivated(true);
	    this.setLocalServiceTableActivated(true);
	    this.setPopulationsTableActivated(false);
	    this.setMunicipioInfoTableActivated(false);
	    abn = ngPublicService.getAbnByCodigoNir(String.valueOf(nir.getCodigo()));
	    this.setNumberInfoTableActivated(false);
	    this.setLocalServiceTableActivated(true);
	    this.setMexicoInfoTableActivated(false);
	    this.setPrestadoresServicioPoblacionNumero(
		    ngPublicService.findAllPrestadoresServicioBy(null, null, this.getPoblacionNumero(), null, null));
	    this.setNir(nir);
	    if (this.getNir() != null && abn != null) {
		this.setNirsAbn(ngPublicService.findAllNirByAbn(abn.getCodigoAbn()));
		this.setPoblacionAbnMaxNumAsignada(ngPublicService.getPoblacionWithMaxNumAsignadaByAbn(abn));
		this.setPoblacionesNir(ngPublicService.findALLPoblacionesNumeracionAsignadaByNir(this.getNir()));
		this.setMunicipiosNir(ngPublicService.findAllMunicipiosByNir(this.getNir(), true));
		this.setProveedoresNir(
			ngPublicService.findAllPrestadoresServicioBy(this.getNir(), null, null, null, null));
		BigDecimal bg = new BigDecimal(ngPublicService.findNumeracionesAsignadasNir(this.getNir()));
		this.setNumeracionNirFormat(this.formatoNumeracionAsignada(bg));
		if (abn.getPresuscripcion() != null) {
		    if (abn.getPresuscripcion().compareTo("P") == 0) {
			this.setPresuscripcion("Sí");
		    } else {
			this.setPresuscripcion("No");
		    }
		} else {
		    this.setPresuscripcion("No");
		}
	    }
	} catch (Exception e) {
	    LOGGER.error("error en la carga de datos" + e.getMessage());
	    //MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado"); //FJAH
	}
    }

    /**
     * Setea la información ligada a una población seleccionada.
     * 
     * @param poblacion Poblacion
     */
    public void infoPoblacionNir(Poblacion poblacion) {
	try {
	    this.setActivaTablaPoblacionesNoNir(true);
	    this.setNumberInfoTableActivated(false);
	    this.setMexicoInfoTableActivated(false);
	    this.setLocalServiceTableActivated(false);
	    this.setPopulationsTableActivated(false);
	    this.setMunicipioInfoTableActivated(false);
	    this.setPoblacionNumero(poblacion);
	    this.setNirsPoblacion(ngPublicService.findAllNirByPoblacion(this.poblacionNumero));
	    Abn abn = ngPublicService.getAbnByPoblacion(this.poblacionNumero);
	    this.setNumeracionAsignadaPobFormat(this.formatoNumeracionAsignada(
		    ngPublicService.getTotalNumRangosAsignadosByPoblacion("", "", null, this.poblacionNumero)));

	    this.setPrestadoresServicioPoblacionNumero(
		    ngPublicService.findAllPrestadoresServicioBy(null, null, this.getPoblacionNumero(), null, null));
	    if (abn != null) {
		this.setPoblacionAbnMaxNumAsignada(ngPublicService.getPoblacionWithMaxNumAsignadaByAbn(abn));
		if (abn.getPresuscripcion() != null) {
		    if (abn.getPresuscripcion().compareTo("P") == 0) {
			this.setPresuscripcion("Sí");
		    } else {
			this.setPresuscripcion("No");
		    }
		} else {
		    this.setPresuscripcion("No");
		}
	    }
	} catch (Exception e) {
	    LOGGER.error("Error al cargar datos" + e.getMessage());
	    //MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado"); //FJAH
	}
    }

    /**
     * Recupera un estado por su id.
     * 
     * @param estadoId String
     * @return Estado
     */
    public Estado getEstadoById(String estadoId) {
	try {
	    this.estado = ngPublicService.findEstadoById(estadoId);
	} catch (Exception e) {
	}
	return this.estado;
    }

    /**
     * Revisa que tipo de NIR es para saber cuantos numeros retirar al principio de
     * la cadena
     * 
     * @param numero
     * @return
     */
    public String getNumSinNir(String numero) {

	nirNumConsultado = Integer.parseInt(numero.substring(0, 2));

	if (nirNumConsultado == 33 || nirNumConsultado == 55 || nirNumConsultado == 56 || nirNumConsultado == 81) {
	    numConsultadoSinNir = numero.substring(2);
	} else {
	    numConsultadoSinNir = numero.substring(3);
	}

	return numConsultadoSinNir;
    }

    // ////////////////////////////////////////////GETTERS Y
    // SETTERS//////////////////////////////////////

	/**
	 * FJAH 30.06.2025
	 * Cantidades de municipio, proveedores y asignadas
	 * @return
	 */
	public int getCantidadMunicipiosZona() {
		return cantidadMunicipiosZona;
	}

	public void setCantidadMunicipiosZona(int cantidadMunicipiosZona) {
		this.cantidadMunicipiosZona = cantidadMunicipiosZona;
	}

	public int getCantidadEmpresasZona() {
		return cantidadEmpresasZona;
	}

	public void setCantidadEmpresasZona(int cantidadEmpresasZona) {
		this.cantidadEmpresasZona = cantidadEmpresasZona;
	}

	public BigDecimal getNumeracionZona() {
		return numeracionZona;
	}

	public void setNumeracionZona(BigDecimal numeracionZona) {
		this.numeracionZona = numeracionZona;
	}

	public String getZona() {
		return zona;
	}

	public void setZona(String zona) {
		this.zona = zona;
	}

	public List<mx.ift.sns.modelo.pst.Proveedor> getProveedoresZona() {
		return proveedoresZona;
	}

	public void setProveedoresZona(List<mx.ift.sns.modelo.pst.Proveedor> proveedoresZona) {
		this.proveedoresZona = proveedoresZona;
	}

	/**
     * Flag comprobacion input poblacion activado.
     * 
     * @return the populationInputActivated
     */
    public boolean getPopulationInputActivated() {
	return populationInputActivated;
    }

    /**
     * Flag comprobacion input poblacion activado.
     * 
     * @param populationInputActivated the populationInputActivated to set
     */
    public void setPopulationInputActivated(boolean populationInputActivated) {
	this.populationInputActivated = populationInputActivated;
    }

    /**
     * Campo numero nacional.
     * 
     * @return the nationalNumber
     */
    public String getNationalNumber() {
	return nationalNumber;
    }

    /**
     * Campo numero nacional.
     * 
     * @param nationalNumber the nationalNumber to set
     */
    public void setNationalNumber(String nationalNumber) {
	this.nationalNumber = nationalNumber;
    }

    /**
     * Flag comprobacion input numero nacional activado.
     * 
     * @return the nationalNumberInputActivated
     */
    public boolean getNationalNumberInputActivated() {
	return nationalNumberInputActivated;
    }

    /**
     * Flag comprobacion input numero nacional activado.
     * 
     * @param nationalNumberInputActivated the nationalNumberInputActivated to set
     */
    public void setNationalNumberInputActivated(boolean nationalNumberInputActivated) {
	this.nationalNumberInputActivated = nationalNumberInputActivated;
    }

    /**
     * Campo numero local.
     * 
     * @return the localNumber
     */
    public String getLocalNumber() {
	return localNumber;
    }

    /**
     * Campo numero local.
     * 
     * @param localNumber the localNumber to set
     */
    public void setLocalNumber(String localNumber) {
	this.localNumber = localNumber;
    }

    /**
     * Flag comprobacion input numero local activado.
     * 
     * @return the localNumberInputActivated
     */
    public boolean getLocalNumberInputActivated() {
	return localNumberInputActivated;
    }

    /**
     * Flag comprobacion input numero local activado.
     * 
     * @param localNumberInputActivated the localNumberInputActivated to set
     */
    public void setLocalNumberInputActivated(boolean localNumberInputActivated) {
	this.localNumberInputActivated = localNumberInputActivated;
    }

    /**
     * Campo nir.
     * 
     * @return the nir
     */
    public String getCodigoNir() {
	return codigoNir;
    }

    /**
     * Campo nir.
     * 
     * @param nir the nir to set
     */
    public void setCodigoNir(String nir) {
	this.codigoNir = nir;
    }

    /**
     * Flag comprobacion input nir local activado.
     * 
     * @return the nirInputActivated
     */
    public boolean getNirInputActivated() {
	return nirInputActivated;
    }

    /**
     * Flag comprobacion input nir local activado.
     * 
     * @param nirInputActivated the nirInputActivated to set
     */
    public void setNirInputActivated(boolean nirInputActivated) {
	this.nirInputActivated = nirInputActivated;
    }

    /**
     * Flag comprobacion boton formulario activado.
     * 
     * @return the formButtonActivated
     */
    public boolean getFormButtonActivated() {
	return formButtonActivated;
    }

    /**
     * Flag comprobacion boton formulario activado.
     * 
     * @param formButtonActivated the formButtonActivated to set
     */
    public void setFormButtonActivated(boolean formButtonActivated) {
	this.formButtonActivated = formButtonActivated;
    }

    /**
     * Flag para renderizado de tabla poblacion.
     * 
     * @return the populationsTableActivated
     */
    public boolean getPopulationsTableActivated() {
	return populationsTableActivated;
    }

    /**
     * Flag para renderizado de tabla poblacion.
     * 
     * @param populationsTableActivated the populationsTableActivated to set
     */
    public void setPopulationsTableActivated(boolean populationsTableActivated) {
	this.populationsTableActivated = populationsTableActivated;
    }

    /**
     * Flag para renderizado de tabla servicio local.
     * 
     * @return the localServiceTableActivated
     */
    public boolean getLocalServiceTableActivated() {
	return localServiceTableActivated;
    }

    /**
     * Flag para renderizado de tabla servicio local.
     * 
     * @param localServiceTableActivated the localServiceTableActivated to set
     */
    public void setLocalServiceTableActivated(boolean localServiceTableActivated) {
	this.localServiceTableActivated = localServiceTableActivated;
    }

    /**
     * Flag para renderizado de informacion del numero.
     * 
     * @return the numberInfoTableActivated
     */
    public boolean getNumberInfoTableActivated() {
	return numberInfoTableActivated;
    }

    /**
     * Flag para renderizado de informacion del numero.
     * 
     * @param numberInfoTableActivated the numberInfoTableActivated to set
     */
    public void setNumberInfoTableActivated(boolean numberInfoTableActivated) {
	this.numberInfoTableActivated = numberInfoTableActivated;
    }

    /**
     * Flag para renderizado de tabla de informacion de mexico.
     * 
     * @return the mexicoInfoTableActivated
     */
    public boolean getMexicoInfoTableActivated() {
	return mexicoInfoTableActivated;
    }

    /**
     * Flag para renderizado de tabla de informacion de mexico.
     * 
     * @param mexicoInfoTableActivated the mexicoInfoTableActivated to set
     */
    public void setMexicoInfoTableActivated(boolean mexicoInfoTableActivated) {
	this.mexicoInfoTableActivated = mexicoInfoTableActivated;
    }

    /**
     * Lista de poblaciones.
     * 
     * @return the poblaciones.
     */
    public List<Poblacion> getPoblaciones() {
	return poblaciones;
    }

    /**
     * Lista de poblaciones.
     * 
     * @param poblaciones the poblaciones to set.
     */
    public void setPoblaciones(List<Poblacion> poblaciones) {
	this.poblaciones = poblaciones;
    }

    /**
     * Campo poblacion buscada.
     * 
     * @return the poblacion
     */
    public Poblacion getPoblacion() {
	return poblacion;
    }

    /**
     * Campo poblacion buscada.
     * 
     * @param poblacion the poblacion to set
     */
    public void setPoblacion(Poblacion poblacion) {
	this.poblacion = poblacion;
    }

    /**
     * Abn.
     * 
     * @return the abn
     */
    public Abn getAbn() {
	return abn;
    }

    /**
     * Abn.
     * 
     * @param abn the abn to set
     */
    public void setAbn(Abn abn) {
	this.abn = abn;
    }

    /**
     * Lista de municipios del abn.
     * 
     * @return the municipiosNir
     */
    public List<Municipio> getMunicipiosNir() {
	return municipiosNir;
    }

    /**
     * Lista de municipios del abn.
     * 
     * @param municipiosNir the municipiosNir to set
     */
    public void setMunicipiosNir(List<Municipio> municipiosNir) {
	this.municipiosNir = municipiosNir;
    }

    /**
     * Poblacion con mayor numeracion asignada.
     * 
     * @return the poblacionAbnMaxNumAsignada
     */
    public Poblacion getPoblacionAbnMaxNumAsignada() {
	return poblacionAbnMaxNumAsignada;
    }

    /**
     * Poblacion con mayor numeracion asignada.
     * 
     * @param poblacionAbnMaxNumAsignada the poblacionAbnMaxNumAsignada to set
     */
    public void setPoblacionAbnMaxNumAsignada(Poblacion poblacionAbnMaxNumAsignada) {
	this.poblacionAbnMaxNumAsignada = poblacionAbnMaxNumAsignada;
    }

    /**
     * Campo poblacion de un numero consultado.
     * 
     * @return the poblacionNumero
     */
    public Poblacion getPoblacionNumero() {
	return poblacionNumero;
    }

    /**
     * Campo poblacion de un numero consultado.
     * 
     * @param poblacionNumero the poblacionNumero to set
     */
    public void setPoblacionNumero(Poblacion poblacionNumero) {
	this.poblacionNumero = poblacionNumero;
    }

    /**
     * Numero consultado.
     * 
     * @return the numeroConsultado
     */
    public Numero getNumeroConsultado() {
	return numeroConsultado;
    }

    /**
     * Numero consultado.
     * 
     * @param numeroConsultado the numeroConsultado to set
     */
    public void setNumeroConsultado(Numero numeroConsultado) {
	this.numeroConsultado = numeroConsultado;
    }

    /**
     * Lista de nirs de un numero local.
     * 
     * @return the nirsNumeroLocal
     */
    public List<Nir> getNirsNumeroLocal() {
	return nirsNumeroLocal;
    }

    /**
     * Lista de nirs de un numero local.
     * 
     * @param nirsNumeroLocal the nirsNumeroLocal to set
     */
    public void setNirsNumeroLocal(List<Nir> nirsNumeroLocal) {
	this.nirsNumeroLocal = nirsNumeroLocal;
    }

    /**
     * Prestador del servicio del numero consultado.
     * 
     * @return the prestadorNumero
     */
    public Proveedor getPrestadorNumero() {
	return prestadorNumero;
    }

    /**
     * Prestador del servicio del numero consultado.
     * 
     * @param prestadorNumero the prestadorNumero to set
     */
    public void setPrestadorNumero(Proveedor prestadorNumero) {
	this.prestadorNumero = prestadorNumero;
    }

    /**
     * Flag para renderizado de combo nirs numero local.
     * 
     * @return the comboNumeroLocalNirsActivated
     */
    public boolean isComboNumeroLocalNirsActivated() {
	return comboNumeroLocalNirsActivated;
    }

    /**
     * Flag para renderizado de combo nirs numero local.
     * 
     * @param comboNumeroLocalNirsActivated the comboNumeroLocalNirsActivated to set
     */
    public void setComboNumeroLocalNirsActivated(boolean comboNumeroLocalNirsActivated) {
	this.comboNumeroLocalNirsActivated = comboNumeroLocalNirsActivated;
    }

    /**
     * Nir numero local seleccionado.
     * 
     * @return the nirNumeroLocalSelected
     */
    public String getNirNumeroLocalSelected() {
	return nirNumeroLocalSelected;
    }

    /**
     * Nir numero local seleccionado.
     * 
     * @param nirNumeroLocalSelected the nirNumeroLocalSelected to set
     */
    public void setNirNumeroLocalSelected(String nirNumeroLocalSelected) {
	this.nirNumeroLocalSelected = nirNumeroLocalSelected;
    }

    /**
     * Lista de poblaciones y numeracion de un proveedor de servicio.
     * 
     * @return the poblacionesNumeracionProveedor
     */
    public List<PoblacionNumeracion> getPoblacionesNumeracionProveedor() {
	return this.poblacionesNumeracionProveedor;
    }

    /**
     * Lista de poblaciones y numeracion de un proveedor de servicio.
     * 
     * @param poblacionesNumeracionProveedor the poblacionesNumeracionProveedor to
     *                                       set
     */
    public void setPoblacionesNumeracionProveedor(List<PoblacionNumeracion> poblacionesNumeracionProveedor) {
	this.poblacionesNumeracionProveedor = poblacionesNumeracionProveedor;
    }

    /**
     * Lista de poblaciones y numeracion de un proveedor de servicio.
     * 
     * @return the estadosNumeracionProveedor
     */
    public List<EstadoNumeracion> getEstadosNumeracionProveedor() {
	return estadosNumeracionProveedor;
    }

    /**
     * Lista de poblaciones y numeracion de un proveedor de servicio.
     * 
     * @param estadosNumeracionProveedor the estadosNumeracionProveedor to set
     */
    public void setEstadosNumeracionProveedor(List<EstadoNumeracion> estadosNumeracionProveedor) {
	this.estadosNumeracionProveedor = estadosNumeracionProveedor;
    }

    /**
     * Lista de nirs y numeracion de un proveedor de servicio.
     * 
     * @return the nirsNumeracionProveedor
     */
    public List<NirNumeracion> getNirsNumeracionProveedor() {
	return nirsNumeracionProveedor;
    }

    /**
     * Lista de nirs y numeracion de un proveedor de servicio.
     * 
     * @param nirsNumeracionProveedor the nirsNumeracionProveedor to set
     */
    public void setNirsNumeracionProveedor(List<NirNumeracion> nirsNumeracionProveedor) {
	this.nirsNumeracionProveedor = nirsNumeracionProveedor;
    }

    /**
     * Lista de prestadores de servicio de una poblacion.
     * 
     * @return the prestadoresServicioPoblacionNumero
     */
    public List<Proveedor> getPrestadoresServicioPoblacionNumero() {
	return prestadoresServicioPoblacionNumero;
    }

    /**
     * Lista de prestadores de servicio de una poblacion.
     * 
     * @param prestadoresServicioPoblacionNumero the
     *                                           prestadoresServicioPoblacionNumero
     *                                           to set
     */
    public void setPrestadoresServicioPoblacionNumero(List<Proveedor> prestadoresServicioPoblacionNumero) {
	this.prestadoresServicioPoblacionNumero = prestadoresServicioPoblacionNumero;
    }

    /**
     * Flag de activación tabla poblaciones sin nir especificado.
     * 
     * @return the activaTablaPoblacionesNorNir
     */
    public boolean isActivaTablaPoblacionesNoNir() {
	return activaTablaPoblacionesNoNir;
    }

    /**
     * Flag de activación tabla poblaciones sin nir especificado.
     * 
     * @param activaTablaPoblacionesNoNir the activaTablaPoblacionesNoNir to set
     */
    public void setActivaTablaPoblacionesNoNir(boolean activaTablaPoblacionesNoNir) {
	this.activaTablaPoblacionesNoNir = activaTablaPoblacionesNoNir;
    }

    /**
     * Poblaciones Nir.
     * 
     * @return the poblacionesNir
     */
    public List<Poblacion> getPoblacionesNir() {
	return poblacionesNir;
    }

    /**
     * Poblaciones Nir.
     * 
     * @param poblacionesNir the poblacionesNir to set
     */
    public void setPoblacionesNir(List<Poblacion> poblacionesNir) {
	this.poblacionesNir = poblacionesNir;
    }

    /**
     * Numeración asignada nir en formato USA.
     * 
     * @return the numeracionNirFormat
     */
    public String getNumeracionNirFormat() {
	return numeracionNirFormat;
    }

    /**
     * Numeración asignada nir en formato USA.
     * 
     * @param numeracionNirFormat the numeracionNirFormat to set
     */
    public void setNumeracionNirFormat(String numeracionNirFormat) {
	this.numeracionNirFormat = numeracionNirFormat;
    }

    /**
     * Poblaciones con numeración asignada nir. *
     * 
     * @return the poblacionesNumeracionAsignadaNir
     */
    public List<Poblacion> getPoblacionesNumeracionAsignadaNir() {
	return poblacionesNumeracionAsignadaNir;
    }

    /**
     * Poblaciones con numeración asignada nir. *
     * 
     * @param poblacionesNumeracionAsignadaNir the poblacionesNumeracionAsignadaNir
     *                                         to set
     */
    public void setPoblacionesNumeracionAsignadaNir(List<Poblacion> poblacionesNumeracionAsignadaNir) {
	this.poblacionesNumeracionAsignadaNir = poblacionesNumeracionAsignadaNir;
    }

    /**
     * Proveedores Nir.
     * 
     * @return the proveedoresNir
     */
    public List<Proveedor> getProveedoresNir() {
	return proveedoresNir;
    }

    /**
     * Proveedores Nir.
     * 
     * @param proveedoresNir the proveedoresNir to set
     */
    public void setProveedoresNir(List<Proveedor> proveedoresNir) {
	this.proveedoresNir = proveedoresNir;
    }

    /**
     * Número de digitos según longitud nir.
     * 
     * @return the numerDigitos
     */
    public Integer getNumeroDigitos() {
	return numeroDigitos;
    }

    /**
     * Número de digitos según longitud nir.
     * 
     * @param numerDigitos the numerDigitos to set
     */
    public void setNumeroDigitos(Integer numerDigitos) {
	this.numeroDigitos = numerDigitos;
    }

    /**
     * Inegi municipio.
     * 
     * @return the inegiMunicipio
     */
    public String getInegiMunicipio() {
	return inegiMunicipio;
    }

    /**
     * Inegi municipio.
     * 
     * @param inegiMunicipio the inegiMunicipio to set
     */
    public void setInegiMunicipio(String inegiMunicipio) {
	this.inegiMunicipio = inegiMunicipio;
    }

    /**
     * Proveedores por municipio.
     * 
     * @return the proveedoresMunicipio
     */
    public List<Proveedor> getProveedoresMunicipio() {
	return proveedoresMunicipio;
    }

    /**
     * Proveedores por municipio.
     * 
     * @param proveedoresMunicipio the proveedoresMunicipio to set
     */
    public void setProveedoresMunicipio(List<Proveedor> proveedoresMunicipio) {
	this.proveedoresMunicipio = proveedoresMunicipio;
    }

    /**
     * Numeración asignada municipio en formato.
     * 
     * @return the numeracionAsignadaMunicipioFormat
     */
    public String getNumeracionAsignadaMunicipioFormat() {
	return numeracionAsignadaMunicipioFormat;
    }

    /**
     * Numeración asignada municipio en formato.
     * 
     * @param numeracionAsignadaMunicipioFormat the
     *                                          numeracionAsignadaMunicipioFormat to
     *                                          set
     */
    public void setNumeracionAsignadaMunicipioFormat(String numeracionAsignadaMunicipioFormat) {
	this.numeracionAsignadaMunicipioFormat = numeracionAsignadaMunicipioFormat;
    }

    /**
     * Lista de nirs de un municipio.
     * 
     * @return the nirsMunicipio
     */
    public List<Nir> getNirsMunicipio() {
	return nirsMunicipio;
    }

    /**
     * Lista de nirs de un municipio.
     * 
     * @param nirsMunicipio the nirsMunicipio to set
     */
    public void setNirsMunicipio(List<Nir> nirsMunicipio) {
	this.nirsMunicipio = nirsMunicipio;
    }

    /**
     * Bean detallePoblacionBean.
     * 
     * @return the detallePoblacionBean
     */
    public DetallePoblacionBean getDetallePoblacionBean() {
	return detallePoblacionBean;
    }

    /**
     * Bean detallePoblacionBean.
     * 
     * @param detallePoblacionBean the detallePoblacionBean to set
     */
    public void setDetallePoblacionBean(DetallePoblacionBean detallePoblacionBean) {
	this.detallePoblacionBean = detallePoblacionBean;
    }

    /**
     * Flag de activación tablas info municipio.
     * 
     * @return the municipioInfoTableActivated
     */
    public boolean isMunicipioInfoTableActivated() {
	return municipioInfoTableActivated;
    }

    /**
     * Flag de activación tablas info municipio.
     * 
     * @param municipioInfoTableActivated the municipioInfoTableActivated to set
     */
    public void setMunicipioInfoTableActivated(boolean municipioInfoTableActivated) {
	this.municipioInfoTableActivated = municipioInfoTableActivated;
    }

    /**
     * Municipio seleccionado en el detalle.
     * 
     * @return the municipio
     */
    public Municipio getMunicipio() {
	return municipio;
    }

    /**
     * Municipio seleccionado en el detalle.
     * 
     * @param municipio the municipio to set
     */
    public void setMunicipio(Municipio municipio) {
	this.municipio = municipio;
    }

    /**
     * Nirs por área de numeracion.
     * 
     * @return the nirsAbn
     */
    public List<Nir> getNirsAbn() {
	return nirsAbn;
    }

    /**
     * Nirs por área de numeracion.
     * 
     * @param nirsAbn the nirsAbn to set
     */
    public void setNirsAbn(List<Nir> nirsAbn) {
	this.nirsAbn = nirsAbn;
    }

    /**
     * Nir de búsqueda por nir.
     * 
     * @return the nir
     */
    public Nir getNir() {
	return nir;
    }

    /**
     * Nir de búsqueda por nir.
     * 
     * @param nir the nir to set
     */
    public void setNir(Nir nir) {
	this.nir = nir;
    }

    /**
     * Modalidad y tipo de red del numero consultado.
     * 
     * @return the tipoModalidadRed
     */
    public String getTipoModalidadRed() {
	return tipoModalidadRed;
    }

    /**
     * Modalidad y tipo de red del numero consultado.
     * 
     * @param tipoModalidadRed the tipoModalidadRed to set
     */
    public void setTipoModalidadRed(String tipoModalidadRed) {
	this.tipoModalidadRed = tipoModalidadRed;
    }

    /**
     * Nirs por poblacion.
     * 
     * @return the nirsPoblacion
     */
    public List<Nir> getNirsPoblacion() {
	return nirsPoblacion;
    }

    /**
     * Nirs por poblacion.
     * 
     * @param nirsPoblacion the nirsPoblacion to set
     */
    public void setNirsPoblacion(List<Nir> nirsPoblacion) {
	this.nirsPoblacion = nirsPoblacion;
    }

    /**
     * Estado con numeración asignada de un proveedor.
     * 
     * @return the estadoProveedor
     */
    public Estado getEstadoProveedor() {
	return estadoProveedor;
    }

    /**
     * Estado con numeración asignada de un proveedor.
     * 
     * @param estadoProveedor the estadoProveedor to set
     */
    public void setEstadoProveedor(Estado estadoProveedor) {
	this.estadoProveedor = estadoProveedor;
    }

    /**
     * PoblacionNumeracion por proveedor y estado.
     * 
     * @return the poblacionNumeracionProveedorEstado
     */
    public List<PoblacionNumeracion> getPoblacionNumeracionProveedorEstado() {
	return poblacionNumeracionProveedorEstado;
    }

    /**
     * PoblacionNumeracion por proveedor y estado.
     * 
     * @param poblacionNumeracionProveedorEstado the
     *                                           poblacionNumeracionProveedorEstado
     *                                           to set
     */
    public void setPoblacionNumeracionProveedorEstado(List<PoblacionNumeracion> poblacionNumeracionProveedorEstado) {
	this.poblacionNumeracionProveedorEstado = poblacionNumeracionProveedorEstado;
    }

    /**
     * Presusbscripcion.
     * 
     * @return the presubscripcion
     */
    public String getPresuscripcion() {
	return presuscripcion;
    }

    /**
     * Presusbscripcion.
     * 
     * @param presuscripcion the presuscripcion to set
     */
    public void setPresuscripcion(String presuscripcion) {
	this.presuscripcion = presuscripcion;
    }

    /**
     * Numeración asignada por proveedor.
     * 
     * @return the numeracionAsignadaProveedor
     */
    public Integer getNumeracionAsignadaProveedor() {
	return numeracionAsignadaProveedor;
    }

    /**
     * Numeración asignada por proveedor.
     * 
     * @param numeracionAsignadaProveedor the numeracionAsignadaProveedor to set
     */
    public void setNumeracionAsignadaProveedor(Integer numeracionAsignadaProveedor) {
	this.numeracionAsignadaProveedor = numeracionAsignadaProveedor;
    }

    /**
     * Numeración asignada por población en formato String.
     * 
     * @return the numeracionAsignadaPobFormat
     */
    public String getNumeracionAsignadaPobFormat() {
	return numeracionAsignadaPobFormat;
    }

    /**
     * Numeración asignada por población en formato String.
     * 
     * @param numeracionAsignadaPobFormat the numeracionAsignadaPobFormat to set
     */
    public void setNumeracionAsignadaPobFormat(String numeracionAsignadaPobFormat) {
	this.numeracionAsignadaPobFormat = numeracionAsignadaPobFormat;
    }

    /**
     * Muestra la tabla de poblaciones .
     * 
     * @return the paginatedResultsPoblacionesActive
     */
    public boolean isPaginatedResultsPoblacionesActive() {
	return paginatedResultsPoblacionesActive;
    }

    /**
     * Muestra la tabla de poblaciones .
     * 
     * @param paginatedResultsPoblacionesActive the
     *                                          paginatedResultsPoblacionesActive to
     *                                          set
     */
    public void setPaginatedResultsPoblacionesActive(boolean paginatedResultsPoblacionesActive) {
	this.paginatedResultsPoblacionesActive = paginatedResultsPoblacionesActive;
    }

    /**
     * Muestra la tabla de municipios .
     * 
     * @return the paginatedResultsMunicipiosActive
     */
    public boolean isPaginatedResultsMunicipiosActive() {
	return paginatedResultsMunicipiosActive;
    }

    /**
     * Muestra la tabla de municipios .
     * 
     * @param paginatedResultsMunicipiosActive the paginatedResultsMunicipiosActive
     *                                         to set
     */
    public void setPaginatedResultsMunicipiosActive(boolean paginatedResultsMunicipiosActive) {
	this.paginatedResultsMunicipiosActive = paginatedResultsMunicipiosActive;
    }

    /**
     * Muestra la tabla de concesionarios .
     * 
     * @return the paginatedResultsConcesionariosActive
     */
    public boolean isPaginatedResultsConcesionariosActive() {
	return paginatedResultsConcesionariosActive;
    }

    /**
     * Muestra la tabla de concesionarios .
     * 
     * @param paginatedResultsConcesionariosActive the
     *                                             paginatedResultsConcesionariosActive
     *                                             to set
     */
    public void setPaginatedResultsConcesionariosActive(boolean paginatedResultsConcesionariosActive) {
	this.paginatedResultsConcesionariosActive = paginatedResultsConcesionariosActive;
    }

    /**
     * Flag para renderizado de tabla poblaciones donde el proveedor tiene
     * numeración.
     * 
     * @return the poblacionesProveedorTabla
     */
    public boolean isPoblacionesProveedorTabla() {
	return poblacionesProveedorTabla;
    }

    /**
     * Flag para renderizado de tabla poblaciones donde el proveedor tiene
     * numeración.
     * 
     * @param poblacionesProveedorTabla the poblacionesProveedorTabla to set
     */
    public void setPoblacionesProveedorTabla(boolean poblacionesProveedorTabla) {
	this.poblacionesProveedorTabla = poblacionesProveedorTabla;
    }

    /**
     * Flag para renderizado de tabla estados donde el proveedor tiene numeración.
     * 
     * @return the estadosProveedorTabla
     */
    public boolean isEstadosProveedorTabla() {
	return estadosProveedorTabla;
    }

    /**
     * Flag para renderizado de tabla estados donde el proveedor tiene numeración.
     * 
     * @param estadosProveedorTabla the estadosProveedorTabla to set
     */
    public void setEstadosProveedorTabla(boolean estadosProveedorTabla) {
	this.estadosProveedorTabla = estadosProveedorTabla;
    }

    /**
     * Flag para renderizado de tabla nirs donde el proveedor tiene numeración.
     * 
     * @return the nirsProveedorTabla
     */
    public boolean isNirsProveedorTabla() {
	return nirsProveedorTabla;
    }

    /**
     * Flag para renderizado de tabla nirs donde el proveedor tiene numeración.
     * 
     * @param nirsProveedorTabla the nirsProveedorTabla to set
     */
    public void setNirsProveedorTabla(boolean nirsProveedorTabla) {
	this.nirsProveedorTabla = nirsProveedorTabla;
    }

    /**
     * Flag que activa la consulta numérica.
     * 
     * @return the consultaActivada
     */
    public boolean isConsultaActivada() {
	return consultaActivada;
    }

    /**
     * Flag que activa la consulta numérica.
     * 
     * @param consultaActivada the consultaActivada to set
     */
    public void setConsultaActivada(boolean consultaActivada) {
	this.consultaActivada = consultaActivada;
    }

    /**
     * String con los distintos número de registros por página. Por defecto:
     * 5,10,15,20,15
     * 
     * @return the numeroRegistros
     */
    public String getNumeroRegistros() {
	return numeroRegistros;
    }

    /**
     * String con los distintos número de registros por página. Por defecto:
     * 5,10,15,20,15
     * 
     * @param numeroRegistros the numeroRegistros to set
     */
    public void setNumeroRegistros(String numeroRegistros) {
	this.numeroRegistros = numeroRegistros;
    }

    public int getNirNumConsultado() {
	return nirNumConsultado;
    }

    public void setNirNumConsultado(int nirNumConsultado) {
	this.nirNumConsultado = nirNumConsultado;
    }

    public String getNumConsultadoSinNir() {
	return numConsultadoSinNir;
    }

    public void setNumConsultadoSinNir(String numConsultadoSinNir) {
	this.numConsultadoSinNir = numConsultadoSinNir;
    }

}
