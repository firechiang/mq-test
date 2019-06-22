package com.firecode.mqtest.disruptor;

import java.util.concurrent.TimeUnit;

import com.firecode.mqtest.disruptor.domain.OrderEvent;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

/**
 * 简单消费者实现
 * @author JIANG
 */
public class OrderHandler  implements EventHandler<OrderEvent>,WorkHandler<OrderEvent>{
	
	private final Integer number;
	private final boolean sleep;
	
	public OrderHandler(Integer number,boolean sleep){
		this.number = number;
		this.sleep = sleep;
	}

	@Override
	public void onEvent(OrderEvent event, long sequence, boolean endOfBatch) throws Exception {
		System.err.println("消费者"+number+": "+event);
		event.setOrderNo(event.getOrderNo()+"__"+number);
		if(sleep){
			TimeUnit.SECONDS.sleep(3);
		}
	}

	@Override
	public void onEvent(OrderEvent event) throws Exception {
		// TODO Auto-generated method stub
		
	}
}