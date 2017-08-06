package de.mq.portfolio.share.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameterAggregation;
import de.mq.portfolio.gateway.ShareGatewayParameterService;
import de.mq.portfolio.share.Data;
import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.ShareService;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.share.support.TimeCourseConverter.TimeCourseConverterType;


public class ShareServiceTest {

	private static final Double DOW_LAST_RATE_DB = 21050D;
	private static final Double DAX_LAST_RATE_DB = 12050D;
	private static final Double DOW_REALTIME_RATE = 21100D;
	private static final Double DOW_LAST_RATE = 21000D;
	private static final Double DAX_REALTIME_RATE = 12100D;
	private static final Double DAX_LAST_RATE = 12000D;
	private static final String DOW = "Dow Jones Industrial Average";
	private static final String OTHER_CODE = "^DJI";
	private static final Number PAGE_SIZE = 50;
	private final HistoryRepository historyRepository = Mockito.mock(HistoryRepository.class);
	private final ShareRepository shareRepository = Mockito.mock(ShareRepository.class);
	
	private final RealTimeRateRepository realTimeRateRestRepository = Mockito.mock(RealTimeRateRepository.class);

	

	private final TimeCourseConverter timeCourseConverter = Mockito.mock(TimeCourseConverter.class);
	private final ShareGatewayParameterService  shareGatewayParameterService = Mockito.mock(ShareGatewayParameterService.class);
	private  ShareService shareService;
	
	@SuppressWarnings("unchecked")
	private final GatewayParameterAggregation<Share> gatewayParameterAggregation = Mockito.mock(GatewayParameterAggregation.class);

	private final Share share = Mockito.mock(Share.class);

	private final TimeCourse timeCourse = Mockito.mock(TimeCourse.class);
	
	private final TimeCourse convertedTimeCourse = Mockito.mock(TimeCourse.class);
	
	private final Collection<String> indexes = new ArrayList<>();
	
	private final Pageable pageable = Mockito.mock(Pageable.class);
	
	private final Collection<TimeCourse> timeCourses = new ArrayList<>();
	private Sort sort = Mockito.mock(Sort.class);
	
	private final static String DAX = "Deutscher Aktien Index"; 
	
	private final static String CODE = "^GDAXI"; 
	
	
	@Before
	public final void setup() {
		indexes.add(DAX);
		Mockito.when(shareRepository.distinctIndex()).thenReturn(indexes);
		timeCourses.add(timeCourse);
		Mockito.when(shareRepository.timeCourses(pageable, share)).thenReturn(timeCourses);
		
		Mockito.when(shareRepository.pageable(share,sort, PAGE_SIZE)).thenReturn(pageable);
		
		Mockito.when(shareRepository.timeCourses(Arrays.asList(CODE))).thenReturn(timeCourses);
		Mockito.when(timeCourseConverter.timeCourseConverterType()).thenReturn(TimeCourseConverterType.DateInRange);
		
		Mockito.when(historyRepository.converters(share)).thenReturn(Arrays.asList(TimeCourseConverterType.DateInRange));
		Mockito.when(timeCourseConverter.convert(timeCourse)).thenReturn(convertedTimeCourse);
		
		Mockito.when(shareGatewayParameterService.aggregationForRequiredGateways(share, Arrays.asList(Gateway.GoogleRateHistory))).thenReturn(gatewayParameterAggregation);
		Mockito.when(historyRepository.supports(share)).thenReturn(Arrays.asList(Gateway.GoogleRateHistory));
		Mockito.when(historyRepository.history(gatewayParameterAggregation)).thenReturn(timeCourse);
		
		shareService = new ShareServiceImpl(historyRepository, shareRepository, realTimeRateRestRepository, shareGatewayParameterService, Arrays.asList(timeCourseConverter));
	}

