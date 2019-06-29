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

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.lmax.disruptor.util.ThreadHints;

/**
 * Blocking strategy that uses a lock and condition variable for
 * {@link EventProcessor}s waiting on a barrier.
 * <p>
 * This strategy can be used when throughput and low-latency are not as
 * important as CPU resource.
 */
public final class BlockingWaitStrategy implements WaitStrategy {
	private final Lock lock = new ReentrantLock();
	private final Condition processorNotifyCondition = lock.newCondition();

	/**
	 * 消费者等待并获取实际可消费的序号
	 * @param sequence           消费者序号
	 * @param cursorSequence     生产者已生产的最大序号
	 * @param dependentSequence  
	 * @param barrier             
	 */
	@Override
	public long waitFor(long sequence, Sequence cursorSequence, Sequence dependentSequence, SequenceBarrier barrier)
			throws AlertException, InterruptedException {
		long availableSequence;
		// 生产者最大序号小于消费者序号（消费者速度过快，没有数据消费了，消费者进行等待）
		if (cursorSequence.get() < sequence) {
			lock.lock();
			try {
				while (cursorSequence.get() < sequence) {
					barrier.checkAlert();
					processorNotifyCondition.await();
				}
			} finally {
				lock.unlock();
			}
		}

		while ((availableSequence = dependentSequence.get()) < sequence) {
			barrier.checkAlert();
			ThreadHints.onSpinWait();
		}

		return availableSequence;
	}

	/**
	 * 如果消费者在等待就唤醒
	 */
	@Override
	public void signalAllWhenBlocking() {
		lock.lock();
		try {
			processorNotifyCondition.signalAll();
		} finally {
			lock.unlock();
		}
	}

	@Override
	public String toString() {
		return "BlockingWaitStrategy{" + "processorNotifyCondition=" + processorNotifyCondition + '}';
	}
}
