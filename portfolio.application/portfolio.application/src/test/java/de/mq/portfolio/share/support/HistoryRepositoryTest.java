package de.mq.portfolio.share.support;

import java.text.ParseException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestOperations;

import de.mq.portfolio.share.Data;
import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;

public class HistoryRepositoryTest {
	
	private static final Double DIVIDEND = 1.52;
	private static final String DATE = "1968-05-28";
	private static final Integer RATE = 9500;
	private static final String CODE = "^IDAXI";
	private final RestOperations restOperations = Mockito.mock(RestOperations.class);
	private final Share share = Mockito.mock(Share.class);
	private final HistoryRepository historyRepository = new HistoryRestRepositoryImpl(restOperations); 
	
	@Before
	public final void setup() {
		Mockito.when(share.code()).thenReturn(CODE);
	
		Mockito.doAnswer(i -> {
			Assert.assertEquals(String.class, (i.getArguments()[1]));
			final String url = (String) i.getArguments()[0];
			if( url.endsWith("&g=v")) {
				return String.format("%s,%s\n,xxx,xxx", DATE, DIVIDEND);
			} else {
				return String.format("%s,xxx,xxx,xxx,%s\n ", DATE, RATE); 
			}
			
		}).when(restOperations).getForObject(Mockito.anyString(), Mockito.any());
		
	}
	
	@Test
	public final void history() throws ParseException {
		final TimeCourse timeCourse = historyRepository.history(share);
		Assert.assertEquals(1, timeCourse.rates().size());
		Assert.assertEquals(1, timeCourse.dividends().size());
		Assert.assertTrue(timeCourse.rates().stream().findAny().isPresent());
		Assert.assertTrue(timeCourse.dividends().stream().findAny().isPresent());
		final Data rate = timeCourse.rates().stream().findAny().get();
		Assert.assertEquals(RATE.doubleValue(), rate.value());
		Assert.assertEquals( ((HistoryRestRepositoryImpl)historyRepository).df.parseObject(DATE), rate.date());
		
		final Data dividend = timeCourse.dividends().stream().findAny().get();
		Assert.assertEquals(DIVIDEND.doubleValue(), dividend.value());
		Assert.assertEquals( ((HistoryRestRepositoryImpl)historyRepository).df.parseObject(DATE), dividend.date());
	}
}
