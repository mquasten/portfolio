package de.mq.portfolio.share.support;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;


public class HistoryDateUtilTest {
	
	private final HistoryDateUtil historyDateUtil = new HistoryDateUtil();
	
	
	public  Date dateDaysBack(final int daysBack) {
		final Calendar calendar = new GregorianCalendar();
		calendar.add(Calendar.DAY_OF_YEAR, -daysBack);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND,0);
		return calendar.getTime();
	} 
	
	
	@Test
	public final void oneYearBack() {
		Assert.assertEquals(dateDaysBack(HistoryDateUtil.OFFSET_DAYS_ONE_YEAR_BACK), historyDateUtil.getOneYearBack());
	}

	@Test
	public final void getOneDayBack() {
		Assert.assertEquals(dateDaysBack(HistoryDateUtil.OFFSET_DAYS_ONE_DAY_BACK), historyDateUtil.getOneDayBack());
	}
	
	@Test
	public final void dateDaysBack() {
		final int tenDaysBack = 10;
		Assert.assertEquals(dateDaysBack(tenDaysBack), historyDateUtil.dateDaysBack(tenDaysBack));
	}
	
	@Test
	public final void oneYearBackString() {
		final DateFormat dateFormat = new SimpleDateFormat(HistoryDateUtil.GERMAN_YEAR_TO_DAY_DATE_FORMAT);
		Assert.assertEquals(dateFormat.format(dateDaysBack(HistoryDateUtil.OFFSET_DAYS_ONE_YEAR_BACK)), historyDateUtil.oneYearBack(dateFormat));
	}

	
	@Test
	public final void oneDayBackString() {
		final DateFormat dateFormat = new SimpleDateFormat(HistoryDateUtil.GOOGLE_DATE_FORMAT,Locale.US);
		Assert.assertEquals(dateFormat.format(dateDaysBack(HistoryDateUtil.OFFSET_DAYS_ONE_DAY_BACK)), historyDateUtil.oneDayBack(dateFormat));
	}
	
	@Test
	public final void googleDateFormat() {
		Assert.assertEquals(new SimpleDateFormat(HistoryDateUtil.GOOGLE_DATE_FORMAT, Locale.US), historyDateUtil.getGoogleDateFormat());
	}
	
	@Test
	public final void germanYearToDayDateFormat() {
		Assert.assertEquals(new SimpleDateFormat(HistoryDateUtil.GERMAN_YEAR_TO_DAY_DATE_FORMAT, Locale.GERMANY), historyDateUtil.getGermanYearToDayDateFormat());
	}
}
