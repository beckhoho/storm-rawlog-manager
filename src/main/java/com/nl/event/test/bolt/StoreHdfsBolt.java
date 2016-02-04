/**
 * @author: gsw
 * @version: 1.0
 * @CreateTime: 2015年11月3日 上午9:23:30
 * @Description: 无
 *//*
package com.nl.event.test.bolt;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import com.nl.util.hdfs.StoreController2;
import com.nl.util.hdfs.format.DefaultFileNameFormat;
import com.nl.util.hdfs.format.FileNameFormat;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

public class StoreHdfsBolt extends BaseRichBolt {

	private static final long serialVersionUID = 1L;
	private static final ScheduledExecutorService SERVICE = Executors
			.newSingleThreadScheduledExecutor();
	protected OutputCollector collector;
	protected transient FileSystem fs;
	protected transient Configuration hdfsConfig;
	protected transient Object writeLock;
	private transient FSDataOutputStream outputStream;

	private final String storePath;
	private final String prefix;
	private final String extension;

	private transient StoreController2 controller = null;
	private StringBuffer outStr;
	private FileNameFormat fileNameFormat;
	private final List<String> workingSet = new ArrayList<String>();
	private long COUNT = 0;
	private long ERROR = 0;

	public StoreHdfsBolt(String storePath, String prefix, String extension) {
		this.storePath = storePath;
		this.prefix = prefix;
		this.extension = extension;
	}

	@Override
	public void execute(Tuple tuple) {
		// tuple.getValue(0);

		String data = tuple.getString(0);
		if (data.startsWith("#")) {
			ERROR++;
			// com.nl.util.log.Log.error("err data:" + data);
			return;
		}
		String[] fields = data.split(" ");
		if (fields.length < 10) {
			ERROR++;
			// com.nl.util.log.Log.error("err data:" + data);
			return;
		}
		String date = fields[0];

		if (workingSet.isEmpty()) {
			init(date);
		}

		synchronized (this.writeLock) {
			if (this.controller.mark(date)) {
				append();// 追加
				this.controller.reset(date);// 重置
			}
		}
		this.outStr.append(data).append("\n");
		COUNT++;
		com.nl.util.log.Log.info("本次处理日期:" + date + ",第"
				+ (this.controller.getExecuteCount() + 1) + "条记录,总第" + COUNT
				+ "条记录,错误记录数:" + ERROR);
	}

	void append() {
		try {
			fs = FileSystem.get(this.hdfsConfig);
			com.nl.util.log.Log.debug("获取文件系统:" + fs);
			String monPath = this.controller.getDate().replaceAll("-", "")
					.substring(0, 6);
			Path path = new Path(fileNameFormat.getPath() + monPath
					+ File.separator
					+ fileNameFormat.getName(this.controller.getDate()));
			if (!fs.exists(path)) {
				com.nl.util.log.Log.debug("路径不存在,创建:" + path.toString());
				fs.createNewFile(path);
				Thread.sleep(10);
			} else {
				outputStream = fs.append(path);
				PrintWriter writer = new PrintWriter(outputStream);
				com.nl.util.log.Log.debug("开始追加:" + path.toString());
				writer.append(outStr.toString());
				writer.close();
				com.nl.util.log.Log.debug("追加完成:" + path.toString());
				com.nl.util.log.Log.debug("本次追加记录数:"
						+ (this.controller.getExecuteCount() - 1));
				com.nl.util.log.Log.debug("本次追加文件总大小:"
						+ this.outStr.toString().length() + " 字节");
				com.nl.util.log.Log.debug("当前文件总大小:"
						+ fs.getFileStatus(path).getLen() + " 字节");
				fs.close();
				com.nl.util.log.Log.debug("关闭文件系统");
				// Thread.sleep(10);
			}
		} catch (IOException | InterruptedException e) {
			com.nl.util.log.Log.error(e.getMessage());
		}
		outStr.setLength(0);// 清空
	}

	@Override
	public void prepare(Map conf, TopologyContext context,
			OutputCollector collector) {
		this.collector = collector;
		this.writeLock = new Object();
		outStr = new StringBuffer();
		fileNameFormat = new DefaultFileNameFormat()
				.withPath(storePath + File.separator).withPrefix(prefix)
				.withExtension(extension);

		// **********************************HDFS*******************************************
		this.hdfsConfig = new Configuration();
		// append
		this.hdfsConfig.setBoolean("dfs.support.append", true);
		this.hdfsConfig.set(
				"dfs.client.block.write.replace-datanode-on-failure.policy",
				"NEVER");
		this.hdfsConfig.set(
				"dfs.client.block.write.replace-datanode-on-failure.enable",
				"true");

		this.hdfsConfig.set("fs.hdfs.impl",
				org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
		this.hdfsConfig.set("fs.file.impl",
				org.apache.hadoop.fs.LocalFileSystem.class.getName());
		this.hdfsConfig.addResource(conf.get("hdfs.hdfssite.file.path")
				.toString());
		this.hdfsConfig.addResource(conf.get("hdfs.coresite.file.path")
				.toString());
		this.hdfsConfig
				.set("fs.defaultFS", conf.get("fs.defaultFS").toString());
		this.hdfsConfig.set("dfs.nameservices", conf.get("dfs.nameservices")
				.toString());
		this.hdfsConfig.set("dfs.ha.namenodes."
				+ conf.get("dfs.nameservices").toString(),
				conf.get("dfs.ha.namenodes").toString());

		for (int i = 0; i < Integer.parseInt(conf.get("dfs.ha.namenodes.size")
				.toString()); i++) {
			this.hdfsConfig.set(
					"dfs.namenode.rpc-address."
							+ conf.get("dfs.nameservices").toString()
							+ "."
							+ conf.get("dfs.ha.namenodes").toString()
									.split(",")[i],
					conf.get("dfs.ha.namenodes.addr").toString().split(",")[i]);
		}
		this.hdfsConfig.set(
				"dfs.client.failover.proxy.provider."
						+ conf.get("dfs.nameservices").toString(),
				conf.get("dfs.client.failover.proxy.provider").toString());

		try {
			this.fs = FileSystem.get(this.hdfsConfig);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 计划任务
		final Runnable append = new Runnable() {
			public void run() {
				synchronized (writeLock) {
					com.nl.util.log.Log.debug("执行计划任务");
					if (controller != null) {
						append();
						controller.reset(null);
					}
				}
			}
		};
		// 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
		SERVICE.scheduleAtFixedRate(append, 10, 15, TimeUnit.MINUTES);
	}

	void init(String date) {
		outStr.setLength(0);
		workingSet.add(date);
		this.controller = new StoreController2(date);
		// this.controller.setStartTime(System.currentTimeMillis());
		com.nl.util.log.Log.debug("初始化,初始化写入数据的日期:" + date);
		com.nl.util.log.Log.debug("工作集大小:" + workingSet.size());
		com.nl.util.log.Log.debug("工作集存在的日期记录:" + workingSet.get(0));
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {

	}

}
*/