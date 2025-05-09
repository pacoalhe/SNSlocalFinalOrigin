package mx.ift.sns.dao.ng.implementation;

import java.util.List;

import javax.inject.Named;
import javax.persistence.TypedQuery;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.ng.IEstadoAbnDao;
import mx.ift.sns.modelo.abn.EstadoAbn;

/**
 * Implementación de los métodos para base de datos de estatus de área de numeración.
 */
@Named
public class EstadoAbnDaoImpl extends BaseDAO<EstadoAbn> implements IEstadoAbnDao {

    @Override
    public List<EstadoAbn> findAllEstadosAbn() {
        String query = "SELECT a FROM EstadoAbn a ORDER BY a.codigo";
        TypedQuery<EstadoAbn> tQuery = getEntityManager().createQuery(query, EstadoAbn.class);
        return tQuery.getResultList();
    }

    @Override
    public EstadoAbn getEstadoAbnByCodigo(String pCodigo) {
        EstadoAbn estadoAbn = getEntityManager().find(EstadoAbn.class, pCodigo);
        return estadoAbn;
    }
}
