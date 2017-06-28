package de.mq.portfolio.gateway.support;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.Assert;

import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.ShareGatewayParameter;

@Document(collection = "ShareGatewayParameter")
class ShareGatewayParameterImpl implements ShareGatewayParameter {

	@Id
	private final String id;

	private final Map<String, String> parameters = new HashMap<>();

	ShareGatewayParameterImpl(final String code, final Gateway gateway, final Map<String, String> parameters) {
		Assert.hasText(code, "Code is mandatory.");
		Assert.notNull(gateway, "Gateway is mandatory.");
		Assert.notEmpty(parameters, "At least one Parameter should exist.");
		parameters.keySet().forEach(parameter -> Assert.hasText("Parameter key is mandatory."));
		parameters.values().forEach(parameter -> Assert.hasText("Parameter value is mandatory."));
		this.id = gateway.id(code);

		this.parameters.putAll(parameters);
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

}
