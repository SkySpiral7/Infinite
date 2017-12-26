package com.github.skySpiral7.java.infinite.numbers;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.github.skySpiral7.java.staticSerialization.ObjectStreamReader;
import com.github.skySpiral7.java.staticSerialization.ObjectStreamWriter;
import org.hamcrest.Matchers;
import org.junit.Test;

import static org.junit.Assert.assertThat;

public class InfiniteRational_UT
{
   private List<InfiniteRational> constantList = Arrays.asList(InfiniteRational.NEGATIVE_INFINITY, InfiniteRational.POSITIVE_INFINITY,
         InfiniteRational.NaN);

   @Test
   public void staticSerializableIt_finite() throws IOException
   {
      final File tempFile = File.createTempFile("InfiniteRational_UT.TempFile.staticSerializableIt_finite.", ".txt");
      tempFile.deleteOnExit();

      final ObjectStreamWriter writer = new ObjectStreamWriter(tempFile);
      writer.writeObject(InfiniteRational.valueOf(5));
      writer.writeObject(InfiniteRational.valueOf(-5, 3));
      writer.writeObject(InfiniteRational.valueOf(0, 3));
      writer.close();

      final ObjectStreamReader reader = new ObjectStreamReader(tempFile);
      assertThat(reader.readObject(InfiniteRational.class), Matchers.is(InfiniteRational.valueOf(5)));
      assertThat(reader.readObject(InfiniteRational.class), Matchers.is(InfiniteRational.valueOf(-5, 3)));
      assertThat(reader.readObject(InfiniteRational.class), Matchers.is(InfiniteRational.valueOf(0, 3)));
      reader.close();
   }

   @Test
   public void staticSerializableIt_NonFinite() throws IOException
   {
      final File tempFile = File.createTempFile("InfiniteRational_UT.TempFile.staticSerializableIt_NonFinite.", ".txt");
      tempFile.deleteOnExit();

      final ObjectStreamWriter writer = new ObjectStreamWriter(tempFile);
      constantList.forEach(writer::writeObject);
      writer.close();

      final ObjectStreamReader reader = new ObjectStreamReader(tempFile);
      constantList.forEach(constant -> assertThat(reader.readObject(InfiniteRational.class), Matchers.is(constant)));
      reader.close();
   }
}
