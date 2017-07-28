package de.mq.portfolio.share.support;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

import org.springframework.stereotype.Component;

@Component
public final class HistoryDateUtil {
	
	static final String GERMAN_YEAR_TO_DAY_DATE_FORMAT = "yyyy-MM-dd";
	static final String GOOGLE_DATE_FORMAT = "d-MMM-yy";
	static final int OFFSET_DAYS_ONE_DAY_BACK = 1;
	static final int OFFSET_DAYS_ONE_YEAR_BACK =365;
	

	private final DateFormat googleDateFormat = new SimpleDateFormat(GOOGLE_DATE_FORMAT , Locale.US);
	
	private final SimpleDateFormat germanYearToDayDateFormat = new SimpleDateFormat(GERMAN_YEAR_TO_DAY_DATE_FORMAT, Locale.GERMANY);
	
	public  final Date getOneYearBack() {
		return dateDaysBack(OFFSET_DAYS_ONE_YEAR_BACK);
	}
	
	public  final Date getOneDayBack() {
		return dateDaysBack(OFFSET_DAYS_ONE_DAY_BACK);
	}
	
	public final Date dateDaysBack(final long daysBack) {
		return Date.from(LocalDate.now().minusDays(daysBack).atStartOfDay(ZoneId.systemDefault()).toInstant());
	} 
	
	public final String oneYearBack(final DateFormat dateFormat) {	
		return dateFormat.format(getOneYearBack());
	}
	
	public final String oneDayBack(final DateFormat dateFormat) {	
		return dateFormat.format(getOneDayBack());
	}
	
	public final DateFormat getGoogleDateFormat() {
		return googleDateFormat;
	}
	
	public final DateFormat getGermanYearToDayDateFormat() {
		return  germanYearToDayDateFormat;
	}

}
