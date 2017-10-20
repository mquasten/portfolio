package de.mq.portfolio.exchangerate.support;

import java.util.Collection;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameterAggregation;

public interface RealtimeExchangeRateRepository {

	Collection<ExchangeRate> exchangeRates(final GatewayParameterAggregation<Collection<ExchangeRate>>  gatewayParameterAggregation);
	
	Gateway supports(Collection<ExchangeRate> exchangeRates);

}
