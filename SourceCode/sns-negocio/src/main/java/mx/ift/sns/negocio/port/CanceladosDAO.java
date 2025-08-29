package mx.ift.sns.negocio.port;

import java.io.*;
import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import mx.ift.sns.negocio.conf.IParametrosService;
import mx.ift.sns.negocio.ng.model.ResultadoValidacionCSV;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.ift.sns.modelo.port.NumeroCancelado;
import mx.ift.sns.utils.dbConnection.ConnectionBD;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.sql.DataSource;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import java.util.Date;


@Stateless
public class CanceladosDAO {

	public CanceladosDAO() {
		// Constructor vacío requerido por EJB
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(CanceladosDAO.class);

	//private static Connection conn = ConnectionBD.getConnection();

	/**
	 * FJAH 23052025 Refactorización 25.07.2025 Fix duplicada Port_num_cancelado
	 * Sin campos estáticos de conexión.
	 * Cada método abre y cierra su propia conexión.
	 * Manejo seguro de autoCommit:
	 * Se pone en false para transacción manual.
	 * Se hace rollback en caso de excepción.
	 * Se restaura autoCommit a true antes de cerrar conexión (buena práctica con pools).
	 * Todos los recursos se cierran en el bloque finally.
	 * Thread-safe:
	 * No hay variables de instancia ni estáticas compartidas.
	 */

	@Resource(lookup = "jdbc/SNS")
	private DataSource dataSource;

	/** Servicio configuracion. */
	@EJB
	private IParametrosService paramService;

	final int BATCH_SIZE = 5000;

	private final ReentrantLock lock = new ReentrantLock();
	private static final long LOCK_TIMEOUT = 30; // segundos

	public void delete(String numero) throws SQLException {
		LOGGER.debug("Iniciando eliminación para número: {}", numero);

		if (!acquireLockWithTimeout()) {
			throw new SQLException("No se pudo adquirir el lock para eliminación");
		}

		try {
			executeDeleteTransaction(numero);
		} finally {
			releaseLock();
		}
	}

	public void insert(NumeroCancelado numero) throws SQLException {
		LOGGER.debug("Iniciando inserción para número: {}", numero.getNumberFrom());

		if (!acquireLockWithTimeout()) {
			throw new SQLException("No se pudo adquirir el lock para inserción");
		}

		try {
			executeInsertTransaction(numero);
		} finally {
			releaseLock();
		}
	}

	private boolean acquireLockWithTimeout() throws SQLException {
		try {
			if (!lock.tryLock()) {
				LOGGER.warn("Lock no disponible inmediatamente, esperando...");
				return lock.tryLock(LOCK_TIMEOUT, TimeUnit.SECONDS);
			}
			return true;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new SQLException("Interrupción durante la espera del lock", e);
		}
	}

	private void executeDeleteTransaction(String numero) throws SQLException {
		try (Connection conn = dataSource.getConnection()) {
			//conn.setAutoCommit(false);

			//try {
			if (existeNumeroPortado(conn, numero)) {
				eliminarNumeroPortado(conn, numero);
				LOGGER.debug("Número {} eliminado correctamente de port_num_portado", numero);
			}
			//conn.commit();
			//} catch (SQLException e) {
			//	if (!conn.isClosed()) {
			//conn.rollback();
			//	}
			//	throw e;
			//}
		}
	}

	private void executeInsertTransaction(NumeroCancelado numero) throws SQLException {
		try (Connection conn = dataSource.getConnection()) {
			//conn.setAutoCommit(false);

			//try {
			insertarNumeroCancelado(conn, numero);
			LOGGER.debug("Número {} insertado correctamente en port_num_cancelado", numero.getNumberFrom());
			//conn.commit();
			/*
			} catch (SQLException e) {
				if (!conn.isClosed()) {
					//conn.rollback();
				}
				throw e;
			}

			 */
		}
	}

	private boolean existeNumeroPortado(Connection conn, String numberFrom) throws SQLException {
		final String sql = "SELECT count(1) FROM port_num_portado WHERE numberfrom=?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, numberFrom);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() && rs.getInt(1) > 0;
			}
		}
	}

	private void eliminarNumeroPortado(Connection conn, String numero) throws SQLException {
		final String sql = "DELETE FROM port_num_portado WHERE NUMBERFROM=?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, numero);
			ps.executeUpdate();
		}
	}

	private void insertarNumeroCancelado(Connection conn, NumeroCancelado numero) throws SQLException {
		final String sql = "INSERT INTO port_num_cancelado (PORTID, PORTTYPE, ACTION, NUMBERFROM, " +
				"NUMBERTO, ISMPP, RIDA, RCR, DIDA, DCR, ACTIONDATE, ASSIGNEEIDA, ASSIGNEECR) " +
				"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			// Validar campos obligatorios
			if (numero.getPortId() == null || numero.getNumberFrom() == null) {
				throw new SQLException("Campos obligatorios (PORTID o NUMBERFROM) son nulos");
			}
			ps.setString(1, numero.getPortId());
			ps.setString(2, numero.getPortType() != null ? numero.getPortType() : "");
			ps.setString(3, (numero.getAction() != null ? numero.getAction() : "") + "_test");
			ps.setString(4, numero.getNumberFrom());
			ps.setString(5, numero.getNumberTo() != null ? numero.getNumberTo() : "");
			ps.setString(6, numero.getIsMpp() != null ? numero.getIsMpp() : "N");
			ps.setInt(7, toInt(numero.getRida()));
			ps.setInt(8, toInt(numero.getRcr()));
			ps.setObject(9, numero.getDida(), java.sql.Types.INTEGER);
			ps.setObject(10, numero.getDcr(), java.sql.Types.INTEGER);
			ps.setTimestamp(11, numero.getActionDate() != null ? numero.getActionDate() : new java.sql.Timestamp(System.currentTimeMillis()));
			ps.setBigDecimal(12, numero.getAssigneeIda() != null ? numero.getAssigneeIda() : BigDecimal.ZERO);
			ps.setBigDecimal(13, numero.getAssigneeCr() != null ? numero.getAssigneeCr() : BigDecimal.ZERO);

			ps.executeUpdate();
		}
	}

	private int toInt(Object value) {
		if (value == null) return 0;
		try {
			return new BigDecimal(value.toString()).intValue();
		} catch (NumberFormatException e) {
			LOGGER.warn("Error al convertir valor numérico: {}", value);
			return 0;
		}
	}

	private void releaseLock() {
		if (lock.isHeldByCurrentThread()) {
			lock.unlock();
			LOGGER.debug("Lock liberado");
		}
	}

	public int procesarCanceladosConLimpiezaPorFecha(List<NumeroCancelado> numeros) throws SQLException {
		int totalInsertados = 0;
		int totalBorradosDePortados = 0;
		Connection conn = null;
		PreparedStatement psInsert = null;
		PreparedStatement psDeletePortados = null;

		String insertSQL = "INSERT INTO PORT_NUM_CANCELADO " +
				"(PORTID, PORTTYPE, ACTION, NUMBERFROM, NUMBERTO, ISMPP, RIDA, RCR, DIDA, DCR, ACTIONDATE, ASSIGNEEIDA, ASSIGNEECR) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		String deletePortadosSQL = "DELETE FROM PORT_NUM_PORTADO WHERE NUMBERFROM = ?";

		int batchCountInsert = 0;
		int batchCountDelete = 0;

		try {
			conn = dataSource.getConnection();
			psInsert = conn.prepareStatement(insertSQL);
			psDeletePortados = conn.prepareStatement(deletePortadosSQL);

			for (NumeroCancelado num : numeros) {
				// INSERT
				psInsert.setString(1, num.getPortId());
				psInsert.setString(2, num.getPortType());
				psInsert.setString(3, num.getAction());
				psInsert.setString(4, num.getNumberFrom());
				psInsert.setString(5, num.getNumberTo());
				psInsert.setString(6, num.getIsMpp());
				psInsert.setBigDecimal(7, num.getRida());
				psInsert.setBigDecimal(8, num.getRcr());
				psInsert.setBigDecimal(9, num.getDida());
				psInsert.setBigDecimal(10, num.getDcr());
				psInsert.setTimestamp(11, num.getActionDate());
				psInsert.setBigDecimal(12, num.getAssigneeIda());
				psInsert.setBigDecimal(13, num.getAssigneeCr());
				psInsert.addBatch();
				batchCountInsert++;

				// DELETE de portados
				psDeletePortados.setString(1, num.getNumberFrom());
				psDeletePortados.addBatch();
				batchCountDelete++;

				if (batchCountInsert >= BATCH_SIZE) {
					int[] insertResults = psInsert.executeBatch();
					for (int r : insertResults) if (r > 0) totalInsertados++;
					psInsert.clearBatch();
					batchCountInsert = 0;
				}

				if (batchCountDelete >= BATCH_SIZE) {
					int[] deleteResults = psDeletePortados.executeBatch();
					for (int r : deleteResults) if (r > 0) totalBorradosDePortados++;
					psDeletePortados.clearBatch();
					batchCountDelete = 0;
				}
			}

			// Lotes finales
			if (batchCountInsert > 0) {
				int[] insertResults = psInsert.executeBatch();
				for (int r : insertResults) if (r > 0) totalInsertados++;
				psInsert.clearBatch();
			}

			if (batchCountDelete > 0) {
				int[] deleteResults = psDeletePortados.executeBatch();
				for (int r : deleteResults) if (r > 0) totalBorradosDePortados++;
				psDeletePortados.clearBatch();
			}

			LOGGER.info("Proceso finalizado: {} insertados en cancelados, {} borrados de portados.", totalInsertados, totalBorradosDePortados);
		} catch (SQLException ex) {
			LOGGER.error("Error global al procesar cancelados con limpieza: {}", ex.getMessage(), ex);
			throw ex;
		} finally {
			try { if (psInsert != null) psInsert.close(); } catch (Exception e) {}
			try { if (psDeletePortados != null) psDeletePortados.close(); } catch (Exception e) {}
			try { if (conn != null) conn.close(); } catch (Exception e) {}
		}

		return totalInsertados;
	}

	public int deleteCanceladosPorFechaStr(String fechaStr) throws SQLException {
		String sql = "DELETE FROM PORT_NUM_CANCELADO WHERE TRUNC(actiondate) = TO_DATE(?, 'YYYY-MM-DD')";
		int total = 0;

		try (Connection conn = dataSource.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setString(1, fechaStr);
			total = ps.executeUpdate();

		} catch (SQLException ex) {
			LOGGER.error("Error SQL eliminando cancelados por fecha {}", fechaStr, ex);
			throw ex;
		}

		LOGGER.info("Cancelados eliminados por fecha {}: {}", fechaStr, total);
		return total;
	}

	// =========================================================
	// SNAPSHOTS HIBRIDO FALLBACK 27.08.2025
	// =========================================================

	// =======================================
	// Paso 1: Insertar Snapshot ORIGEN (cancelados)
	// =======================================
	public void insertSnapshotOrigen(List<NumeroCancelado> lote) {
		String sql = "INSERT INTO SNAPSHOT_PORT_NUM_CANCELADO (" +
				"PORTID, PORTTYPE, ACTION, NUMBERFROM, NUMBERTO, ISMPP," +
				"RIDA, RCR, DIDA, DCR, ACTIONDATE, ASSIGNEEIDA, ASSIGNEECR, ESTADO) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'Origen')";

		try (Connection conn = dataSource.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {

			for (NumeroCancelado n : lote) {
				ps.setString(1, n.getPortId());
				ps.setString(2, n.getPortType());
				ps.setString(3, n.getAction());
				ps.setString(4, n.getNumberFrom());
				ps.setString(5, n.getNumberTo());
				ps.setString(6, n.getIsMpp());

				if (n.getRida() != null) ps.setBigDecimal(7, n.getRida()); else ps.setNull(7, Types.NUMERIC);
				if (n.getRcr() != null)  ps.setBigDecimal(8, n.getRcr());  else ps.setNull(8, Types.NUMERIC);
				if (n.getDida() != null) ps.setBigDecimal(9, n.getDida()); else ps.setNull(9, Types.NUMERIC);
				if (n.getDcr() != null)  ps.setBigDecimal(10, n.getDcr()); else ps.setNull(10, Types.NUMERIC);

				if (n.getActionDate() != null)
					ps.setTimestamp(11, new Timestamp(n.getActionDate().getTime()));
				else
					ps.setNull(11, Types.TIMESTAMP);

				if (n.getAssigneeIda() != null) ps.setBigDecimal(12, n.getAssigneeIda()); else ps.setNull(12, Types.NUMERIC);
				if (n.getAssigneeCr() != null)  ps.setBigDecimal(13, n.getAssigneeCr());  else ps.setNull(13, Types.NUMERIC);

				ps.addBatch();
			}
			ps.executeBatch();
			LOGGER.info("Snapshot ORIGEN (cancelados) insertado: {} registros", lote.size());
		} catch (Exception e) {
			LOGGER.error("Error insertando snapshot origen cancelados", e);
		}
	}

	// =======================================
	// Paso 2: Borrar de PORT_NUM_CANCELADO
	// =======================================
	public void deleteCanceladosLote(List<NumeroCancelado> lote) {
		String sql = "DELETE FROM PORT_NUM_CANCELADO WHERE NUMBERFROM=? AND ACTIONDATE=?";
		try (Connection conn = dataSource.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {

			for (NumeroCancelado n : lote) {
				ps.setString(1, n.getNumberFrom());
				ps.setTimestamp(2, new Timestamp(n.getActionDate().getTime()));
				ps.addBatch();
			}
			ps.executeBatch();
			LOGGER.info("Cancelados borrados en PORT_NUM_CANCELADO: {} registros", lote.size());
		} catch (Exception e) {
			LOGGER.error("Error borrando cancelados lote", e);
		}
	}

	// =======================================
	// Paso 3: Insertar en PORT_NUM_CANCELADO (batch con fallback)
	// =======================================
	public int upsertHibridoCancelados(List<NumeroCancelado> origen, int totalFilasCsv) throws SQLException {
		int totalExitos = 0;

		try (Connection conn = dataSource.getConnection();
			 Statement st = conn.createStatement()) {

			// 1. Validar snapshot vs total CSV
			int totalSnapshot = 0;
			try (ResultSet rs = st.executeQuery(
					"SELECT COUNT(*) FROM SNAPSHOT_PORT_NUM_CANCELADO")) {
				if (rs.next()) {
					totalSnapshot = rs.getInt(1);
				}
			}

			LOGGER.info("Validación snapshot vs CSV Cancelados -> Snapshot={}, CSV={}", totalSnapshot, totalFilasCsv);

			// 2. Si coincide → intentar MERGE express único desde snapshot
			if (totalSnapshot == totalFilasCsv) {
				LOGGER.info("Snapshot completo Cancelados. Ejecutando MERGE express (único desde snapshot)...");

				String sql =
						"MERGE INTO PORT_NUM_CANCELADO t " +
								"USING ( " +
								"  SELECT portid, porttype, action, numberfrom, numberto, ismpp, " +
								"         rida, rcr, dida, dcr, actiondate, assigneeida, assigneecr " +
								"  FROM SNAPSHOT_PORT_NUM_CANCELADO " +
								") s " +
								"ON (t.numberfrom = s.numberfrom AND t.actiondate = s.actiondate) " + // match compuesto
								"WHEN MATCHED THEN UPDATE SET " +
								"   t.portid       = s.portid, " +
								"   t.porttype     = s.porttype, " +
								"   t.action       = s.action, " +
								"   t.numberto     = s.numberto, " +
								"   t.ismpp        = s.ismpp, " +
								"   t.rida         = s.rida, " +
								"   t.rcr          = s.rcr, " +
								"   t.dida         = s.dida, " +
								"   t.dcr          = s.dcr, " +
								"   t.assigneeida  = s.assigneeida, " +
								"   t.assigneecr   = s.assigneecr " +
								"WHEN NOT MATCHED THEN INSERT " +
								" (portid, porttype, action, numberfrom, numberto, ismpp, " +
								"  rida, rcr, dida, dcr, actiondate, assigneeida, assigneecr) " +
								"VALUES (s.portid, s.porttype, s.action, s.numberfrom, s.numberto, s.ismpp, " +
								"        s.rida, s.rcr, s.dida, s.dcr, s.actiondate, s.assigneeida, s.assigneecr)";

				try {
					totalExitos = st.executeUpdate(sql);
					LOGGER.info("MERGE express ejecutado correctamente en Cancelados. Filas afectadas={}", totalExitos);

				} catch (SQLException ex) {
					//Aquí se cambia de estrategia
					LOGGER.error("MERGE express falló en Cancelados → rollback automático aplicado. "
							+ "Reintentando con modo seguro (batch+fallback).", ex);

					totalExitos = insertCanceladosBatchFallback(origen);
				}

			} else {
				// 3. Si NO coincide → fallback directo
				LOGGER.warn("Snapshot incompleto en Cancelados ({} vs {}). Ejecutando modo seguro (batch+fallback)...",
						totalSnapshot, totalFilasCsv);

				totalExitos = insertCanceladosBatchFallback(origen);
			}
		}

		return totalExitos;
	}

	// =======================================
	// Paso BATCH: inserción cancelados con fallback fila a fila
	// =======================================
	public int insertCanceladosBatchFallback(List<NumeroCancelado> lote) {
		final int BATCH_SIZE = 5000;
		int totalInsertados = 0;
		String sql = "INSERT INTO PORT_NUM_CANCELADO (" +
				"PORTID, PORTTYPE, ACTION, NUMBERFROM, NUMBERTO, ISMPP," +
				"RIDA, RCR, DIDA, DCR, ACTIONDATE, ASSIGNEEIDA, ASSIGNEECR) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try (Connection conn = dataSource.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {

			List<NumeroCancelado> bloque = new ArrayList<>();
			for (NumeroCancelado n : lote) {
				try {
					ps.setString(1, n.getPortId());
					ps.setString(2, n.getPortType());
					ps.setString(3, n.getAction());
					ps.setString(4, n.getNumberFrom());
					ps.setString(5, n.getNumberTo());
					ps.setString(6, n.getIsMpp());
					ps.setBigDecimal(7, n.getRida());
					ps.setBigDecimal(8, n.getRcr());
					ps.setBigDecimal(9, n.getDida());
					ps.setBigDecimal(10, n.getDcr());
					ps.setTimestamp(11, new Timestamp(n.getActionDate().getTime()));
					ps.setBigDecimal(12, n.getAssigneeIda());
					ps.setBigDecimal(13, n.getAssigneeCr());
					ps.addBatch();
					bloque.add(n);

					if (bloque.size() == BATCH_SIZE) {
						totalInsertados += ejecutarBatchConFallback(conn, sql, ps, bloque);
						bloque.clear();
					}
				} catch (Exception exFila) {
					LOGGER.warn("Error preparando fila cancelado {}: {}", n.getNumberFrom(), exFila.getMessage());
				}
			}

			// pendientes
			if (!bloque.isEmpty()) {
				totalInsertados += ejecutarBatchConFallback(conn, sql, ps, bloque);
			}

		} catch (Exception e) {
			LOGGER.error("Error general insertando cancelados batch", e);
		}
		return totalInsertados;
	}

	private int ejecutarBatchConFallback(Connection conn, String sql, PreparedStatement ps, List<NumeroCancelado> bloque) {
		int insertados = 0;
		try {
			ps.executeBatch();
			insertados = bloque.size();
			LOGGER.debug("Batch {} cancelados insertados OK", insertados);
		} catch (Exception exBatch) {
			LOGGER.error("Error batch {} cancelados, fallback fila a fila", bloque.size(), exBatch);
			for (NumeroCancelado f : bloque) {
				try (PreparedStatement psSingle = conn.prepareStatement(sql)) {
					psSingle.setString(1, f.getPortId());
					psSingle.setString(2, f.getPortType());
					psSingle.setString(3, f.getAction());
					psSingle.setString(4, f.getNumberFrom());
					psSingle.setString(5, f.getNumberTo());
					psSingle.setString(6, f.getIsMpp());
					psSingle.setBigDecimal(7, f.getRida());
					psSingle.setBigDecimal(8, f.getRcr());
					psSingle.setBigDecimal(9, f.getDida());
					psSingle.setBigDecimal(10, f.getDcr());
					psSingle.setTimestamp(11, new Timestamp(f.getActionDate().getTime()));
					psSingle.setBigDecimal(12, f.getAssigneeIda());
					psSingle.setBigDecimal(13, f.getAssigneeCr());
					psSingle.executeUpdate();
					insertados++;

					// log cada 10k
					if (insertados % 10000 == 0) {
						LOGGER.info("Escritos {} registros en XML (cancelados)...", insertados);
					}

				} catch (Exception exFila) {
					LOGGER.warn("Fallo insertando cancelado {} en fallback: {}", f.getNumberFrom(), exFila.getMessage());
				}
			}
		}
		return insertados;
	}


	// =======================================
	// Paso 4: Eliminar de PORT_NUM_PORTADO (híbrido: express + batch+fallback)
	// =======================================
	public int deleteHibridoPortados(List<NumeroCancelado> origen, int totalFilasCsv) throws SQLException {
		int totalEliminados = 0;

		try (Connection conn = dataSource.getConnection();
			 Statement st = conn.createStatement()) {

			// 1. Validar snapshot vs total CSV
			int totalSnapshot = 0;
			try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM SNAPSHOT_PORT_NUM_CANCELADO")) {
				if (rs.next()) {
					totalSnapshot = rs.getInt(1);
				}
			}

			LOGGER.info("Validación snapshot vs CSV (delete portados) -> Snapshot={}, CSV={}", totalSnapshot, totalFilasCsv);

			// 2. Si coincide → intentar DELETE express desde snapshot
			if (totalSnapshot == totalFilasCsv) {
				LOGGER.info("Snapshot completo. Ejecutando DELETE express en PORT_NUM_PORTADO...");

				String sql =
						"DELETE FROM PORT_NUM_PORTADO p " +
								"WHERE EXISTS ( " +
								"   SELECT 1 FROM SNAPSHOT_PORT_NUM_CANCELADO s " +
								"   WHERE s.numberfrom = p.numberfrom " +
								"     AND s.actiondate = p.actiondate " +
								")";

				try {
					totalEliminados = st.executeUpdate(sql);
					LOGGER.info("DELETE express ejecutado correctamente. Filas eliminadas={}", totalEliminados);

				} catch (SQLException ex) {
					LOGGER.error("DELETE express falló → rollback automático aplicado. "
							+ "Reintentando con modo seguro (batch+fallback).", ex);

					totalEliminados = deleteFromPortadosBatchFallback(origen);
				}

			} else {
				// 3. Si NO coincide → fallback directo
				LOGGER.warn("Snapshot incompleto ({} vs {}). Ejecutando delete batch+fallback...",
						totalSnapshot, totalFilasCsv);

				totalEliminados = deleteFromPortadosBatchFallback(origen);
			}
		}

		return totalEliminados;
	}

	// =======================================
	// Paso 4: Batch con fallback por fila
	// =======================================
	private int deleteFromPortadosBatchFallback(List<NumeroCancelado> lote) {
		String sql = "DELETE FROM PORT_NUM_PORTADO WHERE NUMBERFROM=? AND ACTIONDATE=?";
		final int BATCH_SIZE = 5000;
		int totalEliminados = 0;

		try (Connection conn = dataSource.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {

			List<NumeroCancelado> bloque = new ArrayList<>();

			for (NumeroCancelado n : lote) {
				try {
					ps.setString(1, n.getNumberFrom());
					ps.setTimestamp(2, new Timestamp(n.getActionDate().getTime()));
					ps.addBatch();
					bloque.add(n);

					if (bloque.size() == BATCH_SIZE) {
						totalEliminados += ejecutarDeleteBatchConFallback(conn, sql, bloque);
						bloque.clear();
						ps.clearBatch();
					}
				} catch (Exception exFila) {
					LOGGER.warn("Error preparando fila portado {}: {}", n.getNumberFrom(), exFila.getMessage());
				}
			}

			// pendientes
			if (!bloque.isEmpty()) {
				totalEliminados += ejecutarDeleteBatchConFallback(conn, sql, bloque);
			}

		} catch (Exception e) {
			LOGGER.error("Error general borrando de portados", e);
		}
		return totalEliminados;
	}

	private int ejecutarDeleteBatchConFallback(Connection conn, String sql, List<NumeroCancelado> bloque) {
		int eliminados = 0;
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			for (NumeroCancelado n : bloque) {
				ps.setString(1, n.getNumberFrom());
				ps.setTimestamp(2, new Timestamp(n.getActionDate().getTime()));
				ps.addBatch();
			}
			ps.executeBatch();
			eliminados = bloque.size();
			LOGGER.debug("Batch {} portados eliminados OK", eliminados);
		} catch (Exception exBatch) {
			LOGGER.error("Error en batch delete {} portados, fallback fila a fila", bloque.size(), exBatch);
			for (NumeroCancelado f : bloque) {
				try (PreparedStatement psSingle = conn.prepareStatement(sql)) {
					psSingle.setString(1, f.getNumberFrom());
					psSingle.setTimestamp(2, new Timestamp(f.getActionDate().getTime()));
					psSingle.executeUpdate();
					eliminados++;

					// log cada 10k
					if (eliminados % 10000 == 0) {
						LOGGER.info("Escritos {} registros en XML (cancelados)...", eliminados);
					}

				} catch (Exception exFila) {
					LOGGER.warn("Fallo eliminando portado {} en fallback: {}", f.getNumberFrom(), exFila.getMessage());
				}
			}
		}
		return eliminados;
	}

	// =======================================
	// Paso 5: Updates de snapshot estadoFinalCanc
	// =======================================
	public void updateSnapshotEstadoFinalCanc() {
		try (Connection conn = dataSource.getConnection();
			 Statement st = conn.createStatement()) {
			st.executeUpdate(
					"UPDATE SNAPSHOT_PORT_NUM_CANCELADO s " +
							"SET ESTADO_FINAL_CANC = 'INSERTADO' " +
							"WHERE EXISTS (" +
							"  SELECT 1 FROM PORT_NUM_CANCELADO c " +
							"  WHERE c.NUMBERFROM = s.NUMBERFROM " +
							"    AND c.ACTIONDATE = s.ACTIONDATE)"
			);
			st.executeUpdate(
					"UPDATE SNAPSHOT_PORT_NUM_CANCELADO " +
							"SET ESTADO_FINAL_CANC = 'FALLIDO' " +
							"WHERE ESTADO_FINAL_CANC IS NULL"
			);
		} catch (Exception e) {
			LOGGER.error("Error actualizando ESTADO_FINAL_CANC", e);
		}
	}

	// =======================================
	// Paso 6: Updates de snapshot estadoFinalPort
	// =======================================
	public void updateSnapshotEstadoFinalPort() {
		try (Connection conn = dataSource.getConnection();
			 Statement st = conn.createStatement()) {
			st.executeUpdate(
					"UPDATE SNAPSHOT_PORT_NUM_CANCELADO s " +
							"SET ESTADO_FINAL_PORT = 'ELIMINADO' " +
							"WHERE EXISTS (" +
							"  SELECT 1 FROM PORT_NUM_PORTADO p " +
							"  WHERE p.NUMBERFROM = s.NUMBERFROM)"
			);
			st.executeUpdate(
					"UPDATE SNAPSHOT_PORT_NUM_CANCELADO " +
							"SET ESTADO_FINAL_PORT = 'FALLIDO' " +
							"WHERE ESTADO_FINAL_PORT IS NULL"
			);
		} catch (Exception e) {
			LOGGER.error("Error actualizando ESTADO_FINAL_PORT", e);
		}
	}

	// =======================================
	// Paso 7: Totales desde snapshot CANCELADOS
	// =======================================
	public ResultadoValidacionCSV getTotalesSnapshot() {
		ResultadoValidacionCSV resultado = new ResultadoValidacionCSV();

		String sql =
				"SELECT " +
						" COUNT(*) AS TOTAL_ORIGEN, " +
						" SUM(CASE WHEN ESTADO_FINAL_CANC = 'INSERTADO' THEN 1 ELSE 0 END) AS TOTAL_INSERTADOS, " +
						" SUM(CASE WHEN ESTADO_FINAL_PORT = 'ELIMINADO' THEN 1 ELSE 0 END) AS TOTAL_ELIMINADOS, " +
						" SUM(CASE WHEN ESTADO_FINAL_CANC = 'FALLIDO' THEN 1 ELSE 0 END) AS TOTAL_FALLIDOS_INSERCION, " +
						" SUM(CASE WHEN ESTADO_FINAL_PORT = 'FALLIDO' THEN 1 ELSE 0 END) AS TOTAL_FALLIDOS_ELIMINACION, " +
						" SUM(CASE WHEN ESTADO_FINAL_CANC = 'INSERTADO' AND ESTADO_FINAL_PORT = 'ELIMINADO' THEN 1 ELSE 0 END) AS TOTAL_PROCESADOS " +
						"FROM SNAPSHOT_PORT_NUM_CANCELADO";

		try (Connection conn = dataSource.getConnection();
			 Statement st = conn.createStatement();
			 ResultSet rs = st.executeQuery(sql)) {

			if (rs.next()) {
				//resultado.setTotalOrigenCanc(rs.getInt("TOTAL_ORIGEN"));
				resultado.setTotalInsertadosCanc(rs.getInt("TOTAL_INSERTADOS"));
				resultado.setTotalEliminadosCanc(rs.getInt("TOTAL_ELIMINADOS"));
				resultado.setTotalFallidosInsercionCanc(rs.getInt("TOTAL_FALLIDOS_INSERCION"));
				resultado.setTotalFallidosEliminacionCanc(rs.getInt("TOTAL_FALLIDOS_ELIMINACION"));
				resultado.setTotalProcesadosCanc(rs.getInt("TOTAL_PROCESADOS"));
				// 🔑 No seteamos totalFallidosCanc aquí,
				//     se calcula automáticamente en ResultadoValidacionCSV
			}

		} catch (Exception e) {
			LOGGER.error("Error obteniendo totales snapshot cancelados", e);
		}

		return resultado;
	}

	// =======================================
	// PASO 9: Genera un XML con los cancelados fallidos,
	// usando BufferedWriter + StringBuilder para máxima velocidad.
	// =======================================

	public void generarXmlFallidosCanceladosBatch(String timestampOriginal) throws Exception {
		LOGGER.info("Iniciando generación de XML con cancelados fallidos (modo batch)...");

		final String SQL_SELECT =
				"SELECT PORTID, PORTTYPE, ACTION, NUMBERFROM, NUMBERTO, ISMPP, " +
						"       RIDA, RCR, DIDA, DCR, ACTIONDATE, ASSIGNEEIDA, ASSIGNEECR " +
						"FROM SNAPSHOT_PORT_NUM_CANCELADO " +
						"WHERE ESTADO_FINAL_PORT = 'FALLIDO'";

		final String SQL_COUNT =
				"SELECT COUNT(*) FROM SNAPSHOT_PORT_NUM_CANCELADO " +
						"WHERE ESTADO_FINAL_PORT = 'FALLIDO'";

		// Ruta salida
		String basePath = paramService.getParamByName("port_XMLLOG_path.portados");
		if (StringUtils.isEmpty(basePath)) {
			LOGGER.warn("Parametro port_XMLLOG_path.portados no definido, usando directorio actual");
			basePath = ".";
		}
		File dir = new File(basePath);
		if (!dir.exists()) {
			LOGGER.warn("Directorio {} no existe, verificar configuración de parámetros", basePath);
		}

		String fechaStr = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File outFile = new File(dir, "port_num_cancelados_fallidos_" + fechaStr + ".xml");

		SimpleDateFormat sdfActionDate = new SimpleDateFormat("yyyyMMddHHmmss");

		int totalEsperado = 0;

		try (Connection conn = dataSource.getConnection();
			 Statement stCount = conn.createStatement();
			 ResultSet rsCount = stCount.executeQuery(SQL_COUNT)) {
			if (rsCount.next()) {
				totalEsperado = rsCount.getInt(1);
			}
		}

		try (Connection conn = dataSource.getConnection();
			 PreparedStatement ps = conn.prepareStatement(SQL_SELECT,
					 ResultSet.TYPE_FORWARD_ONLY,
					 ResultSet.CONCUR_READ_ONLY);
			 ResultSet rs = ps.executeQuery();
			 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					 new FileOutputStream(outFile), "UTF-8"), 131072)) { // 128 KB buffer

			ps.setFetchSize(2000);

			// Encabezado XML
			writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			writer.write("<NPCData>\n");
			writer.write("  <MessageName>Porting Data - Cancelados</MessageName>\n");
			writer.write("  <Timestamp>" + (timestampOriginal != null ? timestampOriginal : "") + "</Timestamp>\n");
			writer.write("  <NumberOfMessages>" + totalEsperado + "</NumberOfMessages>\n");
			writer.write("  <PortDataList>\n");

			int total = 0;
			StringBuilder sb = new StringBuilder(512);

			while (rs.next()) {
				total++;
				sb.setLength(0); // limpiar

				sb.append("    <PortData>\n");
				sb.append("      <PortID>").append(rs.getString("PORTID")).append("</PortID>\n");
				sb.append("      <PortType>").append(rs.getString("PORTTYPE")).append("</PortType>\n");
				sb.append("      <Action>").append(rs.getString("ACTION")).append("</Action>\n");

				sb.append("      <NumberRanges>\n");
				sb.append("        <NumberRange>\n");
				sb.append("          <NumberFrom>").append(rs.getString("NUMBERFROM")).append("</NumberFrom>\n");
				sb.append("          <NumberTo>").append(rs.getString("NUMBERTO")).append("</NumberTo>\n");
				sb.append("          <isMPP>").append(rs.getString("ISMPP")).append("</isMPP>\n");
				sb.append("        </NumberRange>\n");
				sb.append("      </NumberRanges>\n");

				sb.append("      <RIDA>").append(rs.getString("RIDA")).append("</RIDA>\n");
				sb.append("      <RCR>").append(rs.getString("RCR")).append("</RCR>\n");
				sb.append("      <DIDA>").append(rs.getString("DIDA")).append("</DIDA>\n");
				sb.append("      <DCR>").append(rs.getString("DCR")).append("</DCR>\n");

				sb.append("      <AssigneeIDA>").append(rs.getString("ASSIGNEEIDA")).append("</AssigneeIDA>\n");
				sb.append("      <AssigneeCR>").append(rs.getString("ASSIGNEECR")).append("</AssigneeCR>\n");

				Timestamp ts = rs.getTimestamp("ACTIONDATE");
				sb.append("      <ActionDate>")
						.append(ts != null ? sdfActionDate.format(ts) : "")
						.append("</ActionDate>\n");

				sb.append("    </PortData>\n");

				writer.write(sb.toString());

				// log cada 10k
				if (total % 10000 == 0) {
					LOGGER.info("Escritos {} registros en XML (cancelados)...", total);
				}
			}

			writer.write("  </PortDataList>\n");
			writer.write("</NPCData>\n");
			writer.flush();

			LOGGER.info("Archivo XML (batch) de cancelados fallidos generado: {} (Esperados={}, Generados={})",
					outFile.getAbsolutePath(), totalEsperado, total);
		}
	}

}

