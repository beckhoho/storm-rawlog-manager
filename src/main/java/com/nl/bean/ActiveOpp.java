/**
 * @author: gsw
 * @version: 1.0
 * @CreateTime: 2015年12月4日 下午5:13:32
 * @Description: 无
 */
package com.nl.bean;

import java.io.Serializable;

public class ActiveOpp implements Serializable {

	private static final long serialVersionUID = 1L;
	private int monNum;// 月分区标识
	private long chanceId;// 机会编码
	private String eventType;// 事件类型
	private String userId;// 用户标识
	private String msisdn;// 手机号码
	private String brandId;
	private String userGroupId;// 用户群标识
	private String activeId;// 活动编码
	private String stepId;// 步骤编码
	private String activeOppId;// 营销时机编码
	private String ruleId;// 规则编码
	private String ruleInstanceId;// 规则
	private String evtArgList;// 事件参数
	private long insertTime;// 机会匹配
	private long acceptTime;// 信令接收
	private String dealStat;// 处理状态
	public int getMonNum() {
		return monNum;
	}
	public void setMonNum(int monNum) {
		this.monNum = monNum;
	}
	public long getChanceId() {
		return chanceId;
	}
	public void setChanceId(long chanceId) {
		this.chanceId = chanceId;
	}
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getMsisdn() {
		return msisdn;
	}
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	public String getBrandId() {
		return brandId;
	}
	public void setBrandId(String brandId) {
		this.brandId = brandId;
	}
	public String getUserGroupId() {
		return userGroupId;
	}
	public void setUserGroupId(String userGroupId) {
		this.userGroupId = userGroupId;
	}
	public String getActiveId() {
		return activeId;
	}
	public void setActiveId(String activeId) {
		this.activeId = activeId;
	}
	public String getStepId() {
		return stepId;
	}
	public void setStepId(String stepId) {
		this.stepId = stepId;
	}
	public String getActiveOppId() {
		return activeOppId;
	}
	public void setActiveOppId(String activeOppId) {
		this.activeOppId = activeOppId;
	}
	public String getRuleId() {
		return ruleId;
	}
	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}
	public String getRuleInstanceId() {
		return ruleInstanceId;
	}
	public void setRuleInstanceId(String ruleInstanceId) {
		this.ruleInstanceId = ruleInstanceId;
	}
	public String getEvtArgList() {
		return evtArgList;
	}
	public void setEvtArgList(String evtArgList) {
		this.evtArgList = evtArgList;
	}
	public long getInsertTime() {
		return insertTime;
	}
	public void setInsertTime(long insertTime) {
		this.insertTime = insertTime;
	}
	public long getAcceptTime() {
		return acceptTime;
	}
	public void setAcceptTime(long acceptTime) {
		this.acceptTime = acceptTime;
	}
	public String getDealStat() {
		return dealStat;
	}
	public void setDealStat(String dealStat) {
		this.dealStat = dealStat;
	}
	
}
