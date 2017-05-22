package de.mq.portfolio.share.support;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application.xml" })
@ActiveProfiles("google")
@Ignore
public class NewProviderIntegrationTest {
	
	@Autowired
	private  RestTemplate restOperations;
	
	//@Test
	public final void testit() {
		
	
	
		
		
		
		String result = restOperations.getForObject("https://stooq.com/q/d/l/?s=sap.de", String.class);
		
		System.out.println(result);
		
		
	}
	
	@Test
	public final void testit2() {
		
	
	
		DateFormat df = new SimpleDateFormat("dd MMM yyyy" , new Locale("pl")) ;
	
		
		String result = restOperations.getForObject("https://stooq.com/q/m/?s=ko.us", String.class);
		
	System.out.println(result);
		Document document = Jsoup.parse(result);
		
		
		final List<Element> tables = document.getAllElements().stream().filter(e -> e.nodeName().equals("table")&& e.className().equals("fth1")).collect(Collectors.toList());
		
		List<Element> results = tables.get(0).getElementsByTag("tr");
		
		
		System.out.println("*****");
	
		results.stream().filter(e -> e.getElementsByTag("td").size() > 3).forEach(e -> {
			System.out.println("*****");
		System.out.println(e);
			final List<String> values = e.getElementsByTag("td").stream().map(x -> x.text()).collect(Collectors.toList());
		
			
			String x = values.get(0); /*.split("[,]")[1].trim();*/
			try {
				System.out.println(df.parse(x));
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.println("*******************");
		});
		

	
		
		
	}

}
