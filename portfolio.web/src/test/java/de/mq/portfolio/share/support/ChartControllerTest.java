package de.mq.portfolio.share.support;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.primefaces.model.chart.LineChartSeries;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.gateway.GatewayParameterAggregation;
import de.mq.portfolio.gateway.ShareGatewayParameterService;
import de.mq.portfolio.share.Data;
import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.ShareService;
import de.mq.portfolio.share.TimeCourse;

public class ChartControllerTest {

	private static final String CONTENT = "They call me the wild rose\nBut my name was Elisa Day...";

	private static final String MESSAGE = "At least one GatewayParameter should exist.";

	private static final double VALUE = 47.11d;

	private static final Date DATE = new Date();

	private static final String WKN = "DE0815";

	private static final String CURRENCY = "EUR";

	private static final String NAME = "SAP'AG";

	private static final String CODE = "SAP";

	private final ShareService shareService = Mockito.mock(ShareService.class);

	private final ShareGatewayParameterService shareGatewayParameterService = Mockito.mock(ShareGatewayParameterService.class);

	private final ChartControllerImpl chartController = new ChartControllerImpl(shareService, shareGatewayParameterService);

	private final TimeCourse timeCourse = Mockito.mock(TimeCourse.class);

	private final ChartAO chartAO = Mockito.mock(ChartAO.class);

	private final Share share = Mockito.mock(Share.class);

	private Data rate = Mockito.mock(Data.class);

	private Data last = Mockito.mock(Data.class);
	private Data current = Mockito.mock(Data.class);

	private final TimeCourse realTimeCourse = Mockito.mock(TimeCourse.class);

	private final GatewayParameter gatewayParameter = Mockito.mock(GatewayParameter.class);

	@SuppressWarnings("unchecked")
	private final GatewayParameterAggregation<Share> gatewayParameterAggregation = Mockito.mock(GatewayParameterAggregation.class);

	@SuppressWarnings("rawtypes")
	private final ArgumentCaptor<Collection> chartSeries = ArgumentCaptor.forClass(Collection.class);

	private final FacesContext facesContext = Mockito.mock(FacesContext.class);

	private final ExternalContext externalContext = Mockito.mock(ExternalContext.class);

	private final ByteArrayOutputStream responseOutputStream = new ByteArrayOutputStream();

	@Before
	public final void setup() throws IOException {
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

		Mockito.when(last.value()).thenReturn(47d);
		Mockito.when(current.value()).thenReturn(47.11d);
		Mockito.when(realTimeCourse.rates()).thenReturn(Arrays.asList(last, current));
		Mockito.when(shareService.realTimeCourses(Arrays.asList(CODE), false)).thenReturn(Arrays.asList(realTimeCourse));

		Mockito.when(shareGatewayParameterService.gatewayParameters(share)).thenReturn(gatewayParameterAggregation);
		Mockito.when(gatewayParameterAggregation.gatewayParameters()).thenReturn(Arrays.asList(gatewayParameter));

		Mockito.when(facesContext.getExternalContext()).thenReturn(externalContext);

		Mockito.when(shareGatewayParameterService.history(gatewayParameter)).thenReturn(CONTENT);

		Mockito.when(gatewayParameter.gateway()).thenReturn(Gateway.GoogleRateHistory);

		Mockito.when(externalContext.getResponseOutputStream()).thenReturn(responseOutputStream);
		Mockito.when(gatewayParameter.code()).thenReturn(CODE);

	}

	@SuppressWarnings("unchecked")
	@Test
	public final void init() {
		chartController.init(chartAO);

		Mockito.verify(chartAO, Mockito.atLeast(1)).getCode();

		Mockito.verify(chartAO).setTimeCourse(timeCourse);

		Mockito.verify(chartAO).setRealTimeRates(Arrays.asList());
		Mockito.verify(chartAO).setRealTimeRates(realTimeCourse.rates());

		Mockito.verify(chartAO).assign(chartSeries.capture());

		Assert.assertEquals(1, chartSeries.getValue().size());

		final LineChartSeries result = (LineChartSeries) chartSeries.getValue().stream().findAny().get();

		Assert.assertEquals(1, result.getData().size());

		Assert.assertEquals(VALUE, result.getData().values().stream().findAny().get());

		Assert.assertEquals(chartController.df.format(DATE), result.getData().keySet().stream().findAny().get());

		Assert.assertFalse(result.isShowMarker());
		Assert.assertEquals(NAME.replaceAll("'", " "), result.getLabel());

		Mockito.verify(chartAO).setGatewayParameters(Arrays.asList(gatewayParameter));
		Mockito.verify(chartAO, Mockito.never()).setMessage(Mockito.anyString());

	}

