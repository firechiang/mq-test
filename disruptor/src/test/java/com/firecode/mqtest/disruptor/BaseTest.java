package com.firecode.mqtest.disruptor;

import org.junit.Test;

public class BaseTest {

	@Test
	public void test() {
		p("测试");
	}
	
	
	public static void p(Object o){
		p(o,true);
	}
	
	
	public static void p(Object o,boolean isErr){
		if(isErr){
			System.err.println(o);
		}else{
			System.out.println(o);
		}
	}

}
