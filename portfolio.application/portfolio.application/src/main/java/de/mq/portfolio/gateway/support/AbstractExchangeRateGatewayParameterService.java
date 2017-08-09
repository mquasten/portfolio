package de.mq.portfolio.gateway.support;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.gateway.ExchangeRateGatewayParameterService;
import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.Gateway.GatewayGroup;
import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.gateway.GatewayParameterAggregation;

@Service
abstract class AbstractExchangeRateGatewayParameterService implements ExchangeRateGatewayParameterService {

	private final GatewayParameterRepository gatewayParameterRepository;

	private final GatewayHistoryRepository gatewayHistoryRepository;

	@Autowired
	AbstractExchangeRateGatewayParameterService(final GatewayParameterRepository gatewayParameterRepository, final GatewayHistoryRepository gatewayHistoryRepository) {
		this.gatewayParameterRepository = gatewayParameterRepository;
		this.gatewayHistoryRepository = gatewayHistoryRepository;
	}

	@Override
	public GatewayParameterAggregation<ExchangeRate> aggregationForRequiredGateway(final ExchangeRate exchangeRate, final Gateway gateway) {
		exchangeRateMandatoryGurad(exchangeRate);
		gatewayGroupGuard(gateway);
		final GatewayParameter gatewayParameter = gatewayParameterRepository.gatewayParameter(gateway, exchangeRate.source(), exchangeRate.target());
		return gatewayParameterAggregationBuilder().withDomain(exchangeRate).withGatewayParameter(gatewayParameter).build();

	}

	private void gatewayGroupGuard(final Gateway gateway) {
		Assert.notNull(gateway, "Gateway is mandatory.");
		Assert.notNull(gateway.gatewayGroup(), "GatewayGroup is mandatory.");
		Assert.isTrue(gateway.gatewayGroup() == GatewayGroup.ExchangeRate, String.format("Wrong GatewayGroup %s, GatewayGroup must by %s.", gateway.gatewayGroup(), GatewayGroup.ExchangeRate));
	}

	private void exchangeRateMandatoryGurad(final ExchangeRate exchangeRate) {
		Assert.notNull(exchangeRate, "ExchangeRate is mandatory.");
		Assert.hasText(exchangeRate.source(), "Source is mandatory.");
		Assert.hasText(exchangeRate.target(), "Target is mandatory.");
	}

	@Override
	public GatewayParameterAggregation<ExchangeRate> aggregationForAllGateways(final ExchangeRate exchangeRate) {
		exchangeRateMandatoryGurad(exchangeRate);
		final Collection<GatewayParameter> gatewayParameters = gatewayParameterRepository.gatewayParameters(exchangeRate.source(), exchangeRate.target());
		gatewayParameters.stream().map(gatewayParameter -> gatewayParameter.gateway()).forEach(gateway -> gatewayGroupGuard(gateway));
		return gatewayParameterAggregationBuilder().withDomain(exchangeRate).withGatewayParameters(gatewayParameters).build();
	}

	@Override
	public String history(final GatewayParameter gatewayParameter) {
		return gatewayHistoryRepository.history(gatewayParameter).getBody();
	}

	@Lookup
	abstract GatewayParameterAggregationBuilder<ExchangeRate> gatewayParameterAggregationBuilder();
}