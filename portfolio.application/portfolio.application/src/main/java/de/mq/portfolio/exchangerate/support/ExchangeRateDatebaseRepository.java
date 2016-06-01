package de.mq.portfolio.exchangerate.support;

import java.util.Collection;

import de.mq.portfolio.exchangerate.ExchangeRate;

interface ExchangeRateDatebaseRepository {

	void save(ExchangeRate exchangeRate);

	Collection<ExchangeRate> exchangerates(final Collection<ExchangeRate>   exchangerates);

	Collection<ExchangeRate> exchangerates();

}