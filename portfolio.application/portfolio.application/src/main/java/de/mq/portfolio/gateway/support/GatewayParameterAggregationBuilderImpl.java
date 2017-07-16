package de.mq.portfolio.gateway.support;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.gateway.GatewayParameterAggregation;

@Component
@Scope("prototype")
class GatewayParameterAggregationBuilderImpl<T> implements GatewayParameterAggregationBuilder<T> {
	
	private T domain;
	
	private GatewayParameter gatewayParameter;
	
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
		Assert.isNull(this.gatewayParameter , "GatewayParameter already assigned.");
		this.gatewayParameter=gatewayParameter;
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
		Assert.notNull(gatewayParameter, "GatewayParameter is mandatory.");
		return new GatewayParameterAggregationImpl<>(domain, gatewayParameter);
	}

}
