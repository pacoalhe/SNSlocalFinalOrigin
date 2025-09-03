/**
 * 
 */
package mx.ift.sns.negocio.port;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.Future;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.ift.sns.modelo.port.NumeroCancelado;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import javax.inject.Inject;


/**
 * @author jparanda
 *
 */

@Stateless
public class ProcesarRegistrosCancelados {
	/**
	 * Logger de la clase.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ProcesarRegistrosCancelados.class);

	@EJB
	private CanceladosDAO canceladosDAO;

	@Asynchronous
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Future<Boolean> procesarAsync(String[] valores) {
		try {
			checkFila(valores);
			return new AsyncResult<>(true);
		} catch (Exception e) {
			// Maneja log de error aquí
			LoggerFactory.getLogger(getClass()).error("Error procesando cancelado", e);
			return new AsyncResult<>(false);
		}
	}

	/*
	@Asynchronous
	public void procesarAsync(String[] valores) {
		try {
			checkFila(valores);
		} catch (Exception e) {
			LOGGER.error("Error procesando registro cancelado: ", e);
		}
	}

	 */

	public ProcesarRegistrosCancelados() {
		// Obligatorio: constructor público sin parámetros
	}

/*
	private String[] valores;

	public ProcesarRegistrosCancelados(String[] vals) {
		this.valores = vals;
	}

	@Override
	public void run() {
		checkFila(valores);
	}

	public String[] getValores() {
		return valores;
	}

	public void setValores(String[] valores) {
		this.valores = valores;
	}

	 */

	/**
	 * FJAH 24052025 Refactorización del metodo Chefila para el manejo
	 * de errores del nullpointer, con Métodos auxiliares para operaciones comunes
	 */
	public void checkFila(String[] valores) {
		if (canceladosDAO == null) {
			LOGGER.error("canceladosDAO no ha sido inyectado correctamente");
			return;
		}

		// Validación inicial robusta
		if (valores == null) {
			LOGGER.error("El array de valores es nulo");
			return;
		}

		if (valores.length < 13) {
			LOGGER.error("Fila incompleta. Se esperaban 13 columnas, recibidas: {}", valores.length);
			return;
		}

		String numberFrom = null;
		try {
			// 1. Extracción segura del número
			numberFrom = StringUtils.trimToEmpty(valores[3]);
			if (StringUtils.isBlank(numberFrom)) {
				LOGGER.warn("Número de origen vacío. Fila omitida.");
				return;
			}

			// 2. Eliminación segura del número existente
			/*
			try {
				if (canceladosDAO != null) {
					canceladosDAO.delete(numberFrom);
				} else {
					LOGGER.error("canceladosDAO no ha sido inyectado correctamente");
					return;
				}
			} catch (Exception e) {
				LOGGER.error("Error al eliminar número {}: {}", numberFrom, e.toString());
				// Continuamos a pesar del error (puede que el número no exista)
			}
			 */

			// 3. Construcción segura del objeto NumeroCancelado
			NumeroCancelado num = new NumeroCancelado();

			// Campos básicos
			num.setPortId(StringUtils.trimToEmpty(valores[0]));
			num.setPortType(StringUtils.trimToEmpty(valores[1]));
			num.setAction(StringUtils.trimToEmpty(valores[2]));
			num.setNumberFrom(numberFrom);
			num.setNumberTo(StringUtils.trimToEmpty(valores[4]));
			num.setIsMpp(StringUtils.defaultIfBlank(StringUtils.trim(valores[5]), "N"));

			// Campos numéricos con manejo seguro de null
			num.setRida(parseBigDecimalSafe(valores[6]));
			num.setRcr(parseBigDecimalSafe(valores[7]));
			num.setDida("null".equalsIgnoreCase(StringUtils.trimToNull(valores[8])) ? null : parseBigDecimalSafe(valores[8], true));
			num.setDcr("null".equalsIgnoreCase(StringUtils.trimToNull(valores[9])) ? null : parseBigDecimalSafe(valores[9], true));
			num.setAssigneeIda(parseBigDecimalSafe(valores[11]));
			num.setAssigneeCr(parseBigDecimalSafe(valores[12]));

			// Manejo especial para la fecha
			String dateStr = StringUtils.trimToNull(valores[10]);
			try {
				if (dateStr != null) {
					// Formato esperado: yyyyMMddHHmmss (ej: 20250523020500)
					SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyyMMddHHmmss");
					Date parsedDate = sourceFormat.parse(dateStr);
					num.setActionDate(new java.sql.Timestamp(parsedDate.getTime()));
				} else {
					num.setActionDate(new java.sql.Timestamp(System.currentTimeMillis()));
				}
			} catch (ParseException e) {
				LOGGER.warn("Formato de fecha inválido: '{}'. Usando fecha actual.", dateStr);
				num.setActionDate(new java.sql.Timestamp(System.currentTimeMillis()));
			}

			// 4. Inserción segura
			try {
				if (canceladosDAO != null) {
					canceladosDAO.insert(num);
					LOGGER.debug("Número {} procesado correctamente", numberFrom);
				} else {
					LOGGER.error("canceladosDAO es nulo. No se pudo insertar el número {}", numberFrom);
				}
			} catch (SQLException e) {
				LOGGER.error("Error al insertar número {}: {}", numberFrom, e.toString());
			}

		} catch (Exception e) {
			LOGGER.error("Error crítico procesando número {}. Fila: {}. Error: {}",
					numberFrom, Arrays.toString(valores), e.toString());
		}
	}

