package de.mq.portfolio.exchangerate.support;

import java.util.Collection;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.exchangerate.ExchangeRateCalculator;

interface ExchangeRateCalculatorBuilder {

	ExchangeRateCalculatorBuilder withExchangeRate(ExchangeRate exchangeRate);

	ExchangeRateCalculatorBuilder withExchangeRates(Collection<ExchangeRate> exchangeRates);

	ExchangeRateCalculator build();

}