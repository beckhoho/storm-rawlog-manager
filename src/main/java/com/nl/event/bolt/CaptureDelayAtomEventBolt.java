/**
 * @author: gsw
 * @version: 1.0
 * @CreateTime: 2015年12月3日 下午7:13:21
 * @Description: 无
 */
package com.nl.event.bolt;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.JedisCluster;

import com.nl.util.GlobalConst;
import com.nl.util.config.DBCfg;
import com.nl.util.config.RedisClusterCfg;
import com.nl.util.delay.bean.DelayEvent;
import com.nl.util.delay.listener.DelayListener;
import com.nl.util.redis.RedisCluster;
import com.nl.bean.NLMessage;
import com.nl.event.conf.ConfManager;
import com.nl.bean.AtomEvent;
import com.nl.util.CommonUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 
 * @ClassName: CaptureDelayAtomEventBolt
 * @Description: 延时原子事件捕获
 *               </p>
 * 
 * <pre>
 * </pre>
 */
public class CaptureDelayAtomEventBolt extends BaseBasicBolt {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(CaptureDelayAtomEventBolt.class);
	private static final String FALSE="0";
	private static final String TRUE="1";
	private static final ScheduledExecutorService SERVICE = Executors.newSingleThreadScheduledExecutor();
	private transient Map<String, Integer> fieldsName;
	private transient List<AtomEvent> delayAtomEventList;
	private JedisCluster redisCluster;
	// 构造函数参数
	private final RedisClusterCfg redisCfg;
	private final int exprie;
	private final String storeTablename; 
	private final String sourceDataId;
	private final String sourceDataFieldsName;
	private final String dynamicArgsFields;
	private String[] dynamicArgsFieldsNames;
	private final DBCfg dbCfg;
	private final long initialDelay;
	private final long period;
	
	// 延时队列
	private DelayQueue<DelayEvent> events ;
	private ExecutorService exec;

	public CaptureDelayAtomEventBolt(final RedisClusterCfg redisCfg,int exprie, final String storeTablename, final String sourceDataId,
			final String sourceDataFieldsName,final String dynamicArgsFields, final DBCfg dbCfg) {
		this(redisCfg, exprie, storeTablename, sourceDataId,
				sourceDataFieldsName,dynamicArgsFields, dbCfg, 5, 15);
	}
	/**
	 * 
	 * @param redisCfg
	 * @param exprie
	 * @param storeTablename
	 * @param sourceDataId
	 * @param sourceDataFieldsName
	 * @param dbCfg
	 */
	public CaptureDelayAtomEventBolt(final RedisClusterCfg redisCfg,int exprie,final String storeTablename,
			final String sourceDataId, final String sourceDataFieldsName,final String dynamicArgsFields,final DBCfg dbCfg,final long initialDelay,final long period) {
		LOG.info("instancing...");
		this.redisCfg = redisCfg;
		this.exprie=exprie;
		this.storeTablename=storeTablename;
		this.sourceDataId = sourceDataId;
		this.sourceDataFieldsName = sourceDataFieldsName;
		this.dynamicArgsFields=dynamicArgsFields;
		this.dbCfg = dbCfg;
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
		this.fieldsName = new HashMap<String, Integer>();
		final String[] sdfns = this.sourceDataFieldsName.split(GlobalConst.DEFAULT_SEPARATOR);
		for (int i = 0; i < sdfns.length; i++)  this.fieldsName.put(sdfns[i], i);
		
		events = new DelayQueue<DelayEvent>();
		exec = Executors.newCachedThreadPool();
		// 延时队列
		exec.execute(new DelayListener(events));		
		
		// 从数据库加载配置信息
		ConfManager.getInstance().setDb(dbCfg); // 初始化数据库
		ConfManager.getInstance().loadCaptureConfigBySourceId(this.sourceDataId);
		this.delayAtomEventList = ConfManager.getInstance().getDelayAtomEventList();
		// 计划任务：更新
		final Runnable runLoad = new Runnable() {
			public void run() {
				ConfManager.getInstance().loadCaptureConfigBySourceId(sourceDataId);
				delayAtomEventList = ConfManager.getInstance().getDelayAtomEventList();
			}
		};
		// 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
		SERVICE.scheduleAtFixedRate(runLoad, initialDelay, period, TimeUnit.MINUTES);
	}

