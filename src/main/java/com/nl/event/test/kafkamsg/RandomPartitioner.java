package com.nl.event.test.kafkamsg;

import java.util.Random;
import kafka.producer.Partitioner;
import kafka.utils.VerifiableProperties;

public class RandomPartitioner
  implements Partitioner
{
  public RandomPartitioner(VerifiableProperties props)
  {
  }

  public int partition(int a_numPartitions)
  {
    Random rnd = new Random();
    int partition = Math.abs(rnd.nextInt() % a_numPartitions);

    return partition;
  }

  public int partition(Object key, int numPartitions) {
    return partition(numPartitions);
  }
}
