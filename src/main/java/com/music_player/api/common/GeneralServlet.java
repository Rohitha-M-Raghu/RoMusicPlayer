//$Id$
package com.music_player.api.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.json.JSONObject;


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
		JSONObject jsonResponse = new JSONObject();
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		
		// get required data
		String requestBody = JacksonUtils.convertReaderToString(request.getReader());
        JSONObject requestBodyJson = JacksonUtils.convertStringToJSONObject(requestBody);
        
        String action = requestBodyJson.optString("action");
        String path = request.getRequestURI();
        String method = request.getMethod();
        
        // endpoint
        ApiConfig.EndPoint endpoint = findEndpoint(path, method);
        if(endpoint == null) {
        	// return invalid url
        	jsonResponse.put("message", "Invalid URL");
        } else {
        	// get valid action 
            ApiConfig.Action configAction = findAction(endpoint, action);
            if(configAction == null) {
            	// invalid url - action
            	jsonResponse.put("message", "Invalid Action");

            } else {
            	// validate params
            	boolean isValid = validateParams(configAction.getParams(), requestBodyJson, jsonResponse);
            	if(isValid) {
            		// invoke service layer using reflection
            		invokeServiceMethod(configAction.getServiceClass(), configAction.getMethod(), requestBodyJson, jsonResponse);
            		
            	}
            }
        }
        
        out.print(JacksonUtils.convertJSONObjectToString(jsonResponse));
        out.flush();
	}
	
	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        
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
	
	
	private boolean validateParams(List<ApiConfig.Param> params, JSONObject requestBodyJson, JSONObject jsonResponse) {
		if(params == null) {
			return true;
		}
		// check for mandatory params
		for(ApiConfig.Param param: params) {
			if(param.isRequired() && !requestBodyJson.has(param.getName())) {
				// set jsonresponse
                jsonResponse.put("message", "Missing required parameter: " + param.getName());
                return false;
			}
			// validate param types - to implement
		}
		return true;
	}
	
	private void invokeServiceMethod(String serviceClassName, String serviceClassMethod, JSONObject requestBodyJson, JSONObject jsonResponse) {
		try {
			Class<?> serviceClass = Class.forName(serviceClassName);
			Object serviceInstance = serviceClass.getDeclaredConstructor().newInstance();
			Method method = serviceClass.getDeclaredMethod(serviceClassMethod, JSONObject.class, JSONObject.class);
			method.invoke(serviceInstance, requestBodyJson, jsonResponse);
		} catch (ClassNotFoundException e) {
            jsonResponse.put("message", "Error invoking service method: " + e.getMessage());
		} catch (InstantiationException| IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}
}
