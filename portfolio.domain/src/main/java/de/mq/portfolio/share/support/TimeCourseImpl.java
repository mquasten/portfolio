package de.mq.portfolio.share.support;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import de.mq.portfolio.share.Data;
import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;

@Document(collection="TimeCourse")
class TimeCourseImpl implements TimeCourse {
	@Id
	private String id; 
	
	private Share share; 
	
	@Indexed(unique=true)
	private String code;

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
	 * @see de.mq.portfolio.share.support.support.TimeCourse#share()
	 */
	@Override
	public Share share() {
		return this.share;
	}
	
	
	void onBeforeSave() {
		code=share.code();
		final Data[] samples = toArray(rates);
		double n = rates.size()-1;
		
	
		meanRate=  sum(samples, (v, i) -> rateOfReturn(v, i)) /n;
		variance=sum(samples , (v,i) -> Math.pow(rateOfReturn(v, i) - meanRate, 2)) / n; 
	}

	private Data[] toArray(final Collection<Data> col) {
		return col.toArray(new Data[col.size()]);
	}

	private double rateOfReturn(final Data[] v, final int i) {
		return  (v[i].value() - v[i-1].value())/v[i-1].value();
	}
	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.share.support.TimeCourse#covariance(de.mq.portfolio.share.support.TimeCourse)
	 */
	@Override
	public final double covariance(final TimeCourse other) {
		
		final Data[] samples = toArray(rates);
		final Data[] otherSamples = toArray(other.rates());
		final Map<Date,Double> rateOfReturnDelta = new HashMap<>();
		IntStream.range(1, otherSamples.length).forEach(i -> rateOfReturnDelta.put(otherSamples[i].date(), rateOfReturn(otherSamples,i) - other.meanRate()));
		final Collection<Data> inBoth = IntStream.range(0, samples.length).filter(i -> rateOfReturnDelta.containsKey(samples[i].date())|| i == 0 ).mapToObj(i -> samples[i]).collect(Collectors.toList());
		final Data[] sampleVector = toArray(inBoth);
	
		return   sum(sampleVector, (v,i) -> ( rateOfReturn(v, i) - meanRate ) *  rateOfReturnDelta.get(sampleVector[i].date()))  / (sampleVector.length-1);
	
	
	}
	
	
	
	private <T> double  sum(final Data[] samples, final SampleFunction function)  {
		return IntStream.range(1, samples.length).mapToDouble(i -> function.f(samples,i)).reduce((result, yi) ->  result +yi).orElse(0);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.share.support.support.TimeCourse#meanRate()
	 */
	@Override
	public double meanRate() {
		return meanRate;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.share.support.support.TimeCourse#variance()
	 */
	@Override
	public double variance() {
		return variance;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.share.support.TimeCourse#rates()
	 */
	@Override
	public List<Data> rates() {
		return Collections.unmodifiableList(this.rates);
	}

	@Override
	public List<Data> dividends() {
		return Collections.unmodifiableList(this.dividends);
	}
	
	
	@Override
	public final double correlation(final TimeCourse other) {
		return covariance(other) / ( Math.sqrt(variance)* Math.sqrt(other.variance()));
	}
	
}

@FunctionalInterface
interface SampleFunction {
	
	 double f(final Data[]  samples, int i);
	
}



