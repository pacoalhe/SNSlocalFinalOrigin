package mx.ift.sns.negocio.port;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.ift.sns.modelo.port.NumeroPortado;
import mx.ift.sns.utils.dbConnection.ConnectionBD;

public final class PortadosDAO {

	private static final Logger LOGGER = LoggerFactory.getLogger(PortadosDAO.class);

	private static Connection conn = ConnectionBD.getConnection();

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

}