/*
public int insertCanceladosBatch(List<NumeroCancelado> lote) {
		String sql = "INSERT INTO PORT_NUM_CANCELADO (" +
				"PORTID, PORTTYPE, ACTION, NUMBERFROM, NUMBERTO, ISMPP," +
				"RIDA, RCR, DIDA, DCR, ACTIONDATE, ASSIGNEEIDA, ASSIGNEECR) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		final int BATCH_SIZE = 5000;
		int totalInsertados = 0;

		try (Connection conn = dataSource.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {

			List<NumeroCancelado> bloque = new ArrayList<>();

			for (NumeroCancelado n : lote) {
				try {
					ps.setString(1, n.getPortId());
					ps.setString(2, n.getPortType());
					ps.setString(3, n.getAction());
					ps.setString(4, n.getNumberFrom());
					ps.setString(5, n.getNumberTo());
					ps.setString(6, n.getIsMpp());
					ps.setBigDecimal(7, n.getRida());
					ps.setBigDecimal(8, n.getRcr());
					ps.setBigDecimal(9, n.getDida());
					ps.setBigDecimal(10, n.getDcr());
					ps.setTimestamp(11, new Timestamp(n.getActionDate().getTime()));
					ps.setBigDecimal(12, n.getAssigneeIda());
					ps.setBigDecimal(13, n.getAssigneeCr());
					ps.addBatch();

					bloque.add(n);

					if (bloque.size() == BATCH_SIZE) {
						try {
							ps.executeBatch();
							totalInsertados += bloque.size();
							LOGGER.debug("Batch de {} cancelados insertado OK", bloque.size());
						} catch (Exception exBatch) {
							LOGGER.error("Error en batch de {} registros, fallback fila a fila", bloque.size(), exBatch);
							// fallback uno a uno
							for (NumeroCancelado f : bloque) {
								try (PreparedStatement psSingle = conn.prepareStatement(sql)) {
									psSingle.setString(1, f.getPortId());
									psSingle.setString(2, f.getPortType());
									psSingle.setString(3, f.getAction());
									psSingle.setString(4, f.getNumberFrom());
									psSingle.setString(5, f.getNumberTo());
									psSingle.setString(6, f.getIsMpp());
									psSingle.setBigDecimal(7, f.getRida());
									psSingle.setBigDecimal(8, f.getRcr());
									psSingle.setBigDecimal(9, f.getDida());
									psSingle.setBigDecimal(10, f.getDcr());
									psSingle.setTimestamp(11, new Timestamp(f.getActionDate().getTime()));
									psSingle.setBigDecimal(12, f.getAssigneeIda());
									psSingle.setBigDecimal(13, f.getAssigneeCr());
									psSingle.executeUpdate();
									totalInsertados++;
								} catch (Exception exFila) {
									LOGGER.warn("Fallo insertando cancelado {} en fallback: {}", f.getNumberFrom(), exFila.getMessage());
								}
							}
						}
						ps.clearBatch();
						bloque.clear();
					}

				} catch (Exception exFila) {
					LOGGER.warn("Error preparando fila cancelado {}: {}", n.getNumberFrom(), exFila.getMessage());
				}
			}

			// procesar últimos registros pendientes
			if (!bloque.isEmpty()) {
				try {
					ps.executeBatch();
					totalInsertados += bloque.size();
					LOGGER.debug("Batch final de {} cancelados insertado OK", bloque.size());
				} catch (Exception exBatch) {
					LOGGER.error("Error en batch final de {} registros, fallback fila a fila", bloque.size(), exBatch);
					for (NumeroCancelado f : bloque) {
						try (PreparedStatement psSingle = conn.prepareStatement(sql)) {
							psSingle.setString(1, f.getPortId());
							psSingle.setString(2, f.getPortType());
							psSingle.setString(3, f.getAction());
							psSingle.setString(4, f.getNumberFrom());
							psSingle.setString(5, f.getNumberTo());
							psSingle.setString(6, f.getIsMpp());
							psSingle.setBigDecimal(7, f.getRida());
							psSingle.setBigDecimal(8, f.getRcr());
							psSingle.setBigDecimal(9, f.getDida());
							psSingle.setBigDecimal(10, f.getDcr());
							psSingle.setTimestamp(11, new Timestamp(f.getActionDate().getTime()));
							psSingle.setBigDecimal(12, f.getAssigneeIda());
							psSingle.setBigDecimal(13, f.getAssigneeCr());
							psSingle.executeUpdate();
							totalInsertados++;
						} catch (Exception exFila) {
							LOGGER.warn("Fallo insertando cancelado {} en fallback: {}", f.getNumberFrom(), exFila.getMessage());
						}
					}
				}
			}

		} catch (Exception e) {
			LOGGER.error("Error general insertando cancelados", e);
		}
		return totalInsertados;
	}
 */

