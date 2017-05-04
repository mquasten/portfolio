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
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
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
	
	//String URL= "http://query.yahooapis.com/v1/public/yql?q=select id,Rate,Date,Time from yahoo.finance.xchange where pair in ({exchangeRates})&format=json&env=store://datatables.org/alltableswithkeys";
	static final DateFormat DATE_FORMAT = new SimpleDateFormat("M/d/yy h:mma", Locale.US);
	final String URL = "http://finance.yahoo.com/d/quotes.csv?s={currencies}&f=sl1d1t1";
	final String path ="query.results.rate" ;
	private final RestOperations restOperations;
	

	
	@Autowired
	AbstractRealtimeExchangeRateRepository(final RestOperations restOperations) {
		this.restOperations = restOperations;
		
	}
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.exchangerate.support.RealtimeExchangeRateRepository#exchangeRates(java.util.Collection)
	 */
	@Override
	public final Collection<ExchangeRate> exchangeRates(final Collection<ExchangeRate> rates) {
		
		
		final String queryString = rates.stream().map(exchangeRate ->  exchangeRate.source() + exchangeRate.target() + "=X").reduce( "",  ( a,b) -> !StringUtils.isEmpty(a)? a+", "+ b :b );
		return  exceptionTranslationBuilder().withResource( () ->  new BufferedReader(new StringReader(restOperations.getForObject(URL, String.class, queryString)))).withTranslation(IllegalStateException.class, Arrays.asList(IOException.class)).withStatement(bufferedReader -> {return read(bufferedReader);}).translate();	
	}
	private Collection<ExchangeRate> read(BufferedReader bufferedReader) throws IOException, ParseException {
		final Collection<ExchangeRate> results = new ArrayList<>();
		for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine() ) {	
			
			final String[] cols = line.replaceAll("[\"]", "").replaceAll("=X", "").split("[,;]");
			if(cols.length < 4){
				continue;
			}
			
			final  String dateString = cols[2]+ " " + cols[3];
			Assert.isTrue(cols[0].length()==6, "Invalid currencyCodes: " + cols[0]);
			Assert.isTrue(validateDouble(cols[1]), "Invalid number: "+ cols[1]); 
			Assert.isTrue(validateDate(dateString),  "Invalid dateTime: "+dateString);
			final ExchangeRate exchangeRate = new ExchangeRateImpl(cols[0].substring(0, 3), cols[0].substring(3));
			
			
			
			
			final Data rate = new DataImpl(dateString,Double.parseDouble(cols[1]));
			Arrays.asList(rate.getClass().getDeclaredFields()).stream().filter(field -> field.getType().equals(DateFormat.class)).forEach(field -> ReflectionUtils.setField(field, rate, DATE_FORMAT));
			exchangeRate.assign(Arrays.asList(rate));
			results.add(exchangeRate);
	 
		}
		return results;
	}

	

	private final boolean validateDouble(final String rate){
		try {
			Double.parseDouble(rate);
		return true;
		} catch(final NumberFormatException nf) {
			return false;
		}
	}
	
	
	private boolean  validateDate(final String dateString) {
		try {
			DATE_FORMAT.parse(dateString);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}
	
	
	@Lookup
	abstract ExceptionTranslationBuilder<Collection<ExchangeRate>,BufferedReader> exceptionTranslationBuilder(); 
	
	
	

	
	

}
