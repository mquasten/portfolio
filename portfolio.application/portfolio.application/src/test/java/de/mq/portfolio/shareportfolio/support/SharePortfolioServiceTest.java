package de.mq.portfolio.shareportfolio.support;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.CollectionUtils;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.exchangerate.ExchangeRateCalculator;
import de.mq.portfolio.exchangerate.support.ExchangeRateService;
import de.mq.portfolio.share.Data;
import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.share.support.ShareRepository;
import de.mq.portfolio.shareportfolio.PortfolioOptimisation;
import de.mq.portfolio.shareportfolio.SharePortfolio;
import junit.framework.Assert;


public class SharePortfolioServiceTest {
	
	private static final String ID = "19680528";
	private static final long NEXT_COUNTER = 2L;
	private static final String STATUS_STPPED = "STOPPED";
	private static final Long LIMIT = 10L;
	private static final Long COUNTER = 1L;
	private static final double VARIANCE = 1e-3;
	private static final int SAMPLES_SIZE = 100;
	private static final String NAME = "mq-test";

	@Mock
	private SharePortfolioRepository sharePortfolioRepository = Mockito.mock(SharePortfolioRepository.class);
	@Mock
	private ShareRepository shareRepository = Mockito.mock(ShareRepository.class);
	@Mock
	private ExchangeRateService exchangeRateService = Mockito.mock(ExchangeRateService.class) ;
	
	
	private SharePortfolioService sharePortfolioService =  new SharePortfolioServiceImpl(sharePortfolioRepository, shareRepository, exchangeRateService );
			
			
			//Mockito.mock(SharePortfolioServiceImpl.class,Mockito.CALLS_REAL_METHODS);
			
		
	
	
	
	
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
	
	
	@Test
	public final void assignTimecourses() {
		final List<TimeCourse> timeCourses = newTimecourses();
		Mockito.when(sharePortfolio.timeCourses()).thenReturn(timeCourses);
		Mockito.when(sharePortfolio.id()).thenReturn(ID);
		Mockito.when(sharePortfolioRepository.sharePortfolio(ID)).thenReturn(sharePortfolio);
		
		
		@SuppressWarnings("unchecked")
		ArgumentCaptor<Collection<String>> codesCaptor = (ArgumentCaptor<Collection<String>>) ArgumentCaptor.forClass( (Class<?>) Collection.class);
		final List<TimeCourse> newTimeCourses = Arrays.asList(Mockito.mock(TimeCourse.class), Mockito.mock(TimeCourse.class));
		Mockito.when(shareRepository.timeCourses(codesCaptor.capture())).thenReturn(newTimeCourses);
		
		
		sharePortfolioService.assign(sharePortfolio, timeCourses);
		
		Mockito.verify(sharePortfolio).assign(newTimeCourses);
		sharePortfolioRepository.save(sharePortfolio);
		Assert.assertEquals(2,codesCaptor.getValue().size());
		
		timeCourses.stream().map(tc -> tc.share().code()).forEach(code -> Assert.assertTrue(codesCaptor.getValue().contains(code)));;
		
	}

	private List<TimeCourse> newTimecourses() {
		final List<TimeCourse> timeCourses = new ArrayList<>();
		IntStream.range(1, 3).forEach(i -> {
			final TimeCourse timeCourse = Mockito.mock(TimeCourse.class);
			final Share share = Mockito.mock(Share.class);	
			Mockito.when(share.code()).thenReturn("code"+i);
			Mockito.when(timeCourse.share()).thenReturn(share);
			final Data data = Mockito.mock(Data.class);
			Mockito.when(data.value()).thenReturn(50d);
			
			timeCourses.add(timeCourse);
		});
		return timeCourses;
	}
	
