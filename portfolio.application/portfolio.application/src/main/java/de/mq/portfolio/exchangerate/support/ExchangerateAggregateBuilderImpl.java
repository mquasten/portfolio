package de.mq.portfolio.exchangerate.support;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import de.mq.portfolio.exchangerate.ExchangeRate;

class ExchangerateAggregateBuilderImpl implements ExchangerateAggregateBuilder {

	private final Map<ExchangeRate,Map<Date,Double>>  exchangeRates = new HashMap<>();
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.exchangerate.support.ExchangerateAggregateBuilder#withExchangeRate(de.mq.portfolio.exchangerate.ExchangeRate)
	 */
	@Override
	public final ExchangerateAggregateBuilder withExchangeRate(final ExchangeRate exchangeRate) {
		Assert.notNull(exchangeRate, "ExchangeRate is mandatory.");
		if( CollectionUtils.isEmpty(exchangeRate.rates())) {
			return this;
		}		
		Assert.isTrue(!exchangeRates.containsKey(exchangeRate), String.format("ExchangeRates already assigned for %s", exchangeRate));
		final Map<Date,Double> results = new HashMap<>();
		exchangeRate.rates().forEach(rate -> results.put(rate.date(), rate.value()));
		exchangeRates.put(exchangeRate, results);
		return this;
	}
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.exchangerate.support.ExchangerateAggregateBuilder#build()
	 */
	@Override
	public final ExchangerateAggregate build() {
		Assert.isTrue(! CollectionUtils.isEmpty(exchangeRates), "At least 1 ExchangeRate with Data should be aware.");
		return  new ExchangerateAggregateImpl(exchangeRates);
	}
	
}
