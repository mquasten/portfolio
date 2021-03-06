package de.mq.portfolio.exchangerate.support;

import java.util.Collection;
import java.util.Optional;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.exchangerate.ExchangeRateCalculator;

public interface ExchangeRateService {

	ExchangeRate exchangeRate(ExchangeRate exchangeRate);

	void save(ExchangeRate exchangeRate);

	ExchangeRateCalculator exchangeRateCalculator(Collection<ExchangeRate> exchangerates);

	ExchangeRateCalculator exchangeRateCalculator();
	
	Collection<ExchangeRate> exchangeRates();

	Collection<ExchangeRate> exchangeRates(Collection<ExchangeRate> exchangeRates);

	Collection<ExchangeRate> realTimeExchangeRates(Collection<ExchangeRate> exchangeRates);

	Optional<ExchangeRate> exchangeRateOrReverse(ExchangeRate exchangeRate);

}