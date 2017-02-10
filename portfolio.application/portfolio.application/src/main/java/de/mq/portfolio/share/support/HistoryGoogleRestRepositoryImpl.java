package de.mq.portfolio.share.support;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.springframework.web.client.RestOperations;

import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;

@Repository("historyGoogleRestRepository")
@Profile("googleHistoryRepository" )
class HistoryGoogleRestRepositoryImpl implements HistoryRepository {
	
	private  final String url = "http://www.google.com/finance/historical?q=%s&output=csv";
	private final RestOperations restOperations;
	
	enum Index {
		Dax("FRA"),Dow("NYSE");
		private final String stockExchange;
		
		Index(final String stockExchange) {
			this.stockExchange=stockExchange;
		}
		static Index from(final String text ){
			if( text.toLowerCase().startsWith("dow")){
				return Dow;
			};
			if( text.toLowerCase().startsWith("deutscher")){
				return Dax;
			};
			throw new IllegalArgumentException(String.format("Index not found for: '%s'", text));
			
		}
		
		final String defaultStockExchange() {
			return stockExchange;
		}
	}

	@Autowired
	HistoryGoogleRestRepositoryImpl(final RestOperations restOperations) {
		this.restOperations = restOperations;
	}

	@Override
	public TimeCourse history(final Share share) {
		Assert.notNull(share, "Share is mandatory.");
	
		Assert.notNull(share.code(), "ShareCode is mandatory");
		
		final Index index = Index.from(share.index());
		
		final String name = index.defaultStockExchange() + ":" + share.code().replaceFirst("[.].*$", "");
		System.out.println(name);
		final String result =restOperations.getForObject(String.format(url, name), String.class);
		
		System.out.println(result);
		return null;
	} 

}
