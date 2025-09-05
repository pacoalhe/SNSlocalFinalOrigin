package mx.ift.sns.negocio.port;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import mx.ift.sns.negocio.conf.IParametrosService;
import mx.ift.sns.negocio.ng.model.ResultadoValidacionCSV;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.ift.sns.modelo.port.NumeroPortado;
import mx.ift.sns.utils.dbConnection.ConnectionBD;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Query;
import javax.sql.DataSource;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;


@Stateless
public class PortadosDAO {

	private static final Logger LOGGER = LoggerFactory.getLogger(PortadosDAO.class);

	// === Resultado del control final (Java 7) ===
	// === Resultado del control final ===
	public static class ControlResultado {
		private final int totalOrigen;
		private final int totalInsertados;
		private final int totalActualizados;
		private final int totalFallidos;
		private final int totalOk;
		private final int totalDiferentes;
		private final int totalFallidosLista;
		private final java.nio.file.Path csvFallidosPath;

		public ControlResultado(int totalOrigen,
								int totalInsertados,
								int totalActualizados,
								int totalFallidos,
								int totalOk,
								int totalDiferentes,
								int totalFallidosLista,
								java.nio.file.Path csvFallidosPath) {
			this.totalOrigen = totalOrigen;
			this.totalInsertados = totalInsertados;
			this.totalActualizados = totalActualizados;
			this.totalFallidos = totalFallidos;
			this.totalOk = totalOk;
			this.totalDiferentes = totalDiferentes;
			this.totalFallidosLista = totalFallidosLista;
			this.csvFallidosPath = csvFallidosPath;
		}

		public int getTotalOrigen() {
			return totalOrigen;
		}

		public int getTotalInsertados() {
			return totalInsertados;
		}

		public int getTotalActualizados() {
			return totalActualizados;
		}

		public int getTotalFallidos() {
			return totalFallidos;
		}

		public int getTotalOk() {
			return totalOk;
		}

		public int getTotalDiferentes() {
			return totalDiferentes;
		}

		public int getTotalFallidosLista() {
			return totalFallidosLista;
		}

		public java.nio.file.Path getCsvFallidosPath() {
			return csvFallidosPath;
		}
	}


	// Helper Java 7 para armar "?, ?, ?, ..."
	private String buildPlaceholders(int n) {
		StringBuilder sb = new StringBuilder();
		for (int j = 0; j < n; j++) {
			if (j > 0) sb.append(',');
			sb.append('?');
		}
		return sb.toString();
	}


	//FJAH 26052025
	public PortadosDAO() {
		// Constructor vacío requerido por EJB
	}

	final int BATCH_SIZE = 5000;

	//private static Connection conn = ConnectionBD.getConnection();

	@Resource(lookup = "jdbc/SNS")
	private DataSource dataSource;

	private final ReentrantLock lock = new ReentrantLock();
	private static final long LOCK_TIMEOUT = 30; // segundos

	/**
	 * Servicio configuracion.
	 */
	@EJB
	private IParametrosService paramService;

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
	public void init() {

	}

	// =========================================================
	// SNAPSHOTS HIBRIDO FALLBACK 27.08.2025
	// =========================================================

	// =========================================================
	// Paso Sub0: limpiar snapshots persistentes
	// =========================================================
	public void limpiarSnapshotPortado() {
		final String SQL = "DELETE FROM SS_PORT_NUM_PORTADO";
		try (Connection conn = dataSource.getConnection();
			 PreparedStatement ps = conn.prepareStatement(SQL)) {

			int borrados = ps.executeUpdate();
			LOGGER.info("Tabla SS_PORT_NUM_PORTADO limpiada. Registros eliminados={}", borrados);

		} catch (Exception e) {
			LOGGER.error("Error limpiando SS_PORT_NUM_PORTADO", e);
		}
	}

