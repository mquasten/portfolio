package de.mq.portfolio.exchangerate.support;

import java.util.Collection;

import de.mq.portfolio.share.Data;

interface ExchangeRateRepository {

	Collection<Data> history(final String url);

}