/**
 * @author: gsw
 * @version: 1.0
 * @CreateTime: 2015年12月25日 下午2:46:59
 * @Description: 无
 *//*
package com.nl.event.test;

import java.util.Random;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.nl.util.delay.bean.DelayEvent;
import com.nl.util.delay.listener.DelayListener;

public class TestDelayEvent  {
	static final int SIZE = 5;

	public static void main(String[] args) {
		Random r = new Random();
		DelayQueue<DelayEvent> events = new DelayQueue<DelayEvent>();
		ExecutorService exec = Executors.newCachedThreadPool();
		for (int i = 0; i < SIZE; i++) {
			events.put(new DelayEvent("user"+i, null, null, null, System.currentTimeMillis(),1000 + r.nextInt(4000), null,0));
		}
		exec.execute(new DelayListener(events));
		
		try {
			Thread.sleep(8000);// 间隔
			System.out.println("----------------------------------------------------------------------------");
			for (int i = 0; i < SIZE; i++) {
				events.put(new DelayEvent("user"+i, null, null, null, System.currentTimeMillis(),5000, null,0));
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}*/