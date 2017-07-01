package de.mq.portfolio.gateway.support;

import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;

public interface GatewayParameterRepository {
	
	GatewayParameter shareGatewayParameter(final Gateway gateway, final String ... keys);

}
