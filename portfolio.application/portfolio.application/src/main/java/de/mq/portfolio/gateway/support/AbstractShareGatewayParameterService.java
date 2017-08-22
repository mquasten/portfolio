package de.mq.portfolio.gateway.support;

import java.util.Collection;
import java.util.stream.Collectors;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;


import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.gateway.GatewayParameterAggregation;
import de.mq.portfolio.gateway.ShareGatewayParameterService;
import de.mq.portfolio.share.Share;

@Service
abstract class AbstractShareGatewayParameterService implements ShareGatewayParameterService {

	private final GatewayParameterRepository gatewayParameterRepository;
	
	private final GatewayHistoryRepository gatewayHistoryRepository;

	@Autowired
	AbstractShareGatewayParameterService(final GatewayParameterRepository gatewayParameterRepository, final GatewayHistoryRepository gatewayHistoryRepository) {
		this.gatewayParameterRepository = gatewayParameterRepository;
		this.gatewayHistoryRepository=gatewayHistoryRepository;
	}

	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.gateway.support.ShareGatewayParameterService#gatewayParameter(de.mq.portfolio.share.Share, java.util.Collection)
	 */
	@Override
	public GatewayParameterAggregation<Share> aggregationForRequiredGateways(final Share share, final Collection<Gateway> gateways) {
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
	public GatewayParameterAggregation<Share> aggregationForAllGateways(final Share share) {
		shareRequiredGuard(share);
		return gatewayParameterAggregationBuilder().withGatewayParameters(gatewayParameterRepository.gatewayParameters(share.code())).withDomain(share).build();
	}
	
	public GatewayParameterAggregation<Share> merge(final Collection<Share> shares, final Gateway gateway) {
		final Collection<GatewayParameter> gatewayParameters = shares.stream().map(share -> gatewayParameterRepository.gatewayParameter(gateway, share.code())).collect(Collectors.toList());
		final  String key = gatewayParameters.stream().map(gatewayParameter -> gatewayParameter.code()).reduce("" , (a,b)-> a+b  );
		final String urlTemplate = DataAccessUtils.requiredSingleResult(gatewayParameters.stream().map(gatewayParameter -> gatewayParameter.urlTemplate().replaceAll("\\s+", "")).collect(Collectors.toSet()));
		return null;
	}

	
	
	public final void save(final GatewayParameter gatewayParameter) {
		gatewayParameterRepository.save(gatewayParameter);
	}
	

	@Override
	public final String history(final GatewayParameter gatewayParameter) {
		return gatewayHistoryRepository.history(gatewayParameter).getBody();
	}

	@Lookup
	abstract GatewayParameterAggregationBuilder<Share> gatewayParameterAggregationBuilder();
}