	@Test
	public final void initGatewayParameterSucks() {
		Mockito.doThrow(new IllegalArgumentException(MESSAGE)).when(shareGatewayParameterService).gatewayParameters(share);
		chartController.init(chartAO);
		Mockito.verify(chartAO).setMessage(MESSAGE);
	}

	@Test
	public final void initNoTimeCourse() {
		Mockito.when(shareService.timeCourse(CODE)).thenReturn(Optional.empty());
		chartController.init(chartAO);

		Mockito.verify(chartAO).getCode();

		Mockito.verify(chartAO, Mockito.never()).setTimeCourse(Mockito.any());

		Mockito.verify(chartAO, Mockito.never()).assign(Mockito.any());

	}

	@Test
	public final void refresh() {
		chartController.refresh(chartAO);

		Mockito.verify(chartAO).setRealTimeRates(Arrays.asList());
		Mockito.verify(chartAO).setRealTimeRates(realTimeCourse.rates());
	}

	@Test
	public final void refreshNoRealTimeCourse() {

		Mockito.when(shareService.realTimeCourses(Mockito.any(), Mockito.anyBoolean())).thenReturn(Arrays.asList());

		chartController.refresh(chartAO);

		Mockito.verify(chartAO).setRealTimeRates(Arrays.asList());
		Mockito.verify(chartAO, Mockito.never()).setRealTimeRates(realTimeCourse.rates());
	}

	@Test
	public final void download() throws IOException {

		chartController.download(facesContext, gatewayParameter);

		Assert.assertEquals(CONTENT, new String(responseOutputStream.toByteArray()));

		Mockito.verify(facesContext).responseComplete();
		Mockito.verify(externalContext).setResponseHeader(ChartControllerImpl.CONTENT_DISPOSITION_HEADER, String.format(ChartControllerImpl.FILE_ATTACHEMENT_FORMAT, Gateway.GoogleRateHistory.downloadName(CODE)));
		Mockito.verify(externalContext).setResponseContentLength(CONTENT.getBytes().length);
		Mockito.verify(externalContext).responseReset();
	}

	@Test
	public final void downloadSucks() throws IOException {
		final HttpClientErrorException httpClientErrorException = new HttpClientErrorException(HttpStatus.NOT_FOUND);
		final String messageAsHtml = replaceStackTraceLines(String.format(ChartControllerImpl.ERROR_HTML_PATTERN, Gateway.GoogleRateHistory, httpClientErrorException.getMessage(), printStackTrace(httpClientErrorException)));
		Mockito.doThrow(httpClientErrorException).when(shareGatewayParameterService).history(gatewayParameter);

		chartController.download(facesContext, gatewayParameter);

		final byte[] content = responseOutputStream.toByteArray();
		Assert.assertEquals(messageAsHtml, replaceStackTraceLines(new String(content)));

		Mockito.verify(facesContext).responseComplete();
		Mockito.verify(externalContext).responseReset();
		Mockito.verify(externalContext).setResponseContentLength(content.length);
		Mockito.verify(externalContext).setResponseHeader(ChartControllerImpl.CONTENT_DISPOSITION_HEADER, String.format(ChartControllerImpl.FILE_ATTACHEMENT_FORMAT, Gateway.GoogleRateHistory.id(CODE) + ChartControllerImpl.HTML_EXTENSION));

	}

	private String printStackTrace(final HttpClientErrorException httpClientErrorException) {
		final StringWriter stackTrace = new StringWriter();
		httpClientErrorException.printStackTrace(new PrintWriter(stackTrace));
		return stackTrace.getBuffer().toString();
	}

	private String replaceStackTraceLines(final String stackStrace) {
		return stackStrace.replaceAll("[\t]+at" + ".*", "").replaceAll("[\n\r]", "");
	}

}
