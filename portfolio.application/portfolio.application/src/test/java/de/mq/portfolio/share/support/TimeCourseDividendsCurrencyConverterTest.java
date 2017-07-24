package de.mq.portfolio.share.support;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.exchangerate.ExchangeRateCalculator;
import de.mq.portfolio.exchangerate.support.ExchangeRateCalculatorBuilder;
import de.mq.portfolio.exchangerate.support.ExchangeRateDatebaseRepository;
import de.mq.portfolio.share.Data;
import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;

public class TimeCourseDividendsCurrencyConverterTest {

	private static final double FACTOR = 0.9d;

	private static final String CURRENCY_USD = "USD";

	private final TimeCourseDividendsCurrencyConverterImpl timeCourseDividendsCurrencyConverter = Mockito.mock(TimeCourseDividendsCurrencyConverterImpl.class, Mockito.CALLS_REAL_METHODS);

	private final ExchangeRateDatebaseRepository exchangeRateDatebaseRepository = Mockito.mock(ExchangeRateDatebaseRepository.class);

	private final Map<Class<?>, Object> dependencies = new HashMap<>();

	private final Share share = Mockito.mock(Share.class);

	private Date date = new Date();

	private final Data data = new DataImpl(date, 1.20d);
	private final TimeCourse timeCourse = new TimeCourseImpl(share, Arrays.asList(), Arrays.asList(data));

	private final ExchangeRateCalculatorBuilder exchangeRateCalculatorBuilder = Mockito.mock(ExchangeRateCalculatorBuilder.class);

	private final ExchangeRateCalculator exchangeRateCalculator = Mockito.mock(ExchangeRateCalculator.class);

	private final ArgumentCaptor<ExchangeRate> exchangeRateCaptor = ArgumentCaptor.forClass(ExchangeRate.class);
	private final ArgumentCaptor<Date> dateCaptor = ArgumentCaptor.forClass(Date.class);

	@Before
	public final void setup() {
		dependencies.put(ExchangeRateDatebaseRepository.class, exchangeRateDatebaseRepository);
		Arrays.asList(TimeCourseDividendsCurrencyConverterImpl.class.getDeclaredFields()).stream().filter(field -> dependencies.containsKey(field.getType())).forEach(field -> ReflectionTestUtils.setField(timeCourseDividendsCurrencyConverter, field.getName(), dependencies.get(field.getType())));
		Mockito.when(share.currency()).thenReturn(CURRENCY_USD);
		Mockito.when(exchangeRateCalculatorBuilder.withExchangeRates(Mockito.anyCollection())).thenReturn(exchangeRateCalculatorBuilder);
		Mockito.when(exchangeRateCalculatorBuilder.build()).thenReturn(exchangeRateCalculator);
		Mockito.when(timeCourseDividendsCurrencyConverter.exchangeRateCalculatorBuilder()).thenReturn(exchangeRateCalculatorBuilder);
		Mockito.when(exchangeRateCalculator.factor(exchangeRateCaptor.capture(), dateCaptor.capture())).thenReturn(FACTOR);

	}

	@Test
	public final void convert() {
		final Collection<ExchangeRate> exchangeRates = new ArrayList<>();
		Mockito.doAnswer(answer -> {
			exchangeRates.addAll(answer.getArgument(0));
			Assert.assertEquals(1, exchangeRates.size());
			final ExchangeRate exchangeRate = exchangeRates.stream().findAny().get();
			Assert.assertEquals(CURRENCY_USD, exchangeRate.target());
			Assert.assertEquals(TimeCourseDividendsCurrencyConverterImpl.CURRENCY_EUR, exchangeRate.source());

			return exchangeRates;
		}).when(exchangeRateDatebaseRepository).exchangerates(Mockito.anyCollection());

		final DateFormat df = new SimpleDateFormat("dd.MM.yy");
		final TimeCourse result = timeCourseDividendsCurrencyConverter.convert(timeCourse);

		Assert.assertEquals(1, result.dividends().size());
		Assert.assertEquals(df.format(date), df.format(result.dividends().get(0).date()));
		Assert.assertEquals(data.value() * FACTOR, result.dividends().get(0).value(), 1e-12);

		Mockito.verify(exchangeRateCalculatorBuilder).withExchangeRates(exchangeRates);
		Mockito.verify(exchangeRateCalculatorBuilder).build();

		Assert.assertEquals(df.format(date), df.format(dateCaptor.getValue()));
		Assert.assertEquals(TimeCourseDividendsCurrencyConverterImpl.CURRENCY_EUR, exchangeRateCaptor.getValue().source());
		Assert.assertEquals(CURRENCY_USD, exchangeRateCaptor.getValue().target());

	}

	@Test
	public final void timeCourseConverterType() {
		Assert.assertEquals(TimeCourseConverter.TimeCourseConverterType.EurDividendsCurrency, timeCourseDividendsCurrencyConverter.timeCourseConverterType());
	}

	@Test
	public final void dependenciesInjected() throws NoSuchMethodException, SecurityException {
		final Object converter = BeanUtils.instantiateClass(timeCourseDividendsCurrencyConverter.getClass().getDeclaredConstructor(ExchangeRateDatebaseRepository.class), exchangeRateDatebaseRepository);
		Assert.assertEquals(exchangeRateDatebaseRepository, DataAccessUtils
				.requiredSingleResult(Arrays.asList(TimeCourseDividendsCurrencyConverterImpl.class.getDeclaredFields()).stream().filter(field -> dependencies.containsKey(field.getType())).map(field -> ReflectionTestUtils.getField(converter, field.getName())).collect(Collectors.toList())));
	}
}
