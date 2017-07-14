package de.mq.portfolio.share.support;

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
import de.mq.portfolio.share.TimeCourse;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/mongo-test.xml", "/application-test.xml" })
@Ignore
public class HistoryGoogleRestRepositoryIntegrationTest {
	
	
	@Autowired
	@Qualifier("googleHistoryRepository")
	private HistoryRepository historyRestRepository;
	
	private Share share = Mockito.mock(Share.class);
	
	
	@Test
	public final void history() {
		
		Mockito.when(share.code()).thenReturn("KO");
		
		final TimeCourse result = historyRestRepository.history(share);
		
		Assert.assertTrue(result.rates().size() > 250 );;
	}

}
