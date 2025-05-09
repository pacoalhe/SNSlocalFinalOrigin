package mx.ift.sns.dao.pst.implementation;

import java.util.List;

import javax.inject.Named;
import javax.persistence.TypedQuery;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.pst.ITipoRedDao;
import mx.ift.sns.modelo.pst.TipoRed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación de los métodos para base de datos de tipos de red.
 */
@Named
public class TipoRedDaoImpl extends BaseDAO<TipoRed> implements ITipoRedDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(TipoRedDaoImpl.class);

    @Override
    public List<TipoRed> findAllTiposRed() {
        String query = "SELECT tr FROM TipoRed tr where tr.cdg <> :idAmbas AND tr.cdg <> :desconocida "
                + "ORDER BY tr.descripcion";
        TypedQuery<TipoRed> consulta = getEntityManager().createQuery(query, TipoRed.class);
        consulta.setParameter("idAmbas", TipoRed.AMBAS);
        consulta.setParameter("desconocida", TipoRed.DESCONOCIDA);

        List<TipoRed> list = consulta.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Tipos de red encontrados: {}", list.size());
        }

        return list;
    }

    @Override
    public TipoRed getTipoRedById(String idTipoRed) {
        String query = "SELECT tr FROM TipoRed tr where tr.cdg = :idTipoRed";
        TypedQuery<TipoRed> consulta = getEntityManager().createQuery(query, TipoRed.class);
        consulta.setParameter("idTipoRed", idTipoRed);

        TipoRed resultado = consulta.getSingleResult();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Tipo de red encontrado: {}", resultado.getDescripcion());
        }

        return resultado;
    }

    @Override
    public List<TipoRed> findAllTiposRedValidos() {
        String query = "SELECT tr FROM TipoRed tr where tr.cdg <> :desconocida "
                + "ORDER BY tr.descripcion";
        TypedQuery<TipoRed> consulta = getEntityManager().createQuery(query, TipoRed.class);
        consulta.setParameter("desconocida", TipoRed.DESCONOCIDA);

        List<TipoRed> list = consulta.getResultList();

        return list;
    }

    @Override
    public List<TipoRed> findAllTiposRedValidos(TipoRed tipoRed) {
        String query = "";

        if (TipoRed.AMBAS.equals(tipoRed.getCdg())) {
            query = "SELECT tr FROM TipoRed tr where tr.cdg = :tipoRed ORDER BY tr.descripcion";
        } else {
            query = "SELECT tr FROM TipoRed tr "
                    + "where (tr.cdg = :tipoRed OR tr.cdg = :tipoRedAmbas) ORDER BY tr.descripcion";
        }

        TypedQuery<TipoRed> consulta = getEntityManager().createQuery(query, TipoRed.class);
        consulta.setParameter("tipoRed", tipoRed.getCdg());

        if (!TipoRed.AMBAS.equals(tipoRed.getCdg())) {
            consulta.setParameter("tipoRedAmbas", TipoRed.AMBAS);
        }

        List<TipoRed> list = consulta.getResultList();

        return list;
    }
}
