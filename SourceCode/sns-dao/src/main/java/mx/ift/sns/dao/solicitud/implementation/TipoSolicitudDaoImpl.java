package mx.ift.sns.dao.solicitud.implementation;

import java.util.List;

import javax.inject.Named;
import javax.persistence.TypedQuery;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.solicitud.ITipoSolicitudDao;
import mx.ift.sns.modelo.solicitud.TipoSolicitud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación de los métodos para base de datos de tipos de solicitud.
 */
@Named
public class TipoSolicitudDaoImpl extends BaseDAO<TipoSolicitud> implements ITipoSolicitudDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(TipoSolicitudDaoImpl.class);

    @Override
    public List<TipoSolicitud> findAllTiposSolicitudNG() {
        String query = "SELECT ts FROM TipoSolicitud ts WHERE ts.cdg IN "
                + "(" + TipoSolicitud.ASIGNACION + "," + TipoSolicitud.CESION_DERECHOS
                + "," + TipoSolicitud.LIBERACION + "," + TipoSolicitud.REDISTRIBUCION
                + "," + TipoSolicitud.CONSOLIDACION + "," + TipoSolicitud.LINEAS_ACTIVAS + ")";

        TypedQuery<TipoSolicitud> tQuery = getEntityManager().createQuery(query, TipoSolicitud.class);

        List<TipoSolicitud> listaTipoSolicitud = tQuery.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Query: " + tQuery.toString());
            LOGGER.debug("Se han encontrado {} tipos de solicitud NG ", listaTipoSolicitud.size());
        }

        return listaTipoSolicitud;
    }

    @Override
    public List<TipoSolicitud> findAllTiposSolicitudNNG() {
        String query = "SELECT ts FROM TipoSolicitud ts WHERE ts.cdg IN "
                + "(" + TipoSolicitud.ASIGNACION_NNG + "," + TipoSolicitud.CESION_DERECHOS_NNG
                + "," + TipoSolicitud.LIBERACION_NNG + "," + TipoSolicitud.REDISTRIBUCION_NNG
                + ")";

        TypedQuery<TipoSolicitud> tQuery = getEntityManager().createQuery(query, TipoSolicitud.class);

        List<TipoSolicitud> listaTipoSolicitud = tQuery.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Query: " + tQuery.toString());
            LOGGER.debug("Se han encontrado {} tipos de solicitud NG ", listaTipoSolicitud.size());
        }

        return listaTipoSolicitud;
    }

    @Override
    public List<TipoSolicitud> findAllTiposSolicitudCpsn() {
        String query = "SELECT ts FROM TipoSolicitud ts WHERE ts.cdg IN "
                + "(" + TipoSolicitud.ASIGNACION_CPSN + "," + TipoSolicitud.CESION_CPSN
                + "," + TipoSolicitud.LIBERACION_CPSN + ")";

        TypedQuery<TipoSolicitud> tQuery = getEntityManager().createQuery(query, TipoSolicitud.class);

        List<TipoSolicitud> listaTipoSolicitud = tQuery.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Query: " + tQuery.toString());
            LOGGER.debug("Se han encontrado {} tipos de solicitud NG ", listaTipoSolicitud.size());
        }

        return listaTipoSolicitud;
    }

    @Override
    public List<TipoSolicitud> findAllTiposSolicitudCpsi() {
        String query = "SELECT ts FROM TipoSolicitud ts WHERE ts.cdg IN "
                + "(" + TipoSolicitud.ASIGNACION_CPSI + "," + TipoSolicitud.CESION_CPSI
                + "," + TipoSolicitud.LIBERACION_CPSI + "," + TipoSolicitud.SOLICITUD_CPSI_UIT + ")";

        TypedQuery<TipoSolicitud> tQuery = getEntityManager().createQuery(query, TipoSolicitud.class);

        List<TipoSolicitud> listaTipoSolicitud = tQuery.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Query: " + tQuery.toString());
            LOGGER.debug("Se han encontrado {} tipos de solicitud NG ", listaTipoSolicitud.size());
        }

        return listaTipoSolicitud;
    }

    @Override
    public TipoSolicitud getTipoSolicitudById(Integer idTipoSolicitud) {
        String query = "SELECT ts FROM TipoSolicitud ts WHERE ts.cdg = :idTipoSolicitud";
        TypedQuery<TipoSolicitud> tQuery = getEntityManager().createQuery(query, TipoSolicitud.class);
        tQuery.setParameter("idTipoSolicitud", idTipoSolicitud);

        TipoSolicitud resultado = tQuery.getSingleResult();
        return resultado;
    }

}
