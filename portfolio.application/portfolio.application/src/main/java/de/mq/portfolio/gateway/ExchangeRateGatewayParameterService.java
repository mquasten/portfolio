package de.mq.portfolio.gateway;


import java.util.Collection;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.gateway.Gateway;

public interface ExchangeRateGatewayParameterService {

	GatewayParameterAggregation<ExchangeRate> aggregationForRequiredGateway(final ExchangeRate exchangeRate, final Gateway gateway);

	Collection<GatewayParameter> allGatewayParameters(final ExchangeRate exchangeRate);

	String history(final GatewayParameter gatewayParameter);

	GatewayParameterAggregation<Collection<ExchangeRate>> merge(Collection<ExchangeRate> exhangerates, final Gateway gateway);

	

}