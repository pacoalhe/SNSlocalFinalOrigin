package mx.ift.sns.dao.ng.implementation;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Named;
import javax.persistence.TypedQuery;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.ng.INirConsolidarDao;
import mx.ift.sns.modelo.ng.NirConsolidar;
import mx.ift.sns.modelo.series.Nir;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación de los métodos para base de datos de Identificadores de Región.
 */
@Named
public class NirConsolidarDaoImpl extends BaseDAO<Nir> implements INirConsolidarDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(NirConsolidarDaoImpl.class);

    @Override
    public List<NirConsolidar> findNirConsolidarById(BigDecimal pId) {
        String query = "SELECT n FROM NirConsolidar n where n.abnConsolidar.id = :idAbnConsolidacion";
        TypedQuery<NirConsolidar> tQuery = getEntityManager().createQuery(query, NirConsolidar.class);
        tQuery.setParameter("idAbnConsolidacion", pId);

        List<NirConsolidar> nirConso = tQuery.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Se han encontrado {} poblaciones para el ABN: {}",
                    nirConso.size(), pId);
        }

        return nirConso;
    }
}
