package de.mq.portfolio.support;

import java.util.Collection;

import org.springframework.beans.factory.BeanNameAware;


import de.mq.portfolio.support.SimpleCommandlineProcessorImpl.Main;

public class DummyBean implements BeanNameAware {
	
	
	
	@Main
	public void process(final Collection<String> arguments){
		System.out.println("Get the party started..." + arguments.iterator().next());
	}

	@Override
	public void setBeanName(final String name) {
		System.out.println(name);	
	}

}