	/**
	 * Paso 1: Inserta en snapshot las filas originales del archivo procesado (estado ORIGEN).
	 */
	public void insertSnapshotOrigen(List<NumeroPortado> lote) throws SQLException {
		final String SQL =
				"INSERT INTO SS_PORT_NUM_PORTADO " +
						"(PORTID, PORTTYPE, ACTION, NUMBERFROM, NUMBERTO, ISMPP, RIDA, RCR, DIDA, DCR, ACTIONDATE, ESTADO, FECHA_SNAPSHOT) " +
						"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'ORIGEN', SYSDATE)";

		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(SQL);

			for (NumeroPortado n : lote) {
				ps.setString(1, n.getPortId());
				ps.setString(2, n.getPortType());
				ps.setString(3, n.getAction());

				//Trim en los números para que no entren con espacios
				ps.setString(4, n.getNumberFrom() != null ? n.getNumberFrom().trim() : null);
				ps.setString(5, n.getNumberTo() != null ? n.getNumberTo().trim() : null);

				ps.setString(6, n.getIsMpp());
				ps.setBigDecimal(7, n.getRida());
				ps.setBigDecimal(8, n.getRcr());
				ps.setBigDecimal(9, n.getDida());
				ps.setBigDecimal(10, n.getDcr());
				ps.setTimestamp(11, n.getActionDate());
				ps.addBatch();
			}
			ps.executeBatch();
		} finally {
			if (ps != null) try {
				ps.close();
			} catch (Exception ignore) {
			}
			if (conn != null) try {
				conn.close();
			} catch (Exception ignore) {
			}
		}
	}

	/**
	 * PASO 2: Clasifica los registros del snapshot ORIGEN como INSERTAR o ACTUALIZAR.
	 */
	public void clasificarSnapshot() throws SQLException {
		final String SQL =
				"UPDATE SS_PORT_NUM_PORTADO s " +
						"SET ESTADO = ( " +
						"   SELECT CASE WHEN COUNT(*) > 0 THEN 'ACTUALIZAR' ELSE 'INSERTAR' END " +
						"   FROM PORT_NUM_PORTADO t " +
						"   WHERE t.NUMBERFROM = s.NUMBERFROM " +
						") " +
						"WHERE s.ESTADO = 'ORIGEN'";

		try (Connection conn = dataSource.getConnection();
			 PreparedStatement ps = conn.prepareStatement(SQL)) {
			ps.executeUpdate();
		}
	}

	/**
	 * PASO 3: Actualiza PORT_NUM_PORTADO de maner hibridad
	 * HIBRIDO lectura directa SNAPSHOT_PORT_NUM_PORTADO
	 * FALLBACK lectura directa del CSV
	 */

	public int upsertHibrido(List<NumeroPortado> origen, int totalFilasCsv) throws SQLException {

		int totalExitos = 0;

		try (Connection conn = dataSource.getConnection();
			 Statement st = conn.createStatement()) {

			// 1. Validar snapshot vs total CSV
			int totalSnapshot = 0;
			try (ResultSet rs = st.executeQuery(
					"SELECT COUNT(*) FROM SS_PORT_NUM_PORTADO")) {
				if (rs.next()) {
					totalSnapshot = rs.getInt(1);
				}
			}

			LOGGER.info("Validación snapshot vs CSV -> Snapshot={}, CSV={}", totalSnapshot, totalFilasCsv);

			// 2. Si coincide → intentar MERGE único desde snapshot
			if (totalSnapshot == totalFilasCsv) {
				LOGGER.info("Snapshot completo. Ejecutando MERGE express (único desde snapshot)...");

				String sql =
						"MERGE INTO PORT_NUM_PORTADO t " +
								"USING ( " +
								"  SELECT portid, porttype, action, numberfrom, numberto, actiondate, " +
								"         ismpp, rida, rcr, dida, dcr " +
								"  FROM SS_PORT_NUM_PORTADO " +
								") s " +
								"ON (t.numberfrom = s.numberfrom) " +  //Sin TRIM(), índice usable
								"WHEN MATCHED THEN UPDATE SET " +
								"   t.portid     = s.portid, " +
								"   t.porttype   = s.porttype, " +
								"   t.action     = s.action, " +
								"   t.numberto   = s.numberto, " +
								"   t.actiondate = s.actiondate, " +
								"   t.ismpp      = s.ismpp, " +
								"   t.rida       = s.rida, " +
								"   t.rcr        = s.rcr, " +
								"   t.dida       = s.dida, " +
								"   t.dcr        = s.dcr " +
								"WHEN NOT MATCHED THEN INSERT " +
								" (portid, porttype, action, numberfrom, numberto, actiondate, ismpp, rida, rcr, dida, dcr) " +
								"VALUES (s.portid, s.porttype, s.action, s.numberfrom, s.numberto, " +
								"        s.actiondate, s.ismpp, s.rida, s.rcr, s.dida, s.dcr)";

				try {
					totalExitos = st.executeUpdate(sql);
					LOGGER.info("MERGE express ejecutado correctamente. Filas afectadas={}", totalExitos);

				} catch (SQLException ex) {
					//Aquí se cambia de estrategia
					LOGGER.error("MERGE express falló → rollback automático aplicado. "
							+ "Reintentando con modo seguro (recorrido CSV + fallback).", ex);

					totalExitos = upsertMergeBatchUnionAll(origen);
				}

			} else {
				// 3. Si NO coincide → fallback directo
				LOGGER.warn("Snapshot incompleto ({} vs {}). Ejecutando modo seguro (recorrido CSV)...",
						totalSnapshot, totalFilasCsv);

				totalExitos = upsertMergeBatchUnionAll(origen);
			}
		}

		return totalExitos;
	}

	/**
	 * Upsert con MERGE en bloques de 5,000 filas.
	 * - Construye dinámicamente un MERGE con SELECT ... UNION ALL ...
	 * - Cada bloque se ejecuta como un único SQL (rápido en Oracle).
	 * - Si un bloque falla, hace fallback fila a fila.
	 *
	 * @param origen lista de registros a procesar
	 * @return total de filas exitosas
	 */

	public int upsertMergeBatchUnionAll(List<NumeroPortado> origen) throws SQLException {
		final int BLOCK_SIZE = 500;
		int totalExitos = 0;

		try (Connection conn = dataSource.getConnection();
			 Statement st = conn.createStatement()) {

			LOGGER.info("Iniciando MERGE por bloques de {} filas. Total registros={}", BLOCK_SIZE, origen.size());

			for (int start = 0; start < origen.size(); start += BLOCK_SIZE) {
				int end = Math.min(start + BLOCK_SIZE, origen.size());
				List<NumeroPortado> subList = origen.subList(start, end);

				// Log normal o especial si es el último bloque
				if (end == origen.size() && (end - start) < BLOCK_SIZE) {
					LOGGER.info("Procesando último bloque SOBRANTE: {} filas ({}-{})",
							subList.size(), start, end - 1);
				} else {
					LOGGER.info("Procesando bloque completo: {} filas ({}-{})",
							subList.size(), start, end - 1);
				}

				// Armar SQL dinámico con UNION ALL
				StringBuilder sb = new StringBuilder();
				sb.append("MERGE INTO PORT_NUM_PORTADO t ")
						.append("USING (");

				for (int i = 0; i < subList.size(); i++) {
					NumeroPortado n = subList.get(i);
					if (i > 0) sb.append(" UNION ALL ");

					sb.append("SELECT ")
							.append(quote(n.getPortId())).append(" AS portid, ")
							.append(quote(n.getPortType())).append(" AS porttype, ")
							.append(quote(n.getAction())).append(" AS action, ")
							.append(quote(n.getNumberFrom())).append(" AS nf, ")
							.append(quote(n.getNumberTo())).append(" AS nt, ")
							.append(toSqlTimestamp(n.getActionDate())).append(" AS ad, ")
							.append(quote(n.getIsMpp())).append(" AS mp, ")
							.append(toSqlNumber(n.getRida())).append(" AS rida, ")
							.append(toSqlNumber(n.getRcr())).append(" AS rcr, ")
							.append(toSqlNumber(n.getDida())).append(" AS dida, ")
							.append(toSqlNumber(n.getDcr())).append(" AS dcr ")
							.append("FROM dual");
				}

				sb.append(") s ")
						.append("ON (TRIM(t.numberfrom) = TRIM(s.nf)) ")
						.append("WHEN MATCHED THEN UPDATE SET ")
						.append(" t.portid = s.portid, ")
						.append(" t.porttype = s.porttype, ")
						.append(" t.action = s.action, ")
						.append(" t.numberto = s.nt, ")
						.append(" t.actiondate = s.ad, ")
						.append(" t.ismpp = s.mp, ")
						.append(" t.rida = s.rida, ")
						.append(" t.rcr = s.rcr, ")
						.append(" t.dida = s.dida, ")
						.append(" t.dcr = s.dcr ")
						.append("WHEN NOT MATCHED THEN INSERT ")
						.append(" (portid, porttype, action, numberfrom, numberto, actiondate, ismpp, rida, rcr, dida, dcr) ")
						.append("VALUES (s.portid, s.porttype, s.action, s.nf, s.nt, s.ad, s.mp, s.rida, s.rcr, s.dida, s.dcr)");

				String sql = sb.toString();

				try {
					int rows = st.executeUpdate(sql);
					totalExitos += rows;
					LOGGER.info("Bloque {}-{} ejecutado. Filas afectadas={}, acumuladas={}", start, end - 1, rows, totalExitos);
				} catch (SQLException ex) {
					LOGGER.error("Error en bloque {}-{}, entrando a fallback fila por fila", start, end - 1, ex);
					for (int j = start; j < end; j++) {
						try {
							NumeroPortado n = origen.get(j);
							int r = mergeSingle(conn, n);
							totalExitos += r;
						} catch (SQLException e2) {
							LOGGER.warn("Falló fila {} con numberFrom={}", j, origen.get(j).getNumberFrom(), e2);
						}
					}
				}
			}

			LOGGER.info("MERGE finalizado. Total exitosas={}", totalExitos);
		}
		return totalExitos;
	}

	/**
	 * Escapa strings para SQL (maneja NULL y comillas simples).
	 */
	private String quote(String val) {
		if (val == null) return "NULL";
		return "'" + val.trim().replace("'", "''") + "'";
	}

	/**
	 * Convierte BigDecimal a SQL literal.
	 */
	private String toSqlNumber(BigDecimal val) {
		return (val != null) ? val.toPlainString() : "NULL";
	}

	/**
	 * Convierte Timestamp a SQL TO_TIMESTAMP.
	 */
	private String toSqlTimestamp(Timestamp ts) {
		if (ts == null) return "NULL";
		String formatted = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ts);
		return "TO_TIMESTAMP('" + formatted + "', 'YYYY-MM-DD HH24:MI:SS')";
	}

	/**
	 * MERGE individual como fallback.
	 */
	private int mergeSingle(Connection conn, NumeroPortado n) throws SQLException {
		final String SQL =
				"MERGE INTO PORT_NUM_PORTADO t " +
						"USING (SELECT ? nf, ? portid, ? porttype, ? action, ? nt, ? ad, ? mp, ? rida, ? rcr, ? dida, ? dcr FROM dual) s " +
						"ON (TRIM(t.numberfrom) = TRIM(s.nf)) " +
						"WHEN MATCHED THEN UPDATE SET " +
						" t.portid = s.portid, " +
						" t.porttype = s.porttype, " +
						" t.action = s.action, " +
						" t.numberto = s.nt, " +
						" t.actiondate = s.ad, " +
						" t.ismpp = s.mp, " +
						" t.rida = s.rida, " +
						" t.rcr = s.rcr, " +
						" t.dida = s.dida, " +
						" t.dcr = s.dcr " +
						"WHEN NOT MATCHED THEN INSERT " +
						" (numberfrom, portid, porttype, action, numberto, actiondate, ismpp, rida, rcr, dida, dcr) " +
						"VALUES (s.nf, s.portid, s.porttype, s.action, s.nt, s.ad, s.mp, s.rida, s.rcr, s.dida, s.dcr)";

		try (PreparedStatement ps = conn.prepareStatement(SQL)) {
			ps.setString(1, n.getNumberFrom());
			ps.setString(2, n.getPortId());
			ps.setString(3, n.getPortType());
			ps.setString(4, n.getAction());
			ps.setString(5, n.getNumberTo());
			ps.setTimestamp(6, n.getActionDate());
			ps.setString(7, n.getIsMpp());
			ps.setBigDecimal(8, n.getRida());
			ps.setBigDecimal(9, n.getRcr());
			ps.setBigDecimal(10, n.getDida());
			ps.setBigDecimal(11, n.getDcr());
			return ps.executeUpdate();
		}
	}

	/**
	 * PASO 4.1: Actualiza ESTADO_FINAL para registros con ESTADO=INSERTAR.
	 * Si existen en PORT_NUM_PORTADO → INSERTADO, si no → FALLIDO.
	 */
	public void actualizarEstadoFinalInsertar() throws SQLException {
		final String SQL =
				"UPDATE SS_PORT_NUM_PORTADO s " +
						"SET ESTADO_FINAL = ( " +
						"   SELECT CASE WHEN COUNT(*) > 0 THEN 'INSERTADO' ELSE 'FALLIDO' END " +
						"   FROM PORT_NUM_PORTADO t " +
						"   WHERE t.NUMBERFROM = s.NUMBERFROM " +
						") " +
						"WHERE s.ESTADO = 'INSERTAR'";

		try (Connection conn = dataSource.getConnection();
			 PreparedStatement ps = conn.prepareStatement(SQL)) {
			ps.executeUpdate();
		}
	}

	/**
	 * PASO 4.2: Actualiza ESTADO_FINAL para registros con ESTADO=ACTUALIZAR.
	 * - Si no existe en PORT_NUM_PORTADO → FALLIDO
	 * - Si existe con todos los campos iguales → ACTUALIZADO
	 * - Si existe pero hay diferencias → FALLIDO
	 */
	public void actualizarEstadoFinalActualizar() throws SQLException {
		final String SQL =
				"UPDATE SS_PORT_NUM_PORTADO s " +
						"SET ESTADO_FINAL = ( " +
						"   SELECT CASE " +
						"            WHEN NOT EXISTS ( " +
						"               SELECT 1 FROM PORT_NUM_PORTADO t WHERE t.NUMBERFROM = s.NUMBERFROM " +
						"            ) THEN 'FALLIDO' " +
						"            WHEN EXISTS ( " +
						"               SELECT 1 FROM PORT_NUM_PORTADO t " +
						"               WHERE t.NUMBERFROM = s.NUMBERFROM " +
						"                 AND NVL(s.PORTID,'x')   = NVL(t.PORTID,'x') " +
						"                 AND NVL(s.PORTTYPE,'x') = NVL(t.PORTTYPE,'x') " +
						"                 AND NVL(s.ACTION,'x')   = NVL(t.ACTION,'x') " +
						"                 AND NVL(s.NUMBERTO,'x') = NVL(t.NUMBERTO,'x') " +
						"                 AND NVL(s.ISMPP,'x')    = NVL(t.ISMPP,'x') " +
						"                 AND NVL(s.RIDA,-1)      = NVL(t.RIDA,-1) " +
						"                 AND NVL(s.RCR,-1)       = NVL(t.RCR,-1) " +
						"                 AND NVL(s.DIDA,-1)      = NVL(t.DIDA,-1) " +
						"                 AND NVL(s.DCR,-1)       = NVL(t.DCR,-1) " +
						"            ) THEN 'ACTUALIZADO' " +
						"            ELSE 'FALLIDO' " +
						"          END " +
						"   FROM dual " +
						") " +
						"WHERE s.ESTADO = 'ACTUALIZAR'";

		try (Connection conn = dataSource.getConnection();
			 PreparedStatement ps = conn.prepareStatement(SQL)) {
			int updated = ps.executeUpdate();
			LOGGER.info("actualizarEstadoFinalActualizar -> {} registros actualizados con estado final", updated);
		}
	}

	/**
	 * Paso 5: Totales desde snapshot PORTADOS
	 */
	public ResultadoValidacionCSV getTotalesSnapshotPortados() {
		ResultadoValidacionCSV resultado = new ResultadoValidacionCSV();

		String sql =
				"SELECT " +
						" COUNT(*) AS TOTAL_ORIGEN, " +
						" SUM(CASE WHEN ESTADO_FINAL = 'INSERTADO' THEN 1 ELSE 0 END) AS TOTAL_INSERTADOS, " +
						" SUM(CASE WHEN ESTADO_FINAL = 'ACTUALIZADO' THEN 1 ELSE 0 END) AS TOTAL_ACTUALIZADOS, " +
						" SUM(CASE WHEN ESTADO_FINAL = 'FALLIDO' THEN 1 ELSE 0 END) AS TOTAL_NOPERSISTIDOS, " +
						" SUM(CASE WHEN ESTADO = 'INSERTAR' THEN 1 ELSE 0 END) AS TOTAL_PENDIENTES_INSERTAR, " +
						" SUM(CASE WHEN ESTADO = 'ACTUALIZAR' THEN 1 ELSE 0 END) AS TOTAL_PENDIENTES_ACTUALIZAR " +
						"FROM SS_PORT_NUM_PORTADO";

		try (Connection conn = dataSource.getConnection();
			 Statement st = conn.createStatement();
			 ResultSet rs = st.executeQuery(sql)) {

			if (rs.next()) {
				//resultado.setTotalOrigen(rs.getInt("TOTAL_ORIGEN"));
				resultado.setTotalProcesados(rs.getInt("TOTAL_INSERTADOS") + rs.getInt("TOTAL_ACTUALIZADOS"));
				resultado.setTotalInsertados(rs.getInt("TOTAL_INSERTADOS"));
				resultado.setTotalActualizados(rs.getInt("TOTAL_ACTUALIZADOS"));
				resultado.setTotalNoPersistidos(rs.getInt("TOTAL_NOPERSISTIDOS"));

				// Nuevas variables que pediste
				resultado.setTotalPendientesInsertar(rs.getInt("TOTAL_PENDIENTES_INSERTAR"));
				resultado.setTotalPendientesActualizar(rs.getInt("TOTAL_PENDIENTES_ACTUALIZAR"));
			}

		} catch (Exception e) {
			LOGGER.error("Error obteniendo totales snapshot portados", e);
		}

		return resultado;
	}

	/**
	 * PASO 7: Genera un XML con los no persistidos (fallidos),
	 * usando BufferedWriter + StringBuilder para máxima velocidad.
	 * Solo se genera cuando hay registros fallidos.
	 */
	public void generarXmlFallidosBatch() throws Exception {
		LOGGER.info("Iniciando generación de XML con no persistidos (modo batch)...");

		final String SQL_SELECT =
				"SELECT PORTID, PORTTYPE, ACTION, NUMBERFROM, NUMBERTO, ISMPP, " +
						"       RIDA, RCR, DIDA, DCR, ACTIONDATE " +
						"FROM SS_PORT_NUM_PORTADO " +
						"WHERE ESTADO_FINAL='FALLIDO'";

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

		// Nombre archivo: fecha actual -1
		Calendar calArchivo = Calendar.getInstance();
		calArchivo.add(Calendar.DATE, -1);
		String fechaArchivo = new SimpleDateFormat("yyyyMMdd").format(calArchivo.getTime());
		File outFile = new File(dir, "NumbersPorted-" + fechaArchivo + ".xml");

		// Timestamp encabezado: fecha actual -1 (con hora)
		String tsEncabezado = new SimpleDateFormat("yyyyMMddHHmmss").format(calArchivo.getTime());

		// ActionDate por registro: fecha/hora actual
		String actionDateActual = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

		int total = 0;
		StringBuilder xmlContent = new StringBuilder(1024 * 1024); // buffer inicial de 1 MB

		// Encabezado inicial
		xmlContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		xmlContent.append("<NPCData>\n");
		xmlContent.append("  <MessageName>Porting Data</MessageName>\n");
		xmlContent.append("  <Timestamp>").append(tsEncabezado).append("</Timestamp>\n");
		xmlContent.append("  <NumberOfMessages>PLACEHOLDER</NumberOfMessages>\n");
		xmlContent.append("  <PortDataList>\n");

		try (Connection conn = dataSource.getConnection();
			 PreparedStatement ps = conn.prepareStatement(SQL_SELECT,
					 ResultSet.TYPE_FORWARD_ONLY,
					 ResultSet.CONCUR_READ_ONLY);
			 ResultSet rs = ps.executeQuery()) {

			ps.setFetchSize(2000);

			while (rs.next()) {
				total++;
				xmlContent.append("    <PortData>\n")
						.append("      <PortID>").append(rs.getString("PORTID")).append("</PortID>\n")
						.append("      <PortType>").append(rs.getString("PORTTYPE")).append("</PortType>\n")
						.append("      <Action>").append(rs.getString("ACTION")).append("</Action>\n")
						.append("      <NumberRanges>\n")
						.append("        <NumberRange>\n")
						.append("          <NumberFrom>").append(rs.getString("NUMBERFROM")).append("</NumberFrom>\n")
						.append("          <NumberTo>").append(rs.getString("NUMBERTO")).append("</NumberTo>\n")
						.append("          <isMPP>").append(rs.getString("ISMPP")).append("</isMPP>\n")
						.append("        </NumberRange>\n")
						.append("      </NumberRanges>\n")
						.append("      <RIDA>").append(rs.getString("RIDA")).append("</RIDA>\n")
						.append("      <RCR>").append(rs.getString("RCR")).append("</RCR>\n")
						.append("      <DIDA>").append(rs.getString("DIDA")).append("</DIDA>\n")
						.append("      <DCR>").append(rs.getString("DCR")).append("</DCR>\n")
						.append("      <ActionDate>").append(actionDateActual).append("</ActionDate>\n")
						.append("    </PortData>\n");

				if (total % 10000 == 0) {
					LOGGER.info("Escritos {} registros en XML (portados)...", total);
				}
			}
		}

		if (total == 0) {
			LOGGER.info("No se generó XML de portados fallidos: no hay registros.");
			return;
		}

		xmlContent.append("  </PortDataList>\n");
		xmlContent.append("</NPCData>\n");

		// Reemplazar el marcador con el total real
		int idx = xmlContent.indexOf("PLACEHOLDER");
		if (idx != -1) {
			xmlContent.replace(idx, idx + "PLACEHOLDER".length(), String.valueOf(total));
		}

		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(outFile), "UTF-8"), 131072)) {
			writer.write(xmlContent.toString());
		}

		LOGGER.info("Archivo XML (batch) de portados fallidos generado: {} (Total={})",
				outFile.getAbsolutePath(), total);
	}

}

