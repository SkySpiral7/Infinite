package com.github.skySpiral7.java.infinite.numbers;

import java.math.BigInteger;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.ListIterator;

import com.github.skySpiral7.java.iterators.JumpingIterator;
import com.github.skySpiral7.java.pojo.IntegerQuotient;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Methods that simply delegate do not need a test.
 * Additionally the following do not need a test:
 * the other versions of littleEndian and bigEndian, magnitude Iterator and Stream,
 * getMagnitudeTail, selfPower, factorial, abs, negate, signum, isNaN, isInfinite, isFinite, signalNaN,
 * the other versions of equalValue, hashCode, copy, toFile (but toString should be tested when finished),
 * calculateMaxBigInteger (too slow), calculateGoogolplex (lol slow and nothing to test)
 */
public class MutableInfiniteInteger_UT
{
   private MutableInfiniteInteger testObject;
   //TODO: make tests to ensure that this is mutated but not param
   //TODO: better test coverage

   @Test
   public void valueOf_returnsMutableInfiniteInteger_givenLong()
   {
      //simple case
      assertEqualNodes(MutableInfiniteInteger.valueOf(-5), -1, 5);

      //more than max int
      assertEqualNodes(MutableInfiniteInteger.valueOf(8_589_934_592L), 1, 0, 2);

      //prove that each node is unsigned
      assertEqualNodes(MutableInfiniteInteger.valueOf(2_147_483_648L), 1, (int) 2_147_483_648L);

      //special case: can't use Math.abs
      assertEqualNodes(MutableInfiniteInteger.valueOf(Long.MIN_VALUE), -1, 0, Integer.MIN_VALUE);
   }

   @Test
   public void valueOf_returnsMutableInfiniteInteger_givenBigInteger()
   {
      assertEquals(MutableInfiniteInteger.valueOf(0), MutableInfiniteInteger.valueOf(BigInteger.valueOf(0)));
      assertEquals(MutableInfiniteInteger.valueOf(5), MutableInfiniteInteger.valueOf(BigInteger.valueOf(5)));
      assertEquals(MutableInfiniteInteger.valueOf(-5), MutableInfiniteInteger.valueOf(BigInteger.valueOf(-5)));
      assertEquals(MutableInfiniteInteger.valueOf(Long.MIN_VALUE), MutableInfiniteInteger.valueOf(BigInteger.valueOf(Long.MIN_VALUE)));
      assertEquals(MutableInfiniteInteger.valueOf(Long.MAX_VALUE - 5),
            MutableInfiniteInteger.valueOf(BigInteger.valueOf(Long.MAX_VALUE - 5)));

      testObject = MutableInfiniteInteger.valueOf(Long.MAX_VALUE).add(Long.MAX_VALUE).add(152).negate();
      final BigInteger input = BigInteger.valueOf(Long.MAX_VALUE)
                                         .add(BigInteger.valueOf(Long.MAX_VALUE))
                                         .add(BigInteger.valueOf(152))
                                         .negate();
      assertEquals(testObject, MutableInfiniteInteger.valueOf(input));
   }

   @Test
   public void valueOf_returnsMutableInfiniteInteger_givenInfiniteInteger()
   {
      final MutableInfiniteInteger actual = MutableInfiniteInteger.valueOf(InfiniteInteger.ONE);
      assertThat(actual, is(MutableInfiniteInteger.valueOf(1)));
   }

   @Test
   public void valueOf_returnsCopy_givenMutableInfiniteInteger()
   {
      testObject = MutableInfiniteInteger.valueOf(2);
      final MutableInfiniteInteger actual = MutableInfiniteInteger.valueOf(testObject);
      assertThat(actual, is(equalTo(testObject)));
      assertThat(actual, is(not(sameInstance(testObject))));
   }

   @Test
   public void toInfiniteInteger()
   {
      final InfiniteInteger actual = MutableInfiniteInteger.valueOf(2).toInfiniteInteger();
      assertTrue(actual.equalValue(2));
   }

   @Test
   public void littleEndian()
   {
      assertEquals(MutableInfiniteInteger.valueOf(0), MutableInfiniteInteger.littleEndian(Collections.emptyIterator(), true));
      Iterator<Long> input = Arrays.asList(0L, 0L, 0L, 0L, 0L).iterator();
      assertEquals(MutableInfiniteInteger.valueOf(0), MutableInfiniteInteger.littleEndian(input, true));
      input = Arrays.asList(1L, 1L, Long.MAX_VALUE, 0L).iterator();
      assertEqualNodes(MutableInfiniteInteger.littleEndian(input, true), -1, 1, 0, 1, 0, -1, Integer.MAX_VALUE);
   }

   @Test
   public void streamAllIntegers()
   {
      final Iterator<MutableInfiniteInteger> integerIterator = MutableInfiniteInteger.streamAllIntegers().iterator();
      assertEquals(MutableInfiniteInteger.valueOf(0), integerIterator.next());
      assertEquals(MutableInfiniteInteger.valueOf(1), integerIterator.next());
      assertEquals(MutableInfiniteInteger.valueOf(-1), integerIterator.next());
      assertEquals(MutableInfiniteInteger.valueOf(2), integerIterator.next());
      assertEquals(MutableInfiniteInteger.valueOf(-2), integerIterator.next());
      assertEquals(MutableInfiniteInteger.valueOf(3), integerIterator.next());
      assertEquals(MutableInfiniteInteger.valueOf(-3), integerIterator.next());
      assertEquals(MutableInfiniteInteger.valueOf(4), integerIterator.next());
      assertEquals(MutableInfiniteInteger.valueOf(-4), integerIterator.next());
      assertEquals(MutableInfiniteInteger.valueOf(5), integerIterator.next());
      assertEquals(MutableInfiniteInteger.valueOf(-5), integerIterator.next());
      assertTrue(integerIterator.hasNext());  //continues forever
   }

   @Test
   public void iterateAllIntegers()
   {
      final ListIterator<MutableInfiniteInteger> integerIterator = MutableInfiniteInteger.iterateAllIntegers();
      assertEquals(MutableInfiniteInteger.valueOf(0), integerIterator.previous());
      integerIterator.next();
      assertEquals(MutableInfiniteInteger.valueOf(1), integerIterator.next());
      assertEquals(MutableInfiniteInteger.valueOf(2), integerIterator.next());
      assertEquals(MutableInfiniteInteger.valueOf(3), integerIterator.next());
      assertTrue(integerIterator.hasNext());  //continues forever

      assertEquals(4, integerIterator.nextIndex());
      JumpingIterator.jumpByIndex(integerIterator, -4);
      assertEquals(0, integerIterator.nextIndex());
      assertEquals(-1, integerIterator.previous().intValue());
      assertEquals(-2, integerIterator.previous().intValue());
      assertEquals(-3, integerIterator.previous().intValue());
      assertTrue(integerIterator.hasPrevious());  //continues forever
   }

   @Test
   public void streamFibonacciSequence()
   {
      final Iterator<MutableInfiniteInteger> integerIterator = MutableInfiniteInteger.streamFibonacciSequence().iterator();
      assertEquals(MutableInfiniteInteger.valueOf(0), integerIterator.next());
      assertEquals(MutableInfiniteInteger.valueOf(1), integerIterator.next());
      assertEquals(MutableInfiniteInteger.valueOf(1), integerIterator.next());
      assertEquals(MutableInfiniteInteger.valueOf(2), integerIterator.next());
      assertEquals(MutableInfiniteInteger.valueOf(3), integerIterator.next());
      assertEquals(MutableInfiniteInteger.valueOf(5), integerIterator.next());
      assertEquals(MutableInfiniteInteger.valueOf(8), integerIterator.next());
      assertEquals(MutableInfiniteInteger.valueOf(13), integerIterator.next());
      assertEquals(MutableInfiniteInteger.valueOf(21), integerIterator.next());
      assertEquals(MutableInfiniteInteger.valueOf(34), integerIterator.next());
      assertEquals(MutableInfiniteInteger.valueOf(55), integerIterator.next());
      assertTrue(integerIterator.hasNext());  //continues forever
   }

