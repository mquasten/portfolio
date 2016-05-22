package de.mq.portfolio.exchangerate.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.share.Data;

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
	
	@Override
	public final ExchangerateAggregate exchangerates(final ExchangeRate ... exchangerates) {
		
	
		Assert.assertNotNull(exchangerates);
		 final Map<ExchangeRate,Map<Date,Double>>  results = new HashMap<>();
		final Collection<ExchangeRate> rates = new HashSet<>();
		rates.addAll(Arrays.asList(exchangerates));
	
		rates.addAll(Arrays.asList(exchangerates).stream().map(er -> new ExchangeRateImpl(er.target(), er.source())).collect(Collectors.toSet()));
		rates.forEach(er -> {
			final Query query= Query.query(Criteria.where("source").is(er.source()).and("target").is(er.target()));
			
		    rates(query).stream().forEach(r -> {
		    	if( ! results.containsKey(er)){
		    		results.put(er, new HashMap<>());
		    	}
		    	results.get(er).put(r.date(), r.value());
		    
		    }
		    		
		    		
		    		);
		});
		
		return new ExchangerateAggregateImpl(results);
	}

	private Collection<Data> rates(final Query query) {
		final ExchangeRate result = mongoOperations.findOne(query, ExchangeRateImpl.class);
		final Collection<Data> data =  result == null ? new ArrayList<>() : result.rates();
		return data;
	}

}
