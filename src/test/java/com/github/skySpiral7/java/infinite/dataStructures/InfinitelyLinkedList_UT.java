package com.github.skySpiral7.java.infinite.dataStructures;

import java.io.File;
import java.io.IOException;

import com.github.skySpiral7.java.staticSerialization.ObjectStreamReader;
import com.github.skySpiral7.java.staticSerialization.ObjectStreamWriter;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class InfinitelyLinkedList_UT
{
   @Test
   public void staticSerializableIt() throws IOException
   {
      final File tempFile = File.createTempFile("InfinitelyLinkedList_UT.TempFile.staticSerializableIt_finite.", ".txt");
      tempFile.deleteOnExit();

      final ObjectStreamWriter writer = new ObjectStreamWriter(tempFile);
      writer.writeObject(new InfinitelyLinkedList<>());
      writer.writeObject(new InfinitelyLinkedList<>(new Integer[]{2, 5}));
      writer.close();

      final ObjectStreamReader reader = new ObjectStreamReader(tempFile);
      assertThat(reader.readObject(InfinitelyLinkedList.class), is(new InfinitelyLinkedList<>()));
      assertThat(reader.readObject(InfinitelyLinkedList.class), is(new InfinitelyLinkedList<>(new Integer[]{2, 5})));
      reader.close();
   }
}
