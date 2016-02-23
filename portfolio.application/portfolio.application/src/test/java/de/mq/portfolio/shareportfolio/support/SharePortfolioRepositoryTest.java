package de.mq.portfolio.shareportfolio.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import de.mq.portfolio.shareportfolio.PortfolioOptimisation;
import de.mq.portfolio.shareportfolio.SharePortfolio;

public class SharePortfolioRepositoryTest {

	private static final String NAME = "mq-test";

	final MongoOperations mongoOperations = Mockito.mock(MongoOperations.class);

	final SharePortfolioRepository sharePortfolioRepository = new SharePortfolioRepositoryImpl(mongoOperations);

	final ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);

	@SuppressWarnings("rawtypes")
	final ArgumentCaptor<Class> classCaptor = ArgumentCaptor.forClass(Class.class);

	final SharePortfolio sharePortfolio = Mockito.mock(SharePortfolio.class);

	final PortfolioOptimisation portfolioOptimisation = Mockito.mock(PortfolioOptimisation.class);

	@SuppressWarnings("unchecked")
	@Test
	public final void portfolio() {
		final List<SharePortfolio> results = new ArrayList<>();
		results.add(sharePortfolio);
		Mockito.when(mongoOperations.find(queryCaptor.capture(), classCaptor.capture())).thenReturn(results);
		Assert.assertEquals(sharePortfolio, sharePortfolioRepository.portfolio(NAME));

		Assert.assertEquals(SharePortfolioImpl.class, classCaptor.getValue());
		final Query query = queryCaptor.getValue();
		Assert.assertEquals(1, query.getQueryObject().keySet().stream().count());
		Assert.assertEquals(SharePortfolioRepositoryImpl.NAME_FIELD, query.getQueryObject().keySet().stream().findAny().get());
		Assert.assertEquals(NAME, query.getQueryObject().toMap().values().stream().findAny().get());
	}

	@Test
	public final void save() {
		sharePortfolioRepository.save(sharePortfolio);
		Mockito.verify(mongoOperations).save(sharePortfolio);
	}

	@Test
	public final void savePortfolioOptimisation() {
		sharePortfolioRepository.save(portfolioOptimisation);
		Mockito.verify(mongoOperations).save(portfolioOptimisation);
	}

	@SuppressWarnings("unchecked")
	@Test
	public final void minVariance() {

		@SuppressWarnings("rawtypes")
		ArgumentCaptor<TypedAggregation> aggregationCaptor = ArgumentCaptor.forClass(TypedAggregation.class);
		@SuppressWarnings("rawtypes")
		final ArgumentCaptor<Class> outputTypeCaptor = ArgumentCaptor.forClass(Class.class);
		final List<PortfolioOptimisation> aggregationResultRows = new ArrayList<>();
		Mockito.when(portfolioOptimisation.samples()).thenReturn(1000L);
		Mockito.when(portfolioOptimisation.variance()).thenReturn(1e-3);
		aggregationResultRows.add(portfolioOptimisation);
		final List<PortfolioOptimisationImpl> rows = new ArrayList<>();
		final PortfolioOptimisationImpl row = BeanUtils.instantiateClass(PortfolioOptimisationImpl.class);
		rows.add(row);
		Mockito.when(mongoOperations.find(new Query(Criteria.where(SharePortfolioRepositoryImpl.VARIANCE_FIELD).lte(portfolioOptimisation.variance())), PortfolioOptimisationImpl.class)).thenReturn(rows);

		final AggregationResults<PortfolioOptimisation> aggregationResults = Mockito.mock(AggregationResults.class);
		Mockito.when(aggregationResults.getMappedResults()).thenReturn(aggregationResultRows);
		Mockito.when(mongoOperations.aggregate(aggregationCaptor.capture(), classCaptor.capture(), outputTypeCaptor.capture())).thenReturn(aggregationResults);

		final Optional<PortfolioOptimisation> result = sharePortfolioRepository.minVariance(NAME);
		Assert.assertTrue(result.isPresent());

		Assert.assertEquals(1000L, (long) result.get().samples());
		Assert.assertEquals(row, result.get());

		Assert.assertEquals(PortfolioOptimisationImpl.class, outputTypeCaptor.getValue());
		Assert.assertEquals(PortfolioOptimisationImpl.class, classCaptor.getValue());

	}

}
