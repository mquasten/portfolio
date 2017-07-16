package de.mq.portfolio.gateway.support;

import java.util.Collection;
import java.util.stream.Collectors;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Service;

import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.gateway.GatewayParameterAggregation;
import de.mq.portfolio.share.Share;

@Service
abstract class ShareGatewayParameterServiceImpl implements ShareGatewayParameterService {

	private final GatewayParameterRepository gatewayParameterRepository;

	@Autowired
	ShareGatewayParameterServiceImpl(final GatewayParameterRepository gatewayParameterRepository) {
		this.gatewayParameterRepository = gatewayParameterRepository;
	}

	/* (non-Javadoc)
	 * @see de.mq.portfolio.gateway.support.ShareGatewayParameterService#gatewayParameter(de.mq.portfolio.share.Share, de.mq.portfolio.gateway.Gateway)
	 */
	@Override
	public GatewayParameterAggregation<Share> gatewayParameter(final Share share, final Gateway gateway) {
		final GatewayParameter gatewayParameter = gatewayParameterRepository.gatewayParameter(gateway, share.code());
		return new GatewayParameterAggregationImpl<>(share, gatewayParameter);

	}

	/* (non-Javadoc)
	 * @see de.mq.portfolio.gateway.support.ShareGatewayParameterService#gatewayParameters(de.mq.portfolio.share.Share)
	 */
	@Override
	public Collection<GatewayParameterAggregation<Share>> gatewayParameters(final Share share) {
		return gatewayParameterRepository.gatewayParameters(share.code()).stream().map(gatewayParameter -> gatewayParameterAggregationBuilder().withDomain(share).withGatewayParameter(gatewayParameter).build()).collect(Collectors.toList());
	}

	

	@Lookup
	abstract GatewayParameterAggregationBuilder<Share> gatewayParameterAggregationBuilder();
}
