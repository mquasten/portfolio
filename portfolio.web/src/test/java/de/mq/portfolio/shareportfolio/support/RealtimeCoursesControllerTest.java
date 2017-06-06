package de.mq.portfolio.shareportfolio.support;



import org.junit.Test;
import org.mockito.Mockito;


public class RealtimeCoursesControllerTest {
	
	private static final String ID = "19680528";

	private final SharePortfolioService sharePortfolioService = Mockito.mock(SharePortfolioService.class);
	
	private final RealtimeCoursesController realtimeCoursesController = new RealtimeCoursesController(sharePortfolioService);
	
	private final RealtimeCoursesAO realtimeCourses = Mockito.mock(RealtimeCoursesAO.class);
	
	private final RealtimePortfolioAggregation realtimePortfolioAggregation = Mockito.mock(RealtimePortfolioAggregation.class);
	
	@Test
	public final void init() {
		Mockito.doReturn(ID).when(realtimeCourses).getPortfolioId();
		Mockito.doReturn(true).when(realtimeCourses).getLastStoredTimeCourse();
		Mockito.doReturn(realtimePortfolioAggregation).when(sharePortfolioService).realtimePortfolioAggregation(ID, true);
		realtimeCoursesController.init(realtimeCourses);
		
		Mockito.verify(realtimeCourses).assign(realtimePortfolioAggregation);
	}
}
