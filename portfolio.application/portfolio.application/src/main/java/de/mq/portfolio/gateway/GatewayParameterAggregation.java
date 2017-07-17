package de.mq.portfolio.gateway;

import java.util.Collection;

import de.mq.portfolio.gateway.GatewayParameter;

public interface GatewayParameterAggregation<T> {

	T domain();

	Collection<GatewayParameter> gatewayParameters();
	
	GatewayParameter gatewayParameter(final Gateway gateway);

}