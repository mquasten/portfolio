package de.mq.portfolio.exchangerate.support;

import java.util.Collection;
import java.util.Date;

import de.mq.portfolio.share.Data;

public interface ExchangeRateRetrospectiveBuilder {

	ExchangeRateRetrospectiveBuilder withName(final String name);

	ExchangeRateRetrospectiveBuilder withStartDate(final Date startDate);

	ExchangeRateRetrospectiveBuilder withExchangeRates(final Collection<Data> exchangeRates);
	
	ExchangeRateRetrospectiveBuilder withTarget(final String target);

	ExchangeRateRetrospective build();

}