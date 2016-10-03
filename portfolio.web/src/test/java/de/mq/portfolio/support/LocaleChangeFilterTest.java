package de.mq.portfolio.support;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ReflectionUtils;

import junit.framework.Assert;


public class LocaleChangeFilterTest {
	
	private static final String LANG_DE = "de";

	private static final String LANG_EN = "en";

	private final Filter filter = Mockito.mock(AbstractLocaleChangeFilter.class, Mockito.CALLS_REAL_METHODS);
	
	private final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
	
	private final HttpServletResponse  response = Mockito.mock(HttpServletResponse .class);
	
	private final FilterChain filterChain = Mockito.mock(FilterChain.class);
	
	private final UserModel userModel = Mockito.mock(UserModel.class);
	
	@Before
	public void setup() {
		ReflectionUtils.doWithFields(AbstractLocaleChangeFilter.class, field -> ReflectionTestUtils.setField(filter, field.getName(), Arrays.asList(LANG_EN)), field -> field.getType().equals(Collection.class)); 
	
		Mockito.when(request.getParameter(AbstractLocaleChangeFilter.PARAM_LANGUAGE)).thenReturn(LANG_EN);
		
		Mockito.doAnswer(a -> {
			return userModel;
		}).when((AbstractLocaleChangeFilter)filter).userModel();
	}
	
	
	@SuppressWarnings("unchecked")
	@Test
	public final void languages() {
		final Filter filter = new AbstractLocaleChangeFilter() {

			@Override
			UserModel userModel() {
				return null;
			} };
			
		final Collection<String>	languages = new ArrayList<>();
		ReflectionUtils.doWithFields(AbstractLocaleChangeFilter.class, field -> languages.addAll( (Collection<String>) ReflectionTestUtils.getField(filter,field.getName())), field -> field.getType().equals(Collection.class));
	    Assert.assertTrue(languages.size()>0);
	    Assert.assertEquals(Arrays.asList(Locale.getISOLanguages()), languages);
	}
	
	
	
	@Test
	public final void doFilterInternal() throws IOException, ServletException {
	    filter.doFilter(request, response, filterChain);
	    
	    Mockito.verify(userModel).setLocale(Locale.ENGLISH);
	    Mockito.verify(filterChain).doFilter(request, response);
	}
	
	@Test
	public final void doFilterInternalBadLanguage() throws IOException, ServletException {
		Mockito.when(request.getParameter(AbstractLocaleChangeFilter.PARAM_LANGUAGE)).thenReturn(LANG_DE);
		
		filter.doFilter(request, response, filterChain);
		 Mockito.verifyZeroInteractions(userModel);
		 Mockito.verify(filterChain).doFilter(request, response);
	}



	@Test
	public final void doFilterInternalMissingLanguage() throws IOException, ServletException {
		Mockito.when(request.getParameter(AbstractLocaleChangeFilter.PARAM_LANGUAGE)).thenReturn(null);
	    filter.doFilter(request, response, filterChain);
	    
	    Mockito.verifyZeroInteractions(userModel);
	    Mockito.verify(filterChain).doFilter(request, response);
	}

}
