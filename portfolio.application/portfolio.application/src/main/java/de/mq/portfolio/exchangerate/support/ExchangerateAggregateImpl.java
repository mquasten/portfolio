package de.mq.portfolio.exchangerate.support;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.Assert;

import de.mq.portfolio.exchangerate.ExchangeRate;

class ExchangerateAggregateImpl implements ExchangerateAggregate {
	 private final Map<ExchangeRate,Map<Date,Double>>  exchangeRates = new HashMap<>();
	 
	 ExchangerateAggregateImpl(final Map<ExchangeRate,Map<Date,Double>>  exchangeRates ) {
		 this.exchangeRates.clear();
		 this.exchangeRates.putAll(exchangeRates);
	 }
	 
	 /* (non-Javadoc)
	 * @see de.mq.portfolio.exchangerate.support.ExchangerateAggregate#factor(de.mq.portfolio.exchangerate.ExchangeRate, java.util.Date, double)
	 */
	@Override
	public double factor(final ExchangeRate exchangeRate, final Date date) {
		 if( exchangeRate.source().equals(exchangeRate.target())){
			 return 1d;
		 }
		 if( exchangeRates.containsKey(exchangeRate)){
			 Assert.isTrue(exchangeRates.get(exchangeRate).containsKey(date), String.format("Exchangerate not found %s for date %s.", exchangeRate , date));
			 return  BigDecimal.ONE.divide(BigDecimal.valueOf(exchangeRates.get(exchangeRate).get(date)),MathContext.DECIMAL64).doubleValue();
		 }
		 
		 if( exchangeRates.containsKey(new ExchangeRateImpl(exchangeRate.target(), exchangeRate.source()))){
			 Assert.isTrue(exchangeRates.get(new ExchangeRateImpl(exchangeRate.target(), exchangeRate.source())).containsKey(date), String.format("Exchangerate (inverse) not found %s for date %s.", exchangeRate , date));
			 return exchangeRates.get(new ExchangeRateImpl(exchangeRate.target(), exchangeRate.source())).get(date).doubleValue();
		 }
		
		 throw new IllegalArgumentException(String.format("ExchangeRate not found %s." , exchangeRate));
		 
	 }
}
