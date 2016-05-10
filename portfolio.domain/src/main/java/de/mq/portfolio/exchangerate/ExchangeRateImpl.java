package de.mq.portfolio.exchangerate;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.StringUtils;

@Document(collection="ExchangeRate")
public class ExchangeRateImpl  implements ExchangeRate {
	
	@Id
	private final String id;
	
	private final String source;
	
	private final String target;
	
	private final String link;
	
	public ExchangeRateImpl(final String source, final String target, final String link) {
		this.source = source;
		this.target = target;
		this.link = link;
		this.id= new UUID(source.hashCode(), target.hashCode()).toString();
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
	public final String link() {
		return link;
	}




	@Override
	public int hashCode() {
		if( !isValid(this) ){
			return super.hashCode();
		}
		
		return this.source.hashCode() + this.target.hashCode();
	}


	private boolean isValid(ExchangeRate exchangeRate) {
		if( ! StringUtils.hasText(exchangeRate.source()) ) {
			return false;
		}
		
		if( ! StringUtils.hasText(target)) {
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
		if( isValid(this) && isValid(other)) {
			return source.equals(other.source()) && target.equals(other.target());
		}
		return super.equals(obj);
	}


	@Override
	public String toString() {
		
		return "source=" + source + ", target=" + target;
	}
	
	
	
}
