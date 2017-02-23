package de.mq.portfolio.support;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/stockcodes-test.xml" })
public class MigrateSareCsvFileTest {
	
	

	
	@Value("#{stocks}")
	private Map<String,String> stocks;

	@Test
	public final void createNewFile() throws IOException {
		try (final BufferedReader reader = new BufferedReader( new InputStreamReader( new FileInputStream("data/stocks.csv"), Charset.forName("UTF-8")))) {
		String line; 
		   while( (line =reader.readLine()) != null){
			   
			   Arrays.asList(line.split(";")).forEach(col -> {
				 
				   });
			   }
		   System.out.println();
		}
		
		
		}
	}
	

