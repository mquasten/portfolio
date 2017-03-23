package de.mq.portfolio.share.support;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.primefaces.model.chart.LineChartSeries;

import de.mq.portfolio.share.Data;
import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.ShareService;
import de.mq.portfolio.share.TimeCourse;
import org.junit.Assert;

public class ChartControllerTest {

	private static final double VALUE = 47.11d;

	private static final Date DATE = new Date();

	private static final String WKN = "DE0815";

	private static final String CURRENCY = "EUR";

	private static final String NAME = "SAP'AG";

	private static final String CODE = "SAP";

	private final ShareService shareService = Mockito.mock(ShareService.class);

	private final ChartControllerImpl chartController = new ChartControllerImpl(shareService);

	private final TimeCourse timeCourse = Mockito.mock(TimeCourse.class);

	private final ChartAO chartAO = Mockito.mock(ChartAO.class);

	private final Share share = Mockito.mock(Share.class);

	private Data rate = Mockito.mock(Data.class);

	@SuppressWarnings("rawtypes")
	ArgumentCaptor<Collection> chartSeries = ArgumentCaptor.forClass(Collection.class);

	@Before
	public final void setup() {
		Mockito.when(shareService.timeCourse(CODE)).thenReturn(Optional.of(timeCourse));
		Mockito.when(chartAO.getCode()).thenReturn(CODE);
		Mockito.when(timeCourse.share()).thenReturn(share);
		Mockito.when(share.name()).thenReturn(NAME);
		Mockito.when(timeCourse.dividends()).thenReturn(Arrays.asList(Mockito.mock(Data.class)));
		Mockito.when(share.currency()).thenReturn(CURRENCY);
		Mockito.when(share.wkn()).thenReturn(WKN);

		Mockito.when(rate.date()).thenReturn(DATE);
		Mockito.when(rate.value()).thenReturn(VALUE);
		Mockito.when(timeCourse.rates()).thenReturn(Arrays.asList(rate));

	}

	@SuppressWarnings("unchecked")
	@Test
	public final void init() {
		chartController.init(chartAO);

		Mockito.verify(chartAO).getCode();

		Mockito.verify(chartAO).setTimeCourse(timeCourse);

		

		Mockito.verify(chartAO).assign(chartSeries.capture());

		Assert.assertEquals(1, chartSeries.getValue().size());

		final LineChartSeries result = (LineChartSeries) chartSeries.getValue().stream().findAny().get();

		Assert.assertEquals(1, result.getData().size());

		Assert.assertEquals(VALUE, result.getData().values().stream().findAny().get());

		Assert.assertEquals(chartController.df.format(DATE), result.getData().keySet().stream().findAny().get());

		Assert.assertFalse(result.isShowMarker());
		Assert.assertEquals(NAME.replaceAll("'", " "), result.getLabel());

	}

	@Test
	public final void initNoTimeCourse() {
		Mockito.when(shareService.timeCourse(CODE)).thenReturn(Optional.empty());
		chartController.init(chartAO);

		Mockito.verify(chartAO).getCode();

		Mockito.verify(chartAO, Mockito.never()).setTimeCourse(Mockito.any());

		
		Mockito.verify(chartAO, Mockito.never()).assign(Mockito.any());

	}

}
