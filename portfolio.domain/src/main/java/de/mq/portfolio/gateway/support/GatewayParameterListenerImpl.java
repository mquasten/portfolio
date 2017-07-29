package de.mq.portfolio.gateway.support;

import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterConvertEvent;
import org.springframework.stereotype.Component;

@Component
public class GatewayParameterListenerImpl extends AbstractMongoEventListener<GatewayParameterImpl> {

	@Override
	public void onAfterConvert(final AfterConvertEvent<GatewayParameterImpl> event) {
		event.getSource().initParameters();
	}

}
