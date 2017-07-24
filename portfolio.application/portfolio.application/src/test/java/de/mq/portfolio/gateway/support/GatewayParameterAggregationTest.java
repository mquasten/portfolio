package de.mq.portfolio.gateway.support;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.gateway.GatewayParameterAggregation;
import de.mq.portfolio.share.Share;

public class GatewayParameterAggregationTest {

	private final GatewayParameter gatewayParameter = Mockito.mock(GatewayParameter.class);

	private GatewayParameterAggregation<Share> gatewayParameterAggregation;

	private final Share share = Mockito.mock(Share.class);

	@Before
	public final void setup() {
		Mockito.when(gatewayParameter.gateway()).thenReturn(Gateway.GoogleRateHistory);

		gatewayParameterAggregation = new GatewayParameterAggregationImpl<>(share, Arrays.asList(gatewayParameter));
	}

	@Test
	public final void gatewayParameter() {
		Assert.assertEquals(gatewayParameter, gatewayParameterAggregation.gatewayParameter(Gateway.GoogleRateHistory));
	}

	@Test
	public final void domain() {
		Assert.assertEquals(share, gatewayParameterAggregation.domain());
	}

	@Test
	public final void gatewayParameters() {
		final Collection<GatewayParameter> gatewayParameters = gatewayParameterAggregation.gatewayParameters();
		Assert.assertEquals(1, gatewayParameters.size());
		Assert.assertEquals(gatewayParameter, gatewayParameters.stream().findAny().get());
	}

	@Test(expected = IllegalArgumentException.class)
	public final void createAtLeastOnParameterRequired() {
		new GatewayParameterAggregationImpl<>(share, Arrays.asList());
	}
}
