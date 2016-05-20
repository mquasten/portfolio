package de.mq.portfolio.exchangerate;

import java.util.Collection;

import de.mq.portfolio.share.Data;

public interface ExchangeRate {

	String source();

	String target();

	String link();

	Collection<Data> rates();

	void assign(final Collection<Data> rates);

}
