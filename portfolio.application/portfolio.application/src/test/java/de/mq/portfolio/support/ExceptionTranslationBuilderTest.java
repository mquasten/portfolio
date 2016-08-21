package de.mq.portfolio.support;

import java.io.ByteArrayInputStream;
import java.io.Closeable;

import org.junit.Test;



public class ExceptionTranslationBuilderTest {
	
	@Test
	public final void testit(){
	
		 String result =  new ExceptionTranslationBuilderImpl<String, Closeable>().withStatement(() -> "Kylie is nice and ...").translate();
		
		
		  
		  System.out.println(result);
	}
	
	
	@Test
	public final void testitWithResource(){
		final String result = new ExceptionTranslationBuilderImpl<String,ByteArrayInputStream>().withStatement(r -> {
			byte[] buffer = new byte[50];
			r.read(buffer);
			return new String(buffer);
			
		}).withResource(()-> new ByteArrayInputStream("Kylie is nice and ...".getBytes())).translate();
		System.out.println(result +"*");
	}
	

}
