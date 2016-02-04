/**
 * @author: gsw
 * @version: 1.0
 * @CreateTime: 2016年1月3日 上午9:43:35
 * @Description: 无
 */
package com.nl.event.bolt;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import com.nl.bean.ParsedSdcData;
import com.nl.util.GlobalConst;
import com.nl.util.CommonUtil;
import com.nl.util.db.DBAccess;
import com.nl.util.hdfs.CountController;
import com.nl.util.hdfs.format.DefaultFileNameFormat;
import com.nl.util.hdfs.format.FileNameFormat;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

public class SdcStoreHdfsBolt extends BaseRichBolt {

	private static final long serialVersionUID = 1L;
	// 计划任务调度
	private static final ScheduledExecutorService SERVICE = Executors.newSingleThreadScheduledExecutor(); 
	// 统计信息表
	private static final String INSERTABLE="insert into bigdata.RPT_SDC_TOPO_INFO (topo_id,current_count,current_append_cnt,current_append_size,current_cost_time,insert_time,ip,host) values (";	
	// 文件操作
	protected transient FileSystem hdfs;
	protected transient Configuration hdfsConf;
	protected transient Object writeLock;
	private transient FSDataOutputStream outputStream;
	private final String storePath, prefix, extension;
	private FileNameFormat fileNameFormat;
	private final Map<String,CountController> workingSpace =new HashMap<String,CountController>();
	private final Map<String,StringBuffer> outputSpace =new HashMap<String,StringBuffer >();
	// 统计信息
	private long COUNT = 0, SIZE = 0, CNT = 0,LASTCOUNT=9999, BEGIN;
	private String IP,HOST;
	// 数据库操作
	private  DBAccess dbAccess;
	private Connection conn = null;
	
	// 全局配置文件
	private Map<String,Object> config;
	
	protected OutputCollector collector;
	
	/**
	 * 构造方法:存储路径,文件前缀,文件后缀
	 * @param storePath
	 * @param prefix
	 * @param extension
	 */
	public SdcStoreHdfsBolt(String storePath, String prefix, String extension) {
		this.storePath = storePath;
		this.prefix = prefix;
		this.extension = extension;
	}

