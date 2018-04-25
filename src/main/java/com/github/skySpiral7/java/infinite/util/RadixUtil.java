package com.github.skySpiral7.java.infinite.util;

import java.util.Arrays;
import java.util.Objects;

import com.github.skySpiral7.java.infinite.exeptions.WillNotFitException;
import com.github.skySpiral7.java.numbers.NumberFormatException;
import com.github.skySpiral7.java.numbers.NumericOverflowException;

public enum RadixUtil
{
   ;  //no instances

   /**
    * This is the smallest possible radix. This radix is supported by this class.
    */
   public static final int MIN_RADIX = 1;

   /**
    * While there is no maximum possible radix this is the largest value supported by this class.
    */
   public static final int MAX_SUPPORTED_RADIX = 62;
   /*base64 notes
   Order is A-Za-z0-9 then 2 more
   original: +/, required =, no end lines
   URL safe: -_, optional =, no end lines
   base62 below order is 0-9a-zA-Z
   just use java.util.Base64
   */

   /**
    * All possible chars for representing a number as a String up to base 62.
    * private because arrays are mutable.
    */
   private final static char[] base62Digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
         'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
         'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

   /**
    * <p>Returns a String that represents value in the given number base (radix).
    * The minimum radix is 1 with a maximum of 62. For radix &gt; 36 upper case letters are used.
    * In such cases the String returned is case sensitive. The digits used are in this order:</p>
    *
    * <blockquote>
    * {@code 0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ}
    * </blockquote>
    *
    * <p>This order is used to be compatible with Long.toString.</p>
    * <p><b>Special case</b>: Base 1 uses the character '1' instead of '0'.</p>
    *
    * @param value the number that is to be represented
    * @param radix the number base
    *
    * @return a String that represents value in the given number base (radix).
    *
    * @throws IllegalArgumentException for unsupported radix
    * @throws WillNotFitException      if the base 1 number will not fit into a String
    * @see Long#toString(long, int)
    */
   public static String toString(final long value, final int radix)
   {
      RadixUtil.enforceStandardRadix(radix);
      if (1 == value) return "1";  //true for every radix

      final boolean isNegative = (value < 0);
      if (1 == radix) return RadixUtil.toStringBase1(value, isNegative);
      if (0 == value) return "0";  //true for every radix except 1

      //delegate for powers of 2 because Long uses a different (ie optimized) formula
      //however it doesn't handle negative so I manually negate if possible (not possible for Min due to 2s compliment)
      if (BitWiseUtil.isPowerOf2(radix) && Long.MIN_VALUE != value)
      {
         if (isNegative) return "-" + Long.toUnsignedString(-value, radix);
         return Long.toUnsignedString(value, radix);
      }

      //delegate to Long.toString(long, int) because it could be better optimized
      if (radix <= Character.MAX_RADIX) return Long.toString(value, radix);
      //Character.MAX_RADIX is max JRE supported radix not max possible

      return RadixUtil.toStringStandardBase(value, radix, isNegative);
   }

   /**
    * Mostly copied from JRE Long.toString(long, int).
    *
    * @see Long#toString(long, int)
    */
   private static String toStringStandardBase(long value, final int radix, final boolean isNegative)
   {
      final char[] buf = new char[65];
      int charPos = 64;

      if (!isNegative)
      {
         value = -value;
      }

      //value is negative in order to handle max and min values alike (since negative can hold 1 more than positive)
      while (value <= -radix)
      {
         buf[charPos] = RadixUtil.base62Digits[(int) (-(value % radix))];
         charPos--;
         value /= radix;
      }
      buf[charPos] = RadixUtil.base62Digits[(int) (-value)];

      if (isNegative)
      {
         --charPos;
         buf[charPos] = '-';
      }

      return new String(buf, charPos, (65 - charPos));
   }

   private static String toStringBase1(long value, final boolean isNegative)
   {
      if (0 == value) return "";
      //must check for Long.Min here to avoid 2s complement issue
      if (Long.MIN_VALUE == value) throw new WillNotFitException(value + " in base 1 would exceed max string length");
      String sign = "";
      if (isNegative)
      {
         if (-Integer.MAX_VALUE == value) throw new WillNotFitException(value + " in base 1 would exceed max string length");
         sign = "-";
         value = Math.abs(value);
      }
      if (value > Integer.MAX_VALUE) throw new WillNotFitException(value + " in base 1 would exceed max string length");

      //I'm not checking for array overhead such as -8 since it is implementation dependent
      final char[] ones = new char[(int) value];
      Arrays.fill(ones, '1');
      return (sign + String.valueOf(ones));
   }

