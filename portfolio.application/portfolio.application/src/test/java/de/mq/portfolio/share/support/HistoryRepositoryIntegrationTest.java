package de.mq.portfolio.share.support;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mq.portfolio.share.Data;
import de.mq.portfolio.share.Share;
import junit.framework.Assert;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application-test.xml" })
@Ignore
//@ActiveProfiles({"yahooHistoryRepository", "googleHistoryRepository"})
public class HistoryRepositoryIntegrationTest {
	
	
	
	@Autowired
	@Qualifier("googleHistoryRepository")
	private  HistoryRepository   historyGoogleRestRepository; 
	
	

	@Autowired
	@Qualifier("yahooHistoryRepository")
	private  HistoryRepository   historyYahooRestRepository; 

	@Value("#{stocks}")
	private Map<String,String> stocks;

	private final Share share = Mockito.mock(Share.class);
	
	final DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
	
	
	
	
	
	
	@Test
	public  void historySap() throws ParseException {
		
		int max = 4;
		@SuppressWarnings("unchecked")
		final List<Data>[]  results = new  List[max];
		Mockito.when(share.code2()).thenReturn("ETR:SAP"); 		
				
		results[0] = historyGoogleRestRepository.history(share).rates();
		
		Mockito.when(share.code2()).thenReturn("FRA:SAP"); 
		
		results[1] = historyGoogleRestRepository.history(share).rates();
		
		Mockito.when(share.code2()).thenReturn("NYSE:SAP");
		
		results[2] = historyGoogleRestRepository.history(share).rates();
		
		Mockito.when(share.index()).thenReturn(null); 
		
		Mockito.when(share.code()).thenReturn("SAP.DE"); 
		results[3] = historyYahooRestRepository.history(share).rates();
		
		final Map<Date, Double[]> prices = new HashMap<>();
		IntStream.range(0, max).forEach(i -> {
			results[i].forEach(data -> {
				prices.put(data.date(), new Double[max]);
			});
			
		});
		
		
		IntStream.range(0, max).forEach(i ->  {
			results[i].forEach(data -> prices.get(data.date())[i]=data.value());
			
		});
		
		final List<Date> dates = new ArrayList<>(prices.keySet());
		Collections.sort(dates, (d1, d2) -> (int) Math.round(d1.getTime() - d2.getTime()));
	
		int missing[] = new int[] {0};
		dates.forEach(date -> {
			    final Double values [] = prices.get(date);
			    System.out.println(df.format(date) +";" +  values[0] +";" +values[1] +";" +values[2] +";" +values[3]);
			    
			   if(  Arrays.asList(values).stream().filter(value -> value == null).count() > 0 ) {
				   missing[0]++;
			   }
			    
			    if (( values[0] != null) && (values[3]!=null))  {
			    
			    	Assert.assertEquals(values[3], values[0]);
			    } 
			    
			    if (( values[3] != null) && (values[1]!=null))  {
			    	Assert.assertTrue(100*  Math.abs(values[1] - values[3]) / values[3] < 2d);
			    }
			    
			    if (( values[3] != null) && (values[2]!=null))  {
			    	double error = 100*  Math.abs(values[2] - values[3]) / values[3];
			    	
			    	
			    	Assert.assertTrue(error > 2.5d && error < 20);
			    }
			    
				
		});
		
		Assert.assertTrue( missing[0] < 20 );
		
	}
	

	
	@Test
	public  void historyKO()   {
	
		Mockito.when(share.code()).thenReturn("KO");
		Mockito.when(share.index()).thenReturn("Dow Jones"); 
		int max = 2;
		@SuppressWarnings("unchecked")
		final List<Data>[]  results = new  List[max];
				
		Mockito.when(share.code2()).thenReturn("NYSE:KO");		
		results[0] = historyGoogleRestRepository.history(share).rates();
		results[1] = historyYahooRestRepository.history(share).rates();
		
		final Map<Date, Double[]> prices = new HashMap<>();
		IntStream.range(0, max).forEach(i -> {
			results[i].forEach(data -> {
				prices.put(data.date(), new Double[max]);
			});
			
		});
		
		
		IntStream.range(0, max).forEach(i ->  {
			results[i].forEach(data -> prices.get(data.date())[i]=data.value());
			
		});
		
		final List<Date> dates = new ArrayList<>(prices.keySet());
		Collections.sort(dates, (d1, d2) -> (int) Math.round(d1.getTime() - d2.getTime()));
		
		final int missing[] = new int[] {0};
		dates.forEach(date -> {
			    final Double values [] = prices.get(date);
			    System.out.println(df.format(date) +";" +  values[0] +";" +values[1] );
			    if(  Arrays.asList(values).stream().filter(value -> value == null).count() > 0 ) {
					   missing[0]++;
				}
			    
			    if( values[0] != null &&  values[1] != null  ) {
			    	Assert.assertEquals( Math.round(100 * values[1]) , Math.round(100 * values[0]));
			    }
			  
			    
				
		});
		
		Assert.assertTrue( missing[0] == 0 );
	}
	
	@Test
	public final void allshares() {
		
		stocks.entrySet().forEach(entry -> {
			final Map<Date,double[]> results = new HashMap<>();
			Mockito.when(share.code2()).thenReturn(entry.getValue());
			Mockito.when(share.code()).thenReturn(entry.getKey());
			historyGoogleRestRepository.history(share).rates().forEach(rate -> addResult(results, rate,0));
			
			historyYahooRestRepository.history(share).rates().forEach(rate -> addResult(results, rate, 1));
			Assert.assertTrue(results.size() > 200);
			final Set<double[]> resultsWithBoth = results.values().stream().filter(values -> values.length == 2 ).collect(Collectors.toSet());
			Assert.assertTrue(results.size() - resultsWithBoth.size() == 0);
			
			resultsWithBoth.forEach(values -> Assert.assertTrue(Math.abs(Math.round(100d*values[0]) - Math.round(100d*values[1])) <= 1d ));
		});
	}



	private void addResult(final Map<Date,double[]> results, final Data rate, final int index) {
		if( ! results.containsKey(rate.date())) {
			results.put(rate.date(), new double[2]);
		}
		results.get(rate.date())[index]=rate.value();
	}
	
	

}
