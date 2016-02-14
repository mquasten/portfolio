package de.mq.portfolio.shareportfolio.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

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

}
