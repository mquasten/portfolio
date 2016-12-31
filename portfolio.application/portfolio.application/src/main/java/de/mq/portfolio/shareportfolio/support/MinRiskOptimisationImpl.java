package de.mq.portfolio.shareportfolio.support;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import Jama.Matrix;
import de.mq.portfolio.shareportfolio.AlgorithmParameter;
import de.mq.portfolio.shareportfolio.OptimisationAlgorithm;

@Service
class MinRiskOptimisationImpl implements OptimisationAlgorithm {

	@Override
	public double[] weights(final double[][] varianceMatrix, final AlgorithmParameter ... params) {
		

		final double[][] array = new double[varianceMatrix.length + 1][varianceMatrix.length + 1];
		IntStream.range(0, varianceMatrix.length).forEach(i -> IntStream.range(0, varianceMatrix.length).forEach(j -> array[i][j] = varianceMatrix[i][j]));

		IntStream.range(0, varianceMatrix.length).forEach(i -> {
			
			array[i][varianceMatrix.length] = 1;
			array[varianceMatrix.length][i] = 1;
		});
		array[varianceMatrix.length][varianceMatrix.length] = 0d;
	
		final Matrix matrix = new Matrix(array);
		final Matrix vectorAsMatrix = new Matrix(varianceMatrix.length + 1, 1, 0d);
		vectorAsMatrix.set(varianceMatrix.length, 0, 1d);
		final Matrix vector = vectorAsMatrix;
		// matrix.print(15, 10);

		// vector.print(15,10);
		final Matrix result = matrix.solve(vector);
		final double[] weights = new double[varianceMatrix.length];
		IntStream.range(0, varianceMatrix.length).forEach(i -> weights[i]= result.get(i, 0));
		return weights;
	}

	@Override
	public AlgorithmType algorithmType() {
		
		return AlgorithmType.MVP;
	}

	@Override
	public Collection<Enum<?>> params() {
		return Collections.unmodifiableList(Arrays.asList());
	}
	

	
	
}
