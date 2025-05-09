package mx.ift.sns.dao.ng;

import javax.persistence.NoResultException;

/**
 * Interfaz del DAO de parametros.
 */
public interface IParametroDao {

    /**
     * Devuelve el valor de un parametro por su nombre.
     * @param name nombre
     * @return valor
     */
    String getParamByName(String name) throws NoResultException;

}
