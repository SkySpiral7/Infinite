package com.github.SkySpiral7.Java.Infinite.numbers;

import java.math.BigInteger;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.ListIterator;

import com.github.SkySpiral7.Java.iterators.JumpingIterator;
import com.github.SkySpiral7.Java.pojo.IntegerQuotient;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;

import static com.github.SkySpiral7.Java.pojo.Comparison.EQUAL_TO;
import static com.github.SkySpiral7.Java.util.ComparableSugar.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
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
 * the other versions of equals, hashCode, copy, toFile (but toString should be tested when finished),
 * calculateMaxBigInteger (too slow), calculateGoogolplex (lol slow and nothing to test)
 */
public class MutableInfiniteInteger_UT
{
   private MutableInfiniteInteger mutableInfiniteInteger;
   //TODO: make tests to ensure that this is mutated but not param

   @Test
   public void add_long()
   {
      //simple case
      assertEqualNodes(MutableInfiniteInteger.valueOf(5).add(5), 1, 10);

      //simple negative case
      assertEqualNodes(MutableInfiniteInteger.valueOf(-5).add(-5), -1, 10);

      //more than max int
      assertEqualNodes(MutableInfiniteInteger.valueOf(8_589_934_592L).add(5), 1, 5, 2);

      //more than max long
      assertEqualNodes(MutableInfiniteInteger.valueOf(Long.MAX_VALUE).add(Long.MAX_VALUE).add(2), 1, 0, 0, 1);

      //special case is negative but can't use Math.abs
      assertEqualNodes(MutableInfiniteInteger.valueOf(-1).add(Long.MIN_VALUE), -1, 1, Integer.MIN_VALUE);
   }

   @Test
   public void add_InfiniteInteger()
   {
      //simple case
      assertEqualNodes(MutableInfiniteInteger.valueOf(5).add(MutableInfiniteInteger.valueOf(5)), 1, 10);

      //simple negative case
      assertEqualNodes(MutableInfiniteInteger.valueOf(-5).add(MutableInfiniteInteger.valueOf(-5)), -1, 10);

      //more than max int
      assertEqualNodes(MutableInfiniteInteger.valueOf(8_589_934_592L).add(MutableInfiniteInteger.valueOf(5)), 1, 5, 2);

      //more than max long
      mutableInfiniteInteger = MutableInfiniteInteger.valueOf(Long.MAX_VALUE)
                                                     .add(MutableInfiniteInteger.valueOf(Long.MAX_VALUE))
                                                     .add(MutableInfiniteInteger.valueOf(2));
      assertEqualNodes(mutableInfiniteInteger, 1, 0, 0, 1);
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
      mutableInfiniteInteger = bigIntMaxValueInf.multiplyByPowerOf2(2).add(3);
      assertEquals(bigIntMaxValueBig, mutableInfiniteInteger.bigIntegerValue());

      assertEquals(BigInteger.valueOf(-1), MutableInfiniteInteger.valueOf(-1).bigIntegerValue());
      assertEquals(negativeBigIntMaxValueBig, negativeBigIntMaxValueInf.bigIntegerValue());
      mutableInfiniteInteger = negativeBigIntMaxValueInf.multiplyByPowerOf2(2).subtract(3);
      assertEquals(negativeBigIntMaxValueBig, mutableInfiniteInteger.bigIntegerValue());
   }

   @Test
   @Ignore  //ignored because WAY too slow
   public void bigIntegerValueExact()
   {
      mutableInfiniteInteger = MutableInfiniteInteger.calculateMaxBigIntegerAsInfiniteInteger();
      assertEquals(MutableInfiniteInteger.calculateMaxBigInteger(),
            mutableInfiniteInteger.bigIntegerValueExact());  //let throw on test fail

      try
      {
         mutableInfiniteInteger.add(1).bigIntegerValueExact();
         fail("Did not throw when > BigInteger.");
      }
      catch (ArithmeticException e) {}
   }

