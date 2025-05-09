package mx.ift.sns.dao.ng.implementation;

import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.ng.IParametroDao;
import mx.ift.sns.modelo.config.Parametro;

/**
 * Implementación de los métodos para base de datos de Parámetros.
 */
@Named
public class ParametroDaoImpl extends BaseDAO<Parametro> implements IParametroDao {

    /** Logger de la clase. */
    // private static final Logger LOGGER = LoggerFactory.getLogger(ParametroDaoImpl.class);

    @Override
    public String getParamByName(String name) throws NoResultException {
        String query = "SELECT p.valor FROM Parametro p where p.nombre = :name";
        TypedQuery<String> tQuery = getEntityManager().createQuery(query, String.class);
        tQuery.setParameter("name", name);
        return tQuery.getSingleResult();
    }

}
