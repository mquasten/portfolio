package de.mq.portfolio.exchangerate.support;

import java.util.Collection;

import de.mq.portfolio.exchangerate.ExchangeRate;

@FunctionalInterface
public interface RealtimeExchangeRateRepository {

	Collection<ExchangeRate> exchangeRates(Collection<ExchangeRate> rates);

}
