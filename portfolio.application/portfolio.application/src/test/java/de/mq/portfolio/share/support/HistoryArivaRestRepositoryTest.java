package de.mq.portfolio.share.support;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;


import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.gateway.GatewayParameterAggregation;
import de.mq.portfolio.gateway.support.GatewayHistoryRepository;
import de.mq.portfolio.share.Data;
import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.support.ExceptionTranslationBuilderImpl;

public class HistoryArivaRestRepositoryTest {

	private static final String ARIVA_END_DATE = "endDate";

	private static final String ARIVA_START_DATE = "startDate";

	private static final String EXCHANGE_RATES_FIELD = "exchangeRates";

	private static final String SHARE_NAME = "coca_cola_aktie";

	private static final String DIVIDENDS_URL = "http://www.ariva.de/{shareName}/historische_ereignisse";

	private static final String START_RATE = "90,00";

	private static final String END_RATE = "90,50";

	private static final String ARIVA_STOCK_EXCHANGE_ID = "stockExchangeId";

	private static final String ARIVA_SHARE_ID = "shareId";

	private static final String ARIVA_SHARE_ID_VALUE = "4711";

	private static String ARIVA_STOCK_EXCHANGE_ID_VALUE = "21";
	private static final String CODE = "JNJ";

	private final String datePattern = "yyyy-MM-dd";

	private final HistoryArivaRestRepositoryImpl historyRepository = Mockito.mock(HistoryArivaRestRepositoryImpl.class, Mockito.CALLS_REAL_METHODS);

	private final Map<Class<?>, Object> dependencies = new HashMap<>();

	

	private final GatewayParameter gatewayParameter = Mockito.mock(GatewayParameter.class);

	private final Share share = Mockito.mock(Share.class);

	private final HttpHeaders httpHeaders = Mockito.mock(HttpHeaders.class);

	private static String WKN = "853260";

	private final static String CSV_PATTERN = "Datum|Erster|Hoch|Tief|Schlusskurs|Stuecke|Volumen\n" + "%s|xxx|xxx|xxx|%s|xxx|xxx\n" + "%s|xxx|xxx|xxx|%s|xxx|xxx\nxxx|xxx\n";

	private final static String CSV_PATTERN_INDEX = "Datum|Erster|Hoch|Tief|Schlusskurs|Stuecke|Volumen\n" + "%s|xxx|xxx|xxx|%s\nxxx|xxx\n";

	@SuppressWarnings("unchecked")
	private final ResponseEntity<String> responseEntity = (ResponseEntity<String>) Mockito.mock((Class<?>) ResponseEntity.class);

	private final String urlTemplate = "url?secu={shareId}?boerse_id={stockExchangeId}&min_time={startDate}&max_time={endDate}&trenner={delimiter}";
	private final Map<String, String> parameters = new HashMap<>();

	@SuppressWarnings("unchecked")
	private GatewayParameterAggregation<Share> gatewayParameterAggregation = Mockito.mock(GatewayParameterAggregation.class);
	
	private final GatewayHistoryRepository gatewayHistoryRepository = Mockito.mock(GatewayHistoryRepository.class);


	@Before
	public final void setup() {

		final Map<String, String> headers = new HashMap<>();
		headers.put("Content-Disposition", String.format("filename=wkn_%s_historic.csv", WKN));

		// Content-Disposition=attachment; filename=wkn_853260_historic.csv
		parameters.put(ARIVA_SHARE_ID, ARIVA_SHARE_ID_VALUE);

		parameters.put(ARIVA_STOCK_EXCHANGE_ID, ARIVA_STOCK_EXCHANGE_ID_VALUE);

		parameters.put(ARIVA_START_DATE, dateString(365));
		parameters.put(ARIVA_END_DATE, dateString(1));
		parameters.put(HistoryArivaRestRepositoryImpl.PARAM_DELIMITER, "|");
		dependencies.put(boolean.class, Boolean.TRUE);
		dependencies.put(DateFormat.class, new SimpleDateFormat(datePattern));
		dependencies.put(GatewayHistoryRepository.class, gatewayHistoryRepository);

		dependencies.put(Collection.class, Arrays.asList(HistoryArivaRestRepositoryImpl.Imports.Rates));

		Mockito.doReturn(headers).when(httpHeaders).toSingleValueMap();
		Mockito.doReturn(httpHeaders).when(responseEntity).getHeaders();

		inject();
		Mockito.doReturn(CODE).when(share).code();
		Mockito.doReturn(WKN).when(share).wkn();

		Mockito.doReturn(parameters).when(gatewayParameter).parameters();
		Mockito.doReturn(urlTemplate).when(gatewayParameter).urlTemplate();

		Mockito.when(gatewayParameterAggregation.domain()).thenReturn(share);
		Mockito.when(gatewayParameterAggregation.gatewayParameter(Gateway.ArivaRateHistory)).thenReturn(gatewayParameter);

		Mockito.doAnswer(answer -> {
			
			Assert.assertEquals( gatewayParameter, answer.getArguments()[0]);
			
			return responseEntity;
		}

		).when(gatewayHistoryRepository).history(gatewayParameter);
		Mockito.doReturn(String.format(CSV_PATTERN, dateString(1), END_RATE, dateString(365), START_RATE)).when(responseEntity).getBody();
		Mockito.doAnswer(answer -> new DefaultConversionService()).when(historyRepository).configurableConversionService();
		Mockito.doAnswer(answer -> new ExceptionTranslationBuilderImpl<>()).when(historyRepository).exceptionTranslationBuilder();
	}

