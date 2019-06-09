package com.firecode.mqtest.rabbitmq;

public abstract class BasicTest {
	
	protected void p(Object o){
		p(o,false);
	}
	
	protected void p(Object o,boolean isError){
		if(isError){
			System.err.println(o);
		}else{
			System.out.println(o);
		}
	}

}
