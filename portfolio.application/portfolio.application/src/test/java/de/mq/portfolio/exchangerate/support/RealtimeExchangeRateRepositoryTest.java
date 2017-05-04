package de.mq.portfolio.exchangerate.support;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
	
	
	private static final String CURRENCY_FORMAT = "%s%s=X";
	private static Double RATE_EUR_USD = 1.0901d;
	private static Double RATE_USD_EUR= 0.9171d;
	
	
	
	private static String CURRENCY_EUR = "EUR";
	private static String CURRENCY_USD = "USD";
	
	private static String DATE = "5/28/1";
	
	private static String TIME = "11:00am";
	
	private static final String DATA = String.format("\"%s%s=X\",%s,\"%s\",\"%s\"\n\"%s%s=X\",%s,\"%s\",\"%s\"\nxxxXXX", CURRENCY_USD, CURRENCY_EUR, RATE_USD_EUR,DATE, TIME, CURRENCY_EUR, CURRENCY_USD, RATE_EUR_USD, DATE, TIME );

	private final  AbstractRealtimeExchangeRateRepository realtimeExchangeRateRepository = Mockito.mock(AbstractRealtimeExchangeRateRepository.class, Mockito.CALLS_REAL_METHODS);

	private final RestOperations restOperations = Mockito.mock(RestOperations.class);
	private ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
	
	
	
	@SuppressWarnings("unchecked")
	private ArgumentCaptor<Class<String>> classCaptor = (ArgumentCaptor<Class<String>>) ArgumentCaptor.forClass( (Class<?>) Class.class);
	
	private ArgumentCaptor<String> paramCaptor = ArgumentCaptor.forClass(String.class);
	
	@Before
	public final void setup() {
		Mockito.when(restOperations.getForObject(urlCaptor.capture(), (Class<String>) classCaptor.capture(), paramCaptor.capture())).thenReturn( DATA);
		Mockito.doReturn(new   ExceptionTranslationBuilderImpl<>()).when(realtimeExchangeRateRepository).exceptionTranslationBuilder();
		Arrays.asList(AbstractRealtimeExchangeRateRepository.class.getDeclaredFields()).stream().filter(field -> field.getType().equals(RestOperations.class)).forEach(field -> ReflectionTestUtils.setField(realtimeExchangeRateRepository, field.getName(), restOperations));
	}
	
	@Test
	public final void exchangeRates() throws ParseException {
		
		final List<ExchangeRate> results = new ArrayList<>(realtimeExchangeRateRepository.exchangeRates(Arrays.asList(new ExchangeRateImpl("USD", "EUR"), new ExchangeRateImpl("EUR", "USD"))));
		
		final Date date = AbstractRealtimeExchangeRateRepository.DATE_FORMAT.parse(DATE + " " + TIME);
		Assert.assertEquals(2, results.size());
		
		Assert.assertEquals(CURRENCY_USD, results.get(0).source());
		Assert.assertEquals(CURRENCY_EUR, results.get(0).target());
		Assert.assertEquals(1, results.get(0).rates().size());
		Assert.assertEquals(RATE_USD_EUR, (Double)results.get(0).rates().stream().findAny().get().value());
		Assert.assertEquals(date, results.get(0).rates().stream().findAny().get().date());
		
		Assert.assertEquals(CURRENCY_EUR, results.get(1).source());
		Assert.assertEquals(CURRENCY_USD, results.get(1).target());
		Assert.assertEquals(1, results.get(1).rates().size());
		Assert.assertEquals(RATE_EUR_USD, (Double)results.get(1).rates().stream().findAny().get().value());
		Assert.assertEquals(date, results.get(1).rates().stream().findAny().get().date());
		
		
		Assert.assertEquals(String.class, classCaptor.getValue());
		final List<String> currencies =  Arrays.asList(paramCaptor.getValue().split("[,]")).stream().map(value -> value.trim()).collect(Collectors.toList());
		Assert.assertEquals(2, currencies.size());
		Assert.assertEquals(String.format(CURRENCY_FORMAT,CURRENCY_USD, CURRENCY_EUR), currencies.get(0));
		Assert.assertEquals(String.format(CURRENCY_FORMAT,CURRENCY_EUR, CURRENCY_USD), currencies.get(1));
		
	}
	

}
