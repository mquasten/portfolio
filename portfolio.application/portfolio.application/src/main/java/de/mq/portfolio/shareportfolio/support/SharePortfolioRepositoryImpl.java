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
import org.springframework.util.ReflectionUtils;

import de.mq.portfolio.shareportfolio.PortfolioOptimisation;
import de.mq.portfolio.shareportfolio.SharePortfolio;

@Repository
class SharePortfolioRepositoryImpl implements SharePortfolioRepository {

	static final String SAMPLES_FIELD = "samples";
	static final String VARIANCE_FIELD = "variance";
	static final String PORTFOLIO_FIELD = "portfolio";
	static final String NAME_FIELD = "name";
	private final MongoOperations mongoOperations;

	@Autowired
	SharePortfolioRepositoryImpl(final MongoOperations mongoOperations) {

		this.mongoOperations = mongoOperations;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.mq.portfolio.shareportfolio.support.SharePortfolioRepository#portfolio
	 * (java.lang.String)
	 */
	@Override
	public final SharePortfolio portfolio(final String name) {
		return DataAccessUtils.requiredSingleResult(mongoOperations.find(new Query(Criteria.where(NAME_FIELD).is(name)), SharePortfolioImpl.class));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.mq.portfolio.shareportfolio.support.SharePortfolioRepository#save(de
	 * .mq.portfolio.shareportfolio.SharePortfolio)
	 */
	@Override
	public final void save(final SharePortfolio sharePortfolio) {
		mongoOperations.save(sharePortfolio);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.mq.portfolio.shareportfolio.support.SharePortfolioRepository#save(de
	 * .mq.portfolio.shareportfolio.PortfolioOptimisation)
	 */
	@Override
	public void save(final PortfolioOptimisation portfolioOptimisation) {
		mongoOperations.save(portfolioOptimisation);

	}

	@Override
	public final Optional<PortfolioOptimisation> minVariance(final String name) {

		Aggregation agg = Aggregation.newAggregation(Aggregation.match(Criteria.where(PORTFOLIO_FIELD).is(name)), Aggregation.group(PORTFOLIO_FIELD).min(VARIANCE_FIELD).as(VARIANCE_FIELD).sum(SAMPLES_FIELD).as(SAMPLES_FIELD), Aggregation.project(VARIANCE_FIELD, SAMPLES_FIELD).and(PORTFOLIO_FIELD).previousOperation()

		);

		AggregationResults<? extends PortfolioOptimisation> groupResults = mongoOperations.aggregate(agg, PortfolioOptimisationImpl.class, PortfolioOptimisationImpl.class);

		final List<? extends PortfolioOptimisation> aggregationResults = groupResults.getMappedResults();

		final Collection<PortfolioOptimisation> results = new ArrayList<>();
		aggregationResults.forEach(r -> {

			final Query query = new Query(Criteria.where(VARIANCE_FIELD).lte(r.variance()));
			final List<? extends PortfolioOptimisation> existing = mongoOperations.find(query, PortfolioOptimisationImpl.class);
			existing.stream().forEach(result -> {
				ReflectionUtils.doWithFields(result.getClass(), field -> {
					field.setAccessible(true);
					ReflectionUtils.setField(field, result, r.samples());
				}, field -> field.getName().equals(SAMPLES_FIELD));
			});
			results.addAll(existing);
		});

		return Optional.ofNullable(DataAccessUtils.singleResult(results));

	}

}
