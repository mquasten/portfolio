package de.mq.portfolio.share.support;


import org.aspectj.lang.annotation.After;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class StateAspectImpl {
	
	 @After("execution(* de.mq.portfolio.share.support.SharesControllerImpl.init(..))")
	 void deSerialize()  {
		 System.out.println("get the party started....");
	 }
	 
	 @After("execution(* de.mq.portfolio.share.support.SharesControllerImpl.next(..))")
	 void serialize()  {
		 System.out.println("serialize....");
	 }

}
