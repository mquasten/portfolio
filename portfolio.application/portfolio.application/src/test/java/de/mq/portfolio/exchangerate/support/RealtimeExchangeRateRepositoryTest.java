package de.mq.portfolio.exchangerate.support;

import java.lang.reflect.Modifier;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.gateway.GatewayParameterAggregation;
import de.mq.portfolio.gateway.support.GatewayHistoryRepository;
import de.mq.portfolio.support.ExceptionTranslationBuilderImpl;

public class RealtimeExchangeRateRepositoryTest {
	
	

	final static String EXCHANGERATES_DATEFORMAT = "M/d/yy h:mma";

	private static Double RATE_EUR_USD = 1.0901d;
	private static Double RATE_USD_EUR = 0.9171d;

	private static String CURRENCY_EUR = "EUR";
	private static String CURRENCY_USD = "USD";

	private static String DATE = "5/28/1";

	private static String TIME = "11:00am";

	private static final String DATA = String.format("\"%s%s=X\",%s,\"%s\",\"%s\"\n\"%s%s=X\",%s,\"%s\",\"%s\"\nxxxXXX", CURRENCY_USD, CURRENCY_EUR, RATE_USD_EUR, DATE, TIME, CURRENCY_EUR, CURRENCY_USD, RATE_EUR_USD, DATE, TIME);

	private final AbstractRealtimeExchangeRateRepository realtimeExchangeRateRepository = Mockito.mock(AbstractRealtimeExchangeRateRepository.class, Mockito.CALLS_REAL_METHODS);


	private final Map<String, Object> dependencies = new HashMap<>();

	private final GatewayParameter gatewayParameter = Mockito.mock(GatewayParameter.class);

	@SuppressWarnings("unchecked")
	private final GatewayParameterAggregation<Collection<ExchangeRate>> gatewayParameterAggregation = Mockito.mock(GatewayParameterAggregation.class);

	private GatewayHistoryRepository gatewayHistoryRepository = Mockito.mock(GatewayHistoryRepository.class);
	
	@Before
	public final void setup() {
		Mockito.when(gatewayParameterAggregation.gatewayParameter(Gateway.ApiLayerRealtimeExchangeRates)).thenReturn(gatewayParameter);
		dependencies.put("dateFormat", new SimpleDateFormat(EXCHANGERATES_DATEFORMAT));
		dependencies.put("gatewayHistoryRepository", gatewayHistoryRepository);
		Mockito.when(gatewayHistoryRepository.historyAsString(gatewayParameter)).thenReturn(DATA);
		Mockito.doReturn(new DefaultConversionService()).when(realtimeExchangeRateRepository).configurableConversionService();
		Mockito.doAnswer(a -> new ExceptionTranslationBuilderImpl<>()).when(realtimeExchangeRateRepository).exceptionTranslationBuilder();
		Arrays.asList(AbstractRealtimeExchangeRateRepository.class.getDeclaredFields()).stream().filter(field -> !Modifier.isStatic(field.getModifiers()) && dependencies.containsKey(field.getName()))
				.forEach(field -> ReflectionTestUtils.setField(realtimeExchangeRateRepository, field.getName(), dependencies.get(field.getName())));
	}

	@Test
	public final void exchangeRates() throws ParseException {
		final List<ExchangeRate> results = new ArrayList<>(realtimeExchangeRateRepository.exchangeRates(gatewayParameterAggregation));
		final Date date = new SimpleDateFormat(EXCHANGERATES_DATEFORMAT).parse(DATE + " " + TIME);

		Assert.assertEquals(2, results.size());

		Assert.assertEquals(CURRENCY_USD, results.get(0).source());
		Assert.assertEquals(CURRENCY_EUR, results.get(0).target());
		Assert.assertEquals(1, results.get(0).rates().size());
		Assert.assertEquals(RATE_USD_EUR, (Double) results.get(0).rates().stream().findAny().get().value());
		Assert.assertEquals(date, results.get(0).rates().stream().findAny().get().date());

		Assert.assertEquals(CURRENCY_EUR, results.get(1).source());
		Assert.assertEquals(CURRENCY_USD, results.get(1).target());
		Assert.assertEquals(1, results.get(1).rates().size());
		Assert.assertEquals(RATE_EUR_USD, (Double) results.get(1).rates().stream().findAny().get().value());
		Assert.assertEquals(date, results.get(1).rates().stream().findAny().get().date());

	}

	@Test(expected = IllegalArgumentException.class)
	public final void exchangeRatesWrongCurrencies() {
		prepareAndExecuteWithWrongLine(newWrongLine("xxxx", String.valueOf(RATE_USD_EUR), TIME));
	}

	private void prepareAndExecuteWithWrongLine(final String wrongLine) {
		
		Mockito.when(gatewayHistoryRepository.historyAsString(gatewayParameter)).thenReturn(wrongLine);
		realtimeExchangeRateRepository.exchangeRates(gatewayParameterAggregation);
	}

	private String newWrongLine(final String sourceCurrency, final String rate, final String time) {
		return String.format("\"%s%s=X\",%s,\"%s\",\"%s\"", sourceCurrency, CURRENCY_EUR, rate, DATE, time);
	}

	@Test(expected = ConversionFailedException.class)
	public final void exchangeRatesWrongRate() {
		prepareAndExecuteWithWrongLine(newWrongLine("USD", "x", TIME));
	}

	@Test(expected = ConversionFailedException.class)
	public final void exchangeRatesWrongTime() {
		prepareAndExecuteWithWrongLine(newWrongLine("USD", String.valueOf(RATE_USD_EUR), "x"));
	}

	@Test
	public final void create() throws BeanInstantiationException, NoSuchMethodException, SecurityException {
		final RealtimeExchangeRateRepository newRealtimeExchangeRateRepository = BeanUtils.instantiateClass(realtimeExchangeRateRepository.getClass().getDeclaredConstructor(GatewayHistoryRepository.class, String.class), gatewayHistoryRepository, EXCHANGERATES_DATEFORMAT);
		final Map<String, Object> results = Arrays.asList(AbstractRealtimeExchangeRateRepository.class.getDeclaredFields()).stream().filter(field -> !Modifier.isStatic(field.getModifiers()) && dependencies.containsKey(field.getName()))
				.collect(Collectors.toMap(field -> field.getName(), field -> ReflectionTestUtils.getField(newRealtimeExchangeRateRepository, field.getName())));

		Assert.assertEquals(dependencies, results);
	}
	@Test
	public final void supports(){
		Assert.assertEquals(Gateway.ApiLayerRealtimeExchangeRates, realtimeExchangeRateRepository.supports(Arrays.asList(Mockito.any(ExchangeRate.class))));
	}

}
