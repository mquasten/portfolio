package de.mq.portfolio.exchangerate.support;

import de.mq.portfolio.exchangerate.ExchangeRate;

public interface ExchangeRateDatebaseRepository {

	void save(ExchangeRate exchangeRate);

	ExchangerateAggregate exchangerates(final ExchangeRate ...  exchangerates);

}