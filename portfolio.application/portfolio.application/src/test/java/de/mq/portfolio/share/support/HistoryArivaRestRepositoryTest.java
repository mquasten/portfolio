package de.mq.portfolio.share.support;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestOperations;

import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.gateway.support.GatewayParameterRepository;
import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.support.ExceptionTranslationBuilderImpl;

public class HistoryArivaRestRepositoryTest {

	private static final String START_RATE = "90,00";

	private static final String END_RATE = "90,50";

	private static final String ARIVA_STOCK_EXCHANGE_ID = "stockExchangeId";

	private static final String ARIVA_SHARE_ID = "shareId";

	private static final String ARIVA_SHARE_ID_VALUE = "4711";

	private static final String CODE = "JNJ";

	private final String datePattern = "yyyy-MM-dd";

	private final HistoryArivaRestRepositoryImpl historyRepository = Mockito.mock(HistoryArivaRestRepositoryImpl.class, Mockito.CALLS_REAL_METHODS);

	private final Map<Class<?>, Object> dependencies = new HashMap<>();

	private final RestOperations restOperations = Mockito.mock(RestOperations.class);

	private final GatewayParameterRepository gatewayParameterRepository = Mockito.mock(GatewayParameterRepository.class);

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
	@Before
	public final void setup() {

		final Map<String, String> headers = new HashMap<>();
		headers.put("Content-Disposition", String.format("filename=wkn_%s_historic.csv", WKN));

		// Content-Disposition=attachment; filename=wkn_853260_historic.csv
		parameters.put(ARIVA_SHARE_ID, ARIVA_SHARE_ID_VALUE);
		String ARIVA_STOCK_EXCHANGE_ID_VALUE = "21";
		parameters.put(ARIVA_STOCK_EXCHANGE_ID, ARIVA_STOCK_EXCHANGE_ID_VALUE);
		dependencies.put(boolean.class, Boolean.TRUE);
		dependencies.put(DateFormat.class, new SimpleDateFormat(datePattern));
		dependencies.put(RestOperations.class, restOperations);
		dependencies.put(GatewayParameterRepository.class, gatewayParameterRepository);

		Mockito.doReturn(headers).when(httpHeaders).toSingleValueMap();
		Mockito.doReturn(httpHeaders).when(responseEntity).getHeaders();

		inject();
		Mockito.doReturn(CODE).when(share).code();
		Mockito.doReturn(WKN).when(share).wkn();
		Mockito.doReturn(gatewayParameter).when(gatewayParameterRepository).shareGatewayParameter(Gateway.ArivaRateHistory, CODE);
		Mockito.doReturn(parameters).when(gatewayParameter).parameters();
		Mockito.doReturn(urlTemplate).when(gatewayParameter).urlTemplate();
		Mockito.doAnswer(answer -> {
			Assert.assertEquals(urlTemplate, answer.getArguments()[0]);
			Assert.assertEquals(String.class, answer.getArguments()[1]);

			final Map<?, ?> params = (Map<?, ?>) answer.getArguments()[2];
			Assert.assertEquals(ARIVA_SHARE_ID_VALUE, params.get(ARIVA_SHARE_ID));
			Assert.assertEquals(ARIVA_STOCK_EXCHANGE_ID_VALUE, params.get(ARIVA_STOCK_EXCHANGE_ID));
			Assert.assertEquals(dateString(1), params.get("endDate"));
			Assert.assertEquals(dateString(365), params.get("startDate"));
			Assert.assertEquals(HistoryArivaRestRepositoryImpl.DELIMITER, params.get("delimiter"));

			return responseEntity;
		}

		).when(restOperations).getForEntity(Mockito.anyString(), Mockito.any(), Mockito.any(Map.class));
		Mockito.doReturn(String.format(CSV_PATTERN, dateString(1), END_RATE, dateString(365), START_RATE)).when(responseEntity).getBody();
		Mockito.doAnswer(answer -> new DefaultConversionService()).when(historyRepository).configurableConversionService();
		Mockito.doAnswer(answer -> new ExceptionTranslationBuilderImpl<>()).when(historyRepository).exceptionTranslationBuilder();
	}

	private void inject() {
		Arrays.asList(HistoryArivaRestRepositoryImpl.class.getDeclaredFields()).stream().filter(field -> dependencies.containsKey(field.getType())).forEach(field -> ReflectionTestUtils.setField(historyRepository, field.getName(), dependencies.get(field.getType())));
	}

	@Test
	public final void history() throws ParseException {
		final TimeCourse result = historyRepository.history(share);
		Assert.assertEquals(2, result.rates().size());
		Assert.assertEquals(new SimpleDateFormat(datePattern).parse(dateString(365)), result.rates().get(0).date());
		Assert.assertEquals((Double) Double.parseDouble(START_RATE.replace(",", ".")), (Double) result.rates().get(0).value());

		Assert.assertEquals(new SimpleDateFormat(datePattern).parse(dateString(1)), result.rates().get(1).date());
		Assert.assertEquals((Double) Double.parseDouble(END_RATE.replace(",", ".")), (Double) result.rates().get(1).value());
	}

	private String dateString(final long daysBack) {
		return new SimpleDateFormat(datePattern).format(Date.from(LocalDate.now().minusDays(daysBack).atStartOfDay(ZoneId.systemDefault()).toInstant()));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public final void historyWrongContentDispositionHeader() throws ParseException {
		
		final Map<String, String> headers = new HashMap<>();
		headers.put("Content-Disposition", String.format("filename=wkn_historic.csv", WKN));
		Mockito.doReturn(headers).when(httpHeaders).toSingleValueMap();
		
		historyRepository.history(share);
		
	}

	@Test
	public final void historyIndex() throws ParseException {
		dependencies.put(boolean.class, Boolean.FALSE);
		inject();
		Mockito.doReturn(String.format(CSV_PATTERN_INDEX, dateString(1), END_RATE, dateString(365), START_RATE)).when(responseEntity).getBody();
		Mockito.doReturn(true).when(share).isIndex();
	
	
		final TimeCourse result = historyRepository.history(share);
		Assert.assertEquals(1, result.rates().size());
		Assert.assertEquals(new SimpleDateFormat(datePattern).parse(dateString(1)), result.rates().get(0).date());
		Assert.assertEquals((Double) Double.parseDouble(END_RATE.replace(",", ".")), (Double) result.rates().get(0).value());
	}
	}
