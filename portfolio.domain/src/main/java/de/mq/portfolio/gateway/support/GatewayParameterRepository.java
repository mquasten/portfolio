package de.mq.portfolio.gateway.support;

import java.util.Collection;

import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;

public interface GatewayParameterRepository {
	
	GatewayParameter gatewayParameter(final Gateway gateway, final String ... keys);
	
	
	Collection<GatewayParameter> gatewayParameters(final String ... keys);
	
	void save(final GatewayParameter gatewayParameter) ;
	
}