   @Test
   public void compareTo()
   {
      //don't use hamcrest for these because they would use .equals
      final MutableInfiniteInteger multiNode = MutableInfiniteInteger.valueOf(Long.MAX_VALUE).add(Long.MAX_VALUE).add(2);
      assertTrue(is(multiNode, EQUAL_TO, multiNode));  //same object
      mutableInfiniteInteger = MutableInfiniteInteger.valueOf(123);
      assertTrue(is(mutableInfiniteInteger.copy(), EQUAL_TO, mutableInfiniteInteger));  //different object same value
      mutableInfiniteInteger = MutableInfiniteInteger.valueOf(0);
      assertTrue(is(mutableInfiniteInteger.copy(), EQUAL_TO, mutableInfiniteInteger));  //different object same value

      //use hamcrest for rest to get a more meaningful failure message
      assertThat(MutableInfiniteInteger.valueOf(-5), lessThan(MutableInfiniteInteger.valueOf(5)));
      assertThat(MutableInfiniteInteger.valueOf(5), greaterThan(MutableInfiniteInteger.valueOf(-5)));
      assertThat(MutableInfiniteInteger.valueOf(10), greaterThan(MutableInfiniteInteger.valueOf(5)));
      assertThat(multiNode, greaterThan(MutableInfiniteInteger.valueOf(10)));  //left has more nodes
      assertThat(multiNode.copy().add(1), greaterThan(multiNode));  //same node count but different value

      mutableInfiniteInteger = MutableInfiniteInteger.valueOf(Integer.MAX_VALUE).add(1);
      assertThat(mutableInfiniteInteger.copy().add(1), greaterThan(mutableInfiniteInteger));  //make sure nodes are compared unsigned
   }

   @Test
   public void compareTo_special()
   {
      //same object (non-special value) is covered by the other compareTo test

      //don't use hamcrest for these because they would use .equals
      assertTrue(is(MutableInfiniteInteger.POSITIVE_INFINITY, EQUAL_TO, MutableInfiniteInteger.POSITIVE_INFINITY));
      assertTrue(is(MutableInfiniteInteger.NaN, EQUAL_TO, MutableInfiniteInteger.NaN));  //this is logical

      mutableInfiniteInteger = MutableInfiniteInteger.valueOf(5);

      //assert in both directions to test that the code correctly checks itself and the other
      assertThat(MutableInfiniteInteger.NaN, greaterThan(MutableInfiniteInteger.POSITIVE_INFINITY));  //odd but that's the ordering
      assertThat(MutableInfiniteInteger.POSITIVE_INFINITY, lessThan(MutableInfiniteInteger.NaN));
      assertThat(MutableInfiniteInteger.NaN, greaterThan(mutableInfiniteInteger));
      assertThat(mutableInfiniteInteger, lessThan(MutableInfiniteInteger.NaN));

      assertThat(MutableInfiniteInteger.NEGATIVE_INFINITY, lessThan(mutableInfiniteInteger));
      assertThat(mutableInfiniteInteger, greaterThan(MutableInfiniteInteger.NEGATIVE_INFINITY));

      assertThat(MutableInfiniteInteger.POSITIVE_INFINITY, greaterThan(mutableInfiniteInteger));
      assertThat(mutableInfiniteInteger, lessThan(MutableInfiniteInteger.POSITIVE_INFINITY));
   }

