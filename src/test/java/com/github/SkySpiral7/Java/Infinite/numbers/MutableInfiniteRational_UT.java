package com.github.SkySpiral7.Java.Infinite.numbers;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class MutableInfiniteRational_UT
{
   private MutableInfiniteRational testObject;

   /**
    * Happy path for {@link MutableInfiniteRational#valueOf(double)}
    */
   @Test
   @Ignore
   public void valueOf_returnsValue_givenDouble() throws Exception
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(1.5d);
      assertThat(actual.toImproperFractionalString(), is("3/2"));
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
   @Ignore
   public void valueOf_returnsValue_givenBigDecimal() throws Exception
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(BigDecimal.ONE);
      assertThat(actual.toImproperFractionalString(), is("1"));
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
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(MutableInfiniteInteger.NaN, MutableInfiniteInteger.valueOf(10));
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
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(MutableInfiniteInteger.valueOf(2), MutableInfiniteInteger
            .valueOf(0));
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
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(MutableInfiniteInteger.valueOf(2), MutableInfiniteInteger
            .NEGATIVE_INFINITY);
      assertThat(actual.toString(), is("0"));
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
   public void longValue_throws_whenNonFinite() throws Exception
   {
      try
      {
         MutableInfiniteRational.POSITIVE_INFINITY.longValue();
      }
      catch (ArithmeticException actual)
      {
         assertEquals("+Infinity can't be even partially represented as a long.", actual.getMessage());
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
   public void compareTo() throws Exception
   {
   }

   @Test
   public void equals() throws Exception
   {
   }

   @Test
   public void hashCode1() throws Exception
   {
   }

   @Test
   public void toString1() throws Exception
   {
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
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(10*3+1, 3);
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
    * to confirm a fast path.
    */
   @Test
   public void toMixedFactionalString_returnsOne_givenOne() throws Exception
   {
      testObject = MutableInfiniteRational.valueOf(4, 4);
      assertThat(testObject.toMixedFactionalString(8), is("1"));
   }

   @Test
   public void copy() throws Exception
   {
      testObject = MutableInfiniteRational.valueOf(2);
      final MutableInfiniteRational actual = testObject.copy();
      assertThat(actual, is(equalTo(testObject)));
      assertThat(actual, is(not(sameInstance(testObject))));
   }
}
