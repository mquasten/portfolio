package de.mq.portfolio.share.support;

import java.util.ArrayList;
import java.util.Collection;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mockito;

import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.ShareService;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.share.support.HistoryRepository;
import de.mq.portfolio.share.support.ShareRepository;

public class ShareServiceTest {

	HistoryRepository historyRepository = Mockito.mock(HistoryRepository.class);
	ShareRepository shareRepository = Mockito.mock(ShareRepository.class);

	private final ShareService shareService = new ShareServiceImpl(historyRepository, shareRepository);

	private final Share share = Mockito.mock(Share.class);

	private final TimeCourse timeCourse = Mockito.mock(TimeCourse.class);

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

}
