package de.mq.portfolio.shareportfolio.support;

import java.util.List;


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
	public final void minRisk(final String name) {
		
		Aggregation agg = Aggregation.newAggregation(
				Aggregation.match(Criteria.where("portfolio").is(name)),
				Aggregation.group("portfolio").min("risk").as("risk"),
				Aggregation.project("risk").and("portfolio").previousOperation()
				
					
			);
		
		AggregationResults<? extends PortfolioOptimisation> groupResults  = mongoOperations.aggregate(agg, PortfolioOptimisationImpl.class, PortfolioOptimisationImpl.class);
		final List<? extends PortfolioOptimisation> result = groupResults.getMappedResults();
	
		System.out.println(result.size());
		
		result.forEach(r -> {
			System.out.println(r.portfolio() + ":" + r.risk());
			
		});
	
		
	}

}
