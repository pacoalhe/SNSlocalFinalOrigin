/**
 * 
 */
package mx.ift.sns.negocio.port;

import java.math.BigDecimal;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.ift.sns.modelo.port.NumeroCancelado;



/**
 * @author jparanda
 *
 */
public class ProcesarRegistrosCancelados implements Runnable {

	/** Logger de la clase. */
	private static final Logger LOGGER = LoggerFactory.getLogger(ProcesarRegistrosCancelados.class);

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
				CanceladosDAO.delete(numberFrom);
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
				CanceladosDAO.insert(num);
			} catch (SQLException e) {
				e.printStackTrace();
//				CanceladosDAO.closed();
			}
//			CanceladosDAO.closed();
		}

	}

}
