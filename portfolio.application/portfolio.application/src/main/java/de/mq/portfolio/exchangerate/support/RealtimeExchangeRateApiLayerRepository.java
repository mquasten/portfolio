package de.mq.portfolio.exchangerate.support;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.springframework.web.client.RestOperations;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.gateway.GatewayParameterAggregation;
import de.mq.portfolio.share.support.DataImpl;

@Repository
class RealtimeExchangeRateApiLayerRepository implements RealtimeExchangeRateRepository {

	private final DateFormat dateFormat;

	
	
	
	private final RestOperations restOperations;

	@Autowired
	RealtimeExchangeRateApiLayerRepository(final RestOperations restOperations, @Value("${realtime.exchangerates.dateformat}") final String dateFormat) {
		this.restOperations = restOperations;
		this.dateFormat = new SimpleDateFormat(dateFormat);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.portfolio.exchangerate.support.RealtimeExchangeRateRepository#
	 * exchangeRates(java.util.Collection)
	 */

	@SuppressWarnings("unchecked")
	@Override
	public final List<ExchangeRate> exchangeRates(final GatewayParameterAggregation<Collection<ExchangeRate>> gatewayParameterAggregation) {
		final GatewayParameter gatewayParameter = gatewayParameterAggregation.gatewayParameter(Gateway.YahooRealtimeExchangeRates);
		
		
		
		
		
	
		final Map<String,Object> jsonAsMap = restOperations.getForObject(gatewayParameter.urlTemplate(), HashMap.class, gatewayParameter.parameters());
		
		final List<ExchangeRate> results = new ArrayList<>();
		final Date estimatedDate =  Date.from(ZonedDateTime.now(Clock.systemDefaultZone()).minusMinutes(30).toInstant());;
		for(final Entry<String,Number> entry : ((Map<String,Number>) jsonAsMap.get("quotes")).entrySet()) {
			Assert.isTrue(entry.getKey().length()==6, "Invalid Currency.");
			final ExchangeRate exchangeRate = new ExchangeRateImpl(entry.getKey().substring(0, 3), entry.getKey().substring(3, 6));
		    
			exchangeRate.assign(Arrays.asList(new DataImpl(estimatedDate, entry.getValue().doubleValue())));
			results.add(exchangeRate);
		}
		results.add(new ExchangeRateImpl("EUR", "EUR" , Arrays.asList(new DataImpl(estimatedDate, 1d))));
		
		return results;
		
		
	}

	@Override
	public Gateway supports(Collection<ExchangeRate> exchangeRates) {
		// TODO Auto-generated method stub
		return null;
	}

	

}
