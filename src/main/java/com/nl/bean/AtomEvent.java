/**
 * @author: gsw
 * @version: 1.0
 * @CreateTime: 2015年11月24日 下午6:31:17
 * @Description: 无
 */
package com.nl.bean;

public class AtomEvent {

	private String eventId;// 事件编码
	private String eventName;// 事件名称
	private String classCode;// 事件类型
	private String sourceId;// 数据源编码
	private String eventDesc;// 事件描述
	private String captureField;// 事件捕获字段
	private String expression;// 事件过滤表达式
	private String expressionParam;// 事件过滤表达式参数
	private String isRealize;// 是否实现
	private String inUse;// 是否在使用

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public String getClassCode() {
		return classCode;
	}

	public void setClassCode(String classCode) {
		this.classCode = classCode;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getEventDesc() {
		return eventDesc;
	}

	public void setEventDesc(String eventDesc) {
		this.eventDesc = eventDesc;
	}

	public String getCaptureField() {
		return captureField;
	}

	public void setCaptureField(String captureField) {
		this.captureField = captureField;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String getExpressionParam() {
		return expressionParam;
	}

	public void setExpressionParam(String expressionParam) {
		this.expressionParam = expressionParam;
	}

	public String getIsRealize() {
		return isRealize;
	}

	public void setIsRealize(String isRealize) {
		this.isRealize = isRealize;
	}

	public String getInUse() {
		return inUse;
	}

	public void setInUse(String inUse) {
		this.inUse = inUse;
	}

}
