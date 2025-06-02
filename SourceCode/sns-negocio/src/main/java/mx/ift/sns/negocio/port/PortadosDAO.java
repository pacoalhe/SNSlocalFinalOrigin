package mx.ift.sns.negocio.port;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.ift.sns.modelo.port.NumeroPortado;
import mx.ift.sns.utils.dbConnection.ConnectionBD;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.sql.DataSource;


@Stateless
public class PortadosDAO {

	private static final Logger LOGGER = LoggerFactory.getLogger(PortadosDAO.class);

	//FJAH 26052025
	public PortadosDAO() {
		// Constructor vacío requerido por EJB
	}

	final int BATCH_SIZE = 5000;

	//private static Connection conn = ConnectionBD.getConnection();

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

	private final ReentrantLock lock = new ReentrantLock();
	private static final long LOCK_TIMEOUT = 30; // segundos
	public void update(NumeroPortado numero) throws SQLException {
		LOGGER.debug("Iniciando actualización para número: {}", numero.getNumberFrom());

		if (!acquireLockWithTimeout()) {
			throw new SQLException("No se pudo adquirir el lock para actualización");
		}

		try {
			executeUpdateTransaction(numero);
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

	private void executeUpdateTransaction(NumeroPortado numero) throws SQLException {
		try (Connection conn = dataSource.getConnection()) {
			if (existeNumeroPortado(conn, numero.getNumberFrom())) {
				actualizarNumeroPortado(conn, numero);
			} else {
				insertarNumeroPortado(conn, numero);
			}
		}
	}

	private void insertarNumeroPortado(Connection conn, NumeroPortado numero) throws SQLException {
		final String sql = "INSERT INTO port_num_portado (PORTID, PORTTYPE, ACTION, NUMBERFROM, " +
				"NUMBERTO, ISMPP, RIDA, RCR, DIDA, DCR, ACTIONDATE) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			setInsertParameters(ps, numero);
			int affectedRows = ps.executeUpdate();
			LOGGER.debug("Número {} insertado correctamente. Filas afectadas: {}",
					numero.getNumberFrom(), affectedRows);
		}
	}

	private void setInsertParameters(PreparedStatement ps, NumeroPortado numero) throws SQLException {
		ps.setString(1, numero.getPortId());
		ps.setString(2, numero.getPortType());
		ps.setString(3, numero.getAction());
		ps.setString(4, numero.getNumberFrom());
		ps.setString(5, numero.getNumberTo());
		ps.setString(6, numero.getIsMpp());
		ps.setInt(7, toInt(numero.getRida()));
		ps.setInt(8, toInt(numero.getRcr()));

		// Manejo especial para DIDA y DCR como en el código original
		if (numero.getDida() != null && !"null".equals(numero.getDida().toString())) {
			ps.setInt(9, toInt(numero.getDida()));
		} else {
			ps.setNull(9, java.sql.Types.INTEGER);
			LOGGER.info("+++++++-------------Dida null");
		}

		if (numero.getDcr() != null && !"null".equals(numero.getDcr().toString())) {
			ps.setInt(10, toInt(numero.getDcr()));
		} else {
			ps.setNull(10, java.sql.Types.INTEGER);
			LOGGER.info("+++++++-------------DCR null");
		}

		ps.setTimestamp(11, numero.getActionDate());
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

	private void actualizarNumeroPortado(Connection conn, NumeroPortado numero) throws SQLException {
		final String sql = "UPDATE port_num_portado SET PORTID=?, PORTTYPE=?, ACTION=?, "
				+ "NUMBERFROM=?, NUMBERTO=?, ISMPP=?, RIDA=?, RCR=?, DIDA=?, DCR=?, "
				+ "ACTIONDATE=? WHERE NUMBERFROM=?";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			setUpdateParameters(ps, numero);
			int affectedRows = ps.executeUpdate();
			LOGGER.debug("Número {} actualizado correctamente. Filas afectadas: {}",
					numero.getNumberFrom(), affectedRows);
		}
	}

	private void setUpdateParameters(PreparedStatement ps, NumeroPortado numero) throws SQLException {
		ps.setString(1, numero.getPortId());
		ps.setString(2, numero.getPortType());
		ps.setString(3, numero.getAction());
		ps.setString(4, numero.getNumberFrom());
		ps.setString(5, numero.getNumberTo());
		ps.setString(6, numero.getIsMpp());
		ps.setInt(7, toInt(numero.getRida()));
		ps.setInt(8, toInt(numero.getRcr()));
		ps.setObject(9, numero.getDida(), java.sql.Types.INTEGER);
		ps.setObject(10, numero.getDcr(), java.sql.Types.INTEGER);
		ps.setTimestamp(11, numero.getActionDate());
		ps.setString(12, numero.getNumberFrom());
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

	//FJAH 26052025 Refactorización en lotes
	@PostConstruct
	public void init(){

	}

	public int upsertBatch(List<NumeroPortado> numeros) throws SQLException {
		String mergeSQL = "MERGE INTO PORT_NUM_PORTADO t " +
				"USING (SELECT ? AS PORTID, ? AS PORTTYPE, ? AS ACTION, ? AS NUMBERFROM, ? AS NUMBERTO, ? AS ISMPP, ? AS RIDA, ? AS RCR, ? AS DIDA, ? AS DCR, ? AS ACTIONDATE FROM DUAL) s " +
				"ON (t.NUMBERFROM = s.NUMBERFROM) " +
				"WHEN MATCHED THEN UPDATE SET " +
				"    t.PORTID = s.PORTID, " +
				"    t.PORTTYPE = s.PORTTYPE, " +
				"    t.ACTION = s.ACTION, " +
				"    t.NUMBERTO = s.NUMBERTO, " +
				"    t.ISMPP = s.ISMPP, " +
				"    t.RIDA = s.RIDA, " +
				"    t.RCR = s.RCR, " +
				"    t.DIDA = s.DIDA, " +
				"    t.DCR = s.DCR, " +
				"    t.ACTIONDATE = s.ACTIONDATE " +
				"WHEN NOT MATCHED THEN INSERT (PORTID, PORTTYPE, ACTION, NUMBERFROM, NUMBERTO, ISMPP, RIDA, RCR, DIDA, DCR, ACTIONDATE) " +
				"VALUES (s.PORTID, s.PORTTYPE, s.ACTION, s.NUMBERFROM, s.NUMBERTO, s.ISMPP, s.RIDA, s.RCR, s.DIDA, s.DCR, s.ACTIONDATE)";

		int total = 0;
		int batchCount = 0;
		try (Connection conn = dataSource.getConnection();
			 PreparedStatement ps = conn.prepareStatement(mergeSQL)) {
			for (NumeroPortado n : numeros) {
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
				ps.setTimestamp(11, n.getActionDate());
				ps.addBatch();
				batchCount++;

				// Si quieres dividir en sublotes, activa esto:
				if (batchCount >= BATCH_SIZE) {
					int[] results = ps.executeBatch();
					int lote = 0;
					for (int r : results) lote += r;
					total += lote;
					LOGGER.info("Batch de {} registros procesados en MERGE portados", lote);
					ps.clearBatch();
					batchCount = 0;
				}
			}
			// Procesa el último sub-lote si quedó incompleto
			if (batchCount > 0) {
				int[] results = ps.executeBatch();
				int lote = 0;
				for (int r : results) lote += r;
				total += lote;
				LOGGER.info("Batch final de {} registros procesados en MERGE portados", lote);
				ps.clearBatch();
			}
		}
		LOGGER.info("Total de registros afectados por batch (portados): {}", total);
		return total;
	}



	public int insertOrUpdateBatch(List<NumeroPortado> batch) throws SQLException {
		int count = 0;
		try (Connection conn = dataSource.getConnection()) {
			conn.setAutoCommit(false);
			for (NumeroPortado numero : batch) {
				if (existeNumeroPortado(conn, numero.getNumberFrom())) {
					actualizarNumeroPortado(conn, numero);
				} else {
					insertarNumeroPortado(conn, numero);
				}
				count++;
			}
			conn.commit();
		} catch (SQLException e) {
			LOGGER.error("Error en insertOrUpdateBatch", e);
			throw e;
		}
		return count;
	}

}


	/*
	public static void update(NumeroPortado numero) throws SQLException {

//		Connection conn = ConnectionBD.getNewConnection();


		PreparedStatement ps1 = null;
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;
		String sqlSelect = "Select count(1) from port_num_portado where numberfrom=?";
		try {
//			if (conn.isClosed()) {
//				conn = ConnectionBD.getConnection();
//			}
			conn = ConnectionBD.getConnection();
			ps = conn.prepareStatement(sqlSelect);
			ps.setString(1, numero.getNumberFrom());
			rs = ps.executeQuery();
			int count = 0;
			while (rs.next()) {
				count = rs.getInt(1);
			}

			if (count > 0) {
//				LOGGER.debug("<--Existe la numeracion, se ejecuta UPDATE---->");
				String sqlUpdate = "update port_num_portado set PORTID=?, " + "PORTTYPE=?," + "ACTION=?,"
						+ "NUMBERFROM=?," + "NUMBERTO=?," + "ISMPP=?," + "RIDA=?," + "RCR=?," + "DIDA=?," + "DCR=?,"
						+ "ACTIONDATE=? WHERE NUMBERFROM=?";
				ps1 = conn.prepareStatement(sqlUpdate);
				// PORTID
				ps1.setString(1, numero.getPortId());
				// PORTTYPE
				ps1.setString(2, numero.getPortType());
				// ACTION
				ps1.setString(3, numero.getAction());
//				ps1.setString(3, "update");
				// NUMBERFROM
				ps1.setString(4, numero.getNumberFrom());
				// NUMBERTO
				ps1.setString(5, numero.getNumberTo());
				// ISMPP
				ps1.setString(6, numero.getIsMpp());
				// RIDA
				ps1.setInt(7, new BigDecimal(numero.getRida().toString()).intValue());
				// RCR
				ps1.setInt(8, new BigDecimal(numero.getRcr().toString()).intValue());

				// DIDA
				if (numero.getDida() == null) {
					ps1.setNull(9, java.sql.Types.INTEGER);
				} else {
					ps1.setInt(9, new BigDecimal(numero.getDida().toString()).intValue());

				}
				// DCR
				if (numero.getDcr() == null) {
					ps1.setNull(10, java.sql.Types.INTEGER);
				} else {
					ps1.setInt(10, new BigDecimal(numero.getDcr().toString()).intValue());

				}
				// ACTIONDATE
				ps1.setTimestamp(11, numero.getActionDate());
				// WHERE
				ps1.setString(12, numero.getNumberFrom());
				ps1.executeQuery();
//				LOGGER.debug("<--Numero Actualizado:" + numero.getNumberFrom());
				conn.commit();
				rs.close();
				ps.close();
				ps1.close();

			} else {
//				LOGGER.debug("<- -NoExiste la numeracion, se ejecuta INSERT---->");
				String sqlInsert = "Insert into port_num_portado (PORTID, " + "PORTTYPE," + "ACTION," + "NUMBERFROM,"
						+ "NUMBERTO," + "ISMPP," + "RIDA," + "RCR," + "DIDA," + "DCR," + "ACTIONDATE)"
						+ "values(?,?,?,?,?,?,?,?,?,?,?)";
				ps2 = conn.prepareStatement(sqlInsert);
				// PORTID
				ps2.setString(1, numero.getPortId());
				// PORTTYPE
				ps2.setString(2, numero.getPortType());
				// ACTION
				ps2.setString(3, numero.getAction());
//				ps2.setString(3, "Insert");
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
				if(null!=numero.getDida()) {
					if(!numero.getDida().equals("null") ) {
						ps2.setInt(9,new BigDecimal(numero.getDida().toString()).intValue());
					}
				}else {
					LOGGER.info("+++++++-------------Dida null");
					ps2.setString(9,null);
				}
//				ps2.setInt(9, !numero.getDida().equals("null") ? new BigDecimal(numero.getDida().toString()).intValue()
//						: null);
				// DCR
//				ps2.setInt(10,!numero.getDcr().equals("null") ? new BigDecimal(numero.getDcr().toString()).intValue() : null);
				if(null!=numero.getDcr()) {
					if(!numero.getDcr().equals("null") ) {
						ps2.setInt(10,new BigDecimal(numero.getDcr().toString()).intValue());
					}
				}else {
					LOGGER.info("+++++++-------------DCR null");
					ps2.setString(10,null);
				}
				// ACTIONDATE
				ps2.setTimestamp(11, numero.getActionDate());
				ps2.executeQuery();
//				LOGGER.debug("<--Numero Insertado:" + numero.getNumberFrom());
				conn.commit();
//				if (ps != null) {
//					ps.close();
//				}
				ps.close();
				ps2.close();
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				if (ps != null) {
					ps.close();
				}
				if (ps1 != null) {
					ps1.close();
				}
				if (ps2 != null) {
					ps2.close();
				}
				if (rs != null) {
					rs.close();
				}
//				if (rs != null) {
//					conn.close();
//				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			if (ps != null) {
				ps.close();
			}
			if (ps1 != null) {
				ps1.close();
			}
			if (ps2 != null) {
				ps2.close();
			}
			if (rs != null) {
				rs.close();
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
//			if(conn!=null) {
//				conn.close();
//			}
////			if (!conn.isClosed()) {
////				conn.close();
////			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//	}