/*
	public void generarXmlFallidosDirecto(String timestampOriginal) throws Exception {
		LOGGER.info("Iniciando generación de XML con no persistidos (directo desde BD)...");

		final String SQL_SELECT =
				"SELECT PORTID, PORTTYPE, ACTION, NUMBERFROM, NUMBERTO, ISMPP, " +
						"       RIDA, RCR, DIDA, DCR, ACTIONDATE " +
						"FROM SS_PORT_NUM_PORTADO " +
						"WHERE ESTADO_FINAL='FALLIDO'";

		final String SQL_COUNT =
				"SELECT COUNT(*) FROM SS_PORT_NUM_PORTADO WHERE ESTADO_FINAL='FALLIDO'";

		// Ruta de salida
		LOGGER.info("Ruta de salida");
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
		File outFile = new File(dir, "port_num_portados_fallidos_" + fechaStr + ".xml");

		SimpleDateFormat sdfActionDate = new SimpleDateFormat("yyyyMMddHHmmss");

		LOGGER.info("uso del XMLOutputFactory");
		XMLOutputFactory factory = XMLOutputFactory.newInstance();

		try (Connection conn = dataSource.getConnection();
			 Statement stCount = conn.createStatement();
			 ResultSet rsCount = stCount.executeQuery(SQL_COUNT)) {

			int totalEsperado = 0;
			LOGGER.info("antes del rscount.next");
			if (rsCount.next()) {
				totalEsperado = rsCount.getInt(1);
			}
			LOGGER.info("valor total esperado {}" + totalEsperado);

			LOGGER.info("Entrando al try preparedStatement ps = conn.prepareStatement(SQL_SELECT " +
					"ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);");

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
				writer.writeCharacters("Porting Data");
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

					writer.writeStartElement("ActionDate");
					Timestamp ts = rs.getTimestamp("ACTIONDATE");
					writer.writeCharacters(ts != null ? sdfActionDate.format(ts) : "");
					writer.writeEndElement();

					writer.writeEndElement(); // </PortData>

					if (total <= 170 ) {
						LOGGER.info("escribiendo el registro {} menor a 171" + total);
					}

				}

				LOGGER.info("Finalizo de armar filas {} en el xml" + total);

				writer.writeEndElement(); // </PortDataList>
				writer.writeEndElement(); // </NPCData>
				writer.writeEndDocument();
				writer.flush();

				LOGGER.info("Finalizo flush");

				LOGGER.info("Archivo XML de no persistidos generado: {} (Esperados={}, Generados={})",
						outFile.getAbsolutePath(), totalEsperado, total);
			}
		}
	}

 */




	/**
	 * Inserta snapshot del estado ANTES del merge usando staging en la misma tabla snapshot.
	 */
