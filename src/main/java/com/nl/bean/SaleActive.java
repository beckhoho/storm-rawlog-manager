/**
 * @author: gsw
 * @version: 1.0
 * @CreateTime: 2015年12月4日 下午4:54:41
 * @Description: 无
 */
package com.nl.bean;

import java.io.Serializable;


public class SaleActive implements Serializable {

	private static final long serialVersionUID = 1L;
	private String activeId;// 活动编码
	private String stepId;// 活动步骤编码
	private String userGroupId;// 客户群编码
	private String userGroupRefresh;// 客户群刷新方式
	private String areaId;// 活动覆盖区域
	private String isCapture;// 是否捕获时机
	private String activeOppId;// 营销时机编码
	private String ruleExpres;// 规则表达式
	private String requestContent;// 请求参数报文
	private String requestTime;// 请求时间
	private String requestTimeStamp;// 请求时间戳
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
	public String getUserGroupId() {
		return userGroupId;
	}
	public void setUserGroupId(String userGroupId) {
		this.userGroupId = userGroupId;
	}
	public String getUserGroupRefresh() {
		return userGroupRefresh;
	}
	public void setUserGroupRefresh(String userGroupRefresh) {
		this.userGroupRefresh = userGroupRefresh;
	}
	public String getAreaId() {
		return areaId;
	}
	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}
	public String getIsCapture() {
		return isCapture;
	}
	public void setIsCapture(String isCapture) {
		this.isCapture = isCapture;
	}
	public String getActiveOppId() {
		return activeOppId;
	}
	public void setActiveOppId(String activeOppId) {
		this.activeOppId = activeOppId;
	}
	public String getRuleExpres() {
		return ruleExpres;
	}
	public void setRuleExpres(String ruleExpres) {
		this.ruleExpres = ruleExpres;
	}
	public String getRequestContent() {
		return requestContent;
	}
	public void setRequestContent(String requestContent) {
		this.requestContent = requestContent;
	}
	public String getRequestTime() {
		return requestTime;
	}
	public void setRequestTime(String requestTime) {
		this.requestTime = requestTime;
	}
	public String getRequestTimeStamp() {
		return requestTimeStamp;
	}
	public void setRequestTimeStamp(String requestTimeStamp) {
		this.requestTimeStamp = requestTimeStamp;
	}
	
}
