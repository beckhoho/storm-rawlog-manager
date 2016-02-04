package com.nl.event.topo;

import java.io.IOException;
import java.util.List;

import storm.kafka.BrokerHosts;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.StringScheme;
import storm.kafka.ZkHosts;

import com.nl.event.bolt.SdcPreprocessBolt;
import com.nl.event.bolt.SdcStoreHdfsBolt;
import com.nl.util.GlobalConst;
import com.nl.util.CommonUtil;
import com.nl.util.config.ConfigLoader;
import com.nl.util.config.DBCfg;
import com.nl.util.config.RedisClusterCfg;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.topology.TopologyBuilder;

public class SdcCaptureAtomEventTopo {
	public final static String STORM_CONF_PATH = "/home/stormdev/storm/conf/mc-capture-atom-event-topo.yaml";

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException, AlreadyAliveException, InvalidTopologyException, InterruptedException  {

		// 1、读取配置
		final Config conf = new ConfigLoader().loadStormConfig(args.length > 0 ? args[0] : STORM_CONF_PATH);
		if (conf == null) System.exit(-1);
		if (conf.size() <= 1) System.exit(-2);
		CommonUtil.printConfig(conf);
		// Gbase
		final DBCfg gbaseCfg = new DBCfg();
		gbaseCfg.setDbName(conf.get("gbase.db.name").toString());
		gbaseCfg.setDriver(conf.get("gbase.db.driver").toString());
		gbaseCfg.setUrl(conf.get("gbase.db.url").toString());
		gbaseCfg.setUser(conf.get("gbase.db.username").toString());
		gbaseCfg.setPassword(conf.get("gbase.db.password").toString());
		// Redis
		final RedisClusterCfg redisClusterCfg = new RedisClusterCfg();
		redisClusterCfg.setHostAndport((List<String>) conf.get("redis.cluster.nodes.hostandport"));
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
		spoutConf.socketTimeoutMs=100000;
		final KafkaSpout kafkaSpout =new KafkaSpout(spoutConf);

		// 2、装配
		final TopologyBuilder builder = new TopologyBuilder();
		// a、数据源
		builder.setSpout(GlobalConst.COMPONENT_GET_SDC_DATA, kafkaSpout,1);
		builder.setBolt(GlobalConst.COMPONENT_SDC_PREPROCESS, new SdcPreprocessBolt(),1).noneGrouping(GlobalConst.COMPONENT_GET_SDC_DATA).setNumTasks(1);
		builder.setBolt(GlobalConst.COMPONENT_SDC_STORE_HDFS, new SdcStoreHdfsBolt("/sdc_data_parse","","-sdc"),1).noneGrouping(GlobalConst.COMPONENT_SDC_PREPROCESS).setNumTasks(1);
		
		// 3、提交
		if (GlobalConst.RELEASED.equals(conf.get("dev.versions").toString())) {
			StormSubmitter.submitTopology(conf.get("topology.name").toString(),conf, builder.createTopology());
		} else {
			final LocalCluster cluster = new LocalCluster();
			cluster.submitTopology(conf.get("topology.name").toString(), conf,builder.createTopology());
			Thread.sleep(60000);
			cluster.killTopology(conf.get("topology.name").toString());
			cluster.shutdown();
		}

	}

}
