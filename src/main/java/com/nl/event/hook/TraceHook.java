/**
 * @author: gsw
 * @version: 1.0
 * @CreateTime: 2016年1月20日 下午3:14:07
 * @Description: 无
 */
package com.nl.event.hook;

import backtype.storm.hooks.BaseTaskHook;
import backtype.storm.hooks.info.BoltAckInfo;
import backtype.storm.hooks.info.BoltExecuteInfo;
import backtype.storm.hooks.info.BoltFailInfo;
import backtype.storm.hooks.info.EmitInfo;
import backtype.storm.hooks.info.SpoutAckInfo;
import backtype.storm.hooks.info.SpoutFailInfo;
/**
 * 
 * @Override
   public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
    _collector = collector;
     context.addTaskHook(new TraceHook());
}
 *
 */
public class TraceHook extends BaseTaskHook {


	@Override
	public void emit(EmitInfo info) {
		
	}

	@Override
	public void spoutAck(SpoutAckInfo info) {
	}

	@Override
	public void spoutFail(SpoutFailInfo info) {
	}

	@Override
	public void boltAck(BoltAckInfo info) {
	}

	@Override
	public void boltFail(BoltFailInfo info) {
	}

	@Override
	public void boltExecute(BoltExecuteInfo info) {
	}
}
