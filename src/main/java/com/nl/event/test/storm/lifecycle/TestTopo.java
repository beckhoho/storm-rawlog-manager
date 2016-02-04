package com.nl.event.test.storm.lifecycle;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import storm.kafka.BrokerHosts;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.StringScheme;
import storm.kafka.ZkHosts;

import com.nl.util.CommonUtil;
import com.nl.util.config.ConfigLoader;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.topology.TopologyBuilder;

public class TestTopo {

	public static void main(String[] args) throws IOException, AlreadyAliveException, InvalidTopologyException, InterruptedException  {

		// 1、读取配置
		final Config conf = new ConfigLoader().loadStormConfig(args[0]);
		if (conf == null) System.exit(-1);
		if (conf.size() <= 1) System.exit(-2);
		CommonUtil.printConfig(conf);
		// Kafka
		final String zks = conf.get("kafka.zklist").toString();
		final String topic = conf.get("kafka.topic").toString();
		final String zkRoot = conf.get("zookeeper.root").toString();
		final String id = conf.get("kafka.spoutid").toString();
		BrokerHosts brokerHosts = new ZkHosts(zks);
		SpoutConfig spoutConf = new SpoutConfig(brokerHosts, topic, zkRoot, id);
		spoutConf.scheme = new SchemeAsMultiScheme(new StringScheme());
		spoutConf.forceFromStart = (boolean) conf.get("force.from.begin");
		spoutConf.zkServers = (List<String>) conf.get("zookeeper.servers");
		spoutConf.zkPort = 2181;
		spoutConf.socketTimeoutMs = 100000;
		final KafkaSpout kafkaSpout = new KafkaSpout(spoutConf);
		// 2、装配
		final TopologyBuilder builder = new TopologyBuilder();
		// a、数据源
		builder.setSpout("spout-test", kafkaSpout,1);
		builder.setBolt("print-test", new Print(),1).noneGrouping("spout-test").setNumTasks(1);
		// 3、提交
		if ("released".equals(conf.get("dev.versions").toString())) {
			StormSubmitter.submitTopology(conf.get("topology.name").toString(),
					conf, builder.createTopology());
		} else {
			final LocalCluster cluster = new LocalCluster();
			cluster.submitTopology(conf.get("topology.name").toString(), conf,
					builder.createTopology());
			Thread.sleep(60000);
			cluster.killTopology(conf.get("topology.name").toString());
			cluster.shutdown();
		}

	}

}
