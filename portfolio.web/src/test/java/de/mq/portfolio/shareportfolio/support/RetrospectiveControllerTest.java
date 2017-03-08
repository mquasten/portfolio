package de.mq.portfolio.shareportfolio.support;

import java.util.Arrays;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import org.springframework.core.convert.converter.Converter;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.exchangerate.ExchangeRateCalculator;
import de.mq.portfolio.exchangerate.support.ExchangeRateService;
import de.mq.portfolio.shareportfolio.SharePortfolio;
import org.junit.Assert;

public class RetrospectiveControllerTest {

	private static final String FILTER = "filter";
	private static final String PORTFOLIO_ID = "19680528";
	private final SharePortfolioService sharePortfolioService = Mockito.mock(SharePortfolioService.class);
	private final ExchangeRateService exchangeRateService = Mockito.mock(ExchangeRateService.class);
	@SuppressWarnings("unchecked")
	private final Converter<String, String> currencyConverter = Mockito.mock(Converter.class);

	private final RetrospectiveControllerImpl retrospectiveController = new RetrospectiveControllerImpl(sharePortfolioService, exchangeRateService, currencyConverter);

	private final RetrospectiveAO retrospectiveAO = Mockito.mock(RetrospectiveAO.class);

	private final ExchangeRate exchangeRate = Mockito.mock(ExchangeRate.class);

	private final SharePortfolio sharePortfolio = Mockito.mock(SharePortfolio.class);

	private final ExchangeRateCalculator exchangeRateCalculator = Mockito.mock(ExchangeRateCalculator.class);
	private final SharePortfolioRetrospective sharePortfolioRetrospective = Mockito.mock(SharePortfolioRetrospective.class);

	@Before
	public final void setup() {
		Mockito.when(retrospectiveAO.getPortfolioId()).thenReturn(PORTFOLIO_ID);
		Mockito.when(sharePortfolioService.sharePortfolio(PORTFOLIO_ID)).thenReturn(sharePortfolio);
		Mockito.when(sharePortfolio.exchangeRateTranslations()).thenReturn(Arrays.asList(exchangeRate));
		Mockito.when(sharePortfolioService.retrospective(PORTFOLIO_ID)).thenReturn(sharePortfolioRetrospective);
		Mockito.when(exchangeRateService.exchangeRateCalculator(Arrays.asList(exchangeRate))).thenReturn(exchangeRateCalculator);
		Mockito.when(retrospectiveAO.getFilter()).thenReturn(FILTER);
	}

	@Test
	public final void init() {
		retrospectiveController.init(retrospectiveAO);
		Mockito.verify(retrospectiveAO).assign(sharePortfolioRetrospective, currencyConverter, Optional.of(exchangeRateCalculator));

	}

	@Test
	public final void show() {
		Assert.assertEquals(String.format(RetrospectiveControllerImpl.REDIRECT_PATTERN, PORTFOLIO_ID, FILTER), retrospectiveController.show(retrospectiveAO));
	}
}