	private void inject() {
		Arrays.asList(HistoryArivaRestRepositoryImpl.class.getDeclaredFields()).stream().filter(field -> dependencies.containsKey(field.getType())).forEach(field -> ReflectionTestUtils.setField(historyRepository, field.getName(), dependencies.get(field.getType())));
	}

	@Test
	public final void history() throws ParseException {
		final TimeCourse result = historyRepository.history(gatewayParameterAggregation);
		Assert.assertEquals(2, result.rates().size());
		Assert.assertEquals(new SimpleDateFormat(datePattern).parse(dateString(365)), result.rates().get(0).date());
		Assert.assertEquals((Double) Double.parseDouble(START_RATE.replace(",", ".")), (Double) result.rates().get(0).value());

		Assert.assertEquals(new SimpleDateFormat(datePattern).parse(dateString(1)), result.rates().get(1).date());
		Assert.assertEquals((Double) Double.parseDouble(END_RATE.replace(",", ".")), (Double) result.rates().get(1).value());
	}

	private String dateString(final long daysBack) {
		return new SimpleDateFormat(datePattern).format(Date.from(LocalDate.now().minusDays(daysBack).atStartOfDay(ZoneId.systemDefault()).toInstant()));
	}

	@Test(expected = IllegalArgumentException.class)
	public final void historyWrongContentDispositionHeader() throws ParseException {

		final Map<String, String> headers = new HashMap<>();
		headers.put("Content-Disposition", String.format("filename=wkn_historic.csv", WKN));
		Mockito.doReturn(headers).when(httpHeaders).toSingleValueMap();

		historyRepository.history(gatewayParameterAggregation);

	}

	@Test
	public final void historyIndex() throws ParseException {
		dependencies.put(boolean.class, Boolean.FALSE);
		inject();
		Mockito.doReturn(String.format(CSV_PATTERN_INDEX, dateString(1), END_RATE, dateString(365), START_RATE)).when(responseEntity).getBody();
		Mockito.doReturn(true).when(share).isIndex();

		final TimeCourse result = historyRepository.history(gatewayParameterAggregation);
		Assert.assertEquals(1, result.rates().size());
		Assert.assertEquals(new SimpleDateFormat(datePattern).parse(dateString(1)), result.rates().get(0).date());
		Assert.assertEquals((Double) Double.parseDouble(END_RATE.replace(",", ".")), (Double) result.rates().get(0).value());
	}

	@Test
	public final void dividends() throws IOException {
		final List<Data> expectedDividends = Arrays.asList(new DataImpl("2016-9-13", 0.31d), new DataImpl("2016-11-29", 0.33d), new DataImpl("2017-3-13", 0.35d), new DataImpl("2017-6-13", 0.33));

		prepareForDividends();

		final TimeCourse timeCourse = historyRepository.history(gatewayParameterAggregation);
		Assert.assertTrue(timeCourse.rates().isEmpty());
		Assert.assertEquals(4, timeCourse.dividends().size());

		IntStream.range(0, 4).forEach(i -> {
			Assert.assertEquals(expectedDividends.get(i).date(), timeCourse.dividends().get(i).date());
			Assert.assertEquals((Double) expectedDividends.get(i).value(), (Double) timeCourse.dividends().get(i).value());
		});
	}


