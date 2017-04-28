package de.mq.portfolio.exchangerate.support;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestOperations;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.support.ExceptionTranslationBuilderImpl;


public class RealtimeExchangeRateRepositoryTest {
	
	private static final String DATA = "\"EURUSD=X\",1.0901,\"4/28/2017\",\"6:55pm\"\n\"USDEUR=X\",0.9171,\"4/28/2017\",\"6:55pm\"\nxxxXXX";

	private final  AbstractRealtimeExchangeRateRepository realtimeExchangeRateRepository = Mockito.mock(AbstractRealtimeExchangeRateRepository.class, Mockito.CALLS_REAL_METHODS);

	private final RestOperations restOperations = Mockito.mock(RestOperations.class);
	private ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
	
	
	
	@SuppressWarnings("unchecked")
	private ArgumentCaptor<Class<String>> classCaptor = (ArgumentCaptor<Class<String>>) ArgumentCaptor.forClass( (Class<?>) Class.class);
	
	private ArgumentCaptor<Object[]> paramCaptor = ArgumentCaptor.forClass(Object[].class);
	
	@Before
	public final void setup() {
		Mockito.when(restOperations.getForObject(urlCaptor.capture(), (Class<String>) classCaptor.capture(), paramCaptor.capture())).thenReturn( DATA);
		Mockito.doReturn(new   ExceptionTranslationBuilderImpl<>()).when(realtimeExchangeRateRepository).exceptionTranslationBuilder();
		Arrays.asList(AbstractRealtimeExchangeRateRepository.class.getDeclaredFields()).stream().filter(field -> field.getType().equals(RestOperations.class)).forEach(field -> ReflectionTestUtils.setField(realtimeExchangeRateRepository, field.getName(), restOperations));
	}
	
	@Test
	public final void exchangeRates() {
		final Collection<ExchangeRate> results = realtimeExchangeRateRepository.exchangeRates(null);
		Assert.assertEquals(2, results.size());
		
	}
	

}
