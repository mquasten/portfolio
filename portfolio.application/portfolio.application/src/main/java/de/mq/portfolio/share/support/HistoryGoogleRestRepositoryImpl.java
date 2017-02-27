package de.mq.portfolio.share.support;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestOperations;

import de.mq.portfolio.share.Data;
import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;

@Repository()
@Profile("google" )
class HistoryGoogleRestRepositoryImpl implements HistoryRepository {
	
	private final String url = "http://www.google.com/finance/historical?q=%s&output=csv&startdate=%s";
	private final RestOperations restOperations;
	
	private final DateFormat dateFormat = new SimpleDateFormat("d-MMM-yy", Locale.US );
	
	private final int periodeInDays = 365;
	
	

	@Autowired
	HistoryGoogleRestRepositoryImpl(final RestOperations restOperations) {
		this.restOperations = restOperations;
	}

	@Override
	public TimeCourse history(final Share share) {
	
		Assert.notNull(share, "Share is mandatory.");
	
		
		if( ! StringUtils.hasText(share.code2())) {
			return new TimeCourseImpl(share, new ArrayList<>(), new ArrayList<>() );
		}
		
		
		
		System.out.println(String.format(url, share.code2(), startDate()));
		final String result =restOperations.getForObject(String.format(url, share.code2(), startDate()), String.class);
		
		final List<Data> rates = Arrays.asList(result.split("[\n]")).stream().map(line -> line.split("[,]")).filter(cols -> cols.length >= 5 ).filter(cols -> isDate(cols[0]) ).map(cols -> toData(cols)).collect(Collectors.toList());
		rates.sort((d1, d2) -> Long.valueOf(d1.date().getTime() - d2.date().getTime()).intValue());
		
		return new TimeCourseImpl(share, rates, Arrays.asList());
	}

	private String startDate() {
		return dateFormat.format(Date.from(LocalDate.now().minusDays(periodeInDays).atStartOfDay(ZoneId.systemDefault()).toInstant()));
	} 

	
	private boolean isDate(final String dateAsString) {
	try {
		dateFormat.parse(dateAsString);
		return true;
		} catch (final ParseException ex) {
			return false;
		}
	}
	
	private Data toData(final String[] cols ) {
		try {
			return new DataImpl(dateFormat.parse(cols[0]), Double.parseDouble(cols[4]));
		} catch (final Exception e) {
			throw new IllegalArgumentException(e);
		}
		
	}
	
	
}