   @Test
   public void divide()
   {
      //simple case
      assertDivision(MutableInfiniteInteger.valueOf(10).divide(5), 1, new int[]{2}, new int[]{0});

      //previous bug caused by shifting down. Shifting affects remainder of small number
      assertDivision(MutableInfiniteInteger.valueOf(78).divide(10), 1, new int[]{7}, new int[]{8});

      //not so clean numbers: (2^32) / (2^5-1) = (2^32) / 31 = 0x842_1084 r 4
      assertDivision(MutableInfiniteInteger.valueOf(1L << 32).divide(31), 1, new int[]{0x842_1084}, new int[]{4});

      //simple negative with remainder
      assertDivision(MutableInfiniteInteger.valueOf(-11).divide(5), -1, new int[]{2}, new int[]{1});

      //multiple starting nodes that fit into long after shifting
      mutableInfiniteInteger = MutableInfiniteInteger.valueOf(Long.MAX_VALUE).add(1).multiplyByPowerOf2(32);
      //(2^95) / -(2^32) == -(2^63). That's what I'm testing
      assertDivision(mutableInfiniteInteger.divide(-1L << 32), -1, new int[]{0, Integer.MIN_VALUE}, new int[]{0});

      //multiple nodes for both that can't fit into long
      //(2^95)/(2^63) == (2^32)
      final MutableInfiniteInteger twoPower63 = MutableInfiniteInteger.valueOf(Long.MAX_VALUE).add(1);
      mutableInfiniteInteger = twoPower63.copy().multiplyByPowerOf2(32);
      assertDivision(mutableInfiniteInteger.copy().divide(twoPower63), 1, new int[]{0, 1}, new int[]{0});

      //again but with remainder
      //(2^95)/(2^63-1) == (2^32) r (2^32). Weird but true
      //same mutableInfiniteInteger
      assertDivision(mutableInfiniteInteger.divide(Long.MAX_VALUE), 1, new int[]{0, 1}, new int[]{0, 1});

      //previous bug caused by shifting down. Shifting affects remainder of large number
      mutableInfiniteInteger = MutableInfiniteInteger.valueOf(1).multiplyByPowerOf2(65).add(1).multiplyByPowerOf2(70);
      //10{65}10{70} / 20{70} => 10{65}1 / 2 = 10{64} r 10{70}
      assertDivision(mutableInfiniteInteger.divide(MutableInfiniteInteger.valueOf(2).multiplyByPowerOf2(70)), 1, new int[]{0, 0, 1},
            new int[]{0, 0, 0b1000000});
   }

   @Test
   public void divideByPowerOf2DropRemainder()
   {
      //simple case
      assertEqualNodes(MutableInfiniteInteger.valueOf(1024).divideByPowerOf2DropRemainder(3), 1, 128);

      //shift by x32
      mutableInfiniteInteger = MutableInfiniteInteger.valueOf(1).multiplyByPowerOf2(64).add(Long.MAX_VALUE);
      assertEqualNodes(mutableInfiniteInteger.divideByPowerOf2DropRemainder(64), 1, 1);

      //shift more than 32
      mutableInfiniteInteger = MutableInfiniteInteger.valueOf(1).multiplyByPowerOf2(32 * 3).subtract(1);  //3 nodes all high
      assertEqualNodes(mutableInfiniteInteger.divideByPowerOf2DropRemainder(35), 1, -1, 0x1FFF_FFFF);
   }

   @Test
   public void equals()
   {
      assertEquals(MutableInfiniteInteger.valueOf(10), MutableInfiniteInteger.valueOf(10));
      assertEquals(MutableInfiniteInteger.valueOf(5).add(5), MutableInfiniteInteger.valueOf(7).add(3));
      mutableInfiniteInteger = MutableInfiniteInteger.valueOf(Long.MAX_VALUE).add(Long.MAX_VALUE).add(2);
      assertEquals(mutableInfiniteInteger, mutableInfiniteInteger);
      MutableInfiniteInteger mutableInfiniteInteger = MutableInfiniteInteger.valueOf(123);
      assertEquals(mutableInfiniteInteger.copy(), mutableInfiniteInteger);
      assertNotEquals(this.mutableInfiniteInteger.copy().add(1), this.mutableInfiniteInteger);
   }

   //@Test
   //this only compiles if estimateSqrt is made public (see below)
   public void estimateSqrt()
   {
      //mutableInfiniteInteger = MutableInfiniteInteger.valueOf(0).estimateSqrt();
      assertEquals(mutableInfiniteInteger.intValue(), 0);

      for (int i = 1; i < 10_000_000; i++)
      {
         //mutableInfiniteInteger = MutableInfiniteInteger.valueOf(i).estimateSqrt();
         int actualLow = (int) Math.floor((Math.sqrt(i)));
         int actualHigh = (int) Math.ceil((Math.sqrt(i) * 2));
         assertThat(mutableInfiniteInteger.intValue(), Matchers.greaterThanOrEqualTo(actualLow));
         assertThat(mutableInfiniteInteger.intValue(), Matchers.lessThan(actualHigh));
      }
   }

