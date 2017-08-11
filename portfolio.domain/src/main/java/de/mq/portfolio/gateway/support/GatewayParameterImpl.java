package de.mq.portfolio.gateway.support;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.Assert;

import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.share.support.HistoryDateUtil;

@Document(collection = "GatewayParameter")
class GatewayParameterImpl implements GatewayParameter {

	static final String IDS_VARIABLE_NAME = "ids";

	@Id
	private final String id;

	private final String urlTemplate;

	private final String parameterExpression;

	@Transient
	private final Map<String, String> parameters = new HashMap<>();


	GatewayParameterImpl(final String code, final Gateway gateway, final String urlTemplate, final String parameterExpression) {
		Assert.hasText(code, "Code is mandatory.");
		Assert.notNull(gateway, "Gateway is mandatory.");
		Assert.hasText(urlTemplate, "UrlTemplate is mandatory.");

		this.id = gateway.id(code);
		this.urlTemplate = urlTemplate;
		this.parameterExpression = parameterExpression;
		initParameters();

	}

	void initParameters() {
		Assert.hasText(parameterExpression, "ParameterString is mandatory.");

		final ExpressionParser parser = new SpelExpressionParser();
		final StandardEvaluationContext context = new StandardEvaluationContext(new HistoryDateUtil());
		context.setVariable(IDS_VARIABLE_NAME, Gateway.ids(id));
		
		@SuppressWarnings("unchecked")
		final Map<String, String> parameterMap = (Map<String, String>) parser.parseExpression(parameterExpression).getValue(context);
		this.parameters.putAll(parameterMap);

		Assert.notEmpty(this.parameters, "At least one Parameter should exist.");
		this.parameters.keySet().forEach(parameter -> Assert.hasText("Parameter key is mandatory."));
		this.parameters.values().forEach(parameter -> Assert.hasText("Parameter value is mandatory."));
	}

	@SuppressWarnings("unused")
	private GatewayParameterImpl() {
		id = null;
		urlTemplate = null;
		parameterExpression = null;
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
	 * 
	 * @see de.mq.portfolio.gateway.ShareGatewayParameter#gateway()
	 */
	@Override
	public Gateway gateway() {
		return Gateway.gateway(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.portfolio.gateway.GatewayParameter#urlTemplate()
	 */
	@Override
	public final String urlTemplate() {
		return urlTemplate;
	}

}
