package com.nl.loadscenarios.spout;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nl.loadscenarios.configtable.BusinessData;
import com.nl.loadscenarios.configtable.bean.Scenarios;
import com.nl.util.config.DBCfg;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 
 * @author: gsw
 * @version: 1.0
 * @CreateTime: 2015年12月23日 上午10:15:34
 * @ClassName: GetSaleActiveListSpout
 * @Description: GetScenariosListSpout
 *               </p>
 *               </pre>
 */
public class GetScenariosListSpout extends BaseRichSpout {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory
			.getLogger(GetScenariosListSpout.class);
	private static final Fields FIELDS = new Fields("scenarios");
	private final DBCfg dbCfg;

	private final static long WAITING_TIME_MS = TimeUnit.DAYS.toMillis(30);// 时间间隔，默认设置成10秒

	// private TopologyContext context = null;
	private SpoutOutputCollector collector = null;

	public GetScenariosListSpout(final DBCfg dbCfg) {
		this.dbCfg = dbCfg;

	}

	@SuppressWarnings("rawtypes")
	@Override
	public void open(Map conf, TopologyContext context,
			SpoutOutputCollector collector) {
		// this.context = context;
		this.collector = collector;
		BusinessData.getInstance().setDb(dbCfg); // 初始化数据库

	}

	@Override
	public void nextTuple() {
		BusinessData.getInstance().loadScenarios();
		final List<Scenarios> scenariosList = BusinessData.getInstance().getScenariosList();
		if (scenariosList != null) {
			if (!scenariosList.isEmpty()) {
				for (Scenarios scenarios : scenariosList) {
					this.collector.emit(new Values(scenarios));
				}
			}
		}

		try {
			Thread.sleep(WAITING_TIME_MS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}// 间隔

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(FIELDS);
	}

	@Override
	public void ack(Object unique) {
		LOG.info("ack scenarios:" + unique);
	}

	@Override
	public void fail(Object unique) {
		LOG.info("fail scenarios:" + unique);
	}

}
