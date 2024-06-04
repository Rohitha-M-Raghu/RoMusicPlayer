//$Id$
package com.music_player.api.userauthentication.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.music_player.db.DBConnector;

public class UserUtil {
	
	private final Connection conn;
	private String query;
	private PreparedStatement pstmt;
	private ResultSet res;
	
	public static UserUtil getInstance() {
		return UserUtilInstance.INSTANCE;
	}
	
	private static class UserUtilInstance {
		private static final UserUtil INSTANCE = new UserUtil();
	}
	
	private UserUtil() {
		this.conn = DBConnector.getInstance().getConnection();
	}
	
	// DB Calls and other methods
	public boolean isUserExist(String userName) throws SQLException {
		query = "SELECT COUNT(*) AS ISUSEREXISTS "
				+ "FROM User_Details "
				+ "WHERE USER_NAME = ?";
		pstmt = conn.prepareStatement(query);
		pstmt.setString(1, userName);
		res = pstmt.executeQuery();
		if(res.next()) {
			return res.getBoolean("ISUSEREXISTS");
		}
		return false;
	}
	
	public boolean validateUserNamePassword(String userName, String password) throws SQLException {
		query = "SELECT COUNT(*) AS ISUSEREXISTS "
				+ "FROM User_Details "
				+ "WHERE USER_NAME = ? AND Password = ?";
		pstmt = conn.prepareStatement(query);
		pstmt.setString(1, userName);
		pstmt.setString(2, password);
		res = pstmt.executeQuery();
		if(res.next()) {
			return res.getBoolean("ISUSEREXISTS");
		}
		return false;
	}
}
