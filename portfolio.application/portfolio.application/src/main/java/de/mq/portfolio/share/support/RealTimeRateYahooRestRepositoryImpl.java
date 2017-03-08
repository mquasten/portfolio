package de.mq.portfolio.share.support;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestOperations;

import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;

@Repository
@Profile("yahhoo")
public class RealTimeRateYahooRestRepositoryImpl implements RealTimeRateRestRepository {
	
	private final String url = "http://finance.yahoo.com/d/quotes.csv?s=%s&f=snbaopl1";	
	
	
	private final RestOperations restOperations;
	
	@Autowired
	RealTimeRateYahooRestRepositoryImpl(RestOperations restOperations) {
		this.restOperations = restOperations;
	}
	//restOperations.getForObject(requestUrl, String.class)
	

	/* (non-Javadoc)
	 * @see de.mq.portfolio.share.support.RealTimeRateRestRepository#rates(java.util.Collection)
	 */
	@Override
	public  Collection<TimeCourse> rates(final Collection<Share> shares) {
		
		
		final String codes = shares.stream().map(share -> share.code()).reduce("",   (a , b) ->  StringUtils.isEmpty(a ) ? b :  a+"+" +b  ) ;
		String.format(url, codes);
		
		final String response = restOperations.getForObject(String.format(url, codes), String.class);
		
		System.out.println(response);
		final Collection<TimeCourse> results = new ArrayList<>();
		return results;
		
	}
	
}
