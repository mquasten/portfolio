package de.mq.portfolio.gateway.support;

import org.springframework.http.HttpEntity;

import de.mq.portfolio.gateway.GatewayParameter;

public interface GatewayHistoryRepository {

	HttpEntity<String> history(final GatewayParameter gatewayParameter);
	
	String historyAsString(final GatewayParameter gatewayParameter);

}