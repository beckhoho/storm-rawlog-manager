/**
* @author: gsw
* @version: 1.0
* @CreateTime: 2015年12月25日 下午2:43:39
* @Description: 无
*/
package com.nl.event.test.delayqueue;

import java.util.concurrent.ExecutorService;

public class EndExam extends Student {
	private ExecutorService exec;

	public EndExam(int submitTime, ExecutorService exec) {
		super(null, submitTime);
		this.exec = exec;
	}

	@Override
	public void run() {
		exec.shutdownNow();
	}
}
