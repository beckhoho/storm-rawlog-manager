package com.nl.event.topo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import storm.kafka.KafkaSpout;

import com.nl.event.bolt.CaptureAtomEventBolt;
import com.nl.event.bolt.CaptureDelayAtomEventBolt;
import com.nl.event.bolt.GroupByKeyBolt;
import com.nl.event.bolt.Hmset2RedisBolt;
import com.nl.event.bolt.JoinMsisdnByImsiBolt;
import com.nl.event.spout.KafkaSpoutFactory;
import com.nl.util.GlobalConst;
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
import backtype.storm.topology.TopologyBuilder;

public class McCaptureAtomEventTopo {
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
		final KafkaCfg kafkaCfg = new KafkaCfg();
		kafkaCfg.setTopic(conf.get("kafka.topic").toString());
		kafkaCfg.setSpoutId(conf.get("kafka.spoutid").toString());
		final KafkaSpout kafkaSpout = new KafkaSpoutFactory(kafkaCfg).getKafkaSpout();

		final ArrayList<String> captureAtomEventBoltStreams = (ArrayList<String>) conf.get("capture.event.bolt.streams.list");
		final ArrayList<String> groupByKeyBoltStreams = (ArrayList<String>) conf.get("group.by.key.bolt.streams.list");
		
		// 2、装配
		final TopologyBuilder builder = new TopologyBuilder();
		// a、数据源
		builder.setSpout(GlobalConst.COMPONENT_GET_MC_DATA, kafkaSpout);
		// b-a-1、预处理
		builder.setBolt(GlobalConst.COMPONENT_JOIN_MSISDN_BY_IMSI, new JoinMsisdnByImsiBolt(redisClusterCfg, conf.get("join.msisdn.by.imsi.bolt.store.tablename").toString(),conf.get("source.data.id").toString(), gbaseCfg), (int) conf.get("join.msisdn.by.imsi.bolt.parallelism.hint")).noneGrouping(GlobalConst.COMPONENT_GET_MC_DATA);
		// c-b-1、捕获
		builder.setBolt(GlobalConst.COMPONENT_CAPTURE_MC_EVENT, new CaptureAtomEventBolt(redisClusterCfg,conf.get("source.data.id").toString(), conf.get("source.data.fileds").toString(), conf.get("source.data.dynamic.args.fileds").toString(), gbaseCfg, captureAtomEventBoltStreams), (int) conf.get("capture.event.bolt.parallelism.hint")).noneGrouping(GlobalConst.COMPONENT_JOIN_MSISDN_BY_IMSI);
		// d-c-1入 Redis
		for (int i = 0; i < captureAtomEventBoltStreams.size(); i++) 
			builder.setBolt(GlobalConst.COMPONENT_HMSET_2_REDIS + i, new Hmset2RedisBolt(redisClusterCfg, conf.get("hmset.redis.bolt.store.tablename").toString(),(int) conf.get("hmset.redis.bolt.redis.key.expire")), (int) conf.get("hmset.redis.bolt.parallelism.hint")).noneGrouping(GlobalConst.COMPONENT_CAPTURE_MC_EVENT, captureAtomEventBoltStreams.get(i));
		// c-b-2、分组
		builder.setBolt(GlobalConst.COMPONENT_GROUP_BY_KEY, new GroupByKeyBolt(groupByKeyBoltStreams), (int) conf.get("group.by.key.bolt.parallelism.hint")).noneGrouping(GlobalConst.COMPONENT_JOIN_MSISDN_BY_IMSI);	
		// d-c-2 捕获延时事件
		for (int i = 0; i < groupByKeyBoltStreams.size(); i++) 
			builder.setBolt(GlobalConst.COMPONENT_CAPTURE_MC_DELAY_EVENT+i, new CaptureDelayAtomEventBolt(redisClusterCfg,(int) conf.get("capture.delay.event.bolt.redis.key.expire"),conf.get("capture.delay.event.bolt.store.tablename").toString(),conf.get("source.data.id").toString(),conf.get("source.data.fileds").toString(),conf.get("source.data.dynamic.args.fileds").toString(), gbaseCfg), (int) conf.get("capture.delay.event.bolt.parallelism.hint")).noneGrouping(GlobalConst.COMPONENT_GROUP_BY_KEY, groupByKeyBoltStreams.get(i));
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
