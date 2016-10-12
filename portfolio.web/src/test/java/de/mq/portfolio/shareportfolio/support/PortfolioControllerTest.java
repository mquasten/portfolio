package de.mq.portfolio.shareportfolio.support;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.core.convert.converter.Converter;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.client.ResourceAccessException;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.exchangerate.ExchangeRateCalculator;
import de.mq.portfolio.exchangerate.support.ExchangeRateService;
import de.mq.portfolio.share.ShareService;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.shareportfolio.SharePortfolio;
import de.mq.portfolio.support.UserModel;
import junit.framework.Assert;

public class PortfolioControllerTest {

	private static final String CODE = "code";
	private static final byte[] CONTENT = "kylie is nice and ...".getBytes();
	private static final String SELECTED_PORTFOLIO_NAME = "selectedPortfolio";
	private static final String SELECTED_PORTFOLIO_ID = "19680528";
	private static final String PORTFOLIO_NAME = "minRisk";
	private static final String PORTFOLIO_ID = "4711";
	private final SharePortfolioService sharePortfolioService = Mockito.mock(SharePortfolioService.class);
	private final ShareService shareService = Mockito.mock(ShareService.class);
	private final ExchangeRateService exchangeRateService = Mockito.mock(ExchangeRateService.class);
	@SuppressWarnings("unchecked")
	private final Converter<PortfolioAO, byte[]> pdfConverter = Mockito.mock(Converter.class);

	private final AbstractPortfolioController portfolioController = Mockito.mock(AbstractPortfolioController.class, Mockito.CALLS_REAL_METHODS);

	private final UserModel userModel = Mockito.mock(UserModel.class);

	private final PortfolioSearchAO portfolioSearchAO = Mockito.mock(PortfolioSearchAO.class);

	private final SharePortfolio sharePortfolio = Mockito.mock(SharePortfolio.class);

	private final SharePortfolio criteria = Mockito.mock(SharePortfolio.class);

	private final ExchangeRateCalculator exchangeRateCalculator = Mockito.mock(ExchangeRateCalculator.class);

	private final Pageable pageable = Mockito.mock(Pageable.class);

	private final SharePortfolio selectedSharePortfolio = Mockito.mock(SharePortfolio.class);

	private final FacesContext facesContext = Mockito.mock(FacesContext.class);

	private final static String EXISTS_MESSAGE = "existsMessage";

	private final ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
	private final ArgumentCaptor<String> clientIdCaptor = ArgumentCaptor.forClass(String.class);

	private final PortfolioAO portfolioAO = Mockito.mock(PortfolioAO.class);

	private final TimeCourse timeCourse = Mockito.mock(TimeCourse.class);

	private final ExchangeRate exchangeRate = Mockito.mock(ExchangeRate.class);

	private final ExternalContext externalContext = Mockito.mock(ExternalContext.class);

	private MockHttpServletResponse response = new MockHttpServletResponse();

	@Before
	public final void setup() {
		Mockito.when(selectedSharePortfolio.id()).thenReturn(SELECTED_PORTFOLIO_ID);
		Mockito.when(selectedSharePortfolio.name()).thenReturn(SELECTED_PORTFOLIO_NAME);
		Mockito.when(sharePortfolio.name()).thenReturn(PORTFOLIO_NAME);
		Mockito.when(userModel.getPortfolioId()).thenReturn(PORTFOLIO_ID);
		Mockito.when(sharePortfolioService.sharePortfolio(PORTFOLIO_ID)).thenReturn(sharePortfolio);
		Mockito.when(exchangeRateService.exchangeRateCalculator()).thenReturn(exchangeRateCalculator);

		Mockito.when(portfolioSearchAO.criteria()).thenReturn(criteria);

		Mockito.when(sharePortfolioService.pageable(portfolioSearchAO.criteria(), AbstractPortfolioController.DEFAULT_SORT, AbstractPortfolioController.DEFAULT_PAGE_SIZE)).thenReturn(pageable);

		Mockito.when(portfolioSearchAO.getPageable()).thenReturn(pageable);

		Mockito.when(sharePortfolioService.portfolios(pageable, criteria)).thenReturn(Arrays.asList(sharePortfolio, criteria));

		Mockito.when(portfolioSearchAO.getSelectedPortfolio()).thenReturn(selectedSharePortfolio);

		Mockito.when(portfolioAO.getId()).thenReturn(PORTFOLIO_ID);
		Mockito.when(portfolioAO.getName()).thenReturn(PORTFOLIO_NAME);

		Mockito.when(portfolioAO.getSharePortfolio()).thenReturn(sharePortfolio);
		Mockito.when(portfolioAO.getTimeCourses()).thenReturn(Arrays.asList(timeCourse));

		Mockito.when(sharePortfolio.exchangeRateTranslations()).thenReturn(Arrays.asList(exchangeRate));

		Mockito.when(facesContext.getExternalContext()).thenReturn(externalContext);

		Mockito.when(externalContext.getResponse()).thenReturn(response);

		Mockito.when(pdfConverter.convert(portfolioAO)).thenReturn(CONTENT);

		Mockito.when(sharePortfolioService.committedPortfolio(PORTFOLIO_NAME)).thenReturn(sharePortfolio);

		Mockito.when(sharePortfolio.id()).thenReturn(PORTFOLIO_ID);

		Mockito.when(shareService.timeCourse(CODE)).thenReturn(Optional.of(timeCourse));

		Mockito.when(exchangeRateService.exchangeRateCalculator(sharePortfolio.exchangeRateTranslations())).thenReturn(exchangeRateCalculator);
		final Map<Class<?>, Object> dependencies = new HashMap<>();
		dependencies.put(SharePortfolioService.class, sharePortfolioService);
		dependencies.put(ShareService.class, shareService);
		dependencies.put(ExchangeRateService.class, exchangeRateService);
		dependencies.put(Converter.class, pdfConverter);

		ReflectionUtils.doWithFields(AbstractPortfolioController.class, field -> ReflectionTestUtils.setField(portfolioController, field.getName(), dependencies.get(field.getType())), field -> dependencies.containsKey(field.getType()));

		Mockito.doAnswer(a -> facesContext).when(portfolioController).facesContext();

	}