   /**
    * Parses the inputString as a signed long in the radix specified.
    * See {@link RadixUtil#toString(long, int)} for a description of legal characters per radix.
    * Note some differences from Long.parseLong:
    * <ul>
    * <li>base 1 is allowed to have inputString be the empty string (with an optional leading - or +).
    * For any other radix NumberFormatException is thrown (same behavior).</li>
    * <li>this method is case sensitive (in order to support radix &gt; 36).</li>
    * <li>the fullwidth Latin letters are not supported (because they are different characters).</li>
    * </ul>
    *
    * @param inputString the String to be parsed
    * @param radix       the number base
    *
    * @return the long that inputString represents
    *
    * @throws NullPointerException     if inputString is null
    * @throws NumericOverflowException if inputString represents a number greater than a Long can represent
    * @throws NumberFormatException    excluding a leading + or - if inputString is empty (and not base 1)
    *                                  or contains illegal characters for that radix
    * @throws IllegalArgumentException {@code if(radix > 62 || radix < 1)}
    * @see Long#parseLong(String, int)
    * @see #toString(long, int)
    */
   public static long parseLong(final String inputString, final int radix)
   {
      Objects.requireNonNull(inputString, "inputString");
      RadixUtil.enforceStandardRadix(radix);

      if (1 == radix)
      {
         //should be faster than parseLongStandardBase. Also this handles the special case that "" is 0 in base 1
         if (inputString.isEmpty()) return 0;
         if (!inputString.matches("^[+-]?1*$")) throw NumberFormatException.forInputRadix(inputString, radix);
         if (inputString.charAt(0) == '-') return -(inputString.length() - 1);  //these 2 might return 0
         if (inputString.charAt(0) == '+') return (inputString.length() - 1);
         return inputString.length();
      }

      return RadixUtil.parseLongStandardBase(inputString, radix);
   }

   /**
    * Mostly copied from JRE Long.parseLong(String, int).
    *
    * @see Long#parseLong(String, int)
    */
   private static long parseLongStandardBase(final String inputString, final int radix)
   {
      long result = 0;
      final boolean isNegative = inputString.charAt(0) == '-';
      int i = 0;
      final int inputLength = inputString.length();
      long limit = -Long.MAX_VALUE;
      final long multmin;
      int digit;

      if (inputLength == 0) throw NumberFormatException.forInputRadix(inputString, radix);

      final char firstChar = inputString.charAt(0);
      if (firstChar < '0')
      { // Possible leading "+" or "-"
         if (firstChar == '-')
         {
            limit = Long.MIN_VALUE;
         }
         else if (firstChar != '+') throw NumberFormatException.forInputRadix(inputString, radix);

         if (inputLength == 1) // Cannot have lone "+" or "-"
            throw NumberFormatException.forInputRadix(inputString, radix);
         i++;
      }
      multmin = limit / radix;
      while (i < inputLength)
      {
         // Accumulating negatively avoids surprises near MAX_VALUE
         digit = RadixUtil.getDigitValue(inputString.charAt(i), radix);
         i++;
         if (digit < 0)
         {
            throw NumberFormatException.forInputRadix(inputString, radix);
         }
         if (result < multmin)
         {
            throw new NumericOverflowException("radix: " + radix + " input string: \"" + inputString + "\"");
         }
         result *= radix;
         if (result < limit + digit)
         {
            throw new NumericOverflowException("radix: " + radix + " input string: \"" + inputString + "\"");
         }
         result -= digit;
      }
      return isNegative ? result : -result;
   }

   /**
    * <p>Returns the numeric value of the character {@code digit} in the
    * specified radix. If the character is not a valid digit in the specified radix, -1 is returned.
    * Note that unlike Character.digit the fullwidth Latin letters are not supported (because they are different characters).</p>
    *
    * <p><b>Special case</b>: Base 1 uses the character '1' instead of '0' and returns 1 or -1 accordingly.</p>
    *
    * @param digit the character to be converted
    * @param radix the number base
    *
    * @return the numeric value of digit or -1
    *
    * @throws IllegalArgumentException {@code if(radix > 62 || radix < 1)}
    * @see Character#digit(char, int)
    */
   public static int getDigitValue(final char digit, final int radix)
   {
      RadixUtil.enforceStandardRadix(radix);

      if (1 == radix)
      {
         //special case: base 1 uses '1' instead of '0' and never returns 0
         if ('1' == digit) return 1;
         return -1;
      }

      //why is there no Arrays.indexOf?
      for (int i = 0; i < RadixUtil.base62Digits.length && i < radix; i++)
      {
         if (digit == RadixUtil.base62Digits[i]) return i;
      }

      return -1;
   }

   /**
    * @throws IllegalArgumentException if radix is outside the supported range
    */
   public static void enforceStandardRadix(final int radix)
   {
      if (radix < RadixUtil.MIN_RADIX) throw new IllegalArgumentException("expected: radix <= 1 got: " + radix);
      if (radix > RadixUtil.MAX_SUPPORTED_RADIX) throw new IllegalArgumentException("expected: radix >= 62 got: " + radix);
   }

}
