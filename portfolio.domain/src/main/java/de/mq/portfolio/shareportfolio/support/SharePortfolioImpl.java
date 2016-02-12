package de.mq.portfolio.shareportfolio.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.Assert;

import de.mq.portfolio.share.TimeCourse;

@Document(collection="Portfolio")
class SharePortfolioImpl implements SharePortfolio {
	@Id
	private String id; 
	
	@Indexed(unique=true)
	private final String name;
	
	@Reference
	private List<TimeCourse>  timeCourses = new ArrayList<>();
	
	private double[] variances;
	
	private double[][] covariances;
	
	private double[][] correlation;


	SharePortfolioImpl(final String name, final List<TimeCourse>  timeCourses) {
		this.name=name;
		this.timeCourses.addAll(timeCourses);
	}
	

	/* (non-Javadoc)
	 * @see de.mq.portfolio.shareportfolio.support.SharePortfolio#timeCourses()
	 */
	@Override
	public List<TimeCourse> timeCourses() {
		return Collections.unmodifiableList(timeCourses);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.shareportfolio.support.SharePortfolio#variances()
	 */
	@Override
   public double[]  variances() {
   	Assert.notNull(variances, "Variances not calculated");
   	return variances;
   }
	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.shareportfolio.support.SharePortfolio#getCovariances()
	 */
	@Override
	public double[][] getCovariances() {
		Assert.notNull(covariances, "Covariances not calculated");
		return covariances;
	}

	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.shareportfolio.support.SharePortfolio#getCorrelation()
	 */
	@Override
	public double[][] getCorrelation() {
		return correlation;
	}
   
   void onBeforeSave() {
   	variances=toVarianceArray(timeCourses);
   	covariances=toMatrix(timeCourses, (timeCourses, i,j) -> timeCourses.get(i).covariance( timeCourses.get(j)));
   	correlation=toMatrix(timeCourses,  (timeCourses, i,j) -> timeCourses.get(i).covariance( timeCourses.get(j)) / (Math.sqrt(variances[i] )*Math.sqrt(variances[j]) ));
   }


	private double[][] toMatrix(final List<TimeCourse> timeCourses, final MatixFunction function) {
		double[][] results =new double[timeCourses.size()][timeCourses.size()];
   	IntStream.range(0,timeCourses.size()).forEach(i -> IntStream.range(0, timeCourses.size()).forEach(j -> results[i][j]=function.f(timeCourses, i,j)));
   	return results;
	}


	private double[] toVarianceArray(final Collection<TimeCourse> timeCourses) {
		double[] results=new double[timeCourses.size()];
   	
   	IntStream.range(0, timeCourses.size()).forEach(i -> results[i]=timeCourses().get(i).variance());
   	return results;
	}
   
   
}

interface MatixFunction {
	double f(final List<TimeCourse> timeCourses, final int i , final int j );
}
