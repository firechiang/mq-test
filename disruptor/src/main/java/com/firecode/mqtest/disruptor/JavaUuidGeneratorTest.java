package com.firecode.mqtest.disruptor;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;

/**
 * 带顺序的UUID生成器简单测试
 * @author JIANG
 */
public class JavaUuidGeneratorTest {
	
	
	public static void main(String[] args) {
		System.err.println(generatorUUID());
		System.err.println(generatorUUID());
	}
	
	public static String generatorUUID(){
		TimeBasedGenerator timeBasedGenerator = Generators.timeBasedGenerator(EthernetAddress.fromInterface());
		return timeBasedGenerator.generate().toString();
	}

}
