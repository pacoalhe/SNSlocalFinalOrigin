package mx.ift.sns.dao.nng.implementation;

import javax.inject.Named;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.nng.ISolicitudLineasActivasNngDao;
import mx.ift.sns.modelo.nng.SolicitudLineasActivasNng;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase que implementa la interfaz de solicitudes de lineas activas.
 */
@Named
public class SolicitudLineasActivasNngDaoImpl extends BaseDAO<SolicitudLineasActivasNng>
        implements ISolicitudLineasActivasNngDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SolicitudLineasActivasNngDaoImpl.class);

    @Override
    public SolicitudLineasActivasNng saveSolicitudLineasActivas(SolicitudLineasActivasNng solicitud) {
        LOGGER.debug("{}", solicitud.getId());
        return getEntityManager().merge(solicitud);
    }

}
