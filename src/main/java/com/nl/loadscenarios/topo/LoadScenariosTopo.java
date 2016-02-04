package com.nl.loadscenarios.topo;

import java.io.IOException;
import java.util.List;

import com.nl.loadscenarios.bolt.GetLacciInScenariosBolt;
import com.nl.loadscenarios.bolt.Hset2RedisBolt;
import com.nl.loadscenarios.spout.GetScenariosListSpout;
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
import backtype.storm.topology.TopologyBuilder;

public class LoadScenariosTopo {
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
		// 2、装配
		final TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout(GlobalConst.COMPONENT_GET_SCENARIOS_LIST, new GetScenariosListSpout(gbaseCfg));
		// 
		builder.setBolt(GlobalConst.COMPONENT_GET_LACCI, new GetLacciInScenariosBolt(gbaseCfg), (int) conf.get("get.lacci.in.scenarios.bolt.parallelism.hint")).noneGrouping(GlobalConst.COMPONENT_GET_SCENARIOS_LIST);
		// 入 Redis
		builder.setBolt(GlobalConst.COMPONENT_HSET_2_REDIS, new Hset2RedisBolt(redisClusterCfg), (int) conf.get("hmset.redis.bolt.parallelism.hint")).noneGrouping(GlobalConst.COMPONENT_GET_LACCI);
		// 3、提交
		if ("released".equals(conf.get("dev.versions").toString())) {
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
