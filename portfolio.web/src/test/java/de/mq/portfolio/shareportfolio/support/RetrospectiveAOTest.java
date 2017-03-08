package de.mq.portfolio.shareportfolio.support;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LegendPlacement;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;
import org.primefaces.model.chart.LinearAxis;
import org.springframework.core.convert.converter.Converter;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.portfolio.exchangerate.ExchangeRateCalculator;
import de.mq.portfolio.share.Data;
import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.shareportfolio.OptimisationAlgorithm;
import de.mq.portfolio.shareportfolio.OptimisationAlgorithm.AlgorithmType;
import de.mq.portfolio.shareportfolio.SharePortfolio;
import org.junit.Assert;

public class RetrospectiveAOTest {
	
	private static final double RATE = 47.11;
	private static final String US$_SYMBOL = "US$";
	private static final String SHARE_NAME = "shareName";
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
	
	
	private final TimeCourseRetrospective timeCourseRetrospective = Mockito.mock(TimeCourseRetrospective.class);
	
	private final SharePortfolioRetrospective sharePortfolioRetrospective = Mockito.mock(SharePortfolioRetrospective.class);
	
	@SuppressWarnings("unchecked")
	private final Converter<String,String> currencyConverter = (Converter<String, String>) Mockito.mock((Class<?>) Converter.class);
	
	private final ExchangeRateCalculator exchangeRateCalculator = Mockito.mock(ExchangeRateCalculator.class);
	
	
	private final SharePortfolio commitedPortfolio = Mockito.mock(SharePortfolio.class);
	private final SharePortfolio currentPortfolio = Mockito.mock(SharePortfolio.class);
	
	
	private final Date initialDate = asDate(LocalDateTime.now().minusDays(1));
	
	private final  Data initialRateWithExchangeRate = Mockito.mock(Data.class);
	
	private final Date endDate = new Date();
	private final  Data endRateWithExchangeRate = Mockito.mock(Data.class);
	
	private final  Data rate = Mockito.mock(Data.class);
	
	private final TimeCourse timeCourse = Mockito.mock(TimeCourse.class);
	
	private final Share share = Mockito.mock(Share.class);

	@Before
	public final void setup() {
		Mockito.when(rs01.getLabel()).thenReturn("label01");
		Mockito.when(rs02.getLabel()).thenReturn("label02");
		
		Mockito.when(sharePortfolioRetrospective.committedSharePortfolio()).thenReturn(commitedPortfolio);
		
		Mockito.when(sharePortfolioRetrospective.currentSharePortfolio()).thenReturn(currentPortfolio);
		Mockito.when(initialRateWithExchangeRate.date()).thenReturn(initialDate);
		Mockito.when(sharePortfolioRetrospective.initialRateWithExchangeRate()).thenReturn(initialRateWithExchangeRate);
		
		Mockito.when(initialRateWithExchangeRate.value()).thenReturn(RATE);
		
		Mockito.when(sharePortfolioRetrospective.endRateWithExchangeRate()).thenReturn(endRateWithExchangeRate);
		Mockito.when(endRateWithExchangeRate.date()).thenReturn(endDate);
		
		Mockito.when(sharePortfolioRetrospective.standardDeviation()).thenReturn(STANDARD_DEVIATION);
		
		Mockito.when(sharePortfolioRetrospective.totalRate()).thenReturn(TOTAL_RATE);
		
		Mockito.when(sharePortfolioRetrospective.totalRateDividends()).thenReturn(TOTAL_RATE_DIVIDENDS);
		
		
		Mockito.when(sharePortfolioRetrospective.timeCoursesWithExchangeRate()).thenReturn(Arrays.asList(timeCourseRetrospective));
		
		Mockito.when(rate.date()).thenReturn(initialDate);
		Mockito.when(rate.value()).thenReturn(RATE);
		Mockito.when(timeCourse.rates()).thenReturn(Arrays.asList(rate));
		
		Mockito.when(timeCourseRetrospective.timeCourse()).thenReturn(timeCourse);
		Mockito.when(share.name()).thenReturn(SHARE_NAME);
		Mockito.when(timeCourse.share()).thenReturn(share);
		Mockito.when(commitedPortfolio.currency()).thenReturn(CURRENCY_USD);
		
		Mockito.when(currencyConverter.convert(CURRENCY_USD)).thenReturn(US$_SYMBOL);
		Mockito.when(commitedPortfolio.name()).thenReturn(TITLE);
		
	}
	
	
	 public Date asDate(LocalDateTime localDateTime) {
		    return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
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
		Assert.assertEquals((Double) 0d, (Double)  retrospectiveAO.getStandardDeviation());
		ReflectionTestUtils.setField(retrospectiveAO, STANDARD_DEVIATION_FIELD, STANDARD_DEVIATION);
		Assert.assertEquals((Double) STANDARD_DEVIATION, (Double) retrospectiveAO.getStandardDeviation());
	}
	
	@Test
	public final void totalRate() {
		Assert.assertEquals((Double) 0d,  retrospectiveAO.getTotalRate());
		ReflectionTestUtils.setField(retrospectiveAO, TOTAL_RATE_FIELD, TOTAL_RATE);
		Assert.assertEquals((Double) TOTAL_RATE, retrospectiveAO.getTotalRate());
	}
	