/*
	public int deleteFromPortados(List<NumeroCancelado> lote) {
		String sql = "DELETE FROM PORT_NUM_PORTADO WHERE NUMBERFROM=?";
		final int BATCH_SIZE = 5000;
		int totalEliminados = 0;

		try (Connection conn = dataSource.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {

			List<NumeroCancelado> bloque = new ArrayList<>();

			for (NumeroCancelado n : lote) {
				try {
					ps.setString(1, n.getNumberFrom());
					ps.addBatch();
					bloque.add(n);

					if (bloque.size() == BATCH_SIZE) {
						try {
							ps.executeBatch();
							totalEliminados += bloque.size();
							LOGGER.debug("Batch de {} portados eliminados OK", bloque.size());
						} catch (Exception exBatch) {
							LOGGER.error("Error en batch de {} eliminados, fallback fila a fila", bloque.size(), exBatch);
							for (NumeroCancelado f : bloque) {
								try (PreparedStatement psSingle = conn.prepareStatement(sql)) {
									psSingle.setString(1, f.getNumberFrom());
									psSingle.executeUpdate();
									totalEliminados++;
								} catch (Exception exFila) {
									LOGGER.warn("Fallo eliminando portado {} en fallback: {}", f.getNumberFrom(), exFila.getMessage());
								}
							}
						}
						ps.clearBatch();
						bloque.clear();
					}

				} catch (Exception exFila) {
					LOGGER.warn("Error preparando fila portado {}: {}", n.getNumberFrom(), exFila.getMessage());
				}
			}

			// procesar últimos pendientes
			if (!bloque.isEmpty()) {
				try {
					ps.executeBatch();
					totalEliminados += bloque.size();
					LOGGER.debug("Batch final de {} portados eliminados OK", bloque.size());
				} catch (Exception exBatch) {
					LOGGER.error("Error en batch final de {} eliminados, fallback fila a fila", bloque.size(), exBatch);
					for (NumeroCancelado f : bloque) {
						try (PreparedStatement psSingle = conn.prepareStatement(sql)) {
							psSingle.setString(1, f.getNumberFrom());
							psSingle.executeUpdate();
							totalEliminados++;
						} catch (Exception exFila) {
							LOGGER.warn("Fallo eliminando portado {} en fallback: {}", f.getNumberFrom(), exFila.getMessage());
						}
					}
				}
			}

		} catch (Exception e) {
			LOGGER.error("Error general borrando de portados", e);
		}
		return totalEliminados;
	}
 */