/*
	public void insertSnapshotAntes(List<String> numbersOrigen) throws SQLException {
		if (numbersOrigen == null || numbersOrigen.isEmpty()) return;

		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = dataSource.getConnection();

			// 1. Insertar claves en snapshot con ESTADO='CLAVES'
			final String SQL_INSERT_CLAVES =
					"INSERT INTO SS_PORT_NUM_PORTADO (NUMBERFROM, ESTADO, FECHA_SNAPSHOT) VALUES (?, 'CLAVES', SYSDATE)";
			ps = conn.prepareStatement(SQL_INSERT_CLAVES);
			for (String nf : numbersOrigen) {
				ps.setString(1, nf);
				ps.addBatch();
			}
			ps.executeBatch();
			ps.close();

			// 2. Insertar snapshot ANTES haciendo JOIN contra las claves
			final String SQL_ANTES =
					"INSERT INTO SS_PORT_NUM_PORTADO " +
							"(PORTID, PORTTYPE, ACTION, NUMBERFROM, NUMBERTO, ISMPP, RIDA, RCR, DIDA, DCR, ACTIONDATE, ESTADO, FECHA_SNAPSHOT) " +
							"SELECT p.PORTID, p.PORTTYPE, p.ACTION, p.NUMBERFROM, p.NUMBERTO, p.ISMPP, " +
							"       p.RIDA, p.RCR, p.DIDA, p.DCR, p.ACTIONDATE, 'ANTES', SYSDATE " +
							"FROM PORT_NUM_PORTADO p " +
							"JOIN SS_PORT_NUM_PORTADO claves ON claves.NUMBERFROM = p.NUMBERFROM " +
							"WHERE claves.ESTADO = 'CLAVES'";
			ps = conn.prepareStatement(SQL_ANTES);
			ps.executeUpdate();
			ps.close();

			// 3. Limpiar claves
			final String SQL_DELETE_CLAVES = "DELETE FROM SS_PORT_NUM_PORTADO WHERE ESTADO='CLAVES'";
			ps = conn.prepareStatement(SQL_DELETE_CLAVES);
			ps.executeUpdate();

		} finally {
			if (ps != null) try { ps.close(); } catch (Exception ignore) {}
			if (conn != null) try { conn.close(); } catch (Exception ignore) {}
		}
	}


 */
	/**
	 * Inserta snapshot del estado DESPUES del merge usando staging en la misma tabla snapshot.
	 */

	/*
	public void insertSnapshotDespues(List<String> numbersOrigen) throws SQLException {
		if (numbersOrigen == null || numbersOrigen.isEmpty()) return;

		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = dataSource.getConnection();

			// 1. Insertar claves en snapshot con ESTADO='CLAVES'
			final String SQL_INSERT_CLAVES =
					"INSERT INTO SS_PORT_NUM_PORTADO (NUMBERFROM, ESTADO, FECHA_SNAPSHOT) VALUES (?, 'CLAVES', SYSDATE)";
			ps = conn.prepareStatement(SQL_INSERT_CLAVES);
			for (String nf : numbersOrigen) {
				ps.setString(1, nf);
				ps.addBatch();
			}
			ps.executeBatch();
			ps.close();

			// 2. Insertar snapshot DESPUES haciendo JOIN contra las claves
			final String SQL_DESPUES =
					"INSERT INTO SS_PORT_NUM_PORTADO " +
							"(PORTID, PORTTYPE, ACTION, NUMBERFROM, NUMBERTO, ISMPP, RIDA, RCR, DIDA, DCR, ACTIONDATE, ESTADO, FECHA_SNAPSHOT) " +
							"SELECT p.PORTID, p.PORTTYPE, p.ACTION, p.NUMBERFROM, p.NUMBERTO, p.ISMPP, " +
							"       p.RIDA, p.RCR, p.DIDA, p.DCR, p.ACTIONDATE, 'DESPUES', SYSDATE " +
							"FROM PORT_NUM_PORTADO p " +
							"JOIN SS_PORT_NUM_PORTADO claves ON claves.NUMBERFROM = p.NUMBERFROM " +
							"WHERE claves.ESTADO = 'CLAVES'";
			ps = conn.prepareStatement(SQL_DESPUES);
			ps.executeUpdate();
			ps.close();

			// 3. Limpiar claves
			final String SQL_DELETE_CLAVES = "DELETE FROM SS_PORT_NUM_PORTADO WHERE ESTADO='CLAVES'";
			ps = conn.prepareStatement(SQL_DELETE_CLAVES);
			ps.executeUpdate();

		} finally {
			if (ps != null) try { ps.close(); } catch (Exception ignore) {}
			if (conn != null) try { conn.close(); } catch (Exception ignore) {}
		}
	}


	// =========================================================
	// COMPARACIONES
	// =========================================================

	public int getInsertados() throws SQLException {
		final String SQL =
				"SELECT COUNT(*) " +
						"FROM SS_PORT_NUM_PORTADO o " +
						"WHERE o.ESTADO = 'ORIGEN' " +
						"  AND EXISTS ( " +
						"       SELECT 1 FROM SS_PORT_NUM_PORTADO d " +
						"       WHERE d.ESTADO='DESPUES' " +
						"         AND d.NUMBERFROM = o.NUMBERFROM " +
						"  ) " +
						"  AND NOT EXISTS ( " +
						"       SELECT 1 FROM SS_PORT_NUM_PORTADO a " +
						"       WHERE a.ESTADO='ANTES' " +
						"         AND a.NUMBERFROM = o.NUMBERFROM " +
						"  )";

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(SQL);
			rs = ps.executeQuery();
			return rs.next() ? rs.getInt(1) : 0;
		} finally {
			if (rs != null) try { rs.close(); } catch (Exception ignore) {}
			if (ps != null) try { ps.close(); } catch (Exception ignore) {}
			if (conn != null) try { conn.close(); } catch (Exception ignore) {}
		}
	}

	public int getActualizados() throws SQLException {
		final String SQL =
				"SELECT COUNT(*) " +
						"FROM SS_PORT_NUM_PORTADO a " +
						"JOIN SS_PORT_NUM_PORTADO d " +
						"  ON a.NUMBERFROM = d.NUMBERFROM " +
						"WHERE a.ESTADO='ANTES' AND d.ESTADO='DESPUES' " +
						"  AND (NVL(a.NUMBERTO,'X') <> NVL(d.NUMBERTO,'X') " +
						"    OR (a.ACTIONDATE IS NULL AND d.ACTIONDATE IS NOT NULL) " +
						"    OR (a.ACTIONDATE IS NOT NULL AND d.ACTIONDATE IS NULL) " +
						"    OR (a.ACTIONDATE <> d.ACTIONDATE))";

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(SQL);
			rs = ps.executeQuery();
			return rs.next() ? rs.getInt(1) : 0;
		} finally {
			if (rs != null) try { rs.close(); } catch (Exception ignore) {}
			if (ps != null) try { ps.close(); } catch (Exception ignore) {}
			if (conn != null) try { conn.close(); } catch (Exception ignore) {}
		}
	}

	public List<NumeroPortado> getFallidos() throws SQLException {
		final String SQL =
				"SELECT o.PORTID, o.PORTTYPE, o.ACTION, o.NUMBERFROM, o.NUMBERTO, " +
						"       o.ISMPP, o.RIDA, o.RCR, o.DIDA, o.DCR, o.ACTIONDATE " +
						"FROM SS_PORT_NUM_PORTADO o " +
						"WHERE o.ESTADO = 'ORIGEN' " +
						"  AND NOT EXISTS ( " +
						"      SELECT 1 " +
						"      FROM SNAPSHOT_PORT_NUM_PORTADO d " +
						"      WHERE d.ESTADO = 'DESPUES' " +
						"        AND d.NUMBERFROM = o.NUMBERFROM " +
						"  )";

		List<NumeroPortado> result = new ArrayList<>();
		try (Connection conn = dataSource.getConnection();
			 PreparedStatement ps = conn.prepareStatement(SQL);
			 ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				NumeroPortado n = new NumeroPortado();
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
				result.add(n);
			}
		}
		return result;
	}


	 */

	/**
	 * Devuelve todos los numberFrom en BD para una fecha dada.
	 */

	/*
	public List<String> getAllNumberFromByActionDate(Date fecha) throws SQLException {
		List<String> result = new ArrayList<>();
		String sql = "SELECT numberfrom FROM PORT_NUM_PORTADO WHERE TRUNC(actiondate) = ?";
		try (Connection conn = dataSource.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setDate(1, new java.sql.Date(fecha.getTime()));
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					result.add(rs.getString(1));
				}
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> getSnapshotByActionDate(Date actionDate) throws SQLException {
		final String sql = "SELECT numberfrom, numberto, actiondate " +
				"FROM port_num_portado " +
				"WHERE TRUNC(actiondate) = TRUNC(?)";

		List<Object[]> result = new ArrayList<>();

		try (Connection conn = dataSource.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)) {

			// actionDate viene como java.util.Date → lo pasamos a java.sql.Date
			ps.setDate(1, new java.sql.Date(actionDate.getTime()));

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Object[] row = new Object[3];
					row[0] = rs.getString("numberfrom");
					row[1] = rs.getString("numberto");
					row[2] = rs.getTimestamp("actiondate");
					result.add(row);
				}
			}
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> getSnapshotByNumbers(List<String> numbers) throws SQLException {
		if (numbers == null || numbers.isEmpty()) {
			return Collections.emptyList();
		}

		// Construimos lista de placeholders (?, ?, ?)
		StringBuilder inClause = new StringBuilder();
		for (int i = 0; i < numbers.size(); i++) {
			if (i > 0) inClause.append(",");
			inClause.append("?");
		}

		final String sql = "SELECT numberfrom, numberto, actiondate " +
				"FROM port_num_portado " +
				"WHERE numberfrom IN (" + inClause.toString() + ")";

		List<Object[]> result = new ArrayList<Object[]>();

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);

			int idx = 1;
			for (String num : numbers) {
				ps.setString(idx++, num);
			}

			rs = ps.executeQuery();
			while (rs.next()) {
				Object[] row = new Object[3];
				row[0] = rs.getString("numberfrom");
				row[1] = rs.getString("numberto");
				row[2] = rs.getTimestamp("actiondate");
				result.add(row);
			}
		} finally {
			if (rs != null) try { rs.close(); } catch (SQLException ignore) {}
			if (ps != null) try { ps.close(); } catch (SQLException ignore) {}
			if (conn != null) try { conn.close(); } catch (SQLException ignore) {}
		}

		return result;
	}

	 */

	/**
	 * PASO 5.1: Totales: Insertados, Actualizados, Fallidos.
	 */

	/*
	public int getTotalInsertados() throws SQLException {
		final String SQL = "SELECT COUNT(*) FROM SNAPSHOT_PORT_NUM_PORTADO WHERE ESTADO_FINAL='INSERTADO'";
		try (Connection conn = dataSource.getConnection();
			 PreparedStatement ps = conn.prepareStatement(SQL);
			 ResultSet rs = ps.executeQuery()) {
			return rs.next() ? rs.getInt(1) : 0;
		}
	}

	 */

	/**
	 * PASO 5.2: Totales: Insertados, Actualizados, Fallidos.
	 */

	/*
	public int getTotalActualizados() throws SQLException {
		final String SQL = "SELECT COUNT(*) FROM SNAPSHOT_PORT_NUM_PORTADO WHERE ESTADO_FINAL='ACTUALIZADO'";
		try (Connection conn = dataSource.getConnection();
			 PreparedStatement ps = conn.prepareStatement(SQL);
			 ResultSet rs = ps.executeQuery()) {
			return rs.next() ? rs.getInt(1) : 0;
		}
	}

	 */

	/**
	 * PASO 5.3: Totales: Insertados, Actualizados, Fallidos.
	 */
	/*
	public int getTotalFallidos() throws SQLException {
		final String SQL = "SELECT COUNT(*) FROM SNAPSHOT_PORT_NUM_PORTADO WHERE ESTADO_FINAL='FALLIDO'";
		try (Connection conn = dataSource.getConnection();
			 PreparedStatement ps = conn.prepareStatement(SQL);
			 ResultSet rs = ps.executeQuery()) {
			return rs.next() ? rs.getInt(1) : 0;
		}
	}



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

/*
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

	/**
	 * Upsert con MERGE + batch para reducir roundtrips.
	 * - Envía lotes con addBatch()/executeBatch()
	 * - Commit cada COMMIT_EVERY filas
	 * - Si un executeBatch() falla, se hace fallback a MERGE por fila con savepoint,
	 *   para identificar y saltar sólo las filas problemáticas.
	 *
	 * @param origen filas a upsert
	 * @return conteo de filas exitosas (insert/update) según driver
	 */
