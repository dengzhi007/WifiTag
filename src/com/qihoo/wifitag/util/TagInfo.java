package com.qihoo.wifitag.util;public class TagInfo {	private String tagName;	private String ssId;	private int encryType;	private String pwd;	private String createTime = null;	private boolean mode;		public TagInfo(String tagName, String ssId, int encryType, String pwd,			String createTime, boolean mode) {		super();		this.tagName = tagName;		this.ssId = ssId;		this.encryType = encryType;		this.pwd = pwd;		this.createTime = createTime;		this.mode = mode;	}	public String getTagName() {		return tagName;	}	public void setTagName(String tagName) {		this.tagName = tagName;	}	public String getSsId() {		return ssId;	}	public void setSsId(String ssId) {		this.ssId = ssId;	}	public int getEncryType() {		return encryType;	}	public void setEncryType(int encryType) {		this.encryType = encryType;	}	public String getPwd() {		return pwd;	}	public void setPwd(String pwd) {		this.pwd = pwd;	}	public String getCreateTime() {		return createTime;	}	public void setCreateTime(String createTime) {		this.createTime = createTime;	}	public boolean isMode() {		return mode;	}	public void setMode(boolean mode) {		this.mode = mode;	}	}