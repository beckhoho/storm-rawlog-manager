/**
 * @author: gsw
 * @version: 1.0
 * @CreateTime: 2015年12月4日 下午5:39:17
 * @Description: 无
 */
package com.nl.bean;

import java.io.Serializable;

public class User implements Serializable {

	private static final long serialVersionUID = 1L;
	private String userId;// 用户编码
	private String msisdn;// 手机号码

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

}
