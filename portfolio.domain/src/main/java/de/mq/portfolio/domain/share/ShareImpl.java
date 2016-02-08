package de.mq.portfolio.domain.share;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.StringUtils;

@Document(collection="share")
public class ShareImpl {
	
	@Id
	private String id;
	
	private  String code;
	
	
	private String name;
	
	private String index; 
	
	ShareImpl(String code, String name, String index) {
		
		this.code = code;
		this.name = name;
		this.index = index;
	}

	ShareImpl(String code, String name) {
		this(code,name,null);
	}

	public String name() {
		return name;
	}
	
	public String index() {
		return name;
	}
	
	public String code() {
		return code;
	}
	
	public boolean isIndex() {
		return ! StringUtils.hasText(index);
	}
	
}
