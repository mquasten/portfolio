package de.mq.portfolio.gateway.support;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanUtils;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.gateway.GatewayParameterAggregation;
import de.mq.portfolio.share.Share;

public class ShareGatewayParameterServiceTest {

	private static final String CODE = "JNJ";

	private final GatewayParameterRepository gatewayParameterRepository = Mockito.mock(GatewayParameterRepository.class);

	private final ShareGatewayParameterServiceImpl shareGatewayParameterService = Mockito.mock(ShareGatewayParameterServiceImpl.class, Mockito.CALLS_REAL_METHODS);

	private final Share share = Mockito.mock(Share.class);

	private final GatewayParameter gatewayParameter = Mockito.mock(GatewayParameter.class);
	private final GatewayHistoryRepository gatewayHistoryRepository = Mockito.mock(GatewayHistoryRepository.class);
	private final Map<Class<?>, Object> dependencies = new HashMap<>();

	@Before
	public final void setup() {

		dependencies.put(GatewayParameterRepository.class, gatewayParameterRepository);
		dependencies.put(GatewayHistoryRepository.class, gatewayHistoryRepository);
		Arrays.asList(ShareGatewayParameterServiceImpl.class.getDeclaredFields()).stream().filter(field -> dependencies.containsKey(field.getType())).forEach(field -> ReflectionTestUtils.setField(shareGatewayParameterService, field.getName(), dependencies.get(field.getType())));

		Mockito.when(gatewayParameter.gateway()).thenReturn(Gateway.GoogleRateHistory);

		Mockito.when(share.code()).thenReturn(CODE);
		Mockito.when(gatewayParameterRepository.gatewayParameter(Gateway.GoogleRateHistory, CODE)).thenReturn(gatewayParameter);
		Mockito.doAnswer(answer -> {
			return new GatewayParameterAggregationBuilderImpl<>();
		}).when(shareGatewayParameterService).gatewayParameterAggregationBuilder();

		Mockito.when(gatewayParameterRepository.gatewayParameters(CODE)).thenReturn(Arrays.asList(gatewayParameter));
	}

	@Test
	public final void gatewayParameter() {
		final GatewayParameterAggregation<Share> gatewayParameterAggregation = shareGatewayParameterService.gatewayParameter(share, Arrays.asList(Gateway.GoogleRateHistory));

		Assert.assertEquals(share, gatewayParameterAggregation.domain());
		Assert.assertEquals(1, gatewayParameterAggregation.gatewayParameters().size());
		Assert.assertEquals(gatewayParameter, gatewayParameterAggregation.gatewayParameters().stream().findAny().get());

	}

	@Test(expected = IllegalArgumentException.class)
	public final void gatewayParameterGatewaysEmpty() {
		shareGatewayParameterService.gatewayParameter(share, Arrays.asList());

	}

	@Test
	public final void gatewayParameters() {
		final GatewayParameterAggregation<Share> gatewayParameterAggregation = shareGatewayParameterService.gatewayParameters(share);

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

	    Assert.assertEquals(dependencies, Arrays.asList(ShareGatewayParameterServiceImpl.class.getDeclaredFields()).stream().filter(field -> dependencies.containsKey(field.getType())).map(field -> new AbstractMap.SimpleImmutableEntry<>(field.getType(), ReflectionTestUtils.getField(service, field.getName()))).collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue())));
	}
}
