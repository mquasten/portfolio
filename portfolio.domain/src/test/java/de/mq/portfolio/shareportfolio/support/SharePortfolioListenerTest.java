package de.mq.portfolio.shareportfolio.support;



import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.verification.VerificationMode;
import org.springframework.data.mongodb.core.mapping.event.AfterConvertEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent;

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
	@SuppressWarnings("unchecked")
	private final BeforeSaveEvent<SharePortfolioImpl> beforeSaveEvent = Mockito.mock(BeforeSaveEvent.class); 
	
	@SuppressWarnings("unchecked")
	private final AfterConvertEvent<SharePortfolioImpl> afterConvertEvent =  Mockito.mock(AfterConvertEvent.class); 

	@Before()
	public final void setup() {
		Mockito.when(sharePortfolio.isCommitted()).thenReturn(false);
		Mockito.when(sharePortfolio.onBeforeSave()).thenReturn(true);
		Mockito.when(sharePortfolio.variances()).thenReturn(VARIANCES);
		Mockito.when(sharePortfolio.covariances()).thenReturn(COVARIANCES);
		Mockito.when(sharePortfolio.correlations()).thenReturn(CORRELATIONS);
		Mockito.when(optimisationAlgorithm.algorithmType()).thenReturn(OptimisationAlgorithm.AlgorithmType.RiskGainPreference);
		Mockito.when(beforeSaveEvent.getDBObject()).thenReturn(dbo);
		Mockito.when(beforeSaveEvent.getSource()).thenReturn(sharePortfolio);
		
		listener = new SharePortfolioListenerImpl(Arrays.asList(optimisationAlgorithm));
	}

/*	@Test
	public final void onBeforeSave() {
		listener.onBeforeSave(sharePortfolio, dbo);
		
		verifyOnBeforeSave(Mockito.times(1));
	} */
	
	@Test
	public final void onBeforeSaveEvent() {
		listener.onBeforeSave(beforeSaveEvent);
		
		verifyOnBeforeSave(Mockito.times(1));
	}

	private void verifyOnBeforeSave(VerificationMode verificationMode) {
		Mockito.verify(dbo, verificationMode).put(SharePortfolioListenerImpl.VARIANCES, VARIANCES);
		Mockito.verify(dbo, verificationMode).put(SharePortfolioListenerImpl.COVARIANCES, COVARIANCES);
		Mockito.verify(dbo, verificationMode).put(SharePortfolioListenerImpl.CORRELATIONS, CORRELATIONS);
	}

	
	@Test
	public final void onBeforeSaveCommittedEvent() {
		Mockito.when(sharePortfolio.isCommitted()).thenReturn(true);

		listener.onBeforeSave(beforeSaveEvent);
		
		verifyOnBeforeSave(Mockito.never());
	}

	
	@Test
	public final void onBeforeSaveFalseEvent() {
		Mockito.when(sharePortfolio.onBeforeSave()).thenReturn(false);

		listener.onBeforeSave(beforeSaveEvent);

		verifyOnBeforeSave(Mockito.never());
	}
	
	
	
	@Test
	public final void  onAfterConvertEvent() {
		final SharePortfolioImpl sharePortfolio = new SharePortfolioImpl("name", Arrays.asList(), OptimisationAlgorithm.AlgorithmType.RiskGainPreference);
		Mockito.when(afterConvertEvent.getSource()).thenReturn(sharePortfolio);
		
		Assert.assertNull(sharePortfolio.optimisationAlgorithm());
		
		listener.onAfterConvert(afterConvertEvent);
		
		Assert.assertEquals(optimisationAlgorithm, sharePortfolio.optimisationAlgorithm());
	}


}
