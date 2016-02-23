/**
 * @author: gsw
 * @version: 1.0
 * @CreateTime: 2015年12月3日 下午7:13:21
 * @Description: 无
 */
package com.nl.event.bolt;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import neu.sxc.expression.Expression;
import neu.sxc.expression.ExpressionFactory;
import neu.sxc.expression.lexical.LexicalException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.JedisCluster;

import com.nl.util.GlobalConst;
import com.nl.util.config.DBCfg;
import com.nl.util.config.RedisClusterCfg;
import com.nl.util.redis.RedisCluster;
import com.nl.bean.NLAtomEvent;
import com.nl.bean.NLMessage;
import com.nl.event.conf.ConfManager;
import com.nl.bean.AtomEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 
 * @ClassName: CaptureAtomEventBolt
 * @Description: 原子事件捕获
 * </p>
 * 
 * <pre>
 * </pre>
 */
public class CaptureAtomEventBolt extends BaseBasicBolt {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(CaptureAtomEventBolt.class);
	/*定时任务*/
	private static final ScheduledExecutorService TIMINGSERVICE = Executors.newSingleThreadScheduledExecutor(); 
	/*表达式工具*/
	private static final ExpressionFactory EXPRESSIONFACTORY = ExpressionFactory.getInstance();
	/*输出*/
	private static final Fields FIELDS=new Fields("nlAtomEvent");
	/*原始数据字段映射*/
	private  Map<String, Integer> fieldsName;
	/*原子事件列表*/
	private  List<AtomEvent> atomEventList;
	private JedisCluster redisCluster;
	// 构造函数参数
	private final RedisClusterCfg redisCfg;
	private final String sourceDataId, sourceDataFields;
	private final String dynamicArgsFields;
	private String[] dynamicArgsFieldsNames;
	private final DBCfg dbCfg;
	private final List<String> streams;
	private final int STREAMSIZE;
	private final long initialDelay, period;
	
	
	final Map<String, String> values = new HashMap<String, String>();

	public CaptureAtomEventBolt( final RedisClusterCfg redisCfg,final String sourceDataId,final String sourceDataFieldsName,final String dynamicArgsFields, final DBCfg dbCfg, final List<String> streams) {
		this(redisCfg,sourceDataId,sourceDataFieldsName,dynamicArgsFields, dbCfg, streams,10,15);
	}
	
