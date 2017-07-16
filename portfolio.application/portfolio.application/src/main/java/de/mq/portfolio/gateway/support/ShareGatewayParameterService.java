package de.mq.portfolio.gateway.support;

import java.util.Collection;

import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameterAggregation;
import de.mq.portfolio.share.Share;

interface ShareGatewayParameterService {

	GatewayParameterAggregation<Share> gatewayParameter(Share share, Gateway gateway);

	Collection<GatewayParameterAggregation<Share>> gatewayParameters(Share share);

}