package com.github.skySpiral7.java.infinite.numbers;

import com.github.skySpiral7.java.infinite.util.BitWiseUtil;
import com.github.skySpiral7.java.infinite.util.RadixUtil;
import com.github.skySpiral7.java.numbers.NumberFormatException;
import com.github.skySpiral7.java.staticSerialization.ObjectStreamReader;
import com.github.skySpiral7.java.staticSerialization.ObjectStreamWriter;
import com.github.skySpiral7.java.staticSerialization.StaticSerializable;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

/**
 * This supports all possible rational numbers with perfect precision by using {@link InfiniteInteger}.
 * A rational number is defined as numerator/denominator where numerator and denominator are both integers and denominator is not 0.
 *
 * @see InfiniteInteger
 */
public final class InfiniteRational extends AbstractInfiniteRational<InfiniteRational> implements StaticSerializable
{
   private static final long serialVersionUID = 1L;

   /**
    * Common abbreviation for "not a number". This constant is the result of invalid math such as 0/0.
    * Note that this is a normal object such that <code>(MutableInfiniteRational.NaN == MutableInfiniteRational.NaN)</code> is
    * always true. Therefore it is logically correct unlike the floating point unit's NaN.
    */
   public static final InfiniteRational NaN = new InfiniteRational(MutableInfiniteRational.NaN);
   /**
    * +∞ is a concept rather than a number and can't be the result of math involving finite numbers.
    * It is defined for completeness and behaves as expected with math resulting in ±∞ or NaN.
    */
   public static final InfiniteRational POSITIVE_INFINITY = new InfiniteRational(MutableInfiniteRational.POSITIVE_INFINITY);
   /**
    * -∞ is a concept rather than a number and can't be the result of math involving finite numbers.
    * It is defined for completeness and behaves as expected with math resulting in ±∞ or NaN.
    */
   public static final InfiniteRational NEGATIVE_INFINITY = new InfiniteRational(MutableInfiniteRational.NEGATIVE_INFINITY);
   /**
    * This constant represents 0 and it is the only InfiniteRational that can be 0 (ie this is a singleton).
    * Therefore it is safe to use pointer equality for comparison: <code>if(var == InfiniteRational.ZERO)</code>
    */
   public static final InfiniteRational ZERO = new InfiniteRational(MutableInfiniteRational.valueOf(0));
   /**
    * This constant represents 1 and it is the only InfiniteRational that can be 1 (ie this is a singleton).
    * Therefore it is safe to use pointer equality for comparison: <code>if(var == InfiniteRational.ONE)</code>
    */
   public static final InfiniteRational ONE = new InfiniteRational(MutableInfiniteRational.valueOf(1));
   /**
    * This constant represents 2 and it is the only InfiniteRational that can be 2 (ie this is a singleton).
    * Therefore it is safe to use pointer equality for comparison: <code>if(var == InfiniteRational.TWO)</code>
    */
   public static final InfiniteRational TWO = new InfiniteRational(MutableInfiniteRational.valueOf(2));

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
      if (baseNumber.isNaN()) return InfiniteRational.NaN;
      if (MutableInfiniteRational.POSITIVE_INFINITY.equals(baseNumber)) return InfiniteRational.POSITIVE_INFINITY;
      if (MutableInfiniteRational.NEGATIVE_INFINITY.equals(baseNumber)) return InfiniteRational.NEGATIVE_INFINITY;

      if (baseNumber.equalValue(0)) return InfiniteRational.ZERO;
      if (baseNumber.equalValue(1)) return InfiniteRational.ONE;
      if (baseNumber.equalValue(2)) return InfiniteRational.TWO;