   @Test
   public void intValue()
   {
      assertEquals(5, MutableInfiniteInteger.valueOf(5).intValue());
      assertEquals(Integer.MAX_VALUE, MutableInfiniteInteger.valueOf(Integer.MAX_VALUE).intValue());
      testObject = MutableInfiniteInteger.valueOf(Integer.MAX_VALUE).add(Integer.MAX_VALUE).add(1);
      assertEquals(Integer.MAX_VALUE, testObject.intValue());

      assertEquals(-1, MutableInfiniteInteger.valueOf(-1).intValue());
      assertEquals(-Integer.MAX_VALUE, MutableInfiniteInteger.valueOf(-Integer.MAX_VALUE).intValue());
      testObject = MutableInfiniteInteger.valueOf(Integer.MAX_VALUE).add(Integer.MAX_VALUE).add(1).negate();
      assertEquals(-Integer.MAX_VALUE, testObject.intValue());
   }

   @Test
   public void longValue()
   {
      assertEquals(5, MutableInfiniteInteger.valueOf(5).longValue());
      assertEquals(Long.MAX_VALUE, MutableInfiniteInteger.valueOf(Long.MAX_VALUE).longValue());
      testObject = MutableInfiniteInteger.valueOf(Long.MAX_VALUE).add(Long.MAX_VALUE).add(1);
      assertEquals(Long.MAX_VALUE, testObject.longValue());

      assertEquals(-1, MutableInfiniteInteger.valueOf(-1).longValue());
      assertEquals(-Long.MAX_VALUE, MutableInfiniteInteger.valueOf(-Long.MAX_VALUE).longValue());
      testObject = MutableInfiniteInteger.valueOf(Long.MAX_VALUE).add(Long.MAX_VALUE).add(1).negate();
      assertEquals(-Long.MAX_VALUE, testObject.longValue());
   }

   @Test
   public void longValueExact()
   {
      testObject = MutableInfiniteInteger.valueOf(Long.MAX_VALUE);
      assertEquals(Long.MAX_VALUE, testObject.longValueExact());

      testObject = testObject.add(1);
      try
      {
         testObject.longValueExact();
         fail("Did not throw when > signed long.");
      }
      catch (final ArithmeticException actual)
      {
         assertEquals("9223372036854775808 is too large to be represented as a signed long.", actual.getMessage());
      }

      try
      {
         testObject.add(Long.MAX_VALUE).add(1).longValueExact();
         fail("Did not throw when > unsigned long.");
      }
      catch (final ArithmeticException actual)
      {
         assertEquals("18446744073709551616 is too large to be represented as a long.", actual.getMessage());
      }
   }

   @Test
   @Ignore  //ignored because the code doesn't exist yet and is WAY too slow
   public void bigIntegerValue()
   {
      final BigInteger bigIntMaxValueBig = MutableInfiniteInteger.calculateMaxBigInteger();
      final MutableInfiniteInteger bigIntMaxValueInf = MutableInfiniteInteger.calculateMaxBigIntegerAsInfiniteInteger();
      final BigInteger negativeBigIntMaxValueBig = bigIntMaxValueBig.negate();
      final MutableInfiniteInteger negativeBigIntMaxValueInf = bigIntMaxValueInf.negate();

      assertEquals(BigInteger.valueOf(5), MutableInfiniteInteger.valueOf(5).bigIntegerValue());
      assertEquals(bigIntMaxValueBig, bigIntMaxValueInf.bigIntegerValue());
      testObject = bigIntMaxValueInf.multiplyByPowerOf2(2).add(3);
      assertEquals(bigIntMaxValueBig, testObject.bigIntegerValue());

      assertEquals(BigInteger.valueOf(-1), MutableInfiniteInteger.valueOf(-1).bigIntegerValue());
      assertEquals(negativeBigIntMaxValueBig, negativeBigIntMaxValueInf.bigIntegerValue());
      testObject = negativeBigIntMaxValueInf.multiplyByPowerOf2(2).subtract(3);
      assertEquals(negativeBigIntMaxValueBig, testObject.bigIntegerValue());
   }

   @Test
   @Ignore  //ignored because WAY too slow
   public void bigIntegerValueExact()
   {
      testObject = MutableInfiniteInteger.calculateMaxBigIntegerAsInfiniteInteger();
      assertEquals(MutableInfiniteInteger.calculateMaxBigInteger(), testObject.bigIntegerValueExact());  //let throw on test fail

      try
      {
         testObject.add(1).bigIntegerValueExact();
         fail("Did not throw when > BigInteger.");
      }
      catch (final ArithmeticException actual)
      {
         assertEquals("", actual.getMessage());
      }
   }

   @Test
   public void add_returns_whenBothPositive()
   {
      assertEqualNodes(MutableInfiniteInteger.valueOf(5).add(MutableInfiniteInteger.valueOf(5)), 1, 10);
   }

   @Test
   public void add_returns_whenBothNegative()
   {
      assertEqualNodes(MutableInfiniteInteger.valueOf(-5).add(MutableInfiniteInteger.valueOf(-5)), -1, 10);
   }

   @Test
   public void add_returns_whenMoreThanMaxInt()
   {
      assertEqualNodes(MutableInfiniteInteger.valueOf(8_589_934_592L).add(MutableInfiniteInteger.valueOf(5)), 1, 5, 2);
   }

   @Test
   public void add_returns_whenMoreThanMaxLong()
   {
      testObject = MutableInfiniteInteger.valueOf(Long.MAX_VALUE)
                                         .add(MutableInfiniteInteger.valueOf(Long.MAX_VALUE))
                                         .add(MutableInfiniteInteger.valueOf(2));
      assertEqualNodes(testObject, 1, 0, 0, 1);
      //0x7fffffffffffffff + 0x7fffffffffffffff + 0x2 = 0x8000000000000000 + 0x8000000000000000
   }

   @Test
   public void add_returnsNan_whenNan()
   {
      assertSame(MutableInfiniteInteger.NaN, MutableInfiniteInteger.NaN.add(12));
      assertSame(MutableInfiniteInteger.NaN, MutableInfiniteInteger.valueOf(-12).add(MutableInfiniteInteger.NaN));
   }

   @Test
   public void add_returnsPositiveInfinity_whenPositiveInfinity()
   {
      assertSame(MutableInfiniteInteger.POSITIVE_INFINITY, MutableInfiniteInteger.POSITIVE_INFINITY.add(12));
      assertSame(MutableInfiniteInteger.POSITIVE_INFINITY,
            MutableInfiniteInteger.valueOf(12).add(MutableInfiniteInteger.POSITIVE_INFINITY));
   }

   @Test
   public void add_returnsNegativeInfinity_whenNegativeInfinity()
   {
      assertSame(MutableInfiniteInteger.NEGATIVE_INFINITY, MutableInfiniteInteger.NEGATIVE_INFINITY.add(-12));
      assertSame(MutableInfiniteInteger.NEGATIVE_INFINITY,
            MutableInfiniteInteger.valueOf(-12).add(MutableInfiniteInteger.NEGATIVE_INFINITY));
   }

   @Test
   public void add_returnsOther_whenZero()
   {
      testObject = MutableInfiniteInteger.valueOf(12);
      assertSame(testObject, testObject.add(0));
      assertSame(testObject, testObject.add(MutableInfiniteInteger.valueOf(0)));
   }

