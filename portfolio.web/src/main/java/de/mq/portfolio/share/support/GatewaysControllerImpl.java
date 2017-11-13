package de.mq.portfolio.share.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.mq.portfolio.gateway.ShareGatewayParameterService;

@Component("gatewaysController")
@Scope("singleton")
public class GatewaysControllerImpl {
	
	private final ShareGatewayParameterService shareGatewayParameterService; 
	
	@Autowired
	GatewaysControllerImpl(ShareGatewayParameterService shareGatewayParameterService) {
		this.shareGatewayParameterService = shareGatewayParameterService;
	}

	public void init(final GatewaysAO gatewaysAO) {
		System.out.println("*** GatewaysControllerImpl.init() ***");
		System.out.println(shareGatewayParameterService);
	}

}
