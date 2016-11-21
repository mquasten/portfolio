package de.mq.portfolio.support;

import java.util.Arrays;
import java.util.stream.IntStream;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import de.mq.portfolio.share.support.ClosedIntervalPageRequest;

public class SerialisationUtilTest {
	
	private static final int PAGE_NUMBER = 42;

	private final Pageable pageable = new ClosedIntervalPageRequest(50, Mockito.mock(Sort.class), 5000);
	
	private final SimpleSerialisationUtilImpl serialisationUtil = new SimpleSerialisationUtilImpl();
	@Test
	public final void serialize() {
		
		final Pageable[] pageable = {this.pageable};
	
		IntStream.range(0, PAGE_NUMBER).forEach( i -> pageable[0]=pageable[0].next());
		
		
		System.out.println(pageable[0].getPageNumber());
		
		serialisationUtil.serialize(pageable[0], Arrays.asList("page"));;
	}

}
