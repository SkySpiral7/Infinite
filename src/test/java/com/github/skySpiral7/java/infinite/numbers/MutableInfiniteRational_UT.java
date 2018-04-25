package com.github.skySpiral7.java.infinite.numbers;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.github.skySpiral7.java.numbers.NumberFormatException;
import com.github.skySpiral7.java.staticSerialization.ObjectStreamReader;
import com.github.skySpiral7.java.staticSerialization.ObjectStreamWriter;
import org.hamcrest.Matchers;
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
   private List<MutableInfiniteRational> constantList = Arrays.asList(MutableInfiniteRational.NEGATIVE_INFINITY,
         MutableInfiniteRational.POSITIVE_INFINITY, MutableInfiniteRational.NaN);

   /**
    * Happy path for {@link MutableInfiniteRational#valueOf(double)}
    */
   @Test
   public void valueOf_returnsValue_givenDouble()
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(1.5d);
      assertThat(actual.toImproperFractionalString(), is("15/10"));
   }

   /**
    * Test for {@link MutableInfiniteRational#valueOf(double)}
    */
   @Test
   public void valueOf_returnsNan_givenDoubleNan()
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(Double.NaN);
      assertThat(actual, is(sameInstance(MutableInfiniteRational.NaN)));
   }

   /**
    * Test for {@link MutableInfiniteRational#valueOf(double)}
    */
   @Test
   public void valueOf_returnsPositiveInfinity_givenDoublePositiveInfinity()
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(Double.POSITIVE_INFINITY);
      assertThat(actual, is(sameInstance(MutableInfiniteRational.POSITIVE_INFINITY)));
   }

   /**
    * Test for {@link MutableInfiniteRational#valueOf(double)}
    */
   @Test
   public void valueOf_returnsNegativeInfinity_givenDoubleNegativeInfinity()
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(Double.NEGATIVE_INFINITY);
      assertThat(actual, is(sameInstance(MutableInfiniteRational.NEGATIVE_INFINITY)));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#valueOf(BigDecimal)}
    */
   @Test
   public void valueOf_returnsValue_givenWholeBigDecimal()
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(BigDecimal.ONE);
      assertThat(actual.toImproperFractionalString(), is("1"));
   }

   /**
    * Test for {@link MutableInfiniteRational#valueOf(BigDecimal)}
    */
   @Test
   public void valueOf_usesCorrectDenominator_givenNonWholeBigDecimal()
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(BigDecimal.valueOf(10.25));
      //10 25/100 => 1025/100. Does not reduce.
      assertThat(actual.toImproperFractionalString(), is("1025/100"));
   }

   /**
    * Test for {@link MutableInfiniteRational#valueOf(BigDecimal)}
    */
   @Test
   public void valueOf_retainsPrecision_givenBigDecimalWithTrailingZeros()
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(new BigDecimal("1.200"));
      assertThat(actual.toImproperFractionalString(), is("1200/1000"));
   }

   /**
    * Test for {@link MutableInfiniteRational#valueOf(BigDecimal)}
    */
   @Test
   public void valueOf_retainsPrecision_givenWholeBigDecimalWithTrailingZeros()
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(new BigDecimal("1.0"));
      assertThat(actual.toImproperFractionalString(), is("10/10"));
   }

   /**
    * Test for {@link MutableInfiniteRational#valueOf(BigDecimal)}
    */
   @Test
   public void valueOf_retainsPrecision_givenBigDecimalWithNegativeScale()
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(new BigDecimal("6e2"));
      assertThat(actual.toImproperFractionalString(), is("600"));
   }

   /**
    * Test for {@link MutableInfiniteRational#valueOf(BigDecimal)}
    */
   @Test
   public void valueOf_retainsZero_givenBigDecimalZero()
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(BigDecimal.ZERO);
      assertThat(actual.toImproperFractionalString(), is("0"));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#valueOf(long)}
    */
   @Test
   public void valueOf_returnsValue_givenLong()
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(2);
      assertThat(actual.toImproperFractionalString(), is("2"));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#valueOf(long, long)}
    */
   @Test
   public void valueOf_returnsValue_givenLongLong()
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(1, 2);
      assertThat(actual.toImproperFractionalString(), is("1/2"));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#valueOf(BigInteger)}
    */
   @Test
   public void valueOf_returnsValue_givenBigInteger()
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(BigInteger.TEN);
      assertThat(actual.toImproperFractionalString(), is("10"));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#valueOf(BigInteger, BigInteger)}
    */
   @Test
   public void valueOf_returnsValue_givenBigIntegerBigInteger()
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(BigInteger.ONE, BigInteger.TEN);
      assertThat(actual.toImproperFractionalString(), is("1/10"));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#valueOf(InfiniteInteger)}
    */
   @Test
   public void valueOf_returnsValue_givenInfiniteInteger()
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(InfiniteInteger.valueOf(5));
      assertThat(actual.toImproperFractionalString(), is("5"));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#valueOf(InfiniteInteger, InfiniteInteger)}
    */
   @Test
   public void valueOf_returnsValue_givenInfiniteIntegerInfiniteInteger()
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(InfiniteInteger.valueOf(1), InfiniteInteger.valueOf(5));
      assertThat(actual.toImproperFractionalString(), is("1/5"));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#valueOf(MutableInfiniteInteger)}
    */
   @Test
   public void valueOf_returnsValue_givenMutableInfiniteInteger()
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(MutableInfiniteInteger.valueOf(5));
      assertThat(actual.toImproperFractionalString(), is("5"));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#valueOf(MutableInfiniteInteger, MutableInfiniteInteger)}
    */
   @Test
   public void valueOf_returnsValue_givenMutableInfiniteIntegerMutableInfiniteInteger()
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
   public void valueOf_returnsNan_givenMutableInfiniteIntegerNanNumerator()
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
   public void valueOf_returnsNan_givenMutableInfiniteIntegerNanDenominator()
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(MutableInfiniteInteger.valueOf(1), MutableInfiniteInteger.NaN);
      assertThat(actual, is(sameInstance(MutableInfiniteRational.NaN)));
   }

   /**
    * Test for {@link MutableInfiniteRational#valueOf(MutableInfiniteInteger, MutableInfiniteInteger)}
    * Infinity / Infinity == NaN
    */
   @Test
   public void valueOf_returnsNan_givenMutableInfiniteIntegerInfinityDividedByInfinity()
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
   public void valueOf_returnsNan_givenMutableInfiniteIntegerFiniteDividedByZero()
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
   public void valueOf_returnsNan_givenMutableInfiniteIntegerInfiniteDividedByZero()
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
   public void valueOf_returnsPositiveInfinity_givenMutableInfiniteIntegerPositiveInfinityDividedByFinite()
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
   public void valueOf_returnsNegativeInfinity_givenMutableInfiniteIntegerNegativeInfinityDividedByFinite()
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
   public void valueOf_returnsZero_givenMutableInfiniteIntegerFiniteDividedByInfinity()
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(MutableInfiniteInteger.valueOf(2),
            MutableInfiniteInteger.NEGATIVE_INFINITY);
      assertThat(actual, is(MutableInfiniteRational.valueOf(0)));
   }

   /**
    * Test for {@link MutableInfiniteRational#valueOf(MutableInfiniteInteger, MutableInfiniteInteger)}
    */
   @Test
   public void valueOf_normalizedSign_givenPositiveNegative()
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(MutableInfiniteInteger.valueOf(1),
            MutableInfiniteInteger.valueOf(-2));
      assertFraction(actual, -1, 2);
   }

   /**
    * Only used for checking normalized signs. Everything else should use toImproperFractionalString.
    */
   private void assertFraction(final MutableInfiniteRational actual, final long expectedNumerator, final long expectedDenominator)
   {
      assertThat(actual.getNumerator(), is(MutableInfiniteInteger.valueOf(expectedNumerator)));
      assertThat(actual.getDenominator(), is(MutableInfiniteInteger.valueOf(expectedDenominator)));
   }

   /**
    * Test for {@link MutableInfiniteRational#valueOf(MutableInfiniteInteger, MutableInfiniteInteger)}
    */
   @Test
   public void valueOf_normalizedSign_givenNegativeNegative()
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(MutableInfiniteInteger.valueOf(-1),
            MutableInfiniteInteger.valueOf(-2));
      assertFraction(actual, 1, 2);
   }

   /**
    * Test for {@link MutableInfiniteRational#valueOf(MutableInfiniteInteger, MutableInfiniteInteger)}
    */
   @Test
   public void valueOf_normalizedSign_givenZeroNegative()
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(MutableInfiniteInteger.valueOf(0),
            MutableInfiniteInteger.valueOf(-2));
      assertFraction(actual, 0, 2);
   }

   /**
    * Happy path for {@link MutableInfiniteRational#valueOf(MutableInfiniteRational)}
    */
   @Test
   public void valueOf_returnsCopy_givenMutableInfiniteRational()
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
   public void valueOf_returnsValue_givenInfiniteRational()
   {
      testObject = MutableInfiniteRational.valueOf(2);
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(InfiniteRational.valueOf(2));
      assertThat(actual, is(testObject));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#parseImproperFraction(String)}
    */
   @Test
   public void parseImproperFraction_usesRadix10_givenString()
   {
      testObject = MutableInfiniteRational.valueOf(InfiniteRational.valueOf(1, 10));
      final MutableInfiniteRational actual = MutableInfiniteRational.parseImproperFraction("1/10");
      assertThat(actual, is(testObject));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#parseImproperFraction(String, int)}
    */
   @Test
   public void parseImproperFraction_returnsValue_givenStringAndRadix()
   {
      testObject = MutableInfiniteRational.valueOf(1, 2);
      final MutableInfiniteRational actual = MutableInfiniteRational.parseImproperFraction("1/10", 2);
      assertThat(actual, is(testObject));
   }

   /**
    * Test for {@link MutableInfiniteRational#parseImproperFraction(String, int)}
    */
   @Test
   public void parseImproperFraction_returnsInfinity_givenInfinity()
   {
      testObject = MutableInfiniteRational.POSITIVE_INFINITY;
      final MutableInfiniteRational actual = MutableInfiniteRational.parseImproperFraction("∞", 5);
      assertThat(actual, is(testObject));
   }

   /**
    * Test for {@link MutableInfiniteRational#parseImproperFraction(String, int)}
    */
   @Test
   public void parseImproperFraction_returnsInfinity_givenPositiveInfinity()
   {
      testObject = MutableInfiniteRational.POSITIVE_INFINITY;
      final MutableInfiniteRational actual = MutableInfiniteRational.parseImproperFraction("+∞", 5);
      assertThat(actual, is(testObject));
   }

   /**
    * Test for {@link MutableInfiniteRational#parseImproperFraction(String, int)}
    */
   @Test
   public void parseImproperFraction_returnsNegativeInfinity_givenNegativeInfinity()
   {
      testObject = MutableInfiniteRational.NEGATIVE_INFINITY;
      final MutableInfiniteRational actual = MutableInfiniteRational.parseImproperFraction("-∞", 5);
      assertThat(actual, is(testObject));
   }

   /**
    * Test for {@link MutableInfiniteRational#parseImproperFraction(String, int)}
    */
   @Test
   public void parseImproperFraction_returnsNan_givenNan()
   {
      testObject = MutableInfiniteRational.NaN;
      final MutableInfiniteRational actual = MutableInfiniteRational.parseImproperFraction("∉ℚ", 5);
      assertThat(actual, is(testObject));
   }

   /**
    * Test for {@link MutableInfiniteRational#parseImproperFraction(String, int)}
    */
   @Test
   public void parseImproperFraction_returnsvalue_givenWhole()
   {
      testObject = MutableInfiniteRational.valueOf(5);
      final MutableInfiniteRational actual = MutableInfiniteRational.parseImproperFraction("5", 10);
      assertThat(actual, is(testObject));
   }

   /**
    * Test for {@link MutableInfiniteRational#parseImproperFraction(String, int)}
    */
   @Test
   public void parseImproperFraction_throws_givenSignAfterSlash()
   {
      try
      {
         MutableInfiniteRational.parseImproperFraction("1/+2", 10);
         fail("Should've thrown");
      }
      catch (final NumberFormatException actual)
      {
         assertEquals("input string: \"1/+2\"", actual.getMessage());
      }
   }

   /**
    * Happy path for {@link MutableInfiniteRational#parseDecimal(String)}
    */
   @Test
   public void parseDecimal_usesRadix10_givenString()
   {
      testObject = MutableInfiniteRational.valueOf(InfiniteRational.valueOf(11, 10));
      final MutableInfiniteRational actual = MutableInfiniteRational.parseDecimal("1.1");
      assertThat(actual, is(testObject));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#parseDecimal(String, int)}
    */
   @Test
   public void parseDecimal_returnsValue_givenStringAndRadix()
   {
      testObject = MutableInfiniteRational.valueOf(InfiniteRational.valueOf(11, 10));
      final MutableInfiniteRational actual = MutableInfiniteRational.parseDecimal("1.1", 10);
      assertThat(actual, is(testObject));
   }

   /**
    * Test for {@link MutableInfiniteRational#parseDecimal(String, int)}
    */
   @Test
   public void parseDecimal_returnsInfinity_givenInfinity()
   {
      testObject = MutableInfiniteRational.POSITIVE_INFINITY;
      final MutableInfiniteRational actual = MutableInfiniteRational.parseDecimal("∞", 5);
      assertThat(actual, is(testObject));
   }

   /**
    * Test for {@link MutableInfiniteRational#parseDecimal(String, int)}
    */
   @Test
   public void parseDecimal_returnsInfinity_givenPositiveInfinity()
   {
      testObject = MutableInfiniteRational.POSITIVE_INFINITY;
      final MutableInfiniteRational actual = MutableInfiniteRational.parseDecimal("+∞", 5);
      assertThat(actual, is(testObject));
   }

   /**
    * Test for {@link MutableInfiniteRational#parseDecimal(String, int)}
    */
   @Test
   public void parseDecimal_returnsNegativeInfinity_givenNegativeInfinity()
   {
      testObject = MutableInfiniteRational.NEGATIVE_INFINITY;
      final MutableInfiniteRational actual = MutableInfiniteRational.parseDecimal("-∞", 5);
      assertThat(actual, is(testObject));
   }

   /**
    * Test for {@link MutableInfiniteRational#parseDecimal(String, int)}
    */
   @Test
   public void parseDecimal_returnsNan_givenNan()
   {
      testObject = MutableInfiniteRational.NaN;
      final MutableInfiniteRational actual = MutableInfiniteRational.parseDecimal("∉ℚ", 5);
      assertThat(actual, is(testObject));
   }

   /**
    * Test for {@link MutableInfiniteRational#parseDecimal(String, int)}
    */
   @Test(expected = IllegalArgumentException.class)
   public void parseDecimal_throws_givenRepeatingDecimal()
   {
      testObject = MutableInfiniteRational.valueOf(1, 3);
      MutableInfiniteRational.parseDecimal(testObject.toDecimalStringExact());
   }

   /**
    * Test for {@link MutableInfiniteRational#parseDecimal(String, int)}
    */
   @Test
   public void parseDecimal_returnsValue_givenWholeNumberOnly()
   {
      testObject = MutableInfiniteRational.valueOf(2);
      final MutableInfiniteRational actual = MutableInfiniteRational.parseDecimal("11", 1);
      assertThat(actual, is(testObject));
   }

   /**
    * Test for {@link MutableInfiniteRational#parseDecimal(String, int)}
    */
   @Test
   public void parseDecimal_returnsValue_givenWholeNumberWithDot()
   {
      testObject = MutableInfiniteRational.valueOf(1);
      final MutableInfiniteRational actual = MutableInfiniteRational.parseDecimal("1.", 1);
      assertThat(actual, is(testObject));
   }

   /**
    * Test for {@link MutableInfiniteRational#parseDecimal(String, int)}
    */
   @Test
   public void parseDecimal_returnsValue_givenWholeNumberWithDotZero()
   {
      testObject = MutableInfiniteRational.valueOf(2);
      final MutableInfiniteRational actual = MutableInfiniteRational.parseDecimal("2.0", 10);
      assertThat(actual, is(testObject));
   }

   /**
    * Test for {@link MutableInfiniteRational#parseDecimal(String, int)}
    */
   @Test
   public void parseDecimal_throws_givenBase1AndNotWholeNumber()
   {
      try
      {
         MutableInfiniteRational.parseDecimal("1.1", 1);
         fail("Should've thrown");
      }
      catch (final IllegalArgumentException actual)
      {
         assertEquals("Base 1 doesn't support decimal representations. inputString: 1.1", actual.getMessage());
      }
   }

   /**
    * Test for {@link MutableInfiniteRational#parseDecimal(String, int)}
    */
   @Test
   public void parseDecimal_throws_givenSignAfterDecimal()
   {
      try
      {
         MutableInfiniteRational.parseDecimal("1.+1", 10);
         fail("Should've thrown");
      }
      catch (final NumberFormatException actual)
      {
         assertEquals("input string: \"1.+1\"", actual.getMessage());
      }
   }

   /**
    * Test for {@link MutableInfiniteRational#parseDecimal(String, int)}
    */
   @Test
   public void parseDecimal_returnsValue_givenZeroDotNumber()
   {
      testObject = MutableInfiniteRational.valueOf(1, 2);
      final MutableInfiniteRational actual = MutableInfiniteRational.parseDecimal("0.1", 2);
      assertThat(actual, is(testObject));
   }

   /**
    * Test for {@link MutableInfiniteRational#parseDecimal(String, int)}
    */
   @Test
   public void parseDecimal_returnsValue_givenDotNumber()
   {
      testObject = MutableInfiniteRational.valueOf(1, 2);
      final MutableInfiniteRational actual = MutableInfiniteRational.parseDecimal(".1", 2);
      assertThat(actual, is(testObject));
   }

   //NumberDotNumber is happy path

   /**
    * Test for {@link MutableInfiniteRational#parseDecimal(String, int)}
    */
   @Test
   public void parseDecimal_returnsZero_givenDot()
   {
      testObject = MutableInfiniteRational.valueOf(0);
      final MutableInfiniteRational actual = MutableInfiniteRational.parseDecimal(".", 1);
      assertThat(actual, is(testObject));
   }

   /**
    * Test for {@link MutableInfiniteRational#parseDecimal(String, int)}
    */
   @Test
   public void parseDecimal_throws_givenEmptyStringNotBase1()
   {
      try
      {
         MutableInfiniteRational.parseDecimal("", 10);
         fail("Should've thrown");
      }
      catch (final NumberFormatException actual)
      {
         assertEquals("input string: \"\"", actual.getMessage());
      }
   }

   @Test
   public void toInfiniteRational()
   {
      testObject = MutableInfiniteRational.valueOf(2);
      final InfiniteRational actual = testObject.toInfiniteRational();
      assertThat(actual, is(InfiniteRational.valueOf(2)));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#random(MutableInfiniteInteger, MutableInfiniteInteger)}
    */
   @Test
   public void random_returns_notGivenRandom()
   {
      testObject = MutableInfiniteRational.random(MutableInfiniteInteger.valueOf(2), MutableInfiniteInteger.valueOf(2));
      assertThat(testObject.isFinite(), is(true));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#random(MutableInfiniteInteger, MutableInfiniteInteger, Random)}
    */
   @Test
   public void random_returns_givenRandom()
   {
      testObject = MutableInfiniteRational.random(MutableInfiniteInteger.valueOf(2), MutableInfiniteInteger.valueOf(2), new Random());
      assertThat(testObject.isFinite(), is(true));
   }

   /**
    * Test for {@link MutableInfiniteRational#random(MutableInfiniteInteger, MutableInfiniteInteger, Random)}
    */
   @Test
   public void random_returnNan_givenInvalidNumeratorNodeCount()
   {
      testObject = MutableInfiniteRational.random(MutableInfiniteInteger.valueOf(-2), MutableInfiniteInteger.valueOf(2), new Random());
      assertThat(testObject, is(MutableInfiniteRational.NaN));
   }

   /**
    * Test for {@link MutableInfiniteRational#random(MutableInfiniteInteger, MutableInfiniteInteger, Random)}
    */
   @Test
   public void random_returnNan_givenInvalidDenominatorNodeCount()
   {
      testObject = MutableInfiniteRational.random(MutableInfiniteInteger.valueOf(2), MutableInfiniteInteger.valueOf(-2), new Random());
      assertThat(testObject, is(MutableInfiniteRational.NaN));
   }

   @Test
   public void reduce_returnsNan_givenNan()
   {
      assertThat(MutableInfiniteRational.NaN.reduce(), is(MutableInfiniteRational.NaN));
   }

   @Test
   public void reduce_returnsPositiveInfinity_givenPositiveInfinity()
   {
      assertThat(MutableInfiniteRational.POSITIVE_INFINITY.reduce(), is(MutableInfiniteRational.POSITIVE_INFINITY));
   }

   @Test
   public void reduce_returnsNegativeInfinity_givenNegativeInfinity()
   {
      assertThat(MutableInfiniteRational.NEGATIVE_INFINITY.reduce(), is(MutableInfiniteRational.NEGATIVE_INFINITY));
   }

   @Test
   public void reduce_returnsWholeNumber()
   {
      testObject = MutableInfiniteRational.valueOf(12, 12);
      assertThat(testObject.reduce(), is(MutableInfiniteRational.valueOf(1, 1)));

      testObject = MutableInfiniteRational.valueOf(12, 2);
      assertThat(testObject.reduce(), is(MutableInfiniteRational.valueOf(6, 1)));
   }

   @Test
   public void reduce_returnsZero()
   {
      testObject = MutableInfiniteRational.valueOf(0, 12);
      assertThat(testObject.reduce(), is(MutableInfiniteRational.valueOf(0, 1)));
   }

   @Test
   public void reduce()
   {
      testObject = MutableInfiniteRational.valueOf(6, 14);
      assertThat(testObject.reduce(), is(MutableInfiniteRational.valueOf(3, 7)));
   }

   @Test
   public void intValue()
   {
      final int numerator = 10;
      testObject = MutableInfiniteRational.valueOf(numerator, 1);
      assertThat(testObject.intValue(), is(numerator));
   }

   /**
    * Happy path
    */
   @Test
   public void longValue()
   {
      testObject = MutableInfiniteRational.valueOf(3, 2);
      assertThat(testObject.intValue(), is(1));
   }

   @Test
   public void longValue_throws_whenPositiveInfinity()
   {
      try
      {
         MutableInfiniteRational.POSITIVE_INFINITY.longValue();
         fail("Should've thrown");
      }
      catch (final ArithmeticException actual)
      {
         assertEquals("Infinity can't be even partially represented as a long.", actual.getMessage());
      }
   }

   @Test
   public void longValue_throws_whenNegativeInfinity()
   {
      try
      {
         MutableInfiniteRational.NEGATIVE_INFINITY.longValue();
         fail("Should've thrown");
      }
      catch (final ArithmeticException actual)
      {
         assertEquals("-Infinity can't be even partially represented as a long.", actual.getMessage());
      }
   }

   @Test
   public void longValue_throws_whenNan()
   {
      try
      {
         MutableInfiniteRational.NaN.longValue();
         fail("Should've thrown");
      }
      catch (final ArithmeticException actual)
      {
         assertEquals("NaN can't be even partially represented as a long.", actual.getMessage());
      }
   }

   @Test
   public void floatValue()
   {
   }

   @Test
   public void doubleValue()
   {
   }

   /**
    * Happy path for {@link MutableInfiniteRational#add(long)}
    */
   @Test
   public void add_returns_givenLong()
   {
      testObject = MutableInfiniteRational.valueOf(5).add(5);
      assertThat(testObject, is(MutableInfiniteRational.valueOf(10)));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#add(BigInteger)}
    */
   @Test
   public void add_returns_givenBigInteger()
   {
      testObject = MutableInfiniteRational.valueOf(5).add(BigInteger.valueOf(5));
      assertThat(testObject, is(MutableInfiniteRational.valueOf(10)));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#add(double)}
    */
   @Test
   public void add_returns_givenDouble()
   {
      testObject = MutableInfiniteRational.valueOf(5).add((double) 5);
      assertThat(testObject, is(MutableInfiniteRational.valueOf(100, 10)));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#add(BigDecimal)}
    */
   @Test
   public void add_returns_givenBigDecimal()
   {
      testObject = MutableInfiniteRational.valueOf(5).add(BigDecimal.valueOf(5));
      assertThat(testObject, is(MutableInfiniteRational.valueOf(10)));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#add(InfiniteInteger)}
    */
   @Test
   public void add_returns_givenInfiniteInteger()
   {
      testObject = MutableInfiniteRational.valueOf(5).add(InfiniteInteger.valueOf(5));
      assertThat(testObject, is(MutableInfiniteRational.valueOf(10)));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#add(MutableInfiniteInteger)}
    */
   @Test
   public void add_returns_givenMutableInfiniteInteger()
   {
      testObject = MutableInfiniteRational.valueOf(5).add(MutableInfiniteInteger.valueOf(5));
      assertThat(testObject, is(MutableInfiniteRational.valueOf(10)));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#add(InfiniteRational)}
    */
   @Test
   public void add_returns_givenInfiniteRational()
   {
      testObject = MutableInfiniteRational.valueOf(5).add(InfiniteRational.valueOf(5));
      assertThat(testObject, is(MutableInfiniteRational.valueOf(10)));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#add(MutableInfiniteRational)}
    */
   @Test
   public void add_returns_givenMutableInfiniteRational()
   {
      testObject = MutableInfiniteRational.valueOf(5).add(MutableInfiniteRational.valueOf(5));
      assertThat(testObject, is(MutableInfiniteRational.valueOf(10)));
   }

   /**
    * Test for {@link MutableInfiniteRational#add(MutableInfiniteRational)}
    */
   @Test
   public void add_returnsNan_givenNan()
   {
      testObject = MutableInfiniteRational.valueOf(5).add(MutableInfiniteRational.NaN);
      assertThat(testObject, is(MutableInfiniteRational.NaN));
   }

   /**
    * Test for {@link MutableInfiniteRational#add(MutableInfiniteRational)}
    */
   @Test
   public void add_returnsNan_whenNan()
   {
      testObject = MutableInfiniteRational.NaN.add(MutableInfiniteRational.valueOf(5));
      assertThat(testObject, is(MutableInfiniteRational.NaN));
   }

   /**
    * Test for {@link MutableInfiniteRational#add(MutableInfiniteRational)}
    */
   @Test
   public void add_returnsPositiveInfinity_givenPositiveInfinity()
   {
      testObject = MutableInfiniteRational.valueOf(5).add(MutableInfiniteRational.POSITIVE_INFINITY);
      assertThat(testObject, is(MutableInfiniteRational.POSITIVE_INFINITY));
   }

   /**
    * Test for {@link MutableInfiniteRational#add(MutableInfiniteRational)}
    */
   @Test
   public void add_returnsPositiveInfinity_whenPositiveInfinity()
   {
      testObject = MutableInfiniteRational.POSITIVE_INFINITY.add(MutableInfiniteRational.valueOf(5));
      assertThat(testObject, is(MutableInfiniteRational.POSITIVE_INFINITY));
   }

   /**
    * Test for {@link MutableInfiniteRational#add(MutableInfiniteRational)}
    */
   @Test
   public void add_returnsNegativeInfinity_givenNegativeInfinity()
   {
      testObject = MutableInfiniteRational.valueOf(5).add(MutableInfiniteRational.NEGATIVE_INFINITY);
      assertThat(testObject, is(MutableInfiniteRational.NEGATIVE_INFINITY));
   }

   /**
    * Test for {@link MutableInfiniteRational#add(MutableInfiniteRational)}
    */
   @Test
   public void add_returnsNegativeInfinity_whenNegativeInfinity()
   {
      testObject = MutableInfiniteRational.NEGATIVE_INFINITY.add(MutableInfiniteRational.valueOf(5));
      assertThat(testObject, is(MutableInfiniteRational.NEGATIVE_INFINITY));
   }

   /**
    * Test for {@link MutableInfiniteRational#add(MutableInfiniteRational)}
    */
   @Test
   public void add_returnsNan_whenNegativeInfinityAndPositiveInfinity()
   {
      testObject = MutableInfiniteRational.POSITIVE_INFINITY.add(MutableInfiniteRational.NEGATIVE_INFINITY);
      assertThat(MutableInfiniteRational.POSITIVE_INFINITY, is(not(MutableInfiniteRational.NaN)));
      assertThat(MutableInfiniteRational.NEGATIVE_INFINITY, is(not(MutableInfiniteRational.NaN)));
      assertThat(MutableInfiniteRational.POSITIVE_INFINITY, is(not(MutableInfiniteRational.NEGATIVE_INFINITY)));

      assertThat(testObject, is(MutableInfiniteRational.NaN));
      testObject = MutableInfiniteRational.NEGATIVE_INFINITY.add(MutableInfiniteRational.POSITIVE_INFINITY);
      assertThat(testObject, is(MutableInfiniteRational.NaN));
   }

   /**
    * Test for {@link MutableInfiniteRational#add(MutableInfiniteRational)}
    */
   @Test
   public void add_returnsResult_givenSameDenominator()
   {
      testObject = MutableInfiniteRational.valueOf(1, 3).add(MutableInfiniteRational.valueOf(2, 3));
      assertThat(testObject, is(MutableInfiniteRational.valueOf(3, 3)));
   }

   /**
    * Test for {@link MutableInfiniteRational#add(MutableInfiniteRational)}
    */
   @Test
   public void add_returnsResult_givenDifferentDenominator()
   {
      testObject = MutableInfiniteRational.valueOf(1, 3).add(MutableInfiniteRational.valueOf(1, 2));
      // 1/3+1/2 = 2/6+3/6 = 5/6
      assertThat(testObject, is(MutableInfiniteRational.valueOf(5, 6)));
   }

   /**
    * Test for {@link MutableInfiniteRational#add(MutableInfiniteRational)}
    */
   @Test
   public void add_returnsOther_givenZero()
   {
      testObject = MutableInfiniteRational.valueOf(0).add(MutableInfiniteRational.valueOf(5));
      assertThat(testObject, is(MutableInfiniteRational.valueOf(5)));
      testObject = MutableInfiniteRational.valueOf(5).add(MutableInfiniteRational.valueOf(0));
      assertThat(testObject, is(MutableInfiniteRational.valueOf(5)));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#subtract(long)}
    */
   @Test
   public void subtract_returns_givenLong()
   {
      testObject = MutableInfiniteRational.valueOf(10).subtract(5);
      assertThat(testObject, is(MutableInfiniteRational.valueOf(5)));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#subtract(BigInteger)}
    */
   @Test
   public void subtract_returns_givenBigInteger()
   {
      testObject = MutableInfiniteRational.valueOf(10).subtract(BigInteger.valueOf(5));
      assertThat(testObject, is(MutableInfiniteRational.valueOf(5)));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#subtract(double)}
    */
   @Test
   public void subtract_returns_givenDouble()
   {
      testObject = MutableInfiniteRational.valueOf(10).subtract((double) 5);
      assertThat(testObject, is(MutableInfiniteRational.valueOf(50, 10)));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#subtract(BigDecimal)}
    */
   @Test
   public void subtract_returns_givenBigDecimal()
   {
      testObject = MutableInfiniteRational.valueOf(10).subtract(BigDecimal.valueOf(5));
      assertThat(testObject, is(MutableInfiniteRational.valueOf(5)));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#subtract(InfiniteInteger)}
    */
   @Test
   public void subtract_returns_givenInfiniteInteger()
   {
      testObject = MutableInfiniteRational.valueOf(10).subtract(InfiniteInteger.valueOf(5));
      assertThat(testObject, is(MutableInfiniteRational.valueOf(5)));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#subtract(MutableInfiniteInteger)}
    */
   @Test
   public void subtract_returns_givenMutableInfiniteInteger()
   {
      testObject = MutableInfiniteRational.valueOf(10).subtract(MutableInfiniteInteger.valueOf(5));
      assertThat(testObject, is(MutableInfiniteRational.valueOf(5)));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#subtract(InfiniteRational)}
    */
   @Test
   public void subtract_returns_givenInfiniteRational()
   {
      testObject = MutableInfiniteRational.valueOf(10).subtract(InfiniteRational.valueOf(5));
      assertThat(testObject, is(MutableInfiniteRational.valueOf(5)));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#subtract(MutableInfiniteRational)}
    */
   @Test
   public void subtract_returns_givenMutableInfiniteRational()
   {
      testObject = MutableInfiniteRational.valueOf(10).subtract(MutableInfiniteRational.valueOf(5));
      assertThat(testObject, is(MutableInfiniteRational.valueOf(5)));
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

   /**
    * Happy path for {@link MutableInfiniteRational#power(long)}
    */
   @Test
   public void power_returns_givenLong()
   {
      testObject = MutableInfiniteRational.valueOf(2, 3);
      assertThat(testObject.power(2), is(MutableInfiniteRational.valueOf(4, 9)));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#power(BigInteger)}
    */
   @Test
   public void power_returns_givenBigInteger()
   {
      testObject = MutableInfiniteRational.valueOf(2, 3);
      assertThat(testObject.power(BigInteger.valueOf(2)), is(MutableInfiniteRational.valueOf(4, 9)));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#power(InfiniteInteger)}
    */
   @Test
   public void power_returns_givenInfiniteInteger()
   {
      testObject = MutableInfiniteRational.valueOf(2, 3);
      assertThat(testObject.power(InfiniteInteger.valueOf(2)), is(MutableInfiniteRational.valueOf(4, 9)));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#power(MutableInfiniteInteger)}
    */
   @Test
   public void power_returns_givenMutableInfiniteInteger()
   {
      testObject = MutableInfiniteRational.valueOf(2, 3);
      assertThat(testObject.power(MutableInfiniteInteger.valueOf(2)), is(MutableInfiniteRational.valueOf(4, 9)));
   }

   /**
    * Test for {@link MutableInfiniteRational#power(MutableInfiniteInteger)}
    */
   @Test
   public void power_inverts_givenNegativeExponent()
   {
      testObject = MutableInfiniteRational.valueOf(2, 3);
      assertThat(testObject.power(MutableInfiniteInteger.valueOf(-2)), is(MutableInfiniteRational.valueOf(9, 4)));
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
   public void abs_returnsNan_givenNan()
   {
      assertThat(MutableInfiniteRational.NaN.abs(), is(MutableInfiniteRational.NaN));
   }

   @Test
   public void abs_returnsPositiveInfinity_givenPositiveInfinity()
   {
      assertThat(MutableInfiniteRational.POSITIVE_INFINITY.abs(), is(MutableInfiniteRational.POSITIVE_INFINITY));
   }

   @Test
   public void abs_returnsNegativeInfinity_givenNegativeInfinity()
   {
      assertThat(MutableInfiniteRational.NEGATIVE_INFINITY.abs(), is(MutableInfiniteRational.POSITIVE_INFINITY));
   }

   @Test
   public void abs_returnsZero_givenZero()
   {
      assertThat(MutableInfiniteRational.valueOf(0).abs(), is(MutableInfiniteRational.valueOf(0)));
   }

   @Test
   public void abs_returnsPositive_givenPositive()
   {
      assertThat(MutableInfiniteRational.valueOf(1).abs(), is(MutableInfiniteRational.valueOf(1)));
   }

   @Test
   public void abs_returnsPositive_givenNegative()
   {
      assertThat(MutableInfiniteRational.valueOf(-1).abs(), is(MutableInfiniteRational.valueOf(1)));
   }

   @Test
   public void negate_returnsNan_givenNan()
   {
      assertThat(MutableInfiniteRational.NaN.negate(), is(MutableInfiniteRational.NaN));
   }

   @Test
   public void negate_returnsPositiveInfinity_givenPositiveInfinity()
   {
      assertThat(MutableInfiniteRational.POSITIVE_INFINITY.negate(), is(MutableInfiniteRational.NEGATIVE_INFINITY));
   }

   @Test
   public void negate_returnsNegativeInfinity_givenNegativeInfinity()
   {
      assertThat(MutableInfiniteRational.NEGATIVE_INFINITY.negate(), is(MutableInfiniteRational.POSITIVE_INFINITY));
   }

   @Test
   public void negate_returnsZero_givenZero()
   {
      assertThat(MutableInfiniteRational.valueOf(0).negate(), is(MutableInfiniteRational.valueOf(0)));
   }

   @Test
   public void negate_returnsPositive_givenPositive()
   {
      assertThat(MutableInfiniteRational.valueOf(1).negate(), is(MutableInfiniteRational.valueOf(-1)));
   }

   @Test
   public void negate_returnsPositive_givenNegative()
   {
      assertThat(MutableInfiniteRational.valueOf(-1).negate(), is(MutableInfiniteRational.valueOf(1)));
   }

   @Test
   public void signum_returnsOne_givenPositive()
   {
      testObject = MutableInfiniteRational.valueOf(1, 2);
      assertThat(testObject.signum(), is((byte) 1));
   }

   @Test
   public void signum_returnsNegativeOne_givenNegative()
   {
      testObject = MutableInfiniteRational.valueOf(-1, 2);
      assertThat(testObject.signum(), is((byte) -1));
   }

   @Test
   public void signum_returnsZero_givenZero()
   {
      testObject = MutableInfiniteRational.valueOf(0, 1);
      assertThat(testObject.signum(), is((byte) 0));
   }

   @Test
   public void isNaN()
   {
   }

   @Test
   public void isInfinite()
   {
   }

   @Test
   public void isFinite()
   {
   }

   @Test
   public void signalNaN()
   {
   }

   @Test
   public void compareTo_returnsZero_givenSameObject()
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
   public void compareTo_nanIsGreatest()
   {
      assertThat(MutableInfiniteRational.NaN, is(greaterThan(MutableInfiniteRational.POSITIVE_INFINITY)));
      assertThat(MutableInfiniteRational.NaN, is(greaterThan(MutableInfiniteRational.NEGATIVE_INFINITY)));
      assertThat(MutableInfiniteRational.NaN, is(greaterThan(MutableInfiniteRational.valueOf(2))));

      assertThat(MutableInfiniteRational.POSITIVE_INFINITY, is(lessThan(MutableInfiniteRational.NaN)));
      assertThat(MutableInfiniteRational.NEGATIVE_INFINITY, is(lessThan(MutableInfiniteRational.NaN)));
      assertThat(MutableInfiniteRational.valueOf(2), is(lessThan(MutableInfiniteRational.NaN)));
   }

   @Test
   public void compareTo_negativeInfinityIsLeast()
   {
      assertThat(MutableInfiniteRational.NEGATIVE_INFINITY, is(lessThan(MutableInfiniteRational.POSITIVE_INFINITY)));
      assertThat(MutableInfiniteRational.NEGATIVE_INFINITY, is(lessThan(MutableInfiniteRational.valueOf(2))));

      assertThat(MutableInfiniteRational.POSITIVE_INFINITY, is(greaterThan(MutableInfiniteRational.NEGATIVE_INFINITY)));
      assertThat(MutableInfiniteRational.valueOf(2), is(greaterThan(MutableInfiniteRational.NEGATIVE_INFINITY)));
   }

   @Test
   public void compareTo_positiveInfinityIsNextGreatest()
   {
      assertThat(MutableInfiniteRational.POSITIVE_INFINITY, is(greaterThan(MutableInfiniteRational.valueOf(2))));
      assertThat(MutableInfiniteRational.valueOf(2), is(lessThan(MutableInfiniteRational.POSITIVE_INFINITY)));
   }

   @Test
   public void compareTo_positiveGreaterThanNegative()
   {
      assertThat(MutableInfiniteRational.valueOf(2), is(greaterThan(MutableInfiniteRational.valueOf(-2))));
      assertThat(MutableInfiniteRational.valueOf(-2), is(lessThan(MutableInfiniteRational.valueOf(2))));
   }

   @Test
   public void compareTo_returnsZero_whenBothAreZero()
   {
      //I must call compareTo myself since hamcrest would use .equals() for is()
      testObject = MutableInfiniteRational.valueOf(0);
      assertThat(testObject.compareTo(testObject.copy()), is(0));
   }

   @Test
   public void compareTo_returnsZero_whenBothAreFiniteEqual()
   {
      //I must call compareTo myself since hamcrest would use .equals() for is()
      testObject = MutableInfiniteRational.valueOf(2);
      assertThat(testObject.compareTo(testObject.copy()), is(0));
   }

   @Test
   public void compareTo_compares_givenSameDenominator()
   {
      testObject = MutableInfiniteRational.valueOf(1, 3);
      final MutableInfiniteRational other = MutableInfiniteRational.valueOf(2, 3);
      assertThat(testObject, is(lessThan(other)));
      assertThat(other, is(greaterThan(testObject)));
   }

   @Test
   public void compareTo_compares_whenBothAreNegative()
   {
      testObject = MutableInfiniteRational.valueOf(-1, 3);
      final MutableInfiniteRational other = MutableInfiniteRational.valueOf(-2, 3);
      assertThat(testObject, is(greaterThan(other)));
      assertThat(other, is(lessThan(testObject)));
   }

   @Test
   public void compareTo_usesLcm_whenDifferentDenominator()
   {
      testObject = MutableInfiniteRational.valueOf(1, 3);  //== 4/12
      final MutableInfiniteRational other = MutableInfiniteRational.valueOf(1, 4);  //== 3/12
      assertThat(testObject, is(greaterThan(other)));
      assertThat(other, is(lessThan(testObject)));
   }

   @Test
   public void equals()
   {
   }

   /**
    * Happy path for {@link MutableInfiniteRational#equalValue(long)}
    */
   @Test
   public void equalValue_returns_givenLong()
   {
      testObject = MutableInfiniteRational.valueOf(2, 2);
      assertTrue(testObject.equalValue(1));
      assertFalse(testObject.equalValue(2));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#equalValue(double)}
    */
   @Test
   public void equalValue_returns_givenDouble()
   {
      testObject = MutableInfiniteRational.valueOf(6, 4);
      assertTrue(testObject.equalValue(1.5));
      assertFalse(testObject.equalValue(2.5));
   }

   /**
    * Test for {@link MutableInfiniteRational#equalValue(Object)}
    */
   @Test
   public void equalValue_returnsTrue_givenSameObject()
   {
      testObject = MutableInfiniteRational.valueOf(1);
      assertTrue(testObject.equalValue(testObject));
   }

   /**
    * Test for {@link MutableInfiniteRational#equalValue(Object)}
    */
   @Test
   public void equalValue_returnsTrue_givenByte()
   {
      testObject = MutableInfiniteRational.valueOf(1);
      assertTrue(testObject.equalValue(Byte.valueOf((byte) 1)));
   }

   /**
    * Test for {@link MutableInfiniteRational#equalValue(Object)}
    */
   @Test
   public void equalValue_returnsTrue_givenShort()
   {
      testObject = MutableInfiniteRational.valueOf(1);
      assertTrue(testObject.equalValue(Short.valueOf((short) 1)));
   }

   /**
    * Test for {@link MutableInfiniteRational#equalValue(Object)}
    */
   @Test
   public void equalValue_returnsTrue_givenInteger()
   {
      testObject = MutableInfiniteRational.valueOf(1);
      assertTrue(testObject.equalValue(Integer.valueOf(1)));
   }

   /**
    * Test for {@link MutableInfiniteRational#equalValue(Object)}
    */
   @Test
   public void equalValue_returnsTrue_givenLong()
   {
      testObject = MutableInfiniteRational.valueOf(1);
      assertTrue(testObject.equalValue(Long.valueOf(1)));
   }

   /**
    * Test for {@link MutableInfiniteRational#equalValue(Object)}
    */
   @Test
   public void equalValue_returnsTrue_givenFloat()
   {
      testObject = MutableInfiniteRational.valueOf(1);
      assertTrue(testObject.equalValue(Float.valueOf(1)));
   }

   /**
    * Test for {@link MutableInfiniteRational#equalValue(Object)}
    */
   @Test
   public void equalValue_returnsTrue_givenDouble()
   {
      testObject = MutableInfiniteRational.valueOf(1);
      assertTrue(testObject.equalValue(Double.valueOf(1)));
   }

   /**
    * Test for {@link MutableInfiniteRational#equalValue(Object)}
    */
   @Test
   public void equalValue_returnsTrue_givenBigInteger()
   {
      testObject = MutableInfiniteRational.valueOf(1);
      assertTrue(testObject.equalValue(BigInteger.valueOf(1)));
   }

   /**
    * Test for {@link MutableInfiniteRational#equalValue(Object)}
    */
   @Test
   public void equalValue_returnsTrue_givenBigDecimal()
   {
      testObject = MutableInfiniteRational.valueOf(1);
      assertTrue(testObject.equalValue(BigDecimal.valueOf(1)));
   }

   /**
    * Test for {@link MutableInfiniteRational#equalValue(Object)}
    */
   @Test
   public void equalValue_returnsTrue_givenInfiniteInteger()
   {
      testObject = MutableInfiniteRational.valueOf(1);
      assertTrue(testObject.equalValue(InfiniteInteger.valueOf(1)));
   }

   /**
    * Test for {@link MutableInfiniteRational#equalValue(Object)}
    */
   @Test
   public void equalValue_returnsTrue_givenMutableInfiniteInteger()
   {
      testObject = MutableInfiniteRational.valueOf(1);
      assertTrue(testObject.equalValue(MutableInfiniteInteger.valueOf(1)));
   }

   /**
    * Test for {@link MutableInfiniteRational#equalValue(Object)}
    */
   @Test
   public void equalValue_returnsTrue_givenInfiniteRational()
   {
      testObject = MutableInfiniteRational.valueOf(2, 4);
      assertTrue(testObject.equalValue(InfiniteRational.valueOf(1, 2)));
   }

   /**
    * Test for {@link MutableInfiniteRational#equalValue(Object)}
    */
   @Test
   public void equalValue_returnsTrue_givenMutableInfiniteRational()
   {
      testObject = MutableInfiniteRational.valueOf(2, 4);
      assertTrue(testObject.equalValue(MutableInfiniteRational.valueOf(1, 2)));
   }

   /**
    * Test for {@link MutableInfiniteRational#equalValue(Object)}
    */
   @Test
   public void equalValue_returnsFalse_givenNull()
   {
      testObject = MutableInfiniteRational.valueOf(1);
      assertFalse(testObject.equalValue(null));
   }

   /**
    * Test for {@link MutableInfiniteRational#equalValue(Object)}
    */
   @Test
   public void equalValue_returnsFalse_givenOtherObject()
   {
      testObject = MutableInfiniteRational.valueOf(1);
      assertFalse(testObject.equalValue(new Object()));
   }

   @Test
   public void hashCode1()
   {
   }

   /**
    * Test for {@link MutableInfiniteRational#toString()}
    */
   @Test
   public void toString_returnsInfinitySymbol_givenPositiveInfinity()
   {
      assertEquals("Infinity", MutableInfiniteRational.POSITIVE_INFINITY.toString());
   }

   /**
    * Test for {@link MutableInfiniteRational#toString()}
    */
   @Test
   public void toString_returnsInfinitySymbol_givenNegativeInfinity()
   {
      assertEquals("-Infinity", MutableInfiniteRational.NEGATIVE_INFINITY.toString());
   }

   /**
    * Test for {@link MutableInfiniteRational#toString()}
    */
   @Test
   public void toString_returnsNotIntegerSymbols_givenNan()
   {
      assertEquals("NaN", MutableInfiniteRational.NaN.toString());
   }

   /**
    * Happy path for {@link MutableInfiniteRational#toString()}
    */
   @Test
   public void toString_returnsWholeThing_whenFits()
   {
      final MutableInfiniteInteger moreThanLong = MutableInfiniteInteger.valueOf(Long.MAX_VALUE).add(1);
      testObject = MutableInfiniteRational.valueOf(moreThanLong, MutableInfiniteInteger.valueOf(2));
      assertThat(testObject.toString(), is("9223372036854775808/2"));
   }

   /**
    * Test for {@link MutableInfiniteRational#toString()}
    */
   @Test
   public void toString_returnsEnding_whenTooLarge()
   {
      final MutableInfiniteInteger tooBig = MutableInfiniteInteger.valueOf(Long.MAX_VALUE).multiply(100);
      testObject = MutableInfiniteRational.valueOf(tooBig, tooBig);
      assertThat(testObject.toString(), is("…22337203685477580700/…22337203685477580700"));
   }

   /**
    * Test for {@link MutableInfiniteRational#toString()}
    */
   @Test
   public void toString_correctlyPlacesNegative_whenLargeNegative()
   {
      final MutableInfiniteInteger tooBig = MutableInfiniteInteger.valueOf(Long.MAX_VALUE).multiply(100);
      testObject = MutableInfiniteRational.valueOf(tooBig.copy().negate(), tooBig);
      assertThat(testObject.toString(), is("-…22337203685477580700/…22337203685477580700"));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#toImproperFractionalString()}
    */
   @Test
   public void toImproperFractionalString()
   {
      testObject = MutableInfiniteRational.valueOf(1, 15);
      assertThat(testObject.toImproperFractionalString(), is("1/15"));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#toImproperFractionalString(int)}
    */
   @Test
   public void toImproperFractionalString_returnsFractionString_givenRadix()
   {
      testObject = MutableInfiniteRational.valueOf(10, 3);
      assertThat(testObject.toImproperFractionalString(16), is("a/3"));
   }

   /**
    * Test for {@link MutableInfiniteRational#toImproperFractionalString(int)}
    */
   @Test
   public void toImproperFractionalString_returnsInfinitySymbol_givenPositiveInfinity()
   {
      assertThat(MutableInfiniteRational.POSITIVE_INFINITY.toImproperFractionalString(2), is("∞"));
   }

   /**
    * Test for {@link MutableInfiniteRational#toImproperFractionalString(int)}
    */
   @Test
   public void toImproperFractionalString_returnsInfinitySymbol_givenNegativeInfinity()
   {
      assertThat(MutableInfiniteRational.NEGATIVE_INFINITY.toImproperFractionalString(2), is("-∞"));
   }

   /**
    * Test for {@link MutableInfiniteRational#toImproperFractionalString(int)}
    */
   @Test
   public void toImproperFractionalString_returnsNotRationalSymbols_givenNan()
   {
      assertThat(MutableInfiniteRational.NaN.toImproperFractionalString(2), is("∉ℚ"));
   }

   /**
    * Test for {@link MutableInfiniteRational#toImproperFractionalString(int)}
    */
   @Test
   public void toImproperFractionalString_returnsNoSlash_givenWholeNumber()
   {
      testObject = MutableInfiniteRational.valueOf(2);
      assertThat(testObject.toImproperFractionalString(8), is("2"));
   }

   /**
    * Test for {@link MutableInfiniteRational#toImproperFractionalString(int)}
    */
   @Test
   public void toImproperFractionalString_doesNotReduce_givenUnreduced()
   {
      testObject = MutableInfiniteRational.valueOf(2, 4);
      assertThat(testObject.toImproperFractionalString(8), is("2/4"));
   }

   /**
    * Test for {@link MutableInfiniteRational#toImproperFractionalString(int)}
    */
   @Test
   public void toImproperFractionalString_allowsGreaterNumerator_whenImproper()
   {
      testObject = MutableInfiniteRational.valueOf(4, 2);
      assertThat(testObject.toImproperFractionalString(8), is("4/2"));
   }

   /**
    * Test for {@link MutableInfiniteRational#toImproperFractionalString(int)}
    */
   @Test
   public void toImproperFractionalString_returnsMinus_whenNegative()
   {
      testObject = MutableInfiniteRational.valueOf(-1, 2);
      assertThat(testObject.toImproperFractionalString(16), is("-1/2"));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#toMixedFractionalString()}
    */
   @Test
   public void toMixedFractionalString()
   {
      testObject = MutableInfiniteRational.valueOf(1, 13);
      assertThat(testObject.toMixedFractionalString(), is("1/13"));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#toMixedFractionalString(int)}
    */
   @Test
   public void toMixedFractionalString_returnsMixedString_givenRadix()
   {
      testObject = MutableInfiniteRational.valueOf(10 * 3 + 1, 3);
      assertThat(testObject.toMixedFractionalString(16), is("a 1/3"));
   }

   /**
    * Test for {@link MutableInfiniteRational#toMixedFractionalString(int)}
    */
   @Test
   public void toMixedFractionalString_returnsInfinitySymbol_givenPositiveInfinity()
   {
      assertThat(MutableInfiniteRational.POSITIVE_INFINITY.toMixedFractionalString(2), is("∞"));
   }

   /**
    * Test for {@link MutableInfiniteRational#toMixedFractionalString(int)}
    */
   @Test
   public void toMixedFractionalString_returnsInfinitySymbol_givenNegativeInfinity()
   {
      assertThat(MutableInfiniteRational.NEGATIVE_INFINITY.toMixedFractionalString(2), is("-∞"));
   }

   /**
    * Test for {@link MutableInfiniteRational#toMixedFractionalString(int)}
    */
   @Test
   public void toMixedFractionalString_returnsNotRationalSymbols_givenNan()
   {
      assertThat(MutableInfiniteRational.NaN.toMixedFractionalString(2), is("∉ℚ"));
   }

   /**
    * Test for {@link MutableInfiniteRational#toMixedFractionalString(int)}
    */
   @Test
   public void toMixedFractionalString_returnsWhole_givenWholeNumber()
   {
      testObject = MutableInfiniteRational.valueOf(2);
      assertThat(testObject.toMixedFractionalString(8), is("2"));
   }

   /**
    * Test for {@link MutableInfiniteRational#toMixedFractionalString(int)}
    */
   @Test
   public void toMixedFractionalString_returnsOnlyFraction_whenNoWhole()
   {
      testObject = MutableInfiniteRational.valueOf(1, 2);
      assertThat(testObject.toMixedFractionalString(8), is("1/2"));
   }

   /**
    * Test for {@link MutableInfiniteRational#toMixedFractionalString(int)}
    */
   @Test
   public void toMixedFractionalString_doesNotReduce_whenUnreduced()
   {
      testObject = MutableInfiniteRational.valueOf(2, 4);
      assertThat(testObject.toMixedFractionalString(8), is("2/4"));
   }

   /**
    * Test for {@link MutableInfiniteRational#toMixedFractionalString(int)}
    */
   @Test
   public void toMixedFractionalString_returnsWhole_givenOnlyWhole()
   {
      testObject = MutableInfiniteRational.valueOf(4, 2);
      assertThat(testObject.toMixedFractionalString(8), is("2"));
   }

   /**
    * Test for {@link MutableInfiniteRational#toMixedFractionalString(int)}
    */
   @Test
   public void toMixedFractionalString_returnsMinus_whenNegativeWhole()
   {
      testObject = MutableInfiniteRational.valueOf(-3, 2);
      assertThat(testObject.toMixedFractionalString(16), is("-1 1/2"));
   }

   /**
    * Test for {@link MutableInfiniteRational#toMixedFractionalString(int)}
    */
   @Test
   public void toMixedFractionalString_returnsMinus_whenNegativeFraction()
   {
      testObject = MutableInfiniteRational.valueOf(-1, 2);
      assertThat(testObject.toMixedFractionalString(16), is("-1/2"));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#toDecimalString(int)}
    */
   @Test
   public void toDecimalString_returns_givenOnlyDecimalPlaces()
   {
      testObject = MutableInfiniteRational.valueOf(1, 2);
      assertThat(testObject.toDecimalString(3), is("0.500"));
   }

   /**
    * Test for {@link MutableInfiniteRational#toDecimalString(int, int)}
    */
   @Test
   public void toDecimalString_returnsInfinitySymbol_givenPositiveInfinity()
   {
      testObject = MutableInfiniteRational.POSITIVE_INFINITY;
      assertThat(testObject.toDecimalString(5, 10), is("∞"));
   }

   /**
    * Test for {@link MutableInfiniteRational#toDecimalString(int, int)}
    */
   @Test
   public void toDecimalString_returnsInfinitySymbol_givenNegativeInfinity()
   {
      testObject = MutableInfiniteRational.NEGATIVE_INFINITY;
      assertThat(testObject.toDecimalString(5, 10), is("-∞"));
   }

   /**
    * Test for {@link MutableInfiniteRational#toDecimalString(int, int)}
    */
   @Test
   public void toDecimalString_returnsNotRationalSymbols_givenNan()
   {
      testObject = MutableInfiniteRational.NaN;
      assertThat(testObject.toDecimalString(5, 10), is("∉ℚ"));
   }

   /**
    * Test for {@link MutableInfiniteRational#toDecimalString(int, int)}
    */
   @Test
   public void toDecimalString_throws_givenRadixOneAndNonWhole()
   {
      testObject = MutableInfiniteRational.valueOf(1, 2);
      try
      {
         testObject.toDecimalString(5, 1);
         fail("Should've thrown");
      }
      catch (final IllegalArgumentException actual)
      {
         assertThat(actual.getMessage(), is("Base 1 doesn't support decimal representations. This: 1/2"));
      }
   }

   /**
    * Test for {@link MutableInfiniteRational#toDecimalString(int, int)}
    */
   @Test
   public void toDecimalString_throws_givenInvalidDecimalPlaces()
   {
      testObject = MutableInfiniteRational.valueOf(1, 2);
      try
      {
         testObject.toDecimalString(-5, 10);
         fail("Should've thrown");
      }
      catch (final IllegalArgumentException actual)
      {
         assertThat(actual.getMessage(), is("decimalPlaces must be at least 0 but got -5. This: 1/2"));
      }
   }

   /**
    * Test for {@link MutableInfiniteRational#toDecimalString(int, int)}
    */
   @Test
   public void toDecimalString_returnsNoDecimalPoint_givenZeroDecimalPlacesNonWhole()
   {
      testObject = MutableInfiniteRational.valueOf(5.75);
      assertThat(testObject.toDecimalString(0, 10), is("5"));
   }

   /**
    * Test for {@link MutableInfiniteRational#toDecimalString(int, int)}
    */
   @Test
   public void toDecimalString_returnsTrailingZeroes_givenUnreducedWhole()
   {
      testObject = MutableInfiniteRational.valueOf(10, 2);
      assertThat(testObject.toDecimalString(5, 10), is("5.00000"));
   }

   /**
    * Test for {@link MutableInfiniteRational#toDecimalString(int, int)}
    */
   @Test
   public void toDecimalString_returns_givenNegativeUnreducedWhole()
   {
      testObject = MutableInfiniteRational.valueOf(-10, 2);
      assertThat(testObject.toDecimalString(2, 10), is("-5.00"));
   }

   /**
    * Test for {@link MutableInfiniteRational#toDecimalString(int, int)}
    */
   @Test
   public void toDecimalString_usesRadix_givenRadix()
   {
      testObject = MutableInfiniteRational.valueOf(5, 7);
      assertThat(testObject.toDecimalString(2, 7), is("0.50"));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#toDecimalStringExact()}
    */
   @Test
   public void toDecimalStringExact_returns_notGivenRadix()
   {
      testObject = MutableInfiniteRational.valueOf(1, 2);
      assertThat(testObject.toDecimalStringExact(), is("0.5"));
   }

   /**
    * Test for {@link MutableInfiniteRational#toDecimalStringExact(int)}
    */
   @Test
   public void toDecimalStringExact_returnsInfinitySymbol_givenPositiveInfinity()
   {
      testObject = MutableInfiniteRational.POSITIVE_INFINITY;
      assertThat(testObject.toDecimalStringExact(10), is("∞"));
   }

   /**
    * Test for {@link MutableInfiniteRational#toDecimalStringExact(int)}
    */
   @Test
   public void toDecimalStringExact_returnsInfinitySymbol_givenNegativeInfinity()
   {
      testObject = MutableInfiniteRational.NEGATIVE_INFINITY;
      assertThat(testObject.toDecimalStringExact(10), is("-∞"));
   }

   /**
    * Test for {@link MutableInfiniteRational#toDecimalStringExact(int)}
    */
   @Test
   public void toDecimalStringExact_returnsNotRationalSymbols_givenNan()
   {
      testObject = MutableInfiniteRational.NaN;
      assertThat(testObject.toDecimalStringExact(10), is("∉ℚ"));
   }

   /**
    * Test for {@link MutableInfiniteRational#toDecimalStringExact(int)}
    */
   @Test
   public void toDecimalStringExact_throws_givenRadixOneAndNonWhole()
   {
      testObject = MutableInfiniteRational.valueOf(1, 2);
      try
      {
         testObject.toDecimalStringExact(1);
         fail("Should've thrown");
      }
      catch (final IllegalArgumentException actual)
      {
         assertThat(actual.getMessage(), is("Base 1 doesn't support decimal representations. This: 1/2"));
      }
   }

   /**
    * Test for {@link MutableInfiniteRational#toDecimalStringExact(int)}
    */
   @Test
   public void toDecimalStringExact_returns_givenReducedWhole()
   {
      testObject = MutableInfiniteRational.valueOf(2);
      assertThat(testObject.toDecimalStringExact(10), is("2"));
   }

   /**
    * Test for {@link MutableInfiniteRational#toDecimalStringExact(int)}
    */
   @Test
   public void toDecimalStringExact_returns_givenUnreducedWhole()
   {
      testObject = MutableInfiniteRational.valueOf(10, 2);
      assertThat(testObject.toDecimalStringExact(10), is("5"));
   }

   /**
    * Test for {@link MutableInfiniteRational#toDecimalStringExact(int)}
    */
   @Test
   public void toDecimalStringExact_returns_givenNegative()
   {
      testObject = MutableInfiniteRational.valueOf(-1, 2);
      assertThat(testObject.toDecimalStringExact(10), is("-0.5"));
   }

   /**
    * Test for {@link MutableInfiniteRational#toDecimalStringExact(int)}
    */
   @Test
   public void toDecimalStringExact_usesRadix_givenRadix()
   {
      testObject = MutableInfiniteRational.valueOf(5, 7);
      assertThat(testObject.toDecimalStringExact(7), is("0.5"));
   }

   /**
    * Test for {@link MutableInfiniteRational#toDecimalStringExact(int)}
    */
   @Test
   public void toDecimalStringExact_detectsRepeat_whenRepeats()
   {
      testObject = MutableInfiniteRational.valueOf(1, 3);
      assertThat(testObject.toDecimalStringExact(10), is("0._3…"));
      testObject = MutableInfiniteRational.valueOf(7, 12);
      assertThat(testObject.toDecimalStringExact(10), is("0.58_3…"));
/*
7/12=
  0.58_3…
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
      40 repeat
*/
   }

   /**
    * Test for {@link MutableInfiniteRational#toDecimalStringExact(int)}
    */
   @Test
   public void toDecimalStringExact_detectsRepeatOfMultipleDigits_whenRepeats()
   {
      testObject = MutableInfiniteRational.valueOf(3227, 555);
      assertThat(testObject.toDecimalStringExact(10), is("5.8_144…"));
   }

   @Test
   public void copy()
   {
      testObject = MutableInfiniteRational.valueOf(2);
      final MutableInfiniteRational actual = testObject.copy();
      assertThat(actual, is(equalTo(testObject)));
      assertThat(actual, is(not(sameInstance(testObject))));
   }

   @Test
   public void copy_returnsSameInstance_whenNotFinite()
   {
      for (final MutableInfiniteRational constant : constantList)
      {
         testObject = constant;
         assertThat(testObject.copy(), is(sameInstance(testObject)));
      }
   }

   @Test
   public void set()
   {
      testObject = MutableInfiniteRational.valueOf(2);
      final MutableInfiniteRational input = MutableInfiniteRational.valueOf(5);

      assertThat(testObject.set(input), is(testObject));
      input.multiply(MutableInfiniteRational.valueOf(2, 2));  //prove defensive copy

      assertThat(testObject, is(MutableInfiniteRational.valueOf(5)));
   }

   @Test
   public void set_doesNothing_whenNotFinite()
   {
      for (final MutableInfiniteRational constant : constantList)
      {
         testObject = constant;
         final MutableInfiniteRational input = MutableInfiniteRational.valueOf(5);
         assertThat(testObject.set(input), is(constant));
         assertThat(testObject, is(constant));
      }
   }

   @Test
   public void set_doesNothing_givenNotFinite()
   {
      for (final MutableInfiniteRational constant : constantList)
      {
         testObject = MutableInfiniteRational.valueOf(5);
         assertThat(testObject.set(constant), is(constant));
         assertThat(testObject, is(testObject));
      }
   }

   /**
    * Happy path for {@link MutableInfiniteRational#setNumerator(long)}
    */
   @Test
   public void setNumerator_setsNumerator_givenLong()
   {
      testObject = MutableInfiniteRational.valueOf(2);
      assertThat(testObject.setNumerator(5), is(testObject));
      assertThat(testObject, is(MutableInfiniteRational.valueOf(5)));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#setNumerator(BigInteger)}
    */
   @Test
   public void setNumerator_setsNumerator_givenBigInteger()
   {
      testObject = MutableInfiniteRational.valueOf(2);
      assertThat(testObject.setNumerator(BigInteger.valueOf(4)), is(testObject));
      assertThat(testObject, is(MutableInfiniteRational.valueOf(4)));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#setNumerator(InfiniteInteger)}
    */
   @Test
   public void setNumerator_setsNumerator_givenInfiniteInteger()
   {
      testObject = MutableInfiniteRational.valueOf(2);
      assertThat(testObject.setNumerator(InfiniteInteger.valueOf(4)), is(testObject));
      assertThat(testObject, is(MutableInfiniteRational.valueOf(4)));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#setNumerator(MutableInfiniteInteger)}
    */
   @Test
   public void setNumerator_setsNumerator_givenMutableInfiniteInteger()
   {
      testObject = MutableInfiniteRational.valueOf(2);
      final MutableInfiniteInteger input = MutableInfiniteInteger.valueOf(4);
      assertThat(testObject.setNumerator(input), is(testObject));
      input.multiply(5);  //prove defensive copy
      assertThat(testObject, is(MutableInfiniteRational.valueOf(4)));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#getNumerator()}
    */
   @Test
   public void getNumerator()
   {
      testObject = MutableInfiniteRational.valueOf(2);

      final MutableInfiniteInteger actual = testObject.getNumerator();

      assertThat(actual, is(MutableInfiniteInteger.valueOf(2)));
      actual.multiply(5);  //prove defensive copy
      assertThat(testObject, is(MutableInfiniteRational.valueOf(2)));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#setDenominator(long)}
    */
   @Test
   public void setDenominator_setsDenominator_givenLong()
   {
      testObject = MutableInfiniteRational.valueOf(2);
      assertThat(testObject.setDenominator(5), is(testObject));
      assertThat(testObject, is(MutableInfiniteRational.valueOf(2, 5)));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#setDenominator(BigInteger)}
    */
   @Test
   public void setDenominator_setsDenominator_givenBigInteger()
   {
      testObject = MutableInfiniteRational.valueOf(2);
      assertThat(testObject.setDenominator(BigInteger.valueOf(4)), is(testObject));
      assertThat(testObject, is(MutableInfiniteRational.valueOf(2, 4)));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#setDenominator(InfiniteInteger)}
    */
   @Test
   public void setDenominator_setsDenominator_givenInfiniteInteger()
   {
      testObject = MutableInfiniteRational.valueOf(2);
      assertThat(testObject.setDenominator(InfiniteInteger.valueOf(4)), is(testObject));
      assertThat(testObject, is(MutableInfiniteRational.valueOf(2, 4)));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#setDenominator(MutableInfiniteInteger)}
    */
   @Test
   public void setDenominator_setsDenominator_givenMutableInfiniteInteger()
   {
      testObject = MutableInfiniteRational.valueOf(2);
      final MutableInfiniteInteger input = MutableInfiniteInteger.valueOf(4);
      assertThat(testObject.setDenominator(input), is(testObject));
      input.multiply(5);  //prove defensive copy
      assertThat(testObject, is(MutableInfiniteRational.valueOf(2, 4)));
   }

   /**
    * Test for {@link MutableInfiniteRational#setDenominator(MutableInfiniteInteger)}
    */
   @Test
   public void setDenominator_returnsNan_givenZero()
   {
      testObject = MutableInfiniteRational.valueOf(2);
      assertThat(testObject.setDenominator(MutableInfiniteInteger.valueOf(0)), is(MutableInfiniteRational.NaN));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#getDenominator()}
    */
   @Test
   public void getDenominator()
   {
      testObject = MutableInfiniteRational.valueOf(1.5);

      final MutableInfiniteInteger actual = testObject.getDenominator();

      assertThat(actual, is(MutableInfiniteInteger.valueOf(10)));
      actual.multiply(5);  //prove defensive copy
      assertThat(testObject, is(MutableInfiniteRational.valueOf(1.5)));
   }

   @Test
   public void staticSerializableIt_finite() throws IOException
   {
      final File tempFile = File.createTempFile("MutableInfiniteRational_UT.TempFile.staticSerializableIt_finite.", ".txt");
      tempFile.deleteOnExit();

      final ObjectStreamWriter writer = new ObjectStreamWriter(tempFile);
      writer.writeObject(MutableInfiniteRational.valueOf(5));
      writer.writeObject(MutableInfiniteRational.valueOf(-5, 3));
      writer.writeObject(MutableInfiniteRational.valueOf(0, 3));
      writer.close();

      final ObjectStreamReader reader = new ObjectStreamReader(tempFile);
      assertThat(reader.readObject(MutableInfiniteRational.class), Matchers.is(MutableInfiniteRational.valueOf(5)));
      assertThat(reader.readObject(MutableInfiniteRational.class), Matchers.is(MutableInfiniteRational.valueOf(-5, 3)));
      //Zero auto reduces
      assertThat(reader.readObject(MutableInfiniteRational.class), Matchers.is(MutableInfiniteRational.valueOf(0, 1)));
      reader.close();
   }

   @Test
   public void staticSerializableIt_NonFinite() throws IOException
   {
      final File tempFile = File.createTempFile("MutableInfiniteRational_UT.TempFile.staticSerializableIt_NonFinite.", ".txt");
      tempFile.deleteOnExit();

      final ObjectStreamWriter writer = new ObjectStreamWriter(tempFile);
      constantList.forEach(writer::writeObject);
      writer.close();

      final ObjectStreamReader reader = new ObjectStreamReader(tempFile);
      constantList.forEach(constant -> assertThat(reader.readObject(MutableInfiniteRational.class), Matchers.is(constant)));
      reader.close();
   }
}
