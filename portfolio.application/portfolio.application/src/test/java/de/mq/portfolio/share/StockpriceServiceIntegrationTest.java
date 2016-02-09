package de.mq.portfolio.share;

import java.util.Collection;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/application-test.xml" })
public class StockpriceServiceIntegrationTest {
	
	@Autowired
	StockpriceServiceImpl servive ; 
	@Autowired
	MongoOperations mongoOperations;
	
	@Test
	
	public final void stockPrice() {
		Share share = new ShareImpl("ADS.DE");
		final TimeCourse timeCourse = servive.history(share);
		
	
		mongoOperations.save(timeCourse);
	}

@Test
	
	public final void read() {
		Collection<TimeCourse> results = mongoOperations.findAll(TimeCourse.class, "TimeCourse");
	}
}
