package de.mq.portfolio.gateway.support;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.gateway.GatewayParameterAggregation;


public class ExchangeRateGatewayParameterServiceTest {
	
	private static final String CURRENCY_USD = "USD";
	private static final String CURRENCY_EUR = "EUR";
	private final AbstractExchangeRateGatewayParameterService  exchangeRateGatewayParameterService = Mockito.mock(AbstractExchangeRateGatewayParameterService.class, Mockito.CALLS_REAL_METHODS);
	private final GatewayParameterRepository gatewayParameterRepository=Mockito.mock(GatewayParameterRepository.class);
    private final GatewayHistoryRepository gatewayHistoryRepository=Mockito.mock(GatewayHistoryRepository.class);
	private final Map<Class<?>, Object> dependencies = new HashMap<>();
	private GatewayParameter gatewayParameter = Mockito.mock(GatewayParameter.class);
	private final  ExchangeRate exchangeRate = Mockito.mock(ExchangeRate.class);
	
	@SuppressWarnings("unchecked")
	private final GatewayParameterAggregationBuilder<ExchangeRate> gatewayParameterAggregationBuilder = Mockito.mock(GatewayParameterAggregationBuilder.class);
	@SuppressWarnings("unchecked")
	private GatewayParameterAggregation<ExchangeRate> gatewayParameterAggregation = Mockito.mock(GatewayParameterAggregation.class);
	
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
	
	}
	
	@Test
	public final void aggregationForRequiredGateway(){
	
		final GatewayParameterAggregation<ExchangeRate> result = exchangeRateGatewayParameterService.aggregationForRequiredGateway(exchangeRate, Gateway.CentralBankExchangeRates);
		
		Assert.assertEquals(gatewayParameterAggregation,result);	
		Mockito.verify(gatewayParameterAggregationBuilder).withDomain(exchangeRate);
		Mockito.verify(gatewayParameterAggregationBuilder).withGatewayParameter(gatewayParameter);
	
	}
	
}
