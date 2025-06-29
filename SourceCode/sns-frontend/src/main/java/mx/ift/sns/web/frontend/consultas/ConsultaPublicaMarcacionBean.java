package mx.ift.sns.web.frontend.consultas;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.ift.sns.modelo.abn.Abn;
import mx.ift.sns.modelo.ng.RangoSerie;
import mx.ift.sns.modelo.ot.Municipio;
import mx.ift.sns.modelo.ot.Poblacion;
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

/** Bean que controla el formulario de consultas públicas. */
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

	/** Servicio de Busqueda Pública. */
	@EJB(mappedName = "ConsultaPublicaFacade")
	private IConsultaPublicaFacade ngPublicService;

	/** Servicio de configuración Pública. */
	@EJB(mappedName = "ConfiguracionFacade")
	private IConfiguracionFacade ngConfiguracionService;

	@EJB(mappedName = "PlanMaestroService")
	private IPlanMaestroService planService;

	/** Campo poblacion de un número consultado. */
	private Poblacion poblacionNumero;

	/** Lista de poblaciones. */
	private List<Poblacion> poblaciones;

	/** Campo numero nacional. */
	private String nationalNumber = "";

	/** Campo zona. */
	private String zona = "";

	/** Flag comprobación input numero nacional activado. */
	private boolean nationalNumberInputActivated = true;

	/** Flag comprobación input nir local activado. */
	private boolean zonaInputActivated = true;

	/** Flag comprobación boton formulario activado. */
	private boolean formButtonActivated = false;

	/** Flag para renderizado de tabla poblacion. */
    private boolean populationsTableActivated = false;

	/** Flag para renderizado de información del número. */
	private boolean numberInfoTableActivated = false;

	private boolean zonaInfoTableActivated = false;

	/** Abn. */
	private Abn abn;

	/** Numero consultado. **/
	private Numero numeroConsultado;

	/** Prestador del servicio del número consultado. **/
	private Proveedor prestadorNumero;

	/** Lista de municipios del abn. */
	private List<Municipio> municipiosNir;

	/** Poblacion con mayor numeracion asignada. */
    private Poblacion poblacionAbnMaxNumAsignada;

	/** Nirs por poblacion. */
    private List<Nir> nirsPoblacion;

	/** Nirs por área de numeracion. */
	private List<Nir> nirsAbn;

	/** Modalidad y tipo de red del número consultado. */
	private String tipoModalidadRed;

	/** Flag que activa la consulta numérica. */
	private boolean consultaActivada = true;

	/** Nir de búsqueda por nir. */
	private Nir nir;

	/** Municipio seleccionado en el detalle. */
	private Municipio municipio;

	/** Flag de activación tablas info municipio. */
	private boolean municipioInfoTableActivated = false;

	/** Número de digitos según longitud nir. */
	private Integer numeroDigitos;

	/** Proveedores Nir. */
	private List<Proveedor> proveedoresNir;

	/** Numeración asignada nir en formato USA. */
	private String numeracionNirFormat;

	/** Flag de activación tabla poblaciones sin nir especificado. */
	private boolean activaTablaPoblacionesNoNir = false;

	/**
	 * String con los distintos número de registros por página. Por defecto:
	 * 5,10,15,20,15
	 */
	private String numeroRegistros = "5, 10, 15, 20, 25";

	/** Lista de nirs de una zona */
	private List<Nir> nirsZona;

	/** Lista de Abn de una zona */
	private List<Abn> abnZona;

	// //////////////////////////////POSTCONSTRUCT//////////////////
	/**
	 * Postconstructor.
	 */
	@PostConstruct
	public void init() {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Postconstruct consulta numeracion: esta activado");
		}
	}

	/**
	 * Metodo que resetea el formulario y activa los inputs.
	 */
	public void resetSearch() {
		try {
			this.setNationalNumberInputActivated(true);
			this.setZonaInputActivated(true);
			this.setFormButtonActivated(false);
			this.setActivaTablaPoblacionesNoNir(false);
			this.setNumberInfoTableActivated(false);
			this.setZonaInfoTableActivated(false);
			this.setPopulationsTableActivated(false);
			this.setNationalNumber("");
			this.setZona("");
			this.setMunicipioInfoTableActivated(false);
		} catch (Exception e) {
            LOGGER.error("Error al limpiar todo {}", e.getMessage());
			MensajesFrontBean.addErrorMsg(MSG_ID, "Error insperado");
		}
	}

	/**
	 * Comprueba si algun campo esta relleno y deshabilita o habilita inputs y
	 * boton.
	 */
	public void checkFormStatus() {
		try {
			if (!getNationalNumber().isEmpty()) {
				setZonaInputActivated(false);
				if (getNationalNumber().length() >= 10) {
					setNationalNumberInputActivated(true);

					setFormButtonActivated(true);
				} else {
					setFormButtonActivated(false);
				}
			}

			if (!getZona().isEmpty()) {
				setNationalNumberInputActivated(false);
				if (getZona().length() == 1) {
					setZonaInputActivated(true);
					setFormButtonActivated(true);
				} else {
					setFormButtonActivated(false);
				}
			}
			if (getNationalNumber().isEmpty() && getZona().isEmpty()) {
				setNationalNumberInputActivated(true);
				setZonaInputActivated(true);
				setNumberInfoTableActivated(false);
				setZonaInfoTableActivated(false);
				this.setActivaTablaPoblacionesNoNir(false);
				setPopulationsTableActivated(false);
				setFormButtonActivated(false);
			}
		} catch (Exception e) {
			LOGGER.error("Error insesperado {}", e.getMessage());
			MensajesFrontBean.addErrorMsg(MSG_ID, "Error insperado");
		}
	}

	/**
	 * Método que realiza distintos tipos de busqueda dependiendo del input activo.
	 */
	public void publicSearch() {
		try {
			String regex = "[0-9]+";
			if (this.getNationalNumberInputActivated()) {
				this.setFormButtonActivated(false);
				this.setNationalNumberInputActivated(false);
				if (this.nationalNumber.matches(regex)) {
					parseAndSearchNationalNumber(this.nationalNumber);
				} else {
					MensajesFrontBean.addErrorMsg(MSG_ID, "Introduzca sólo caracteres del 0 al 9");
				}
			}
			if (this.getZonaInputActivated()) {
				String regexZona = "[2-9]+";
				this.setFormButtonActivated(false);
				this.setZonaInputActivated(false);
				if (this.zona.matches(regexZona)) {
					this.searchZona();
				} else {
					MensajesFrontBean.addErrorMsg(MSG_ID, "Introduzca sólo caracteres del 2 al 9");
				}
				this.setPopulationsTableActivated(true);
			}
		} catch (Exception e) {
            LOGGER.error("Error insesperado al buscar por alguno de los cuatro inputs{}", e.getMessage());
			MensajesFrontBean.addErrorMsg(MSG_ID, "Error insperado");
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
					this.setPoblacionNumero(rango.getPoblacion());

					this.setNumeroDigitos(10);
					this.setNir(numeroConsultado.getNir());

					this.nirsZona = ngPublicService.getNirByZona(Integer.parseInt(this.zona));
					this.abnZona = ngPublicService.getAbnByZona(Integer.parseInt(numeroConsultado.getZona()));
					List<Nir> nirList = new ArrayList<>();
					for(Abn _abn : this.abnZona) {
						nirList.addAll(ngPublicService.findAllNirByAbn(_abn.getCodigoAbn()));
					}
					this.setNirsAbn(nirList);

					if(this.nirsZona != null && this.abnZona != null) {
						List<Proveedor> proveedoresNirList = new ArrayList<>();
						List<Municipio> municipioList = new ArrayList<>();
						List<BigDecimal> bgList = new ArrayList<>();
						for(Nir nirZona : this.nirsZona) {
							proveedoresNirList.addAll(ngPublicService.findAllPrestadoresServicioBy(nirZona, null, null, null, null));
							municipioList.addAll(ngPublicService.findAllMunicipiosByNir(nirZona, true));
							bgList.add(new BigDecimal(ngPublicService.findNumeracionesAsignadasNir(nirZona)));
						}

						this.setProveedoresNir(new ArrayList<>(new LinkedHashSet<>(proveedoresNirList)));
						this.setMunicipiosNir(new ArrayList<>(new LinkedHashSet<>(municipioList)));

						this.setNirsPoblacion(ngPublicService.findAllNirByPoblacion(this.poblacionNumero));

						int i = 0;
						StringBuilder sb = new StringBuilder();
						for(BigDecimal bg : new ArrayList<>(new LinkedHashSet<>(bgList))) {
							sb.append(this.formatoNumeracionAsignada(bg));
							if(i < bgList.size()) {
								sb.append(",");
							}
							i++;
						}
						this.setNumeracionNirFormat(sb.toString());
						this.setPoblacionAbnMaxNumAsignada(ngPublicService.getPoblacionWithMaxNumAsignadaByAbn(this.getAbn()));
					}

					this.findProveedorPrestador(rango, nationalNumber);
					this.setNumberInfoTableActivated(true);
					this.setZonaInfoTableActivated(true);
					this.setActivaTablaPoblacionesNoNir(false);
				} else {
					MensajesFrontBean.addErrorMsg(MSG_ID, "Número no encontrado");
					this.setNumberInfoTableActivated(false);
					this.setZonaInfoTableActivated(false);
					this.setActivaTablaPoblacionesNoNir(false);
					this.setPopulationsTableActivated(false);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error insesperado en el parseo del numero nacional o local {}", e.getMessage());
			MensajesFrontBean.addErrorMsg(MSG_ID, "Numero no encontrado");
			this.setNumberInfoTableActivated(false);
			this.setActivaTablaPoblacionesNoNir(false);
			this.setPopulationsTableActivated(false);
		}
	}

	/**
	 * Chequea si existe la Zona introducida y setea los datos
	 * correspondientes relacionados.
	 */
	private void searchZona() {
		try {
			if (ngPublicService.existsZona(this.zona)) {
				this.nirsZona = ngPublicService.getNirByZona(Integer.parseInt(this.zona));
				this.abnZona = ngPublicService.getAbnByZona(Integer.parseInt(this.zona));
				List<Nir> nirList = new ArrayList<>();
				for(Abn _abn : this.abnZona) {
					nirList.addAll(ngPublicService.findAllNirByAbn(_abn.getCodigoAbn()));
				}
				this.setNirsAbn(nirList);
				this.setNumeroDigitos(10);

				if(this.nirsZona != null && this.abnZona != null) {
					List<Proveedor> proveedoresNirList = new ArrayList<>();
					List<Municipio> municipioList = new ArrayList<>();
					List<BigDecimal> bgList = new ArrayList<>();
					for(Nir nirZona : this.nirsZona) {
						proveedoresNirList.addAll(ngPublicService.findAllPrestadoresServicioBy(nirZona, null, null, null, null));
						municipioList.addAll(ngPublicService.findAllMunicipiosByNir(nirZona, true));
						bgList.add(new BigDecimal(ngPublicService.findNumeracionesAsignadasNir(nirZona)));
					}

					this.setProveedoresNir(new ArrayList<>(new LinkedHashSet<>(proveedoresNirList)));
					this.setMunicipiosNir(new ArrayList<>(new LinkedHashSet<>(municipioList)));

					int i = 0;
					StringBuilder sb = new StringBuilder();
					for(BigDecimal bg : new ArrayList<>(new LinkedHashSet<>(bgList))) {
						sb.append(this.formatoNumeracionAsignada(bg));
						if(i < bgList.size()) {
							sb.append(",");
						}
						i++;
					}
					this.setNumeracionNirFormat(sb.toString());
					this.setPoblacionAbnMaxNumAsignada(ngPublicService.getPoblacionWithMaxNumAsignadaByAbn(this.getAbn()));
				}

				this.setZonaInfoTableActivated(true);
				this.setFormButtonActivated(false);
				this.setZonaInputActivated(false);
			} else {
				MensajesFrontBean.addErrorMsg(MSG_ID, "El número introducido no corresponde con ninguna zona");
				this.setZonaInfoTableActivated(false);
			}
			this.setActivaTablaPoblacionesNoNir(false);
		} catch (Exception e) {
			LOGGER.error("Error insesperado en la carga de datos al buscar por zona {}", e.getMessage());
			MensajesFrontBean.addErrorMsg(MSG_ID, "Error en la búsqueda del zona");
		}
	}

	/**
	 * Busca el proveedor a partir de un rangoSerie.
	 *
	 * @param rango            RangoSerie
	 * @param numeroConsultado Strings
	 */
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

            LOGGER.info("Se obtiene el resultado de la busqueda del numero en el plan maestro {}", planEncontrado.toString() != null ? planEncontrado.toString() : "");

			Nir nirNumero = ngPublicService.getNirById(rango.getId().getIdNir());
			Long numeroIni = Long
					.valueOf(nirNumero.getCodigo() + rango.getId().getSna().toString() + rango.getNumInicio());
			Long numeroFinal = Long
					.valueOf(nirNumero.getCodigo() + rango.getId().getSna().toString() + rango.getNumFinal());

			long lnumeroConsultado = Long.parseLong(numeroConsultado != null ? numeroConsultado : "0");
			boolean numIni = numeroIni <= lnumeroConsultado;
			boolean numFin = numeroFinal >= lnumeroConsultado;

			if (numeroPortado != null || (planEncontrado != null && numIni && numFin)) {

				List<Proveedor> pstIDOListAux = new ArrayList<>();
				String actionPorted = "";

				if (numeroPortado != null) {

					numeroPortadoBool = true;

					actionPorted = numeroPortado.getAction();

					// Se agrega esta condicion para tener en cuenta los procesos de reverse en
					// portabilidad.
					if (actionPorted.equals(action)) {
						pstIDOListAux = ngPublicService.getProveedorByIDO(numeroPortado.getDida());

					} else {

						pstIDOListAux = ngPublicService.getProveedorByIDO(numeroPortado.getRida());
					}

				} else if (planEncontrado != null && !numeroPortadoBool) {

                    LOGGER.info("Se busca el PST con IDA {}", planEncontrado.getIda());
					pstIDOListAux = ngPublicService.getProveedorByIDA(BigDecimal.valueOf(planEncontrado.getIda()));

					LOGGER.info("Se encontraron " + pstIDOListAux.size() + " PST(s) con IDA " + planEncontrado.getIda());

					if (pstIDOListAux.isEmpty()) {
                        LOGGER.info("Se busca el PST como IDO debido a que no se localizó como IDA {}", planEncontrado.getIda());
						pstIDOListAux = ngPublicService.getProveedorByIDO(BigDecimal.valueOf(planEncontrado.getIda()));

						LOGGER.info("Se encontraron " + pstIDOListAux.size() + " PST(s) con IDA "
								+ planEncontrado.getIda());

						if (pstIDOListAux.isEmpty()) {

                            LOGGER.info("Se busca el PST con IDO debido a que no se localizó el IDA {}", planEncontrado.getIdo());
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
		} catch (Exception e) {
			LOGGER.error("Error insesperado en la carga de datos {}", e.getMessage());
			MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado");
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
            LOGGER.error("Error insesperado al dar formato a la numeración asignada{}", e.getMessage());
			MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado");
		}
		return numeroStr;
	}

	/**
	 * Devuelve el tipo de red de un número local o nacional.
	 *
	 * @param rs RangoSerie
	 */
	private void getTipoRed(RangoSerie rs) {
		try {
			String resultado;
			String modalidad;
			String tipoRed;
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
            LOGGER.error("Error insesperado al devolver el tipo de red de la numeración consultada {}", e.getMessage());
			MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado");
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
	    //MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado"); //FJAH se retira por no ofrecer valor al usuario final
	}
	return this.nirsPoblacion;
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
			this.setZonaInfoTableActivated(false);
			this.setMunicipioInfoTableActivated(false);
	    	this.setPopulationsTableActivated(false);
			if (!tablaNir) {
				abn = ngPublicService.getAbnByPoblacion(poblacion);
				this.setPoblacionNumero(poblacion);
				this.setNirsPoblacion(ngPublicService.findAllNirByPoblacion(poblacion));
			} else if (tablaNir) {
				abn = ngPublicService.getAbnByCodigoNir(String.valueOf(nir.getCodigo()));
				this.setNumberInfoTableActivated(false);
				this.setZonaInfoTableActivated(false);
			}
			this.setNir(nir);
			if (this.getNir() != null && abn != null) {
				this.setNirsAbn(ngPublicService.findAllNirByAbn(abn.getCodigoAbn()));
				this.setMunicipiosNir(ngPublicService.findAllMunicipiosByNir(this.getNir(), true));
				this.setProveedoresNir(ngPublicService.findAllPrestadoresServicioBy(this.getNir(), null, null, null, null));
				BigDecimal bg = new BigDecimal(ngPublicService.findNumeracionesAsignadasNir(this.getNir()));
				this.setNumeracionNirFormat(this.formatoNumeracionAsignada(bg));
				this.setPoblacionAbnMaxNumAsignada(ngPublicService.getPoblacionWithMaxNumAsignadaByAbn(abn));
			}
		} catch (Exception e) {
			LOGGER.error("Error insesperado de carga de datos de la población seleccionada {}", e.getMessage());
			MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado");
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
			this.setZonaInfoTableActivated(false);
			this.setMunicipioInfoTableActivated(true);
			this.setPopulationsTableActivated(false);
			this.setAbn(abnMunicipio);
			this.setMunicipio(municipio);
			this.setNir(nir);
			this.setNirsAbn(ngPublicService.findAllNirByAbn(abnMunicipio.getCodigoAbn()));
			if (this.getNir() != null && abnMunicipio != null) {
				this.setMunicipiosNir(ngPublicService.findAllMunicipiosByNir(this.getNir(), true));
				this.setProveedoresNir(ngPublicService.findAllPrestadoresServicioBy(this.getNir(), null, null, null, null));
				BigDecimal bgNir = new BigDecimal(ngPublicService.findNumeracionesAsignadasNir(this.getNir()));
				this.setNumeracionNirFormat(this.formatoNumeracionAsignada(bgNir));
				this.setPoblacionAbnMaxNumAsignada(ngPublicService.getPoblacionWithMaxNumAsignadaByAbn(abnMunicipio));
			}
		} catch (Exception e) {
			LOGGER.error("Error insesperado de carga de datos de la población seleccionada {}", e.getMessage());
			MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado");
		}
	}

	/**
	 * Setea la información ligada a una población seleccionada o un nir
	 * seleccionado.
	 *
	 * @param nir Nir
	 */
	public void infoNir(Nir nir) {
		try {
			this.setActivaTablaPoblacionesNoNir(false);
			this.setNumberInfoTableActivated(false);
			this.setZonaInfoTableActivated(false);
			this.setMunicipioInfoTableActivated(false);
			Abn abn = ngPublicService.getAbnByCodigoNir(String.valueOf(nir.getCodigo()));
			this.setNumberInfoTableActivated(false);
			this.setZonaInfoTableActivated(false);
			this.setPopulationsTableActivated(false);
			this.setNir(nir);
			if (this.getNir() != null && abn != null) {
				this.setNirsAbn(ngPublicService.findAllNirByAbn(abn.getCodigoAbn()));
				this.setPoblacionAbnMaxNumAsignada(ngPublicService.getPoblacionWithMaxNumAsignadaByAbn(abn));
				this.setMunicipiosNir(ngPublicService.findAllMunicipiosByNir(this.getNir(), true));
				this.setProveedoresNir(
						ngPublicService.findAllPrestadoresServicioBy(this.getNir(), null, null, null, null));
				BigDecimal bg = new BigDecimal(ngPublicService.findNumeracionesAsignadasNir(this.getNir()));
				this.setNumeracionNirFormat(this.formatoNumeracionAsignada(bg));
			}
		} catch (Exception e) {
			LOGGER.error("error en la carga de datos {}", e.getMessage());
			MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado");
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
			this.setZonaInfoTableActivated(false);
			this.setMunicipioInfoTableActivated(false);
			this.setPopulationsTableActivated(false);
			this.setPoblacionNumero(poblacion);
			this.setNirsPoblacion(ngPublicService.findAllNirByPoblacion(this.poblacionNumero));
			Abn abn = ngPublicService.getAbnByPoblacion(this.poblacionNumero);
			if (abn != null) {
				 this.setPoblacionAbnMaxNumAsignada(ngPublicService.getPoblacionWithMaxNumAsignadaByAbn(abn));
			}
		} catch (Exception e) {
            LOGGER.error("Error al cargar datos{}", e.getMessage());
			MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado");
		}
	}

	// ////////////////////////////////////////////GETTERS Y
	// SETTERS//////////////////////////////////////
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
	 * Campo zona
	 *
	 * @return the zona
	 */
	public String getZona() {
		return zona;
	}

	/**
	 * Campo zona
	 *
	 * @param zona the zona to set
	 */
	public void setZona(String zona) {
		this.zona = zona;
	}

	/**
	 * Flag comprobacion input nir local activado.
	 *
	 * @return the nirInputActivated
	 */
	public boolean getZonaInputActivated() {
		return zonaInputActivated;
	}

	/**
	 * Flag comprobacion input nir local activado.
	 *
	 * @param zonaInputActivated the nirInputActivated to set
	 */
	public void setZonaInputActivated(boolean zonaInputActivated) {
		this.zonaInputActivated = zonaInputActivated;
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

	public boolean getZonaInfoTableActivated() {
		return zonaInfoTableActivated;
	}

	public void setZonaInfoTableActivated(boolean zonaInfoTableActivated) {
		this.zonaInfoTableActivated = zonaInfoTableActivated;
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
	 * String con los distintos números de registros por página. Por defecto:
	 * 5,10,15,20,15
	 *
	 * @return the numeroRegistros
	 */
	public String getNumeroRegistros() {
		return numeroRegistros;
	}

	/**
	 * String con los distintos números de registros por página. Por defecto:
	 * 5,10,15,20,15
	 *
	 * @param numeroRegistros the numeroRegistros to set
	 */
	public void setNumeroRegistros(String numeroRegistros) {
		this.numeroRegistros = numeroRegistros;
	}
}
