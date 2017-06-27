package de.mq.portfolio.share.support;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import de.mq.portfolio.share.ShareGatewayParameter;

@Document(collection = "ShareGatewayParameter")
class ShareGatewayParameterImpl implements ShareGatewayParameter {
	static final String DELIMITER = "-";

	@Id
	private final String id;

	private final Map<String, String> parameters = new HashMap<>();

	ShareGatewayParameterImpl(final String code, final Gateway gateway, final Map<String, String> parameters) {
		Assert.hasText(code, "Code is mandatory.");
		Assert.notNull(gateway, "Gateway is mandatory.");
		Assert.notEmpty(parameters, "At least one Parameter should exist.");
		parameters.keySet().forEach(parameter -> Assert.hasText("Parameter key is mandatory."));
		parameters.values().forEach(parameter -> Assert.hasText("Parameter value is mandatory."));
		this.id = id(code, gateway);

		this.parameters.putAll(parameters);
	}

	String id(final String code, final Gateway gateway) {
		return StringUtils.trimAllWhitespace(code) + DELIMITER + StringUtils.trimAllWhitespace(gateway.id());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.portfolio.share.support.ShareGatewayParameter#code()
	 */
	@Override
	public String code() {
		return id(0);
	}

	private String id(final int index) {
		Assert.isTrue(index == 0 || index == 1, "Index must be in [0..1].");
		Assert.hasText(id, "Id is mandatory.");
		final String[] results = id.split(String.format("[%s]", DELIMITER));
		Assert.isTrue(results.length == 2, "Composite Key should have 2 columns.");
		return results[index];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.portfolio.share.support.ShareGatewayParameter#gateway()
	 */
	@Override
	public Gateway gateway() {
		return Gateway.forId(id(1));
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

}
