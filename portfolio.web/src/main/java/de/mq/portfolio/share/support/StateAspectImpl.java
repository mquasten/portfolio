package de.mq.portfolio.share.support;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.annotation.After;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.mq.portfolio.support.SerialisationUtil;

@Component
@Aspect
class StateAspectImpl {
	
	@Autowired
	private SerialisationUtil serialisationUtil;
	
	
	
	 @After("execution(* de.mq.portfolio.share.support.SharesControllerImpl.init(..))")
	 void deSerialize()  {
		
	 }
	 
	 @After("execution(* de.mq.portfolio.share.support.SharesControllerImpl.*(..)) && @annotation(de.mq.portfolio.support.Serialize) && args(sharesSearchAO,..))")
	 void serialize(final SharesSearchAO sharesSearchAO)  {
		
			final Map<String,Object> state = new HashMap<>();
			state.putAll(serialisationUtil.toMap(sharesSearchAO, Arrays.asList("code", "name" , "index", "selectedSort", "selectedTimeCourseCode")));
			state.putAll(serialisationUtil.toMap(sharesSearchAO.getPageable(), Arrays.asList("page", "counter", "sort")));
			sharesSearchAO.setState(serialisationUtil.serialize(state));  
	 }

}
