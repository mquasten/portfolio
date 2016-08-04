package de.mq.portfolio.exchangerate.support;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestOperations;

import de.mq.portfolio.share.Data;
import junit.framework.Assert;

public class ExchangeRateRepositoryTest {
	
	LocalDate date = LocalDate.of(1968, Month.MAY, 28);
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	double value = 1.58;
	
	private  final String data = String.format("%s;.\n%s;%s\n%s\nx;x\nx\n%s;.", formatter.format(date.minusDays(1)) ,formatter.format(date), String.valueOf(value).replace('.', ',') ,formatter.format(date), formatter.format(date.plusDays(1)) );

	final String URL = "http://www.bundesbank.de/cae/servlet/StatisticDownload?tsId=BBEX3.D.USD.EUR.BB.AC.000&its_csvFormat=de&its_fileFormat=csv&mode=its";
	
	final RestOperations restOperations = Mockito.mock(RestOperations.class);
	ExchangeRateRepository  exchangeRateRepository = new ExchangeRateRepositoryImpl (restOperations);
	
	@Test
	public final void history() {
		Mockito.when(restOperations.getForObject(URL, String.class)).thenReturn(data)	;
		final Collection<Data>  results = exchangeRateRepository.history(URL);
		Assert.assertEquals(2, results.size());
		Assert.assertTrue(results.stream().findFirst().isPresent());
		Assert.assertEquals(asDate(date), results.stream().findFirst().get().date());
		
		Assert.assertEquals(value, results.stream().findFirst().get().value());
		Assert.assertTrue(results.stream().skip(1).findFirst().isPresent());
		Assert.assertEquals(asDate(date.plusDays(1)), results.stream().skip(1).findFirst().get().date());
		
		Assert.assertEquals(value, results.stream().skip(1).findFirst().get().value());
		
	}
	
	public  Date asDate(LocalDate localDate) {
	    return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	  }
}
