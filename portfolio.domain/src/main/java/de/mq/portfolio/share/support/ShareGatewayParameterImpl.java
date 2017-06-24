package de.mq.portfolio.share.support;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.Assert;



@Document(collection="ShareGatewayParameter")
class ShareGatewayParameterImpl {
	
	@Id
	private final String code;

	@Id
	private final Gateway gateway;
	
	private final Map<String,String> parameters = new HashMap<>(); 
	
	ShareGatewayParameterImpl(final String code, final Gateway gateway,  final Map<String,String> parameters ) {
		Assert.hasText(code , "Code is mandatory.");
		Assert.notNull(gateway, "Gateway is mandatory.");
		Assert.notEmpty(parameters, "At least on Parameter should exist.");
		parameters.keySet().forEach(parameter -> Assert.hasText("Parameter key is mandatory."));
		parameters.values().forEach(parameter -> Assert.hasText("Parameter value is mandatory."));
		this.code=code;
		this.gateway=gateway;
		
	    this.parameters.putAll(parameters); 
	}

	
	
	public String code() {
		return code;
	}

	public Gateway gateway() {
		return gateway;
	}

	public Map<String, String> parameters() {
		return Collections.unmodifiableMap(parameters);
	}

}
