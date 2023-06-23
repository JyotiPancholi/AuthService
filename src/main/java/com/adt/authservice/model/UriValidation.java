package com.adt.authservice.model;


public class UriValidation {
	
	private String uri;
	private String methedType;
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getMethedType() {
		return methedType;
	}
	public void setMethedType(String methedType) {
		this.methedType = methedType;
	}
	
	public UriValidation() {
		
	}
	public UriValidation(String uri, String methedType) {
		super();
		this.uri = uri;
		this.methedType = methedType;
	}
	

}
