package de.mq.portfolio.shareportfolio.support;

import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;

import de.mq.portfolio.shareportfolio.OptimisationAlgorithm;
import de.mq.portfolio.shareportfolio.OptimisationAlgorithm.AlgorithmType;
import junit.framework.Assert;

public class MinRiskOptimisationTest {


	
	private final OptimisationAlgorithm optimisationAlgorithm = new MinRiskOptimisationImpl();
	
	private final static double[] WEIGHTS = {54.50d, 44.29d , 1.21d};
	static final String VARIANCES_FIELD = "variances";

	static final String COVARIANCES_FIELD = "covariances";
	
	
	private double[][]  matrix;
	
	@Before
	public  void setup() {
		
		matrix = new double[3][3];
	
		matrix[0][0] = 0.0014023;
		matrix[0][1] = 0.0004629;
		matrix[0][2] = 0.0004031;

		matrix[1][1] = 0.0015854;
		matrix[1][0] = 0.0004629;
		matrix[1][2] = 0.0016245;

		matrix[2][2] = 0.0028889;
		matrix[2][0] = 0.0004031;
		matrix[2][1] = 0.0016245;

		
	}
	
	@Test
	public final void resolve() {
		final  double[] results = optimisationAlgorithm.weights(matrix);
		Assert.assertEquals(WEIGHTS.length, results.length);
		IntStream.range(0, results.length).forEach(i -> Assert.assertEquals(WEIGHTS[i], percentRound(results[i])));
		
		
	}
	
	@Test
	public final void algorithm() {
		Assert.assertEquals(AlgorithmType.MVP, optimisationAlgorithm.algorithmType());
	}
	
	public final void params() {
		Assert.assertTrue(optimisationAlgorithm.params().isEmpty());
	}
	
	
	private double percentRound(double value) {
		return Math.round(10000 * value) / 100d;
	}
}