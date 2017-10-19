package de.mq.portfolio.gateway.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.gateway.ExchangeRateGatewayParameterService;
import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.Gateway.GatewayGroup;
import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.gateway.GatewayParameterAggregation;

@Service
abstract class AbstractExchangeRateGatewayParameterService implements ExchangeRateGatewayParameterService {

	private final GatewayParameterRepository gatewayParameterRepository;

	private final GatewayHistoryRepository gatewayHistoryRepository;

	@Autowired
	AbstractExchangeRateGatewayParameterService(final GatewayParameterRepository gatewayParameterRepository, final GatewayHistoryRepository gatewayHistoryRepository) {
		this.gatewayParameterRepository = gatewayParameterRepository;
		this.gatewayHistoryRepository = gatewayHistoryRepository;
	}

	@Override
	public GatewayParameterAggregation<ExchangeRate> aggregationForRequiredGateway(final ExchangeRate exchangeRate, final Gateway gateway) {
		exchangeRateMandatoryGurad(exchangeRate);
		gatewayGroupGuard(gateway);
	
		final GatewayParameter gatewayParameter = gatewayParameterRepository.gatewayParameter(gateway, exchangeRate.source(), exchangeRate.target());
		return builder().withDomain(exchangeRate).withGatewayParameter(gatewayParameter).build();

	}

	private void gatewayGroupGuard(final Gateway gateway) {
		Assert.notNull(gateway, "Gateway is mandatory.");
		Assert.notNull(gateway.gatewayGroup(), "GatewayGroup is mandatory.");
		Assert.isTrue(gateway.gatewayGroup() == GatewayGroup.ExchangeRate, String.format("Wrong GatewayGroup %s, GatewayGroup must by %s.", gateway.gatewayGroup(), GatewayGroup.ExchangeRate));
	}

	private void exchangeRateMandatoryGurad(final ExchangeRate exchangeRate) {
		Assert.notNull(exchangeRate, "ExchangeRate is mandatory.");
		Assert.hasText(exchangeRate.source(), "Source is mandatory.");
		Assert.hasText(exchangeRate.target(), "Target is mandatory.");
	}

	@Override
	public GatewayParameterAggregation<ExchangeRate> aggregationForAllGateways(final ExchangeRate exchangeRate) {
		exchangeRateMandatoryGurad(exchangeRate);
		final Collection<GatewayParameter> gatewayParameters = gatewayParameterRepository.gatewayParameters(exchangeRate.source(), exchangeRate.target());
		
		gatewayParameters.stream().map(gatewayParameter -> gatewayParameter.gateway()).forEach(gateway -> gatewayGroupGuard(gateway));
		return builder().withDomain(exchangeRate).withGatewayParameters(gatewayParameters).build();
	}

	@Override
	public String history(final GatewayParameter gatewayParameter) {
		return gatewayHistoryRepository.history(gatewayParameter).getBody();
	}
	
	@Override
	public GatewayParameterAggregation<Collection<ExchangeRate>>   merge(final Collection<ExchangeRate> exhangerates, final Gateway gateway) {
		final Collection<GatewayParameter> gatewayParameters = exhangerates.stream().map(exhangerate -> gatewayParameterRepository.gatewayParameter(gateway, exhangerate.source() +"-" + exhangerate.target())).collect(Collectors.toList());
			
		final int keySize =  DataAccessUtils.requiredSingleResult(gatewayParameters.stream().map(gatewayParameter -> Gateway.ids(gateway.id(gatewayParameter.code())).size()).collect(Collectors.toSet()));
		
		Assert.isTrue(keySize == 3, "Key should have 3 columns.");
		//IntStream.range(0, keySize-2).forEach(i -> keys.add(StringUtils.collectionToCommaDelimitedString(gatewayParameters.stream().map(gatewayParameter -> Arrays.asList(gatewayParameter.code()).get(i)).collect(Collectors.toList()))));
		//keys.addAll(gatewayParameters.stream().map(gatewayParameter -> gatewayParameter.code()).collect(Collectors.toList()));
		final String key = StringUtils.collectionToDelimitedString(gatewayParameters.stream().map(gatewayParameter -> gatewayParameter.code()).collect(Collectors.toList()), "|");
		
	
		final String urlTemplate = DataAccessUtils.requiredSingleResult(gatewayParameters.stream().map(GatewayParameter::urlTemplate).map(StringUtils::trimAllWhitespace).collect(Collectors.toSet()));
		final Map<String, Collection<String>> parameters  = new HashMap<>();
		gatewayParameters.forEach(gatewayParameter-> gatewayParameter.parameters().keySet().forEach(name -> parameters.put(name, new ArrayList<>())));
		gatewayParameters.forEach(gatewayParameter-> gatewayParameter.parameters().entrySet().forEach(entry -> parameters.get(entry.getKey()).add(entry.getValue())));
		final Collection<String> mergedParameters = parameters.entrySet().stream().map(entry -> String.format("%s:'%s'", entry.getKey(), StringUtils.collectionToCommaDelimitedString(entry.getValue()))).collect(Collectors.toList());
		
		final String spEl = String.format("{%s}",StringUtils.collectionToCommaDelimitedString(mergedParameters));
		
		return aggregateBuilder().withGatewayParameter(new GatewayParameterImpl(key, gateway, urlTemplate, spEl)).withDomain(exhangerates).build();
		
	}
	
	
	@Lookup
	abstract <T>  GatewayParameterAggregationBuilder<T> gatewayParameterAggregationBuilder();
	
	
	private GatewayParameterAggregationBuilder<ExchangeRate> builder() {
		return  gatewayParameterAggregationBuilder();
	} 
	
	private GatewayParameterAggregationBuilder<Collection<ExchangeRate>> aggregateBuilder() {
		return  gatewayParameterAggregationBuilder();
	} 
	

}
