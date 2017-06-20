package de.mq.portfolio.share.support;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.support.ConfigurableConversionService;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import org.springframework.web.client.RestOperations;

import de.mq.portfolio.share.Data;
import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.support.ExceptionTranslationBuilder;


@Repository()
@Profile("ariva" )
abstract
class HistoryArivaRestRepositoryImpl implements HistoryRepository {

	private static final String DELIMITER = "|";
	private final DateFormat dateFormat ;
	private final int periodeInDays = 365;
	
	private final RestOperations restOperations;
	private final String url; 
	@Autowired
	HistoryArivaRestRepositoryImpl(final RestOperations restOperations, @Value("${history.ariva.url}" ) final String url,  @Value("${history.ariva.dateformat?:yyyy-MM-dd}" ) final String dateFormat) {
		this.restOperations = restOperations;
		this.url=url;
		this.dateFormat=new SimpleDateFormat(dateFormat);
	}

	@Override
	public TimeCourse history(Share share) {
		
		
		final LocalDate date = LocalDate.now();
		Map<String,Object> params = new HashMap<>();
		params.put("shareId",  412L);
		params.put("stockExchangeId",  21L);
		params.put("startDate",  dateString(date, periodeInDays));
		params.put("endDate",  dateString(date, 1));
		params.put("delimiter", DELIMITER );
		final ResponseEntity<String> responseEntity = restOperations.getForEntity(url, String.class, params);
		
		Map<String,String> headers = responseEntity.getHeaders().toSingleValueMap();
	
		final String attachement = headers.get("Content-Disposition");
		Assert.hasText(attachement, "Content-Disposition should not  empty");
	    final String[] cols = attachement.split("[_]");
	    Assert.isTrue(cols.length == 3 , " Wrong Content-Disposition Header");
	    System.out.println(cols[1]);
	
	    System.out.println(attachement);
		return new TimeCourseImpl(share, exceptionTranslationBuilderResult().withResource( () ->  {
			//478160104
					
			return new BufferedReader(new StringReader(responseEntity.getBody()));
		}).withTranslation(IllegalStateException.class, Arrays.asList(IOException.class)).withStatement(bufferedReader -> {return  read(bufferedReader);}).translate(), Arrays.asList());
	}
	
	
	private List<Data> read(final BufferedReader bufferedReader) throws IOException, ParseException {
		final ConfigurableConversionService configurableConversionService = configurableConversionService();
		configurableConversionService.addConverter(String.class, Date.class, dateString -> exceptionTranslationBuilderConversionService().withStatement(() ->  dateFormat.parse(dateString) ).translate());
		
		final List<Data> results = new ArrayList<>();
		for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine() ) {
			
			
			if( line.startsWith("Datum")) {
				
				continue;
			}
			
			
			final String[] cols =line.split(String.format("[%s]",DELIMITER));
			if(cols.length  != 7) {
				continue;
			}
			
			//System.out.println(line);
			results.add(new DataImpl(configurableConversionService.convert(cols[0], Date.class), configurableConversionService.convert(cols[4].replace(',', '.'), Double.class)));
			
		}
		return results;
		
		
	}
	private String dateString(final LocalDate date, final long daysBack) {
		return dateFormat.format(Date.from(date.minusDays(daysBack).atStartOfDay(ZoneId.systemDefault()).toInstant()));
	}
	
	@SuppressWarnings("unchecked")
	private ExceptionTranslationBuilder<Date,BufferedReader> exceptionTranslationBuilderConversionService(){
		return (ExceptionTranslationBuilder<Date,BufferedReader>) exceptionTranslationBuilder();
	}
	
	
	@SuppressWarnings("unchecked")
	private ExceptionTranslationBuilder<List<Data>,BufferedReader> exceptionTranslationBuilderResult(){
		return (ExceptionTranslationBuilder<List<Data>,BufferedReader>) exceptionTranslationBuilder();
	}
	
	@Lookup
	abstract ExceptionTranslationBuilder<?,BufferedReader> exceptionTranslationBuilder(); 
	
	@Lookup
	abstract ConfigurableConversionService configurableConversionService();
	
	
	

}
