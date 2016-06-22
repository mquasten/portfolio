package de.mq.portfolio.share.support;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ReflectionUtils;

import de.mq.portfolio.share.Data;
import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;

public class TimeCourseTest {

	private static final String WKN = "KM19680528";
	private static final String CODE = "KMMM";
	private static final String NAME = "Minogue-Music";
	private static final String ID = "19680528";
	private static final String TIME_COURSE = "TimeCourse";
	private final double rate1 = 1.0d;
	private final double rate2 = 1.5d;
	private final double rate3 = 2.0d;
	private final Date date1 = Mockito.mock(Date.class);
	private final Date date2 = Mockito.mock(Date.class);
	private final Date date3 = Mockito.mock(Date.class);

	final Share share = Mockito.mock(Share.class);

	final Collection<Data> rates = new ArrayList<>();
	final Collection<Data> dividends = new ArrayList<>();

	final Data data1 = Mockito.mock(Data.class);
	final Data data2 = Mockito.mock(Data.class);
	final Data data3 = Mockito.mock(Data.class);

	final Data datax = Mockito.mock(Data.class);
	final Data datay = Mockito.mock(Data.class);

	private TimeCourse timeCourse;

	@Before
	public final void setup() {

		Mockito.when(datax.value()).thenReturn(0.5d);
		Mockito.when(datay.value()).thenReturn(0.25d);

		Mockito.when(data1.date()).thenReturn(date1);
		Mockito.when(data1.value()).thenReturn(rate1);
		rates.add(data1);

		Mockito.when(data2.date()).thenReturn(date2);
		Mockito.when(data2.value()).thenReturn(rate2);
		rates.add(data2);

		Mockito.when(data3.date()).thenReturn(date3);
		Mockito.when(data3.value()).thenReturn(rate3);
		rates.add(data3);
		dividends.add(datax);
		dividends.add(datay);
		Mockito.when(share.name()).thenReturn(NAME);
		Mockito.when(share.code()).thenReturn(CODE);
		Mockito.when(share.wkn()).thenReturn(WKN);
		timeCourse = new TimeCourseImpl(share, rates, dividends);
	}

	@Test
	public final void share() {
		Assert.assertEquals(share, timeCourse.share());
	}

	@Test
	public final void rates() {
		Assert.assertEquals(rates, timeCourse.rates());
	}

	@Test
	public final void onBeforeSave() {
		Assert.assertEquals(0d, timeCourse.variance());
		Assert.assertEquals(0d, timeCourse.meanRate());

		((TimeCourseImpl) timeCourse).onBeforeSave();

		doubleEquals(5d / 12d, timeCourse.meanRate());
		doubleEquals(1d / 144d, timeCourse.variance());
		
		doubleEquals(1d/12, timeCourse.standardDeviation());
		

	}

	@Test
	public final void totalRateDividends() {
		Mockito.when(data1.value()).thenReturn(10d);
		((TimeCourseImpl) timeCourse).onBeforeSave();

		doubleEquals(3d / 40, timeCourse.totalRateDividends());
	}

	@Test
	public final void totalRateDividendsDividendsEmpty() {

		ReflectionTestUtils.setField(timeCourse, "dividends", new ArrayList<>());
		((TimeCourseImpl) timeCourse).onBeforeSave();

		doubleEquals(0d, timeCourse.totalRateDividends());

	}

	@Test
	public final void totalRateDividendsRatesEmpty() {
		ReflectionTestUtils.setField(timeCourse, "rates", new ArrayList<>());

		((TimeCourseImpl) timeCourse).onBeforeSave();
		doubleEquals(0d, timeCourse.totalRateDividends());

	}

	private void doubleEquals(final double d1, double d2) {
		Assert.assertTrue(Math.abs(d1 - d2) < 1e-15);
		;
	}

	@Test
	public final void covarianz() {
		((TimeCourseImpl) timeCourse).onBeforeSave();
		Assert.assertEquals(1d, timeCourse.covariance(timeCourse) / timeCourse.variance());
		final TimeCourse other = other();

		doubleEquals(1d / (24d * 24d), other.variance());
		doubleEquals(-7d / 24d, other.meanRate());

		doubleEquals(1d / (12d * 24d), other.covariance(timeCourse));
		doubleEquals(1d / (12d * 24d), timeCourse.covariance(other));

	}

