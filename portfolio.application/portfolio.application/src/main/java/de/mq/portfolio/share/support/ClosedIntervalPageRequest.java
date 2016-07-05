package de.mq.portfolio.share.support;

import org.springframework.data.domain.AbstractPageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class ClosedIntervalPageRequest extends AbstractPageRequest {

	private final Sort sort; 
	
	private final long counter;

	private static final long serialVersionUID = 1L;
	
	public ClosedIntervalPageRequest(final int size, final Sort sort, final long counter) {
		this(0,size,sort,counter);
		
	} 
	
	private ClosedIntervalPageRequest(final int page, final int size, final Sort sort, final long counter) {
		super(page, size);
		this.sort=sort;
		this.counter=counter;
	}



	@Override
	public Sort getSort() {
		return sort;
	}

	@Override
	public Pageable next() {
		int page = hasNext() ? getPageNumber() +1 : maxPage();
		return new ClosedIntervalPageRequest(page, getPageSize(), sort, counter);
		
	}

	@Override
	public Pageable previous() {
		int page = hasPrevious() ? getPageNumber() -1  : 0;
		return new ClosedIntervalPageRequest(page ,getPageSize(), sort, counter);
		
	}

	@Override
	public Pageable first() {
		return  new ClosedIntervalPageRequest(0, getPageSize(), sort, counter);
	}

	
	public Pageable last() {
		return new ClosedIntervalPageRequest(maxPage(), getPageSize(), sort, counter);
	}

	public boolean  hasNext() {
		return  getPageNumber() < maxPage();	
	}

	public int maxPage() {
		if(counter==0 ) {
			return 0; 
		}
		
		return Double.valueOf(Math.ceil( (double) counter / (double) getPageSize())).intValue() -1 ;
		
	}
	
	public boolean isFirst() {
		return getPageNumber()==0;
	}
	
	public boolean isLast() {
		return getPageNumber()==maxPage();
	}
	

}
