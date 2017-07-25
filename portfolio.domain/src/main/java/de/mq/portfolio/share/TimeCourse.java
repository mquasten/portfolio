package de.mq.portfolio.share;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import de.mq.portfolio.IdentifierAware;
import de.mq.portfolio.gateway.Gateway;



public interface TimeCourse  extends IdentifierAware<String>{


	public abstract Share share();

	double meanRate();

	double variance();

	double covariance(final TimeCourse other);

	List<Data> rates();

	List<Data> dividends();

	double correlation(final TimeCourse other);

	double standardDeviation();

	double totalRate();

	double totalRateDividends();


	String name();
	
	String code();
	

	Date start();

	Date end();

	String wkn();

	void assign(final TimeCourse timeCourse);

	void assign(final TimeCourse timeCourse, final boolean overwriteEmptyRatesAndDividends);

	void assign(final Collection<Gateway> gateways);

	Collection<Entry<Gateway, Date>> updates();






	

}
