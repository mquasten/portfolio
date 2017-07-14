package de.mq.portfolio.share.support;

import java.lang.reflect.Constructor;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestOperations;

import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.gateway.support.GatewayParameterRepository;
import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.support.ExceptionTranslationBuilderImpl;

public class HistoryGoogleRestRepositoryTest {

	private static final String CODE = "KO";

	private static final String LINES_PATTERN = "Date,Open,High,Low,Close,Volume\n%s,x,x,x,%s,x\n28-Mai-68, Kylies Bithday\n%s,x,x,x,%s,x";

	private static final double CURRENT_RATE = 100.00d;

	private static final double START_RATE = 47.11;

	private static final DateFormat dateFormat = new SimpleDateFormat("d-MMM-yy", Locale.US);

	private static final int DAYS_OFFSET = 365;

	private final RestOperations restOperations = Mockito.mock(RestOperations.class);

	private final GatewayParameterRepository gatewayParameterRepository = Mockito.mock(GatewayParameterRepository.class);

	private final HistoryGoogleRestRepositoryImpl historyRepository = Mockito.mock(HistoryGoogleRestRepositoryImpl.class, Mockito.CALLS_REAL_METHODS);

	private final Share share = Mockito.mock(Share.class);

	private final String startDate = dateFormat.format(startDate());
	private final String now = dateFormat.format(new Date());

	private final String lines = String.format(LINES_PATTERN, now, CURRENT_RATE, startDate, START_RATE);

	private final String urlTemplate = "http://www.google.com/finance/historical?q={query}&startdate={startdate}&output=csv";

	private final GatewayParameter gatewayParameter = Mockito.mock(GatewayParameter.class);

	private final Map<String, String> urlParameter = new HashMap<>();

	private final Map<Class<?>, Object> dependencies = new HashMap<>();

	@Before
	public final void setup() {

		final Map<String, String> parameter = new HashMap<>();
		parameter.put("query", "NYSE:KO");

		parameter.put("query", "NYSE:KO");

		urlParameter.put("startdate", startDate);

		urlParameter.putAll(parameter);

		dependencies.put(RestOperations.class, restOperations);
		dependencies.put(GatewayParameterRepository.class, gatewayParameterRepository);

		dependencies.put(DateFormat.class, new SimpleDateFormat("d-MMM-yy", Locale.US));
		// Mockito.when(share.code2()).thenReturn(CODE2);

		Mockito.doReturn(CODE).when(share).code();
		Arrays.asList(HistoryGoogleRestRepositoryImpl.class.getDeclaredFields()).stream().filter(field -> dependencies.containsKey(field.getType())).forEach(field -> ReflectionTestUtils.setField(historyRepository, field.getName(), dependencies.get(field.getType())));

		Mockito.when(restOperations.getForObject(urlTemplate, String.class, urlParameter)).thenReturn(lines);

		Mockito.doAnswer(answer -> new DefaultConversionService()).when(historyRepository).configurableConversionService();

		Mockito.doAnswer(answer -> new ExceptionTranslationBuilderImpl<>()).when(historyRepository).exceptionTranslationBuilder();

		Mockito.doReturn(gatewayParameter).when(gatewayParameterRepository).shareGatewayParameter(Gateway.GoogleRateHistory, CODE);

		Mockito.doReturn(parameter).when(gatewayParameter).parameters();
		Mockito.doReturn(urlTemplate).when(gatewayParameter).urlTemplate();
	}

	@Test
	public final void history() throws ParseException {

		final TimeCourse timeCourse = historyRepository.history(share);

		System.out.println(historyRepository);

		Assert.assertEquals(2, timeCourse.rates().size());
		Assert.assertTrue(timeCourse.dividends().isEmpty());
		Assert.assertEquals(share, timeCourse.share());

		Assert.assertEquals(dateFormat.parse(startDate), timeCourse.rates().get(0).date());
		Assert.assertEquals((Double) START_RATE, (Double) timeCourse.rates().get(0).value());

		Assert.assertEquals(dateFormat.parse(now), timeCourse.rates().get(1).date());
		Assert.assertEquals((Double) CURRENT_RATE, (Double) timeCourse.rates().get(1).value());

		Mockito.verify(restOperations).getForObject(urlTemplate, String.class, urlParameter);

	}

	@Test(expected = ConversionFailedException.class)
	public final void historyInvalidData() {
		Mockito.when(restOperations.getForObject(urlTemplate, String.class, urlParameter)).thenReturn(String.format(LINES_PATTERN, now, CURRENT_RATE, startDate, "x.x"));

		historyRepository.history(share);
	}

	private Date startDate() {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		cal.add(Calendar.DAY_OF_YEAR, -DAYS_OFFSET);
		return cal.getTime();
	}

	@Test
	public final void createWithDependencies() throws NoSuchMethodException, SecurityException {
		final Constructor<? extends HistoryRepository> constructor = historyRepository.getClass().getDeclaredConstructor(RestOperations.class, GatewayParameterRepository.class);
		final HistoryRepository historyRepository = BeanUtils.instantiateClass(constructor, restOperations, gatewayParameterRepository);
		final Map<Class<?>, Object> injectedDependencies = Arrays.asList(HistoryGoogleRestRepositoryImpl.class.getDeclaredFields()).stream().filter(field -> dependencies.containsKey(field.getType())).collect(Collectors.toMap(field -> field.getType(), field -> ReflectionTestUtils.getField(historyRepository, field.getName())));

		Assert.assertEquals(dependencies, injectedDependencies);
	}

}