   /**
    * Previous bug.
    */
   @Test
   public void add_onlyRemovesLeadingZeros_whenMiddleZeroes() throws Exception
   {
      testObject = MutableInfiniteInteger.valueOf(1);
      assertEqualNodes(testObject, 1, 1);
      testObject = testObject.multiplyByPowerOf2(65);
      assertEqualNodes(testObject, 1, 0, 0, 2);
      testObject = testObject.add(1);
      assertEqualNodes(testObject, 1, 1, 0, 2);
      //00000002 00000000 00000001
      assertThat(testObject.toString(16), is("20000000000000001"));
      //00000000000000000000000000000010 00000000000000000000000000000000 00000000000000000000000000000001
      assertThat(testObject.toString(2), is("100000000000000000000000000000000000000000000000000000000000000001"));

      testObject = testObject.multiplyByPowerOf2(70);
      // 00000000000000000000000010000000
      // 00000000000000000000000000000000
      // 00000000000000000000000001000000
      // 00000000000000000000000000000000
      // 00000000000000000000000000000000
      assertEqualNodes(testObject, 1, 0, 0, 0b00000000000000000000000001000000, 0, 0b00000000000000000000000010000000);
      // 00000080 00000000 00000040 00000000 00000000
      assertThat(testObject.toString(16), is("8000000000000000400000000000000000"));
      assertThat(testObject.toString(2),
            is("1000000000000000000000000000000000000000000000000000000000000000010000000000000000000000000000000000000000000000000000000000000000000000"));
   }

   @Test
   public void subtract_returnsNan_whenNan()
   {
      testObject = MutableInfiniteInteger.NaN.subtract(1);
      assertThat(testObject, is(MutableInfiniteInteger.NaN));
      testObject = MutableInfiniteInteger.valueOf(1).subtract(MutableInfiniteInteger.NaN);
      assertThat(testObject, is(MutableInfiniteInteger.NaN));
   }

   @Test
   public void subtract_returnsOther_whenZero()
   {
      testObject = MutableInfiniteInteger.valueOf(0).subtract(1);
      assertThat(testObject, is(MutableInfiniteInteger.valueOf(-1)));
      testObject = MutableInfiniteInteger.valueOf(1).subtract(0);
      assertThat(testObject, is(MutableInfiniteInteger.valueOf(1)));
   }

   @Test
   public void subtract_returnsNan_whenInfinityMinusInfinity()
   {
      testObject = MutableInfiniteInteger.POSITIVE_INFINITY.subtract(MutableInfiniteInteger.POSITIVE_INFINITY);
      assertThat(testObject, is(MutableInfiniteInteger.NaN));
   }

   @Test
   public void subtract_returnsInfinity_whenInfinity()
   {
      testObject = MutableInfiniteInteger.POSITIVE_INFINITY.subtract(5);
      assertThat(testObject, is(MutableInfiniteInteger.POSITIVE_INFINITY));
      testObject = MutableInfiniteInteger.NEGATIVE_INFINITY.subtract(5);
      assertThat(testObject, is(MutableInfiniteInteger.NEGATIVE_INFINITY));
   }

   @Test
   public void subtract_returnsInfinity_givenInfinity()
   {
      testObject = MutableInfiniteInteger.valueOf(5).subtract(MutableInfiniteInteger.POSITIVE_INFINITY);
      assertThat(testObject, is(MutableInfiniteInteger.NEGATIVE_INFINITY));
      testObject = MutableInfiniteInteger.valueOf(5).subtract(MutableInfiniteInteger.NEGATIVE_INFINITY);
      assertThat(testObject, is(MutableInfiniteInteger.POSITIVE_INFINITY));
   }

   @Test
   public void subtract_returnsZero_whenSame()
   {
      testObject = MutableInfiniteInteger.valueOf(10).subtract(MutableInfiniteInteger.valueOf(10));
      assertThat(testObject, is(MutableInfiniteInteger.valueOf(0)));
   }

   @Test
   public void subtract_returnsPositive_whenBigSubtractSmall()
   {
      assertEqualNodes(MutableInfiniteInteger.valueOf(10).subtract(MutableInfiniteInteger.valueOf(5)), 1, 5);
   }

   @Test
   public void subtract_returnsNegative_whenSmallSubtractBig()
   {
      assertEqualNodes(MutableInfiniteInteger.valueOf(5).subtract(MutableInfiniteInteger.valueOf(10)), -1, 5);
   }

   @Test
   public void subtract_returns_whenMoreThanMaxInt()
   {
      assertEqualNodes(MutableInfiniteInteger.valueOf(4_294_967_295L).subtract(1), 1, (int) 4_294_967_294L);
   }

   @Test
   public void subtract_returns_whenMoreThanMaxLong()
   {
      testObject = MutableInfiniteInteger.valueOf(1).subtract(Long.MAX_VALUE).subtract(Long.MAX_VALUE).subtract(3);
      assertEqualNodes(testObject, -1, 0, 0, 1);
   }

   @Test
   public void subtract_returns_whenMultipleBorrows()
   {
      testObject = MutableInfiniteInteger.valueOf(Long.MAX_VALUE).add(Long.MAX_VALUE).add(2);
      testObject = testObject.subtract(1);
      assertEqualNodes(testObject, 1, -1, -1);
   }

   /**
    * Previous bug.
    */
   @Test
   public void subtract_onlyRemovesLeadingZeros_whenMiddleZeroes() throws Exception
   {
      testObject = MutableInfiniteInteger.valueOf(1).multiplyByPowerOf2(64).add(1);
      assertEqualNodes(testObject.subtract(1), 1, 0, 0, 1);
   }

   /**
    * Happy path for {@link MutableInfiniteInteger#multiply(long)}
    */
   @Test
   public void multiply_returns_givenLong()
   {
      assertEqualNodes(MutableInfiniteInteger.valueOf(5).multiply(5), 1, 25);
   }

   /**
    * Test for {@link MutableInfiniteInteger#multiply(MutableInfiniteInteger)}
    */
   @Test
   public void multiply_returnsPositive_givenBothFinitePositive()
   {
      assertEqualNodes(MutableInfiniteInteger.valueOf(5).multiply(MutableInfiniteInteger.valueOf(5)), 1, 25);
   }

   /**
    * Test for {@link MutableInfiniteInteger#multiply(MutableInfiniteInteger)}
    */
   @Test
   public void multiply_returnsPositive_givenBothFiniteNegative()
   {
      assertEqualNodes(MutableInfiniteInteger.valueOf(-5).multiply(MutableInfiniteInteger.valueOf(-5)), 1, 25);
   }

   /**
    * Test for {@link MutableInfiniteInteger#multiply(MutableInfiniteInteger)}
    */
   @Test
   public void multiply_returnsNegative_givenBothFiniteOneNegative()
   {
      assertEqualNodes(MutableInfiniteInteger.valueOf(5).multiply(MutableInfiniteInteger.valueOf(-5)), -1, 25);
      assertEqualNodes(MutableInfiniteInteger.valueOf(-5).multiply(MutableInfiniteInteger.valueOf(5)), -1, 25);
   }

   /**
    * Test for {@link MutableInfiniteInteger#multiply(MutableInfiniteInteger)}
    */
   @Test
   public void multiply_returnsResult_whenMultiNode()
   {
      //more than max int
      assertEqualNodes(MutableInfiniteInteger.valueOf(4_294_967_295L).multiply(MutableInfiniteInteger.valueOf(-2)), -1,
            (int) 4_294_967_294L, 1);

      //more than max long
      assertEqualNodes(MutableInfiniteInteger.valueOf(Long.MAX_VALUE).multiply(2).add(MutableInfiniteInteger.valueOf(2)), 1, 0, 0, 1);
   }

   /**
    * Test for {@link MutableInfiniteInteger#multiply(MutableInfiniteInteger)}
    */
   @Test
   public void multiply_returnsResult_whenLongMultiplication()
   {
      assertEqualNodes(MutableInfiniteInteger.valueOf(-Long.MAX_VALUE).multiply(MutableInfiniteInteger.valueOf(-Long.MAX_VALUE)), 1, 1, 0,
            -1, 0x3FFF_FFFF);
      //first pass should be: + 1, 7FFFFFFF, 7FFFFFFF
      //second pass should be: + 0, 80000001, 7FFFFFFF, 3FFFFFFF

      testObject = MutableInfiniteInteger.littleEndian(new long[]{0x1000_0000__0000_000FL}, false);
      assertEqualNodes(testObject.multiply(16), 1, 0xF0, 0, 1);
   }

