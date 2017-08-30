package de.mq.portfolio.share.support;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.gateway.GatewayParameterAggregation;
import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application-test.xml" ,  "/mongo-test.xml" })
@Ignore
public class RealTimeRateYahooRestRepositoryIntegrationTest {
	
	private static final String QUERY_PARAMETER = "query";
	private static final String CODE_VZ = "VZ";
	private static final String CODE_KO = "KO";
	private static final String CODE_PG = "PG";
	private static final String CODE_JNJ = "JNJ";
	private static final String CODE_SAP = "SAP.DE";
	@Autowired
	@Qualifier("yahooRealtimeRepository")
	private  RealTimeRateRepository   realTimeRateRestRepository; 
	private final static  String URL="http://download.finance.yahoo.com/d/quotes.csv?s={query}&f=snbaopl1";
	
	private final GatewayParameter gatewayParameter = Mockito.mock(GatewayParameter.class);
	@SuppressWarnings("unchecked")
	private final GatewayParameterAggregation<Collection<Share>> gatewayParameterAggregation = Mockito.mock(GatewayParameterAggregation.class);
	private final Collection<Share> shares = new ArrayList<>();
	
	@Before
	public  final void setup() {
		shares.clear();
		shares.add(newshareMock(CODE_SAP));
		shares.add(newshareMock(CODE_JNJ));
		shares.add(newshareMock(CODE_PG));
		shares.add(newshareMock(CODE_KO));
		shares.add(newshareMock(CODE_VZ));
	
		final Map<String,String> parameters = new HashMap<>();
		parameters.put(QUERY_PARAMETER, String.format("%s,%s,%s,%s,%s", CODE_SAP, CODE_JNJ, CODE_PG, CODE_KO, CODE_VZ ));;
		Mockito.when(gatewayParameter.parameters()).thenReturn(parameters);
		Mockito.when(gatewayParameter.urlTemplate()).thenReturn(URL);
		Mockito.when(gatewayParameterAggregation.gatewayParameter(Gateway.YahooRealtimeRate)).thenReturn(gatewayParameter);
		Mockito.when(gatewayParameterAggregation.domain()).thenReturn(shares);
	}
	private Share newshareMock(final String code) {
		final Share share = Mockito.mock(Share.class);
		Mockito.when(share.code()).thenReturn(code);
		return share;
	}
	
	@Test
	public  void rates() {
		final List<TimeCourse> results = (List<TimeCourse>)  realTimeRateRestRepository.rates(gatewayParameterAggregation);
		Assert.assertEquals(shares.size(), results.size());
		results.forEach(tc -> {
			Assert.assertEquals(DataAccessUtils.requiredSingleResult(shares.stream().map(share -> share.code()).filter(code -> code.equals(tc.share().code())).collect(Collectors.toSet())),  tc.share().code());
			
			System.out.println(tc.code() + ":"+  tc.rates().get(0).value() + ":" +tc.rates().get(1).value() + ":"+ tc.totalRate());
			
		});
	}
	
	@Test
	public  void ratesIndex() {
		final Map<String,String> parameters = new HashMap<>();
		parameters.put(QUERY_PARAMETER, "^DJI");
		Mockito.when(gatewayParameter.parameters()).thenReturn(parameters);
		final Collection<Share> ahares = Arrays.asList(newshareMock("^DJI"));
		Mockito.when(gatewayParameterAggregation.domain()).thenReturn(ahares);
		final List<TimeCourse> results = (List<TimeCourse>)  realTimeRateRestRepository.rates(gatewayParameterAggregation);
		Assert.assertTrue(results.isEmpty());
	}

	

}
