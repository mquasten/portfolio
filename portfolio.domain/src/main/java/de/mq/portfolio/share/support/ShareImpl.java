package de.mq.portfolio.share.support;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.StringUtils;

import de.mq.portfolio.share.Share;

@Document(collection = "share")
class ShareImpl implements Share {

	@Id
	private String id;

	@Indexed( unique=true)
	private String code;

	private String name;

	private String index;

	ShareImpl(final String code, final String name, final String index) {
		this.code = code;
		this.name = name;
		this.index = index;
	}

	ShareImpl(final String code, final String name) {
		this(code, name, null);
	}

	ShareImpl(final String code) {
		this(code, null, null);
	}

	@SuppressWarnings("unused")
	private ShareImpl() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.portfolio.share.support.support.Share#name()
	 */
	@Override
	public String name() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.portfolio.share.support.support.Share#index()
	 */
	@Override
	public String index() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.portfolio.share.support.support.Share#code()
	 */
	@Override
	public String code() {
		return code;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.portfolio.share.support.support.Share#isIndex()
	 */
	@Override
	public boolean isIndex() {
		return !StringUtils.hasText(index);
	}

}
