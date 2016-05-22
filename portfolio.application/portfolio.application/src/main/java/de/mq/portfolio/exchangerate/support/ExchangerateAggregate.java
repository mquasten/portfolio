package de.mq.portfolio.exchangerate.support;

import java.util.Date;

import de.mq.portfolio.exchangerate.ExchangeRate;

public interface ExchangerateAggregate {

	double factor(final ExchangeRate exchangeRate, final Date date);

}