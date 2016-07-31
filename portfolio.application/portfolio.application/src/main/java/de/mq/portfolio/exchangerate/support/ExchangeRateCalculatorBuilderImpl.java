package de.mq.portfolio.exchangerate.support;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.exchangerate.ExchangeRateCalculator;

@Component
@Scope("prototype")
class ExchangeRateCalculatorBuilderImpl implements ExchangeRateCalculatorBuilder {
	private final Map<ExchangeRate,Map<Date,Double>>  exchangeRates = new HashMap<>();
	


	/* (non-Javadoc)
	 * @see de.mq.portfolio.exchangerate.support.ExchangeRateCalculator#withExchangeRate(de.mq.portfolio.exchangerate.ExchangeRate)
	 */
	@Override
	public   ExchangeRateCalculatorBuilder withExchangeRate(final ExchangeRate exchangeRate) {
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
	 * @see de.mq.portfolio.exchangerate.support.ExchangeRateCalculator#withExchangeRates(java.util.Collection)
	 */
	@Override
	public ExchangeRateCalculatorBuilder withExchangeRates(final Collection<ExchangeRate> exchangeRates) {
		//Assert.isTrue( !CollectionUtils.isEmpty(exchangeRates), "At least one ExchangeRate should be given.");
		exchangeRates.forEach(er -> withExchangeRate(er));
		return this;
	}
	
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.exchangerate.support.ExchangeRateCalculator#build()
	 */
	@Override
	public ExchangeRateCalculator build() {
		return new SimpleExchangeRateCalculatorImpl(exchangeRates);
	}
}
