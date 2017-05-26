package de.mq.portfolio.exchangerate.support;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestOperations;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application.xml" })
@ActiveProfiles("yahoo")
public class TestIt {
	
	
	@Autowired
	private RestOperations restOperations;
	
	@Test
	public final void test() {
		System.out.println(restOperations);
		
		
		String response   = restOperations.getForObject("http://www.ariva.de/quote/historic/historic.csv?secu=400&boerse_id=21&clean_split=1&clean_payout=0&clean_bezug=1&min_time=26.5.2016&max_time=26.5.2017&trenner=|&go=Download", String.class);
		
		
		System.out.println(response);
	
		
		
		
	}
}
