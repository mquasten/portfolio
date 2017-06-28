package de.mq.portfolio.gateway;

import java.util.Map;

public interface ShareGatewayParameter {

	String code();
	
	Gateway gateway();

	Map<String, String> parameters();

}