package de.mq.portfolio.share.support;




import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestOperations;

import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.gateway.GatewayParameterAggregation;
import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.support.ExceptionTranslationBuilder;

@Repository
abstract class RealTimeRateGoogleRestRepositoryImpl  implements RealTimeRateRepository{
	
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

	private TimeCourse rates(final String url,final Map<String,String> parameter) {
		
		final String result = restOperations.getForObject(url, String.class, parameter);
		
		TimeCourse timeCourse =  exceptionTranslationBuilder().withResource(() -> new BufferedReader(new StringReader(result))).withTranslation(IllegalStateException.class, Arrays.asList(IOException.class)).withStatement(bufferedReader -> {
			return toTimeCourse(bufferedReader);
		}).translate();
		
		System.exit(1);
		return timeCourse;
				
	}
	
	private TimeCourse toTimeCourse(BufferedReader bufferedReader) throws IOException {
		double close=-1;
		double last=-1;
		
		for (String line = ""; line != null; line = bufferedReader.readLine()) {
		//	System.out.println(line);
			final  String[] columns=line.split("[,]");
			if(( columns.length != 2)|| (!columns[0].matches("^[a0-9]+"))){
				continue;
			}
			
		
			if( line.matches("^a.*")) {
				close=last;
			} 
			
			last=Double.parseDouble(columns[1]);
			System.out.println(line);
			
		}
		System.out.println(close);
		System.out.println(last);
		System.out.println(new Date((1505309460l+ 390*60L) *1000));
		return null;
	}

	@Override
	public Gateway supports(final Collection< Share> shares) {
		return Gateway.GoogleRealtimeRate;
	}
	
	@Lookup
	abstract ExceptionTranslationBuilder<TimeCourse, BufferedReader> exceptionTranslationBuilder();

}