/*
	// TODO ==================== FLAGS DE PRUEBA ====================
	private static final boolean FORZAR_FALLO_BATCH = false; // Forzar fallo al ejecutar batch completo
	private static final boolean FORZAR_FALLO_ROW   = false; // Forzar fallo al ejecutar fila individual
	// =========================================================

	public int upsertBatchNoStopMergeBatch(List<NumeroPortado> origen) throws SQLException {
		final int BATCH = 500;          // tamaño de lote para addBatch()
		final int COMMIT_EVERY = 2000;  // commit cada N filas efectivas

		int ok = 0;
		Connection conn = null;
		PreparedStatement ps = null;

		final String MERGE_SQL =
				"MERGE INTO PORT_NUM_PORTADO t " +
						"USING (SELECT ? portid, ? porttype, ? action, TRIM(?) nf, ? nt, ? ad, ? mp, ? rida, ? rcr, ? dida, ? dcr FROM dual) s " +
						"   ON (TRIM(t.numberfrom) = s.nf) " + // clave por NUMBERFROM (normalizado)
						"WHEN MATCHED THEN UPDATE SET " +
						"   t.portid     = s.portid, " +
						"   t.porttype   = s.porttype, " +
						"   t.action     = s.action, " +
						"   t.numberto   = s.nt, " +
						"   t.actiondate = s.ad, " +
						"   t.ismpp      = s.mp, " +
						"   t.rida       = s.rida, " +
						"   t.rcr        = s.rcr, " +
						"   t.dida       = s.dida, " +
						"   t.dcr        = s.dcr " +
						"WHEN NOT MATCHED THEN INSERT " +
						"   (portid, porttype, action, numberfrom, numberto, actiondate, ismpp, rida, rcr, dida, dcr) " +
						"VALUES (s.portid, s.porttype, s.action, s.nf, s.nt, s.ad, s.mp, s.rida, s.rcr, s.dida, s.dcr)";

		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(MERGE_SQL);

			int inBatch = 0;
			int sinceLastCommit = 0;

			LOGGER.info("Iniciando upsert MERGE batch para {} registros...", origen.size());

			for (int i = 0; i < origen.size(); i++) {
				NumeroPortado n = origen.get(i);

				// Log de los primeros 3 registros para diagnóstico
				if (i < 3) {
					LOGGER.debug("DEBUG MERGE fila {} -> numberFrom={}, portId={}", i, n.getNumberFrom(), n.getPortId());
				}

				// Bind parámetros 1..11 (portid, porttype, action, nf, nt, ad, mp, rida, rcr, dida, dcr)
				ps.setString(1, n.getPortId() != null ? n.getPortId().trim() : null);
				ps.setString(2, n.getPortType() != null ? n.getPortType().trim() : null);
				ps.setString(3, n.getAction()   != null ? n.getAction().trim()   : null);
				ps.setString(4, n.getNumberFrom() != null ? n.getNumberFrom().trim() : null);
				ps.setString(5, n.getNumberTo()   != null ? n.getNumberTo().trim()   : null);
				ps.setTimestamp(6, n.getActionDate());
				ps.setString(7, n.getIsMpp() != null ? n.getIsMpp().trim() : null);

				if (n.getRida() != null) ps.setBigDecimal(8, n.getRida()); else ps.setNull(8, Types.NUMERIC);
				if (n.getRcr()  != null) ps.setBigDecimal(9, n.getRcr());  else ps.setNull(9, Types.NUMERIC);
				if (n.getDida() != null) ps.setBigDecimal(10, n.getDida()); else ps.setNull(10, Types.NUMERIC);
				if (n.getDcr()  != null) ps.setBigDecimal(11, n.getDcr());  else ps.setNull(11, Types.NUMERIC);

				ps.addBatch();
				inBatch++;

				if (inBatch >= BATCH) {
					try {
						int[] res = ps.executeBatch();

						if (FORZAR_FALLO_BATCH) {
							//LOGGER.error("Forzado para pruebas en batch");
							// en lugar de lanzar excepción, caemos directo al fallback
							ok += fallbackMergePerRow(conn, MERGE_SQL, origen, i - inBatch + 1, i);
						} else {
							int success = countSuccess(res);
							ok += success;
							LOGGER.info("Batch ejecutado ({} filas) -> {} exitosas acumuladas={}", inBatch, success, ok);
						}
						sinceLastCommit = 0;
					} catch (SQLException batchEx) {
						// Fallback fila por fila
						LOGGER.error("Error en executeBatch(), entrando a fallback fila por fila. Rango {}-{}", i - inBatch + 1, i, batchEx);
						ok += fallbackMergePerRow(conn, MERGE_SQL, origen, i - inBatch + 1, i);
					} finally {
						ps.clearBatch();
						inBatch = 0;
					}
				}

				sinceLastCommit++;
				if (sinceLastCommit >= COMMIT_EVERY) {
					LOGGER.debug("Marcador de commit lógico alcanzado en fila {}.", i);
					sinceLastCommit = 0;
				}
			}

			// Flush final
			if (inBatch > 0) {
				try {
					int[] res = ps.executeBatch();

					if (FORZAR_FALLO_BATCH) {
						LOGGER.error("Forzado para pruebas en flush final");
						ok += fallbackMergePerRow(conn, MERGE_SQL,
								origen, origen.size() - inBatch, origen.size() - 1);
					} else {
						int success = countSuccess(res);
						ok += success;
						LOGGER.info("Flush final ejecutado ({} filas) -> {} exitosas acumuladas={}", inBatch, success, ok);
					}
				} catch (SQLException batchEx) {
					LOGGER.error("Error en flush final, fallback fila por fila. Rango {}-{}", origen.size() - inBatch, origen.size() - 1, batchEx);
					ok += fallbackMergePerRow(conn, MERGE_SQL, origen, origen.size() - inBatch, origen.size() - 1);
				} finally {
					try { ps.clearBatch(); } catch (Exception ignore) {}
				}
			}

			LOGGER.info("Upsert MERGE batch finalizado. Total exitosas={}", ok);
			return ok;
		} catch (SQLException e) {
			LOGGER.error("Error general en upsertBatchNoStopMergeBatch", e);
			throw e;
		} finally {
			try { if (ps != null) ps.close(); } catch (Exception ignore) {}
			try { if (conn != null) conn.close(); } catch (Exception ignore) {}
		}
	}


 */
	/** Cuenta éxitos del executeBatch(). SUCCESS_NO_INFO (-2) lo contamos como éxito. */

