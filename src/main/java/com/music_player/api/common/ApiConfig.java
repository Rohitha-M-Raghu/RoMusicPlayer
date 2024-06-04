//$Id$
package com.music_player.api.common;

import java.util.List;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "api-config")
@XmlAccessorType(XmlAccessType.FIELD)
public class ApiConfig {
	
	@XmlElement(name = "api")
	private List<Api> apis;
	
	public List<Api> getApis() {
		return apis;
	}

	public void setApis(List<Api> apis) {
		this.apis = apis;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Api {
		
		@XmlAttribute(name = "name")
		private String name;
		
		@XmlElement(name = "endpoint")
		
		private List<EndPoint> endpoints;
		
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public List<EndPoint> getEndpoints() {
			return endpoints;
		}

		public void setEndpoints(List<EndPoint> endpoints) {
			this.endpoints = endpoints;
		}	
	}
	
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class EndPoint {
		
		@XmlAttribute(name = "path")
		private String path;
		
		@XmlAttribute(name = "method")
		private String method;
		
		@XmlElement(name = "action")
		private List<Action> actions;
		
		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		public String getMethod() {
			return method;
		}

		public void setMethod(String method) {
			this.method = method;
		}

		public List<Action> getActions() {
			return actions;
		}

		public void setActions(List<Action> actions) {
			this.actions = actions;
		}

		
	}
	
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Action {
		
		@XmlAttribute(name = "name")
		private String name;
		
		@XmlAttribute(name = "service-class")
		private String serviceClass;
		
		@XmlAttribute(name = "method")
		private String method;
		
		@XmlElement(name = "param")
		private List<Param> params;
		
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getServiceClass() {
			return serviceClass;
		}

		public void setServiceClass(String serviceClass) {
			this.serviceClass = serviceClass;
		}

		public String getMethod() {
			return method;
		}

		public void setMethod(String method) {
			this.method = method;
		}

		public List<Param> getParams() {
			return params;
		}

		public void setParams(List<Param> params) {
			this.params = params;
		}

	}
	
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Param {
		
		@XmlAttribute(name = "name")
		private String name;
		
		@XmlAttribute(name = "required")
		private boolean isRequired;
		
		@XmlAttribute(name = "type")
		private String type;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public boolean isRequired() {
			return isRequired;
		}

		public void setRequired(boolean isRequired) {
			this.isRequired = isRequired;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}
		
		public enum ParamTypePattern{
			USERNAME("username", "^[a-zA-Z0-9._]{2,20}$"),
		    PASSWORD("password", "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"),
		    INTEGER("integer", "^\\d+$");
			
			private final String type;
			private final String pattern;

			private ParamTypePattern(String type, String pattern) {
				this.type = type;
				this.pattern = pattern;
			}
			
			public String getType() {
				return type;
			}

			public String getPattern() {
				return pattern;
			}
			
			public static String getPatternFromType(String type) {
				for(ParamTypePattern typePattern: ParamTypePattern.values()) {
					if(typePattern.getType().equals(type)) {
						return typePattern.getPattern();
					}
				}
				return "";
			}
		}
		
	}


	
}
