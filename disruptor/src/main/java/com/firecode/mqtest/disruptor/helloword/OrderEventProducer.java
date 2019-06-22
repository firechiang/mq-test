package com.firecode.mqtest.disruptor.helloword;

import java.nio.ByteBuffer;

import com.firecode.mqtest.disruptor.domain.OrderEvent;
import com.lmax.disruptor.RingBuffer;

/**
 * 生产者
 * 
 * @author JIANG
 */
public class OrderEventProducer {

	private RingBuffer<OrderEvent> ringBuffer;

	public OrderEventProducer(RingBuffer<OrderEvent> ringBuffer) {
		this.ringBuffer = ringBuffer;
	}

	public void addData(ByteBuffer bf) {
		// 在生产者发送消息时。首先需要从 ringBuffer 里面获取到一个可用的序号
		long sequence = ringBuffer.next();
		try {
			// 根据可用序号找到对应元素（注意：这个元素其实就是调用 OrderEventFactory.newInstance() 创建的）
			OrderEvent orderEvent = ringBuffer.get(sequence);
			orderEvent.setOrderNo(bf.getLong(OrderEventMain.index));
		} finally {
			// 提交数据
			ringBuffer.publish(sequence);
		}
	}

}