	private void prepareForDividends() throws IOException {
		dependencies.put(Collection.class, Arrays.asList(HistoryArivaRestRepositoryImpl.Imports.Dividends));
		dependencies.put(DateFormat.class, new SimpleDateFormat("dd.MM.yy"));
		inject();
		Mockito.doReturn("KO").when(share).code();
		Mockito.doReturn("USD").when(share).currency();
		parameters.clear();
		parameters.put("shareName", SHARE_NAME);
		Mockito.doReturn(DIVIDENDS_URL).when(gatewayParameter).urlTemplate();
		

		Mockito.when(gatewayParameterAggregation.gatewayParameter(Gateway.ArivaRateHistory)).thenReturn(null);
		Mockito.when(gatewayParameterAggregation.gatewayParameter(Gateway.ArivaDividendHistory)).thenReturn(gatewayParameter);

		Mockito.doReturn(parameters).when(gatewayParameter).parameters();
		final String html = org.springframework.util.StreamUtils.copyToString(getClass().getClassLoader().getResourceAsStream("arivaDividend.html"), Charset.forName("UTF-8"));
		Mockito.when(responseEntity.getBody()).thenReturn(html);
		Mockito.doAnswer(answer -> {
			
			Assert.assertEquals(gatewayParameter, answer.getArguments()[0]);
		
			return responseEntity;
		}).when(gatewayHistoryRepository).history(gatewayParameter);
		final GregorianCalendar cal = new GregorianCalendar(2017, 6, 1);
		cal.add(Calendar.DATE, -365);

		// Mockito.doReturn(cal.getTime()).when(historyRepository).date(Mockito.any(LocalDate.class),
		// Mockito.anyLong());

	}

	@Test
	public final void importDividendsIndex() throws IOException {
		prepareForDividends();
		Mockito.doReturn(true).when(share).isIndex();

		final TimeCourse timeCourse = historyRepository.history(gatewayParameterAggregation);
		Assert.assertTrue(timeCourse.rates().isEmpty());
		Assert.assertTrue(timeCourse.dividends().isEmpty());

		Mockito.verifyNoMoreInteractions(gatewayHistoryRepository);

	}

	@Test
	public final void constructorDependencies() throws NoSuchMethodException, SecurityException {
		final HistoryDateUtil historyDateUtil = Mockito.mock(HistoryDateUtil.class);
		final Constructor<?> constructor = (Constructor<?>) historyRepository.getClass().getDeclaredConstructor(GatewayHistoryRepository.class, HistoryDateUtil.class, boolean.class, String.class);
		final Object historyRepository = BeanUtils.instantiateClass(constructor, gatewayHistoryRepository, historyDateUtil, true, "rates,dividends");

		final Map<Class<?>, Object> dependencies = Arrays.asList(HistoryArivaRestRepositoryImpl.class.getDeclaredFields()).stream().filter(field -> !field.getName().equals(EXCHANGE_RATES_FIELD)).filter(field -> !field.getType().equals(DateFormat.class))
				.filter(field -> !field.getType().equals(int.class)).filter(field -> !Modifier.isStatic(field.getModifiers())).collect(Collectors.toMap(field -> field.getType(), field -> ReflectionTestUtils.getField(historyRepository, field.getName())));

		Assert.assertEquals(gatewayHistoryRepository, dependencies.get(GatewayHistoryRepository.class));

		Assert.assertTrue((Boolean) dependencies.get(boolean.class));
		Assert.assertEquals(Arrays.asList(HistoryArivaRestRepositoryImpl.Imports.Rates, HistoryArivaRestRepositoryImpl.Imports.Dividends), dependencies.get(Collection.class));

	}

	@Test
	public final void supports() {
		Assert.assertEquals(Arrays.asList(Gateway.ArivaRateHistory, Gateway.ArivaDividendHistory), historyRepository.supports(share));
	}

	@Test
	public final void supportsIndex() {
		Mockito.when(share.isIndex()).thenReturn(true);
		Assert.assertEquals(Arrays.asList(Gateway.ArivaRateHistory), historyRepository.supports(share));
	}

	@Test
	public final void converters() {
		Mockito.when(share.currency()).thenReturn(TimeCourseDividendsCurrencyConverterImpl.CURRENCY_EUR);
		Assert.assertEquals(Arrays.asList(TimeCourseConverter.TimeCourseConverterType.DateInRange), historyRepository.converters(share));
	}

	@Test
	public final void convertersNotEuro() {
		Mockito.when(share.currency()).thenReturn("USD");
		Assert.assertEquals(Arrays.asList(TimeCourseConverter.TimeCourseConverterType.DateInRange, TimeCourseConverter.TimeCourseConverterType.EurDividendsCurrency), historyRepository.converters(share));
	}

}
