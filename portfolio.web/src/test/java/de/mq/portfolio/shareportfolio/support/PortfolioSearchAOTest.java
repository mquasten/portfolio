package de.mq.portfolio.shareportfolio.support;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import de.mq.portfolio.shareportfolio.SharePortfolio;

public class PortfolioSearchAOTest {
	
	private final  PortfolioSearchAO portfolioSearchAO = new PortfolioSearchAO();
	
	private final SharePortfolio sharePortfolio = Mockito.mock(SharePortfolio.class);
	
	@Test
	public void selectedPortfolio() {
		Assert.assertNull(portfolioSearchAO.getSelectedPortfolio());
		portfolioSearchAO.setSelectedPortfolio(sharePortfolio);
		Assert.assertEquals(sharePortfolio, portfolioSearchAO.getSelectedPortfolio());
	}
	
	@Test
	public void  isSelectedPortfolioReadonly() {
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

}