	@Test
	public final void corelation() {
		((TimeCourseImpl) timeCourse).onBeforeSave();
		Assert.assertEquals(1d, timeCourse.covariance(timeCourse) / timeCourse.variance());
		final TimeCourse other = other();

		doubleEquals(1d, timeCourse.correlation(other));
		doubleEquals(1d, other.correlation(timeCourse));
	}

	private TimeCourse other() {
		final Collection<Data> rates1 = new ArrayList<>();
		rates1.add(data3);
		rates1.add(data2);

		rates1.add(data1);
		final TimeCourse other = new TimeCourseImpl(share, rates1, dividends);
		((TimeCourseImpl) other).onBeforeSave();
		return other;
	}

	@Test
	public final void dividends() {
		Assert.assertEquals(dividends, timeCourse.dividends());

	}

	@Test
	public final void constructor() {
		final TimeCourse timeCourse = BeanUtils.instantiateClass(TimeCourseImpl.class);
		Assert.assertTrue(timeCourse.dividends().isEmpty());
		Assert.assertTrue(timeCourse.rates().isEmpty());
	}

	@Test
	public final void annotations() {
		Assert.assertTrue(TimeCourseImpl.class.isAnnotationPresent(Document.class));

		Assert.assertEquals(TIME_COURSE, TimeCourseImpl.class.getAnnotation(Document.class).collection());

		Assert.assertTrue(ReflectionUtils.findField(TimeCourseImpl.class, "id").isAnnotationPresent(Id.class));

		final Collection<Field> fields = Arrays.asList(TimeCourseImpl.class.getDeclaredFields()).stream()
				.filter(field -> field.getType().isAssignableFrom(List.class)).collect(Collectors.toList());
		Assert.assertEquals(2, fields.size());

		fields.stream().forEach(field -> Assert.assertTrue(field.isAnnotationPresent(Reference.class)));

	}

	@Test
	public final void id() {
		Assert.assertNull(timeCourse.id());
		ReflectionUtils.doWithFields(TimeCourseImpl.class,
				field -> ReflectionTestUtils.setField(timeCourse, field.getName(), ID),
				field -> field.isAnnotationPresent(Id.class));
		Assert.assertEquals(ID, timeCourse.id());
	}

	@Test
	public final void name() {
		Assert.assertEquals(NAME, timeCourse.name());
		setShareNull();
		Assert.assertNull(timeCourse.name());
	}

	private void setShareNull() {
		ReflectionUtils.doWithFields(TimeCourseImpl.class,
				field -> ReflectionTestUtils.setField(timeCourse, field.getName(), null),
				field -> field.getType().equals(Share.class));
	}

	@Test
	public final void code() {
		Assert.assertEquals(CODE, timeCourse.code());
		setShareNull();
		Assert.assertNull(CODE, timeCourse.code());
	}

	@Test
	public final void wkn() {
		Assert.assertEquals(WKN, timeCourse.wkn());
		setShareNull();
		Assert.assertNull(timeCourse.wkn());
	}

	@Test
	public final void start() {
		Assert.assertEquals(date1, timeCourse.start());
		setListToNull();
		Assert.assertNull(timeCourse.start());
		setListToEmptyList();
		Assert.assertNull(timeCourse.start());
	}

	@Test
	public final void end() {
		Assert.assertEquals(date3, timeCourse.end());
		setListToNull();
		Assert.assertNull(timeCourse.end());
		setListToEmptyList();
		Assert.assertNull(timeCourse.end());
	}

	private void setListToEmptyList() {
		ReflectionUtils.doWithFields(TimeCourseImpl.class,
				field -> ReflectionTestUtils.setField(timeCourse, field.getName(), new ArrayList<>()),
				field -> field.getType().equals(List.class));
	}

	private void setListToNull() {
		ReflectionUtils.doWithFields(TimeCourseImpl.class,
				field -> ReflectionTestUtils.setField(timeCourse, field.getName(), null),
				field -> field.getType().equals(List.class));
	}

}
