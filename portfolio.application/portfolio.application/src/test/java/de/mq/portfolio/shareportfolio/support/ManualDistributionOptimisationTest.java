package de.mq.portfolio.shareportfolio.support;

import java.util.Arrays;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


import de.mq.portfolio.shareportfolio.OptimisationAlgorithm;
import de.mq.portfolio.shareportfolio.OptimisationAlgorithm.AlgorithmType;
import de.mq.portfolio.shareportfolio.SharePortfolio;


public class ManualDistributionOptimisationTest {
	
	final OptimisationAlgorithm optimisationAlgorithm = new ManualDistributionOptimisationImpl();
	
	
	private final double varianceMatrix[][] = new double[3][3];
	//private final AlgorithmParameter param = Mockito.mock(AlgorithmParameter.class);
	
	private SharePortfolio sharePortfolio = Mockito.mock(SharePortfolio.class);
	
	@Before

	public final void setup() {
	//	Mockito.when(param.type()).thenReturn((Enum) ManualDistributionOptimisationImpl.ParameterType.Weights);
		Mockito.when(sharePortfolio.varianceMatrix()).thenReturn(varianceMatrix);
	}
	
	@Test
	public final void resolve() {
		final double[] results = optimisationAlgorithm.weights(sharePortfolio) ;
		Assert.assertEquals(varianceMatrix.length, results.length);
		
		IntStream.range(0, varianceMatrix.length).forEach(i -> Assert.assertEquals((Double) (1d/varianceMatrix.length),  (Double) results[i]));
	
	}
	
	@Test(expected=IllegalArgumentException.class)
	public final void resolveWeightsNullGuard() {
	    Mockito.when(sharePortfolio.parameterVector(ManualDistributionOptimisationImpl.ParameterType.Weights)).thenReturn(Arrays.asList(null,1d));
		optimisationAlgorithm.weights(sharePortfolio);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public final void resolveWeights0Guard() {
	    Mockito.when(sharePortfolio.parameterVector(ManualDistributionOptimisationImpl.ParameterType.Weights)).thenReturn(Arrays.asList(0d,1d));
		optimisationAlgorithm.weights(sharePortfolio);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public final void resolveSumGuard() {
		 Mockito.when(sharePortfolio.parameterVector(ManualDistributionOptimisationImpl.ParameterType.Weights)).thenReturn(Arrays.asList(3d,6d));
		 optimisationAlgorithm.weights(sharePortfolio);
	}
	
	@Test
	public final void resolveFromParameters() {
		final double[] results = {0.4, 0.6};
		Mockito.when(sharePortfolio.parameterVector(ManualDistributionOptimisationImpl.ParameterType.Weights)).thenReturn(Arrays.asList(results[0], results[1]));
		
		IntStream.range(0, results.length).forEach(i->  Assert.assertEquals((Double) results[i], (Double)optimisationAlgorithm.weights(sharePortfolio)[i]));
	}
	
	@Test
	public final void algorithmType() {
		Assert.assertEquals(AlgorithmType.ManualDistribution, optimisationAlgorithm.algorithmType());
	}
	
	@Test
	public final void  params() {
		Assert.assertEquals(Arrays.asList(ManualDistributionOptimisationImpl.ParameterType.values()), optimisationAlgorithm.params());
	}
	
	@Test
	public final void  enumValues() {
		Arrays.asList(ManualDistributionOptimisationImpl.ParameterType.values()).forEach(val -> Assert.assertEquals(val, ManualDistributionOptimisationImpl.ParameterType.valueOf(val.name()) ));
		Assert.assertTrue(ManualDistributionOptimisationImpl.ParameterType.Weights.isVector());
	}

}
