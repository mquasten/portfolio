package de.mq.portfolio.share.support;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

import de.mq.portfolio.support.SerialisationUtil;
import de.mq.portfolio.support.Serialize;
import de.mq.portfolio.support.UserModel;

@Component
@Aspect
abstract class  StateAspectImpl {
	
	@Autowired
	private SerialisationUtil serialisationUtil;
	
	
	
	 @After("execution(* de.mq.portfolio.share.support.SharesControllerImpl.*(..))&& @annotation(de.mq.portfolio.support.DeSerialize)&& args(sharesSearchAO,..) && target(controller)")
	 void deSerialize(final SharesSearchAO sharesSearchAO, final SharesControllerImpl controller)  {
		
		if( ! sharesSearchAO.isNew()) {
			return;
		}
		sharesSearchAO.setUsed();
		
		if( userModel().state(facesContext().getViewRoot().getViewId())==null) {
			return ;
		}
		
	
		final Map <String,Object> stateMap = serialisationUtil.deSerialize(userModel().state(facesContext().getViewRoot().getViewId()));
		
		
		serialisationUtil.toBean(stateMap, sharesSearchAO);
		//sharesSearchAO.setPageable(shareService.pageable(sharesSearchAO.getSearch(),orderBy.get(sharesSearchAO.getSelectedSort()), 10));
		serialisationUtil.toBean(stateMap, sharesSearchAO.getPageable());
		
		final String selectedTimeCourseCode = sharesSearchAO.getSelectedTimeCourseCode();
		
		controller.refreshTimeCourses(sharesSearchAO);
		sharesSearchAO.getTimeCourses().stream().filter(tc ->tc.getValue().code().equals(selectedTimeCourseCode)).findAny().ifPresent(selected ->sharesSearchAO.setSelectedTimeCourse(selected) );
	 }
	 
	 @After("execution(* de.mq.portfolio.share.support.SharesControllerImpl.*(..)) && @annotation(de.mq.portfolio.support.Serialize) && args(sharesSearchAO,..) && @annotation(serialize))")
	 void serialize(final SharesSearchAO sharesSearchAO, final Serialize serialize)  {		 
			final Map<String,Object> state = new HashMap<>();
			state.putAll(serialisationUtil.toMap(sharesSearchAO, Arrays.asList(serialize.fields()), Arrays.asList(serialize.mappings())));
		//	state.putAll(serialisationUtil.toMap(sharesSearchAO.getPageable(), Arrays.asList("page", "counter", "sort")));
			
			userModel().assign(facesContext().getViewRoot().getViewId(), serialisationUtil.serialize(state));
	 }
	 
	
	 
	@Lookup
	abstract  UserModel userModel() ;
	
	@Lookup
	abstract FacesContext facesContext();
}
