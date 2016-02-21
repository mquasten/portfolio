package de.mq.portfolio.shareportfolio.support;

import java.lang.reflect.Field;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.ReflectionUtils;

import de.mq.portfolio.shareportfolio.PortfolioOptimisation;

public class PortfolioOptimisationTest {
	
	private static final String ID_FIELD = "id";
	private static final Long SAMPLES = 1000L;
	private static final double VARIANCE = 1e3;
	private static final double[] WEIGHTS = new double[] {0.1d, 0.2, 0.3d, 0.4d};
	private static final String NAME = "mq-test";
	private PortfolioOptimisation portfolioOptimisation = new PortfolioOptimisationImpl(NAME, WEIGHTS,VARIANCE, SAMPLES );
	
	@Test
	public final void portfolio() {
		Assert.assertEquals(NAME, portfolioOptimisation.portfolio());
	}
	
	@Test
	public final void weights() {
		Assert.assertEquals(WEIGHTS, portfolioOptimisation.weights());
	}

	@Test
	public final void variance() {
		Assert.assertEquals( VARIANCE, portfolioOptimisation.variance());
	}
	
	@Test
	public final void samples() {
		Assert.assertEquals(SAMPLES, portfolioOptimisation.samples());
	}
	
	@Test
	public final void constructor() {
		PortfolioOptimisation portfolioOptimisation  = BeanUtils.instantiateClass(PortfolioOptimisationImpl.class);
		Assert.assertNull(portfolioOptimisation.portfolio());
		Assert.assertEquals(0, portfolioOptimisation.weights().length);
		Assert.assertEquals(0d, portfolioOptimisation.variance());
		Assert.assertEquals(1L, (long) portfolioOptimisation.samples());
	}
	@Test
	public final void annotations() {
		Assert.assertTrue(PortfolioOptimisationImpl.class.isAnnotationPresent(Document.class));
		Assert.assertEquals("PortfolioOptimisation" , PortfolioOptimisationImpl.class.getAnnotation(Document.class).collection());
		
		final Field field = ReflectionUtils.findField(PortfolioOptimisationImpl.class, ID_FIELD);
		Assert.assertNotNull(field);
		
		Assert.assertTrue(field.isAnnotationPresent(Id.class));
	}
}
