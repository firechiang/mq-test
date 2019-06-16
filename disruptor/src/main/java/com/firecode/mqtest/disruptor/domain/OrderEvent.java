package com.firecode.mqtest.disruptor.domain;

/**
 * @author JIANG
 */
public class OrderEvent {
	
	private Long orderNo;
	
	public OrderEvent() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Long getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(Long orderNo) {
		this.orderNo = orderNo;
	}
}
