package de.mq.portfolio.shareportfolio.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;
import de.mq.portfolio.shareportfolio.PortfolioOptimisation;
import de.mq.portfolio.shareportfolio.SharePortfolio;
import junit.framework.Assert;

public class SharePortfolioTest {
	
	

	private static final String CODE = "CODE";

	private static final String SHARE_NAME_02 = "Share02";

	private static final String SHARE_NAME_01 = "Share01";

	private static final String NEW_SHARE_NAME = "Coca Cola";

	private static final String MIN_VARIANCE_FIELD = "minVariance";

	static final String VARIANCES_FIELD = "variances";

	static final String COVARIANCES_FIELD = "covariances";

	static final String CORRELATIONS_FIELD = "correlations";

	static final String COLLECTION = "Portfolio";

	static final String NAME = "mq-test";

	private final List<TimeCourse> timeCourses = new ArrayList<>(); 
	
	private  SharePortfolio sharePortfolio;
	
	private final double[] variances = new double[] {1d/144d , 1d/(24d*24d) }; 
   
	private final double[] []  covariances = new double[2][2] ;
	
	private final double[] []  correlations = new double[2][2] ;
   
	private final TimeCourse timeCourse1 = Mockito.mock(TimeCourse.class);
	private final TimeCourse timeCourse2 = Mockito.mock(TimeCourse.class);
	private final double[] weights = new double[] { 1d/3d , 2d/3d};
	
	private final Share share = Mockito.mock(Share.class);
	private final TimeCourse timeCourse = Mockito.mock(TimeCourse.class);
	private final Share share1 = Mockito.mock(Share.class);
	private final Share share2 = Mockito.mock(Share.class);
	private final PortfolioOptimisation portfolioOptimisation = Mockito.mock(PortfolioOptimisation.class);
   
