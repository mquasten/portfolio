package de.mq.portfolio.share.support;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Before;
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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application-test.xml" })

public class HistoryRepositoryIntegrationTest {
	
	
	
	@Autowired
	@Qualifier("googleHistoryRepository")
	private  HistoryRepository   historyGoogleRestRepository; 
	
	

	@Autowired
	@Qualifier("arivaHistoryRepository")
	private  HistoryRepository   historyArivaRestRepository; 

	@Value("#{stocks}")
	private Map<String,String> stocks;
	
	@Value("#{arivaHistory}")
	private List<ShareGatewayParameterImpl> arivaHistory;
	
	private final Map <String,Map<String,String>> arivaParameter = new HashMap<>();
	
	@Value("#{wkns}")
	private Map<String,String> wkns;

	private final Share share = Mockito.mock(Share.class);
	
	final DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
	
	@Before
	public final void setup() {
		arivaParameter.putAll(arivaHistory.stream().collect(Collectors.toMap(history -> history.code(), history -> history.parameters())));
	}
	
	
	int counter =0; 
	
	private Map<String, String> parameterMap(final String shareId, final String stockExchangeId) {
		final Map<String,String>  parameter = new HashMap<>();
		parameter.put("shareId", shareId);
		parameter.put("stockExchangeId", stockExchangeId);
		return parameter;
	}
	
	@Test

