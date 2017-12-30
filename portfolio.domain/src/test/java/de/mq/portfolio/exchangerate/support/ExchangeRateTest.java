package de.mq.portfolio.exchangerate.support;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ReflectionUtils;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.share.Data;


public class ExchangeRateTest {

	
	private static final String TARGET = "USD";
	private static final String SOURCE = "EUR";
	private final ExchangeRate exchangeRate = new ExchangeRateImpl(SOURCE, TARGET);

	@Test
	public final void source() {
		Assert.assertEquals(SOURCE, exchangeRate.source());
	}

	@Test
	public final void target() {
		Assert.assertEquals(TARGET, exchangeRate.target());
	}

	

	@Test
	public final void hash() {
		Assert.assertEquals(SOURCE.hashCode() + TARGET.hashCode(), exchangeRate.hashCode());
		setInvalid(exchangeRate);
		Assert.assertEquals(System.identityHashCode(exchangeRate), exchangeRate.hashCode());

		ReflectionTestUtils.setField(exchangeRate, "source", SOURCE);
		Assert.assertEquals(System.identityHashCode(exchangeRate), exchangeRate.hashCode());
	}

	private void setInvalid(final ExchangeRate exchangeRate) {
		ReflectionUtils.doWithFields(ExchangeRateImpl.class, field -> ReflectionTestUtils.setField(exchangeRate, field.getName(), null), field -> !Modifier.isStatic(field.getModifiers()));
	}

	@Test
	public final void equals() {
		Assert.assertTrue(exchangeRate.equals(new ExchangeRateImpl(SOURCE, TARGET)));
		Assert.assertFalse(exchangeRate.equals(new ExchangeRateImpl(TARGET, SOURCE)));
		Assert.assertFalse(exchangeRate.equals(new ExchangeRateImpl(SOURCE, SOURCE)));

		final ExchangeRate inValid = new ExchangeRateImpl(SOURCE, TARGET);
		setInvalid(inValid);
		Assert.assertFalse(inValid.equals(new ExchangeRateImpl(TARGET, SOURCE)));
		Assert.assertFalse(exchangeRate.equals(inValid));
		Assert.assertFalse(exchangeRate.equals(new Date()));
	}

	@Test
	public final void string() {
		Assert.assertTrue(exchangeRate.toString().contains(SOURCE));
		Assert.assertTrue(exchangeRate.toString().contains(TARGET));
	}

	@Test
	public final void rates() {
		Assert.assertTrue(exchangeRate.rates().isEmpty());
		final Data data = Mockito.mock(Data.class);
		final Collection<Data> rates = new ArrayList<>();
		rates.add(data);
		exchangeRate.assign(rates);
		Assert.assertEquals(1, exchangeRate.rates().size());
		Assert.assertTrue(exchangeRate.rates().stream().findAny().isPresent());
		Assert.assertEquals(data, exchangeRate.rates().stream().findAny().get());
	}

	
	@Test
	public final void id() {
		ReflectionUtils.doWithFields(ExchangeRateImpl.class, field -> Assert.assertEquals(new UUID(SOURCE.hashCode(), TARGET.hashCode()).toString(), ReflectionTestUtils.getField(exchangeRate, field.getName())),
				field -> field.isAnnotationPresent(Id.class));
	}

	@Test
	public final void privateConstructor() {
		final ExchangeRate exchangeRate = BeanUtils.instantiateClass(ExchangeRateImpl.class);
		Assert.assertNull(exchangeRate.source());
		Assert.assertNull(exchangeRate.target());
		ReflectionUtils.doWithFields(ExchangeRateImpl.class, field -> Assert.assertNull(ReflectionTestUtils.getField(exchangeRate, field.getName())), field -> field.isAnnotationPresent(Id.class));
	}
	
	@Test
	public final void createWithrates() {
		final Data data = Mockito.mock(Data.class);
		final ExchangeRate exchangeRate = new ExchangeRateImpl(SOURCE, TARGET, Arrays.asList(data));
		
		Assert.assertEquals(SOURCE, exchangeRate.source());
		Assert.assertEquals(TARGET, exchangeRate.target());
		Assert.assertEquals(Arrays.asList(data), exchangeRate.rates());
	}
	@Test
	public final void updates() {
		Assert.assertTrue(exchangeRate.updates().isEmpty());
		final Data data = Mockito.mock(Data.class);
		final Collection<Data> rates = new ArrayList<>();
		rates.add(data);
		exchangeRate.assign(rates);
		Assert.assertEquals(1, exchangeRate.updates().size());
		Assert.assertEquals(Gateway.CentralBankExchangeRates, exchangeRate.updates().stream().findFirst().get().getKey());
		Assert.assertTrue(50 > Math.abs(exchangeRate.updates().stream().findFirst().get().getValue().getTime()- System.currentTimeMillis()));
	}

}
