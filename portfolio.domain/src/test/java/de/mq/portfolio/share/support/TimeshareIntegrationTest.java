package de.mq.portfolio.share.support;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mq.portfolio.share.TimeCourse;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/mongo.xml" })
public class TimeshareIntegrationTest {
	
	@Autowired
	private MongoOperations mongoOperations;
	
	@Test
	public final void covariance() {
		TimeCourse dax = mongoOperations.findOne(query("^GDAXI"), TimeCourseImpl.class);
		TimeCourse dow = mongoOperations.findOne(query("^DJI"), TimeCourseImpl.class);
		
		System.out.println(dax.correlation(dow));
		System.out.println(dow.correlation(dax));
		System.out.println(dax.correlation(dax));
		System.out.println(dow.correlation(dow));
	}

	private Query query(final String code) {
		return new Query(Criteria.where("share.code" ).is(code));
	}

}
