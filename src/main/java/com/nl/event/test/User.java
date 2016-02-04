/**
 * @author: gsw
 * @version: 1.0
 * @CreateTime: 2015年12月4日 下午5:39:17
 * @Description: 无
 */
package com.nl.event.test;

import java.io.Serializable;

public class User implements Serializable {

	private static final long serialVersionUID = 1L;
	private  String userId;// 用户编码
	private String msisdn;// 手机号码
	public User(final String userId,final String msisdn){
		this.msisdn=msisdn;
		this.userId=userId;
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

	public static void main(String[] args) {
		final User u=new User("111","222");
		u.setMsisdn("gggg");
		System.out.println(u.getUserId());
		System.out.println(u.getMsisdn());
		
	}
}
