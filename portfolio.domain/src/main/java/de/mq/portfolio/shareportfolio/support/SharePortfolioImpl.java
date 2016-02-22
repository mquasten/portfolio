package de.mq.portfolio.shareportfolio.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.Assert;

import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.shareportfolio.PortfolioOptimisation;
import de.mq.portfolio.shareportfolio.SharePortfolio;

@Document(collection = "Portfolio")
class SharePortfolioImpl implements SharePortfolio {
	@Id
	private String id;

	@Indexed(unique = true)
	private final String name;

	private PortfolioOptimisation minVariance;

	@Reference
	private List<TimeCourse> timeCourses = new ArrayList<>();

	private double[] variances;

	private double[][] covariances;

	private double[][] correlations;

	private boolean committed;
	
	@SuppressWarnings("unused")
	private SharePortfolioImpl() {
		name=null;
	}

	SharePortfolioImpl(final String name, final List<TimeCourse> timeCourses) {
		this.name = name;
		this.timeCourses.addAll(timeCourses);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.portfolio.shareportfolio.support.SharePortfolio#timeCourses()
	 */
	@Override
	public List<TimeCourse> timeCourses() {
		return Collections.unmodifiableList(timeCourses);
	}

	double[] variances() {
		variancesExistsGuard();
		return variances;
	}

	void variancesExistsGuard() {
		Assert.notNull(variances, "Variances not calculated");
	}

	double[][] covariances() {
		covariancesExistsGuard();
		return covariances;
	}

	private void covariancesExistsGuard() {
		Assert.notNull(covariances, "Covariances not calculated");
	}

	double[][] correlations() {
		return correlations;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.portfolio.shareportfolio.SharePortfolio#name()
	 */
	@Override
	public String name() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.portfolio.shareportfolio.SharePortfolio#risk(double[])
	 */
	@Override
	public final double risk(final double[] weightingVector) {
		variancesExistsGuard();
		covariancesExistsGuard();
		weightingVectorExistsGuard(weightingVector);
		Assert.isTrue(variances.length == covariances.length, "Variances and covariances Vector should have the same size.");
		Assert.isTrue(weightingVector.length == variances.length, "Variances and weighting Vector should have the same size.");
		final double[] sum = { IntStream.range(0, variances.length).mapToDouble(i -> Math.pow(weightingVector[i], 2) * variances[i]).reduce((result, yi) -> result + yi).orElse(0) };
		IntStream.range(0, variances.length).forEach(i -> IntStream.range(i + 1, variances.length).forEach(j -> sum[0] += 2 * weightingVector[i] * weightingVector[j] * covariances[i][j]));
		return sum[0];

	}

	private void weightingVectorExistsGuard(final double[] weightingVector) {
		Assert.notNull(weightingVector, "WeightingVector should be given");
	}

	boolean onBeforeSave() {
		if (timeCourses.isEmpty()) {
			return false;
		}
		variances = toVarianceArray(timeCourses);
		covariances = toMatrix(timeCourses, (timeCourses, i, j) -> timeCourses.get(i).covariance(timeCourses.get(j)));
		correlations = toMatrix(timeCourses, (timeCourses, i, j) -> timeCourses.get(i).covariance(timeCourses.get(j)) / (Math.sqrt(variances[i]) * Math.sqrt(variances[j])));
		return true;
	}

	private double[][] toMatrix(final List<TimeCourse> timeCourses, final MatixFunction function) {
		double[][] results = new double[timeCourses.size()][timeCourses.size()];
		IntStream.range(0, timeCourses.size()).forEach(i -> IntStream.range(0, timeCourses.size()).forEach(j -> results[i][j] = function.f(timeCourses, i, j)));
		return results;
	}

	private double[] toVarianceArray(final Collection<TimeCourse> timeCourses) {
		double[] results = new double[timeCourses.size()];

		IntStream.range(0, timeCourses.size()).forEach(i -> results[i] = timeCourses().get(i).variance());
		return results;
	}

	@Override
	public boolean isCommitted() {
		return committed;
	}

	@Override
	public final void commit() {
		Assert.isTrue(timeCourses.size() >= 2, "Portfolio should have at least 2 TimeCourse");
		this.committed = true;
	}

	@Override
	public Optional<PortfolioOptimisation> minVariance() {
		return Optional.ofNullable(minVariance);
	}

}

interface MatixFunction {
	double f(final List<TimeCourse> timeCourses, final int i, final int j);
}
