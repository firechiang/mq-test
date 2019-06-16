package com.firecode.mqtest.disruptor.helloword;

import com.firecode.mqtest.disruptor.domain.OrderEvent;
import com.lmax.disruptor.EventFactory;

/**
 * 事件生产工厂
 * @author JIANG
 */
public class OrderEventFactory implements EventFactory<OrderEvent>{

	@Override
	public OrderEvent newInstance() {
		
		return new OrderEvent();
	}

}
