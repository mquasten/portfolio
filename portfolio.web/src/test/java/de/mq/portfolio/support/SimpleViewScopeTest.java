package de.mq.portfolio.support;

import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.ObjectFactory;

import org.junit.Assert;

public class SimpleViewScopeTest {
	
	
	private static final String BEAN_NAME = "userModel";

	private UserModel userModel = Mockito.mock(UserModel.class);
	
	private SimpleViewScopeImpl viewScope =   Mockito.mock(SimpleViewScopeImpl.class, Mockito.CALLS_REAL_METHODS);
	
	@SuppressWarnings("unchecked")
	private final ObjectFactory<UserModel> objectFactory = Mockito.mock(ObjectFactory.class);
	
	private final Map<String,Object> viewMap = new HashMap<>();
	
	private FacesContext facesContext = Mockito.mock(FacesContext.class);
	
	
	private UIViewRoot viewRoot = Mockito.mock(UIViewRoot.class);
	
	@Before
	public final void setup() {
		
		Mockito.when(facesContext.getViewRoot()).thenReturn(viewRoot);
		Mockito.when(viewRoot.getViewMap()).thenReturn(viewMap);
		Mockito.doAnswer(a -> {
			return facesContext;
			
		}).when(viewScope).facesContext();
	}
	
	@Test
	public final void get() {
		viewMap.put(BEAN_NAME, userModel);
		Assert.assertEquals(userModel, viewScope.get(BEAN_NAME, objectFactory));
		
		Mockito.verifyZeroInteractions(objectFactory);
	}
	
	@Test
	public final void getNotExists() {
		Assert.assertTrue(viewMap.isEmpty());
		Mockito.when(objectFactory.getObject()).thenReturn(userModel);
		Assert.assertEquals(userModel, viewScope.get(BEAN_NAME, objectFactory));
		Assert.assertEquals(userModel, viewMap.get(BEAN_NAME));
	}

	
	@Test
	public final void remove() {
		viewMap.put(BEAN_NAME, userModel);
		viewScope.remove(BEAN_NAME);
		Assert.assertTrue(viewMap.isEmpty());
	}
	
	@Test
	public final void trash() {
		Assert.assertNull(viewScope.getConversationId());
		Assert.assertNull(viewScope.resolveContextualObject(null));
		viewScope.registerDestructionCallback(null,null);
	}
	
	@Test
	public final void facesContext() {
		
		Assert.assertNull(new SimpleViewScopeImpl().facesContext());
	}
}
