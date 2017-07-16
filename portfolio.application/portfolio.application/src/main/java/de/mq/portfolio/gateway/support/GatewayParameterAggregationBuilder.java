package de.mq.portfolio.gateway.support;

import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.gateway.GatewayParameterAggregation;

interface GatewayParameterAggregationBuilder<T> {

	GatewayParameterAggregationBuilder<T> withDomain(final T domain);

	GatewayParameterAggregationBuilder<T> withGatewayParameter(final GatewayParameter gatewayParameter);

	GatewayParameterAggregation<T> build();

}