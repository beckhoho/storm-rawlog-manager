package com.nl.event.test.kafkamsg;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

public class KafkaMsgSender {
	private String brokerList;
	private String serializerClass = "com.nl.event.test.kafkamsg.KafkaEventEncoder";

	private String partitionerClass = "com.nl.event.test.kafkamsg.RandomPartitioner";
	private Producer<String, String> producer = null;
	private ProducerConfig config = null;
	private boolean async = false;
	private int ack = 1;
	private int compressType;

	public KafkaMsgSender() {
	}

	public void setAck(int ack) {
		this.ack = ack;
	}

	public KafkaMsgSender(String brokerList) {
		this.brokerList = brokerList;
	}

	public boolean isAsync() {
		return this.async;
	}

	public void setAsync(boolean async) {
		this.async = async;
	}

	public int getCompressType() {
		return this.compressType;
	}

	public void setCompressType(int compressType) {
		this.compressType = compressType;
	}

	public String getBrokerList() {
		return this.brokerList;
	}

	public void setBrokerList(String brokerList) {
		this.brokerList = brokerList;
	}

	public String getSerializerClass() {
		return this.serializerClass;
	}

	public void setSerializerClass(String serializerClass) {
		this.serializerClass = serializerClass;
	}

	public String getPartitionerClass() {
		return this.partitionerClass;
	}

	public void setDirect(boolean direct) {
		if (direct)
			this.partitionerClass = "com.nl.event.test.kafkamsg.DirectPartitioner";
		else {
			this.partitionerClass = "com.nl.event.test.kafkamsg.RandomPartitioner";
		}

		close();
		init();
	}

	public void setPartitionerClass(String partitionerClass) {
		this.partitionerClass = partitionerClass;
	}

	private void init() {
		Properties props = new Properties();
		props.put("metadata.broker.list", this.brokerList);
		props.put("serializer.class", this.serializerClass);
		props.put("partitioner.class", this.partitionerClass);
		props.put("request.required.acks", "" + this.ack);
		if (this.async) {
			props.put("producer.type", "async");
			props.put("batch.num.messages", "200");
			props.put("queue.buffering.max.ms", "3000");
			props.put("queue.buffering.max.messages", "20000");
			props.put("queue.enqueue.timeout.ms", "-1");
		} else {
			props.put("producer.type", "sync");
		}
		if (this.compressType != 0) {
			props.put("compression.codec", "" + this.compressType);
		}
		this.config = new ProducerConfig(props);
		this.producer = new Producer(this.config);
	}

	public boolean send(String topic, int partition, String msg) {
		if (this.config == null) {
			init();
		}

		KeyedMessage data = new KeyedMessage(topic, msg, partition + "", msg);
		try {
			this.producer.send(data);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public boolean send(String topic, String msg) {
		return send(topic, 0, msg);
	}

	public boolean send(String topic, int partition, List<String> msgs) {
		if (this.config == null) {
			init();
		}
		List messages = new ArrayList();

		for (String msg : msgs) {
			KeyedMessage data = new KeyedMessage(topic, msg, partition + "",
					msg);
			messages.add(data);
		}
		try {
			this.producer.send(messages);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public boolean send(String topic, List<String> msgs) {
		return send(topic, 0, msgs);
	}

	public void close() {
		if (this.producer != null)
			this.producer.close();
	}
}
