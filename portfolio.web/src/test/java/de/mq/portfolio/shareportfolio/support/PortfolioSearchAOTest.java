package de.mq.portfolio.shareportfolio.support;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;

import de.mq.portfolio.exchangerate.ExchangeRateCalculator;
import de.mq.portfolio.shareportfolio.SharePortfolio;

public class PortfolioSearchAOTest {

	private static final String PORTFOLIO_NAME = "portfolioName";

	private static final String SHARE_NAME = "shareName";

	private static final String NAME = "name";

	private final PortfolioSearchAO portfolioSearchAO = new PortfolioSearchAO();

	private final SharePortfolio sharePortfolio = Mockito.mock(SharePortfolio.class);

	private final Pageable pageable = Mockito.mock(Pageable.class);

	private final ExchangeRateCalculator exchangeRateCalculator = Mockito.mock(ExchangeRateCalculator.class);

	@Test
	public void selectedPortfolio() {
		Assert.assertNull(portfolioSearchAO.getSelectedPortfolio());
		portfolioSearchAO.setSelectedPortfolio(sharePortfolio);
		Assert.assertEquals(sharePortfolio, portfolioSearchAO.getSelectedPortfolio());
	}

	@Test
	public void isSelectedPortfolioReadonly() {
		Assert.assertTrue(portfolioSearchAO.isSelectedPortfolioReadonly());
		Mockito.when(sharePortfolio.isCommitted()).thenReturn(true);
		portfolioSearchAO.setSelectedPortfolio(sharePortfolio);
		Assert.assertTrue(portfolioSearchAO.isSelectedPortfolioReadonly());
		Mockito.when(sharePortfolio.isCommitted()).thenReturn(false);
		Assert.assertFalse(portfolioSearchAO.isSelectedPortfolioReadonly());
	}

	@Test
	public void isRetrospectiveAware() {
		Assert.assertFalse(portfolioSearchAO.isRetrospectiveAware());
		portfolioSearchAO.setSelectedPortfolio(sharePortfolio);
		Mockito.when(sharePortfolio.isCommitted()).thenReturn(true);
		Assert.assertTrue(portfolioSearchAO.isRetrospectiveAware());
		Mockito.when(sharePortfolio.isCommitted()).thenReturn(false);
		Assert.assertFalse(portfolioSearchAO.isRetrospectiveAware());
	}

	@Test
	public void name() {
		Assert.assertNull(portfolioSearchAO.getName());
		portfolioSearchAO.setName(NAME);
		Assert.assertEquals(NAME, portfolioSearchAO.getName());
	}

	@Test
	public void share() {
		Assert.assertNull(portfolioSearchAO.getShare());
		portfolioSearchAO.setShare(SHARE_NAME);
		Assert.assertEquals(SHARE_NAME, portfolioSearchAO.getShare());
	}

	@Test
	public void pageable() {
		Assert.assertNull(portfolioSearchAO.getPageable());
		portfolioSearchAO.setPageable(pageable);
		Assert.assertEquals(pageable, portfolioSearchAO.getPageable());
	}

	@Test
	public void sharePortfolios() {
		Assert.assertTrue(portfolioSearchAO.getSharePortfolios().isEmpty());
		portfolioSearchAO.setSharePortfolios(Arrays.asList(sharePortfolio));
		Assert.assertEquals(Arrays.asList(sharePortfolio), portfolioSearchAO.getSharePortfolios());
	}

	@Test
	public void criteria() {
		portfolioSearchAO.setShare(SHARE_NAME);
		portfolioSearchAO.setName(NAME);

		Assert.assertEquals(NAME, portfolioSearchAO.criteria().name());
		Assert.assertEquals(1, portfolioSearchAO.criteria().timeCourses().stream().count());
		Assert.assertEquals(SHARE_NAME, portfolioSearchAO.criteria().timeCourses().stream().findAny().get().name());
		Assert.assertEquals(SHARE_NAME, portfolioSearchAO.criteria().timeCourses().stream().findAny().get().share().name());
	}

	@Test
	public void portfolioName() {
		Assert.assertNull(portfolioSearchAO.getPortfolioName());
		portfolioSearchAO.setPortfolioName(PORTFOLIO_NAME);
		Assert.assertEquals(PORTFOLIO_NAME, portfolioSearchAO.getPortfolioName());
	}

	@Test
	public void exchangeRateCalculator() {
		portfolioSearchAO.setExchangeRateCalculator(exchangeRateCalculator);
		Assert.assertEquals(exchangeRateCalculator, portfolioSearchAO.getExchangeRateCalculator());
	}

	@Test(expected = IllegalArgumentException.class)
	public void exchangeRateCalculatorNull() {
		portfolioSearchAO.getExchangeRateCalculator();
	}

}
