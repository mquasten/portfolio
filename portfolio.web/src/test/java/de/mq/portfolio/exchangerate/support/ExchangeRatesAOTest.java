package de.mq.portfolio.exchangerate.support;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LegendPlacement;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;

import junit.framework.Assert;

public class ExchangeRatesAOTest {
	
	private static final String ID = "19680528";

	private static final String PORTFOLIO_NAME = "PortfolioName";

	private static final String PERIODE_WEEK = "7";

	private static final String CUR_USD = "USD";

	private final ExchangeRatesAO exchangeRatesAO = new ExchangeRatesAO();
	
	private final ChartSeries chartSeries = Mockito.mock(ChartSeries.class);
	
	private final Entry<String, ChartSeries> chartSeriesUSD = new AbstractMap.SimpleImmutableEntry<>(CUR_USD, chartSeries); 
	
	
	private final ExchangeRateRetrospective exchangeRateRetrospective = Mockito.mock(ExchangeRateRetrospective.class);
	
	@Before
	public final void setup() {
		final Map<Object, Number> values = new HashMap<>();
		values.put(new Date(), 1D);
		Mockito.when(chartSeries.getData()).thenReturn(values);
		Mockito.when(chartSeries.getLabel()).thenReturn("US$");
	}
	
	@Test
	public final void  getChartModel() {
		final LineChartModel model = (LineChartModel) exchangeRatesAO.getChartModel();
		Assert.assertEquals(LegendPlacement.OUTSIDEGRID, model.getLegendPlacement());
		Assert.assertEquals(ExchangeRatesAO.LEGEGEND_POSITION, model.getLegendPosition());
		Assert.assertEquals(2, model.getAxes().size());
		Assert.assertEquals(ExchangeRatesAO.DATE_AXIS_TITLE, model.getAxes().get(AxisType.X).getLabel());
		
	}
	
	
	@Test
	public final void assign() {
		exchangeRatesAO.assign(Arrays.asList(chartSeriesUSD));
		
		Assert.assertEquals(1, (((LineChartModel)exchangeRatesAO.getChartModel()).getSeries().size()));
		Assert.assertEquals(chartSeries, (((LineChartModel)exchangeRatesAO.getChartModel()).getSeries().stream().findAny().get()));
		
		Assert.assertEquals(1, exchangeRatesAO.getCurves().size());
		
		Assert.assertEquals(chartSeriesUSD.getKey(), exchangeRatesAO.getCurves().stream().findAny().get().getValue());
		
		Assert.assertEquals(chartSeries.getLabel(), exchangeRatesAO.getCurves().stream().findAny().get().getLabel());
		
	}
	
	@Test
	public final void assignNoValues() {
		
		exchangeRatesAO.setFilter("EUR");
		
		exchangeRatesAO.assign(Arrays.asList(chartSeriesUSD));
		
		Assert.assertEquals(1, (((LineChartModel)exchangeRatesAO.getChartModel()).getSeries().size()));
		Assert.assertEquals(LineChartSeries.class, (((LineChartModel)exchangeRatesAO.getChartModel()).getSeries().stream().findAny().get().getClass()));
		
		Assert.assertEquals(1, exchangeRatesAO.getCurves().size());
		
		Assert.assertEquals(chartSeriesUSD.getKey(), exchangeRatesAO.getCurves().stream().findAny().get().getValue());
		
		Assert.assertEquals(chartSeries.getLabel(), exchangeRatesAO.getCurves().stream().findAny().get().getLabel());
		
	}
	
	
	@Test
	public final void filter() {
		Assert.assertEquals(ExchangeRatesAO.DEFAULT_FILTER, exchangeRatesAO.getFilter());
		exchangeRatesAO.setFilter(CUR_USD);
		
		Assert.assertEquals(CUR_USD, exchangeRatesAO.getFilter());
		
		exchangeRatesAO.setFilter("*");
		Assert.assertEquals(ExchangeRatesAO.DEFAULT_FILTER, exchangeRatesAO.getFilter());
	}
	
	@Test
	public final void  period() {
		Assert.assertEquals(String.valueOf(ExchangeRatesAO.PERIOD_FOREVER), exchangeRatesAO.getPeriod());
		Assert.assertEquals(ExchangeRatesAO.PERIOD_FOREVER, exchangeRatesAO.period());
		
		exchangeRatesAO.setPeriod(PERIODE_WEEK);
		Assert.assertEquals(PERIODE_WEEK, exchangeRatesAO.getPeriod());
		
		Assert.assertEquals(Integer.valueOf(PERIODE_WEEK).intValue(), exchangeRatesAO.period());
		
		exchangeRatesAO.setPeriod("");
		Assert.assertEquals(String.valueOf(ExchangeRatesAO.PERIOD_FOREVER), exchangeRatesAO.getPeriod());
		Assert.assertEquals(ExchangeRatesAO.PERIOD_FOREVER, exchangeRatesAO.period());
	}
	
	@Test
	public final void portfolioName() {
		Assert.assertNull(exchangeRatesAO.getPortfolioName());
		exchangeRatesAO.setPortfolioName(PORTFOLIO_NAME);
		Assert.assertEquals(PORTFOLIO_NAME, exchangeRatesAO.getPortfolioName());
	}
	
	@Test
	public final void exchangeRateRetrospectives() {
		Assert.assertTrue(exchangeRatesAO.getExchangeRateRetrospectives().isEmpty());
		
		exchangeRatesAO.setExchangeRateRetrospectives(Arrays.asList(exchangeRateRetrospective));
		Assert.assertEquals(1, exchangeRatesAO.getExchangeRateRetrospectives().size());
		Assert.assertEquals(exchangeRateRetrospective, exchangeRatesAO.getExchangeRateRetrospectives().stream().findAny().get());
	}
	
	@Test
	public final void portfolioId() {
		Assert.assertNull(exchangeRatesAO.getPortfolioId());
		
		exchangeRatesAO.setPortfolioId(ID);
		Assert.assertEquals(ID, exchangeRatesAO.getPortfolioId());
	}

}
