package com.github.skySpiral7.java.infinite.util;

public enum BitWiseUtil
{
   ;  //no instances

   public static final long HIGH_64 = -1;  //== 0xFFFF_FFFF__FFFF_FFFFL

   /**
    * Only works for unsigned numbers. So 0 returns true, 2 returns true, Long.MIN_VALUE returns true, -2 returns false.
    */
   public static boolean isPowerOf2(final long x){return ((x & -x) == x);}

   /**
    * Works for negative (including Long.MIN_VALUE) and unsigned values.
    *
    * @return true if abs(x)%2==0
    */
   public static boolean isEven(final long x){return ((x & 1) == 0);}

   /**
    * Works for negative (including Long.MIN_VALUE) and unsigned values.
    *
    * @return true if abs(x)%2==1
    */
   public static boolean isOdd(final long x){return ((x & 1) == 1);}

   public static long getLowestNBits(final long value, final int nBitsToKeep)
   {
      if (nBitsToKeep > 64) throw new IllegalArgumentException();
      if (nBitsToKeep == 64) return value;
      if (nBitsToKeep == 0) return 0;
      final int nBitsRemoved = 64 - nBitsToKeep;
      final long bitMask = (BitWiseUtil.HIGH_64 >>> nBitsRemoved);
      return (value & bitMask);
   }

   public static long getHighestNBits(final long value, final int nBitsToKeep)
   {
      if (nBitsToKeep > 64) throw new IllegalArgumentException();
      if (nBitsToKeep == 64) return value;
      if (nBitsToKeep == 0) return 0;
      final int nBitsRemoved = 64 - nBitsToKeep;
      final long bitMask = (BitWiseUtil.HIGH_64 << nBitsRemoved);
      return (value & bitMask);
   }

   public static int getHighestNBits(final int value, final int nBitsToKeep)
   {
      if (nBitsToKeep > 32) throw new IllegalArgumentException();
      if (nBitsToKeep == 32) return value;
      if (nBitsToKeep == 0) return 0;
      final int nBitsRemoved = 32 - nBitsToKeep;
      int bitMask = (int) BitWiseUtil.HIGH_64;
      bitMask <<= nBitsRemoved;
      return (value & bitMask);
   }

   public static short getHighestNBits(final short value, final int nBitsToKeep)
   {
      if (nBitsToKeep > 16) throw new IllegalArgumentException();
      if (nBitsToKeep == 16) return value;
      if (nBitsToKeep == 0) return 0;
      final int nBitsRemoved = 16 - nBitsToKeep;
      short bitMask = (short) BitWiseUtil.HIGH_64;
      bitMask <<= nBitsRemoved;
      return ((short) (value & bitMask));
   }

   public static byte getHighestNBits(final byte value, final int nBitsToKeep)
   {
      if (nBitsToKeep > 8) throw new IllegalArgumentException();
      if (nBitsToKeep == 8) return value;
      if (nBitsToKeep == 0) return 0;
      final int nBitsRemoved = 8 - nBitsToKeep;
      byte bitMask = (byte) BitWiseUtil.HIGH_64;
      bitMask <<= nBitsRemoved;
      return ((byte) (value & bitMask));
   }

   public static long multiplyByPowerOf2(final long value, final int exponent){return (value << exponent);}

   public static long divideByPowerOf2(final long value, final int exponent){return (value >> exponent);}

   public static int bigEndianBytesToInteger(final byte[] input)
   {
      if (input.length != 4) throw new IllegalArgumentException("expected length 4, got: " + input.length);
      int result = (input[0] & 0xff);
      for (int i = 1; i < input.length; ++i)
      {
         result <<= 8;
         result |= (input[i] & 0xff);
      }
      return result;
   }
}
