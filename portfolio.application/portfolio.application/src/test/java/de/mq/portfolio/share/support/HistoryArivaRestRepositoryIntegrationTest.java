package de.mq.portfolio.share.support;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mq.portfolio.share.Share;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application-test.xml" })

public class HistoryArivaRestRepositoryIntegrationTest {
	
	
	@Autowired
	@Qualifier("arivaHistoryRepository")
	private  HistoryRepository   historyRestRepository; 
	
	
	@Test
	public final void history() {
		Assert.assertNotNull(historyRestRepository);
		Share share = Mockito.mock(Share.class);
		historyRestRepository.history(share);
	}

}
