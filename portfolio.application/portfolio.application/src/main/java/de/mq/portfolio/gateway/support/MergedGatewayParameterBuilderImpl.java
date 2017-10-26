package de.mq.portfolio.gateway.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Scope;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;

@Component
@Scope("prototype")
class MergedGatewayParameterBuilderImpl implements MergedGatewayParameterBuilder {
	
	private	final Collection<GatewayParameter> gatewayParameters = new ArrayList<>(); 
	private Gateway gateway=null;
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.gateway.support.MergedGatewayParameterBuilder#withGatewayParameter(java.util.Collection)
	 */
	@Override
	public MergedGatewayParameterBuilder withGatewayParameter(final Collection<GatewayParameter> gatewayParameters) {
		Assert.isTrue(this.gatewayParameters.isEmpty(), "GatewayParameters already assigned.");
		Assert.notNull(gatewayParameters, "At least one GatewayParameter is required.");
		Assert.isTrue(!gatewayParameters.isEmpty(), "At least one GatewayParameter is required.");
		this.gatewayParameters.addAll(gatewayParameters);
		return this;
	}
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.gateway.support.MergedGatewayParameterBuilder#withGateway(de.mq.portfolio.gateway.Gateway)
	 */
	@Override
	public MergedGatewayParameterBuilder withGateway(Gateway gateway) {
		Assert.notNull(gateway, "Gateway is mandatory");
		Assert.isNull(this.gateway, "Gateway is already assigned.");
		this.gateway=gateway;
		return this;
	}
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.gateway.support.MergedGatewayParameterBuilder#build()
	 */
	@Override
	public GatewayParameter  build() {
		final int keySize =  DataAccessUtils.requiredSingleResult(gatewayParameters.stream().map(gatewayParameter -> Gateway.ids(gateway.id(gatewayParameter.code())).size()).collect(Collectors.toSet()));

		Assert.isTrue(keySize >=  2, "Key should have al least  2 columns.");
		
		
		final String key = StringUtils.collectionToDelimitedString(gatewayParameters.stream().map(gatewayParameter -> gatewayParameter.code()).collect(Collectors.toList()), ",");
		
	
		final String urlTemplate = DataAccessUtils.requiredSingleResult(gatewayParameters.stream().map(GatewayParameter::urlTemplate).map(StringUtils::trimAllWhitespace).collect(Collectors.toSet()));
		final Map<String, Collection<String>> parameters  = new HashMap<>();
		gatewayParameters.forEach(gatewayParameter-> gatewayParameter.parameters().keySet().forEach(name -> parameters.put(name, new ArrayList<>())));
		gatewayParameters.forEach(gatewayParameter-> gatewayParameter.parameters().entrySet().forEach(entry -> parameters.get(entry.getKey()).add(entry.getValue())));
		final Collection<String> mergedParameters = parameters.entrySet().stream().map(entry -> String.format("%s:'%s'", entry.getKey(), StringUtils.collectionToCommaDelimitedString(entry.getValue()))).collect(Collectors.toList());
		
		final String spEl = String.format("{%s}",StringUtils.collectionToCommaDelimitedString(mergedParameters));
		
		 return new GatewayParameterImpl(key, gateway, urlTemplate, spEl);
		
	}

}
