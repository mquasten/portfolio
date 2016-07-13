package de.mq.portfolio.batch.support;

import org.easyrules.annotation.Action;
import org.easyrules.annotation.Condition;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import de.mq.portfolio.batch.JobEnvironment;

public abstract class AbstractServiceRule<T> {

	protected static final String ITEMS_PARAMETER = "items";
	private final JobEnvironment jobEnvironment;
	private final Expression expression;
	private final T target;

	AbstractServiceRule(final T target, final JobEnvironment jobEnvironment, final String spEl ) {
		this.jobEnvironment=jobEnvironment;
		this.expression= new SpelExpressionParser(new SpelParserConfiguration(true,true)).parseExpression(spEl);
		this.target=target;
	
	}


	protected final <R> R executeEl(Class<? extends R> resultType) {
		final StandardEvaluationContext context = new StandardEvaluationContext(this.target);
		jobEnvironment.parameterNames().forEach(name -> context.setVariable(name, jobEnvironment.parameter(name)));
		return    (R) expression.getValue(context, resultType);
		
	}
	
	protected final JobEnvironment jobEnvironment() {
		return jobEnvironment;
	}
	
	@Condition
	public final  boolean condition() {
		return checkCondion();
	}
	
	@Action
	public final  void action()  {
		action(jobEnvironment());
	}


	protected abstract void action(final JobEnvironment jobEnvironment); 


	protected boolean checkCondion() {
		return true;
	}

}