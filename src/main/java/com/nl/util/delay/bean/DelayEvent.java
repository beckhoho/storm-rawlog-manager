/**
 * @author: gsw
 * @version: 1.0
 * @CreateTime: 2015年12月25日 下午2:38:47
 * @Description: 无
 */
package com.nl.util.delay.bean;

import java.io.Serializable;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.JedisCluster;


public class DelayEvent implements Runnable, Delayed,Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(DelayEvent.class);
	private static final String FALSE="0";
	private String userId;
	private String eventId;
	private String storeTablename;
	private String startValue;
	private long timeStamp;
	private long delayTime;
	private final JedisCluster jc;
	private int expire;
	private String dynamicArgs;
	
	

	public DelayEvent(String userId,String eventId,String storeTablename,String startValue,long timeStamp,long delayTime,JedisCluster jc,int expire,String dynamicArgs) {
		super();
		this.userId=userId;
		this.eventId=eventId;
		this.storeTablename=storeTablename;
		this.startValue=startValue;
		this.timeStamp=timeStamp;	
		// 都转为转为 NANOSECONDS
		this.delayTime = TimeUnit.NANOSECONDS.convert(delayTime,TimeUnit.MILLISECONDS) + System.nanoTime();
	    this.jc=jc;
	    this.expire= expire;
	    this.dynamicArgs=dynamicArgs;
	}

	@Override
	public void run() {
		com.nl.util.log.Log.info("[DelayEvent] "+storeTablename + userId+ ":" + eventId  + " 延时" + (System.currentTimeMillis()-timeStamp)/1000 + "秒");
		//LOG.info(userId + " 延时" + (System.currentTimeMillis()-timeStamp)/1000 + "秒");
		final String flag=this.jc.get(this.userId+this.eventId);
		if (flag != null) {
			if (!flag.contains(FALSE)) {
				this.jc.hset(storeTablename + userId, eventId, startValue);
				this.jc.setex(storeTablename + userId + ":" + eventId,this.expire, startValue);
				// 存入动态参数
				this.jc.set("XJAE:AEDA:"+userId+":"+eventId,this.dynamicArgs);
			} else {
				com.nl.util.log.Log.info("[DelayEvent] "+"不符合捕获条件：" + eventId + ":" + userId + ":" + flag);
			}
			this.jc.del(this.userId+this.eventId);
			com.nl.util.log.Log.info("[DelayEvent] "+"删除标志位：" +this.userId+this.eventId+ ":" + flag);
		} else {
			com.nl.util.log.Log.error("[DelayEvent] "+"标志位不存在："  + this.userId+this.eventId);
		}
	}

	@Override
	public long getDelay(TimeUnit unit) {
		return unit.convert(delayTime - System.nanoTime(), TimeUnit.NANOSECONDS);
	}

	@Override
	public int compareTo(Delayed o) {
		DelayEvent that = (DelayEvent) o;
		return ((timeStamp == that.timeStamp) && userId.equals(that.userId)
				&& eventId.equals(that.eventId)) ? 1 : 0;
	}

}