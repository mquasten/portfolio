package de.mq.portfolio.exchangerate.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import org.primefaces.model.chart.LineChartModel;
import org.springframework.core.convert.converter.Converter;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.share.Data;
import de.mq.portfolio.shareportfolio.SharePortfolio;
import de.mq.portfolio.shareportfolio.support.SharePortfolioRetrospective;
import de.mq.portfolio.shareportfolio.support.SharePortfolioService;

public class ExchangeRateControllerTest {
	
	
	private static final String PORTFOLIO_ID = "19680528";
	private final ExchangeRateService exchangeRateService = Mockito.mock(ExchangeRateService.class);
	private final SharePortfolioService sharePortfolioService = Mockito.mock(SharePortfolioService.class);
	@SuppressWarnings("unchecked")
	private final Converter<String, String> currencyConverter = Mockito.mock(Converter.class);
	private final ExchangeRatesAO exchangeRatesAO = Mockito.mock(ExchangeRatesAO.class);
	private ExchangeRateController exchangeRateController = Mockito.mock(ExchangeRateController.class, Mockito.CALLS_REAL_METHODS);
	private Collection<ExchangeRate> exchangeRates = Arrays.asList(Mockito.mock(ExchangeRate.class)); 

	private final SharePortfolio sharePortfolio = Mockito.mock(SharePortfolio.class);
	
	private final LineChartModel chartModel = Mockito.mock(LineChartModel.class);
	
	
	final SharePortfolioRetrospective sharePortfolioRetrospective = Mockito.mock(SharePortfolioRetrospective.class);
	
	Data initialRateWithExchangeRate = Mockito.mock(Data.class); 
	
	@Before
	public final void setup() {
		Mockito.when(sharePortfolioRetrospective.initialRateWithExchangeRate()).thenReturn(initialRateWithExchangeRate);
		Mockito.when(sharePortfolio.exchangeRateTranslations()).thenReturn(exchangeRates);
		Mockito.when(sharePortfolioRetrospective.committedSharePortfolio()).thenReturn(sharePortfolio);
		
		Mockito.when(sharePortfolioService.retrospective(PORTFOLIO_ID)).thenReturn(sharePortfolioRetrospective);
		
		final Map<Class<?>, Object > mocks = new HashMap<>();
		mocks.put(ExchangeRateService.class, exchangeRateService);
		mocks.put(SharePortfolioService.class, sharePortfolioService);
		mocks.put(Converter.class, currencyConverter);
		
		
		Arrays.asList(ExchangeRateController.class.getDeclaredFields()).stream().filter(field -> mocks.containsKey(field.getType())).forEach(field -> ReflectionTestUtils.setField(exchangeRateController, field.getName(), mocks.get(field.getType())));
	
		Mockito.when(exchangeRatesAO.period()).thenReturn(365);
	    Mockito.when(exchangeRateController.exchangeRatesAO()).thenReturn(exchangeRatesAO);
	  
	    Mockito.when(exchangeRatesAO.getPortfolioId()).thenReturn(PORTFOLIO_ID);
	    
	    Arrays.asList(ExchangeRatesAO.class.getDeclaredFields()).stream().filter(field -> field.getType().equals(LineChartModel.class)).forEach(field ->  ReflectionTestUtils.setField(exchangeRatesAO, field.getName(), chartModel));
	    Arrays.asList(ExchangeRatesAO.class.getDeclaredFields()).stream().filter(field -> field.getType().equals(Collection.class)).forEach(field ->  ReflectionTestUtils.setField(exchangeRatesAO, field.getName(), new ArrayList<>()));
	}

	@Test
	public final void init() {
		exchangeRateController.init();
	}
}
