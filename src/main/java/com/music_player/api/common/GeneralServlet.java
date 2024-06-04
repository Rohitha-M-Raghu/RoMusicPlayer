//$Id$
package com.music_player.api.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.json.JSONObject;

import com.music_player.api.common.utils.JacksonUtils;


public class GeneralServlet extends HttpServlet{
	private ApiConfig apiConfig;
	
	@Override
	public void init() throws ServletException {
		super.init();
		
		try {
            InputStream inputStream = getServletContext().getResourceAsStream("/WEB-INF/api-config.xml");
            // Java Architecture for XML Binding
            JAXBContext context = JAXBContext.newInstance(ApiConfig.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            // converting xml data to java object
            apiConfig = (ApiConfig) unmarshaller.unmarshal(inputStream);
        } catch (JAXBException e) {
            throw new ServletException("Failed to load API configuration", e);
        }
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// get required data
		String requestBody = JacksonUtils.convertReaderToString(request.getReader());
        JSONObject requestBodyJson = JacksonUtils.convertStringToJSONObject(requestBody);
        
        String action = requestBodyJson.optString("action");
        String path = request.getRequestURI();
        String method = request.getMethod();
        
        // get token from cookie
        String token = getTokenFromCookie(request);
        // endpoint
        // call get response
        Response serverResponse = getResponse(path, method, action, requestBodyJson, token);
        serverResponse.setHttpServletResponse(response);
	}
	
	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        
    }
	
	private String getTokenFromCookie(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		for(Cookie cookie: cookies) {
			if(cookie.getName().equals("token")) {
				return cookie.getValue();
			}
		}
		return null;
	}
	
	private Response getResponse(String path, String method, String action, JSONObject requestBodyJson, String token) {
		Response serverResponse;
		ApiConfig.EndPoint endpoint = findEndpoint(path, method);
        if(endpoint == null) {
        	// return invalid url
        	serverResponse = new Response.Builder().statusCode(HttpServletResponse.SC_NOT_FOUND).responseBody("Invalid URL").build();
        	return serverResponse;
        } else {
        	// get valid action 
            ApiConfig.Action configAction = findAction(endpoint, action);
            if(configAction == null) {
            	serverResponse = new Response.Builder().statusCode(HttpServletResponse.SC_NOT_FOUND).responseBody("Invalid URL").build();
            	return serverResponse;
            } else {
            	// validate params
            	serverResponse = validateParams(configAction.getParams(), requestBodyJson);
            	
            	// validate token here for all requests except login and signup
            	if(!configAction.getName().equals("login") && !configAction.getName().equals("signup")) {
            		// remove the following
            		String userName = "ro";
            		requestBodyJson.put("userName", userName);
            		
            		// if token == null - 403 unauthorized
            		// decrypt token and get username from it and add it to requestBodyJSON
            		// if invalid token, return response
            	}
            	
            	if(serverResponse == null) {
            		return invokeServiceMethod(configAction.getServiceClass(), configAction.getMethod(), requestBodyJson);
            	} else {
            		return serverResponse;
            	}
            	
            }
        }
	}
	
	private ApiConfig.EndPoint findEndpoint(String path, String method) {
		for(ApiConfig.Api api: apiConfig.getApis()) {
			for(ApiConfig.EndPoint endpoint: api.getEndpoints()) {
				Pattern pattern = Pattern.compile(endpoint.getPath());
				Matcher matcher = pattern.matcher(path);
				if(matcher.matches() && endpoint.getMethod().equalsIgnoreCase(method)) {
					return endpoint;
				}
			}
		}
		return null;
	}
	
	private ApiConfig.Action findAction(ApiConfig.EndPoint endpoint, String action) {
		if(action.equals("")) {
			if (endpoint.getActions().size() == 1) {
                return endpoint.getActions().get(0);
            } else {
                return null;
            }
		}
		
		for(ApiConfig.Action endpointAction: endpoint.getActions()) {
			if(endpointAction.getName().equals(action)) {
				return endpointAction;
			}
		}
		return null;
	}
	
	
	private Response validateParams(List<ApiConfig.Param> params, JSONObject requestBodyJson) {
		String errorMsg = "";
		if(params == null) {
			return null;
		}
		// check for extra param
		Set<String> validParamNames = params.stream()
                .map(ApiConfig.Param::getName)
                .collect(Collectors.toSet());
		
		for (String key : requestBodyJson.keySet()) {
	        if (!validParamNames.contains(key) && !key.equals("action")) {
	            errorMsg = "An extra parameter '" + key + "' is found";
	            return new Response.Builder()
	                               .statusCode(422)
	                               .responseBody(errorMsg)
	                               .build();
	        }
	    }
		
		// check for mandatory params
		for(ApiConfig.Param param: params) {
			if(param.isRequired() && !requestBodyJson.has(param.getName())) {
				// set jsonresponse
				errorMsg = "Missing required parameter: " + param.getName();
			}
			// validate param types - to implement
			if(errorMsg.equals("") && !validateParamType(param.getType(), requestBodyJson.getString(param.getName()))) {
				// couldn't find 422 - unprocessable entity
				errorMsg = "Invalid parameter type for " + param.getName();	
			}
			if(!errorMsg.equals("")) {
				// send 422 instead of bad request
				return new Response.Builder().statusCode(422).responseBody(errorMsg).build();
			}
		}
		return null;
	}
	
	private boolean validateParamType(String type, String value) {
		String stringPattern = ApiConfig.Param.ParamTypePattern.getPatternFromType(type);
		Pattern pattern = Pattern.compile(ApiConfig.Param.ParamTypePattern.getPatternFromType(type));
		Matcher matcher = pattern.matcher(value);
		return matcher.matches();
	}
	
	private Response invokeServiceMethod(String serviceClassName, String serviceClassMethod, JSONObject requestBodyJson) {
		try {
			Class<?> serviceClass = Class.forName(serviceClassName);
			Object serviceInstance = serviceClass.getDeclaredConstructor().newInstance();
			Method method = serviceClass.getDeclaredMethod(serviceClassMethod, JSONObject.class);
			return (Response) method.invoke(serviceInstance, requestBodyJson);
		} catch (Exception e) {
			return new Response.Builder().statusCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).build();
		}
	}
	
	private void setResponse(Response response, HttpServletResponse servletReponse) throws IOException {
		if(response == null) {
			// find the right status
			servletReponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		PrintWriter out = servletReponse.getWriter();
		servletReponse.setStatus(response.getStatusCode());
		if(response.getCookie() != null) {
			servletReponse.addCookie(response.getCookie());
		}
		out.print(response.getResponseBody());
        out.flush();
	}
	
	private String getResponseErrorMsg(String errorMsg) {
		JSONObject errorResponse = new JSONObject();
		errorResponse.put("message", errorMsg);
		return JacksonUtils.convertJSONObjectToString(errorResponse);
	}
}
