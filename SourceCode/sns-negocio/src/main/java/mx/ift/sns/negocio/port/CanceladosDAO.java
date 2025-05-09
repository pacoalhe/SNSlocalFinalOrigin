package mx.ift.sns.negocio.port;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.ift.sns.modelo.port.NumeroCancelado;
import mx.ift.sns.utils.dbConnection.ConnectionBD;

public final class CanceladosDAO {

	private static final Logger LOGGER = LoggerFactory.getLogger(CanceladosDAO.class);

	private static Connection conn = ConnectionBD.getConnection();

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

}
