package de.mq.portfolio.exchangerate.support;

import java.util.Collection;
import java.util.Date;

import de.mq.portfolio.share.Data;

public interface ExchangeRateRetrospective {

	Date startDate();

	Date endDate();

	Double startValue();

	Double endValue();

	String name();

	Double rate();

	Collection<Data> exchangeRates();

	String target();

}