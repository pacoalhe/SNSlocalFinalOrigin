package mx.ift.sns.negocio.port;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Future;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.ift.sns.modelo.port.NumeroPortado;

import javax.annotation.PostConstruct;
import javax.ejb.*;


/**
 * @author jparanda
 *
 */

@Stateless
public class ProcesarRegistrosPortados {

    @EJB
    private PortadosDAO portadosDAO;

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcesarRegistrosPortados.class);

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Future<Boolean> procesarAsync(String[] valores) {
        try {
            checkFila(valores);
            return new AsyncResult<>(true);
        } catch (Exception e) {
            // Maneja log de error aquí
            return new AsyncResult<>(false);
        }
    }


    /*
    @Asynchronous
    public void procesarAsync(String[] valores) {
        try {
            checkFila(valores);
        } catch (Exception e) {
            LOGGER.error("Error procesando registro portado: ", e);
        }
    }

     */

    public ProcesarRegistrosPortados() {
        // Obligatorio: constructor público sin parámetros
    }

/*
    private String[] valores;

    public ProcesarRegistrosPortados(String[] vals) {

        this.valores=vals;
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

    public void checkFila(String[] valores) {
        // portId,portType,action,numberFrom,numberTo,isMpp,rida,rcr,dida,dcr,actionDate
        if (portadosDAO == null) {
            LOGGER.error("portadosDAO no ha sido inyectado correctamente");
            return;
        }

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

            if (StringUtils.isNotEmpty(numberFrom)) {
                NumeroPortado num = new NumeroPortado();

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


                try {
                    portadosDAO.update(num);
                    //PortadosDAO.update(num);
					LOGGER.info("<---PorId procesado:"+portId);
                } catch (SQLException e) {
                    LOGGER.info("**********************************Error se cierra conexión PortadosDAO.closed();");
                    e.printStackTrace();
//					PortadosDAO.closed();
                }
//                PortadosDAO.closed();
            } else {
                LOGGER.error("numberfrom nulo en Portados.");
            }
        }

    }

    @PostConstruct
    public void init() {
        LOGGER.info("ProcesarRegistrosCancelados inicializado correctamente");
        LOGGER.info("PortadosDAO inyectado: {}", portadosDAO != null);
    }

}