	@Before
	public void setup() {
		
		Mockito.when(share.name()).thenReturn(NEW_SHARE_NAME);
		Mockito.when(share.code()).thenReturn(CODE);
		Mockito.when(timeCourse.share()).thenReturn(share);
		
		Mockito.when(share1.name()).thenReturn(SHARE_NAME_01);
		Mockito.when(share1.code()).thenReturn(CODE);
		
		Mockito.when(share2.name()).thenReturn(SHARE_NAME_02);
		Mockito.when(share2.code()).thenReturn(CODE);
		
		Mockito.when(timeCourse1.share()).thenReturn(share1);
		Mockito.when(timeCourse2.share()).thenReturn(share2);
		timeCourses.add(timeCourse1);
		timeCourses.add(timeCourse2);
		sharePortfolio = new SharePortfolioImpl(NAME, timeCourses);
	   covariances[0][1]=1d/(12d*24);
	   covariances[1][0]=1d/(12d*24);
		
	  
	   correlations[0][0]=1d;
	   correlations[1][1]=1d;
	   correlations[0][1]=1d/(12d*24 * Math.sqrt(variances[0])* Math.sqrt(variances[1]));
	   correlations[1][0]=1d/(12d*24* Math.sqrt(variances[0])* Math.sqrt(variances[1]));
	   
	   ReflectionTestUtils.setField(sharePortfolio, VARIANCES_FIELD, variances);
	   ReflectionTestUtils.setField(sharePortfolio, COVARIANCES_FIELD, covariances);
	   ReflectionTestUtils.setField(sharePortfolio, CORRELATIONS_FIELD, correlations);
	   
	   ReflectionTestUtils.setField(sharePortfolio, MIN_VARIANCE_FIELD, portfolioOptimisation);
	}
	
	
	@Test
	public final void variance() {
		
		
		Assert.assertEquals(1d/(9d*36d), sharePortfolio.risk(weights));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public final void varianceWrongData() {
		 ReflectionTestUtils.setField(sharePortfolio, COVARIANCES_FIELD, new double[1][1]);
		 sharePortfolio.risk(weights);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public final void weightWrong() {
		sharePortfolio.risk(new double[] { 1d,2d,3d});
	}

	@Test
	public final void variances() {
		Assert.assertEquals(variances, ((SharePortfolioImpl)sharePortfolio).variances());
	}
	

	@Test
	public final void covariances() {
		Assert.assertEquals(covariances, ((SharePortfolioImpl)sharePortfolio).covariances());
	}

	
	@Test
	public final void correlations() {
		Assert.assertEquals(correlations, ((SharePortfolioImpl)sharePortfolio).correlations());
	}
	
	@Test
	public final void name() {
		Assert.assertEquals(NAME, sharePortfolio.name());
	}
	
	@Test
	public final void commit() {
		Assert.assertFalse(sharePortfolio.isCommitted());
		sharePortfolio.commit();
		Assert.assertTrue(sharePortfolio.isCommitted());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public final void commitNoTimeCourses() {
		new SharePortfolioImpl(NAME, new ArrayList<>()).commit();
	}
	
	@Test
	public final void minVariance() {
		Assert.assertEquals(Optional.of(portfolioOptimisation), sharePortfolio.minVariance());
	}
	
	@Test
	public final void timeCourses() {
		Assert.assertEquals(timeCourses, sharePortfolio.timeCourses());
	}
	
	@Test
	public final void onBeforeSave() {
		Mockito.when(timeCourse1.variance()).thenReturn(1e-3);
		Mockito.when(timeCourse1.variance()).thenReturn(2e-3);
		Mockito.when(timeCourse1.covariance(timeCourse1)).thenReturn(4e-6);
		Mockito.when(timeCourse2.covariance(timeCourse2)).thenReturn(5e-6);
		
		Mockito.when(timeCourse1.covariance(timeCourse2)).thenReturn(6e-3);
		Mockito.when(timeCourse2.covariance(timeCourse1)).thenReturn(7e-3);
		
		Assert.assertTrue(((SharePortfolioImpl)sharePortfolio).onBeforeSave());
		
		final double[] variances = (double[]) ReflectionTestUtils.getField(sharePortfolio, VARIANCES_FIELD);
		Assert.assertEquals(2, variances.length);
		Assert.assertEquals(timeCourse1.variance(), variances[0]);
		Assert.assertEquals(timeCourse2.variance(), variances[1]);
		
		final double[][] covariances = (double[][]) ReflectionTestUtils.getField(sharePortfolio, COVARIANCES_FIELD);
		Assert.assertEquals(2, covariances.length);
		Assert.assertEquals(2, covariances[0].length);
		Assert.assertEquals(2, covariances[1].length);
		
		Assert.assertEquals(timeCourse1.covariance(timeCourse1), covariances[0][0]);
		Assert.assertEquals(timeCourse2.covariance(timeCourse2), covariances[1][1]);
		Assert.assertEquals(timeCourse1.covariance(timeCourse2), covariances[0][1]);
		Assert.assertEquals(timeCourse2.covariance(timeCourse1), covariances[1][0]);
		
		final double[][] correlations = (double[][]) ReflectionTestUtils.getField(sharePortfolio, CORRELATIONS_FIELD);
		final int[] counter = { 0};
		IntStream.range(0, 2).forEach(i -> {
			IntStream.range(0, 2).forEach(j -> {
				Assert.assertEquals(covariances[i][j] / ( Math.sqrt(variances[i])*  Math.sqrt(variances[j])), correlations[i][j]);
				counter[0]= counter[0] +1;
			});
			
		});
		Assert.assertEquals(4, counter[0]);
	}
	
	@Test
	public final void onBeforeSaveNoTimeCourses() {
		Assert.assertFalse(new SharePortfolioImpl(NAME, new ArrayList<>()).onBeforeSave());
	}
	
	@Test
	public final void annotations() {
		Assert.assertTrue(SharePortfolioImpl.class.isAnnotationPresent(Document.class));
		Assert.assertEquals(COLLECTION, SharePortfolioImpl.class.getAnnotation(Document.class).collection());
	}
	
	@Test
	public final void assignTimeCourse() {
		
		Assert.assertEquals(timeCourses, sharePortfolio.timeCourses());
		sharePortfolio.assign(timeCourse);
		Assert.assertEquals(timeCourses.size()+1, sharePortfolio.timeCourses().size());
	
		final Optional<TimeCourse> result = sharePortfolio.timeCourses().stream().filter(tc -> tc.share().name()==share.name()).findFirst();
		Assert.assertTrue(result.isPresent());
		Assert.assertEquals(timeCourse, result.get());
	}
	
	@Test
	public final void assignTimeCourseExists() {
		Assert.assertEquals(timeCourses, sharePortfolio.timeCourses());
		Mockito.when(share.name()).thenReturn(SHARE_NAME_01);	
		sharePortfolio.assign(timeCourse);
		Assert.assertEquals(timeCourses.size(), sharePortfolio.timeCourses().size());
		Assert.assertEquals(timeCourses.size(), sharePortfolio.timeCourses().stream().filter(tc -> tc.share().equals(share1)||tc.share().equals(share2)).count());
	}
}
