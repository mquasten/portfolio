package de.mq.portfolio.share;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;



@Service
public class StockpriceServiceImpl {
	
	@Autowired
	private RestOperations restOperations; 
	
	private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	
	private final String url = "http://real-chart.finance.yahoo.com/table.csv?s=%s&a=%s&b=%s&c=%s";

	
	
	

	
	public final TimeCourse history(final Share share ) {
		
		final GregorianCalendar cal = new GregorianCalendar();
		cal.add(Calendar.DATE, -365);
		final int month = cal.get(Calendar.MONTH);
		final int day = cal.get(Calendar.DAY_OF_MONTH);
		final int year = cal.get(Calendar.YEAR);
		
	
		
		
		final String requestUrl = String.format(url, share.code(), month, day, year);
	
		
	
		

		final Collection<Data> rates = getValues(requestUrl, 4);
	
		final Collection<Data> dividends = getValues(requestUrl + "&g=v", 1);
	 
	
		return  new TimeCourseImpl(share, rates, dividends );
	}


	private Collection<Data> getValues(final String requestUrl, final int colIndex) {
		return Arrays.asList(restOperations.getForObject(requestUrl, String.class).split("\n")).stream().map(line->line.split(",")).filter(cols -> cols.length >= colIndex+1).filter(cols -> isDate(df, cols[0])).map(cols -> new DataImpl(cols[0], Double.parseDouble(cols[colIndex]))).collect(Collectors.toList());
	}


	private boolean  isDate(final SimpleDateFormat df, final String key) {
		try {
			
			df.parse(key);
			return true;
		} catch (final ParseException ex) {
			return false;
		}
	}
	
	
}
