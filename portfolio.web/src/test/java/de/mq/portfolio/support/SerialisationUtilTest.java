package de.mq.portfolio.support;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import de.mq.portfolio.share.support.ClosedIntervalPageRequest;
import junit.framework.Assert;

public class SerialisationUtilTest {
	
	private static final String KEY = "paging";

	private static final int SIZE = 50;

	private static final int COUNTER = 5000;

	private static final Sort SORT = Mockito.mock(Sort.class);

	private static final String PAGE = "page";

	private static final int PAGE_NUMBER = 42;
	
	private Entry<String,Pageable> entry; 

	private final Pageable pageable = new ClosedIntervalPageRequest(SIZE, SORT, COUNTER);
	
	private final AbstractSerialisationUtil serialisationUtil =  Mockito.mock(AbstractSerialisationUtil.class, Mockito.CALLS_REAL_METHODS);
	
	private static final String STATE = "rO0ABXNyABFqYXZhLnV0aWwuSGFzaE1hcAUH2sHDFmDRAwACRgAKbG9hZEZhY3RvckkACXRocmVzaG9sZHhwP0AAAAAAAAx3CAAAABAAAAABdAAEcGFnZXNyABFqYXZhLmxhbmcuSW50ZWdlchLioKT3gYc4AgABSQAFdmFsdWV4cgAQamF2YS5sYW5nLk51bWJlcoaslR0LlOCLAgAAeHAAAAAqeA==";
	
	
	@Before
	public final void setup() {
		Mockito.when(serialisationUtil.builder()).thenReturn(new ExceptionTranslationBuilderImpl<>());
		final Pageable[] pageable = {this.pageable};
		IntStream.range(0, PAGE_NUMBER).forEach( i -> pageable[0]=pageable[0].next());
		
		entry= new AbstractMap.SimpleImmutableEntry<>(KEY, pageable[0]); 
		Assert.assertNotNull(entry);
	}
	
	@Test
	public final void toMap() {
		
		final Pageable[] pageable = {this.pageable};
	
		IntStream.range(0, PAGE_NUMBER).forEach( i -> pageable[0]=pageable[0].next());
		
		final Map<String,Object> results = serialisationUtil.toMap(pageable[0], Arrays.asList(PAGE));
		Assert.assertEquals(1, results.size());
		Assert.assertEquals(PAGE_NUMBER, results.get(PAGE));
	}
	
	@Test
	public final void serialize() throws IOException {
		final Map<String,Object> results = new HashMap<>();
		results.put(PAGE, PAGE_NUMBER);
		Assert.assertEquals(STATE, serialisationUtil.serialize(results));
	}
	
	@Test
	public final void deSerialize() {
		final Map<String,Object> results = serialisationUtil.deSerialize(STATE);
		Assert.assertEquals(1, results.size());
		Assert.assertEquals(PAGE, results.keySet().stream().findAny().get());
		Assert.assertEquals(PAGE_NUMBER,  results.values().stream().findAny().get());
	}
	
	@Test
	public final void toBean() {
		final Pageable pageable = new ClosedIntervalPageRequest(50, SORT, 5000);
		
		Assert.assertEquals(0, pageable.getPageNumber());
		Assert.assertFalse(pageable.hasPrevious());
		final Map<String,Object> map = new HashMap<>();
		map.put(PAGE, PAGE_NUMBER);
		serialisationUtil.toBean(map, pageable);
		
		Assert.assertEquals(PAGE_NUMBER, pageable.getPageNumber());
		Assert.assertTrue(pageable.hasPrevious());
	
	}
	
	@Test
	public final void value() {
		Assert.assertEquals(PAGE_NUMBER, ((Number) ((AbstractSerialisationUtil)serialisationUtil).value(this, "entry.value.page")).intValue());
		
		Assert.assertEquals(SORT, (Sort) ((AbstractSerialisationUtil)serialisationUtil).value(this, "entry.value.sort"));
		
		Assert.assertEquals(COUNTER, ((Number) ((AbstractSerialisationUtil)serialisationUtil).value(this, "entry.value.counter")).intValue());
		
		Assert.assertEquals(SIZE, ((Number) ((AbstractSerialisationUtil)serialisationUtil).value(this, "entry.value.size")).intValue());
		
		Assert.assertEquals(KEY,  ((AbstractSerialisationUtil)serialisationUtil).value(this, "entry.key"));
		
	}
	
	@Test
	public final void valueNull() {
		this.entry= new AbstractMap.SimpleImmutableEntry<>(KEY,null); 
		Assert.assertNull(this.entry.getValue());
		Assert.assertNull(((AbstractSerialisationUtil)serialisationUtil).value(this, "entry.value.page"));
	}
	
	@Test
	public final void valueFlat() {
		Assert.assertEquals(PAGE_NUMBER, ((Number) ((AbstractSerialisationUtil)serialisationUtil).value(this.entry.getValue(), "page")).intValue());
		
		Assert.assertEquals(SORT, (Sort) ((AbstractSerialisationUtil)serialisationUtil).value(this.entry.getValue(), "sort"));
		
		Assert.assertEquals(COUNTER, ((Number) ((AbstractSerialisationUtil)serialisationUtil).value(this.entry.getValue(), "counter")).intValue());
		
		Assert.assertEquals(SIZE, ((Number) ((AbstractSerialisationUtil)serialisationUtil).value(this.entry.getValue(), "size")).intValue());
		Assert.assertEquals(KEY,  ((AbstractSerialisationUtil)serialisationUtil).value(this.entry, "key"));
	}
}