	// Métodos auxiliares mejorados
	private BigDecimal parseBigDecimalSafe(String value) {
		return parseBigDecimalSafe(value, false);
	}

	private BigDecimal parseBigDecimalSafe(String value, boolean allowNull) {
		try {
			String trimmed = StringUtils.trimToNull(value);
			if (trimmed == null || "null".equalsIgnoreCase(trimmed)) {
				return allowNull ? null : BigDecimal.ZERO;
			}
			return new BigDecimal(trimmed);
		} catch (Exception e) {
			LOGGER.warn("Valor numérico inválido: '{}'. Usando {}.", value, allowNull ? "null" : "cero");
			return allowNull ? null : BigDecimal.ZERO;
		}
	}

	@PostConstruct
	public void init() {
		LOGGER.info("ProcesarRegistrosCancelados inicializado correctamente");
		LOGGER.info("CanceladosDAO inyectado: {}", canceladosDAO != null);
	}

}

	/*
	public void checkFila(String[] valores) {

		// portId,portType,action,numberFrom,numberTo,isMpp,rida,rcr,dida,dcr,actionDate
		if (valores.length >= 1) {

			String portId = StringUtils.trim(valores[0]);
			String portType = StringUtils.trim(valores[1]);
			String action = StringUtils.trim(valores[2]);
			String numberFrom = StringUtils.trim(valores[3]);
			String numberTo = StringUtils.trim(valores[4]);
			String isMpp = StringUtils.trim(valores[5]);
			String rida = StringUtils.trim(valores[6]);
			String rcr = StringUtils.trim(valores[7]);
			String dida = StringUtils.trim(valores[8]);
			String dcr = StringUtils.trim(valores[9]);
			String actionDate = StringUtils.trim(valores[10]);
			String assigneeIDA = StringUtils.trim(valores[11]);
			String assigneeCR = StringUtils.trim(valores[12]);

			try {
				canceladosDAO.delete(numberFrom);
				//CanceladosDAO.delete(numberFrom);
			} catch (Exception e) {
				LOGGER.error("error", e);
			}

			NumeroCancelado num = new NumeroCancelado();
			num.setPortId(portId);
			num.setPortType(portType);
			num.setAction(action);
			num.setNumberFrom(numberFrom);
			num.setNumberTo(numberTo);
			num.setIsMpp(isMpp);
			num.setRida(new BigDecimal(rida));
			num.setRcr(new BigDecimal(rcr));
			num.setDida(!dida.equals("null") ? new BigDecimal(dida) : null);
			num.setDcr(!dcr.equals("null") ? new BigDecimal(dcr) : null);
			num.setActionDate(actionDate);
			num.setAssigneeCr(new BigDecimal(assigneeCR));
			num.setAssigneeIda(new BigDecimal(assigneeIDA));

			try {
				canceladosDAO.insert(num);
				//CanceladosDAO.insert(num);
			} catch (SQLException e) {
				e.printStackTrace();
//				CanceladosDAO.closed();
			}
//			CanceladosDAO.closed();
		}

	}

	 */

