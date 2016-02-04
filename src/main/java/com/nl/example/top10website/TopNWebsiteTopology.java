/*package com.nl.example.top10website;

import com.nl.event.spout.KafkaSpoutFactory;

import storm.kafka.KafkaSpout;
import storm.kafka.StringScheme;
import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;

public class  TopNWebsiteTopology {
	private final static String[] ZKSERVERS = { "192.168.1.102",
			"192.168.1.103", "192.168.1.104" };

	public static void main(String[] args) throws AlreadyAliveException,
			InvalidTopologyException {
		Config conf = new Config();
		// 构建 topo
		TopologyBuilder builder = new TopologyBuilder();
		buildTopo(builder, conf);
		// 提交topo
		StormSubmitter.submitTopologyWithProgressBar("topN-website", conf,
				builder.createTopology());
	}

	public static void buildTopo(TopologyBuilder builder, Config conf) {

		KafkaSpout kafkaSpout = new KafkaSpoutFactory("kafka-website",
				"topn-websit-id-00", ZKSERVERS,new StringScheme(),"/example-test",2182).getKafkaSpout();
		builder.setSpout("website-spout", kafkaSpout);
		builder.setBolt("get-website-bolt", new GetWebsiteBolt())
				.localOrShuffleGrouping("website-spout");
		builder.setBolt("count-website-bolt", new CountWebsiteBolt())
				.localOrShuffleGrouping("get-website-bolt");
		conf.setNumWorkers(3);

	}

}*/