package mx.ift.sns.dao.oficios.implementation;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.oficios.IOficioDao;
import mx.ift.sns.modelo.oficios.Oficio;
import mx.ift.sns.modelo.oficios.TipoDestinatario;
import mx.ift.sns.modelo.solicitud.Solicitud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación de los métodos para base de datos para la generación de Oficios.
 */
@Named
public class OficioDaoImpl extends BaseDAO<Oficio> implements IOficioDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(OficioDaoImpl.class);

    @Override
    public Oficio getOficio(BigDecimal pIdOficio) {
        return getEntityManager().find(Oficio.class, pIdOficio);
    }

    @Override
    public Oficio getOficio(Solicitud pSolicitud, TipoDestinatario pTipoDestinatario) {
        // Query
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Oficio> cQuery = cb.createQuery(Oficio.class);
        Root<Oficio> of = cQuery.from(Oficio.class);

        // Parámetros
        ParameterExpression<Solicitud> solicitud = cb.parameter(Solicitud.class);
        ParameterExpression<TipoDestinatario> tipoDest = cb.parameter(TipoDestinatario.class);
        cQuery.where(cb.equal(of.get("solicitud"), solicitud), cb.equal(of.get("tipoDestinatario"), tipoDest));

        TypedQuery<Oficio> tQuery = getEntityManager().createQuery(cQuery.select(of));
        tQuery.setParameter(solicitud, pSolicitud);
        tQuery.setParameter(tipoDest, pTipoDestinatario);

        Oficio oficio = null;
        try {
            oficio = tQuery.getSingleResult();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Oficio número {} encontrado para la solicitud {} ",
                        oficio.getNumOficio(), pSolicitud.getId());
            }
        } catch (NoResultException nre) {
            // Si no hay resultados devolvemos un null
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Oficio para tipo destinatario {} inexistente para la solicitud {} ",
                        pTipoDestinatario, pSolicitud.getId());
            }
        }

        return oficio;
    }

    @Override
    public Oficio getOficioByNumOficio(String numOficio, TipoDestinatario pTipoDestinatario) {

        String query = "SELECT o FROM Oficio o WHERE o.numOficio = :numOfi AND o.tipoDestinatario = :tipoDest";
        TypedQuery<Oficio> tQuery = getEntityManager().createQuery(query, Oficio.class);
        tQuery.setParameter("numOfi", numOficio);
        tQuery.setParameter("tipoDest", pTipoDestinatario);

        Oficio oficio = null;
        try {
            oficio = tQuery.getSingleResult();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Oficio número: " + numOficio + " encontrado, id: " + oficio.getId());
            }
        } catch (NoResultException nre) {
            // Si no hay resultados devolvemos un null
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Oficio número: " + numOficio + " inexistente.");
            }
        }
        return oficio;
    }

    @Override
    public List<Oficio> getOficiosBySolicitud(BigDecimal idSolicitud) {

        String query = "SELECT o FROM Oficio o WHERE o.solicitud.id = :idSolicitud ";
        TypedQuery<Oficio> tQuery = getEntityManager().createQuery(query, Oficio.class);
        tQuery.setParameter("idSolicitud", idSolicitud);

        List<Oficio> oficios = tQuery.getResultList();
        return oficios;
    }

    @Override
    public int getOficiosCount() {
        Query query = getEntityManager().createQuery("SELECT COUNT(o.id) FROM Oficio o");
        return ((Long) query.getSingleResult()).intValue();
    }

    @Override
    public boolean existeNumeroOficio(String numeroOficio) {

        String squery = "SELECT COUNT(o) FROM Oficio o WHERE o.numOficio = :numero";
        TypedQuery<Long> query = getEntityManager().createQuery(squery, Long.class);
        query.setParameter("numero", numeroOficio);

        Long resultado = query.getSingleResult();

        return (resultado != null && resultado > 0);
    }

}
