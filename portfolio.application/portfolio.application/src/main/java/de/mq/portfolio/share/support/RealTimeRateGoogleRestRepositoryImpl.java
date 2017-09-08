package de.mq.portfolio.share.support;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestOperations;

import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.gateway.GatewayParameterAggregation;
import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;

@Repository
class RealTimeRateGoogleRestRepositoryImpl  implements RealTimeRateRepository{
	
	private final RestOperations restOperations;
	RealTimeRateGoogleRestRepositoryImpl(final RestOperations restOperations){
		this.restOperations=restOperations;
	}

	@Override
	public Collection<TimeCourse> rates(final GatewayParameterAggregation<Collection<Share>> gatewayParameterAggregation) {
		// TODO Auto-generated method stub
		
		final GatewayParameter gatewayParameter = gatewayParameterAggregation.gatewayParameter(Gateway.GoogleRealtimeRate);
		System.out.println(gatewayParameter.urlTemplate());
		
		System.out.println(gatewayParameter.parameters());
		final String result = restOperations.getForObject(gatewayParameter.urlTemplate(), String.class, gatewayParameter.parameters());
		
		System.out.println(result);
		
		
		System.out.println("Hier kommt der Code...");
		System.out.println(restOperations);
		return Arrays.asList(new TimeCourseImpl(null, Arrays.asList(), Arrays.asList()));
	}

	@Override
	public Gateway supports(final Collection< Share> shares) {
		return Gateway.GoogleRealtimeRate;
	}

}
