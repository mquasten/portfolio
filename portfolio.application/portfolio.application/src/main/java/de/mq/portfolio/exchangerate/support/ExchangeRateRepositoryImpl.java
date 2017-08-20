package de.mq.portfolio.exchangerate.support;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.gateway.GatewayParameterAggregation;
import de.mq.portfolio.gateway.support.GatewayHistoryRepository;
import de.mq.portfolio.share.Data;
import de.mq.portfolio.share.support.DataImpl;

@Repository("exchangeRateRepository")
class ExchangeRateRepositoryImpl implements ExchangeRateRepository {
	

	private final GatewayHistoryRepository gatewayHistoryRepository;
	
	@Autowired
	public ExchangeRateRepositoryImpl(final GatewayHistoryRepository gatewayHistoryRepository) {
		this.gatewayHistoryRepository = gatewayHistoryRepository;
	}


	@Override
	public final Collection<Data> history(GatewayParameterAggregation<ExchangeRate> gatewayParameterAggregation) {
		final String[] last = {null };
		final GatewayParameter gatewayParameter = gatewayParameterAggregation.gatewayParameter(Gateway.CentralBankExchangeRates);
		
		return Collections.unmodifiableList(Arrays.asList(gatewayHistoryRepository.history(gatewayParameter).getBody().split("\n")).stream().map(line -> line.split(";")).filter(cols -> cols.length>=2 && cols[0].matches("^[0-9]{4}.*")).map(cols-> {
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
