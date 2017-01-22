package de.mq.portfolio.shareportfolio.support;

import org.junit.Test;


import de.mq.portfolio.shareportfolio.AlgorithmParameter;
import junit.framework.Assert;

public class AlgorithmParameterTest {
	
	@Test
	public final void isVector() {
		
		Assert.assertFalse(MyParameter.Test.isVector());
	}
	
	enum MyParameter   implements AlgorithmParameter {
		Test;
	}

}
