package de.mq.portfolio.exchangerate.support;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.exchangerate.ExchangeRateCalculator;
import de.mq.portfolio.share.Data;
import junit.framework.Assert;

public class ExchangeRateServiceTest {

	
	private static final String LINK = "http://www.bundesbank.de/cae/servlet/StatisticDownload?tsId=BBEX3.D.USD.EUR.BB.AC.000&its_csvFormat=de&its_fileFormat=csv&mode=its";
	private final ExchangeRateDatebaseRepository exchangeRateDatebaseRepository = Mockito.mock(ExchangeRateDatebaseRepository.class); 
	private final ExchangeRateRepository exchangeRateRepository = Mockito.mock(ExchangeRateRepository.class);
	private final ExchangeRateService exchangeRateService = Mockito.spy(new ExchangeRateServiceImpl(exchangeRateDatebaseRepository, exchangeRateRepository));
	private final  ExchangeRateCalculatorBuilder builder = Mockito.mock(ExchangeRateCalculatorBuilder.class);
	private final ExchangeRate exchangeRate = Mockito.mock(ExchangeRate.class);
	private final Data rows = Mockito.mock(Data.class);
	private final Collection<Data> rates = Arrays.asList(rows);
	private final Collection<ExchangeRate> exchangeRates = Arrays.asList(exchangeRate);
	
	private final ExchangeRateCalculator  exchangeRateCalculator = Mockito.mock(ExchangeRateCalculator.class);
	@Before
	public final void setup() {
		Mockito.when(((ExchangeRateServiceImpl)exchangeRateService).newBuilder()).thenReturn(builder);
	}
	
	@Test
	public final void exchangeRate() {
		Mockito.when(exchangeRate.link()).thenReturn(LINK);
		
		Mockito.when(exchangeRateRepository.history(exchangeRate.link())).thenReturn(rates);
		
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
		Mockito.when(builder.withExchangeRates(exchangeRates)).thenReturn(builder);
		Mockito.when(builder.build()).thenReturn(exchangeRateCalculator);
		Assert.assertEquals(exchangeRateCalculator, exchangeRateService.exchangeRateCalculator());
		
		Mockito.verify(builder).build();
		Mockito.verify(builder).withExchangeRates(exchangeRates);
		Mockito.verify(exchangeRateDatebaseRepository).exchangerates();
		
	}
	
	@Test
	public final void exchangeRateCalculatorAsArgument() {
		Mockito.when(builder.withExchangeRates(exchangeRates)).thenReturn(builder);
		Mockito.when(builder.build()).thenReturn(exchangeRateCalculator);
		Mockito.when(exchangeRateDatebaseRepository.exchangerates(exchangeRates)).thenReturn(exchangeRates);
		Assert.assertEquals(exchangeRateCalculator, exchangeRateService.exchangeRateCalculator(exchangeRates));
		
		Mockito.verify(builder).build();
		Mockito.verify(builder).withExchangeRates(exchangeRates);
		Mockito.verify(exchangeRateDatebaseRepository).exchangerates(exchangeRates);
		
	}
	
}
