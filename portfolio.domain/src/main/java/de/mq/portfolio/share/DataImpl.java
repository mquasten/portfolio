package de.mq.portfolio.share;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.data.annotation.Transient;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;



class DataImpl implements Data {
	
	@Transient
	private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	
	private String  date;
	

	/* (non-Javadoc)
	 * @see de.mq.portfolio.share.Data#date()
	 */
	@Override
	public Date date() {
		try {
			if( ! StringUtils.hasText(date)) {
				Assert.hasText(date, "Date is mandatory");
			}
			return df.parse(this.date);
		} catch (ParseException e) {
			 throw new IllegalArgumentException(String.format("Invalid Date %s", this.date));
		}
	}

	

	/* (non-Javadoc)
	 * @see de.mq.portfolio.share.Data#getValue()
	 */
	@Override
	public double getValue() {
		Assert.notNull(value, "Value is mandatory");
		return value;
	}

	
	private Double value;
	
	DataImpl(String date, Double value) {
		this.date = date;
		this.value = value;
	}
	
	@SuppressWarnings("unused")
	private DataImpl()  {
		
	}

}
