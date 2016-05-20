package de.mq.portfolio.exchangerate.support;

import de.mq.portfolio.exchangerate.ExchangeRate;

interface ExchangeRateDatebaseRepository {

	void save(ExchangeRate exchangeRate);

}