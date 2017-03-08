package de.mq.portfolio;

import java.util.Arrays;

import org.junit.Test;

import de.mq.portfolio.shareportfolio.OptimisationAlgorithm;
import org.junit.Assert;

public class AlgorithmTypeTest {
	
	@Test
	public final void values() {
		Arrays.asList(OptimisationAlgorithm.AlgorithmType.values()).forEach(a -> Assert.assertEquals(a, OptimisationAlgorithm.AlgorithmType.valueOf(a.name())));
		
		Assert.assertEquals(3, OptimisationAlgorithm.AlgorithmType.values().length);
	}

}