   /**
    * Test for {@link MutableInfiniteInteger#multiply(MutableInfiniteInteger)}
    */
   @Test
   public void multiply_returnsNan_givenNan()
   {
      assertThat(MutableInfiniteInteger.NaN.multiply(MutableInfiniteInteger.valueOf(2)), is(MutableInfiniteInteger.NaN));
      assertThat(MutableInfiniteInteger.valueOf(2).multiply(MutableInfiniteInteger.NaN), is(MutableInfiniteInteger.NaN));
   }

   /**
    * Test for {@link MutableInfiniteInteger#multiply(MutableInfiniteInteger)}
    */
   @Test
   public void multiply_returnsNan_givenInfinityAndZero()
   {
      assertThat(MutableInfiniteInteger.POSITIVE_INFINITY.multiply(MutableInfiniteInteger.valueOf(0)), is(MutableInfiniteInteger.NaN));
      assertThat(MutableInfiniteInteger.NEGATIVE_INFINITY.multiply(MutableInfiniteInteger.valueOf(0)), is(MutableInfiniteInteger.NaN));

      assertThat(MutableInfiniteInteger.valueOf(0).multiply(MutableInfiniteInteger.POSITIVE_INFINITY), is(MutableInfiniteInteger.NaN));
      assertThat(MutableInfiniteInteger.valueOf(0).multiply(MutableInfiniteInteger.NEGATIVE_INFINITY), is(MutableInfiniteInteger.NaN));
   }

   /**
    * Test for {@link MutableInfiniteInteger#multiply(MutableInfiniteInteger)}
    */
   @Test
   public void multiply_returnsPositiveInfinity_givenSameSignInfinity()
   {
      testObject = MutableInfiniteInteger.POSITIVE_INFINITY.multiply(MutableInfiniteInteger.POSITIVE_INFINITY);
      assertThat(testObject, is(MutableInfiniteInteger.POSITIVE_INFINITY));
      testObject = MutableInfiniteInteger.NEGATIVE_INFINITY.multiply(MutableInfiniteInteger.NEGATIVE_INFINITY);
      assertThat(testObject, is(MutableInfiniteInteger.POSITIVE_INFINITY));
   }

   /**
    * Test for {@link MutableInfiniteInteger#multiply(MutableInfiniteInteger)}
    */
   @Test
   public void multiply_returnsNegativeInfinity_givenDifferentSignInfinity()
   {
      testObject = MutableInfiniteInteger.NEGATIVE_INFINITY.multiply(MutableInfiniteInteger.POSITIVE_INFINITY);
      assertThat(testObject, is(MutableInfiniteInteger.NEGATIVE_INFINITY));
      testObject = MutableInfiniteInteger.POSITIVE_INFINITY.multiply(MutableInfiniteInteger.NEGATIVE_INFINITY);
      assertThat(testObject, is(MutableInfiniteInteger.NEGATIVE_INFINITY));
   }

   /**
    * Test for {@link MutableInfiniteInteger#multiply(MutableInfiniteInteger)}
    */
   @Test
   public void multiply_returnsInfinity_givenPositiveAndInfinity()
   {
      testObject = MutableInfiniteInteger.POSITIVE_INFINITY.multiply(MutableInfiniteInteger.valueOf(2));
      assertThat(testObject, is(MutableInfiniteInteger.POSITIVE_INFINITY));
      testObject = MutableInfiniteInteger.valueOf(2).multiply(MutableInfiniteInteger.POSITIVE_INFINITY);
      assertThat(testObject, is(MutableInfiniteInteger.POSITIVE_INFINITY));

      testObject = MutableInfiniteInteger.NEGATIVE_INFINITY.multiply(MutableInfiniteInteger.valueOf(2));
      assertThat(testObject, is(MutableInfiniteInteger.NEGATIVE_INFINITY));
      testObject = MutableInfiniteInteger.valueOf(2).multiply(MutableInfiniteInteger.NEGATIVE_INFINITY);
      assertThat(testObject, is(MutableInfiniteInteger.NEGATIVE_INFINITY));
   }

   /**
    * Test for {@link MutableInfiniteInteger#multiply(MutableInfiniteInteger)}
    */
   @Test
   public void multiply_returnsOppositeInfinity_givenNegativeAndInfinity()
   {
      testObject = MutableInfiniteInteger.POSITIVE_INFINITY.multiply(MutableInfiniteInteger.valueOf(-2));
      assertThat(testObject, is(MutableInfiniteInteger.NEGATIVE_INFINITY));
      testObject = MutableInfiniteInteger.valueOf(-2).multiply(MutableInfiniteInteger.POSITIVE_INFINITY);
      assertThat(testObject, is(MutableInfiniteInteger.NEGATIVE_INFINITY));

      testObject = MutableInfiniteInteger.NEGATIVE_INFINITY.multiply(MutableInfiniteInteger.valueOf(-2));
      assertThat(testObject, is(MutableInfiniteInteger.POSITIVE_INFINITY));
      testObject = MutableInfiniteInteger.valueOf(-2).multiply(MutableInfiniteInteger.NEGATIVE_INFINITY);
      assertThat(testObject, is(MutableInfiniteInteger.POSITIVE_INFINITY));
   }

   /**
    * Test for {@link MutableInfiniteInteger#multiply(MutableInfiniteInteger)}
    */
   @Test
   public void multiply_returnsResult_givenOne()
   {
      testObject = MutableInfiniteInteger.valueOf(5).multiply(MutableInfiniteInteger.valueOf(1));
      assertThat(testObject, is(MutableInfiniteInteger.valueOf(5)));
      testObject = MutableInfiniteInteger.valueOf(1).multiply(MutableInfiniteInteger.valueOf(5));
      assertThat(testObject, is(MutableInfiniteInteger.valueOf(5)));
   }

   /**
    * Test for {@link MutableInfiniteInteger#multiply(MutableInfiniteInteger)}
    */
   @Test
   public void multiply_returnsResult_givenNegativeOne()
   {
      testObject = MutableInfiniteInteger.valueOf(5).multiply(MutableInfiniteInteger.valueOf(-1));
      assertThat(testObject, is(MutableInfiniteInteger.valueOf(-5)));
      testObject = MutableInfiniteInteger.valueOf(-1).multiply(MutableInfiniteInteger.valueOf(5));
      assertThat(testObject, is(MutableInfiniteInteger.valueOf(-5)));
   }

   /**
    * Test for {@link MutableInfiniteInteger#multiply(MutableInfiniteInteger)}
    */
   @Test
   public void multiply_returnsZero_givenZero()
   {
      testObject = MutableInfiniteInteger.valueOf(5).multiply(MutableInfiniteInteger.valueOf(0));
      assertThat(testObject, is(MutableInfiniteInteger.valueOf(0)));
      testObject = MutableInfiniteInteger.valueOf(0).multiply(MutableInfiniteInteger.valueOf(5));
      assertThat(testObject, is(MutableInfiniteInteger.valueOf(0)));
   }

   @Test
   public void multiplyByPowerOf2()
   {
      //simple case
      assertEqualNodes(MutableInfiniteInteger.valueOf(1).multiplyByPowerOf2(3), 1, 8);

      //shift by x32
      assertEqualNodes(MutableInfiniteInteger.valueOf(1).multiplyByPowerOf2(64), 1, 0, 0, 1);

      //shift more than 32
      assertEqualNodes(MutableInfiniteInteger.valueOf(1).multiplyByPowerOf2(35), 1, 0, 8);

      //multiple starting nodes (shift not x32)
      assertEqualNodes(MutableInfiniteInteger.valueOf(Long.MAX_VALUE).multiplyByPowerOf2(34), 1, 0, -4, -1, 1);
   }

