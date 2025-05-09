package mx.ift.sns.negocio.port;

import java.math.BigDecimal;

import mx.ift.sns.modelo.port.NumeroPortado;
import mx.ift.sns.negocio.utils.csv.ValidadorArchivoCSV;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Importa los numeros portados.
 */
public class ImportPortadosCSV extends ValidadorArchivoCSV {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ImportPortadosCSV.class);

    /** Servicio de portabilidad. */
    private IPortabilidadService portabilidadService;

    /**
     * Valida una fila del archivo.
     * @param valores d
     */
    @Override
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

                portabilidadService.update(num);
            } else {
                LOGGER.error("numberfrom nulo en importPortados.");
            }
        }
    }

    /**
     * @param portabilidadService the portabilidadService to set
     */
    public void setPortabilidadService(IPortabilidadService portabilidadService) {
        this.portabilidadService = portabilidadService;
    }
}
