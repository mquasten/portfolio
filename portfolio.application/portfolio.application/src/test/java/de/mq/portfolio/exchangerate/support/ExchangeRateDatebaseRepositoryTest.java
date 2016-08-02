package de.mq.portfolio.exchangerate.support;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;

import de.mq.portfolio.exchangerate.ExchangeRate;

public class ExchangeRateDatebaseRepositoryTest {
	
	private static final String CURRENCY_USD = "USD";

	private static final String CURRENCY_EUR = "EUR";

	private final MongoOperations mongoOperations = Mockito.mock(MongoOperations.class);
	
	private final ExchangeRateDatebaseRepository exchangeRateDatebaseRepository = new ExchangeRateDatebaseRepositoryImpl(mongoOperations);
	
	private ExchangeRate exchangeRate = Mockito.mock(ExchangeRate.class);
	private ExchangeRate inverseExchangeRate = Mockito.mock(ExchangeRate.class);
	
	private final ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
	@SuppressWarnings("unchecked")
	private final ArgumentCaptor<Class<ExchangeRate>> classCaptor = (ArgumentCaptor<Class<ExchangeRate>>) ArgumentCaptor.forClass((Class<?>) Class.class);
	
	@Test
	public final void save() {
		exchangeRateDatebaseRepository.save(exchangeRate);
		Mockito.verify(mongoOperations).save(exchangeRate);
	}
	
	
	@Test
	public final void exchangerates() {
		Mockito.when(exchangeRate.source()).thenReturn(CURRENCY_EUR);
		Mockito.when(exchangeRate.target()).thenReturn(CURRENCY_USD);
		Mockito.when(inverseExchangeRate.source()).thenReturn(CURRENCY_USD);
		Mockito.when(inverseExchangeRate.target()).thenReturn(CURRENCY_EUR);
	
	
		Mockito.doAnswer(i -> {
			final Query query = (Query) i.getArguments()[0];
			if( query.getQueryObject().get(ExchangeRateDatebaseRepositoryImpl.SOURCE_FIELD_NAME).equals(CURRENCY_EUR) && query.getQueryObject().get(ExchangeRateDatebaseRepositoryImpl.TARGET_FIELD_NAME).equals(CURRENCY_USD)) {
				return Arrays.asList(exchangeRate);
			}else if ( query.getQueryObject().get(ExchangeRateDatebaseRepositoryImpl.SOURCE_FIELD_NAME).equals(CURRENCY_USD) && query.getQueryObject().get(ExchangeRateDatebaseRepositoryImpl.TARGET_FIELD_NAME).equals(CURRENCY_EUR)) {
				return Arrays.asList(inverseExchangeRate);
			}
			Assert.fail("Wrong Query Arguments");
			return null;
		}).when(mongoOperations).find(queryCaptor.capture(), classCaptor.capture());
		
		final Collection<ExchangeRate> results = exchangeRateDatebaseRepository.exchangerates(Arrays.asList(exchangeRate));
		Assert.assertEquals(2, results.size());
		Assert.assertTrue(results.contains(exchangeRate));
		Assert.assertTrue(results.contains(inverseExchangeRate));
		
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public final void allExchangerates() {
		Mockito.when(mongoOperations.findAll((Class) ExchangeRateImpl.class)).thenReturn( Arrays.asList(exchangeRate));
		final Collection<ExchangeRate> results = exchangeRateDatebaseRepository.exchangerates();
		Assert.assertEquals(1, results.size());
		Assert.assertTrue(results.stream().findAny().isPresent());
		Assert.assertEquals(exchangeRate, results.stream().findAny().get());
	}

}
