package com.github.skySpiral7.java.infinite.numbers;

import com.github.skySpiral7.java.staticSerialization.ObjectStreamReader;
import com.github.skySpiral7.java.staticSerialization.ObjectStreamWriter;
import com.github.skySpiral7.java.staticSerialization.stream.ByteAppender;
import com.github.skySpiral7.java.staticSerialization.stream.ByteReader;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertThat;

public class InfiniteRational_UT
{
   private List<InfiniteRational> constantList = Arrays.asList(InfiniteRational.NEGATIVE_INFINITY, InfiniteRational.POSITIVE_INFINITY,
         InfiniteRational.NaN);

   @Test
   public void staticSerializableIt_finite()
   {
      final ByteAppender mockFileAppend = new ByteAppender();
      final ObjectStreamWriter writer = new ObjectStreamWriter(mockFileAppend);
      writer.writeObject(InfiniteRational.valueOf(5));
      writer.writeObject(InfiniteRational.valueOf(-5, 3));
      writer.writeObject(InfiniteRational.valueOf(0, 3));
      writer.close();

      final ByteReader mockFileRead = new ByteReader(mockFileAppend.getAllBytes());
      final ObjectStreamReader reader = new ObjectStreamReader(mockFileRead);
      assertThat(reader.readObject(InfiniteRational.class), Matchers.is(InfiniteRational.valueOf(5)));
      assertThat(reader.readObject(InfiniteRational.class), Matchers.is(InfiniteRational.valueOf(-5, 3)));
      assertThat(reader.readObject(InfiniteRational.class), Matchers.is(InfiniteRational.valueOf(0, 3)));
      reader.close();
   }

   @Test
   public void staticSerializableIt_NonFinite()
   {
      final ByteAppender mockFileAppend = new ByteAppender();
      final ObjectStreamWriter writer = new ObjectStreamWriter(mockFileAppend);
      constantList.forEach(writer::writeObject);
      writer.close();

      final ByteReader mockFileRead = new ByteReader(mockFileAppend.getAllBytes());
      final ObjectStreamReader reader = new ObjectStreamReader(mockFileRead);
      constantList.forEach(constant -> assertThat(reader.readObject(InfiniteRational.class), Matchers.is(constant)));
      reader.close();
   }
}
