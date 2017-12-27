package de.mq.portfolio.exchangerate.support;



import java.util.Arrays;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import de.mq.portfolio.share.Data;
import org.junit.Assert;

public class ExchangeRateRetrospectiveTest {
	
	private static final String PORTFOLIO_NAME = "min-risk";
	private static final String TARGET = "USD";
	private final Data start = Mockito.mock(Data.class);
	private final Data end = Mockito.mock(Data.class);
	
	private Date startDate = Mockito.mock(Date.class);
	private Date endDate = Mockito.mock(Date.class);
	
	private Double startValue = 47.11D;
	private Double endValue = 99.99D;
	
	private  ExchangeRateRetrospective exchangeRateRetrospective;
	
	@Before
	public final void setup() {
		Mockito.when(start.date()).thenReturn(startDate);
		Mockito.when(end.date()).thenReturn(endDate);
		Mockito.when(start.value()).thenReturn(startValue);
		Mockito.when(end.value()).thenReturn(endValue);
		exchangeRateRetrospective = new ExchangeRateRetrospectiveImpl(PORTFOLIO_NAME , TARGET, start,  end, Arrays.asList(start,end));
	}
	
	
	
	@Test
	public final void startDate() {
		Assert.assertEquals(startDate, exchangeRateRetrospective.startDate());
	}
	

	@Test
	public final void endDate() {
		Assert.assertEquals(endDate, exchangeRateRetrospective.endDate());
	}
	
	@Test
	public final void startValue() {
		Assert.assertEquals(startValue, exchangeRateRetrospective.startValue());
	}
	
	@Test
	public final void endValue() {
		Assert.assertEquals(endValue, exchangeRateRetrospective.endValue());
	}
	
	@Test
	public final void name() {
		Assert.assertEquals(PORTFOLIO_NAME, exchangeRateRetrospective.name());
	}
	
	@Test
	public final void target() {
		Assert.assertEquals(TARGET, exchangeRateRetrospective.target());
	}
	
	
	@Test
	public final void  rate() {
		Assert.assertEquals((Double) ((endValue-startValue)/startValue), exchangeRateRetrospective.rate());
	}
	
	@Test
	public final void exchangeRates() {
		Assert.assertEquals(Arrays.asList(start,end).size(), exchangeRateRetrospective.exchangeRates().size());
		Assert.assertEquals(start, exchangeRateRetrospective.exchangeRates().toArray()[0]);
		Assert.assertEquals(end, exchangeRateRetrospective.exchangeRates().toArray()[1]);
	}
	
	@Test
	public final void createNameAndTargetOnly() {
		exchangeRateRetrospective= new ExchangeRateRetrospectiveImpl(PORTFOLIO_NAME, TARGET);
		Assert.assertEquals(PORTFOLIO_NAME, exchangeRateRetrospective.name());
		Assert.assertEquals(TARGET, exchangeRateRetrospective.target());
		Assert.assertNull(exchangeRateRetrospective.endDate());
		Assert.assertNull(exchangeRateRetrospective.startDate());
		Assert.assertNull(exchangeRateRetrospective.endValue());
		Assert.assertNull(exchangeRateRetrospective.startValue());
		Assert.assertNull(exchangeRateRetrospective.rate());
		Assert.assertTrue(exchangeRateRetrospective.exchangeRates().isEmpty());
	}
}
