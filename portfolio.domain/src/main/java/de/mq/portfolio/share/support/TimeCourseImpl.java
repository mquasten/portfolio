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
import org.springframework.util.Assert;

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
	
	private double totalRate;
	
	private double totalRateDividends;


	private double variance;

	private double standardDeviation;
	
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
		
	
		meanRate=  sum(samples, 1, (v, i) -> rateOfReturn(v, i)) /n;
		variance=sum(samples ,1, (v,i) -> Math.pow(rateOfReturn(v, i) - meanRate, 2)) / n; 
		if( rates.size() > 1){
			totalRate = rateOfReturn(rates.get(rates.size()-1).value(), rates.get(0).value(), rates.get(0).value());
		}
		totalRateDividends=calculateDividends();
		
		standardDeviation=Math.sqrt(variance);
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
	
		return   sum(sampleVector, 1, (v,i) -> ( rateOfReturn(v, i) - meanRate ) *  rateOfReturnDelta.get(sampleVector[i].date()))  / (sampleVector.length-1);
	
	
	}
	
	
	
	private <T> double  sum(final Data[] samples, final int startIndex, final SampleFunction function )  {
		return IntStream.range(startIndex, samples.length).mapToDouble(i -> function.f(samples,i)).reduce((result, yi) ->  result +yi).orElse(0);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.share.support.support.TimeCourse#meanRate()
	 */
	@Override
	public  double meanRate() {
		return meanRate;
	}
	
	@Override
	public final double totalRate() {
		return totalRate;
	}
	
	@Override
	public final double totalRateDividends() {
		return totalRateDividends;
	}

	private double calculateDividends() {
		if (dividends.size()==0){
			return 0;
		}
		
		if( rates.size()==0) {
			return 0;
		}
	
		return rateOfReturn(sum(toArray(dividends), 0, (v,i) -> v[i].value()), 0, rates.get(0).value());
	}

	private double rateOfReturn(final double falsch, final double richtig, final double wahr) {
		return (falsch - richtig)/wahr;
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

	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.share.TimeCourse#dividends()
	 */
	@Override
	public List<Data> dividends() {
		return Collections.unmodifiableList(this.dividends);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.share.TimeCourse#correlation(de.mq.portfolio.share.TimeCourse)
	 */
	@Override
	public final double correlation(final TimeCourse other) {
		return covariance(other) / ( Math.sqrt(variance)* Math.sqrt(other.variance()));
	}
	
	@Override
 	public final double standardDeviation() {
		return standardDeviation;
 	}

	
	@Override
	public String name() {
		return share != null ? share.name(): null;
	}
	
	@Override
	public String code() {
		return  share != null ? share.code(): null;
	}
	
	@Override
	public String wkn() {
		return  share != null ? share.wkn(): null;
	}
	
	@Override
	public final void assign(final TimeCourse timeCourse) {
		if( timeCourse.rates().size() > 0) {
			rates.clear();
			rates.addAll(timeCourse.rates());
		}
		if( timeCourse.dividends().size() > 0) {
			dividends.clear();
			dividends.addAll(timeCourse.dividends());
		}
		if( timeCourse.share() != null) {
			this.share=timeCourse.share();
		}
	}
	
	@Override
	public  void assign(final TimeCourse timeCourse, final boolean overwriteEmptyRatesAndDividends) {
			if( ! overwriteEmptyRatesAndDividends) {
				assign(timeCourse);
				return;
			}
		
			Assert.notNull(timeCourse, "TimeCourse is mandatory.");
		 	Assert.notNull(timeCourse.share(), "Share is mandatory.");
			
		 	this.share=timeCourse.share();
			this.rates.clear();
		 	this.rates.addAll(timeCourse.rates());
		 	this.dividends.clear();
			this.dividends.addAll(timeCourse.dividends());
		}
	
	
	@Override
	public Date start() {
		if( rates==null){
			return null;
		}
		if( rates.isEmpty()){
			return null;
		}
		return rates.get(0).date();
	}
	
	@Override
	public Date end() {
		if( rates==null){
			return null;
		}
		if( rates.isEmpty()){
			return null;
		}
		return rates.get(rates.size()-1).date();
	
}

/**
 * Austauschbarer Algorithmus auf einer zeitvarianten REihe
 * @author Admin
 *
 */
@FunctionalInterface
interface SampleFunction {
	
	/**
	 * Berechnung auf einem SampleVector.
	 * @param samples Array mit Daten beschreibt eine zeitvariante Reihe
	 * @param i der Index des aktuellen Wertes in der Reihen 
	 * @return das Ergebnis der Berechnung
	 */
	 double f(final Data[]  samples, int i);
	
}



}

