package com.firecode.mqtest.disruptor.helloword;

import com.firecode.mqtest.disruptor.domain.OrderEvent;
import com.lmax.disruptor.EventHandler;

/**
 * 事件处理器
 * @author JIANG
 */
public class OrderEventHandler implements EventHandler<OrderEvent>{

	@Override
	public void onEvent(OrderEvent event, long sequence, boolean endOfBatch) throws Exception {
		System.err.println("消费："+event.getOrderNo());
	}

}
