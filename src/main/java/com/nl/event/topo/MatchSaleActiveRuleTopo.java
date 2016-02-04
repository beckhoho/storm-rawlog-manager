package com.nl.event.topo;

import java.util.ArrayList;
import java.util.List;

import com.nl.event.bolt.MatchRuleBolt;
import com.nl.event.bolt.MatchSaleActiveUserListBolt;
import com.nl.event.bolt.StoreDbBolt;
import com.nl.event.spout.GetSaleActiveListSpout;
import com.nl.util.GlobalConst;
import com.nl.util.CommonUtil;
import com.nl.util.config.ConfigLoader;
import com.nl.util.config.DBCfg;
import com.nl.util.config.RedisClusterCfg;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;

public class MatchSaleActiveRuleTopo {
	public final static String STORM_CONF_PATH = "/home/stormdev/storm/conf/event-conf.yaml";

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {

		// 1、读取配置
		Config conf = new ConfigLoader()
				.loadStormConfig(args.length > 0 ? args[0] : STORM_CONF_PATH);
		if (conf == null)
			System.exit(-1);
		if (conf.size() <= 1)
			System.exit(-2);
		CommonUtil.printConfig(conf);
		// Gbase
		DBCfg gbaseCfg = new DBCfg();
		gbaseCfg.setDbName(conf.get("gbase.db.name").toString());
		gbaseCfg.setDriver(conf.get("gbase.db.driver").toString());
		gbaseCfg.setUrl(conf.get("gbase.db.url").toString());
		gbaseCfg.setUser(conf.get("gbase.db.username").toString());
		gbaseCfg.setPassword(conf.get("gbase.db.password").toString());
		// Oracle
		DBCfg oracleCfg = new DBCfg();
		oracleCfg.setDbName(conf.get("oracle.db.name").toString());
		oracleCfg.setDriver(conf.get("oracle.db.driver").toString());
		oracleCfg.setUrl(conf.get("oracle.db.url").toString());
		oracleCfg.setUser(conf.get("oracle.db.username").toString());
		oracleCfg.setPassword(conf.get("oracle.db.password").toString());
		// Redis
		RedisClusterCfg redisClusterCfg = new RedisClusterCfg();
		redisClusterCfg.setHostAndport((List<String>) conf.get("redis.cluster.nodes.hostandport"));

		ArrayList<String> matchRuleBoltStreams = (ArrayList<String>) conf.get("match.rule.bolt.streams.list");
		//ArrayList<String> storeDbBoltStreams = (ArrayList<String>) conf.get("store.db.bolt.streams.list")

		// 2、装配
		TopologyBuilder builder = new TopologyBuilder();
		// 获取营销活动
		builder.setSpout(GlobalConst.COMPONENT_GET_SALE_ACTIVE_LIST, new GetSaleActiveListSpout(gbaseCfg,redisClusterCfg,(int) conf.get("get.sale.active.list.spout.lock.time.second")),1);
		// 匹配用户群
		builder.setBolt(
				GlobalConst.COMPONENT_MATCH_SALE_AVTIVE_USER_LIST,
				new MatchSaleActiveUserListBolt(gbaseCfg, redisClusterCfg,conf.get("match.sale.active.user.list.bolt.store.tablename").toString(),matchRuleBoltStreams),
				(int) conf.get("match.sale.active.user.list.bolt.parallelism.hint"))
				.noneGrouping(GlobalConst.COMPONENT_GET_SALE_ACTIVE_LIST);
		// 匹配规则
		for (int i = 0; i < matchRuleBoltStreams.size(); i++){//match.rule.bolt.store.tablename
		builder.setBolt(
				GlobalConst.COMPONENT_MATCH_RULE+i,
				new MatchRuleBolt(gbaseCfg, redisClusterCfg,conf.get("match.rule.bolt.store.tablename").toString() ),//,storeDbBoltStreams),
				(int) conf.get("match.rule.bolt.parallelism.hint"))
				.noneGrouping(GlobalConst.COMPONENT_MATCH_SALE_AVTIVE_USER_LIST,matchRuleBoltStreams.get(i));		
		// 入 db
		//for (int i = 0; i < storeDbBoltStreams.size(); i++)
			builder.setBolt(
					GlobalConst.COMPONENT_STORE_DB + i,
					new StoreDbBolt(oracleCfg, conf.get(
							"store.db.bolt.store.tablename").toString()),
					(int) conf.get("store.db.bolt.parallelism.hint"))
					.noneGrouping(GlobalConst.COMPONENT_MATCH_RULE+i);
							//,storeDbBoltStreams.get(i));
		}
		// 3、提交
		if ("released".equals(conf.get("dev.versions").toString())) {
			StormSubmitter.submitTopology(conf.get("topology.name").toString(),
					conf, builder.createTopology());
		} else {
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology(conf.get("topology.name").toString(), conf,
					builder.createTopology());
			Thread.sleep(60000);
			cluster.killTopology(conf.get("topology.name").toString());
			cluster.shutdown();
		}

	}

}
