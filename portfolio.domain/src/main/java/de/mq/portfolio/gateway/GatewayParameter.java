package de.mq.portfolio.gateway;

import java.util.Map;

public interface GatewayParameter {

	String code();
	
	Gateway gateway();

	Map<String, String> parameters();

	String urlTemplate();

}