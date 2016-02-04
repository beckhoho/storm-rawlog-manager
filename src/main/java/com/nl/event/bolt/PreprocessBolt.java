/**
 * @author: gsw
 * @version: 1.0
 * @CreateTime: 2015年12月22日 上午11:02:13
 * @Description: 无
 */
package com.nl.event.bolt;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.nl.bean.NLMessage;
import com.nl.event.conf.ConfManager;
import com.nl.bean.SourceData;
import com.nl.util.config.DBCfg;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 
 * @ClassName: PreprocessBolt
 * @Description: 对数据进行预处理:过滤、关联补全手机号
 *               </p>
 * 
 * <pre>
 * </pre>
 */
public class PreprocessBolt extends BaseBasicBolt {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(PreprocessBolt.class);
	private static final Fields FIELDS=new Fields("nlMessage");
	private  transient SourceData sourceData;
	
	private final String sourceDataId;
	private final DBCfg dbCfg;
	/**
	 * 
	 * @param redisCfg Redis配置
	 * @param tableName 表名
	 */
	public PreprocessBolt(final String sourceDataId, final DBCfg dbCfg) {
		LOG.info("instancing...");
		this.sourceDataId = sourceDataId;
		this.dbCfg = dbCfg;
	
	}
	

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(Map stormConf, TopologyContext context) {
		LOG.info("prepare...");
		// 从数据库加载配置信息
		ConfManager.getInstance().setDb(dbCfg); // 初始化数据库
		ConfManager.getInstance().loadCaptureConfigBySourceId(this.sourceDataId);
		this.sourceData = ConfManager.getInstance().getSourceData();
	}

	@Override
	public void execute(Tuple tuple, BasicOutputCollector collector) {

		final String input = (String) tuple.getValue(0);
		LOG.info("data:" + input);
		// 1、过滤
		if ("".equals(input)) return;
		List<String> fields = Arrays.asList(input.split(this.sourceData.getFieldDelimiter()));
		if (fields.size() != this.sourceData.getFieldsSize()) return;	
		final NLMessage nlMessage = new NLMessage();
		nlMessage.setKey(fields.get(this.sourceData.getKeyFieldIndex() - 1));
		nlMessage.setFields(fields);
		collector.emit(new Values(nlMessage));		

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(FIELDS);
	}
}