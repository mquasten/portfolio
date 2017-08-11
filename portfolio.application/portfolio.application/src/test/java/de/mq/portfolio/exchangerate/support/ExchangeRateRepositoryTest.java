package de.mq.portfolio.exchangerate.support;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpEntity;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.gateway.GatewayParameter;
import de.mq.portfolio.gateway.GatewayParameterAggregation;
import de.mq.portfolio.gateway.support.GatewayHistoryRepository;
import de.mq.portfolio.share.Data;

public class ExchangeRateRepositoryTest {
	
	private  final LocalDate date = LocalDate.of(1968, Month.MAY, 28);
	private  final  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private  final  double value = 1.58;
	
	private  final String data = String.format("%s;.\n%s;%s\n%s\nx;x\nx\n%s;.", formatter.format(date.minusDays(1)) ,formatter.format(date), String.valueOf(value).replace('.', ',') ,formatter.format(date), formatter.format(date.plusDays(1)) );
	
	private final GatewayHistoryRepository gatewayHistoryRepository = Mockito.mock(GatewayHistoryRepository.class);
	
	private final ExchangeRateRepository  exchangeRateRepository = new ExchangeRateRepositoryImpl (gatewayHistoryRepository);
	
	private final GatewayParameter gatewayParameter = Mockito.mock(GatewayParameter.class);
	
	@SuppressWarnings("unchecked")
	private final HttpEntity<String> responseEntity = Mockito.mock(HttpEntity.class);
	
	@SuppressWarnings("unchecked")
	private final GatewayParameterAggregation<ExchangeRate> gatewayParameterAggregation = Mockito.mock(GatewayParameterAggregation.class);
	@Before
	public final void setup() {
		Mockito.when(gatewayParameterAggregation.gatewayParameter(Gateway.CentralBankExchangeRates)).thenReturn(gatewayParameter);
		Mockito.when(responseEntity.getBody()).thenReturn(data);
		Mockito.when(gatewayHistoryRepository.history(gatewayParameter)).thenReturn(responseEntity);
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
