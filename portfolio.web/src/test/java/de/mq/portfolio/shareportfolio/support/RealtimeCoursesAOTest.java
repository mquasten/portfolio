package de.mq.portfolio.shareportfolio.support;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class RealtimeCoursesAOTest {
	
	private static final String ID = UUID.randomUUID().toString();
	private static final String REGEX_FILTER = ".*";
	private static final double LAST_EXCHANGE_RATE_USD =  0.90D;
	private static final double CURRENT_EXCHANGE_RATE_USD =  0.95D;
	private static final double EXCHANGE_RATE_EUR =1d ;
	
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
	
	
	
	
	private Date lastDate = Mockito.mock(Date.class);
	
	private Date lastDateExchangeRateEur = Mockito.mock(Date.class);
	
	private Date currentDateExchangeRateEur  = Mockito.mock(Date.class);
	
	private Date lastDateExchangeRateUsd = Mockito.mock(Date.class);
	
	private Date currentDateExchangeRateUsd  = Mockito.mock(Date.class);
	
	private RealtimePortfolioAggregation realtimePortfolioAggregation = Mockito.mock(RealtimePortfolioAggregation.class);
	
	
	@Before
	public final void setup() {
		Mockito.doReturn(PORTFOLIO_NAME).when(realtimePortfolioAggregation).portfolioName();
		Mockito.doReturn(PORTFOLIO_CURRENCY).when(realtimePortfolioAggregation).portfolioCurrency();
		Mockito.doReturn(Arrays.asList(CODE_01, CODE_02)).when(realtimePortfolioAggregation).shareCodes();
		Mockito.doReturn(RATE_01_START).when(realtimePortfolioAggregation).lastShareRate(CODE_01);
		Mockito.doReturn(RATE_01_END).when(realtimePortfolioAggregation).shareRealtimeRate(CODE_01);
		Mockito.doReturn(RATE_01_END-RATE_01_START).when(realtimePortfolioAggregation).shareDelata(CODE_01);
		
		Mockito.doReturn(100*(RATE_01_END-RATE_01_START)/RATE_01_START).when(realtimePortfolioAggregation).shareDeltaPercent(CODE_01);
		Mockito.doReturn(PORTFOLIO_CURRENCY).when(realtimePortfolioAggregation).shareCurrency(CODE_01);
		Mockito.doReturn(NAME_01).when(realtimePortfolioAggregation).shareName(CODE_01);
		
		
		
		Mockito.doReturn(RATE_02_START).when(realtimePortfolioAggregation).lastShareRate(CODE_02);
		Mockito.doReturn(RATE_02_END).when(realtimePortfolioAggregation).shareRealtimeRate(CODE_02);
		Mockito.doReturn(RATE_02_END-RATE_02_START).when(realtimePortfolioAggregation).shareDelata(CODE_02);
		Mockito.doReturn(100*(RATE_02_END-RATE_02_START)/RATE_02_START).when(realtimePortfolioAggregation).shareDeltaPercent(CODE_02);
		Mockito.doReturn(CURRENCY_USD).when(realtimePortfolioAggregation).shareCurrency(CODE_02);
		Mockito.doReturn(NAME_02).when(realtimePortfolioAggregation).shareName(CODE_02);
		
		
		Mockito.doReturn(RATE_01_START*EXCHANGE_RATE_EUR*WEIGHT_01).when(realtimePortfolioAggregation).lastRatePortfolio(CODE_01);
		Mockito.doReturn(RATE_01_END*EXCHANGE_RATE_EUR*WEIGHT_01).when(realtimePortfolioAggregation).realtimeRatePortfolio(CODE_01);
		Mockito.doReturn(EXCHANGE_RATE_EUR*WEIGHT_01*(RATE_01_END-RATE_01_START)).when(realtimePortfolioAggregation).deltaPortfolio(CODE_01);
		Mockito.doReturn(100*(RATE_01_END-RATE_01_START)/RATE_01_START).when(realtimePortfolioAggregation).deltaPortfolioPercent(CODE_01);
		Mockito.doReturn(lastDate).when(realtimePortfolioAggregation).lastShareDate(CODE_01);
		Mockito.doReturn(WEIGHT_01).when(realtimePortfolioAggregation).weight(CODE_01);
		
		
		Mockito.doReturn(RATE_02_START*LAST_EXCHANGE_RATE_USD*WEIGHT_02).when(realtimePortfolioAggregation).lastRatePortfolio(CODE_02);
		Mockito.doReturn(RATE_02_END*LAST_EXCHANGE_RATE_USD*WEIGHT_02).when(realtimePortfolioAggregation).realtimeRatePortfolio(CODE_02);
		Mockito.doReturn(LAST_EXCHANGE_RATE_USD*WEIGHT_02*(RATE_02_END-RATE_02_START)).when(realtimePortfolioAggregation).deltaPortfolio(CODE_02);
		Mockito.doReturn(100*(RATE_02_END-RATE_02_START)/RATE_02_START).when(realtimePortfolioAggregation).deltaPortfolioPercent(CODE_02);
		Mockito.doReturn(lastDate).when(realtimePortfolioAggregation).lastShareDate(CODE_02);
		Mockito.doReturn(WEIGHT_02).when(realtimePortfolioAggregation).weight(CODE_02);
		
		
		Mockito.doReturn(RATE_01_START*EXCHANGE_RATE_EUR*WEIGHT_01+ RATE_02_START*LAST_EXCHANGE_RATE_USD*WEIGHT_02).when(realtimePortfolioAggregation).lastRatePortfolio();
		Mockito.doReturn(RATE_01_END*EXCHANGE_RATE_EUR*WEIGHT_01 + RATE_02_END*LAST_EXCHANGE_RATE_USD*WEIGHT_02).when(realtimePortfolioAggregation).realtimeRatePortfolio();
		Mockito.doReturn(RATE_01_END*EXCHANGE_RATE_EUR*WEIGHT_01 + RATE_02_END*LAST_EXCHANGE_RATE_USD*WEIGHT_02  -(RATE_01_START*EXCHANGE_RATE_EUR*WEIGHT_01 + RATE_02_START*LAST_EXCHANGE_RATE_USD*WEIGHT_02)).when(realtimePortfolioAggregation).deltaPortfolio();
		Mockito.doReturn(100*(RATE_01_END*EXCHANGE_RATE_EUR*WEIGHT_01 + RATE_02_END*LAST_EXCHANGE_RATE_USD*WEIGHT_02  -(RATE_01_START*EXCHANGE_RATE_EUR*WEIGHT_01 + RATE_02_START*LAST_EXCHANGE_RATE_USD*WEIGHT_02))/(RATE_01_START*EXCHANGE_RATE_EUR*WEIGHT_01 + RATE_02_START*LAST_EXCHANGE_RATE_USD*WEIGHT_02)).when(realtimePortfolioAggregation).deltaPortfolioPercent();
		
		
		Mockito.doReturn(Arrays.asList(PORTFOLIO_CURRENCY, CURRENCY_USD)).when(realtimePortfolioAggregation).currencies();
		Mockito.doReturn(EXCHANGE_RATE_EUR).when(realtimePortfolioAggregation).lastExchangeRateForCurrency(PORTFOLIO_CURRENCY);
		Mockito.doReturn(EXCHANGE_RATE_EUR).when(realtimePortfolioAggregation).realtimeExchangeRateForCurrency(PORTFOLIO_CURRENCY);
		Mockito.doReturn(lastDateExchangeRateEur).when(realtimePortfolioAggregation).lastExchangeRateDate(PORTFOLIO_CURRENCY);
		Mockito.doReturn(currentDateExchangeRateEur).when(realtimePortfolioAggregation).realtimeExchangeRateDate(PORTFOLIO_CURRENCY);
		
		Mockito.doReturn(0d).when(realtimePortfolioAggregation).deltaPercentExchangeRate(PORTFOLIO_CURRENCY);
		
		Mockito.doReturn(LAST_EXCHANGE_RATE_USD).when(realtimePortfolioAggregation).lastExchangeRateForCurrency(CURRENCY_USD);
		Mockito.doReturn(CURRENT_EXCHANGE_RATE_USD).when(realtimePortfolioAggregation).realtimeExchangeRateForCurrency(CURRENCY_USD);
		Mockito.doReturn(lastDateExchangeRateUsd).when(realtimePortfolioAggregation).lastExchangeRateDate(CURRENCY_USD);
		Mockito.doReturn(currentDateExchangeRateUsd).when(realtimePortfolioAggregation).realtimeExchangeRateDate(CURRENCY_USD);
		
		Mockito.doReturn((CURRENT_EXCHANGE_RATE_USD - LAST_EXCHANGE_RATE_USD) / LAST_EXCHANGE_RATE_USD).when(realtimePortfolioAggregation).deltaPercentExchangeRate(CURRENCY_USD);
		
		realtimeCoursesAO.assign(realtimePortfolioAggregation);
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
		Assert.assertEquals(CODE_01, sapMap.get(RealtimeCoursesAO.CODE_COLUMN));
		
		final Map<String,Object> koMap = shares.get(1);
		Assert.assertEquals(RATE_02_START, koMap.get(RealtimeCoursesAO.LAST_COLUMN));
		Assert.assertEquals(RATE_02_END, koMap.get(RealtimeCoursesAO.CURRENT_COLUMN));
		Assert.assertEquals((RATE_02_END-RATE_02_START), koMap.get(RealtimeCoursesAO.DELTA_COLUMN));
		Assert.assertEquals(100*(RATE_02_END-RATE_02_START)/RATE_02_START, koMap.get(RealtimeCoursesAO.DELTA_PERCENT_COLUMN));
		Assert.assertEquals(CURRENCY_USD, koMap.get(RealtimeCoursesAO.CURRENCY_COLUMN));
		Assert.assertEquals(String.format("%s (%s)", NAME_02, CODE_02), koMap.get(RealtimeCoursesAO.NAME_COLUMN));
		Assert.assertEquals(CODE_02, koMap.get(RealtimeCoursesAO.CODE_COLUMN));
	}
	
	@Test
	public final void  realtimeCourses() {
		final List<Map<String, Object>>  realtimeCourses =  new ArrayList<>(realtimeCoursesAO.getRealtimeCourses());
		Assert.assertEquals(3, realtimeCourses.size());
		final Map<String,Object> sapMap = realtimeCourses.get(1);
		
		Assert.assertEquals(RATE_01_START*EXCHANGE_RATE_EUR*WEIGHT_01, sapMap.get(RealtimeCoursesAO.LAST_COLUMN));
		Assert.assertEquals(RATE_01_END*EXCHANGE_RATE_EUR*WEIGHT_01, sapMap.get(RealtimeCoursesAO.CURRENT_COLUMN));
		Assert.assertEquals(truncate(EXCHANGE_RATE_EUR*WEIGHT_01*(RATE_01_END-RATE_01_START)), truncate((Double) sapMap.get(RealtimeCoursesAO.DELTA_COLUMN)));
		Assert.assertEquals(truncate(100*(RATE_01_END-RATE_01_START)/RATE_01_START), truncate((Double)sapMap.get(RealtimeCoursesAO.DELTA_PERCENT_COLUMN)));
		Assert.assertEquals(String.format("%s (%s)", NAME_01, CODE_01), sapMap.get(RealtimeCoursesAO.NAME_COLUMN));
		Assert.assertEquals(lastDate, sapMap.get(RealtimeCoursesAO.LAST_DATE_COLUMN));
		Assert.assertEquals(WEIGHT_01, sapMap.get(RealtimeCoursesAO.WEIGHT_COLUMN));
		final Map<String,Object> coMap = realtimeCourses.get(2);
	
		Assert.assertEquals(RATE_02_START*LAST_EXCHANGE_RATE_USD*WEIGHT_02, coMap.get(RealtimeCoursesAO.LAST_COLUMN));
		Assert.assertEquals(truncate(RATE_02_END*LAST_EXCHANGE_RATE_USD*WEIGHT_02), truncate((Double) coMap.get(RealtimeCoursesAO.CURRENT_COLUMN)));
		Assert.assertEquals(truncate(LAST_EXCHANGE_RATE_USD*WEIGHT_02*(RATE_02_END-RATE_02_START)), truncate((Double) coMap.get(RealtimeCoursesAO.DELTA_COLUMN)));
		Assert.assertEquals(truncate(100*(RATE_02_END-RATE_02_START)/RATE_02_START), truncate((Double)coMap.get(RealtimeCoursesAO.DELTA_PERCENT_COLUMN)));
		Assert.assertEquals(String.format("%s (%s)", NAME_02, CODE_02), coMap.get(RealtimeCoursesAO.NAME_COLUMN));
		
		Assert.assertEquals(WEIGHT_02, coMap.get(RealtimeCoursesAO.WEIGHT_COLUMN));
		Assert.assertEquals(lastDate, coMap.get(RealtimeCoursesAO.LAST_DATE_COLUMN));
		
		final Map<String,Object> portfolioMap = realtimeCourses.get(0);
		
		Assert.assertEquals(RATE_01_START*EXCHANGE_RATE_EUR*WEIGHT_01+ RATE_02_START*LAST_EXCHANGE_RATE_USD*WEIGHT_02 , portfolioMap.get(RealtimeCoursesAO.LAST_COLUMN));
		Assert.assertEquals(RATE_01_END*EXCHANGE_RATE_EUR*WEIGHT_01 + RATE_02_END*LAST_EXCHANGE_RATE_USD*WEIGHT_02, portfolioMap.get(RealtimeCoursesAO.CURRENT_COLUMN));
		
		Assert.assertEquals(RATE_01_END*EXCHANGE_RATE_EUR*WEIGHT_01 + RATE_02_END*LAST_EXCHANGE_RATE_USD*WEIGHT_02  -(RATE_01_START*EXCHANGE_RATE_EUR*WEIGHT_01 + RATE_02_START*LAST_EXCHANGE_RATE_USD*WEIGHT_02),  portfolioMap.get(RealtimeCoursesAO.DELTA_COLUMN));
		
		Assert.assertEquals(100*(RATE_01_END*EXCHANGE_RATE_EUR*WEIGHT_01 + RATE_02_END*LAST_EXCHANGE_RATE_USD*WEIGHT_02  -(RATE_01_START*EXCHANGE_RATE_EUR*WEIGHT_01 + RATE_02_START*LAST_EXCHANGE_RATE_USD*WEIGHT_02))/(RATE_01_START*EXCHANGE_RATE_EUR*WEIGHT_01 + RATE_02_START*LAST_EXCHANGE_RATE_USD*WEIGHT_02),  portfolioMap.get(RealtimeCoursesAO.DELTA_PERCENT_COLUMN));
		
		Assert.assertEquals(PORTFOLIO_NAME, portfolioMap.get(RealtimeCoursesAO.NAME_COLUMN));
		Assert.assertFalse(portfolioMap.containsKey(RealtimeCoursesAO.LAST_DATE_COLUMN));
		
		
	}
	
	@Test
	public final void exchangeRateToMap() {
		final List<Map<String, Object>>  exchangeReates =  new ArrayList<>(realtimeCoursesAO.getRealtimeExchangeRates());
		Assert.assertEquals(2, exchangeReates.size());
		final Map<String,Object> eurMap = exchangeReates.get(0);
		Assert.assertEquals(PORTFOLIO_CURRENCY, eurMap.get(RealtimeCoursesAO.NAME_COLUMN));
		Assert.assertEquals(EXCHANGE_RATE_EUR, eurMap.get(RealtimeCoursesAO.LAST_COLUMN));
		Assert.assertEquals(EXCHANGE_RATE_EUR, eurMap.get(RealtimeCoursesAO.CURRENT_COLUMN));
		Assert.assertEquals(lastDateExchangeRateEur, eurMap.get(RealtimeCoursesAO.LAST_DATE_COLUMN));
		Assert.assertEquals(currentDateExchangeRateEur, eurMap.get(RealtimeCoursesAO.CURRENT_DATE_COLUMN));
		Assert.assertEquals(0d, eurMap.get(RealtimeCoursesAO.DELTA_PERCENT_COLUMN));
		
		final Map<String,Object> usdMap = exchangeReates.get(1);
		Assert.assertEquals(CURRENCY_USD, usdMap.get(RealtimeCoursesAO.NAME_COLUMN));
		
		Assert.assertEquals(LAST_EXCHANGE_RATE_USD, usdMap.get(RealtimeCoursesAO.LAST_COLUMN));
		Assert.assertEquals(CURRENT_EXCHANGE_RATE_USD, usdMap.get(RealtimeCoursesAO.CURRENT_COLUMN));
		
		Assert.assertEquals(lastDateExchangeRateUsd, usdMap.get(RealtimeCoursesAO.LAST_DATE_COLUMN));
		Assert.assertEquals(currentDateExchangeRateUsd, usdMap.get(RealtimeCoursesAO.CURRENT_DATE_COLUMN));
		
		Assert.assertEquals((CURRENT_EXCHANGE_RATE_USD - LAST_EXCHANGE_RATE_USD) / LAST_EXCHANGE_RATE_USD, usdMap.get(RealtimeCoursesAO.DELTA_PERCENT_COLUMN));
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
	public final void codeColumn() {
		Assert.assertEquals(RealtimeCoursesAO.CODE_COLUMN, realtimeCoursesAO.getCodeColumn());
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
	
	@Test
	public final void currentDateColumn() {
		Assert.assertEquals(RealtimeCoursesAO.CURRENT_DATE_COLUMN, realtimeCoursesAO.getCurrentDateColumn());
	}
}
