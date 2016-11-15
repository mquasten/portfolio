package de.mq.portfolio.exchangerate.support;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import java.util.Map;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;
import org.springframework.core.convert.converter.Converter;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.share.Data;
import de.mq.portfolio.shareportfolio.SharePortfolio;
import de.mq.portfolio.shareportfolio.support.SharePortfolioRetrospective;
import de.mq.portfolio.shareportfolio.support.SharePortfolioService;
import junit.framework.Assert;

public class ExchangeRateControllerTest {
	
	
	private static final double INITIAL_VALUE = 47.11D;
	private static final String PORTFOLIO_NAME = "PortfolioName";
	private  final ExchangeRate exchangeRate = Mockito.mock(ExchangeRate.class);
	private static final String PORTFOLIO_ID = "19680528";
	private final ExchangeRateService exchangeRateService = Mockito.mock(ExchangeRateService.class);
	private final SharePortfolioService sharePortfolioService = Mockito.mock(SharePortfolioService.class);
	@SuppressWarnings("unchecked")
	private final Converter<String, String> currencyConverter = Mockito.mock(Converter.class);
	private final ExchangeRatesAO exchangeRatesAO = Mockito.mock(ExchangeRatesAO.class);
	private ExchangeRateController exchangeRateController = Mockito.mock(ExchangeRateController.class, Mockito.CALLS_REAL_METHODS);
	private Collection<ExchangeRate> exchangeRates = Arrays.asList(exchangeRate); 

	private final SharePortfolio sharePortfolio = Mockito.mock(SharePortfolio.class);
	
	private final LineChartModel chartModel = Mockito.mock(LineChartModel.class);
	
	
	ExchangeRateRetrospectiveBuilder exchangeRateRetrospectiveBuilder = Mockito.mock(ExchangeRateRetrospectiveBuilder.class);
	
	final SharePortfolioRetrospective sharePortfolioRetrospective = Mockito.mock(SharePortfolioRetrospective.class);
	
	Data initialRateWithExchangeRate = Mockito.mock(Data.class); 
	
	ExchangeRateRetrospective exchangeRateRetrospective = Mockito.mock(ExchangeRateRetrospective.class);
	
	Date initialDate = Date.from(LocalDateTime.now().minusDays(10).truncatedTo(ChronoUnit.DAYS).atZone(ZoneId.systemDefault()).toInstant());
	
	@SuppressWarnings("rawtypes")
	private final ArgumentCaptor<Collection> entriesCaptor = ArgumentCaptor.forClass(Collection.class);
	
	@Before
	public final void setup() {
		
		Mockito.when(currencyConverter.convert("EUR")).thenReturn("€");
		Mockito.when(currencyConverter.convert("USD")).thenReturn("US$");
		
		Mockito.when(exchangeRate.source()).thenReturn("EUR");
		Mockito.when(exchangeRate.target()).thenReturn("USD");
		
		Mockito.when(exchangeRate.rates()).thenReturn(Arrays.asList(initialRateWithExchangeRate));
		
		
		Mockito.when(exchangeRateController.exchangeRateRetrospectiveBuilder()).thenReturn(exchangeRateRetrospectiveBuilder);
		Mockito.when(exchangeRateRetrospectiveBuilder.withExchangeRates(Arrays.asList(initialRateWithExchangeRate))).thenReturn(exchangeRateRetrospectiveBuilder);
		
		Mockito.when(exchangeRateRetrospectiveBuilder.withName("€-US$")).thenReturn(exchangeRateRetrospectiveBuilder);
		
		Mockito.when(exchangeRateRetrospectiveBuilder.withStartDate(initialDate)).thenReturn(exchangeRateRetrospectiveBuilder);
		Mockito.when(exchangeRateRetrospectiveBuilder.build()).thenReturn(exchangeRateRetrospective);
		
		
		Mockito.when(initialRateWithExchangeRate.date()).thenReturn(initialDate);
		
		Mockito.when(sharePortfolioRetrospective.initialRateWithExchangeRate()).thenReturn(initialRateWithExchangeRate);
		Mockito.when(sharePortfolio.exchangeRateTranslations()).thenReturn(exchangeRates);
		Mockito.when(sharePortfolioRetrospective.committedSharePortfolio()).thenReturn(sharePortfolio);
		
		
		Mockito.when(sharePortfolio.name()).thenReturn(PORTFOLIO_NAME);
		
		Mockito.when(exchangeRateService.exchangeRates(exchangeRates)).thenReturn(exchangeRates);
		
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
	
	    Mockito.when(initialRateWithExchangeRate.value()).thenReturn(INITIAL_VALUE);
	   Mockito.when(exchangeRateRetrospective.exchangeRates()).thenReturn(Arrays.asList(initialRateWithExchangeRate));
	}

