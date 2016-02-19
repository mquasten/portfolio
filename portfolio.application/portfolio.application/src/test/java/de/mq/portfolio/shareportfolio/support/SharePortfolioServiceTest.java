package de.mq.portfolio.shareportfolio.support;

import java.util.ArrayList;
import java.util.Collection;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.dao.support.DataAccessUtils;

import de.mq.portfolio.shareportfolio.PortfolioOptimisation;

public class SharePortfolioServiceTest {
	
	private final SharePortfolioRepository sharePortfolioRepository = Mockito.mock(SharePortfolioRepository.class);
	private final  SharePortfolioService sharePortfolioService = new SharePortfolioServiceImpl(sharePortfolioRepository);
	Collection<PortfolioOptimisation> portfolioOptimisations = new ArrayList<>();
	
	@Test
	public final void aggregate() {
	
		
		Assert.assertTrue((((SharePortfolioServiceImpl)sharePortfolioService).aggregate(portfolioOptimisations).isEmpty()));
		
		portfolioOptimisations.add(new  PortfolioOptimisationImpl("mq-test", new double[]{} ,0.5, 1L));
	  
		Assert.assertEquals(0.5, DataAccessUtils.requiredSingleResult(((SharePortfolioServiceImpl)sharePortfolioService).aggregate(portfolioOptimisations)).variance());
		
		portfolioOptimisations.add(new  PortfolioOptimisationImpl("mq-test", new double[]{} ,0.6, 1L));
	
		Assert.assertEquals(0.5, DataAccessUtils.requiredSingleResult(((SharePortfolioServiceImpl)sharePortfolioService).aggregate(portfolioOptimisations)).variance());
		
		portfolioOptimisations.add(new  PortfolioOptimisationImpl("mq-test", new double[]{} ,0.4, 1L));
		
		Assert.assertEquals(0.4, DataAccessUtils.requiredSingleResult(((SharePortfolioServiceImpl)sharePortfolioService).aggregate(portfolioOptimisations)).variance());
		
	}

}
