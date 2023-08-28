package com.github.skySpiral7.java.infinite.numbers;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import com.github.skySpiral7.java.Copyable;
import com.github.skySpiral7.java.infinite.exceptions.WillNotFitException;
import com.github.skySpiral7.java.infinite.util.BitWiseUtil;
import com.github.skySpiral7.java.infinite.util.RadixUtil;
import com.github.skySpiral7.java.numbers.NumberFormatException;
import com.github.skySpiral7.java.staticSerialization.ObjectStreamReader;
import com.github.skySpiral7.java.staticSerialization.ObjectStreamWriter;
import com.github.skySpiral7.java.staticSerialization.StaticSerializable;

import static com.github.skySpiral7.java.pojo.Comparison.GREATER_THAN;
import static com.github.skySpiral7.java.pojo.Comparison.LESS_THAN;
import static com.github.skySpiral7.java.util.ComparableSugar.THIS_EQUAL;
import static com.github.skySpiral7.java.util.ComparableSugar.THIS_GREATER;
import static com.github.skySpiral7.java.util.ComparableSugar.THIS_LESSER;
import static com.github.skySpiral7.java.util.ComparableSugar.is;

/**
 * This supports all possible rational numbers with perfect precision by using {@link InfiniteInteger}.
 * A rational number is defined as numerator/denominator where numerator and denominator are both integers and denominator is not 0.
 *
 * @see InfiniteInteger
 */
