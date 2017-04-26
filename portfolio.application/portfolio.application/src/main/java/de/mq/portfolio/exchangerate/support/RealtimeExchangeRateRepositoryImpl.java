package de.mq.portfolio.exchangerate.support;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import org.springframework.web.client.RestOperations;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.share.Data;

@Repository
public class RealtimeExchangeRateRepositoryImpl implements RealtimeExchangeRateRepository {
	
	String URL= "http://query.yahooapis.com/v1/public/yql?q=select id,Rate from yahoo.finance.xchange where pair in ({exchangeRates})&format=json&env=store://datatables.org/alltableswithkeys";

	final String path ="query.results.rate" ;
private final RestOperations restOperations;
	
	@Autowired
	RealtimeExchangeRateRepositoryImpl(final RestOperations restOperations) {
		this.restOperations = restOperations;
	}
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.exchangerate.support.RealtimeExchangeRateRepository#exchangeRates(java.util.Collection)
	 */
	@Override
	public final Collection<Data> exchangeRates(final Collection<ExchangeRate> rates) {
		
		
		final Object results[] = new Object[] {restOperations.getForObject(URL, Map.class, "\"USDEUR\",\"EURGBP\", \"USDGBP\"")};
		
		Arrays.asList(path.split("[.]")).forEach(path -> results[0]=parse(results[0], path));
		
		@SuppressWarnings("unchecked")
		final Collection<Map<String,Object>>resultList= (Collection<Map<String, Object>>) results[0];
		System.out.println(resultList);
		return null;
		
	}
	
	
	private Object parse(Object map, String path ) {
		return ((Map<?,?>) map).get(path);
	}
	
	

}
