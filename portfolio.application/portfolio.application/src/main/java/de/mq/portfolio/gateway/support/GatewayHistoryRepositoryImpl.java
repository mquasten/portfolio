package de.mq.portfolio.gateway.support;

import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestOperations;

import de.mq.portfolio.gateway.GatewayParameter;

@Repository
class GatewayHistoryRepositoryImpl implements GatewayHistoryRepository {
	
	private final RestOperations restOperations;
	
	GatewayHistoryRepositoryImpl(final RestOperations restOperations) {
		this.restOperations = restOperations;
	}


	
	@Override
	public final  HttpEntity<String> history(final GatewayParameter gatewayParameter) {
		return restOperations.getForEntity(gatewayParameter.urlTemplate(),String.class, gatewayParameter.parameters());		
	}



	@Override
	public String historyAsString(GatewayParameter gatewayParameter) {
		return restOperations.getForObject(gatewayParameter.urlTemplate(), String.class, gatewayParameter.parameters());
	}
	
	

}