	@Test
	public void timeCourse() {
		
		Assert.assertEquals(timeCourse, shareService.timeCourse(share));
		
		Mockito.verify(historyRepository).supports(share);
		Mockito.verify(historyRepository).history(gatewayParameterAggregation);
		
		Mockito.verify(timeCourseConverter).convert(timeCourse);
		Mockito.verify(timeCourse).assign(convertedTimeCourse, true);
		Mockito.verify(timeCourse).assign(Arrays.asList(Gateway.GoogleRateHistory));
	}
	
	@Test
	public void timeCourseNothingSupported() {
		Mockito.when(historyRepository.supports(share)).thenReturn(Arrays.asList());
		
		final TimeCourse timeCourse =  shareService.timeCourse(share);
		
		Assert.assertTrue(timeCourse instanceof TimeCourseImpl); 
		Assert.assertEquals(share, timeCourse.share());
		Assert.assertTrue(timeCourse.rates().isEmpty());
		Assert.assertTrue(timeCourse.dividends().isEmpty());
		
		Mockito.verifyNoMoreInteractions(convertedTimeCourse);
		
		Mockito.verify(historyRepository, Mockito.never()).history(gatewayParameterAggregation);
		
	}

	@Test
	public void replaceTimeCourse() {
		TimeCourse newTimeCourse = Mockito.mock(TimeCourse.class);
	
		Mockito.when(newTimeCourse.code()).thenReturn(CODE);
		
		Mockito.when(shareRepository.timeCourses(Arrays.asList(CODE))).thenReturn(Arrays.asList(timeCourse));
		
		shareService.replaceTimeCourse(newTimeCourse);

		Mockito.verify(timeCourse).assign(newTimeCourse);
		Mockito.verify(shareRepository).save(timeCourse);
	}
	
	@Test
	public void replaceTimeCourseNotFound() {
		final TimeCourse newTimeCourse = Mockito.mock(TimeCourse.class);
		Mockito.when(newTimeCourse.code()).thenReturn(CODE);
		Mockito.when(share.code()).thenReturn(CODE);
		Mockito.when(newTimeCourse.share()).thenReturn(share);
	
		final List<Data> rates = Arrays.asList(Mockito.mock(Data.class));
		Mockito.when(newTimeCourse.rates()).thenReturn(rates);
		final List<Data> dividends = Arrays.asList(Mockito.mock(Data.class));
		Mockito.when(newTimeCourse.dividends()).thenReturn(dividends);
		
		Mockito.when(shareRepository.timeCourses(Arrays.asList(CODE))).thenReturn(Arrays.asList());
		
		shareService.replaceTimeCourse(newTimeCourse);

		final ArgumentCaptor<TimeCourse> timeCourseCaptor = ArgumentCaptor.forClass(TimeCourse.class);
		Mockito.verify(timeCourse, Mockito.never()).assign(Mockito.any(TimeCourse.class));
		Mockito.verify(shareRepository).save(timeCourseCaptor.capture());
		Assert.assertEquals(TimeCourseImpl.class, timeCourseCaptor.getValue().getClass());
		Assert.assertEquals(share, timeCourseCaptor.getValue().share());
		
		Assert.assertEquals(rates, timeCourseCaptor.getValue().rates());
		Assert.assertEquals(dividends, timeCourseCaptor.getValue().dividends());
		
	}
	
	

	@Test
	public void shares() {
		final Collection<Share> shares = new ArrayList<>();
		shares.add(share);
		Mockito.when(shareRepository.shares()).thenReturn(shares);

		Assert.assertEquals(shares, shareService.shares());
	}

	@Test
	public void save() {
		shareService.save(share);
		Mockito.verify(shareRepository).save(share);
	}
	
	
	@Test
	public void indexes() {
		Assert.assertEquals(indexes, shareService.indexes());
		Mockito.verify(shareRepository).distinctIndex();
	}
	
	@Test
	public void timeCourses() {
		Assert.assertEquals(timeCourses, shareService.timeCourses(pageable, share));
		Mockito.verify(shareRepository).timeCourses(pageable, share);
	
	}
	