   @Test
   public void divide()
   {
      //simple case
      assertDivision(MutableInfiniteInteger.valueOf(10).divide(5), 1, new int[]{2}, new int[]{0});
      assertDivision(MutableInfiniteInteger.valueOf(0).divide(5), 0, new int[]{0}, new int[]{0});

      //previous bug caused by shifting down. Shifting affects remainder of small number
      assertDivision(MutableInfiniteInteger.valueOf(78).divide(10), 1, new int[]{7}, new int[]{8});

      //not so clean numbers: (2^32) / (2^5-1) = (2^32) / 31 = 0x842_1084 r 4
      assertDivision(MutableInfiniteInteger.valueOf(1L << 32).divide(31), 1, new int[]{0x842_1084}, new int[]{4});

      //simple negative with remainder
      assertDivision(MutableInfiniteInteger.valueOf(-11).divide(5), -1, new int[]{2}, new int[]{1});

      //multiple starting nodes that fit into long after shifting
      testObject = MutableInfiniteInteger.valueOf(Long.MAX_VALUE).add(1).multiplyByPowerOf2(32);
      //(2^95) / -(2^32) == -(2^63). That's what I'm testing
      assertDivision(testObject.divide(-1L << 32), -1, new int[]{0, Integer.MIN_VALUE}, new int[]{0});

      //multiple nodes for both that can't fit into long
      //(2^95)/(2^63) == (2^32)
      final MutableInfiniteInteger twoPower63 = MutableInfiniteInteger.valueOf(Long.MAX_VALUE).add(1);
      testObject = twoPower63.copy().multiplyByPowerOf2(32);
      assertDivision(testObject.copy().divide(twoPower63), 1, new int[]{0, 1}, new int[]{0});

      //again but with remainder
      //(2^95)/(2^63-1) == (2^32) r (2^32). Weird but true
      //same testObject
      assertDivision(testObject.divide(Long.MAX_VALUE), 1, new int[]{0, 1}, new int[]{0, 1});

      //previous bug caused by shifting down. Shifting affects remainder of large number
      testObject = MutableInfiniteInteger.valueOf(1).multiplyByPowerOf2(65).add(1).multiplyByPowerOf2(70);
      //10{64}10{70} / 20{70} => 10{64}1 / 2 = 10{64} r 10{70}
      final MutableInfiniteInteger operand = MutableInfiniteInteger.valueOf(2).multiplyByPowerOf2(70);
      assertDivision(testObject.divide(operand), 1, new int[]{0, 0, 1}, new int[]{0, 0, 0b1000000});

      testObject = MutableInfiniteInteger.valueOf(Long.MAX_VALUE).add(1);
      assertDivision(testObject.divide(16), 1, new int[]{0, 0x8000000}, new int[]{0});

      testObject = MutableInfiniteInteger.valueOf(Integer.MAX_VALUE);
      assertDivision(testObject.divide(10), 1, new int[]{214748364}, new int[]{7});

      testObject = MutableInfiniteInteger.valueOf(Long.MAX_VALUE).add(152);
      assertDivision(testObject.divide(10), 1, new int[]{0xcccc_ccdb, 0xccc_cccc}, new int[]{9});

      testObject = MutableInfiniteInteger.valueOf(Long.MAX_VALUE).add(151);
      assertDivision(testObject.divide(10), 1, new int[]{0xcccc_ccdb, 0xccc_cccc}, new int[]{8});

      testObject = MutableInfiniteInteger.valueOf(Long.MAX_VALUE).add(Long.MAX_VALUE).add(152);
      assertDivision(testObject.divide(10), 1, new int[]{0x9999_99A8, 0x1999_9999}, new int[]{6});
   }

   @Test
   public void divideByPowerOf2DropRemainder()
   {
      //simple case
      assertEqualNodes(MutableInfiniteInteger.valueOf(1024).divideByPowerOf2DropRemainder(3), 1, 128);

      //shift by x32
      testObject = MutableInfiniteInteger.valueOf(1).multiplyByPowerOf2(64).add(Long.MAX_VALUE);
      assertEqualNodes(testObject.divideByPowerOf2DropRemainder(64), 1, 1);

      //shift more than 32
      testObject = MutableInfiniteInteger.valueOf(1).multiplyByPowerOf2(32 * 3).subtract(1);  //3 nodes all high
      assertEqualNodes(testObject.divideByPowerOf2DropRemainder(35), 1, -1, 0x1FFF_FFFF);

      //previous bug: last shift would leave a leading 0
      testObject = MutableInfiniteInteger.littleEndian(new long[]{0x0000_0000__0000_0000L, 1}, false);
      assertEqualNodes(testObject.divideByPowerOf2DropRemainder(1), 1, 0, 0x8000_0000);
   }

   @Test
   public void power()
   {
      //simple case
      assertEqualNodes(MutableInfiniteInteger.valueOf(3).power(3), 1, 27);

      //multiple ending nodes
      assertEqualNodes(MutableInfiniteInteger.valueOf(0x800).power(3), 1, 0, 2);

      //multiple starting nodes
      assertEqualNodes(MutableInfiniteInteger.valueOf(Long.MIN_VALUE).power(3), -1, 0, 0, 0, 0, 0, 0x2000_0000);
   }

   @Test
   public void selfPower()
   {
      assertEqualNodes(MutableInfiniteInteger.valueOf(3).selfPower(), 1, 27);
   }

   @Test
   public void isPrime()
   {
      assertFalse(MutableInfiniteInteger.valueOf(0).isPrime());
      assertFalse(MutableInfiniteInteger.valueOf(15).isPrime());
      assertFalse(MutableInfiniteInteger.valueOf(95).isPrime());
      assertFalse(MutableInfiniteInteger.valueOf(1005).isPrime());
      assertFalse(MutableInfiniteInteger.valueOf(1024).isPrime());
      //10,005 takes a little long (600 ms)
      assertFalse(MutableInfiniteInteger.valueOf(Long.MAX_VALUE).add(1).isPrime());

      assertTrue(MutableInfiniteInteger.valueOf(2).isPrime());
      assertTrue(MutableInfiniteInteger.valueOf(3).isPrime());
      assertTrue(MutableInfiniteInteger.valueOf(5).isPrime());
      assertTrue(MutableInfiniteInteger.valueOf(199).isPrime());
   }

   @Test
   public void leastCommonMultiple_returnsNan_givenNan() throws Exception
   {
      assertEquals(MutableInfiniteInteger.NaN, MutableInfiniteInteger.NaN.leastCommonMultiple(5));
      assertEquals(MutableInfiniteInteger.NaN, MutableInfiniteInteger.valueOf(5).leastCommonMultiple(MutableInfiniteInteger.NaN));
   }

   @Test
   public void leastCommonMultiple_returnsNan_givenPositiveInfinity() throws Exception
   {
      assertEquals(MutableInfiniteInteger.NaN, MutableInfiniteInteger.POSITIVE_INFINITY.leastCommonMultiple(5));
      assertEquals(MutableInfiniteInteger.NaN,
            MutableInfiniteInteger.valueOf(5).leastCommonMultiple(MutableInfiniteInteger.POSITIVE_INFINITY));
   }

   @Test
   public void leastCommonMultiple_returnsNan_givenNegativeInfinity() throws Exception
   {
      assertEquals(MutableInfiniteInteger.NaN, MutableInfiniteInteger.NEGATIVE_INFINITY.leastCommonMultiple(5));
      assertEquals(MutableInfiniteInteger.NaN,
            MutableInfiniteInteger.valueOf(5).leastCommonMultiple(MutableInfiniteInteger.NEGATIVE_INFINITY));
   }

   @Test
   public void leastCommonMultiple_returnsNan_givenZero() throws Exception
   {
      assertEquals(MutableInfiniteInteger.NaN, MutableInfiniteInteger.valueOf(5).leastCommonMultiple(0));
      assertEquals(MutableInfiniteInteger.NaN, MutableInfiniteInteger.valueOf(0).leastCommonMultiple(MutableInfiniteInteger.valueOf(5)));
   }

   @Test
   public void leastCommonMultiple_returnsNonOne_givenOne() throws Exception
   {
      testObject = MutableInfiniteInteger.valueOf(5);
      assertEquals(testObject, testObject.leastCommonMultiple(1));
      assertEquals(testObject, MutableInfiniteInteger.valueOf(1).leastCommonMultiple(testObject));
   }

   @Test
   public void leastCommonMultiple_returnsNumber_whenEqual() throws Exception
   {
      assertEquals(MutableInfiniteInteger.valueOf(5),
            MutableInfiniteInteger.valueOf(5).leastCommonMultiple(MutableInfiniteInteger.valueOf(5)));
   }

