/**
 * @author: gsw
 * @version: 1.0
 * @CreateTime: 2015年12月10日 下午9:52:45
 * @Description: 无
 */
package com.nl.bean;

import java.io.Serializable;

public class SaleActiveRuleParam implements Serializable {

	private static final long serialVersionUID = 1L;
	private String activeId;// 活动编码
	private String ruleInstanceId;// 规则实例编码
	private String paramId;// 参数编码
	private String operId;// 运算符
	private String paramValue;// 参数值

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


	public String getParamId() {
		return paramId;
	}

	public void setParamId(String paramId) {
		this.paramId = paramId;
	}

	public String getOperId() {
		return operId;
	}

	public void setOperId(String operId) {
		this.operId = operId;
	}

	public String getParamValue() {
		return paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}

}
