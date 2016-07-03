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
	private static final String DATE1 = "1980-11-18";
	private static final Integer RATE1 = 9500;
	
	private static final String DATE2 = "19-05-28";
	private static final Integer RATE2 = 17500;
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
				return String.format("%s,%s\n,xxx,xxx", DATE1, DIVIDEND);
			} else {
				return String.format("%s,xxx,xxx,xxx,%s\n%s,xxx,xxx,xxx,%s\n  ", DATE1, RATE1, DATE2, RATE2); 
			}
			
		}).when(restOperations).getForObject(Mockito.anyString(), Mockito.any());
		
	}
	
	@Test
	public final void history() throws ParseException {
		final TimeCourse timeCourse = historyRepository.history(share);
		Assert.assertEquals(2, timeCourse.rates().size());
		Assert.assertEquals(1, timeCourse.dividends().size());
		Assert.assertTrue(timeCourse.rates().stream().findAny().isPresent());
		Assert.assertTrue(timeCourse.dividends().stream().findAny().isPresent());
		final Data rate1= timeCourse.rates().stream().findFirst().get();
		Assert.assertEquals(RATE1.doubleValue(), rate1.value());
		Assert.assertEquals( ((HistoryRestRepositoryImpl)historyRepository).df.parseObject(DATE1), rate1.date());
		
		final Data rate2= timeCourse.rates().get(1);
		Assert.assertEquals(RATE2.doubleValue(), rate2.value());
		Assert.assertEquals( ((HistoryRestRepositoryImpl)historyRepository).df.parseObject(DATE2), rate2.date());
		
		
		final Data dividend = timeCourse.dividends().stream().findAny().get();
		Assert.assertEquals(DIVIDEND.doubleValue(), dividend.value());
		Assert.assertEquals( ((HistoryRestRepositoryImpl)historyRepository).df.parseObject(DATE1), dividend.date());
	}
}
