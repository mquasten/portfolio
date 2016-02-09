package de.mq.portfolio.share;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mq.portfolio.share.ShareImpl;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/mongo-test.xml" })
public class ImportSharesIntegrationTest {
	
	private static final String FILE = "stocks.csv";
	private static final String REGEX = "[;]";
	@Autowired
	MongoOperations mongoOperations;

	@Test
	public final void importShares() throws IOException {
	
		mongoOperations.dropCollection(ShareImpl.class);
		try (final InputStream in =  this.getClass().getClassLoader().getResourceAsStream(FILE);final InputStreamReader isr = new InputStreamReader(in, Charset.forName("UTF-8")); final BufferedReader br = new BufferedReader(isr)) {;
	    doIMport(br);
		}
	   
	}

	private void doIMport(final BufferedReader br) throws IOException {
	  String line;
		while(( line = br.readLine()) != null) {
			System.out.println(line);
	   	 final String[] cols = line.split(REGEX);
	   	 mongoOperations.save((cols.length==3) ?  new ShareImpl(cols[0], cols[1], cols[2]) : new ShareImpl(cols[0], cols[1]));
		}
	}
	
}
