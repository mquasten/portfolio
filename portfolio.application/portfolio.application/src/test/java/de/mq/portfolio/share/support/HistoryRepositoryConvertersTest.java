package de.mq.portfolio.share.support;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import de.mq.portfolio.share.Share;


public class HistoryRepositoryConvertersTest {
	
	private final HistoryRepository historyRepository = Mockito.mock(HistoryRepository.class, Mockito.CALLS_REAL_METHODS);
	
	private final Share share = Mockito.mock(Share.class);
	
	@Test
	public final void converters() {
		Assert.assertEquals(Arrays.asList(TimeCourseConverter.TimeCourseConverterType.DateInRange), historyRepository.converters(share));	
	}

}
