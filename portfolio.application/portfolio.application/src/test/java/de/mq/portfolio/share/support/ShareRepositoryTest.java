package de.mq.portfolio.share.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.util.ReflectionTestUtils;

import org.springframework.util.StringUtils;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;

public class ShareRepositoryTest {
	
	private static final int PAGE_SIZE = 20;

	private static final int PAGE_OFFSET = 50;

	private static final String SHARE_NAME = "ShareName";

	private static final String ID = "19680528";

	private static final String ID_FIELD = "id";

	private static final String CODE = "^IDAXI";

	final MongoOperations mongoOperations = Mockito.mock(MongoOperations.class);
	
	private final ShareRepository shareepository = new  ShareMongoRepositoryImpl(mongoOperations); 
	
	final ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
	@SuppressWarnings("rawtypes")
	final ArgumentCaptor<Class> classCaptor = ArgumentCaptor.forClass(Class.class);
	
	private final Share share = Mockito.mock(Share.class);
	private final TimeCourse timeCourse = Mockito.mock(TimeCourse.class);
	private final List<TimeCourse> timeCourses = Arrays.asList(timeCourse);
	
	private final Pageable pageable = Mockito.mock(Pageable.class);
	
	private final Sort sort = new Sort(Direction.ASC, "share.name");
	
	@SuppressWarnings("unchecked")
	@Before
	public final void setup() {
		Mockito.when(mongoOperations.find(queryCaptor.capture(), classCaptor.capture())).thenReturn(timeCourses);
		
	}
	
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
	
	
	@Test
	public final void  timeCoursesByCodes() {
		
		
		final Collection<TimeCourse> results = shareepository.timeCourses(Arrays.asList(CODE));
	
		Assert.assertEquals(TimeCourseImpl.class, classCaptor.getValue());
		
		Assert.assertEquals(1, queryCaptor.getValue().getQueryObject().keySet().size());
		Assert.assertEquals(ShareMongoRepositoryImpl.SHARE_CODE_FIELD, queryCaptor.getValue().getQueryObject().keySet().stream().findAny().get());
		final BasicDBObject criteria = (BasicDBObject) queryCaptor.getValue().getQueryObject().get(ShareMongoRepositoryImpl.SHARE_CODE_FIELD);
	
		Assert.assertEquals(Arrays.asList(CODE), criteria.get("$in"));
		Assert.assertEquals(1, results.size());
		Assert.assertEquals(timeCourses, results);
	}
	
	@Test
	public final void distinctIndex() {
		final DBCollection dbCollection = Mockito.mock(DBCollection.class);
		ArgumentCaptor<String> collectionCaptur = ArgumentCaptor.forClass(String.class);
		Mockito.when(dbCollection.distinct(ShareMongoRepositoryImpl.INDEX_FIELD)).thenReturn(Arrays.asList(CODE));
		Mockito.when(mongoOperations.getCollection(collectionCaptur.capture())).thenReturn(dbCollection);
		
		Assert.assertEquals(Arrays.asList(CODE), shareepository.distinctIndex());
		Assert.assertTrue(StringUtils.hasText(ShareImpl.class.getAnnotation(Document.class).collection()));
		Assert.assertEquals(ShareImpl.class.getAnnotation(Document.class).collection(), collectionCaptur.getValue());
	}
	
	@Test
	public final void  timeCourses() {
		
		
		Mockito.when(pageable.getOffset()).thenReturn(PAGE_OFFSET);
		Mockito.when(pageable.getPageSize()).thenReturn(PAGE_SIZE);
		Mockito.when(pageable.getSort()).thenReturn(sort);
		Mockito.when(timeCourse.share()).thenReturn(share);
		Mockito.when(share.name()).thenReturn(SHARE_NAME);
		Mockito.when(share.code()).thenReturn(CODE);
		
		Mockito.when(share.index()).thenReturn("Index");
		
		Assert.assertEquals(1, timeCourses.size());
		Assert.assertEquals(timeCourses, shareepository.timeCourses(pageable, share));
		Assert.assertEquals(TimeCourseImpl.class, classCaptor.getValue());
		
		final DBObject dbObject = queryCaptor.getValue().getQueryObject();
		Assert.assertEquals(3, dbObject.keySet().size());
		assertPattern(SHARE_NAME, (Pattern) dbObject.get(ShareMongoRepositoryImpl.SHARE_NAME_FIELD));
		assertPattern(CODE, (Pattern) dbObject.get(ShareMongoRepositoryImpl.SHARE_CODE_FIELD));
		
		Assert.assertEquals("Index", (String) dbObject.get(ShareMongoRepositoryImpl.SHARE_INDEX_FIELD));
		
		Assert.assertEquals(1, queryCaptor.getValue().getSortObject().keySet().size());
		Assert.assertEquals("share.name",  queryCaptor.getValue().getSortObject().keySet().stream().findAny().get());
		Assert.assertEquals(1, queryCaptor.getValue().getSortObject().get("share.name"));
		
		Assert.assertEquals(PAGE_OFFSET, queryCaptor.getValue().getSkip());
		Assert.assertEquals(PAGE_SIZE, queryCaptor.getValue().getLimit());
		
	}

	private void assertPattern(final String pattern, final Pattern criteria) {
		Assert.assertNotNull(criteria);
		Assert.assertEquals(pattern, criteria.pattern());
		Assert.assertEquals(Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE, criteria.flags());
	}

}
