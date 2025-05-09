package mx.ift.sns.dao.ng.implementation;

import java.util.List;

import javax.inject.Named;
import javax.persistence.TypedQuery;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.ng.IEstatusDao;
import mx.ift.sns.modelo.central.Estatus;

/**
 * Implementación de los métodos para base de datos de estatus de catalogo.
 */
@Named
public class EstatusDaoImpl extends BaseDAO<Estatus> implements IEstatusDao {
    @Override
    public List<Estatus> findAllEstatus() {
        String query = "SELECT est FROM Estatus est ORDER BY est.cdg";
        TypedQuery<Estatus> tQuery = getEntityManager().createQuery(query, Estatus.class);
        return tQuery.getResultList();
    }

    @Override
    public Estatus getEstatusById(String idEstatus) {
        return getEntityManager().find(Estatus.class, idEstatus);
    }

}
