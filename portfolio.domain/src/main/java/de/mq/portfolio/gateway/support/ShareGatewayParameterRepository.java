package de.mq.portfolio.gateway.support;

import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.ShareGatewayParameter;

public interface ShareGatewayParameterRepository {
	
	ShareGatewayParameter shareGatewayParameter(final Gateway gateway, final String ... keys);

}
