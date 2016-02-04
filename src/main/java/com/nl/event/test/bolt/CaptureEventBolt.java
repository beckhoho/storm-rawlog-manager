/**
 * @author: gsw
 * @version: 1.0
 * @CreateTime: 2015年11月12日 下午10:54:35
 * @Description: 无
 *//*
package com.nl.event.bolt;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nl.util.GlobalConst;
import com.nl.event.bean.NLMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

*//**
 * <p>
 * 
 * @ClassName: CaptureEventBolt
 * @Description: 原子事件捕获
 *               </p>
 * 
 * <pre>
 * </pre>
 *//*
public class CaptureEventBolt extends BaseBasicBolt {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(CaptureEventBolt.class);
	private NLMessage nlMessage;
	private Map<String, Object> header;
	private final int fieldSize;// 数据分割完大小
	private final int keyIndex;
	//private final ArrayList<Integer> captureIndexList;
	private final ArrayList<ArrayList<Integer>> captureIndexLists;
	private final String separator;// 分隔符
	private final ArrayList<String> streams;
	private final int STREAMSIZE;

	public CaptureEventBolt(int keyIndex, int fieldSize, ArrayList<ArrayList<Integer>> captureIndexLists,
			ArrayList<String> streams) {
		this(keyIndex, fieldSize, captureIndexLists, streams, GlobalConst.DEFAULT_SEPARATOR);

	}


	*//**
	 * @ClassName: CaptureEventBolt
	 * @param keyIndex key字段索引
	 * @param fieldSize 数据分割后的大小
	 * @param captureList 捕获索引列表
	 * @param streams 输出流定义列表
	 * @param separator 数据分隔符
	 *//*
	public CaptureEventBolt(int keyIndex, int fieldSize, ArrayList<ArrayList<Integer>> captureIndexLists,
			ArrayList<String> streams, String separator) {
		LOG.info("instancing...");
		this.keyIndex = keyIndex;
		this.fieldSize = fieldSize;
		this.captureIndexLists = captureIndexLists;
		this.streams = streams;
		this.STREAMSIZE= streams.size();
		this.separator = separator;
		LOG.info(GlobalConst.MARK_STRING + "\n" + "PARAMETERS:\n" + "\n"
				+ "keyIndex:" + this.keyIndex + "\n" + "separator:"
				+ this.separator + "\n" + "fieldSize:" + this.fieldSize + "\n"
				+ "captureIndexLists:" + this.captureIndexLists + "\n"
				+ "streams:" + this.streams + "\n" + GlobalConst.MARK_STRING
				+ "\n");
	}

	@SuppressWarnings("rawtypes" )
	@Override
	public void prepare(Map stormConf, TopologyContext context) {
		LOG.info("prepare...");
		nlMessage=new NLMessage();
		header = new HashMap<String, Object >();		
	}

	@Override
	public void execute(Tuple tuple, BasicOutputCollector collector) {

		//StringBuffer out = new StringBuffer();
		List<String > body=new ArrayList<String>() ;
		String in  =(String) tuple.getValue(0);
		LOG.info("222222222222:"+in);
		// 1、过滤
		if ("".equals(in)) return;
		String[] fields = in.split(this.separator);
		if (fields.length != this.fieldSize) return;
		// 2、捕获
		for (ArrayList<Integer> captureIndexList : captureIndexLists) {
			for (int i : captureIndexList) {
				if (fields[i] == null) return;
				if ("".equals(fields[i])) return;
				body.add(fields[i]);
			}
			// 3、封装
			String key = fields[this.keyIndex];
			header.put("key", key);
			nlMessage.setHeaders(header);
			//nlMessage.setBody(body);
			// 4、分发
			collector.emit(streams.get(Math.abs(key.hashCode() % this.STREAMSIZE)),new Values(nlMessage));
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		for (String stream : streams) declarer.declareStream(stream, new Fields("nlMessage"));
	}
}
*/