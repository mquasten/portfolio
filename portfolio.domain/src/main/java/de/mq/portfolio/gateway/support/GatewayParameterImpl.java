package de.mq.portfolio.gateway.support;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.annotation.Id;
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

	@Id
	private final String id;
	
	private final String urlTemplate;
	
	@SuppressWarnings("unused")
	private final String parameterExpression;
 
	//@Transient
	private final  Map<String, String> parameters = new HashMap<>();

	@Deprecated
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
		this.parameterExpression=null;
	} 
	
	GatewayParameterImpl(final String code, final Gateway gateway,final String urlTemplate, final String parameterExpression) {
		Assert.hasText(code, "Code is mandatory.");
		Assert.notNull(gateway, "Gateway is mandatory.");
		Assert.hasText(urlTemplate, "UrlTemplate is mandatory.");
	
		this.id = gateway.id(code);
		this.urlTemplate=urlTemplate;
		this.parameterExpression=parameterExpression;
		initParameters(parameterExpression);
		
	}
	
	


	void initParameters(final String parameters) {
		Assert.hasText(parameters, "ParameterString is mandatory.");

		final ExpressionParser parser = new SpelExpressionParser();
		final StandardEvaluationContext context = new StandardEvaluationContext(new HistoryDateUtil());
		@SuppressWarnings("unchecked")
		final Map<String, String> parameterMap = (Map<String, String>) parser.parseExpression(parameters).getValue(context);
		this.parameters.putAll(parameterMap);
			
		Assert.notEmpty(this.parameters, "At least one Parameter should exist.");
		this.parameters.keySet().forEach(parameter -> Assert.hasText("Parameter key is mandatory."));
		this.parameters.values().forEach(parameter -> Assert.hasText("Parameter value is mandatory."));
	}
	
	@SuppressWarnings("unused")
	private GatewayParameterImpl() {
		id=null;
		urlTemplate=null;
		parameterExpression=null;
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
