package de.mq.portfolio.batch.support;

import java.util.HashMap;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

import de.mq.portfolio.batch.JobContent;
import de.mq.portfolio.batch.MethodParameterInjection;

class SimpleDeciderImpl extends AbstractServiceAdapter implements JobExecutionDecider {

	protected SimpleDeciderImpl(final MethodParameterInjection<String> methodParameterInjection) {
		super(methodParameterInjection, new JobContentImpl<String>(), new HashMap<>());
	}

	protected SimpleDeciderImpl(final MethodParameterInjection<String> methodParameterInjection, final JobContent<String> content) {
		super(methodParameterInjection, content, new HashMap<>());

	}

	@Override
	public FlowExecutionStatus decide(final JobExecution jobExecution, final StepExecution stepExecution) {

		/*
		 * Only you and your hand ...
		 */
		beforeStep(stepExecution);

		final ExitStatus exitStatus = stepExecution.getExitStatus();

		putItem(exitStatus.getExitCode());
		final Object result = super.invokeMethod();

		if (result instanceof String) {
			return new FlowExecutionStatus((String) result);

		}
		if (result instanceof FlowExecutionStatus) {
			return (FlowExecutionStatus) result;
		}

		if (result instanceof ExitStatus) {
			return new FlowExecutionStatus(((ExitStatus) result).getExitCode());
		}

		if (result == null) {
			return new FlowExecutionStatus(exitStatus.getExitCode());
		}

		throw new IllegalArgumentException(String.format("Unable to convert value %s (type: %s) to FlowExecutionStatus.", result, result.getClass().getSimpleName()));
	}
}