	@Test
	public final void init() {
		portfolioController.init(portfolioSearchAO, userModel);

		Mockito.verify(portfolioSearchAO).setPortfolioName(PORTFOLIO_NAME);
		Mockito.verify(portfolioSearchAO).setExchangeRateCalculator(exchangeRateCalculator);

		Mockito.verify(portfolioSearchAO).setPageable(pageable);
		portfolioSearchAO.setSharePortfolios(Arrays.asList(sharePortfolio, criteria));
	}

	@Test
	public final void initPortFolioIdNull() {
		Mockito.when(userModel.getPortfolioId()).thenReturn(null);
		portfolioController.init(portfolioSearchAO, userModel);

		Mockito.verify(portfolioSearchAO, Mockito.never()).setPortfolioName(Mockito.anyString());
		Mockito.verify(portfolioSearchAO).setExchangeRateCalculator(exchangeRateCalculator);

		Mockito.verify(portfolioSearchAO).setPageable(pageable);
		portfolioSearchAO.setSharePortfolios(Arrays.asList(sharePortfolio, criteria));
	}

	@Test
	public final void activate() {
		portfolioController.activate(portfolioSearchAO, userModel);
		Mockito.verify(userModel).setPortfolioId(SELECTED_PORTFOLIO_ID);
		Mockito.verify(portfolioSearchAO).setPortfolioName(SELECTED_PORTFOLIO_NAME);

	}

	@Test
	public final void save() {
		Assert.assertEquals(AbstractPortfolioController.REDIRECT_TO_PORTFOLIOS_PAGE, portfolioController.save(sharePortfolio, EXISTS_MESSAGE));
		Mockito.verify(sharePortfolioService).save(sharePortfolio);
	}

	@Test
	public final void saveDuplicate() {
		Mockito.doThrow(DuplicateKeyException.class).when(sharePortfolioService).save(sharePortfolio);
		portfolioController.save(sharePortfolio, EXISTS_MESSAGE);

		Mockito.verify(facesContext).addMessage(clientIdCaptor.capture(), facesMessageCaptor.capture());

		Assert.assertNull(clientIdCaptor.getValue());

		Assert.assertEquals(FacesMessage.SEVERITY_ERROR, facesMessageCaptor.getValue().getSeverity());
		Assert.assertEquals(EXISTS_MESSAGE, facesMessageCaptor.getValue().getSummary());

	}

	@Test
	public final void assign() {
		portfolioController.assign(portfolioAO);
		Mockito.verify(sharePortfolioService).assign(sharePortfolio, portfolioAO.getTimeCourses());
	}

	@Test
	public final void initPortfolio() {
		portfolioController.init(portfolioAO);

		Mockito.verify(portfolioAO).setSharePortfolio(sharePortfolio, Optional.of(exchangeRateCalculator));
	}

