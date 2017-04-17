package de.mq.portfolio.shareportfolio.support;

import java.sql.Date;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


import de.mq.portfolio.share.Data;
import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.shareportfolio.SharePortfolio;


public class RealtimeCoursesAOTest {
	
	private static final double FACTOR_02 = 0.6D * 0.95D;
	private static final double FACTOR_01 = 0.4D;
	private static final String CURRENCY_USD = "USD";
	private static final String NAME_02 = "Coca Cola";
	private static final String NAME_01 = "SAP AG";
	private static final double RATE_02_END = 43D;
	private static final double RATE_02_START = 42D;
	private static final double RATE_01_END = 91.5d;
	private static final double RATE_01_START = 90d;
	private static final String CODE_01 = "SAP.DE";
	private static final String CODE_02 = "KO";
	private static final String PORTFOLIO_CURRENCY = "EUR";
	private static final String PORTFOLIO_NAME = "min-risk portfolio";
	private  final RealtimeCoursesAO  realtimeCoursesAO = new RealtimeCoursesAO();
	private  final SharePortfolio sharePortfolio = Mockito.mock(SharePortfolio.class);
	
	private final TimeCourse timeCourse01 = Mockito.mock(TimeCourse.class);
	private final TimeCourse timeCourse02 = Mockito.mock(TimeCourse.class);
	private final Share share01 = Mockito.mock(Share.class);
	private final Share share02 = Mockito.mock(Share.class);
	
	private Data data01Start = Mockito.mock(Data.class);
	private Data data01End = Mockito.mock(Data.class);
	private Data data02Start = Mockito.mock(Data.class);
	private Data data02End = Mockito.mock(Data.class);
	
	private Date lastDate = Mockito.mock(Date.class);
	private final List<Entry<TimeCourse,List<Data>>> entries = new ArrayList<>();
	
	@Before
	public final void setup() {
		
		final Map<String,Double> factors = new HashMap<>();
		factors.put(CODE_01, FACTOR_01);
		factors.put(CODE_02, FACTOR_02);
		
		Mockito.when(share01.currency()).thenReturn(PORTFOLIO_CURRENCY);
		Mockito.when(share02.currency()).thenReturn(CURRENCY_USD);
		
		Mockito.when(data01Start.value()).thenReturn(RATE_01_START);
		Mockito.when(data01Start.date()).thenReturn(lastDate);
		Mockito.when(data01End.value()).thenReturn(RATE_01_END);
		
		Mockito.when(data02Start.value()).thenReturn(RATE_02_START);
		Mockito.when(data02Start.date()).thenReturn(lastDate);
		Mockito.when(data02End.value()).thenReturn(RATE_02_END);
		
		Mockito.when(timeCourse01.share()).thenReturn(share01);
		Mockito.when(timeCourse02.share()).thenReturn(share02);
		Mockito.when(timeCourse01.code()).thenReturn(CODE_01);
		Mockito.when(timeCourse01.name()).thenReturn(NAME_01);
		Mockito.when(timeCourse02.code()).thenReturn(CODE_02);
		Mockito.when(timeCourse02.name()).thenReturn(NAME_02);
		Mockito.when(sharePortfolio.name()).thenReturn(PORTFOLIO_NAME);
		Mockito.when(sharePortfolio.currency()).thenReturn(PORTFOLIO_CURRENCY);
		realtimeCoursesAO.setFactors(factors);
		realtimeCoursesAO.assign(sharePortfolio);
		
		entries.add(new AbstractMap.SimpleImmutableEntry<>(timeCourse01, Arrays.asList(data01Start, data01End)));
		entries.add(new AbstractMap.SimpleImmutableEntry<>(timeCourse02, Arrays.asList(data02Start, data02End)));
		realtimeCoursesAO.assign(entries);
	}
	
	@Test
	public final void portfolioName() {
		Assert.assertEquals(PORTFOLIO_NAME, realtimeCoursesAO.getPortfolioName());
	}
	
	@Test
	public final void  portfolioCurrency() {
		Assert.assertEquals(PORTFOLIO_CURRENCY, realtimeCoursesAO.getPortfolioCurrency());
	}

	
	@Test
	public final void  shares() {
		final List<Map<String, Object>>  shares =  new ArrayList<>(realtimeCoursesAO.getShares());
		Assert.assertEquals(2, shares.size());
		final Map<String,Object> sapMap = shares.get(0);
		
		Assert.assertEquals(RATE_01_START, sapMap.get(RealtimeCoursesAO.LAST_COLUMN));
		Assert.assertEquals(RATE_01_END, sapMap.get(RealtimeCoursesAO.CURRENT_COLUMN));
		Assert.assertEquals((RATE_01_END-RATE_01_START), sapMap.get(RealtimeCoursesAO.DELTA_COLUMN));
		Assert.assertEquals(100*(RATE_01_END-RATE_01_START)/RATE_01_START, sapMap.get(RealtimeCoursesAO.DELTA_PERCENT_COLUMN));
		Assert.assertEquals(PORTFOLIO_CURRENCY, sapMap.get(RealtimeCoursesAO.CURRENCY_COLUMN));
		Assert.assertEquals(String.format("%s (%s)", NAME_01, CODE_01), sapMap.get(RealtimeCoursesAO.NAME_COLUMN));
		
		final Map<String,Object> koMap = shares.get(1);
		Assert.assertEquals(RATE_02_START, koMap.get(RealtimeCoursesAO.LAST_COLUMN));
		Assert.assertEquals(RATE_02_END, koMap.get(RealtimeCoursesAO.CURRENT_COLUMN));
		Assert.assertEquals((RATE_02_END-RATE_02_START), koMap.get(RealtimeCoursesAO.DELTA_COLUMN));
		Assert.assertEquals(100*(RATE_02_END-RATE_02_START)/RATE_02_START, koMap.get(RealtimeCoursesAO.DELTA_PERCENT_COLUMN));
		Assert.assertEquals(CURRENCY_USD, koMap.get(RealtimeCoursesAO.CURRENCY_COLUMN));
		Assert.assertEquals(String.format("%s (%s)", NAME_02, CODE_02), koMap.get(RealtimeCoursesAO.NAME_COLUMN));
		
	}
	
