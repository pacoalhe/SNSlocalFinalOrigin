package mx.ift.sns.dao.oficios.implementation;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.oficios.IPlantillaDao;
import mx.ift.sns.modelo.filtros.FiltroBusqueda;
import mx.ift.sns.modelo.filtros.FiltroBusquedaPlantillas;
import mx.ift.sns.modelo.oficios.Plantilla;
import mx.ift.sns.modelo.oficios.PlantillaPK;
import mx.ift.sns.modelo.oficios.TipoDestinatario;
import mx.ift.sns.modelo.solicitud.TipoSolicitud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación de los métodos para base de datos para la generación de Plantillas.
 */
@Named
public class PlantillaDaoImpl extends BaseDAO<Plantilla> implements IPlantillaDao {

    /** Logger de la clase. */
    private static final Logger LOGGER = LoggerFactory.getLogger(PlantillaDaoImpl.class);

    @Override
    public List<Plantilla> findAllPlantillas() {
        String query = "SELECT p FROM Plantilla p ORDER BY p.tipoSolicitud";
        TypedQuery<Plantilla> tQuery = getEntityManager().createQuery(query, Plantilla.class);

        List<Plantilla> result = tQuery.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Se han recuperado {} plantillas", result.size());
        }
        return result;
    }

    @Override
    public List<Plantilla> findAllPlantillas(FiltroBusquedaPlantillas pFiltros) {

        // Filtros de Búsqueda definidos
        ArrayList<FiltroBusqueda> filtros = pFiltros.getListaFiltros();

        // if (filtros.isEmpty()) {
        // return findAllPlantillas();
        // }

        CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Plantilla> cQuery = cb.createQuery(Plantilla.class);
        Root<Plantilla> plantilla = cQuery.from(Plantilla.class);

        // WHERE
        Predicate wherePredicate = cb.conjunction();
        for (FiltroBusqueda filtro : filtros) {
            Predicate pred = cb.equal(plantilla.get(filtro.getCampo()), filtro.getValor());
            wherePredicate = cb.and(wherePredicate, pred);
        }
        cQuery.where(wherePredicate).distinct(true);
        cQuery.orderBy(cb.asc(plantilla.get("tipoSolicitud")));
        cQuery.select(plantilla);

        TypedQuery<Plantilla> tQuery = getEntityManager().createQuery(cQuery);

        // Paginación
        if (pFiltros.isUsarPaginacion()) {
            tQuery.setFirstResult(pFiltros.getNumeroPagina());
            tQuery.setMaxResults(pFiltros.getResultadosPagina());
        }

        List<Plantilla> result = tQuery.getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Se han recuperado {} plantillas", result.size());
        }
        return result;
    }

    @Override
    public int findAllPlantillasCount(FiltroBusquedaPlantillas pFiltros) {

        // Filtros de Búsqueda definidos
        ArrayList<FiltroBusqueda> filtros = pFiltros.getListaFiltros();

        CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cQuery = cb.createQuery(Long.class);
        Root<Plantilla> plantilla = cQuery.from(Plantilla.class);

        // WHERE
        Predicate wherePredicate = cb.conjunction();
        for (FiltroBusqueda filtro : filtros) {
            Predicate pred = cb.equal(plantilla.get(filtro.getCampo()), filtro.getValor());
            wherePredicate = cb.and(wherePredicate, pred);
        }
        cQuery.where(wherePredicate).distinct(true);
        cQuery.select(cb.count(plantilla));

        TypedQuery<Long> tQuery = getEntityManager().createQuery(cQuery);
        int rowCount = tQuery.getSingleResult().intValue();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Plantillas contadas: " + rowCount);
        }

        return rowCount;
    }

    @Override
    public Plantilla saveOrUpdate(Plantilla entity) {
        if (entity.getId() == null) {
            PlantillaPK plantillaPk = new PlantillaPK();
            plantillaPk.setCdgTipoDestinatario(entity.getTipoDestinatario().getCdg());
            plantillaPk.setCdgTipoSolicitud(entity.getTipoSolicitud().getCdg());
            entity.setId(plantillaPk);
        }
        return super.saveOrUpdate(entity);
    }

    @Override
    public Plantilla getPlantilla(TipoSolicitud pTipoSolicitud, TipoDestinatario pTipoDestinatario) {
        StringBuffer sbQuery = new StringBuffer();
        sbQuery.append("SELECT p FROM Plantilla p ");
        sbQuery.append("WHERE p.id.cdgTipoSolicitud=:cdgTipoSolicitud ");
        sbQuery.append("AND p.id.cdgTipoDestinatario=:cdgTipoDestinatario");

        TypedQuery<Plantilla> tQuery = getEntityManager().createQuery(sbQuery.toString(), Plantilla.class);
        tQuery.setParameter("cdgTipoSolicitud", pTipoSolicitud.getCdg());
        tQuery.setParameter("cdgTipoDestinatario", pTipoDestinatario.getCdg());

        try {
            Plantilla plantilla = tQuery.getSingleResult();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Plantilla recuperada. Tipo Solicitud {}, Tipo Destinatario {}",
                        pTipoSolicitud.getCdg(),
                        pTipoDestinatario.getCdg());
            }

            return plantilla;
        } catch (NoResultException nre) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("No existe plantilla para Tipo Solicitud {} y Tipo Destinatario {}",
                        pTipoSolicitud.getCdg(),
                        pTipoDestinatario.getCdg());
            }
            return null;
        }
    }
}
