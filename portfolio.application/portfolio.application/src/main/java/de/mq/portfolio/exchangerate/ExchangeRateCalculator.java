package de.mq.portfolio.exchangerate;

import java.util.Date;

import de.mq.portfolio.exchangerate.ExchangeRate;

@FunctionalInterface
public interface ExchangeRateCalculator {
	double factor(final ExchangeRate exchangeRate, final Date date);
}