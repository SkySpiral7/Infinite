package com.github.skySpiral7.java.infinite.dataStructures;

import com.github.skySpiral7.java.staticSerialization.ObjectStreamReader;
import com.github.skySpiral7.java.staticSerialization.ObjectStreamWriter;
import com.github.skySpiral7.java.staticSerialization.stream.ByteAppender;
import com.github.skySpiral7.java.staticSerialization.stream.ByteReader;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class InfinitelyLinkedList_UT
{
   @Test
   public void staticSerializableIt()
   {
      final ByteAppender mockFileAppend = new ByteAppender();
      final ObjectStreamWriter writer = new ObjectStreamWriter(mockFileAppend);
      writer.writeObject(new InfinitelyLinkedList<>());
      writer.writeObject(new InfinitelyLinkedList<>(new Integer[]{2, 5}));
      writer.close();

      final ByteReader mockFileRead = new ByteReader(mockFileAppend.getAllBytes());
      final ObjectStreamReader reader = new ObjectStreamReader(mockFileRead);
      assertThat(reader.readObject(InfinitelyLinkedList.class), is(new InfinitelyLinkedList<>()));
      assertThat(reader.readObject(InfinitelyLinkedList.class), is(new InfinitelyLinkedList<>(new Integer[]{2, 5})));
      reader.close();
   }
}
