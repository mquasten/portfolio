package de.mq.portfolio.exchangerate.support;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.Assert;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.exchangerate.ExchangeRateCalculator;

class SimpleExchangeRateCalculatorImpl implements ExchangeRateCalculator {
	
	private final Map<ExchangeRate,Map<Date,Double>>  exchangeRates=new HashMap<>();
	
	SimpleExchangeRateCalculatorImpl(final Map<ExchangeRate, Map<Date, Double>> exchangeRates) {
		this.exchangeRates .clear();
		this.exchangeRates.putAll(exchangeRates);
	}


	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.exchangerate.support.ExchangeRateCalculator#factor(de.mq.portfolio.exchangerate.ExchangeRate, java.util.Date)
	 */
	@Override
	public double factor(final ExchangeRate exchangeRate, final Date date) {
		 if( exchangeRate.source().equals(exchangeRate.target())){
			 return 1d;
		 }
		 if( exchangeRates.containsKey(exchangeRate)){
			 
			 Assert.isTrue(exchangeRates.get(exchangeRate).containsKey(date), String.format("Exchangerate not found %s for date %s.", exchangeRate , date));
			 return exchangeRates.get(exchangeRate).get(date).doubleValue();
		 }
		 
		final ExchangeRateImpl inverseExchangeRate = new ExchangeRateImpl(exchangeRate.target(), exchangeRate.source());
		if( exchangeRates.containsKey(inverseExchangeRate)){
			 Assert.isTrue(exchangeRates.get(inverseExchangeRate).containsKey(date), String.format("Exchangerate (inverse) not found %s for date %s.", exchangeRate , date));
			 
			 return  BigDecimal.ONE.divide(BigDecimal.valueOf(exchangeRates.get(inverseExchangeRate).get(date)),MathContext.DECIMAL64).doubleValue();
		 }
		
		 throw new IllegalArgumentException(String.format("ExchangeRate not found %s." , exchangeRate));
		 
	 }
}
