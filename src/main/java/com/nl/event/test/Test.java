/**
* @author: gsw
* @version: 1.0
* @CreateTime: 2015年11月30日 下午12:06:51
* @Description: 无
*/
package com.nl.event.test;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.nl.util.CommonUtil;
import com.nl.util.GlobalConst;


public class Test {

	public static void main(String[] args) {
     String urlClass = "url1";
		
		//if (1==1) if ((urlClass = null) == null) System.out.println(urlClass);;
		
		
		
		List<String> values=new ArrayList<String>();
		
		
		values.add("0");
		values.add("1");
		values.add("2");
		values.add("3");
		
		for(String value:values){
			System.out.println(value);
		}
		//values.remove(2);
		values.set(2,"222222222222");
		
		for(String value:values){
			System.out.println(value);
		}
		
		
		System.out.println(System.currentTimeMillis());
		Calendar calendar=Calendar.getInstance();
		System.out.println(calendar.getTimeInMillis());
		System.out.println(calendar.get(Calendar.MONTH)+1);
		System.out.println(calendar.get(Calendar.YEAR));
		
		
		final StringBuffer sql=new StringBuffer(512);
		/*MON_NUM
		CHANCE_ID
		EVENT_TYPE
		USER_ID
		MSISDN
		BRAND_ID
		USERGROUP_ID
		ACTIVE_ID
		STEP_ID
		ACTIVE_OPP_ID
		RULE_ID
		RULE_INSTANCE_ID
		EVT_ARG_LIST
		INSERT_TIME
		ACCEPT_TIME
		DEAL_STAT*/

		sql.append("insert into mgic_perf_event_chance values ('");
		sql.append("activeOpp.getMonNum()");
		sql.append("','");
		sql.append("activeOpp.getChanceId()");
		sql.append("',");
		sql.append("activeOpp.getEventType()");
		sql.append(",'");
		sql.append("activeOpp.getUserId()");
		sql.append("','");
		sql.append("activeOpp.getMsisdn()");
		sql.append("',");
		sql.append("activeOpp.getBrandId()");
		sql.append(",'");		
		sql.append("activeOpp.getUserGroupId()");
		sql.append("','");
		sql.append("activeOpp.getActiveId()");
		sql.append("','");
		sql.append("activeOpp.getStepId()");
		sql.append("','");
		sql.append("activeOpp.getActiveOppId()");
		sql.append("','");
		sql.append("activeOpp.getRuleId()");
		sql.append("','");
		sql.append("activeOpp.getRuleInstanceId()");
		sql.append("','");
		sql.append("activeOpp.getEvtArgList()");
		sql.append("',");
		sql.append("SYSDATE()");
		sql.append(",'");
		sql.append("activeOpp.getAcceptTime()");
		sql.append("','");
		sql.append("activeOpp.getDealStat()");
		sql.append("') ");	
		
		System.out.println(sql.toString());
		
		String str="aaaaabbbbbbcccc";
		System.out.println(str.replaceAll("a", ""));
		str=str.replaceAll("a", "");
		System.out.println(str);
		
		final Map<String,String> m=new HashMap<String,String> ();
		m.put("1", "a");
		System.out.println(m.get("1"));
		System.out.println(new Date());
		System.out.println(CommonUtil.Md5("gbase20150531"));
		System.out.println(CommonUtil.Md5(CommonUtil.Md5("gbase20150531")));
		
		String mcData="2015-12-08 00:01:10,460023995405612,3575130567807301,5,0,39197,65535,39183,54533,39197,65535,39183,54533,200,,,15859921075,,,,3338";
		System.out.println(mcData.replaceFirst("^\\d{16}$", "15859921075"));
		System.out.println(mcData.substring(20, 35));
		System.out.println(mcData.substring(mcData.indexOf(",")+1, mcData.indexOf(",")+16));
		System.out.println(mcData.replaceFirst(mcData.substring(mcData.indexOf(",")+1, mcData.indexOf(",")+16), "15859921075"));
		System.out.println("2015-12-08 00:01:22,".length());
		
		System.out.println("LOCATION,AIRPORT".startsWith("LOCATION"));
		System.out.println("EVENT_TYPE==5".contains("=="));
		System.out.println(TimeUnit.DAYS.toMillis(30));
		
		Date now = new Date(); 
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");//可以方便地修改日期格式
		String hehe = dateFormat.format( now ); 
		System.out.println(hehe); 
		
		System.out.println("2016-01-15".replaceAll("-", "").substring(0, 6));
		
		System.out.println(Math.abs("2015-12-31".replaceAll("-", "").hashCode())%10);
		System.out.println(Math.abs("2016-01-01".replaceAll("-", "").hashCode())%10);
		System.out.println(Math.abs("2016-01-02".replaceAll("-", "").hashCode())%10);
		
		String substring="aaaaaaaaaa";
		System.out.println(substring.substring(0, substring.length()));
		
		InetAddress netAddress = CommonUtil.getInetAddress();  
        System.out.println("host ip:" + CommonUtil.getHostIp(netAddress));  
        System.out.println("host name:" + CommonUtil.getHostName(netAddress));  
        Properties properties = System.getProperties();  
        Set<String> set = properties.stringPropertyNames(); //获取java虚拟机和系统的信息。  
        /*for(String name : set){  
            System.out.println(name + ":" + properties.getProperty(name));  
        } */ 
        
        String INSERTABLE="insert into bigdata.RPT_SDC_TOPO_INFO (topo_id,current_count,current_append_cnt,current_append_size,current_cost_time,insert_time,ip,host) values (";	
    	
		long COUNT = 0, SIZE = 0, CNT = 0, BEGIN = 0;
		String IP, HOST;
		IP = CommonUtil.getHostIp(netAddress);
		HOST = CommonUtil.getHostName(netAddress);
		StringBuffer sql2 = new StringBuffer();
		sql2.append(INSERTABLE).append(BEGIN).append(GlobalConst.DEFAULT_SEPARATOR).append(COUNT).append(GlobalConst.DEFAULT_SEPARATOR).append(CNT).append(GlobalConst.DEFAULT_SEPARATOR).append(SIZE).append(GlobalConst.DEFAULT_SEPARATOR).append((System.currentTimeMillis()-BEGIN)/1000).append(GlobalConst.DEFAULT_SEPARATOR).append("now()").append(GlobalConst.DEFAULT_SEPARATOR).append("'").append(IP).append("'").append(GlobalConst.DEFAULT_SEPARATOR).append("'").append(HOST).append("')");
		System.out.println(sql2);
		
		System.out.println(GlobalConst.MARK_STRING + "\n追加完成:" + "path.toString()"
				+ "\n本次追加记录数:" + "(controller.getExecuteCount() - 1)"
				+ "\n本次追加文件总大小:" + "out.length()" + " 字节" + "\n当前文件总大小:"
				+ "hdfs.getFileStatus(path).getLen()" + " 字节");
		
		List <String> list=new ArrayList< String>() ; 
		list.add("追加完成:\t"+"/sdc_data_prase/201601/14325325345-20160125-sdc");
		list.add("本次追加记录数:\t"+ "123");
		list.add("本次追加文件总大小:\t"+ sql2.toString().length());
		list.add("当前文件总大小:\t"+ sql2.toString().length());
		System.out.println(CommonUtil.printLog(list));
		
	for (String s:"a b".split("\\x20")){
		System.out.println(s);
	}
	}
}
