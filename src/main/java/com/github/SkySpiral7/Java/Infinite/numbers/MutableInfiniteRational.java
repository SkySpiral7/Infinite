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
import com.github.SkySpiral7.Java.pojo.IntegerQuotient;

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
    * Common abbreviation for "not a number". This constant is the result of invalid math such as 1/0.
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

   /**
    * @see #valueOf(BigDecimal)
    */
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

   /**
    * Simply calls copy. This exists for orthogonality.
    *
    * @see #copy()
    */
   public static MutableInfiniteRational valueOf(final MutableInfiniteRational value)
   {
      return value.copy();
   }

   /**
    * Creates a whole number.
    *
    * @see #valueOf(MutableInfiniteInteger, MutableInfiniteInteger)
    */
   public static MutableInfiniteRational valueOf(final long value)
   {
      return MutableInfiniteRational.valueOf(MutableInfiniteInteger.valueOf(value), MutableInfiniteInteger.valueOf(1));
   }

   /**
    * @see #valueOf(MutableInfiniteInteger, MutableInfiniteInteger)
    */
   public static MutableInfiniteRational valueOf(final long numerator, final long denominator)
   {
      return MutableInfiniteRational.valueOf(MutableInfiniteInteger.valueOf(numerator), MutableInfiniteInteger.valueOf(denominator));
   }

   /**
    * Creates a whole number.
    *
    * @see #valueOf(MutableInfiniteInteger, MutableInfiniteInteger)
    */
   public static MutableInfiniteRational valueOf(final BigInteger value)
   {
      return MutableInfiniteRational.valueOf(MutableInfiniteInteger.valueOf(value), MutableInfiniteInteger.valueOf(1));
   }

   /**
    * @see #valueOf(MutableInfiniteInteger, MutableInfiniteInteger)
    */
   public static MutableInfiniteRational valueOf(final BigInteger numerator, final BigInteger denominator)
   {
      return MutableInfiniteRational.valueOf(MutableInfiniteInteger.valueOf(numerator), MutableInfiniteInteger.valueOf(denominator));
   }

   /**
    * Creates a whole number.
    *
    * @see #valueOf(MutableInfiniteInteger, MutableInfiniteInteger)
    */
   public static MutableInfiniteRational valueOf(final MutableInfiniteInteger value)
   {
      return MutableInfiniteRational.valueOf(MutableInfiniteInteger.valueOf(value), MutableInfiniteInteger.valueOf(1));
   }

   /**
    * Returns a MutableInfiniteRational with the given numerator and denominator.
    * Note that X / 0 == NaN. &plusmn;&infin; / 0 == NaN. &plusmn;&infin; / &plusmn;&infin; == NaN.
    * &plusmn;&infin; / X == &plusmn;&infin;. X / &plusmn;&infin; == 0.
    */
   public static MutableInfiniteRational valueOf(final MutableInfiniteInteger numerator, final MutableInfiniteInteger denominator)
   {
      if (MutableInfiniteInteger.NaN.equals(numerator) || MutableInfiniteInteger.NaN.equals(denominator))
         return MutableInfiniteRational.NaN;
      if (numerator.isInfinite() && denominator.isInfinite()) return MutableInfiniteRational.NaN;
      if (denominator.equals(0)) return MutableInfiniteRational.NaN;  //this is mathematically correct
      //all NaN results are covered

      if (MutableInfiniteInteger.POSITIVE_INFINITY.equals(numerator)) return MutableInfiniteRational.POSITIVE_INFINITY;
      if (MutableInfiniteInteger.NEGATIVE_INFINITY.equals(numerator)) return MutableInfiniteRational.NEGATIVE_INFINITY;
      if (denominator.isInfinite())
         return new MutableInfiniteRational(MutableInfiniteInteger.valueOf(0), MutableInfiniteInteger.valueOf(1));

      //Now they are both finite. The defensive copy is to prevent internal corruption and unexpected changes.
      final MutableInfiniteRational result = new MutableInfiniteRational(numerator.copy(), denominator.copy());
      result.normalizeSign();
      return result;
   }

   /**
    * Creates a whole number.
    *
    * @see #valueOf(MutableInfiniteInteger, MutableInfiniteInteger)
    */
   public static MutableInfiniteRational valueOf(final InfiniteInteger value)
   {
      return MutableInfiniteRational.valueOf(MutableInfiniteInteger.valueOf(value), MutableInfiniteInteger.valueOf(1));
   }

   /**
    * @see #valueOf(MutableInfiniteInteger, MutableInfiniteInteger)
    */
   public static MutableInfiniteRational valueOf(final InfiniteInteger numerator, final InfiniteInteger denominator)
   {
      return MutableInfiniteRational.valueOf(MutableInfiniteInteger.valueOf(numerator), MutableInfiniteInteger.valueOf(denominator));
   }

   /**
    * This MutableInfiniteRational will be reduced (smallest possible numerator and denominator).
    * Note that this class will never auto-reduce.
    *
    * @return this, so that other methods can be called in the same line of code
    */
   public MutableInfiniteRational reduce()
   {
      if (!this.isFinite()) return this;  //nothing to reduce since they aren't numbers (this prevents below from doing bad math)
      final MutableInfiniteInteger divisor = numerator.greatestCommonDivisor(denominator);
      numerator.divideDropRemainder(divisor);
      denominator.divideDropRemainder(divisor);
      return this;
   }

   //TODO: inline normalizeSign if it is rarely used
   private void normalizeSign()
   {
      //denominator.signum() can't be 0 and if it is 1 then the sign is already normalized.
      if (denominator.signum() == -1)
      {
         numerator.negate();  //numerator may be negative or positive (or 0)
         denominator.abs();
      }
   }

   @Override
   public int intValue()
   {
      return (int) longValue();
   }

   @Override
   public long longValue()
   {
      //TODO: what should throwable toString be? Also in other places.
      if (!this.isFinite()) throw new ArithmeticException(this + " can't be even partially represented as a long.");
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
    * Returns the absolute value of this MutableInfiniteRational.
    *
    * @return itself or the positive version of this
    *
    * @see Math#abs(double)
    */
   public MutableInfiniteRational abs()
   {
      if (isNaN()) return MutableInfiniteRational.NaN;
      if (this.equals(MutableInfiniteRational.NEGATIVE_INFINITY) || this.equals(MutableInfiniteRational.POSITIVE_INFINITY))
         return MutableInfiniteRational.POSITIVE_INFINITY;
      numerator.abs();
      return this;
   }

   /**
    * Returns a MutableInfiniteRational whose value is {@code (0-this)}.
    *
    * @return {@code -this}
    */
   public MutableInfiniteRational negate()
   {
      if (isNaN()) return MutableInfiniteRational.NaN;
      if (this.equals(MutableInfiniteRational.NEGATIVE_INFINITY)) return MutableInfiniteRational.POSITIVE_INFINITY;
      if (this.equals(MutableInfiniteRational.POSITIVE_INFINITY)) return MutableInfiniteRational.NEGATIVE_INFINITY;
      numerator.negate();  //also works for 0
      return this;
   }

   /**
    * @return -1, 0 or 1 as the value of this number is negative, zero or
    * positive respectively. NaN returns 0.
    */
   public byte signum()
   {
      //The denominator is always positive.
      return numerator.signum();  //All special values have denominator of 1.
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

   /**
    * This doesn't default to base 10 so that debuggers are not slowed down when they automatically call this method.
    * And because there are multiple possible formats.
    *
    * @return String representation of this MutableInfiniteRational.
    * No particular format or radix is specified and may change arbitrarily.
    *
    * @see #toImproperFractionalString()
    * @see #toMixedFactionalString(int)
    * @see #toDecimalString(int, int)
    */
   @Override
   public String toString()
   {
      return toDebuggingString();
   }

   /**
    * Uses a radix of 10.
    *
    * @see #toImproperFractionalString(int)
    */
   public String toImproperFractionalString(){return toImproperFractionalString(10);}

   /**
    * <p>It calls {@link MutableInfiniteInteger#toString(int)} putting a slash between the numerator and denominator.
    * If the denominator is 1 then the slash and denominator are omitted. This method won't reduce and will allow the numerator
    * to be greater than the denominator. Examples: {@code "1/2", "5", "4/2"}.</p>
    *
    * <p>Note the special values of ∞, -∞, and ∉ℚ (for NaN) which were chosen to avoid collision
    * with any radix. These values are returned for all radix values.</p>
    *
    * @return String representation of this MutableInfiniteRational in the given radix.
    *
    * @throws IllegalArgumentException if radix is illegal or this MutableInfiniteRational can't fit into a string of the given radix
    * @see MutableInfiniteInteger#toString(int)
    * @see #reduce()
    * @see #toMixedFactionalString(int)
    */
   public String toImproperFractionalString(final int radix)
   {
      if (this.equals(MutableInfiniteRational.POSITIVE_INFINITY)) return "∞";
      if (this.equals(MutableInfiniteRational.NEGATIVE_INFINITY)) return "-∞";
      if (this.equals(MutableInfiniteRational.NaN)) return "∉ℚ";
      if (denominator.equals(1)) return numerator.toString(radix);

      return numerator.toString(radix) + "/" + denominator.toString(radix);
   }

   /**
    * Uses a radix of 10.
    *
    * @see #toMixedFactionalString(int)
    */
   public String toMixedFactionalString(){return toMixedFactionalString(10);}

   /**
    * <p>The format is {@code "whole remainingNumerator/denominator"}. It calls {@link MutableInfiniteInteger#toString(int)} for each
    * number. whole is omitted if 0. remainingNumerator is the numerator after removing the whole. If this is a whole number then
    * only whole will be present. This method won't reduce. Examples: {@code "1/2", "5", "2 2/4"}.</p>
    *
    * <p>Note the special values of ∞, -∞, and ∉ℚ (for NaN) which were chosen to avoid collision
    * with any radix. These values are returned for all radix values.</p>
    *
    * @return String representation of this MutableInfiniteRational in the given radix.
    *
    * @throws IllegalArgumentException if radix is illegal or this MutableInfiniteRational can't fit into a string of the given radix
    * @see MutableInfiniteInteger#toString(int)
    * @see #reduce()
    * @see #toImproperFractionalString(int)
    */
   public String toMixedFactionalString(final int radix)
   {
      if (this.equals(MutableInfiniteRational.POSITIVE_INFINITY)) return "∞";
      if (this.equals(MutableInfiniteRational.NEGATIVE_INFINITY)) return "-∞";
      if (this.equals(MutableInfiniteRational.NaN)) return "∉ℚ";
      if (denominator.equals(1)) return numerator.toString(radix);

      final StringBuilder stringBuilder = new StringBuilder();
      //Don't need to copy numerator because divide doesn't mutate.
      final IntegerQuotient<MutableInfiniteInteger> quotient = numerator.divide(denominator);
      if (this.signum() == -1) stringBuilder.append('-');
      //abs the whole so that above can cover cases with and without whole.
      if (!quotient.getWholeResult().equals(0)) stringBuilder.append(quotient.getWholeResult().abs().toString(radix));
      if (!quotient.getWholeResult().equals(0) && !quotient.getRemainder().equals(0)) stringBuilder.append(" ");
      if (!quotient.getRemainder().equals(0))
      {
         stringBuilder.append(quotient.getRemainder().toString(radix));  //Remainder is never negative. sign already added to string.
         stringBuilder.append("/");
         stringBuilder.append(denominator.toString(radix));
      }
      return stringBuilder.toString();
   }

   public String toDecimalString(final int radix, final int decimalPlaces)
   {
      throw new UnsupportedOperationException("Not yet implemented");
      //example: 1.5, 0._3… (U+2026)
      //decimal will repeat if (after reducing) the denominator does not share all unique prime factors with the radix
      //decimal repeats whenever pulling another 0 uses a number already used:
      /*
7/12=
  0.583...
  ---------
12|7.
   60 is 5
   --
   100
   -96 is 8
   ---
     40
    -36 is 3
    ---
      40
      36 repeat
*/
   }

   String toDebuggingString()
   {
      if (this.equals(MutableInfiniteRational.POSITIVE_INFINITY)) return "+Infinity";
      if (this.equals(MutableInfiniteRational.NEGATIVE_INFINITY)) return "-Infinity";
      if (this.equals(MutableInfiniteRational.NaN)) return "NaN";
      if (denominator.equals(1)) return numerator.toDebuggingString();
      return numerator.toDebuggingString() + "\n/\n" + denominator.toDebuggingString();
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
      //I just checked for singletons so no need for valueOf
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