   @Test
   public void leastCommonMultiple() throws Exception
   {
      final MutableInfiniteInteger actual = MutableInfiniteInteger.valueOf(3).leastCommonMultiple(4);
      assertThat(actual, is(MutableInfiniteInteger.valueOf(12)));
   }

   @Test
   public void leastCommonMultiple_returnsHigher_whenHigherIsAMultipleOfLower() throws Exception
   {
      final MutableInfiniteInteger actual = MutableInfiniteInteger.valueOf(2).leastCommonMultiple(4);
      assertThat(actual, is(MutableInfiniteInteger.valueOf(4)));
   }

   @Test
   public void greatestCommonDivisor_returnsNan_givenNan() throws Exception
   {
      assertEquals(MutableInfiniteInteger.NaN, MutableInfiniteInteger.NaN.greatestCommonDivisor(10));
      assertEquals(MutableInfiniteInteger.NaN, MutableInfiniteInteger.valueOf(10).greatestCommonDivisor(MutableInfiniteInteger.NaN));
   }

   @Test
   public void greatestCommonDivisor_returnsNan_givenPositiveInfinity() throws Exception
   {
      assertEquals(MutableInfiniteInteger.NaN, MutableInfiniteInteger.POSITIVE_INFINITY.greatestCommonDivisor(10));
      assertEquals(MutableInfiniteInteger.NaN,
            MutableInfiniteInteger.valueOf(10).greatestCommonDivisor(MutableInfiniteInteger.POSITIVE_INFINITY));
   }

   @Test
   public void greatestCommonDivisor_returnsNan_givenNegativeInfinity() throws Exception
   {
      assertEquals(MutableInfiniteInteger.NaN, MutableInfiniteInteger.NEGATIVE_INFINITY.greatestCommonDivisor(10));
      assertEquals(MutableInfiniteInteger.NaN,
            MutableInfiniteInteger.valueOf(10).greatestCommonDivisor(MutableInfiniteInteger.NEGATIVE_INFINITY));
   }

   @Test
   public void greatestCommonDivisor_returnsPositiveInfinity_whenBothAreZero() throws Exception
   {
      assertEquals(MutableInfiniteInteger.POSITIVE_INFINITY, MutableInfiniteInteger.valueOf(0).greatestCommonDivisor(0));
   }

   @Test
   public void greatestCommonDivisor_returnsNonZero_whenOnlyOneOfThemIsNonZero() throws Exception
   {
      assertEqualNodes(MutableInfiniteInteger.valueOf(0).greatestCommonDivisor(5), 1, 5);
      assertEqualNodes(MutableInfiniteInteger.valueOf(3).greatestCommonDivisor(0), 1, 3);
   }

   @Test
   public void greatestCommonDivisor_returnsOne_whenOneOfThemIsOne() throws Exception
   {
      assertEqualNodes(MutableInfiniteInteger.valueOf(1).greatestCommonDivisor(10), 1, 1);
      assertEqualNodes(MutableInfiniteInteger.valueOf(10).greatestCommonDivisor(1), 1, 1);
   }

   /**
    * For all other cases.
    */
   @Test
   public void greatestCommonDivisor()
   {
      //list of low primes: 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37

      //simple cases
      assertEqualNodes(MutableInfiniteInteger.valueOf(12).greatestCommonDivisor(10), 1, 2);
      assertEqualNodes(MutableInfiniteInteger.valueOf(10).greatestCommonDivisor(10), 1, 10);

      //the answer is the lower one. also negatives
      assertEqualNodes(MutableInfiniteInteger.valueOf(-15).greatestCommonDivisor(5), 1, 5);
      assertEqualNodes(MutableInfiniteInteger.valueOf(-5).greatestCommonDivisor(15), 1, 5);

      //both prime
      assertEqualNodes(MutableInfiniteInteger.valueOf(7).greatestCommonDivisor(5), 1, 1);

      //more than max long
      testObject = MutableInfiniteInteger.valueOf(7).multiplyByPowerOf2(64);
      final MutableInfiniteInteger infiniteInteger2 = MutableInfiniteInteger.valueOf(11).multiplyByPowerOf2(64);
      assertEqualNodes(testObject.greatestCommonDivisor(infiniteInteger2), 1, 0, 0, 1);
   }

   //@Test
   //this only compiles if sqrtCeil is made public (see below)
   //this test is only meaningful if the Math.sqrt delegation is commented out
   public void sqrtCeil()
   {
      for (int i = 0; i <= 1_200_000; i++)
      {
         //testObject = MutableInfiniteInteger.valueOf(i).sqrtCeil();
         final int actualHigh = (int) Math.ceil((Math.sqrt(i)));
         assertEquals(testObject.intValue(), actualHigh);
      }
   }

   //@Test
   //this only compiles if estimateSqrt is made public (see below)
   public void estimateSqrt()
   {
      //testObject = MutableInfiniteInteger.valueOf(0).estimateSqrt();
      assertEquals(testObject.intValue(), 0);

      for (int i = 1; i < 10_000_000; i++)
      {
         //testObject = MutableInfiniteInteger.valueOf(i).estimateSqrt();
         final int actualLow = (int) Math.floor((Math.sqrt(i)));
         final int actualHigh = (int) Math.ceil((Math.sqrt(i) * 2));
         assertThat(testObject.intValue(), Matchers.greaterThanOrEqualTo(actualLow));
         assertThat(testObject.intValue(), Matchers.lessThan(actualHigh));
      }
   }

   @Test
   public void signum()
   {
      assertThat(MutableInfiniteInteger.POSITIVE_INFINITY.signum(), is((byte) 1));
      assertThat(MutableInfiniteInteger.NEGATIVE_INFINITY.signum(), is((byte) -1));
      assertThat(MutableInfiniteInteger.NaN.signum(), is((byte) 0));
      assertThat(MutableInfiniteInteger.valueOf(0).signum(), is((byte) 0));
      assertThat(MutableInfiniteInteger.valueOf(5).signum(), is((byte) 1));
      assertThat(MutableInfiniteInteger.valueOf(-5).signum(), is((byte) -1));
   }

   @Test
   public void isPowerOf2()
   {
      assertTrue(MutableInfiniteInteger.valueOf(0).isPowerOf2());
      assertTrue(MutableInfiniteInteger.valueOf(1).isPowerOf2());
      assertTrue(MutableInfiniteInteger.valueOf(2).isPowerOf2());
      assertTrue(MutableInfiniteInteger.valueOf(4).isPowerOf2());
      assertTrue(MutableInfiniteInteger.valueOf(8).isPowerOf2());
      assertTrue(MutableInfiniteInteger.valueOf(-8).isPowerOf2());
      assertTrue(MutableInfiniteInteger.valueOf(0x800000000L).isPowerOf2());

      assertFalse(MutableInfiniteInteger.valueOf(3).isPowerOf2());
      assertFalse(MutableInfiniteInteger.valueOf(5).isPowerOf2());
      assertFalse(MutableInfiniteInteger.valueOf(10).isPowerOf2());
      assertFalse(MutableInfiniteInteger.valueOf(-10).isPowerOf2());
      assertFalse(MutableInfiniteInteger.valueOf(0x800000020L).isPowerOf2());
   }

   @Test
   public void equals()
   {
      assertEquals(MutableInfiniteInteger.valueOf(10), MutableInfiniteInteger.valueOf(10));
      assertEquals(MutableInfiniteInteger.valueOf(5).add(5), MutableInfiniteInteger.valueOf(7).add(3));
      testObject = MutableInfiniteInteger.valueOf(Long.MAX_VALUE).add(Long.MAX_VALUE).add(2);
      assertEquals(testObject, testObject);
      final MutableInfiniteInteger mutableInfiniteInteger = MutableInfiniteInteger.valueOf(123);
      assertEquals(mutableInfiniteInteger.copy(), mutableInfiniteInteger);
      assertNotEquals(this.testObject.copy().add(1), this.testObject);
   }

