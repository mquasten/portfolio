package de.mq.portfolio.exchangerate.support;

import java.util.Collection;

import de.mq.portfolio.exchangerate.ExchangeRate;

public interface ExchangeRateDatebaseRepository {

	void save(ExchangeRate exchangeRate);

	ExchangerateAggregate exchangerates(final Collection<ExchangeRate>   exchangerates);

}