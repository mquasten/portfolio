package de.mq.portfolio.share.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;

public class ShareRepositoryTest {
	
	private static final String ID = "19680528";

	private static final String ID_FIELD = "id";

	private static final String CODE = "^IDAXI";

	final MongoOperations mongoOperations = Mockito.mock(MongoOperations.class);
	
	private final ShareRepository shareepository = new  ShareMongoRepositoryImpl(mongoOperations); 
	
	private final Share share = Mockito.mock(Share.class);
	
	
	@SuppressWarnings("unchecked")
	@Test
	public final void shares() {
		@SuppressWarnings("rawtypes")
		final List results = new ArrayList<>();
		results.add( share);
		Mockito.when(mongoOperations.findAll(ShareImpl.class)).thenReturn(results);
		Assert.assertEquals(results, shareepository.shares());
	}
	
	@Test
	public final void  saveTimeCourse() {
		final TimeCourse timeCourse = Mockito.mock(TimeCourse.class);
		shareepository.save(timeCourse);
		Mockito.verify(mongoOperations).save(timeCourse);
	}
	
	@Test
	public final void deleteTimeCourse() {
		Mockito.when(share.code()).thenReturn(CODE);
		final ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
		@SuppressWarnings("rawtypes")
		final ArgumentCaptor<Class> classCaptor = ArgumentCaptor.forClass(Class.class);
		shareepository.deleteTimeCourse(share);
		
		Mockito.verify(mongoOperations).remove(queryCaptor.capture(), classCaptor.capture());
		
		Assert.assertEquals(TimeCourseImpl.class, classCaptor.getValue());
		final Map<?,?> query = queryCaptor.getValue().getQueryObject().toMap();
		Assert.assertEquals(1, query.size());
		Assert.assertEquals(ShareMongoRepositoryImpl.SHARE_CODE_FIELD, query.keySet().stream().findFirst().get());
		Assert.assertEquals(CODE, query.values().stream().findFirst().get());
	}
	
	@Test
	public final void  save() {
		final ShareImpl result = new ShareImpl(CODE);
		ReflectionTestUtils.setField(result, ID_FIELD, ID);
		Mockito.when(mongoOperations.findOne(Query.query(Criteria.where(ShareMongoRepositoryImpl.CODE_FIELD).is(CODE)), ShareImpl.class)).thenReturn(result);
		final ShareImpl share = new ShareImpl(CODE);
		shareepository.save(share);
		Mockito.verify(mongoOperations).save(share);
		Assert.assertEquals(ID, ReflectionTestUtils.getField(share, ID_FIELD));
	}
	
	@Test
	public final void  saveNew() {
		final ShareImpl share = new ShareImpl(CODE);
		shareepository.save(share);
		Mockito.verify(mongoOperations).save(share);
		Assert.assertNull(ReflectionTestUtils.getField(share, ID_FIELD));
	}

}
