package de.mq.portfolio.support;

import java.util.Map;

import org.springframework.expression.Expression;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import de.mq.portfolio.batch.Rule;

abstract class AbstractServiceRule<T> implements Rule  {

	protected static final String ITEMS_PARAMETER = "items";
	private final Expression expression;
	private final T target;
	


	AbstractServiceRule(final T target, final String spEl) {
		this.expression = new SpelExpressionParser(new SpelParserConfiguration(true, true)).parseExpression(spEl);
		this.target = target;
		
	}

	@SuppressWarnings("unchecked")
	protected final <R> R executeEl(final Map<String,Object> parameters) {
		final StandardEvaluationContext context = new StandardEvaluationContext(this.target);
		parameters.entrySet().stream().forEach(entry -> context.setVariable(entry.getKey(), entry.getValue()));

		return (R) expression.getValue(context);

	}

	protected abstract void action(final Map<String,Object> parameters);

	protected boolean checkCondion() {
		return true;
	}

	

	

	/* (non-Javadoc)
	 * @see de.mq.portfolio.support.Rule#getName()
	 */
	@Override
	public final String getName() {
		return String.format("%s::%s", target.getClass().getSimpleName(), expression.getExpressionString());
	}

	
	/* (non-Javadoc)
	 * @see de.mq.portfolio.support.Rule#evaluate()
	 */

	@Override
	public final boolean evaluate() {
		return checkCondion();
	}

	/*
	 * (non-Javadoc)
	 * @see de.mq.portfolio.batch.Rule#execute(java.util.Map)
	 */
	@Override
	public final void execute(final Map<String,Object> parameters)  {
		action(parameters);

	}

	

	

	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof Rule)) {
			return super.equals(obj);
		}
		final Rule other = (Rule) obj;
		return getName().equals(other.getName());
	}

}