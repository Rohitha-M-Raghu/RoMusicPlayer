//$Id$
package com.music_player.api.userauthentication.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

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
	public boolean isUserExist(String userName) throws Exception {
		query = "SELECT COUNT(*) AS ISUSEREXISTS "
				+ "FROM User_Details "
				+ "WHERE USER_NAME = ?";
		pstmt = conn.prepareStatement(query);
		pstmt.setString(1, userName);
		res = pstmt.executeQuery();
		if(!res.next()) {
			throw new Exception();
		}
		
		return res.getBoolean("ISUSEREXISTS");
	}
	
	public boolean isUserExist(int userId) throws Exception {
		query = "SELECT COUNT(*) AS ISUSEREXISTS "
				+ "FROM User_Details "
				+ "WHERE USER_ID  = ?";
		pstmt = conn.prepareStatement(query);
		pstmt.setInt(1, userId);
		res = pstmt.executeQuery();
		if(!res.next()) {
			throw new Exception();
		}
		
		return res.getBoolean("ISUSEREXISTS");
	}
	
	public boolean isEmailExist(String emailId) throws Exception {
		query = "SELECT COUNT(*) AS ISUSEREXISTS "
				+ "FROM User_Details "
				+ "WHERE EMAIL_ID = ?";
		pstmt = conn.prepareStatement(query);
		pstmt.setString(1, emailId);
		res = pstmt.executeQuery();
		if(!res.next()) {
			throw new Exception();
		}
		return res.getBoolean("ISUSEREXISTS");
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
	
	public boolean validateEmailPassword(String emailId, String password) throws SQLException {
		query = "SELECT COUNT(*) AS ISUSEREXISTS "
				+ "FROM User_Details "
				+ "WHERE EMAIL_ID = ? AND Password = ?";
		pstmt = conn.prepareStatement(query);
		pstmt.setString(1, emailId);
		pstmt.setString(2, password);
		res = pstmt.executeQuery();
		if(res.next()) {
			return res.getBoolean("ISUSEREXISTS");
		}
		return false;
	}
	
	public boolean addNewUser(User newUser) throws SQLException {
		if(newUser.getLastname() == null || newUser.getLastname().isEmpty()) {
			query = "INSERT INTO User_Details(USER_NAME , Password, FIRST_NAME) VALUES (?, ?, ?)";
		} else {
			query = "INSERT INTO User_Details(USER_NAME , Password, FIRST_NAME , LAST_NAME) VALUES (?, ?, ?, ?)";
		}
		pstmt = conn.prepareStatement(query);
		pstmt.setString(1, newUser.getUserName());
		pstmt.setString(2, newUser.getPassword());
		pstmt.setString(3, newUser.getFirstName());
		if(newUser.getLastname() == null || newUser.getLastname().isEmpty()) {
			pstmt.setString(4, newUser.getLastname());
		}
		return pstmt.executeUpdate() > 0;
	}
	
	public Set<String> getAllExistingUserNames() throws SQLException {
		Set<String> userNames = new HashSet<>();
		query = "SELECT USER_NAME FROM User_Details";
		pstmt = conn.prepareStatement(query);
		res = pstmt.executeQuery();
		while(res.next()) {
			userNames.add(res.getString("USER_NAME"));
		}
		return userNames;
	}
}