	/**
	 * 
	 * @param sourceDataId 数据源
	 * @param sourceDataFieldsName 数据源字段定义
	 * @param dbCfg 数据库配置
	 * @param streams 输出流定义
	 */
	public CaptureAtomEventBolt( final RedisClusterCfg redisCfg,final String sourceDataId,final String sourceDataFields,final String dynamicArgsFields, final DBCfg dbCfg, final List<String> streams,final long initialDelay,final long period) {
		LOG.info("instancing...");
		this.redisCfg = redisCfg;
		this.sourceDataId = sourceDataId;
		this.sourceDataFields=sourceDataFields;
		this.dynamicArgsFields=dynamicArgsFields;
		this.dbCfg = dbCfg;
		this.streams = streams;
		this.STREAMSIZE = streams.size();
		this.initialDelay=initialDelay;
		this.period=period;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(Map stormConf, TopologyContext context) {
		LOG.info("prepare...");
		// 初始化redis
		RedisCluster.getRedisCluster().setCfg(redisCfg);
		this.redisCluster = RedisCluster.getRedisCluster().getInstance();
		// 映射数据和字段名
		this.fieldsName=new HashMap<String, Integer>();
		final String[] sdfns=this.sourceDataFields.split(GlobalConst.DEFAULT_SEPARATOR);
		for (int i=0;i<sdfns.length;i ++){
			this.fieldsName.put(sdfns[i], i);
		}
		// 动态参数
		this.dynamicArgsFieldsNames=dynamicArgsFields.split(GlobalConst.DEFAULT_SEPARATOR);
		// 从数据库加载原子事件配置信息
		ConfManager.getInstance().setDb(dbCfg); // 初始化数据库
		ConfManager.getInstance().loadCaptureConfigBySourceId(this.sourceDataId);
		this.atomEventList = ConfManager.getInstance().getAtomEventList();
		
		// 计划任务：更新
		final Runnable update = new Runnable() {
			public void run() {
				ConfManager.getInstance().loadCaptureConfigBySourceId(sourceDataId);
				atomEventList = ConfManager.getInstance().getAtomEventList();
			}
		};
		// 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
		TIMINGSERVICE.scheduleAtFixedRate(update, initialDelay,period,TimeUnit.MINUTES);
	}

	@Override
	public void execute(Tuple tuple, BasicOutputCollector collector) {

		final NLMessage nlMessage = (NLMessage) tuple.getValue(0);
		final List<String> fields = nlMessage.getFields();
		final String key = nlMessage.getKey();
		// 2、捕获
		final Map<String, String> values = capture(fields);
		// 3、封装
		final NLAtomEvent nlAtomEvent = new NLAtomEvent();
		nlAtomEvent.setKey(key);
		nlAtomEvent.setValues(values);
		nlAtomEvent.setDynamicArgs(captureDynamic(fields));// 动态参数
		// 4、分发
		collector.emit(streams.get(Math.abs(key.hashCode() % this.STREAMSIZE)), new Values(nlAtomEvent));
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		for (final String stream : streams)
			declarer.declareStream(stream, FIELDS);
	}
	
	/**
	 * 
	 * @param fields
	 * @return
	 */
	private String captureDynamic(List<String> fields) {
		StringBuffer dynamicArgs = new StringBuffer();
		final int length = this.dynamicArgsFieldsNames.length;
		for (int i = 0; i < length; i++) {
			dynamicArgs.append(fields.get(this.fieldsName
					.get(this.dynamicArgsFieldsNames[i])));
			if (i != length - 1) {
				dynamicArgs.append(GlobalConst.DEFAULT_SEPARATOR);
			}
		}
		return dynamicArgs.toString();

	}
	
	/**
	 * 
	 * @param fields
	 * @return
	 */
	private Map<String, String> capture(List<String> fields) {
		// 2、捕获
		final Map<String, String> values = new HashMap<String, String>();
		for (final AtomEvent atomEvent : atomEventList) {
			String strExpression = atomEvent.getExpression();
			final String[] expressionParams = atomEvent.getExpressionParam()
					.split(GlobalConst.DEFAULT_SEPARATOR);
			final String[] captureFields = atomEvent.getCaptureField().split(
					GlobalConst.DEFAULT_SEPARATOR);
			final StringBuffer value = new StringBuffer();
			boolean isCapture = false;

			LOG.info("-----1----strExpression:" + strExpression);
			// 拼接表达式
			if ("ALL".equals(strExpression)) {
				isCapture = true;
			} else if (strExpression != null
					&& strExpression.startsWith("LOCATION")) {
				final String info[] = strExpression
						.split(GlobalConst.DEFAULT_SEPARATOR);
				String tablename = null;
				if (info.length == 2) {
					tablename = info[1];
				} else {
					LOG.info("Worng Expression:" + strExpression);
					continue;
				}
				String field = "";
				for (final String expressionParam : expressionParams) {
					field = field
							+ fields.get(this.fieldsName.get(expressionParam));
				}
				if (this.redisCluster.hexists(tablename, field))
					isCapture = true;
			} else if (strExpression != null && strExpression.contains("==")) {
				for (final String expressionParam : expressionParams) {
					LOG.info("---------expressionParam:" + expressionParam);
					final String replacement = fields.get(this.fieldsName
							.get(expressionParam));
					// final String
					// replacement=fields[this.fieldsName.get(expressionParam)];
					LOG.info("---------replacement:" + replacement);
					if ("".equals(replacement.trim()))
						break;
					strExpression = strExpression.replaceAll(expressionParam,
							replacement);
				}
				LOG.info("-----2----strExpression:" + strExpression);
				if (strExpression.equals(atomEvent.getExpression()))
					continue;
				Expression expression = null;
				try {
					expression = EXPRESSIONFACTORY.getExpression(strExpression);
					LOG.info("---------expression:" + expression);
					expression.lexicalAnalysis();// 词法分析
					isCapture = expression.evaluate().getBooleanValue();
				} catch (LexicalException e) {
					continue;
					// e.printStackTrace();
				}
			} else {
				LOG.info("Other Expression can not support:" + strExpression);
				continue;
			}
			if (isCapture) {
				for (final String captureField : captureFields) {
					value.append(fields.get(this.fieldsName.get(captureField)));
				}
			}
			if (!"".equals(value.toString().trim()))
				values.put(atomEvent.getEventId(), value.toString());
		}
		return values;
	}
}

/*final String input = (String) tuple.getValue(0);
LOG.info("222222222222:" + input);
// 1、过滤
if ("".equals(input)) return;
final String[] fields = input.split(this.sourceData.getFieldDelimiter());
if (fields.length != this.sourceData.getFieldsSize()) return;*/
