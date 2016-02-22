package de.mq.portfolio.shareportfolio.support;

import java.util.ArrayList;

import java.util.Collection;

import java.util.List;






import junit.framework.Assert;






import org.junit.Test;
import org.mockito.Mockito;

import org.springframework.dao.support.DataAccessUtils;


import org.springframework.util.CollectionUtils;

import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.shareportfolio.PortfolioOptimisation;
import de.mq.portfolio.shareportfolio.SharePortfolio;

public class SharePortfolioServiceTest {
	
	private static final double VARIANCE = 1e-3;
	private static final int SAMPLES_SIZE = 100;
	private static final String NAME = "mq-test";
	private final SharePortfolioRepository sharePortfolioRepository = Mockito.mock(SharePortfolioRepository.class);
	private final  SharePortfolioService sharePortfolioService = new SharePortfolioServiceImpl(sharePortfolioRepository);
	private final Collection<PortfolioOptimisation> portfolioOptimisations = new ArrayList<>();
	private final SharePortfolio sharePortfolio = Mockito.mock(SharePortfolio.class);
	
	@Test
	public final void aggregate() {
	
		
		Assert.assertTrue((((SharePortfolioServiceImpl)sharePortfolioService).aggregate(portfolioOptimisations).isEmpty()));
		
		portfolioOptimisations.add(new  PortfolioOptimisationImpl(NAME, new double[]{} ,0.5, 1L));
	  
		Assert.assertEquals(0.5, DataAccessUtils.requiredSingleResult(((SharePortfolioServiceImpl)sharePortfolioService).aggregate(portfolioOptimisations)).variance());
		
		portfolioOptimisations.add(new  PortfolioOptimisationImpl(NAME, new double[]{} ,0.6, 1L));
	
		Assert.assertEquals(0.5, DataAccessUtils.requiredSingleResult(((SharePortfolioServiceImpl)sharePortfolioService).aggregate(portfolioOptimisations)).variance());
		
		portfolioOptimisations.add(new  PortfolioOptimisationImpl(NAME, new double[]{} ,0.4, 1L));
		
		Assert.assertEquals(0.4, DataAccessUtils.requiredSingleResult(((SharePortfolioServiceImpl)sharePortfolioService).aggregate(portfolioOptimisations)).variance());
		
	}
	
	@Test
	public final void committedPortfolio() {
		Mockito.when(sharePortfolioRepository.portfolio(NAME)).thenReturn(sharePortfolio);
		Assert.assertEquals(sharePortfolio, sharePortfolioService.committedPortfolio(NAME));
		
		Mockito.verify(sharePortfolio).commit();
		Mockito.verify(sharePortfolioRepository).save(sharePortfolio);
	}
	
	@Test
	public final void committedPortfolioAlredyCommitted() {
		Mockito.when(sharePortfolioRepository.portfolio(NAME)).thenReturn(sharePortfolio);
		Mockito.when(sharePortfolio.isCommitted()).thenReturn(true);
		
		Assert.assertEquals(sharePortfolio, sharePortfolioService.committedPortfolio(NAME));
		
		Mockito.verify(sharePortfolio, Mockito.never()).commit();
		Mockito.verify(sharePortfolioRepository, Mockito.never()).save(sharePortfolio);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public final void samples() {
		
		final List<TimeCourse> timeCourses = Mockito.mock(List.class);
		Mockito.when(timeCourses.size()).thenReturn(10);
		Mockito.when(sharePortfolio.timeCourses()).thenReturn(timeCourses);
		final Collection<double[]> results = sharePortfolioService.samples(sharePortfolio, SAMPLES_SIZE);
		Assert.assertEquals(100, results.size());
		results.stream().forEach(samples -> Assert.assertTrue(Math.abs( 1 -((Collection<Double>) CollectionUtils.arrayToList(samples)).stream().reduce((a,b) -> a+b).get())< 1e-15));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public final void samplesWrongSize(){
		 sharePortfolioService.samples(sharePortfolio, 0);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public final void samplesWrongSizeTimeCourses(){
		 sharePortfolioService.samples(sharePortfolio,SAMPLES_SIZE);
	}

	
	@Test
	public final void variance() {
		Mockito.when(sharePortfolio.name()).thenReturn(NAME);
		final double[] weightingVector = new double[] {0.5,0.5};
		Mockito.when(sharePortfolio.risk(weightingVector)).thenReturn(VARIANCE);
		PortfolioOptimisation result = sharePortfolioService.variance(sharePortfolio, weightingVector);
		Assert.assertEquals(VARIANCE, result.variance());
		Assert.assertEquals(1L, (long) result.samples());
		Assert.assertEquals(weightingVector, result.weights());
		Assert.assertEquals(NAME, result.portfolio());
	}
}
