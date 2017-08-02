package de.mq.portfolio.gateway.support;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterConvertEvent;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.portfolio.gateway.Gateway;

public class GatewayParameterListenerTest {
	
	private static final String STOCK_EXCHANGE_ID = "NYSE";


	private static final String CODE = "KO";


	private final String QUERY_PARAMETER_NAME = "query";
	private static final String SPEL_FORMAT = "{%s:'%s:%s'}";
	@SuppressWarnings("unchecked")
	private final AfterConvertEvent<GatewayParameterImpl> afterConvertEvent = Mockito.mock(AfterConvertEvent.class);

	private final AbstractMongoEventListener<GatewayParameterImpl> gatewayParameterListener = new GatewayParameterListenerImpl();
	
	
	private final GatewayParameterImpl gatewayParameter = new GatewayParameterImpl(CODE , Gateway.GoogleRateHistory, "url" , String.format(SPEL_FORMAT, QUERY_PARAMETER_NAME, STOCK_EXCHANGE_ID, CODE));
	
	@Test
	public final void onAfterConvert() {
		Assert.assertTrue(!gatewayParameter.parameters().isEmpty());
		Arrays.asList(GatewayParameterImpl.class.getDeclaredFields()).stream().filter(field -> field.getType().equals(Map.class)).forEach(field -> ReflectionTestUtils.setField(gatewayParameter, field.getName(), new HashMap<>()));
		
		Assert.assertTrue(gatewayParameter.parameters().isEmpty());
		Mockito.when(afterConvertEvent.getSource()).thenReturn(gatewayParameter);
		gatewayParameterListener.onAfterConvert(afterConvertEvent);
		
		Assert.assertTrue(gatewayParameter.parameters().size()==1);
		
		Assert.assertEquals(String.format("%s:%s", STOCK_EXCHANGE_ID, CODE), gatewayParameter.parameters().values().stream().findFirst().get());
		Assert.assertEquals(QUERY_PARAMETER_NAME, gatewayParameter.parameters().keySet().stream().findFirst().get());
	}

}
