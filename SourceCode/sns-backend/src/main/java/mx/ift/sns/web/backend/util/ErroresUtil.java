package mx.ift.sns.web.backend.util;

import java.util.List;

import mx.ift.sns.web.backend.common.MensajesBean;

/**
 * Clase encargada de obtener los mensajes asociados a los errores recibidos y setearlos al componente JSF recibido por
 * parámetro.
 * @author X23016PE
 */
public final class ErroresUtil {

    /** Constructor vacío. */
    private ErroresUtil() {
    }

    /**
     * Método encargado de obtener el texto asociado a los ids recibidos y de setearlo al componente JSF cuyo
     * identificador se corresponde al recibido como parámetro.
     * @param errores de los que obtener el texto
     * @param id componente al que setear los errores.
     */
    public static void generarErrores(List<String> errores, String id) {
        StringBuffer mensajes = new StringBuffer();

        for (String error : errores) {
            mensajes.append(MensajesBean.getTextoResource(error)).append("<br>");
        }

        if (mensajes.length() > 0) {
            MensajesBean.addErrorMsg(id, mensajes.toString(), "");
        }
    }
}
