/**
* @author: gsw
* @version: 1.0
* @CreateTime: 2015年12月22日 上午10:40:58
* @Description: 无
*/
package com.nl.bean;

import java.io.Serializable;
import java.util.List;

public class NLMessage  implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String key;
	private String secondKey;
	private  List<String> fields;
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getSecondKey() {
		return secondKey;
	}
	public void setSecondKey(String secondKey) {
		this.secondKey = secondKey;
	}
	public List<String> getFields() {
		return fields;
	}
	public void setFields(List<String> fields) {
		this.fields = fields;
	}
	
}
