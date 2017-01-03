package de.mq.portfolio.shareportfolio.support;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.IntStream;

import Jama.Matrix;
import de.mq.portfolio.shareportfolio.AlgorithmParameter;
import de.mq.portfolio.shareportfolio.OptimisationAlgorithm;

public class RiskGainPreferenceOptimisationImpl implements OptimisationAlgorithm {

	@Override
	public AlgorithmType algorithmType() {
		return AlgorithmType.RiskGainPreference;
	}

	@Override
	public double[] weights(final double[][] varianceMatrix, final AlgorithmParameter... params) {
		final double[] gain = new double[] { 0.0139178 ,0.0130860 ,  0.0249126 };
		final double[][] gainVector = new double[varianceMatrix.length][1]; 
		
		IntStream.range(0, gain.length).forEach(i -> gainVector[i][0]=gain[i] );
		final double[][] array = new double[varianceMatrix.length + 1][varianceMatrix.length + 1];
		IntStream.range(0, varianceMatrix.length).forEach(i -> IntStream.range(0, varianceMatrix.length).forEach(j -> array[i][j] = varianceMatrix[i][j]));

		IntStream.range(0, varianceMatrix.length).forEach(i -> {
			
			array[i][varianceMatrix.length] = 1;
			array[varianceMatrix.length][i] = 1;
		});
		array[varianceMatrix.length][varianceMatrix.length] = 0d;
	
		final Matrix inverseMatrix = new Matrix(array).inverse();
		
		 final Matrix m = new Matrix(varianceMatrix.length, varianceMatrix.length);
		 IntStream.range(0, varianceMatrix.length).forEach(i -> IntStream.range(0,varianceMatrix.length).forEach(j -> m.set(i, j, inverseMatrix.get(i, j))));
	
		
	
		final Matrix d = m.times(new Matrix(gainVector));
		
		
		
		final double ke = IntStream.range(0, varianceMatrix.length).mapToDouble(i -> d.get(i,0) * gain[i] ).sum();
		
	
		
		final double[] weightsMVP = new double[varianceMatrix.length]; 
		IntStream.range(0, varianceMatrix.length).forEach(i -> weightsMVP[i]=inverseMatrix.get(varianceMatrix.length, i) );
		
		
		final double totalGainMVP = IntStream.range(0, varianceMatrix.length).mapToDouble(i -> weightsMVP[i]*gain[i]).sum();
		
		
	
		
		final double theta = (0.018769 - totalGainMVP)/ke;
		
		
		final double[] results = new double[varianceMatrix.length];
		IntStream.range(0, varianceMatrix.length).forEach(i -> results[i]=weightsMVP[i] + theta*d.get(i,0) );
		return results;
	}

	@Override
	public Collection<Enum<?>> params() {
		return Collections.unmodifiableCollection(Arrays.asList());
	}

}
