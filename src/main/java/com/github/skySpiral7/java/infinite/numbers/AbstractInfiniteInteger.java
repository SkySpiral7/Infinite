package com.github.skySpiral7.java.infinite.numbers;

import com.github.skySpiral7.java.iterators.ReadOnlyListIterator;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.math.BigInteger;
import java.util.stream.Stream;

public abstract class AbstractInfiniteInteger<T extends AbstractInfiniteInteger<T>> extends Number implements Comparable<T>
{
   private static final long serialVersionUID = 1L;
   //TODO: move all stubs and JavaDoc here (but not converting BigInt/long)

   public abstract long longValueExact();

   public abstract BigInteger bigIntegerValue();

   public abstract BigInteger bigIntegerValueExact();

   public abstract ReadOnlyListIterator<Integer> magnitudeIterator();

   public abstract Stream<Integer> magnitudeStream();

   public abstract T add(long value);

   public abstract T add(BigInteger value);

   public abstract T add(T value);

   public abstract T subtract(long value);

   public abstract T subtract(BigInteger value);

   public abstract T subtract(T value);

   public abstract T multiply(long value);

   public abstract T multiply(BigInteger value);

   public abstract T multiply(T value);

   public abstract T multiplyByPowerOf2(long exponent);

   public abstract T multiplyByPowerOf2(BigInteger exponent);

   public abstract T multiplyByPowerOf2(T exponent);

   public abstract IntegerQuotient<T> divide(long value);

   public abstract IntegerQuotient<T> divide(BigInteger value);

   public abstract IntegerQuotient<T> divide(T value);

   public abstract T divideDropRemainder(long value);

   public abstract T divideDropRemainder(BigInteger value);

   public abstract T divideDropRemainder(T value);

   public abstract T divideByPowerOf2DropRemainder(long exponent);

   public abstract T divideByPowerOf2DropRemainder(BigInteger exponent);

   public abstract T divideByPowerOf2DropRemainder(T exponent);

   public abstract T divideReturnRemainder(long value);

   public abstract T divideReturnRemainder(BigInteger value);

   public abstract T divideReturnRemainder(T value);

   public abstract T power(long exponent);

   public abstract T power(BigInteger exponent);

   public abstract T power(T exponent);

   public abstract T selfPower();

   public abstract T factorial();

   public abstract T abs();

   public abstract T negate();

   public abstract byte signum();

   public abstract boolean isNaN();

   public abstract boolean isInfinite();

   public abstract boolean isFinite();

   public abstract void signalNaN();

   public abstract boolean equalValue(Object other);

   public abstract boolean equalValue(long value);

   @Override
   public abstract int compareTo(T other);

   public abstract int compareTo(BigInteger other);

   public abstract int compareTo(long other);

   @Override
   public abstract int hashCode();

   @Override
   public abstract String toString();

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
