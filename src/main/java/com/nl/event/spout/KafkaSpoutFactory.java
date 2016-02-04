/**
 * @author: gsw
 * @version: 1.0
 * @CreateTime: 2015年7月24日 下午2:52:38
 * @Modified: 2015年10月20日 gsw
 * @Description: 无
 */
package com.nl.event.spout;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import backtype.storm.spout.SchemeAsMultiScheme;

import com.nl.util.config.KafkaCfg;

public class KafkaSpoutFactory {

	// private final SpoutConfig spoutConf;
	private final KafkaSpout kafkaSpout;
	private final KafkaCfg cfg;
	private final static Logger LOG = LoggerFactory
			.getLogger(KafkaSpoutFactory.class);

	public KafkaSpoutFactory(final KafkaCfg cfg) {
		this.cfg = cfg;
		SpoutConfig spoutConf = new SpoutConfig(this.cfg.getBrokerHosts(),
				this.cfg.getTopic(), this.cfg.getZkRoot(),
				this.cfg.getSpoutId());
		spoutConf.scheme = new SchemeAsMultiScheme(this.cfg.getScheme());
		// 该配置是指，如果该Topology因故障停止处理，下次正常运行时是否从Spout对应数据源Kafka中的该订阅Topic的起始位置开始读取，如果forceFromStart=true，
		// 则之前处理过的Tuple还要重新处理一遍，否则会从上次处理的位置继续处理，保证Kafka中的Topic数据不被重复处理，是在数据源的位置进行状态记录
		//
		//spoutConf.ignoreZkOffsets = false;// ignoreZkOffsets
		spoutConf.forceFromStart= false;
		spoutConf.zkServers = Arrays.asList(this.cfg.getZkServers());
		spoutConf.zkPort = this.cfg.getZkPort();
		spoutConf.socketTimeoutMs = 100000;
		this.kafkaSpout = new KafkaSpout(spoutConf);
	}

	/**
	 * return kafka spout instance
	 * 
	 * @return
	 */
	public KafkaSpout getKafkaSpout() {
		return this.kafkaSpout;
	}
}