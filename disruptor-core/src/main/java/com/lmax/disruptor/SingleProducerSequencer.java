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

import java.util.concurrent.locks.LockSupport;

import com.lmax.disruptor.util.Util;

abstract class SingleProducerSequencerPad extends AbstractSequencer {
	protected long p1, p2, p3, p4, p5, p6, p7;

	SingleProducerSequencerPad(int bufferSize, WaitStrategy waitStrategy) {
		super(bufferSize, waitStrategy);
	}
}

abstract class SingleProducerSequencerFields extends SingleProducerSequencerPad {
	SingleProducerSequencerFields(int bufferSize, WaitStrategy waitStrategy) {
		super(bufferSize, waitStrategy);
	}

	/**
	 * Set to -1 as sequence starting point 默认数据的序号是 -1
	 */
	long nextValue = Sequence.INITIAL_VALUE;
	long cachedValue = Sequence.INITIAL_VALUE;
}

/**
 * <p>
 * Coordinator for claiming sequences for access to a data structure while
 * tracking dependent {@link Sequence}s. Not safe for use from multiple threads
 * as it does not implement any barriers.
 * </p>
 *
 * <p>
 * * Note on {@link Sequencer#getCursor()}: With this sequencer the cursor value
 * is updated after the call to {@link Sequencer#publish(long)} is made.
 * </p>
 */

public final class SingleProducerSequencer extends SingleProducerSequencerFields {
	protected long p1, p2, p3, p4, p5, p6, p7;

	/**
	 * Construct a Sequencer with the selected wait strategy and buffer size.
	 *
	 * @param bufferSize
	 *            the size of the buffer that this will sequence over.
	 * @param waitStrategy
	 *            for those waiting on sequences.
	 */
	public SingleProducerSequencer(int bufferSize, WaitStrategy waitStrategy) {
		super(bufferSize, waitStrategy);
	}

	/**
	 * @see Sequencer#hasAvailableCapacity(int)
	 */
	@Override
	public boolean hasAvailableCapacity(int requiredCapacity) {
		return hasAvailableCapacity(requiredCapacity, false);
	}

	private boolean hasAvailableCapacity(int requiredCapacity, boolean doStore) {
		long nextValue = this.nextValue;

		long wrapPoint = (nextValue + requiredCapacity) - bufferSize;
		long cachedGatingSequence = this.cachedValue;

		if (wrapPoint > cachedGatingSequence || cachedGatingSequence > nextValue) {
			if (doStore) {
				cursor.setVolatile(nextValue); // StoreLoad fence
			}

			long minSequence = Util.getMinimumSequence(gatingSequences, nextValue);
			this.cachedValue = minSequence;

			if (wrapPoint > minSequence) {
				return false;
			}
		}

		return true;
	}

	/**
	 * 获取一个环形缓冲区可用的序号，就是用来计算当前数据要存在哪个位置上（计算公式：可用序号 % 缓冲区大小 = 存储位置）
	 * @see Sequencer#next()
	 */
	@Override
	public long next() {
		return next(1);
	}

	/**
	 * 获取一个环形缓冲区可用的序号，就是用来计算当前数据要存在哪个位置上（计算公式：可用序号 % 缓冲区大小 = 存储位置）
	 * @see Sequencer#next(int)
	 */
	@Override
	public long next(int n) {
		if (n < 1) {
			throw new IllegalArgumentException("n must be > 0");
		}
        // 前一个数据序号（默认是从-1开始）
		long nextValue = this.nextValue;
        // 当前的数据序号（其实就是每来一个数据，序号就加1）
		long nextSequence = nextValue + n;
		// 记录当前的数据序号是否已经绕过了环形缓冲区，如果是负数说明没有绕过环，还在环以内。如果是正数，说明已近绕过了，已经是第N圈填充数据了
		long wrapPoint = nextSequence - bufferSize;
		// 获取前一个缓存起来的消费者最小序号
		long cachedGatingSequence = this.cachedValue;
		// 如果当前的数据序号大于缓存起来的消费者最小序号
		if (wrapPoint > cachedGatingSequence || cachedGatingSequence > nextValue) {
			cursor.setVolatile(nextValue); // StoreLoad fence
            // 数据最小序号
			long minSequence;
	        // 如果当前的数据序号大于消费者中最小序号，那么就挂起，进行自旋操作
			// 当前的数据序号值不能大于消费者中最小序号值（如果当前的数据序号大于消费者序号，说明消费者慢生产者快，生产者需要等待消费者先消费）
			// 出现以上情况主要是多个消费者
			while (wrapPoint > (minSequence = Util.getMinimumSequence(gatingSequences, nextValue))) {
				// 阻塞1纳秒
				LockSupport.parkNanos(1L); // TODO: Use waitStrategy to spin?
			}
            // 缓存最小的数据序号
			this.cachedValue = minSequence;
		}

		this.nextValue = nextSequence;

		return nextSequence;
	}

	/**
	 * @see Sequencer#tryNext()
	 */
	@Override
	public long tryNext() throws InsufficientCapacityException {
		return tryNext(1);
	}

	/**
	 * @see Sequencer#tryNext(int)
	 */
	@Override
	public long tryNext(int n) throws InsufficientCapacityException {
		if (n < 1) {
			throw new IllegalArgumentException("n must be > 0");
		}

		if (!hasAvailableCapacity(n, true)) {
			throw InsufficientCapacityException.INSTANCE;
		}

		long nextSequence = this.nextValue += n;

		return nextSequence;
	}

	/**
	 * @see Sequencer#remainingCapacity()
	 */
	@Override
	public long remainingCapacity() {
		long nextValue = this.nextValue;

		long consumed = Util.getMinimumSequence(gatingSequences, nextValue);
		long produced = nextValue;
		return getBufferSize() - (produced - consumed);
	}

	/**
	 * @see Sequencer#claim(long)
	 */
	@Override
	public void claim(long sequence) {
		this.nextValue = sequence;
	}

	/**
	 * 发布消息
	 * @see Sequencer#publish(long)
	 */
	@Override
	public void publish(long sequence) {
		// 将游标的位置设置成可用序号
		cursor.set(sequence);
		// 如果消费者在等待就唤醒
		waitStrategy.signalAllWhenBlocking();
	}

	/**
	 * @see Sequencer#publish(long, long)
	 */
	@Override
	public void publish(long lo, long hi) {
		publish(hi);
	}

	/**
	 * @see Sequencer#isAvailable(long)
	 */
	@Override
	public boolean isAvailable(long sequence) {
		return sequence <= cursor.get();
	}

	@Override
	public long getHighestPublishedSequence(long lowerBound, long availableSequence) {
		return availableSequence;
	}
}
