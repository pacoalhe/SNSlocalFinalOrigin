package mx.ift.sns.negocio.pnn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jparanda
 *
 */
public class ProcesarRegistrosElimindosPlanMaestro implements Runnable {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcesarRegistrosElimindosPlanMaestro.class);

    private String[] valores;

    public ProcesarRegistrosElimindosPlanMaestro(String[] vals) {

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

	if (valores.length >= 1) {

	    Integer ido = Integer.parseInt(valores[0]);
	    Long numeroInicial = Long.parseLong(valores[1]);
	    Long numeroFinal = Long.parseLong(valores[2]);
	    String tipoServicio = String.valueOf(valores[3].charAt(0));
	    String mpp = String.valueOf(valores[4].charAt(0));
	    Integer ida = Integer.parseInt(valores[5]);
	    Integer areaServicio = Integer.parseInt(valores[6]);

	    try {
		CanceladosPlanMaestroDao.delete(numeroInicial, numeroFinal);
	    } catch (Exception e) {
		LOGGER.error("error", e);
	    }

	}

    }
}