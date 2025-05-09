package mx.ift.sns.negocio;


/**
 * Interfaz de métodos de configuración.
 */
public interface IConfiguracionFacade {

    /**
     * Devuelve el valor de un parametro por su nombre.
     * @param name nombre
     * @return valor
     */
    String getParamByName(String name);
}
