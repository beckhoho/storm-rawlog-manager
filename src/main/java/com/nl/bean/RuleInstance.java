/**
 * @author: gsw
 * @version: 1.0
 * @CreateTime: 2015年12月4日 下午4:49:17
 * @Description: 无
 */
package com.nl.bean;

import java.io.Serializable;

public class RuleInstance implements Serializable {

	private static final long serialVersionUID = 1L;
	private String activeId;// 活动编码
	private String ruleInstanceId;// 规则实例编码
	private String ruleId;// 规则编码（目前无用）
	private String eventId;// 事件编码
	private String paramExpres;// 参数表达式
	private String paramExpresDetail;// 参数表达式明细

	public String getActiveId() {
		return activeId;
	}

	public void setActiveId(String activeId) {
		this.activeId = activeId;
	}

	public String getRuleInstanceId() {
		return ruleInstanceId;
	}

	public void setRuleInstanceId(String ruleInstanceId) {
		this.ruleInstanceId = ruleInstanceId;
	}

	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public String getParamExpres() {
		return paramExpres;
	}

	public void setParamExpres(String paramExpres) {
		this.paramExpres = paramExpres;
	}

	public String getParamExpresDetail() {
		return paramExpresDetail;
	}

	public void setParamExpresDetail(String paramExpresDetail) {
		this.paramExpresDetail = paramExpresDetail;
	}

}
