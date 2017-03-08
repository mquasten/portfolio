package de.mq.portfolio.exchangerate.support;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.exchangerate.ExchangeRateCalculator;
import org.junit.Assert;

public class ExchangeRateCalculatorTest {
	
	private static final String CURRENCY_USD = "USD";

	private static final String CURRENCY_EUR = "EUR";

	private final Map<ExchangeRate, Map<Date, Double>> exchangeRates = new HashMap<>();
	
	private  ExchangeRateCalculator exchangeRateCalculator ;
	
	private ExchangeRate exchangeRate = new ExchangeRateImpl(CURRENCY_EUR, CURRENCY_USD);
	private final Date date =  Mockito.mock(Date.class);
	private final Double factor =  1.25D;
	
	@Before
	public final void setup() {
		final Map<Date,Double> rates = new HashMap<>();
		rates.put(date, factor);
		exchangeRates.put(exchangeRate, rates);
	  exchangeRateCalculator=new SimpleExchangeRateCalculatorImpl(exchangeRates);
	}
	
	@Test
	public final void  factor() {
		Assert.assertEquals((Double) factor,(Double) exchangeRateCalculator.factor(new ExchangeRateImpl(CURRENCY_EUR, CURRENCY_USD), date));
	}
	
	@Test
	public final void  factorInverse() {
		Assert.assertEquals((Double) (1d/factor),(Double) exchangeRateCalculator.factor(new ExchangeRateImpl(CURRENCY_USD,CURRENCY_EUR), date));
	}
	
	@Test
	public final void  factorSameCurrency() {
		Assert.assertEquals((Double) 1d, (Double) exchangeRateCalculator.factor(new ExchangeRateImpl(CURRENCY_EUR, CURRENCY_EUR), date));
		Assert.assertEquals((Double) 1d, (Double) exchangeRateCalculator.factor(new ExchangeRateImpl(CURRENCY_USD, CURRENCY_USD), date));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public final void  factorUnkown() {
		exchangeRateCalculator.factor(new ExchangeRateImpl("xxx", CURRENCY_EUR),date);
	}

}
