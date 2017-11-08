package de.mq.portfolio.exchangerate.support;

import java.lang.reflect.Modifier;
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
import org.springframework.data.util.ReflectionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.springframework.web.client.RestOperations;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.gateway.GatewayParameterAggregation;
import de.mq.portfolio.share.Data;
import de.mq.portfolio.share.support.DataImpl;

@Repository
class RealtimeExchangeRateApiLayerRepositoryImpl implements RealtimeExchangeRateRepository {	
	
	static final String QUOTES_KEY = "quotes";

	private final RestOperations restOperations;
	
	private final DateFormat df = new SimpleDateFormat( "yyyy-MM-ddHHmm");

	@Autowired
	RealtimeExchangeRateApiLayerRepositoryImpl(final RestOperations restOperations) {
		this.restOperations = restOperations;
	
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
		final GatewayParameter gatewayParameter = gatewayParameterAggregation.gatewayParameter(Gateway.ApiLayerRealtimeExchangeRates);
		final Map<String,Object> jsonAsMap = restOperations.getForObject(gatewayParameter.urlTemplate(), HashMap.class, gatewayParameter.parameters());
		final List<ExchangeRate> results = new ArrayList<>();
		final Date estimatedDate =  Date.from(ZonedDateTime.now(Clock.systemDefaultZone()).minusMinutes(30).toInstant());;
		Assert.notNull(jsonAsMap.get(QUOTES_KEY), "Quotes should exist in ResultMap.");
		for(final Entry<String,Number> entry : ((Map<String,Number>) jsonAsMap.get(QUOTES_KEY)).entrySet()) {
			Assert.isTrue(entry.getKey().length()==6, "Invalid currencies: 2 x 3 letters expected.");
			final ExchangeRate exchangeRate = new ExchangeRateImpl(entry.getKey().substring(0, 3), entry.getKey().substring(3, 6));
			final Data data = newData(estimatedDate, entry.getValue().doubleValue());
			exchangeRate.assign(Arrays.asList(data));
			results.add(exchangeRate);
		}
		results.add(new ExchangeRateImpl("EUR", "EUR" , Arrays.asList(newData(estimatedDate, 1d))));
		
		return results;
		
		
	}

	private Data newData(final Date estimatedDate, final double value) {
		final Data data = new DataImpl(estimatedDate, value);
		Arrays.asList(data.getClass().getDeclaredFields()).stream().filter(field -> field.getType().equals(DateFormat.class)).forEach(field -> ReflectionUtils.setField(field, data, df));
		Arrays.asList(data.getClass().getDeclaredFields()).stream().filter(field ->  !Modifier.isStatic(field.getModifiers()) && field.getType().equals(String.class)).forEach(field -> ReflectionUtils.setField(field,data, df.format(estimatedDate)));
		return data;
	}

	@Override
	public Gateway supports(Collection<ExchangeRate> exchangeRates) {
		return Gateway.ApiLayerRealtimeExchangeRates;
	}

	

}
