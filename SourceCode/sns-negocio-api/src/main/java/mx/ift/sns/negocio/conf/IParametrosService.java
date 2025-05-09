package mx.ift.sns.negocio.conf;

/**
 * Interfaz servicio de parametros.
 */
public interface IParametrosService {

    /**
     * Devuelve el valor de un parametro por su nombre.
     * @param name nombre
     * @return valor
     */
    String getParamByName(String name);

}
