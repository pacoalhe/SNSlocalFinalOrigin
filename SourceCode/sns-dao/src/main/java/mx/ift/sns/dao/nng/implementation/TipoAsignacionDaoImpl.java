package mx.ift.sns.dao.nng.implementation;

import java.util.List;

import javax.persistence.TypedQuery;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.nng.ITipoAsignacionDao;
import mx.ift.sns.modelo.nng.TipoAsignacion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementacion del DAO de TipoAsignacion.
 * @author X36155QU
 */
public class TipoAsignacionDaoImpl extends BaseDAO<TipoAsignacion> implements ITipoAsignacionDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(TipoAsignacionDaoImpl.class);

    @Override
    public TipoAsignacion getTipoAsignacionById(String cdg) {

        String query = "SELECT ta FROM TipoAsignacion ta WHERE ta.cdg = :cdg";
        TypedQuery<TipoAsignacion> consulta = getEntityManager().createQuery(query, TipoAsignacion.class);
        consulta.setParameter("cdg", cdg);

        TipoAsignacion resultado = consulta.getSingleResult();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Encontrado tipo de asignacion '{}' para id {}", resultado.getDescripcion(), cdg);
        }

        return resultado;
    }

    @Override
    public List<TipoAsignacion> findAllTipoAsignacion() {

        String query = "SELECT ta FROM TipoAsignacion ta";
        TypedQuery<TipoAsignacion> consulta = getEntityManager().createQuery(query, TipoAsignacion.class);

        List<TipoAsignacion> lista = consulta.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Encontrado {} tipos de asignacion", lista.size());
        }

        return lista;
    }

}
