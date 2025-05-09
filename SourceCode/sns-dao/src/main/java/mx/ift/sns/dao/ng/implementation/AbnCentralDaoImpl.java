package mx.ift.sns.dao.ng.implementation;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.ng.IAbnCentralDao;
import mx.ift.sns.modelo.abn.AbnCentral;
import mx.ift.sns.modelo.abn.AbnCentralPK;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación de los métodos para base de datos de centrales de áreas de numeración.
 */
@Named
public class AbnCentralDaoImpl extends BaseDAO<AbnCentral> implements IAbnCentralDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbnCentralDaoImpl.class);

    @SuppressWarnings("unchecked")
    @Override
    public List<AbnCentral> findAllCentralesAbn() {
        return getEntityManager().createQuery("SELECT a FROM AbnCentral a").getResultList();
    }

    @Override
    public AbnCentral getCentralAbnById(BigDecimal pCentralId) {
        return getEntityManager().find(AbnCentral.class, pCentralId);
    }

    @Override
    public AbnCentral findCentralByAbnNir(long idCentral, long numAbn) {

        String strQuery = "SELECT a FROM AbnCentral a where a.id.idCentral =:idCentral and a.id.idAbn =:numAbn";

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Ejecutando Query: " + strQuery);
        }

        AbnCentral abnCentral = null;

        try {
            Query nativeQuery = getEntityManager().createQuery(strQuery);
            nativeQuery.setParameter("idCentral", idCentral);
            nativeQuery.setParameter("numAbn", numAbn);

            abnCentral = (AbnCentral) nativeQuery.getSingleResult();

        } catch (NoResultException nre) {
            return null;
        }
        return abnCentral;
    }

    @Override
    public AbnCentral saveAbnCentral(AbnCentral abnCentral) {

        AbnCentralPK id = new AbnCentralPK();
        id.setIdAbn(abnCentral.getAbn().getCodigoAbn().longValue());
        id.setIdCentral(abnCentral.getCentral().getId().longValue());
        abnCentral.setId(id);

        return this.saveOrUpdate(abnCentral);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<AbnCentral> findAllAbnCentralesByAbn(BigDecimal codigo) {

        String strQuery = "SELECT a FROM AbnCentral a where a.abn.codigoAbn=:codigo";

        List<AbnCentral> listaAbnCentral = null;
        try {
            Query nativeQuery = getEntityManager().createQuery(strQuery);
            nativeQuery.setParameter("codigo", codigo);
            listaAbnCentral = (List<AbnCentral>) nativeQuery.getResultList();
        } catch (NoResultException nre) {
            return null;
        }
        return listaAbnCentral;
    }

}
