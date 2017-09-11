package de.mq.portfolio.share.support;




import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestOperations;

import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.gateway.GatewayParameterAggregation;
import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;

@Repository
class RealTimeRateGoogleRestRepositoryImpl  implements RealTimeRateRepository{
	
	private final RestOperations restOperations;
	RealTimeRateGoogleRestRepositoryImpl(final RestOperations restOperations){
		this.restOperations=restOperations;
	}

	@Override
	public Collection<TimeCourse> rates(final GatewayParameterAggregation<Collection<Share>> gatewayParameterAggregation) {
		
		
		final GatewayParameter gatewayParameter = gatewayParameterAggregation.gatewayParameter(Gateway.GoogleRealtimeRate);
		final List<Map<String, String>> parameters = parameters(gatewayParameter, gatewayParameterAggregation.domain().size());
		
		
		
		parameters.forEach(parameterMap -> {
			rates(gatewayParameter.urlTemplate() , parameterMap);
		} );
		
		
		
		
		return Arrays.asList(new TimeCourseImpl(null, Arrays.asList(), Arrays.asList()));
	}

	protected List<Map<String, String>> parameters(final GatewayParameter gatewayParameter, final int expectedSize ) {
		final List<Map<String,String>> parameters = IntStream.range(0, expectedSize).mapToObj(i -> new HashMap<String,String>()).collect(Collectors.toList());
		
		
		final Collection<Entry<String,String[]>> allEntries = gatewayParameter.parameters().entrySet().stream().map(entry -> new AbstractMap.SimpleImmutableEntry<>(entry.getKey(), StringUtils.commaDelimitedListToStringArray(entry.getValue()))).collect(Collectors.toList()) ;
		
		allEntries.stream().map(entry -> entry.getValue().length).forEach(length -> Assert.isTrue(length==expectedSize, String.format("ParameterArray has wrong size : %s, expected %s.", length, expectedSize) ));
		
		
		
		
		allEntries.forEach(entry -> IntStream.range(0, expectedSize).forEach(i -> parameters.get(i).put(entry.getKey(), entry.getValue()[i])));
		return parameters;
	}

	private void rates(final String url,final Map<String,String> parameter) {
		
		final String result = restOperations.getForObject(url, String.class, parameter);
		
		System.out.println(result);
	}

	@Override
	public Gateway supports(final Collection< Share> shares) {
		return Gateway.GoogleRealtimeRate;
	}

}
