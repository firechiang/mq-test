package com.firecode.mqtest.disruptor;

import com.firecode.mqtest.disruptor.domain.OrderEvent;
import com.lmax.disruptor.ExceptionHandler;

/**
 * 错误处理器
 */
public class OrderExceptionHandler implements ExceptionHandler<OrderEvent>{

	/**
	 * 消费时错误处理
	 */
	@Override
	public void handleEventException(Throwable ex, long sequence, OrderEvent event) {
		System.err.println("消费时出项错误了："+ex);
	}

	/**
	 * 启动时错误处理
	 */
	@Override
	public void handleOnStartException(Throwable ex) {
		System.err.println("启动时出项错误了："+ex);
	}

	/**
	 *关闭时错误处理
	 */
	@Override
	public void handleOnShutdownException(Throwable ex) {
		System.err.println("关闭时出项错误了："+ex);
	}

}
