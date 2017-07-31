package de.mq.portfolio.gateway;

import java.util.Collection;

import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.share.Share;

public interface ShareGatewayParameterService {

	GatewayParameterAggregation<Share> gatewayParameter(final Share share, final Collection<Gateway> gateways);

	GatewayParameterAggregation<Share> gatewayParameters(final Share share);

	String history(final GatewayParameter gatewayParameter);

	

}