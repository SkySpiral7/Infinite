package com.github.skySpiral7.java.infinite.numbers;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MutableInfiniteRational_UT
{
   private MutableInfiniteRational testObject;

   /**
    * Happy path for {@link MutableInfiniteRational#valueOf(double)}
    */
   @Test
   public void valueOf_returnsValue_givenDouble() throws Exception
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(1.5d);
      assertThat(actual.toImproperFractionalString(), is("15/10"));
   }

   /**
    * Test for {@link MutableInfiniteRational#valueOf(double)}
    */
   @Test
   public void valueOf_returnsNan_givenDoubleNan() throws Exception
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(Double.NaN);
      assertThat(actual, is(sameInstance(MutableInfiniteRational.NaN)));
   }

   /**
    * Test for {@link MutableInfiniteRational#valueOf(double)}
    */
   @Test
   public void valueOf_returnsPositiveInfinity_givenDoublePositiveInfinity() throws Exception
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(Double.POSITIVE_INFINITY);
      assertThat(actual, is(sameInstance(MutableInfiniteRational.POSITIVE_INFINITY)));
   }

   /**
    * Test for {@link MutableInfiniteRational#valueOf(double)}
    */
   @Test
   public void valueOf_returnsNegativeInfinity_givenDoubleNegativeInfinity() throws Exception
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(Double.NEGATIVE_INFINITY);
      assertThat(actual, is(sameInstance(MutableInfiniteRational.NEGATIVE_INFINITY)));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#valueOf(BigDecimal)}
    */
   @Test
   public void valueOf_returnsValue_givenWholeBigDecimal() throws Exception
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(BigDecimal.ONE);
      assertThat(actual.toImproperFractionalString(), is("1"));
   }

   /**
    * Test for {@link MutableInfiniteRational#valueOf(BigDecimal)}
    */
   @Test
   public void valueOf_usesCorrectDenominator_givenNonWholeBigDecimal() throws Exception
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(BigDecimal.valueOf(10.25));
      //10 25/100 => 1025/100. Does not reduce.
      assertThat(actual.toImproperFractionalString(), is("1025/100"));
   }

   /**
    * Test for {@link MutableInfiniteRational#valueOf(BigDecimal)}
    */
   @Test
   public void valueOf_retainsPrecision_givenBigDecimalWithTrailingZeros() throws Exception
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(new BigDecimal("1.200"));
      assertThat(actual.toImproperFractionalString(), is("1200/1000"));
   }

   /**
    * Test for {@link MutableInfiniteRational#valueOf(BigDecimal)}
    */
   @Test
   public void valueOf_retainsPrecision_givenWholeBigDecimalWithTrailingZeros() throws Exception
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(new BigDecimal("1.0"));
      assertThat(actual.toImproperFractionalString(), is("10/10"));
   }

   /**
    * Test for {@link MutableInfiniteRational#valueOf(BigDecimal)}
    */
   @Test
   public void valueOf_retainsPrecision_givenBigDecimalWithNegativeScale() throws Exception
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(new BigDecimal("6e2"));
      assertThat(actual.toImproperFractionalString(), is("600"));
   }

   /**
    * Test for {@link MutableInfiniteRational#valueOf(BigDecimal)}
    */
   @Test
   public void valueOf_retainsZero_givenBigDecimalZero() throws Exception
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(BigDecimal.ZERO);
      assertThat(actual.toImproperFractionalString(), is("0"));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#valueOf(long)}
    */
   @Test
   public void valueOf_returnsValue_givenLong() throws Exception
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(2);
      assertThat(actual.toImproperFractionalString(), is("2"));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#valueOf(long, long)}
    */
   @Test
   public void valueOf_returnsValue_givenLongLong() throws Exception
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(1, 2);
      assertThat(actual.toImproperFractionalString(), is("1/2"));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#valueOf(BigInteger)}
    */
   @Test
   public void valueOf_returnsValue_givenBigInteger() throws Exception
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(BigInteger.TEN);
      assertThat(actual.toImproperFractionalString(), is("10"));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#valueOf(BigInteger, BigInteger)}
    */
   @Test
   public void valueOf_returnsValue_givenBigIntegerBigInteger() throws Exception
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(BigInteger.ONE, BigInteger.TEN);
      assertThat(actual.toImproperFractionalString(), is("1/10"));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#valueOf(InfiniteInteger)}
    */
   @Test
   public void valueOf_returnsValue_givenInfiniteInteger() throws Exception
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(InfiniteInteger.valueOf(5));
      assertThat(actual.toImproperFractionalString(), is("5"));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#valueOf(InfiniteInteger, InfiniteInteger)}
    */
   @Test
   public void valueOf_returnsValue_givenInfiniteIntegerInfiniteInteger() throws Exception
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(InfiniteInteger.valueOf(1), InfiniteInteger.valueOf(5));
      assertThat(actual.toImproperFractionalString(), is("1/5"));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#valueOf(MutableInfiniteInteger)}
    */
   @Test
   public void valueOf_returnsValue_givenMutableInfiniteInteger() throws Exception
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(MutableInfiniteInteger.valueOf(5));
      assertThat(actual.toImproperFractionalString(), is("5"));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#valueOf(MutableInfiniteInteger, MutableInfiniteInteger)}
    */
   @Test
   public void valueOf_returnsValue_givenMutableInfiniteIntegerMutableInfiniteInteger() throws Exception
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(MutableInfiniteInteger.valueOf(1),
            MutableInfiniteInteger.valueOf(5));
      assertThat(actual.toImproperFractionalString(), is("1/5"));
   }

   /**
    * Test for {@link MutableInfiniteRational#valueOf(MutableInfiniteInteger, MutableInfiniteInteger)}
    * NaN / X == NaN
    */
   @Test
   public void valueOf_returnsNan_givenMutableInfiniteIntegerNanNumerator() throws Exception
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(MutableInfiniteInteger.NaN,
            MutableInfiniteInteger.valueOf(10));
      assertThat(actual, is(sameInstance(MutableInfiniteRational.NaN)));
   }

   /**
    * Test for {@link MutableInfiniteRational#valueOf(MutableInfiniteInteger, MutableInfiniteInteger)}
    * X / NaN == NaN
    */
   @Test
   public void valueOf_returnsNan_givenMutableInfiniteIntegerNanDenominator() throws Exception
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(MutableInfiniteInteger.valueOf(1), MutableInfiniteInteger.NaN);
      assertThat(actual, is(sameInstance(MutableInfiniteRational.NaN)));
   }

   /**
    * Test for {@link MutableInfiniteRational#valueOf(MutableInfiniteInteger, MutableInfiniteInteger)}
    * Infinity / Infinity == NaN
    */
   @Test
   public void valueOf_returnsNan_givenMutableInfiniteIntegerInfinityDividedByInfinity() throws Exception
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(MutableInfiniteInteger.POSITIVE_INFINITY,
            MutableInfiniteInteger.NEGATIVE_INFINITY);
      assertThat(actual, is(sameInstance(MutableInfiniteRational.NaN)));
   }

   /**
    * Test for {@link MutableInfiniteRational#valueOf(MutableInfiniteInteger, MutableInfiniteInteger)}
    * X / 0 == NaN
    */
   @Test
   public void valueOf_returnsNan_givenMutableInfiniteIntegerFiniteDividedByZero() throws Exception
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(MutableInfiniteInteger.valueOf(2),
            MutableInfiniteInteger.valueOf(0));
      assertThat(actual, is(sameInstance(MutableInfiniteRational.NaN)));
   }

   /**
    * Test for {@link MutableInfiniteRational#valueOf(MutableInfiniteInteger, MutableInfiniteInteger)}
    * Infinity / 0 == NaN
    */
   @Test
   public void valueOf_returnsNan_givenMutableInfiniteIntegerInfiniteDividedByZero() throws Exception
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(MutableInfiniteInteger.POSITIVE_INFINITY,
            MutableInfiniteInteger.valueOf(0));
      assertThat(actual, is(sameInstance(MutableInfiniteRational.NaN)));
   }

   /**
    * Test for {@link MutableInfiniteRational#valueOf(MutableInfiniteInteger, MutableInfiniteInteger)}
    * Infinity / X == Infinity
    */
   @Test
   public void valueOf_returnsPositiveInfinity_givenMutableInfiniteIntegerPositiveInfinityDividedByFinite() throws Exception
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(MutableInfiniteInteger.POSITIVE_INFINITY,
            MutableInfiniteInteger.valueOf(2));
      assertThat(actual, is(sameInstance(MutableInfiniteRational.POSITIVE_INFINITY)));
   }

   /**
    * Test for {@link MutableInfiniteRational#valueOf(MutableInfiniteInteger, MutableInfiniteInteger)}
    * -Infinity / X == -Infinity
    */
   @Test
   public void valueOf_returnsNegativeInfinity_givenMutableInfiniteIntegerNegativeInfinityDividedByFinite() throws Exception
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(MutableInfiniteInteger.NEGATIVE_INFINITY,
            MutableInfiniteInteger.valueOf(2));
      assertThat(actual, is(sameInstance(MutableInfiniteRational.NEGATIVE_INFINITY)));
   }

   /**
    * Test for {@link MutableInfiniteRational#valueOf(MutableInfiniteInteger, MutableInfiniteInteger)}
    * X / Infinity == 0
    */
   @Test
   public void valueOf_returnsZero_givenMutableInfiniteIntegerFiniteDividedByInfinity() throws Exception
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(MutableInfiniteInteger.valueOf(2),
            MutableInfiniteInteger.NEGATIVE_INFINITY);
      assertThat(actual.toDebuggingString(), is("0"));
   }

   /**
    * Test for {@link MutableInfiniteRational#valueOf(MutableInfiniteInteger, MutableInfiniteInteger)}
    */
   @Test
   public void valueOf_normalizedSign_givenPositiveNegative() throws Exception
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(MutableInfiniteInteger.valueOf(1),
            MutableInfiniteInteger.valueOf(-2));
      assertThat(actual.toDebuggingString(), is("- 1, \n/\n+ 2, "));
   }

   /**
    * Test for {@link MutableInfiniteRational#valueOf(MutableInfiniteInteger, MutableInfiniteInteger)}
    */
   @Test
   public void valueOf_normalizedSign_givenNegativeNegative() throws Exception
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(MutableInfiniteInteger.valueOf(-1),
            MutableInfiniteInteger.valueOf(-2));
      assertThat(actual.toDebuggingString(), is("+ 1, \n/\n+ 2, "));
   }

   /**
    * Test for {@link MutableInfiniteRational#valueOf(MutableInfiniteInteger, MutableInfiniteInteger)}
    */
   @Test
   public void valueOf_normalizedSign_givenZeroNegative() throws Exception
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(MutableInfiniteInteger.valueOf(0),
            MutableInfiniteInteger.valueOf(-2));
      assertThat(actual.toDebuggingString(), is("0\n/\n+ 2, "));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#valueOf(MutableInfiniteRational)}
    */
   @Test
   public void valueOf_returnsCopy_givenMutableInfiniteRational() throws Exception
   {
      testObject = MutableInfiniteRational.valueOf(2);
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(testObject);
      assertThat(actual, is(equalTo(testObject)));
      assertThat(actual, is(not(sameInstance(testObject))));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#valueOf(InfiniteRational)}
    */
   @Test
   public void valueOf_returnsValue_givenInfiniteRational() throws Exception
   {
      testObject = MutableInfiniteRational.valueOf(2);
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(InfiniteRational.valueOf(2));
      assertThat(actual, is(testObject));
   }

   @Test
   public void toInfiniteRational() throws Exception
   {
      testObject = MutableInfiniteRational.valueOf(2);
      final InfiniteRational actual = testObject.toInfiniteRational();
      assertThat(actual, is(InfiniteRational.valueOf(2)));
   }

   @Test
   public void reduce_returnsNan_givenNan() throws Exception
   {
      assertThat(MutableInfiniteRational.NaN.reduce(), is(MutableInfiniteRational.NaN));
   }

   @Test
   public void reduce_returnsPositiveInfinity_givenPositiveInfinity() throws Exception
   {
      assertThat(MutableInfiniteRational.POSITIVE_INFINITY.reduce(), is(MutableInfiniteRational.POSITIVE_INFINITY));
   }

   @Test
   public void reduce_returnsNegativeInfinity_givenNegativeInfinity() throws Exception
   {
      assertThat(MutableInfiniteRational.NEGATIVE_INFINITY.reduce(), is(MutableInfiniteRational.NEGATIVE_INFINITY));
   }

   @Test
   public void reduce_returnsWholeNumber() throws Exception
   {
      testObject = MutableInfiniteRational.valueOf(12, 12);
      assertThat(testObject.reduce(), is(MutableInfiniteRational.valueOf(1, 1)));

      testObject = MutableInfiniteRational.valueOf(12, 2);
      assertThat(testObject.reduce(), is(MutableInfiniteRational.valueOf(6, 1)));
   }

   @Test
   public void reduce_returnsZero() throws Exception
   {
      testObject = MutableInfiniteRational.valueOf(0, 12);
      assertThat(testObject.reduce(), is(MutableInfiniteRational.valueOf(0, 1)));
   }

   @Test
   public void reduce() throws Exception
   {
      testObject = MutableInfiniteRational.valueOf(6, 14);
      assertThat(testObject.reduce(), is(MutableInfiniteRational.valueOf(3, 7)));
   }

   @Test
   public void intValue() throws Exception
   {
      final int numerator = 10;
      testObject = MutableInfiniteRational.valueOf(numerator, 1);
      assertThat(testObject.intValue(), is(numerator));
   }

   /**
    * Happy path
    */
   @Test
   public void longValue() throws Exception
   {
      testObject = MutableInfiniteRational.valueOf(3, 2);
      assertThat(testObject.intValue(), is(1));
   }

   @Test
   public void longValue_throws_whenPositiveInfinity() throws Exception
   {
      try
      {
         MutableInfiniteRational.POSITIVE_INFINITY.longValue();
      }
      catch (final ArithmeticException actual)
      {
         assertEquals("Infinity can't be even partially represented as a long.", actual.getMessage());
      }
   }

   @Test
   public void longValue_throws_whenNegativeInfinity() throws Exception
   {
      try
      {
         MutableInfiniteRational.NEGATIVE_INFINITY.longValue();
      }
      catch (final ArithmeticException actual)
      {
         assertEquals("-Infinity can't be even partially represented as a long.", actual.getMessage());
      }
   }

   @Test
   public void longValue_throws_whenNan() throws Exception
   {
      try
      {
         MutableInfiniteRational.NaN.longValue();
      }
      catch (final ArithmeticException actual)
      {
         assertEquals("NaN can't be even partially represented as a long.", actual.getMessage());
      }
   }

   @Test
   public void floatValue() throws Exception
   {
   }

   @Test
   public void doubleValue() throws Exception
   {
   }

   /**
    * Happy path for {@link MutableInfiniteRational#multiply(long)}
    */
   @Test
   public void multiply_returns_givenLong()
   {
      testObject = MutableInfiniteRational.valueOf(5).multiply(5);
      assertThat(testObject, is(MutableInfiniteRational.valueOf(25)));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#multiply(BigInteger)}
    */
   @Test
   public void multiply_returns_givenBigInteger()
   {
      testObject = MutableInfiniteRational.valueOf(5).multiply(BigInteger.valueOf(5));
      assertThat(testObject, is(MutableInfiniteRational.valueOf(25)));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#multiply(double)}
    */
   @Test
   public void multiply_returns_givenDouble()
   {
      testObject = MutableInfiniteRational.valueOf(5).multiply((double) 5);
      assertThat(testObject, is(MutableInfiniteRational.valueOf(250, 10)));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#multiply(BigDecimal)}
    */
   @Test
   public void multiply_returns_givenBigDecimal()
   {
      testObject = MutableInfiniteRational.valueOf(5).multiply(BigDecimal.valueOf(5));
      assertThat(testObject, is(MutableInfiniteRational.valueOf(25)));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#multiply(InfiniteInteger)}
    */
   @Test
   public void multiply_returns_givenInfiniteInteger()
   {
      testObject = MutableInfiniteRational.valueOf(5).multiply(InfiniteInteger.valueOf(5));
      assertThat(testObject, is(MutableInfiniteRational.valueOf(25)));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#multiply(MutableInfiniteInteger)}
    */
   @Test
   public void multiply_returns_givenMutableInfiniteInteger()
   {
      testObject = MutableInfiniteRational.valueOf(5).multiply(MutableInfiniteInteger.valueOf(5));
      assertThat(testObject, is(MutableInfiniteRational.valueOf(25)));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#multiply(InfiniteRational)}
    */
   @Test
   public void multiply_returns_givenInfiniteRational()
   {
      testObject = MutableInfiniteRational.valueOf(5).multiply(InfiniteRational.valueOf(5));
      assertThat(testObject, is(MutableInfiniteRational.valueOf(25)));
   }

   /**
    * Test for {@link MutableInfiniteRational#multiply(MutableInfiniteRational)}
    */
   @Test
   public void multiply_returnsPositive_givenBothFinite()
   {
      testObject = MutableInfiniteRational.valueOf(1, 3).multiply(MutableInfiniteRational.valueOf(5, 10));
      assertThat(testObject, is(MutableInfiniteRational.valueOf(5, 30)));
   }

   /**
    * Test for {@link MutableInfiniteRational#multiply(MutableInfiniteRational)}
    */
   @Test
   public void multiply_returnsNan_givenNan()
   {
      assertThat(MutableInfiniteRational.NaN.multiply(MutableInfiniteRational.valueOf(2)), is(MutableInfiniteRational.NaN));
      assertThat(MutableInfiniteRational.valueOf(2).multiply(MutableInfiniteRational.NaN), is(MutableInfiniteRational.NaN));
   }

   /**
    * Test for {@link MutableInfiniteRational#multiply(MutableInfiniteRational)}
    */
   @Test
   public void multiply_returnsNan_givenInfinityAndZero()
   {
      assertThat(MutableInfiniteRational.POSITIVE_INFINITY.multiply(MutableInfiniteRational.valueOf(0)), is(MutableInfiniteRational.NaN));
      assertThat(MutableInfiniteRational.NEGATIVE_INFINITY.multiply(MutableInfiniteRational.valueOf(0)), is(MutableInfiniteRational.NaN));

      assertThat(MutableInfiniteRational.valueOf(0).multiply(MutableInfiniteRational.POSITIVE_INFINITY), is(MutableInfiniteRational.NaN));
      assertThat(MutableInfiniteRational.valueOf(0).multiply(MutableInfiniteRational.NEGATIVE_INFINITY), is(MutableInfiniteRational.NaN));
   }

   /**
    * Test for {@link MutableInfiniteRational#multiply(MutableInfiniteRational)}
    */
   @Test
   public void multiply_returnsPositiveInfinity_givenSameSignInfinity()
   {
      testObject = MutableInfiniteRational.POSITIVE_INFINITY.multiply(MutableInfiniteRational.POSITIVE_INFINITY);
      assertThat(testObject, is(MutableInfiniteRational.POSITIVE_INFINITY));
      testObject = MutableInfiniteRational.NEGATIVE_INFINITY.multiply(MutableInfiniteRational.NEGATIVE_INFINITY);
      assertThat(testObject, is(MutableInfiniteRational.POSITIVE_INFINITY));
   }

   /**
    * Test for {@link MutableInfiniteRational#multiply(MutableInfiniteRational)}
    */
   @Test
   public void multiply_returnsNegativeInfinity_givenDifferentSignInfinity()
   {
      testObject = MutableInfiniteRational.NEGATIVE_INFINITY.multiply(MutableInfiniteRational.POSITIVE_INFINITY);
      assertThat(testObject, is(MutableInfiniteRational.NEGATIVE_INFINITY));
      testObject = MutableInfiniteRational.POSITIVE_INFINITY.multiply(MutableInfiniteRational.NEGATIVE_INFINITY);
      assertThat(testObject, is(MutableInfiniteRational.NEGATIVE_INFINITY));
   }

   /**
    * Test for {@link MutableInfiniteRational#multiply(MutableInfiniteRational)}
    */
   @Test
   public void multiply_returnsInfinity_givenPositiveAndInfinity()
   {
      testObject = MutableInfiniteRational.POSITIVE_INFINITY.multiply(MutableInfiniteRational.valueOf(2));
      assertThat(testObject, is(MutableInfiniteRational.POSITIVE_INFINITY));
      testObject = MutableInfiniteRational.valueOf(2).multiply(MutableInfiniteRational.POSITIVE_INFINITY);
      assertThat(testObject, is(MutableInfiniteRational.POSITIVE_INFINITY));

      testObject = MutableInfiniteRational.NEGATIVE_INFINITY.multiply(MutableInfiniteRational.valueOf(2));
      assertThat(testObject, is(MutableInfiniteRational.NEGATIVE_INFINITY));
      testObject = MutableInfiniteRational.valueOf(2).multiply(MutableInfiniteRational.NEGATIVE_INFINITY);
      assertThat(testObject, is(MutableInfiniteRational.NEGATIVE_INFINITY));
   }

   /**
    * Test for {@link MutableInfiniteRational#multiply(MutableInfiniteRational)}
    */
   @Test
   public void multiply_returnsOppositeInfinity_givenNegativeAndInfinity()
   {
      testObject = MutableInfiniteRational.POSITIVE_INFINITY.multiply(MutableInfiniteRational.valueOf(-2));
      assertThat(testObject, is(MutableInfiniteRational.NEGATIVE_INFINITY));
      testObject = MutableInfiniteRational.valueOf(-2).multiply(MutableInfiniteRational.POSITIVE_INFINITY);
      assertThat(testObject, is(MutableInfiniteRational.NEGATIVE_INFINITY));

      testObject = MutableInfiniteRational.NEGATIVE_INFINITY.multiply(MutableInfiniteRational.valueOf(-2));
      assertThat(testObject, is(MutableInfiniteRational.POSITIVE_INFINITY));
      testObject = MutableInfiniteRational.valueOf(-2).multiply(MutableInfiniteRational.NEGATIVE_INFINITY);
      assertThat(testObject, is(MutableInfiniteRational.POSITIVE_INFINITY));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#divide(long)}
    */
   @Test
   public void divide_returns_givenLong()
   {
      testObject = MutableInfiniteRational.valueOf(6, 2).divide(3);
      assertThat(testObject, is(MutableInfiniteRational.valueOf(6, 6)));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#divide(BigInteger)}
    */
   @Test
   public void divide_returns_givenBigInteger()
   {
      testObject = MutableInfiniteRational.valueOf(6, 2).divide(BigInteger.TEN);
      assertThat(testObject, is(MutableInfiniteRational.valueOf(6, 20)));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#divide(double)}
    */
   @Test
   public void divide_returns_givenDouble()
   {
      testObject = MutableInfiniteRational.valueOf(6, 2).divide(1.5);  //1.5==15/10
      assertThat(testObject, is(MutableInfiniteRational.valueOf(60, 30)));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#divide(BigDecimal)}
    */
   @Test
   public void divide_returns_givenBigDecimal()
   {
      testObject = MutableInfiniteRational.valueOf(6, 2).divide(BigDecimal.valueOf(1.5));  //1.5==15/10
      assertThat(testObject, is(MutableInfiniteRational.valueOf(60, 30)));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#divide(InfiniteInteger)}
    */
   @Test
   public void divide_returns_givenInfiniteInteger()
   {
      testObject = MutableInfiniteRational.valueOf(6, 2).divide(InfiniteInteger.valueOf(2));
      assertThat(testObject, is(MutableInfiniteRational.valueOf(6, 4)));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#divide(MutableInfiniteInteger)}
    */
   @Test
   public void divide_returns_givenMutableInfiniteInteger()
   {
      testObject = MutableInfiniteRational.valueOf(6, 2).divide(MutableInfiniteInteger.valueOf(2));
      assertThat(testObject, is(MutableInfiniteRational.valueOf(6, 4)));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#divide(InfiniteRational)}
    */
   @Test
   public void divide_returns_givenInfiniteRational()
   {
      testObject = MutableInfiniteRational.valueOf(6, 2).divide(InfiniteRational.valueOf(2, 3));
      assertThat(testObject, is(MutableInfiniteRational.valueOf(18, 4)));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#divide(MutableInfiniteRational)}
    */
   @Test
   public void divide_returns_givenMutableInfiniteRational()
   {
      testObject = MutableInfiniteRational.valueOf(6, 2).divide(MutableInfiniteRational.valueOf(2, 3));
      assertThat(testObject, is(MutableInfiniteRational.valueOf(18, 4)));
   }

   @Test
   public void invert()
   {
      testObject = MutableInfiniteRational.valueOf(6, 2).invert();
      assertThat(testObject, is(MutableInfiniteRational.valueOf(2, 6)));
   }

   @Test
   public void invert_returnNan_givenNan()
   {
      testObject = MutableInfiniteRational.NaN.invert();
      assertThat(testObject, is(MutableInfiniteRational.NaN));
   }

   @Test
   public void invert_returnNan_givenZero()
   {
      testObject = MutableInfiniteRational.valueOf(0).invert();
      assertThat(testObject, is(MutableInfiniteRational.NaN));
   }

   @Test
   public void invert_returnZero_givenPositiveInfinity()
   {
      testObject = MutableInfiniteRational.POSITIVE_INFINITY.invert();
      assertThat(testObject, is(MutableInfiniteRational.valueOf(0)));
   }

   @Test
   public void invert_returnZero_givenNegativeInfinity()
   {
      testObject = MutableInfiniteRational.NEGATIVE_INFINITY.invert();
      assertThat(testObject, is(MutableInfiniteRational.valueOf(0)));
   }

   @Test
   public void isWhole_returnsFalse_whenFiniteNonWhole()
   {
      testObject = MutableInfiniteRational.valueOf(5, 3);
      assertThat(testObject.isWhole(), is(false));
   }

   @Test
   public void isWhole_returnsTrue_whenFiniteWhole()
   {
      testObject = MutableInfiniteRational.valueOf(10, 2);
      assertThat(testObject.isWhole(), is(true));
   }

   @Test
   public void isWhole_returnsTrue_whenFiniteWholeNegative()
   {
      testObject = MutableInfiniteRational.valueOf(-10, 2);
      assertThat(testObject.isWhole(), is(true));
   }

   @Test
   public void isWhole_returnsFalse_whenPositiveInfinity()
   {
      testObject = MutableInfiniteRational.POSITIVE_INFINITY;
      assertThat(testObject.isWhole(), is(false));
   }

   @Test
   public void isWhole_returnsFalse_whenNegativeInfinity()
   {
      testObject = MutableInfiniteRational.NEGATIVE_INFINITY;
      assertThat(testObject.isWhole(), is(false));
   }

   @Test
   public void isWhole_returnsFalse_whenNan()
   {
      testObject = MutableInfiniteRational.NaN;
      assertThat(testObject.isWhole(), is(false));
   }

   @Test
   public void truncateToWhole()
   {
      testObject = MutableInfiniteRational.valueOf(5, 3);
      assertThat(testObject.truncateToWhole(), is(MutableInfiniteRational.valueOf(1)));
   }

   @Test
   public void roundToWhole_returnsUnchanged_whenWholeOrNotFinite()
   {
      final List<MutableInfiniteRational> unchangedList = Arrays.asList(MutableInfiniteRational.NaN,
            MutableInfiniteRational.POSITIVE_INFINITY, MutableInfiniteRational.NEGATIVE_INFINITY, MutableInfiniteRational.valueOf(1),
            MutableInfiniteRational.valueOf(-1), MutableInfiniteRational.valueOf(0));
      for (final MutableInfiniteRational input : unchangedList)
      {
         for (final RoundingMode roundingMode : RoundingMode.values())
         {
            assertThat(input.roundToWhole(roundingMode), is(input));
         }
      }
   }

   @Test
   public void roundToWhole_roundsUp_givenUp()
   {
      final MutableInfiniteRational[] inputArray = new MutableInfiniteRational[]
            //@formatter:off
            {
            MutableInfiniteRational.valueOf(1.5), MutableInfiniteRational.valueOf(-1.5),
            MutableInfiniteRational.valueOf(2.5), MutableInfiniteRational.valueOf(-2.5),
            MutableInfiniteRational.valueOf(1, 3), MutableInfiniteRational.valueOf(-1, 3),
            MutableInfiniteRational.valueOf(2, 3), MutableInfiniteRational.valueOf(-2, 3)
            };
            //@formatter:on
      final MutableInfiniteRational[] expectedArray = new MutableInfiniteRational[]
            //@formatter:off
            {
            MutableInfiniteRational.valueOf(2), MutableInfiniteRational.valueOf(-2),
            MutableInfiniteRational.valueOf(3), MutableInfiniteRational.valueOf(-3),
            MutableInfiniteRational.valueOf(1), MutableInfiniteRational.valueOf(-1),
            MutableInfiniteRational.valueOf(1), MutableInfiniteRational.valueOf(-1)
            };
            //@formatter:on
      for (int i = 0; i < inputArray.length; i++)
      {
         assertThat("Index " + i, inputArray[i].roundToWhole(RoundingMode.UP), is(expectedArray[i]));
      }
   }

   @Test
   public void roundToWhole_roundsDown_givenDown()
   {
      final MutableInfiniteRational[] inputArray = new MutableInfiniteRational[]
            //@formatter:off
            {
            MutableInfiniteRational.valueOf(1.5), MutableInfiniteRational.valueOf(-1.5),
            MutableInfiniteRational.valueOf(2.5), MutableInfiniteRational.valueOf(-2.5),
            MutableInfiniteRational.valueOf(1, 3), MutableInfiniteRational.valueOf(-1, 3),
            MutableInfiniteRational.valueOf(2, 3), MutableInfiniteRational.valueOf(-2, 3)
            };
            //@formatter:on
      final MutableInfiniteRational[] expectedArray = new MutableInfiniteRational[]
            //@formatter:off
            {
            MutableInfiniteRational.valueOf(1), MutableInfiniteRational.valueOf(-1),
            MutableInfiniteRational.valueOf(2), MutableInfiniteRational.valueOf(-2),
            MutableInfiniteRational.valueOf(0), MutableInfiniteRational.valueOf(0),
            MutableInfiniteRational.valueOf(0), MutableInfiniteRational.valueOf(0)
            };
            //@formatter:on
      for (int i = 0; i < inputArray.length; i++)
      {
         assertThat("Index " + i, inputArray[i].roundToWhole(RoundingMode.DOWN), is(expectedArray[i]));
      }
   }

   @Test
   public void roundToWhole_rounds_givenCeiling()
   {
      final MutableInfiniteRational[] inputArray = new MutableInfiniteRational[]
            //@formatter:off
            {
            MutableInfiniteRational.valueOf(1.5), MutableInfiniteRational.valueOf(-1.5),
            MutableInfiniteRational.valueOf(2.5), MutableInfiniteRational.valueOf(-2.5),
            MutableInfiniteRational.valueOf(1, 3), MutableInfiniteRational.valueOf(-1, 3),
            MutableInfiniteRational.valueOf(2, 3), MutableInfiniteRational.valueOf(-2, 3)
            };
            //@formatter:on
      final MutableInfiniteRational[] expectedArray = new MutableInfiniteRational[]
            //@formatter:off
            {
            MutableInfiniteRational.valueOf(2), MutableInfiniteRational.valueOf(-1),
            MutableInfiniteRational.valueOf(3), MutableInfiniteRational.valueOf(-2),
            MutableInfiniteRational.valueOf(1), MutableInfiniteRational.valueOf(0),
            MutableInfiniteRational.valueOf(1), MutableInfiniteRational.valueOf(0)
            };
            //@formatter:on
      for (int i = 0; i < inputArray.length; i++)
      {
         assertThat("Index " + i, inputArray[i].roundToWhole(RoundingMode.CEILING), is(expectedArray[i]));
      }
   }

   @Test
   public void roundToWhole_rounds_givenFloor()
   {
      final MutableInfiniteRational[] inputArray = new MutableInfiniteRational[]
            //@formatter:off
            {
            MutableInfiniteRational.valueOf(1.5), MutableInfiniteRational.valueOf(-1.5),
            MutableInfiniteRational.valueOf(2.5), MutableInfiniteRational.valueOf(-2.5),
            MutableInfiniteRational.valueOf(1, 3), MutableInfiniteRational.valueOf(-1, 3),
            MutableInfiniteRational.valueOf(2, 3), MutableInfiniteRational.valueOf(-2, 3)
            };
            //@formatter:on
      final MutableInfiniteRational[] expectedArray = new MutableInfiniteRational[]
            //@formatter:off
            {
            MutableInfiniteRational.valueOf(1), MutableInfiniteRational.valueOf(-2),
            MutableInfiniteRational.valueOf(2), MutableInfiniteRational.valueOf(-3),
            MutableInfiniteRational.valueOf(0), MutableInfiniteRational.valueOf(-1),
            MutableInfiniteRational.valueOf(0), MutableInfiniteRational.valueOf(-1)
            };
            //@formatter:on
      for (int i = 0; i < inputArray.length; i++)
      {
         assertThat("Index " + i, inputArray[i].roundToWhole(RoundingMode.FLOOR), is(expectedArray[i]));
      }
   }

   @Test
   public void roundToWhole_rounds_givenHalfUp()
   {
      final MutableInfiniteRational[] inputArray = new MutableInfiniteRational[]
            //@formatter:off
            {
            MutableInfiniteRational.valueOf(1.5), MutableInfiniteRational.valueOf(-1.5),
            MutableInfiniteRational.valueOf(2.5), MutableInfiniteRational.valueOf(-2.5),
            MutableInfiniteRational.valueOf(1, 3), MutableInfiniteRational.valueOf(-1, 3),
            MutableInfiniteRational.valueOf(2, 3), MutableInfiniteRational.valueOf(-2, 3)
            };
            //@formatter:on
      final MutableInfiniteRational[] expectedArray = new MutableInfiniteRational[]
            //@formatter:off
            {
            MutableInfiniteRational.valueOf(2), MutableInfiniteRational.valueOf(-2),
            MutableInfiniteRational.valueOf(3), MutableInfiniteRational.valueOf(-3),
            MutableInfiniteRational.valueOf(0), MutableInfiniteRational.valueOf(0),
            MutableInfiniteRational.valueOf(1), MutableInfiniteRational.valueOf(-1)
            };
            //@formatter:on
      for (int i = 0; i < inputArray.length; i++)
      {
         assertThat("Index " + i, inputArray[i].roundToWhole(RoundingMode.HALF_UP), is(expectedArray[i]));
      }
   }

   @Test
   public void roundToWhole_rounds_givenHalfDown()
   {
      final MutableInfiniteRational[] inputArray = new MutableInfiniteRational[]
            //@formatter:off
            {
            MutableInfiniteRational.valueOf(1.5), MutableInfiniteRational.valueOf(-1.5),
            MutableInfiniteRational.valueOf(2.5), MutableInfiniteRational.valueOf(-2.5),
            MutableInfiniteRational.valueOf(1, 3), MutableInfiniteRational.valueOf(-1, 3),
            MutableInfiniteRational.valueOf(2, 3), MutableInfiniteRational.valueOf(-2, 3)
            };
            //@formatter:on
      final MutableInfiniteRational[] expectedArray = new MutableInfiniteRational[]
            //@formatter:off
            {
            MutableInfiniteRational.valueOf(1), MutableInfiniteRational.valueOf(-1),
            MutableInfiniteRational.valueOf(2), MutableInfiniteRational.valueOf(-2),
            MutableInfiniteRational.valueOf(0), MutableInfiniteRational.valueOf(0),
            MutableInfiniteRational.valueOf(1), MutableInfiniteRational.valueOf(-1)
            };
            //@formatter:on
      for (int i = 0; i < inputArray.length; i++)
      {
         assertThat("Index " + i, inputArray[i].roundToWhole(RoundingMode.HALF_DOWN), is(expectedArray[i]));
      }
   }

   @Test
   public void roundToWhole_rounds_givenHalfEven()
   {
      final MutableInfiniteRational[] inputArray = new MutableInfiniteRational[]
            //@formatter:off
            {
            MutableInfiniteRational.valueOf(1.5), MutableInfiniteRational.valueOf(-1.5),
            MutableInfiniteRational.valueOf(2.5), MutableInfiniteRational.valueOf(-2.5),
            MutableInfiniteRational.valueOf(1, 3), MutableInfiniteRational.valueOf(-1, 3),
            MutableInfiniteRational.valueOf(2, 3), MutableInfiniteRational.valueOf(-2, 3)
            };
            //@formatter:on
      final MutableInfiniteRational[] expectedArray = new MutableInfiniteRational[]
            //@formatter:off
            {
            MutableInfiniteRational.valueOf(2), MutableInfiniteRational.valueOf(-2),
            MutableInfiniteRational.valueOf(2), MutableInfiniteRational.valueOf(-2),
            MutableInfiniteRational.valueOf(0), MutableInfiniteRational.valueOf(0),
            MutableInfiniteRational.valueOf(1), MutableInfiniteRational.valueOf(-1)
            };
            //@formatter:on
      for (int i = 0; i < inputArray.length; i++)
      {
         assertThat("Index " + i, inputArray[i].roundToWhole(RoundingMode.HALF_EVEN), is(expectedArray[i]));
      }
   }

   @Test
   public void roundToWhole_throws_givenUnnecessaryWhenRoundingNeeded()
   {
      final MutableInfiniteRational[] inputArray = new MutableInfiniteRational[]
            //@formatter:off
            {
            MutableInfiniteRational.valueOf(1.5), MutableInfiniteRational.valueOf(-1.5),
            MutableInfiniteRational.valueOf(2.5), MutableInfiniteRational.valueOf(-2.5),
            MutableInfiniteRational.valueOf(1, 3), MutableInfiniteRational.valueOf(-1, 3),
            MutableInfiniteRational.valueOf(2, 3), MutableInfiniteRational.valueOf(-2, 3)
            };
            //@formatter:on
      for (final MutableInfiniteRational input : inputArray)
      {
         try
         {
            input.roundToWhole(RoundingMode.UNNECESSARY);
            fail("Should've thrown.");
         }
         catch (final ArithmeticException actual)
         {
            assertThat(actual.getMessage(), is("Rounding necessary for " + input));
         }
      }
   }

   @Test
   public void roundToWhole_reduces_whenUnreducedWhole()
   {
      testObject = MutableInfiniteRational.valueOf(10, 2);
      assertThat(testObject.roundToWhole(RoundingMode.DOWN), is(MutableInfiniteRational.valueOf(5)));
      testObject = MutableInfiniteRational.valueOf(0, 100);
      assertThat(testObject.roundToWhole(RoundingMode.DOWN), is(MutableInfiniteRational.valueOf(0)));
   }

   @Test
   public void abs_returnsNan_givenNan() throws Exception
   {
      assertThat(MutableInfiniteRational.NaN.abs(), is(MutableInfiniteRational.NaN));
   }

   @Test
   public void abs_returnsPositiveInfinity_givenPositiveInfinity() throws Exception
   {
      assertThat(MutableInfiniteRational.POSITIVE_INFINITY.abs(), is(MutableInfiniteRational.POSITIVE_INFINITY));
   }

   @Test
   public void abs_returnsNegativeInfinity_givenNegativeInfinity() throws Exception
   {
      assertThat(MutableInfiniteRational.NEGATIVE_INFINITY.abs(), is(MutableInfiniteRational.POSITIVE_INFINITY));
   }

   @Test
   public void abs_returnsZero_givenZero() throws Exception
   {
      assertThat(MutableInfiniteRational.valueOf(0).abs(), is(MutableInfiniteRational.valueOf(0)));
   }

   @Test
   public void abs_returnsPositive_givenPositive() throws Exception
   {
      assertThat(MutableInfiniteRational.valueOf(1).abs(), is(MutableInfiniteRational.valueOf(1)));
   }

   @Test
   public void abs_returnsPositive_givenNegative() throws Exception
   {
      assertThat(MutableInfiniteRational.valueOf(-1).abs(), is(MutableInfiniteRational.valueOf(1)));
   }

   @Test
   public void negate_returnsNan_givenNan() throws Exception
   {
      assertThat(MutableInfiniteRational.NaN.negate(), is(MutableInfiniteRational.NaN));
   }

   @Test
   public void negate_returnsPositiveInfinity_givenPositiveInfinity() throws Exception
   {
      assertThat(MutableInfiniteRational.POSITIVE_INFINITY.negate(), is(MutableInfiniteRational.NEGATIVE_INFINITY));
   }

   @Test
   public void negate_returnsNegativeInfinity_givenNegativeInfinity() throws Exception
   {
      assertThat(MutableInfiniteRational.NEGATIVE_INFINITY.negate(), is(MutableInfiniteRational.POSITIVE_INFINITY));
   }

   @Test
   public void negate_returnsZero_givenZero() throws Exception
   {
      assertThat(MutableInfiniteRational.valueOf(0).negate(), is(MutableInfiniteRational.valueOf(0)));
   }

   @Test
   public void negate_returnsPositive_givenPositive() throws Exception
   {
      assertThat(MutableInfiniteRational.valueOf(1).negate(), is(MutableInfiniteRational.valueOf(-1)));
   }

   @Test
   public void negate_returnsPositive_givenNegative() throws Exception
   {
      assertThat(MutableInfiniteRational.valueOf(-1).negate(), is(MutableInfiniteRational.valueOf(1)));
   }

   @Test
   public void signum_returnsOne_givenPositive() throws Exception
   {
      testObject = MutableInfiniteRational.valueOf(1, 2);
      assertThat(testObject.signum(), is((byte) 1));
   }

   @Test
   public void signum_returnsNegativeOne_givenNegative() throws Exception
   {
      testObject = MutableInfiniteRational.valueOf(-1, 2);
      assertThat(testObject.signum(), is((byte) -1));
   }

   @Test
   public void signum_returnsZero_givenZero() throws Exception
   {
      testObject = MutableInfiniteRational.valueOf(0, 1);
      assertThat(testObject.signum(), is((byte) 0));
   }

   @Test
   public void isNaN() throws Exception
   {
   }

   @Test
   public void isInfinite() throws Exception
   {
   }

   @Test
   public void isFinite() throws Exception
   {
   }

   @Test
   public void signalNaN() throws Exception
   {
   }

   @Test
   public void compareTo_returnsZero_givenSameObject() throws Exception
   {
      //I must call compareTo myself since hamcrest would use .equals() for is()
      testObject = MutableInfiniteRational.valueOf(2);
      assertThat(testObject.compareTo(testObject), is(0));
      testObject = MutableInfiniteRational.NaN;
      assertThat(testObject.compareTo(testObject), is(0));
      testObject = MutableInfiniteRational.POSITIVE_INFINITY;
      assertThat(testObject.compareTo(testObject), is(0));
      testObject = MutableInfiniteRational.NEGATIVE_INFINITY;
      assertThat(testObject.compareTo(testObject), is(0));
   }

   @Test
   public void compareTo_nanIsGreatest() throws Exception
   {
      assertThat(MutableInfiniteRational.NaN, is(greaterThan(MutableInfiniteRational.POSITIVE_INFINITY)));
      assertThat(MutableInfiniteRational.NaN, is(greaterThan(MutableInfiniteRational.NEGATIVE_INFINITY)));
      assertThat(MutableInfiniteRational.NaN, is(greaterThan(MutableInfiniteRational.valueOf(2))));

      assertThat(MutableInfiniteRational.POSITIVE_INFINITY, is(lessThan(MutableInfiniteRational.NaN)));
      assertThat(MutableInfiniteRational.NEGATIVE_INFINITY, is(lessThan(MutableInfiniteRational.NaN)));
      assertThat(MutableInfiniteRational.valueOf(2), is(lessThan(MutableInfiniteRational.NaN)));
   }

   @Test
   public void compareTo_negativeInfinityIsLeast() throws Exception
   {
      assertThat(MutableInfiniteRational.NEGATIVE_INFINITY, is(lessThan(MutableInfiniteRational.POSITIVE_INFINITY)));
      assertThat(MutableInfiniteRational.NEGATIVE_INFINITY, is(lessThan(MutableInfiniteRational.valueOf(2))));

      assertThat(MutableInfiniteRational.POSITIVE_INFINITY, is(greaterThan(MutableInfiniteRational.NEGATIVE_INFINITY)));
      assertThat(MutableInfiniteRational.valueOf(2), is(greaterThan(MutableInfiniteRational.NEGATIVE_INFINITY)));
   }

   @Test
   public void compareTo_positiveInfinityIsNextGreatest() throws Exception
   {
      assertThat(MutableInfiniteRational.POSITIVE_INFINITY, is(greaterThan(MutableInfiniteRational.valueOf(2))));
      assertThat(MutableInfiniteRational.valueOf(2), is(lessThan(MutableInfiniteRational.POSITIVE_INFINITY)));
   }

   @Test
   public void compareTo_positiveGreaterThanNegative() throws Exception
   {
      assertThat(MutableInfiniteRational.valueOf(2), is(greaterThan(MutableInfiniteRational.valueOf(-2))));
      assertThat(MutableInfiniteRational.valueOf(-2), is(lessThan(MutableInfiniteRational.valueOf(2))));
   }

   @Test
   public void compareTo_returnsZero_whenBothAreZero() throws Exception
   {
      //I must call compareTo myself since hamcrest would use .equals() for is()
      testObject = MutableInfiniteRational.valueOf(0);
      assertThat(testObject.compareTo(testObject.copy()), is(0));
   }

   @Test
   public void compareTo_returnsZero_whenBothAreFiniteEqual() throws Exception
   {
      //I must call compareTo myself since hamcrest would use .equals() for is()
      testObject = MutableInfiniteRational.valueOf(2);
      assertThat(testObject.compareTo(testObject.copy()), is(0));
   }

   @Test
   public void compareTo_compares_givenSameDenominator() throws Exception
   {
      testObject = MutableInfiniteRational.valueOf(1, 3);
      final MutableInfiniteRational other = MutableInfiniteRational.valueOf(2, 3);
      assertThat(testObject, is(lessThan(other)));
      assertThat(other, is(greaterThan(testObject)));
   }

   @Test
   public void compareTo_compares_whenBothAreNegative() throws Exception
   {
      testObject = MutableInfiniteRational.valueOf(-1, 3);
      final MutableInfiniteRational other = MutableInfiniteRational.valueOf(-2, 3);
      assertThat(testObject, is(greaterThan(other)));
      assertThat(other, is(lessThan(testObject)));
   }

   @Test
   public void compareTo_usesLcm_whenDifferentDenominator() throws Exception
   {
      testObject = MutableInfiniteRational.valueOf(1, 3);  //== 4/12
      final MutableInfiniteRational other = MutableInfiniteRational.valueOf(1, 4);  //== 3/12
      assertThat(testObject, is(greaterThan(other)));
      assertThat(other, is(lessThan(testObject)));
   }

   @Test
   public void equals() throws Exception
   {
   }

   /**
    * Happy path for {@link MutableInfiniteRational#equalValue(long)}
    */
   @Test
   public void equalValue_returns_givenLong() throws Exception
   {
      testObject = MutableInfiniteRational.valueOf(2, 2);
      assertTrue(testObject.equalValue(1));
      assertFalse(testObject.equalValue(2));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#equalValue(double)}
    */
   @Test
   public void equalValue_returns_givenDouble() throws Exception
   {
      testObject = MutableInfiniteRational.valueOf(6, 4);
      assertTrue(testObject.equalValue(1.5));
      assertFalse(testObject.equalValue(2.5));
   }

   /**
    * Test for {@link MutableInfiniteRational#equalValue(Object)}
    */
   @Test
   public void equalValue_returnsTrue_givenSameObject() throws Exception
   {
      testObject = MutableInfiniteRational.valueOf(1);
      assertTrue(testObject.equalValue(testObject));
   }

   /**
    * Test for {@link MutableInfiniteRational#equalValue(Object)}
    */
   @Test
   public void equalValue_returnsTrue_givenByte() throws Exception
   {
      testObject = MutableInfiniteRational.valueOf(1);
      assertTrue(testObject.equalValue(Byte.valueOf((byte) 1)));
   }

   /**
    * Test for {@link MutableInfiniteRational#equalValue(Object)}
    */
   @Test
   public void equalValue_returnsTrue_givenShort() throws Exception
   {
      testObject = MutableInfiniteRational.valueOf(1);
      assertTrue(testObject.equalValue(Short.valueOf((short) 1)));
   }

   /**
    * Test for {@link MutableInfiniteRational#equalValue(Object)}
    */
   @Test
   public void equalValue_returnsTrue_givenInteger() throws Exception
   {
      testObject = MutableInfiniteRational.valueOf(1);
      assertTrue(testObject.equalValue(Integer.valueOf(1)));
   }

   /**
    * Test for {@link MutableInfiniteRational#equalValue(Object)}
    */
   @Test
   public void equalValue_returnsTrue_givenLong() throws Exception
   {
      testObject = MutableInfiniteRational.valueOf(1);
      assertTrue(testObject.equalValue(Long.valueOf(1)));
   }

   /**
    * Test for {@link MutableInfiniteRational#equalValue(Object)}
    */
   @Test
   public void equalValue_returnsTrue_givenFloat() throws Exception
   {
      testObject = MutableInfiniteRational.valueOf(1);
      assertTrue(testObject.equalValue(Float.valueOf(1)));
   }

   /**
    * Test for {@link MutableInfiniteRational#equalValue(Object)}
    */
   @Test
   public void equalValue_returnsTrue_givenDouble() throws Exception
   {
      testObject = MutableInfiniteRational.valueOf(1);
      assertTrue(testObject.equalValue(Double.valueOf(1)));
   }

   /**
    * Test for {@link MutableInfiniteRational#equalValue(Object)}
    */
   @Test
   public void equalValue_returnsTrue_givenBigInteger() throws Exception
   {
      testObject = MutableInfiniteRational.valueOf(1);
      assertTrue(testObject.equalValue(BigInteger.valueOf(1)));
   }

   /**
    * Test for {@link MutableInfiniteRational#equalValue(Object)}
    */
   @Test
   public void equalValue_returnsTrue_givenBigDecimal() throws Exception
   {
      testObject = MutableInfiniteRational.valueOf(1);
      assertTrue(testObject.equalValue(BigDecimal.valueOf(1)));
   }

   /**
    * Test for {@link MutableInfiniteRational#equalValue(Object)}
    */
   @Test
   public void equalValue_returnsTrue_givenInfiniteInteger() throws Exception
   {
      testObject = MutableInfiniteRational.valueOf(1);
      assertTrue(testObject.equalValue(InfiniteInteger.valueOf(1)));
   }

   /**
    * Test for {@link MutableInfiniteRational#equalValue(Object)}
    */
   @Test
   public void equalValue_returnsTrue_givenMutableInfiniteInteger() throws Exception
   {
      testObject = MutableInfiniteRational.valueOf(1);
      assertTrue(testObject.equalValue(MutableInfiniteInteger.valueOf(1)));
   }

   /**
    * Test for {@link MutableInfiniteRational#equalValue(Object)}
    */
   @Test
   public void equalValue_returnsTrue_givenInfiniteRational() throws Exception
   {
      testObject = MutableInfiniteRational.valueOf(2, 4);
      assertTrue(testObject.equalValue(InfiniteRational.valueOf(1, 2)));
   }

   /**
    * Test for {@link MutableInfiniteRational#equalValue(Object)}
    */
   @Test
   public void equalValue_returnsTrue_givenMutableInfiniteRational() throws Exception
   {
      testObject = MutableInfiniteRational.valueOf(2, 4);
      assertTrue(testObject.equalValue(MutableInfiniteRational.valueOf(1, 2)));
   }

   /**
    * Test for {@link MutableInfiniteRational#equalValue(Object)}
    */
   @Test
   public void equalValue_returnsFalse_givenNull() throws Exception
   {
      testObject = MutableInfiniteRational.valueOf(1);
      assertFalse(testObject.equalValue(null));
   }

   /**
    * Test for {@link MutableInfiniteRational#equalValue(Object)}
    */
   @Test
   public void equalValue_returnsFalse_givenOtherObject() throws Exception
   {
      testObject = MutableInfiniteRational.valueOf(1);
      assertFalse(testObject.equalValue(new Object()));
   }

   @Test
   public void hashCode1() throws Exception
   {
   }

   /**
    * Test for {@link MutableInfiniteRational#toString()}
    */
   @Test
   public void toString_returnsInfinitySymbol_givenPositiveInfinity() throws Exception
   {
      assertEquals("Infinity", MutableInfiniteRational.POSITIVE_INFINITY.toString());
   }

   /**
    * Test for {@link MutableInfiniteRational#toString()}
    */
   @Test
   public void toString_returnsInfinitySymbol_givenNegativeInfinity() throws Exception
   {
      assertEquals("-Infinity", MutableInfiniteRational.NEGATIVE_INFINITY.toString());
   }

   /**
    * Test for {@link MutableInfiniteRational#toString()}
    */
   @Test
   public void toString_returnsNotIntegerSymbols_givenNan() throws Exception
   {
      assertEquals("NaN", MutableInfiniteRational.NaN.toString());
   }

   /**
    * Happy path for {@link MutableInfiniteRational#toString()}
    */
   @Test
   public void toString_returnsWholeThing_whenFits() throws Exception
   {
      final MutableInfiniteInteger moreThanLong = MutableInfiniteInteger.valueOf(Long.MAX_VALUE).add(1);
      testObject = MutableInfiniteRational.valueOf(moreThanLong, MutableInfiniteInteger.valueOf(2));
      assertThat(testObject.toString(), is("9223372036854775808/2"));
   }

   /**
    * Test for {@link MutableInfiniteRational#toString()}
    */
   @Test
   public void toString_returnsEnding_whenTooLarge() throws Exception
   {
      final MutableInfiniteInteger tooBig = MutableInfiniteInteger.valueOf(Long.MAX_VALUE).multiply(100);
      testObject = MutableInfiniteRational.valueOf(tooBig, tooBig);
      assertThat(testObject.toString(), is("…22337203685477580700/…22337203685477580700"));
   }

   /**
    * Test for {@link MutableInfiniteRational#toString()}
    */
   @Test
   public void toString_correctlyPlacesNegative_whenLargeNegative() throws Exception
   {
      final MutableInfiniteInteger tooBig = MutableInfiniteInteger.valueOf(Long.MAX_VALUE).multiply(100);
      testObject = MutableInfiniteRational.valueOf(tooBig.copy().negate(), tooBig);
      assertThat(testObject.toString(), is("-…22337203685477580700/…22337203685477580700"));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#toImproperFractionalString()}
    */
   @Test
   public void toImproperFractionalString() throws Exception
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(1, 15);
      assertThat(actual.toImproperFractionalString(), is("1/15"));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#toImproperFractionalString(int)}
    */
   @Test
   public void toImproperFractionalString_returnsFractionString_givenRadix() throws Exception
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(10, 3);
      assertThat(actual.toImproperFractionalString(16), is("a/3"));
   }

   /**
    * Test for {@link MutableInfiniteRational#toImproperFractionalString(int)}
    */
   @Test
   public void toImproperFractionalString_returnsInfinitySymbol_givenPositiveInfinity() throws Exception
   {
      assertThat(MutableInfiniteRational.POSITIVE_INFINITY.toImproperFractionalString(2), is("∞"));
   }

   /**
    * Test for {@link MutableInfiniteRational#toImproperFractionalString(int)}
    */
   @Test
   public void toImproperFractionalString_returnsInfinitySymbol_givenNegativeInfinity() throws Exception
   {
      assertThat(MutableInfiniteRational.NEGATIVE_INFINITY.toImproperFractionalString(2), is("-∞"));
   }

   /**
    * Test for {@link MutableInfiniteRational#toImproperFractionalString(int)}
    */
   @Test
   public void toImproperFractionalString_returnsNotRationalSymbols_givenNan() throws Exception
   {
      assertThat(MutableInfiniteRational.NaN.toImproperFractionalString(2), is("∉ℚ"));
   }

   /**
    * Test for {@link MutableInfiniteRational#toImproperFractionalString(int)}
    */
   @Test
   public void toImproperFractionalString_returnsNoSlash_givenWholeNumber() throws Exception
   {
      testObject = MutableInfiniteRational.valueOf(2);
      assertThat(testObject.toImproperFractionalString(8), is("2"));
   }

   /**
    * Test for {@link MutableInfiniteRational#toImproperFractionalString(int)}
    */
   @Test
   public void toImproperFractionalString_doesNotReduce_givenUnreduced() throws Exception
   {
      testObject = MutableInfiniteRational.valueOf(2, 4);
      assertThat(testObject.toImproperFractionalString(8), is("2/4"));
   }

   /**
    * Test for {@link MutableInfiniteRational#toImproperFractionalString(int)}
    */
   @Test
   public void toImproperFractionalString_allowsGreaterNumerator_whenImproper() throws Exception
   {
      testObject = MutableInfiniteRational.valueOf(4, 2);
      assertThat(testObject.toImproperFractionalString(8), is("4/2"));
   }

   /**
    * Test for {@link MutableInfiniteRational#toImproperFractionalString(int)}
    */
   @Test
   public void toImproperFractionalString_returnsMinus_whenNegative() throws Exception
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(-1, 2);
      assertThat(actual.toImproperFractionalString(16), is("-1/2"));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#toMixedFactionalString()}
    */
   @Test
   public void toMixedFactionalString() throws Exception
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(1, 13);
      assertThat(actual.toMixedFactionalString(), is("1/13"));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#toMixedFactionalString(int)}
    */
   @Test
   public void toMixedFactionalString_returnsMixedString_givenRadix() throws Exception
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(10 * 3 + 1, 3);
      assertThat(actual.toMixedFactionalString(16), is("a 1/3"));
   }

   /**
    * Test for {@link MutableInfiniteRational#toMixedFactionalString(int)}
    */
   @Test
   public void toMixedFactionalString_returnsInfinitySymbol_givenPositiveInfinity() throws Exception
   {
      assertThat(MutableInfiniteRational.POSITIVE_INFINITY.toMixedFactionalString(2), is("∞"));
   }

   /**
    * Test for {@link MutableInfiniteRational#toMixedFactionalString(int)}
    */
   @Test
   public void toMixedFactionalString_returnsInfinitySymbol_givenNegativeInfinity() throws Exception
   {
      assertThat(MutableInfiniteRational.NEGATIVE_INFINITY.toMixedFactionalString(2), is("-∞"));
   }

   /**
    * Test for {@link MutableInfiniteRational#toMixedFactionalString(int)}
    */
   @Test
   public void toMixedFactionalString_returnsNotRationalSymbols_givenNan() throws Exception
   {
      assertThat(MutableInfiniteRational.NaN.toMixedFactionalString(2), is("∉ℚ"));
   }

   /**
    * Test for {@link MutableInfiniteRational#toMixedFactionalString(int)}
    */
   @Test
   public void toMixedFactionalString_returnsWhole_givenWholeNumber() throws Exception
   {
      testObject = MutableInfiniteRational.valueOf(2);
      assertThat(testObject.toMixedFactionalString(8), is("2"));
   }

   /**
    * Test for {@link MutableInfiniteRational#toMixedFactionalString(int)}
    */
   @Test
   public void toMixedFactionalString_returnsOnlyFraction_whenNoWhole() throws Exception
   {
      testObject = MutableInfiniteRational.valueOf(1, 2);
      assertThat(testObject.toMixedFactionalString(8), is("1/2"));
   }

   /**
    * Test for {@link MutableInfiniteRational#toMixedFactionalString(int)}
    */
   @Test
   public void toMixedFactionalString_doesNotReduce_whenUnreduced() throws Exception
   {
      testObject = MutableInfiniteRational.valueOf(2, 4);
      assertThat(testObject.toMixedFactionalString(8), is("2/4"));
   }

   /**
    * Test for {@link MutableInfiniteRational#toMixedFactionalString(int)}
    */
   @Test
   public void toMixedFactionalString_returnsWhole_givenOnlyWhole() throws Exception
   {
      testObject = MutableInfiniteRational.valueOf(4, 2);
      assertThat(testObject.toMixedFactionalString(8), is("2"));
   }

   /**
    * Test for {@link MutableInfiniteRational#toMixedFactionalString(int)}
    */
   @Test
   public void toMixedFactionalString_returnsMinus_whenNegativeWhole() throws Exception
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(-3, 2);
      assertThat(actual.toMixedFactionalString(16), is("-1 1/2"));
   }

   /**
    * Test for {@link MutableInfiniteRational#toMixedFactionalString(int)}
    */
   @Test
   public void toMixedFactionalString_returnsMinus_whenNegativeFraction() throws Exception
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(-1, 2);
      assertThat(actual.toMixedFactionalString(16), is("-1/2"));
   }

   @Test
   public void copy() throws Exception
   {
      testObject = MutableInfiniteRational.valueOf(2);
      final MutableInfiniteRational actual = testObject.copy();
      assertThat(actual, is(equalTo(testObject)));
      assertThat(actual, is(not(sameInstance(testObject))));
   }

   @Test
   public void copy_returnsSameInstance_whenSpecialValue() throws Exception
   {
      testObject = MutableInfiniteRational.NaN;
      MutableInfiniteRational actual = testObject.copy();
      assertThat(actual, is(sameInstance(testObject)));

      testObject = MutableInfiniteRational.POSITIVE_INFINITY;
      actual = testObject.copy();
      assertThat(actual, is(sameInstance(testObject)));

      testObject = MutableInfiniteRational.NEGATIVE_INFINITY;
      actual = testObject.copy();
      assertThat(actual, is(sameInstance(testObject)));
   }
}
