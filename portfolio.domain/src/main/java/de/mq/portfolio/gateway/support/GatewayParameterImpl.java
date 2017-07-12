package de.mq.portfolio.gateway.support;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.Assert;

import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;

@Document(collection = "GatewayParameter")
class GatewayParameterImpl implements GatewayParameter {

	@Id
	private final String id;
	
	private final String urlTemplate;
 
	private final Map<String, String> parameters = new HashMap<>();

	GatewayParameterImpl(final String code, final Gateway gateway,final String urlTemplate, final Map<String, String> parameters) {
		Assert.hasText(code, "Code is mandatory.");
		Assert.notNull(gateway, "Gateway is mandatory.");
		Assert.hasText(urlTemplate, "UrlTemplate is mandatory.");
		Assert.notEmpty(parameters, "At least one Parameter should exist.");
		parameters.keySet().forEach(parameter -> Assert.hasText("Parameter key is mandatory."));
		parameters.values().forEach(parameter -> Assert.hasText("Parameter value is mandatory."));
		this.id = gateway.id(code);
		this.urlTemplate=urlTemplate;
		this.parameters.putAll(parameters);
	}
	
	@SuppressWarnings("unused")
	private GatewayParameterImpl() {
		id=null;
		urlTemplate=null;
   }

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.portfolio.share.support.ShareGatewayParameter#parameters()
	 */
	@Override
	public Map<String, String> parameters() {
		return Collections.unmodifiableMap(parameters);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.portfolio.gateway.ShareGatewayParameter#code()
	 */
	@Override
	public String code() {
		return Gateway.code(id);
	}

	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.gateway.ShareGatewayParameter#gateway()
	 */
	@Override
	public Gateway gateway() {
		return Gateway.gateway(id);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.gateway.GatewayParameter#urlTemplate()
	 */
	@Override
	public final String urlTemplate() {
		return urlTemplate;
	}

}
