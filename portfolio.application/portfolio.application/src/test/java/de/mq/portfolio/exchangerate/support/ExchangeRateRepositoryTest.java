package de.mq.portfolio.exchangerate.support;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestOperations;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.gateway.GatewayParameterAggregation;
import de.mq.portfolio.share.Data;
import org.junit.Assert;
import org.junit.Before;

public class ExchangeRateRepositoryTest {
	
	LocalDate date = LocalDate.of(1968, Month.MAY, 28);
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	double value = 1.58;
	
	private  final String data = String.format("%s;.\n%s;%s\n%s\nx;x\nx\n%s;.", formatter.format(date.minusDays(1)) ,formatter.format(date), String.valueOf(value).replace('.', ',') ,formatter.format(date), formatter.format(date.plusDays(1)) );

	private final String URL = "http://www.bundesbank.de/cae/servlet/StatisticDownload?tsId=BBEX3.D.{targetCurrency}.{sourceCurrency}.BB.AC.000&its_csvFormat=de&its_fileFormat=csv&mode=its";
	
	private final RestOperations restOperations = Mockito.mock(RestOperations.class);
	private final ExchangeRateRepository  exchangeRateRepository = new ExchangeRateRepositoryImpl (restOperations);
	
	private final Map<String,String> parameters = new HashMap<>();
	
	private final GatewayParameter gatewayParameter = Mockito.mock(GatewayParameter.class);
	
	
	@SuppressWarnings("unchecked")
	private final GatewayParameterAggregation<ExchangeRate> gatewayParameterAggregation = Mockito.mock(GatewayParameterAggregation.class);
	@Before
	public final void setup() {
		
		parameters.put("targetCurrency", "USD");
		parameters.put("sourceCurrency", "EUR");
		Mockito.when(gatewayParameter.urlTemplate()).thenReturn(URL);
		Mockito.when(gatewayParameter.parameters()).thenReturn(parameters);
		Mockito.when(gatewayParameterAggregation.gatewayParameter(Gateway.CentralBankExchangeRates)).thenReturn(gatewayParameter);
		Mockito.when(restOperations.getForObject(URL, String.class, parameters)).thenReturn(data)	;
	}
	
	@Test
	public final void history() {
	
		final Collection<Data>  results = exchangeRateRepository.history(gatewayParameterAggregation);
		
		Assert.assertEquals(2, results.size());
		Assert.assertTrue(results.stream().findFirst().isPresent());
		Assert.assertEquals(asDate(date), results.stream().findFirst().get().date());
		
		Assert.assertEquals((Double) value, (Double) results.stream().findFirst().get().value());
		Assert.assertTrue(results.stream().skip(1).findFirst().isPresent());
		Assert.assertEquals(asDate(date.plusDays(1)), results.stream().skip(1).findFirst().get().date());
		
		Assert.assertEquals((Double) value, (Double) results.stream().skip(1).findFirst().get().value());
		
	}
	
	private  Date asDate(LocalDate localDate) {
	    return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	}
	
	@Test
	public final void  supports() {
		Assert.assertEquals(Gateway.CentralBankExchangeRates, exchangeRateRepository.supports());
	}
}