	@Test
	public void pageable() {
		Assert.assertEquals(pageable, shareService.pageable(share, sort, PAGE_SIZE));
		Mockito.verify(shareRepository).pageable(share, sort, PAGE_SIZE);
	}

	
	@Test
	public void timeCourseByCode() {
		Optional<TimeCourse> result = shareService.timeCourse(CODE);
		Assert.assertTrue(result.isPresent());
		Assert.assertEquals(timeCourse, result.get());
		Mockito.verify(shareRepository, Mockito.times(1)).timeCourses(Arrays.asList(CODE));
	}
	

	@Test
	public final void realTimeCourses() {
		final TimeCourse timeCourseDax = newTimeCourseMock(CODE, DAX, "EUR", DAX_LAST_RATE_DB);
		final TimeCourse timeCourseDow = newTimeCourseMock(OTHER_CODE, DOW, "USD", DOW_LAST_RATE_DB);
		Mockito.when(shareRepository.timeCourses(Arrays.asList(CODE, OTHER_CODE))).thenReturn(Arrays.asList(timeCourseDax, timeCourseDow));
		final List<TimeCourse> results = realtimeRates(timeCourseDax, timeCourseDow);
		Mockito.when( realTimeRateRestRepository.rates(Arrays.asList(timeCourseDax.share(), timeCourseDow.share()))).thenReturn(results);
		
		final List<TimeCourse> realTimeCourses = new ArrayList<>(shareService.realTimeCourses(Arrays.asList(CODE, OTHER_CODE), false));
		
		Assert.assertEquals(2, realTimeCourses.size());
		Assert.assertEquals(2, realTimeCourses.get(1).rates().size());
		Assert.assertEquals(2, realTimeCourses.get(0).rates().size());
		Assert.assertEquals(CODE, realTimeCourses.get(0).code());
		Assert.assertEquals(OTHER_CODE, realTimeCourses.get(1).code());
		Assert.assertEquals(DAX_LAST_RATE,  (Double)realTimeCourses.get(0).rates().get(0).value());
		Assert.assertEquals(DAX_REALTIME_RATE,  (Double)realTimeCourses.get(0).rates().get(1).value());
		Assert.assertEquals(DOW_LAST_RATE,  (Double)realTimeCourses.get(1).rates().get(0).value());
		Assert.assertEquals(DOW_REALTIME_RATE,  (Double)realTimeCourses.get(1).rates().get(1).value());
	
	}
	
