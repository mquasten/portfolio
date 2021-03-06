package de.mq.portfolio.exchangerate.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.StringUtils;

import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.gateway.Gateway;
import de.mq.portfolio.share.Data;

@Document(collection = "ExchangeRate")
public class ExchangeRateImpl implements ExchangeRate {

	@Id
	private final String id;

	private final String source;

	private final String target;

	private final Map<Gateway, Date> lastUpdate = new HashMap<>();
	
	@Transient
	private final Gateway supportedGateway = Gateway.CentralBankExchangeRates;

	private final List<Data> rates = new ArrayList<>();

	@SuppressWarnings("unused")
	private ExchangeRateImpl() {
		this.source = null;
		this.target = null;
		this.id = null;
	}

	public ExchangeRateImpl(final String source, final String target) {
		this.source = source;
		this.target = target;
		this.id = new UUID(source.hashCode(), target.hashCode()).toString();
	}

	
	
	public ExchangeRateImpl(final String source, final String target, final  List<Data> rates) {
		this(source, target);
		assign(rates);
	}

	@Override
	public final String source() {
		return source;
	}

	@Override
	public final String target() {
		return target;
	}

	@Override
	public int hashCode() {
		if (!isValid(this)) {
			return super.hashCode();
		}

		return this.source.hashCode() + this.target.hashCode();
	}

	private boolean isValid(ExchangeRate exchangeRate) {
		if (!StringUtils.hasText(exchangeRate.source())) {
			return false;
		}

		if (!StringUtils.hasText(target)) {
			return false;
		}

		return true;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ExchangeRate)) {
			return super.equals(obj);
		}

		final ExchangeRate other = (ExchangeRate) obj;
		if (isValid(this) && isValid(other)) {
			return source.equals(other.source()) && target.equals(other.target());
		}
		return super.equals(obj);
	}

	@Override
	public String toString() {

		return "source=" + source + ", target=" + target;
	}

	@Override
	public final List<Data> rates() {
		return Collections.unmodifiableList(rates);
	}

	@Override
	public final void assign(final Collection<Data> rates) {
		this.rates.clear();
		this.rates.addAll(rates);
		lastUpdate.clear();
		lastUpdate.put(supportedGateway, new Date());
	
	}
	
	@Override
	public Collection<Entry<Gateway,Date>> updates() {
		return Collections.unmodifiableCollection(lastUpdate.entrySet());
	}
	
	


}
