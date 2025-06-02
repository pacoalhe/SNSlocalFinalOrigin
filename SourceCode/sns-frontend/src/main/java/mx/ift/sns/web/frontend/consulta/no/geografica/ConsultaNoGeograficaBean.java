package mx.ift.sns.web.frontend.consulta.no.geografica;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.ift.sns.modelo.nng.ClaveServicio;
import mx.ift.sns.modelo.nng.RangoSerieNng;
import mx.ift.sns.modelo.pnn.PlanMaestroDetalle;
import mx.ift.sns.modelo.port.NumeroPortado;
import mx.ift.sns.modelo.pst.Proveedor;
import mx.ift.sns.modelo.series.EstadoRango;
import mx.ift.sns.negocio.IConsultaPublicaFacade;
import mx.ift.sns.negocio.num.model.Numero;
import mx.ift.sns.negocio.pnn.IPlanMaestroService;
import mx.ift.sns.web.frontend.common.MensajesFrontBean;
import mx.ift.sns.web.frontend.consultas.ConsultaPublicaMarcacionBean;

/**
 * Bean para la consulta de Numeración No Geográfica.
 * 
 * @author X51461MO
 */
@ManagedBean(name = "consultaNoGeograficaBean")
@ViewScoped
public class ConsultaNoGeograficaBean implements Serializable {

    /** Servicio de Busqueda Publica. */
    @EJB(mappedName = "ConsultaPublicaFacade")
    private IConsultaPublicaFacade ngPublicService;