/*
	private int countSuccess(int[] res) {
		if (res == null) return 0;
		int c = 0;
		for (int r : res) {
			if (r == Statement.SUCCESS_NO_INFO) c++;
			else if (r >= 0) c += r; // algunos drivers devuelven 1 por fila upsert
		}
		return c;
	}

 */

	/** Fallback: ejecuta MERGE por fila con savepoint para identificar fallidos y continuar. */
/*
	private int fallbackMergePerRow(Connection conn, String MERGE_SQL,
									List<NumeroPortado> origen,
									int fromIdxInclusive, int toIdxInclusive) throws SQLException {
		PreparedStatement psOne = null;
		int ok = 0;
		try {
			psOne = conn.prepareStatement(MERGE_SQL);
			for (int j = fromIdxInclusive; j <= toIdxInclusive; j++) {
				NumeroPortado n = origen.get(j);
				//Savepoint sp = null;
				try {
					//sp = conn.setSavepoint();

					psOne.clearParameters();
					// === Bind parámetros 1..11 (idéntico al batch) ===
					psOne.setString(1,  n.getPortId()     != null ? n.getPortId().trim()     : null); // portid
					psOne.setString(2,  n.getPortType()   != null ? n.getPortType().trim()   : null); // porttype
					psOne.setString(3,  n.getAction()     != null ? n.getAction().trim()     : null); // action
					psOne.setString(4,  n.getNumberFrom() != null ? n.getNumberFrom().trim() : null); // nf
					psOne.setString(5,  n.getNumberTo()   != null ? n.getNumberTo().trim()   : null); // nt
					psOne.setTimestamp(6, n.getActionDate());                                         // ad
					psOne.setString(7,  n.getIsMpp()     != null ? n.getIsMpp().trim()      : null); // mp

					if (n.getRida() != null) psOne.setBigDecimal(8, n.getRida()); else psOne.setNull(8, Types.NUMERIC);
					if (n.getRcr()  != null) psOne.setBigDecimal(9, n.getRcr());  else psOne.setNull(9, Types.NUMERIC);
					if (n.getDida() != null) psOne.setBigDecimal(10, n.getDida()); else psOne.setNull(10, Types.NUMERIC);
					if (n.getDcr()  != null) psOne.setBigDecimal(11, n.getDcr());  else psOne.setNull(11, Types.NUMERIC);

					//Forzar fallo fila si está activo
					if (FORZAR_FALLO_ROW) {
						//throw new SQLException("Forzado para pruebas en fila individual numberfrom=" + n.getNumberFrom());
						LOGGER.warn("Forzado para pruebas en fila individual numberfrom={}", n.getNumberFrom());
						// opcional: return o marcar manualmente como fallido sin lanzar excepción
						continue;
					}

					psOne.executeUpdate();
					ok++;
				} catch (SQLException rowEx) {
					//if (sp != null) conn.rollback(sp);
					//LOGGER.error("MERGE fallido para numberfrom={} (continúa con la siguiente fila)",
					//		n != null ? n.getNumberFrom() : null, rowEx);
					LOGGER.error("MERGE fallido para numberfrom={} (continúa con la siguiente fila): {}",
							n != null ? n.getNumberFrom() : null,
							rowEx.getMessage());

				}
			}
			return ok;
		} finally {
			try { if (psOne != null) psOne.close(); } catch (Exception ignore) {}
		}
	}
 */

