package de.mq.portfolio.share.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.mq.portfolio.share.Data;

@Component("chart")
@Scope("view")
public class ChartAO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static final String URL_PATTERN="http://chart.finance.yahoo.com/z?s=%s&amp;t=1y&amp;q=&amp;l=&amp;z=l&amp;a=v&amp;p=s&amp";
	
	private String code ;
	
	private String wkn;
	
	
	


	private  List<Data> dividends = new ArrayList<>();

	
	




	public static String getUrlPattern() {
		return URL_PATTERN;
	}
	



	public List<Data> getDividends() {
		return dividends;
	}
	
	public void setDividends(final List<Data> dividends) {
		this.dividends = dividends;
	}
	


	public String getCode() {
		return code;
	}
	

	public void setCode(String code) {
		this.code = code;
	}
	

	public final String  getUrl() {
		return String.format(URL_PATTERN, code);
	}
	

	public String getWkn() {
		return wkn;
	}
	




	public void setWkn(String wkn) {
		this.wkn = wkn;
	}
	 

}
