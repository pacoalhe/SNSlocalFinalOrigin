package mx.ift.sns.dao.ng.implementation;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Named;
import javax.persistence.TypedQuery;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.ng.IPoblacionConsolidarDao;
import mx.ift.sns.modelo.ng.PoblacionConsolidar;
import mx.ift.sns.modelo.series.Nir;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación de los métodos para base de datos de Identificadores de Región.
 */
@Named
public class PoblacionConsolidarDaoImpl extends BaseDAO<Nir> implements IPoblacionConsolidarDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(PoblacionConsolidarDaoImpl.class);

    @Override
    public List<PoblacionConsolidar> findPoblacionConsolidarById(BigDecimal pId) {
        String query = "SELECT p FROM PoblacionConsolidar p where p.abnConsolidar.id = :idAbnConsolidacion";
        TypedQuery<PoblacionConsolidar> tQuery = getEntityManager().createQuery(query, PoblacionConsolidar.class);
        tQuery.setParameter("idAbnConsolidacion", pId);

        List<PoblacionConsolidar> poblacionesConso = tQuery.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Se han encontrado {} poblaciones para el ABN: {}",
                    poblacionesConso.size(), pId);
        }

        return poblacionesConso;
    }

}