	@Test
	public final void  delete() {
		Mockito.when(sharePortfolio.id()).thenReturn(ID);
		Mockito.when(sharePortfolioRepository.sharePortfolio(ID)).thenReturn(sharePortfolio);
		sharePortfolioService.delete(ID);
		Mockito.verify(sharePortfolioRepository).delete(sharePortfolio);
		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public final void  deleteCommitted() {
		Mockito.when(sharePortfolio.id()).thenReturn(ID);
		Mockito.when(sharePortfolio.isCommitted()).thenReturn(true);
		Mockito.when(sharePortfolioRepository.sharePortfolio(ID)).thenReturn(sharePortfolio);
		sharePortfolioService.delete(ID);
		
	}
	
	@Test
	public final void  retrospective() {
		
		
	
		final ArgumentCaptor<SharePortfolio> sharePortfolioCaptor = ArgumentCaptor.forClass(SharePortfolio.class);
		final ArgumentCaptor<ExchangeRateCalculator> exchangeRateCalculatorCaptor = ArgumentCaptor.forClass(ExchangeRateCalculator.class);
		
		
		@SuppressWarnings("unchecked")
		final ArgumentCaptor<Collection<TimeCourse>> timeCourseCaptor = (ArgumentCaptor<Collection<TimeCourse>>) ArgumentCaptor.forClass((Class<?>) Collection.class);
 		final SharePortfolioRetrospectiveBuilder builder = Mockito.mock(SharePortfolioRetrospectiveBuilder.class);
		Mockito.when(builder.withCommitedSharePortfolio(sharePortfolioCaptor.capture())).thenReturn(builder);
		Mockito.when(builder.withExchangeRateCalculator(exchangeRateCalculatorCaptor.capture())).thenReturn(builder);
	
		Mockito.when(builder.withTimeCourses(timeCourseCaptor.capture())).thenReturn(builder);
		SharePortfolioRetrospective sharePortfolioRetrospective = Mockito.mock(SharePortfolioRetrospective.class);
		Mockito.when(builder.build()).thenReturn(sharePortfolioRetrospective);
		
		final List<TimeCourse> timeCourses = newTimecourses();
		Mockito.when(sharePortfolio.timeCourses()).thenReturn(timeCourses);
		Mockito.when(sharePortfolio.isCommitted()).thenReturn(true);
		@SuppressWarnings("unchecked")
		ArgumentCaptor<Collection<String>> codesCaptor = (ArgumentCaptor<Collection<String>>) ArgumentCaptor.forClass( (Class<?>) Collection.class);
		final List<TimeCourse> newTimeCourses = Arrays.asList(Mockito.mock(TimeCourse.class), Mockito.mock(TimeCourse.class));
		Mockito.when(shareRepository.timeCourses(codesCaptor.capture())).thenReturn(newTimeCourses);
		
		final Collection<ExchangeRate> exchangeRates = Arrays.asList(Mockito.mock(ExchangeRate.class));
		Mockito.when(sharePortfolio.exchangeRateTranslations()).thenReturn(exchangeRates);
	
	
		final ExchangeRateCalculator exchangeRateCalculator = Mockito.mock(ExchangeRateCalculator.class);
		
		Mockito.when(sharePortfolioRepository.sharePortfolio(ID)).thenReturn(sharePortfolio);
		
		Mockito.when(exchangeRateService.exchangeRateCalculator(exchangeRates)).thenReturn(exchangeRateCalculator);
		
		Mockito.when(exchangeRateCalculator.factor(Mockito.any(ExchangeRate.class), Mockito.any(Date.class))).thenReturn(1d);
		final SharePortfolioService sharePortfolioService = Mockito.spy(this.sharePortfolioService);
	//	Mockito.doAnswer(a ->  builder ).when(sharePortfolioService).newBuilder();
		Mockito.doReturn(builder).when((SharePortfolioServiceImpl)sharePortfolioService).newBuilder();
		
		//Mockito.when(sharePortfolioService.newBuilder()).thenReturn(builder);
		Assert.assertEquals(sharePortfolioRetrospective, sharePortfolioService.retrospective(ID));
		
		Assert.assertEquals(sharePortfolio, sharePortfolioCaptor.getValue());
		Assert.assertEquals(exchangeRateCalculator, exchangeRateCalculatorCaptor.getValue());
		Assert.assertEquals(newTimeCourses, timeCourseCaptor.getValue());
		
	}
	
	@Test
	public final void  newSharePortfolioService()   {
		Assert.assertTrue(((SharePortfolioServiceImpl)sharePortfolioService).newBuilder() instanceof SharePortfolioRetrospectiveBuilder); 
		
	}
	
	
}
