package de.mq.portfolio.exchangerate.support;

import de.mq.portfolio.exchangerate.ExchangeRate;

interface ExchangerateAggregateBuilder {

	ExchangerateAggregateBuilder withExchangeRate(final ExchangeRate exchangeRate);

	ExchangerateAggregate build();

}