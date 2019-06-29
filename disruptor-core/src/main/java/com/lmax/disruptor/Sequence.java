/*
 * Copyright 2012 LMAX Ltd.
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

import com.lmax.disruptor.util.Util;

import sun.misc.Unsafe;

/**
 * Sequence使用填充数据来做到独占缓存行，以消除为缓存伪共享来提高效率
 * 
 * 
 * 内存中的缓存行和伪共享简要说明
 * 1，缓存系统中是以缓存行（cache line）为单位存储
 * 2，缓存行是2的整数幂个连续字节，一般为32-256个字节
 * 3，最常见的缓存行的大小是64个或128个字节
 * 当多线程修改相互独立的变量时（前提是多变量在同一个缓存行里面），如果这些变量共享在同一个缓存行，就会无意中影响彼此的性能，这个就是伪共享
 */ 
/**
 * 用于在左边填充缓存行的7个long类型数据
 */
@SuppressWarnings("restriction")
class LhsPadding {
	protected long p1, p2, p3, p4, p5, p6, p7;
}
/**
 * LhsPadding表示在Value的的左边填充7个long类型的数据，Value占一个long类型，最后Value加上左边填充的数据就可能占一个缓存行，这样就可以消费缓存伪共享，来提高效率
 */
class Value extends LhsPadding {
	protected volatile long value;
}

/**
 * RhsPadding表示在Value的右边填充7个long类型的数据，Value加上左右两边填充的数据，最后可能占一个缓存行，这样就可以消费缓存伪共享，来提高效率
 */
class RhsPadding extends Value {
	protected long p9, p10, p11, p12, p13, p14, p15;
}

/**
 * 数据递增序号类
 * <p>
 * Concurrent sequence class used for tracking the progress of the ring buffer
 * and event processors. Support a number of concurrent operations including CAS
 * and order writes.
 *
 * <p>
 * Also attempts to be more efficient with regards to false sharing by adding
 * padding around the volatile field.
 */
@SuppressWarnings("restriction")
public class Sequence extends RhsPadding {
	static final long INITIAL_VALUE = -1L;
	private static final Unsafe UNSAFE;
	private static final long VALUE_OFFSET;

	static {
		UNSAFE = Util.getUnsafe();
		try {
			VALUE_OFFSET = UNSAFE.objectFieldOffset(Value.class.getDeclaredField("value"));
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Create a sequence initialised to -1.
	 */
	public Sequence() {
		this(INITIAL_VALUE);
	}

	/**
	 * Create a sequence with a specified initial value.
	 *
	 * @param initialValue
	 *            The initial value for this sequence.
	 */
	public Sequence(final long initialValue) {
		UNSAFE.putOrderedLong(this, VALUE_OFFSET, initialValue);
	}

	/**
	 * Perform a volatile read of this sequence's value.
	 *
	 * @return The current value of the sequence.
	 */
	public long get() {
		return value;
	}

	/**
	 * Perform an ordered write of this sequence. The intent is a Store/Store
	 * barrier between this write and any previous store.
	 *
	 * @param value
	 *            The new value for the sequence.
	 */
	public void set(final long value) {
		UNSAFE.putOrderedLong(this, VALUE_OFFSET, value);
	}

	/**
	 * Performs a volatile write of this sequence. The intent is a Store/Store
	 * barrier between this write and any previous write and a Store/Load
	 * barrier between this write and any subsequent volatile read.
	 *
	 * @param value
	 *            The new value for the sequence.
	 */
	public void setVolatile(final long value) {
		UNSAFE.putLongVolatile(this, VALUE_OFFSET, value);
	}

	/**
	 * Perform a compare and set operation on the sequence.
	 *
	 * @param expectedValue
	 *            The expected current value.
	 * @param newValue
	 *            The value to update to.
	 * @return true if the operation succeeds, false otherwise.
	 */
	public boolean compareAndSet(final long expectedValue, final long newValue) {
		return UNSAFE.compareAndSwapLong(this, VALUE_OFFSET, expectedValue, newValue);
	}

	/**
	 * Atomically increment the sequence by one.
	 *
	 * @return The value after the increment
	 */
	public long incrementAndGet() {
		return addAndGet(1L);
	}

	/**
	 * Atomically add the supplied value.
	 *
	 * @param increment
	 *            The value to add to the sequence.
	 * @return The value after the increment.
	 */
	public long addAndGet(final long increment) {
		long currentValue;
		long newValue;

		do {
			currentValue = get();
			newValue = currentValue + increment;
		} while (!compareAndSet(currentValue, newValue));

		return newValue;
	}

	@Override
	public String toString() {
		return Long.toString(get());
	}
}
