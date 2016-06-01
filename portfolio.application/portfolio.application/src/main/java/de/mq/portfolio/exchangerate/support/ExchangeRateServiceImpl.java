package de.mq.portfolio.exchangerate.support;

import java.util.Collection;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.exchangerate.ExchangeRateCalculator;
import de.mq.portfolio.share.Data;

@Service("exchangeRateService")
public class ExchangeRateServiceImpl implements ExchangeRateService {
	
	private final ExchangeRateDatebaseRepository exchangeRateDatebaseRepository;
	
	private final ExchangeRateRepository exchangeRateRepository;
	
	private final Class< ? extends ExchangeRateCalculatorBuilder> builderClass=ExchangeRateCalculatorBuilderImpl.class; 
	
	@Autowired
	public ExchangeRateServiceImpl(final ExchangeRateDatebaseRepository exchangeRateDatebaseRepository, final ExchangeRateRepository exchangeRateRepository) {
		this.exchangeRateDatebaseRepository = exchangeRateDatebaseRepository;
		this.exchangeRateRepository=exchangeRateRepository;
	}
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.exchangerate.support.ExchangeRateService#exchangeRate(de.mq.portfolio.exchangerate.ExchangeRate)
	 */
	@Override
	public final ExchangeRate exchangeRate(final ExchangeRate exchangeRate){
		final Collection<Data> rates = exchangeRateRepository.history(exchangeRate.link());
		exchangeRate.assign(rates);
		return exchangeRate;
	}

	/* (non-Javadoc)
	 * @see de.mq.portfolio.exchangerate.support.ExchangeRateService#save(de.mq.portfolio.exchangerate.ExchangeRate)
	 */
	@Override
	public final void save(final ExchangeRate exchangeRate) {
		exchangeRateDatebaseRepository.save(exchangeRate);
	}
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.exchangerate.support.ExchangeRateService#exchangeRateCalculator(java.util.Collection)
	 */
	@Override
	public final ExchangeRateCalculator exchangeRateCalculator(final Collection<ExchangeRate> exchangerates) {
		return BeanUtils.instantiateClass(builderClass).withExchangeRates(exchangeRateDatebaseRepository.exchangerates(exchangerates)).build();
	}
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.exchangerate.support.ExchangeRateService#exchangeRateCalculator()
	 */
	@Override
	public final ExchangeRateCalculator exchangeRateCalculator() {
		return BeanUtils.instantiateClass(builderClass).withExchangeRates(exchangeRateDatebaseRepository.exchangerates()).build();
	}

}
