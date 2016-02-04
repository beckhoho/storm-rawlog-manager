/**
 * @author: gsw
 * @version: 1.0
 * @CreateTime: 2015年12月23日 上午10:07:17
 * @Description: 无
 */
package com.nl.loadscenarios.configtable.bean;

import java.io.Serializable;

public class Lacci implements Serializable {

	private static final long serialVersionUID = 1L;

	private String redisTablename;
	private String lacci;
	private String ciName;

	public String getRedisTablename() {
		return redisTablename;
	}

	public void setRedisTablename(String redisTablename) {
		this.redisTablename = redisTablename;
	}

	public String getLacci() {
		return lacci;
	}

	public void setLacci(String lacci) {
		this.lacci = lacci;
	}

	public String getCiName() {
		return ciName;
	}

	public void setCiName(String ciName) {
		this.ciName = ciName;
	}

}
