/**
 * @author: gsw
 * @version: 1.0
 * @CreateTime: 2016年1月3日 上午9:43:35
 * @Description: 无
 */
package com.nl.event.test.storm.lifecycle;

import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

public class Print extends BaseRichBolt {

	private static final long serialVersionUID = 1L;
	protected OutputCollector collector;
	
	@Override
	public void execute(Tuple tuple) {
	com.nl.util.log.Log.info("execute");
	collector.ack(tuple);
	
	}
	

	@Override
	public void prepare(Map conf, TopologyContext context,
			OutputCollector collector) {
		this.collector = collector;
		com.nl.util.log.Log.debug("prepare");
	}

	@Override
	public void cleanup() {
		com.nl.util.log.Log.debug("当kill时/异常退出时cleanup");
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// do nothing
	}

}