	@Test
	public final void  realtimeCourses() {
		final List<Map<String, Object>>  realtimeCourses =  new ArrayList<>(realtimeCoursesAO.getRealtimeCourses());
		Assert.assertEquals(3, realtimeCourses.size());
		final Map<String,Object> sapMap = realtimeCourses.get(1);
		
		Assert.assertEquals(RATE_01_START*FACTOR_01, sapMap.get(RealtimeCoursesAO.LAST_COLUMN));
		Assert.assertEquals(RATE_01_END*FACTOR_01, sapMap.get(RealtimeCoursesAO.CURRENT_COLUMN));
		Assert.assertEquals(percentRound(FACTOR_01*(RATE_01_END-RATE_01_START)), percentRound((Double) sapMap.get(RealtimeCoursesAO.DELTA_COLUMN)));
		Assert.assertEquals(percentRound(100*(RATE_01_END-RATE_01_START)/RATE_01_START), percentRound((Double)sapMap.get(RealtimeCoursesAO.DELTA_PERCENT_COLUMN)));
		Assert.assertEquals(String.format("%s (%s)", NAME_01, CODE_01), sapMap.get(RealtimeCoursesAO.NAME_COLUMN));
		Assert.assertEquals(lastDate, sapMap.get(RealtimeCoursesAO.LAST_DATE_COLUMN));
		
		final Map<String,Object> coMap = realtimeCourses.get(2);
		
		Assert.assertEquals(RATE_02_START*FACTOR_02, coMap.get(RealtimeCoursesAO.LAST_COLUMN));
		Assert.assertEquals(RATE_02_END*FACTOR_02, coMap.get(RealtimeCoursesAO.CURRENT_COLUMN));
		Assert.assertEquals(percentRound(FACTOR_02*(RATE_02_END-RATE_02_START)), percentRound((Double) coMap.get(RealtimeCoursesAO.DELTA_COLUMN)));
		Assert.assertEquals(percentRound(100*(RATE_02_END-RATE_02_START)/RATE_02_START), percentRound((Double)coMap.get(RealtimeCoursesAO.DELTA_PERCENT_COLUMN)));
		Assert.assertEquals(String.format("%s (%s)", NAME_02, CODE_02), coMap.get(RealtimeCoursesAO.NAME_COLUMN));
		Assert.assertEquals(lastDate, coMap.get(RealtimeCoursesAO.LAST_DATE_COLUMN));
		
		final Map<String,Object> portfolioMap = realtimeCourses.get(0);
		
		Assert.assertEquals(RATE_01_START*FACTOR_01 + RATE_02_START*FACTOR_02 , portfolioMap.get(RealtimeCoursesAO.LAST_COLUMN));
		Assert.assertEquals(RATE_01_END*FACTOR_01 + RATE_02_END*FACTOR_02, portfolioMap.get(RealtimeCoursesAO.CURRENT_COLUMN));
		
		Assert.assertEquals(RATE_01_END*FACTOR_01 + RATE_02_END*FACTOR_02  -(RATE_01_START*FACTOR_01 + RATE_02_START*FACTOR_02),  portfolioMap.get(RealtimeCoursesAO.DELTA_COLUMN));
		
		Assert.assertEquals(100*(RATE_01_END*FACTOR_01 + RATE_02_END*FACTOR_02  -(RATE_01_START*FACTOR_01 + RATE_02_START*FACTOR_02))/(RATE_01_START*FACTOR_01 + RATE_02_START*FACTOR_02),  portfolioMap.get(RealtimeCoursesAO.DELTA_PERCENT_COLUMN));
		
		Assert.assertEquals(PORTFOLIO_NAME, portfolioMap.get(RealtimeCoursesAO.NAME_COLUMN));
		Assert.assertFalse(portfolioMap.containsKey(RealtimeCoursesAO.LAST_DATE_COLUMN));
		
		
	}
	
	private Double percentRound(double value) {
		return Math.round(10000 * value) / 100d;
	}

}
