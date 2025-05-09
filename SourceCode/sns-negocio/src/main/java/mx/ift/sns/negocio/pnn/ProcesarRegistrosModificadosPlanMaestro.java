package mx.ift.sns.negocio.pnn;

import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.ift.sns.modelo.pnn.PlanMaestroDetalle;
import mx.ift.sns.modelo.pnn.PlanMaestroDetallePK;

/**
 * @author jparanda
 *
 */
public class ProcesarRegistrosModificadosPlanMaestro implements Runnable {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcesarRegistrosModificadosPlanMaestro.class);

    private String[] valores;

    public ProcesarRegistrosModificadosPlanMaestro(String[] vals) {

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

	    Integer ido = Integer.parseInt(valores[0]); // IDO
	    Long numeroInicial = Long.parseLong(valores[1]); // NO.INICIAL
	    Long numeroFinal = Long.parseLong(valores[2]); // NO.FINAL
	    String tipoServicio = String.valueOf(valores[3].charAt(0)); // TIPO SERVICIO
	    String mpp = String.valueOf(valores[4].charAt(0)); // MPP
	    Integer ida = Integer.parseInt(valores[5]); // IDA
	    Integer areaServicio = Integer.parseInt(valores[6]);

	    if (StringUtils.isNotEmpty(numeroInicial.toString()) && StringUtils.isNotEmpty(numeroFinal.toString())) {
		PlanMaestroDetalle num = new PlanMaestroDetalle();

		PlanMaestroDetallePK pk = new PlanMaestroDetallePK();

		pk.setNumeroInicial(numeroInicial);
		pk.setNumeroFinal(numeroFinal);

		num.setIdo(ido);
		num.setId(pk);
		num.setTipoServicio(tipoServicio.charAt(0));
		num.setMpp(mpp.charAt(0));
		num.setIda(ida);
		num.setAreaServicio(areaServicio);

		try {
		    PlanMaestroDao.update(num);
		} catch (SQLException e) {
		    LOGGER.info("**********************************Error se cierra conexión PlanMaestroDAO.closed();");
		    e.printStackTrace();
//					PortadosDAO.closed();
		}
//                PortadosDAO.closed();
	    } else {
		LOGGER.error("numberfrom nulo en PlanMaestroDetalle.");
	    }
	}

    }

}