package com.nl.event.test;  

import java.io.File;
import java.io.IOException;



import java.util.ArrayList;
import java.util.Map.Entry;

import com.nl.util.CommonUtil;
import com.nl.util.config.ConfigLoader;

import backtype.storm.Config;

public class TestLoadConf {
	public static void main(String[] args) throws IOException {
		Config conf = new ConfigLoader()
				.loadStormConfig("E:\\Eclipse\\workspace\\XJ_atom_events\\conf\\sdc-capture-atom-event-topo.yaml");
		
		
		/*System.out.println(conf.get("bolt.match.rule.streams"));
		ArrayList<Integer> streams = (ArrayList<Integer>) conf.get("capture.event.bolt.capture.list");
		
		for(int s:streams){
			System.out.println(s);
			
		}
		
		ArrayList<String> streams2 = (ArrayList<String  >) conf.get("hset.redis.bolt.streams.list");
		for( String s:streams2){
			System.out.println(s);
			
		}*/
		
		System.out.println(Config.NIMBUS_HOST);
		System.out.println(Config.NIMBUS_THRIFT_PORT);	
		if((boolean) conf.get("force.from.begin"))
		System.out.println(conf.get("force.from.begin"));
		CommonUtil.printConfig(conf);
		
	}
	

	
}