	@SuppressWarnings("unchecked")
	@Test
	public final void initPortfolioIdNull() {
		final ArgumentCaptor<SharePortfolio> sharePortfolioCaptor = ArgumentCaptor.forClass(SharePortfolio.class);

		@SuppressWarnings("rawtypes")
		final ArgumentCaptor<Optional> exchangeRateCalculatorCaptor = ArgumentCaptor.forClass(Optional.class);
		Mockito.when(portfolioAO.getId()).thenReturn(null);
		portfolioController.init(portfolioAO);

		Mockito.verify(portfolioAO).setSharePortfolio(sharePortfolioCaptor.capture(), exchangeRateCalculatorCaptor.capture());
		Assert.assertFalse(exchangeRateCalculatorCaptor.getValue().isPresent());
		Assert.assertNull(sharePortfolioCaptor.getValue().name());
		Assert.assertTrue(sharePortfolioCaptor.getValue().timeCourses().isEmpty());
	}

	@Test
	public final void pdf() throws UnsupportedEncodingException {

		portfolioController.pdf(portfolioAO);

		Assert.assertEquals(AbstractPortfolioController.CONTENT_TYPE_APPLICATION_PDF, response.getContentType());

		Assert.assertEquals(String.format(AbstractPortfolioController.DISPOSITION_PATTERN, PORTFOLIO_NAME), response.getHeader(AbstractPortfolioController.HEADER_CONTENT_DISPOSITION));

		Assert.assertEquals(CONTENT.length, response.getContentLength());
		Assert.assertEquals(new String(CONTENT), response.getContentAsString());
		Mockito.verify(facesContext).responseComplete();
	}

	@Test(expected = ResourceAccessException.class)
	public final void pdfSucks() throws IOException {
		final HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
		Mockito.when(externalContext.getResponse()).thenReturn(response);
		Mockito.doThrow(IOException.class).when(response).getOutputStream();
		portfolioController.pdf(portfolioAO);
	}

	@Test
	public final void commit() {
		Assert.assertEquals(String.format(AbstractPortfolioController.REDIRECT_PATTERN, PORTFOLIO_ID), portfolioController.commit(PORTFOLIO_NAME));
	}

	@Test
	public final void delete() {
		Assert.assertEquals(AbstractPortfolioController.REDIRECT_TO_PORTFOLIOS_PAGE, portfolioController.delete(PORTFOLIO_ID));
		Mockito.verify(sharePortfolioService).delete(PORTFOLIO_ID);
	}

	@Test
	public final void deleteTimeCourse() {
		Assert.assertEquals(String.format(AbstractPortfolioController.REDIRECT_PATTERN, PORTFOLIO_ID), portfolioController.deleteTimeCourse(PORTFOLIO_ID, CODE));

		Mockito.verify(sharePortfolio).remove(timeCourse);
		Mockito.verify(sharePortfolioService).save(sharePortfolio);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void deleteTimeCoursePortfolioCommitted() {
		Mockito.when(sharePortfolio.isCommitted()).thenReturn(true);
		portfolioController.deleteTimeCourse(PORTFOLIO_ID, CODE);
	}

	@Test
	public final void deleteTimeCourseNotPresent() {
		Mockito.when(shareService.timeCourse(CODE)).thenReturn(Optional.empty());
		Assert.assertEquals(String.format(AbstractPortfolioController.REDIRECT_PATTERN, PORTFOLIO_ID), portfolioController.deleteTimeCourse(PORTFOLIO_ID, CODE));

		Mockito.verify(sharePortfolio, Mockito.never()).remove(timeCourse);
		Mockito.verify(sharePortfolioService, Mockito.never()).save(sharePortfolio);
	}

	@Test
	public final void constructor() {
		final AbstractPortfolioController portfolioController = new AbstractPortfolioController(sharePortfolioService, shareService, exchangeRateService, pdfConverter) {
			@Override
			FacesContext facesContext() {
				return facesContext;
			}
		};
		final Map<Class<?>, Object> dependencies = new HashMap<>();

		ReflectionUtils.doWithFields(AbstractPortfolioController.class, field -> dependencies.put(field.getType(), ReflectionTestUtils.getField(portfolioController, field.getName())), field -> !Modifier.isStatic(field.getModifiers()));

		Assert.assertEquals(4, dependencies.size());

		Assert.assertEquals(sharePortfolioService, dependencies.get(SharePortfolioService.class));
		Assert.assertEquals(shareService, dependencies.get(ShareService.class));
		Assert.assertEquals(exchangeRateService, dependencies.get(ExchangeRateService.class));
		Assert.assertEquals(pdfConverter, dependencies.get(Converter.class));
	}

}
