package de.mq.portfolio.support;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;

class LocaleChangeFilter  extends OncePerRequestFilter {

	final List<String> languages =  Arrays.asList(Locale.getISOLanguages());
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		
		final String language = request.getParameter("language");
		
		if( language==null){
			filterChain.doFilter(request, response);
			return;
		}
		if( languages.contains(language.trim().toLowerCase())) {
			WebApplicationContextUtils.getWebApplicationContext(request.getServletContext()).getBean(UserModel.class).setLocale(new Locale(language.trim().toLowerCase()));
		}
		
		filterChain.doFilter(request, response);
		
	}

}
