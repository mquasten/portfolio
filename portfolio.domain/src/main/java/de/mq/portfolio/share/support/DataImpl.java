package de.mq.portfolio.share.support;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.data.annotation.Transient;
import org.springframework.util.Assert;
import de.mq.portfolio.share.Data;



public class DataImpl implements Data {
	
	static final String DATE_PATTERN = "yyyy-MM-dd";

	@Transient
	private final SimpleDateFormat df = new SimpleDateFormat(DATE_PATTERN);
	
	private String  date;

	/* (non-Javadoc)
	 * @see de.mq.portfolio.share.support.support.Data#date()
	 */
	@Override
	public Date date() {
		try {
			Assert.hasText(date);
			
			return df.parse(this.date);
		} catch (ParseException e) {
			 throw new IllegalArgumentException(String.format("Invalid Date %s", this.date));
		}
	}

	

	/* (non-Javadoc)
	 * @see de.mq.portfolio.share.support.support.Data#getValue()
	 */
	@Override
	public double value() {
		Assert.notNull(value, "Value is mandatory");
		return value;
	}

	
	private Double value;
	
	DataImpl(String date, Double value) {
		this.date = date;
		this.value = value;
	}
	
	public DataImpl(Date date, Double value) {
		this.date = df.format(date);
		this.value = value;
	}
	
	@SuppressWarnings("unused")
	private DataImpl()  {
		
	}
	
	
	

}
