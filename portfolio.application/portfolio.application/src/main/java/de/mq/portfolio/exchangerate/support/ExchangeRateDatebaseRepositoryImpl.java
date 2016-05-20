package de.mq.portfolio.exchangerate.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Repository;

import de.mq.portfolio.exchangerate.ExchangeRate;

@Repository
class ExchangeRateDatebaseRepositoryImpl implements ExchangeRateDatebaseRepository {
	
	private final MongoOperations mongoOperations;

	@Autowired
	ExchangeRateDatebaseRepositoryImpl(final MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.exchangerate.support.ExchangeRateDatebaseRepository#save(de.mq.portfolio.exchangerate.ExchangeRate)
	 */
	@Override
	public final void save(final ExchangeRate exchangeRate) {
		mongoOperations.save(exchangeRate);
	}

}
