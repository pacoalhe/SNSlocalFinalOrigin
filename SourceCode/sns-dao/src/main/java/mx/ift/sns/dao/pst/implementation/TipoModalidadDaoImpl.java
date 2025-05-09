package mx.ift.sns.dao.pst.implementation;

import java.util.List;

import javax.inject.Named;
import javax.persistence.TypedQuery;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.pst.ITipoModalidadDao;
import mx.ift.sns.modelo.pst.TipoModalidad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación de los métodos para base de datos de tipos de modalidad.
 */
@Named
public class TipoModalidadDaoImpl extends BaseDAO<TipoModalidad> implements ITipoModalidadDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(TipoModalidadDaoImpl.class);

    @Override
    public List<TipoModalidad> findAllTiposModalidad() {
        String query = "SELECT tm FROM TipoModalidad tm ORDER BY tm.descripcion";
        TypedQuery<TipoModalidad> consulta = getEntityManager().createQuery(query, TipoModalidad.class);

        List<TipoModalidad> list = consulta.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Tipos de modalidad encontrados: " + list.size());
        }

        return list;
    }

    @Override
    public TipoModalidad getTipoModalidadById(String idTipoModalidad) {
        String query = "SELECT tm FROM TipoModalidad tm WHERE tm.cdg = :idTipoModalidad";
        TypedQuery<TipoModalidad> consulta = getEntityManager().createQuery(query, TipoModalidad.class);
        consulta.setParameter("idTipoModalidad", idTipoModalidad);

        TipoModalidad resultado = consulta.getSingleResult();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Encontrada modalidad '{}' para id {}", resultado.getDescripcion(), idTipoModalidad);
        }

        return resultado;
    }
}
