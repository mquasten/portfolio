package de.mq.portfolio.gateway;



import java.util.Collection;
import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.share.Share;

public interface ShareGatewayParameterService {

	GatewayParameterAggregation<Share> aggregationForRequiredGateways(final Share share, final Collection<Gateway> gateways);

	Collection<GatewayParameter> allGatewayParameters(final Share share);

	String history(final GatewayParameter gatewayParameter);

	GatewayParameterAggregation<Collection<Share>>  merge(Collection<Share> shares, Gateway gateway);

	

}