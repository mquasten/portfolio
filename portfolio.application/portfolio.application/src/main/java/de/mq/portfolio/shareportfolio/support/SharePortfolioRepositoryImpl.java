package de.mq.portfolio.shareportfolio.support;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import de.mq.portfolio.share.support.ClosedIntervalPageRequest;
import de.mq.portfolio.shareportfolio.SharePortfolio;



@Repository
class SharePortfolioRepositoryImpl implements SharePortfolioRepository {

	static final String SHARE_NAME_FIELD = "timeCourses.share.name";
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
	 * @see de.mq.portfolio.shareportfolio.support.SharePortfolioRepository#portfolios(org.springframework.data.domain.Pageable, de.mq.portfolio.shareportfolio.SharePortfolio)
	 */
	@Override
	public final  Collection<SharePortfolio> portfolios(final Pageable pageable, final SharePortfolio criteria) {
		final Query query = query(criteria);
		query.skip(pageable.getOffset());
		query.limit(pageable.getPageSize());
		if(pageable.getSort() != null ){
			query.with(pageable.getSort());
		}
		return Collections.unmodifiableList(mongoOperations.find(query, SharePortfolioImpl.class));
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.shareportfolio.support.SharePortfolioRepository#sharePortfolio(java.lang.String)
	 */
	@Override
	public final SharePortfolio sharePortfolio(final String id){
		SharePortfolio result = mongoOperations.findById(id, SharePortfolioImpl.class);
		Assert.notNull(result, "Entity not found id :" +id);
		return result;
	}

	private Query query(final SharePortfolio criteria) {
		final Query query = new Query();
		if( StringUtils.hasText(criteria.name())) {
			query.addCriteria(Criteria.where(NAME_FIELD).regex(pattern(criteria.name())));
		}
		if (criteria.timeCourses().isEmpty()){
			return query;
		}
		if(criteria.timeCourses().get(0).share()==null){
			return query;
		}
		if(StringUtils.hasText(criteria.timeCourses().get(0).share().name())) {
			query.addCriteria(Criteria.where(SHARE_NAME_FIELD).regex(pattern(criteria.timeCourses().get(0).share().name())));
		}
		return query;
	}
	
	private Pattern pattern(final String pattern) {
		return Pattern.compile( pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
	}
	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.shareportfolio.support.SharePortfolioRepository#pageable(de.mq.portfolio.sharepo rtfolio.SharePortfolio, org.springframework.data.domain.Sort, java.lang.Number)
	 */
	@Override
	public Pageable pageable(final SharePortfolio criteria,final Sort sort, final Number pageSize) {
		return  new ClosedIntervalPageRequest(pageSize.intValue(),sort, mongoOperations.count(query(criteria), SharePortfolioImpl.class));
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
	 * @see de.mq.portfolio.shareportfolio.support.SharePortfolioRepository#save(java.lang.String)
	 */
	@Override
	public final void save(final String json) {
		Assert.isTrue(SharePortfolioImpl.class.isAnnotationPresent(Document.class));
		Assert.isTrue(StringUtils.hasText(SharePortfolioImpl.class.getAnnotation(Document.class).collection()));
		final String docName = SharePortfolioImpl.class.getAnnotation(Document.class).collection();
		mongoOperations.save(json,docName);
	}

	
	
	@Override
	public final void delete(final SharePortfolio sharePortfolio) {
		mongoOperations.remove(sharePortfolio);
	}

}
