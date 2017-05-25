package de.mq.portfolio.shareportfolio.support;

import java.sql.Date;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;


import de.mq.portfolio.share.Data;
import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.shareportfolio.SharePortfolio;
@Ignore
public class RealtimeCoursesAOTest {
	
	private static final String ID = UUID.randomUUID().toString();
	private static final String REGEX_FILTER = ".*";
	private static final double EXCHANGE_RATE_02 =  0.95D;
	private static final double EXCHANGE_RATE_01 =1d ;
	
	private static final double WEIGHT_02 = 0.6D ;
	private static final double WEIGHT_01 = 0.4D ;
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
		
		final Map<TimeCourse,Double> weights = new HashMap<>();
		weights.put(timeCourse01, WEIGHT_01);
		weights.put(timeCourse02, WEIGHT_02);
		Mockito.when(sharePortfolio.min()).thenReturn(weights);
		
		final Map<String,Double> exchangeRates = new HashMap<>();
		exchangeRates.put(CODE_01, EXCHANGE_RATE_01);
		exchangeRates.put(CODE_02, EXCHANGE_RATE_02);
		
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
	//	realtimeCoursesAO.setExchangeRates(exchangeRates);
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
		
		Assert.assertEquals(RATE_01_START*EXCHANGE_RATE_01*WEIGHT_01, sapMap.get(RealtimeCoursesAO.LAST_COLUMN));
		Assert.assertEquals(RATE_01_END*EXCHANGE_RATE_01*WEIGHT_01, sapMap.get(RealtimeCoursesAO.CURRENT_COLUMN));
		Assert.assertEquals(truncate(EXCHANGE_RATE_01*WEIGHT_01*(RATE_01_END-RATE_01_START)), truncate((Double) sapMap.get(RealtimeCoursesAO.DELTA_COLUMN)));
		Assert.assertEquals(truncate(100*(RATE_01_END-RATE_01_START)/RATE_01_START), truncate((Double)sapMap.get(RealtimeCoursesAO.DELTA_PERCENT_COLUMN)));
		Assert.assertEquals(String.format("%s (%s)", NAME_01, CODE_01), sapMap.get(RealtimeCoursesAO.NAME_COLUMN));
		Assert.assertEquals(lastDate, sapMap.get(RealtimeCoursesAO.LAST_DATE_COLUMN));
		Assert.assertEquals(WEIGHT_01, sapMap.get(RealtimeCoursesAO.WEIGHT_COLUMN));
		final Map<String,Object> coMap = realtimeCourses.get(2);
	
		Assert.assertEquals(RATE_02_START*EXCHANGE_RATE_02*WEIGHT_02, coMap.get(RealtimeCoursesAO.LAST_COLUMN));
		Assert.assertEquals(truncate(RATE_02_END*EXCHANGE_RATE_02*WEIGHT_02), truncate((Double) coMap.get(RealtimeCoursesAO.CURRENT_COLUMN)));
		Assert.assertEquals(truncate(EXCHANGE_RATE_02*WEIGHT_02*(RATE_02_END-RATE_02_START)), truncate((Double) coMap.get(RealtimeCoursesAO.DELTA_COLUMN)));
		Assert.assertEquals(truncate(100*(RATE_02_END-RATE_02_START)/RATE_02_START), truncate((Double)coMap.get(RealtimeCoursesAO.DELTA_PERCENT_COLUMN)));
		Assert.assertEquals(String.format("%s (%s)", NAME_02, CODE_02), coMap.get(RealtimeCoursesAO.NAME_COLUMN));
		
		Assert.assertEquals(WEIGHT_02, coMap.get(RealtimeCoursesAO.WEIGHT_COLUMN));
		Assert.assertEquals(lastDate, coMap.get(RealtimeCoursesAO.LAST_DATE_COLUMN));
		
		final Map<String,Object> portfolioMap = realtimeCourses.get(0);
		
		Assert.assertEquals(RATE_01_START*EXCHANGE_RATE_01*WEIGHT_01+ RATE_02_START*EXCHANGE_RATE_02*WEIGHT_02 , portfolioMap.get(RealtimeCoursesAO.LAST_COLUMN));
		Assert.assertEquals(RATE_01_END*EXCHANGE_RATE_01*WEIGHT_01 + RATE_02_END*EXCHANGE_RATE_02*WEIGHT_02, portfolioMap.get(RealtimeCoursesAO.CURRENT_COLUMN));
		
