package com.nl.example.top10website;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetWebsiteBolt extends BaseBasicBolt {
	private static final long serialVersionUID = 1L;
	private static Logger LOG = LoggerFactory.getLogger(GetWebsiteBolt.class);

	public void execute(Tuple tuple, BasicOutputCollector collector) {
		String fields[] = tuple.getString(0).split("\\|");
		String v = null;
		if (fields.length == 28) {
			 v = fields[26];
			if (v != null) {
				if (!"".equals(v)) {
					collector.emit(new Values(v));
				}
			}
		}
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("website"));
	}
}