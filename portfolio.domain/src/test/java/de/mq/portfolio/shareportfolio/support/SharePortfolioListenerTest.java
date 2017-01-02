package de.mq.portfolio.shareportfolio.support;



import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.mongodb.DBObject;

import de.mq.portfolio.shareportfolio.OptimisationAlgorithm;


public class SharePortfolioListenerTest {

	private static final double[][] COVARIANCES = new double[][] { new double[] { 0.1 } };
	private static final double[][] CORRELATIONS = new double[][] { new double[] { 0.2 } };
	private static final double[] VARIANCES = new double[] { 1e-3 };
	
	private final OptimisationAlgorithm optimisationAlgorithm = Mockito.mock(OptimisationAlgorithm.class);
	private  SharePortfolioListenerImpl listener ;
	private final SharePortfolioImpl sharePortfolio = Mockito.mock(SharePortfolioImpl.class);
	private final DBObject dbo = Mockito.mock(DBObject.class);

	@Before()
	public final void setup() {
		Mockito.when(sharePortfolio.isCommitted()).thenReturn(false);
		Mockito.when(sharePortfolio.onBeforeSave()).thenReturn(true);
		Mockito.when(sharePortfolio.variances()).thenReturn(VARIANCES);
		Mockito.when(sharePortfolio.covariances()).thenReturn(COVARIANCES);
		Mockito.when(sharePortfolio.correlations()).thenReturn(CORRELATIONS);
		Mockito.when(optimisationAlgorithm.algorithmType()).thenReturn(OptimisationAlgorithm.AlgorithmType.RiskGainPreference);
		listener = new SharePortfolioListenerImpl(Arrays.asList(optimisationAlgorithm));
	}

	@Test
	public final void onBeforeSave() {
		listener.onBeforeSave(sharePortfolio, dbo);
		Mockito.verify(dbo).put(SharePortfolioListenerImpl.VARIANCES, VARIANCES);
		Mockito.verify(dbo).put(SharePortfolioListenerImpl.COVARIANCES, COVARIANCES);
		Mockito.verify(dbo).put(SharePortfolioListenerImpl.CORRELATIONS, CORRELATIONS);
	}

	@Test
	public final void onBeforeSaveCommitted() {
		Mockito.when(sharePortfolio.isCommitted()).thenReturn(true);

		listener.onBeforeSave(sharePortfolio, dbo);
		Mockito.verify(dbo, Mockito.never()).put(SharePortfolioListenerImpl.VARIANCES, VARIANCES);
		Mockito.verify(dbo, Mockito.never()).put(SharePortfolioListenerImpl.COVARIANCES, COVARIANCES);
		Mockito.verify(dbo, Mockito.never()).put(SharePortfolioListenerImpl.CORRELATIONS, CORRELATIONS);
	}

	@Test
	public final void onBeforeSaveFalse() {
		Mockito.when(sharePortfolio.onBeforeSave()).thenReturn(false);

		listener.onBeforeSave(sharePortfolio, dbo);
		Mockito.verify(dbo, Mockito.never()).put(SharePortfolioListenerImpl.VARIANCES, VARIANCES);
		Mockito.verify(dbo, Mockito.never()).put(SharePortfolioListenerImpl.COVARIANCES, COVARIANCES);
		Mockito.verify(dbo, Mockito.never()).put(SharePortfolioListenerImpl.CORRELATIONS, CORRELATIONS);
	}
	
	
	@Test
	public final void  onAfterConvert() {
		final SharePortfolioImpl sharePortfolio = new SharePortfolioImpl("name", Arrays.asList(), OptimisationAlgorithm.AlgorithmType.RiskGainPreference);
		
		Assert.assertNull(sharePortfolio.optimisationAlgorithm());
		
		listener.onAfterConvert(dbo, sharePortfolio);
		
		Assert.assertEquals(optimisationAlgorithm, sharePortfolio.optimisationAlgorithm());
	}

}
