package com.firecode.mqtest.disruptor;

import java.util.concurrent.TimeUnit;

import com.firecode.mqtest.disruptor.domain.OrderEvent;
import com.lmax.disruptor.WorkHandler;

/**
 * 简单消费者实现
 * @author JIANG
 */
public class WorkOrderHandler implements WorkHandler<OrderEvent>{
	
	private final Integer number;
	private final boolean sleep;
	
	public WorkOrderHandler(Integer number,boolean sleep){
		this.number = number;
		this.sleep = sleep;
	}

	@Override
	public void onEvent(OrderEvent event) throws Exception {
		System.err.println("消费者"+number+": "+event);
		event.setOrderNo(event.getOrderNo()+"__"+number);
		if(sleep){
			TimeUnit.SECONDS.sleep(3);
		}
	}
}