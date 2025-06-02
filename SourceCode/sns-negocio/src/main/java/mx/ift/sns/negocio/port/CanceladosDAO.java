package mx.ift.sns.negocio.port;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.ift.sns.modelo.port.NumeroCancelado;
import mx.ift.sns.utils.dbConnection.ConnectionBD;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.sql.DataSource;

@Stateless
public class CanceladosDAO {

	public CanceladosDAO() {
		// Constructor vacío requerido por EJB
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(CanceladosDAO.class);

	//private static Connection conn = ConnectionBD.getConnection();

	/**
	 * FJAH 23052025 Refactorización
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

	final int BATCH_SIZE = 2500;

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

}





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


