package de.mq.portfolio.gateway.support;

import org.springframework.http.HttpEntity;

import de.mq.portfolio.gateway.GatewayParameter;

interface GatewayHistoryRepository {

	HttpEntity<String> history(GatewayParameter gatewayParameter);

}