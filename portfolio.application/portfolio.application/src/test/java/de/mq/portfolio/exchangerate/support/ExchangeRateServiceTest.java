package de.mq.portfolio.exchangerate.support;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ReflectionUtils;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.exchangerate.ExchangeRateCalculator;
import de.mq.portfolio.gateway.ExchangeRateGatewayParameterService;
import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameterAggregation;
import de.mq.portfolio.share.Data;

 
public class ExchangeRateServiceTest {

	
	
	private  final ExchangeRateDatebaseRepository exchangeRateDatebaseRepository = Mockito.mock(ExchangeRateDatebaseRepository.class); 
	private final  ExchangeRateRepository exchangeRateRepository = Mockito.mock(ExchangeRateRepository.class);  ;
	private  final ExchangeRateService exchangeRateService = Mockito.mock(AbstractExchangeRateService.class , Mockito.CALLS_REAL_METHODS );
	private final  ExchangeRateCalculatorBuilder builder = Mockito.mock(ExchangeRateCalculatorBuilder.class);
	private final ExchangeRate exchangeRate = Mockito.mock(ExchangeRate.class);
	private final Data rows = Mockito.mock(Data.class);
	private final Collection<Data> rates = Arrays.asList(rows);
	private final Collection<ExchangeRate> exchangeRates = Arrays.asList(exchangeRate);
	private final ExchangeRateCalculator  exchangeRateCalculator = Mockito.mock(ExchangeRateCalculator.class);
	
	private final RealtimeExchangeRateRepository realtimeExchangeRateRepository = Mockito.mock(RealtimeExchangeRateRepository.class);
	
	private final Map<Class<?> ,Object> dependencies = new HashMap<>();
	
	ExchangeRateGatewayParameterService exchangeRateGatewayParameterService = Mockito.mock(ExchangeRateGatewayParameterService.class);
	
	@SuppressWarnings("unchecked")
	private GatewayParameterAggregation<ExchangeRate> gatewayParameterAggregation = Mockito.mock(GatewayParameterAggregation.class);
	
	@Before
	public final void setup() throws NoSuchMethodException, SecurityException {
		
		Mockito.when(builder.withExchangeRates(exchangeRates)).thenReturn(builder);
		Mockito.when(builder.build()).thenReturn(exchangeRateCalculator);
		
		dependencies.clear();
		dependencies.put(ExchangeRateRepository.class, exchangeRateRepository);
		dependencies.put(ExchangeRateDatebaseRepository.class, exchangeRateDatebaseRepository);
		dependencies.put(RealtimeExchangeRateRepository.class, realtimeExchangeRateRepository);
		dependencies.put(ExchangeRateGatewayParameterService.class, exchangeRateGatewayParameterService);
		ReflectionUtils.doWithFields(exchangeRateService.getClass(), field -> ReflectionTestUtils.setField(exchangeRateService, field.getName(), dependencies.get(field.getType())), field -> dependencies.containsKey(field.getType()));
		
		Mockito.doReturn(builder).when(((AbstractExchangeRateService)exchangeRateService)).newBuilder();
	}
	
	@Test
	public final void exchangeRate() {
		Mockito.when(exchangeRateGatewayParameterService.aggregationForRequiredGateway(exchangeRate, Gateway.CentralBankExchangeRates)).thenReturn(gatewayParameterAggregation);
		Mockito.when(exchangeRateRepository.supports()).thenReturn(Gateway.CentralBankExchangeRates);
		Mockito.when(exchangeRateRepository.history(gatewayParameterAggregation)).thenReturn(rates);
		
		Assert.assertEquals(exchangeRate, exchangeRateService.exchangeRate(exchangeRate));
		Mockito.verify(exchangeRate, Mockito.times(1)).assign(rates);
	}
	
	@Test
	public final void save() {
		exchangeRateService.save(exchangeRate);
		Mockito.verify(exchangeRateDatebaseRepository, Mockito.times(1)).save(exchangeRate);
	}
	
	@Test
	public final void exchangeRateCalculator() {
		Mockito.when(exchangeRateDatebaseRepository.exchangerates()).thenReturn(exchangeRates);
		
		Assert.assertEquals(exchangeRateCalculator, exchangeRateService.exchangeRateCalculator());
		
		Mockito.verify(builder).build();
		Mockito.verify(builder).withExchangeRates(exchangeRates);
		Mockito.verify(exchangeRateDatebaseRepository).exchangerates(); 
		
	}
	
