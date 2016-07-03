package de.mq.portfolio.share.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.ShareService;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.share.support.HistoryRepository;
import de.mq.portfolio.share.support.ShareRepository;

public class ShareServiceTest {

	private static final Number PAGE_SIZE = 50;
	HistoryRepository historyRepository = Mockito.mock(HistoryRepository.class);
	ShareRepository shareRepository = Mockito.mock(ShareRepository.class);

	private final ShareService shareService = new ShareServiceImpl(historyRepository, shareRepository);

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
	public void replacetTimeCourse() {
		Mockito.when(timeCourse.share()).thenReturn(share);
		shareService.replacetTimeCourse(timeCourse);

		Mockito.verify(shareRepository).deleteTimeCourse(share);
		Mockito.verify(shareRepository).save(timeCourse);
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
	public void timeCoursebyCode() {
		Optional<TimeCourse> result = shareService.timeCourse(CODE);
		Assert.assertTrue(result.isPresent());
		Assert.assertEquals(timeCourse, result.get());
		Mockito.verify(shareRepository, Mockito.times(1)).timeCourses(Arrays.asList(CODE));
	}
}
