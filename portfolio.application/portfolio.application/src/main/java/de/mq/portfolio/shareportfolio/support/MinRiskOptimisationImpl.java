package de.mq.portfolio.shareportfolio.support;

import java.util.Collection;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import Jama.Matrix;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.shareportfolio.OptimisationAlgorithm;
import de.mq.portfolio.shareportfolio.SharePortfolio;

@Service
public class MinRiskOptimisationImpl implements OptimisationAlgorithm {

	@Override
	public double[] weights(final SharePortfolio sharePortfolio) {
		
		final Collection<TimeCourse> timeCourses = sharePortfolio.timeCourses();
		final double[] variances = ((SharePortfolioImpl)sharePortfolio).variances();
		final double[][] covariances =  ((SharePortfolioImpl)sharePortfolio).covariances();
		final double[][] array = new double[timeCourses.size() + 1][timeCourses.size() + 1];
		IntStream.range(0, timeCourses.size()).forEach(i -> IntStream.range(0, timeCourses.size()).filter(j -> j != i).forEach(j -> array[i][j] = covariances[i][j]));

		IntStream.range(0, timeCourses.size()).forEach(i -> {
			array[i][i] = variances[i];
			array[i][timeCourses.size()] = 1;
			array[timeCourses.size()][i] = 1;
		});
		array[timeCourses.size()][timeCourses.size()] = 0d;
	
		final Matrix matrix = new Matrix(array);
		final Matrix vectorAsMatrix = new Matrix(timeCourses.size() + 1, 1, 0d);
		vectorAsMatrix.set(timeCourses.size(), 0, 1d);
		final Matrix vector = vectorAsMatrix;
		// matrix.print(15, 10);

		// vector.print(15,10);
		final Matrix result = matrix.solve(vector);
		final double[] weights = new double[timeCourses.size()];
		IntStream.range(0, timeCourses.size()).forEach(i -> weights[i]= result.get(i, 0));
		return weights;
	}

	@Override
	public AlgorithmType algorithmType() {
		
		return AlgorithmType.MVP;
	}
	

	
	
}
