/**
 * @author: gsw
 * @version: 1.0
 * @CreateTime: 2015年12月3日 下午7:08:47
 * @Description: 无
 */
package com.nl.bean;

public class SourceData {

	private String sourceId;// 数据源编码
	private String sourceName;// 数据源名称
	private String sourceDesc;// 数据源描述
	private String fieldsList;// 数据源字段定义
	private String fieldDelimiter;// 数据分隔符
	private int fieldsSize;// 数据源字段长度
	private int keyFieldIndex;// 数据源KEY字段位置
	private String keyField;// 数据源KEY字段名称
	private String instance;// 数据实例
	public String getSourceId() {
		return sourceId;
	}
	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}
	public String getSourceName() {
		return sourceName;
	}
	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}
	public String getSourceDesc() {
		return sourceDesc;
	}
	public void setSourceDesc(String sourceDesc) {
		this.sourceDesc = sourceDesc;
	}
	public String getFieldsList() {
		return fieldsList;
	}
	public void setFieldsList(String fieldsList) {
		this.fieldsList = fieldsList;
	}
	public String getFieldDelimiter() {
		return fieldDelimiter;
	}
	public void setFieldDelimiter(String fieldDelimiter) {
		this.fieldDelimiter = fieldDelimiter;
	}
	public int getFieldsSize() {
		return fieldsSize;
	}
	public void setFieldsSize(int fieldsSize) {
		this.fieldsSize = fieldsSize;
	}
	public int getKeyFieldIndex() {
		return keyFieldIndex;
	}
	public void setKeyFieldIndex(int keyFieldIndex) {
		this.keyFieldIndex = keyFieldIndex;
	}
	public String getKeyField() {
		return keyField;
	}
	public void setKeyField(String keyField) {
		this.keyField = keyField;
	}
	public String getInstance() {
		return instance;
	}
	public void setInstance(String instance) {
		this.instance = instance;
	}
	
	
	

}