	@Override
	public void execute(Tuple tuple, BasicOutputCollector collector) {

		final NLMessage nlMessage = (NLMessage) tuple.getValue(0);
		final List<String> fields = nlMessage.getFields();
		final String key = nlMessage.getKey();
		com.nl.util.log.Log.info("[CaptureDelayAtomEventBolt] "+key);
		// 2、捕获
		for (final AtomEvent atomEvent : delayAtomEventList) {
			String strExpression = atomEvent.getExpression();
			final String[] expressionParams = atomEvent.getExpressionParam().split(GlobalConst.DEFAULT_SEPARATOR);
			//final String[] captureFields = atomEvent.getCaptureField().split(GlobalConst.DEFAULT_SEPARATOR);
			final String eventId=atomEvent.getEventId();

			LOG.info("-----1----strExpression:" + strExpression);
			// DELAY_LOCATION,30,XJAE:CLE:AIRPORT
			if (strExpression != null && strExpression.startsWith("DELAY_LOCATION")) {
				final String info[] = strExpression.split(GlobalConst.DEFAULT_SEPARATOR);
				String tablename = null;
				final long now=System.currentTimeMillis();
				int delayTime;
				if (info.length == 3) {
					if (CommonUtil.isNumeric(info[1])) {
						delayTime = Integer.parseInt(info[1]);
						tablename = info[2];
					} else {
						LOG.info("Worng Expression:" + strExpression);
						continue;
					}
				} else {
					LOG.info("Worng Expression:" + strExpression);
					continue;
				}
				String field = "";
				for (final String expressionParam : expressionParams) {
					field = field + fields.get(this.fieldsName.get(expressionParam));
				}
				final String k = key+eventId;
				final String v = this.redisCluster.get(k);
				// 判断是否在场景中
				if (this.redisCluster.hexists(tablename, field)) {
					if (v != null) {
						this.redisCluster.append(k,TRUE);
						//this.redisCluster.getSet(key, value)
					} else {
						// 捕获动态参数
						StringBuffer dynamicArgs = new StringBuffer();
						final int length = this.dynamicArgsFieldsNames.length;
						for (int i = 0; i < length; i++) {
							dynamicArgs.append(fields.get(this.fieldsName
									.get(this.dynamicArgsFieldsNames[i])));
							if (i != length - 1) {
								dynamicArgs.append(GlobalConst.DEFAULT_SEPARATOR);
							}
						}
						this.redisCluster.set(k, TRUE);
						events.put(new DelayEvent(key, eventId,
								this.storeTablename, field, now,
								TimeUnit.MINUTES.toMillis(delayTime),
								this.redisCluster, this.exprie,dynamicArgs.toString()));
					}
				} else {
					if (v != null) {
						this.redisCluster.append(k,FALSE);
					} else {
						continue;
					}
				}

				// ****************方案1*********************
				// 1、判断捕获事件为：DELAY_LOCATION（位置延迟事件） ? 2 : RETURN
				// 2、判断 用户是否已经在LOCATION ? 3 : 判断 【标志位】不存在 ? RETURN: 更新【标志位】
				// 3、(if 【标志位】不存在 ? 设置【标志位】&& 封装成对象存入延迟队列: 更新【标志位】) &&  将参数捕获存入Redis(链表结构保存)
				// 4、监听延迟队列，延时时间到了，判断【标志位】状态 捕获 ? isCapture = true : do nothing
				// 5、删除【标志位】

				// ****************方案2*********************
				// 1、判断捕获事件为：DELAY_LOCATION（位置延迟事件） ? 2 : RETURN
				// 2 、判断缓存【捕获过滤条件策略】
				// 2.1、判断是否设置【连续捕获过滤条件标志位】 ? 4 : 2.2
				// 2.2、判断 用户是否已经在LOCATION ? 3 : RETURN
				// 3、设置【连续捕获过滤条件标志位】
				// 4、将参数捕获存入Redis(链表结构保存)
				// 5、封装成对象存入延迟队列
				// 6、监听延迟队列，处理捕获结果
				// 7、删除【连续捕获过滤条件标志位】

			} else {
				LOG.info("Other Expression can not support:" + strExpression);
				continue;
			}
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
			// do nothing 
	}
}
