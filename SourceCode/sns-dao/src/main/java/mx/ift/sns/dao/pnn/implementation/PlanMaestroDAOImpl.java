package mx.ift.sns.dao.pnn.implementation;

import java.util.List;

import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.ift.sns.dao.BaseDAO;
import mx.ift.sns.dao.pnn.IPlanMaestroDAO;
import mx.ift.sns.modelo.pnn.PlanMaestroDetalle;

/**
 * Implementación de los métodos para base de datos de planes.
 */
@Named
public class PlanMaestroDAOImpl extends BaseDAO<PlanMaestroDetalle> implements IPlanMaestroDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlanMaestroDAOImpl.class);

    @Override
    public PlanMaestroDetalle getDetalleNumero(Long numeroInicial, Long numeroFinal) {
	final String strQuery = "SELECT IDO, NUMERO_INICIAL, NUMERO_FINAL, TIPO_SERVICIO, MPP, IDA, AREA_SERVICIO "
		+ "FROM PNN_DETALLE WHERE NUMERO_INICIAL <= " + numeroInicial + " AND NUMERO_FINAL >= " + numeroFinal;

	PlanMaestroDetalle planMaestroDetalle = null;

	try {
	    Query consulta = getEntityManager().createNativeQuery(strQuery, PlanMaestroDetalle.class);

	    LOGGER.info("Se realiza la búsqueda del número " + numeroInicial + " en el plan maestro");
	    @SuppressWarnings("unchecked")
	    List<PlanMaestroDetalle> listPlanMaestroDetalle = consulta.getResultList();

	    if ((listPlanMaestroDetalle != null) && (listPlanMaestroDetalle.size() > 0)) {
		planMaestroDetalle = listPlanMaestroDetalle.get(0);
		LOGGER.info("Se localizó el número " + numeroInicial + "en el plan maestro.");
	    }

	} catch (NoResultException e) {
	    LOGGER.error("No se localizó el número " + numeroInicial + "en el plan maestro.");
	    return null;
	}

	return planMaestroDetalle;
    }

    @Override
    public PlanMaestroDetalle actualizaNumero(Long numeroInicial, Long numeroFinal,
	    PlanMaestroDetalle registroActualizado) {
	final String strQueryUpdate = "UPDATE PlanMaestroDetalle p SET p.tipoServicio = :tipoServicio, p.mpp = :mpp, p.areaServicio = :areaServicio, p.ido = :ido, p.ida = :ida "
		+ "WHERE ((p.id.numeroInicial = :noInicial AND p.id.numeroFinal = :noFinal))";
	// PlanMaestroDetalle planMaestroDetalle = null;
	LOGGER.info("Editar entidad: " + registroActualizado.toString());
	if (getEntityManager().isOpen()) {
	    LOGGER.info("em opened..." + getEntityManager().getProperties());
	}
	try {
	    // getEntityManager().joinTransaction();
	    // getEntityManager().getTransaction().begin();

	    // planMaestroDetalle = saveOrUpdate(registroActualizado);
	    // refresh(planMaestroDetalle);

	    TypedQuery<PlanMaestroDetalle> query = getEntityManager().createQuery(strQueryUpdate,
		    PlanMaestroDetalle.class);

	    query.setParameter("ido", registroActualizado.getIdo());
	    query.setParameter("noInicial", numeroInicial);
	    query.setParameter("noFinal", numeroFinal);
	    query.setParameter("tipoServicio", registroActualizado.getTipoServicio());
	    query.setParameter("mpp", registroActualizado.getMpp());
	    query.setParameter("ida", registroActualizado.getIda());
	    query.setParameter("areaServicio", registroActualizado.getAreaServicio());

	    LOGGER.info("Query=> " + query);
	    int updated = query.executeUpdate();
	    LOGGER.info("updated?=> " + updated);

	    // getEntityManager().merge(registroActualizado);
	    // getEntityManager().persist(registroActualizado);
	    // getEntityManager().flush();
	    // getEntityManager().getTransaction().commit();

	    // LOGGER.info("PlanMaestroUpdated=> " + planMaestroDetalle.toString());
	    // isUpdated = true;
	} catch (Exception e) {
	    LOGGER.error("ERROR: " + e.getMessage());
	    if (LOGGER.isDebugEnabled()) {
		LOGGER.debug("No se pudo actualizar el numero en el plan maestro.");
	    }
	    return null;
	}

	return registroActualizado;
    }

    @Override
    public Boolean eliminaNumero(Long numeroInicial, Long numeroFinal) {
	String strQuery = "DELETE FROM PlanMaestroDetalle p WHERE ((p.id.numeroInicial = :numeroInicial AND p.id.numeroFinal = :numeroFinal))";

	LOGGER.info("Valores a eliminar=> noInicial: " + numeroInicial + " noFinal: " + numeroFinal);
	try {
	    Query query = getEntityManager().createQuery(strQuery);

	    query.setParameter("numeroInicial", numeroInicial);
	    query.setParameter("numeroFinal", numeroFinal);
	    LOGGER.info("Query=> " + query);

	    query.executeUpdate();
	} catch (Exception e) {
	    LOGGER.error("ERROR: " + e.getMessage());
	    if (LOGGER.isDebugEnabled()) {
		LOGGER.debug("No se pudo eliminar el numero en el plan maestro.");
	    }
	    return false;
	}

	return true;
    }
}
