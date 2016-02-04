/**
 * @author: gsw
 * @version: 1.0
 * @CreateTime: 2015年12月4日 上午11:18:58
 * @Description: 无
 */
package com.nl.bean;

import java.io.Serializable;
import java.util.Map;

/**
 * NL原子事件
 *
 */
public class NLAtomEvent implements Serializable {

	private static final long serialVersionUID = 1L;
	/**  key column */
	private String key;
	/**  atom event <eventId,value> */
	private Map<String, String> values;
	private String dynamicArgs;// 
	
	
	public String getDynamicArgs() {
		return dynamicArgs;
	}
	public void setDynamicArgs(String dynamicArgs) {
		this.dynamicArgs = dynamicArgs;
	}
	/**
	 * 
	 * @return
	 */
	public String getKey() {
		return key;
	}
	/**
	 * 
	 * @param key
	 */
	public void setKey(final String key) {
		this.key = key;
	}
	/**
	 * 
	 * @return
	 */
	public Map<String, String> getValues() {
		return values;
	}
	/**
	 * 
	 * @param values
	 */
	public void setValues(final Map<String, String> values) {
		this.values = values;
	}

}