//	/**
//	 * PASO 7: Genera un XML con los cancelados fallidos,
//	 * escribiendo directamente desde la BD al archivo, sin cargar lista en memoria.
//	 */
	/*
	public void generarXmlFallidosCanceladosDirecto(String timestampOriginal) throws Exception {
		LOGGER.info("Iniciando generación de XML con cancelados fallidos (directo desde BD)...");

		final String SQL_SELECT =
				"SELECT PORTID, PORTTYPE, ACTION, NUMBERFROM, NUMBERTO, ISMPP, " +
						"       RIDA, RCR, DIDA, DCR, ACTIONDATE, ASSIGNEEIDA, ASSIGNEECR " +
						"FROM SNAPSHOT_PORT_NUM_CANCELADO " +
						"WHERE ESTADO = 'Origen' " +
						"  AND (ESTADO_FINAL_CANC = 'FALLIDO' OR ESTADO_FINAL_PORT = 'FALLIDO')";

		final String SQL_COUNT =
				"SELECT COUNT(*) FROM SNAPSHOT_PORT_NUM_CANCELADO " +
						"WHERE ESTADO = 'Origen' " +
						"  AND (ESTADO_FINAL_CANC = 'FALLIDO' OR ESTADO_FINAL_PORT = 'FALLIDO')";

		// Ruta de salida
		String basePath = paramService.getParamByName("port_XMLLOG_path.cancelados");
		if (StringUtils.isEmpty(basePath)) {
			LOGGER.warn("Parametro port_XMLLOG_path.cancelados no definido, usando directorio actual");
			basePath = ".";
		}
		File dir = new File(basePath);
		if (!dir.exists()) {
			LOGGER.warn("Directorio {} no existe, verificar configuración de parámetros", basePath);
		}

		String fechaStr = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File outFile = new File(dir, "port_num_cancelados_fallidos_" + fechaStr + ".xml");

		SimpleDateFormat sdfActionDate = new SimpleDateFormat("yyyyMMddHHmmss");

		XMLOutputFactory factory = XMLOutputFactory.newInstance();

		try (Connection conn = dataSource.getConnection();
			 Statement stCount = conn.createStatement();
			 ResultSet rsCount = stCount.executeQuery(SQL_COUNT)) {

			int totalEsperado = 0;
			if (rsCount.next()) {
				totalEsperado = rsCount.getInt(1);
			}

			try (PreparedStatement ps = conn.prepareStatement(SQL_SELECT,
					ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
				 ResultSet rs = ps.executeQuery();
				 OutputStream out = new BufferedOutputStream(new FileOutputStream(outFile), 65536)) {

				ps.setFetchSize(500);

				XMLStreamWriter writer = factory.createXMLStreamWriter(out, "UTF-8");
				writer.writeStartDocument("UTF-8", "1.0");
				writer.writeStartElement("NPCData");

				// Encabezado
				writer.writeStartElement("MessageName");
				writer.writeCharacters("Porting Data - Cancelados");
				writer.writeEndElement();

				writer.writeStartElement("Timestamp");
				writer.writeCharacters(timestampOriginal != null ? timestampOriginal : "");
				writer.writeEndElement();

				// Número de mensajes correcto
				writer.writeStartElement("NumberOfMessages");
				writer.writeCharacters(String.valueOf(totalEsperado));
				writer.writeEndElement();

				writer.writeStartElement("PortDataList");

				int total = 0;
				while (rs.next()) {
					total++;

					writer.writeStartElement("PortData");

					writer.writeStartElement("PortID");
					writer.writeCharacters(rs.getString("PORTID"));
					writer.writeEndElement();

					writer.writeStartElement("PortType");
					writer.writeCharacters(rs.getString("PORTTYPE"));
					writer.writeEndElement();

					writer.writeStartElement("Action");
					writer.writeCharacters(rs.getString("ACTION"));
					writer.writeEndElement();

					// NumberRanges -> NumberRange
					writer.writeStartElement("NumberRanges");
					writer.writeStartElement("NumberRange");

					writer.writeStartElement("NumberFrom");
					writer.writeCharacters(rs.getString("NUMBERFROM"));
					writer.writeEndElement();

					writer.writeStartElement("NumberTo");
					writer.writeCharacters(rs.getString("NUMBERTO"));
					writer.writeEndElement();

					writer.writeStartElement("isMPP");
					writer.writeCharacters(rs.getString("ISMPP"));
					writer.writeEndElement();

					writer.writeEndElement(); // </NumberRange>
					writer.writeEndElement(); // </NumberRanges>

					writer.writeStartElement("RIDA");
					writer.writeCharacters(rs.getString("RIDA"));
					writer.writeEndElement();

					writer.writeStartElement("RCR");
					writer.writeCharacters(rs.getString("RCR"));
					writer.writeEndElement();

					writer.writeStartElement("DIDA");
					writer.writeCharacters(rs.getString("DIDA"));
					writer.writeEndElement();

					writer.writeStartElement("DCR");
					writer.writeCharacters(rs.getString("DCR"));
					writer.writeEndElement();

					writer.writeStartElement("AssigneeIDA");
					writer.writeCharacters(rs.getString("ASSIGNEEIDA"));
					writer.writeEndElement();

					writer.writeStartElement("AssigneeCR");
					writer.writeCharacters(rs.getString("ASSIGNEECR"));
					writer.writeEndElement();

					writer.writeStartElement("ActionDate");
					Timestamp ts = rs.getTimestamp("ACTIONDATE");
					writer.writeCharacters(ts != null ? sdfActionDate.format(ts) : "");
					writer.writeEndElement();

					writer.writeEndElement(); // </PortData>
				}

				writer.writeEndElement(); // </PortDataList>
				writer.writeEndElement(); // </NPCData>
				writer.writeEndDocument();
				writer.flush();

				LOGGER.info("Archivo XML de cancelados fallidos generado: {} (Esperados={}, Generados={})",
						outFile.getAbsolutePath(), totalEsperado, total);
			}
		}
	}

	// =======================================
	// Obtener detalle de fallidos en snapshot cancelados
	// =======================================
	public List<NumeroCancelado> getFallidosDetalleCancelados() {
		List<NumeroCancelado> lista = new ArrayList<>();

		String sql = "SELECT PORTID, PORTTYPE, ACTION, NUMBERFROM, NUMBERTO, ISMPP, " +
				"RIDA, RCR, DIDA, DCR, ACTIONDATE, ASSIGNEEIDA, ASSIGNEECR " +
				"FROM SNAPSHOT_PORT_NUM_CANCELADO " +
				"WHERE ESTADO = 'Origen' " +
				"  AND (ESTADO_FINAL_CANC = 'Fallido' OR ESTADO_FINAL_PORT = 'Fallido')";

		try (Connection conn = dataSource.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql);
			 ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				NumeroCancelado n = new NumeroCancelado();

				n.setPortId(rs.getString("PORTID"));
				n.setPortType(rs.getString("PORTTYPE"));
				n.setAction(rs.getString("ACTION"));
				n.setNumberFrom(rs.getString("NUMBERFROM"));
				n.setNumberTo(rs.getString("NUMBERTO"));
				n.setIsMpp(rs.getString("ISMPP"));
				n.setRida(rs.getBigDecimal("RIDA"));
				n.setRcr(rs.getBigDecimal("RCR"));
				n.setDida(rs.getBigDecimal("DIDA"));
				n.setDcr(rs.getBigDecimal("DCR"));
				n.setActionDate(rs.getTimestamp("ACTIONDATE"));
				n.setAssigneeIda(rs.getBigDecimal("ASSIGNEEIDA"));
				n.setAssigneeCr(rs.getBigDecimal("ASSIGNEECR"));

				lista.add(n);
			}

		} catch (Exception e) {
			LOGGER.error("Error obteniendo detalle de fallidos en cancelados", e);
		}

		return lista;
	}

	 */


	/*
	public int upsertCanceladosBatch(List<NumeroCancelado> numeros) throws SQLException {
		String mergeSQL = "MERGE INTO PORT_NUM_CANCELADO t " +
				"USING (SELECT ? AS PORTID, ? AS PORTTYPE, ? AS ACTION, ? AS NUMBERFROM, ? AS NUMBERTO, " +
				"? AS ISMPP, ? AS RIDA, ? AS RCR, ? AS DIDA, ? AS DCR, ? AS ACTIONDATE, ? AS ASSIGNEEIDA, ? AS ASSIGNEECR FROM DUAL) s " +
				"ON (t.NUMBERFROM = s.NUMBERFROM AND TRUNC(t.ACTIONDATE) = TRUNC(s.ACTIONDATE)) " +
				"WHEN MATCHED THEN UPDATE SET " +
				"t.PORTID = s.PORTID, t.PORTTYPE = s.PORTTYPE, t.ACTION = s.ACTION, " +
				"t.NUMBERTO = s.NUMBERTO, t.ISMPP = s.ISMPP, t.RIDA = s.RIDA, t.RCR = s.RCR, " +
				"t.DIDA = s.DIDA, t.DCR = s.DCR, " +
				// "t.ACTIONDATE = s.ACTIONDATE,  <-- REMOVIDO por ORA-38104
				"t.ASSIGNEEIDA = s.ASSIGNEEIDA, t.ASSIGNEECR = s.ASSIGNEECR " +
				"WHEN NOT MATCHED THEN INSERT " +
				"(PORTID, PORTTYPE, ACTION, NUMBERFROM, NUMBERTO, ISMPP, RIDA, RCR, DIDA, DCR, ACTIONDATE, ASSIGNEEIDA, ASSIGNEECR) " +
				"VALUES (s.PORTID, s.PORTTYPE, s.ACTION, s.NUMBERFROM, s.NUMBERTO, s.ISMPP, s.RIDA, s.RCR, s.DIDA, s.DCR, s.ACTIONDATE, s.ASSIGNEEIDA, s.ASSIGNEECR)";

		LOGGER.info("Valor del mergeSQL: {}", mergeSQL);
		int total = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		int batchCount = 0;

		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(mergeSQL);

			for (NumeroCancelado nc : numeros) {
				ps.setString(1, nc.getPortId());
				ps.setString(2, nc.getPortType());
				ps.setString(3, nc.getAction());
				ps.setString(4, nc.getNumberFrom());
				ps.setString(5, nc.getNumberTo());
				ps.setString(6, nc.getIsMpp());
				ps.setBigDecimal(7, nc.getRida());
				ps.setBigDecimal(8, nc.getRcr());
				ps.setBigDecimal(9, nc.getDida());
				ps.setBigDecimal(10, nc.getDcr());
				ps.setTimestamp(11, nc.getActionDate());
				ps.setBigDecimal(12, nc.getAssigneeIda());
				ps.setBigDecimal(13, nc.getAssigneeCr());

				ps.addBatch();
				batchCount++;

				if (batchCount >= BATCH_SIZE) {
					LOGGER.info("Dentro del valor batcount antes de error int results = ps.executeBatch....");
					int[] results = ps.executeBatch();
					LOGGER.info("Salida del valor batcount después de error int results = ps.executeBatch....");
					for (int r : results) total += r;
					LOGGER.info("MERGE cancelados - lote de {} registros procesado", results.length);
					ps.clearBatch();
					batchCount = 0;
				}
			}

			if (batchCount > 0) {
				int[] results = ps.executeBatch();
				for (int r : results) total += r;
				LOGGER.info("MERGE cancelados - lote final de {} registros procesado", results.length);
				ps.clearBatch();
			}

		} catch (SQLException e) {
			LOGGER.error("Error al ejecutar upsertCanceladosBatch", e);
			throw e;
		} finally {
			try { if (ps != null) ps.close(); } catch (Exception e) {}
			try { if (conn != null) conn.close(); } catch (Exception e) {}
		}

		return total;
	}

	 */

	/*
	public int deleteDePortadosPorNumero(List<NumeroCancelado> cancelados) throws SQLException {
		String deleteSQL = "DELETE FROM PORT_NUM_PORTADO WHERE NUMBERFROM = ?";
		int totalBorrados = 0;
		int batchCount = 0;

		try (Connection conn = dataSource.getConnection();
			 PreparedStatement ps = conn.prepareStatement(deleteSQL)) {

			for (NumeroCancelado n : cancelados) {
				ps.setString(1, n.getNumberFrom());
				ps.addBatch();
				batchCount++;

				if (batchCount >= BATCH_SIZE) {
					int[] results = ps.executeBatch();
					for (int r : results) totalBorrados += r;
					ps.clearBatch();
					batchCount = 0;
				}
			}

			if (batchCount > 0) {
				int[] results = ps.executeBatch();
				for (int r : results) totalBorrados += r;
				ps.clearBatch();
			}

		}

		LOGGER.info("TOTAL portados borrados tras cancelación: {}", totalBorrados);
		return totalBorrados;
	}

	 */

	/*
	public int deleteCanceladosPorFecha(Timestamp fecha) throws SQLException {
		String sql = "DELETE FROM PORT_NUM_CANCELADO WHERE TRUNC(actiondate) = TO_DATE(?, 'YYYY-MM-DD')";
		int total = 0;

		try (Connection conn = dataSource.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {

			// Format en java como String tipo '2025-07-24'
			String fechaStr = new SimpleDateFormat("yyyy-MM-dd").format(fecha);
			ps.setString(1, fechaStr);
			total = ps.executeUpdate();

		} catch (SQLException ex) {
			LOGGER.error("Error SQL eliminando cancelados por fecha", ex);
			throw ex;
		}

		LOGGER.info("Cancelados eliminados por fecha {}: {}", fecha, total);
		return total;
	}

	 */

	/*
	public int procesarCanceladosBatch(List<NumeroCancelado> numeros) throws SQLException {
		int totalProcesados = 0;
		int totalDeletes = 0;
		int totalDeletesAfectados = 0;
		Connection conn = null;
		PreparedStatement psDelete = null;
		PreparedStatement psInsert = null;

		String deleteSQL = "DELETE FROM PORT_NUM_PORTADO WHERE NUMBERFROM = ?";
		String insertSQL = "INSERT INTO PORT_NUM_CANCELADO " +
				"(PORTID, PORTTYPE, ACTION, NUMBERFROM, NUMBERTO, ISMPP, RIDA, RCR, DIDA, DCR, ACTIONDATE, ASSIGNEEIDA, ASSIGNEECR) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		//final int BATCH_SIZE = 1000;
		int batchCountDelete = 0;
		int batchCountInsert = 0;

		try {
			conn = dataSource.getConnection();
			psDelete = conn.prepareStatement(deleteSQL);
			psInsert = conn.prepareStatement(insertSQL);

			// ====== DELETE EN BATCH ======
			for (NumeroCancelado num : numeros) {
				psDelete.setString(1, num.getNumberFrom());
				psDelete.addBatch();
				batchCountDelete++;
				// Ejecutar el batch de deletes cada BATCH_SIZE
				if (batchCountDelete >= BATCH_SIZE) {
					int[] deleteResults = psDelete.executeBatch();
					for (int del : deleteResults) if (del > 0) totalDeletesAfectados++;
					totalDeletes += deleteResults.length;
					psDelete.clearBatch();
					batchCountDelete = 0;
				}
			}
			// Ejecutar deletes restantes
			if (batchCountDelete > 0) {
				int[] deleteResults = psDelete.executeBatch();
				for (int del : deleteResults) if (del > 0) totalDeletesAfectados++;
				totalDeletes += deleteResults.length;
				psDelete.clearBatch();
			}
			LOGGER.info("Batch DELETE: Total registros en XML deleted = {}, deletes ejecutados = {}, portados afectados (borrados) = {}",
					numeros.size(), totalDeletes, totalDeletesAfectados);

			// ====== INSERT EN BATCH ======
			for (NumeroCancelado num : numeros) {
				psInsert.setString(1, num.getPortId());
				psInsert.setString(2, num.getPortType());
				psInsert.setString(3, num.getAction());
				psInsert.setString(4, num.getNumberFrom());
				psInsert.setString(5, num.getNumberTo());
				psInsert.setString(6, num.getIsMpp());
				psInsert.setBigDecimal(7, num.getRida());
				psInsert.setBigDecimal(8, num.getRcr());
				psInsert.setBigDecimal(9, num.getDida());
				psInsert.setBigDecimal(10, num.getDcr());
				psInsert.setTimestamp(11, num.getActionDate());
				psInsert.setBigDecimal(12, num.getAssigneeIda());
				psInsert.setBigDecimal(13, num.getAssigneeCr());
				psInsert.addBatch();
				batchCountInsert++;

				if (batchCountInsert >= BATCH_SIZE) {
					try {
						int[] insertResults = psInsert.executeBatch();
						int loteProcesados = 0;
						for (int r : insertResults) if (r > 0) loteProcesados++;
						totalProcesados += loteProcesados;
						LOGGER.info("Batch INSERT: {} registros insertados en cancelados.", loteProcesados);
					} catch (SQLException ex) {
						LOGGER.error("Error ejecutando batch insert en cancelados: {}", ex.getMessage(), ex);
					}
					psInsert.clearBatch();
					batchCountInsert = 0;
				}
			}
			// Ejecutar inserts restantes
			if (batchCountInsert > 0) {
				try {
					int[] insertResults = psInsert.executeBatch();
					int loteProcesados = 0;
					for (int r : insertResults) if (r > 0) loteProcesados++;
					totalProcesados += loteProcesados;
					LOGGER.info("Batch final INSERT: {} registros insertados en cancelados.", loteProcesados);
				} catch (SQLException ex) {
					LOGGER.error("Error ejecutando batch final insert en cancelados: {}", ex.getMessage(), ex);
				}
				psInsert.clearBatch();
			}

			LOGGER.info("PROCESO COMPLETO CANCELADOS: Total insertados = {}, Total a procesar = {}", totalProcesados, numeros.size());

			if (totalProcesados < numeros.size()) {
				LOGGER.warn("Alerta: No todos los registros de XML deleted se insertaron en cancelados. Esperados: {}, insertados: {}",
						numeros.size(), totalProcesados);
			}
			if (totalDeletesAfectados < numeros.size()) {
				LOGGER.warn("Alerta: No todos los numbersFrom existían en portados. Esperados: {}, borrados en portados: {}",
						numeros.size(), totalDeletesAfectados);
			}
		} catch (SQLException ex) {
			LOGGER.error("Error global al procesar cancelados batch: {}", ex.getMessage(), ex);
			throw ex;
		} finally {
			try { if (psDelete != null) psDelete.close(); } catch (Exception e) {}
			try { if (psInsert != null) psInsert.close(); } catch (Exception e) {}
			try { if (conn != null) conn.close(); } catch (Exception e) {}
		}
		return totalProcesados;
	}

	 */


	/*
	public int procesarCanceladosBatch(List<NumeroCancelado> numeros) throws SQLException {
		int totalProcesados = 0;
		Connection conn = null;
		PreparedStatement psDelete = null;
		PreparedStatement psInsert = null;

		String deleteSQL = "DELETE FROM PORT_NUM_PORTADO WHERE NUMBERFROM = ?";
		String insertSQL = "INSERT INTO PORT_NUM_CANCELADO " +
				"(PORTID, PORTTYPE, ACTION, NUMBERFROM, NUMBERTO, ISMPP, RIDA, RCR, DIDA, DCR, ACTIONDATE, ASSIGNEEIDA, ASSIGNEECR) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try {
			conn = dataSource.getConnection();
			psDelete = conn.prepareStatement(deleteSQL);
			psInsert = conn.prepareStatement(insertSQL);

			int batchCount = 0;
			final int BATCH_SIZE = 1000; // Ajusta aquí si quieres

			for (NumeroCancelado num : numeros) {
				// Eliminar de portados
				psDelete.setString(1, num.getNumberFrom());
				int deleted = psDelete.executeUpdate();

				//if (deleted > 0) {
					// Insertar en cancelados (usando los datos del objeto NumeroCancelado)
					psInsert.setString(1, num.getPortId());
					psInsert.setString(2, num.getPortType());
					psInsert.setString(3, num.getAction());
					psInsert.setString(4, num.getNumberFrom());
					psInsert.setString(5, num.getNumberTo());
					psInsert.setString(6, num.getIsMpp());
					psInsert.setBigDecimal(7, num.getRida());
					psInsert.setBigDecimal(8, num.getRcr());
					psInsert.setBigDecimal(9, num.getDida());
					psInsert.setBigDecimal(10, num.getDcr());
					psInsert.setTimestamp(11, num.getActionDate());
					psInsert.setBigDecimal(12, num.getAssigneeIda());
					psInsert.setBigDecimal(13, num.getAssigneeCr());
					psInsert.addBatch();
					batchCount++;
				//}

				// Ejecutar batch cada BATCH_SIZE registros
				if (batchCount >= BATCH_SIZE) {
					try {
						int[] results = psInsert.executeBatch();
						int loteProcesados = 0;
						for (int r : results) {
							if (r > 0) loteProcesados++;
						}
						totalProcesados += loteProcesados;
						LOGGER.info("Batch de {} procesados (insertados en cancelados).", loteProcesados);
					} catch (SQLException ex) {
						LOGGER.error("Error ejecutando batch insert en cancelados: {}", ex.getMessage(), ex);
					}
					psInsert.clearBatch();
					batchCount = 0;
				}
			}

			// Ejecutar los restantes (menos de BATCH_SIZE)
			if (batchCount > 0) {
				try {
					int[] results = psInsert.executeBatch();
					int loteProcesados = 0;
					for (int r : results) {
						if (r > 0) loteProcesados++;
					}
					totalProcesados += loteProcesados;
					LOGGER.info("Batch final de {} procesados (insertados en cancelados).", loteProcesados);
				} catch (SQLException ex) {
					LOGGER.error("Error ejecutando batch final insert en cancelados: {}", ex.getMessage(), ex);
				}
				psInsert.clearBatch();
			}

			LOGGER.info("Batch completo finalizado. Total insertados en cancelados: {}", totalProcesados);

			if (totalProcesados < numeros.size()) {
				LOGGER.warn("No todos los registros fueron procesados. Esperados: {}, afectados: {}", numeros.size(), totalProcesados);
			} else if (totalProcesados == 0) {
				LOGGER.warn("Ningún registro fue procesado en cancelados.");
			}

		} catch (SQLException ex) {
			LOGGER.error("Error global al procesar cancelados batch: {}", ex.getMessage(), ex);
		} finally {
			try { if (psDelete != null) psDelete.close(); } catch (Exception e) {}
			try { if (psInsert != null) psInsert.close(); } catch (Exception e) {}
			try { if (conn != null) conn.close(); } catch (Exception e) {}
		}

		LOGGER.info("Total de registros cancelados (borrados e insertados): {}", totalProcesados);
		return totalProcesados;
	}

	 */

	/*
	public static void delete(String numero) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		PreparedStatement ps1 = null;
		ResultSet rs = null;
		String sqlSelect = "SELECT count(1) FROM port_num_portado WHERE numberfrom=?";
		try {
			conn = ConnectionBD.getConnection();
			conn.setAutoCommit(false); // Transacción manual

			ps = conn.prepareStatement(sqlSelect);
			ps.setString(1, numero);
			rs = ps.executeQuery();
			int count = 0;
			if (rs.next()) {
				count = rs.getInt(1);
			}

			if (count > 0) {
				String sqlDelete = "DELETE FROM port_num_portado WHERE NUMBERFROM=?";
				ps1 = conn.prepareStatement(sqlDelete);
				ps1.setString(1, numero);
				ps1.executeUpdate(); // Corregido: debe ser executeUpdate, no executeQuery
				conn.commit();
				LOGGER.debug("Numero eliminado de port_num_portado: " + numero);
			}
		} catch (SQLException e) {
			// Rollback seguro
			if (conn != null) {
				try {
					if (!conn.isClosed()) {
						conn.rollback();
					}
				} catch (SQLException ex) {
					LOGGER.error("Error en rollback: ", ex);
				}
			}
			LOGGER.error("Ocurrio un Error en delete: " + e.getMessage(), e);
			throw e;
		} finally {
			if (rs != null) try { rs.close(); } catch (SQLException ignored) {}
			if (ps1 != null) try { ps1.close(); } catch (SQLException ignored) {}
			if (ps != null) try { ps.close(); } catch (SQLException ignored) {}
			if (conn != null) {
				try {
					if (!conn.isClosed()) {
						conn.setAutoCommit(true);
						conn.close();
					}
				} catch (SQLException ignored) {}
			}
		}
	}

	 */

	/**
	 * Inserta un registro en port_num_cancelado.
	 */
	/*
	public static void insert(NumeroCancelado numero) throws SQLException {
		Connection conn = null;
		PreparedStatement ps2 = null;
		String sqlInsert = "INSERT INTO port_num_cancelado (PORTID, PORTTYPE, ACTION, NUMBERFROM, NUMBERTO, ISMPP, RIDA, RCR, DIDA, DCR, ACTIONDATE, ASSIGNEEIDA, ASSIGNEECR) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try {
			conn = ConnectionBD.getConnection();
			conn.setAutoCommit(false); // Transacción manual

			ps2 = conn.prepareStatement(sqlInsert);
			ps2.setString(1, numero.getPortId());
			ps2.setString(2, numero.getPortType());
			ps2.setString(3, numero.getAction() + "_test");
			ps2.setString(4, numero.getNumberFrom());
			ps2.setString(5, numero.getNumberTo());
			ps2.setString(6, numero.getIsMpp());
			ps2.setInt(7, numero.getRida() != null ? new BigDecimal(numero.getRida().toString()).intValue() : 0);
			ps2.setInt(8, numero.getRcr() != null ? new BigDecimal(numero.getRcr().toString()).intValue() : 0);
			// DIDA
			if (numero.getDida() == null) {
				ps2.setNull(9, java.sql.Types.INTEGER);
			} else {
				ps2.setInt(9, new BigDecimal(numero.getDida().toString()).intValue());
			}
			// DCR
			if (numero.getDcr() == null) {
				ps2.setNull(10, java.sql.Types.INTEGER);
			} else {
				ps2.setInt(10, new BigDecimal(numero.getDcr().toString()).intValue());
			}
			ps2.setTimestamp(11, numero.getActionDate());
			ps2.setBigDecimal(12, numero.getAssigneeIda());
			ps2.setBigDecimal(13, numero.getAssigneeCr());
			ps2.executeUpdate(); // Corregido: debe ser executeUpdate, no executeQuery
			conn.commit();
			LOGGER.debug("Numero insertado en port_num_cancelado: " + numero.getNumberFrom());
		} catch (SQLException e) {
			// Rollback seguro
			if (conn != null) {
				try {
					if (!conn.isClosed()) {
						conn.rollback();
					}
				} catch (SQLException ex) {
					LOGGER.error("Error en rollback: ", ex);
				}
			}
			LOGGER.error("Ocurrio un Error en insert: " + e.getMessage(), e);
			throw e;
		} finally {
			if (ps2 != null) try { ps2.close(); } catch (SQLException ignored) {}
			if (conn != null) {
				try {
					if (!conn.isClosed()) {
						conn.setAutoCommit(true);
						conn.close();
					}
				} catch (SQLException ignored) {}
			}
		}
	}

	 */

	/*
	public static void delete(String numero) throws SQLException {
//		conn = null;
		conn = ConnectionBD.getConnection();
//		Connection conn = ConnectionBD.getNewConnection();
		PreparedStatement ps = null;
		PreparedStatement ps1 = null;
		ResultSet rs = null;
		String sqlSelect = "Select count(1) from port_num_portado where numberfrom=?";

		try {
			ps = conn.prepareStatement(sqlSelect);
			ps.setString(1, numero);
			rs = ps.executeQuery();
			int count = 0;
			while (rs.next()) {
				count = rs.getInt(1);
			}
			if (count > 0) {
//				LOGGER.debug("<--Existe la numeracion en port_num_portado, por lo tanto se elimina---->");
				String sqlDelete = "delete port_num_portado WHERE NUMBERFROM=?";
				ps1 = conn.prepareStatement(sqlDelete);
				// WHERE
				ps1.setString(1, numero);
				ps1.executeQuery();
//				LOGGER.debug("<--Numero Eliminado:" + numero);
				conn.commit();
				ps1.close();
			}
			ps.close();
			rs.close();
//			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			if (rs != null) {
				rs.close();
			}
			if (ps != null) {
				ps.close();
			}
			if (ps1 != null) {
				ps1.close();
			}			
//			if (conn != null) {
//				conn.close();
//			}
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (ps != null) {
				ps.close();
			}
			if (ps1 != null) {
				ps1.close();
			}
//			if (conn != null) {
//				conn.close();
//			}
//			closed();//Forzamos a cerrar conexion -> Perfonrmance del servidor
		}

	}

	public static void insert(NumeroCancelado numero) throws SQLException {
		LOGGER.debug("<--Existe la numeracion :" + numero.getNumberFrom()
				+ " se eliminó de port_num_portado, por lo tanto se inserta en port_num_cancelado ---->");
		PreparedStatement ps2 = null;
//		Connection conn = ConnectionBD.getNewConnection();
		String sqlInsert = "Insert into port_num_cancelado (PORTID," + "PORTTYPE," + "ACTION," + "NUMBERFROM,"
				+ "NUMBERTO," + "ISMPP," + "RIDA," + "RCR," + "DIDA," + "DCR," + "ACTIONDATE," + "ASSIGNEEIDA,"
				+ "ASSIGNEECR)" + "values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try {
			
//			conn = null;
			conn = ConnectionBD.getConnection();
			ps2 = conn.prepareStatement(sqlInsert);
			// PORTID
			ps2.setString(1, numero.getPortId());
			// PORTTYPE
			ps2.setString(2, numero.getPortType());
			// ACTION
			ps2.setString(3, numero.getAction()+"_test");
			// NUMBERFROM
			ps2.setString(4, numero.getNumberFrom());
			// NUMBERTO
			ps2.setString(5, numero.getNumberTo());
			// ISMPP
			ps2.setString(6, numero.getIsMpp());
			// RIDA
			ps2.setInt(7, new BigDecimal(numero.getRida().toString()).intValue());
			// RCR
			ps2.setInt(8, new BigDecimal(numero.getRcr().toString()).intValue());
			// DIDA
			if (numero.getDida() == null) {
				ps2.setNull(9, java.sql.Types.INTEGER);
			} else {
				ps2.setInt(9, new BigDecimal(numero.getDida().toString()).intValue());
			}
			// DCR
			if (numero.getDcr() == null) {
				ps2.setNull(10, java.sql.Types.INTEGER);
			} else {
				ps2.setInt(10, new BigDecimal(numero.getDcr().toString()).intValue());

			}
			// ACTIONDATE
			ps2.setTimestamp(11, numero.getActionDate());
			// ASSIGNEEIDA
			ps2.setBigDecimal(12, numero.getAssigneeIda());
			// ASSIGNEECR
			ps2.setBigDecimal(13, numero.getAssigneeCr());
			ps2.executeQuery();
//			LOGGER.debug("<--Numero Insertado:" + numero.getNumberFrom());
			conn.commit();
			ps2.close();
//			conn.close();
		} catch (SQLException e) {
			if (ps2 != null) {
				ps2.close();
			}
//			if (conn != null) {
//				conn.close();
//			}
			LOGGER.error("<--Ocurrio un Error: " + e.getMessage());
		} finally {
			if (ps2 != null) {
				ps2.close();
			}
//			if (conn != null) {
//				conn.close();
//			}
//			closed();//Forzamos a cerrar conexion -> Perfonrmance del servidor
		}

	}

	 */

//	public static void closed() {
//		try {
////			if (!conn.isClosed()) {
////				conn.close();
////			}
//			if (conn!=null) {
//				conn.close();
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//	}


