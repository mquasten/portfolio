package de.mq.portfolio.share.support;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import java.util.Optional;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import com.lowagie.text.pdf.codec.Base64.OutputStream;

import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.gateway.GatewayParameterAggregation;
import de.mq.portfolio.gateway.ShareGatewayParameterService;
import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.ShareService;
import de.mq.portfolio.share.TimeCourse;


public class GatewaysControllerTest {
	

	private static final String PORTFOLIO_ID = UUID.randomUUID().toString();
	private static final String HISTORY = "content";
	private static final String MESSAGE = "don't worry just for test";
	private static final List<Entry<Gateway, Date>> GATEWAY_ENTRIES = Arrays.asList(new AbstractMap.SimpleImmutableEntry<>(Gateway.GoogleRealtimeRate, new Date()));
	private static final String CODE = "KO";
	
	private final ShareGatewayParameterService shareGatewayParameterService = Mockito.mock(ShareGatewayParameterService.class);
	private final ShareService shareService = Mockito.mock(ShareService.class);
	
	private final   GatewaysControllerImpl gatewaysController = new GatewaysControllerImpl(shareGatewayParameterService, shareService);
	private final TimeCourse timeCourse = Mockito.mock(TimeCourse.class);
	private final GatewayParameter gatewayParameter = Mockito.mock(GatewayParameter.class);
	
	private final GatewaysAO gatewaysAO = Mockito.mock(GatewaysAO.class);
	@SuppressWarnings("unchecked")
	private final GatewayParameterAggregation<Share> gatewayParameterAggregation = Mockito.mock(GatewayParameterAggregation.class);
	private final ArgumentCaptor<Share> shareCaptor = ArgumentCaptor.forClass(Share.class);
	
	private final FacesContext facesContext = Mockito.mock(FacesContext.class);
	private final ExternalContext externalContext = Mockito.mock(ExternalContext.class);
	
	
			
	private final OutputStream outputStream = Mockito.mock(OutputStream.class);
	@Before
	public final void setup() throws IOException {
		Mockito.when(gatewaysAO.getCode()).thenReturn(CODE);
		Mockito.when(shareService.timeCourse(CODE)).thenReturn(Optional.of(timeCourse));
		Mockito.when(shareGatewayParameterService.aggregationForAllGateways(shareCaptor.capture())).thenReturn(gatewayParameterAggregation);
		Mockito.when(gatewayParameterAggregation.gatewayParameters()).thenReturn(Arrays.asList(gatewayParameter));
		Mockito.when(timeCourse.updates()).thenReturn(GATEWAY_ENTRIES);
		Mockito.when(facesContext.getExternalContext()).thenReturn(externalContext);
		Mockito.when(shareGatewayParameterService.history(gatewayParameter)).thenReturn(HISTORY);
		Mockito.when(gatewayParameter.gateway()).thenReturn(Gateway.GoogleRealtimeRate);
		Mockito.when(externalContext.getResponseOutputStream()).thenReturn(outputStream);
		Mockito.when(gatewayParameter.code()).thenReturn(CODE);
		
	}
	
	@Test
	public final void init() {
		gatewaysController.init(gatewaysAO);
		Assert.assertEquals(CODE, shareCaptor.getValue().code());
		Mockito.verify(gatewaysAO).assign(GATEWAY_ENTRIES);
		Mockito.verify(gatewaysAO).setGatewayParameters(Arrays.asList(gatewayParameter));
	}
	
	@Test
	public final void initException() {
		Mockito.doThrow(new IllegalStateException(MESSAGE)).when(shareGatewayParameterService).aggregationForAllGateways(shareCaptor.capture());
		gatewaysController.init(gatewaysAO);
		Mockito.verify(gatewaysAO).setMessage(MESSAGE);
	}
	
	@Test
	public final void download() throws IOException {
		gatewaysController.download(facesContext, gatewayParameter);
		
		Mockito.verify(externalContext).responseReset();
		Mockito.verify(externalContext).setResponseContentLength(HISTORY.length());
		Mockito.verify(outputStream).write(HISTORY.getBytes());
		Mockito.verify(facesContext).responseComplete();
		Mockito.verify(externalContext).setResponseHeader(GatewaysControllerImpl.CONTENT_DISPOSITION_HEADER,String.format(GatewaysControllerImpl.FILE_ATTACHEMENT_FORMAT, Gateway.GoogleRealtimeRate.downloadName(CODE)));
	}
	
	@Test
	public final void downloadException() throws IOException {
		Mockito.doThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, MESSAGE)).when(shareGatewayParameterService).history(gatewayParameter);
		gatewaysController.download(facesContext, gatewayParameter);
		
		Mockito.verify(externalContext).responseReset();
		final ArgumentCaptor<Integer> contentLengthCaptor = ArgumentCaptor.forClass(Integer.class);
		Mockito.verify(externalContext).setResponseContentLength(contentLengthCaptor.capture());
		Mockito.verify(externalContext).setResponseHeader(GatewaysControllerImpl.CONTENT_DISPOSITION_HEADER,String.format(GatewaysControllerImpl.FILE_ATTACHEMENT_FORMAT, gatewayParameter.gateway().id(gatewayParameter.code()) + GatewaysControllerImpl.HTML_EXTENSION));
		final ArgumentCaptor<byte[]> contentCaptor = ArgumentCaptor.forClass(byte[].class);
		Mockito.verify(outputStream).write(contentCaptor.capture());
		Mockito.verify(facesContext).responseComplete();
		Assert.assertEquals(contentCaptor.getValue().length, (int) contentLengthCaptor.getValue());
		Assert.assertTrue(new String(contentCaptor.getValue()).contains(HttpStatus.INTERNAL_SERVER_ERROR.value() +" " + MESSAGE));
	}
	
	@Test
	public final void backRealtimeCourses() {
		Mockito.when(gatewaysAO.isPortfolio()).thenReturn(true);
		Mockito.when(gatewaysAO.isExchangeRate()).thenReturn(false);
		Mockito.when(gatewaysAO.getPortfolioId()).thenReturn(PORTFOLIO_ID);
		Assert.assertEquals(String.format(GatewaysControllerImpl.URL_PATTERN_REALTIME_COURSES, PORTFOLIO_ID), gatewaysController.back(gatewaysAO));
	}
	
	@Test
	public final void backChart() {
		Mockito.when(gatewaysAO.isPortfolio()).thenReturn(false);
		Mockito.when(gatewaysAO.isExchangeRate()).thenReturn(false);
		Assert.assertEquals(String.format(GatewaysControllerImpl.URL_PATTERN_CHART, CODE), gatewaysController.back(gatewaysAO));
	}
	
	@Test
	public final void  backExchangeRatesPortfolio() {
		Mockito.when(gatewaysAO.isPortfolio()).thenReturn(true);
		Mockito.when(gatewaysAO.isExchangeRate()).thenReturn(true);
		Mockito.when(gatewaysAO.getPortfolioId()).thenReturn(PORTFOLIO_ID);
		
		Assert.assertEquals(String.format(GatewaysControllerImpl.URL_PATTERN_EXCHANGE_RATES_PORTFOLIO, PORTFOLIO_ID), gatewaysController.back(gatewaysAO));
	}
	
	@Test
	public final void  backexchangeRates() {
		Mockito.when(gatewaysAO.isPortfolio()).thenReturn(false);
		Mockito.when(gatewaysAO.isExchangeRate()).thenReturn(true);
		Assert.assertEquals(GatewaysControllerImpl.URL_EXCHANGE_RATES, gatewaysController.back(gatewaysAO));
	}
	

}
