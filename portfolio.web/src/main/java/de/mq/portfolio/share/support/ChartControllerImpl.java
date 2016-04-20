package de.mq.portfolio.share.support;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.mq.portfolio.share.ShareService;
import de.mq.portfolio.share.TimeCourse;


@Component("chartController")
@Scope("singleton")
public class ChartControllerImpl {
	
	private final ShareService shareService;
	
	@Autowired
	ChartControllerImpl(final ShareService shareService) {
		this.shareService=shareService;
	}
	
	public void init(final ChartAO chartAO) {
	
		
		final Optional<TimeCourse> timeCourse = shareService.timeCourse(chartAO.getCode());
		if(!timeCourse.isPresent()){
			return;
		}
		
		chartAO.setDividends(timeCourse.get().dividends());
		chartAO.setWkn(timeCourse.get().share().wkn());
	
	}

}
