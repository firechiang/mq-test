/*
 * Copyright 2011 LMAX Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lmax.disruptor;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 单消费者所使用的处理实现
 * Convenience class for handling the batching semantics of consuming entries
 * from a {@link RingBuffer} and delegating the available events to an
 * {@link EventHandler}.
 * <p>
 * If the {@link EventHandler} also implements {@link LifecycleAware} it will be
 * notified just after the thread is started and just before the thread is
 * shutdown.
 *
 * @param <T> event implementation storing the data for sharing during exchange or parallel coordination of an event.
 */
public final class BatchEventProcessor<T> implements EventProcessor {
	private static final int IDLE = 0;
	private static final int HALTED = IDLE + 1;
	private static final int RUNNING = HALTED + 1;

	// 判断是否在运行
	private final AtomicInteger running = new AtomicInteger(IDLE);
	// 错误处理器
	private ExceptionHandler<? super T> exceptionHandler = new FatalExceptionHandler();
	// 根据数据序号获取实际数据
	private final DataProvider<T> dataProvider;
	// 序号栅栏（序号协调类）
	private final SequenceBarrier sequenceBarrier;
	// 消费者接口实现
	private final EventHandler<? super T> eventHandler;
	// 生产者已生产的最大数据序号
	private final Sequence sequence = new Sequence(Sequencer.INITIAL_CURSOR_VALUE);
	// 消费超时处理
	private final TimeoutHandler timeoutHandler;
	private final BatchStartAware batchStartAware;

	/**
	 * Construct a {@link EventProcessor} that will automatically track the
	 * progress by updating its sequence when the
	 * {@link EventHandler#onEvent(Object, long, boolean)} method returns.
	 * @param dataProvider to which events are published.
	 * @param sequenceBarrier on which it is waiting.
	 * @param eventHandler is the delegate to which events are dispatched.
	 *            
	 */
	public BatchEventProcessor(final DataProvider<T> dataProvider, final SequenceBarrier sequenceBarrier,
			final EventHandler<? super T> eventHandler) {
		this.dataProvider = dataProvider;
		this.sequenceBarrier = sequenceBarrier;
		this.eventHandler = eventHandler;

		if (eventHandler instanceof SequenceReportingEventHandler) {
			((SequenceReportingEventHandler<?>) eventHandler).setSequenceCallback(sequence);
		}

		batchStartAware = (eventHandler instanceof BatchStartAware) ? (BatchStartAware) eventHandler : null;
		timeoutHandler = (eventHandler instanceof TimeoutHandler) ? (TimeoutHandler) eventHandler : null;
	}

	@Override
	public Sequence getSequence() {
		return sequence;
	}

	@Override
	public void halt() {
		running.set(HALTED);
		sequenceBarrier.alert();
	}

	@Override
	public boolean isRunning() {
		return running.get() != IDLE;
	}

	/**
	 * Set a new {@link ExceptionHandler} for handling exceptions propagated out
	 * of the {@link BatchEventProcessor}
	 *
	 * @param exceptionHandler
	 *            to replace the existing exceptionHandler.
	 */
	public void setExceptionHandler(final ExceptionHandler<? super T> exceptionHandler) {
		if (null == exceptionHandler) {
			throw new NullPointerException();
		}

		this.exceptionHandler = exceptionHandler;
	}

	/**
	 * It is ok to have another thread rerun this method after a halt().
	 *
	 * @throws IllegalStateException
	 *             if this object instance is already running in a thread
	 */
	@Override
	public void run() {
		// 如果消费任务没有执行，就设置成执行状态
		if (running.compareAndSet(IDLE, RUNNING)) {
			// 清空序号栅栏
			sequenceBarrier.clearAlert();
            // 唤醒线程，让其开始执行
			notifyStart();
			try {
				if (running.get() == RUNNING) {
					processEvents();
				}
			} finally {
				notifyShutdown();
				running.set(IDLE);
			}
		// 如果任务已经运行了抛异常	
		} else {
			// This is a little bit of guess work. The running state could of
			// changed to HALTED by
			// this point. However, Java does not have compareAndExchange which
			// is the only way
			// to get it exactly correct.
			if (running.get() == RUNNING) {
				throw new IllegalStateException("Thread is already running");
			} else {
				earlyExit();
			}
		}
	}

	/**
	 * 实际消费数据实现
	 */
	private void processEvents() {
		T event = null;
		// 消费者想消费的序号
		long nextSequence = sequence.get() + 1L;

		while (true) {
			try {
				// 等待并获取真实的生产者已生产的最大序号
				final long availableSequence = sequenceBarrier.waitFor(nextSequence);
				if (batchStartAware != null) {
					batchStartAware.onBatchStart(availableSequence - nextSequence + 1);
				}
                // 消费者可用消费序号 小于等于 生产者已生产的最大序号，将所有待消费的数据都消费完
				while (nextSequence <= availableSequence) {
					// 获取待消费的数据
					event = dataProvider.get(nextSequence);
					// 消费
					eventHandler.onEvent(event, nextSequence, nextSequence == availableSequence);
					nextSequence++;
				}
                // 设置消费者下一个想要消费的序号
				sequence.set(availableSequence);
			} catch (final TimeoutException e) {
				notifyTimeout(sequence.get());
			} catch (final AlertException ex) {
				if (running.get() != RUNNING) {
					break;
				}
			} catch (final Throwable ex) {
				exceptionHandler.handleEventException(ex, nextSequence, event);
				sequence.set(nextSequence);
				nextSequence++;
			}
		}
	}

	private void earlyExit() {
		notifyStart();
		notifyShutdown();
	}

	private void notifyTimeout(final long availableSequence) {
		try {
			if (timeoutHandler != null) {
				timeoutHandler.onTimeout(availableSequence);
			}
		} catch (Throwable e) {
			exceptionHandler.handleEventException(e, availableSequence, null);
		}
	}

	/**
	 * Notifies the EventHandler when this processor is starting up
	 */
	private void notifyStart() {
		if (eventHandler instanceof LifecycleAware) {
			try {
				((LifecycleAware) eventHandler).onStart();
			} catch (final Throwable ex) {
				exceptionHandler.handleOnStartException(ex);
			}
		}
	}

	/**
	 * Notifies the EventHandler immediately prior to this processor shutting
	 * down
	 */
	private void notifyShutdown() {
		if (eventHandler instanceof LifecycleAware) {
			try {
				((LifecycleAware) eventHandler).onShutdown();
			} catch (final Throwable ex) {
				exceptionHandler.handleOnShutdownException(ex);
			}
		}
	}
}