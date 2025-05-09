package com.ift.sns.utils.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import mx.ift.sns.utils.dbConnection.ConnectionBD;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ConnectionBDTest {
	

	//@Ignore
	@Test
	public void test1ConnectionBD()
	{
		Connection conn=null;
		try {
		conn=ConnectionBD.getConnection();
				
		String sql = "Select * from cat_parametro";
		
		PreparedStatement stmt = conn.prepareStatement(sql);
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			String id_paramtetro = rs.getString("ID_PARAMETRO");
			String nombre = rs.getString("NOMBRE");
			System.out.println("Contenido tabla CAT_PARAMETRO :"+"ID: "+id_paramtetro+", NOMBRE: "+nombre+"\n");
			
		}
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				if(conn!=null) {
					conn.close();
				}
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		

	}
	
	
	@Ignore
	@Test
	public void test2ConnectionBD()
	{
		Connection conn=null;
		try {
		conn=ConnectionBD.getConnection();
				
		String sql = "Select count(1) from cat_parametro";
		
		PreparedStatement stmt = conn.prepareStatement(sql);
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			int count=rs.getInt(1);
			System.out.println(count);
		}
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		

	}

	
}