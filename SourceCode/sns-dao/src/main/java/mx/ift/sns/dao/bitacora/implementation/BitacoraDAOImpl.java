package mx.ift.sns.dao.bitacora.implementation;

import java.util.List;

import javax.inject.Named;
import javax.persistence.Query;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.bitacora.IBitacoraDAO;
import mx.ift.sns.modelo.bitacora.Bitacora;
import mx.ift.sns.modelo.filtros.FiltroBusquedaBitacoraLog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementacion de BitacoraDAO.
 */
@Named
public class BitacoraDAOImpl extends BaseDAO<Bitacora> implements IBitacoraDAO {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(BitacoraDAOImpl.class);

    @Override
    public Bitacora save(Bitacora bitacora) {
        getEntityManager().persist(bitacora);

        return bitacora;
    }

    /**
     * convierte el filtro en jpa.
     * @param filtro filtro
     * @return cadema
     */
    private String filtro2JPA(FiltroBusquedaBitacoraLog filtro) {

        StringBuilder builder = new StringBuilder();
        builder.append("FROM Bitacora b WHERE (1=1) ");

        if (filtro.getFechaInicio() != null) {
            builder.append(" AND b.fecha >= :fechaInicio ");
        }

        if (filtro.getFechaFin() != null) {
            builder.append(" AND b.fecha <= :fechaFin ");
        }

        if (filtro.getUsuario() != null) {
            builder.append(" AND b.usuario = :usuario ");
        }

        return builder.toString();
    }

    @Override
    public List<Bitacora> findBitacora(FiltroBusquedaBitacoraLog filtro) {
        // String query = "SELECT b FROM Bitacora b order by b.fecha desc";
        // Query q = getEntityManager().createQuery(query);

        StringBuilder builder = new StringBuilder();
        builder.append("SELECT b ");
        builder.append(filtro2JPA(filtro));
        builder.append(" order by b.fecha desc");

        Query q = getEntityManager().createQuery(builder.toString());

        if (filtro.getFechaInicio() != null) {
            q.setParameter("fechaInicio", filtro.getFechaInicio());
        }

        if (filtro.getFechaFin() != null) {
            q.setParameter("fechaFin", filtro.getFechaFin());
        }

        if (filtro.getUsuario() != null) {
            q.setParameter("usuario", filtro.getUsuario());
        }

        if (filtro.getFirst() != -1) {
            q.setFirstResult(filtro.getFirst());
            q.setMaxResults(filtro.getPageSize());
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("query {}", q);
        }

        @SuppressWarnings("unchecked")
        List<Bitacora> list = (List<Bitacora>) q.getResultList();

        if (LOGGER.isDebugEnabled()) {
            if (list != null) {
                LOGGER.debug("num res {}", list.size());
            }
        }

        return list;
    }

    @Override
    public int findBitacoraCount(FiltroBusquedaBitacoraLog filtro) {

        StringBuilder builder = new StringBuilder();
        builder.append("SELECT count(b) ");
        builder.append(filtro2JPA(filtro));

        Query q = getEntityManager().createQuery(builder.toString());

        if (filtro.getFechaInicio() != null) {
            q.setParameter("fechaInicio", filtro.getFechaInicio());
        }

        if (filtro.getFechaFin() != null) {
            q.setParameter("fechaFin", filtro.getFechaFin());
        }

        if (filtro.getUsuario() != null) {
            q.setParameter("usuario", filtro.getUsuario());
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("query {}", q);
        }

        Long num = (Long) q.getSingleResult();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("num res {}", num);
        }

        return num.intValue();

    }
}
