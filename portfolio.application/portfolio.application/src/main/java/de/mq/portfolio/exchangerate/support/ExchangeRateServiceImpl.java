package de.mq.portfolio.exchangerate.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Service;

import de.mq.portfolio.exchangerate.ExchangeRate;

@Service("exchangeRateService")
public class ExchangeRateServiceImpl {
	
	private final MongoOperations mongoOperations;
	
	@Autowired
	public ExchangeRateServiceImpl(final MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}

	public final void save(final ExchangeRate exchangeRate) {
		mongoOperations.save(exchangeRate);
	}

}
