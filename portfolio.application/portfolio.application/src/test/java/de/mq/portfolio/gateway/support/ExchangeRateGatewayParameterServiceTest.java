package de.mq.portfolio.gateway.support;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpEntity;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.gateway.GatewayParameterAggregation;

public class ExchangeRateGatewayParameterServiceTest {

	private static final String CONTENT = "They call me the wild rose ...";
	private static final String CURRENCY_USD = "USD";
	private static final String CURRENCY_EUR = "EUR";
	private final AbstractExchangeRateGatewayParameterService exchangeRateGatewayParameterService = Mockito.mock(AbstractExchangeRateGatewayParameterService.class, Mockito.CALLS_REAL_METHODS);
	private final GatewayParameterRepository gatewayParameterRepository = Mockito.mock(GatewayParameterRepository.class);
	private final GatewayHistoryRepository gatewayHistoryRepository = Mockito.mock(GatewayHistoryRepository.class);
	private final Map<Class<?>, Object> dependencies = new HashMap<>();
	private GatewayParameter gatewayParameter = Mockito.mock(GatewayParameter.class);
	private final ExchangeRate exchangeRate = Mockito.mock(ExchangeRate.class);

	@SuppressWarnings("unchecked")
	private final GatewayParameterAggregationBuilder<ExchangeRate> gatewayParameterAggregationBuilder = Mockito.mock(GatewayParameterAggregationBuilder.class);
	@SuppressWarnings("unchecked")
	private GatewayParameterAggregation<ExchangeRate> gatewayParameterAggregation = Mockito.mock(GatewayParameterAggregation.class);

	@SuppressWarnings("unchecked")
	private final HttpEntity<String> httpEntity = Mockito.mock(HttpEntity.class);

	@Before
	public final void setup() {
		dependencies.put(GatewayParameterRepository.class, gatewayParameterRepository);
		dependencies.put(GatewayHistoryRepository.class, gatewayHistoryRepository);
		Arrays.asList(AbstractExchangeRateGatewayParameterService.class.getDeclaredFields()).stream().filter(field -> dependencies.containsKey(field.getType())).forEach(field -> ReflectionTestUtils.setField(exchangeRateGatewayParameterService, field.getName(), dependencies.get(field.getType())));

		Mockito.when(exchangeRate.source()).thenReturn(CURRENCY_EUR);
		Mockito.when(exchangeRate.target()).thenReturn(CURRENCY_USD);
		Mockito.doAnswer(answer -> gatewayParameterAggregationBuilder).when(exchangeRateGatewayParameterService).gatewayParameterAggregationBuilder();
		Mockito.when(gatewayParameter.gateway()).thenReturn(Gateway.CentralBankExchangeRates);
		Mockito.when(gatewayParameterAggregationBuilder.withDomain(Mockito.any())).thenReturn(gatewayParameterAggregationBuilder);
		Mockito.when(gatewayParameterAggregationBuilder.withGatewayParameter(Mockito.any())).thenReturn(gatewayParameterAggregationBuilder);
		Mockito.when(gatewayParameterAggregationBuilder.withGatewayParameters(Mockito.any())).thenReturn(gatewayParameterAggregationBuilder);
		Mockito.when(gatewayParameterAggregationBuilder.build()).thenReturn(gatewayParameterAggregation);
		Mockito.when(gatewayParameterRepository.gatewayParameter(Gateway.CentralBankExchangeRates, CURRENCY_EUR, CURRENCY_USD)).thenReturn(gatewayParameter);

		Mockito.when(gatewayHistoryRepository.history(gatewayParameter)).thenReturn(httpEntity);
		Mockito.when(httpEntity.getBody()).thenReturn(CONTENT);
	}

	@Test
	public final void aggregationForRequiredGateway() {

		final GatewayParameterAggregation<ExchangeRate> result = exchangeRateGatewayParameterService.aggregationForRequiredGateway(exchangeRate, Gateway.CentralBankExchangeRates);

		Assert.assertEquals(gatewayParameterAggregation, result);
		Mockito.verify(gatewayParameterAggregationBuilder).withDomain(exchangeRate);
		Mockito.verify(gatewayParameterAggregationBuilder).withGatewayParameter(gatewayParameter);

	}

	@Test(expected = IllegalArgumentException.class)
	public final void aggregationForRequiredGatewayWrongGroup() {

		exchangeRateGatewayParameterService.aggregationForRequiredGateway(exchangeRate, Gateway.GoogleRateHistory);
	}

	@Test
	public final void aggregationForAllGateways() {
		final GatewayParameter otherGatewayParameter = Mockito.mock(GatewayParameter.class);
		Mockito.when(otherGatewayParameter.gateway()).thenReturn(Gateway.CentralBankExchangeRates);
		final Collection<GatewayParameter> gatewayParameters = Arrays.asList(otherGatewayParameter, otherGatewayParameter);
		Mockito.when(gatewayParameterRepository.gatewayParameters(CURRENCY_EUR, CURRENCY_USD)).thenReturn(gatewayParameters);

		Assert.assertEquals(gatewayParameterAggregation, exchangeRateGatewayParameterService.aggregationForAllGateways(exchangeRate));

		Mockito.verify(gatewayParameterAggregationBuilder).withDomain(exchangeRate);
		Mockito.verify(gatewayParameterAggregationBuilder).withGatewayParameters(gatewayParameters);
	}

	@Test
	public final void history() {
		Assert.assertEquals(CONTENT, exchangeRateGatewayParameterService.history(gatewayParameter));
	}

	@Test
	public final void dependencies() throws Exception {
		final Constructor<? extends AbstractExchangeRateGatewayParameterService> constructor = exchangeRateGatewayParameterService.getClass().getDeclaredConstructor(GatewayParameterRepository.class, GatewayHistoryRepository.class);
		final AbstractExchangeRateGatewayParameterService service = BeanUtils.instantiateClass(constructor, gatewayParameterRepository, gatewayHistoryRepository);

		final Map<Class<?>, Object> injected = Arrays.asList(AbstractExchangeRateGatewayParameterService.class.getDeclaredFields()).stream().filter(field -> dependencies.containsKey(field.getType()))
				.collect(Collectors.toMap(Field::getType, field -> ReflectionTestUtils.getField(service, field.getName())));
		Assert.assertEquals(dependencies, injected);
	}
	
	@Test
	public final void aggregateBuilder() {
	   final Collection<Method> methods = Arrays.asList(AbstractExchangeRateGatewayParameterService.class.getDeclaredMethods()).stream().filter(method -> method.getParameterTypes().length==0&& method.getReturnType().equals(GatewayParameterAggregationBuilder.class)).collect(Collectors.toSet());	
	   Assert.assertEquals(3, methods.size());
	   
	   methods.forEach(method -> Assert.assertEquals(gatewayParameterAggregationBuilder, ReflectionTestUtils.invokeMethod(exchangeRateGatewayParameterService, method.getName())));
	}

}
