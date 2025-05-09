package mx.ift.sns.dao.ng.implementation;

import java.util.List;

import javax.inject.Named;
import javax.persistence.TypedQuery;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.ng.IEstadoRangoDao;
import mx.ift.sns.modelo.series.EstadoRango;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación de los métodos para base de datos de estados de rango.
 */
@Named
public class EstadoRangoDaoImpl extends BaseDAO<EstadoRango> implements IEstadoRangoDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(EstadoRangoDaoImpl.class);

    @Override
    public List<EstadoRango> findAllEstadosRango() {
        String query = "SELECT e FROM EstadoRango e ORDER BY e.codigo";
        TypedQuery<EstadoRango> tQuery = getEntityManager().createQuery(query, EstadoRango.class);
        return tQuery.getResultList();
    }

    @Override
    public EstadoRango getEstadoRangoByCodigo(String codigo) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Buscando Estatus Rango por código {}", codigo);
        }

        String query = "SELECT e FROM EstadoRango e WHERE e.codigo = :codigo";
        TypedQuery<EstadoRango> tQuery = getEntityManager().createQuery(query, EstadoRango.class);
        tQuery.setParameter("codigo", codigo);

        EstadoRango estatus = tQuery.getSingleResult();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Estatus Rango contrado: {}", estatus.getDescripcion());
        }

        return estatus;
    }
}