   @Test
   public void compareTo_returnsZero_givenSameObject() throws Exception
   {
      //I must call compareTo myself since hamcrest would use .equals() for is()
      testObject = MutableInfiniteInteger.valueOf(2);
      assertThat(testObject.compareTo(testObject), is(0));
      testObject = MutableInfiniteInteger.NaN;
      assertThat(testObject.compareTo(testObject), is(0));
      testObject = MutableInfiniteInteger.POSITIVE_INFINITY;
      assertThat(testObject.compareTo(testObject), is(0));
      testObject = MutableInfiniteInteger.NEGATIVE_INFINITY;
      assertThat(testObject.compareTo(testObject), is(0));
   }

   @Test
   public void compareTo_nanIsGreatest() throws Exception
   {
      assertThat(MutableInfiniteInteger.NaN, is(greaterThan(MutableInfiniteInteger.POSITIVE_INFINITY)));
      assertThat(MutableInfiniteInteger.NaN, is(greaterThan(MutableInfiniteInteger.NEGATIVE_INFINITY)));
      assertThat(MutableInfiniteInteger.NaN, is(greaterThan(MutableInfiniteInteger.valueOf(2))));

      assertThat(MutableInfiniteInteger.POSITIVE_INFINITY, is(lessThan(MutableInfiniteInteger.NaN)));
      assertThat(MutableInfiniteInteger.NEGATIVE_INFINITY, is(lessThan(MutableInfiniteInteger.NaN)));
      assertThat(MutableInfiniteInteger.valueOf(2), is(lessThan(MutableInfiniteInteger.NaN)));
   }

   @Test
   public void compareTo_negativeInfinityIsLeast() throws Exception
   {
      assertThat(MutableInfiniteInteger.NEGATIVE_INFINITY, is(lessThan(MutableInfiniteInteger.POSITIVE_INFINITY)));
      assertThat(MutableInfiniteInteger.NEGATIVE_INFINITY, is(lessThan(MutableInfiniteInteger.valueOf(2))));

      assertThat(MutableInfiniteInteger.POSITIVE_INFINITY, is(greaterThan(MutableInfiniteInteger.NEGATIVE_INFINITY)));
      assertThat(MutableInfiniteInteger.valueOf(2), is(greaterThan(MutableInfiniteInteger.NEGATIVE_INFINITY)));
   }

   @Test
   public void compareTo_positiveInfinityIsNextGreatest() throws Exception
   {
      assertThat(MutableInfiniteInteger.POSITIVE_INFINITY, is(greaterThan(MutableInfiniteInteger.valueOf(2))));
      assertThat(MutableInfiniteInteger.valueOf(2), is(lessThan(MutableInfiniteInteger.POSITIVE_INFINITY)));
   }

   @Test
   public void compareTo_positiveGreaterThanNegative() throws Exception
   {
      assertThat(MutableInfiniteInteger.valueOf(2), is(greaterThan(MutableInfiniteInteger.valueOf(-2))));
      assertThat(MutableInfiniteInteger.valueOf(-2), is(lessThan(MutableInfiniteInteger.valueOf(2))));
   }

   @Test
   public void compareTo_returnsZero_whenBothAreFiniteEqual() throws Exception
   {
      //I must call compareTo myself since hamcrest would use .equals() for is()
      //1 node
      testObject = MutableInfiniteInteger.valueOf(2);
      assertThat(testObject.compareTo(MutableInfiniteInteger.valueOf(2)), is(0));
      testObject = MutableInfiniteInteger.valueOf(0);
      assertThat(testObject.compareTo(MutableInfiniteInteger.valueOf(0)), is(0));
      //2+ nodes
      testObject = MutableInfiniteInteger.valueOf(Long.MAX_VALUE).add(Long.MAX_VALUE).add(2);
      assertThat(testObject.compareTo(MutableInfiniteInteger.valueOf(Long.MAX_VALUE).add(Long.MAX_VALUE).add(2)), is(0));
   }

   @Test
   public void compareTo_compares_givenBothFinitePositive() throws Exception
   {
      assertThat(MutableInfiniteInteger.valueOf(2), is(greaterThan(MutableInfiniteInteger.valueOf(1))));
      assertThat(MutableInfiniteInteger.valueOf(1), is(lessThan(MutableInfiniteInteger.valueOf(2))));
   }

   @Test
   public void compareTo_compares_givenBothFiniteNegative() throws Exception
   {
      assertThat(MutableInfiniteInteger.valueOf(-1), is(greaterThan(MutableInfiniteInteger.valueOf(-2))));
      assertThat(MutableInfiniteInteger.valueOf(-2), is(lessThan(MutableInfiniteInteger.valueOf(-1))));
   }

   @Test
   public void compareTo_compares_givenMultiNodeSameLength() throws Exception
   {
      final MutableInfiniteInteger multiNode = MutableInfiniteInteger.valueOf(Long.MAX_VALUE).add(Long.MAX_VALUE).add(2);
      final MutableInfiniteInteger greaterMultiNode = MutableInfiniteInteger.valueOf(Long.MAX_VALUE).add(Long.MAX_VALUE).add(10);
      assertThat(greaterMultiNode, is(greaterThan(multiNode)));
      assertThat(multiNode, is(lessThan(greaterMultiNode)));
   }

   @Test
   public void compareTo_compares_givenMultiNodeDifferentLength() throws Exception
   {
      final MutableInfiniteInteger multiNode = MutableInfiniteInteger.valueOf(1).multiplyByPowerOf2(32);
      final MutableInfiniteInteger greaterMultiNode = MutableInfiniteInteger.valueOf(1).multiplyByPowerOf2(64);
      assertThat(greaterMultiNode, is(greaterThan(multiNode)));
      assertThat(multiNode, is(lessThan(greaterMultiNode)));
   }

   @Test
   public void compareTo_comparesNodesAsUnsigned() throws Exception
   {
      testObject = MutableInfiniteInteger.valueOf(Integer.MAX_VALUE).add(1);
      final MutableInfiniteInteger greaterValue = testObject.copy().add(1);
      assertThat(greaterValue, is(greaterThan(testObject)));
      assertThat(testObject, is(lessThan(greaterValue)));
   }

   /**
    * Happy path for {@link MutableInfiniteInteger#toString(int)}
    */
   @Test
   public void toString_returnsValue() throws Exception
   {
      assertEquals("ff", MutableInfiniteInteger.valueOf(255).toString(16));

      testObject = MutableInfiniteInteger.valueOf(Long.MAX_VALUE).add(1);
      assertEquals("8000000000000000", testObject.toString(16));
      testObject = MutableInfiniteInteger.valueOf(Long.MIN_VALUE).subtract(1);
      assertEquals("-1000000000000000000000000000000000000000000000000000000000000001", testObject.toString(2));
      testObject = MutableInfiniteInteger.valueOf(Long.MAX_VALUE).add(1);
      assertEquals("2021110011022210012102010021220101220222", testObject.toString(3));
      testObject = MutableInfiniteInteger.valueOf(Long.MIN_VALUE).subtract(1);
      assertEquals("-2021110011022210012102010021220101221000", testObject.toString(3));

      testObject = MutableInfiniteInteger.valueOf(Long.MAX_VALUE).add(1);
      assertEquals("9223372036854775808", testObject.toString(10));
      testObject = MutableInfiniteInteger.valueOf(Long.MIN_VALUE).subtract(1);
      assertEquals("-9223372036854775809", testObject.toString(10));

      //leftPad maintains order of node
      testObject = MutableInfiniteInteger.valueOf(Long.MAX_VALUE).add(1).add(0xab);
      //80000000_000000ab
      assertEquals("80000000000000ab", testObject.toString(16));

      //highest node isn't padded when negative
      testObject = MutableInfiniteInteger.valueOf(Long.MAX_VALUE).add(1).multiplyByPowerOf2(1);
      //1_00000000_00000000
      assertEquals("-10000000000000000", testObject.negate().toString(16));

      testObject = MutableInfiniteInteger.valueOf(Long.MAX_VALUE).add(Long.MAX_VALUE).add(152);
      assertEquals("10000000000000096", testObject.toString(16));
      assertEquals("18446744073709551766", testObject.toString(10));
   }

