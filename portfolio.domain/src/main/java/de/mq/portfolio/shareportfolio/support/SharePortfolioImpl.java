package de.mq.portfolio.shareportfolio.support;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.Assert;

import Jama.Matrix;
import de.mq.portfolio.exchangerate.ExchangeRate;
import de.mq.portfolio.exchangerate.ExchangeRateCalculator;
import de.mq.portfolio.exchangerate.support.ExchangeRateImpl;
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
	private final List<TimeCourse> timeCourses = new ArrayList<>();

	private double[] variances;

	private double[][] covariances;

	private double[][] correlations;

	private boolean committed;

	@SuppressWarnings("unused")
	private SharePortfolioImpl() {
		name = null;
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
		Assert.isTrue(variances.length == covariances.length,
				"Variances and covariances Vector should have the same size.");
		Assert.isTrue(weightingVector.length == variances.length,
				"Variances and weighting Vector should have the same size.");
		final double[] sum = {
				IntStream.range(0, variances.length).mapToDouble(i -> Math.pow(weightingVector[i], 2) * variances[i])
						.reduce((result, yi) -> result + yi).orElse(0) };
		IntStream.range(0, variances.length).forEach(i -> IntStream.range(i + 1, variances.length)
				.forEach(j -> sum[0] += 2 * weightingVector[i] * weightingVector[j] * covariances[i][j]));
		return sum[0];

	}

	@Override
	public final Double standardDeviation() {

		if (timeCourses.size() < 2) {
			return null;
		}

		return Math.sqrt(risk(minWeights()));
	}
	
	@Override
	public final Double standardDeviation(double[] weights) {

		if (timeCourses.size() < 2) {
			return null;
		}

		return Math.sqrt(risk(weights));
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
		correlations = toMatrix(timeCourses, (timeCourses, i, j) -> timeCourses.get(i).covariance(timeCourses.get(j))
				/ (Math.sqrt(variances[i]) * Math.sqrt(variances[j])));
		return true;
	}

	private double[][] toMatrix(final List<TimeCourse> timeCourses, final MatixFunction function) {
		double[][] results = new double[timeCourses.size()][timeCourses.size()];
		IntStream.range(0, timeCourses.size()).forEach(i -> IntStream.range(0, timeCourses.size())
				.forEach(j -> results[i][j] = function.f(timeCourses, i, j)));
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

	@Override
	public final String id() {
		return id;
	}

	@Override
	public Map<TimeCourse, Double> min() {
		final Map<TimeCourse, Double> weights = new HashMap<>();
		if (timeCourses.isEmpty()) {
			return Collections.unmodifiableMap(weights);
		}
		if (variances == null) {
			return Collections.unmodifiableMap(weights);
		}

		if (covariances == null) {
			return Collections.unmodifiableMap(weights);
		}
		variancesExistsGuard();
		covariancesExistsGuard();
		Assert.isTrue(covariances.length == timeCourses.size());
		Assert.isTrue(variances.length == timeCourses.size());
		final double[][] array = new double[timeCourses.size() + 1][timeCourses.size() + 1];
		IntStream.range(0, timeCourses.size()).forEach(i -> IntStream.range(0, timeCourses.size()).filter(j -> j != i)
				.forEach(j -> array[i][j] = covariances[i][j]));

		IntStream.range(0, timeCourses.size()).forEach(i -> {
			array[i][i] = variances[i];
			array[i][timeCourses.size()] = 1;
			array[timeCourses.size()][i] = 1;
		});
		array[timeCourses.size()][timeCourses.size()] = 0d;
		/* seien ein Vektor und eine Matrix ... */
		final Matrix matrix = new Matrix(array);
		final Matrix vectorAsMatrix = new Matrix(timeCourses.size() + 1, 1, 0d);
		vectorAsMatrix.set(timeCourses.size(), 0, 1d);
		final Matrix vector = vectorAsMatrix;
		// matrix.print(15, 10);

		// vector.print(15,10);
		final Matrix result = matrix.solve(vector);
		// result.print(15, 10);

		IntStream.range(0, timeCourses.size()).forEach(i -> weights.put(timeCourses.get(i), result.get(i, 0)));

		return Collections.unmodifiableMap(weights);

	}

	@Override
	public double[] minWeights() {

		final double[] weightingVector = new double[timeCourses.size()];
		Map<TimeCourse, Double> weights = min();

		IntStream.range(0, weightingVector.length).forEach(i -> weightingVector[i] = weights.get(timeCourses.get(i)));

		return weightingVector;
	}

	@Override
	public void assign(final TimeCourse timeCourse) {
		Assert.notNull(timeCourse);
		Assert.notNull(timeCourse.share());
		Assert.notNull(timeCourse.share().name());
		if (timeCourses.stream().map(tc -> tc.share().name()).filter(n -> n.equals(timeCourse.share().name())).findAny()
				.isPresent()) {
			return;
		}
		this.timeCourses.add(timeCourse);

	}

	@Override
	public void assign(final Collection<TimeCourse> timeCourses) {
		Assert.notNull(timeCourses);
		timeCourses.stream().map(tc -> tc.share()).forEach(share -> {
			Assert.notNull(share, "Timecourse should have a share");
			Assert.hasText(share.code(), "Code is mandatory");
		});
		this.timeCourses.clear();
		this.timeCourses.addAll(timeCourses);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.mq.portfolio.shareportfolio.SharePortfolio#remove(de.mq.portfolio.
	 * share.TimeCourse)
	 */
	@Override
	public void remove(final TimeCourse timeCourse) {
		this.timeCourses.removeAll(
				this.timeCourses.stream().filter(tc -> tc.id().equals(timeCourse.id())).collect(Collectors.toSet()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.portfolio.shareportfolio.SharePortfolio#correlationEntries()
	 */
	@Override
	public List<Entry<String, Map<String, Double>>> correlationEntries() {
		final List<Entry<String, Map<String, Double>>> results = new ArrayList<>();
		if (covariances == null) {
			return Collections.unmodifiableList(results);
		}
		if (timeCourses.size() != covariances.length) {
			return Collections.unmodifiableList(results);
		}
		IntStream.range(0, timeCourses.size()).forEach(line -> {
			final Map<String, Double> cols = new HashMap<>();
			IntStream.range(0, timeCourses.size()).forEach(col -> {
				cols.put(timeCourses.get(col).share().name(), correlations[line][col]);
			});
			results.add(new AbstractMap.SimpleImmutableEntry<>(timeCourses.get(line).share().name(), cols));
		});
		return Collections.unmodifiableList(results);
	}

	

	@Override
	public Double totalRate(final double[] weights, final ExchangeRateCalculator exchangeRateCalculator) {
		weightsVectorGuard(weights);
		final double richtig = sum((tc, i) -> ratesReference(weights, tc, i, exchangeRateCalculator));

		final double falsch = sum((tc, i) -> exchangeRateFactor(exchangeRateCalculator, tc, i) * weights[i]
				* tc.rates().get(timeCourses.get(i).rates().size() - 1).value());

		return (falsch - richtig) / richtig;
	}

	@Override
	public Double totalRate(final ExchangeRateCalculator exchangeRateCalculator) {
		if (timeCourses.size() < 2) {
			return null;
		}

		return totalRate(minWeights(), exchangeRateCalculator);
	}

	private double exchangeRateFactor(final ExchangeRateCalculator exchangeRateCalculator, TimeCourse tc, int i) {
		return exchangeRateCalculator.factor(exchangeRate(tc),
				tc.rates().get(timeCourses.get(i).rates().size() - 1).date());
	}


	private double ratesReference(final double[] weights, TimeCourse tc, int i,
			ExchangeRateCalculator exchangeRateCalculator) {
		return exchangeRateFactor(exchangeRateCalculator, tc, i) * weights[i] * tc.rates().get(0).value();
	}

	private double sum(final Filter filter) {
		return IntStream.range(0, timeCourses.size()).mapToDouble(i -> filter.filter(timeCourses.get(i), i))
				.reduce((a, b) -> a + b).orElse(0d);
	}

	private void weightsVectorGuard(final double[] weights) {
		Assert.isTrue(weights.length == timeCourses.size(), "Incorrects size of weightsvector");
	}

	

	@Override
	public Double totalRateDividends(final ExchangeRateCalculator exchangeRateCalculator) {
		if (timeCourses.size() < 2) {
			return null;
		}
		return totalRateDividends(minWeights(), exchangeRateCalculator);
	}

	@Override
	public Double totalRateDividends(final double[] weights, final ExchangeRateCalculator exchangeRateCalculator) {
		weightsVectorGuard(weights);
		final double falsch = sum((tc, i) -> exchangeRateFactor(exchangeRateCalculator, tc, i) * weights[i]
				* tc.dividends().stream().mapToDouble(d -> d.value()).reduce((a, b) -> a + b).orElse(0d));
		final double wahr = sum((tc, i) -> ratesReference(weights, tc, i, exchangeRateCalculator));
		return falsch / wahr;
	}

	
	@Override
	public final ExchangeRate exchangeRate(final TimeCourse timeCourse) {

		return new ExchangeRateImpl(currency(), timeCourse.share().currency());
	}

	@Override
	public final String currency() {
		return "EUR";
	}

	@Override
	public final Collection<ExchangeRate> exchangeRateTranslations() {
		return timeCourses.stream().filter(tc -> !currency().equals(tc.share().currency()))
				.map(tc -> new ExchangeRateImpl(currency(), tc.share().currency())).collect(Collectors.toSet());
	}

}

interface MatixFunction {
	double f(final List<TimeCourse> timeCourses, final int i, final int j);
}

interface Filter {
	double filter(final TimeCourse timecourse, final int i);
}
