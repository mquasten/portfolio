package de.mq.portfolio.support;

import java.util.Map;

import javax.faces.context.FacesContext;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

class SimpleViewScopeImpl implements Scope {

	public final Object get(final String name, final ObjectFactory<?> objectFactory) {
		final Map<String, Object> viewMap = viewMap();

		if (viewMap.containsKey(name)) {
			return viewMap.get(name);
		} else {
			Object object = objectFactory.getObject();
			viewMap.put(name, object);

			return object;
		}
	}

	private Map<String, Object> viewMap() {

		return facesContext().getViewRoot().getViewMap();
	}

	FacesContext facesContext() {
		return FacesContext.getCurrentInstance();
	}

	public final Object remove(String name) {
		return viewMap().remove(name);
	}

	public final String getConversationId() {
		return null;
	}

	public final void registerDestructionCallback(String name, Runnable callback) {
		// Not supported
	}

	public final Object resolveContextualObject(String key) {
		return null;
	}

}