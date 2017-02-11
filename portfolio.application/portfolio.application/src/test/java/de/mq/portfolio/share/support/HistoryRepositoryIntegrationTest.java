package de.mq.portfolio.share.support;

import java.text.ParseException;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mq.portfolio.share.Data;
import de.mq.portfolio.share.Share;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application.xml" })
//@ActiveProfiles({"googleHistoryRepository"})
@Ignore
@ActiveProfiles({"yahooHistoryRepository"})
public class HistoryRepositoryIntegrationTest {
	
	@Autowired
	private  HistoryRepository historyGoogleRestRepository; 
	
	private final Share share = Mockito.mock(Share.class);
	
	@Before
	public final void setup() {
		Mockito.when(share.code()).thenReturn("SAP.DE");
		Mockito.when(share.index()).thenReturn("Deutscher Aktien Index"); 
		
		//Mockito.when(share.code()).thenReturn("KO");
		//Mockito.when(share.index()).thenReturn("Dow Jones");
	}
	
	
	@Test
	public  void history() throws ParseException {
		
		final List<Data> results = historyGoogleRestRepository.history(share).rates();
		
		results.forEach(rate -> System.out.println(rate.date() + ":" + rate.value()));
		
		/*final String x = new SimpleDateFormat("dd-MMM-YY").format(new Date());
		
		Date date = new SimpleDateFormat("d-MMM-yy", Locale.US).parse("12-feb-16"); 
		
		System.out.println(date); */
	}

}
