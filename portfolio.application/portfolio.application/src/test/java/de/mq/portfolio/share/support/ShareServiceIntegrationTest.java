package de.mq.portfolio.share.support;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mq.portfolio.share.ShareService;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application-test.xml" })
public class ShareServiceIntegrationTest {
	
	@Autowired
	private  ShareService shareService;
	
	@Test
	
	public final void stockPrice() {
		 shareService.importTimeCourses();
	}


}