    @EJB(mappedName = "PlanMaestroService")
    private IPlanMaestroService planService;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsultaPublicaMarcacionBean.class);

    /** Serialización. */
    private static final long serialVersionUID = 1L;

    /** Identificador del componente de mensajes JSF. */
    private static final String MSG_ID = "MSG_NoGeo";

    /** Identificador del tipo de numeración a consultar. */
    private static final String NUMERACION_NO_GEOGRAFICA = "NNG";

    /**
     * Listado de todas la claves de servicio.
     */
    private List<ClaveServicio> clavesServicio;
    /**
     * Código de la clave seleccionada en la lista desplegable.
     */
    private Integer claveSelected;
    /**
     * Flag de activación de tabla de búsqueda.
     */
    private boolean tablaActivated = false;
    /**
     * Status Rango de la numeración consultada.
     */
    private EstadoRango statusConsulta;
    /**
     * Proveedor de la numeración consultada.
     */
    private Proveedor proveedorConsulta;

    /**
     * Numero no geográfico.
     */
    private String numeroIntroducido = "";
    /**
     * RangoSerie buscado.
     */
    private RangoSerieNng rangoSerieNng;
    /**
     * Numeración asignada.
     */
    private String numAsignada;
    /**
     * Ativación del botón buscar.
     */
    private boolean submitActivate = true;
    /**
     * Sna del número buscado.
     */
    private String sna;

    /**
     * Clave servicio del número buscado.
     */
    private String claveServicio;

    /**
     * Tipo de asignación.
     */
    private String formaAsignacion;

    // /////////////////////////////////POST
    // CONSTRUCT/////////////////////////////////

    /**
     * Postconstructor.
     */
    @PostConstruct
    public void init() {
	if (LOGGER.isDebugEnabled()) {
	    LOGGER.debug("PostConstruc Consulta No Geográfica: está activado");
	}
	try {
	    // Listado de claves de servicio
	    this.setClavesServicio(ngPublicService.findAllClaveServicio());
	} catch (Exception e) {
	    LOGGER.error("Error al buscar las claves de servico visibles en la web" + e.getMessage());
	    MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado");
	}

    }

    // ///////////////////////////////////////MÉTODOS//////////////////////////////////
    /**
     * Action del botón buscar.
     */
    public void searchNumeroNoGeo() {
	try {
	    this.setSubmitActivate(true);
	    String regex = "[0-9A-Z]+";
	    if (this.claveSelected != null) {
		if (this.numeroIntroducido.matches(regex)) {
		    this.checkIntroducido(this.numeroIntroducido);
		} else {
		    MensajesFrontBean.addErrorMsg(MSG_ID, "Introduzca un número en el formato correcto");
		}
	    } else {
		MensajesFrontBean.addErrorMsg(MSG_ID, "Seleccione una clave de servicio");
	    }
	} catch (Exception e) {
	    LOGGER.error("Error inesperado al validar el número introducido" + e.getMessage());
	    MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado");
	}
    }

    /**
     * Action del botón limpiar.
     */
    public void clearAll() {
	try {
	    this.setClaveSelected(null);
	    this.setTablaActivated(false);
	    this.setNumeroIntroducido("");
	    this.setSubmitActivate(true);
	} catch (Exception e) {
	    LOGGER.error("Error inesperado" + e.getMessage());
	    MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado");
	}
    }

    /**
     * Parsea el número introducido para descomponerlo en serie y numeración
     * Inicial/final.
     * 
     * @param numeroIntroducido String
     */
    private void checkIntroducido(String numeroIntroducido) {

	String regexNum = "[0-9]+";
	String regexComb = "[0-9A-Z]+";

	if (LOGGER.isDebugEnabled()) {
	    LOGGER.debug("El numer introducido es: {}", numeroIntroducido);
	}
	if (numeroIntroducido.matches(regexNum)) {
	    this.parseAndSetNumeracion(numeroIntroducido);
	} else if (numeroIntroducido.matches(regexComb)) {
	    // Bucle for que recorre el string.
	    String cadena = "";
	    for (int i = 0; i < numeroIntroducido.length(); i++) {
		char c = numeroIntroducido.charAt(i);
		cadena = cadena + this.letterToNumber(c);
	    }
	    if (LOGGER.isDebugEnabled()) {
		LOGGER.debug("La cadena introducida corresponde a: {}", cadena);
	    }
	    this.parseAndSetNumeracion(cadena);

	}
    }

    /**
     * Parsea el numero introducido y setea el rangoSerieNng asociado si existe.
     * 
     * @param numString String
     */

    private void parseAndSetNumeracion(String numString) {
	Numero numeroParseado = ngPublicService.parseNumeracion(numString);
	String serieStr = "";
	String rangoNumStr = "";
	serieStr = numeroParseado.getSna();
	rangoNumStr = numeroParseado.getNumeroInterno();

	BigDecimal sna = new BigDecimal(Integer.valueOf(serieStr));
	BigDecimal claveBd = new BigDecimal(this.claveSelected);

	this.setRangoSerieNng(ngPublicService.getRangoSeriePertenece(claveBd, sna, rangoNumStr));

	if (this.rangoSerieNng != null) {
	    this.buscaFormaAsignacion(this.rangoSerieNng);
	    this.findProveedorPrestador(this.rangoSerieNng, numString);
	    this.setClaveServicio(String.valueOf(this.claveSelected));
	    this.setNumAsignada(rangoNumStr);
	    this.setSna(serieStr);
	    this.setStatusConsulta(this.rangoSerieNng.getEstatus());
	    this.setTablaActivated(true);

	} else {
	    MensajesFrontBean.addErrorMsg(MSG_ID, "El número " + this.numeroIntroducido + " no ha sido asignado");
	}
    }

    /**
     * Busca el proveedor a partir de un rangoSerie.
     * 
     * @param rango            RangoSerie
     * @param numeroConsultado Strings
     */
	/**
	 * FJAH 29.05.2025
	 * Refactorización del proveedor para Numeración No Geográfica.
	 *
	 * @param rango RangoSerieNng
	 * @param numeroConsultado String
	 */
	private void findProveedorPrestador(RangoSerieNng rango, String numeroConsultado) {
		try {
			LOGGER.info("===> findProveedorPrestador ejecutado para número no geográfico {}", numeroConsultado);

			if (this.claveSelected == null || numeroConsultado == null || numeroConsultado.trim().isEmpty()) {
				LOGGER.error("Datos insuficientes: claveSelected={}, numeroConsultado='{}'", this.claveSelected, numeroConsultado);
				this.setProveedorConsulta(null);
				MensajesFrontBean.addErrorMsg(MSG_ID, "Datos insuficientes para consulta de proveedor.");
				return;
			}

			String numeroCompleto = this.claveSelected + numeroConsultado;
			LOGGER.info("===> Número completo armado: {}", numeroCompleto);

			if (!numeroCompleto.matches("\\d+")) {
				LOGGER.error("El número completo no es numérico: {}", numeroCompleto);
				this.setProveedorConsulta(null);
				MensajesFrontBean.addErrorMsg(MSG_ID, "El número consultado es inválido.");
				return;
			}

			NumeroPortado numeroPortado = ngPublicService.findNumeroPortado(numeroCompleto);
			LOGGER.info("===> Resultado findNumeroPortado: {}", (numeroPortado != null ? "Sí" : "No"));
			if (numeroPortado != null) {
				LOGGER.info("    RIDA: {}, DIDA: {}, ACTION: {}", numeroPortado.getRida(), numeroPortado.getDida(), numeroPortado.getAction());
			}

			PlanMaestroDetalle planEncontrado = planService.getPlanMaestroDetalle(Long.valueOf(numeroCompleto), Long.valueOf(numeroCompleto));
			LOGGER.info("===> Resultado planService.getPlanMaestroDetalle: {}", (planEncontrado != null ? "Sí" : "No"));

			List<Proveedor> pstList = new ArrayList<>();

			// Portabilidad primero
			if (numeroPortado != null) {
				// Orden estricto RIDA ABC → RIDA IDA → DIDA IDA → DIDA IDO
				pstList = ngPublicService.getProveedorByABC(numeroPortado.getRida());
				if (pstList.isEmpty()) {
					LOGGER.info("No se encontró por ABC. Buscando por IDA: {}", numeroPortado.getRida());
					pstList = ngPublicService.getProveedorByIDA(numeroPortado.getRida());
					if (pstList.isEmpty()) {
						LOGGER.info("No se encontró por RIDA. Buscando por DIDA como IDA: {}", numeroPortado.getDida());
						pstList = ngPublicService.getProveedorByIDA(numeroPortado.getDida());
						if (pstList.isEmpty()) {
							LOGGER.info("No se encontró por DIDA en IDA. Buscando por DIDA como IDO: {}", numeroPortado.getDida());
							pstList = ngPublicService.getProveedorByIDO(numeroPortado.getDida());
						}
					}
				}
			} else if (planEncontrado != null) {
				// Por catálogo (BCD -> IDA -> IDO -> IDO de IDA)
				pstList = ngPublicService.getProveedorByABC(BigDecimal.valueOf(planEncontrado.getIda()));
				if (pstList.isEmpty()) {
					LOGGER.info("No se encontró por ABC. Buscando por IDA: {}", numeroPortado.getRida());
					pstList = ngPublicService.getProveedorByIDA(BigDecimal.valueOf(planEncontrado.getIda()));
					if (pstList.isEmpty()) {
						LOGGER.info("No se encontró por RIDA. Buscando por DIDA como IDA: {}", numeroPortado.getDida());
						pstList = ngPublicService.getProveedorByIDO(BigDecimal.valueOf(planEncontrado.getIda()));
						if (pstList.isEmpty()) {
							LOGGER.info("No se encontró por DIDA en IDA. Buscando por DIDA como IDO: {}", numeroPortado.getDida());
							pstList = ngPublicService.getProveedorByIDO(BigDecimal.valueOf(planEncontrado.getIdo()));
						}
					}
				}
			}

			if (!pstList.isEmpty()) {
				// Selecciona el proveedor correcto igual que los métodos refactorizados
				Proveedor seleccionado = null;
				if (pstList.size() == 1) {
					seleccionado = pstList.get(0);
				} else {
					// Busca por consultaPublicaSns = NUMERACION_NO_GEOGRAFICA (o el criterio correcto)
					for (Proveedor pst : pstList) {
						if (pst.getConsultaPublicaSns() != null &&
								pst.getConsultaPublicaSns().equals(NUMERACION_NO_GEOGRAFICA)) {
							seleccionado = pst;
							break;
						}
					}
					// Si no se encontró con el filtro, agarra el primero (igual que IDO/IDA/ABC)
					if (seleccionado == null) {
						seleccionado = pstList.get(0);
					}
				}
				this.setProveedorConsulta(seleccionado);
				LOGGER.info("Proveedor asignado: {}", seleccionado != null ? seleccionado.getNombre() : "null");
			} else {
				// No se encontró proveedor por ningún camino
				this.setProveedorConsulta(null);
				LOGGER.warn("No se encontró proveedor para el número consultado (No Geo).");
				MensajesFrontBean.addErrorMsg(MSG_ID, "Proveedor no encontrado para el número consultado.");
			}

		} catch (Exception e) {
			LOGGER.error("Error inesperado en la carga de datos: {}", e.getMessage(), e);
			MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado");
		}
	}

	/*
    private void findProveedorPrestador(RangoSerieNng rango, String numeroConsultado) {
	try {
//FJAH 28.05.2025 Rastreo
		LOGGER.debug("Clave seleccionada: " + this.claveSelected);
		LOGGER.debug("Número consultado: " + numeroConsultado);

		if (this.claveSelected == null
				|| numeroConsultado == null
				|| numeroConsultado.trim().isEmpty()) {
			LOGGER.error("Datos insuficientes: claveSelected=" + this.claveSelected + ", numeroConsultado='" + numeroConsultado + "'");
			this.setProveedorConsulta(null);
			MensajesFrontBean.addErrorMsg(MSG_ID, "Datos insuficientes para consulta de proveedor.");
			return;
		}
//Fin Rastreo

		String numeroCompleto = this.claveSelected + numeroConsultado;

//FJAH 28.05.2025 Rastreo
		LOGGER.debug("Número completo armado: " + numeroCompleto);
		if (!numeroCompleto.matches("\\d+")) {
			LOGGER.error("El número completo no es numérico: " + numeroCompleto);
			this.setProveedorConsulta(null);
			MensajesFrontBean.addErrorMsg(MSG_ID, "El número consultado es inválido.");
			return;
		}
//Fin Rastreo

		// Si existe numero portado, buscamos el pst por ABC, si no devuelve nada
	    // buscamos por IDA.

		NumeroPortado numeroPortado = ngPublicService.findNumeroPortado(numeroCompleto);

//FJAH 28.05.2025 Rastreo
		LOGGER.debug("Resultado de findNumeroPortado para " + numeroCompleto + ": " + (numeroPortado != null ? numeroPortado.toString() : "null"));
//Fin Rastreo

	    LOGGER.info("Se realiza la busqueda del numero en el plan maestro ******");
//FJAH 28.05.2025 Rastreo
		if (numeroCompleto == null || !numeroCompleto.matches("\\d+")) {
			LOGGER.error("El número completo es inválido: " + numeroCompleto);
			this.setProveedorConsulta(null);
			MensajesFrontBean.addErrorMsg(MSG_ID, "El número consultado no es válido para esta consulta.");
			return;
		}

		try {
			Long numCompletoLong = Long.valueOf(numeroCompleto);
			PlanMaestroDetalle planEncontrado = planService.getPlanMaestroDetalle(numCompletoLong, numCompletoLong);
			LOGGER.debug("Resultado de getPlanMaestroDetalle: " + (planEncontrado != null ? planEncontrado.toString() : "null"));
			// ... resto del flujo
		} catch (NumberFormatException ex) {
			LOGGER.error("Error al convertir numeroCompleto a Long: " + numeroCompleto, ex);
			this.setProveedorConsulta(null);
			MensajesFrontBean.addErrorMsg(MSG_ID, "Número consultado inválido para consulta en plan maestro.");
			return;
		}
//FIN Rastreo

		PlanMaestroDetalle planEncontrado = planService.getPlanMaestroDetalle(Long.valueOf(numeroCompleto),
		    Long.valueOf(numeroCompleto));

	    LOGGER.info("Se obtiene el resultado de la busqueda del numero en el plan maestro "
		    + planEncontrado.toString() != null ? planEncontrado.toString() : "");

	    if (numeroPortado != null) {
			List<Proveedor> pstIDOListAux = ngPublicService.getProveedorByABC(numeroPortado.getRida());

//FJAH 28.05.2025 Rastreo
		LOGGER.debug("Proveedores encontrados por ABC para RIDA " + numeroPortado.getRida() + ": " + (pstIDOListAux != null ? pstIDOListAux.size() : "null"));
//FIN Rastreo

		if (pstIDOListAux.size() >= 1) {
		    // Se recorre la lista en caso de existir más de un PST con un mismo código de
		    // identificación
		    for (Proveedor pst : pstIDOListAux) {

//FJAH 28.05.2025 Rastreo
				LOGGER.debug("Revisando proveedor: " + pst.toString());
//FIN Rastreo
				if (pstIDOListAux.size() == 1 || (pst.getConsultaPublicaSns() != null
				&& pst.getConsultaPublicaSns().equals(NUMERACION_NO_GEOGRAFICA)))
			    this.setProveedorConsulta(pst);

//FJAH 28.05.2025 Rastreo
				LOGGER.info("Proveedor asignado: " + (this.getProveedorConsulta() != null ? this.getProveedorConsulta().toString() : "null"));
//FIN Rastreo

		    }
		} else {
		    List<Proveedor> pstIDAListAux = ngPublicService.getProveedorByIDA(numeroPortado.getRida());
		    if (pstIDAListAux.size() >= 1) {
			// Se recorre la lista en caso de existir más de un PST con un mismo código de
			// identificación
			for (Proveedor pst : pstIDAListAux) {

//FJAH 28.05.2025 Rastreo
				LOGGER.debug("Revisando proveedor: " + pst.toString());
//FIN Rastreo

			    if (pstIDAListAux.size() == 1 || (pst.getConsultaPublicaSns() != null
				    && pst.getConsultaPublicaSns().equals(NUMERACION_NO_GEOGRAFICA)))
				this.setProveedorConsulta(pst);

//FJAH 28.05.2025 Rastreo
				LOGGER.info("Proveedor asignado: " + (this.getProveedorConsulta() != null ? this.getProveedorConsulta().toString() : "null"));
//FIN Rastreo

			}
		    } else {
			LOGGER.error("No se ha definido un proveedor principal en la tabla CAT_PST para el RIDA: {}",
				numeroPortado.getRida());
			this.setProveedorConsulta(null);

//FJAH 28.05.2025 Rastreo
				LOGGER.info("Proveedor asignado: " + (this.getProveedorConsulta() != null ? this.getProveedorConsulta().toString() : "null"));
//FIN Rastreo

		    }
		}
	    } else if (planEncontrado != null) {
		List<Proveedor> pstIDOListAux = null;

		LOGGER.info("Se busca el PST con IDA " + planEncontrado.getIda());

		pstIDOListAux = ngPublicService.getProveedorByIDA(BigDecimal.valueOf(planEncontrado.getIda()));

		LOGGER.info("Se encontraron " + pstIDOListAux.size() + " PST(s) con IDA " + planEncontrado.getIda());

		if (pstIDOListAux.isEmpty()) {

		    LOGGER.info(
			    "Se busca el PST como IDO debido a que no se localizó por IDA " + planEncontrado.getIda());
		    pstIDOListAux = ngPublicService.getProveedorByIDO(BigDecimal.valueOf(planEncontrado.getIda()));

		    if (pstIDOListAux.isEmpty()) {

			LOGGER.info("Se busca el PST con IDO debido a que no se localizó el IDA "
				+ planEncontrado.getIdo());
			pstIDOListAux = ngPublicService.getProveedorByIDO(BigDecimal.valueOf(planEncontrado.getIdo()));
		    }
		}

		if (pstIDOListAux.size() >= 1) {
		    // Se recorre la lista en caso de existir más de un PST con un mismo código de
		    // identificación
		    LOGGER.info(
			    "No se encontro información en portabilidad o en el plan maestro, se busca internamente en el SNS");
		    for (Proveedor pst : pstIDOListAux) {
			LOGGER.info("Se asocia el PST localizado al número búscado");

//FJAH 28.05.2025 Rastreo
				LOGGER.debug("Revisando proveedor: " + pst.toString());
//FIN Rastreo

			if (pstIDOListAux.size() == 1 || (pst.getConsultaPublicaSns() != null
				&& pst.getConsultaPublicaSns().equals(NUMERACION_NO_GEOGRAFICA)))
			    this.setProveedorConsulta(pst);

//FJAH 28.05.2025 Rastreo
				LOGGER.info("Proveedor asignado: " + (this.getProveedorConsulta() != null ? this.getProveedorConsulta().toString() : "null"));
//FIN Rastreo

		    }
		}

	    } else {
		this.setProveedorConsulta(rango.getAsignatario());

//FJAH 28.05.2025 Rastreo
			LOGGER.info("Proveedor asignado: " + (this.getProveedorConsulta() != null ? this.getProveedorConsulta().toString() : "null"));
//FIN Rastreo

		;
	    }

	} catch (Exception e) {
	    LOGGER.error("Error insesperado en la carga de datos" + e.getMessage());
	    MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado");
	}
    }

	 */

    /**
     * Devuelve el número correspondiente a la letra introducida.
     * 
     * @param c char
     * @return char
     */
    private char letterToNumber(char c) {

	if (c == 'A' | c == 'B' | c == 'C') {
	    c = '2';
	    return c;
	} else if (c == 'D' | c == 'E' | c == 'F') {
	    c = '3';
	    return c;
	} else if (c == 'G' | c == 'H' | c == 'I') {
	    c = '4';
	    return c;
	} else if (c == 'J' | c == 'K' | c == 'L') {
	    c = '5';
	    return c;
	} else if (c == 'M' | c == 'N' | c == 'O') {
	    c = '6';
	    return c;
	} else if (c == 'P' | c == 'Q' | c == 'R' | c == 'S') {
	    c = '7';
	    return c;
	} else if (c == 'T' | c == 'U' | c == 'V') {
	    c = '8';
	    return c;
	} else if (c == 'W' | c == 'X' | c == 'Y' | c == 'Z') {
	    c = '9';
	    return c;
	}
	return c;
    }

    /**
     * Activa el botón submit sí la longitud del número introducido es 7.
     */
    public void checkStatus() {
	try {
	    if (this.numeroIntroducido.length() >= 7) {
		this.setSubmitActivate(false);
	    } else {
		this.setSubmitActivate(true);
	    }
	} catch (Exception e) {
	    LOGGER.error("Error inesperado" + e.getMessage());
	    MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado");
	}

    }

    /**
     * Método que define el tipo de forma de asignación.
     * 
     * @param rsng RangoSerieNng
     */
    public void buscaFormaAsignacion(RangoSerieNng rsng) {
	String numInicio = "0000";
	String numFinal = "9999";
	try {
	    if (LOGGER.isDebugEnabled()) {
		LOGGER.debug("numinicio {}, numfinal {}, cliente {}", rsng.getNumInicio(), rsng.getNumFinal(),
			rsng.getCliente());
	    }
	    if (rsng.getNumFinal() != null && rsng.getNumInicio() != null) {
		if (rsng.getNumInicio().compareTo(numInicio) == 0 && rsng.getNumFinal().compareTo(numFinal) == 0
			&& rsng.getCliente() == null) {
		    this.setFormaAsignacion("Serie");
		} else if (rsng.getCliente() == null) {
		    this.setFormaAsignacion("Rango");
		} else if (rsng.getCliente() != null) {
		    this.setFormaAsignacion("Específica");
		}
	    }
	} catch (Exception e) {
	    LOGGER.error("Error inesperado al buscar la forma de asignación" + e.getMessage());
	    MensajesFrontBean.addErrorMsg(MSG_ID, "Error inesperado");
	}
    }

    // //////////////////////////////////////GETTERS Y
    // SETTERS////////////////////////////////////////////////////////

    /**
     * Tipo de asignación.
     * 
     * @return the tipoAsignacion
     */
    public String getFormaAsignacion() {
	return formaAsignacion;
    }

    /**
     * Tipo de asignación.
     * 
     * @param formaAsignacion the formaAsignacion to set
     */
    public void setFormaAsignacion(String formaAsignacion) {
	this.formaAsignacion = formaAsignacion;
    }

    /**
     * Clave servicio del número buscado.
     * 
     * @return the claveServicio
     */
    public String getClaveServicio() {
	return claveServicio;
    }

    /**
     * Clave servicio del número buscado.
     * 
     * @param claveServicio the claveServicio to set
     */
    public void setClaveServicio(String claveServicio) {
	this.claveServicio = claveServicio;
    }

    /**
     * Listado de todas la claves de servicio.
     * 
     * @return the clavesServicio
     */
    public List<ClaveServicio> getClavesServicio() {
	return clavesServicio;
    }

    /**
     * Listado de todas la claves de servicio.
     * 
     * @param clavesServicio the clavesServicio to set
     */
    public void setClavesServicio(List<ClaveServicio> clavesServicio) {
	this.clavesServicio = clavesServicio;
    }

    /**
     * Sna del número buscado.
     * 
     * @return the sna
     */
    public String getSna() {
	return sna;
    }

    /**
     * Sna del número buscado.
     * 
     * @param sna the sna to set
     */
    public void setSna(String sna) {
	this.sna = sna;
    }

    /**
     * Ativación del botón buscar.
     * 
     * @return the submitAtivate
     */
    public boolean isSubmitActivate() {
	return submitActivate;
    }

    /**
     * Ativación del botón buscar.
     * 
     * @param submitActivate the submitAtivate to set
     */
    public void setSubmitActivate(boolean submitActivate) {
	this.submitActivate = submitActivate;
    }

    /**
     * Numeración asignada.
     * 
     * @return the numAsignada
     */
    public String getNumAsignada() {
	return numAsignada;
    }

    /**
     * Numeración asignada.
     * 
     * @param numAsignada the numAsignada to set
     */
    public void setNumAsignada(String numAsignada) {
	this.numAsignada = numAsignada;
    }

    /**
     * RangoSerie buscado.
     * 
     * @return the rangoSerieNng
     */
    public RangoSerieNng getRangoSerieNng() {
	return rangoSerieNng;
    }

    /**
     * RangoSerie buscado.
     * 
     * @param rangoSerieNng the rangoSerieNng to set
     */
    public void setRangoSerieNng(RangoSerieNng rangoSerieNng) {
	this.rangoSerieNng = rangoSerieNng;
    }

    /**
     * Numero no geográfico.
     * 
     * @return the numeroIntroducido
     */
    public String getNumeroIntroducido() {
	return numeroIntroducido;
    }

    /**
     * Numero no geográfico.
     * 
     * @param numeroIntroducido the numeroIntroducido to set
     */
    public void setNumeroIntroducido(String numeroIntroducido) {
	this.numeroIntroducido = numeroIntroducido;
    }

    /**
     * Proveedor de la numeración consultada.
     * 
     * @return the proveedorConsulta
     */
    public Proveedor getProveedorConsulta() {
	return proveedorConsulta;
    }

    /**
     * Proveedor de la numeración consultada.
     * 
     * @param proveedorConsulta the proveedorConsulta to set
     */
    public void setProveedorConsulta(Proveedor proveedorConsulta) {
	this.proveedorConsulta = proveedorConsulta;
    }

    /**
     * Status Rango de la numeración consultada.
     * 
     * @return the statusConsulta
     */
    public EstadoRango getStatusConsulta() {
	return statusConsulta;
    }

    /**
     * Status Rango de la numeración consultada.
     * 
     * @param statusConsulta the statusConsulta to set
     */
    public void setStatusConsulta(EstadoRango statusConsulta) {
	this.statusConsulta = statusConsulta;
    }

    /**
     * Flag de activación de tabla de búsqueda.
     * 
     * @return the tablaActivated
     */
    public boolean isTablaActivated() {
	return tablaActivated;
    }

    /**
     * Flag de activación de tabla de búsqueda.
     * 
     * @param tablaActivated the tablaActivated to set
     */
    public void setTablaActivated(boolean tablaActivated) {
	this.tablaActivated = tablaActivated;
    }

    /**
     * Código de la clave seleccionada en la lista desplegable.
     * 
     * @return the claveSelected
     */
    public Integer getClaveSelected() {
	return claveSelected;
    }

    /**
     * Código de la clave seleccionada en la lista desplegable.
     * 
     * @param claveSelected the claveSelected to set
     */
    public void setClaveSelected(Integer claveSelected) {
	this.claveSelected = claveSelected;
    }

}
