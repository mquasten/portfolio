package de.mq.portfolio.gateway.support;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.gateway.GatewayParameterAggregation;
import de.mq.portfolio.share.Share;

public class GatewayParameterAggregationBuilderTest {

	private final GatewayParameterAggregationBuilder<Share> gatewayParameterAggregationBuilder = new GatewayParameterAggregationBuilderImpl<>();
	private final GatewayParameter gatewayParameter = Mockito.mock(GatewayParameter.class);

	private final Share share = Mockito.mock(Share.class);

	@Before
	public final void setup() {
		Mockito.when(gatewayParameter.gateway()).thenReturn(Gateway.GoogleRateHistory);
	}

	@Test
	public final void build() {
		final GatewayParameterAggregation<Share> gatewayParameterAggregation = gatewayParameterAggregationBuilder.withDomain(share).withGatewayParameters(Arrays.asList(gatewayParameter)).build();
		Assert.assertEquals(share, gatewayParameterAggregation.domain());
		Assert.assertEquals(1, gatewayParameterAggregation.gatewayParameters().size());
		Assert.assertEquals(gatewayParameter, gatewayParameterAggregation.gatewayParameters().stream().findAny().get());
	}

	@Test(expected = IllegalArgumentException.class)
	public final void withGatewayParametersEmpty() {
		gatewayParameterAggregationBuilder.withGatewayParameters(Arrays.asList());
	}

	@Test(expected = IllegalArgumentException.class)
	public final void buildGatewayParametersEmpty() {
		gatewayParameterAggregationBuilder.withDomain(share).build();
	}

	@Test(expected = IllegalArgumentException.class)
	public final void withGatewayParameterAlreadyAssigned() {
		gatewayParameterAggregationBuilder.withGatewayParameter(gatewayParameter).withGatewayParameter(gatewayParameter);
	}

}
