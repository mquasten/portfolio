package de.mq.portfolio.support;




import org.easyrules.api.Rule;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import de.mq.portfolio.batch.JobEnvironment;

 abstract class AbstractServiceRule<T>  implements Rule , Comparable<Rule>{



	protected static final String ITEMS_PARAMETER = "items";
	private final Expression expression;
	private final T target;
	private final JobEnvironment jobEnvironment;

	private final int priority; 

	AbstractServiceRule(final T target, final String spEl, final JobEnvironment jobEnvironment, int priority ) {
		this.expression= new SpelExpressionParser(new SpelParserConfiguration(true,true)).parseExpression(spEl);
		this.target=target;
		this.priority=priority;
		this.jobEnvironment=jobEnvironment;
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
	
	
	
	
	


	public final int getPriority() {
		return priority();
	}

/*	@Override
	public final int compareTo(final Rule o) {
		return  Math.round(Math.signum(getPriority()-  deProxyMize(o).getPriority()));
			
	} 


	@SuppressWarnings("unchecked")
	private AbstractServiceRule<T> deProxyMize(final Rule o) {
		final Object InvocationHandler = Proxy.getInvocationHandler(o);
		final Field field  = ReflectionUtils.findField(InvocationHandler.getClass(),"target");
		field.setAccessible(true);		
		return  (AbstractServiceRule<T>) ReflectionUtils.getField(field, InvocationHandler);
		
	} */


	


	@Override
	public final  String getName() {
		return String.format( "%s::%s" , target.getClass().getSimpleName(), expression.getExpressionString());
	}


	@Override
	public final  String getDescription() {
		return  String.format( "class=%s, name=%s, priority=%s" , getClass().getSimpleName(), getName(), getPriority());
	}


	@Override
	public final boolean evaluate() {
		return checkCondion();
	}


	@Override
	public final  void execute() /*throws Exception*/ {
		action(jobEnvironment);
		
	} 
	
	@Override
	public final int compareTo(Rule rule) {
		
		return getPriority() - rule.getPriority();
		
	}

	@Override
	public final String toString() {
		return getDescription();
	}



	@Override
	public int hashCode() {
		return  getName().hashCode() + priority;
	}


	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof Rule)) {
			return super.equals(obj);
		}
		final Rule other = (Rule) obj;
		return getName().equals(other.getName()) && getPriority()==other.getPriority(); 
		
	}
	

}