	@SuppressWarnings("unchecked")
	@Test
	public final void init() {
		exchangeRateController.init();
		
		Mockito.verify(exchangeRatesAO).setExchangeRateRetrospectives(Arrays.asList(exchangeRateRetrospective));
		Mockito.verify(exchangeRateService, Mockito.never()).exchangeRates();
		
		Mockito.verify(sharePortfolioService).retrospective(PORTFOLIO_ID);
		Mockito.verify(exchangeRatesAO).setPortfolioName(PORTFOLIO_NAME);
		Mockito.verify(exchangeRatesAO).assign(entriesCaptor.capture());
		
		Assert.assertEquals(1, entriesCaptor.getValue().size());
		
		final Entry<String,LineChartSeries> result = (Entry<String, LineChartSeries>) entriesCaptor.getValue().stream().findFirst().get();
		
		Assert.assertEquals("EUR-USD", result.getKey());
		Assert.assertEquals(1, result.getValue().getData().entrySet().size());
		
		Assert.assertEquals(initialDate.getTime(), result.getValue().getData().entrySet().stream().findAny().get().getKey());
		
		Assert.assertEquals(INITIAL_VALUE, result.getValue().getData().entrySet().stream().findAny().get().getValue());
	}
	
	
	@SuppressWarnings("unchecked")
	@Test
	public final void initWithoutPortfolio() {
		Mockito.when(exchangeRatesAO.getPortfolioId()).thenReturn(null);
		Date date = Date.from(LocalDateTime.now().minusDays(exchangeRatesAO.period()).truncatedTo(ChronoUnit.DAYS).atZone(ZoneId.systemDefault()).toInstant());
		Mockito.when(exchangeRateRetrospectiveBuilder.withStartDate(date)).thenReturn(exchangeRateRetrospectiveBuilder);
		Mockito.when(exchangeRateService.exchangeRates()).thenReturn(Arrays.asList(exchangeRate));
		exchangeRateController.init();
		
		Mockito.verify(exchangeRatesAO).setExchangeRateRetrospectives(Arrays.asList(exchangeRateRetrospective));
		Mockito.verify(exchangeRateService).exchangeRates();
		Mockito.verify(exchangeRateService, Mockito.never()).exchangeRates(Mockito.any());
		
		Mockito.verify(sharePortfolioService, Mockito.never()).retrospective(PORTFOLIO_ID);
		Mockito.verify(exchangeRatesAO, Mockito.never()).setPortfolioName(PORTFOLIO_NAME);
		Mockito.verify(exchangeRatesAO).assign(entriesCaptor.capture());
		
		Assert.assertEquals(1, entriesCaptor.getValue().size());
		
		final Entry<String,LineChartSeries> result = (Entry<String, LineChartSeries>) entriesCaptor.getValue().stream().findFirst().get();
		
		Assert.assertEquals("EUR-USD", result.getKey());
		Assert.assertEquals(1, result.getValue().getData().entrySet().size());
		
		Assert.assertEquals(initialDate.getTime(), result.getValue().getData().entrySet().stream().findAny().get().getKey());
		
		Assert.assertEquals(INITIAL_VALUE, result.getValue().getData().entrySet().stream().findAny().get().getValue());
	}
}
