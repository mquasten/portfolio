package de.mq.portfolio.share.support;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.springframework.web.client.RestOperations;

import de.mq.portfolio.share.Data;
import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;

@Repository("historyGoogleRestRepository")
@Profile("googleHistoryRepository" )
class HistoryGoogleRestRepositoryImpl implements HistoryRepository {
	
	private  final String url = "http://www.google.com/finance/historical?q=%s&output=csv&startdate=%s";
	private final RestOperations restOperations;
	
	private DateFormat dateFormat = new SimpleDateFormat("d-MMM-yy", Locale.US );
	
	private final int periodeInDays = 365;
	
	enum Index {
		Dax("ETR" ),Dow("NYSE");
		private final String stockExchange;
		
		Index(final String stockExchange) {
			this.stockExchange=stockExchange;
		}
		static Index from(final String text ){
			if( text.toLowerCase().startsWith("dow")){
				return Dow;
			};
			if( text.toLowerCase().startsWith("deutscher")){
				return Dax;
			};
			throw new IllegalArgumentException(String.format("Index not found for: '%s'", text));
			
		}
		
		final String defaultStockExchange() {
			return stockExchange;
		}
	}

	@Autowired
	HistoryGoogleRestRepositoryImpl(final RestOperations restOperations) {
		this.restOperations = restOperations;
	}

	@Override
	public TimeCourse history(final Share share) {
		Assert.notNull(share, "Share is mandatory.");
	
		Assert.notNull(share.code(), "ShareCode is mandatory");
		
		final Index index = Index.from(share.index());
		
		final String name = index.defaultStockExchange() + ":" + share.code().replaceFirst("[.].*$", "");
		
		final String result =restOperations.getForObject(String.format(url, name, startDate()), String.class);
		
		final List<Data> rates = Arrays.asList(result.split("[\n]")).stream().map(line -> line.split("[,]")).filter(cols -> cols.length >= 5 ).filter(cols -> isDate(cols[0]) ).map(cols -> toData(cols)).collect(Collectors.toList());
		rates.sort((d1, d2) -> Long.valueOf(d1.date().getTime() - d2.date().getTime()).intValue());
		
		return new TimeCourseImpl(share, rates, Arrays.asList());
	}

	private String startDate() {
		String result =  dateFormat.format(Date.from(LocalDate.now().minusDays(periodeInDays).atStartOfDay(ZoneId.systemDefault()).toInstant()));
		System.out.println(result);
		return result;
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
