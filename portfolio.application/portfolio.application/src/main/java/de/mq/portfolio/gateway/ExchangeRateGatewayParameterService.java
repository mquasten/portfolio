package de.mq.portfolio.gateway;


import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.gateway.Gateway;

public interface ExchangeRateGatewayParameterService {

	GatewayParameterAggregation<ExchangeRate> aggregationForRequiredGateway(final ExchangeRate exchangeRate, final Gateway gateway);

	GatewayParameterAggregation<ExchangeRate> aggregationForAllGateways(final ExchangeRate exchangeRate);

	String history(final GatewayParameter gatewayParameter);

	

}