package de.mq.portfolio.shareportfolio.support;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.LegendPlacement;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;
import org.primefaces.model.chart.LinearAxis;
import org.springframework.test.util.ReflectionTestUtils;

import junit.framework.Assert;

public class RetrospectiveAOTest {
	
	private static final double TOTAL_RATE_DIVIDENDS = 4.711e-2;
	private static final String TOTAL_RATE_DIVIDENDS_FIELDS = "totalRateDividends";
	private static final String TOTAL_RATE_FIELD = "totalRate";
	private static final double TOTAL_RATE = 12.34e-2;
	private static final String STANDARD_DEVIATION_FIELD = "standardDeviation";
	private static final double STANDARD_DEVIATION = 1.23456e-6;
	private static final String CURRENT_PORTFOLIO_FIELD = "currentPortfolio";
	private static final String COMMITTED_PORTFOLIO_FIELD = "committedPortfolio";
	private static final String CURRENCY_USD = "USD";
	private static final String CURRENCY_FIELD = "currency";
	private static final String END_DATE_FIELD = "endDate";
	private static final String START_DATE_FIELD = "startDate";
	private static final String TITLE = "whereTheWildRosesGrow";
	private static final String FILTER = "filter";
	private static final String PORTFOLIO_ID = "19680528";
	private final RetrospectiveAO retrospectiveAO = new RetrospectiveAO();
	
	private final LineChartSeries rs01 = Mockito.mock(LineChartSeries.class);
	
	private final LineChartSeries rs02 = Mockito.mock(LineChartSeries.class);
	
	
	private final Collection<LineChartSeries> ratesSeries = Arrays.asList(rs01,rs02);
	
	
	private TimeCourseRetrospective timeCourseRetrospective = Mockito.mock(TimeCourseRetrospective.class);
	
	@Before
	public final void setup() {
		Mockito.when(rs01.getLabel()).thenReturn("label01");
		Mockito.when(rs02.getLabel()).thenReturn("label02");
		
	}
	
	@Test
	public final void getChartModel() {
		final LineChartModel model = (LineChartModel) retrospectiveAO.getChartModel();
		
		Assert.assertEquals(retrospectiveAO.axis, model.getAxes().get(AxisType.X));
		
		Assert.assertEquals(RetrospectiveAO.LEGEGEND_POSITION, model.getLegendPosition());
		
		Assert.assertEquals(LegendPlacement.OUTSIDEGRID, model.getLegendPlacement());
		
		Assert.assertEquals(RetrospectiveAO.TICKFORMAT, retrospectiveAO.axis.getTickFormat());
		
		Assert.assertEquals(LinearAxis.class, model.getAxes().get(AxisType.Y).getClass());
		
		Assert.assertTrue(model.getSeries().isEmpty());
		Assert.assertNull(model.getTitle());
	}
	
	
	@Test
	public final void  portfolioId() {
		Assert.assertNull(retrospectiveAO.getPortfolioId());
		retrospectiveAO.setPortfolioId(PORTFOLIO_ID);
		Assert.assertEquals(PORTFOLIO_ID, retrospectiveAO.getPortfolioId());
	}
	
	@Test
	public final void assign() {
		
		Assert.assertTrue(retrospectiveAO.getCurves().isEmpty());
		
		retrospectiveAO.assign(ratesSeries);
		
		final LineChartModel model = (LineChartModel) retrospectiveAO.getChartModel();
		Assert.assertEquals(ratesSeries, model.getSeries());
		Assert.assertEquals(2, retrospectiveAO.getCurves().size());
		retrospectiveAO.getCurves().stream().map(e -> e.getLabel()).forEach(l -> Assert.assertTrue(l.equals(rs01.getLabel())||l.equals(rs02.getLabel())));
		retrospectiveAO.getCurves().stream().map(e -> e.getValue()).forEach(v -> Assert.assertTrue(v.equals(rs01.getLabel())||v.equals(rs02.getLabel())));
		
	}
	
	@Test
	public final void assignFilter() {
		retrospectiveAO.setFilter(".*01");
		retrospectiveAO.assign(ratesSeries);
		
		final LineChartModel model = (LineChartModel) retrospectiveAO.getChartModel();
		Assert.assertEquals(Arrays.asList(rs01), model.getSeries());
		Assert.assertEquals(2, retrospectiveAO.getCurves().size());
		retrospectiveAO.getCurves().stream().map(e -> e.getLabel()).forEach(l -> Assert.assertTrue(l.equals(rs01.getLabel())||l.equals(rs02.getLabel())));
		retrospectiveAO.getCurves().stream().map(e -> e.getValue()).forEach(v -> Assert.assertTrue(v.equals(rs01.getLabel())||v.equals(rs02.getLabel())));
		
		
	}
	
