package com.firecode.mqtest.rocketmq.domain;

/**
 * 消息体
 * @author JIANG
 */
public class MsgContent {
	
	private String name;
	
	private Long time;
	
	private String desc;
	

	public MsgContent() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public MsgContent(String name, Long time, String desc) {
		super();
		this.name = name;
		this.time = time;
		this.desc = desc;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public String getDesc() {
		return desc;
	}
	
	public void setDesc(String desc) {
		this.desc = desc;
	}

	@Override
	public String toString() {
		return "MsgContent [name=" + name + ", time=" + time + ", desc=" + desc + "]";
	}
}
