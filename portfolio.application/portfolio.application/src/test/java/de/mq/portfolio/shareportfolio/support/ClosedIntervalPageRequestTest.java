package de.mq.portfolio.shareportfolio.support;

import java.lang.reflect.Constructor;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.ReflectionUtils;

import de.mq.portfolio.share.support.ClosedIntervalPageRequest;
import junit.framework.Assert;

public class ClosedIntervalPageRequestTest {
	private static final long ROW_COUNTER = 52L;
	private static final int PAGE_SIZE = 20;
	
	private final Sort sort = Mockito.mock(Sort.class);
	private final ClosedIntervalPageRequest pageable = new ClosedIntervalPageRequest(PAGE_SIZE, sort, ROW_COUNTER);
	
	@Test
	public final void getSort() {
		Assert.assertEquals(sort, pageable.getSort());
	}
	
	@Test
	public final void next()  {
		Assert.assertEquals(0, pageable.getPageNumber());
		Assert.assertEquals(0, pageable.getOffset());
		
		Assert.assertEquals(PAGE_SIZE, pageable.getPageSize());
		Assert.assertEquals(1, pageable.next().getPageNumber());
		Assert.assertEquals(PAGE_SIZE, pageable.next().getOffset());
		Assert.assertEquals(sort, pageable.next().getSort());
		
		Assert.assertEquals(pageable.next().next(), pageable.next().next().next());
		Assert.assertEquals(newPage(1),pageable.next());
		
	}
	
	@Test
	public final void previous()  {
		Assert.assertEquals(pageable, pageable.previous());
		
		Assert.assertEquals(pageable,((ClosedIntervalPageRequest) pageable.next()).previous());
		
		Assert.assertEquals(0, ((ClosedIntervalPageRequest) pageable.next()).previous().getPageNumber());
		Assert.assertEquals(PAGE_SIZE, ((ClosedIntervalPageRequest) pageable.next()).previous().getPageSize());
		Assert.assertEquals(0, ((ClosedIntervalPageRequest) pageable.next()).previous().getOffset());
		Assert.assertEquals(pageable, ((ClosedIntervalPageRequest)newPage(1)).previous());
	}
	
	@Test
	public final void  first() {
		Assert.assertEquals(pageable, pageable.first());
		Assert.assertEquals(pageable, newPage(2).first());
		
		Assert.assertEquals(sort, newPage(2).first().getSort());
		Assert.assertEquals(0, newPage(2).first().getOffset());
		Assert.assertEquals(0, newPage(2).first().getPageNumber());
		Assert.assertEquals(PAGE_SIZE, newPage(2).first().getPageSize());
		
	}

	private Pageable newPage(final int page)  {
		Constructor<ClosedIntervalPageRequest> declaredConstructor;
		try {
			declaredConstructor = ClosedIntervalPageRequest.class.getDeclaredConstructor(int.class, int.class, Sort.class, long.class);
		} catch (NoSuchMethodException | SecurityException ex) {
			 ReflectionUtils.handleReflectionException(ex);
			 return null;
		}
		
		 return BeanUtils.instantiateClass(declaredConstructor, page, PAGE_SIZE, sort, ROW_COUNTER);
	}
	
	@Test
	public final void last() {
		Assert.assertEquals(newPage(2), pageable.last());
		Assert.assertEquals(sort, pageable.last().getSort());
		Assert.assertEquals(2*PAGE_SIZE, pageable.last().getOffset());
		Assert.assertEquals(2, pageable.last().getPageNumber());
		Assert.assertEquals(PAGE_SIZE, newPage(2).first().getPageSize());
	}
	
	@Test
	public final void  maxPage() {
		Assert.assertEquals(2, pageable.maxPage());
		Assert.assertEquals( 0, new ClosedIntervalPageRequest(PAGE_SIZE, sort,0).maxPage());
	}
	
	@Test
	public final void   isFirst() {
		Assert.assertTrue(pageable.isFirst());
		Assert.assertFalse(((ClosedIntervalPageRequest)newPage(1)).isFirst());
	}
	
	@Test
	public final void isLast() {
		Assert.assertFalse(pageable.isLast());
		Assert.assertTrue(((ClosedIntervalPageRequest)newPage(2)).isLast());
	}
}
