package com.nl.event.test.kafkamsg;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import java.io.PrintStream;
import java.util.HashMap;
import kafka.serializer.Encoder;
import kafka.utils.VerifiableProperties;

public class KafkaEventEncoder implements Encoder<String> {
	public KafkaEventEncoder(VerifiableProperties props) {
	}

	public byte[] toBytes(String o) {
		return SerializeUtil.serialize(o);
	}

	public static void main(String[] args) {
		/*
		 * String nlMessage = new String(); HashMap hsHeader = new HashMap();
		 * hsHeader.put("test1", "value1"); hsHeader.put("test2", "value2");
		 * hsHeader.put("test3", "value3"); nlMessage.setHeaders(hsHeader);
		 * nlMessage
		 * .setBody("sssssssss99999999999999999999999sssss".getBytes()); byte[]
		 * byte1 = null; byte[] byte2 = null;
		 * 
		 * long beginTime = System.currentTimeMillis(); for (int i = 0; i < 1;
		 * i++) { byte1 = SerializeUtil.serialize(nlMessage); } long endTime =
		 * System.currentTimeMillis(); System.out.println(endTime - beginTime);
		 * 
		 * Schema schema = RuntimeSchema.getSchema(String.class);
		 * 
		 * LinkedBuffer buffer = LinkedBuffer.allocate(1024); beginTime =
		 * System.currentTimeMillis(); for (int i = 0; i < 1; i++) { byte2 =
		 * ProtobufIOUtil.toByteArray(nlMessage, schema, buffer); }
		 * System.out.println(byte1.length + "  " + byte2.length);
		 * 
		 * String nlMessage2 = new String(); ProtobufIOUtil.mergeFrom(byte2,
		 * nlMessage2, schema);
		 * 
		 * System.out.println(nlMessage2.getHeaders().toString());
		 * System.out.println(new String(nlMessage2.getBody()));
		 * 
		 * endTime = System.currentTimeMillis(); System.out.println(endTime -
		 * beginTime);
		 */}
}