package de.mq.portfolio.share.support;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import de.mq.portfolio.share.Share;
import de.mq.portfolio.share.TimeCourse;

@Component("sharesSearch")
@Scope("view")
public class SharesSearchAO  implements Serializable{
	

	private static final long serialVersionUID = 1L;
	
	private final Collection<Entry<Share,TimeCourse>> timeCourses = new ArrayList<>();
	
	private final Collection<String> indexes = new ArrayList<>();
	
	private TimeCourse selectedTimeCourse;
	
	
	private Pageable pageable; 

	

	private String selectedSort="id";

	


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
	
	public final String getPageInfo() {
	
		if( pageable == null){
			return null ; 
		}
	   return  (1+pageable.getPageNumber()) + "/" +  + (1+((ClosedIntervalPageRequest) pageable).maxPage());
	}
	
	public final Collection<Entry<Share, TimeCourse>> getTimeCourses() {
		return timeCourses;
	}

	public final void  setTimeCorses(final Collection<TimeCourse> timeCourses) {
		this.timeCourses.clear();
		this.timeCourses.addAll(timeCourses.stream().map(tc -> new AbstractMap.SimpleImmutableEntry<>(tc.share(), tc)).collect(Collectors.toList()));
	}
	
	public final Collection<String> getIndexes() {
		return indexes;
	}
	
	public final void  setIndexes(final Collection<String> indexes) {
		this.indexes.clear();
		this.indexes.addAll(indexes);
	
	}
	
	
	public String getSelectedSort() {
		return selectedSort;
	}

	public void setSelectedSort(final String selectedSort) {
		this.selectedSort=selectedSort;
	}

}
