package mx.ift.sns.dao.oficios.implementation;

import java.util.List;

import javax.inject.Named;
import javax.persistence.TypedQuery;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.oficios.ITipoDestinatarioDao;
import mx.ift.sns.modelo.oficios.TipoDestinatario;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación de los métodos para base de datos de tipos de destinatario.
 */
@Named
public class TipoDestinatarioDaoImpl extends BaseDAO<TipoDestinatario> implements ITipoDestinatarioDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(TipoDestinatarioDaoImpl.class);

    @Override
    public List<TipoDestinatario> findAllTiposDestinatario() {
        String query = "SELECT td FROM TipoDestinatario td ORDER BY td.cdg";
        TypedQuery<TipoDestinatario> tQuery = getEntityManager().createQuery(query, TipoDestinatario.class);

        List<TipoDestinatario> listaDestinatarios = tQuery.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Destinatarios de Oficio encontrados: " + listaDestinatarios.size());
        }

        return listaDestinatarios;
    }

    @Override
    public TipoDestinatario getTipoDestinatarioByCdg(String pCdgDestinatario) {

        String query = "SELECT td FROM TipoDestinatario td WHERE td.cdg = :cdgDestinatario";
        TypedQuery<TipoDestinatario> tQuery = getEntityManager().createQuery(query, TipoDestinatario.class);
        tQuery.setParameter("cdgDestinatario", pCdgDestinatario);

        TipoDestinatario destinatario = tQuery.getSingleResult();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Destinatario encontrado: {}", destinatario.getDescripcion());
        }

        return destinatario;
    }

}
