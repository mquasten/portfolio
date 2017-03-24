package de.mq.portfolio.share.support;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanUtils;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestOperations;

import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.support.ExceptionTranslationBuilderImpl;

public class RealTimeRateRepositoryTest {
	
	private static final String CODE_DOW = "^DJI";
	private static final String CODE_KO = "KO";
	private static final String CODE_SAP = "SAP.DE";
	private final RestOperations restOperations = Mockito.mock(RestOperations.class);
	private final RealTimeRateYahooRestRepositoryImpl realTimeRateRepository = Mockito.mock(RealTimeRateYahooRestRepositoryImpl.class, Mockito.CALLS_REAL_METHODS);
	
	private double sapStart = 90.65d;
	private double sapEnd = 90.95;
	private double koStart = 42.25;
	private double koEnd = 42.28;
	
	private final Date end = Date.from(LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).atZone(ZoneId.systemDefault()).toInstant());
	private final Date start = Date.from(LocalDateTime.now().plusDays(-1).truncatedTo(ChronoUnit.DAYS).atZone(ZoneId.systemDefault()).toInstant());
	
	@Before
	public final void setup() {
		Arrays.asList(RealTimeRateYahooRestRepositoryImpl.class.getDeclaredFields()).stream().filter(field -> field.getType().equals(RestOperations.class)).forEach(field ->{
			ReflectionTestUtils.setField(realTimeRateRepository, field.getName(), restOperations);
		});
		Mockito.doAnswer(a -> new   ExceptionTranslationBuilderImpl<>()).when(realTimeRateRepository).exceptionTranslationBuilder();
	
	   Mockito.when(restOperations.getForObject(String.format(RealTimeRateYahooRestRepositoryImpl.URL, String.format("%s+%s+%s", CODE_SAP, CODE_KO, CODE_DOW)), String.class)).thenReturn(String.format("\"%s\",\"SAP SE O.N.\",N/A,N/A,x,%s,%s\n\"%s\",\"Coca-Cola Company (The) Common \",N/A,N/A,x,%s,%s\n\"%s\",\"Dow Jones Index \",N/A,N/A,x,N/A,N/A", CODE_SAP, sapStart, sapEnd, CODE_KO, koStart, koEnd, CODE_DOW));
	}
	
	@Test
	public final void rates() {
		final Share sap = shareMock(CODE_SAP);
		final Share ko= shareMock(CODE_KO);
		final Share dow= shareMock(CODE_DOW);
		
		
		final List<TimeCourse> results = new ArrayList<>(realTimeRateRepository.rates(Arrays.asList(sap,ko, dow)));
		
		Assert.assertEquals(2, results.size());
		results.forEach(tc -> Assert.assertEquals(2, tc.rates().size()));
		Assert.assertEquals(sap, results.get(0).share());
		Assert.assertEquals(ko, results.get(1).share());
		Assert.assertEquals((Double) sapStart, (Double)results.get(0).rates().get(0).value());
		Assert.assertEquals((Double) sapEnd, (Double)results.get(0).rates().get(1).value());
		Assert.assertEquals((Double) koStart, (Double)results.get(1).rates().get(0).value());
		Assert.assertEquals((Double) koEnd, (Double)results.get(1).rates().get(1).value());
		IntStream.range(0, 2).forEach(i ->  Assert.assertEquals(end, results.get(i).rates().get(1).date()));
		IntStream.range(0, 2).forEach(i ->  Assert.assertEquals(start, results.get(i).rates().get(0).date()));
		
	}

	private Share shareMock(final String key) {
		final Share sap= Mockito.mock(Share.class);
		Mockito.when(sap.code()).thenReturn(key);
		return sap;
	}
	
	@Test
	public final void create() throws BeanInstantiationException, NoSuchMethodException, SecurityException {
		
		final RealTimeRateRepository realTimeRateRepository = BeanUtils.instantiateClass(this.realTimeRateRepository.getClass().getDeclaredConstructor(RestOperations.class), restOperations);
	    final boolean[] counters = {false};
		Arrays.asList(RealTimeRateYahooRestRepositoryImpl.class.getDeclaredFields()).stream().filter(field -> field.getType().equals(RestOperations.class)).forEach(field -> {
			counters[0]=true;
			Assert.assertEquals(restOperations, ReflectionTestUtils.getField(realTimeRateRepository, field.getName()));
		});
		Assert.assertTrue(counters[0]);
	}
	
}
