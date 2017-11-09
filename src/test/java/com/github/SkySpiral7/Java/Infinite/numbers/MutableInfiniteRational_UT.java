package com.github.SkySpiral7.Java.Infinite.numbers;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
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
      assertThat(actual.toImproperFractionalString(10), is("3/2"));
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
      assertThat(actual.toImproperFractionalString(10), is("1"));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#valueOf(long, long)}
    */
   @Test
   public void valueOf_returnsValue_givenLongLong() throws Exception
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(1, 2);
      assertThat(actual.toImproperFractionalString(10), is("1/2"));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#valueOf(BigInteger, BigInteger)}
    */
   @Test
   public void valueOf_returnsValue_givenBigIntegerBigInteger() throws Exception
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(BigInteger.ONE, BigInteger.TEN);
      assertThat(actual.toImproperFractionalString(10), is("1/10"));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#valueOf(MutableInfiniteInteger, MutableInfiniteInteger)}
    */
   @Test
   public void valueOf_returnsValue_givenMutableInfiniteIntegerMutableInfiniteInteger() throws Exception
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(MutableInfiniteInteger.valueOf(1),
            MutableInfiniteInteger.valueOf(10));
      assertThat(actual.toImproperFractionalString(10), is("1/10"));
   }

   /**
    * Happy path for {@link MutableInfiniteRational#valueOf(InfiniteInteger, InfiniteInteger)}
    */
   @Test
   public void valueOf_returnsValue_givenInfiniteIntegerInfiniteInteger() throws Exception
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(InfiniteInteger.valueOf(1), InfiniteInteger.valueOf(10));
      assertThat(actual.toImproperFractionalString(10), is("1/10"));
   }

   /**
    * Test for {@link MutableInfiniteRational#valueOf(InfiniteInteger, InfiniteInteger)}
    * NaN / X == NaN
    */
   @Test
   public void valueOf_returnsNan_givenInfiniteIntegerNanNumerator() throws Exception
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(InfiniteInteger.NaN, InfiniteInteger.valueOf(10));
      assertThat(actual, is(sameInstance(MutableInfiniteRational.NaN)));
   }

   /**
    * Test for {@link MutableInfiniteRational#valueOf(InfiniteInteger, InfiniteInteger)}
    * X / NaN == NaN
    */
   @Test
   public void valueOf_returnsNan_givenInfiniteIntegerNanDenominator() throws Exception
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(InfiniteInteger.ONE, InfiniteInteger.NaN);
      assertThat(actual, is(sameInstance(MutableInfiniteRational.NaN)));
   }

   /**
    * Test for {@link MutableInfiniteRational#valueOf(InfiniteInteger, InfiniteInteger)}
    * Infinity / Infinity == NaN
    */
   @Test
   public void valueOf_returnsNan_givenInfiniteIntegerInfinityDividedByInfinity() throws Exception
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(InfiniteInteger.POSITIVE_INFINITY,
            InfiniteInteger.NEGATIVE_INFINITY);
      assertThat(actual, is(sameInstance(MutableInfiniteRational.NaN)));
   }

   /**
    * Test for {@link MutableInfiniteRational#valueOf(InfiniteInteger, InfiniteInteger)}
    * X / 0 == NaN
    */
   @Test
   public void valueOf_returnsNan_givenInfiniteIntegerFiniteDividedByZero() throws Exception
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(InfiniteInteger.TWO, InfiniteInteger.ZERO);
      assertThat(actual, is(sameInstance(MutableInfiniteRational.NaN)));
   }

   /**
    * Test for {@link MutableInfiniteRational#valueOf(InfiniteInteger, InfiniteInteger)}
    * Infinity / 0 == NaN
    */
   @Test
   public void valueOf_returnsNan_givenInfiniteIntegerInfiniteDividedByZero() throws Exception
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(InfiniteInteger.POSITIVE_INFINITY, InfiniteInteger.ZERO);
      assertThat(actual, is(sameInstance(MutableInfiniteRational.NaN)));
   }

   /**
    * Test for {@link MutableInfiniteRational#valueOf(InfiniteInteger, InfiniteInteger)}
    * Infinity / X == Infinity
    */
   @Test
   public void valueOf_returnsPositiveInfinity_givenInfiniteIntegerPositiveInfinityDividedByFinite() throws Exception
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(InfiniteInteger.POSITIVE_INFINITY, InfiniteInteger.TWO);
      assertThat(actual, is(sameInstance(MutableInfiniteRational.POSITIVE_INFINITY)));
   }

   /**
    * Test for {@link MutableInfiniteRational#valueOf(InfiniteInteger, InfiniteInteger)}
    * -Infinity / X == -Infinity
    */
   @Test
   public void valueOf_returnsNegativeInfinity_givenInfiniteIntegerNegativeInfinityDividedByFinite() throws Exception
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(InfiniteInteger.NEGATIVE_INFINITY, InfiniteInteger.TWO);
      assertThat(actual, is(sameInstance(MutableInfiniteRational.NEGATIVE_INFINITY)));
   }

   /**
    * Test for {@link MutableInfiniteRational#valueOf(InfiniteInteger, InfiniteInteger)}
    * X / Infinity == 0
    */
   @Test
   public void valueOf_returnsZero_givenInfiniteIntegerFiniteDividedByInfinity() throws Exception
   {
      final MutableInfiniteRational actual = MutableInfiniteRational.valueOf(InfiniteInteger.TWO, InfiniteInteger.NEGATIVE_INFINITY);
      assertThat(actual.toString(), is("0"));
   }

   @Test
   public void intValue() throws Exception
   {
      int numerator = 10;
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

   @Test
   public void copy() throws Exception
   {
   }
}
