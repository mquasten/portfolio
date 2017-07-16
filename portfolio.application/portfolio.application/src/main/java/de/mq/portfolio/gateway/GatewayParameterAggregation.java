package de.mq.portfolio.gateway;

import de.mq.portfolio.gateway.GatewayParameter;

public interface GatewayParameterAggregation<T> {

	T domain();

	GatewayParameter gatewayParameter();

}