package de.mq.portfolio.share.support;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestOperations;

import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.gateway.GatewayParameterAggregation;
import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.support.ExceptionTranslationBuilderImpl;

public class RealTimeRateGoogleRestRepositoryTest {
	
	
	private static final String CODE_KO = "KO";


	private static final String MARKET_KO = "NYSE";


	private static final String MARKET_SAP = "ETR";


	private static final String CODE_SAP = "SAP.DE";


	private static final String URL = "urlTemplate";

	
	

	private final  Date yesterday = dateForDaysBefore(1);
	private final Date today = dateForDaysBefore(0);

	
	private static Double START_SAP = 91d;
	private static Double END_SAP= 92d;
	
	private static Double START_KO = 45d;
	private static Double END_KO= 46d;
	
	
	
	private final  RestOperations restOperations = Mockito.mock(RestOperations.class);
	
	private final GatewayParameter gatewayParameter = Mockito.mock(GatewayParameter.class);
	
	@SuppressWarnings("unchecked")
	private final GatewayParameterAggregation<Collection<Share>> gatewayParameterAggregation = Mockito.mock(GatewayParameterAggregation.class, Mockito.CALLS_REAL_METHODS);
	
	private Share shareSAP = Mockito.mock(Share.class);
	private Share shareKO = Mockito.mock(Share.class);
	private RealTimeRateGoogleRestRepositoryImpl rateRepository;
	
	@Before
	public final void setup() throws Exception {
		Mockito.when(shareSAP.code()).thenReturn(CODE_SAP);
		Mockito.when(shareKO.code()).thenReturn(CODE_KO);
		final Map<String, String> params = parameterMap(shareSAP.code()+"," +shareKO.code(), MARKET_SAP+"," + MARKET_KO);
	
		final Map<String, String> paramsSAP = parameterMap(shareSAP.code(), MARKET_SAP);
		final Map<String, String> paramsKO = parameterMap(shareKO.code(), MARKET_KO);
		
		
		Mockito.when(gatewayParameter.parameters()).thenReturn(params);
		Mockito.when(gatewayParameter.urlTemplate()).thenReturn(URL);
		
	    Mockito.when(restOperations.getForObject(URL,String.class,paramsSAP)).thenReturn(content(START_SAP,END_SAP));
	    Mockito.when(restOperations.getForObject(URL,String.class,paramsKO)).thenReturn(content(START_KO,END_KO));
		rateRepository  = Mockito.mock(RealTimeRateGoogleRestRepositoryImpl.class);
		Mockito.doAnswer(a -> new ExceptionTranslationBuilderImpl<>()).when(rateRepository).exceptionTranslationBuilder();
		Arrays.asList(RealTimeRateGoogleRestRepositoryImpl.class.getDeclaredFields()).stream().filter(field -> field.getType().equals(RestOperations.class)).forEach(field -> ReflectionTestUtils.setField(rateRepository, field.getName(), restOperations));
	    Mockito.when(gatewayParameterAggregation.gatewayParameter(Gateway.GoogleRealtimeRate)).thenReturn(gatewayParameter);
	    Mockito.when(gatewayParameterAggregation.domain()).thenReturn(Arrays.asList(shareSAP, shareKO));
	 
	   
	}

	protected Map<String, String> parameterMap(final String query, final String market) {
		final Map<String,String> params =  new HashMap<>();
	    params.put("query", query.replaceFirst(".DE", ""));
	    params.put("market", market);
		return params;
	}
	
	private String content(final double start, final double end) {
		return "EXCHANGE%3DNYSE\n"+
				"MARKET_OPEN_MINUTE=570\n"+
				"MARKET_CLOSE_MINUTE=960\n"+
				"INTERVAL=60\n"+
				"COLUMNS=DATE,CLOSE\n"+
				"DATA=\n"+
				"TIMEZONE_OFFSET=-240\n"+
				"a" + new Long(yesterday.getTime()/1000) +   ",134.11\n"+
				"1,134.14\n"+
				"360," + start +  "\n"+
				"a" + new Long(today.getTime()/1000) + ",135.43\n"+
				"1,135.14\n"+
				"210," + end + "\n";
	}
	
	@Test
	public final void rates() throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
	
		final List<TimeCourse>  timeCourses = rateRepository.rates(gatewayParameterAggregation).stream().collect(Collectors.toList());
		Assert.assertEquals(2, timeCourses.size());
		timeCourses.forEach(timeCourse -> {
			Assert.assertEquals(dateForDaysBefore(1, 22,30),timeCourse.rates().get(0).date());
			Assert.assertEquals(dateForDaysBefore(0,20,0), timeCourse.rates().get(1).date());	
		});
		
		
		Assert.assertEquals(START_SAP , (Double) timeCourses.get(0).rates().get(0).value());
		Assert.assertEquals(END_SAP, (Double) timeCourses.get(0).rates().get(1).value());
		Assert.assertEquals(START_KO , (Double) timeCourses.get(1).rates().get(0).value());
		Assert.assertEquals(END_KO, (Double) timeCourses.get(1).rates().get(1).value());
	}
	
	
	private Date dateForDaysBefore(final int daysBack, int hour, int min ) {
		return Date.from(LocalDateTime.now().plusDays(-daysBack).truncatedTo(ChronoUnit.DAYS).plusHours(hour).plusMinutes(min).atZone(ZoneId.systemDefault()).toInstant());
	}
	
	
	private Date dateForDaysBefore(final int daysBack) {
		return dateForDaysBefore(daysBack, 16, 30);
	}

}
