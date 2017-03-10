package de.mq.portfolio.share.support;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application-test.xml" })
@Ignore
public class RealTimeRateYahooRestRepositoryIntegrationTest {
	
	@Autowired
	@Qualifier("yahooRealtimeRepository")
	private  RealTimeRateRestRepository   realTimeRateRestRepository; 
	

	
	private final Collection<Share> shares = new ArrayList<>();
	
	@Before
	public  final void setup() {
		shares.clear();
		shares.add(newSahreMock("SAP.DE"));
		shares.add(newSahreMock("JNJ"));
		shares.add(newSahreMock("PG"));
		shares.add(newSahreMock("KO"));
		shares.add(newSahreMock("VZ"));
		
		
	}
	private Share newSahreMock(final String code) {
		final Share share = Mockito.mock(Share.class);
		Mockito.when(share.code()).thenReturn(code);
		return share;
	}
	
	@Test
	public  void rates() {
		final List<TimeCourse> results = (List<TimeCourse>)  realTimeRateRestRepository.rates(shares);
		results.forEach(tc -> {
			System.out.println(tc.code() + ":"+  tc.rates().get(0).value() + ":" +tc.rates().get(1).value() + ":"+ tc.totalRate());
			
		});
	}

}
