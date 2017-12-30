package de.mq.portfolio.exchangerate.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Service;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.exchangerate.ExchangeRateCalculator;
import de.mq.portfolio.gateway.ExchangeRateGatewayParameterService;
import de.mq.portfolio.gateway.GatewayParameterAggregation;
import de.mq.portfolio.share.Data;

@Service("exchangeRateService")
abstract class AbstractExchangeRateService implements ExchangeRateService {

	private final ExchangeRateDatebaseRepository exchangeRateDatebaseRepository;

	private final ExchangeRateRepository exchangeRateRepository;

	private final RealtimeExchangeRateRepository realtimeExchangeRateRepository;

	private final ExchangeRateGatewayParameterService exchangeRateGatewayParameterService;

	@Autowired
	AbstractExchangeRateService(final ExchangeRateDatebaseRepository exchangeRateDatebaseRepository, final ExchangeRateRepository exchangeRateRepository, final RealtimeExchangeRateRepository realtimeExchangeRateRepository,
			final ExchangeRateGatewayParameterService exchangeRateGatewayParameterService) {
		this.exchangeRateDatebaseRepository = exchangeRateDatebaseRepository;
		this.exchangeRateRepository = exchangeRateRepository;
		this.realtimeExchangeRateRepository = realtimeExchangeRateRepository;
		this.exchangeRateGatewayParameterService = exchangeRateGatewayParameterService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.mq.portfolio.exchangerate.support.ExchangeRateService#exchangeRate(de.
	 * mq.portfolio.exchangerate.ExchangeRate)
	 */
	@Override
	public final ExchangeRate exchangeRate(final ExchangeRate exchangeRate) {
		final Collection<Data> rates = exchangeRateRepository.history(exchangeRateGatewayParameterService.aggregationForRequiredGateway(exchangeRate, exchangeRateRepository.supports()));
		exchangeRate.assign(rates);
		return exchangeRate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.mq.portfolio.exchangerate.support.ExchangeRateService#exchangeRates()
	 */
	@Override
	public final Collection<ExchangeRate> exchangeRates() {
		return exchangeRateDatebaseRepository.exchangerates();
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.exchangerate.support.ExchangeRateService#exchangeRateOrReverse(de.mq.portfolio.exchangerate.ExchangeRate)
	 */
	@Override
	public Optional<ExchangeRate> exchangeRateOrReverse(final ExchangeRate exchangeRate ) {
		final Collection<ExchangeRate> results = new ArrayList<>();
		results.addAll(exchangeRateDatebaseRepository.exchangerates(Arrays.asList(exchangeRate)));
		if( results.size() == 0){
			results.addAll(exchangeRateDatebaseRepository.exchangerates(Arrays.asList(new ExchangeRateImpl(exchangeRate.target(), exchangeRate.source()))));
		}
		if( results.isEmpty()){
			return Optional.empty();
		}
		return results.stream().findFirst();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.portfolio.exchangerate.support.ExchangeRateService#save(de.mq.
	 * portfolio.exchangerate.ExchangeRate)
	 */
	@Override
	public final void save(final ExchangeRate exchangeRate) {
		exchangeRateDatebaseRepository.save(exchangeRate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.portfolio.exchangerate.support.ExchangeRateService#
	 * exchangeRateCalculator(java.util.Collection)
	 */
	@Override
	public final ExchangeRateCalculator exchangeRateCalculator(final Collection<ExchangeRate> exchangerates) {
		return newBuilder().withExchangeRates(exchangeRateDatebaseRepository.exchangerates(exchangerates)).build();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.portfolio.exchangerate.support.ExchangeRateService#
	 * exchangeRateCalculator()
	 */
	@Override
	public final ExchangeRateCalculator exchangeRateCalculator() {
		return newBuilder().withExchangeRates(exchangeRateDatebaseRepository.exchangerates()).build();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.mq.portfolio.exchangerate.support.ExchangeRateService#exchangeRates(
	 * java.util.Collection)
	 */
	@Override
	public final Collection<ExchangeRate> exchangeRates(Collection<ExchangeRate> exchangeRates) {
		return exchangeRateDatebaseRepository.exchangerates(exchangeRates);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.portfolio.exchangerate.support.ExchangeRateService#
	 * realTimeExchangeRates(java.util.Collection)
	 */
	@Override
	public final Collection<ExchangeRate> realTimeExchangeRates(final Collection<ExchangeRate> exchangeRates) {
		final GatewayParameterAggregation<Collection<ExchangeRate>> gatewayParameterAggregation = exchangeRateGatewayParameterService.merge(exchangeRates, realtimeExchangeRateRepository.supports(exchangeRates));
		return realtimeExchangeRateRepository.exchangeRates(gatewayParameterAggregation);
	}

	@Lookup
	abstract ExchangeRateCalculatorBuilder newBuilder();
}
