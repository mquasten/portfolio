package de.mq.portfolio.share.support;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mq.portfolio.share.Share;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/mongo-test.xml" ,"/application-test.xml" })
@Ignore
public class HistoryArivaRestRepositoryIntegrationTest {
	
	
	@Autowired
	@Qualifier("arivaHistoryRepository")
	private  HistoryRepository   historyRestRepository; 
	private final Share share = Mockito.mock(Share.class);
	
	@Test
	public final void historyKO() {
		
		//400, 412
		Assert.assertNotNull(historyRestRepository);
		
		Mockito.doReturn(parameterMap("400")).when(share).gatewayParameter();
	
		
		Mockito.doReturn("850663").when(share).wkn(); 
		Mockito.doReturn("KO").when(share).code();
		historyRestRepository.history(share).rates().forEach(rate -> System.out.println(rate.date() + "=" + rate.value()));
	}

	private Map<String, String> parameterMap(final String shareId) {
		final Map<String,String>  parameter = new HashMap<>();
		parameter.put("shareId", shareId);
		parameter.put("stockExchangeId", "21");
		return parameter;
	}
	
	private Map<String, String> parameterMap(final String shareId, final String boerseId) {
		final Map<String,String>  parameter = new HashMap<>();
		parameter.put("shareId", shareId);
		parameter.put("stockExchangeId", boerseId );
		return parameter;
	}
	
	@Test
	public final void historyJNJ() {
		Assert.assertNotNull(historyRestRepository);
		Mockito.doReturn(parameterMap("412")).when(share).gatewayParameter();
		//Mockito.doReturn("412").when(share).id2(); //JNJ
		Mockito.doReturn("853260").when(share).wkn();// JNJ
		Mockito.doReturn("JNJ").when(share).code();
		
		historyRestRepository.history(share).rates().forEach(rate -> System.out.println(rate.date() + "=" + rate.value()));
	}
	
	
	@Test
	public final void dax() {
		Mockito.doReturn(parameterMap("290", "12")).when(share).gatewayParameter();
		Mockito.doReturn("846900").when(share).wkn();
		Mockito.doReturn("^GDAXI").when(share).code();
		Mockito.doReturn(true).when(share).isIndex();
		historyRestRepository.history(share).rates().forEach(rate -> System.out.println(rate.date() + "=" + rate.value()));
	}
	
	@Test
	public final void dow() {
		Mockito.doReturn(parameterMap("4325", "71")).when(share).gatewayParameter();
		Mockito.doReturn("969420").when(share).wkn();
		Mockito.doReturn(true).when(share).isIndex();
		Mockito.doReturn("^DJI").when(share).code();
		historyRestRepository.history(share).rates().forEach(rate -> System.out.println(rate.date() + "=" + rate.value()));
	}
	

}