	public  void historySap() throws ParseException {
		
		int max = 4;
		@SuppressWarnings("unchecked")
		final List<Data>[]  results = new  List[max];
		
		//Mockito.when(share.id2()).thenReturn("910");
		Mockito.doReturn(arivaParameter.get("SAP.DE")).when(share).gatewayParameter();
		Mockito.doReturn(wkns.get("SAP.DE")).when(share).wkn();
		
		Mockito.when(share.code2()).thenReturn("ETR:SAP"); 		
				
		results[0] = historyGoogleRestRepository.history(share).rates();
		
		Mockito.when(share.code2()).thenReturn("FRA:SAP"); 
		
		results[1] = historyGoogleRestRepository.history(share).rates();
		
		Mockito.when(share.code2()).thenReturn("NYSE:SAP");
		
		results[2] = historyGoogleRestRepository.history(share).rates();
		
		Mockito.when(share.index()).thenReturn(null); 
		
		Mockito.when(share.code()).thenReturn("SAP.DE"); 
		results[3] = historyArivaRestRepository.history(share).rates();
		
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
			   // System.out.println(df.format(date) +";" +  values[0] +";" +values[1] +";" +values[2] +";" +values[3]);
			    
			   if(  Arrays.asList(values).stream().filter(value -> value == null).count() > 0 ) {
				   missing[0]++;
			   }
			    
			    if (( values[0] != null) && (values[3]!=null))  {
			    
			    	if(  Math.abs(values[3] - values[0]) > 0d  ){
			    		System.out.println(date +":"  + values[3]+  "<=>" +values[0]);
			    	}
			    	
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
	//	Mockito.when(share.id2()).thenReturn("400");
		Mockito.doReturn(arivaParameter.get("KO")).when(share).gatewayParameter();
		Mockito.doReturn(wkns.get("KO")).when(share).wkn();
		results[0] = historyGoogleRestRepository.history(share).rates();
		results[1] = historyArivaRestRepository.history(share).rates();
		
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
		counter=0;
		final int missing[] = new int[] {0};
		dates.forEach(date -> {
			    final Double values [] = prices.get(date);
			   // System.out.println(df.format(date) +";" +  values[0] +";" +values[1] );
			    if(  Arrays.asList(values).stream().filter(value -> value == null).count() > 0 ) {
					   missing[0]++;
				}
			    
			    if( values[0] != null &&  values[1] != null  ) {
			    	Assert.assertEquals( Math.round(10 * values[1]) , Math.round(10 * values[0]));
			    	if( Math.abs(100 * values[1]   -  100 * values[0]) > 0 ) {
			    		counter++;
			    		System.out.println(date + ":"+ values[0] +"<=>" + values[1] );
			    	}
			    	//;
			    }
			  
			    
				
		});
		
		
		Assert.assertTrue(counter <= 1);
		Assert.assertTrue( missing[0] == 0 );
	}
	
	@Test
	public  void historyEONA()   {
	
		Mockito.when(share.code()).thenReturn("EOAN.DE");
		Mockito.when(share.code2()).thenReturn("ETR:EOAN");
		//Mockito.when(share.id2()).thenReturn("320");
		Mockito.doReturn(arivaParameter.get("EOAN.DE")).when(share).gatewayParameter();
		Mockito.when(share.wkn()).thenReturn("ENAG99");

		int max = 2;
		@SuppressWarnings("unchecked")
		final List<Data>[]  results = new  List[max];
				
		
		results[0] = historyGoogleRestRepository.history(share).rates();
		results[1] = historyArivaRestRepository.history(share).rates();
		
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
		final int counter[] =  new int[] {0};
		dates.stream().filter(date ->  ! date.before(new GregorianCalendar(2016, 8 , 12).getTime())).forEach(date -> {
			    final Double values [] = prices.get(date);
			   // System.out.println(df.format(date) +";" +  values[0] +";" +values[1] );
			    
			    
			    counter[0]++;
			    
			    if(  values[0] != null &&  values[1] != null  )  {
			    	Assert.assertTrue(Math.abs(Math.round(100d*values[0]) - Math.round(100d*values[1])) <= 1d );
			    	
			    } else {
			    	missing[0]++;
			    }
			  
			    
				
		});
		
		Assert.assertTrue(counter[0]> 190);
	  	Assert.assertTrue( missing[0] == 0  );
	  
	}
	
	
	@Test
	
	public  void historyDB11()   {
	
		Mockito.when(share.code()).thenReturn("DB1.DE");
		Mockito.when(share.code2()).thenReturn("ETR:DB1");
		//Mockito.when(share.id2()).thenReturn("4587");
		Mockito.doReturn(arivaParameter.get("DB1.DE")).when(share).gatewayParameter();
		Mockito.doReturn(wkns.get("DB1.DE")).when(share).wkn();
		
		int max = 2;
		@SuppressWarnings("unchecked")
		final List<Data>[]  results = new  List[max];
				
		
		results[0] = historyGoogleRestRepository.history(share).rates();
		results[1] = historyArivaRestRepository.history(share).rates().stream().filter(rate -> ! rate.date().before(new GregorianCalendar(2016, 8 , 5).getTime() )).collect(Collectors.toList());
		
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
		final int counter[] =  new int[] {0};
		dates.stream().filter(date ->  ! date.before(new GregorianCalendar(2016, 8 , 12).getTime())).forEach(date -> {
			    final Double values [] = prices.get(date);
			    //System.out.println(df.format(date) +";" +  values[0] +";" +values[1] );
			    
			    
			    counter[0]++;
			    
			    if(  values[0] != null &&  values[1] != null  )  {
			    	Assert.assertTrue(Math.abs(Math.round(100d*values[0]) - Math.round(100d*values[1])) <= 1d );
			    	
			    } else {
			    	missing[0]++;
			    }
			  
			    
				
		});
		
		
		
		
		Assert.assertTrue(counter[0]> 190);
	  	Assert.assertTrue( missing[0] == 0  );
	}
	
	@Test
	//@Ignore
	public final void allshares() {
		
	
		
		arivaHistory.stream().filter(history -> ! history.code().equals("DB1.DE")).forEach(history -> {
			
			
			System.out.println("***" + history.code() + "***");
			
		//	System.out.println(history.parameters());
			final String code2 = stocks.get(history.code());
			final Map<Date,double[]> results = new HashMap<>();
			
			final String id2 = history.parameters().get("shareId");
			Mockito.when(share.code2()).thenReturn(code2);
			//Mockito.when(share.id2()).thenReturn(id2);
			Mockito.when(share.code()).thenReturn(history.code());
			Mockito.when(share.wkn()).thenReturn(wkns.get(history.code()));
			
			Mockito.when(share.gatewayParameter()).thenReturn(history.parameters());
		
			historyGoogleRestRepository.history(share).rates().forEach(rate -> addResult(results, rate,0));
			historyArivaRestRepository.history(share).rates().forEach(rate -> addResult(results, rate, 1));
			
			Assert.assertTrue(results.size() > 250);
			
			
			final Set<double[]> resultsWithBoth = results.values().stream().filter(values -> values[0] !=  0d && values[1] !=  0d ).collect(Collectors.toSet());
			
			
			//System.out.println(results.size() + ":" + resultsWithBoth.size() );
			Assert.assertTrue(results.size() - resultsWithBoth.size() < 1);
			
			
			
			counter=0;
			resultsWithBoth.forEach(values ->  {
			 	
			 	
			 	//System.out.println(values[0] + ":" + values[1]);
			 	
				if( history.code().equals("GS") || history.code().equals("IBM")|| history.code().equals("JPM")|| history.code().equals("MSFT") || history.code().equals("PG")|| history.code().equals("V")|| history.code().equals("WMT")) {
					Assert.assertTrue( Math.abs(Math.round(100d*values[0]) - Math.round(100d*values[1]))  <=  200);
				} else {
					Assert.assertTrue( Math.abs(Math.round(100d*values[0]) - Math.round(100d*values[1]))  <= 13);
				}
				if ( Math.abs(Math.round(100d*values[0]) - Math.round(100d*values[1])) > 1d) {
					
					System.out.println(history.code() + ":" + values[0] + ":" + values[1]);
					counter++;
				}
				});
			
			Assert.assertTrue(counter <= 3);
			
		});
		/*stocks.entrySet().stream().filter(entry -> !entry.getKey().equals("EOAN.DE")&& !entry.getKey().equals("DB11.DE")).forEach(entry -> {
			final Map<Date,double[]> results = new HashMap<>();
			Mockito.when(share.code2()).thenReturn(entry.getValue());
			Mockito.when(share.code()).thenReturn(entry.getKey());
			historyGoogleRestRepository.history(share).rates().forEach(rate -> addResult(results, rate,0));
			
			historyArivaRestRepository.history(share).rates().forEach(rate -> addResult(results, rate, 1));
		
			
			Assert.assertTrue(results.size() > 250);
			
			
			final Set<double[]> resultsWithBoth = results.values().stream().filter(values -> values[0] !=  0d && values[1] !=  0d ).collect(Collectors.toSet());
			
			Assert.assertTrue(results.size() - resultsWithBoth.size() < 10);
			
			
			
			
			resultsWithBoth.forEach(values ->  {
			 	//System.out.println(values[0] + ":" + values[1]);
				Assert.assertTrue(Math.abs(Math.round(100d*values[0]) - Math.round(100d*values[1])) <= 1d );
				});
		}); */
	}



	private void addResult(final Map<Date,double[]> results, final Data rate, final int index) {
		if( ! results.containsKey(rate.date())) {
			results.put(rate.date(), new double[2]);
		}
		results.get(rate.date())[index]=rate.value();
	}
	
	

}
