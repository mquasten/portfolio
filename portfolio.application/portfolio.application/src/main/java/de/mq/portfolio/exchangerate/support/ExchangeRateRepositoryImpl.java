package de.mq.portfolio.exchangerate.support;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import org.springframework.web.client.RestOperations;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.gateway.GatewayParameterAggregation;
import de.mq.portfolio.share.Data;
import de.mq.portfolio.share.support.DataImpl;

@Repository
class ExchangeRateRepositoryImpl implements ExchangeRateRepository {
	
	private final RestOperations restOperations;
	
	@Autowired
	public ExchangeRateRepositoryImpl(final RestOperations restOperations) {
		this.restOperations = restOperations;
	}


	@Override
	public final Collection<Data> history(GatewayParameterAggregation<ExchangeRate> gatewayParameterAggregation) {
		final String[] last = {null };
		final GatewayParameter gatewayParameter = gatewayParameterAggregation.gatewayParameter(Gateway.CentralBankExchangeRates);
		
		
		return Collections.unmodifiableList(Arrays.asList(restOperations.getForObject(gatewayParameter.urlTemplate(), String.class, gatewayParameter.parameters()).split("\n")).stream().map(line -> line.split(";")).filter(cols -> cols.length>=2 && cols[0].matches("^[0-9]{4}.*")).map(cols-> {
			 if( !  cols[1].matches("[0-9,]+")  ) {
				 cols[1]=last[0];
			 } else {
				 last[0]=cols[1];
			 }
			
			return cols;
		}).filter(cols -> cols[1] != null).map(cols -> new DataImpl(cols[0], Double.valueOf(cols[1].replace(',', '.')))).collect(Collectors.toList()));
		
		
	}


	@Override
	public Gateway supports() {
		return Gateway.CentralBankExchangeRates;
	}

}
