package mx.ift.sns.dao.ng.implementation;

import java.util.List;

import javax.inject.Named;
import javax.persistence.Query;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.ng.ISerieArrendadaDAO;
import mx.ift.sns.modelo.reporteabd.SerieArrendada;
import mx.ift.sns.modelo.reporteabd.SerieArrendadaPadre;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación de los métodos para base de datos de Series Arrendadas.
 */
@Named
public class SerieArrendadaDAOImpl extends BaseDAO<SerieArrendada> implements ISerieArrendadaDAO {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SerieArrendadaDAOImpl.class);

    @Override
    public void deleteAll() {

        LOGGER.debug("borramos todo");
        Query q = getEntityManager().createQuery("DELETE FROM SerieArrendada");
        q.executeUpdate();

        Query q2 = getEntityManager().createQuery("DELETE FROM SerieArrendadaPadre");
        q2.executeUpdate();
    }

    @Override
    public SerieArrendada create(SerieArrendada serie) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("{} {} {} {}", serie.getId().getNumberFrom(),
                    serie.getId().getNumberTo(),
                    serie.getAsignatario());
        }

        serie = getEntityManager().merge(serie);
        return serie;
    }

    @Override
    public void create(SerieArrendadaPadre serie) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("{} {} {} ", serie.getId().getNumberFrom(),
                    serie.getNumberTo(),
                    serie.getId().getIdPstAsignatario());
        }
        getEntityManager().merge(serie);
    }

    @Override
    public List<SerieArrendada> findAll() {

        String strQuery = "SELECT s FROM SerieArrendada s ORDER BY s.id.numberFromFile";
        Query query = getEntityManager().createQuery(strQuery);

        @SuppressWarnings("unchecked")
        List<SerieArrendada> list = (List<SerieArrendada>) query.getResultList();

        return list;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SerieArrendada> findAll(int first, int pageSize) {
        String select = "SELECT s FROM SerieArrendada s";

        Query q = getEntityManager().createQuery(select);

        q.setFirstResult(first);
        q.setMaxResults(pageSize);

        return (List<SerieArrendada>) q.getResultList();
    }

    @Override
    public int getSeriesArrendadasCount() {
        StringBuffer sbQuery = new StringBuffer();
        sbQuery.append("SELECT count(s.id) FROM SerieArrendada s");

        Query query = getEntityManager().createQuery(sbQuery.toString());
        int rowCount = ((Long) query.getSingleResult()).intValue();
        return rowCount;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SerieArrendadaPadre> findAllPadres(int first, int pageSize) {
        String select = "SELECT s FROM SerieArrendadaPadre s ORDER BY s.id.numberFrom";

        Query q = getEntityManager().createQuery(select);

        q.setFirstResult(first);
        q.setMaxResults(pageSize);

        return (List<SerieArrendadaPadre>) q.getResultList();
    }

    @Override
    public int findAllPadresCount() {
        String select = "SELECT count(s.id) FROM SerieArrendadaPadre s";

        Query query = getEntityManager().createQuery(select);
        int rowCount = ((Long) query.getSingleResult()).intValue();
        return rowCount;
    }
}
