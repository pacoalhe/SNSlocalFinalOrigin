package mx.ift.sns.dao.ng.implementation;

import java.util.List;

import javax.inject.Named;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.ng.ITipoReporteDao;
import mx.ift.sns.modelo.lineas.TipoReporte;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación de los métodos para base de datos de tipos de reporte.
 */
@Named
public class TipoReporteDaoImpl extends BaseDAO<TipoReporte> implements ITipoReporteDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(TipoReporteDaoImpl.class);

    @SuppressWarnings("unchecked")
    @Override
    public List<TipoReporte> findAllTiposReporte() {
        String query = "SELECT tr FROM TipoReporte tr ORDER BY tr.descripcion";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Query: " + query);
        }

        List<TipoReporte> list = null;
        Query consulta = getEntityManager().createQuery(query);

        Object o = consulta.getResultList();
        list = (List<TipoReporte>) o;
        return list;
    }

    @Override
    public TipoReporte getTipoReporteByCdg(String value) {
        String query = "SELECT tr FROM TipoReporte tr WHERE tr.codigo = :codigo";

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Query: " + query);
        }

        TypedQuery<TipoReporte> tQuery = getEntityManager().createQuery(query, TipoReporte.class);
        tQuery.setParameter("codigo", value);

        return tQuery.getSingleResult();
    }

}
