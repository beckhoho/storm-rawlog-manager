/**
 * @author: gsw
 * @version: 1.0
 * @CreateTime: 2015年12月5日 下午1:14:25
 * @Description: 无
 */
package com.nl.loadscenarios.bolt;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nl.loadscenarios.configtable.BusinessData;
import com.nl.loadscenarios.configtable.bean.Scenarios;
import com.nl.util.config.DBCfg;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * 
 * @ClassName: GetLacciInScenariosBolt
 * @Description: GetLacciInScenariosBolt
 *               </p>
 * 
 * <pre>
 * </pre>
 */
public class GetLacciInScenariosBolt extends BaseBasicBolt {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(GetLacciInScenariosBolt.class);
	private static final Fields FIELDS = new Fields("tablename", "field","value");
	// 构造函数参数
	private final DBCfg dbCfg;

	/**
	 * 
	 * @param dbCfg
	 *            数据库配置
	 */
	public GetLacciInScenariosBolt(final DBCfg dbCfg) {
		LOG.info("instancing...");
		this.dbCfg = dbCfg;

	}

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(Map stormConf, TopologyContext context) {
		BusinessData.getInstance().setDb(dbCfg); // 初始化数据库
	}

	@Override
	public void execute(Tuple tuple, BasicOutputCollector collector) {
		Scenarios scenarios = (Scenarios) tuple.getValue(0);
		final ConcurrentHashMap<String, String> lacciMap =BusinessData.getInstance().loadLacciBySql(scenarios.getSql());
		if (lacciMap != null) {
			if (!lacciMap.isEmpty()) {
				final String tabelname = scenarios.getRedisTablename();
				for (Entry<String, String> entry : lacciMap.entrySet()) {
					collector.emit(new Values(tabelname, entry.getKey(), entry.getValue()));
					LOG.info(tabelname+":"+entry.getKey()+":"+entry.getValue());
				}
			}
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(FIELDS);
	}

	@Override
	public void cleanup() {
	}
}
