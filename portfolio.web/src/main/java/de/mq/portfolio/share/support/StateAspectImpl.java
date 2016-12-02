package de.mq.portfolio.share.support;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.annotation.After;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import de.mq.portfolio.support.SerialisationUtil;

@Component
@Aspect
class StateAspectImpl {
	
	@Autowired
	private SerialisationUtil serialisationUtil;
	
	
	
	 @After("execution(* de.mq.portfolio.share.support.SharesControllerImpl.*(..))&& @annotation(de.mq.portfolio.support.DeSerialize)&& args(sharesSearchAO,..) && target(controller)")
	 void deSerialize(final SharesSearchAO sharesSearchAO, final SharesControllerImpl controller)  {
		
		if( ! sharesSearchAO.isNew()) {
			return;
		}
		sharesSearchAO.setUsed();
		
		if( ! StringUtils.hasText(sharesSearchAO.getState())) {
			return;
		}
		
		
		//:toDo JSF is f ..., most f...ing shit ever !!!! 
		if( sharesSearchAO.getState().equals("null")) {
			return;
		}
		final Map <String,Object> stateMap = serialisationUtil.deSerialize(sharesSearchAO.getState());
		
		
		serialisationUtil.toBean(stateMap, sharesSearchAO);
		//sharesSearchAO.setPageable(shareService.pageable(sharesSearchAO.getSearch(),orderBy.get(sharesSearchAO.getSelectedSort()), 10));
		serialisationUtil.toBean(stateMap, sharesSearchAO.getPageable());
		
		final String selectedTimeCourseCode = sharesSearchAO.getSelectedTimeCourseCode();
		
		controller.refreshTimeCourses(sharesSearchAO);
		sharesSearchAO.getTimeCourses().stream().filter(tc ->tc.getValue().code().equals(selectedTimeCourseCode)).findAny().ifPresent(selected ->sharesSearchAO.setSelectedTimeCourse(selected) );
	 }
	 
	 @After("execution(* de.mq.portfolio.share.support.SharesControllerImpl.*(..)) && @annotation(de.mq.portfolio.support.Serialize) && args(sharesSearchAO,..))")
	 void serialize(final SharesSearchAO sharesSearchAO)  {
		
			final Map<String,Object> state = new HashMap<>();
			state.putAll(serialisationUtil.toMap(sharesSearchAO, Arrays.asList("code", "name" , "index", "selectedSort", "selectedTimeCourseCode")));
			state.putAll(serialisationUtil.toMap(sharesSearchAO.getPageable(), Arrays.asList("page", "counter", "sort")));
			sharesSearchAO.setState(serialisationUtil.serialize(state));  
	 }

}
