package de.mq.portfolio.shareportfolio.support;

import java.util.Arrays;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import de.mq.portfolio.shareportfolio.AlgorithmParameter;
import de.mq.portfolio.shareportfolio.OptimisationAlgorithm;
import de.mq.portfolio.shareportfolio.OptimisationAlgorithm.AlgorithmType;
import de.mq.portfolio.shareportfolio.SharePortfolio;
import junit.framework.Assert;

public class ManualDistributionOptimisationTest {
	
	final OptimisationAlgorithm optimisationAlgorithm = new ManualDistributionOptimisationImpl();
	
	
	private final double varianceMatrix[][] = new double[3][3];
	private final AlgorithmParameter param = Mockito.mock(AlgorithmParameter.class);
	
	private SharePortfolio sharePortfolio = Mockito.mock(SharePortfolio.class);
	
	@Before
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public final void setup() {
		Mockito.when(param.type()).thenReturn((Enum) ManualDistributionOptimisationImpl.ParameterType.Weights);
		Mockito.when(sharePortfolio.varianceMatrix()).thenReturn(varianceMatrix);
	}
	
	@Test
	public final void resolve() {
		final double[] results = optimisationAlgorithm.weights(sharePortfolio) ;
		Assert.assertEquals(varianceMatrix.length, results.length);
		
		IntStream.range(0, varianceMatrix.length).forEach(i -> Assert.assertEquals(1d/varianceMatrix.length, results[i]));
	
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
	}

}
