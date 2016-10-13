package de.mq.portfolio.shareportfolio.support;

import java.util.Arrays;
import java.util.Collection;


import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.LegendPlacement;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;
import org.primefaces.model.chart.LinearAxis;

import junit.framework.Assert;

public class RetrospectiveAOTest {
	
	private static final String TITLE = "whereTheWildRosesGrow";
	private static final String FILTER = "filter";
	private static final String PORTFOLIO_ID = "19680528";
	private final RetrospectiveAO retrospectiveAO = new RetrospectiveAO();
	
	private final LineChartSeries rs01 = Mockito.mock(LineChartSeries.class);
	
	private final LineChartSeries rs02 = Mockito.mock(LineChartSeries.class);
	
	
	private final Collection<LineChartSeries> ratesSeries = Arrays.asList(rs01,rs02);
	
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

}