	@Test
	public final void exchangeRateCalculatorAsArgument() {
		Mockito.when(exchangeRateDatebaseRepository.exchangerates(exchangeRates)).thenReturn(exchangeRates);
		
		Assert.assertEquals(exchangeRateCalculator, exchangeRateService.exchangeRateCalculator(exchangeRates));
		
		Mockito.verify(builder).build();
		Mockito.verify(builder).withExchangeRates(exchangeRates);
		Mockito.verify(exchangeRateDatebaseRepository).exchangerates(exchangeRates);
		
	}
	
	@Test
	public final void constructorInjection() throws NoSuchMethodException, SecurityException {
		final ExchangeRateService service  = BeanUtils.instantiateClass( exchangeRateService.getClass().getDeclaredConstructor(ExchangeRateDatebaseRepository.class, ExchangeRateRepository.class, RealtimeExchangeRateRepository.class, ExchangeRateGatewayParameterService.class),exchangeRateDatebaseRepository , exchangeRateRepository, realtimeExchangeRateRepository, exchangeRateGatewayParameterService);
		final Map<Class<?> ,Object> results = new HashMap<>();
		ReflectionUtils.doWithFields(service.getClass(), field -> results.put(field.getType(), ReflectionTestUtils.getField(service, field.getName())), field -> dependencies.containsKey(field.getType()));
	    Assert.assertEquals(4, results.size());
	    Assert.assertEquals(exchangeRateRepository, results.get(ExchangeRateRepository.class));
	    Assert.assertEquals(exchangeRateDatebaseRepository, results.get(ExchangeRateDatebaseRepository.class));
	    Assert.assertEquals(realtimeExchangeRateRepository, results.get(RealtimeExchangeRateRepository.class));
	    Assert.assertEquals(exchangeRateGatewayParameterService, results.get(ExchangeRateGatewayParameterService.class));
	}
	
	@Test
	public final void exchangeRates() {
		Mockito.when(exchangeRateDatebaseRepository.exchangerates()).thenReturn(exchangeRates);
		
		Assert.assertEquals(exchangeRates, exchangeRateService.exchangeRates());
		
		Mockito.verify(exchangeRateDatebaseRepository).exchangerates();
	}
	
	
	@Test
	public final void exchangeRates2() {
		Mockito.when(exchangeRateDatebaseRepository.exchangerates(exchangeRates)).thenReturn(exchangeRates);
		Assert.assertEquals(exchangeRates, exchangeRateService.exchangeRates(exchangeRates));
		Mockito.verify(exchangeRateDatebaseRepository).exchangerates(exchangeRates);
	}
	
	@Test
	public final void realTimeExchangeRates() {
		
		final Collection<ExchangeRate> expectedExchangeRates = Arrays.asList(Mockito.mock(ExchangeRate.class));
		@SuppressWarnings("unchecked")
		final GatewayParameterAggregation<Collection<ExchangeRate>> gatewayParameterAggregation = Mockito.mock(GatewayParameterAggregation.class);
		Mockito.doReturn(expectedExchangeRates).when(realtimeExchangeRateRepository).exchangeRates(gatewayParameterAggregation);
		Mockito.when(realtimeExchangeRateRepository.supports(Arrays.asList(exchangeRate))).thenReturn(Gateway.YahooRealtimeExchangeRates);
	
		Mockito.when(exchangeRateGatewayParameterService.merge(Arrays.asList(exchangeRate), Gateway.YahooRealtimeExchangeRates)).thenReturn(gatewayParameterAggregation);
		
		Assert.assertEquals(expectedExchangeRates, exchangeRateService.realTimeExchangeRates(Arrays.asList(exchangeRate)));
		Mockito.verify(realtimeExchangeRateRepository).exchangeRates(gatewayParameterAggregation);
		Mockito.verify(realtimeExchangeRateRepository).supports(Arrays.asList(exchangeRate));
		Mockito.verify(exchangeRateGatewayParameterService).merge(Arrays.asList(exchangeRate), Gateway.YahooRealtimeExchangeRates);
		
	}
}