   @Test
   public void fastPaths()
   {
      //TODO: more fast paths but move them into each other test
      assertSame(MutableInfiniteInteger.POSITIVE_INFINITY, MutableInfiniteInteger.POSITIVE_INFINITY.add(12));
      assertSame(MutableInfiniteInteger.NEGATIVE_INFINITY, MutableInfiniteInteger.NEGATIVE_INFINITY.add(12));
      assertSame(MutableInfiniteInteger.NaN, MutableInfiniteInteger.NaN.add(12));

      assertSame(MutableInfiniteInteger.POSITIVE_INFINITY, MutableInfiniteInteger.POSITIVE_INFINITY.add(BigInteger.TEN));
      assertSame(MutableInfiniteInteger.NEGATIVE_INFINITY, MutableInfiniteInteger.NEGATIVE_INFINITY.add(BigInteger.TEN));
      assertSame(MutableInfiniteInteger.NaN, MutableInfiniteInteger.NaN.add(BigInteger.TEN));

      MutableInfiniteInteger mutableInfiniteInteger = MutableInfiniteInteger.valueOf(12);
      assertSame(mutableInfiniteInteger, mutableInfiniteInteger.add(0));
      assertSame(mutableInfiniteInteger, mutableInfiniteInteger.add(MutableInfiniteInteger.valueOf(0)));

      //must use debugger to see if the fast path was used for these
      //these ones should not be moved since they are not visible
       /*
       MutableInfiniteInteger.valueOf(BigInteger.TEN);
    	/**/
   }

   @Test
   public void greatestCommonDivisor()
   {
      //list of low primes: 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37

      //simple case
      assertEqualNodes(MutableInfiniteInteger.valueOf(12).greatestCommonDivisor(10), 1, 2);

      //the answer is the lower one. also negatives
      assertEqualNodes(MutableInfiniteInteger.valueOf(-15).greatestCommonDivisor(5), 1, 5);
      assertEqualNodes(MutableInfiniteInteger.valueOf(-5).greatestCommonDivisor(15), 1, 5);

      //both prime
      assertEqualNodes(MutableInfiniteInteger.valueOf(7).greatestCommonDivisor(5), 1, 1);

      //with 0
      assertEqualNodes(MutableInfiniteInteger.valueOf(0).greatestCommonDivisor(5), 1, 5);

      //more than max long
      mutableInfiniteInteger = MutableInfiniteInteger.valueOf(7).multiplyByPowerOf2(64);
      MutableInfiniteInteger infiniteInteger2 = MutableInfiniteInteger.valueOf(11).multiplyByPowerOf2(64);
      assertEqualNodes(mutableInfiniteInteger.greatestCommonDivisor(infiniteInteger2), 1, 0, 0, 1);
   }

