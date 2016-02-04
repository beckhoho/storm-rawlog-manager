/**
* @author: gsw
* @version: 1.0
* @CreateTime: 2015年10月14日 下午1:56:23
* @Description: 无
*/
package com.nl.event.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.nl.util.config.RedisClusterCfg;
import com.nl.util.redis.RedisCluster;

import redis.clients.jedis.JedisCluster;

public class TestRedis {

	public static void main(String[] args) throws IOException {
		
		final RedisClusterCfg cfg=new RedisClusterCfg();
		
		ArrayList<String> places = new ArrayList<String>();
		places.add("192.168.1.101:7001");
		places.add("192.168.1.101:7002");
		places.add("192.168.1.101:7003");
		places.add("192.168.1.101:8001");
		places.add("192.168.1.101:8002");
		places.add("192.168.1.101:8003");
		
		cfg.setHostAndport(places);
		//final JedisCluster jc=new RedisCluster(cfg).getInstance();
		//RedisCluster.getRedisCluster().setCfg(cfg);
		
		//RedisCluster.getRedisCluster().getInstance();
		
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					RedisCluster.getRedisCluster().setCfg(cfg);
					//RedisCluster.getRedisCluster().getInstance();
				}
			}

		}).start();
		
		
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					RedisCluster.getRedisCluster().getInstance();
				}
			}

		}).start();
		
		//insertTestData(jc);
		//insertUserId(jc);
	
	}
	
	static void insertUserId(final JedisCluster jc) throws IOException {
		FileInputStream in;
        BufferedReader reader;
        String sTempOneLine;
		 String strFile = "E:\\NewlandSoft\\待处理文档\\大数据一期\\源码\\gn2014060720035988000311149.tmp.bak";
		 in = new FileInputStream(new File(strFile));
		 reader = new BufferedReader(new InputStreamReader(in));
		    while ((sTempOneLine = reader.readLine()) != null) {
		    	String []fields=sTempOneLine.split("\\|");
		    	if(fields.length>5){
		    		jc.set("APP:CUI:"+fields[0], fields[4]);
					System.out.println("插入:<"+fields[0]+","+fields[4]+">");
		    	}
		    	
		    }
		 
	}
	/*
	void hashSet(JedisCluster jc) {
		for (int i = 0; i < 5000000; i++) {
			jc.hset("test_hash_size", "field" + i, "value" + i);
		}
		
	}

	static void insertUserId(final JedisCluster jc) {

		// 1
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					for (int i = 120000; i < 200000; i++) {
						jc.hset("hmap_userid","APP:CUI:" + i, "3586900548" + i);
					}
				}
			}

		}).start();
		// 2
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					for (int i = 0; i < 60000; i++) {
						jc.hset("hmap_userid","APP:CUI:" + i, "3586900548" + i);
					}
				}
			}

		}).start();
		// 3
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					for (int i = 60000; i < 120000; i++) {
						jc.hset("hmap_userid","APP:CUI:" + i, "3586900548" + i);
					}
				}
			}

		}).start();
	}*/
	

	static void insertTestData(JedisCluster jc) {
		String [] data={"15292872954,352841055743260",
				"15000604910,358765053780910",
				"15000604910,358765053780910",
				"18290867793,860312029604250",
				"13886049886,358028056894310",
				"13918276088,358842055369920",
				"13579208657,358198053260550",
				"18758012626,864182026246530",
				"18899198351,358584056003550",
				"13910269623,358766056580810",
				"13910269623,358766056580810",
				"18758012626,864182026246530",
				"13910269623,358766056580810",
				"13910269623,358766056580810",
				"13886103245,359850053580720",
				"13910269623,358766056580810",
				"13910269623,358766056580810",
				"13733998609,354402055675500",
				"13905871509,358844054590450",
				"15299071038,013130009295252",
				"15299071038,013130009295252",
				"13981110913,358765057635580",
				"13659973291,358048020000770",
				"13565939975,860936028812507",
				"13639906275,863223028641817",
				"15282876767,358028057193240",
				"13899934048,358843051438260",
				"15299071038,013130009295252",
				"15282876767,358028057193240",
				"15899183207,863654024849417",
				"18799150573,862297028420537",
				"15899183207,863654024849417",
				"15999133895,358690054811080",
				"15999179110,358840055695220",
				"15898530641,863738024040520",
				"15999179110,358840055695220",
				"15257486616,013138001614121",
				"15999179110,358840055695220",
				"15999179110,358840055695220",
				"15999179110,358840055695220",
				"15147095888,358840059161730",
				"18899162627,355859054982570",
				"13579216209,862243022408958",
				"13871360190,358766058927690",
				"13876446737,352107064196830",
				"13876446737,352107064196830",
				"13588088590,358843059346150",
				"15775878288,358765050930680",
				"15775878288,358765050930680",
				"15775878288,358765050930680",
				"15775878288,358765050930680",
				"15775878288,358765050930680",
				"15775878288,358765050930680",
				"14799057036,869412018193310",
				"15775878288,358765050930680",
				"15775878288,358765050930680",
				"15775878288,358765050930680",
				"13588088590,358843059346150",
				"15775878288,358765050930680",
				"15775878288,358765050930680",
				"15139939777,358765056884920",
				"13572751788,357512058926190",
				"13579811040,863121024589430",
				"13579811040,863121024589430",
				"13968527955,351981061290970",
				"13876446737,352107064196830",
				"13886103245,359850053580720",
				"13968527955,351981061290970",
				"18356866996,358776055709670",
				"13629942345,358763058280950",
				"13629942345,358763058280950",
				"13586077795,358028053008010",
				"13579984819,358584053471080",
				"15999108366,351980062062220",
				"13999820654,352020060313170",
				"15999108366,351980062062220",
				"13639921057,358843058255890",
				"13577058657,9|3588440525941",
				"14799057036,869412018193310",
				"15139939777,358765056884920",
				"15813356899,357140050461270",
				"15813356899,357140050461270",
				"13899897047,358842055458580",
				"15999133895,358690054811080",
				"15999133895,358690054811080",
				"15999133895,358690054811080",
				"18790871995,864394028753620",
				"18290886395,866051010325630",
				"18729307766,358028054341640",
				"13999150030,352020061196020",
				"13871360190,358766058927690",
				"13871360190,358766058927690",
				"15099144153,866820010176040",
				"18790871995,864394028753620",
				"15029999661,358584054754300",
				"13629942345,358763058280950",
				"13571247210,358844054633370",
				"18899162627,355859054982570",
				"13588088590,358843059346150",
				"13876446737,352107064196830"};
		
		for (String s:data){
			jc.set("APP:CUI:"+s.split(",")[0], s.split(",")[1]);
			System.out.println("插入:<"+s.split(",")[0]+","+s.split(",")[1]+">");
		}

	}
}