	@Test
	public final void  filter() {
		Assert.assertEquals(RetrospectiveAO.DEFAULT_FILTER, retrospectiveAO.getFilter());
		
		retrospectiveAO.setFilter(FILTER);
		Assert.assertEquals(FILTER, retrospectiveAO.getFilter());
	}
	
	@Test
	public final void title() {
		retrospectiveAO.setTitle(TITLE);
		
		Assert.assertEquals(TITLE, retrospectiveAO.getChartModel().getTitle());
	}
	
	@Test
	public final void timeCourseRetrospectives() {
		Assert.assertTrue(retrospectiveAO.getTimeCourseRetrospectives().isEmpty());
		retrospectiveAO.setTimeCourseRetrospectives(Arrays.asList(timeCourseRetrospective));
		Assert.assertEquals(Arrays.asList(timeCourseRetrospective), retrospectiveAO.getTimeCourseRetrospectives());
	}
	
	@Test
	public final void startDate() {
		Assert.assertNull(retrospectiveAO.getStartDate());
		final Date date = new Date();
		ReflectionTestUtils.setField(retrospectiveAO, START_DATE_FIELD, date);
		Assert.assertEquals(date, retrospectiveAO.getStartDate());
	}
	
	@Test
	public final void endDate() {
		Assert.assertNull(retrospectiveAO.getEndDate());
		final Date date = new Date();
		ReflectionTestUtils.setField(retrospectiveAO, END_DATE_FIELD, date);
		Assert.assertEquals(date, retrospectiveAO.getEndDate());
		
	}
	
	@Test
	public final void currency() {
		Assert.assertNull(retrospectiveAO.getCurrency());
		ReflectionTestUtils.setField(retrospectiveAO, CURRENCY_FIELD, CURRENCY_USD);
		Assert.assertEquals(CURRENCY_USD, retrospectiveAO.getCurrency());
	}
	
	@Test
	public final void committedPortfolio() {
		final PortfolioAO portfolio = Mockito.mock(PortfolioAO.class);
		Assert.assertEquals(PortfolioAO.class, retrospectiveAO.getCommittedPortfolio().getClass());
		ReflectionTestUtils.setField(retrospectiveAO, COMMITTED_PORTFOLIO_FIELD, portfolio);
		Assert.assertEquals(portfolio, retrospectiveAO.getCommittedPortfolio());
	}
	
	@Test
	public final void currentPortfolio() {
		final PortfolioAO portfolio = Mockito.mock(PortfolioAO.class);
		Assert.assertEquals(PortfolioAO.class, retrospectiveAO.getCurrentPortfolio().getClass());
		ReflectionTestUtils.setField(retrospectiveAO, CURRENT_PORTFOLIO_FIELD, portfolio);
		Assert.assertEquals(portfolio, retrospectiveAO.getCurrentPortfolio());
	}
	
	@Test
	public final void standardDeviation() {
		Assert.assertEquals(0d, retrospectiveAO.getStandardDeviation());
		ReflectionTestUtils.setField(retrospectiveAO, STANDARD_DEVIATION_FIELD, STANDARD_DEVIATION);
		Assert.assertEquals(STANDARD_DEVIATION, retrospectiveAO.getStandardDeviation());
	}
	
	@Test
	public final void totalRate() {
		Assert.assertEquals(0d, retrospectiveAO.getTotalRate());
		ReflectionTestUtils.setField(retrospectiveAO, TOTAL_RATE_FIELD, TOTAL_RATE);
		Assert.assertEquals(TOTAL_RATE, retrospectiveAO.getTotalRate());
	}
	
	@Test
	public final void totalRateDividends() {
		Assert.assertEquals(0d, retrospectiveAO.getTotalRateDividends());
		ReflectionTestUtils.setField(retrospectiveAO, TOTAL_RATE_DIVIDENDS_FIELDS, TOTAL_RATE_DIVIDENDS);
		Assert.assertEquals(TOTAL_RATE_DIVIDENDS, retrospectiveAO.getTotalRateDividends());
	}

}