      return new InfiniteRational(baseNumber.copy());  //TODO: should reduce be called here?
   }

   /**
    * Simply calls parseString with radix 10. This exists for orthogonality and ease of use.
    *
    * @see #parseString(String, int)
    */
   public static InfiniteRational valueOf(final String value)
   {
      return InfiniteRational.valueOf(MutableInfiniteRational.parseString(value, 10));
   }

   /**
    * Simply calls parseString. This exists for orthogonality.
    *
    * @see #parseString(String, int)
    */
   public static InfiniteRational valueOf(final String value, final int radix)
   {
      return InfiniteRational.valueOf(MutableInfiniteRational.parseString(value, radix));
   }

   /**
    * Simply calls parseString with radix 10. This exists for orthogonality and ease of use.
    *
    * @see #parseString(String, int)
    */
   public static InfiniteRational parseString(final String value)
   {
      return InfiniteRational.valueOf(MutableInfiniteRational.parseString(value, 10));
   }

   /**
    * Parses the string based on the format. Supports mixed fraction, improper fraction, and
    * decimal.
    *
    * @see #parseMixedFraction(String, int)
    * @see #parseImproperFraction(String, int)
    * @see #parseDecimal(String, int)
    */
   public static InfiniteRational parseString(final String value, final int radix)
   {
      return InfiniteRational.valueOf(MutableInfiniteRational.parseString(value, radix));
   }

   /**
    * Simply calls parseImproperFraction with radix 10. This exists for orthogonality and ease of use.
    *
    * @see #parseImproperFraction(String, int)
    */
   public static InfiniteRational parseImproperFraction(final String value)
   {
      return InfiniteRational.valueOf(MutableInfiniteRational.parseImproperFraction(value));
   }

   /**
    * <p>Parses the inputString as an InfiniteRational in the radix specified.
    * The format used is the same as {@link MutableInfiniteRational#toImproperFractionalString()}.
    * See {@link RadixUtil#toString(long, int)} for a description of legal characters per radix.
    * See {@link RadixUtil#parseLong(String, int)} for more details.</p>
    *
    * <p>The special values of ∞, -∞, and ∉ℚ (for NaN) can be parsed given any valid
    * radix.</p>
    *
    * @param inputString the String to be parsed
    * @param radix       the number base
    * @return the InfiniteRational that inputString represents
    * @throws NullPointerException     if inputString is null
    * @throws NumberFormatException    if inputString doesn't match the format of
    *                                  {@link MutableInfiniteRational#toImproperFractionalString()}
    * @throws IllegalArgumentException {@code if(radix > 62 || radix < 1)}
    * @see Long#parseLong(String, int)
    * @see RadixUtil#toString(long, int)
    * @see RadixUtil#parseLong(String, int)
    * @see MutableInfiniteRational#toImproperFractionalString()
    */
   public static InfiniteRational parseImproperFraction(final String inputString, final int radix)
   {
      return InfiniteRational.valueOf(MutableInfiniteRational.parseImproperFraction(inputString, radix));
   }

   /**
    * Simply calls parseMixedFraction with radix 10. This exists for orthogonality and ease of use.
    *
    * @see #parseMixedFraction(String, int)
    */
   public static InfiniteRational parseMixedFraction(final String value)
   {
      return InfiniteRational.valueOf(MutableInfiniteRational.parseMixedFraction(value, 10));
   }

   /**
    * <p>Parses the inputString as an InfiniteRational in the radix specified.
    * The format used is the same as {@link MutableInfiniteRational#toMixedFractionalString()}.
    * See {@link RadixUtil#toString(long, int)} for a description of legal characters per radix.
    * See {@link RadixUtil#parseLong(String, int)} for more details.</p>
    *
    * <p>The special values of ∞, -∞, and ∉ℚ (for NaN) can be parsed given any valid
    * radix.</p>
    *
    * @param inputString the String to be parsed
    * @param radix       the number base
    * @return the InfiniteRational that inputString represents
    * @throws NullPointerException     if inputString is null
    * @throws NumberFormatException    if inputString doesn't match the format of {@link MutableInfiniteRational#toMixedFractionalString()}
    * @throws IllegalArgumentException {@code if(radix > 62 || radix < 1)}
    * @see Long#parseLong(String, int)
    * @see RadixUtil#toString(long, int)
    * @see RadixUtil#parseLong(String, int)
    * @see MutableInfiniteRational#toMixedFractionalString()
    */
   public static InfiniteRational parseMixedFraction(final String inputString, final int radix)
   {
      return InfiniteRational.valueOf(MutableInfiniteRational.parseMixedFraction(inputString, radix));
   }

   /**
    * Simply calls parseDecimal with radix 10. This exists for orthogonality and ease of use.
    *
    * @see #parseDecimal(String, int)
    */
   public static InfiniteRational parseDecimal(final String value)
   {
      return InfiniteRational.valueOf(MutableInfiniteRational.parseDecimal(value));
   }

   /**
    * <p>Parses the inputString as an InfiniteRational in the radix specified.
    * The format used is the same as {@link MutableInfiniteRational#toDecimalString(int)}.
    * See {@link RadixUtil#toString(long, int)} for a description of legal characters per radix.
    * See {@link RadixUtil#parseLong(String, int)} for more details.</p>
    *
    * <p>Repeating decimals are not currently supported. The returned denominator will
    * likely be huge so it might be a good idea to call {@link MutableInfiniteRational#reduce()} afterward.</p>
    *
    * <p>The special values of ∞, -∞, and ∉ℚ (for NaN) can be parsed given any valid
    * radix.</p>
    *
    * @param inputString the String to be parsed
    * @param radix       the number base
    * @return the InfiniteRational that inputString represents
    * @throws NullPointerException     if inputString is null
    * @throws NumberFormatException    if inputString doesn't match the format of {@link MutableInfiniteRational#toDecimalString(int)}
    * @throws IllegalArgumentException Repeating decimals are not currently supported.
    * @throws IllegalArgumentException {@code if(radix > 62 || radix < 1)}
    * @throws IllegalArgumentException if radix is 1 and inputString isn't a whole number.
    * @see Long#parseLong(String, int)
    * @see RadixUtil#toString(long, int)
    * @see RadixUtil#parseLong(String, int)
    * @see MutableInfiniteRational#toDecimalString(int)
    */
   public static InfiniteRational parseDecimal(final String inputString, final int radix)
   {
      return InfiniteRational.valueOf(MutableInfiniteRational.parseDecimal(inputString, radix));
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

   //BigInt is wrong: 0^0 is NaN but it returns 1. And 1^(-2) is 1 but it throws
   //TODO: make this table into a class that can't be modified
   /**
    * Used by powerSpecialLookUp. Private to prevent modification.
    *
    * @see #powerSpecialLookUp(InfiniteRational, InfiniteInteger)
    */
   private final static InfiniteRational[][] powerSpecialCaseTable = {
      //[baseIndex][exponentIndex]
      //the elements are in order: 0, 1, Infinity, -Infinity, -X (other), X (other)
      {NaN, ZERO, ZERO, NaN, NaN, ZERO}, //0
      {ONE, ONE, NaN, NaN, ONE, ONE},  //1
      {NaN, POSITIVE_INFINITY, POSITIVE_INFINITY, ZERO, ZERO, POSITIVE_INFINITY},  //Infinity
      {NaN, NEGATIVE_INFINITY, NaN, NaN, ZERO, null},  //-Infinity
      {ONE, null, NaN, ZERO, null, null},  //-X (other)
      {ONE, null, POSITIVE_INFINITY, ZERO, null, null}  //X (other)
   };

   /**
    * <table border="1">
    * <caption><b>Special cases for Base<sup>Exponent</sup></b></caption>
    * <tr><th></th><th colspan="6">Exponent</th></tr>
    * <tr valign="top">
    * <th>Base</th>     <th width="30px">0</th>
    * <th width="30px">1</th>
    * <th width="30px">∞</th>
    * <th width="30px">-∞</th>
    * <th width="30px">-X</th>
    * <th width="30px">X</th></tr>
    *
    * <tr align="center"><td>0</td>  <td>NaN</td> <td>0</td>  <td>0</td>   <td>NaN</td> <td>NaN</td> <td>0</td></tr>
    * <tr align="center"><td>1</td>  <td>1</td>   <td>1</td>  <td>NaN</td> <td>NaN</td> <td>1</td>   <td>1</td></tr>
    * <tr align="center"><td>∞</td>  <td>NaN</td> <td>∞</td>  <td>∞</td>   <td>0</td>   <td>0</td>   <td>∞</td></tr>
    * <tr align="center"><td>-∞</td> <td>NaN</td> <td>-∞</td> <td>NaN</td> <td>NaN</td> <td>0</td>   <td>±∞</td></tr>
    * <tr align="center"><td>-X</td> <td>1</td>   <td>-X</td> <td>NaN</td> <td>0</td>   <td>1/?</td> <td>?</td></tr>
    * <tr align="center"><td>X</td>  <td>1</td>   <td>X</td>  <td>∞</td>   <td>0</td>   <td>1/?</td> <td>?</td></tr>
    * </table>
    *
    * <p>In the table above X is an integer greater than one. 1/? means the result is a
    * fraction instead of an integer. And ? means that the answer is an integer but this method doesn't know the exact value.
    * In the cases of 1/? and ? null is returned. In all other cases the answer is returned.</p>
    *
    * @return the answer or null
    */
   static InfiniteRational powerSpecialLookUp(final InfiniteRational base, final InfiniteInteger exponent)
   {
      if (base.isNaN() || exponent.isNaN()) return InfiniteRational.NaN;
      if (exponent.equalValue(1)) return base;  //always true
      //TODO: test all these special cases of pow

      final byte baseIndex = InfiniteRational.powerSpecialIndex(base);
      final byte exponentIndex = InfiniteRational.powerSpecialIndex(InfiniteRational.valueOf(MutableInfiniteRational.valueOf(exponent)));
      //exponentIndex is never 1 due to above if check
      final InfiniteRational tableValue = InfiniteRational.powerSpecialCaseTable[baseIndex][exponentIndex];

      if (tableValue != null) return tableValue;

      if (base.equals(InfiniteRational.NEGATIVE_INFINITY))
      {
         //exponent.isFinite by this point (exponentIndex == 5 for X)
         if (BitWiseUtil.isEven(exponent.intValue())) return InfiniteRational.POSITIVE_INFINITY;
         return InfiniteRational.NEGATIVE_INFINITY;
      }

      //baseIndex == 4 or 5 and exponentIndex == 4 or 5 for -X or X. in all 4 cases return null
      return null;
   }

   /**
    * Used by powerSpecialLookUp to find the table index to use for a given value.
    *
    * @return the table index which matches the powerSpecialCaseTable
    * @see #powerSpecialLookUp(InfiniteRational, InfiniteInteger)
    * @see #powerSpecialCaseTable
    */
   private static byte powerSpecialIndex(final InfiniteRational value)
   {
      if (value.equals(InfiniteRational.ZERO)) return 0;
      if (value.equals(InfiniteRational.ONE)) return 1;
      if (value.equals(InfiniteRational.POSITIVE_INFINITY)) return 2;
      if (value.equals(InfiniteRational.NEGATIVE_INFINITY)) return 3;
      if (value.signum() == -1) return 4;
      return 5;
   }

   /**
    * @return -1, 0 or 1 as the value of this number is negative, zero or
    * positive respectively. NaN returns 0.
    */
   public byte signum()
   {
      return baseNumber.signum();
   }

   /**
    * Compares this == NaN.
    *
    * @return true if this InfiniteRational is the constant for NaN.
    * @see #NaN
    */
   public boolean isNaN(){return this == InfiniteRational.NaN;}

   /**
    * Compares this InfiniteRational to both positive and negative infinity.
    *
    * @return true if this InfiniteRational is either of the infinity constants.
    * @see #POSITIVE_INFINITY
    * @see #NEGATIVE_INFINITY
    */
   public boolean isInfinite()
   {
      return (this == InfiniteRational.POSITIVE_INFINITY || this == InfiniteRational.NEGATIVE_INFINITY);
   }

   /**
    * Compares this InfiniteRational to ±∞ and NaN (returns false if this is any of them).
    *
    * @return true if this InfiniteRational is not a special value (ie if this is a finite number).
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
      if (!this.isFinite() || !that.isFinite()) return false;
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

   public static InfiniteRational readFromStream(final ObjectStreamReader reader)
   {
      //valueOf will handle constant conversions
      return InfiniteRational.valueOf(reader.readObject(MutableInfiniteRational.class));
   }

   @Override
   public void writeToStream(final ObjectStreamWriter writer)
   {
      writer.writeObject(baseNumber);
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