//BigDecimal max is 10**maxInt which is 10**(2**31-1). I think.
//BigDecimal has unscaledValue*10**-scale however the unscaledValue is BigInteger so it might lose precision. TODO: Check the math.
public final class MutableInfiniteRational extends AbstractInfiniteRational<MutableInfiniteRational>
      implements Copyable<MutableInfiniteRational>, StaticSerializable
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
    * +∞ is a concept rather than a number and can't be the result of math involving finite numbers.
    * It is defined for completeness and behaves as expected with math resulting in ±∞ or NaN.
    *
    * This value is immutable.
    */
   public static final MutableInfiniteRational POSITIVE_INFINITY = new MutableInfiniteRational(MutableInfiniteInteger.POSITIVE_INFINITY,
         MutableInfiniteInteger.valueOf(1));
   /**
    * -∞ is a concept rather than a number and can't be the result of math involving finite numbers.
    * It is defined for completeness and behaves as expected with math resulting in ±∞ or NaN.
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
    * Note that X / 0 == NaN. ±∞ / 0 == NaN. ±∞ / ±∞ == NaN.
    * ±∞ / X == ±∞. X / ±∞ == 0.
    */
   public static MutableInfiniteRational valueOf(final MutableInfiniteInteger numerator, final MutableInfiniteInteger denominator)
   {
      if (numerator.isNaN() || denominator.isNaN()) return MutableInfiniteRational.NaN;
      if (numerator.isInfinite() && denominator.isInfinite()) return MutableInfiniteRational.NaN;
      if (denominator.equalValue(0)) return MutableInfiniteRational.NaN;  //this is mathematically correct
      //all NaN results are covered

      if (MutableInfiniteInteger.POSITIVE_INFINITY.equals(numerator)) return MutableInfiniteRational.POSITIVE_INFINITY;
      if (MutableInfiniteInteger.NEGATIVE_INFINITY.equals(numerator)) return MutableInfiniteRational.NEGATIVE_INFINITY;
      //this isn't reducing it's performing the math asked for because denominator must be finite
      if (denominator.isInfinite())
         return new MutableInfiniteRational(MutableInfiniteInteger.valueOf(0), MutableInfiniteInteger.valueOf(1));

      //Now they are both finite. The defensive copy is to prevent internal corruption and unexpected changes.
      final MutableInfiniteRational result = new MutableInfiniteRational(numerator.copy(), denominator.copy());
      result.normalizeSign();
      //it could easily check for numerator=0 or numerator=denominator but doesn't reduce ever
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
    * Simply calls parseString with radix 10. This exists for orthogonality and ease of use.
    *
    * @see #parseString(String, int)
    */
   public static MutableInfiniteRational valueOf(final String value)
   {
      return MutableInfiniteRational.parseString(value, 10);
   }

   /**
    * Simply calls parseString. This exists for orthogonality.
    *
    * @see #parseString(String, int)
    */
   public static MutableInfiniteRational valueOf(final String value, final int radix)
   {
      return MutableInfiniteRational.parseString(value, radix);
   }

   /**
    * Simply calls parseString with radix 10. This exists for orthogonality and ease of use.
    *
    * @see #parseString(String, int)
    */
   public static MutableInfiniteRational parseString(final String value)
   {
      return MutableInfiniteRational.parseString(value, 10);
   }

   /**
    * Parses the string based on the format. Supports mixed fraction, improper fraction, and
    * decimal.
    *
    * @see #parseMixedFraction(String, int)
    * @see #parseImproperFraction(String, int)
    * @see #parseDecimal(String, int)
    */
   public static MutableInfiniteRational parseString(final String value, final int radix)
   {
      //all can handle whole numbers
      //check for things exclusive to format
      if (value.trim().contains(" ")) return MutableInfiniteRational.parseMixedFraction(value, radix);
      if (value.contains("/")) return MutableInfiniteRational.parseImproperFraction(value, radix);
      return MutableInfiniteRational.parseDecimal(value, radix);
   }

   /**
    * Simply calls parseImproperFraction with radix 10. This exists for orthogonality and ease of use.
    *
    * @see #parseImproperFraction(String, int)
    */
   public static MutableInfiniteRational parseImproperFraction(final String value)
   {
      return MutableInfiniteRational.parseImproperFraction(value, 10);
   }

   /**
    * <p>Parses the inputString as a MutableInfiniteRational in the radix specified.
    * The format used is the same as {@link #toImproperFractionalString()}.
    * See {@link RadixUtil#toString(long, int)} for a description of legal characters per radix.
    * See {@link RadixUtil#parseLong(String, int)} for more details.</p>
    *
    * <p>The special values of ∞, -∞, and ∉ℚ (for NaN) can be parsed given any valid
    * radix.</p>
    *
    * @param inputString the String to be parsed
    * @param radix       the number base
    *
    * @return the MutableInfiniteRational that inputString represents
    *
    * @throws NullPointerException     if inputString is null
    * @throws NumberFormatException    if inputString doesn't match the format of {@link #toImproperFractionalString()}
    * @throws IllegalArgumentException {@code if(radix > 62 || radix < 1)}
    * @see Long#parseLong(String, int)
    * @see RadixUtil#toString(long, int)
    * @see RadixUtil#parseLong(String, int)
    * @see #toImproperFractionalString()
    */
   public static MutableInfiniteRational parseImproperFraction(final String inputString, final int radix)
   {
      final String workingString = inputString.trim();

      //leading + is valid even though this class won't generate it
      if ("∞".equals(workingString) || "+∞".equals(workingString)) return MutableInfiniteRational.POSITIVE_INFINITY;
      if ("-∞".equals(workingString)) return MutableInfiniteRational.NEGATIVE_INFINITY;
      if ("∉ℚ".equals(workingString)) return MutableInfiniteRational.NaN;

      final String[] stringParts = MutableInfiniteRational.literalSplitOnce(workingString, "/");
      final MutableInfiniteInteger numerator = MutableInfiniteInteger.parseString(stringParts[0], radix);

      if (stringParts.length == 1) return MutableInfiniteRational.valueOf(numerator);
      stringParts[1] = stringParts[1].trim();
      if (stringParts[1].startsWith("+") || stringParts[1].startsWith("-")) throw NumberFormatException.forInputString(inputString);

      final MutableInfiniteInteger denominator = MutableInfiniteInteger.parseString(stringParts[1], radix);
      return MutableInfiniteRational.valueOf(numerator, denominator);
   }

   /**
    * Simply calls parseMixedFraction with radix 10. This exists for orthogonality and ease of use.
    *
    * @see #parseMixedFraction(String, int)
    */
   public static MutableInfiniteRational parseMixedFraction(final String value)
   {
      return MutableInfiniteRational.parseMixedFraction(value, 10);
   }

   /**
    * <p>Parses the inputString as a MutableInfiniteRational in the radix specified.
    * The format used is the same as {@link #toMixedFractionalString()}.
    * See {@link RadixUtil#toString(long, int)} for a description of legal characters per radix.
    * See {@link RadixUtil#parseLong(String, int)} for more details.</p>
    *
    * <p>The special values of ∞, -∞, and ∉ℚ (for NaN) can be parsed given any valid
    * radix.</p>
    *
    * @param inputString the String to be parsed
    * @param radix       the number base
    *
    * @return the MutableInfiniteRational that inputString represents
    *
    * @throws NullPointerException     if inputString is null
    * @throws NumberFormatException    if inputString doesn't match the format of {@link #toMixedFractionalString()}
    * @throws IllegalArgumentException {@code if(radix > 62 || radix < 1)}
    * @see Long#parseLong(String, int)
    * @see RadixUtil#toString(long, int)
    * @see RadixUtil#parseLong(String, int)
    * @see #toMixedFractionalString()
    */
   public static MutableInfiniteRational parseMixedFraction(final String inputString, final int radix)
   {
      final String workingString = inputString.trim();

      //leading + is valid even though this class won't generate it
      if ("∞".equals(workingString) || "+∞".equals(workingString)) return MutableInfiniteRational.POSITIVE_INFINITY;
      if ("-∞".equals(workingString)) return MutableInfiniteRational.NEGATIVE_INFINITY;
      if ("∉ℚ".equals(workingString)) return MutableInfiniteRational.NaN;

      if (!workingString.contains(" ")) return MutableInfiniteRational.parseImproperFraction(inputString, radix);
      final String[] stringParts = MutableInfiniteRational.literalSplitOnce(workingString, " ");
      stringParts[1] = stringParts[1].trim();
      if (stringParts[1].startsWith("+") || stringParts[1].startsWith("-")) throw NumberFormatException.forInputString(inputString);

      final MutableInfiniteInteger whole = MutableInfiniteInteger.parseString(stringParts[0], radix);
      final MutableInfiniteRational result = MutableInfiniteRational.valueOf(whole);
      final MutableInfiniteRational fraction = MutableInfiniteRational.parseImproperFraction(stringParts[1], radix);
      return result.add(fraction);
   }

   /**
    * Simply calls parseDecimal with radix 10. This exists for orthogonality and ease of use.
    *
    * @see #parseDecimal(String, int)
    */
   public static MutableInfiniteRational parseDecimal(final String value)
   {
      return MutableInfiniteRational.parseDecimal(value, 10);
   }

   /**
    * <p>Parses the inputString as a MutableInfiniteRational in the radix specified.
    * The format used is the same as {@link #toDecimalString(int)}.
    * See {@link RadixUtil#toString(long, int)} for a description of legal characters per radix.
    * See {@link RadixUtil#parseLong(String, int)} for more details.</p>
    *
    * <p>The returned denominator will
    * likely be huge so it might be a good idea to call {@link #reduce()} afterward.</p>
    *
    * <p>The special values of ∞, -∞, and ∉ℚ (for NaN) can be parsed given any valid
    * radix.</p>
    *
    * @param inputString the String to be parsed
    * @param radix       the number base
    *
    * @return the MutableInfiniteRational that inputString represents
    *
    * @throws NullPointerException     if inputString is null
    * @throws NumberFormatException    if inputString doesn't match the format of {@link #toDecimalString(int)}
    * @throws IllegalArgumentException Repeating decimals are not currently supported.
    * @throws IllegalArgumentException {@code if(radix > 62 || radix < 1)}
    * @throws IllegalArgumentException if radix is 1 and inputString isn't a whole number.
    * @see Long#parseLong(String, int)
    * @see RadixUtil#toString(long, int)
    * @see RadixUtil#parseLong(String, int)
    * @see #toDecimalString(int)
    * @see #toDecimalStringExact(int)
    */
   public static MutableInfiniteRational parseDecimal(final String inputString, final int radix)
   {
      //TODO: double check test coverage of parseDecimal
      String workingString = inputString.trim();

      //leading + is valid even though this class won't generate it
      if ("∞".equals(workingString) || "+∞".equals(workingString)) return MutableInfiniteRational.POSITIVE_INFINITY;
      if ("-∞".equals(workingString)) return MutableInfiniteRational.NEGATIVE_INFINITY;
      if ("∉ℚ".equals(workingString)) return MutableInfiniteRational.NaN;

      if (radix == 1 && (workingString.isEmpty() || workingString.matches("^[+-]$"))) return MutableInfiniteRational.valueOf(0);
      if (workingString.isEmpty()) throw NumberFormatException.forInputRadix(inputString, radix);
      //string must be whole or have a dot, underscore requires repeating digit(s)
      //TODO: regex coverage for fail
      if (!workingString.matches("^[+-]?[a-zA-Z0-9]+$") && !workingString.matches("^[+-]?[a-zA-Z0-9]*\\.[a-zA-Z0-9]*(_[a-zA-Z0-9]+)?$"))
         throw NumberFormatException.forInputString(inputString);

      //TODO: coverage for isNegative
      final boolean isNegative = (workingString.charAt(0) == '-');
      if (isNegative) workingString = workingString.substring(1);  //remove -
      String[] stringParts = MutableInfiniteRational.literalSplitOnce(workingString, ".");
      final MutableInfiniteInteger whole;
      if (stringParts[0].isEmpty() || stringParts[0].matches("^[+-]$")) whole = MutableInfiniteInteger.valueOf(0);
      else whole = MutableInfiniteInteger.parseString(stringParts[0], radix);
      MutableInfiniteRational result = MutableInfiniteRational.valueOf(whole);

      //if there was no dot or was a trailing dot then return whole
      if (stringParts.length == 1 || stringParts[1].isEmpty())
      {
         if (isNegative) result = result.negate();  //does nothing if whole is 0
         return result;
      }
      if (radix == 1) throw new IllegalArgumentException("Base 1 doesn't support decimal representations. inputString: " + inputString);

      if (workingString.contains("_"))
      {
         final String wholeString;
         if (whole.equalValue(0)) wholeString = "0";
            //this is needed because stringParts[0] may be the empty string
            //if preRepeat is also empty then MutableInfiniteInteger.parseString would be given empty (which fails)
            //so give it "0" instead (leading 0s are ignored)
         else wholeString = stringParts[0];
         stringParts = MutableInfiniteRational.literalSplitOnce(stringParts[1], "_");
         final String preRepeat = stringParts[0];
         final String repeating = stringParts[1];

         //TODO: if _0 this could be ignored for below. maybe faster?
         result = MutableInfiniteRational.convertRepeatingDecimalToRational(wholeString, preRepeat, repeating, radix);
         if (isNegative) result = result.negate();
         return result;
      }

      if (stringParts[1].matches("^0+$"))
      {
         //don't bother with fraction math if it's whole
         if (isNegative) result = result.negate();
         return result;
      }
      final MutableInfiniteInteger numerator = MutableInfiniteInteger.parseString(stringParts[1], radix);
      final MutableInfiniteInteger denominator = MutableInfiniteInteger.valueOf(radix).power(stringParts[1].length());
      final MutableInfiniteRational fraction = MutableInfiniteRational.valueOf(numerator, denominator);
      result = result.add(fraction);
      if (isNegative) result = result.negate();
      return result;
   }

   /**
    * @param whole     never empty string (use "0" instead)
    * @param preRepeat may be empty
    * @param repeating never empty string (that's invalid)
    * @param intRadix  never 1 (that's invalid)
    */
   private static MutableInfiniteRational convertRepeatingDecimalToRational(final String whole, final String preRepeat,
                                                                            final String repeating, final int intRadix)
   {
      //https://www.basic-mathematics.com/converting-repeating-decimals-to-fractions.html
      final MutableInfiniteInteger leftSide3;
      {
         final MutableInfiniteInteger bigRadix = MutableInfiniteInteger.valueOf(intRadix);
         //preRepeat.length() may be 0 but repeating.length() won't be
         final MutableInfiniteInteger equation1ShiftDistance = bigRadix.copy().power(preRepeat.length() + repeating.length());
         final MutableInfiniteInteger equation2ShiftDistance = bigRadix.power(preRepeat.length());
         leftSide3 = equation1ShiftDistance.subtract(equation2ShiftDistance);
      }

      final MutableInfiniteInteger rightSide3;
      {
         //since repeating is required this won't be given an empty string (may have leading 0s)
         final MutableInfiniteInteger equation1 = MutableInfiniteInteger.parseString(whole + preRepeat + repeating, intRadix);
         //since whole can't be empty this won't be given an empty string (may have leading 0s)
         final MutableInfiniteInteger equation2 = MutableInfiniteInteger.parseString(whole + preRepeat, intRadix);
         rightSide3 = equation1.subtract(equation2);
      }

      return MutableInfiniteRational.valueOf(rightSide3, leftSide3);
   }

   /**
    * Splits targetString into 2 strings based on a literal delimiter. It only splits once.
    */
   private static String[] literalSplitOnce(final String targetString, final String delimiter)
   {
      final int index = targetString.indexOf(delimiter);
      if (index == -1) return new String[]{targetString};
      final String firstPart = targetString.substring(0, index);
      final String secondPart = targetString.substring(index + delimiter.length());
      return new String[]{firstPart, secondPart};
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
    * Constructs a randomly generated MutableInfiniteRational by calling
    * {@link MutableInfiniteInteger#random(MutableInfiniteInteger, Random)}.
    *
    * @return NaN if either nodeCount < 1 otherwise a new random number is returned.
    *
    * @see Random
    * @see MutableInfiniteInteger#random(MutableInfiniteInteger, Random)
    */
   public static MutableInfiniteRational random(final MutableInfiniteInteger numeratorNodeCount,
                                                final MutableInfiniteInteger denominatorNodeCount)
   {
      return MutableInfiniteRational.random(numeratorNodeCount, denominatorNodeCount, new Random());
   }

   /**
    * Constructs a randomly generated MutableInfiniteRational by calling
    * {@link MutableInfiniteInteger#random(MutableInfiniteInteger, Random)}.
    *
    * @param random source of randomness to be used in computing the new
    *               MutableInfiniteRational.
    *
    * @return NaN if either nodeCount < 1 otherwise a new random number is returned.
    *
    * @see MutableInfiniteInteger#random(MutableInfiniteInteger, Random)
    */
   public static MutableInfiniteRational random(final MutableInfiniteInteger numeratorNodeCount,
                                                final MutableInfiniteInteger denominatorNodeCount, final Random random)
   {
      final MutableInfiniteInteger numerator = MutableInfiniteInteger.random(numeratorNodeCount, random);
      final MutableInfiniteInteger denominator = MutableInfiniteInteger.random(denominatorNodeCount, random);
      return MutableInfiniteRational.valueOf(numerator, denominator);
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
      //remainder is always 0 for these
      numerator.divideDropRemainder(divisor);
      denominator.divideDropRemainder(divisor);
      return this;
   }

   /**
    * denominator becomes positive, the numerator will hold the correct sign.
    */
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
      if (this.isNaN()) return Double.NaN;
      if (this.equals(MutableInfiniteRational.POSITIVE_INFINITY)) return Double.POSITIVE_INFINITY;
      if (this.equals(MutableInfiniteRational.NEGATIVE_INFINITY)) return Double.NEGATIVE_INFINITY;

      // TODO: doubleValue
      throw new UnsupportedOperationException("Not yet implemented");
   }

   //TODO: bigDecimalValue, bigDecimalValueExact

   /**
    * @see #add(MutableInfiniteRational)
    */
   public MutableInfiniteRational add(final long value){return this.add(MutableInfiniteRational.valueOf(value));}

   /**
    * @see #add(MutableInfiniteRational)
    */
   public MutableInfiniteRational add(final BigInteger value){return this.add(MutableInfiniteRational.valueOf(value));}

   /**
    * @see #add(MutableInfiniteRational)
    */
   public MutableInfiniteRational add(final double value){return this.add(MutableInfiniteRational.valueOf(value));}

   /**
    * @see #add(MutableInfiniteRational)
    */
   public MutableInfiniteRational add(final BigDecimal value){return this.add(MutableInfiniteRational.valueOf(value));}

   /**
    * @see #add(MutableInfiniteRational)
    */
   public MutableInfiniteRational add(final InfiniteInteger value){return this.add(MutableInfiniteRational.valueOf(value));}

   /**
    * @see #add(MutableInfiniteRational)
    */
   public MutableInfiniteRational add(final MutableInfiniteInteger value)
   {
      return this.add(MutableInfiniteRational.valueOf(value));
   }

   /**
    * @see #add(MutableInfiniteRational)
    */
   public MutableInfiniteRational add(final InfiniteRational value)
   {
      return this.add(MutableInfiniteRational.valueOf(value));
   }

   /**
    * Returns a MutableInfiniteRational whose value is {@code (this + value)}.
    * Note that ∞ - ∞ is NaN.
    *
    * @param value the operand to be added to this MutableInfiniteRational.
    *
    * @return the result including ±∞ and NaN
    */
   public MutableInfiniteRational add(final MutableInfiniteRational value)
   {
      if (this.isNaN() || value.isNaN()) return MutableInfiniteRational.NaN;
      if (this.isInfinite() && value.isInfinite() && this.signum() != value.signum()) return MutableInfiniteRational.NaN;
      if (!this.isFinite() || value.equalValue(0)) return this;
      if (!value.isFinite() || this.equalValue(0)) return set(value.copy());  //must copy value if it is finite

      final MutableInfiniteInteger leastCommonMultiple = this.denominator.leastCommonMultiple(value.denominator);

      //There is no remainder but don't call divideDropRemainder because that mutates and this way avoids a pointless copy
      //multiplier will always be positive because denominator is positive.
      final MutableInfiniteInteger thisMultiplier = leastCommonMultiple.divide(this.denominator).getWholeResult();
      numerator = this.numerator.multiply(thisMultiplier);
      denominator = this.denominator.multiply(thisMultiplier);

      final MutableInfiniteInteger valueMultiplier = leastCommonMultiple.divide(value.denominator).getWholeResult();
      final MutableInfiniteInteger valueNewNumerator = value.numerator.copy().multiply(valueMultiplier);

      numerator = this.numerator.add(valueNewNumerator);

      return this;
   }

   /**
    * @see #subtract(MutableInfiniteRational)
    */
   public MutableInfiniteRational subtract(final long value){return this.subtract(MutableInfiniteRational.valueOf(value));}

   /**
    * @see #subtract(MutableInfiniteRational)
    */
   public MutableInfiniteRational subtract(final BigInteger value){return this.subtract(MutableInfiniteRational.valueOf(value));}

   /**
    * @see #subtract(MutableInfiniteRational)
    */
   public MutableInfiniteRational subtract(final double value){return this.subtract(MutableInfiniteRational.valueOf(value));}

   /**
    * @see #subtract(MutableInfiniteRational)
    */
   public MutableInfiniteRational subtract(final BigDecimal value){return this.subtract(MutableInfiniteRational.valueOf(value));}

   /**
    * @see #subtract(MutableInfiniteRational)
    */
   public MutableInfiniteRational subtract(final InfiniteInteger value){return this.subtract(MutableInfiniteRational.valueOf(value));}

   /**
    * @see #subtract(MutableInfiniteRational)
    */
   public MutableInfiniteRational subtract(final MutableInfiniteInteger value)
   {
      return this.subtract(MutableInfiniteRational.valueOf(value));
   }

   /**
    * @see #subtract(MutableInfiniteRational)
    */
   public MutableInfiniteRational subtract(final InfiniteRational value)
   {
      return this.subtract(MutableInfiniteRational.valueOf(value));
   }

   /**
    * Returns a MutableInfiniteRational whose value is {@code (this - value)}.
    *
    * @param value the operand to be subtracted from this MutableInfiniteRational.
    *
    * @return the result including ±∞ and NaN
    */
   public MutableInfiniteRational subtract(final MutableInfiniteRational value)
   {
      //negate and add both handle constants
      return this.add(value.copy().negate());
   }

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
    * Note ±∞ * 0 results in NaN.
    *
    * @param value the operand to be multiplied to this InfiniteInteger.
    *
    * @return the result including ±∞ and NaN
    */
   public MutableInfiniteRational multiply(final MutableInfiniteRational value)
   {
      if (this.isNaN() || value.isNaN()) return MutableInfiniteRational.NaN;
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
    * Note that 0 becomes NaN and ±∞ becomes 0.
    */
   public MutableInfiniteRational invert()
   {
      if (this.isNaN() || this.equalValue(0)) return MutableInfiniteRational.NaN;
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
    * @return the result including ±∞ and NaN
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
    * @return true if this MutableInfiniteInteger is a whole number. false for ±∞ and NaN.
    */
   public boolean isWhole()
   {
      if (!this.isFinite()) return false;
      final IntegerQuotient<MutableInfiniteInteger> quotient = numerator.divide(denominator);
      return quotient.getRemainder().equalValue(0);
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

      if (quotient.getRemainder().equalValue(0)) return set(MutableInfiniteRational.valueOf(quotient.getWholeResult()));

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
      numerator = numerator.abs();
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
      numerator = numerator.negate();  //also works for 0
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
   public boolean isNaN(){return this == MutableInfiniteRational.NaN;}

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
      return (this == MutableInfiniteRational.POSITIVE_INFINITY || this == MutableInfiniteRational.NEGATIVE_INFINITY);
   }

   /**
    * Compares this MutableInfiniteRational to ±∞ and NaN (returns false if this is any of them).
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
    * The natural order is as expected with ±∞ being at either end.
    * With the exception that ∞ &lt; NaN (this is consistent with Float/Double.compareTo).
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
    * equal to 2/4. If this is not desired then either call use reduce on each, use compareTo, or use equalValue.
    * equals is for object equality, use equalValue for numeric equality.
    *
    * @see #reduce()
    * @see #compareTo(MutableInfiniteRational)
    * @see #equalValue(Object)
    */
   @Override
   public boolean equals(final Object other)
   {
      if (this == other) return true;
      if (other == null || getClass() != other.getClass()) return false;
      final MutableInfiniteRational that = (MutableInfiniteRational) other;
      if (!this.isFinite() || !that.isFinite()) return false;
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
      //null returns false
      //unknown class returns false
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
    * get cut off.
    *
    * @return String representation of this MutableInfiniteRational.
    * The format may change arbitrarily.
    *
    * @see #toImproperFractionalString()
    * @see #toMixedFractionalString(int)
    * @see #toDecimalString(int, int)
    */
   @Override
   public String toString()
   {
      //These values for the singletons are acceptable for debugging since the number is base 10.
      if (this.equals(MutableInfiniteRational.POSITIVE_INFINITY)) return "Infinity";
      if (this.equals(MutableInfiniteRational.NEGATIVE_INFINITY)) return "-Infinity";
      if (this.isNaN()) return "NaN";
      if (denominator.equalValue(1)) return numerator.toString();

      //won't overflow string since the max is '-'+'…'+20+'/'+'…'+20 = length 44
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
    * @param radix the number base to be used. {@link RadixUtil#toString(long, int)} currently only supports a range of 1 .. 62 (1 and 62
    *              are both inclusive)
    *
    * @return String representation of this MutableInfiniteRational in the given radix.
    *
    * @throws IllegalArgumentException if radix is illegal
    * @throws WillNotFitException      if this MutableInfiniteRational can't fit into a string of the given radix
    * @see MutableInfiniteInteger#toString(int)
    * @see #reduce()
    * @see #toMixedFractionalString(int)
    * @see #toDecimalString(int, int)
    * @see RadixUtil#toString(long, int)
    */
   public String toImproperFractionalString(final int radix)
   {
      if (this.equals(MutableInfiniteRational.POSITIVE_INFINITY)) return "∞";
      if (this.equals(MutableInfiniteRational.NEGATIVE_INFINITY)) return "-∞";
      if (this.isNaN()) return "∉ℚ";
      if (denominator.equalValue(1)) return numerator.toString(radix);

      final FriendlyOverflowStringBuilder stringBuilder = new FriendlyOverflowStringBuilder(this + " in base " + radix);
      stringBuilder.append(numerator.toString(radix));
      stringBuilder.append('/');
      stringBuilder.append(denominator.toString(radix));
      return stringBuilder.toString();
   }

   /**
    * Uses a radix of 10.
    *
    * @see #toMixedFractionalString(int)
    */
   public String toMixedFractionalString(){return toMixedFractionalString(10);}

   /**
    * <p>The format is {@code "whole remainingNumerator/denominator"}. It calls {@link MutableInfiniteInteger#toString(int)} for each
    * number. whole is omitted if 0. remainingNumerator is the numerator after removing the whole. If this is a whole number then
    * only whole will be present. This method won't reduce. Examples: {@code "1/2", "5", "2 2/4"}.</p>
    *
    * <p>Note the special values of ∞, -∞, and ∉ℚ (for NaN) which were chosen to avoid collision
    * with any radix. These values are returned for all radix values.</p>
    *
    * @param radix the number base to be used. {@link RadixUtil#toString(long, int)} currently only supports a range of 1 .. 62 (1 and 62
    *              are both inclusive)
    *
    * @return String representation of this MutableInfiniteRational in the given radix.
    *
    * @throws IllegalArgumentException if radix is illegal
    * @throws WillNotFitException      if this MutableInfiniteRational can't fit into a string of the given radix
    * @see MutableInfiniteInteger#toString(int)
    * @see #reduce()
    * @see #toImproperFractionalString(int)
    * @see #toDecimalString(int, int)
    * @see RadixUtil#toString(long, int)
    */
   public String toMixedFractionalString(final int radix)
   {
      if (this.equals(MutableInfiniteRational.POSITIVE_INFINITY)) return "∞";
      if (this.equals(MutableInfiniteRational.NEGATIVE_INFINITY)) return "-∞";
      if (this.isNaN()) return "∉ℚ";
      if (denominator.equalValue(1)) return numerator.toString(radix);

      final FriendlyOverflowStringBuilder stringBuilder = new FriendlyOverflowStringBuilder(this + " in base " + radix);
      //Don't need to copy numerator because divide doesn't mutate.
      final IntegerQuotient<MutableInfiniteInteger> quotient = numerator.divide(denominator);
      if (this.signum() == -1) stringBuilder.append('-');
      //abs the whole so that above can cover cases with and without whole.
      if (!quotient.getWholeResult().equalValue(0)) stringBuilder.append(quotient.getWholeResult().abs().toString(radix));
      if (!quotient.getWholeResult().equalValue(0) && !quotient.getRemainder().equalValue(0)) stringBuilder.append(" ");
      if (!quotient.getRemainder().equalValue(0))
      {
         stringBuilder.append(quotient.getRemainder().toString(radix));  //Remainder is never negative. sign already added to string.
         stringBuilder.append('/');
         stringBuilder.append(denominator.toString(radix));
      }
      return stringBuilder.toString();
   }

   /**
    * Uses a radix of 10.
    *
    * @see #toDecimalString(int, int)
    */
   public String toDecimalString(final int decimalPlaces)
   {
      return toDecimalString(decimalPlaces, 10);
   }

   /**
    * <p>The format is decimal ({@code "whole.decimalDigits"}).
    * The whole number is always included (may be 0). If decimalPlaces is not 0 a "." is included
    * followed by a number of digits equal to decimalPlaces. This method doesn't mutate and returns the same value
    * whether it is reduced or not. Examples: {@code "0.5", "5", "25.000"}.</p>
    *
    * <p>Note the special values of ∞, -∞, and ∉ℚ (for NaN) which were chosen to avoid collision
    * with any radix. These values are returned for all decimalPlaces and radix values.</p>
    *
    * @param decimalPlaces the number of digits to include after the whole amount.
    * @param radix         the number base to be used. {@link RadixUtil#toString(long, int)} currently only supports a range of 1 .. 62 (1
    *                      and 62 are both inclusive)
    *
    * @return String representation of this MutableInfiniteRational in the given radix with the given number of decimal digits.
    *
    * @throws IllegalArgumentException if radix is 1 and decimalPlaces isn't 0.
    * @throws IllegalArgumentException if decimalPlaces < 0.
    * @throws IllegalArgumentException if radix is illegal
    * @throws WillNotFitException      if this MutableInfiniteRational can't fit into a string of the given radix.
    *                                  This is possible if decimalPlaces + length of whole is greater than max int.
    * @see MutableInfiniteInteger#toString(int)
    * @see #toImproperFractionalString(int)
    * @see #toMixedFractionalString(int)
    * @see #toDecimalStringExact(int)
    * @see RadixUtil#toString(long, int)
    */
   public String toDecimalString(final int decimalPlaces, final int radix)
   {
      if (this.equals(MutableInfiniteRational.POSITIVE_INFINITY)) return "∞";
      if (this.equals(MutableInfiniteRational.NEGATIVE_INFINITY)) return "-∞";
      if (this.isNaN()) return "∉ℚ";

      final FriendlyOverflowStringBuilder stringBuilder = new FriendlyOverflowStringBuilder(this + " in base " + radix);
      IntegerQuotient<MutableInfiniteInteger> workingQuotient = numerator.copy().abs().divide(denominator);
      if (this.signum() == -1) stringBuilder.append('-');

      stringBuilder.append(workingQuotient.getWholeResult().toString(radix));
      if (decimalPlaces == 0) return stringBuilder.toString();  //don't include a "."

      if (radix == 1) throw new IllegalArgumentException("Base 1 doesn't support decimal representations. This: " + this);
      if (decimalPlaces < 0)
         throw new IllegalArgumentException("decimalPlaces must be at least 0 but got " + decimalPlaces + ". This: " + this);
      stringBuilder.append(".");

      MutableInfiniteInteger workingRemainder = workingQuotient.getRemainder();
      int currentDecimalPlaces;
      for (currentDecimalPlaces = 0; currentDecimalPlaces < decimalPlaces && !workingRemainder.equalValue(0); ++currentDecimalPlaces)
      {
         workingRemainder.multiply(radix);
         workingQuotient = workingRemainder.divide(denominator);
         stringBuilder.append(workingQuotient.getWholeResult().toString(radix));
         workingRemainder = workingQuotient.getRemainder();
      }
      if (currentDecimalPlaces < decimalPlaces)
      {
         final char[] zeroes = new char[decimalPlaces - currentDecimalPlaces];
         Arrays.fill(zeroes, '0');
         stringBuilder.append(zeroes);
      }

      return stringBuilder.toString();
   }

   /**
    * Uses a radix of 10.
    *
    * @see #toDecimalStringExact(int)
    */
   public String toDecimalStringExact()
   {
      return toDecimalStringExact(10);
   }

   /**
    * <p>The format is {@code "whole.nonRepeatingDigits_repeatingDigits"}.
    * The whole number is always included (may be 0). If this number isn't whole then a "." is included
    * followed by the decimal digits.
    * Since it is impossible to put a line above the numbers an underscore is put before the repeating starts so that
    * you can tell how much of it repeats (the repeatingDigits will only be shown once).
    * Note that infinite repeating will occur if (after reducing) the denominator does not share all unique prime
    * factors with the radix.
    * This method doesn't mutate and returns the same value
    * whether it is reduced or not. Examples: {@code "0.25", "5", "33._3", "0.58_3", "5.8_144"}.</p>
    *
    * <p>Note the special values of ∞, -∞, and ∉ℚ (for NaN) which were chosen to avoid collision
    * with any radix. These values are returned for all decimalPlaces and radix values.</p>
    *
    * @param radix the number base to be used. {@link RadixUtil#toString(long, int)} currently only supports a range of 1 .. 62 (1 and 62
    *              are both inclusive)
    *
    * @return String representation of this MutableInfiniteRational in the given radix.
    *
    * @throws IllegalArgumentException if radix is 1 and this value isn't a whole number.
    * @throws IllegalArgumentException if radix is illegal
    * @throws WillNotFitException      if value doesn't fit in a String. This is possible if whole number is larger than max BigInteger.
    * @see MutableInfiniteInteger#toString(int)
    * @see #toImproperFractionalString(int)
    * @see #toMixedFractionalString(int)
    * @see #toDecimalString(int, int)
    * @see RadixUtil#toString(long, int)
    */
   public String toDecimalStringExact(final int radix)
   {
      if (this.equals(MutableInfiniteRational.POSITIVE_INFINITY)) return "∞";
      if (this.equals(MutableInfiniteRational.NEGATIVE_INFINITY)) return "-∞";
      if (this.isNaN()) return "∉ℚ";
      if (denominator.equalValue(1)) return numerator.toString();

      final FriendlyOverflowStringBuilder stringBuilder = new FriendlyOverflowStringBuilder(this + " in base " + radix);
      IntegerQuotient<MutableInfiniteInteger> workingQuotient = numerator.copy().abs().divide(denominator);
      if (this.signum() == -1) stringBuilder.append('-');

      stringBuilder.append(workingQuotient.getWholeResult().toString(radix));
      if (workingQuotient.getRemainder().equalValue(0)) return stringBuilder.toString();  //don't include a "."

      if (radix == 1) throw new IllegalArgumentException("Base 1 doesn't support decimal representations. This: " + this);
      stringBuilder.append(".");

      //Decimal will repeat infinitely if (after reducing) the denominator does not share all unique prime factors with the radix.
      //Decimal repeats whenever pulling another digit uses a number already used which will need to be detected anyway so don't factorize.
      final Map<MutableInfiniteInteger, Integer> repeatDetection = new HashMap<>();
      MutableInfiniteInteger workingRemainder = workingQuotient.getRemainder();
      while (!workingRemainder.equalValue(0))
      {
         workingRemainder.multiply(radix);
         if (repeatDetection.containsKey(workingRemainder))
         {
            //modify the current string by adding an underscore
            final String noUnderscore = stringBuilder.toString();
            //repeatIndex is always noUnderscore.length() - workingRemainder.toString().length()
            //but there's no need to convert workingRemainder toString again or do any calculation
            final Integer repeatIndex = repeatDetection.get(workingRemainder);
            return noUnderscore.substring(0, repeatIndex) + "_" + noUnderscore.substring(repeatIndex);
         }
         //Don't need to copy because divide doesn't mutate and workingRemainder will be assigned.
         repeatDetection.put(workingRemainder, stringBuilder.length());
         workingQuotient = workingRemainder.divide(denominator);
         stringBuilder.append(workingQuotient.getWholeResult().toString(radix));
         workingRemainder = workingQuotient.getRemainder();
      }

      return stringBuilder.toString();
   }

   String toDebuggingString()
   {
      if (this.equals(MutableInfiniteRational.POSITIVE_INFINITY)) return "+Infinity";
      if (this.equals(MutableInfiniteRational.NEGATIVE_INFINITY)) return "-Infinity";
      if (this.isNaN()) return "NaN";
      if (denominator.equalValue(1)) return numerator.toDebuggingString();
      return numerator.toDebuggingString() + "\n/\n" + denominator.toDebuggingString();
   }

   /**
    * In order to maintain the singleton constants they will not be copied.
    * So ±∞ and NaN will return themselves but all others will be copied as expected.
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
      if (newDenominator.equalValue(0)) return MutableInfiniteRational.NaN;
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

   public static MutableInfiniteRational readFromStream(final ObjectStreamReader reader)
   {
      //valueOf will handle constant conversions
      final MutableInfiniteInteger numerator = reader.readObject(MutableInfiniteInteger.class);
      if (!numerator.isFinite() || numerator.equalValue(0)) return MutableInfiniteRational.valueOf(numerator);

      final MutableInfiniteInteger denominator = reader.readObject(MutableInfiniteInteger.class);
      return MutableInfiniteRational.valueOf(numerator, denominator);
   }

   @Override
   public void writeToStream(final ObjectStreamWriter writer)
   {
      writer.writeObject(numerator);
      //if check is for the only cases where denominator is already known (see readFromStream)
      if (this.isFinite() && !numerator.equalValue(0)) writer.writeObject(denominator);
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
