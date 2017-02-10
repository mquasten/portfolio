package de.mq.portfolio.share.support;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mq.portfolio.share.Share;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application.xml" })
@ActiveProfiles({"googleHistoryRepository"})
@Ignore
public class HistoryRepositoryIntegrationTest {
	
	@Autowired
	private  HistoryRepository historyGoogleRestRepository; 
	
	private final Share share = Mockito.mock(Share.class);
	
	@Before
	public final void setup() {
		Mockito.when(share.code()).thenReturn("SAP.DE");
		Mockito.when(share.index()).thenReturn("Deutscher Aktien Index");
	}
	
	
	@Test
	public  void history() {
		System.out.println(historyGoogleRestRepository);
		
		historyGoogleRestRepository.history(share);
	}

}
