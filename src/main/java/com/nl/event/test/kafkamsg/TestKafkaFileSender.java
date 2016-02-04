package com.nl.event.test.kafkamsg;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TestKafkaFileSender {

	public static void main(String[] args) {
		String brokerList = "192.168.1.54:9092,192.168.1.55:9092,192.168.1.56:9092,192.168.1.57:9092,192.168.1.58:9092,192.168.1.59:9092,192.168.1.60:9092,192.168.1.61:9092";
		// String brokerList =
		// "192.168.1.102:9092,192.168.1.103:9092,192.168.1.104:9092";
		// String brokerList = "192.168.146.20:9092";
		// String brokerList =
		// "172.32.148.195:9092,172.32.148.196:9092,172.32.148.197:9092";

		KafkaMsgSender kafkaNLMsgSender = new KafkaMsgSender(brokerList);
		String msg;
		FileInputStream in;
		BufferedReader reader;
		String sTempOneLine;

		kafkaNLMsgSender.setDirect(false);
		kafkaNLMsgSender.setCompressType(0);// 0 ��ѹ��,1gzip,2snappy
		long startTime = System.currentTimeMillis();

		List<String> msgs = new ArrayList<String>();
		String strFile = "/home/stormdev/storm/testdata";

		long y = 0;
		try {
			in = new FileInputStream(new File(strFile));
			reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
			while ((sTempOneLine = reader.readLine()) != null) {
				y++;
				msg = sTempOneLine;
				msgs.add(msg);
				if (y % 100 == 0) {
					kafkaNLMsgSender.send("MC_DATA", msgs);
					System.out.println(y + " cost "
							+ (System.currentTimeMillis() - startTime));
					msgs.clear();
					// return;
				}
			}
			if (msgs.size() > 0) {
				kafkaNLMsgSender.send("MC_DATA", msgs);
			}
			reader.close();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();

		}
		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
		kafkaNLMsgSender.close();

	}

}