		Assert.assertEquals(RATE_01_END*EXCHANGE_RATE_01*WEIGHT_01 + RATE_02_END*EXCHANGE_RATE_02*WEIGHT_02  -(RATE_01_START*EXCHANGE_RATE_01*WEIGHT_01 + RATE_02_START*EXCHANGE_RATE_02*WEIGHT_02),  portfolioMap.get(RealtimeCoursesAO.DELTA_COLUMN));
		
		Assert.assertEquals(100*(RATE_01_END*EXCHANGE_RATE_01*WEIGHT_01 + RATE_02_END*EXCHANGE_RATE_02*WEIGHT_02  -(RATE_01_START*EXCHANGE_RATE_01*WEIGHT_01 + RATE_02_START*EXCHANGE_RATE_02*WEIGHT_02))/(RATE_01_START*EXCHANGE_RATE_01*WEIGHT_01 + RATE_02_START*EXCHANGE_RATE_02*WEIGHT_02),  portfolioMap.get(RealtimeCoursesAO.DELTA_PERCENT_COLUMN));
		
		Assert.assertEquals(PORTFOLIO_NAME, portfolioMap.get(RealtimeCoursesAO.NAME_COLUMN));
		Assert.assertFalse(portfolioMap.containsKey(RealtimeCoursesAO.LAST_DATE_COLUMN));
		
		
	}
	
	private Double truncate(double value) {
		return Math.round(1e6* value) / 1e6;
	}
	
	@Test
	public final void currencyColumn() {
		Assert.assertEquals(RealtimeCoursesAO.CURRENCY_COLUMN, realtimeCoursesAO.getCurrencyColumn());
	}
	
	@Test
	public final void nameColumn() {
		Assert.assertEquals(RealtimeCoursesAO.NAME_COLUMN, realtimeCoursesAO.getNameColumn());
	}
	
	@Test
	public final void lastColumn() {
		Assert.assertEquals(RealtimeCoursesAO.LAST_COLUMN, realtimeCoursesAO.getLastColumn());
	}
	
	@Test
	public final void lastDateColumn() {
		Assert.assertEquals(RealtimeCoursesAO.LAST_DATE_COLUMN, realtimeCoursesAO.getLastDateColumn());
	}
	
	@Test
	public final void currentColumn() {
		Assert.assertEquals(RealtimeCoursesAO.CURRENT_COLUMN, realtimeCoursesAO.getCurrentColumn());
	}
	
	@Test
	public final void deltaColumn() {
		Assert.assertEquals(RealtimeCoursesAO.DELTA_COLUMN, realtimeCoursesAO.getDeltaColumn());
	}
	
	@Test
	public final void weightColumn() {
		Assert.assertEquals(RealtimeCoursesAO.WEIGHT_COLUMN, realtimeCoursesAO.getWeightColumn());
	}
	
	@Test
	public final void deltaPercentColumn() {
		Assert.assertEquals(RealtimeCoursesAO.DELTA_PERCENT_COLUMN, realtimeCoursesAO.getDeltaPercentColumn());
	}
	
	@Test
	public final void  getFilter() {
		Assert.assertNull(realtimeCoursesAO.getFilter());
		realtimeCoursesAO.setFilter(REGEX_FILTER);
		
		Assert.assertEquals(REGEX_FILTER, realtimeCoursesAO.getFilter());
	}
	
	@Test
	public final void portfolioId() {
		Assert.assertNull(realtimeCoursesAO.getPortfolioId());
		realtimeCoursesAO.setPortfolioId(ID);
		
		Assert.assertEquals(ID, realtimeCoursesAO.getPortfolioId());
	}

	
	@Test
	public final void lastStoredTimeCourse() {
		Assert.assertTrue(realtimeCoursesAO.getLastStoredTimeCourse());
		realtimeCoursesAO.setLastStoredTimeCourse(Boolean.FALSE);
		
		Assert.assertFalse(realtimeCoursesAO.getLastStoredTimeCourse());
	}
}
