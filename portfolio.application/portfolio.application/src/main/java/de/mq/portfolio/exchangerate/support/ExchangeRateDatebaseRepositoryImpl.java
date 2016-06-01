package de.mq.portfolio.exchangerate.support;

import java.util.Collection;
import java.util.Collections;
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
	
	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.exchangerate.support.ExchangeRateDatebaseRepository#exchangerates(java.util.Collection)
	 */
	@Override
	public final Collection<ExchangeRate> exchangerates(final Collection<ExchangeRate> exchangerates) {
		Assert.notNull(exchangerates);
		final Collection<ExchangeRate> rates = new HashSet<>();
		rates.addAll(exchangerates);
	
		rates.addAll(exchangerates.stream().map(er -> new ExchangeRateImpl(er.target(), er.source())).collect(Collectors.toSet()));
	
		final  Collection<ExchangeRate> results = new HashSet<>();
		rates.forEach(rate -> results.addAll(mongoOperations.find(Query.query(Criteria.where(SOURCE_FIELD_NAME).is(rate.source()).and(TARGET_FIELD_NAME).is(rate.target())), ExchangeRateImpl.class)));
		return Collections.unmodifiableCollection(results);
	}
	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.exchangerate.support.ExchangeRateDatebaseRepository#exchangerates()
	 */
	@Override
	public final Collection<ExchangeRate> exchangerates() {
		return Collections.unmodifiableCollection(mongoOperations.findAll(ExchangeRateImpl.class));
		
	}

	
}
