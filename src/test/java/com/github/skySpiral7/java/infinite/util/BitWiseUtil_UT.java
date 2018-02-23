package com.github.skySpiral7.java.infinite.util;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BitWiseUtil_UT
{
   @Test
   public void isPowerOf2() throws Exception
   {
      assertTrue("0", BitWiseUtil.isPowerOf2(0));
      for (int i = 0; i <= 62; ++i)
      {
         //1L<<0 is 1, 1L<<62 is 4611686018427387904 (max positive)
         //1L<<63 == Long.MIN_VALUE (not in loop), 1L<<64 == 1 because 64 is considered 0
         final long testValue = 1L << i;
         assertTrue("" + testValue, BitWiseUtil.isPowerOf2(testValue));
      }
      assertTrue("Long.MIN_VALUE", BitWiseUtil.isPowerOf2(Long.MIN_VALUE));

      assertFalse("-2", BitWiseUtil.isPowerOf2(-2));
      assertFalse("6", BitWiseUtil.isPowerOf2(6));
   }

   @Test
   public void isEven() throws Exception
   {
      assertTrue("2", BitWiseUtil.isEven(2));
      assertTrue("100", BitWiseUtil.isEven(100));
      assertTrue("-10", BitWiseUtil.isEven(-10));
      assertTrue("Long.MIN_VALUE", BitWiseUtil.isEven(Long.MIN_VALUE));

      assertFalse("7", BitWiseUtil.isEven(7));
      assertFalse("-3", BitWiseUtil.isEven(-3));
   }

   @Test
   public void isOdd() throws Exception
   {
      assertTrue("1", BitWiseUtil.isOdd(1));
      assertTrue("7", BitWiseUtil.isOdd(7));
      assertTrue("-3", BitWiseUtil.isOdd(-3));

      assertFalse("Long.MIN_VALUE", BitWiseUtil.isOdd(Long.MIN_VALUE));
      assertFalse("4", BitWiseUtil.isOdd(4));
      assertFalse("-6", BitWiseUtil.isOdd(-6));
   }
}
