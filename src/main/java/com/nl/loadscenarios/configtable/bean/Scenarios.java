/**
 * @author: gsw
 * @version: 1.0
 * @CreateTime: 2015年12月23日 上午10:07:17
 * @Description: 无
 */
package com.nl.loadscenarios.configtable.bean;

import java.io.Serializable;

public class Scenarios implements Serializable {

	private static final long serialVersionUID = 1L;

	private String coverScenariosId;
	private String coverSenarios;
	private String redisTablename;
	private String inUse;
	private String isConfigured;
	private String sql;

	public String getCoverScenariosId() {
		return coverScenariosId;
	}

	public void setCoverScenariosId(String coverScenariosId) {
		this.coverScenariosId = coverScenariosId;
	}

	public String getCoverSenarios() {
		return coverSenarios;
	}

	public void setCoverSenarios(String coverSenarios) {
		this.coverSenarios = coverSenarios;
	}

	public String getRedisTablename() {
		return redisTablename;
	}

	public void setRedisTablename(String redisTablename) {
		this.redisTablename = redisTablename;
	}

	public String getInUse() {
		return inUse;
	}

	public void setInUse(String inUse) {
		this.inUse = inUse;
	}

	public String getIsConfigured() {
		return isConfigured;
	}

	public void setIsConfigured(String isConfigured) {
		this.isConfigured = isConfigured;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

}
