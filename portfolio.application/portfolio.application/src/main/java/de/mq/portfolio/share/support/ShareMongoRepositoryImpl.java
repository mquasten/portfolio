package de.mq.portfolio.share.support;

import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.ReflectionUtils;

import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;

@Repository("shareRepository")
class ShareMongoRepositoryImpl implements ShareRepository {
	
	private final MongoOperations mongoOperations;

	@Autowired
	ShareMongoRepositoryImpl(final MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.share.support.support.ShareRepository#shares()
	 */
	@Override
	public final Collection<Share> shares(){
		return Collections.unmodifiableList( mongoOperations.findAll(ShareImpl.class));
	}
	

	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.share.support.ShareRepository#save(de.mq.portfolio.share.TimeCourse)
	 */
	@Override
	public final void save(final TimeCourse timeCourse) {
		mongoOperations.save(timeCourse);
	}
	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.share.support.ShareRepository#deleteTimeCourse(de.mq.portfolio.share.Share)
	 */
	@Override
	public final void deleteTimeCourse(final Share share) {
		final Query query = new Query(Criteria.where("share.code").is(share.code()));
		mongoOperations.remove(query, TimeCourseImpl.class);
	}
	
	@Override
	public final void save(final Share share) {
		final Query query = new Query(Criteria.where("code").is(share.code()));
		Share existing =  mongoOperations.findOne(query, ShareImpl.class);
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
