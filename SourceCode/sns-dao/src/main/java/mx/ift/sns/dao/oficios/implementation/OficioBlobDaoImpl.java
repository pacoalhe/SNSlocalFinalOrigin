package mx.ift.sns.dao.oficios.implementation;

import java.math.BigDecimal;

import javax.inject.Named;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.oficios.IOficioBlobDao;
import mx.ift.sns.modelo.oficios.OficioBlob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación de los métodos para base de datos para la generación de Oficios Blob.
 */
@Named
public class OficioBlobDaoImpl extends BaseDAO<OficioBlob> implements IOficioBlobDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(OficioBlobDaoImpl.class);

    @Override
    public OficioBlob getOficioBlob(BigDecimal pOficioBlobId) {
        OficioBlob oficioBlob = getEntityManager().find(OficioBlob.class, pOficioBlobId);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Documento Oficio encontrado. Id: {}", pOficioBlobId);
        }
        return oficioBlob;
    }
}
