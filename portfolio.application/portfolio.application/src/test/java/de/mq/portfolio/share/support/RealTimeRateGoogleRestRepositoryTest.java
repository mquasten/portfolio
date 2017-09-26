package de.mq.portfolio.share.support;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestOperations;

import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.gateway.GatewayParameterAggregation;
import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.support.ExceptionTranslationBuilderImpl;

public class RealTimeRateGoogleRestRepositoryTest {

	private static final String CODE_KO = "KO";

	private static final String MARKET_KO = "NYSE";

	private static final String MARKET_SAP = "ETR";

	private static final String CODE_SAP = "SAP.DE";

	private static final String URL = "urlTemplate";

	private final Date yesterday = dateForDaysBefore(1);
	private final Date today = dateForDaysBefore(0);

	private static Double START_SAP = 91d;
	private static Double END_SAP = 92d;

	private static Double START_KO = 45d;
	private static Double END_KO = 46d;

	private final RestOperations restOperations = Mockito.mock(RestOperations.class);

	private final GatewayParameter gatewayParameter = Mockito.mock(GatewayParameter.class);

	@SuppressWarnings("unchecked")
	private final GatewayParameterAggregation<Collection<Share>> gatewayParameterAggregation = Mockito.mock(GatewayParameterAggregation.class, Mockito.CALLS_REAL_METHODS);

	private Share shareSAP = Mockito.mock(Share.class);
	private Share shareKO = Mockito.mock(Share.class);
	private RealTimeRateGoogleRestRepositoryImpl rateRepository;
	private final Map<String, String> params = new HashMap<>();
	private final Map<String, String> paramsSAP = new HashMap<>();
	private final Map<String, String> paramsKO = new HashMap<>();

	@Before
	public final void setup() throws Exception {
		Mockito.when(shareSAP.code()).thenReturn(CODE_SAP);
		Mockito.when(shareKO.code()).thenReturn(CODE_KO);
		params.putAll(parameterMap(shareSAP.code() + "," + shareKO.code(), MARKET_SAP + "," + MARKET_KO));

		paramsSAP.putAll(parameterMap(shareSAP.code(), MARKET_SAP));
		paramsKO.putAll(parameterMap(shareKO.code(), MARKET_KO));

		Mockito.when(gatewayParameter.parameters()).thenReturn(params);
		Mockito.when(gatewayParameter.urlTemplate()).thenReturn(URL);

		Mockito.when(restOperations.getForObject(URL, String.class, paramsSAP)).thenReturn(content(START_SAP, END_SAP));
		Mockito.when(restOperations.getForObject(URL, String.class, paramsKO)).thenReturn(content(START_KO, END_KO));
		rateRepository = Mockito.mock(RealTimeRateGoogleRestRepositoryImpl.class);
		Mockito.doAnswer(a -> new ExceptionTranslationBuilderImpl<>()).when(rateRepository).exceptionTranslationBuilder();
		Arrays.asList(RealTimeRateGoogleRestRepositoryImpl.class.getDeclaredFields()).stream().filter(field -> field.getType().equals(RestOperations.class)).forEach(field -> ReflectionTestUtils.setField(rateRepository, field.getName(), restOperations));
		Mockito.when(gatewayParameterAggregation.gatewayParameter(Gateway.GoogleRealtimeRate)).thenReturn(gatewayParameter);
		Mockito.when(gatewayParameterAggregation.domain()).thenReturn(Arrays.asList(shareSAP, shareKO));

	}

	private Map<String, String> parameterMap(final String query, final String market) {
		final Map<String, String> params = new HashMap<>();
		params.put("query", query.replaceFirst(".DE", ""));
		params.put("market", market);
		return params;
	}

	private String content(final double start, final double end) {
		return "EXCHANGE%3DNYSE\n" + "MARKET_OPEN_MINUTE=570\n" + "MARKET_CLOSE_MINUTE=960\n" + "INTERVAL=60\n" + "COLUMNS=DATE,CLOSE\n" + "DATA=\n" + "TIMEZONE_OFFSET=-240\n" + "a" + new Long(yesterday.getTime() / 1000) + ",134.11\n" + "1,134.14\n" + "360," + start + "\n" + "a"
				+ new Long(today.getTime() / 1000) + ",135.43\n" + "1,135.14\n" + "210," + end + "\n";
	}

	@Test
	public final void rates() throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		final List<TimeCourse> timeCourses = rateRepository.rates(gatewayParameterAggregation).stream().collect(Collectors.toList());
		Assert.assertEquals(2, timeCourses.size());
		timeCourses.forEach(timeCourse -> {
			Assert.assertEquals(dateForDaysBefore(1, 22, 30), timeCourse.rates().get(0).date());
			Assert.assertEquals(dateForDaysBefore(0, 20, 0), timeCourse.rates().get(1).date());
		});

