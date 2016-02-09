package de.mq.portfolio.share;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.data.annotation.Reference;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="TimeCourse")
class TimeCourseImpl implements TimeCourse {
	
	private Share share; 

	@Reference()
	private final List<Data> rates = new ArrayList<>();
	@Reference()
	private final List<Data> dividends = new ArrayList<>();
	
   TimeCourseImpl(final  Share share, final Collection<Data> rates, final Collection<Data> dividends) {
		this.share=share;
		this.rates.addAll(rates);
		this.dividends.addAll(dividends);
	}

   TimeCourseImpl() {
   	
   }
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.share.TimeCourse#share()
	 */
	@Override
	public Share share() {
		return this.share;
	}
	
	
	
}
