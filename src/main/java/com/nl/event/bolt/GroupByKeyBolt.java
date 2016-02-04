/**
 * @author: gsw
 * @version: 1.0
 * @CreateTime: 2015年12月3日 下午7:13:21
 * @Description: 无
 */
package com.nl.event.bolt;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nl.bean.NLMessage;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 
 * @ClassName: 
 * @Description: 
 *               </p>
 * 
 * <pre>
 * </pre>
 */
public class GroupByKeyBolt extends BaseBasicBolt {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(GroupByKeyBolt.class);
	private static final Fields FIELDS=new Fields("nlMessage");
	
	// 构造函数参数
	private final List<String> streams;
	private final int STREAMSIZE;

	public GroupByKeyBolt(final List<String> streams) {
		LOG.info("instancing...");
		this.streams = streams;
		this.STREAMSIZE = streams.size();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(Map stormConf, TopologyContext context) {
	}

	@Override
	public void execute(Tuple tuple, BasicOutputCollector collector) {
		final NLMessage nlMessage=(NLMessage) tuple.getValue(0);	
		final String key = nlMessage.getKey();
		//LOG.info(nlMessage.getKey());
		com.nl.util.log.Log.info("[GroupByKeyBolt] "+nlMessage.getKey());
		collector.emit(streams.get(Math.abs(key.hashCode() % this.STREAMSIZE)),new Values(nlMessage));
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		for (final String stream : streams)
			declarer.declareStream(stream, FIELDS);
	}
}
