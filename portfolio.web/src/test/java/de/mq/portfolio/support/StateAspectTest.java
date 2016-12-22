package de.mq.portfolio.support;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ReflectionUtils;

import junit.framework.Assert;

public class StateAspectTest {
	
	 private static final String METHOD_REGEX = "methodRegEx";

	private static final String MAPPING = "mapping=mapping";

	private static final long PAGE_VALUE = 42L;

	private static final String PAGE_KEY = "page";

	private static final String STATE_STRING = "stateAsString";

	private static final String VIEW_ID = "shares.xhtml";

	private final SerialisationUtil serialisationUtil = Mockito.mock(SerialisationUtil.class);
	 
	 private final StateAspectImpl  stateAspect = Mockito.mock(StateAspectImpl.class, Mockito.CALLS_REAL_METHODS);
	 
	 private final Object backingBean = Mockito.mock(Object.class);
	 
	 private final Object controller = Mockito.mock(Object.class);
	 
	 private final DeSerialize deSerialize = Mockito.mock(DeSerialize.class);
	 
	 private final FacesContext facesContext = Mockito.mock(FacesContext.class);
	 private final UserModel userModel = Mockito.mock(UserModel.class);
	 
	 private final UIViewRoot viewRoot = Mockito.mock(UIViewRoot.class);
	 
	 final Map<String,Object> stateMap = new HashMap<>();
	 
	 @Before
	 public final void setup() {
		 
		 
		
		 stateMap.put(PAGE_KEY, PAGE_VALUE);
		 Mockito.when(serialisationUtil.getAndIncVersion(backingBean)).thenReturn(0L);
		 Mockito.when(deSerialize.methodRegex()).thenReturn(METHOD_REGEX);
		 Mockito.when(deSerialize.mappings()).thenReturn(new String[] {MAPPING});
		 
		 Mockito.when(viewRoot.getViewId()).thenReturn(VIEW_ID);
		 Mockito.when(facesContext.getViewRoot()).thenReturn(viewRoot);
		 Mockito.when(stateAspect.facesContext()).thenReturn(facesContext);
		 Mockito.when(stateAspect.userModel()).thenReturn(userModel);
		 
		 Mockito.when(userModel.state(VIEW_ID)).thenReturn(STATE_STRING);
		 
		 Mockito.when(serialisationUtil.deSerialize(STATE_STRING)).thenReturn(stateMap);
		 ReflectionUtils.doWithFields(stateAspect.getClass(), field -> ReflectionTestUtils.setField(stateAspect, field.getName(), serialisationUtil), field-> field.getType().equals(SerialisationUtil.class));
	 }
	 
	 @Test
	 public final void deSerialize() {
		
		 stateAspect.deSerialize(backingBean, controller, deSerialize);
		 
		 Mockito.verify(serialisationUtil, Mockito.times(1)).toBean(stateMap, backingBean, Arrays.asList(deSerialize.mappings()));
	 
		 Mockito.verify(serialisationUtil).execute(controller, deSerialize.methodRegex(), stateMap);
		 
		 Assert.assertEquals(2, stateMap.size());
		 Assert.assertEquals(backingBean, stateMap.get(Parameter.DEFAULT_PARAMETER));
		 Assert.assertEquals(PAGE_VALUE, stateMap.get(PAGE_KEY));
	 }
	 
	 @Test
	 public final void deSerializeVersionWrong() {
		 Mockito.when(serialisationUtil.getAndIncVersion(backingBean)).thenReturn(1L);
		 
		 stateAspect.deSerialize(backingBean, controller, deSerialize);
		 
		 Mockito.verify(serialisationUtil,Mockito.never()).toBean(Mockito.any(), Mockito.any(), Mockito.any());
	 
		 Mockito.verify(serialisationUtil,Mockito.never()).execute(Mockito.any(), Mockito.any(), Mockito.any());
		 
		
	 }
	 
	 @Test
	 public final void deSerializeNoState() {
		 Mockito.when(serialisationUtil.getAndIncVersion(backingBean)).thenReturn(0L);
		 Mockito.when(userModel.state(Mockito.any())).thenReturn(null);
		
		 stateAspect.deSerialize(backingBean, controller, deSerialize);
		 
		 Mockito.verify(serialisationUtil, Mockito.never()).toBean(Mockito.any(), Mockito.any(), Mockito.any());
	 
		 Mockito.verify(serialisationUtil, Mockito.never()).execute(Mockito.any(), Mockito.any(), Mockito.any());
	 }

}
