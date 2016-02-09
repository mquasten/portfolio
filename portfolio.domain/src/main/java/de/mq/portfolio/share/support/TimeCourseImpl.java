package de.mq.portfolio.share.support;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.data.annotation.Reference;
import org.springframework.data.mongodb.core.mapping.Document;

import de.mq.portfolio.share.Data;
import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;

@Document(collection="TimeCourse")
class TimeCourseImpl implements TimeCourse {
	
	private Share share; 

	@Reference()
	private final List<Data> rates = new ArrayList<>();
	@Reference()
	private final List<Data> dividends = new ArrayList<>();
	
	private double meanRate;
	private double variance;
	
   TimeCourseImpl(final  Share share, final Collection<Data> rates, final Collection<Data> dividends) {
		this.share=share;
		this.rates.addAll(rates);
		this.dividends.addAll(dividends);
	}

   @SuppressWarnings("unused")
	private TimeCourseImpl() {
   	
   }
   
   
   
	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.share.support.TimeCourse#share()
	 */
	@Override
	public Share share() {
		return this.share;
	}
	
	
	void onBeforeSave() {
		final Data[] samples = rates.toArray(new Data[rates.size()]);
		double n = rates.size()-1;
		meanRate=  sum(samples, (v, i) -> rateOfReturn(v, i)) /n;
		variance=sum(samples , (v,i) -> Math.pow(rateOfReturn(v, i) - meanRate, 2)) / n; 
	}

	private double rateOfReturn(final Data[] v, final int i) {
		return  (v[i-1].getValue() - v[i].getValue())/v[i-1].getValue();
	}
	
	private <T> double  sum(final Data[] samples, final SampleFunction function)  {
		return IntStream.range(1, samples.length).mapToDouble(i -> function.f(samples,i)).reduce((result, yi) ->  result +yi).orElse(0);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.share.support.TimeCourse#meanRate()
	 */
	@Override
	public double meanRate() {
		return meanRate;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.share.support.TimeCourse#variance()
	 */
	@Override
	public double variance() {
		return variance;
	}
}

@FunctionalInterface
interface SampleFunction {
	
	 double f(final Data[]  samples, int i);
	
}

