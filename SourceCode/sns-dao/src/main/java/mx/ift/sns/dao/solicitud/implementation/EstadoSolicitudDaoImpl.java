package mx.ift.sns.dao.solicitud.implementation;

import java.util.List;

import javax.inject.Named;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.solicitud.IEstadoSolicitudDao;
import mx.ift.sns.modelo.solicitud.EstadoSolicitud;

/**
 * Implementación de los métodos para base de datos de estados de solicitud.
 */
@Named
public class EstadoSolicitudDaoImpl extends BaseDAO<EstadoSolicitud> implements IEstadoSolicitudDao {

    @SuppressWarnings("unchecked")
    @Override
    public List<EstadoSolicitud> findAllEstadosSolicitud() {
        return getEntityManager().createQuery("SELECT es FROM EstadoSolicitud es ORDER BY es.descripcion")
                .getResultList();
    }

    @Override
    public EstadoSolicitud getEstadoSolicitudById(String pIdSolicitud) {
        return getEntityManager().find(EstadoSolicitud.class, pIdSolicitud);
    }
}