   @Test
   public void intValue()
   {
      assertEquals(5, MutableInfiniteInteger.valueOf(5).intValue());
      assertEquals(Integer.MAX_VALUE, MutableInfiniteInteger.valueOf(Integer.MAX_VALUE).intValue());
      mutableInfiniteInteger = MutableInfiniteInteger.valueOf(Integer.MAX_VALUE).add(Integer.MAX_VALUE).add(1);
      assertEquals(Integer.MAX_VALUE, mutableInfiniteInteger.intValue());

      assertEquals(-1, MutableInfiniteInteger.valueOf(-1).intValue());
      assertEquals(-Integer.MAX_VALUE, MutableInfiniteInteger.valueOf(-Integer.MAX_VALUE).intValue());
      mutableInfiniteInteger = MutableInfiniteInteger.valueOf(Integer.MAX_VALUE).add(Integer.MAX_VALUE).add(1).negate();
      assertEquals(-Integer.MAX_VALUE, mutableInfiniteInteger.intValue());
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
   public void iterateAllIntegers()
   {
      ListIterator<MutableInfiniteInteger> integerIterator = MutableInfiniteInteger.iterateAllIntegers();
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
   public void littleEndian()
   {
      assertEquals(MutableInfiniteInteger.valueOf(0), MutableInfiniteInteger.littleEndian(Collections.emptyIterator(), true));
      Iterator<Long> input = Arrays.asList(0L, 0L, 0L, 0L, 0L).iterator();
      assertEquals(MutableInfiniteInteger.valueOf(0), MutableInfiniteInteger.littleEndian(input, true));
      input = Arrays.asList(1L, 1L, Long.MAX_VALUE, 0L).iterator();
      assertEqualNodes(MutableInfiniteInteger.littleEndian(input, true), -1, 1, 0, 1, 0, -1, Integer.MAX_VALUE);
   }

   @Test
   public void longValue()
   {
      assertEquals(5, MutableInfiniteInteger.valueOf(5).longValue());
      assertEquals(Long.MAX_VALUE, MutableInfiniteInteger.valueOf(Long.MAX_VALUE).longValue());
      mutableInfiniteInteger = MutableInfiniteInteger.valueOf(Long.MAX_VALUE).add(Long.MAX_VALUE).add(1);
      assertEquals(Long.MAX_VALUE, mutableInfiniteInteger.longValue());

      assertEquals(-1, MutableInfiniteInteger.valueOf(-1).longValue());
      assertEquals(-Long.MAX_VALUE, MutableInfiniteInteger.valueOf(-Long.MAX_VALUE).longValue());
      mutableInfiniteInteger = MutableInfiniteInteger.valueOf(Long.MAX_VALUE).add(Long.MAX_VALUE).add(1).negate();
      assertEquals(-Long.MAX_VALUE, mutableInfiniteInteger.longValue());
   }

   @Test
   public void longValueExact()
   {
      mutableInfiniteInteger = MutableInfiniteInteger.valueOf(Long.MAX_VALUE);
      assertEquals(Long.MAX_VALUE, mutableInfiniteInteger.longValueExact());  //let throw on test fail

      mutableInfiniteInteger = mutableInfiniteInteger.add(1);
      try
      {
         mutableInfiniteInteger.longValueExact();
         fail("Did not throw when > signed long.");
      }
      catch (ArithmeticException e) {}

      try
      {
         mutableInfiniteInteger.add(Long.MAX_VALUE).add(1).longValueExact();
         fail("Did not throw when > unsigned long.");
      }
      catch (ArithmeticException e) {}
   }

   @Test
   public void multiply_long()
   {
      //simple case
      assertEqualNodes(MutableInfiniteInteger.valueOf(5).multiply(5), 1, 25);

      //more than max int
      assertEqualNodes(MutableInfiniteInteger.valueOf(4_294_967_295L).multiply(-2), -1, (int) 4_294_967_294L, 1);

      //more than max long
      assertEqualNodes(MutableInfiniteInteger.valueOf(Long.MAX_VALUE).multiply(2).add(2), 1, 0, 0, 1);

      //multi digit
      assertEqualNodes(MutableInfiniteInteger.valueOf(-Long.MAX_VALUE).multiply(-Long.MAX_VALUE), 1, 1, 0, -1, 0x3FFF_FFFF);
      //first pass should be: + 1, 7FFFFFFF, 7FFFFFFF
      //second pass should be: + 0, 80000001, 7FFFFFFF, 3FFFFFFF
   }

   @Test
   public void multiply_InfiniteInteger()
   {
      //simple case
      assertEqualNodes(MutableInfiniteInteger.valueOf(5).multiply(MutableInfiniteInteger.valueOf(5)), 1, 25);

      //more than max int
      assertEqualNodes(MutableInfiniteInteger.valueOf(4_294_967_295L).multiply(MutableInfiniteInteger.valueOf(-2)), -1,
            (int) 4_294_967_294L, 1);

      //more than max long
      assertEqualNodes(MutableInfiniteInteger.valueOf(Long.MAX_VALUE).multiply(2).add(MutableInfiniteInteger.valueOf(2)), 1, 0, 0, 1);

      //multi digit
      assertEqualNodes(MutableInfiniteInteger.valueOf(-Long.MAX_VALUE).multiply(MutableInfiniteInteger.valueOf(-Long.MAX_VALUE)), 1, 1, 0,
            -1, 0x3FFF_FFFF);
      //first pass should be: + 1, 7FFFFFFF, 7FFFFFFF
      //second pass should be: + 0, 80000001, 7FFFFFFF, 3FFFFFFF
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
   public void power()
   {
      //simple case
      assertEqualNodes(MutableInfiniteInteger.valueOf(3).power(3), 1, 27);

      //multiple ending nodes
      assertEqualNodes(MutableInfiniteInteger.valueOf(0x800).power(3), 1, 0, 2);

      //multiple starting nodes
      assertEqualNodes(MutableInfiniteInteger.valueOf(Long.MIN_VALUE).power(3), -1, 0, 0, 0, 0, 0, 0x2000_0000);
   }

   //@Test
   //this only compiles if sqrtCeil is made public (see below)
   //this test is only meaningful if the Math.sqrt delegation is commented out
   public void sqrtCeil()
   {
      for (int i = 0; i <= 1_200_000; i++)
      {
         //mutableInfiniteInteger = MutableInfiniteInteger.valueOf(i).sqrtCeil();
         int actualHigh = (int) Math.ceil((Math.sqrt(i)));
         assertEquals(mutableInfiniteInteger.intValue(), actualHigh);
      }
   }

   @Test
   public void streamAllIntegers()
   {
      Iterator<MutableInfiniteInteger> integerIterator = MutableInfiniteInteger.streamAllIntegers().iterator();
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
   public void streamFibonacciSequence()
   {
      Iterator<MutableInfiniteInteger> integerIterator = MutableInfiniteInteger.streamFibonacciSequence().iterator();
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
   public void subtract_long()
   {
      //simple case
      assertEqualNodes(MutableInfiniteInteger.valueOf(10).subtract(5), 1, 5);

      //simple negative case
      assertEqualNodes(MutableInfiniteInteger.valueOf(5).subtract(10), -1, 5);

      //more than max int
      assertEqualNodes(MutableInfiniteInteger.valueOf(4_294_967_295L).subtract(1), 1, (int) 4_294_967_294L);

      //more than max long
      mutableInfiniteInteger = MutableInfiniteInteger.valueOf(1).subtract(Long.MAX_VALUE).subtract(Long.MAX_VALUE).subtract(3);
      assertEqualNodes(mutableInfiniteInteger, -1, 0, 0, 1);

      //borrow big
      mutableInfiniteInteger = MutableInfiniteInteger.valueOf(Long.MAX_VALUE).add(Long.MAX_VALUE).add(2);
      mutableInfiniteInteger = mutableInfiniteInteger.subtract(1);
      assertEqualNodes(mutableInfiniteInteger, 1, -1, -1);

      //special case is negative but can't use Math.abs
      assertEqualNodes(MutableInfiniteInteger.valueOf(1).subtract(Long.MIN_VALUE), 1, 1, Integer.MIN_VALUE);
   }

   @Test
   public void subtract_InfiniteInteger()
   {
      //simple case
      assertEqualNodes(MutableInfiniteInteger.valueOf(10).subtract(MutableInfiniteInteger.valueOf(5)), 1, 5);

      //more than max int
      assertEqualNodes(MutableInfiniteInteger.valueOf(4_294_967_295L).subtract(MutableInfiniteInteger.valueOf(1)), 1, (int) 4_294_967_294L);

      //borrow big
      mutableInfiniteInteger = MutableInfiniteInteger.valueOf(Long.MAX_VALUE).add(Long.MAX_VALUE).add(2);
      mutableInfiniteInteger = mutableInfiniteInteger.subtract(MutableInfiniteInteger.valueOf(1));
      assertEqualNodes(mutableInfiniteInteger, 1, -1, -1);
   }

   /**
    * Happy path for {@link MutableInfiniteInteger#toString(int)}
    */
   @Test
   public void toString_returnsValue() throws Exception
   {
      assertEquals("ff", MutableInfiniteInteger.valueOf(255).toString(16));

      mutableInfiniteInteger = MutableInfiniteInteger.valueOf(Long.MAX_VALUE).add(1);
      assertEquals("8000000000000000", mutableInfiniteInteger.toString(16));
      mutableInfiniteInteger = MutableInfiniteInteger.valueOf(Long.MIN_VALUE).subtract(1);
      assertEquals("-1000000000000000000000000000000000000000000000000000000000000001", mutableInfiniteInteger.toString(2));
      mutableInfiniteInteger = MutableInfiniteInteger.valueOf(Long.MAX_VALUE).add(1);
      assertEquals("2021110011022210012102010021220101220222", mutableInfiniteInteger.toString(3));
      mutableInfiniteInteger = MutableInfiniteInteger.valueOf(Long.MIN_VALUE).subtract(1);
      assertEquals("-2021110011022210012102010021220101221000", mutableInfiniteInteger.toString(3));

      mutableInfiniteInteger = MutableInfiniteInteger.valueOf(Long.MAX_VALUE).add(1);
      assertEquals("9223372036854775808", mutableInfiniteInteger.toString(10));
      mutableInfiniteInteger = MutableInfiniteInteger.valueOf(Long.MIN_VALUE).subtract(1);
      assertEquals("-9223372036854775809", mutableInfiniteInteger.toString(10));
   }

   //@Test
   public void speed() throws Exception
   {
      mutableInfiniteInteger = MutableInfiniteInteger.valueOf(Long.MIN_VALUE).subtract(1);
      long start = System.nanoTime();
      assertEquals("-1000000000000000000000000000000000000000000000000000000000000001", mutableInfiniteInteger.toString(2));
      long end = System.nanoTime();
      System.out.println(Duration.ofNanos(end - start));

      start = System.nanoTime();
      assertEquals("-9223372036854775809", mutableInfiniteInteger.toString(10));
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
      catch (IllegalArgumentException actual)
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
      catch (IllegalArgumentException actual)
      {
         assertEquals("radix > 62 (was 63)", actual.getMessage());
      }
   }

   /**
    * Test for {@link MutableInfiniteInteger#toString(int)}
    */
   @Test
   public void toString_returnsInfinitySymbol_givenPositiveInfinity() throws Exception
   {
      assertEquals("∞", MutableInfiniteInteger.POSITIVE_INFINITY.toString(1));
   }

   /**
    * Test for {@link MutableInfiniteInteger#toString(int)}
    */
   @Test
   public void toString_returnsInfinitySymbol_givenNegativeInfinity() throws Exception
   {
      assertEquals("-∞", MutableInfiniteInteger.NEGATIVE_INFINITY.toString(1));
   }

   /**
    * Test for {@link MutableInfiniteInteger#toString(int)}
    */
   @Test
   public void toString_returnsNotIntegerSymbols_givenNan() throws Exception
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
      catch (IllegalArgumentException actual)
      {
         assertEquals("This number in base 1 would exceed max string length.", actual.getMessage());
      }
   }

   @Test
   public void valueOf_BigInteger()
   {
      assertEquals(MutableInfiniteInteger.valueOf(5), MutableInfiniteInteger.valueOf(BigInteger.valueOf(5)));
      assertEquals(MutableInfiniteInteger.valueOf(-5), MutableInfiniteInteger.valueOf(BigInteger.valueOf(-5)));
      assertEquals(MutableInfiniteInteger.valueOf(Long.MAX_VALUE - 5),
            MutableInfiniteInteger.valueOf(BigInteger.valueOf(Long.MAX_VALUE - 5)));

      mutableInfiniteInteger = MutableInfiniteInteger.valueOf(Long.MAX_VALUE).add(Long.MAX_VALUE).add(2).negate();
      BigInteger input = BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.valueOf(Long.MAX_VALUE)).add(BigInteger.valueOf(2)).negate();
      assertEquals(mutableInfiniteInteger, MutableInfiniteInteger.valueOf(input));
   }

   @Test
   public void valueOf_long()
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

   private void assertDivision(IntegerQuotient<MutableInfiniteInteger> divisionResult, int wholeSign, int[] wholeNodes,
                               int[] remainderNodes)
   {
      assertEqualNodes(divisionResult.getWholeResult(), wholeSign, wholeNodes);
      if (remainderNodes.length == 1 && remainderNodes[0] == 0) assertEqualNodes(divisionResult.getRemainder(), 0, 0);
      else assertEqualNodes(divisionResult.getRemainder(), 1, remainderNodes);
   }

   private void assertEqualNodes(MutableInfiniteInteger infiniteIntegerParam, int expectedSignum, int... expectedNodes)
   {
      assertEquals(generateInfiniteIntegerString(expectedSignum, expectedNodes), infiniteIntegerParam.toDebuggingString());
   }

   //tightly coupled with MutableInfiniteInteger.toDebuggingString()
   private String generateInfiniteIntegerString(int signum, int... nodeValues)
   {
      if (signum == 0) return "0";
      String returnValue = "+ ";
      if (signum == -1) returnValue = "- ";
      StringBuilder stringBuilder = new StringBuilder(returnValue);
      for (int node : nodeValues)
      {
         stringBuilder.append(Integer.toHexString(node).toUpperCase());
         stringBuilder.append(", ");
      }
      return stringBuilder.toString();
   }

}