		Assert.assertEquals(START_SAP, (Double) timeCourses.get(0).rates().get(0).value());
		Assert.assertEquals(END_SAP, (Double) timeCourses.get(0).rates().get(1).value());
		Assert.assertEquals(START_KO, (Double) timeCourses.get(1).rates().get(0).value());
		Assert.assertEquals(END_KO, (Double) timeCourses.get(1).rates().get(1).value());
	}

	private Date dateForDaysBefore(final int daysBack, int hour, int min) {
		return Date.from(LocalDateTime.now().plusDays(-daysBack).truncatedTo(ChronoUnit.DAYS).plusHours(hour).plusMinutes(min).atZone(ZoneId.systemDefault()).toInstant());
	}

	private Date dateForDaysBefore(final int daysBack) {
		return dateForDaysBefore(daysBack, 16, 30);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void ratesDifferentSizeGatewayParameterArrays() {
		params.putAll(parameterMap(shareSAP.code() + "," + shareKO.code(), MARKET_SAP));
		rateRepository.rates(gatewayParameterAggregation);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void ratesCurrentTimeOffsetMissing() {
		Mockito.when(restOperations.getForObject(URL, String.class, paramsSAP)).thenReturn(invalidContent(true));
		rateRepository.rates(gatewayParameterAggregation);

	}

	@Test(expected = IllegalArgumentException.class)
	public final void ratesCloseDateMissing() {
		Mockito.when(restOperations.getForObject(URL, String.class, paramsSAP)).thenReturn(invalidContent(false));
		rateRepository.rates(gatewayParameterAggregation);

	}

	@Test(expected = IllegalArgumentException.class)
	public final void ratesCloseRateMissing() {

		String invalidContent = "EXCHANGE%3DNYSE\n" + "MARKET_OPEN_MINUTE=570\n" + "MARKET_CLOSE_MINUTE=960\n" + "INTERVAL=60\n" + "COLUMNS=DATE,CLOSE\n" + "DATA=\n" + "TIMEZONE_OFFSET=-240\n" + "1,134.14\n";
		Mockito.when(restOperations.getForObject(URL, String.class, paramsSAP)).thenReturn(invalidContent);
		rateRepository.rates(gatewayParameterAggregation);

	}

	@Test(expected = IllegalArgumentException.class)
	public final void ratesLasteRateMissing() {

		String invalidContent = "EXCHANGE%3DNYSE\n" + "MARKET_OPEN_MINUTE=570\n" + "MARKET_CLOSE_MINUTE=960\n" + "INTERVAL=60\n" + "COLUMNS=DATE,CLOSE\n" + "DATA=\n" + "TIMEZONE_OFFSET=-240\n";

		Mockito.when(restOperations.getForObject(URL, String.class, paramsSAP)).thenReturn(invalidContent);
		rateRepository.rates(gatewayParameterAggregation);

	}

	private String invalidContent(final boolean closeAware) {
		String result = "EXCHANGE%3DNYSE\n" + "MARKET_OPEN_MINUTE=570\n" + "MARKET_CLOSE_MINUTE=960\n" + "INTERVAL=60\n" + "COLUMNS=DATE,CLOSE\n" + "DATA=\n" + "TIMEZONE_OFFSET=-240\n" +

				"a" + new Long(yesterday.getTime() / 1000) + ",134.11\n";

		if (closeAware) {
			result += "1,134.14\n";

		}

		result += "a" + new Long(today.getTime() / 1000) + ",135.43\n";

		return result;
	}
	
	@Test
	public void supports() {
		Assert.assertEquals(Gateway.GoogleRealtimeRate, rateRepository.supports(Arrays.asList(shareSAP, shareKO)));
	}
	
	@Test
	public final void create() throws Exception {
		final Constructor<?>  constructor=  rateRepository.getClass().getDeclaredConstructor(RestOperations.class);
		final Object repository = 	BeanUtils.instantiateClass(constructor, restOperations);
		final Object dependency = DataAccessUtils.requiredSingleResult(Arrays.asList(RealTimeRateGoogleRestRepositoryImpl.class.getDeclaredFields()).stream().filter( field -> field.getType().equals(RestOperations.class)).map(field -> ReflectionTestUtils.getField(repository, field.getName())).collect(Collectors.toSet()));
	    Assert.assertEquals(restOperations, dependency);
	}

}
