/**
 * @author: gsw
 * @version: 1.0
 * @CreateTime: 2016年1月12日 下午2:27:44
 * @Description: 无
 */
package com.nl.util.hdfs;

public class CountController implements Comparable<CountController> {
	private String date;
	private int count = 100;
	private int executeCount = 0;// 已执行count
	//private long timer = 600000;
	//private long startTime=System.currentTimeMillis();

	public CountController(String date) {
		this.date = date;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getExecuteCount() {
		return executeCount;
	}
	/**
	 * 标记count
	 * @return
	 */
	public boolean mark() {
		this.executeCount++;
		return this.executeCount > this.count;

	}

	public void reset() {
		this.executeCount = 0;
	}

	@Override
	public int compareTo(CountController o) {
		return this.date.equals(o.getDate()) ? 1 : 0;
	}

}
