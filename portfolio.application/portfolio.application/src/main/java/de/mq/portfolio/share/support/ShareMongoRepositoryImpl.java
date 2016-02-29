package de.mq.portfolio.share.support;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;

@Repository("shareRepository")
class ShareMongoRepositoryImpl implements ShareRepository {
	
	private static final String INDEX_FIELD = "index";
	static final String CODE_FIELD = "code";
	static final String SHARE_CODE_FIELD = "share.code";
	private final MongoOperations mongoOperations;

	@Autowired
	ShareMongoRepositoryImpl(final MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.share.support.support.support.ShareRepository#shares()
	 */
	@Override
	public final Collection<Share> shares(){
		return Collections.unmodifiableList( mongoOperations.findAll(ShareImpl.class));
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.share.support.ShareRepository#timeCourses(org.springframework.data.domain.Pageable, de.mq.portfolio.share.Share)
	 */
	@Override
	public final  Collection<TimeCourse> timeCourses(final Pageable pageable, final Share criteria) {
		final Query query = query(criteria);
		
		query.skip(pageable.getOffset());
		query.limit(pageable.getPageSize());
		if(pageable.getSort() != null ){
			query.with(pageable.getSort());
		}
		
		return Collections.unmodifiableList(mongoOperations.find(query, TimeCourseImpl.class));
		
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.share.support.ShareRepository#distinctIndex()
	 */
	@Override
	public final Collection<String> distinctIndex() {
		@SuppressWarnings("unchecked")
		final Collection<String> results = mongoOperations.getCollection(ShareImpl.class.getAnnotation(Document.class).collection()).distinct(INDEX_FIELD);
		return results;
	}

	private Query query(final Share criteria) {
		final Query query = new Query();
		
		if( StringUtils.hasText(criteria.name())) {
			
			query.addCriteria(Criteria.where("share.name").regex(pattern(criteria.name())));
		}
		
		if( StringUtils.hasText(criteria.code())) {
			query.addCriteria(Criteria.where("share.code").regex(pattern(criteria.code())));
		}
		
		if( StringUtils.hasText(criteria.index())) {
			query.addCriteria(Criteria.where("share.index").is(criteria.index()));
		}
		
		return query;
	}

	private Pattern pattern(final String pattern) {
		return Pattern.compile( pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.share.support.ShareRepository#pageable(de.mq.portfolio.share.Share, java.lang.Number)
	 */
	@Override
	public Pageable pageable(final Share criteria, final Number pageSize) {
		return new ClosedIntervalPageRequest(pageSize.intValue(),new Sort("name", "id"), mongoOperations.count(query(criteria), TimeCourseImpl.class));
	}

	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.share.support.support.ShareRepository#save(de.mq.portfolio.share.support.TimeCourse)
	 */
	@Override
	public final void save(final TimeCourse timeCourse) {
		mongoOperations.save(timeCourse);
	}
	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.share.support.support.ShareRepository#deleteTimeCourse(de.mq.portfolio.share.support.Share)
	 */
	@Override
	public final void deleteTimeCourse(final Share share) {
		final Query query = new Query(Criteria.where(SHARE_CODE_FIELD).is(share.code()));
		mongoOperations.remove(query, TimeCourseImpl.class);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.share.support.ShareRepository#save(de.mq.portfolio.share.Share)
	 */
	@Override
	public final void save(final Share share) {
		final Query query = new Query(Criteria.where(CODE_FIELD).is(share.code()));
		final Share existing =  mongoOperations.findOne(query, ShareImpl.class);
		if( existing != null) {
			ReflectionUtils.doWithFields(ShareImpl.class, field -> {
				  field.setAccessible(true);
				  final Object id = ReflectionUtils.getField(field, existing);
				  ReflectionUtils.setField(field, share, id);
			} , field -> field.isAnnotationPresent(Id.class) );
		}
		mongoOperations.save(share);
	}

}
