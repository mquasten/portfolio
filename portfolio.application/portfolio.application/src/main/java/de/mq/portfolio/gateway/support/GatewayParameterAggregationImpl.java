package de.mq.portfolio.gateway.support;

import org.springframework.util.Assert;

import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.gateway.GatewayParameterAggregation;


class GatewayParameterAggregationImpl<T> implements GatewayParameterAggregation<T> {
	
	private  final T domain;

	private  final GatewayParameter gatewayParameter;
	
	GatewayParameterAggregationImpl(final T domain, final GatewayParameter gatewayParameter) {
		Assert.notNull(domain);
		Assert.notNull(gatewayParameter);
		this.domain = domain;
		this.gatewayParameter = gatewayParameter;
	}
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.gateway.support.GatewayParameterAggregation#domain()
	 */
	@Override
	public final T domain() {
		return domain;
	}

	/* (non-Javadoc)
	 * @see de.mq.portfolio.gateway.support.GatewayParameterAggregation#gatewayParameter()
	 */
	@Override
	public final GatewayParameter gatewayParameter() {
		return gatewayParameter;
	}



}
