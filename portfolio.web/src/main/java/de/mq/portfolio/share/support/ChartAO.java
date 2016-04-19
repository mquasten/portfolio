package de.mq.portfolio.share.support;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("chart")
@Scope("view")
public class ChartAO {
	
	static final String URL_PATTERN="http://chart.finance.yahoo.com/z?s=%s&amp;t=1y&amp;q=&amp;l=&amp;z=l&amp;a=v&amp;p=s&amp";
	
	private String code ;

	public String getCode() {
		return code;
	}
	

	public void setCode(String code) {
		this.code = code;
	}
	

	public final String  getUrl() {
		return String.format(URL_PATTERN, code);
	}
	

	
	 

}
