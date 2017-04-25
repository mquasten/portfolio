package de.mq.portfolio.shareportfolio.support;


import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.Map.Entry;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.exchangerate.ExchangeRateCalculator;
import de.mq.portfolio.exchangerate.support.ExchangeRateService;
import de.mq.portfolio.share.Data;
import de.mq.portfolio.share.ShareService;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.shareportfolio.SharePortfolio;


public class RealtimeCoursesControllerTest {
	
	private static final List<Data> RATES_01 = Arrays.asList(Mockito.mock(Data.class), Mockito.mock(Data.class));
	private static final List<Data> RATES_02 = Arrays.asList(Mockito.mock(Data.class), Mockito.mock(Data.class));
	private static final String CODE_02 = "KO";
	private static final String CODE_01 = "SAP.DE";
	private static final Double EXCHANGE_RATE_02 = 0.9d;
	private static final Double EXCHANGE_RATE_01 = 1d;
	private static final String ID = UUID.randomUUID().toString();
	private final SharePortfolioService sharePortfolioService = Mockito.mock(SharePortfolioService.class);
	private final ShareService shareService =  Mockito.mock(ShareService.class); 
	private final ExchangeRateService exchangeRateService = Mockito.mock(ExchangeRateService.class);
	
	private final RealtimeCoursesController realtimeCoursesController = new RealtimeCoursesController(sharePortfolioService, shareService, exchangeRateService);
	
	private RealtimeCoursesAO realtimeCoursesAO = Mockito.mock(RealtimeCoursesAO.class);
	private SharePortfolio sharePortfolio = Mockito.mock(SharePortfolio.class);
	
	private final ExchangeRate exchangeRate01 = Mockito.mock(ExchangeRate.class);
	private final ExchangeRate exchangeRate02 = Mockito.mock(ExchangeRate.class);
	
	private final ExchangeRateCalculator exchangeRateCalculator = Mockito.mock(ExchangeRateCalculator.class);
	
	private final TimeCourse timeCourse01 = Mockito.mock(TimeCourse.class);
	private final TimeCourse timeCourse02 = Mockito.mock(TimeCourse.class);
	
	private final TimeCourse currentTimeCourse01 = Mockito.mock(TimeCourse.class);
	
	private final TimeCourse currentTimeCourse02 = Mockito.mock(TimeCourse.class);
	
	private final Date endDate01 = Mockito.mock(Date.class);
	private final Date endDate02 = Mockito.mock(Date.class);
	
	private final TimeCourse realtimeCourse01 = Mockito.mock(TimeCourse.class);
	
	private final TimeCourse realtimeCourse02 = Mockito.mock(TimeCourse.class);
	
	
	@Before
	public final void setup() {
	
		
		Mockito.when(realtimeCourse01.code()).thenReturn(CODE_01);
		Mockito.when(realtimeCourse02.code()).thenReturn(CODE_02);
		
		Mockito.when(realtimeCourse01.rates()).thenReturn(RATES_01);
		Mockito.when(realtimeCourse02.rates()).thenReturn(RATES_02);
		
		Mockito.when(sharePortfolio.exchangeRate(timeCourse01)).thenReturn(exchangeRate01);
		Mockito.when(sharePortfolio.exchangeRate(timeCourse02)).thenReturn(exchangeRate02);
		
		Mockito.when(timeCourse01.code()).thenReturn(CODE_01);
		Mockito.when(timeCourse02.code()).thenReturn(CODE_02);
		
		Mockito.when(exchangeRateCalculator.factor(exchangeRate01, endDate01)).thenReturn(EXCHANGE_RATE_01);
		
		Mockito.when(exchangeRateCalculator.factor(exchangeRate02, endDate02)).thenReturn(EXCHANGE_RATE_02);
		
		Mockito.when(currentTimeCourse01.end()).thenReturn(endDate01);
		
		Mockito.when(currentTimeCourse02.end()).thenReturn(endDate02);
		
		Mockito.when(shareService.timeCourse(CODE_01)).thenReturn(Optional.of(currentTimeCourse01));
		Mockito.when(shareService.timeCourse(CODE_02)).thenReturn(Optional.of(currentTimeCourse02));
		
		Mockito.when(realtimeCoursesAO.getPortfolioId()).thenReturn(ID);
		Mockito.when(sharePortfolioService.sharePortfolio(ID)).thenReturn(sharePortfolio);
		
		Mockito.when(sharePortfolio.exchangeRateTranslations()).thenReturn(Arrays.asList(exchangeRate01,  exchangeRate02));
		
		Mockito.when(exchangeRateService.exchangeRateCalculator(sharePortfolio.exchangeRateTranslations())).thenReturn(exchangeRateCalculator);
		
		
		Mockito.when(sharePortfolio.timeCourses()).thenReturn(Arrays.asList(timeCourse01, timeCourse02));
		
		Mockito.when(realtimeCoursesAO.getLastStoredTimeCourse()).thenReturn(Boolean.TRUE);
		Mockito.when(shareService.realTimeCourses(Arrays.asList(CODE_01, CODE_02), realtimeCoursesAO.getLastStoredTimeCourse())).thenReturn(Arrays.asList(realtimeCourse01, realtimeCourse02));
		
	}
	
	@Test
	public final void init() {
	
		@SuppressWarnings("unchecked")
		final ArgumentCaptor<Map<String, Double>> exchangeRateCaptor = (ArgumentCaptor<Map<String, Double>>) (ArgumentCaptor<?>)ArgumentCaptor.forClass(Map.class);
		@SuppressWarnings("unchecked")
		final ArgumentCaptor<List<Entry<TimeCourse, List<Data>>>> entriesCaptor = (ArgumentCaptor<List<Entry<TimeCourse, List<Data>>>>) (ArgumentCaptor<?>)  ArgumentCaptor.forClass(List.class);
		realtimeCoursesController.init(realtimeCoursesAO);
		
		Mockito.verify(realtimeCoursesAO).assign(sharePortfolio);
		
		Mockito.verify(realtimeCoursesAO).setExchangeRates(exchangeRateCaptor.capture());		
		Assert.assertEquals(2,exchangeRateCaptor.getValue().size());
		Assert.assertTrue(exchangeRateCaptor.getValue().containsKey(CODE_01));
		Assert.assertTrue(exchangeRateCaptor.getValue().containsKey(CODE_02));
		Assert.assertEquals(  EXCHANGE_RATE_01, (Number)   exchangeRateCaptor.getValue().get(CODE_01));
		Assert.assertEquals(  EXCHANGE_RATE_02 ,   exchangeRateCaptor.getValue().get(CODE_02));
		
		Mockito.verify(realtimeCoursesAO).assign(entriesCaptor.capture());
		Assert.assertEquals(2, entriesCaptor.getValue().size());
		Assert.assertEquals(timeCourse01, entriesCaptor.getValue().get(0).getKey());
		Assert.assertEquals(timeCourse02, entriesCaptor.getValue().get(1).getKey());
		IntStream.range(0, 2).forEach(i -> Assert.assertEquals(RATES_01.get(i), entriesCaptor.getValue().get(0).getValue().get(i)));
		IntStream.range(0, 2).forEach(i -> Assert.assertEquals(RATES_02.get(i), entriesCaptor.getValue().get(1).getValue().get(i)));
	}

}
