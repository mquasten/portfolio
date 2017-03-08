package de.mq.portfolio.exchangerate.support;

import java.sql.Date;

import java.util.Arrays;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.exchangerate.ExchangeRateCalculator;
import de.mq.portfolio.share.Data;
import org.junit.Assert;

public class ExchangeRateCalculatorBuilderTest {
	
	private ExchangeRateCalculatorBuilder builder = new ExchangeRateCalculatorBuilderImpl();
	private final ExchangeRate exchangeRate = Mockito.mock(ExchangeRate.class);
	private Data data01 = Mockito.mock(Data.class);
	private Date date01 = Mockito.mock(Date.class);
	private Date date02 = Mockito.mock(Date.class);
	private Data data02 = Mockito.mock(Data.class);
	private Double value01 = 1d;
	private Double value02 = 2d;

	@Before
	public final void setup() {
		Mockito.when(data01.date()).thenReturn(date01);
		Mockito.when(data02.date()).thenReturn(date02);
		Mockito.when(data01.value()).thenReturn(value01);
		Mockito.when(data02.value()).thenReturn(value02);
		Mockito.when(exchangeRate.rates()).thenReturn(Arrays.asList(data01, data02));
	}
	
	@Test
	public final void withExchangeRate() {
		Assert.assertEquals(builder, builder.withExchangeRate(exchangeRate));
		checkExchangeRates(exchangeRates(builder));
	}

	private void checkExchangeRates(final Map<ExchangeRate, Map<Date,Double>> results) {
	
	    Assert.assertEquals(1, results.size());
	    Assert.assertTrue(results.keySet().stream().findAny().isPresent());
	    Assert.assertEquals(exchangeRate, results.keySet().stream().findAny().get());
	    Assert.assertTrue(results.values().stream().findAny().isPresent());
	    final Map<Date,Double> rates = results.values().stream().findAny().get();
	    Assert.assertEquals(2, rates.size());
	    Assert.assertEquals(value01, rates.get(date01));
	    Assert.assertEquals(value02, rates.get(date02));
	}
	
	@Test
	public final void withExchangeRateEmpty() {
		builder.withExchangeRate(Mockito.mock(ExchangeRate.class));
		Assert.assertEquals(0, exchangeRates(builder).size());
	}

	@SuppressWarnings("unchecked")
	private Map<ExchangeRate, Map<Date, Double>> exchangeRates(Object target) {
		return (Map<ExchangeRate, Map<Date, Double>>) Arrays.asList(target.getClass().getDeclaredFields()).stream().filter(field -> field.getType().equals(Map.class)).map(field -> ReflectionTestUtils.getField(target, field.getName()) ).findFirst().get();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public final void withExchangeRateAlreadyAssigned() {
		IntStream.rangeClosed(0, 1).forEach(i -> builder.withExchangeRate(exchangeRate));
	}
	
	@Test
	public final void withExchangeRates() {
		Assert.assertEquals(builder, builder.withExchangeRates(Arrays.asList(exchangeRate)));
		checkExchangeRates(exchangeRates(builder));
	}
	
	@Test
	public final void build() {
		final Map<ExchangeRate, Map<Date,Double>> exchangeRatesMap = exchangeRates(builder);
		final Map<Date,Double> rates = new HashMap<>();
		rates.put(date01, value01);
		rates.put(date02, value02);
		exchangeRatesMap.put(exchangeRate, rates);
		final ExchangeRateCalculator result =  builder.build();
		Assert.assertEquals(exchangeRatesMap, exchangeRates(result));
		
	}

}
