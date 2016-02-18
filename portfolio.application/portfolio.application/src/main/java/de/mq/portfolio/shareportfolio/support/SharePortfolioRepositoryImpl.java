package de.mq.portfolio.shareportfolio.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import de.mq.portfolio.shareportfolio.PortfolioOptimisation;
import de.mq.portfolio.shareportfolio.SharePortfolio;

@Repository
class SharePortfolioRepositoryImpl implements SharePortfolioRepository {
	
	private final MongoOperations mongoOperations;
	
	@Autowired
	SharePortfolioRepositoryImpl(final MongoOperations mongoOperations) {
	
		this.mongoOperations = mongoOperations;
	}

	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.shareportfolio.support.SharePortfolioRepository#portfolio(java.lang.String)
	 */
	@Override
	public final SharePortfolio portfolio(final String name ) {
		return DataAccessUtils.requiredSingleResult(mongoOperations.find(new Query(Criteria.where("name").is(name)), SharePortfolioImpl.class));
	}
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.shareportfolio.support.SharePortfolioRepository#save(de.mq.portfolio.shareportfolio.SharePortfolio)
	 */
	@Override
	public final void save(final SharePortfolio sharePortfolio ) {
		mongoOperations.save(sharePortfolio);
	}


	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.shareportfolio.support.SharePortfolioRepository#save(de.mq.portfolio.shareportfolio.PortfolioOptimisation)
	 */
	@Override
	public void save(final PortfolioOptimisation portfolioOptimisation) {
		mongoOperations.save(portfolioOptimisation);
		
	}
	
	@Override
	public final Optional<PortfolioOptimisation> minVariance(final String name) {
		
		Aggregation agg = Aggregation.newAggregation(
				Aggregation.match(Criteria.where("portfolio").is(name)),
				Aggregation.group("portfolio").min("variance").as("variance"),
				Aggregation.project("variance").and("portfolio").previousOperation()
				
					
			);
		
		AggregationResults<? extends PortfolioOptimisation> groupResults  = mongoOperations.aggregate(agg, PortfolioOptimisationImpl.class, PortfolioOptimisationImpl.class);
		final List<? extends PortfolioOptimisation> aggregationResults = groupResults.getMappedResults();
	
		
		
		final Collection<PortfolioOptimisation> results = new ArrayList<>();
		aggregationResults.forEach(r -> {
			final Query query =new Query(Criteria.where("variance").lte(r.variance()));
			results.addAll(mongoOperations.find(query, PortfolioOptimisationImpl.class));
		});
	
		return Optional.ofNullable(DataAccessUtils.singleResult(results));
		
	}

}
