package de.mq.portfolio.share.support;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestOperations;

import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.gateway.GatewayParameterAggregation;
import de.mq.portfolio.share.Data;
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
	
		
		return IntStream.range(0, parameters.size()).mapToObj(i -> rates(gatewayParameter.urlTemplate() , parameters.get(i), ((List<Share>)gatewayParameterAggregation.domain()).get(i))).collect(Collectors.toList());
		
		
	}

	private List<Map<String, String>> parameters(final GatewayParameter gatewayParameter, final int expectedSize ) {
		final List<Map<String,String>> parameters = IntStream.range(0, expectedSize).mapToObj(i -> new HashMap<String,String>()).collect(Collectors.toList());
	
		final Collection<Entry<String,String[]>> allEntries = gatewayParameter.parameters().entrySet().stream().map(entry -> new AbstractMap.SimpleImmutableEntry<>(entry.getKey(), StringUtils.commaDelimitedListToStringArray(entry.getValue()))).collect(Collectors.toList()) ;
		
		allEntries.stream().map(entry -> entry.getValue().length).forEach(length -> Assert.isTrue(length==expectedSize, String.format("ParameterArray has wrong size : %s, expected %s.", length, expectedSize) ));
		
		allEntries.forEach(entry -> IntStream.range(0, expectedSize).forEach(i -> parameters.get(i).put(entry.getKey(), entry.getValue()[i])));
		
		return parameters;
	}

	private TimeCourse rates(final String url,final Map<String,String> parameter, final Share share) {
		
		
		
		
		final String result = restOperations.getForObject(url, String.class, parameter);
	
		TimeCourse timeCourse =  exceptionTranslationBuilder().withResource(() -> new BufferedReader(new StringReader(result))).withTranslation(IllegalStateException.class, Arrays.asList(IOException.class)).withStatement(bufferedReader -> {
			return toTimeCourse(bufferedReader, share);
		}).translate();
		
		
		return timeCourse;
				
	}
	
	private TimeCourse toTimeCourse(final BufferedReader bufferedReader, final Share share) throws IOException {
		double close=-1;
		double last=-1;
		
		long startTimeStamp = -1 ;  
		long lastTimeOffset=-1; 
		Date closeDate = null;
		for (String line = ""; line != null; line = bufferedReader.readLine()) {
		
			final  String[] columns=line.split("[,]");
			
			if(( columns.length != 2)|| (!columns[0].matches("^[a0-9]+"))){
				continue;
			}
			
		
			if( line.matches("^a.*") &&  lastTimeOffset > 0 ){
				closeDate= new Date(startTimeStamp + lastTimeOffset*1000 *60 );	
				lastTimeOffset=-1;
			}
			
			if( line.matches("^a.*")   )  {
				
				
				startTimeStamp=1000* Long.parseLong(columns[0].replaceFirst("^a", ""));
				
				close=last;
			} 
			else {
				lastTimeOffset=Long.parseLong(columns[0]);	
			}
			
			
			
			last=Double.parseDouble(columns[1]);
			
			//System.out.println(line);
			
		}
		
		final Date currentDate= new Date(startTimeStamp+ lastTimeOffset*1000*60);
		
		Assert.isTrue(close > 0, "Close rate is not found.");
		Assert.isTrue(last > 0, "Current rate is not found.");
		Assert.notNull(closeDate , "Close date not found.");
		
		Assert.isTrue(startTimeStamp > 0, "Start time not found.");
		Assert.isTrue(lastTimeOffset > 0, "Current time offset not found.");
		
		System.out.println(closeDate);
		System.out.println(close);
		
		System.out.println(currentDate);
		System.out.println(last);
		
		return new TimeCourseImpl(share, Arrays.asList(newData(closeDate, close), newData(currentDate, last) ), Arrays.asList());
	}
	
	private Data newData(final Date date, final double value) {
		final DateFormat df = new SimpleDateFormat(DataImpl.DATE_PATTERN+ "-dd-HH-mm-ss");
		final Data data = new DataImpl(df.format(date), value);
		Arrays.asList(DataImpl.class.getDeclaredFields()).stream().filter(field -> field.getType().equals(DateFormat.class)).forEach(field -> { field.setAccessible(true); ReflectionUtils.setField(field, data, df); });
		return data;
	}

	@Override
	public Gateway supports(final Collection< Share> shares) {
		return Gateway.GoogleRealtimeRate;
	}
	
	@Lookup
	abstract ExceptionTranslationBuilder<TimeCourse, BufferedReader> exceptionTranslationBuilder();

}
