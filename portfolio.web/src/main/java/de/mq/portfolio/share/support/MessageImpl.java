package de.mq.portfolio.share.support;

import java.util.HashMap;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import de.mq.portfolio.support.UserModel;

@Component("message")
public class MessageImpl extends HashMap<String, String> {

	static final String UNDEFINED_MESSAGE_PATTERN = "?%s?";

	private static final long serialVersionUID = 1L;

	private final MessageSource messageSource;

	@Autowired
	public MessageImpl(final MessageSource messageSource) {
		this.messageSource = messageSource;
	}


	@Override
	public String get(Object key) {
		try {
			return messageSource.getMessage((String) key, null, locale());
		} catch (final NoSuchMessageException me) {
			return String.format(UNDEFINED_MESSAGE_PATTERN, key);
		}

	}


	Locale locale() {
		if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserModel) {
			return ((UserModel)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getLocale();
			
		}
		return Locale.GERMAN;
	}

}
