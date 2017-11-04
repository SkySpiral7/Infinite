package com.github.SkySpiral7.Java.Infinite.numbers;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

import com.github.SkySpiral7.Java.Copyable;

import static com.github.SkySpiral7.Java.pojo.Comparison.LESS_THAN;
import static com.github.SkySpiral7.Java.util.ComparableSugar.is;

/**
 * This supports all possible rational numbers with perfect precision by using InfiniteInteger.
 * A rational number is defined as X/Y where X and Y are both integers and Y is not 0.
 *
 * @see InfiniteInteger
 */
public final class MutableInfiniteRational extends AbstractInfiniteRational<MutableInfiniteRational>
      implements Copyable<MutableInfiniteRational>
{
   private static final long serialVersionUID = 1L;

   /**
    * Common abbreviation for "not a number". This constant is the result of invalid math such as 0/0.
    * Note that this is a normal object such that <code>(MutableInfiniteRational.NaN == MutableInfiniteRational.NaN)</code> is
    * always true. Therefore it is logically correct unlike the floating point unit's NaN.
    */
   public static final MutableInfiniteRational NaN = new MutableInfiniteRational(MutableInfiniteInteger.NaN,
         MutableInfiniteInteger.valueOf(1));
   /**
    * +&infin; is a concept rather than a number and can't be the result of math involving finite numbers.
    * It is defined for completeness and behaves as expected with math resulting in &plusmn;&infin; or NaN.
    */
   public static final MutableInfiniteRational POSITIVE_INFINITY = new MutableInfiniteRational(MutableInfiniteInteger.POSITIVE_INFINITY,
         MutableInfiniteInteger.valueOf(1));
   /**
    * -&infin; is a concept rather than a number and can't be the result of math involving finite numbers.
    * It is defined for completeness and behaves as expected with math resulting in &plusmn;&infin; or NaN.
    */
   public static final MutableInfiniteRational NEGATIVE_INFINITY = new MutableInfiniteRational(MutableInfiniteInteger.NEGATIVE_INFINITY,
         MutableInfiniteInteger.valueOf(1));

   /**
    * The number above the fraction line.
    */
   private transient MutableInfiniteInteger numerator;
   /**
    * The number below the fraction line.
    */
   private transient MutableInfiniteInteger denominator;

   private MutableInfiniteRational(final MutableInfiniteInteger numerator, final MutableInfiniteInteger denominator)
   {
      this.numerator = numerator;
      this.denominator = denominator;
   }

   public static MutableInfiniteRational valueOf(final double value)
   {
      //float auto correctly casts into NaN, and infinities
      if (Double.isNaN(value)) return MutableInfiniteRational.NaN;
      if (Double.POSITIVE_INFINITY == value) return MutableInfiniteRational.POSITIVE_INFINITY;
      if (Double.NEGATIVE_INFINITY == value) return MutableInfiniteRational.NEGATIVE_INFINITY;
      return MutableInfiniteRational.valueOf(BigDecimal.valueOf(value));
   }

   public static MutableInfiniteRational valueOf(final BigDecimal value)
   {
      throw new UnsupportedOperationException("Not yet implemented");
   }

   public static MutableInfiniteRational valueOf(final long numerator, final long denominator)
   {
      return MutableInfiniteRational.valueOf(InfiniteInteger.valueOf(numerator), InfiniteInteger.valueOf(denominator));
   }

   public static MutableInfiniteRational valueOf(final BigInteger numerator, final BigInteger denominator)
   {
      return MutableInfiniteRational.valueOf(InfiniteInteger.valueOf(numerator), InfiniteInteger.valueOf(denominator));
   }

   public static MutableInfiniteRational valueOf(final MutableInfiniteInteger numerator, final MutableInfiniteInteger denominator)
   {
      return MutableInfiniteRational.valueOf(InfiniteInteger.valueOf(numerator), InfiniteInteger.valueOf(denominator));
   }

   /**
    * Returns a MutableInfiniteRational with the given numerator and denominator.
    * Note that X / 0 == NaN. &plusmn;&infin; / 0 == NaN. &plusmn;&infin; / &plusmn;&infin; == NaN.
    * &plusmn;&infin; / X == &plusmn;&infin;. X / &plusmn;&infin; == 0.
    */
   public static MutableInfiniteRational valueOf(final InfiniteInteger numerator, final InfiniteInteger denominator)
   {
      if (InfiniteInteger.NaN.equals(numerator) || InfiniteInteger.NaN.equals(denominator)) return MutableInfiniteRational.NaN;
      if (numerator.isInfinite() && denominator.isInfinite()) return MutableInfiniteRational.NaN;
      if (denominator.equals(0)) return MutableInfiniteRational.NaN;  //this is mathematically correct
      //all NaN results are covered

      if (InfiniteInteger.POSITIVE_INFINITY.equals(numerator)) return MutableInfiniteRational.POSITIVE_INFINITY;
      if (InfiniteInteger.NEGATIVE_INFINITY.equals(numerator)) return MutableInfiniteRational.NEGATIVE_INFINITY;
      if (denominator.isInfinite())
         return new MutableInfiniteRational(MutableInfiniteInteger.valueOf(0), MutableInfiniteInteger.valueOf(1));

      //now they are both finite
      final MutableInfiniteRational result = new MutableInfiniteRational(numerator.toMutableInfiniteInteger(),
            denominator.toMutableInfiniteInteger());
      result.reduce();
      return result;
   }

   private void reduce()
   {
      //TODO: should reduce be auto called or public?
      throw new UnsupportedOperationException("Not yet implemented");
   }

   @Override
   public int intValue()
   {
      return (int) longValue();
   }

   @Override
   public long longValue()
   {
      if (!this.isFinite()) throw new ArithmeticException(this + " can't be even partially represented as a long.");
      if (is(numerator.copy().abs(), LESS_THAN, denominator)) return 0;
      return numerator.copy().divideDropRemainder(denominator).longValue();
   }

   @Override
   public float floatValue()
   {
      return (float) doubleValue();
   }

   @Override
   public double doubleValue()
   {
      if (this.equals(MutableInfiniteRational.NaN)) return Double.NaN;
      if (this.equals(MutableInfiniteRational.POSITIVE_INFINITY)) return Double.POSITIVE_INFINITY;
      if (this.equals(MutableInfiniteRational.NEGATIVE_INFINITY)) return Double.NEGATIVE_INFINITY;

      throw new UnsupportedOperationException("Not yet implemented");
   }

   /**
    * Compares this == NaN.
    *
    * @return true if this MutableInfiniteRational is the constant for NaN.
    *
    * @see #NaN
    */
   public boolean isNaN(){return this.equals(MutableInfiniteRational.NaN);}

   /**
    * Compares this MutableInfiniteRational to both positive and negative infinity.
    *
    * @return true if this MutableInfiniteRational is either of the infinity constants.
    *
    * @see #POSITIVE_INFINITY
    * @see #NEGATIVE_INFINITY
    */
   public boolean isInfinite()
   {
      return (this.equals(MutableInfiniteRational.POSITIVE_INFINITY) || this.equals(MutableInfiniteRational.NEGATIVE_INFINITY));
   }

   /**
    * Compares this MutableInfiniteRational to &plusmn;&infin; and NaN (returns false if this is any of them).
    *
    * @return true if this MutableInfiniteRational is not a special value (ie if this is a finite number).
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
   public int compareTo(final MutableInfiniteRational other)
   {
      if (this.equals(other)) return 0;  //poor speed
      throw new UnsupportedOperationException("Not yet implemented");
   }

   @Override
   public boolean equals(final Object other)
   {
      if (this == other) return true;
      if (other == null || getClass() != other.getClass()) return false;
      final MutableInfiniteRational that = (MutableInfiniteRational) other;
      return Objects.equals(numerator, that.numerator) && Objects.equals(denominator, that.denominator);
   }

   @Override
   public int hashCode()
   {
      return Objects.hash(numerator, denominator);
   }

   @Override
   public String toString()
   {
      if (this.equals(MutableInfiniteRational.POSITIVE_INFINITY)) return "+Infinity";  //it doesn't seem like \u221E works
      if (this.equals(MutableInfiniteRational.NEGATIVE_INFINITY)) return "-Infinity";
      if (this.equals(MutableInfiniteRational.NaN)) return "NaN";
      if (numerator.equals(0)) return "0";
      if (denominator.equals(1)) return numerator.toString();
      return numerator + "/" + denominator;
   }

   /**
    * In order to maintain the singleton constants they will not be copied.
    * So &plusmn;&infin; and NaN will return themselves but all others will be copied as expected.
    *
    * @return a copy or a defined singleton
    */
   @Override
   public MutableInfiniteRational copy()
   {
      if (!this.isFinite()) return this;
      //I don't need to reduce and I just checked for singletons so no need for valueOf
      return new MutableInfiniteRational(numerator.copy(), denominator.copy());
   }

   /*
    *
    * The rest of the file is to disable java's Serializable
    *
    */
   private Object writeReplace() throws ObjectStreamException
   {
      numerator = denominator = null;
      throw new NotSerializableException();
   }

   private Object readResolve() throws ObjectStreamException
   {
      numerator = denominator = null;
      throw new NotSerializableException();
   }

   private void writeObject(final ObjectOutputStream out) throws IOException
   {
      numerator = denominator = null;
      throw new NotSerializableException();
   }

   private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException
   {
      numerator = denominator = null;
      throw new NotSerializableException();
   }

   private void readObjectNoData() throws ObjectStreamException
   {
      numerator = denominator = null;
      throw new NotSerializableException();
   }
}
