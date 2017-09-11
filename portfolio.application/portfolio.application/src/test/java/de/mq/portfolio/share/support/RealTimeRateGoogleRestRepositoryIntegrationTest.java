package de.mq.portfolio.share.support;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StringUtils;

import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.gateway.GatewayParameterAggregation;
import de.mq.portfolio.share.Share;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application-test.xml" ,  "/mongo-test.xml" })
@Ignore
public class RealTimeRateGoogleRestRepositoryIntegrationTest {
	
	@Autowired
	@Qualifier("googleRealtimeRepository")
	private  RealTimeRateRepository realTimeRateRepository; 
	
	@Test
	public final void rates() {
		final List<Share> shares =  Arrays.asList(share("JNJ"), share("PG"), share("KO"), share("VZ"), share("VZ"), share("SAP.DE"));
		StringUtils.collectionToCommaDelimitedString(shares.stream().map( Share::code).collect(Collectors.toList()));
		@SuppressWarnings("unchecked")
		final GatewayParameterAggregation<Collection<Share>> gatewayParameterAggregation = Mockito.mock(GatewayParameterAggregation.class);
		final GatewayParameter gatewayParameter = Mockito.mock(GatewayParameter.class);
		//Mockito.when(gatewayParameter.code()).thenReturn(StringUtils.collectionToCommaDelimitedString(shares.stream().map( Share::code).collect(Collectors.toList())));
		Mockito.when(gatewayParameter.urlTemplate()).thenReturn("http://www.google.com/finance/getprices?i=60&p=1d&f=d,o,h,l,c,v&df=cpct&q={query}&x={market}");
		final Map<String,String> parameters = new HashMap<>();
		parameters.put("query", StringUtils.collectionToCommaDelimitedString(shares.stream().map( share -> share.code().replaceAll("[.].*$", "")).collect(Collectors.toList())));
		parameters.put("market", StringUtils.collectionToCommaDelimitedString(shares.stream().map(share -> share.code().endsWith(".DE") ? "ETR" : "NYSE").collect(Collectors.toList())));
		Mockito.when(gatewayParameter.parameters()).thenReturn(parameters);
		Mockito.when(gatewayParameterAggregation.gatewayParameter(Gateway.GoogleRealtimeRate)).thenReturn(gatewayParameter);
		Mockito.when(gatewayParameterAggregation.domain()).thenReturn(shares);
		
		realTimeRateRepository.rates(gatewayParameterAggregation);
		
	}

	protected Share share(final String code) {
		final Share share = Mockito.mock(Share.class);
		Mockito.when(share.code()).thenReturn(code);
		return share;
	}
	

}
