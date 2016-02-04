/**
 * @author: gsw
 * @version: 1.0
 * @CreateTime: 2015年11月30日 下午4:59:12
 * @Description: 无
 */
package com.nl.util.config;

import java.io.Serializable;

import storm.kafka.BrokerHosts;
import storm.kafka.StringScheme;
import storm.kafka.ZkHosts;
import backtype.storm.spout.Scheme;

public class KafkaCfg implements Serializable, Comparable<KafkaCfg> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String topic;
	private String spoutId = "gsw";
	private String[] zkServers = { "supervisor1", "supervisor2",
			"supervisor3", "supervisor4", "supervisor5", "supervisor6",
			"supervisor7" };
	private Scheme scheme = new StringScheme();;
	private String zkRoot = "/xj_bigdata";;
	private int zkPort = 2181;
	private BrokerHosts brokerHosts = new ZkHosts(
			"192.168.1.55:2181,192.168.1.56:2181,192.168.1.57:2181,192.168.1.58:2181,192.168.1.59:2181,192.168.1.60:2181,192.168.1.61:2181");

	public BrokerHosts getBrokerHosts() {
		return brokerHosts;
	}

	public void setBrokerHosts(BrokerHosts brokerHosts) {
		this.brokerHosts = brokerHosts;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getSpoutId() {
		return spoutId;
	}

	public void setSpoutId(String spoutId) {
		this.spoutId = spoutId;
	}

	public String[] getZkServers() {
		return zkServers;
	}

	public void setZkServers(String[] zkServers) {
		this.zkServers = zkServers;
	}

	public Scheme getScheme() {
		return scheme;
	}

	public void setScheme(Scheme scheme) {
		this.scheme = scheme;
	}

	public String getZkRoot() {
		return zkRoot;
	}

	public void setZkRoot(String zkRoot) {
		this.zkRoot = zkRoot;
	}

	public int getZkPort() {
		return zkPort;
	}

	public void setZkPort(int zkPort) {
		this.zkPort = zkPort;
	}

	@Override
	public int compareTo(KafkaCfg o) {
		return 0;
	}

}
