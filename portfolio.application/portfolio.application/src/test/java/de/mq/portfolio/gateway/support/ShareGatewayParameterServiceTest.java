package de.mq.portfolio.gateway.support;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpEntity;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.gateway.GatewayParameterAggregation;
import de.mq.portfolio.share.Share;

public class ShareGatewayParameterServiceTest {

	private static final String QUERY_PARAMETER_NAME = "query";

	private static final String URL = "url?s={query}";

	private static final String RESPONSE = "Response";

	private static final String CODE = "JNJ";

	private final GatewayParameterRepository gatewayParameterRepository = Mockito.mock(GatewayParameterRepository.class);

	private final AbstractShareGatewayParameterService shareGatewayParameterService = Mockito.mock(AbstractShareGatewayParameterService.class, Mockito.CALLS_REAL_METHODS);

	private final Share share = Mockito.mock(Share.class);

	private final GatewayParameter gatewayParameter = Mockito.mock(GatewayParameter.class);
	private final GatewayHistoryRepository gatewayHistoryRepository = Mockito.mock(GatewayHistoryRepository.class);
	private final Map<Class<?>, Object> dependencies = new HashMap<>();

	@Before
	public final void setup() {

		dependencies.put(GatewayParameterRepository.class, gatewayParameterRepository);
		dependencies.put(GatewayHistoryRepository.class, gatewayHistoryRepository);
		Arrays.asList(AbstractShareGatewayParameterService.class.getDeclaredFields()).stream().filter(field -> dependencies.containsKey(field.getType())).forEach(field -> ReflectionTestUtils.setField(shareGatewayParameterService, field.getName(), dependencies.get(field.getType())));

		Mockito.when(gatewayParameter.gateway()).thenReturn(Gateway.GoogleRateHistory);

		Mockito.when(share.code()).thenReturn(CODE);
		Mockito.when(gatewayParameterRepository.gatewayParameter(Gateway.GoogleRateHistory, CODE)).thenReturn(gatewayParameter);
		Mockito.doAnswer(answer -> {
			return new GatewayParameterAggregationBuilderImpl<>();
		}).when(shareGatewayParameterService).gatewayParameterAggregationBuilder();

		Mockito.when(gatewayParameterRepository.gatewayParameters(CODE)).thenReturn(Arrays.asList(gatewayParameter));
	}

	@Test
	public final void aggregationForRequiredGateways() {
		final GatewayParameterAggregation<Share> gatewayParameterAggregation = shareGatewayParameterService.aggregationForRequiredGateways(share, Arrays.asList(Gateway.GoogleRateHistory));

		Assert.assertEquals(share, gatewayParameterAggregation.domain());
		Assert.assertEquals(1, gatewayParameterAggregation.gatewayParameters().size());
		Assert.assertEquals(gatewayParameter, gatewayParameterAggregation.gatewayParameters().stream().findAny().get());

	}

	@Test(expected = IllegalArgumentException.class)
	public final void aggregationForRequiredGatewaysEmpty() {
		shareGatewayParameterService.aggregationForRequiredGateways(share, Arrays.asList());

	}

	@Test
	public final void aggregationForAllGateways() {
		final GatewayParameterAggregation<Share> gatewayParameterAggregation = shareGatewayParameterService.aggregationForAllGateways(share);

		Assert.assertEquals(share, gatewayParameterAggregation.domain());
		Assert.assertEquals(1, gatewayParameterAggregation.gatewayParameters().size());
		Assert.assertEquals(gatewayParameter, gatewayParameterAggregation.gatewayParameters().stream().findAny().get());
	}

	@Test
	public final void save() {
		shareGatewayParameterService.save(gatewayParameter);

		Mockito.verify(gatewayParameterRepository).save(gatewayParameter);
	}

