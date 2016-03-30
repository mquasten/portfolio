package de.mq.portfolio.shareportfolio.support;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.shareportfolio.SharePortfolio;
import junit.framework.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/mongo.xml" })
@Ignore
public class SharePortfolioIntegrationTest {
	
	@Autowired
	private MongoOperations mongoOperations;
	
	@Ignore
	@Test
	public final void persist() {
		
		final Query query = new Query(Criteria.where("share.code").in(Arrays.asList("ADS.DE","BAYN.DE","BMW.DE","SAP.DE","HEN3.DE","BA","PG","JNJ","KO","GS")));
		final List<TimeCourse> timeCourses = mongoOperations.find(query, TimeCourse.class, "TimeCourse");
		final SharePortfolio sharePortfolio = new SharePortfolioImpl("mq-test", timeCourses);
	
		mongoOperations.save(sharePortfolio);
	}
	
	@Test
	@Ignore
	public final void persist2() {
		
		final Query query = new Query(Criteria.where("share.code").in(Arrays.asList("ADS.DE",/*"BAYN.DE","BMW.DE",*/"SAP.DE","HEN3.DE",/*"BA"*/"PG","JNJ","KO"/*"GS"*/)));
		final List<TimeCourse> timeCourses = mongoOperations.find(query, TimeCourse.class, "TimeCourse");
		final SharePortfolio sharePortfolio = new SharePortfolioImpl("mq-test2", timeCourses);
	
		mongoOperations.save(sharePortfolio);
	}
	
	@Test
	@Ignore
	public final void persistMinRisk() {
		
		final Query query = new Query(Criteria.where("share.code").in(Arrays.asList("KO", "VZ", "JNJ", /*"TRV" , */ /*"MCD" */   "SAP.DE" /*, "DB1.DE" */)));
		final List<TimeCourse> timeCourses = mongoOperations.find(query, TimeCourse.class, "TimeCourse");
		final SharePortfolio sharePortfolio = new SharePortfolioImpl("mq-minRisk", timeCourses);
	
		mongoOperations.save(sharePortfolio);
	}
	
	
	@Test
	@Ignore
	public final void solve() {
		final Query query = new Query(Criteria.where("name").is("mq-minRisk"));
		final SharePortfolioImpl sharePortfolio =  mongoOperations.findOne(query, SharePortfolioImpl.class);
		final double[] vector = new double[sharePortfolio.timeCourses().size()];
		final double[] vector2 = new double[sharePortfolio.timeCourses().size()];
		final Map<TimeCourse, Double> results = sharePortfolio.min();
	
		sharePortfolio.timeCourses().forEach(tc -> System.out.println(tc.name() +"=" + results.get(tc)));
		
		IntStream.range(0, sharePortfolio.timeCourses().size()).forEach( i->{
		vector[i]= results.get(sharePortfolio.timeCourses().get(i)) ;
		vector2[i]=1d/sharePortfolio.timeCourses().size();
		}
		
		);
		
		System.out.println("Risiko=" + Math.sqrt(sharePortfolio.risk(vector)));
		System.out.println("Risiko(gleichverteilt)=" + Math.sqrt(sharePortfolio.risk(vector2)));
		Assert.assertEquals(Optional.of(1d), results.values().stream().reduce((a, b) -> a+b)); 
		
	}
}
