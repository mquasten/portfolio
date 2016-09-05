package de.mq.portfolio.share.support;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.DateAxis;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ReflectionUtils;

import de.mq.portfolio.share.Data;
import junit.framework.Assert;

public class ChartAOTest {
	
	private static final String NAME = "Coca Cola";

	private static final String CURRENCY_USD = "USD";

	private static final String WKN = "0815";

	private static final String CODE = "KO";

	private final  ChartAO chartAO = new ChartAO();
	
	private final Map<Class<?>, Object> fields = new HashMap<>();
	private final Collection<LineChartSeries> series = Arrays.asList(Mockito.mock(LineChartSeries.class));
	
	private final List<Data> dividends = Arrays.asList(Mockito.mock(Data.class));
	
	@Before
	public final void setup() {
		ReflectionUtils.doWithFields(chartAO.getClass(), field ->  fields.put(field.getType(), ReflectionTestUtils.getField(chartAO, field.getName())), field -> ! Modifier.isStatic(field.getModifiers()) );
	}
	
	@Test
	public final void create() {
		final Axis axis =  getField(DateAxis.class);
		Assert.assertEquals(ChartAO.TICK_FORMAT, axis.getTickFormat());
		final LineChartModel lineChartModel = getField(LineChartModel.class);
		Assert.assertEquals(2, lineChartModel.getAxes().size());
		Assert.assertEquals(ChartAO.LABEL_TIME, lineChartModel.getAxis(AxisType.X).getLabel());
		
	}

	@SuppressWarnings("unchecked")
	private <T> T  getField(Class<T> clazz) {
		return (T) fields.get(clazz);
	}
	
	@Test
	public final void assign() {
		chartAO.assign(series);
		Assert.assertEquals(series, getField(LineChartModel.class).getSeries());
		
	}
	
	@Test
	public final void dividends() {
		Assert.assertTrue(chartAO.getDividends().isEmpty());
		chartAO.setDividends(dividends);
		Assert.assertEquals(dividends, chartAO.getDividends());
	}
	
	@Test
	public final void  code() {
		Assert.assertNull(chartAO.getCode());
		chartAO.setCode(CODE);
		Assert.assertEquals(CODE, chartAO.getCode());
	}
	
	@Test
	public final void  wkn() {
		Assert.assertNull(chartAO.getWkn());
		chartAO.setWkn(WKN);
		Assert.assertEquals(WKN, chartAO.getWkn());
	}
	
	@Test
	public final void  currency() {
		Assert.assertNull(chartAO.getCurrency());
		chartAO.setCurrency(CURRENCY_USD);
		Assert.assertEquals(CURRENCY_USD, chartAO.getCurrency());
	}
	
	@Test
	public final void  name() {
		Assert.assertNull(chartAO.getName());
		chartAO.setName(NAME);
		Assert.assertEquals(NAME, chartAO.getName());
	}
	
	
	@Test
	public final void chartModel() {
		Assert.assertEquals(getField(LineChartModel.class), chartAO.getChartModel());
	}

}
