package de.mq.portfolio.share.support;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestOperations;

import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;


public class HistoryGoogleRestRepositoryTest {
	
	private static final String LINES_PATTERN = "Date,Open,High,Low,Close,Volume\n%s,x,x,x,%s,x\n28-Mai-68, Kylies Bithday\n%s,x,x,x,%s,x";

	private static final double CURRENT_RATE = 100.00d;

	private static final double START_RATE = 47.11;

	private static final DateFormat dateFormat = new SimpleDateFormat( "d-MMM-yy", Locale.US);

	private static final int DAYS_OFFSET = 365;

	private static final String CODE2 = "NYSE:JNJ";

	private static final String URL_PATTERN = "http://www.google.com/finance/historical?q=%s&output=csv&startdate=%s";
	
	private final RestOperations restOperations = Mockito.mock(RestOperations.class);
	
	private final HistoryRepository historyRepository = new HistoryGoogleRestRepositoryImpl(restOperations); 
	
	private final Share share = Mockito.mock(Share.class);
	
	private final String startDate= dateFormat.format(startDate());
	private final String now = dateFormat.format(new Date());
	
	private final String lines = String.format(LINES_PATTERN, now, CURRENT_RATE, startDate, START_RATE );
	
	private final String url = String.format(URL_PATTERN, CODE2, startDate);
	
	@Before
	public final void setup() {
		Mockito.when(share.code2()).thenReturn(CODE2);
	
		Mockito.when(restOperations.getForObject( url , String.class)).thenReturn(lines);
	}
	
	@Test
	public final void history() throws ParseException {
		
		final TimeCourse timeCourse = historyRepository.history(share);
		
		Assert.assertEquals(2, timeCourse.rates().size());
		Assert.assertTrue(timeCourse.dividends().isEmpty());
		Assert.assertEquals(share, timeCourse.share());
		
		Assert.assertEquals(dateFormat.parse(startDate), timeCourse.rates().get(0).date());
		Assert.assertEquals((Double)START_RATE, (Double) timeCourse.rates().get(0).value());
		
		Assert.assertEquals(dateFormat.parse(now), timeCourse.rates().get(1).date());
		Assert.assertEquals((Double) CURRENT_RATE, (Double) timeCourse.rates().get(1).value());
		
		Mockito.verify(restOperations).getForObject(url, String.class );
		
		

	}
	
	@Test
	public final void historyCode2Missing() throws ParseException {
		Mockito.when(share.code2()).thenReturn(null);
		
		final TimeCourse timeCourse = historyRepository.history(share);
		
		Assert.assertTrue(timeCourse.rates().isEmpty());
		Assert.assertTrue(timeCourse.dividends().isEmpty());
		Assert.assertEquals(share, timeCourse.share());
		
		Mockito.verify(restOperations, Mockito.never()).getForObject(url, String.class );
		
	}
	
	
	
	@Test(expected=IllegalArgumentException.class)
	public final void historyInvalidData()  {
		Mockito.when(restOperations.getForObject( url , String.class)).thenReturn(String.format(LINES_PATTERN,  now, CURRENT_RATE, startDate, "x.x" ));
		
		historyRepository.history(share);
	}

	private Date startDate() {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		cal.add(Calendar.DAY_OF_YEAR, -DAYS_OFFSET);
		return cal.getTime();
	}

}
