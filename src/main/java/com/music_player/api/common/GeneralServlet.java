package com.music_player.api.common;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import com.music_player.api.common.utils.JacksonUtils;

public class GeneralServlet extends HttpServlet {
    private ApiConfig apiConfig;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            InputStream inputStream = getServletContext().getResourceAsStream("/WEB-INF/api-config.xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(ApiConfig.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            apiConfig = (ApiConfig) unmarshaller.unmarshal(inputStream);
        } catch (JAXBException e) {
            throw new ServletException("Failed to parse API configuration", e);
        }
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestPath = request.getRequestURI();
        String requestMethod = request.getMethod();
        
        Response serverResponse = getResponse(request, response, requestPath, requestMethod);
        serverResponse.setHttpServletResponse(response);
    }

	private Response getResponse(HttpServletRequest request, HttpServletResponse response, String requestPath, String requestMethod) throws IOException, ServletException {
		ApiConfig.EndPoint matchedEndPoint = null;
        
        Map<String, String> pathParams = null;

        
        for (ApiConfig.Api api : apiConfig.getApis()) {
            for (ApiConfig.EndPoint endpoint : api.getEndpoints()) {
                if (endpoint.getMethod().equalsIgnoreCase(requestMethod)) {
                    Pattern pattern = Pattern.compile(endpoint.getPath());
                    Matcher matcher = pattern.matcher(requestPath);
                    if (matcher.matches()) {
                        matchedEndPoint = endpoint;
                        pathParams = extractPathParams(endpoint.getPath(), requestPath);
                        break;
                    }
                }
            }
        }
        if(matchedEndPoint == null) {
        	return new Response.Builder().statusCode(HttpServletResponse.SC_NOT_FOUND).build();
        }

        
        ApiConfig.Action matchedAction = null;
        String requestBody = JacksonUtils.convertReaderToString(request.getReader());
        JSONObject requestBodyJSON = JacksonUtils.convertStringToJSONObject(requestBody);
        String requestAction = "";
        if(requestBodyJSON.has("action")) {
        	requestAction = requestBodyJSON.getString("action");
        }
             
    	if(requestAction.equals("")) {
			if (matchedEndPoint.getActions().size() == 1) {
				matchedAction = matchedEndPoint.getActions().get(0);
            }
    	} else {
    		for (ApiConfig.Action action : matchedEndPoint.getActions()) {
    			if (action.getName().equals(requestAction)) {
    				matchedAction = action;
    				break;
    			}
    		}
    	}
        

        if (matchedAction == null) {
        	return new Response.Builder().statusCode(HttpServletResponse.SC_NOT_FOUND).responseBody("Invalid URL").build();   
        }
        
        Map<String, String> queryParams = extractQueryParams(request, matchedAction.getQueryParams());

        RequestData requestData = new RequestData();
        requestData.setPathParams(pathParams);
        requestData.setQueryParams(queryParams);
        requestData.setRequestBodyJSON(requestBodyJSON);

        try {
			validateParams(matchedAction, requestData);
		} catch (UnprocessableEntityException e) {
			JSONObject responseBody = new JSONObject();
			responseBody.put("errorMsg", e.getMessage());
			return new Response.Builder().statusCode(422).responseBody(JacksonUtils.convertJSONObjectToString(responseBody)).build();
		}

        try {
            Class<?> serviceClass = Class.forName(matchedAction.getServiceClass());
            Object serviceInstance = serviceClass.getDeclaredConstructor().newInstance();
            Method method = serviceClass.getMethod(matchedAction.getMethod(), RequestData.class);
            return (Response) method.invoke(serviceInstance, requestData);
            
        } catch (Exception e) {
			return new Response.Builder().statusCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).build();        }
	}

    private Map<String, String> extractPathParams(String pathPattern, String requestPath) {
        Map<String, String> pathParams = new HashMap<>();
        Pattern pattern = Pattern.compile(pathPattern);
        Matcher matcher = pattern.matcher(requestPath);
        if (matcher.matches()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                pathParams.put("param" + i, matcher.group(i));
            }
        }
        return pathParams;
    }

    private Map<String, String> extractQueryParams(HttpServletRequest request, List<ApiConfig.QueryParam> queryParams) {
    	Map<String, String> extractedQueryParams = new HashMap<>();
    	if(queryParams == null || queryParams.isEmpty()) {
    		return extractedQueryParams;
    	}
        for (ApiConfig.QueryParam queryParam : queryParams) {
            String paramName = queryParam.getName();
            String paramValue = request.getParameter(paramName);
            if (paramValue != null) {
                extractedQueryParams.put(paramName, paramValue);
            }
        }
        return extractedQueryParams;
    }

    private void validateParams(ApiConfig.Action action, RequestData requestData) throws ServletException, UnprocessableEntityException {
        validatePathParams(action, requestData.getPathParams());
        validateQueryParams(action, requestData.getQueryParams());
        validateBodyParams(action, requestData.getRequestBodyJSON());
    }

    private void validatePathParams(ApiConfig.Action action, Map<String, String> pathParams) {
        // think this is unnecessary
    }

    private void validateQueryParams(ApiConfig.Action action, Map<String, String> queryParams) throws UnprocessableEntityException {
    	if(action.getQueryParams() == null || action.getQueryParams().isEmpty()) {
    		return;
    	}
        for (ApiConfig.QueryParam queryParam : action.getQueryParams()) {
            String paramName = queryParam.getName();
            String paramValue = queryParams.get(paramName);
            if (queryParam.isRequired() && paramValue == null) {
                throw new UnprocessableEntityException("Missing required query parameter: " + paramName);
            }
            if (paramValue != null) {
                String pattern = ApiConfig.Param.ParamTypePattern.getPatternFromType(queryParam.getType());
                if (!paramValue.matches(pattern)) {
                    throw new UnprocessableEntityException("Invalid query parameter type for: " + paramName);
                }
            }
        }
    }

    private void validateBodyParams(ApiConfig.Action action, JSONObject bodyParams) throws UnprocessableEntityException {
    	if(action.getParams() == null || action.getParams().isEmpty()) {
    		return;
    	}
    	
        for (ApiConfig.Param param : action.getParams()) {
            String paramName = param.getName();
            String paramValue = bodyParams.optString(paramName);
            if (param.isRequired() && paramValue.isEmpty()) {
                throw new UnprocessableEntityException("Missing required parameter: " + paramName);
            }
            if (!paramValue.isEmpty()) {
                String pattern = ApiConfig.Param.ParamTypePattern.getPatternFromType(param.getType());
                if (!paramValue.matches(pattern)) {
                    throw new UnprocessableEntityException("Invalid parameter type for: " + paramName);
                }
            }
        }
    }
}
