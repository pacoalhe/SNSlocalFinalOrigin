package mx.ift.sns.dao.pnn.implementation;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.StringJoiner;

import javax.annotation.Resource;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.sql.DataSource;

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

    final int BATCH_SIZE = 5000;

    /**
     * FJAH 23052025 Refactorización del metodo update
     * Sin campos estáticos de conexión.
     * Cada método abre y cierra su propia conexión.
     * Manejo seguro de autoCommit:
     * 				Se pone en false para transacción manual.
     * 				Se hace rollback en caso de excepción.
     * 				Se restaura autoCommit a true antes de cerrar conexión (buena práctica con pools).
     * Todos los recursos se cierran en el bloque finally.
     * Thread-safe:
     * 				No hay variables de instancia ni estáticas compartidas.
     */
    @Resource(lookup = "jdbc/SNS")
    private DataSource dataSource;


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
                "FROM PNN_DETALLE WHERE NUMERO_INICIAL <= ? AND NUMERO_FINAL >= ?";

        PlanMaestroDetalle planMaestroDetalle = null;

        try {
            Query query = getEntityManager().createNativeQuery(strQuery);
            query.setParameter(1, numeroInicial);
            query.setParameter(2, numeroFinal);
            query.setMaxResults(1); // Obtiene registro unico

            LOGGER.info("Consulta pública: búsqueda del número {} en el plan maestro", numeroInicial);

            @SuppressWarnings("unchecked")
            List<Object[]> resultados = query.getResultList();

            if (resultados != null && !resultados.isEmpty()) {
                Object[] resultado = resultados.get(0);
                planMaestroDetalle = new PlanMaestroDetalle();

                planMaestroDetalle.setIdo(resultado[0] != null ? Integer.valueOf(resultado[0].toString()) : null);
                planMaestroDetalle.setNumeroInicial(resultado[1] != null ? new BigDecimal(resultado[1].toString()) : null);
                planMaestroDetalle.setNumeroFinal(resultado[2] != null ? new BigDecimal(resultado[2].toString()) : null);
                planMaestroDetalle.setTipoServicio(resultado[3] != null ? resultado[3].toString().charAt(0) : null);
                planMaestroDetalle.setMpp(resultado[4] != null ? resultado[4].toString().charAt(0) : null);
                planMaestroDetalle.setIda(resultado[5] != null ? Integer.valueOf(resultado[5].toString()) : null);
                planMaestroDetalle.setAreaServicio(resultado[6] != null ? Integer.valueOf(resultado[6].toString()) : null);
                planMaestroDetalle.setZona(resultado[7] != null ? Integer.valueOf(resultado[7].toString()) : null);

                LOGGER.info("Consulta pública: número {} localizado en el plan maestro.", numeroInicial);
            }

        } catch (NoResultException e) {
            LOGGER.error("Consulta pública: número {} no localizado en plan maestro", numeroInicial);
        } catch (Exception e) {
            LOGGER.error("Error en getDetalleNumeroConsultaPublica: {}", e.getMessage(), e);
        }

        return planMaestroDetalle;
    }

    public int upsertBatchPlanMaestro(List<PlanMaestroDetalle> registros) throws SQLException {
        final String mergeSQL =
                "MERGE INTO PNN_DETALLE tgt " +
                        "USING (SELECT ? AS NUMERO_INICIAL, ? AS NUMERO_FINAL, ? AS IDO, ? AS TIPO_SERVICIO, ? AS MPP, ? AS IDA, ? AS AREA_SERVICIO, ? AS ZONA FROM DUAL) src " +
                        "ON (tgt.NUMERO_INICIAL = src.NUMERO_INICIAL AND tgt.NUMERO_FINAL = src.NUMERO_FINAL) " +
                        "WHEN MATCHED THEN UPDATE SET " +
                        "  tgt.IDO = src.IDO, " +
                        "  tgt.TIPO_SERVICIO = src.TIPO_SERVICIO, " +
                        "  tgt.MPP = src.MPP, " +
                        "  tgt.IDA = src.IDA, " +
                        "  tgt.AREA_SERVICIO = src.AREA_SERVICIO, " +
                        "  tgt.ZONA = src.ZONA " +
                        "WHEN NOT MATCHED THEN INSERT " +
                        "(NUMERO_INICIAL, NUMERO_FINAL, IDO, TIPO_SERVICIO, MPP, IDA, AREA_SERVICIO, ZONA) " +
                        "VALUES (src.NUMERO_INICIAL, src.NUMERO_FINAL, src.IDO, src.TIPO_SERVICIO, src.MPP, src.IDA, src.AREA_SERVICIO, src.ZONA)";

        int total = 0;
        int batchCount = 0;

        if (registros == null || registros.isEmpty()) {
            LOGGER.warn("Lista vacía recibida en upsertBatchPlanMaestro, no se ejecuta nada");
            return 0;
        }

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(mergeSQL)) {

            for (PlanMaestroDetalle p : registros) {
                ps.setLong(1, p.getId().getNumeroInicial());
                ps.setLong(2, p.getId().getNumeroFinal());
                ps.setInt(3, p.getIdo());
                ps.setString(4, String.valueOf(p.getTipoServicio()));
                ps.setString(5, String.valueOf(p.getMpp()));
                ps.setInt(6, p.getIda());
                ps.setInt(7, p.getAreaServicio());
                ps.setInt(8, p.getZona());
                ps.addBatch();
                batchCount++;

                if (batchCount >= BATCH_SIZE) {
                    int[] results = ps.executeBatch();
                    for (int r : results) total += r;
                    LOGGER.info("Batch de {} registros MERGE PMN ejecutado", results.length);
                    ps.clearBatch();
                    batchCount = 0;
                }
            }

            if (batchCount > 0) {
                int[] results = ps.executeBatch();
                for (int r : results) total += r;
                LOGGER.info("Batch final de {} registros MERGE PMN ejecutado", results.length);
                ps.clearBatch();
            }

        } catch (SQLException e) {
            LOGGER.error("Error ejecutando batch MERGE en PMN: {}", e.getMessage(), e);
            throw e;
        }

        LOGGER.info("Total registros afectados en MERGE PMN: {}", total);
        return total;
    }

    //@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public int deleteBatchPlanMaestro(List<PlanMaestroDetalle> registros) {
        int totalEliminados = 0;
        int BATCH_SIZE = 1000;
        int totalSolicitados = registros.size();

        for (int i = 0; i < registros.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, registros.size());
            List<PlanMaestroDetalle> subLista = registros.subList(i, end);

            if (subLista.isEmpty()) continue;

            StringBuilder sql = new StringBuilder();
            sql.append("DELETE FROM PNN_DETALLE WHERE (NUMERO_INICIAL, NUMERO_FINAL) IN (");

            StringBuilder sj = new StringBuilder();
            for (PlanMaestroDetalle p : subLista) {
                if (sj.length() > 0) sj.append(", ");
                sj.append("(")
                        .append(p.getId().getNumeroInicial())
                        .append(", ")
                        .append(p.getId().getNumeroFinal())
                        .append(")");
            }

            sql.append(sj);
            sql.append(")");

            try {
                int afectados = getEntityManager().createNativeQuery(sql.toString()).executeUpdate();
                totalEliminados += afectados;
                //LOGGER.info("Batch DELETE de {} registros ejecutado en PNN_DETALLE", afectados);
            } catch (Exception e) {
                LOGGER.error("Error al ejecutar batch DELETE en rango {} - {}: {}",
                        subLista.get(0).getId().getNumeroInicial(),
                        subLista.get(subLista.size() - 1).getId().getNumeroFinal(),
                        e.getMessage(), e);
            }
        }

        // Nuevo resumen agregado
        LOGGER.info("Resumen eliminación PNN_DETALLE -> Solicitados: {}, Eliminados: {}, No encontrados: {}",
                totalSolicitados, totalEliminados, (totalSolicitados - totalEliminados));

        //LOGGER.info("Total eliminados en PNN_DETALLE: {}", totalEliminados);
        return totalEliminados;
    }

    /*
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
     */

}
