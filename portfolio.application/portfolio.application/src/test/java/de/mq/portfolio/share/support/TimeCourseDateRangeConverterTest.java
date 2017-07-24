package de.mq.portfolio.share.support;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import de.mq.portfolio.share.Data;
import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;


public class TimeCourseDateRangeConverterTest {
	
	
	private final TimeCourseConverter timeCourseConverter = new TimeCourseDateRangeConverterImpl();
	
	private final Share share = Mockito.mock(Share.class);
	
	private final Data todayRate = new DataImpl(new Date(), 111.0d);
	private final Data yesterdayRate = new DataImpl(daysBack(HistoryRepository.OFFSET_DAYS_ONE_DAY_BACK), 111.1d);
	private final Data oneYearBackRate = new DataImpl(daysBack(HistoryRepository.OFFSET_DAYS_ONE_YEAR_BACK), 111.3d);
	private final Data moreThanOneYearRate = new DataImpl(daysBack(HistoryRepository.OFFSET_DAYS_ONE_YEAR_BACK+1), 111.4d);
	
	
	private final Data todayDividend = new DataImpl(new Date(), 1.0d);
	private final Data yesterdayDividend = new DataImpl(daysBack(HistoryRepository.OFFSET_DAYS_ONE_DAY_BACK), 1.1d);

	private final Data oneYearBackDividend = new DataImpl(daysBack(HistoryRepository.OFFSET_DAYS_ONE_YEAR_BACK), 1.2d);
	private final Data moreThanOneYearDividend = new DataImpl(daysBack(HistoryRepository.OFFSET_DAYS_ONE_YEAR_BACK+1), 1.3d);
	
	private final TimeCourse timeCourse = new TimeCourseImpl(share, Arrays.asList(moreThanOneYearRate,oneYearBackRate,yesterdayRate, todayRate ), Arrays.asList(moreThanOneYearDividend,oneYearBackDividend,yesterdayDividend,todayDividend ));
	private Date daysBack(final int days) {
		final Calendar calendar = new GregorianCalendar();
		calendar.add(Calendar.DATE, -days);
		return calendar.getTime();
	}
	
	
	
	@Test
	public final void convert() {
		final TimeCourse result = timeCourseConverter.convert(timeCourse);
		
		Assert.assertEquals(share, result.share());
		
		Assert.assertEquals(2, result.rates().size());
		Assert.assertEquals(oneYearBackRate,  result.rates().get(0));
		Assert.assertEquals(yesterdayRate,  result.rates().get(1));
		
		Assert.assertEquals(2, result.dividends().size());
		
		Assert.assertEquals(oneYearBackDividend,  result.dividends().get(0));
		Assert.assertEquals(yesterdayDividend,  result.dividends().get(1));
	}
	
	@Test
	public final void  timeCourseConverterType() {
		Assert.assertEquals(TimeCourseConverter.TimeCourseConverterType.DateInRange, timeCourseConverter.timeCourseConverterType());
	}

}
