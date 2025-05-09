package mx.ift.sns.negocio.pnn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.ift.sns.modelo.pnn.PlanMaestroDetalle;
import mx.ift.sns.utils.dbConnection.ConnectionBD;

public final class PlanMaestroDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlanMaestroDao.class);

    private static Connection conn = ConnectionBD.getConnection();

    public static void update(PlanMaestroDetalle numero) throws SQLException {

//		Connection conn = ConnectionBD.getNewConnection();

	PreparedStatement ps1 = null;
	PreparedStatement ps = null;
	PreparedStatement ps2 = null;
	ResultSet rs = null;
	String sqlSelect = "Select count(1) from pnn_detalle where numero_inicial =? and numero_final = ?";
	try {

	    conn = ConnectionBD.getConnection();
	    ps = conn.prepareStatement(sqlSelect);
	    ps.setLong(1, numero.getId().getNumeroInicial());
	    ps.setLong(2, numero.getId().getNumeroFinal());
	    rs = ps.executeQuery();
	    int count = 0;
	    while (rs.next()) {
		count = rs.getInt(1);
	    }

	    if (count > 0) {

		LOGGER.debug("<--Existe la numeracion, se ejecuta UPDATE---->");
		String sqlUpdate = "update pnn_detalle set IDO =?, TIPO_SERVICIO =?, MPP =?, IDA =?, AREA_SERVICIO =? WHERE "
			+ "NUMERO_INICIAL =? AND NUMERO_FINAL =?";
		ps1 = conn.prepareStatement(sqlUpdate);

		ps1.setInt(1, numero.getIdo());
		ps1.setString(2, String.valueOf(numero.getTipoServicio()));
		ps1.setString(3, String.valueOf(numero.getMpp()));
		ps1.setInt(4, numero.getIda());
		ps1.setInt(5, numero.getAreaServicio());

		// WHERE
		ps1.setLong(6, numero.getId().getNumeroInicial());
		ps1.setLong(7, numero.getId().getNumeroFinal());
		ps1.executeQuery();

		LOGGER.debug("<--Numero Inicial :" + numero.getId().getNumeroInicial() + ", numero final: "
			+ numero.getId().getNumeroFinal() + " Actualizados");
		conn.commit();
		rs.close();
		ps.close();
		ps1.close();

	    } else {
		LOGGER.debug("<- -NoExiste la numeracion, se ejecuta INSERT---->");
		String sqlInsert = "Insert into pnn_detalle (IDO, NUMERO_INICIAL, NUMERO_FINAL, TIPO_SERVICIO, MPP, IDA, "
			+ "AREA_SERVICIO) values(?,?,?,?,?,?,?)";
		ps2 = conn.prepareStatement(sqlInsert);
		ps2.setInt(1, numero.getIdo());
		ps2.setLong(2, numero.getId().getNumeroInicial());
		ps2.setLong(3, numero.getId().getNumeroFinal());
		ps2.setString(4, String.valueOf(numero.getTipoServicio()));
		ps2.setString(5, String.valueOf(numero.getMpp()));
		ps2.setInt(6, numero.getIda());
		ps2.setInt(7, numero.getAreaServicio());

		ps2.executeQuery();

		LOGGER.debug("<--Numero Inicial :" + numero.getId().getNumeroInicial() + ", numero final: "
			+ numero.getId().getNumeroFinal() + " Insertados");
		conn.commit();

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

		conn.close();
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

	    conn.close();
	}

    }

}
