package de.mq.portfolio.support;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;


import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.web.filter.OncePerRequestFilter;

abstract class AbstractLocaleChangeFilter  extends OncePerRequestFilter {

	static final String PARAM_LANGUAGE = "language";
	final Collection<String> languages =  Arrays.asList(Locale.getISOLanguages());
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		
		final String language = request.getParameter(PARAM_LANGUAGE);
		
		if( language==null){
			filterChain.doFilter(request, response);
			return;
		}
		if( languages.contains(language.trim().toLowerCase())) {
		    userModel().setLocale(new Locale(language.trim().toLowerCase()));
		}
		
		filterChain.doFilter(request, response);
		
	}
	
	@Lookup
	abstract UserModel userModel(); 

}