   //@Test
   public void toString_speed() throws Exception
   {
      testObject = MutableInfiniteInteger.valueOf(Long.MIN_VALUE).subtract(1);
      long start = System.nanoTime();
      assertEquals("-1000000000000000000000000000000000000000000000000000000000000001", testObject.toString(2));
      long end = System.nanoTime();
      System.out.println(Duration.ofNanos(end - start));

      start = System.nanoTime();
      assertEquals("-9223372036854775809", testObject.toString(10));
      end = System.nanoTime();
      System.out.println(Duration.ofNanos(end - start));
   }

   /**
    * Test for {@link MutableInfiniteInteger#toString(int)}
    */
   @Test
   public void toString_throws_givenTooSmallRadix() throws Exception
   {
      try
      {
         MutableInfiniteInteger.valueOf(1).toString(0);
         fail("Should've thrown");
      }
      catch (final IllegalArgumentException actual)
      {
         assertEquals("radix < 1 (was 0)", actual.getMessage());
      }
   }

   /**
    * Test for {@link MutableInfiniteInteger#toString(int)}
    */
   @Test
   public void toString_throws_givenTooBigRadix() throws Exception
   {
      try
      {
         MutableInfiniteInteger.valueOf(1).toString(63);
         fail("Should've thrown");
      }
      catch (final IllegalArgumentException actual)
      {
         assertEquals("radix > 62 (was 63)", actual.getMessage());
      }
   }

   /**
    * Test for {@link MutableInfiniteInteger#toString(int)}
    */
   @Test
   public void toString_returnsInfinitySymbol_givenPositiveInfinityAndRadix() throws Exception
   {
      assertEquals("∞", MutableInfiniteInteger.POSITIVE_INFINITY.toString(1));
   }

   /**
    * Test for {@link MutableInfiniteInteger#toString(int)}
    */
   @Test
   public void toString_returnsInfinitySymbol_givenNegativeInfinityAndRadix() throws Exception
   {
      assertEquals("-∞", MutableInfiniteInteger.NEGATIVE_INFINITY.toString(1));
   }

   /**
    * Test for {@link MutableInfiniteInteger#toString(int)}
    */
   @Test
   public void toString_returnsNotIntegerSymbols_givenNanAndRadix() throws Exception
   {
      assertEquals("∉ℤ", MutableInfiniteInteger.NaN.toString(1));
   }

   /**
    * Test for {@link MutableInfiniteInteger#toString(int)}
    */
   @Test
   public void toString_returnsBase1String_givenInteger() throws Exception
   {
      assertEquals("111", MutableInfiniteInteger.valueOf(3).toString(1));
   }

   /**
    * Test for {@link MutableInfiniteInteger#toString(int)}
    */
   @Test
   public void toString_throws_whenTooBigForBase1() throws Exception
   {
      try
      {
         MutableInfiniteInteger.valueOf(Long.MAX_VALUE).add(1).toString(1);
         fail("Should've thrown");
      }
      catch (final IllegalArgumentException actual)
      {
         assertEquals("9223372036854775808 in base 1 would exceed max string length.", actual.getMessage());
      }
   }

   /**
    * Test for {@link MutableInfiniteInteger#toString()}
    */
   @Test
   public void toString_returnsInfinitySymbol_givenPositiveInfinity() throws Exception
   {
      assertEquals("Infinity", MutableInfiniteInteger.POSITIVE_INFINITY.toString());
   }

   /**
    * Test for {@link MutableInfiniteInteger#toString()}
    */
   @Test
   public void toString_returnsInfinitySymbol_givenNegativeInfinity() throws Exception
   {
      assertEquals("-Infinity", MutableInfiniteInteger.NEGATIVE_INFINITY.toString());
   }

   /**
    * Test for {@link MutableInfiniteInteger#toString()}
    */
   @Test
   public void toString_returnsNotIntegerSymbols_givenNan() throws Exception
   {
      assertEquals("NaN", MutableInfiniteInteger.NaN.toString());
   }

   /**
    * Happy path for {@link MutableInfiniteInteger#toString()}
    */
   @Test
   public void toString_returnsWholeThing_whenFits() throws Exception
   {
      testObject = MutableInfiniteInteger.valueOf(Long.MAX_VALUE).add(1);
      //to show that it isn't based on max long
      assertThat(testObject.toString(), is("9223372036854775808"));
   }

   /**
    * Test for {@link MutableInfiniteInteger#toString()}
    */
   @Test
   public void toString_returnsEnding_whenTooLarge() throws Exception
   {
      testObject = MutableInfiniteInteger.valueOf(Long.MAX_VALUE).multiply(100);
      //the leading 9 is chopped off
      assertThat(testObject.toString(), is("…22337203685477580700"));
   }

   /**
    * Test for {@link MutableInfiniteInteger#toString()}
    */
   @Test
   public void toString_correctlyPlacesNegative_whenLargeNegative() throws Exception
   {
      testObject = MutableInfiniteInteger.valueOf(Long.MAX_VALUE).multiply(100).negate();
      //the leading 9 is chopped off. the minus is before the ellipsis
      assertThat(testObject.toString(), is("-…22337203685477580700"));
   }

   @Test
   public void toString_returnsZero_whenZero() throws Exception
   {
      testObject = MutableInfiniteInteger.valueOf(0);
      assertThat(testObject.toString(), is("0"));
   }

   @Test
   public void copy_copies_whenSingleNode() throws Exception
   {
      testObject = MutableInfiniteInteger.valueOf(2);
      final MutableInfiniteInteger actual = testObject.copy();
      assertThat(actual, is(equalTo(testObject)));
      assertThat(actual, is(not(sameInstance(testObject))));
   }

   @Test
   public void copy_copies_whenMultiNode() throws Exception
   {
      testObject = MutableInfiniteInteger.valueOf(1).multiplyByPowerOf2(32);
      final MutableInfiniteInteger actual = testObject.copy();
      assertThat(actual, is(equalTo(testObject)));
      assertThat(actual, is(not(sameInstance(testObject))));
   }

   @Test
   public void copy_returnsSameInstance_whenSpecialValue() throws Exception
   {
      testObject = MutableInfiniteInteger.NaN;
      MutableInfiniteInteger actual = testObject.copy();
      assertThat(actual, is(sameInstance(testObject)));

      testObject = MutableInfiniteInteger.POSITIVE_INFINITY;
      actual = testObject.copy();
      assertThat(actual, is(sameInstance(testObject)));

      testObject = MutableInfiniteInteger.NEGATIVE_INFINITY;
      actual = testObject.copy();
      assertThat(actual, is(sameInstance(testObject)));
   }

   private void assertDivision(final IntegerQuotient<MutableInfiniteInteger> divisionResult, final int wholeSign, final int[] wholeNodes,
                               final int[] remainderNodes)
   {
      assertEqualNodes(divisionResult.getWholeResult(), wholeSign, wholeNodes);
      if (remainderNodes.length == 1 && remainderNodes[0] == 0) assertEqualNodes(divisionResult.getRemainder(), 0, 0);
      else assertEqualNodes(divisionResult.getRemainder(), 1, remainderNodes);
   }

   private void assertEqualNodes(final MutableInfiniteInteger infiniteIntegerParam, final int expectedSignum, final int... expectedNodes)
   {
      assertEquals(generateInfiniteIntegerString(expectedSignum, expectedNodes), infiniteIntegerParam.toDebuggingString());
   }

   //tightly coupled with MutableInfiniteInteger.toDebuggingString()
   private String generateInfiniteIntegerString(final int signum, final int... nodeValues)
   {
      String returnValue = "+ ";
      if (signum == -1) returnValue = "- ";
      final StringBuilder stringBuilder = new StringBuilder(returnValue);
      for (final int node : nodeValues)
      {
         stringBuilder.append(Integer.toHexString(node).toUpperCase());
         stringBuilder.append(", ");
      }
      return stringBuilder.toString();
   }

}
