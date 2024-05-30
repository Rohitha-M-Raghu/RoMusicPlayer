package com.userauthentication;

import common.json.JacksonUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;

public class UserAuthenticationServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        JSONObject jsonResponse = new JSONObject();
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        String requestBody;
		requestBody = JacksonUtils.convertReaderToString(request.getReader());
		
        JSONObject requestBodyJson = JacksonUtils.convertStringToJSONObject(requestBody);
        String action = requestBodyJson.getString("action");

        if ("login".equals(action)) {
            String username = requestBodyJson.getString("username");
            String password = requestBodyJson.getString("password");
            handleLogin(request, response, jsonResponse, username, password);
        } else if ("logout".equals(action)) {
            handleLogout(request, response, jsonResponse);
        } else {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Invalid action");
            out.print(JacksonUtils.convertJSONObjectToString(jsonResponse));
            out.flush();
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response, JSONObject jsonResponse, String username, String password) throws IOException {
        PrintWriter out = response.getWriter();

        if ("user".equals(username) && "pass".equals(password)) {
            HttpSession session = request.getSession(true);
            session.setAttribute("username", username);
            jsonResponse.put("success", true);
            jsonResponse.put("message", "Login successful");
        } else {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Invalid username or password");
        }

        out.print(JacksonUtils.convertJSONObjectToString(jsonResponse));
        out.flush();
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response, JSONObject jsonResponse) throws IOException {
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
            jsonResponse.put("success", true);
            jsonResponse.put("message", "Logout successful");
        } else {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "No session found");
        }

        out.print(JacksonUtils.convertJSONObjectToString(jsonResponse));
        out.flush();
    }
}
