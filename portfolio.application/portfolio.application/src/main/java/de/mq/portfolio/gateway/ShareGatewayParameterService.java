package de.mq.portfolio.gateway;



import java.util.Collection;
import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.share.Share;

public interface ShareGatewayParameterService {

	GatewayParameterAggregation<Share> aggregationForRequiredGateways(final Share share, final Collection<Gateway> gateways);

	GatewayParameterAggregation<Share> aggregationForAllGateways(final Share share);

	String history(final GatewayParameter gatewayParameter);

	

}