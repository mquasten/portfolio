package de.mq.portfolio.util.support;

import java.util.Map;

import javax.faces.context.FacesContext;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

public class SimpleViewScopeImpl implements Scope {
	 
   public Object get(String name, ObjectFactory<?> objectFactory) {
       final Map<String,Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();

       if(viewMap.containsKey(name)) {
           return viewMap.get(name);
       } else {
           Object object = objectFactory.getObject();
           viewMap.put(name, object);

           return object;
       }
   }

   public Object remove(String name) {
       return FacesContext.getCurrentInstance().getViewRoot().getViewMap().remove(name);
   }

   public String getConversationId() {
       return null;
   }

   public void registerDestructionCallback(String name, Runnable callback) {
       //Not supported
   }

   public Object resolveContextualObject(String key) {
       return null;
   }

	
}
