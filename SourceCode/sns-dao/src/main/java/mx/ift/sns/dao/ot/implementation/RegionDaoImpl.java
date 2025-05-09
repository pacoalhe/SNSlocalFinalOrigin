package mx.ift.sns.dao.ot.implementation;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Named;
import javax.persistence.Query;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.ot.IRegionDao;
import mx.ift.sns.modelo.ot.Region;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación DAO de Region.
 * @author X36155QU
 */
@Named
public class RegionDaoImpl extends BaseDAO<Region> implements IRegionDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(RegionDaoImpl.class);

    @Override
    public Region getRegionById(BigDecimal id) {
        LOGGER.debug("");
        // Query
        String query = "SELECT r FROM Region r WHERE r.idRegion = :id";

        Query nativeQuery = getEntityManager().createQuery(query);
        nativeQuery.setParameter("id", id);

        return (Region) nativeQuery.getSingleResult();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Region> findAllRegiones() {
        LOGGER.debug("");
        // Query
        String query = "SELECT r FROM Region r";
        return getEntityManager().createQuery(query).getResultList();
    }
}
