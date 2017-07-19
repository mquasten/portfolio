package de.mq.portfolio.share.support;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.gateway.GatewayParameterAggregation;
import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/mongo-test.xml", "/application-test.xml" })
@Ignore
public class HistoryGoogleRestRepositoryIntegrationTest {
	
	
	private static final String QUERY_PARAMETER_VALUE = "KO:NYSE";

	private static final String QUERY_PARAMETER_NAME = "query";

	private static final String URL = "http://www.google.com/finance/historical?q={query}&startdate={startdate}&output=csv";

	@Autowired
	@Qualifier("googleHistoryRepository")
	private HistoryRepository historyRestRepository;
	
	private Share share = Mockito.mock(Share.class);
	
	@SuppressWarnings("unchecked")
	final GatewayParameterAggregation<Share> gatewayParameterAggregation = Mockito.mock(GatewayParameterAggregation.class);
	final GatewayParameter gatewayParameter = Mockito.mock(GatewayParameter.class);
	
	@Before
	public void setup() {
		Mockito.doReturn(share).when(gatewayParameterAggregation).domain();
		Mockito.doReturn(gatewayParameter).when(gatewayParameterAggregation).gatewayParameter(Gateway.GoogleRateHistory);
		Mockito.when(gatewayParameter.urlTemplate()).thenReturn(URL);
		final Map<String,String> parameter = new HashMap<>();
		parameter.put(QUERY_PARAMETER_NAME, QUERY_PARAMETER_VALUE);
		Mockito.when(gatewayParameter.parameters()).thenReturn(parameter);
	}
	
	
	@Test
	public final void history() {

		final TimeCourse result = historyRestRepository.history(gatewayParameterAggregation);
		
		Assert.assertTrue(result.rates().size() > 250 );;
	}

	

}
