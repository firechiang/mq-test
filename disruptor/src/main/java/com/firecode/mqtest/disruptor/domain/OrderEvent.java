package com.firecode.mqtest.disruptor.domain;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author JIANG
 */
public class OrderEvent {

	private Object orderNo;

	private AtomicInteger count = new AtomicInteger(0);

	public OrderEvent() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Object getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(Object orderNo) {
		this.orderNo = orderNo;
	}

	public AtomicInteger getCount() {
		return count;
	}

	public void setCount(AtomicInteger count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return "OrderEvent [orderNo=" + orderNo + ", count=" + count + "]";
	}
}
