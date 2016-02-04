package com.nl.example.wordcount;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SplitSentenceBolt extends BaseBasicBolt {
	private static final long serialVersionUID = 1L;
	private static Logger LOG = LoggerFactory.getLogger(SplitSentenceBolt.class);

	public void execute(Tuple tuple, BasicOutputCollector collector) {
		String words[] = tuple.getString(0).split(" ");
		for (String word : words)
			collector.emit(new Values(word));
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("word"));
	}
}