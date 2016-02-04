/**
 * @author: gsw
 * @version: 1_0
 * @CreateTime: 2016年1月15日 下午3:19:11
 * @Description: 无
 */
package com.nl.bean;

import java.io.Serializable;

public class ParsedSdcData implements Serializable {

	private static final long serialVersionUID = 1L;
	private String date;
	private String time;
	private String body;
	/*private String WT_co_f;
	private String WT_mobile;
	private String c_ip;
	private String WT_gc;
	private String WT_branch;
	private String cs_uri_stem;
	private String WT_event;
	private String WT_pn_sku;
	private String WT_si_n;
	private String WT_si_x;
	private String WT_nv;
	private String WT_ac;
	private String WT_oss;
	private String WT_mc_id;
	private String WT_errCode;*/
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	

	
}
