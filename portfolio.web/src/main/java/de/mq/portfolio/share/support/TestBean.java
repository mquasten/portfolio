package de.mq.portfolio.share.support;



import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.mq.portfolio.share.ShareService;

@Component("testBean")
@Scope("view")
public class TestBean implements Serializable {
	
	@Autowired
	private ShareService shareService;
	
	private static final long serialVersionUID = 1L;

	public TestBean() {
		System.out.println( "!!!!");
	}
	
	public final String test(final String text ) {
		System.out.println(text);
		System.out.println(shareService);
		return "Get the party Started";
	}

	
	
}
