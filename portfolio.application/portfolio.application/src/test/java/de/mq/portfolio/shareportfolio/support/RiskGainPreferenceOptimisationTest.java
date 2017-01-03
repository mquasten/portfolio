package de.mq.portfolio.shareportfolio.support;

import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;

import de.mq.portfolio.shareportfolio.OptimisationAlgorithm;

public class RiskGainPreferenceOptimisationTest {
	
private final double[][]  matrix = new double[3][3];
private final OptimisationAlgorithm optimisationAlgorithm = new RiskGainPreferenceOptimisationImpl();
	
	@Before
	public  void setup() {
		
		matrix[0][0] = 0.0005786;
		matrix[0][1] = 0.0003741;
		matrix[0][2] = 0.0002840;

	
		matrix[1][0] = 0.0003741;
		matrix[1][1] = 0.0010420;
		matrix[1][2] = 0.0000202;

		
		matrix[2][0] = 0.0002840;
		matrix[2][1] = 0.0000202;
		matrix[2][2] = 0.0010562;

		
	}
	
	@Test
	public final void weights() {
		final double[] results = optimisationAlgorithm.weights(matrix);
		
		IntStream.range(0, results.length).forEach(i -> System.out.println(results[i]));
		
		
	}

}
