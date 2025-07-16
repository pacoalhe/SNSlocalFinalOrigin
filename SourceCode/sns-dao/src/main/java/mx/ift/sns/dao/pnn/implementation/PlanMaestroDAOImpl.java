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
        //final String strQuery = "SELECT IDO, NUMERO_INICIAL, NUMERO_FINAL, TIPO_SERVICIO, MPP, IDA, AREA_SERVICIO "
        //        + "FROM PNN_DETALLE WHERE NUMERO_INICIAL <= :numeroInicial AND NUMERO_FINAL >= :numeroFinal";
        final String strQuery = "SELECT " +
                "IDO, NUMERO_INICIAL, NUMERO_FINAL, TIPO_SERVICIO, MPP, IDA, AREA_SERVICIO, ZONA " +
                "FROM PNN_DETALLE WHERE NUMERO_INICIAL <= ?1 AND NUMERO_FINAL >= ?2";

        //FJAH 27.05.2025
        LOGGER.debug("getDetalleNumero - numeroInicial: " + numeroInicial + ", numeroFinal: " + numeroFinal);
        if (numeroInicial == null || numeroFinal == null) {
            LOGGER.error("Parámetros null en getDetalleNumero: numeroInicial=" + numeroInicial + ", numeroFinal=" + numeroFinal);
            return null;
        }

        //PlanMaestroDetalle planMaestroDetalle = new PlanMaestroDetalle();
        PlanMaestroDetalle planMaestroDetalle = null;
        //Fin FJAH

        Query consulta = null;
        try {
            consulta = getEntityManager().createNativeQuery(strQuery, PlanMaestroDetalle.class);

            // FJAH 27.05.2025 *** Setteo parámetros ***
            consulta.setParameter(1, numeroInicial);
            consulta.setParameter(2, numeroFinal);
            //Fin FJAH

            LOGGER.info("Se realiza la búsqueda del número {} en el plan maestro", numeroInicial);

            //Uncheked
            @SuppressWarnings("unchecked")
            List<PlanMaestroDetalle> listPlanMaestroDetalle = consulta.getResultList();

            if (listPlanMaestroDetalle != null && !listPlanMaestroDetalle.isEmpty()) {
                planMaestroDetalle = listPlanMaestroDetalle.get(0);
                LOGGER.info("Se localizó el número {} en el plan maestro.", numeroInicial);
            } else {
                LOGGER.warn("No se encontró ningún resultado para el número {} en el plan maestro.", numeroInicial);
            }
        } catch (NoResultException e) {
            LOGGER.error("No se localizó el número {} en el plan maestro.", numeroInicial);
            //FJAH 27.05.2025
            //return null;
        } catch (Exception e) {
            LOGGER.error("Error inesperado en getDetalleNumero: " + e.getMessage(), e);
        }
        //Fin FJAH

        return planMaestroDetalle;
    }

    @Override
    public PlanMaestroDetalle actualizaNumero(Long numeroInicial, Long numeroFinal, PlanMaestroDetalle registroActualizado) {
        final String strQueryUpdate = "UPDATE PlanMaestroDetalle p SET p.tipoServicio = :tipoServicio, p.mpp = :mpp, p.areaServicio = :areaServicio, p.ido = :ido, p.ida = :ida, p.zona = :zona "
                + "WHERE ((p.id.numeroInicial = :noInicial AND p.id.numeroFinal = :noFinal))";
        // PlanMaestroDetalle planMaestroDetalle = null;
        LOGGER.info("Editar entidad: {}", registroActualizado.toString());
        if (getEntityManager().isOpen()) {
            LOGGER.info("em opened...{}", getEntityManager().getProperties());
        }
        try {
            // getEntityManager().joinTransaction();
            // getEntityManager().getTransaction().begin();

            // planMaestroDetalle = saveOrUpdate(registroActualizado);
            // refresh(planMaestroDetalle);

            TypedQuery<PlanMaestroDetalle> query = getEntityManager().createQuery(strQueryUpdate, PlanMaestroDetalle.class);

            query.setParameter("ido", registroActualizado.getIdo());
            query.setParameter("noInicial", numeroInicial);
            query.setParameter("noFinal", numeroFinal);
            query.setParameter("tipoServicio", registroActualizado.getTipoServicio());
            query.setParameter("mpp", registroActualizado.getMpp());
            query.setParameter("ida", registroActualizado.getIda());
            query.setParameter("areaServicio", registroActualizado.getAreaServicio());
            query.setParameter("zona", registroActualizado.getZona());

            LOGGER.info("Query=> {}", query);
			int updated = query.executeUpdate();
			LOGGER.info("updated?=> {}", updated);

            // getEntityManager().merge(registroActualizado);
            // getEntityManager().persist(registroActualizado);
            // getEntityManager().flush();
            // getEntityManager().getTransaction().commit();

            // LOGGER.info("PlanMaestroUpdated=> " + planMaestroDetalle.toString());
            // isUpdated = true;
        } catch (Exception e) {
            LOGGER.error("ERROR: {}", e.getMessage());
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

        LOGGER.info("Valores a eliminar=> noInicial: {} noFinal: {}", numeroInicial, numeroFinal);
        try {
            Query query = getEntityManager().createQuery(strQuery);

            query.setParameter("numeroInicial", numeroInicial);
            query.setParameter("numeroFinal", numeroFinal);
            LOGGER.info("Query=> {}", query);

            query.executeUpdate();
        } catch (Exception e) {
            LOGGER.error("ERROR: {}", e.getMessage());
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("No se pudo eliminar el numero en el plan maestro.");
            }
            return false;
        }
        return true;
    }

    /**
     * FJAH 16042025
     * Creación del metodo para la consulta de numeración geografica .xhtml
     *
     * @param numeroInicial
     * @param numeroFinal
     * @return
     */

    @Override
    public PlanMaestroDetalle getDetalleNumeroConsultaPublica(Long numeroInicial, Long numeroFinal) {
        final String strQuery = "SELECT " +
                "IDO, NUMERO_INICIAL, NUMERO_FINAL, TIPO_SERVICIO, MPP, IDA, AREA_SERVICIO, ZONA " +
                "FROM PNN_DETALLE WHERE NUMERO_INICIAL <= ?1 AND NUMERO_FINAL >= ?2";

        try {
            Query query = getEntityManager().createNativeQuery(strQuery, PlanMaestroDetalle.class);
            query.setParameter(1, numeroInicial);
            query.setParameter(2, numeroFinal);

            LOGGER.info("Consulta pública: búsqueda del número {} en el plan maestro", numeroInicial);

            @SuppressWarnings("unchecked")
            List<PlanMaestroDetalle> resultados = query.getResultList();

            if (resultados != null && !resultados.isEmpty()) {
                return resultados.get(0);
            }

        } catch (NoResultException e) {
            LOGGER.error("Consulta pública: número {} no localizado en plan maestro", numeroInicial);
        } catch (Exception e) {
            LOGGER.error("Error en getDetalleNumeroConsultaPublica: {}", e.getMessage(), e);
		}

        return null;
    }

}
