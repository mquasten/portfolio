package de.mq.portfolio.support;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ReflectionUtils;

import junit.framework.Assert;

public class UserModelTest {
	
	private static final String STATE_VALUE = "serialized Object";
	private static final String STATE_KEY = "view";
	private static final String ID = "19680528";
	private static final String NAME = "kylie";
	private final UserModel userModel = new UserModelImpl(NAME);
	
	@Test
	public final void getName() {
		Assert.assertEquals(NAME, userModel.getName());
	}
	
	@Test
	public final void portfolioId() {
		Assert.assertNull(userModel.getPortfolioId());
		userModel.setPortfolioId(ID);
		Assert.assertEquals(ID, userModel.getPortfolioId());
	}
	
	
	@Test
	public final void locale() {
		Assert.assertEquals(Locale.GERMAN, userModel.getLocale());
		userModel.setLocale(Locale.ENGLISH);
		Assert.assertEquals(Locale.ENGLISH, userModel.getLocale());
	}
	
	
	@Test
	public final void assignState() {
		final Map<String,String> stateMap = new HashMap<>();
		stateMap(stateMap);
		Assert.assertTrue(stateMap.isEmpty());
		userModel.assign(STATE_KEY, STATE_VALUE);
		stateMap(stateMap);
		Assert.assertEquals(1, stateMap.size());
		Assert.assertEquals(STATE_KEY, stateMap.keySet().stream().findAny().get());
		Assert.assertEquals(STATE_VALUE, stateMap.values().stream().findAny().get());
	}

	@SuppressWarnings("unchecked")
	private void stateMap(final Map<String, String> stateMap) {
		stateMap.clear();
		ReflectionUtils.doWithFields(userModel.getClass(), field -> stateMap.putAll((Map<String, String >) ReflectionTestUtils.getField(userModel, field.getName())), field -> field.getType().equals(Map.class)) ;
	}
	
	@Test
	public final void state() {
		Assert.assertNull(userModel.state(STATE_KEY));
		final Map<String,String> states = new HashMap<>();
		states.put(STATE_KEY, STATE_VALUE);
		ReflectionUtils.doWithFields(userModel.getClass(), field -> ReflectionTestUtils.setField(userModel, field.getName(), states), field -> field.getType().equals(Map.class));
	    Assert.assertEquals(STATE_VALUE, userModel.state(STATE_KEY));
	}
	
	

}
