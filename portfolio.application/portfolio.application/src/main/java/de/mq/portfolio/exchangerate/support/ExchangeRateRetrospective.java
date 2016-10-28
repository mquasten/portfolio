package de.mq.portfolio.exchangerate.support;

import java.util.Date;

public interface ExchangeRateRetrospective {

	Date startDate();

	Date endDate();

	Double startValue();

	Double endValue();

	String name();

	Double rate();

}