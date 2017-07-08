package com.github.SkySpiral7.Java;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ArrayBlockingQueue;

import com.github.SkySpiral7.Java.exception.ClosedResourceException;
import com.github.SkySpiral7.Java.exception.NoMoreDataException;

/**
 * <p>Creating this class will start reading the file in another thread and placing the results in a queue.
 * Therefore the other thread will be waiting on the disk instead of the main thread.
 * The main thread can create this object, do other stuff, and hopefully not need to wait when calling the read methods.
 * Note that if creation, reading, and closing happen in quick succession then using this class was pointless.</p>
 * <p><b>Warning:</b> this is the second Java class I've made with multiple threads.</p>
 *
 * @see #close()
 * @see #readBytes(int)
 */
public final class AsynchronousFileReader implements Closeable
{
   private final ReaderClass reader;
   private boolean amOpen = true;
   private int remainingBytes;

   /**
    * @param targetFile the file that will be read from
    *
    * @see com.github.SkySpiral7.Java.util.FileIoUtil#readTextFile(File)
    * @see com.github.SkySpiral7.Java.util.FileIoUtil#readBinaryFile(File)
    */
   public AsynchronousFileReader(final File targetFile)
   {
      if (targetFile.isDirectory()) throw new IllegalArgumentException("It is not possible to read file contents of a directory");
      //it's ok if the file doesn't exist since writing to it will create it
      if (!targetFile.exists()) throw new IllegalArgumentException("File doesn't exist");
      //TODO: create FileIo methods: validateRead (reads 1 byte), validateWrite, validateMaxSize

      if (targetFile.length() > Integer.MAX_VALUE)
         throw new IllegalArgumentException("File (length " + targetFile.length() + ") larger than supported size (max int)");
      remainingBytes = (int) targetFile.length();
      final int queueLength = (remainingBytes > 0) ? remainingBytes : 1;

      try
      {
         reader = new ReaderClass(new FileInputStream(targetFile), queueLength, this);
      }
      catch (final FileNotFoundException e)
      {
         throw new AssertionError("This can't be thrown", e);  //since I already checked that it exists
      }
      new Thread(reader).start();
   }

   /**
    * Closes the file stream. If the queue isn't empty its contents will become unreachable.
    */
   @Override
   public void close()
   {
      if (!amOpen) return;  //this is to prevent getting stuck by this.wait() below

      try
      {
         synchronized (this)
         {
            reader.shouldRead = false;
            this.wait();  //wait for the reader's thread to stop before closing
            //therefore all resources will be released when this method returns
            //as opposed to closing the file stream and assuming the other thread will stop
         }
         reader.inputStream.close();
      }
      catch (final IOException | InterruptedException e)
      {
         throw new RuntimeException(e);
      }
      amOpen = false;
   }

   /**
    * @return the number of bytes in the file that have not yet been consumed
    */
   public int remainingBytes()
   {
      return remainingBytes;
   }

   /**
    * Reads bytes from the file and converts them to a UTF-8 string.
    * This method will wait if the queue is empty.
    *
    * @see #readBytesAsString(int, Charset)
    */
   public String readBytesAsString(final int byteCount)
   {
      return readBytesAsString(byteCount, StandardCharsets.UTF_8);
   }

   /**
    * Reads bytes from the file and converts them to a string using the given encoding.
    * This method will wait if the queue is empty.
    *
    * @see #readBytes(int)
    */
   public String readBytesAsString(final int byteCount, final Charset encoding)
   {
      return new String(readBytes(byteCount), encoding);
   }

   /**
    * Reads binary data from the file.
    * This method will wait if the queue is empty.
    *
    * @see #readBytesAsString(int)
    */
   public byte[] readBytes(final int byteCount)
   {
      if (!amOpen) throw new ClosedResourceException("Can't read from a closed stream");
      //otherwise it would wait forever

      if (byteCount > remainingBytes) throw new NoMoreDataException(byteCount, remainingBytes);
      remainingBytes -= byteCount;

      final byte[] result = new byte[byteCount];
      for (int i = 0; i < byteCount; i++)
      {
         try
         {
            result[i] = reader.queue.take();
         }
         catch (final InterruptedException e)
         {
            throw new RuntimeException(e);
         }
      }
      return result;
   }

   private final static class ReaderClass implements Runnable
   {
      public final InputStream inputStream;
      /**
       * I don't know enough about concurrency to make a simple disruptor.
       * And the fact that each queue entry holds 1 byte makes me think I could be doing this better.
       */
      public final ArrayBlockingQueue<Byte> queue;
      /**
       * I'm using Object.wait ect because I don't know how to use java.util.concurrent.locks.Lock
       */
      private final Object notifyMeWhenDone;
      public volatile boolean shouldRead = true;

      public ReaderClass(final InputStream inputStream, final int length, final Object notifyMeWhenDone)
      {
         this.inputStream = inputStream;
         queue = new ArrayBlockingQueue<>(length);
         this.notifyMeWhenDone = notifyMeWhenDone;
      }

      @Override
      public void run()
      {
         final byte[] bucket = new byte[1024];
         while (shouldRead)
         {
            try
            {
               final int bytesRead = inputStream.read(bucket, 0, bucket.length);
               if (bytesRead == -1) break;
               for (int i = 0; i < bytesRead; i++)
               {
                  //not sure if this is better than converting to Byte[] then addAll
                  //or just reading 1 byte at a time from the stream
                  queue.add(bucket[i]);  //this won't block since the queue size is equal to file size
               }
            }
            catch (final IOException e)
            {
               throw new RuntimeException(e);
            }
         }
         while (shouldRead)
         {
            //this loop is so that after the file is done being read the thread can sleep until ready to notify the main thread
            try
            {
               Thread.sleep(100);
            }
            catch (final InterruptedException e)
            {
               throw new RuntimeException(e);
            }
         }
         synchronized (notifyMeWhenDone)
         {
            //notify the main thread that I am done reading from disk
            notifyMeWhenDone.notify();
         }
      }
   }
}
