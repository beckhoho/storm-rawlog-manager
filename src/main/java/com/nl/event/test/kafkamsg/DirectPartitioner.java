package com.nl.event.test.kafkamsg;

import kafka.producer.Partitioner;
import kafka.utils.VerifiableProperties;

public class DirectPartitioner
  implements Partitioner
{
  public DirectPartitioner(VerifiableProperties props)
  {
  }

  public int partition(String key, int a_numPartitions)
  {
    int partition;
    try
    {
      partition = Integer.parseInt(key);
    } catch (NumberFormatException e) {
      partition = 0;
    }

    if (partition >= a_numPartitions) {
      partition %= a_numPartitions;
    }
    return partition;
  }

  public int partition(Object key, int numPartitions) {
    String strKey = key.toString();

    return partition(strKey, numPartitions);
  }
}