package com.github.skySpiral7.java.infinite.numbers;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;

public abstract class AbstractInfiniteRational<T extends AbstractInfiniteRational<T>> extends Number implements Comparable<T>
{
   private static final long serialVersionUID = 1L;

   private Object writeReplace() throws ObjectStreamException
   {throw new NotSerializableException();}

   private Object readResolve() throws ObjectStreamException
   {throw new NotSerializableException();}

   private void writeObject(final ObjectOutputStream out) throws IOException
   {throw new NotSerializableException();}

   private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException
   {throw new NotSerializableException();}

   private void readObjectNoData() throws ObjectStreamException
   {throw new NotSerializableException();}
}
