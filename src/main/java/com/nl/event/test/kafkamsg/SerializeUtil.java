package com.nl.event.test.kafkamsg;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializeUtil
{
  private static Schema<String> schema = RuntimeSchema.getSchema(String.class);

  public static byte[] serialize(String o)
  {
    LinkedBuffer buffer = LinkedBuffer.allocate(2048 + o.length());

    return ProtobufIOUtil.toByteArray(o, schema, buffer);
  }

  public static byte[] serializeObj(Object object)
  {
    try
    {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(object);
      return baos.toByteArray();
    }
    catch (Exception e) {
    }
    return null;
  }

  public static Object unserializeObj(byte[] bytes) {
    ByteArrayInputStream bais = null;
    try
    {
      bais = new ByteArrayInputStream(bytes);
      ObjectInputStream ois = new ObjectInputStream(bais);
      return ois.readObject();
    }
    catch (Exception e) {
    }
    return null;
  }

  public static String unserialize(byte[] bytes)
  {
    String message = new String();
    ProtobufIOUtil.mergeFrom(bytes, message, schema);
    return message;
  }
}

