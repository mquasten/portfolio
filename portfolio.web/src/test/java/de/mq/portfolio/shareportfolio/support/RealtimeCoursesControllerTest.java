package de.mq.portfolio.shareportfolio.support;


import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.exchangerate.ExchangeRateCalculator;
import de.mq.portfolio.exchangerate.support.ExchangeRateService;
import de.mq.portfolio.share.Data;
import de.mq.portfolio.share.ShareService;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.shareportfolio.SharePortfolio;

public class RealtimeCoursesControllerTest {
	
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
	
		
		Mockito.when(realtimeCourse01.code()).thenReturn("SAP.DE");
		Mockito.when(realtimeCourse02.code()).thenReturn("KO");
		
		Mockito.when(realtimeCourse01.rates()).thenReturn(Arrays.asList(Mockito.mock(Data.class), Mockito.mock(Data.class)));
		Mockito.when(realtimeCourse02.rates()).thenReturn(Arrays.asList(Mockito.mock(Data.class), Mockito.mock(Data.class)));
		
		Mockito.when(sharePortfolio.exchangeRate(timeCourse01)).thenReturn(exchangeRate01);
		Mockito.when(sharePortfolio.exchangeRate(timeCourse02)).thenReturn(exchangeRate02);
		
		Mockito.when(timeCourse01.code()).thenReturn("SAP.DE");
		Mockito.when(timeCourse02.code()).thenReturn("KO");
		
		Mockito.when(exchangeRateCalculator.factor(exchangeRate01, endDate01)).thenReturn(1d);
		
		Mockito.when(exchangeRateCalculator.factor(exchangeRate02, endDate02)).thenReturn(0.9d);
		
		Mockito.when(currentTimeCourse01.end()).thenReturn(endDate01);
		
		Mockito.when(currentTimeCourse02.end()).thenReturn(endDate02);
		
		Mockito.when(shareService.timeCourse("SAP.DE")).thenReturn(Optional.of(currentTimeCourse01));
		Mockito.when(shareService.timeCourse("KO")).thenReturn(Optional.of(currentTimeCourse02));
		
		Mockito.when(realtimeCoursesAO.getPortfolioId()).thenReturn(ID);
		Mockito.when(sharePortfolioService.sharePortfolio(ID)).thenReturn(sharePortfolio);
		
		Mockito.when(sharePortfolio.exchangeRateTranslations()).thenReturn(Arrays.asList(exchangeRate01,  exchangeRate02));
		
		Mockito.when(exchangeRateService.exchangeRateCalculator(sharePortfolio.exchangeRateTranslations())).thenReturn(exchangeRateCalculator);
		
		final Map<TimeCourse, Double> weights = new HashMap<>();
		weights.put(timeCourse01, 0.4);
		weights.put(timeCourse02, 0.6);
		
		Mockito.when(sharePortfolio.min()).thenReturn(weights);
		
		Mockito.when(sharePortfolio.timeCourses()).thenReturn(Arrays.asList(timeCourse01, timeCourse02));
		
		Mockito.when(realtimeCoursesAO.getLastStoredTimeCourse()).thenReturn(Boolean.TRUE);
		Mockito.when(shareService.realTimeCourses(Arrays.asList("SAP.DE", "KO"), realtimeCoursesAO.getLastStoredTimeCourse())).thenReturn(Arrays.asList(realtimeCourse01, realtimeCourse02));
		
	}
	
	@Test
	public final void init() {
		realtimeCoursesController.init(realtimeCoursesAO);
	}

}
