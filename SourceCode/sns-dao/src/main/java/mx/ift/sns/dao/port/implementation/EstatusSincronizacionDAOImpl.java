package mx.ift.sns.dao.port.implementation;

import javax.inject.Named;
import javax.persistence.TypedQuery;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.port.IEstatusSincronizacionDao;
import mx.ift.sns.modelo.port.EstatusSincronizacion;

/**
 * Implementación del DAO de Status de Portabilidad.
 */
@Named
public class EstatusSincronizacionDAOImpl extends BaseDAO<EstatusSincronizacion> implements IEstatusSincronizacionDao {

    @Override
    public EstatusSincronizacion get() {
        // Query
        String query = "SELECT e FROM EstatusSincronizacion e";

        TypedQuery<EstatusSincronizacion> tQuery =
                getEntityManager().createQuery(query, EstatusSincronizacion.class);

        return tQuery.getSingleResult();
    }
}
