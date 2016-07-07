package de.mq.portfolio.shareportfolio.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.CollectionUtils;

import de.mq.portfolio.exchangerate.support.ExchangeRateService;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.share.support.ShareRepository;
import de.mq.portfolio.shareportfolio.PortfolioOptimisation;
import de.mq.portfolio.shareportfolio.SharePortfolio;

public class SharePortfolioServiceTest {
	
	private static final String ID = "19680528";
	private static final long NEXT_COUNTER = 2L;
	private static final String STATUS_STPPED = "STOPPED";
	private static final Long LIMIT = 10L;
	private static final Long COUNTER = 1L;
	private static final double VARIANCE = 1e-3;
	private static final int SAMPLES_SIZE = 100;
	private static final String NAME = "mq-test";
	private final SharePortfolioRepository sharePortfolioRepository = Mockito.mock(SharePortfolioRepository.class);
	private final ShareRepository shareRepository = Mockito.mock(ShareRepository.class);
	private final ExchangeRateService exchangeRateService = Mockito.mock(ExchangeRateService.class);
	private final  SharePortfolioService sharePortfolioService = new SharePortfolioServiceImpl(sharePortfolioRepository, shareRepository, exchangeRateService);
	
	private final Collection<PortfolioOptimisation> portfolioOptimisations = new ArrayList<>();
	private final SharePortfolio sharePortfolio = Mockito.mock(SharePortfolio.class);
	
	private final Pageable pageable = Mockito.mock(Pageable.class);
	
	private final  Sort sort = Mockito.mock(Sort.class);
	
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
	
	@Test
	public final void create() {
		final PortfolioOptimisation portfolioOptimisation = Mockito.mock(PortfolioOptimisation.class);
		sharePortfolioService.create(portfolioOptimisation);
		Mockito.verify(sharePortfolioRepository).save(portfolioOptimisation);
	}
	
	
	@Test
	public final void save() {
		sharePortfolioService.save(sharePortfolio);
		Mockito.verify(sharePortfolioRepository).save(sharePortfolio);
	}
	
	@Test
	public final void assign() {
		final SharePortfolio sharePortfolio = BeanUtils.instantiateClass(SharePortfolioImpl.class);
		Mockito.when(sharePortfolioRepository.portfolio(NAME)).thenReturn(sharePortfolio);
		final PortfolioOptimisation portfolioOptimisation = Mockito.mock(PortfolioOptimisation.class);
		Mockito.when(portfolioOptimisation.portfolio()).thenReturn(NAME);
		Assert.assertFalse(sharePortfolio.minVariance().isPresent());
		Assert.assertEquals(sharePortfolio, sharePortfolioService.assign(portfolioOptimisation));
		
		Assert.assertTrue(sharePortfolio.minVariance().isPresent());
		Assert.assertEquals(portfolioOptimisation, sharePortfolio.minVariance().get());
	}
	@Test
	public final void  minVariance() {
		final PortfolioOptimisation portfolioOptimisation  = Mockito.mock(PortfolioOptimisation.class);
		Mockito.when(sharePortfolioRepository.minVariance(NAME)).thenReturn(Optional.of(portfolioOptimisation));
		
		Assert.assertEquals(portfolioOptimisation, sharePortfolioService.minVariance(NAME));
	}
	@Test(expected=IllegalArgumentException.class)
	public final void  minVarianceNotFound() {
		Mockito.when(sharePortfolioRepository.minVariance(NAME)).thenReturn(Optional.empty());
		sharePortfolioService.minVariance(NAME);
	}
	
	@Test
	public final void  status() {
		Assert.assertEquals(SharePortfolioServiceImpl.STATUS_CONTINUE, (((SharePortfolioServiceImpl)  sharePortfolioService).status(SharePortfolioServiceImpl.STATUS_COMPLETED, COUNTER, LIMIT)));
		Assert.assertEquals(SharePortfolioServiceImpl.STATUS_COMPLETED, (((SharePortfolioServiceImpl)  sharePortfolioService).status(SharePortfolioServiceImpl.STATUS_COMPLETED,LIMIT, LIMIT)));
		Assert.assertEquals(STATUS_STPPED, (((SharePortfolioServiceImpl)  sharePortfolioService).status(STATUS_STPPED, COUNTER, LIMIT)));
		Assert.assertEquals(SharePortfolioServiceImpl.STATUS_COMPLETED, (((SharePortfolioServiceImpl)  sharePortfolioService).status(SharePortfolioServiceImpl.STATUS_COMPLETED,COUNTER, null)));
	}
	
	@Test
	public final void  incCounter() {
		 Assert.assertEquals(NEXT_COUNTER, (long) ((SharePortfolioServiceImpl)  sharePortfolioService).incCounter(COUNTER));
		 Assert.assertEquals(COUNTER, ((SharePortfolioServiceImpl)  sharePortfolioService).incCounter(null));
	}
	
	@Test
	public final void  sharePortfolio() {
		Mockito.when(sharePortfolioRepository.sharePortfolio(ID)).thenReturn(sharePortfolio);
		Assert.assertEquals(sharePortfolio, sharePortfolioService.sharePortfolio(ID));
		Mockito.verify(sharePortfolioRepository).sharePortfolio(ID);
	}
	
	@Test
	public final void  portfolios() {
		Mockito.when(sharePortfolioRepository.portfolios(pageable, sharePortfolio)).thenReturn(Arrays.asList(sharePortfolio));
		Assert.assertEquals(Arrays.asList(sharePortfolio), sharePortfolioService.portfolios(pageable, sharePortfolio));
		Mockito.verify(sharePortfolioRepository).portfolios(pageable, sharePortfolio);
	}
	
	@Test
	public final void pageable(){
		Mockito.when(sharePortfolioRepository.pageable(sharePortfolio, sort, LIMIT)).thenReturn(pageable);
		Assert.assertEquals(pageable, sharePortfolioService.pageable(sharePortfolio, sort, LIMIT));
		Mockito.verify(sharePortfolioRepository).pageable(sharePortfolio, sort, LIMIT);
	}
}
