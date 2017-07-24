package de.mq.portfolio.gateway.support;

import java.util.Collection;
import java.util.stream.Collectors;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;


import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.gateway.GatewayParameterAggregation;
import de.mq.portfolio.gateway.ShareGatewayParameterService;
import de.mq.portfolio.share.Share;

@Service
abstract class ShareGatewayParameterServiceImpl implements ShareGatewayParameterService {

	private final GatewayParameterRepository gatewayParameterRepository;

	@Autowired
	ShareGatewayParameterServiceImpl(final GatewayParameterRepository gatewayParameterRepository) {
		this.gatewayParameterRepository = gatewayParameterRepository;
	}

	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.gateway.support.ShareGatewayParameterService#gatewayParameter(de.mq.portfolio.share.Share, java.util.Collection)
	 */
	@Override
	public GatewayParameterAggregation<Share> gatewayParameter(final Share share, final Collection<Gateway> gateways) {
		shareRequiredGuard(share);
		Assert.isTrue( !CollectionUtils.isEmpty(gateways) , "At least 1 gateway is required.");
		return gatewayParameterAggregationBuilder().withDomain(share).withGatewayParameters(gateways.stream().map(gateway -> gatewayParameterRepository.gatewayParameter(gateway, share.code())).collect(Collectors.toList())).build();
	}

	private void shareRequiredGuard(final Share share) {
		Assert.notNull(share, "Share is mandatory.");
		Assert.notNull(share.code(), "Code is mandatory.");
	}

	/* (non-Javadoc)
	 * @see de.mq.portfolio.gateway.support.ShareGatewayParameterService#gatewayParameters(de.mq.portfolio.share.Share)
	 */
	@Override
	public GatewayParameterAggregation<Share> gatewayParameters(final Share share) {
		shareRequiredGuard(share);
		return gatewayParameterAggregationBuilder().withGatewayParameters(gatewayParameterRepository.gatewayParameters(share.code())).withDomain(share).build();
	}

	
	
	public final void save(final GatewayParameter gatewayParameter) {
		gatewayParameterRepository.save(gatewayParameter);
	}

	@Lookup
	abstract GatewayParameterAggregationBuilder<Share> gatewayParameterAggregationBuilder();
}
