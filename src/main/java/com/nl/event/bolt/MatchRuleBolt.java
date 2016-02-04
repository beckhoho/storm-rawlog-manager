/**
* @author: gsw
* @version: 1.0
* @CreateTime: 2015年12月5日 下午1:15:40
* @Description: 无
*/
package com.nl.event.bolt;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import neu.sxc.expression.Expression;
import neu.sxc.expression.ExpressionFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.JedisCluster;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import com.nl.event.conf.ConfManager;
import com.nl.bean.ActiveOpp;
import com.nl.bean.RuleInstance;
import com.nl.bean.SaleActive;
import com.nl.bean.SaleActiveRuleParam;
import com.nl.bean.User;
import com.nl.util.config.DBCfg;
import com.nl.util.config.RedisClusterCfg;
import com.nl.util.redis.RedisCluster;

public class MatchRuleBolt extends BaseBasicBolt {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(MatchRuleBolt.class);
	private static final ExpressionFactory EXPRESSIONFACTORY = ExpressionFactory.getInstance();
	private static final Fields FIELDS=new Fields("activeOpp");
	private static final String NULL="";
	private JedisCluster redisCluster;
	// 构造函数参数
	private final RedisClusterCfg redisCfg;
	private final String tablename;
	private final DBCfg dbCfg;

	/**
	 * 
	 * @param dbCfg 数据库配置
	 * @param redisCfg Redis配置
	 * @param tablename 表名
	 */
	public MatchRuleBolt(final DBCfg dbCfg, final RedisClusterCfg redisCfg,final String tablename) {
		LOG.info("instancing...");
		this.dbCfg = dbCfg;
		this.redisCfg = redisCfg;
		this.tablename=tablename;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(Map stormConf, TopologyContext context) {
		// 初始化redis
		RedisCluster.getRedisCluster().setCfg(redisCfg);
		this.redisCluster = RedisCluster.getRedisCluster().getInstance();
		ConfManager.getInstance().setDb(dbCfg); // 初始化数据库
	}

	@SuppressWarnings("unchecked")
	@Override
	public void execute(Tuple tuple, BasicOutputCollector collector) {
		// 获取tuple数据
		final SaleActive saleActive = (SaleActive) tuple.getValue(0);
		final List<RuleInstance> ruleInstanceList = (List<RuleInstance>) tuple.getValue(1);
		final Map<String, List<SaleActiveRuleParam>> ruleInstancesParamsMap=(Map<String, List<SaleActiveRuleParam>>) tuple.getValue(2);
		final User user = (User) tuple.getValue(3);
		final String msisdn=user.getMsisdn();
		

		String saleActiveExpression=saleActive.getRuleExpres();// 营销活动表达式(一级表达式) (1601 || 1602);
		LOG.info("------1--------saleActiveExpression:"+saleActiveExpression);
		// 拼接表达式
		for(final RuleInstance ruleInstance:ruleInstanceList){			
			final String ruleInstanceId=ruleInstance.getRuleInstanceId();// 规则实例编码
			final String paramExpresDetail=ruleInstance.getParamExpresDetail();// 事件表达式(二级表达式)
			final String eventId=ruleInstance.getEventId();// 原子事件		
			saleActiveExpression=saleActiveExpression.replaceAll(ruleInstanceId, paramExpresDetail);		
			LOG.info("-------2-------saleActiveExpression:"+saleActiveExpression);
			final List<SaleActiveRuleParam> ruleInstanceParams=ruleInstancesParamsMap.get(ruleInstanceId);// 规则参数
			for(final SaleActiveRuleParam param:ruleInstanceParams){
				final String eventValue= this.redisCluster.hget(this.tablename+msisdn, eventId);
				 LOG.info("--------------this.tablename+msisdn:"+this.tablename+msisdn+":"+eventValue);
				if (eventValue == null) {
					return;
				} else {
					saleActiveExpression = saleActiveExpression.replaceAll(param.getParamId(), eventValue);
				}
			}	
		}
		LOG.info("---------3-----saleActiveExpression:"+saleActiveExpression);
		if (saleActiveExpression.equals(saleActive.getRuleExpres())) return;
		final Expression expression = EXPRESSIONFACTORY.getExpression(saleActiveExpression);
		expression.lexicalAnalysis();// 词法分析
		LOG.info("--------1------is true:"+expression.evaluate().getBooleanValue());
		if (expression.evaluate().getBooleanValue()) {
			LOG.info("-------2-------is true:"+expression.evaluate().getBooleanValue());
			if ("1".equals(saleActive.getIsCapture())) {
				LOG.info("--------------emit:"+msisdn);
				final Calendar calendar=Calendar.getInstance();
				final long currentTime=calendar.getTimeInMillis();
				// 输出的tuple
				final ActiveOpp activeOpp=new ActiveOpp();
				//activeOpp.setMonNum(calendar.get(Calendar.YEAR)+NULL+(calendar.get(Calendar.MONTH)+1));
				activeOpp.setMonNum((calendar.get(Calendar.MONTH)+1));
				activeOpp.setChanceId(currentTime);
				activeOpp.setEventType(NULL);
				activeOpp.setUserId(user.getUserId());
				activeOpp.setMsisdn(msisdn);
				activeOpp.setBrandId(NULL);
				activeOpp.setUserGroupId(saleActive.getUserGroupId());
				activeOpp.setActiveId(saleActive.getActiveId());
				activeOpp.setStepId(saleActive.getStepId());
				activeOpp.setActiveOppId(saleActive.getActiveOppId());
				activeOpp.setRuleId(NULL);
				activeOpp.setRuleInstanceId(NULL);
				activeOpp.setEvtArgList(NULL);
				//activeOpp.setInsertTime(currentTime);
				activeOpp.setAcceptTime(currentTime);
				activeOpp.setDealStat("0");		
				collector.emit(new Values(activeOpp));//collector.emit(streams.get(Math.abs(msisdn.hashCode() % this.STREAMSIZE)),new Values(activeOpp));	
			} 
		}
		
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(FIELDS);//for (String stream : streams) declarer.declareStream(stream, new Fields("activeOpp"));
	}

	@Override
	public void cleanup() {
		this.redisCluster.close();
	}
}
/*LOG.info("--------1----------ActiveId:" + saleActive.getActiveId());
for (RuleInstance ruleInstance : ruleInstanceList) {
	LOG.info("--------2----------RuleInstanceId:" + ruleInstance.getRuleInstanceId());
	List<SaleActiveRuleParam> params = ruleInstancesParamsMap.get(ruleInstance.getRuleInstanceId());
	for (SaleActiveRuleParam p : params) {
		LOG.info("--------3----------ParamId:" + p.getRuleInstanceId()
				+ ":" + p.getParamId() + ":" + p.getParamValue());
	}
}*/