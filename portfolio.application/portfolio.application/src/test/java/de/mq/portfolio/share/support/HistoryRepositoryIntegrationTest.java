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
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mq.portfolio.share.Data;
import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.Share.StockExchange;
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


	private final Share share = Mockito.mock(Share.class);
	
	final DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
	
	@Before
	public final void setup() {
		Mockito.when(share.code()).thenReturn("SAP.DE");
		Mockito.when(share.index()).thenReturn("Deutscher Aktien Index"); 
		
		//Mockito.when(share.code()).thenReturn("KO");
		//Mockito.when(share.index()).thenReturn("Dow Jones");
	}
	
	
	
	
	@Test
	public  void historySap() throws ParseException {
		
		int max = 4;
		@SuppressWarnings("unchecked")
		final List<Data>[]  results = new  List[max];
				
				
		results[0] = historyGoogleRestRepository.history(share).rates();
		
		Mockito.when(share.stockExchange()).thenReturn(StockExchange.FRA); 
		
		results[1] = historyGoogleRestRepository.history(share).rates();
		
		Mockito.when(share.index()).thenReturn("dow"); 
		Mockito.when(share.stockExchange()).thenReturn(null); 
		results[2] = historyGoogleRestRepository.history(share).rates();
		
		Mockito.when(share.index()).thenReturn(null); 
		
		
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
			    //System.out.println(df.format(date) +";" +  values[0] +";" +values[1] +";" +values[2] +";" +values[3]);
			    
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

}
