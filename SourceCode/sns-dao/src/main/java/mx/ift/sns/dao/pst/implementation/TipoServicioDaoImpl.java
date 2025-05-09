package mx.ift.sns.dao.pst.implementation;

import java.util.List;

import javax.inject.Named;
import javax.persistence.Query;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.pst.ITipoServicioDao;
import mx.ift.sns.modelo.pst.TipoServicio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación de los métodos para base de datos de tipos de servicio.
 */
@Named
public class TipoServicioDaoImpl extends BaseDAO<TipoServicio> implements ITipoServicioDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(TipoServicioDaoImpl.class);

    @SuppressWarnings("unchecked")
    @Override
    public List<TipoServicio> findAllTiposServicio() {
        return getEntityManager().createQuery("SELECT ts FROM TipoServicio ts").getResultList();
    }

    @Override
    public TipoServicio getTipoServicioById(String id) throws Exception {
        String query = "SELECT ts FROM TipoServicio ts WHERE ts.cdg = :idTipoServicio";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("IdServicio: " + id + " Query: " + query);
        }

        Query consulta = getEntityManager().createQuery(query);
        consulta.setParameter("idTipoServicio", id);
        TipoServicio tipoServicio = (TipoServicio) consulta.getSingleResult();
        return tipoServicio;
    }
}
