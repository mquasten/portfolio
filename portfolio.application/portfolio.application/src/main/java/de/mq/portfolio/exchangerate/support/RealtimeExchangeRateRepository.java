package de.mq.portfolio.exchangerate.support;

import java.util.Collection;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.share.Data;

public interface RealtimeExchangeRateRepository {

	Collection<Data> exchangeRates(Collection<ExchangeRate> rates);

}