/*
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

	public Set<String> cargarClavesExistentesAntes(List<NumeroPortado> origen) throws SQLException {
		List<String> uniques = new ArrayList<String>();
		Set<String> seen = new HashSet<String>();

		for (NumeroPortado np : origen) {
			if (np == null) {
				continue;
			}
			String nf = np.getNumberFrom();
			if (nf == null) {
				continue;
			}
			nf = nf.trim();
			if (nf.isEmpty()) {
				continue;
			}
			if (!seen.contains(nf)) {
				seen.add(nf);
				uniques.add(nf);
			}
		}

		Set<String> existentes = new HashSet<>(uniques.size());
		final int CHUNK = 1000;

		try (Connection conn = dataSource.getConnection()) {
			for (int i = 0; i < uniques.size(); i += CHUNK) {
				int to = Math.min(i + CHUNK, uniques.size());
				List<String> chunk = uniques.subList(i, to);

				StringBuilder sb = new StringBuilder();
				for (int j = 0; j < chunk.size(); j++) {
					if (j > 0) {
						sb.append(",");
					}
					sb.append("?");
				}
				String placeholders = sb.toString();

				String sql = "SELECT numberfrom FROM PORT_NUM_PORTADO WHERE numberfrom IN (" + placeholders + ")";

				try (PreparedStatement ps = conn.prepareStatement(sql)) {
					int p = 1; for (String nf : chunk) ps.setString(p++, nf);
					try (ResultSet rs = ps.executeQuery()) {
						while (rs.next()) existentes.add(rs.getString(1));
					}
				}
			}
		}
		return existentes;
	}

	public ControlResultado generarControlFinalPortados(
			List<NumeroPortado> origen,
			Set<String> clavesExistianAntes,
			Path outCsvFallidos,
			int maxFallidosEnCorreo // ignorado a propósito
	) {
		// base única por NUMBERFROM (último gana)
		Map<String, NumeroPortado> porNumero = new LinkedHashMap<String, NumeroPortado>();
		for (NumeroPortado n : origen) {
			String nf = (n != null) ? n.getNumberFrom() : null;
			if (nf != null) {
				nf = nf.trim();
				if (!nf.isEmpty()) {
					porNumero.put(nf, n);
				}
			}
		}
		List<NumeroPortado> base = new ArrayList<NumeroPortado>(porNumero.values());

		int totalOrigen = base.size();
		int totalOk = 0, totalDif = 0, totalPend = 0;
		int totalInsertados = 0, totalActualizados = 0;

		List<NumeroPortado> listaFallidos = new ArrayList<NumeroPortado>();

		final int CHUNK = 500;

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(true);

			for (int i = 0; i < base.size(); i += CHUNK) {
				int to = Math.min(i + CHUNK, base.size());
				List<NumeroPortado> chunk = base.subList(i, to);

				// 1) Traer BD por número
				Map<String, RegistroBD> bdByNF = fetchBDPorNumero(conn, chunk);

				// 2) Comparar
				for (NumeroPortado src : chunk) {
					String nf = src.getNumberFrom();
					RegistroBD dst = bdByNF.get(nf);

					if (dst == null) {
						// pendiente / fallido (no quedó en BD)
						listaFallidos.add(src);
						totalPend++;
						continue;
					}

					if (regIgual(src, dst)) totalOk++;
					else                    totalDif++;

					// Insertado vs Actualizado (exitosos que existen en destino)
					if (clavesExistianAntes.contains(nf)) totalActualizados++;
					else                                  totalInsertados++;
				}
			}

			// 3) Escribir CSV con la lista completa de fallidos
			escribirCsvFallidos(outCsvFallidos, listaFallidos);

			// 4) Devolver totales
			return new ControlResultado(
					totalOrigen, totalInsertados, totalActualizados,
					totalPend, totalOk, totalDif,
					listaFallidos.size(),
					outCsvFallidos
			);

		} catch (Exception e) {
			LOGGER.error("Control final portados: error", e);
			return new ControlResultado(
					totalOrigen, 0, 0,
					0, 0, 0,
					0,
					outCsvFallidos
			);

		} finally {
			if (conn != null) {
				try { conn.close(); } catch (Exception ignore) {}
			}
		}
	}


// === Helpers ===

private static final class RegistroBD {
	String numberTo; Timestamp actionDate; String isMpp; BigDecimal rida; BigDecimal dida;
}

	private Map<String, RegistroBD> fetchBDPorNumero(Connection conn, List<NumeroPortado> chunk) throws SQLException {
		List<String> nfs = new ArrayList<>(chunk.size());
		for (NumeroPortado n : chunk) nfs.add(n.getNumberFrom());

		String placeholders = buildPlaceholders(nfs.size());
		String sql = "SELECT numberfrom, numberto, actiondate, ismpp, rida, dida " +
				"FROM PORT_NUM_PORTADO WHERE numberfrom IN (" + placeholders + ")";

		Map<String, RegistroBD> map = new HashMap<>(nfs.size()*2);
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			int p = 1; for (String nf : nfs) ps.setString(p++, nf);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					RegistroBD r = new RegistroBD();
					String nf = rs.getString(1);
					r.numberTo = rs.getString(2);
					r.actionDate = rs.getTimestamp(3);
					r.isMpp = rs.getString(4);
					r.rida = rs.getBigDecimal(5);
					r.dida = rs.getBigDecimal(6);
					map.put(nf, r);
				}
			}
		}
		return map;
	}

	private boolean regIgual(NumeroPortado s, RegistroBD d) {
		// Normaliza nulls y TRUNC(fecha)
		boolean toEq = Objects.equals(nvls(s.getNumberTo()), nvls(d.numberTo));
		boolean mppEq = Objects.equals(nvls(s.getIsMpp()), nvls(d.isMpp));
		boolean ridaEq = Objects.equals(nvlb(s.getRida()), nvlb(d.rida));
		boolean didaEq = Objects.equals(nvlb(s.getDida()), nvlb(d.dida));
		boolean dateEq = truncDay(s.getActionDate()).equals(truncDay(d.actionDate));
		return toEq && mppEq && ridaEq && didaEq && dateEq;
	}

	private String nvls(String v){ return v==null ? "#" : v.trim(); }
	private String nvlb(BigDecimal v){ return v==null ? "#" : v.toPlainString(); }

	private java.sql.Date truncDay(java.util.Date d){
		if (d==null) return null;
		return new java.sql.Date( (d.getTime()/86_400_000L) * 86_400_000L );
	}

	// --- Escribe CSV con fallidos (Java 7) ---
	private void escribirCsvFallidos(java.nio.file.Path out, java.util.List<NumeroPortado> fallidos) throws java.io.IOException {
		if (out == null) return;

		// Crear carpeta destino si no existe
		java.io.File parent = (out.getParent() != null) ? out.getParent().toFile() : null;
		if (parent != null && !parent.exists()) {
			parent.mkdirs();
		}

		java.io.OutputStream fos = null;
		java.io.OutputStreamWriter osw = null;
		java.io.BufferedWriter w = null;
		try {
			fos = new java.io.FileOutputStream(out.toFile(), false); // sobrescribe
			osw = new java.io.OutputStreamWriter(fos, "UTF-8");
			w = new java.io.BufferedWriter(osw);

			w.write("NUMBERFROM,NUMBERTO,ACTIONDATE,ISMPP,RIDA,DIDA");
			w.newLine();

			for (NumeroPortado n : fallidos) {
				String nf = (n.getNumberFrom() == null) ? "" : n.getNumberFrom().replace(",", " ");
				String nt = (n.getNumberTo() == null) ? "" : n.getNumberTo().replace(",", " ");
				String ad = (n.getActionDate() == null) ? "" : n.getActionDate().toString();
				String mp = (n.getIsMpp() == null) ? "" : n.getIsMpp().replace(",", " ");
				String ri = (n.getRida() == null) ? "" : n.getRida().toPlainString();
				String di = (n.getDida() == null) ? "" : n.getDida().toPlainString();

				w.write(nf); w.write(',');
				w.write(nt); w.write(',');
				w.write(ad); w.write(',');
				w.write(mp); w.write(',');
				w.write(ri); w.write(',');
				w.write(di);
				w.newLine();
			}
		} finally {
			try { if (w != null) w.close(); } catch (Exception ignore) {}
			try { if (osw != null) osw.close(); } catch (Exception ignore) {}
			try { if (fos != null) fos.close(); } catch (Exception ignore) {}
		}
	}
 */