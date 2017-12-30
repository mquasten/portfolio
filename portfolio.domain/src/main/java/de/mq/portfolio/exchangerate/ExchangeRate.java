package de.mq.portfolio.exchangerate;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.share.Data;

public interface ExchangeRate {

	String source();

	String target();

	List<Data> rates();

	void assign(final Collection<Data> rates);

	Collection<Entry<Gateway, Date>> updates();

}
