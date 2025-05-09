package mx.ift.sns.negocio.ng.arrendamientos;

import java.io.IOException;

import mx.ift.sns.negocio.ng.model.ResultadoValidacionCSV;
import mx.ift.sns.negocio.utils.csv.ValidadorArchivoCSV;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Validacion Archivo Numeracion Arrendador. El formato de la cabecera es: numberfrom,numberto,Arrendatador,Arrendatario
 */
public class ValidadorArchivoArrendador extends ValidadorArchivoCSV {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidadorArchivoArrendador.class);

    /** Tamaño de la cabecera de los archivos. */
    private static final int TAM_CABECERA = 4;

    /** Campo number from. */
    public static final int CAMPO_NUMBER_FROM = 0;

    /** Campo number to. */
    public static final int CAMPO_NUMBER_TO = 1;

    /** Campo ido arrendador. */
    public static final int CAMPO_IDO_ARRENDADOR = 3;

    /** Campo ido arrendatario. */
    public static final int CAMPO_IDO_ARRENDATARIO = 2;

    @Override
    public boolean checkHeader(String[] headers) throws IOException {

        /* numberfrom,numberto,Arrendatario,Arrendador */
        boolean res = false;

        if (headers.length == TAM_CABECERA) {
            String cabNumberFrom = headers[CAMPO_NUMBER_FROM];
            String cabNumberTo = headers[CAMPO_NUMBER_TO];
            String cabArrendatario = headers[CAMPO_IDO_ARRENDATARIO];
            String cabArrendador = headers[CAMPO_IDO_ARRENDADOR];

            res = StringUtils.equalsIgnoreCase(cabNumberFrom, "numberfrom")
                    && StringUtils.equalsIgnoreCase(cabNumberTo, "numberto")
                    && StringUtils.equalsIgnoreCase(cabArrendatario, "arrendatario")
                    && StringUtils.equalsIgnoreCase(cabArrendador, "arrendador");
        }

        LOGGER.debug("cabecera correcta = {}", res);

        return res;
    }

    @Override
    public void checkFila(String[] valores) {
        if ((valores == null)
                || ((valores != null) && (valores.length != TAM_CABECERA))) {
            setHayError(true);
            setTerminar(true);
            getRes().setError(ResultadoValidacionCSV.ERROR_FORMATO_FICHERO);
        }
    }
}
