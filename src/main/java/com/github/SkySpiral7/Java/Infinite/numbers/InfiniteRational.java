package com.github.SkySpiral7.Java.Infinite.numbers;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

/**
 * This supports all possible rational numbers with perfect precision by using InfiniteInteger.
 * A rational number is defined as X/Y where X and Y are both integers and Y is not 0.
 *
 * @see InfiniteInteger
 */
public final class InfiniteRational extends AbstractInfiniteRational<InfiniteRational>
{
   private static final long serialVersionUID = 1L;

   /**
    * Common abbreviation for "not a number". This constant is the result of invalid math such as 0/0.
    * Note that this is a normal object such that <code>(MutableInfiniteRational.NaN == MutableInfiniteRational.NaN)</code> is
    * always true. Therefore it is logically correct unlike the floating point unit's NaN.
    */
   public static final InfiniteRational NaN = new InfiniteRational(MutableInfiniteRational.NaN);
   /**
    * +&infin; is a concept rather than a number and can't be the result of math involving finite numbers.
    * It is defined for completeness and behaves as expected with math resulting in &plusmn;&infin; or NaN.
    */
   public static final InfiniteRational POSITIVE_INFINITY = new InfiniteRational(MutableInfiniteRational.POSITIVE_INFINITY);
   /**
    * -&infin; is a concept rather than a number and can't be the result of math involving finite numbers.
    * It is defined for completeness and behaves as expected with math resulting in &plusmn;&infin; or NaN.
    */
   public static final InfiniteRational NEGATIVE_INFINITY = new InfiniteRational(MutableInfiniteRational.NEGATIVE_INFINITY);

   private final transient MutableInfiniteRational baseNumber;

   private InfiniteRational(final MutableInfiniteRational baseNumber)
   {
      this.baseNumber = baseNumber;
   }

   public static InfiniteRational valueOf(final double value)
   {
      return InfiniteRational.valueOf(MutableInfiniteRational.valueOf(value));
   }

   public static InfiniteRational valueOf(final BigDecimal value)
   {
      return InfiniteRational.valueOf(MutableInfiniteRational.valueOf(value));
   }

   public static InfiniteRational valueOf(final long value)
   {
      return InfiniteRational.valueOf(MutableInfiniteRational.valueOf(value));
   }

   public static InfiniteRational valueOf(final long numerator, final long denominator)
   {
      return InfiniteRational.valueOf(MutableInfiniteRational.valueOf(numerator, denominator));
   }

   public static InfiniteRational valueOf(final BigInteger numerator, final BigInteger denominator)
   {
      return InfiniteRational.valueOf(MutableInfiniteRational.valueOf(numerator, denominator));
   }

   public static InfiniteRational valueOf(final MutableInfiniteInteger numerator, final MutableInfiniteInteger denominator)
   {
      return InfiniteRational.valueOf(MutableInfiniteRational.valueOf(numerator, denominator));
   }

   public static InfiniteRational valueOf(final MutableInfiniteRational baseNumber)
   {
      if (MutableInfiniteRational.NaN.equals(baseNumber)) return InfiniteRational.NaN;
      if (MutableInfiniteRational.POSITIVE_INFINITY.equals(baseNumber)) return InfiniteRational.POSITIVE_INFINITY;
      if (MutableInfiniteRational.NEGATIVE_INFINITY.equals(baseNumber)) return InfiniteRational.NEGATIVE_INFINITY;
      return new InfiniteRational(baseNumber.copy());
   }

   public MutableInfiniteRational toMutableInfiniteRational()
   {
      return baseNumber.copy();
   }

   @Override
   public int intValue()
   {
      return baseNumber.intValue();
   }

   @Override
   public long longValue()
   {
      return baseNumber.longValue();
   }

   @Override
   public float floatValue()
   {
      return baseNumber.floatValue();
   }

   @Override
   public double doubleValue()
   {
      return baseNumber.doubleValue();
   }

   /**
    * Compares this == NaN.
    *
    * @return true if this InfiniteRational is the constant for NaN.
    *
    * @see #NaN
    */
   public boolean isNaN(){return this.equals(InfiniteRational.NaN);}

   /**
    * Compares this InfiniteRational to both positive and negative infinity.
    *
    * @return true if this InfiniteRational is either of the infinity constants.
    *
    * @see #POSITIVE_INFINITY
    * @see #NEGATIVE_INFINITY
    */
   public boolean isInfinite()
   {
      return (this.equals(InfiniteRational.POSITIVE_INFINITY) || this.equals(InfiniteRational.NEGATIVE_INFINITY));
   }

   /**
    * Compares this InfiniteRational to &plusmn;&infin; and NaN (returns false if this is any of them).
    *
    * @return true if this InfiniteRational is not a special value (ie if this is a finite number).
    *
    * @see #NaN
    * @see #POSITIVE_INFINITY
    * @see #NEGATIVE_INFINITY
    */
   public boolean isFinite(){return (!this.isNaN() && !this.isInfinite());}

   /**
    * @throws ArithmeticException if this == NaN
    */
   public void signalNaN(){if (isNaN()) throw new ArithmeticException("Not a number.");}

   @Override
   public int compareTo(final InfiniteRational other)
   {
      return baseNumber.compareTo(other.baseNumber);
   }

   @Override
   public boolean equals(final Object other)
   {
      if (this == other) return true;
      if (other == null || getClass() != other.getClass()) return false;
      final InfiniteRational that = (InfiniteRational) other;
      return Objects.equals(baseNumber, that.baseNumber);
   }

   @Override
   public int hashCode()
   {
      return Objects.hash(baseNumber);
   }

   @Override
   public String toString()
   {
      return baseNumber.toString();
   }

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
