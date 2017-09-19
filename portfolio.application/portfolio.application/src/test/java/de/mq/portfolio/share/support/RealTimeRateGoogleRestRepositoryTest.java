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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestOperations;

import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.gateway.GatewayParameterAggregation;
import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.support.ExceptionTranslationBuilderImpl;

public class RealTimeRateGoogleRestRepositoryTest {
	
	
	private static final String MARKET = "ETR";


	private static final String CODE = "SAP.DE";


	private static final String URL = "urlTemplate";

	
	

	private final  Date yesterday = dateForDaysBefore(1);
	private final Date today = dateForDaysBefore(0);
	private final double start = 91d;
	private final double end = 92d; 
	
	
	
	final String content = "EXCHANGE%3DNYSE\n"+
	"MARKET_OPEN_MINUTE=570\n"+
	"MARKET_CLOSE_MINUTE=960\n"+
	"INTERVAL=60\n"+
	"COLUMNS=DATE,CLOSE\n"+
	"DATA=\n"+
	"TIMEZONE_OFFSET=-240\n"+
	"a" + new Long(yesterday.getTime()/1000) +   ",134.11\n"+
	"1,134.14\n"+
	"389," + start +  "\n"+
	"a" + new Long(today.getTime()/1000) + ",135.43\n"+
	"1,135.14\n"+
	"206," + end + "\n";
	
	
	private final  RestOperations restOperations = Mockito.mock(RestOperations.class);
	
	private final GatewayParameter gatewayParameter = Mockito.mock(GatewayParameter.class);
	
	@SuppressWarnings("unchecked")
	private final GatewayParameterAggregation<Collection<Share>> gatewayParameterAggregation = Mockito.mock(GatewayParameterAggregation.class, Mockito.CALLS_REAL_METHODS);
	
	
	
	
	private Share share = Mockito.mock(Share.class);
	private RealTimeRateGoogleRestRepositoryImpl rateRepository;
	
	@Before
	public final void setup() throws Exception {
		
		final Map<String,String> params =  new HashMap<>();
	    params.put("query", CODE.replaceFirst(".DE", ""));
	    params.put("market", MARKET);
		Mockito.when(gatewayParameter.parameters()).thenReturn(params);
		Mockito.when(gatewayParameter.urlTemplate()).thenReturn(URL);
	
		
		
	    Mockito.when(restOperations.getForObject(URL,String.class,params)).thenReturn(content);
		rateRepository  = Mockito.mock(RealTimeRateGoogleRestRepositoryImpl.class);
		Mockito.doReturn(new ExceptionTranslationBuilderImpl<>()).when(rateRepository).exceptionTranslationBuilder();
		Arrays.asList(RealTimeRateGoogleRestRepositoryImpl.class.getDeclaredFields()).stream().filter(field -> field.getType().equals(RestOperations.class)).forEach(field -> ReflectionTestUtils.setField(rateRepository, field.getName(), restOperations));
	    Mockito.when(gatewayParameterAggregation.gatewayParameter(Gateway.GoogleRealtimeRate)).thenReturn(gatewayParameter);
	    Mockito.when(gatewayParameterAggregation.domain()).thenReturn(Arrays.asList(share));
	    Mockito.when(gatewayParameter.code()).thenReturn("SAP.DE");
	   
	}
	
	@Test
	public final void rates() throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
	
		final Collection<TimeCourse>  timeCourses = (List<TimeCourse>) rateRepository.rates(gatewayParameterAggregation);
		Assert.assertEquals(yesterday, DataAccessUtils.requiredSingleResult(timeCourses).rates().get(0).date());
		Assert.assertEquals(today, DataAccessUtils.requiredSingleResult(timeCourses).rates().get(1).date());
		
	
		Assert.assertEquals((Double) start , (Double) DataAccessUtils.requiredSingleResult(timeCourses).rates().get(0).value());
		Assert.assertEquals((Double) end , (Double) DataAccessUtils.requiredSingleResult(timeCourses).rates().get(1).value());
	}
	
	
	private Date dateForDaysBefore(final int daysBack) {
		return Date.from(LocalDateTime.now().plusDays(-daysBack).truncatedTo(ChronoUnit.DAYS).atZone(ZoneId.systemDefault()).toInstant());
	}
	

}
