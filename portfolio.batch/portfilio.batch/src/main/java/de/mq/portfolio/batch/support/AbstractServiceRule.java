package de.mq.portfolio.batch.support;




import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

import org.easyrules.annotation.Action;
import org.easyrules.annotation.Condition;
import org.easyrules.api.Rule;

import org.springframework.expression.Expression;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import org.springframework.util.ReflectionUtils;

import de.mq.portfolio.batch.JobEnvironment;

public abstract class AbstractServiceRule<T>  implements Comparable<Rule>{





	protected static final String ITEMS_PARAMETER = "items";
	private final JobEnvironment jobEnvironment;
	private final Expression expression;
	private final T target;

	private final int priority; 

	AbstractServiceRule(final T target, final JobEnvironment jobEnvironment, final String spEl, int priority ) {
		this.jobEnvironment=jobEnvironment;
		this.expression= new SpelExpressionParser(new SpelParserConfiguration(true,true)).parseExpression(spEl);
		this.target=target;
		this.priority=priority;
	}


	@SuppressWarnings("unchecked")
	protected final <R> R executeEl() {
		final StandardEvaluationContext context = new StandardEvaluationContext(this.target);
		jobEnvironment.parameterNames().forEach(name -> context.setVariable(name, jobEnvironment.parameters(name)));
		return    (R) expression.getValue(context);
		
	}
	
	
	
	protected abstract void action(final JobEnvironment jobEnvironment); 


	protected boolean checkCondion() {
		return true;
	}
	
	
	
	protected  int priority()  {
		return priority;
	}
	



	protected final <R> R executeEl(Class<? extends R> resultType) {
		final StandardEvaluationContext context = new StandardEvaluationContext(this.target);
		jobEnvironment.parameterNames().forEach(name -> context.setVariable(name, jobEnvironment.parameters(name)));
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


	public final int getPriority() {
		return priority();
	}
	


	@Override
	public final int compareTo(final Rule o) {
		return  Math.round(Math.signum(getPriority()-  deProxyMize(o).getPriority()));
			
	}


	@SuppressWarnings("unchecked")
	private AbstractServiceRule<T> deProxyMize(final Rule o) {
		final Object InvocationHandler = Proxy.getInvocationHandler(o);
		final Field field  = ReflectionUtils.findField(InvocationHandler.getClass(),"target");
		field.setAccessible(true);		
		return  (AbstractServiceRule<T>) ReflectionUtils.getField(field, InvocationHandler);
		
	}


	@Override
	public String toString() {
		return String.format( "%s::%s" , target.getClass().getSimpleName(), expression.getExpressionString());
	} 
	
	


}