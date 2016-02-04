/**
 * @author: gsw
 * @version: 1.0
 * @CreateTime: 2015年11月17日 上午11:34:34
 * @Description: 无
 */
package com.nl.event.test;

import backtype.storm.utils.RotatingMap;

public class TestRotatingMap {
	final static int numBuckets=10;
	static RotatingMap<String, Integer> rmap = new RotatingMap<String, Integer>(
			numBuckets, new CallBack());

	static class CallBack implements
			RotatingMap.ExpiredCallback<String, Integer> {
		@Override
		public void expire(String key, Integer val) {
			System.out.println("now:"+System.currentTimeMillis());
			System.out.println(key+"超时了"+"："+val);
		}
	}

	public static void main(String[] args) throws InterruptedException {
		final Object lock = new Object();
		synchronized (lock) {
			rmap.put("test1", 1);
		}
		final long start=System.currentTimeMillis();
		
		System.out.println("start:"+start);
		// 启动3个Thread
		
		// Thread cleaner : 获取lock,调用rotate()方法清理数据。这里是自己控制处理时间。
				Thread cleaner = new Thread(new Runnable() {
					final long expirationMillis = (6 * 1000L)/numBuckets;

					@Override
					public void run() {
						try {
							while (true) {
								Thread.sleep(expirationMillis);
								synchronized (lock) {
									rmap.rotate();
									System.out.println("cleaning...");
								}
							}
						} catch (InterruptedException ex) {
						}
					}
				});
				cleaner.setDaemon(true);
				cleaner.start();
		
		
		// Thread A: 获取lock，往Map里面放数据
		new Thread(new Runnable() {
			public void run() {
				//try {
					while (true) {
						synchronized (lock) {
							rmap.put("test2", 1);
						}
						//Thread.sleep(2000);
					//}
				//} catch (InterruptedException e) {
				//	e.printStackTrace();
				}
			}

		}).start();

		// Thread B: 获取lock,读取Map的数据

		new Thread(new Runnable() {
			public void run() {
				//try {
					while (true) {					
						synchronized (lock) {
							rmap.containsKey("test1");
							rmap.containsKey("test2");
							//System.out.println(rmap.containsKey("test1"));
							//System.out.println(rmap.containsKey("test2"));
							
							

						}
						//Thread.sleep(2000);
					}
				//} catch (InterruptedException e) {
				//	e.printStackTrace();
				//}
			}

		}).start();


	}
}