	@Test
	public final void realTimeCoursesReplaceLastRateWithLastFromDatabase() {
		final TimeCourse timeCourseDax = newTimeCourseMock(CODE, DAX, "EUR", DAX_LAST_RATE_DB);
		final TimeCourse timeCourseDow = newTimeCourseMock(OTHER_CODE, DOW, "USD", DOW_LAST_RATE_DB);
		Mockito.when(shareRepository.timeCourses(Arrays.asList(CODE, OTHER_CODE))).thenReturn(Arrays.asList(timeCourseDax, timeCourseDow));
		final List<TimeCourse> results = realtimeRates(timeCourseDax, timeCourseDow);
		Mockito.when( realTimeRateRestRepository.rates(Arrays.asList(timeCourseDax.share(), timeCourseDow.share()))).thenReturn(results);
		
		final List<TimeCourse> realTimeCourses = new ArrayList<>(shareService.realTimeCourses(Arrays.asList(CODE, OTHER_CODE), true));
		
		Assert.assertEquals(2, realTimeCourses.size());
		Assert.assertEquals(2, realTimeCourses.get(1).rates().size());
		Assert.assertEquals(2, realTimeCourses.get(0).rates().size());
		Assert.assertEquals(CODE, realTimeCourses.get(0).code());
		Assert.assertEquals(OTHER_CODE, realTimeCourses.get(1).code());
		Assert.assertEquals(DAX_LAST_RATE_DB,  (Double)realTimeCourses.get(0).rates().get(0).value());
		Assert.assertEquals(DAX_REALTIME_RATE,  (Double)realTimeCourses.get(0).rates().get(1).value());
		Assert.assertEquals(DOW_LAST_RATE_DB,  (Double)realTimeCourses.get(1).rates().get(0).value());
		Assert.assertEquals(DOW_REALTIME_RATE,  (Double)realTimeCourses.get(1).rates().get(1).value());
	
	}
	
	
	@Test
	public final void realTimeCoursesReplaceLastRateWithLastFromDatabaseNoRateAware() {
		final TimeCourse timeCourseDax = newTimeCourseMock(CODE, DAX, "EUR", DAX_LAST_RATE_DB);
		Mockito.when(timeCourseDax.rates()).thenReturn(Arrays.asList());
		final TimeCourse timeCourseDow = newTimeCourseMock(OTHER_CODE, DOW, "USD", DOW_LAST_RATE_DB);
		Mockito.when(timeCourseDow.rates()).thenReturn(Arrays.asList());
		Mockito.when(shareRepository.timeCourses(Arrays.asList(CODE, OTHER_CODE))).thenReturn(Arrays.asList(timeCourseDax, timeCourseDow));
		final List<TimeCourse> results = realtimeRates(timeCourseDax, timeCourseDow);
		Mockito.when( realTimeRateRestRepository.rates(Arrays.asList(timeCourseDax.share(), timeCourseDow.share()))).thenReturn(results);
		
		final List<TimeCourse> realTimeCourses = new ArrayList<>(shareService.realTimeCourses(Arrays.asList(CODE, OTHER_CODE), true));
		
		Assert.assertEquals(2, realTimeCourses.size());
		Assert.assertEquals(2, realTimeCourses.get(1).rates().size());
		Assert.assertEquals(2, realTimeCourses.get(0).rates().size());
		Assert.assertEquals(CODE, realTimeCourses.get(0).code());
		Assert.assertEquals(OTHER_CODE, realTimeCourses.get(1).code());
		Assert.assertEquals(DAX_LAST_RATE,  (Double)realTimeCourses.get(0).rates().get(0).value());
		Assert.assertEquals(DAX_REALTIME_RATE,  (Double)realTimeCourses.get(0).rates().get(1).value());
		Assert.assertEquals(DOW_LAST_RATE,  (Double)realTimeCourses.get(1).rates().get(0).value());
		Assert.assertEquals(DOW_REALTIME_RATE,  (Double)realTimeCourses.get(1).rates().get(1).value());
	
	}


	private List<TimeCourse> realtimeRates(final TimeCourse timeCourseDax, final TimeCourse timeCourseDow) {
		return Arrays.asList(new TimeCourseImpl(timeCourseDax.share(), Arrays.asList(new DataImpl(new Date(), DAX_LAST_RATE), new DataImpl(new Date(), DAX_REALTIME_RATE)), Arrays.asList()),new TimeCourseImpl(timeCourseDow.share(), Arrays.asList(new DataImpl(new Date(), DOW_LAST_RATE), new DataImpl(new Date(), DOW_REALTIME_RATE)), Arrays.asList()));
	}

	private TimeCourse newTimeCourseMock(final String code, final String name, final String currency, final Double endRate) {
		final TimeCourse result = Mockito.mock(TimeCourse.class);
		final Share share = Mockito.mock(Share.class);
		Mockito.when(share.name()).thenReturn(name);
		Mockito.when(share.code()).thenReturn(code);
		Mockito.when(share.currency()).thenReturn(currency);
		Mockito.when(result.code()).thenReturn(code);
		Mockito.when(result.name()).thenReturn(name);
		Mockito.when(result.share()).thenReturn(share);
		final Data endRateData = Mockito.mock(Data.class) ;
		Mockito.when(endRateData.value()).thenReturn(endRate);
		Mockito.when(result.rates()).thenReturn(Arrays.asList(Mockito.mock(Data.class),endRateData));
		return result;
	}
	
}
