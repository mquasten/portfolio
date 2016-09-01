package de.mq.portfolio.spring.support.mock;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import de.mq.portfolio.exchangerate.ExchangeRate;

@Service()
@Scope(scopeName="prototype")
public abstract class AbstractServiceWithoutLookup {
	abstract ExchangeRate axhangeRate();

}
