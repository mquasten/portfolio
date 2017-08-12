package de.mq.portfolio.share.support;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application-test.xml" ,  "/mongo-test.xml" })
@Ignore
public class RealTimeRateYahooRestRepositoryIntegrationTest {
	
	@Autowired
	@Qualifier("yahooRealtimeRepository")
	private  RealTimeRateRepository   realTimeRateRestRepository; 
	
	
	
	private final Collection<Share> shares = new ArrayList<>();
	
	@Before
	public  final void setup() {
		shares.clear();
		shares.add(newshareMock("SAP.DE"));
		shares.add(newshareMock("JNJ"));
		shares.add(newshareMock("PG"));
		shares.add(newshareMock("KO"));
		shares.add(newshareMock("VZ"));
		
		
	}
	private Share newshareMock(final String code) {
		final Share share = Mockito.mock(Share.class);
		Mockito.when(share.code()).thenReturn(code);
		return share;
	}
	
	@Test
	public  void rates() {
		final List<TimeCourse> results = (List<TimeCourse>)  realTimeRateRestRepository.rates(shares);
		Assert.assertEquals(shares.size(), results.size());
		results.forEach(tc -> {
			Assert.assertEquals(DataAccessUtils.requiredSingleResult(shares.stream().map(share -> share.code()).filter(code -> code.equals(tc.share().code())).collect(Collectors.toSet())),  tc.share().code());
			
			System.out.println(tc.code() + ":"+  tc.rates().get(0).value() + ":" +tc.rates().get(1).value() + ":"+ tc.totalRate());
			
		});
	}
	
	@Test
	public  void ratesIndex() {
		final List<TimeCourse> results = (List<TimeCourse>)  realTimeRateRestRepository.rates(Arrays.asList(newshareMock("^DJI")));
		Assert.assertTrue(results.isEmpty());
	}
	
	

}