	@Test
	public final void totalRateDividends() {
		Assert.assertEquals((Double) 0d, retrospectiveAO.getTotalRateDividends());
		ReflectionTestUtils.setField(retrospectiveAO, TOTAL_RATE_DIVIDENDS_FIELDS, TOTAL_RATE_DIVIDENDS);
		Assert.assertEquals((Double) TOTAL_RATE_DIVIDENDS, retrospectiveAO.getTotalRateDividends());
	}
	
	
	@Test
	public final void assignAll() {
		
		final PortfolioAO committedPortfolioAO = Mockito.mock(PortfolioAO.class);
		
		
		final PortfolioAO currentPortfolioAO = Mockito.mock(PortfolioAO.class);
		
		ReflectionTestUtils.setField(retrospectiveAO, COMMITTED_PORTFOLIO_FIELD, committedPortfolioAO);
		ReflectionTestUtils.setField(retrospectiveAO, CURRENT_PORTFOLIO_FIELD, currentPortfolioAO);
		
		retrospectiveAO.assign(sharePortfolioRetrospective, currencyConverter, Optional.of(exchangeRateCalculator));
		
		Mockito.verify(committedPortfolioAO).setSharePortfolio(commitedPortfolio, Optional.of(exchangeRateCalculator));
		
		
		
		Mockito.verify(currentPortfolioAO).setSharePortfolio(currentPortfolio,  Optional.of(exchangeRateCalculator));
		
		Assert.assertEquals(CURRENCY_USD, retrospectiveAO.getCurrency());
		
		Assert.assertEquals(String.format(RetrospectiveAO.ORDINATE_LABEL_PATTERN,  US$_SYMBOL), ((LineChartModel)retrospectiveAO.getChartModel()).getAxis(AxisType.Y).getLabel());
	
		Assert.assertEquals(TITLE, ((LineChartModel)retrospectiveAO.getChartModel()).getTitle() );
		
		Assert.assertEquals(initialDate, retrospectiveAO.getStartDate());
		Assert.assertEquals(endDate, retrospectiveAO.getEndDate());
		
		Assert.assertEquals((Double) STANDARD_DEVIATION, (Double) retrospectiveAO.getStandardDeviation());
		
		Assert.assertEquals((Double) TOTAL_RATE, retrospectiveAO.getTotalRate());
		Assert.assertEquals((Double) TOTAL_RATE_DIVIDENDS, retrospectiveAO.getTotalRateDividends());
		
		Assert.assertEquals(Arrays.asList(timeCourseRetrospective), retrospectiveAO.getTimeCourseRetrospectives());
		
		

		
		final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		
	
		
		final LineChartModel model = (LineChartModel) retrospectiveAO.getChartModel();
		final Collection<ChartSeries> start = model.getSeries().stream().filter(s -> s.getLabel().equals(RetrospectiveAO.START_LINE_LABEL)).collect(Collectors.toList());
		Assert.assertEquals(1, start.size());
		start.stream().findAny().get().getData().entrySet().forEach(e -> {
			Assert.assertEquals(RATE, e.getValue());
			Assert.assertTrue(df.format(initialDate).equals(e.getKey())||df.format(endDate).equals(e.getKey()));
		});
	
		final Collection<ChartSeries> shareChart = model.getSeries().stream().filter(s -> s.getLabel().equals(SHARE_NAME)).collect(Collectors.toList());
		Assert.assertEquals(1, shareChart.size());
		shareChart.stream().findAny().get().getData().entrySet().forEach(e -> {
			Assert.assertEquals(RATE, e.getValue());
			Assert.assertEquals(df.format(initialDate), e.getKey());
		});
		
		Assert.assertEquals(2, retrospectiveAO.getCurves().size());
		
		retrospectiveAO.getCurves().stream().map(item -> item.getLabel()).forEach(l -> Assert.assertTrue(l.equals(RetrospectiveAO.START_LINE_LABEL)||l.equals(SHARE_NAME)));
		retrospectiveAO.getCurves().stream().map(item -> item.getValue()).forEach(v -> Assert.assertTrue(v.equals(RetrospectiveAO.START_LINE_LABEL)||v.equals(SHARE_NAME)));
	}


	

	@Test
	public final void setOptimisationAlgorithms() {
		
		final OptimisationAlgorithm optimisationAlgorithm = Mockito.mock(OptimisationAlgorithm.class);
		Mockito.when(optimisationAlgorithm.algorithmType()).thenReturn(AlgorithmType.MVP);
		Assert.assertTrue(getAlgorithms(retrospectiveAO.getCommittedPortfolio()).isEmpty());
		
		Assert.assertTrue(getAlgorithms(retrospectiveAO.getCurrentPortfolio()).isEmpty());
		
	
		retrospectiveAO.setOptimisationAlgorithms(Arrays.asList(optimisationAlgorithm));
		
		Assert.assertFalse(getAlgorithms(retrospectiveAO.getCommittedPortfolio()).isEmpty());
		
		Assert.assertFalse(getAlgorithms(retrospectiveAO.getCurrentPortfolio()).isEmpty());
		
		Assert.assertEquals(AlgorithmType.MVP, getAlgorithms(retrospectiveAO.getCommittedPortfolio()).keySet().stream().findAny().get());
		Assert.assertEquals(AlgorithmType.MVP, getAlgorithms(retrospectiveAO.getCurrentPortfolio()).keySet().stream().findAny().get());
		
		Assert.assertEquals(optimisationAlgorithm, getAlgorithms(retrospectiveAO.getCommittedPortfolio()).values().stream().findAny().get());
		Assert.assertEquals(optimisationAlgorithm, getAlgorithms(retrospectiveAO.getCurrentPortfolio()).values().stream().findAny().get());
		
	}


	@SuppressWarnings("unchecked")
	private Map<AlgorithmType, OptimisationAlgorithm> getAlgorithms(final PortfolioAO portfolioAO) {
		return (Map<AlgorithmType, OptimisationAlgorithm>) ReflectionTestUtils.getField(portfolioAO, "optimisationAlgorithms");
	}
}
