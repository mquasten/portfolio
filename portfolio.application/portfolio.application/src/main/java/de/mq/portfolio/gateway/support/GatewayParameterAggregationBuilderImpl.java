package de.mq.portfolio.gateway.support;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.gateway.GatewayParameterAggregation;

@Component
@Scope("prototype")
class GatewayParameterAggregationBuilderImpl<T> implements GatewayParameterAggregationBuilder<T> {
	
	private T domain;
	
	private Map<Gateway,GatewayParameter> gatewayParameters = new HashMap<>();
	
	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.gateway.support.GatewayParameterAggregationBuilder#withDomain(java.lang.Object)
	 */
	@Override
	public GatewayParameterAggregationBuilder<T> withDomain(final T domain) {
		Assert.isNull(this.domain , "Domain already assigned.");
		this.domain=domain;
		return this;
	}
	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.gateway.support.GatewayParameterAggregationBuilder#withGatewayParameter(de.mq.portfolio.gateway.GatewayParameter)
	 */
	@Override
	public GatewayParameterAggregationBuilder<T> withGatewayParameter(final GatewayParameter  gatewayParameter) {
		Assert.notNull(gatewayParameter, "GatewayParameter is mandatory.");
		Assert.notNull(gatewayParameter.gateway(), "Gateway is mandatory.");
		
		Assert.isTrue(!this.gatewayParameters.containsKey(gatewayParameter.gateway()) , String.format("GatewayParameter for %s already assigned.", gatewayParameter.gateway()));
		this.gatewayParameters.put(gatewayParameter.gateway(), gatewayParameter);
		return this;
	}
	
	@Override
	public GatewayParameterAggregationBuilder<T> withGatewayParameters(final Collection<GatewayParameter>  gatewayParameters) {
		Assert.isTrue(!CollectionUtils.isEmpty(gatewayParameters), "At least 1 GatewayParameter is required.");
		gatewayParameters.forEach(gatewayParameter -> withGatewayParameter(gatewayParameter));
		return this;
	}
	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.gateway.support.GatewayParameterAggregationBuilder#build()
	 */
	@Override
	public
	GatewayParameterAggregation<T> build() {
		Assert.notNull(domain, "Domain is mandatory.");
		Assert.isTrue(!CollectionUtils.isEmpty(gatewayParameters), "At least 1 GatewayParameter is required.");
		
		return new GatewayParameterAggregationImpl<>(domain, gatewayParameters.values());
	}

}
