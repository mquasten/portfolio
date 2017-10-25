package de.mq.portfolio.gateway.support;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;

import de.mq.portfolio.gateway.GatewayParameter;

public class GatewayHistoryRepositoryTest {

	private static final String RESPONSE = "Kylie is nice";

	private static final String URL_TEMPLATE = "urlTemplate";

	private final RestOperations restOperations = Mockito.mock(RestOperations.class);

	private final GatewayHistoryRepository gatewayHistoryRepository = new GatewayHistoryRepositoryImpl(restOperations);

	private final Map<String, String> parameters = new HashMap<>();

	private final GatewayParameter gatewayParameter = Mockito.mock(GatewayParameter.class);

	@SuppressWarnings("unchecked")
	private final ResponseEntity<String> entity = Mockito.mock(ResponseEntity.class);

	@Before
	public final void setup() {
		parameters.put("query", "NYSE:KO");
		Mockito.when(gatewayParameter.parameters()).thenReturn(parameters);
		Mockito.when(gatewayParameter.urlTemplate()).thenReturn(URL_TEMPLATE);

		Mockito.when(restOperations.getForEntity(URL_TEMPLATE, String.class, parameters)).thenReturn(entity);
		Mockito.when(restOperations.getForObject(URL_TEMPLATE, String.class, parameters)).thenReturn(RESPONSE);
	}

	@Test()
	public final void history() {
		Assert.assertEquals(entity, gatewayHistoryRepository.history(gatewayParameter));

		Mockito.verify(restOperations).getForEntity(URL_TEMPLATE, String.class, parameters);
	}
	
	@Test()
	public final void historyAsString() {
		Assert.assertEquals(RESPONSE, gatewayHistoryRepository.historyAsString(gatewayParameter));
		
		Mockito.verify(restOperations).getForObject(URL_TEMPLATE, String.class, parameters);
	}

}
