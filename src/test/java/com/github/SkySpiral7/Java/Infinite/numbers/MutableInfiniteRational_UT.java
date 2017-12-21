package com.github.SkySpiral7.Java.Infinite.numbers;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
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
      MutableInfiniteRational other = MutableInfiniteRational.valueOf(2, 3);
      assertThat(testObject, is(lessThan(other)));
      assertThat(other, is(greaterThan(testObject)));
   }

   @Test
   public void compareTo_compares_whenBothAreNegative() throws Exception
   {
      testObject = MutableInfiniteRational.valueOf(-1, 3);
      MutableInfiniteRational other = MutableInfiniteRational.valueOf(-2, 3);
      assertThat(testObject, is(greaterThan(other)));
      assertThat(other, is(lessThan(testObject)));
   }

   @Test
   public void compareTo_usesLcm_whenDifferentDenominator() throws Exception
   {
      testObject = MutableInfiniteRational.valueOf(1, 3);  //== 4/12
      MutableInfiniteRational other = MutableInfiniteRational.valueOf(1, 4);  //== 3/12
      assertThat(testObject, is(greaterThan(other)));
      assertThat(other, is(lessThan(testObject)));
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