	@Test
	public final void dependencies() throws BeanInstantiationException, NoSuchMethodException, SecurityException {

		final Object service = BeanUtils.instantiateClass(shareGatewayParameterService.getClass().getDeclaredConstructor(GatewayParameterRepository.class, GatewayHistoryRepository.class), gatewayParameterRepository, gatewayHistoryRepository);

		Assert.assertEquals(dependencies, Arrays.asList(AbstractShareGatewayParameterService.class.getDeclaredFields()).stream().filter(field -> dependencies.containsKey(field.getType()))
				.map(field -> new AbstractMap.SimpleImmutableEntry<>(field.getType(), ReflectionTestUtils.getField(service, field.getName()))).collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue())));
	}

	@Test
	public final void history() {
		@SuppressWarnings("unchecked")
		final HttpEntity<String> entity = Mockito.mock(HttpEntity.class);
		Mockito.when(entity.getBody()).thenReturn(RESPONSE);
		Mockito.when(gatewayHistoryRepository.history(gatewayParameter)).thenReturn(entity);

		Assert.assertEquals(RESPONSE, shareGatewayParameterService.history(gatewayParameter));
		Mockito.verify(gatewayHistoryRepository).history(gatewayParameter);
	}
	@Test
	public final void merge() {
		
		final Share jnj = prepareForShare(CODE, "url?s={query }");
		final Share pg = prepareForShare("PG", "url?s={ query }");
		final Share ko = prepareForShare("KO", "url?s={\r    query  \r    }");
		final Share sap = prepareForShare("SAP.DE","url?s={\tquery\t}"	);
		final Share vz = prepareForShare("VZ",  "url?s={\t\n query \t\n}" );
	
		
		final GatewayParameterAggregation<Collection<Share>>  aggregation = shareGatewayParameterService.merge(Arrays.asList(jnj, pg,ko, sap, vz), Gateway.YahooRealtimeRate);
		final String code = "JNJ,PG,KO,SAP.DE,VZ";
		
		final GatewayParameter mergedGatewayParameter = aggregation.gatewayParameter(Gateway.YahooRealtimeRate);
		Assert.assertEquals(code, mergedGatewayParameter.code());
		Assert.assertEquals(URL, mergedGatewayParameter.urlTemplate());
		Assert.assertEquals(Gateway.YahooRealtimeRate, mergedGatewayParameter.gateway());
		Assert.assertEquals(1, mergedGatewayParameter.parameters().size());
		
		Assert.assertEquals(QUERY_PARAMETER_NAME, mergedGatewayParameter.parameters().keySet().stream().findAny().get());
		Assert.assertEquals(code, mergedGatewayParameter.parameters().values().stream().findAny().get());
		
		Assert.assertEquals(5, aggregation.domain().size());
		final List<Share> aggregatedShares = new ArrayList<>(aggregation.domain());
		Assert.assertEquals(jnj, aggregatedShares.get(0));
		Assert.assertEquals(pg, aggregatedShares.get(1));
		Assert.assertEquals(ko, aggregatedShares.get(2));
		Assert.assertEquals(sap, aggregatedShares.get(3));
		Assert.assertEquals(vz, aggregatedShares.get(4));
		
	}

	private Share prepareForShare(final String code, final String url) {
		final Share share = Mockito.mock(Share.class);
		final GatewayParameter gatewayParameter = Mockito.mock(GatewayParameter.class);
		Mockito.when(gatewayParameter.code()).thenReturn(code);
		
		Mockito.when(share.code()).thenReturn(code);
		Mockito.when(gatewayParameter.urlTemplate()).thenReturn(url);
		Mockito.when(gatewayParameterRepository.gatewayParameter(Gateway.YahooRealtimeRate, code)).thenReturn(gatewayParameter);
		final Map<String,String> parameters = new HashMap<>();
		parameters.put(QUERY_PARAMETER_NAME, code);
		Mockito.when(gatewayParameter.parameters()).thenReturn(parameters);
		return share;
	}
}
