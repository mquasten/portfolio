package de.mq.portfolio.exchangerate.support;

import java.lang.reflect.Modifier;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.portfolio.share.Data;
import org.junit.Assert;

public class ExchangeRateRetrospectiveBuilderTest {

	private static final String NAME = "Name";

	private static Date START_DATE = Date.from(LocalDateTime.now().minusDays(1).atZone(ZoneId.systemDefault()).toInstant());

	private static Date END_DATE = new Date();
	private Data start = Mockito.mock(Data.class);
	private Data end = Mockito.mock(Data.class);

	@Before
	public final void setup() {
		Mockito.when(start.date()).thenReturn(START_DATE);
		Mockito.when(end.date()).thenReturn(END_DATE);
		Mockito.when(start.value()).thenReturn(1D);
		Mockito.when(end.value()).thenReturn(1.5D);
	}

	@Test
	public final void withName() {
		final ExchangeRateRetrospectiveBuilder builder = new ExchangeRateRetrospectiveBuilderImpl();
		Assert.assertEquals(builder, builder.withName(NAME));
		Assert.assertEquals(NAME, values(builder).get(String.class));

	}

	@Test
	public final void withStartDate() {
		final ExchangeRateRetrospectiveBuilder builder = new ExchangeRateRetrospectiveBuilderImpl();
		Assert.assertEquals(builder, builder.withStartDate(START_DATE));
		Assert.assertEquals(START_DATE, values(builder).get(Date.class));
	}

	@Test
	public final void withExchangeRates() {
		final ExchangeRateRetrospectiveBuilder builder = new ExchangeRateRetrospectiveBuilderImpl();
		Assert.assertEquals(builder, builder.withExchangeRates(Arrays.asList(start, end)));
		Assert.assertEquals(Arrays.asList(start, end), values(builder).get(Collection.class));
	}

	private Map<Class<?>, Object> values(final ExchangeRateRetrospectiveBuilder builder) {
		final Map<Class<?>, Object> values = new HashMap<>();
		Arrays.asList(ExchangeRateRetrospectiveBuilderImpl.class.getDeclaredFields()).stream().filter(field -> !Modifier.isStatic(field.getModifiers())).forEach(field -> values.put(field.getType(), ReflectionTestUtils.getField(builder, field.getName())));
		return values;
	}

	@Test
	public final void build() {
		final ExchangeRateRetrospective result = new ExchangeRateRetrospectiveBuilderImpl().withName(NAME).withStartDate(START_DATE).withExchangeRates(Arrays.asList(start, end)).build();
		Assert.assertEquals(START_DATE, result.startDate());
		Assert.assertEquals(END_DATE, result.endDate());

		Assert.assertEquals((Double) end.value(), result.endValue());
		Assert.assertEquals((Double) start.value(), result.startValue());
		Assert.assertEquals(NAME, result.name());
		Assert.assertEquals((Double) ((end.value() - start.value()) / start.value()), result.rate());
		Assert.assertEquals(2, result.exchangeRates().size());
		Assert.assertTrue(result.exchangeRates().contains(start));
		Assert.assertTrue(result.exchangeRates().contains(end));
	}

	@Test
	public final void buildNameOnly() {
		final ExchangeRateRetrospective result = new ExchangeRateRetrospectiveBuilderImpl().withName(NAME).withStartDate(END_DATE).withExchangeRates(Arrays.asList(start)).build();

		Assert.assertNull(result.startDate());
		Assert.assertNull(result.endDate());

		Assert.assertNull(result.endValue());
		Assert.assertNull(result.startValue());
		Assert.assertEquals(NAME, result.name());
		Assert.assertNull(result.rate());
		Assert.assertTrue(result.exchangeRates().isEmpty());
	}

}
