/**
 * @author: gsw
 * @version: 1.0
 * @CreateTime: 2015年12月21日 下午1:43:25
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

import redis.clients.jedis.JedisCluster;

import com.nl.bean.NLMessage;
import com.nl.event.conf.ConfManager;
import com.nl.bean.SourceData;
import com.nl.util.CommonUtil;
import com.nl.util.config.DBCfg;
import com.nl.util.config.RedisClusterCfg;
import com.nl.util.redis.RedisCluster;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 
 * @ClassName: JoinMsisdnByImsiBolt
 * @Description: 对数据进行预处理:过滤、关联补全手机号
 *               </p>
 * 
 * <pre>
 * </pre>
 */
public class JoinMsisdnByImsiBolt extends BaseBasicBolt {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(JoinMsisdnByImsiBolt.class);
	private static final Fields FIELDS=new Fields("nlMessage");
	private SourceData sourceData;
	
	private JedisCluster redisCluster;
	private final RedisClusterCfg redisCfg;
	private final String tableName;
	private final String sourceDataId;
	private final DBCfg dbCfg;
	/**
	 * 
	 * @param redisCfg Redis配置
	 * @param tableName 表名
	 */
	public JoinMsisdnByImsiBolt(final RedisClusterCfg redisCfg, final String tableName,final String sourceDataId, final DBCfg dbCfg) {
		LOG.info("instancing...");
		this.redisCfg = redisCfg;
		this.tableName = tableName;
		this.sourceDataId = sourceDataId;
		this.dbCfg = dbCfg;
	
	}
	

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(Map stormConf, TopologyContext context) {
		LOG.info("prepare...");
		// 初始化redis
		RedisCluster.getRedisCluster().setCfg(redisCfg);
		this.redisCluster = RedisCluster.getRedisCluster().getInstance();
		// 从数据库加载配置信息
		ConfManager.getInstance().setDb(dbCfg); // 初始化数据库
		ConfManager.getInstance().loadCaptureConfigBySourceId(this.sourceDataId);
		this.sourceData = ConfManager.getInstance().getSourceData();
	}

	@Override
	public void execute(Tuple tuple, BasicOutputCollector collector) {

		final String input = (String) tuple.getValue(0);
		com.nl.util.log.Log.info("[JoinMsisdnByImsiBolt] " + input);
		// 1、过滤
		if ("".equals(input)) return;
		List<String> fields = Arrays.asList(input.split(this.sourceData.getFieldDelimiter()));
		if (fields.size() != this.sourceData.getFieldsSize()) return;
		final String imsi = fields.get(1);
		if (CommonUtil.isNumeric(imsi)) {
			final String msisdn = this.redisCluster.get(this.tableName + imsi);
			if (msisdn != null) {
				fields.set(this.sourceData.getKeyFieldIndex()-1, msisdn);
				final NLMessage nlMessage=new NLMessage();
				nlMessage.setKey(msisdn);
				nlMessage.setFields(fields);
				collector.emit(new Values(nlMessage));
			} else {
				return;
			}
		} else {
			return;
		}

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(FIELDS);
	}
}

/*final int index = input.indexOf(GlobalConst.DEFAULT_SEPARATOR) + 1;
final String imsi = input.substring(index, index + 15);
if (StringUtil.isNumeric(imsi)) {
	final String msisdn = this.redisCluster.get(this.tableName + imsi);
	if (msisdn != null) {
		final String output = input.replaceFirst(imsi, msisdn);
		collector.emit(new Values(output));
	} else {
		return;
	}
}else {
		return;
	}*/