package de.mq.portfolio.share.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import de.mq.portfolio.share.Data;
import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.ShareService;
import de.mq.portfolio.share.TimeCourse;


public class ShareServiceTest {

	private static final Number PAGE_SIZE = 50;
	HistoryRepository historyRepository = Mockito.mock(HistoryRepository.class);
	ShareRepository shareRepository = Mockito.mock(ShareRepository.class);
	
	private final RealTimeRateRepository realTimeRateRestRepository = Mockito.mock(RealTimeRateRepository.class);

	private final ShareService shareService = new ShareServiceImpl(historyRepository, shareRepository, realTimeRateRestRepository);

	private final Share share = Mockito.mock(Share.class);

	private final TimeCourse timeCourse = Mockito.mock(TimeCourse.class);
	
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
		
	}

	@Test
	public void timeCourse() {
		Mockito.when(historyRepository.history(share)).thenReturn(timeCourse);

		Assert.assertEquals(timeCourse, shareService.timeCourse(share));
		
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
		Mockito.verify(timeCourse, Mockito.never()).assign(Mockito.any());
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
	
	@SuppressWarnings("unchecked")
	@Test
	@Ignore
	public final void realTimeCourses() {
		
		final ArgumentCaptor<Collection<Share>> sharesCaptor = ArgumentCaptor.forClass(  (Class<Collection<Share>>)(Class<?>)Collection.class);
		Mockito.when(timeCourse.share()).thenReturn(share);
		Mockito.when(realTimeRateRestRepository.rates(Arrays.asList(share))).thenReturn(Arrays.asList(timeCourse));
		Mockito.when(shareRepository.timeCourses(Arrays.asList(CODE))).thenReturn(timeCourses);
		
		Assert.assertEquals(timeCourses, shareService.realTimeCourses(Arrays.asList(CODE), false));
		Mockito.verify(realTimeRateRestRepository).rates(sharesCaptor.capture());
		
		Assert.assertEquals(1, sharesCaptor.getValue().size());
		Assert.assertEquals(share, sharesCaptor.getValue().stream().findAny().get());
	}
}
