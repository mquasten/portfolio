package de.mq.portfolio.share.support;

import java.io.Serializable;

import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;

@Component("sharesSearch")
@Scope("view")
public class SharesSearchAO  implements Serializable{
	

	private static final long serialVersionUID = 1L;
	private TimeCourse selectedTimeCourse;
	
	
	private Pageable pageable; 

	

	

	private String name;
	
	

	private String code;
	
	private String index; 

	
	
	public final Share getSearch() {
		return new ShareImpl(code,name,index);
		
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}
	
	public TimeCourse getSelectedTimeCourse() {
		return selectedTimeCourse;
	}

	public void setSelectedTimeCourse(TimeCourse selectedTimeCourse) {
		this.selectedTimeCourse = selectedTimeCourse;
	}
	
	public Pageable getPageable() {
		return pageable;
	}

	public void setPageable(Pageable pageable) {
		this.pageable = pageable;
	}
	

}