	@Override
	public void execute(Tuple tuple) {
		final ParsedSdcData data = (ParsedSdcData) tuple.getValue(0);
		final String date = data.getDate();
		final String time = data.getTime();
		// 
		init(date);
		// synchronized writeLock,while mark return true do append
		synchronized (this.writeLock) {
			if (this.workingSpace.get(date).mark()) {
				append(date);
			}
		}
		// put data to this date outSpace
		outputSpace.put(date, this.outputSpace.get(date).append(data.getBody()).append("\n"));
		COUNT++;
		com.nl.util.log.Log.info("本次处理:"+date+" "+time+",第" + (this.workingSpace.get(date).getExecuteCount())+"条记录,总第"+COUNT+"条记录");
		collector.ack(tuple);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void prepare(Map conf, TopologyContext context,OutputCollector collector) {
		//context.getStormId();
		this.collector = collector;
		this.writeLock = new Object();// 文件写锁
		fileNameFormat = new DefaultFileNameFormat().withPath(this.storePath + File.separator).withPrefix(this.prefix).withExtension(this.extension);
		// 统计信息
		BEGIN=System.currentTimeMillis();
		final InetAddress netAddress = CommonUtil.getInetAddress();
		IP = CommonUtil.getHostIp(netAddress);
		HOST = CommonUtil.getHostName(netAddress);
		// **********************************HDFS配置*******************************************
		this.hdfsConf = new Configuration();
	    this.hdfsConf.addResource(conf.get("hdfs.hdfssite.file.path").toString());
		this.hdfsConf.addResource(conf.get("hdfs.coresite.file.path").toString());
		this.hdfsConf.set("fs.hdfs.impl",org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
		this.hdfsConf.set("fs.file.impl",org.apache.hadoop.fs.LocalFileSystem.class.getName());
		this.hdfsConf.set("fs.defaultFS", conf.get("fs.defaultFS").toString());
		this.hdfsConf.set("dfs.nameservices", conf.get("dfs.nameservices").toString());
		this.hdfsConf.set("dfs.ha.namenodes." + conf.get("dfs.nameservices").toString(),conf.get("dfs.ha.namenodes").toString());
		for (int i = 0; i < Integer.parseInt(conf.get("dfs.ha.namenodes.size").toString()); i++) {
			this.hdfsConf.set("dfs.namenode.rpc-address." + conf.get("dfs.nameservices").toString() + "." + conf.get("dfs.ha.namenodes").toString().split(",")[i],conf.get("dfs.ha.namenodes.addr").toString().split(",")[i]);
		}
		this.hdfsConf.set("dfs.client.failover.proxy.provider." + conf.get("dfs.nameservices").toString(),conf.get("dfs.client.failover.proxy.provider").toString());
		try {
			this.hdfs = FileSystem.get(this.hdfsConf);
		} catch (IOException e) {
			//e.printStackTrace();
			com.nl.util.log.Log.error("获取文件系统失败:"+e.getMessage());
		}
		// **********************************DB*******************************************
		this.config=conf;
		connectDB(conf);
		/*try {
			Class.forName(conf.get("gbase.db.driver").toString());
			conn = DriverManager.getConnection(conf.get("gbase.db.url").toString(), conf.get("gbase.db.username").toString(), conf.get("gbase.db.password").toString());
			if (conn != null) this.dbAccess = new DBAccess(conn);
		} catch (ClassNotFoundException | SQLException e) {
			//e.printStackTrace();
			com.nl.util.log.Log.error("连接数据库失败:"+e.getMessage());
		}*/
		
		// 计划任务： 每隔15分钟会强制把缓存的数据刷入HDFS，防止kafka数据断了，缓存的数据一直无法写入
		final Runnable append = new Runnable() {
			public void run() {
				com.nl.util.log.Log.debug("超时-执行计划任务,将的数据写入HDFS");
				forceAppend();// 强制写入
				if (conn == null) connectDB(config);
				if (LASTCOUNT != COUNT) {
					com.nl.util.log.Log.debug("超时-执行计划任务,将统计信息存入数据库");
					saveStatInfo();// 统计信息入库
				} else {
					com.nl.util.log.Log.debug("超时-长时间无数据!!!");
				}
				
			}
		};
		// 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
		SERVICE.scheduleAtFixedRate(append, 10,15,TimeUnit.MINUTES);
	}

	@Override
	public void cleanup() {
		com.nl.util.log.Log.debug("发生kill或异常退出");
		forceAppend();// 强制写入
		com.nl.util.log.Log.debug("当kill时/异常退出时数据写入HDFS完成");
		removeAllSpace();
		com.nl.util.log.Log.debug("清除Space");
		com.nl.util.log.Log.debug("bolt将会被kill,输出统计信息!!!" + "\n=============="
				+ new Date() + "==============" + "\n总处理记录数:\t" + COUNT
				+ "\n写入次数:\t" + CNT + "\n写入文件总大小:" + SIZE + "\n总处理时间:\t"
				+ (System.currentTimeMillis() - BEGIN) / 1000 + "秒"
				+ "\n========================================================");
		if (conn == null) connectDB(config);
		com.nl.util.log.Log.debug("bolt将会被kill,输出统计到数据库！！！");
		saveStatInfo();// 统计信息入库
		try {
			if (conn != null)
				conn.close();
			dbAccess.closeSelect();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// do nothing
	}
	
	/**
	 * connect db
	 * @param conf
	 */
	void connectDB(Map<String,Object> conf){
		try {
			Class.forName(conf.get("gbase.db.driver").toString());
			conn = DriverManager.getConnection(conf.get("gbase.db.url").toString(), conf.get("gbase.db.username").toString(), conf.get("gbase.db.password").toString());
			if (conn != null) this.dbAccess = new DBAccess(conn);
		} catch (ClassNotFoundException | SQLException e) {
			//e.printStackTrace();
			com.nl.util.log.Log.error("连接数据库失败:"+e.getMessage());
		}
	}
	/**
	 * append data to this date data to HDFS
	 * NOTE:should close fs after append and get fs before append
	 * @param date
	 */
	protected void append(String date) {
		final CountController controller = this.workingSpace.get(date);
		final String out = this.outputSpace.get(date).toString();
		if (out.length() > 0) {
			try {
				hdfs = FileSystem.get(this.hdfsConf);
				com.nl.util.log.Log.debug("获取文件系统:" + hdfs.getName());
				String monPath = date.replaceAll("-", "").substring(0, 6);
				Path path = new Path(fileNameFormat.getPath() + monPath + File.separator + fileNameFormat.getName(date));
				if (!hdfs.exists(path)) {
					com.nl.util.log.Log.debug("路径不存在,创建:" + path.toString());
					hdfs.createNewFile(path);
					Thread.sleep(10);
				} else {
					outputStream = hdfs.append(path);
					PrintWriter writer = new PrintWriter(outputStream);
					com.nl.util.log.Log.debug("开始追加:" + path.toString());
					writer.append(out);
					writer.close();
					com.nl.util.log.Log
							.debug("\n=============="
									+ new Date()
									+ "=============="
									+ "\n追加完成:\t"
									+ path.toString()
									+ "\n本次追加记录数:\t"
									+ (controller.getExecuteCount() - 1)
									+ "\n本次追加文件总大小:\t"
									+ out.length()
									+ " 字节"
									+ "\n当前文件总大小:\t"
									+ hdfs.getFileStatus(path).getLen()
									+ " 字节"
									+ "\n========================================================");
					hdfs.close();
					com.nl.util.log.Log.debug("关闭文件系统\n");
					SIZE = SIZE + out.length();
					CNT++;
					Thread.sleep(10);
				}
			} catch (IOException | InterruptedException e) {
				com.nl.util.log.Log.error(e.getMessage());
			}
		} else {
			com.nl.util.log.Log.debug("日期："+date+",文件大小为 0,取消追加操作!!!");
		}
		workingSpace.put(date, new CountController(date));
		outputSpace.put(date, new StringBuffer());
	}
	
	/**
	 * 强制将缓存中的数据写入hdfs
	 */
	protected void forceAppend() {
		synchronized (writeLock) {
			for (String date : outputSpace.keySet()) {
				append(date);
			}
		}
	}
	
	// if workSpace not exist this input date data ,put to workSpace and create this date outSpace
	// clean the space
	protected void init(String date) {
		if (workingSpace != null && outputSpace != null) {
			if (!workingSpace.containsKey(date)) {
				workingSpace.put(date, new CountController(date));
				outputSpace.put(date, new StringBuffer());
				com.nl.util.log.Log.debug("加入工作集的日期:" + date);
				com.nl.util.log.Log.debug("工作集大小:" + workingSpace.size());
				cleanSpace();
			}
		}
	}
	
	/**
	 * 移除所有的缓存
	 */
	protected void removeAllSpace(){
		if (workingSpace!=null) workingSpace.clear();
		if (outputSpace!=null) outputSpace.clear();
	}
	/**
	 * remove the old date to claen the space
	 */
	protected void cleanSpace(){
		final String last6day =CommonUtil.getdate(-6), last5day =CommonUtil.getdate(-5),last4day =CommonUtil.getdate(-4), last3day =CommonUtil.getdate(-4);
		if (workingSpace!=null){
			if (workingSpace.size()>=7){
				if (workingSpace.containsKey(last6day)) workingSpace.remove(last6day);
				if (workingSpace.containsKey(last5day)) workingSpace.remove(last5day);
				if (workingSpace.containsKey(last4day)) workingSpace.remove(last4day);
				if (workingSpace.containsKey(last3day)) workingSpace.remove(last3day);
				com.nl.util.log.Log.debug("清理完成历史空间workingSpace:"+workingSpace.size());
			}
		}
		if (outputSpace!=null){
			if (outputSpace.size()>=7){
				if (outputSpace.containsKey(last6day)) outputSpace.remove(last6day);
				if (outputSpace.containsKey(last5day)) outputSpace.remove(last5day);
				if (outputSpace.containsKey(last4day)) outputSpace.remove(last4day);
				if (outputSpace.containsKey(last3day)) outputSpace.remove(last3day);
				com.nl.util.log.Log.debug("清理完成历史空间outputSpace:"+outputSpace.size());
			}
		}
	}
	
	/**
	 * 输出统计信息到数据库，信息分topo任务统计，以 BEGIN 作为拓扑唯一标识
	 */
	protected void saveStatInfo(){
		try {
			// 时间戳取开始时间，表示每一次拓扑的定时统计日志
			// topo_id,current_count,current_append_cnt,current_append_size,current_cost_time,insert_time
			// 14567832156897,57896,126,178964,3478,2016-01-21 10:56
			// 14567832156897,107842,152,255436,4378,2016-01-21 11:11
			// 查询统计信息:select A.* from RPT_SDC_TOPO_INFO A where A.insert_time=(select max(insert_time) from RPT_SDC_TOPO_INFO where topo_id=A.topo_id)
			StringBuffer sql=new StringBuffer();
			sql.append(INSERTABLE).append(BEGIN).append(GlobalConst.DEFAULT_SEPARATOR).append(COUNT).append(GlobalConst.DEFAULT_SEPARATOR).append(CNT).append(GlobalConst.DEFAULT_SEPARATOR).append(SIZE).append(GlobalConst.DEFAULT_SEPARATOR).append((System.currentTimeMillis()-BEGIN)/1000).append(GlobalConst.DEFAULT_SEPARATOR).append("now()").append(GlobalConst.DEFAULT_SEPARATOR).append("'").append(IP).append("'").append(GlobalConst.DEFAULT_SEPARATOR).append("'").append(HOST).append("')");
			if (conn != null) dbAccess.runSql(sql.toString());
			com.nl.util.log.Log.debug("统计信息已存入数据库!!!!");
			com.nl.util.log.Log.debug("\n最新统计信息请查询:"+"select A.* from RPT_SDC_TOPO_INFO A where A.insert_time=(select max(insert_time) from RPT_SDC_TOPO_INFO where topo_id=A.topo_id)");
			com.nl.util.log.Log.debug("\n历史统计信息请查询:"+"select * from RPT_SDC_TOPO_INFO order by topo_id,insert_time desc");
		} catch (SQLException e) {
			//e.printStackTrace();
			com.nl.util.log.Log.error("输出统计信息到数据库操作失败!!!!");
			com.nl.util.log.Log.error(e.getMessage());
		}finally{
			LASTCOUNT=COUNT;
		}
	}

}
