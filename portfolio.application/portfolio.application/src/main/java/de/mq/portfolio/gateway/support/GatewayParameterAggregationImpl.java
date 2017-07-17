package de.mq.portfolio.gateway.support;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.gateway.GatewayParameterAggregation;


class GatewayParameterAggregationImpl<T> implements GatewayParameterAggregation<T> {
	
	private  final T domain;

	private  final Map<Gateway, GatewayParameter> gatewayParameters = new HashMap<>();;
	
	GatewayParameterAggregationImpl(final T domain, final Collection<GatewayParameter> gatewayParameters) {
		Assert.notNull(domain);
		Assert.isTrue(! CollectionUtils.isEmpty(gatewayParameters), "At least one GatewayParameter is required.");
		
		this.gatewayParameters.putAll(gatewayParameters.stream().collect(Collectors.toMap(gatewayParameter -> gatewayParameter.gateway() , gatewayParameter -> gatewayParameter)));
		this.domain = domain;
	
	}
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.gateway.support.GatewayParameterAggregation#domain()
	 */
	@Override
	public final T domain() {
		return domain;
	}

	/* (non-Javadoc)
	 * @see de.mq.portfolio.gateway.support.GatewayParameterAggregation#gatewayParameter()
	 */
	@Override
	public final Collection<GatewayParameter> gatewayParameters() {
		return Collections.unmodifiableCollection(gatewayParameters.values());
	}

	
	@Override
	public GatewayParameter gatewayParameter(final Gateway gateway) {
		Assert.notNull(gateway , "Gateway is mandatory.");
		Assert.isTrue(gatewayParameters.containsKey(gateway) , String.format("GatewayParameter not aware for %s", gateway));
		return gatewayParameters.get(gateway);
	}


	

}
