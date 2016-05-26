package de.mq.portfolio.exchangerate.support;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import de.mq.portfolio.exchangerate.ExchangeRate;



@Repository
class ExchangeRateDatebaseRepositoryImpl implements ExchangeRateDatebaseRepository {
	
	private static final String TARGET_FIELD_NAME = "target";
	private static final String SOURCE_FIELD_NAME = "source";
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
	
	@Override
	public final ExchangerateAggregate exchangerates(final Collection<ExchangeRate> exchangerates) {
		Assert.notNull(exchangerates);
		final Collection<ExchangeRate> rates = new HashSet<>();
		rates.addAll(exchangerates);
	
		rates.addAll(exchangerates.stream().map(er -> new ExchangeRateImpl(er.target(), er.source())).collect(Collectors.toSet()));
		final ExchangerateAggregateBuilder exchangerateAggregateBuilder = new ExchangerateAggregateBuilderImpl();
		rates.forEach(er -> mongoOperations.find(Query.query(Criteria.where(SOURCE_FIELD_NAME).is(er.source()).and(TARGET_FIELD_NAME).is(er.target())), ExchangeRateImpl.class).forEach(ex -> exchangerateAggregateBuilder.withExchangeRate(ex)));
		
		return exchangerateAggregateBuilder.build();
	}

	
}
