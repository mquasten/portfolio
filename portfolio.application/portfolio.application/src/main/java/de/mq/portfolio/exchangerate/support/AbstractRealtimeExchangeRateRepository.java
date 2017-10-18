package de.mq.portfolio.exchangerate.support;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestOperations;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.share.Data;
import de.mq.portfolio.share.support.DataImpl;
import de.mq.portfolio.support.ExceptionTranslationBuilder;

@Repository
public abstract class AbstractRealtimeExchangeRateRepository implements RealtimeExchangeRateRepository {
	
	private  final DateFormat dateFormat;
	private final String url; 
	private final RestOperations restOperations;
		
	@Autowired
	AbstractRealtimeExchangeRateRepository(final RestOperations restOperations, @Value("${realtime.exchangerates.url}" )final String url, @Value("${realtime.exchangerates.dateformat}") final String dateFormat) {
		this.restOperations = restOperations;
		this.url=url;
		this.dateFormat=new SimpleDateFormat(dateFormat);
		
	}
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.exchangerate.support.RealtimeExchangeRateRepository#exchangeRates(java.util.Collection)
	 */
	
	@Override
	public final List<ExchangeRate> exchangeRates(final Collection<ExchangeRate> rates) {
		final String queryString = rates.stream().map(exchangeRate ->  exchangeRate.source() + exchangeRate.target() + "=X").reduce( "",  ( a,b) -> !StringUtils.isEmpty(a)? a+", "+ b :b );
		System.out.println("---------------------------");
		System.out.println(url);
		System.out.println(queryString);
		System.out.println("---------------------------");
		
		return    exceptionTranslationBuilderResult().withResource( () ->  new BufferedReader(new StringReader(restOperations.getForObject(url, String.class, queryString)))).withTranslation(IllegalStateException.class, Arrays.asList(IOException.class)).withStatement(bufferedReader -> {return  read(bufferedReader);}).translate();	
	}
	private List<ExchangeRate> read(BufferedReader bufferedReader) throws IOException, ParseException {
	
		final ConfigurableConversionService configurableConversionService =configurableConversionService();
		
		configurableConversionService.addConverter(String.class, Date.class, dateString -> exceptionTranslationBuilderConversionService().withStatement(() ->  dateFormat.parse(dateString) ).translate());
		
		final List<ExchangeRate> results = new ArrayList<>();
		for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine() ) {	
			
			final String[] cols = line.replaceAll("[\"]", "").replaceAll("=X", "").split("[,;]");
			if(cols.length < 4){
				continue;
			}
			
			
			final  String dateString = cols[2]+ " " + cols[3];
			Assert.isTrue(cols[0].length()==6, "Invalid currencyCodes: " + cols[0]);
			
			configurableConversionService.convert(dateString, Date.class);
			final ExchangeRate exchangeRate = new ExchangeRateImpl(cols[0].substring(0, 3), cols[0].substring(3));
			
			final Data rate = new DataImpl(dateString,configurableConversionService.convert(cols[1], Double.class));
			Arrays.asList(rate.getClass().getDeclaredFields()).stream().filter(field -> field.getType().equals(DateFormat.class)).forEach(field -> ReflectionUtils.setField(field, rate, dateFormat));
			exchangeRate.assign(Arrays.asList(rate));
			results.add(exchangeRate);
	 
		}
		return results;
	}

	
	
	@SuppressWarnings("unchecked")
	private ExceptionTranslationBuilder<List<ExchangeRate>,BufferedReader> exceptionTranslationBuilderResult(){
		return (ExceptionTranslationBuilder<List<ExchangeRate>,BufferedReader>) exceptionTranslationBuilder();
	}
	
	@SuppressWarnings("unchecked")
	private ExceptionTranslationBuilder<Date,BufferedReader> exceptionTranslationBuilderConversionService(){
		return (ExceptionTranslationBuilder<Date,BufferedReader>) exceptionTranslationBuilder();
	}
	
	@Lookup
	abstract ExceptionTranslationBuilder<?,BufferedReader> exceptionTranslationBuilder(); 
	
	@Lookup
	abstract ConfigurableConversionService configurableConversionService();

	
	

}
