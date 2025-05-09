package mx.ift.sns.dao.ng.implementation;

import javax.inject.Named;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.ng.ISolicitudLineasActivasDao;
import mx.ift.sns.modelo.ng.SolicitudLineasActivas;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase que implementa la interfaz de solicitudes de lineas activas.
 */
@Named
public class SolicitudLineasActivasDaoImpl extends BaseDAO<SolicitudLineasActivas>
        implements ISolicitudLineasActivasDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SolicitudLineasActivasDaoImpl.class);

    @Override
    public SolicitudLineasActivas saveSolicitudLineasActivas(SolicitudLineasActivas solicitud) {
        LOGGER.debug("{}", solicitud.getId());
        return getEntityManager().merge(solicitud);
    }

}
