package de.mq.portfolio.gateway.support;

import java.util.Collection;

import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;

interface MergedGatewayParameterBuilder {

	MergedGatewayParameterBuilder withGatewayParameter(Collection<GatewayParameter> gatewayParameters);

	MergedGatewayParameterBuilder withGateway(Gateway gateway);

	GatewayParameter build();

}