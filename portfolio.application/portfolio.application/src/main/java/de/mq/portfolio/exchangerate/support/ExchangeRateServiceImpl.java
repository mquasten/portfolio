package de.mq.portfolio.exchangerate.support;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Service;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.share.Data;

@Service("exchangeRateService")
public class ExchangeRateServiceImpl {
	
	private final MongoOperations mongoOperations;
	
	private final ExchangeRateRepository exchangeRateRepository;
	
	@Autowired
	public ExchangeRateServiceImpl(final MongoOperations mongoOperations, final ExchangeRateRepository exchangeRateRepository) {
		this.mongoOperations = mongoOperations;
		this.exchangeRateRepository=exchangeRateRepository;
	}
	
	public final ExchangeRate exchangeRate(final ExchangeRate exchangeRate){
		final Collection<Data> rates = exchangeRateRepository.history(exchangeRate.link());
		exchangeRate.assign(rates);
		return exchangeRate;
	}

	public final void save(final ExchangeRate exchangeRate) {
		mongoOperations.save(exchangeRate);
	}

}
