package de.mq.portfolio.support;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

@Component
@Aspect
abstract class  StateAspectImpl {
	
	@Autowired
	private SerialisationUtil serialisationUtil;
	
	
	
	 @After("execution(* *(..))&& @annotation(de.mq.portfolio.support.DeSerialize) && args(sharesSearchAO,..) && target(controller) && @annotation(deSerialize)  )")
	 void deSerialize(final Object sharesSearchAO, final Object controller, DeSerialize deSerialize)  {
		
		if( serialisationUtil.getAndIncVersion(sharesSearchAO) > 0 ) {
			return; 
		}
			
		
		if( userModel().state(facesContext().getViewRoot().getViewId())==null) {
			return ;
		}
	
		final Map <String,Object> stateMap = serialisationUtil.deSerialize(userModel().state(facesContext().getViewRoot().getViewId()));
		
		serialisationUtil.toBean(stateMap, sharesSearchAO, Arrays.asList(deSerialize.mappings()));
		
		stateMap.put(Parameter.DEFAULT_PARAMETER, sharesSearchAO);
		serialisationUtil.execute(controller, deSerialize.methodRegex(), stateMap);
		
	 }
	 
	 @After("execution(* *(..)) && @annotation(de.mq.portfolio.support.Serialize) && args(sharesSearchAO,..) && @annotation(serialize))")
	 void serialize(final Object sharesSearchAO, final Serialize serialize)  {		 
			final Map<String,Object> state = new HashMap<>();
			state.putAll(serialisationUtil.toMap(sharesSearchAO, Arrays.asList(serialize.fields()), Arrays.asList(serialize.mappings())));
			userModel().assign(facesContext().getViewRoot().getViewId(), serialisationUtil.serialize(state));
	 }
	 
	
	 
	@Lookup
	abstract  UserModel userModel() ;
	
	@Lookup
	abstract FacesContext facesContext();
}
