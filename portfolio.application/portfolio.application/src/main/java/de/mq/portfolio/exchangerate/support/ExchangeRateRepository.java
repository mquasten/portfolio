package de.mq.portfolio.exchangerate.support;

import java.util.Collection;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameterAggregation;
import de.mq.portfolio.share.Data;

interface ExchangeRateRepository {

	Collection<Data> history(final GatewayParameterAggregation<ExchangeRate> gatewayParameterAggregation);
	
	
	Gateway supports() ; 

}