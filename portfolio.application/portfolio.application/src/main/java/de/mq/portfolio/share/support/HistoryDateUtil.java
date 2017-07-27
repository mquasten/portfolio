package de.mq.portfolio.share.support;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

import org.springframework.stereotype.Component;

@Component
final class HistoryDateUtil {
	
	static final int OFFSET_DAYS_ONE_DAY_BACK = 1;
	static final int OFFSET_DAYS_ONE_YEAR_BACK =365;
	

	private final DateFormat googleDateFormat = new SimpleDateFormat("d-MMM-yy" , Locale.US);
	
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
	
	public final DateFormat getGoogleDateFormat() {
		return googleDateFormat;
	}

}
