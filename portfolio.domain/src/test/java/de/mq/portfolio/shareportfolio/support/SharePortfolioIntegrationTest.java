package de.mq.portfolio.shareportfolio.support;

import java.util.Arrays;
import java.util.List;

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

public class SharePortfolioIntegrationTest {
	
	@Autowired
	private MongoOperations mongoOperations;
	
	@Test
	public final void persist() {
		
		final Query query = new Query(Criteria.where("share.code").in(Arrays.asList("ADS.DE","BAYN.DE","BMW.DE","SAP.DE","HEN3.DE","BA","PG","JNJ","KO","GS")));
		final List<TimeCourse> timeCourses = mongoOperations.find(query, TimeCourse.class, "TimeCourse");
		final SharePortfolio sharePortfolio = new SharePortfolioImpl("mq-test", timeCourses);
		((SharePortfolioImpl) sharePortfolio).onBeforeSave();
		mongoOperations.save(sharePortfolio);
	}
}
