package mx.ift.sns.negocio.pnn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.ift.sns.utils.dbConnection.ConnectionBD;

public final class CanceladosPlanMaestroDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(CanceladosPlanMaestroDao.class);

    private static Connection conn = ConnectionBD.getConnection();

    public static void delete(Long numeroInicial, Long numeroFinal) throws SQLException {

	conn = ConnectionBD.getConnection();
	PreparedStatement ps = null;
	PreparedStatement ps1 = null;
	ResultSet rs = null;
	String sqlSelect = "Select count(1) from PNN_DETALLE where NUMERO_INICIAL = ? AND NUMERO_FINAL = ?";

	try {
	    ps = conn.prepareStatement(sqlSelect);
	    ps.setLong(1, numeroInicial);
	    ps.setLong(2, numeroFinal);
	    rs = ps.executeQuery();
	    int count = 0;
	    while (rs.next()) {
		count = rs.getInt(1);
	    }
	    if (count > 0) {

		LOGGER.debug("<--Existe la numeracion en port_num_portado, por lo tanto se elimina---->");
		String sqlDelete = "delete PNN_DETALLE WHERE NUMERO_INICIAL = ? AND NUMERO_FINAL = ?";
		ps1 = conn.prepareStatement(sqlDelete);
		// WHERE

		ps1.setLong(1, numeroInicial);
		ps1.setLong(2, numeroFinal);
		ps1.executeQuery();

		LOGGER.debug("<--Numeros Inicial: " + numeroInicial + ", Número Final: " + numeroFinal + " eliminados");
		conn.commit();
		ps1.close();
	    }
	    ps.close();
	    rs.close();
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

	    conn.close();
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

	    conn.close();
	}

    }
}