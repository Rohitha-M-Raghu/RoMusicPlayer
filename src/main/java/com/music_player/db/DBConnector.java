//$Id$
package com.music_player.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnector {
	private static final String URL = "jdbc:mysql://localhost:3306/music_player";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "";
	
	private final Connection conn;
	
	public static DBConnector getInstance() {
		return DBConnectorInstance.INSTANCE;
	}
	
	private static class DBConnectorInstance {
		private static final DBConnector INSTANCE = new DBConnector();
	}

	public DBConnector(){
		Connection tempConn = null;

		try {
			//Registering JDBC 
			Class.forName("com.mysql.cj.jdbc.Driver");
			//Creating Connection to Database
			tempConn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
			
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		conn = tempConn;
	}

	public Connection getConnection() {
		return conn;
	}
}
