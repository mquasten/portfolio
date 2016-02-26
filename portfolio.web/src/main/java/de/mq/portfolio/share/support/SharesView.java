package de.mq.portfolio.share.support;

import java.io.Serializable;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.mq.portfolio.share.Share;

@Component("sharesView")
@Scope("view")
public class SharesView  implements Serializable{
	

	private static final long serialVersionUID = 1L;
	private Share selectedShare;
	
	
	private String name;
	
	

	private String code;
	
	private String index; 

	public final Share getSelectedShare() {
		return selectedShare;
	}

	public final void setSelectedShare(final Share selectedShare) {
		this.selectedShare = selectedShare;
	}
	
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

}
