/**
 * @author: gsw
 * @version: 1.0
 * @CreateTime: 2015年12月25日 下午2:45:16
 * @Description: 无
 */
package com.nl.util.delay.listener;

import java.io.Serializable;

import java.util.concurrent.DelayQueue;
import com.nl.util.delay.bean.DelayEvent;

public class DelayListener implements Runnable ,Serializable{
	
	private static final long serialVersionUID = 1L;
	private DelayQueue<DelayEvent> events;

	public DelayListener(DelayQueue<DelayEvent> events) {
		super();
		this.events = events;
	}

	@Override
	public void run() {
		try {
			com.nl.util.log.Log.info("[DelayListener] 开始……");
			while (!Thread.interrupted()) {
				events.take().run();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}