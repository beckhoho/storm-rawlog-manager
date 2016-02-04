package com.nl.event.topo;

import java.io.IOException;
import java.util.Arrays;

import storm.kafka.BrokerHosts;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.StringScheme;
import storm.kafka.ZkHosts;

import com.nl.event.test.bolt.PrintBolt;
import com.nl.util.CommonUtil;
import com.nl.util.config.ConfigLoader;
import com.nl.util.config.DBCfg;
import com.nl.util.config.KafkaCfg;
import com.nl.util.config.RedisClusterCfg;

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
		
		String zks = conf.get("kafka.zklist").toString();
		String topic = conf.get("kafka.topic").toString();
		String zkRoot = conf.get("zookeeper.root").toString(); // default zookeeper root configuration for storm
		String id = conf.get("kafka.spoutid").toString();
		BrokerHosts brokerHosts = new ZkHosts(zks);
		SpoutConfig spoutConf = new SpoutConfig(brokerHosts, topic, zkRoot, id);
		spoutConf.scheme = new SchemeAsMultiScheme(new StringScheme());
		spoutConf.forceFromStart = false;	
		spoutConf.zkServers = Arrays.asList(new String[] { "192.168.1.55",
				"192.168.1.56", "192.168.1.57", "192.168.1.58", "192.168.1.59",
				"192.168.1.60", "192.168.1.61" });
		spoutConf.zkPort = 2181;
		spoutConf.socketTimeoutMs=100000;
		//spoutConf.
		final KafkaSpout kafkaSpout =new KafkaSpout(spoutConf);
		
		// 2、装配
		final TopologyBuilder builder = new TopologyBuilder();
		// a、数据源
		builder.setSpout("spout", kafkaSpout,1);
		builder.setBolt("print", new PrintBolt(),1).noneGrouping("spout").setNumTasks(1);
		
		
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
