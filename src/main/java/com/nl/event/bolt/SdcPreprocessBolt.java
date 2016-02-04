/**
 * @author: gsw
 * @version: 1.0
 * @CreateTime: 2015年11月3日 上午9:23:30
 * @Description: 无
 */
package com.nl.event.bolt;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import com.nl.bean.ParsedSdcData;
import com.nl.util.GlobalConst;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

/**
 * 电渠数据预处理
 */
public class SdcPreprocessBolt extends BaseRichBolt {

	private static final long serialVersionUID = 1L;
	protected OutputCollector collector;
	private long ERROR=0;

	@Override
	public void execute(Tuple tuple) {
		String data = tuple.getString(0);
		// 预处理1:过滤数据
		if (data.startsWith("#")) {
			ERROR++;
			com.nl.util.log.Log.debug("错误记录数:"+ERROR+"-->"+data.substring(0, 20>data.length()?data.length():20 ));
			return;
		}
		String[] fields = data.split("\\x20");
		if (fields.length !=14) {
			ERROR++;
			com.nl.util.log.Log.debug("错误记录数:"+ERROR+"-->"+data.substring(0, 20>data.length()?data.length():20 ));
			return;
		}
		/*
		 * date time WT.co_f WT.mobile c-ip WT.gc WT.branch cs-uri-stem WT.event
		 * WT.pn_sku WT.si_n WT.si_x WT.nv WT.ac WT.oss WT.mc_id WT.errCode
		 */
		
		// 预处理2:封装成解析过的sdc数据
		ParsedSdcData sdc=parse(fields[0],fields[1],fields[2],fields[6],fields[7]);
		if (sdc!=null) collector.emit(new Values(sdc));
		collector.ack(tuple);
		
	}


	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(Map conf, TopologyContext context,
			OutputCollector collector) {
		this.collector = collector;
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("sdc"));
	}
   
	/**
	 * 解析数据
	 * @param date
	 * @param time
	 * @param ip
	 * @param uri
	 * @param query
	 * @return
	 */
	ParsedSdcData parse(String date, String time, String ip,String uri, String query) {
		StringBuffer data = new StringBuffer();
		Map<String, String> kv = new HashMap<String, String>();
		String[] fields = null;
		String parse;
		try {
			parse = URLDecoder.decode(query, "UTF8");//
		} catch (UnsupportedEncodingException|IllegalArgumentException e) {
			ERROR++;
			com.nl.util.log.Log.error(e.getMessage());
			com.nl.util.log.Log.error(query);
			return null;
		}
		fields=parse.split("&");
		for (String s : fields) {
			//System.out.println(s);
			if (s.contains("=")) {
				String[] k_v = s.split("=");
				if (k_v.length>1) kv.put(k_v[0].toLowerCase(), k_v[1]);
			}
		}
		data.append(date).append(GlobalConst.DEFAULT_SEPARATOR);
		data.append(time).append(GlobalConst.DEFAULT_SEPARATOR);
		if (kv.containsKey("wt.co_f")) {
			data.append(kv.get("wt.co_f"));
		}
		data.append(GlobalConst.DEFAULT_SEPARATOR);
		if (kv.containsKey("wt.mobile")) {
			data.append(kv.get("wt.mobile"));
		}
		data.append(GlobalConst.DEFAULT_SEPARATOR);
		data.append(ip).append(GlobalConst.DEFAULT_SEPARATOR);
		if (kv.containsKey("wt.gc")) {
			data.append(kv.get("wt.gc"));
		}
		data.append(GlobalConst.DEFAULT_SEPARATOR);
		if (kv.containsKey("wt.branch")) {
			data.append(kv.get("wt.branch"));
		}
		data.append(GlobalConst.DEFAULT_SEPARATOR);
		data.append(uri).append(GlobalConst.DEFAULT_SEPARATOR);
		if (kv.containsKey("wt.event")) {
			data.append(kv.get("wt.event"));
		}
		data.append(GlobalConst.DEFAULT_SEPARATOR);
		if (kv.containsKey("wt.pn_sku")) {
			data.append(kv.get("wt.pn_sku"));
		}
		data.append(GlobalConst.DEFAULT_SEPARATOR);
		if (kv.containsKey("wt.si_n")) {
			data.append(kv.get("wt.si_n"));
		}
		data.append(GlobalConst.DEFAULT_SEPARATOR);
		if (kv.containsKey("wt.si_x")) {
			data.append(kv.get("wt.si_x"));
		}
		data.append(GlobalConst.DEFAULT_SEPARATOR);
		if (kv.containsKey("WT.nv")) {
			data.append(kv.get("wt.nv"));
		}
		data.append(GlobalConst.DEFAULT_SEPARATOR);
		if (kv.containsKey("wt.ac")) {
			data.append(kv.get("wt.ac"));
		}
		data.append(GlobalConst.DEFAULT_SEPARATOR);
		if (kv.containsKey("wt.oss")) {
			data.append(kv.get("wt.oss"));
		}
		data.append(GlobalConst.DEFAULT_SEPARATOR);
		if (kv.containsKey("wt.mc_id")) {
			data.append(kv.get("wt.mc_id"));
		}
		data.append(GlobalConst.DEFAULT_SEPARATOR);
		if (kv.containsKey("wt.errcode")) {
			data.append(kv.get("wt.errcode"));
		}
		data.append(GlobalConst.DEFAULT_SEPARATOR);

		data.append(parse);
		ParsedSdcData sdc=new ParsedSdcData();
		sdc.setDate(date);
		sdc.setTime(time);
		sdc.setBody(data.toString());
		
		return sdc;
	}
}
