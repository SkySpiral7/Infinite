package com.github.skySpiral7.java.infinite.numbers;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Objects;

import com.github.skySpiral7.java.Copyable;
import com.github.skySpiral7.java.pojo.IntegerQuotient;
import com.github.skySpiral7.java.util.BitWiseUtil;

import static com.github.skySpiral7.java.pojo.Comparison.GREATER_THAN;
import static com.github.skySpiral7.java.pojo.Comparison.LESS_THAN;
import static com.github.skySpiral7.java.util.ComparableSugar.THIS_EQUAL;
import static com.github.skySpiral7.java.util.ComparableSugar.THIS_GREATER;
import static com.github.skySpiral7.java.util.ComparableSugar.THIS_LESSER;
import static com.github.skySpiral7.java.util.ComparableSugar.is;

/**
 * This supports all possible rational numbers with perfect precision by using InfiniteInteger.
 * A rational number is defined as numerator/denominator where numerator and denominator are both integers and denominator is not 0.
 *
 * @see InfiniteInteger
 */
//BigDecimal max is 10**maxInt which is 10**(2**31-1). I think.
//BigDecimal has unscaledValue*10**-scale however the unscaledValue is BigInteger so it might lose precision. TODO: Check the math.
public final class MutableInfiniteRational extends AbstractInfiniteRational<MutableInfiniteRational>
      implements Copyable<MutableInfiniteRational>
{
   private static final long serialVersionUID = 1L;

   /**
    * Common abbreviation for "not a number". This constant is the result of invalid math such as 1/0.
    * Note that this is a normal object such that <code>(MutableInfiniteRational.NaN == MutableInfiniteRational.NaN)</code> is
    * always true. Therefore it is logically correct unlike the floating point unit's NaN.
    *
    * This value is immutable.
    */
   public static final MutableInfiniteRational NaN = new MutableInfiniteRational(MutableInfiniteInteger.NaN,
         MutableInfiniteInteger.valueOf(1));
   /**
    * +&infin; is a concept rather than a number and can't be the result of math involving finite numbers.
    * It is defined for completeness and behaves as expected with math resulting in &plusmn;&infin; or NaN.
    *
    * This value is immutable.
    */
   public static final MutableInfiniteRational POSITIVE_INFINITY = new MutableInfiniteRational(MutableInfiniteInteger.POSITIVE_INFINITY,
         MutableInfiniteInteger.valueOf(1));
   /**
    * -&infin; is a concept rather than a number and can't be the result of math involving finite numbers.
    * It is defined for completeness and behaves as expected with math resulting in &plusmn;&infin; or NaN.
    *
    * This value is immutable.
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

   /**
    * private in order to prevent an illegal object creation. valueOf will retain singletons and validate.
    *
    * @see #valueOf(MutableInfiniteInteger, MutableInfiniteInteger)
    */
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

   /**
    * Creates a MutableInfiniteRational with the same numeric value as the parameter.
    * Note that since this doesn't reduce and pointlessly retains scale the denominator
    * may be large. However negative scale (or 0) isn't retained.
    *
    * <ul>
    * <li>{@code new BigDecimal("2.50") => 250/100}</li>
    * <li>{@code new BigDecimal("6e2") => 600/1}</li>
    * <li>{@code new BigDecimal("6") => 6/1}</li>
    * <li>{@code new BigDecimal("0.00") => 0/100}</li>
    * </ul>
    *
    * @see #reduce()
    * @see BigDecimal#stripTrailingZeros()
    */
   public static MutableInfiniteRational valueOf(final BigDecimal value)
   {
      final MutableInfiniteInteger numerator = MutableInfiniteInteger.valueOf(value.scaleByPowerOfTen(value.scale()).toBigIntegerExact());

      if (value.scale() <= 0)
      {
         //whole numbers may have negative (or 0) scale
         final MutableInfiniteInteger multiplier = MutableInfiniteInteger.valueOf(10).power(-value.scale());
         return MutableInfiniteRational.valueOf(numerator.multiply(multiplier));
      }
      else
      {
         final MutableInfiniteInteger denominator = MutableInfiniteInteger.valueOf(10).power(value.scale());
         return MutableInfiniteRational.valueOf(numerator, denominator);
      }
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
    * Simply calls copy. This exists for orthogonality.
    *
    * @see #copy()
    */
   public static MutableInfiniteRational valueOf(final MutableInfiniteRational value)
   {
      return value.copy();
   }

   /**
    * Simply calls toMutableInfiniteRational. This exists for orthogonality.
    *
    * @see InfiniteRational#toMutableInfiniteRational()
    */
   public static MutableInfiniteRational valueOf(final InfiniteRational value)
   {
      return value.toMutableInfiniteRational();
   }

   /**
    * Converts an MutableInfiniteRational to an InfiniteRational.
    *
    * @return a new InfiniteRational or a defined singleton
    */
   public InfiniteRational toInfiniteRational()
   {
      return InfiniteRational.valueOf(this);
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
         numerator.negate();  //numerator may be anything
         denominator.abs();  //denominator is always finite
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

      // TODO: doubleValue
      throw new UnsupportedOperationException("Not yet implemented");
   }

   //TODO: bigDecimalValue, bigDecimalValueExact
   //TODO: add, subtract

   /**
    * @see #multiply(MutableInfiniteRational)
    */
   public MutableInfiniteRational multiply(final long value){return this.multiply(MutableInfiniteRational.valueOf(value));}

   /**
    * @see #multiply(MutableInfiniteRational)
    */
   public MutableInfiniteRational multiply(final BigInteger value){return this.multiply(MutableInfiniteRational.valueOf(value));}

   /**
    * @see #multiply(MutableInfiniteRational)
    */
   public MutableInfiniteRational multiply(final double value){return this.multiply(MutableInfiniteRational.valueOf(value));}

   /**
    * @see #multiply(MutableInfiniteRational)
    */
   public MutableInfiniteRational multiply(final BigDecimal value){return this.multiply(MutableInfiniteRational.valueOf(value));}

   /**
    * @see #multiply(MutableInfiniteRational)
    */
   public MutableInfiniteRational multiply(final InfiniteInteger value){return this.multiply(MutableInfiniteRational.valueOf(value));}

   /**
    * @see #multiply(MutableInfiniteRational)
    */
   public MutableInfiniteRational multiply(final MutableInfiniteInteger value)
   {
      return this.multiply(MutableInfiniteRational.valueOf(value));
   }

   /**
    * @see #multiply(MutableInfiniteRational)
    */
   public MutableInfiniteRational multiply(final InfiniteRational value)
   {
      return this.multiply(MutableInfiniteRational.valueOf(value));
   }

   /**
    * Returns a MutableInfiniteRational whose value is {@code (this * value)}.
    * Note &plusmn;&infin; * 0 results in NaN.
    *
    * @param value the operand to be multiplied to this InfiniteInteger.
    *
    * @return the result including &plusmn;&infin; and NaN
    */
   public MutableInfiniteRational multiply(final MutableInfiniteRational value)
   {
      if (this.equals(MutableInfiniteRational.NaN) || value.equals(MutableInfiniteRational.NaN)) return MutableInfiniteRational.NaN;
      if (this.isInfinite() && value.equalValue(0)) return MutableInfiniteRational.NaN;
      if (value.isInfinite() && this.equalValue(0)) return MutableInfiniteRational.NaN;

      if (this.equals(MutableInfiniteRational.NEGATIVE_INFINITY) && value.equals(MutableInfiniteRational.NEGATIVE_INFINITY))
         return MutableInfiniteRational.POSITIVE_INFINITY;
      if (this.equals(MutableInfiniteRational.POSITIVE_INFINITY) && value.equals(MutableInfiniteRational.POSITIVE_INFINITY))
         return MutableInfiniteRational.POSITIVE_INFINITY;
      if (this.isInfinite() && value.isInfinite()) return MutableInfiniteRational.NEGATIVE_INFINITY;

      if (this.isInfinite() && value.signum() == 1) return this;
      if (this.isInfinite() && value.signum() == -1) return this.negate();
      if (value.isInfinite() && this.signum() == 1) return value;
      if (value.isInfinite() && this.signum() == -1) return value.negate();

      this.numerator.multiply(value.numerator);
      this.denominator.multiply(value.denominator);

      return this;
   }

   //TODO: multiplyByPowerOf2?

   /**
    * @see #divide(MutableInfiniteRational)
    */
   public MutableInfiniteRational divide(final long value){return this.divide(MutableInfiniteRational.valueOf(value));}

   /**
    * @see #divide(MutableInfiniteRational)
    */
   public MutableInfiniteRational divide(final BigInteger value){return this.divide(MutableInfiniteRational.valueOf(value));}

   /**
    * @see #divide(MutableInfiniteRational)
    */
   public MutableInfiniteRational divide(final double value){return this.divide(MutableInfiniteRational.valueOf(value));}

   /**
    * @see #divide(MutableInfiniteRational)
    */
   public MutableInfiniteRational divide(final BigDecimal value){return this.divide(MutableInfiniteRational.valueOf(value));}

   /**
    * @see #divide(MutableInfiniteRational)
    */
   public MutableInfiniteRational divide(final InfiniteInteger value){return this.divide(MutableInfiniteRational.valueOf(value));}

   /**
    * @see #divide(MutableInfiniteRational)
    */
   public MutableInfiniteRational divide(final MutableInfiniteInteger value)
   {
      return this.divide(MutableInfiniteRational.valueOf(value));
   }

   /**
    * @see #divide(MutableInfiniteRational)
    */
   public MutableInfiniteRational divide(final InfiniteRational value)
   {
      return this.divide(MutableInfiniteRational.valueOf(value));
   }

   /**
    * Simply inverts value (without mutating it) then multiplies.
    *
    * @see #invert()
    * @see #multiply(MutableInfiniteRational)
    */
   public MutableInfiniteRational divide(final MutableInfiniteRational value)
   {
      return this.multiply(value.copy().invert());
   }

   /**
    * Switches the numerator and denominator (same as this<sup>-1</sup>).
    * Note that 0 becomes NaN and &plusmn;&infin; becomes 0.
    */
   public MutableInfiniteRational invert()
   {
      if (this.equals(MutableInfiniteRational.NaN) || this.equalValue(0)) return MutableInfiniteRational.NaN;
      if (this.isInfinite()) return MutableInfiniteRational.valueOf(0);

      final MutableInfiniteInteger oldNumerator = numerator;
      numerator = denominator;
      denominator = oldNumerator;
      normalizeSign();

      return this;
   }

   //TODO: divideByPowerOf2?

   /**
    * @see #power(MutableInfiniteInteger)
    */
   public MutableInfiniteRational power(final long exponent)
   {
      return this.power(MutableInfiniteInteger.valueOf(exponent));
   }

   /**
    * @see #power(MutableInfiniteInteger)
    */
   public MutableInfiniteRational power(final BigInteger exponent)
   {
      return this.power(MutableInfiniteInteger.valueOf(exponent));
   }

   /**
    * @see #power(MutableInfiniteInteger)
    */
   public MutableInfiniteRational power(final InfiniteInteger exponent)
   {
      return this.power(MutableInfiniteInteger.valueOf(exponent));
   }

   /**
    * Returns a MutableInfiniteRational whose value is this<sup>exponent</sup>.
    * There are many special cases, for a full table see {@link InfiniteRational#powerSpecialLookUp(InfiniteRational, InfiniteInteger)
    * this table} except the power method will return the result instead of null.
    *
    * @param exponent to which this InfiniteInteger is to be raised.
    *
    * @return the result including &plusmn;&infin; and NaN
    */
   public MutableInfiniteRational power(final MutableInfiniteInteger exponent)
   {
      final InfiniteRational tableValue = InfiniteRational.powerSpecialLookUp(InfiniteRational.valueOf(this),
            InfiniteInteger.valueOf(exponent));
      if (tableValue != null) return set(tableValue.toMutableInfiniteRational());
      if (exponent.signum() == -1) this.invert();
      final MutableInfiniteInteger absExponent = exponent.copy().abs();
      numerator.power(absExponent);
      denominator.power(absExponent);
      return this;
   }

   /**
    * @return true if this MutableInfiniteInteger is a whole number. false for &plusmn;&infin; and NaN.
    */
   public boolean isWhole()
   {
      if (!this.isFinite()) return false;
      final IntegerQuotient<MutableInfiniteInteger> quotient = numerator.divide(denominator);
      return quotient.getRemainder().equals(0);
   }

   /**
    * Rounds this MutableInfiniteRational towards 0 to the nearest whole number.
    * Alias for {@code roundToWhole(RoundingMode.DOWN)} which is the entire method.
    *
    * @see RoundingMode#DOWN
    * @see #roundToWhole(RoundingMode)
    */
   public MutableInfiniteRational truncateToWhole(){ return roundToWhole(RoundingMode.DOWN); }

   /**
    * Rounds this MutableInfiniteRational towards a whole number which is determined by the given roundingMode.
    * The denominator always becomes 1.
    *
    * @see RoundingMode
    */
   public MutableInfiniteRational roundToWhole(final RoundingMode roundingMode)
   {
      if (!this.isFinite()) return this;

      //divide doesn't mutate
      final IntegerQuotient<MutableInfiniteInteger> quotient = numerator.divide(denominator);

      if (quotient.getRemainder().equals(0)) return set(MutableInfiniteRational.valueOf(quotient.getWholeResult()));

      switch (roundingMode)
      {
         case UP:
         case DOWN:
         case CEILING:
         case FLOOR:
            return roundToWholeNonHalf(quotient, roundingMode);
         case UNNECESSARY:
            throw new ArithmeticException("Rounding necessary for " + this);
      }

      final MutableInfiniteInteger halfWay = denominator.copy();
      //I multiply both by 2 because denominator/2 may not be whole.
      final MutableInfiniteInteger absNumberator = quotient.getRemainder().abs().multiply(2);
      denominator.multiply(2);
      if (is(absNumberator, GREATER_THAN, halfWay)) return roundToWholeNonHalf(quotient, RoundingMode.UP);
      else if (is(absNumberator, LESS_THAN, halfWay)) return set(MutableInfiniteRational.valueOf(quotient.getWholeResult()));  //down
      //else it is exactly half

      switch (roundingMode)
      {
         case HALF_UP:
            return roundToWholeNonHalf(quotient, RoundingMode.UP);
         case HALF_DOWN:
            return roundToWholeNonHalf(quotient, RoundingMode.DOWN);
         //there is no HALF_CEILING or HALF_FLOOR
         case HALF_EVEN:
            //If towards 0 is even then return that else return the number away from 0.
            if (BitWiseUtil.isEven(quotient.getWholeResult().intValue()))
               return set(MutableInfiniteRational.valueOf(quotient.getWholeResult()));
            return roundToWholeNonHalf(quotient, RoundingMode.UP);
      }
      throw new AssertionError("Bug: should be unreachable.");
   }

   private MutableInfiniteRational roundToWholeNonHalf(final IntegerQuotient<MutableInfiniteInteger> quotient,
                                                       final RoundingMode roundingMode)
   {
      switch (roundingMode)
      {
         case UP:
            if (signum() == 1) return set(MutableInfiniteRational.valueOf(quotient.getWholeResult().add(1)));
            return set(MutableInfiniteRational.valueOf(quotient.getWholeResult().subtract(1)));
         case DOWN:
            return set(MutableInfiniteRational.valueOf(quotient.getWholeResult()));
         case CEILING:
            if (signum() == 1) return set(MutableInfiniteRational.valueOf(quotient.getWholeResult().add(1)));
            return set(MutableInfiniteRational.valueOf(quotient.getWholeResult()));
         case FLOOR:
            if (signum() == 1) return set(MutableInfiniteRational.valueOf(quotient.getWholeResult()));
            return set(MutableInfiniteRational.valueOf(quotient.getWholeResult().subtract(1)));
      }
      throw new AssertionError("Bug: method called for non-simple case");
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

   /**
    * Compares this MutableInfiniteRational with the specified other for numeric equality.
    * The natural order is as expected with &plusmn;&infin; being at either end.
    * With the exception that &infin; &lt; NaN (this is consistent with Float/Double.compareTo).
    *
    * @param other the value to be compared to this
    *
    * @return -1, 0 or 1 if this MutableInfiniteRational is numerically less than, equal
    * to, or greater than other.
    */
   @Override
   public int compareTo(final MutableInfiniteRational other)
   {
      if (this == other) return THIS_EQUAL;  //recall that special values are singletons
      if (this == MutableInfiniteRational.NaN || other == MutableInfiniteRational.NEGATIVE_INFINITY) return THIS_GREATER;
      if (this == MutableInfiniteRational.NEGATIVE_INFINITY || other == MutableInfiniteRational.NaN) return THIS_LESSER;
      //+Infinity is only greater if NaN isn't involved
      if (this == MutableInfiniteRational.POSITIVE_INFINITY) return THIS_GREATER;
      if (other == MutableInfiniteRational.POSITIVE_INFINITY) return THIS_LESSER;

      final byte thisSignum = this.signum();
      final byte otherSignum = other.signum();
      if (thisSignum > otherSignum) return THIS_GREATER;
      if (thisSignum < otherSignum) return THIS_LESSER;
      if (thisSignum == 0 && otherSignum == 0) return THIS_EQUAL;

      //at this point: they are not the same object, they have the same sign, they are not special values.

      final MutableInfiniteInteger leastCommonMultiple = this.denominator.leastCommonMultiple(other.denominator);
      //There is no remainder but don't call divideDropRemainder because that mutates and this way avoids a pointless copy
      //multiplier will always be positive because denominator is positive.
      final MutableInfiniteInteger thisMultiplier = leastCommonMultiple.divide(this.denominator).getWholeResult();
      final MutableInfiniteInteger thisNewNumerator = this.numerator.copy().multiply(thisMultiplier);
      final MutableInfiniteInteger otherMultiplier = leastCommonMultiple.divide(other.denominator).getWholeResult();
      final MutableInfiniteInteger otherNewNumerator = other.numerator.copy().multiply(otherMultiplier);

      return thisNewNumerator.compareTo(otherNewNumerator);
   }

   /**
    * Warning: equals doesn't match compareTo! equals uses the exact numerator and denominator for equality therefore 1/2 is not
    * equal to 2/4. If this is not desired then call use reduce on each or use compareTo.
    *
    * @see #reduce()
    * @see #compareTo(MutableInfiniteRational)
    */
   @Override
   public boolean equals(final Object other)
   {
      if (this == other) return true;
      if (other == null || getClass() != other.getClass()) return false;
      final MutableInfiniteRational that = (MutableInfiniteRational) other;
      return Objects.equals(numerator, that.numerator) && Objects.equals(denominator, that.denominator);
   }

   /**
    * Same as {@link #equalValue(Object)} but avoids unneeded boxing.
    *
    * @see #equalValue(Object)
    */
   public boolean equalValue(final long other){return this.compareTo(MutableInfiniteRational.valueOf(other)) == 0;}

   /**
    * Same as {@link #equalValue(Object)} but avoids unneeded boxing.
    *
    * @see #equalValue(Object)
    */
   public boolean equalValue(final double other){return this.compareTo(MutableInfiniteRational.valueOf(other)) == 0;}

   /**
    * Compares this MutableInfiniteRational with the specified value for numeric equality.
    * Since this uses numeric equality rather than object equality the result is unaffected by {@link #reduce()}.
    *
    * @param other the value to be compared to this
    *
    * @return true if this MutableInfiniteRational has the same numeric value as the value parameter
    *
    * @see #longValue()
    * @see #compareTo(MutableInfiniteRational)
    */
   public boolean equalValue(final Object other)
   {
      if (this == other) return true;
      if (other instanceof Byte) return this.compareTo(MutableInfiniteRational.valueOf((Byte) other)) == 0;
      if (other instanceof Short) return this.compareTo(MutableInfiniteRational.valueOf((Short) other)) == 0;
      if (other instanceof Integer) return this.compareTo(MutableInfiniteRational.valueOf((Integer) other)) == 0;
      if (other instanceof Long) return this.compareTo(MutableInfiniteRational.valueOf((Long) other)) == 0;
      if (other instanceof Float) return this.compareTo(MutableInfiniteRational.valueOf((Float) other)) == 0;
      if (other instanceof Double) return this.compareTo(MutableInfiniteRational.valueOf((Double) other)) == 0;
      if (other instanceof BigInteger) return this.compareTo(MutableInfiniteRational.valueOf((BigInteger) other)) == 0;
      if (other instanceof BigDecimal) return this.compareTo(MutableInfiniteRational.valueOf((BigDecimal) other)) == 0;
      if (other instanceof InfiniteInteger) return this.compareTo(MutableInfiniteRational.valueOf((InfiniteInteger) other)) == 0;
      if (other instanceof MutableInfiniteInteger)
         return this.compareTo(MutableInfiniteRational.valueOf((MutableInfiniteInteger) other)) == 0;
      if (other instanceof InfiniteRational) return this.compareTo(MutableInfiniteRational.valueOf((InfiniteRational) other)) == 0;
      if (other instanceof MutableInfiniteRational) return this.compareTo((MutableInfiniteRational) other) == 0;
      return false;
   }

   @Override
   public int hashCode()
   {
      return Objects.hash(numerator, denominator);
   }

   /**
    * This shows the lowest 20 digits in base 10 of both the numerator and denominator so that this number can
    * display something useful to humans
    * when given to a logger or exception. If either number is cut off then it will have a … (after the minus sign).
    * This method will always fit within a string and is reasonably fast. Max long is 19 digits so it won't
    * get cut off. This uses the mixed fraction format.
    *
    * @return String representation of this MutableInfiniteRational.
    * The format may change arbitrarily.
    *
    * @see #toImproperFractionalString()
    * @see #toMixedFactionalString(int)
    * @see #toDecimalString(int, int)
    */
   @Override
   public String toString()
   {
      if (this.equals(MutableInfiniteRational.POSITIVE_INFINITY)) return "Infinity";
      if (this.equals(MutableInfiniteRational.NEGATIVE_INFINITY)) return "-Infinity";
      if (this.equals(MutableInfiniteRational.NaN)) return "NaN";
      if (denominator.equals(1)) return numerator.toString();

      return numerator.toString() + "/" + denominator.toString();
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
      // TODO: toDecimalString
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

   /**
    * Mutates this to have the same value as the parameter (a copy not a live reference).
    * In order to maintain the singleton constants mutation will not
    * occur if this or the parameter are a singleton constant.
    * This isn't overloaded because it isn't really intended for client use.
    * See overloaded setNumerator and setDenominator instead.
    *
    * @return the result which is itself or a defined singleton
    *
    * @see #setNumerator(MutableInfiniteInteger)
    * @see #setDenominator(MutableInfiniteInteger)
    */
   public MutableInfiniteRational set(final MutableInfiniteRational newValue)
   {
      if (!newValue.isFinite()) return newValue;  //immutable constants can't be changed or copied.
      if (!this.isFinite()) return this;
      numerator = newValue.numerator.copy();
      denominator = newValue.denominator.copy();
      return this;
   }

   /**
    * @see #setNumerator(MutableInfiniteInteger)
    */
   public MutableInfiniteRational setNumerator(final long newNumerator)
   {
      return this.setNumerator(MutableInfiniteInteger.valueOf(newNumerator));
   }

   /**
    * @see #setNumerator(MutableInfiniteInteger)
    */
   public MutableInfiniteRational setNumerator(final BigInteger newNumerator)
   {
      return this.setNumerator(MutableInfiniteInteger.valueOf(newNumerator));
   }

   /**
    * @see #setNumerator(MutableInfiniteInteger)
    */
   public MutableInfiniteRational setNumerator(final InfiniteInteger newNumerator)
   {
      return this.setNumerator(MutableInfiniteInteger.valueOf(newNumerator));
   }

   /**
    * Mutates the numerator to have the same value as the parameter (a copy not a live reference).
    * In order to maintain the singleton constants mutation will not
    * occur if this or the parameter are a singleton constant.
    *
    * @return the result which is itself or a defined singleton
    *
    * @see #setDenominator(MutableInfiniteInteger)
    */
   public MutableInfiniteRational setNumerator(final MutableInfiniteInteger newNumerator)
   {
      if (!newNumerator.isFinite()) return MutableInfiniteRational.valueOf(newNumerator);  //this will convert the constant
      if (!this.isFinite()) return this;  //immutable constants can't be changed
      numerator = newNumerator.copy();
      return this;
   }

   /**
    * @return a copy of the numerator or an immutable constant.
    */
   public MutableInfiniteInteger getNumerator()
   {
      return numerator.copy();
   }

   /**
    * @see #setDenominator(MutableInfiniteInteger)
    */
   public MutableInfiniteRational setDenominator(final long newDenominator)
   {
      return this.setDenominator(MutableInfiniteInteger.valueOf(newDenominator));
   }

   /**
    * @see #setDenominator(MutableInfiniteInteger)
    */
   public MutableInfiniteRational setDenominator(final BigInteger newDenominator)
   {
      return this.setDenominator(MutableInfiniteInteger.valueOf(newDenominator));
   }

   /**
    * @see #setDenominator(MutableInfiniteInteger)
    */
   public MutableInfiniteRational setDenominator(final InfiniteInteger newDenominator)
   {
      return this.setDenominator(MutableInfiniteInteger.valueOf(newDenominator));
   }

   /**
    * Mutates the denominator to have the same value as the parameter (a copy not a live reference).
    * In order to maintain the singleton constants mutation will not
    * occur if this or the parameter are a singleton constant.
    * Likewise if newDenominator is 0 then NaN will be returned without mutation.
    *
    * @return the result which is itself or a defined singleton
    *
    * @see #setNumerator(MutableInfiniteInteger)
    */
   public MutableInfiniteRational setDenominator(final MutableInfiniteInteger newDenominator)
   {
      if (!newDenominator.isFinite()) return MutableInfiniteRational.valueOf(newDenominator);  //this will convert the constant
      if (!this.isFinite()) return this;  //immutable constants can't be changed
      if (newDenominator.equals(0)) return MutableInfiniteRational.NaN;
      denominator = newDenominator.copy();
      normalizeSign();
      return this;
   }

   /**
    * @return a copy of the denominator. Note that constants have a denominator of 1.
    */
   public MutableInfiniteInteger getDenominator()
   {
      return denominator.copy();
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
