package mx.ift.sns.dao.pnn.implementation;

import java.util.List;

import javax.inject.Named;
import javax.persistence.Query;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.pnn.ITipoPlanDAO;
import mx.ift.sns.modelo.pnn.TipoPlan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación de los métodos para base de datos de tipos de red.
 */
@Named
public class TipoPlanDAOImpl extends BaseDAO<TipoPlan> implements ITipoPlanDAO {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(TipoPlanDAOImpl.class);

    @Override
    public TipoPlan getTipoPlanById(String id) {
        String query = "SELECT t FROM TipoPlan t where t.id = :id";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Query: " + query);
        }

        Query consulta = getEntityManager().createQuery(query);
        consulta.setParameter("id", id);
        TipoPlan resultado = (TipoPlan) consulta.getSingleResult();
        return resultado;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<TipoPlan> findAll() {
        return (List<TipoPlan>) getEntityManager().createQuery("SELECT t FROM TipoPlan t").getResultList();
    }

}
