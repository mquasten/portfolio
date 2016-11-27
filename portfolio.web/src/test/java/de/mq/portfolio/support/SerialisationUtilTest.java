package de.mq.portfolio.support;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import de.mq.portfolio.share.support.ClosedIntervalPageRequest;
import junit.framework.Assert;

public class SerialisationUtilTest {
	
	private static final String PAGE = "page";

	private static final int PAGE_NUMBER = 42;

	private final Pageable pageable = new ClosedIntervalPageRequest(50, Mockito.mock(Sort.class), 5000);
	
	private final AbstractSerialisationUtil serialisationUtil =  Mockito.mock(AbstractSerialisationUtil.class, Mockito.CALLS_REAL_METHODS);
	
	private static final String STATE = "rO0ABXNyABFqYXZhLnV0aWwuSGFzaE1hcAUH2sHDFmDRAwACRgAKbG9hZEZhY3RvckkACXRocmVzaG9sZHhwP0AAAAAAAAx3CAAAABAAAAABdAAEcGFnZXNyABFqYXZhLmxhbmcuSW50ZWdlchLioKT3gYc4AgABSQAFdmFsdWV4cgAQamF2YS5sYW5nLk51bWJlcoaslR0LlOCLAgAAeHAAAAAqeA==";
	
	
	@Before
	public final void setup() {
		Mockito.when(serialisationUtil.builder()).thenReturn(new ExceptionTranslationBuilderImpl<>());
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
		final Pageable pageable = new ClosedIntervalPageRequest(50, Mockito.mock(Sort.class), 5000);
		
		Assert.assertEquals(0, pageable.getPageNumber());
		Assert.assertFalse(pageable.hasPrevious());
		final Map<String,Object> map = new HashMap<>();
		map.put(PAGE, PAGE_NUMBER);
		serialisationUtil.toBean(map, pageable);
		
		Assert.assertEquals(PAGE_NUMBER, pageable.getPageNumber());
		Assert.assertTrue(pageable.hasPrevious());
	
	}
}