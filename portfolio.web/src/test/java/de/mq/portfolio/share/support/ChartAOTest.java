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
import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;

import org.junit.Assert;

public class ChartAOTest {

	private static final Double CURRENT_RATE = 47.11d;

	private static final Double LAST_RATE = 47d;

	private static final String NAME = "Coca Cola";

	private static final String CURRENCY_USD = "USD";

	private static final String WKN = "0815";

	private static final String CODE = "KO";

	private static final String INDEX = "^DJI";

	private final ChartAO chartAO = new ChartAO();

	private final Map<Class<?>, Object> fields = new HashMap<>();
	private final Collection<LineChartSeries> series = Arrays.asList(Mockito.mock(LineChartSeries.class));
	private final TimeCourse timeCourse = Mockito.mock(TimeCourse.class);

	private final Share share = Mockito.mock(Share.class);

	private final List<Data> dividends = Arrays.asList(Mockito.mock(Data.class));

	final Data last = Mockito.mock(Data.class);
	final Data current = Mockito.mock(Data.class);
	final List<Data> rates = Arrays.asList(last, current);

	@Before
	public final void setup() {
		Mockito.when(last.value()).thenReturn(LAST_RATE);
		Mockito.when(current.value()).thenReturn(CURRENT_RATE);
		Mockito.when(timeCourse.share()).thenReturn(share);
		Mockito.when(timeCourse.dividends()).thenReturn(dividends);
		Mockito.when(share.wkn()).thenReturn(WKN);
		Mockito.when(share.name()).thenReturn(NAME);
		Mockito.when(share.currency()).thenReturn(CURRENCY_USD);
		Mockito.when(share.index()).thenReturn(INDEX);
		ReflectionUtils.doWithFields(chartAO.getClass(), field -> fields.put(field.getType(), ReflectionTestUtils.getField(chartAO, field.getName())), field -> !Modifier.isStatic(field.getModifiers()));
	}

	@Test
	public final void create() {
		final Axis axis = getField(DateAxis.class);
		Assert.assertEquals(ChartAO.TICK_FORMAT, axis.getTickFormat());
		final LineChartModel lineChartModel = getField(LineChartModel.class);
		Assert.assertEquals(2, lineChartModel.getAxes().size());
		Assert.assertEquals(ChartAO.LABEL_TIME, lineChartModel.getAxis(AxisType.X).getLabel());

	}

	@SuppressWarnings("unchecked")
	private <T> T getField(Class<T> clazz) {
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
		chartAO.setTimeCourse(timeCourse);
		Assert.assertEquals(dividends, chartAO.getDividends());
	}

	@Test
	public final void code() {
		Assert.assertNull(chartAO.getCode());
		chartAO.setCode(CODE);
		Assert.assertEquals(CODE, chartAO.getCode());
	}

	@Test
	public final void wkn() {
		Assert.assertNull(chartAO.getWkn());
		chartAO.setTimeCourse(timeCourse);
		Assert.assertEquals(WKN, chartAO.getWkn());
	}

	@Test
	public final void currency() {
		Assert.assertNull(chartAO.getCurrency());
		chartAO.setTimeCourse(timeCourse);
		Assert.assertEquals(CURRENCY_USD, chartAO.getCurrency());
	}

	@Test
	public final void name() {
		Assert.assertNull(chartAO.getName());
		chartAO.setTimeCourse(timeCourse);
		Assert.assertEquals(NAME, chartAO.getName());
	}

	@Test
	public final void getIndex() {
		Assert.assertNull(chartAO.getIndex());
		chartAO.setTimeCourse(timeCourse);
		Assert.assertEquals(INDEX, chartAO.getIndex());
	}

	@Test
	public final void getCurrent() {
		Assert.assertNull(chartAO.getCurrent());
		chartAO.setRealTimeRates(rates);
		Assert.assertEquals(CURRENT_RATE, chartAO.getCurrent());
		chartAO.setRealTimeRates(Arrays.asList());
		Assert.assertNull(chartAO.getCurrent());
	}

	@Test
	public final void getLast() {
		Assert.assertNull(chartAO.getLast());
		chartAO.setRealTimeRates(rates);
		Assert.assertEquals(LAST_RATE, chartAO.getLast());
		chartAO.setRealTimeRates(Arrays.asList());
		Assert.assertNull(chartAO.getLast());
	}

	@Test
	public final void isRealTimeRateValid() {
		Assert.assertTrue(!chartAO.isRealTimeRateValid());
		chartAO.setRealTimeRates(rates);
		Assert.assertTrue(chartAO.isRealTimeRateValid());
		ReflectionTestUtils.setField(chartAO, "last", null);
		Assert.assertTrue(!chartAO.isRealTimeRateValid());
		chartAO.setRealTimeRates(rates);
		ReflectionTestUtils.setField(chartAO, "current", null);
		Assert.assertTrue(!chartAO.isRealTimeRateValid());
	}

	

	@Test
	public final void chartModel() {
		Assert.assertEquals(getField(LineChartModel.class), chartAO.getChartModel());
	}

}
