package de.mq.portfolio.support;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

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
	
	private final SimpleSerialisationUtilImpl serialisationUtil = new SimpleSerialisationUtilImpl();
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
		System.out.println(serialisationUtil.serialize(results));
	}

}
