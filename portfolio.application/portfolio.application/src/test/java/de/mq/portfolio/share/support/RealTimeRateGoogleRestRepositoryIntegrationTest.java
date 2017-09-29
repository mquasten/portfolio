package de.mq.portfolio.share.support;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StringUtils;

import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.gateway.GatewayParameterAggregation;
import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application-test.xml" ,  "/mongo-test.xml" })
@Ignore
public class RealTimeRateGoogleRestRepositoryIntegrationTest {
	
	private static final String URL = "http://finance.google.com/finance/getprices?i=60&p=2d&f=d,c&df=cpct&q={query}&x={market}";

	@Autowired
	@Qualifier("googleRealtimeRepository")
	private  RealTimeRateRepository realTimeRateRepository; 
	
	@Value("#{stocks}")
	private Map<String,String> stocks;
	
	@Test
	public final void rates() {
		final List<Share> shares =  Arrays.asList(share("JNJ"), share("PG"), share("KO"), share("VZ"), share("SAP.DE"));
		StringUtils.collectionToCommaDelimitedString(shares.stream().map( Share::code).collect(Collectors.toList()));
		@SuppressWarnings("unchecked")
		final GatewayParameterAggregation<Collection<Share>> gatewayParameterAggregation = Mockito.mock(GatewayParameterAggregation.class);
		final GatewayParameter gatewayParameter = Mockito.mock(GatewayParameter.class);
		//Mockito.when(gatewayParameter.code()).thenReturn(StringUtils.collectionToCommaDelimitedString(shares.stream().map( Share::code).collect(Collectors.toList())));
		
																
		Mockito.when(gatewayParameter.urlTemplate()).thenReturn(URL);
		final Map<String,String> parameters = new HashMap<>();
		parameters.put("query", StringUtils.collectionToCommaDelimitedString(shares.stream().map( share -> share.code().replaceAll("[.].*$", "")).collect(Collectors.toList())));
		parameters.put("market", StringUtils.collectionToCommaDelimitedString(shares.stream().map(share -> share.code().endsWith(".DE") ? "ETR" : "NYSE").collect(Collectors.toList())));
		Mockito.when(gatewayParameter.parameters()).thenReturn(parameters);
		Mockito.when(gatewayParameterAggregation.gatewayParameter(Gateway.GoogleRealtimeRate)).thenReturn(gatewayParameter);
		Mockito.when(gatewayParameterAggregation.domain()).thenReturn(shares);
		
		final  List<TimeCourse> timeCourses = (List<TimeCourse>) realTimeRateRepository.rates(gatewayParameterAggregation);
		
		Assert.assertSame(shares.size() ,  timeCourses.size());
		IntStream.range(0, timeCourses.size()).forEach(i -> {
			Assert.assertEquals(shares.get(i).code(),timeCourses.get(i).share().code());
			Assert.assertEquals(2, timeCourses.get(i).rates().size());
			Assert.assertTrue(timeCourses.get(i).dividends().isEmpty());
			
			final double delta =100 * Math.abs(timeCourses.get(i).rates().get(1).value() - timeCourses.get(i).rates().get(0).value()  ) / timeCourses.get(i).rates().get(0).value() ;
			Assert.assertTrue(delta < 5d);
			
			final LocalDate firstDate = timeCourses.get(i).rates().get(0).date().toInstant().atZone( ZoneId.systemDefault()).toLocalDate();
			final LocalDate lastDate = timeCourses.get(i).rates().get(1).date().toInstant().atZone( ZoneId.systemDefault()).toLocalDate();
			
			
			final Period period =  Period.between(firstDate, lastDate);
			Assert.assertEquals(1, period.getDays());
			Assert.assertEquals(0, period.getYears());
			Assert.assertEquals(0, period.getMonths());
		
			
		});
		
	}

	protected Share share(final String code) {
		final Share share = Mockito.mock(Share.class);
		Mockito.when(share.code()).thenReturn(code);
		return share;
	}
	
	@Test
	public final void generateCsv() {
		stocks.entrySet().stream().sorted((e1,e2) ->  e1.getValue().compareToIgnoreCase(e2.getValue())).forEach(entry -> {
			final String[] values = entry.getValue().split("[:]");
		
			System.out.println(entry.getKey()+";" + Gateway.GoogleRealtimeRate +";" + URL+";"+String.format("{query:'%s', market:'%s'}", values[1], values[0]));
			
		});
	}
	

}
