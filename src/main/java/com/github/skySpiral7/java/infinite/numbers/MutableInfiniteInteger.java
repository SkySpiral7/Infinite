package com.github.skySpiral7.java.infinite.numbers;

import com.github.skySpiral7.java.Copyable;
import com.github.skySpiral7.java.infinite.dataStructures.InfinitelyLinkedList;
import com.github.skySpiral7.java.infinite.exceptions.WillNotFitException;
import com.github.skySpiral7.java.infinite.util.BitWiseUtil;
import com.github.skySpiral7.java.infinite.util.RadixUtil;
import com.github.skySpiral7.java.iterators.DequeNodeIterator;
import com.github.skySpiral7.java.iterators.DescendingListIterator;
import com.github.skySpiral7.java.iterators.ReadOnlyListIterator;
import com.github.skySpiral7.java.numbers.NumberFormatException;
import com.github.skySpiral7.java.pojo.DequeNode;
import com.github.skySpiral7.java.staticSerialization.ObjectStreamReader;
import com.github.skySpiral7.java.staticSerialization.ObjectStreamWriter;
import com.github.skySpiral7.java.staticSerialization.StaticSerializable;
import com.github.skySpiral7.java.util.ComparableSugar;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Random;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.github.skySpiral7.java.pojo.Comparison.EQUAL_TO;
import static com.github.skySpiral7.java.pojo.Comparison.GREATER_THAN;
import static com.github.skySpiral7.java.pojo.Comparison.GREATER_THAN_OR_EQUAL_TO;
import static com.github.skySpiral7.java.pojo.Comparison.LESS_THAN;
import static com.github.skySpiral7.java.pojo.Comparison.LESS_THAN_OR_EQUAL_TO;
import static com.github.skySpiral7.java.util.ComparableSugar.THIS_EQUAL;
import static com.github.skySpiral7.java.util.ComparableSugar.THIS_GREATER;
import static com.github.skySpiral7.java.util.ComparableSugar.THIS_LESSER;
import static com.github.skySpiral7.java.util.ComparableSugar.is;
import static com.github.skySpiral7.java.util.ComparableSugar.isComparisonResult;

/**
 * A Mutable version of InfiniteInteger for the sake of speed.
 *
 * @see InfiniteInteger
 */
//TODO: move all this class doc to the Abstract
public final class MutableInfiniteInteger extends AbstractInfiniteInteger<MutableInfiniteInteger>
   implements Copyable<MutableInfiniteInteger>, StaticSerializable
{
   private static final long serialVersionUID = 1L;

   /**
    * Common abbreviation for "not a number". This constant is the result of invalid math such as 0/0.
    * Note that this is a normal object such that <code>(InfiniteInteger.NaN == InfiniteInteger.NaN)</code> is
    * always true. Therefore it is logically correct unlike the floating point unit's NaN.
    * <p>
    * This value is immutable.
    */
   public static final MutableInfiniteInteger NaN = new MutableInfiniteInteger(false);
   /**
    * +∞ is a concept rather than a number and can't be the result of math involving finite numbers.
    * It is defined for completeness and behaves as expected with math resulting in ±∞ or NaN.
    * <p>
    * This value is immutable.
    */
   public static final MutableInfiniteInteger POSITIVE_INFINITY = new MutableInfiniteInteger(false);
   /**
    * -∞ is a concept rather than a number and can't be the result of math involving finite numbers.
    * It is defined for completeness and behaves as expected with math resulting in ±∞ or NaN.
    * <p>
    * This value is immutable.
    */
   public static final MutableInfiniteInteger NEGATIVE_INFINITY = new MutableInfiniteInteger(true);

   /**
    * Little endian: the first node is the least significant.
    */
   private transient DequeNode<Integer> magnitudeHead;
   private transient boolean isNegative;

   /**
    * This constructor is used to make special constants.
    * This makes the head null which is something that is otherwise not possible.
    *
    * @param isNegative used for positive and negative infinity. Meaningless to NaN.
    */
   private MutableInfiniteInteger(final boolean isNegative)
   {
      magnitudeHead = null;
      this.isNegative = isNegative;
   }

   /**
    * This constructor is used to create an InfiniteInteger of a small size.
    * This is the only public constructor because it is the only one that can't violate
    * invariants.
    *
    * @param baseValue the desired numeric value
    * @see #valueOf(long)
    */
   public MutableInfiniteInteger(long baseValue)
   {
      if (baseValue == Long.MIN_VALUE)  //Math.abs isn't possible for this
      {
         isNegative = true;
         magnitudeHead = DequeNode.Factory.createStandAloneNode(0);
         DequeNode.Factory.createNodeAfter(magnitudeHead, Integer.MIN_VALUE);
      }
      else
      {
         isNegative = (baseValue < 0);
         baseValue = Math.abs(baseValue);
         magnitudeHead = DequeNode.Factory.createStandAloneNode((int) baseValue);
         baseValue >>>= 32;
         if (baseValue > 0) DequeNode.Factory.createNodeAfter(magnitudeHead, ((int) baseValue));
      }
   }

   /**
    * Converts a long value to an InfiniteInteger. This is simply an alias for the constructor.
    *
    * @param value the desired numeric value
    * @return a new InfiniteInteger
    * @see #MutableInfiniteInteger(long)
    */
   public static MutableInfiniteInteger valueOf(final long value)
   {
      return new MutableInfiniteInteger(value);
   }
   //TODO: make a valueOf(double) for Infinity and NaN. otherwise cast to long

   //new MutableInfiniteInteger(BigInteger) doesn't exist because valueOf(BigInteger) uses fast paths (and I don't want a slow constructor)

   /**
    * Converts a BigInteger value to an MutableInfiniteInteger.
    * Conversion is O(n) and may be slow for large values of the parameter.
    *
    * @param value the desired numeric value
    * @return a new MutableInfiniteInteger
    */
   public static MutableInfiniteInteger valueOf(final BigInteger value)
   {
      if (value.equals(BigInteger.ZERO)) return new MutableInfiniteInteger(0);
      final boolean willBeNegative = (value.signum() == -1);  //don't need to use < 0 because of signum's promise
      final BigInteger absValue = value.abs();

      if (is(absValue, LESS_THAN_OR_EQUAL_TO, BigInteger.valueOf(Long.MAX_VALUE)))
         return MutableInfiniteInteger.valueOf(value.longValue());
      //if abs fits in a signed long then delegate (original value)

      final byte[] bigEndianBytes = absValue.toByteArray();
      final byte[] littleEndianBytes = new byte[bigEndianBytes.length];
      for (int endianIndex = 0; endianIndex < bigEndianBytes.length; endianIndex++)
      {
         littleEndianBytes[bigEndianBytes.length - 1 - endianIndex] = bigEndianBytes[endianIndex];
      }

      final MutableInfiniteInteger result = MutableInfiniteInteger.valueOf(0);
      DequeNode<Integer> magnitudeTail = result.magnitudeHead;
      int byteIndex = 0;
      while (byteIndex < littleEndianBytes.length)
      {
         final byte firstByte = (byteIndex >= littleEndianBytes.length) ? 0 : littleEndianBytes[byteIndex];
         ++byteIndex;
         final byte secondByte = (byteIndex >= littleEndianBytes.length) ? 0 : littleEndianBytes[byteIndex];
         ++byteIndex;
         final byte thirdByte = (byteIndex >= littleEndianBytes.length) ? 0 : littleEndianBytes[byteIndex];
         ++byteIndex;
         final byte fourthByte = (byteIndex >= littleEndianBytes.length) ? 0 : littleEndianBytes[byteIndex];
         ++byteIndex;
         final int integerValue = BitWiseUtil.bigEndianBytesToInteger(new byte[]{fourthByte, thirdByte, secondByte, firstByte});
         magnitudeTail = DequeNode.Factory.createNodeAfter(magnitudeTail, integerValue);
      }
      //the first node created was a leading 0 placeholder so remove it
      result.magnitudeHead = result.magnitudeHead.getNext();
      result.magnitudeHead.getPrev().remove();
      if (magnitudeTail.getData() == 0) magnitudeTail.remove();  //BigInteger sometimes returns a leading 0. Remove it.

      result.isNegative = willBeNegative;
      return result;
   }

   /**
    * Simply calls toMutableInfiniteInteger. This exists for orthogonality.
    *
    * @see InfiniteInteger#toMutableInfiniteInteger()
    */
   public static MutableInfiniteInteger valueOf(final InfiniteInteger value)
   {
      return value.toMutableInfiniteInteger();
   }

   /**
    * Simply calls copy. This exists for orthogonality.
    *
    * @see #copy()
    */
   public static MutableInfiniteInteger valueOf(final MutableInfiniteInteger value)
   {
      return value.copy();
   }

   /**
    * Simply calls parseString (radix 10). This exists for orthogonality.
    *
    * @see #parseString(String, int)
    */
   public static MutableInfiniteInteger valueOf(final String value)
   {
      return MutableInfiniteInteger.parseString(value, 10);
   }

   /**
    * Simply calls parseString. This exists for orthogonality.
    *
    * @see #parseString(String, int)
    */
   public static MutableInfiniteInteger valueOf(final String value, final int radix)
   {
      return MutableInfiniteInteger.parseString(value, radix);
   }

   /**
    * Simply calls parseString with radix 10. This exists for orthogonality and ease of use.
    *
    * @see #parseString(String, int)
    */
   public static MutableInfiniteInteger parseString(final String value)
   {
      return MutableInfiniteInteger.parseString(value, 10);
   }

   /**
    * <p>Parses the inputString as a MutableInfiniteInteger in the radix specified.
    * See {@link RadixUtil#toString(long, int)} for a description of legal characters per radix.
    * See {@link RadixUtil#parseLong(String, int)} for more details.</p>
    *
    * <p>Note the special values of ∞, -∞, and ∉ℤ (for NaN) can be parsed given any valid
    * radix.</p>
    *
    * @param inputString the String to be parsed
    * @param radix       the number base
    * @return the MutableInfiniteInteger that inputString represents
    * @throws NullPointerException     if inputString is null
    * @throws NumberFormatException    excluding a leading + or - if inputString is empty (and not base 1)
    *                                  or contains illegal characters for that radix
    * @throws IllegalArgumentException {@code if(radix > 62 || radix < 1)}
    * @see Long#parseLong(String, int)
    * @see RadixUtil#toString(long, int)
    * @see RadixUtil#parseLong(String, int)
    */
   public static MutableInfiniteInteger parseString(final String inputString, final int radix)
   {
      RadixUtil.enforceStandardRadix(radix);
      String workingString = inputString.trim();

      //leading + is valid even though this class won't generate it
      if ("∞".equals(workingString) || "+∞".equals(workingString)) return MutableInfiniteInteger.POSITIVE_INFINITY;
      if ("-∞".equals(workingString)) return MutableInfiniteInteger.NEGATIVE_INFINITY;
      if ("∉ℤ".equals(workingString)) return MutableInfiniteInteger.NaN;

      //a string of radix 1 will always fit in long so just call RadixUtil
      if (1 == radix) return MutableInfiniteInteger.valueOf(RadixUtil.parseLong(workingString, radix));

      if (!workingString.matches("^[+-]?[a-zA-Z0-9]+$")) throw NumberFormatException.forInputString(inputString);

      final boolean isNegative = (workingString.charAt(0) == '-');
      if (isNegative) workingString = workingString.substring(1);
      else if (workingString.charAt(0) == '+') workingString = workingString.substring(1);

      MutableInfiniteInteger result;
      if (BitWiseUtil.isPowerOf2(radix))
         result = MutableInfiniteInteger.parseStringPowerOf2(inputString, workingString, radix);
      else result = MutableInfiniteInteger.parseStringSlow(inputString, workingString, radix);

      if (isNegative) result = result.negate();  //negate ignores -0
      return result;
   }

   /**
    * This should in theory be faster than parseStringSlow since it uses bit shifting.
    */
   private static MutableInfiniteInteger parseStringPowerOf2(final String originalString, final String workingString, final int radix)
   {
      MutableInfiniteInteger result = new MutableInfiniteInteger(0);
      final int shiftSize = Integer.numberOfTrailingZeros(radix);  //lg(radix) only because radix is power of 2
      for (int i = 0; i < workingString.length(); i++)
      {
         final int digitValue = RadixUtil.getDigitValue(workingString.charAt(i), radix);
         if (-1 == digitValue) throw NumberFormatException.forInputRadix(originalString, radix);
         //leading 0s will do nothing
         result = result.multiplyByPowerOf2(shiftSize);  //first pass is no-op
         result = result.add(digitValue);
      }
      return result;
   }

   private static MutableInfiniteInteger parseStringSlow(final String originalString, final String workingString, final int radix)
   {
      MutableInfiniteInteger result = new MutableInfiniteInteger(0);
      for (int i = 0; i < workingString.length(); i++)
      {
         final int digitValue = RadixUtil.getDigitValue(workingString.charAt(i), radix);
         if (-1 == digitValue) throw NumberFormatException.forInputRadix(originalString, radix);
         //leading 0s will do nothing
         result = result.multiply(radix);  //first pass is no-op
         result = result.add(digitValue);
      }
      return result;
   }

   /**
    * Converts a MutableInfiniteInteger to an InfiniteInteger.
    *
    * @return a new InfiniteInteger or a defined singleton
    */
   public InfiniteInteger toInfiniteInteger()
   {
      return InfiniteInteger.valueOf(this);
   }

   /**
    * Converts an array of UNSIGNED longs into a new InfiniteInteger.
    * The elements must be in little endian order. This method delegates to littleEndian(Iterator, boolean).
    * An empty array is considered 0.
    *
    * @param valueArray the unsigned elements in little endian order
    * @param isNegative whether the resulting InfiniteInteger should be negative or not
    * @return a new InfiniteInteger representing the indicated number
    * @see #littleEndian(Iterator, boolean)
    * @see #bigEndian(long[], boolean)
    */
   public static MutableInfiniteInteger littleEndian(final long[] valueArray, final boolean isNegative)
   {
      final Long[] wrappedValues = new Long[valueArray.length];
      for (int i = 0; i < valueArray.length; i++){wrappedValues[i] = Long.valueOf(valueArray[i]);}
      return MutableInfiniteInteger.littleEndian(Arrays.asList(wrappedValues).listIterator(), isNegative);
   }

   /**
    * Converts an array of UNSIGNED longs into a new InfiniteInteger.
    * The elements must be in big endian order. This method ultimately delegates to littleEndian(Iterator, boolean).
    * An empty array is considered 0.
    *
    * @param valueArray the unsigned elements in big endian order
    * @param isNegative whether the resulting InfiniteInteger should be negative or not
    * @return a new InfiniteInteger representing the indicated number
    * @see #bigEndian(ListIterator, boolean)
    * @see #littleEndian(long[], boolean)
    * @see #littleEndian(Iterator, boolean)
    */
   public static MutableInfiniteInteger bigEndian(final long[] valueArray, final boolean isNegative)
   {
      final Long[] wrappedValues = new Long[valueArray.length];
      for (int i = 0; i < valueArray.length; i++){wrappedValues[i] = Long.valueOf(valueArray[i]);}
      return MutableInfiniteInteger.bigEndian(Arrays.asList(wrappedValues).listIterator(), isNegative);
   }

   /**
    * <p>Converts an iterator of UNSIGNED longs into a new InfiniteInteger.
    * The elements must be in little endian order.</p>
    *
    * <p>The iterator must not return a null element, the meaning of which would be ambiguous.
    * The iterator can't be infinite since this method aggregates the values (it would also be meaningless).
    * An empty iterator is considered 0.</p>
    *
    * @param valueIterator the unsigned elements in little endian order
    * @param isNegative    whether the resulting InfiniteInteger should be negative or not
    * @return a new InfiniteInteger representing the indicated number
    * @see #littleEndian(long[], boolean)
    * @see #bigEndian(ListIterator, boolean)
    */
   public static MutableInfiniteInteger littleEndian(final Iterator<Long> valueIterator, final boolean isNegative)
   {
      if (!valueIterator.hasNext()) return new MutableInfiniteInteger(0);
      final MutableInfiniteInteger result = MutableInfiniteInteger.valueOf(valueIterator.next());
      result.isNegative = isNegative;
      DequeNode<Integer> cursor = result.magnitudeHead;
      if (cursor.getNext() == null) cursor = DequeNode.Factory.createNodeAfter(cursor, 0);
      while (valueIterator.hasNext())
      {
         long value = valueIterator.next();
         cursor = DequeNode.Factory.createNodeAfter(cursor, (int) value);
         value >>>= 32;
         cursor = DequeNode.Factory.createNodeAfter(cursor, (int) value);
      }
      while (cursor.getData().intValue() == 0)  //cursor is always at the last node. so remove leading 0s
      {
         cursor = cursor.getPrev();
         if (cursor == null) return new MutableInfiniteInteger(0);  //if the last and only node was 0
         cursor.getNext().remove();
      }
      return result;
   }

   /**
    * <p>Converts an iterator of UNSIGNED longs into a new InfiniteInteger.
    * The elements must be in big endian order. Note that the iterator
    * must be a list iterator because it must be read backwards.
    * This method delegates to littleEndian(Iterator, boolean).</p>
    *
    * <p>The iterator must not return a null element, the meaning of which would be ambiguous.
    * The iterator can't be infinite since this method aggregates the values (it would also be meaningless).
    * An empty iterator is considered 0.</p>
    *
    * @param valueIterator the unsigned elements in big endian order
    * @param isNegative    whether the resulting InfiniteInteger should be negative or not
    * @return a new InfiniteInteger representing the indicated number
    * @see #bigEndian(long[], boolean)
    * @see #littleEndian(Iterator, boolean)
    */
   public static MutableInfiniteInteger bigEndian(final ListIterator<Long> valueIterator, final boolean isNegative)
   {
      return MutableInfiniteInteger.littleEndian(DescendingListIterator.iterateBackwardsFromEnd(valueIterator), isNegative);
   }

   /**
    * Constructs a randomly generated InfiniteInteger, uniformly distributed over
    * the range 0 to 2^(32 * {@code nodeCount}), inclusive.
    * The uniformity of the distribution assumes the Random class is a fair source of random
    * bits. Note that the result may be positive or negative.
    *
    * @return NaN if {@code nodeCount} < 1 otherwise a new random number is returned.
    * @see Random
    */
   public static MutableInfiniteInteger random(final MutableInfiniteInteger nodeCount)
   {
      return MutableInfiniteInteger.random(nodeCount, new Random());
   }

   /**
    * Constructs a randomly generated InfiniteInteger, uniformly distributed over
    * the range 0 to 2^(32 * {@code nodeCount}), inclusive.
    * The uniformity of the distribution assumes that a fair source of random
    * bits is provided in {@code random}. Note that the result
    * may be positive or negative.
    *
    * @param random source of randomness to be used in computing the new
    *               InfiniteInteger.
    * @return NaN if {@code nodeCount} < 1 otherwise a new random number is returned.
    */
   public static MutableInfiniteInteger random(final MutableInfiniteInteger nodeCount, final Random random)
   {
      if (!nodeCount.isFinite()) return MutableInfiniteInteger.NaN;
      if (is(nodeCount, LESS_THAN, MutableInfiniteInteger.valueOf(1))) return MutableInfiniteInteger.NaN;

      MutableInfiniteInteger remainingNodes = nodeCount.copy();
      MutableInfiniteInteger result = new MutableInfiniteInteger(0);
      while (!remainingNodes.equalValue(0))
      {
         //next int covers all possible unsigned 2^32
         result.magnitudeHead.setData(random.nextInt());
         result = result.multiplyByPowerOf2(32);
         remainingNodes = remainingNodes.subtract(1);
      }
      result = result.divideByPowerOf2DropRemainder(32);  //remove trailing 0 node
      if (!result.equalValue(0)) result.isNegative = random.nextBoolean();  //don't allow negative 0
      return result;
   }

   /**
    * This method returns an infinite stream of all integers.
    * NaN is not included in the stream and ±∞ is unreachable.
    * The stream is logically truely infinite (will never loop around or overflow)
    * but hardware will eventually run out of memory.
    * The stream's order is: 0, 1, -1, 2, -2, 3, -3, 4, -4...
    *
    * @return an infinite stream of all integers
    */
   public static Stream<MutableInfiniteInteger> streamAllIntegers()
   {
      return Stream.iterate(new MutableInfiniteInteger(0), (MutableInfiniteInteger previous) -> {
         if (previous.equalValue(0)) return MutableInfiniteInteger.valueOf(1);
         if (previous.isNegative) return previous.copy().abs().add(1);
         return previous.copy().negate();
      });
   }

   /**
    * <p>This method returns an infinite iterator of all integers.
    * NaN is not included in the stream and ±∞ is unreachable.
    * The stream is logically truely infinite (will never loop around or overflow)
    * but hardware will eventually run out of memory.</p>
    *
    * <p>The iterator starts after 0 such that calling next() will return 1 and previous() will return 0.
    * Calling next or previous index will return the intValue(). Set, add, and remove can't be called because
    * it is read only.</p>
    *
    * @return an infinite iterator of all integers
    * @see #intValue()
    * @see ReadOnlyListIterator
    */
   public static ReadOnlyListIterator<MutableInfiniteInteger> iterateAllIntegers()
   {
      return new ReadOnlyListIterator<>(new ListIterator<MutableInfiniteInteger>()
      {
         private MutableInfiniteInteger nextElement = MutableInfiniteInteger.valueOf(1);

         @Override
         public boolean hasNext(){return true;}

         @Override
         public boolean hasPrevious(){return true;}

         @Override
         public MutableInfiniteInteger next()
         {
            final MutableInfiniteInteger current = nextElement.copy();
            nextElement = nextElement.add(1);
            return current;
         }

         @Override
         public MutableInfiniteInteger previous()
         {
            return nextElement = nextElement.subtract(1);
         }

         @Override
         public int nextIndex()
         {
            return nextElement.intValue();
         }

         @Override
         public int previousIndex()
         {
            return nextElement.intValue() - 1;
         }

         //will be replaced by ReadOnlyListIterator to throw:
         @Override
         public void remove(){}

         @Override
         public void set(final MutableInfiniteInteger e){}

         @Override
         public void add(final MutableInfiniteInteger e){}
      });
   }

   /**
    * <p>This method returns an infinite stream all numbers in the Fibonacci Sequence.
    * The stream starts with 0 which is known as the zeroth element in the sequence.
    * The stream is logically truely infinite (will never loop around or overflow)
    * but hardware will eventually run out of memory.</p>
    *
    * <p>The Fibonacci Sequence aka golden sequence aka Lame's Sequence is defined by starting with
    * f(0)=0 and f(1)=1 and f(n)=f(n-2)+f(n-1).
    * Therefore the sequence starts out with: 0, 1, 1, 2, 3, 5, 8, 13, 21, 34...</p>
    *
    * @return an infinite stream of the Fibonacci Sequence
    */
   public static Stream<MutableInfiniteInteger> streamFibonacciSequence()
   {
      final Iterator<MutableInfiniteInteger> iterator = new Iterator<>()
      {
         private MutableInfiniteInteger previous = null;
         private MutableInfiniteInteger back2 = null;

         @Override
         public boolean hasNext(){return true;}

         @Override
         public MutableInfiniteInteger next()
         {
            final MutableInfiniteInteger next;
            if (previous == null) next = MutableInfiniteInteger.valueOf(0);
            else if (back2 == null) next = MutableInfiniteInteger.valueOf(1);
            else next = previous.copy().add(back2);
            back2 = previous;
            previous = next;
            return next;
         }
      };
      return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED | Spliterator.IMMUTABLE), false);
   }

   /**
    * Entire code: <blockquote>{@code return (float) longValue();}</blockquote>
    *
    * @see #longValue()
    */
   @Override
   public float floatValue(){return (float) longValue();}

   /**
    * Entire code: <blockquote>{@code return (double) longValue();}</blockquote>
    *
    * @see #longValue()
    */
   @Override
   public double doubleValue(){return (double) longValue();}
   //TODO: I can have the floating points return Infinity or NaN

   /**
    * This method returns the least significant 31 bits of the number represented by this InfiniteInteger.
    * The int is then given the same sign as this class. This is different than a narrowing cast because
    * normally the bits would be unchanged signed or otherwise but this method performs a two's complement.
    *
    * @throws ArithmeticException if this is ±∞ or NaN
    * @see #longValue()
    */
   @Override
   public int intValue()
   {
      if (!this.isFinite()) throw new ArithmeticException(this + " can't be even partially represented as an int.");
      final int intValue =
         magnitudeHead.getData().intValue() & Integer.MAX_VALUE;  //drop the sign bit (can't use Math.abs because the nodes are unsigned)
      if (isNegative) return -intValue;
      return intValue;
   }

   /**
    * This method returns the least significant 63 bits of the number represented by this InfiniteInteger.
    * The long is then given the same sign as this class. This is different than a narrowing cast because
    * normally the bits would be unchanged signed or otherwise but this method performs a two's complement.
    *
    * @throws ArithmeticException if this is ±∞ or NaN
    * @see #longValueExact()
    * @see #bigIntegerValue()
    */
   @Override
   public long longValue()
   {
      if (!this.isFinite()) throw new ArithmeticException(this + " can't be even partially represented as a long.");

      long longValue = Integer.toUnsignedLong(magnitudeHead.getData().intValue());
      if (magnitudeHead.getNext() != null)
      {
         longValue += (Integer.toUnsignedLong(magnitudeHead.getNext().getData().intValue()) << 32);
      }
      longValue &= Long.MAX_VALUE;  //drop the sign bit (can't use Math.abs because the nodes are unsigned)
      if (isNegative) return -longValue;
      return longValue;
   }

   /**
    * This method returns the longValue only if this InfiniteInteger can fit within a signed long without losing information.
    *
    * @throws ArithmeticException if this is ±∞ or NaN
    * @throws ArithmeticException if this is greater than max long: 2^63-1
    * @see #longValue()
    * @see #bigIntegerValue()
    */
   @Override
   public long longValueExact()
   {
      if (!this.isFinite()) throw new ArithmeticException(this + " can't be represented as a long.");
      if (magnitudeHead.getNext() != null && magnitudeHead.getNext().getNext() != null)
         throw new ArithmeticException(this + " is too large to be represented as a long.");
      //if there are too many nodes then the number is too large
      if (magnitudeHead.getNext() != null && (magnitudeHead.getNext().getData().intValue() & Long.MIN_VALUE) != 0)
         throw new ArithmeticException(this + " is too large to be represented as a signed long.");
      //the & Min part checks that the most significant bit must be clear since it will be dropped to make the number signed
      return longValue();
   }

   @Override
   public BigInteger bigIntegerValue()
   {
      if (!this.isFinite())
         throw new ArithmeticException(this + " can't be even partially represented as a BigInteger.");
      // TODO: method stubs
      throw new UnsupportedOperationException("Not yet implemented");
      //after exact is done, copy and paste the code but return the previous result instead of throwing
   }

   /**
    * This method returns the a BigInteger representing the same number as this InfiniteInteger.
    * Or will throw if this InfiniteInteger is greater than BigInteger will allow.
    *
    * @throws ArithmeticException if this is ±∞ or NaN
    * @throws ArithmeticException if this is greater than the max of BigInteger: 2^(2^31-1)-1
    * @see #bigIntegerValue()
    * @see #longValue()
    */
   @Override
   public BigInteger bigIntegerValueExact()
   {
      if (!this.isFinite()) throw new ArithmeticException(this + " can't be represented as a BigInteger.");

      try
      {
         DequeNode<Integer> cursor = this.magnitudeHead;
         BigInteger result = BigInteger.valueOf(Integer.toUnsignedLong(cursor.getData().intValue()));
         cursor = cursor.getNext();
         while (cursor != null)
         {
            result = result.shiftLeft(32);  //TODO: unsure of math since I am unsigned
            result = result.add(BigInteger.valueOf(Integer.toUnsignedLong(cursor.getData().intValue())));
            cursor = cursor.getNext();
         }
         if (this.isNegative) return result.negate();
         return result;
      }
      catch (final Throwable cause)
      {
         //ArithmeticException (from 1.8 overflow) or OutOfMemoryError etc
         //before 1.8 I assume it would throw ArrayIndexOutOfBoundsException
         //result.or will not throw but result.shiftLeft might
         throw (ArithmeticException) new ArithmeticException(this + " is too large to be represented as a BigInteger.").initCause(cause);
      }
   }

   /**
    * Takes less than a second to calculate but making a base 10 string took more than 40 minutes then crashed (OutOfMemoryError: Java
    * heap space).
    */
   public static BigInteger calculateMaxBigInteger()
   {
      BigInteger maxValue = BigInteger.ONE.shiftLeft(Integer.MAX_VALUE - 1).subtract(BigInteger.ONE);
      maxValue = maxValue.shiftLeft(1).add(BigInteger.ONE);
      return maxValue;
   }

   /**
    * Takes 21 seconds to calculate. Takes a total of 42 seconds to display as debugging string.
    */
   public static MutableInfiniteInteger calculateMaxBigIntegerAsInfiniteInteger()
   {
      final MutableInfiniteInteger bigIntMaxValue = MutableInfiniteInteger.valueOf(1).multiplyByPowerOf2(Integer.MAX_VALUE).subtract(1);
      return bigIntMaxValue;
   }

   /**
    * <p>A Googolplex is a huge number that is easy to define. It is equal to 10<sup>10^100</sup>.
    * This number will not fit into a BigInteger, the reason this method is defined is to show that this class
    * does allow such a number (even if the hardware does not). This calculation is correct but is guarantee to never finish
    * due to hardware limits (see below).</p>
    *
    * <p>In order to store a googolplex you would need
    * <a href="http://www.wolframalpha.com/input/?i=log2%28googolplex%29">3e100</a> bits.
    * Assuming each bit was
    * <a href="http://www.cio.com/article/2400538/data-management/ibm-smashes-moore-s-law--cuts-bit-size-to-12-atoms.html">12 atoms</a>
    * then the entire observable universe wouldn't have enough bits
    * (using <a href="http://www.wolframalpha.com/input/?i=%28atoms+in+the+observable+universe%29%2F12">8e78</a> atoms).
    * More specifically every atom would need to hold 300 quintillion (<a
    * href="http://www.wolframalpha.com/input/?i=log2(googolplex)+%2F+(1e80)">3.32193e20</a>) bits.
    * So even though this class allows such a number the universe doesn't: but it's the thought that counts.
    * </p>
    *
    * <p>A googol on the other hand would only take 42 bytes (336 bits).</p>
    *
    * @return 10^(10^100)
    * @deprecated Seriously. Don't call this method. See description.
    */
   @Deprecated
   public static MutableInfiniteInteger calculateGoogolplex()
   {
      final MutableInfiniteInteger googol = MutableInfiniteInteger.valueOf(10).power(100);
      return MutableInfiniteInteger.valueOf(10).power(googol);
   }

   /**
    * http://googology.wikia.com/wiki/Arrow_notation
    * http://mathworld.wolfram.com/KnuthUp-ArrowNotation.html
    * http://mathworld.wolfram.com/KnuthUp-ArrowNotation.html
    * private: use power instead of this method
    */
   private static MutableInfiniteInteger arrowNotation(final MutableInfiniteInteger a, final MutableInfiniteInteger n,
                                                       final MutableInfiniteInteger b)
   {
      if (n.equalValue(1)) return a.copy().power(b);
      if (b.equalValue(1)) return a.copy();
      return MutableInfiniteInteger.arrowNotation(a.copy(), n.copy().subtract(1),
         MutableInfiniteInteger.arrowNotation(a.copy(), n.copy(), b.copy().subtract(1)));
   }

   /**
    * <a href="http://googology.wikia.com/wiki/Graham's_number">Graham's_number</a>
    * this function doesn't have an official name
    * private: has no legitimate use case
    */
   private static MutableInfiniteInteger grahamFunction(final MutableInfiniteInteger k)
   {
      if (k.equalValue(0)) return new MutableInfiniteInteger(4);
      return MutableInfiniteInteger.arrowNotation(new MutableInfiniteInteger(3),
         MutableInfiniteInteger.grahamFunction(k.copy().subtract(1)), new MutableInfiniteInteger(3));
   }

   /**
    * Graham's Number is comically large but <a href="http://waitbutwhy.com/2014/11/1000000-grahams-number.html">hard</a>
    * <a href="http://en.wikipedia.org/wiki/Graham's_number#Definition">to</a>
    * <a href="http://googology.wikia.com/wiki/Graham's_number">define</a>.
    * This number will not fit into a BigInteger, the reason this method is defined is to show that this class
    * does allow such a number (even if the hardware does not). This calculation is correct but is guarantee to never finish
    * due to hardware limits (see Googolplex).
    *
    * @return Graham's Number which is g(64)
    * @see #calculateGoogolplex()
    * @deprecated The observable universe can't store a googolplex. How many universe's worth of atoms would
    * you need for Graham's Number?
    */
   @Deprecated
   public static MutableInfiniteInteger calculateGrahamsnumber()
   {
      return MutableInfiniteInteger.grahamFunction(new MutableInfiniteInteger(64));
   }

   /**
    * This method returns a read only list iterator of unknown size that iterates over the data of each of the nodes
    * of this InfiniteInteger. Each node is unsigned and they are in little endian order.
    * Calling nextIndex or previousIndex will return -1 and calling add, set, or remove will throw.
    * Note that there might be more than Long.Max elements (or even max BigInteger!).
    *
    * @throws UnsupportedOperationException if this is ±∞ or NaN
    * @see #magnitudeStream()
    * @see ReadOnlyListIterator
    * @see DequeNodeIterator.IndexAgnosticValueIterator
    */
   @Override
   public ReadOnlyListIterator<Integer> magnitudeIterator()
   {
      if (!this.isFinite()) throw new UnsupportedOperationException(this + " does not have nodes.");
      return new ReadOnlyListIterator<>(new DequeNodeIterator.IndexAgnosticValueIterator<>(magnitudeHead));
   }

   /**
    * This method returns a stream for the data of each of the nodes of this InfiniteInteger.
    * Each node is unsigned and they are in little endian order.
    * Note that there might be more than Long.Max elements (or even max BigInteger!).
    * This method represents that there can be any number of elements better than magnitudeIterator.
    * Streams are also naturally read only with unknown size.
    *
    * @throws UnsupportedOperationException if this is ±∞ or NaN
    * @see #magnitudeIterator()
    */
   @Override
   public Stream<Integer> magnitudeStream()
   {
      return StreamSupport.stream(Spliterators.spliteratorUnknownSize(magnitudeIterator(), Spliterator.ORDERED | Spliterator.IMMUTABLE),
         false);
   }

   /**
    * Helper method to get the last (most significant) node of this InfiniteInteger.
    *
    * @throws NullPointerException if magnitudeHead is null
    */
   private DequeNode<Integer> getMagnitudeTail()
   {
      //TODO: make a variable for magnitudeTail?
      DequeNode<Integer> tail = magnitudeHead;
      while (tail.getNext() != null){tail = tail.getNext();}
      return tail;
   }

   /**
    * Entire code: <blockquote>{@code return this.add(MutableInfiniteInteger.valueOf(value));}</blockquote>
    *
    * @see #add(MutableInfiniteInteger)
    * @see #valueOf(BigInteger)
    */
   @Override
   public MutableInfiniteInteger add(final long value){return this.add(MutableInfiniteInteger.valueOf(value));}

   /**
    * Entire code: <blockquote>{@code return this.add(MutableInfiniteInteger.valueOf(value));}</blockquote>
    *
    * @see #add(MutableInfiniteInteger)
    * @see #valueOf(BigInteger)
    */
   @Override
   public MutableInfiniteInteger add(final BigInteger value){return this.add(MutableInfiniteInteger.valueOf(value));}

   /**
    * Returns an InfiniteInteger whose value is {@code (this + value)}.
    *
    * @param value the operand to be added to this InfiniteInteger.
    * @return the result including ±∞ and NaN
    * @see #add(long)
    */
   @Override
   public MutableInfiniteInteger add(final MutableInfiniteInteger value)
   {
      if (this.isNaN() || value.isNaN()) return MutableInfiniteInteger.NaN;
      if (value.equalValue(0)) return this;
      if (this.equalValue(0)) return set(value.copy());

      //delegations based on the sign of each
      if (!isNegative && value.isNegative) return this.subtract(value.copy().abs());
      if (isNegative && !value.isNegative) return set(value.copy().subtract(this.abs()));

      if (!this.isFinite()) return this;
      if (!value.isFinite()) return value;

      //the rest is for if both positive or both negative
      MutableInfiniteInteger.addAbove(this.magnitudeHead, value);
      //isNegative is already correct for positive or negative
      return this;
   }

   /**
    * Used internally by the add and multiply methods. This method adds value to the starting node
    * and carries upward. It does not affect any nodes before the starting node. The starting node
    * (and above) will be mutated. This method assumes both numbers are positive (or both negative).
    *
    * @param startingNode can't be null
    * @param value        can't be null or a constant
    */
   private static void addAbove(final DequeNode<Integer> startingNode, final MutableInfiniteInteger value)
   {
      long sum = 0;
      DequeNode<Integer> resultCursor = startingNode;
      final ListIterator<Integer> valueMagIterator = value.magnitudeIterator();
      int lowSum, highSum;
      while (valueMagIterator.hasNext() || sum != 0)
      {
         //turns out (true for signed and unsigned) max long > max int * max int. (2^64-1) > ((2^32-1)*(2^32-1))
         lowSum = (int) sum;
         highSum = (int) (sum >>> 32);
         sum = Integer.toUnsignedLong(resultCursor.getData().intValue());
         if (valueMagIterator.hasNext()) sum += Integer.toUnsignedLong(valueMagIterator.next().intValue());
         sum += Integer.toUnsignedLong(lowSum);

         resultCursor.setData((int) sum);
         sum >>>= 32;
         sum += Integer.toUnsignedLong(highSum);

         if (resultCursor.getNext() == null) resultCursor = DequeNode.Factory.createNodeAfter(resultCursor, 0);
         else resultCursor = resultCursor.getNext();
      }
      //go to the end (which won't always happen)
      while (resultCursor.getNext() != null){resultCursor = resultCursor.getNext();}
      if (resultCursor.getData().intValue() == 0) resultCursor.remove();  //remove the last node since it is a leading 0
      //isNegative is already correct for positive or negative
   }

   /**
    * Entire code: <blockquote>{@code return this.subtract(MutableInfiniteInteger.valueOf(value));}</blockquote>
    *
    * @see #subtract(MutableInfiniteInteger)
    * @see #valueOf(BigInteger)
    */
   @Override
   public MutableInfiniteInteger subtract(final long value){return this.subtract(MutableInfiniteInteger.valueOf(value));}

   /**
    * Entire code: <blockquote>{@code return this.subtract(MutableInfiniteInteger.valueOf(value));}</blockquote>
    *
    * @see #subtract(MutableInfiniteInteger)
    * @see #valueOf(BigInteger)
    */
   @Override
   public MutableInfiniteInteger subtract(final BigInteger value){return this.subtract(MutableInfiniteInteger.valueOf(value));}

   /**
    * Returns an InfiniteInteger whose value is {@code (this - value)}.
    * Note ∞ - ∞ results in NaN.
    *
    * @param value the operand to be subtracted from this InfiniteInteger.
    * @return the result including ±∞ and NaN
    * @see #subtract(long)
    */
   @Override
   public MutableInfiniteInteger subtract(final MutableInfiniteInteger value)
   {
      if (this.isNaN() || value.isNaN()) return MutableInfiniteInteger.NaN;
      if (value.equalValue(0)) return this;
      if (this.equalValue(0)) return set(value.copy().negate());

      //delegations based on the sign of each
      if (!this.isNegative && value.isNegative) return this.add(value.copy().abs());
      if (this.isNegative && !value.isNegative) return set(this.abs().add(value).negate());
      if (this.isNegative && value.isNegative) return set(value.copy().abs().subtract(this.abs()));

      //the rest is for if both positive
      if (this.equals(MutableInfiniteInteger.POSITIVE_INFINITY) && value.equals(MutableInfiniteInteger.POSITIVE_INFINITY))
         return MutableInfiniteInteger.NaN;
      if (this.isInfinite()) return this;
      if (value.isInfinite()) return value.negate();
      if (this.equals(value)) return set(new MutableInfiniteInteger(0));
      if (is(this, LESS_THAN, value)) return set(value.copy().subtract(this).negate());

      //this is greater than value
      long difference = 0;
      DequeNode<Integer> thisCursor = this.magnitudeHead;
      final ListIterator<Integer> valueMagIterator = value.magnitudeIterator();
      int lowValue, highValue;
      byte borrowCount;
      while (valueMagIterator.hasNext() || difference != 0)
      {
         lowValue = (int) difference;
         highValue = (int) (difference >>> 32);
         difference = Integer.toUnsignedLong(thisCursor.getData().intValue());
         if (valueMagIterator.hasNext()) difference -= Integer.toUnsignedLong(valueMagIterator.next().intValue());
         difference -= Integer.toUnsignedLong(lowValue);
         //difference == Long.min won't cause a bug due to how borrow is programmed
         borrowCount = 0;
         while (difference < 0)  //can happen 0-2 times
         {
            borrowCount++;
            difference += Integer.toUnsignedLong((int) BitWiseUtil.HIGH_64) + 1;  //add max unsigned int +1
            //this makes difference borrow
            //the +1 is here for the same reason that when borrowing in base 10 you add 10 instead of 9
         }

         thisCursor.setData((int) difference);
         //assert((difference >>> 32) == 0);  //due to the borrowing above

         difference = Integer.toUnsignedLong(highValue) + borrowCount;  //borrow subtracts more

         if (thisCursor.getNext() != null) thisCursor = thisCursor.getNext();
         //if thisCursor is at the end then the loop is done because this > value
      }
      //go to the end (which won't always happen)
      while (thisCursor.getNext() != null){thisCursor = thisCursor.getNext();}
      //There can be any number of leading 0s. Remove them all.
      while (thisCursor.getData().intValue() == 0)
      {
         thisCursor = thisCursor.getPrev();
         thisCursor.getNext().remove();
      }

      return this;
   }

   /**
    * Entire code: <blockquote>{@code return this.multiply(MutableInfiniteInteger.valueOf(value));}</blockquote>
    *
    * @see #multiply(MutableInfiniteInteger)
    * @see #valueOf(long)
    */
   @Override
   public MutableInfiniteInteger multiply(final long value){return this.multiply(MutableInfiniteInteger.valueOf(value));}

   /**
    * Entire code: <blockquote>{@code return this.multiply(InfiniteInteger.valueOf(value));}</blockquote>
    *
    * @see #multiply(MutableInfiniteInteger)
    * @see #valueOf(BigInteger)
    */
   @Override
   public MutableInfiniteInteger multiply(final BigInteger value){return this.multiply(MutableInfiniteInteger.valueOf(value));}

   /**
    * Returns an InfiniteInteger whose value is {@code (this * value)}.
    * Note ±∞ * 0 results in NaN.
    *
    * @param value the operand to be multiplied to this InfiniteInteger.
    * @return the result including ±∞ and NaN
    */
   @Override
   public MutableInfiniteInteger multiply(final MutableInfiniteInteger value)
   {
      if (this.isNaN() || value.isNaN()) return MutableInfiniteInteger.NaN;
      if (this.isInfinite() && value.equalValue(0)) return MutableInfiniteInteger.NaN;
      if (value.isInfinite() && this.equalValue(0)) return MutableInfiniteInteger.NaN;

      if (this.equals(MutableInfiniteInteger.NEGATIVE_INFINITY) && value.equals(MutableInfiniteInteger.NEGATIVE_INFINITY))
         return MutableInfiniteInteger.POSITIVE_INFINITY;
      if (this.equals(MutableInfiniteInteger.POSITIVE_INFINITY) && value.equals(MutableInfiniteInteger.POSITIVE_INFINITY))
         return MutableInfiniteInteger.POSITIVE_INFINITY;
      if (this.isInfinite() && value.isInfinite()) return MutableInfiniteInteger.NEGATIVE_INFINITY;

      if (this.isInfinite() && value.signum() == 1) return this;
      if (this.isInfinite() && value.signum() == -1) return this.negate();
      if (value.isInfinite() && this.signum() == 1) return value;
      if (value.isInfinite() && this.signum() == -1) return value.negate();

      if (value.equalValue(1)) return this;
      if (this.equalValue(1)) return set(value.copy());
      if (this.equalValue(0) || value.equalValue(0)) return set(new MutableInfiniteInteger(0));
      if (value.equalValue(-1)) return this.negate();
      if (this.equalValue(-1)) return set(value.copy().negate());

      final boolean resultIsNegative = (isNegative != value.isNegative);  //!= acts as xor
      MutableInfiniteInteger valueRemaining = value.copy();  //.abs() is not needed since the nodes are unsigned
      final MutableInfiniteInteger result = new MutableInfiniteInteger(0);
      DequeNode<Integer> resultCursor = result.magnitudeHead;

      while (!valueRemaining.equalValue(0))
      {
         MutableInfiniteInteger.addAbove(resultCursor, this.internalMultiply(valueRemaining.magnitudeHead.getData().intValue()));
         valueRemaining = valueRemaining.divideByPowerOf2DropRemainder(32);
         if (resultCursor.getNext() == null) resultCursor = DequeNode.Factory.createNodeAfter(resultCursor, 0);
         else resultCursor = resultCursor.getNext();
      }
      result.isNegative = resultIsNegative;
      resultCursor = result.getMagnitudeTail();
      while (resultCursor.getData().intValue() == 0)  //remove leading 0s
      {
         resultCursor = resultCursor.getPrev();  //prev is never null because the result is not 0
         resultCursor.getNext().remove();
      }
      //TODO: make it suck less by mutating as it goes. also use shifting for speed
      //then re-test divide speed

      return set(result);
   }

   /**
    * Used internally by the multiply methods. This method multiplies this InfiniteInteger by value.
    * This method does not mutate (the returned value will be a copy) and should be removed in the future.
    * This InfiniteInteger can't be a constant or 0.
    *
    * @param value must be positive or 0
    * @return the result
    */
   private MutableInfiniteInteger internalMultiply(final int value)
   {
      if (value == 0) return new MutableInfiniteInteger(0);  //this has already been compared to the singletons
      final MutableInfiniteInteger result = this.copy().abs();
      DequeNode<Integer> resultCursor = result.magnitudeHead;
      long product;
      int carry = 0;
      boolean isHuge;
      while (resultCursor != null)
      {
         //max unsigned int * max unsigned int < max unsigned long but will end up being negative which makes adding carry impossible
         product = Integer.toUnsignedLong(resultCursor.getData().intValue());
         product *= Integer.toUnsignedLong(value);
         isHuge = (product < 0);
         product &= Long.MAX_VALUE;  //TODO: is Long.min possible?
         product += Integer.toUnsignedLong(carry);
         if (isHuge) product |= Long.MIN_VALUE;

         resultCursor.setData((int) product);
         product >>>= 32;
         carry = (int) product;

         resultCursor = resultCursor.getNext();
      }
      resultCursor = result.getMagnitudeTail();
      if (carry != 0) DequeNode.Factory.createNodeAfter(resultCursor, carry);
      else if (resultCursor.getData().intValue() == 0) resultCursor.remove();  //remove leading 0
      return result;
   }

   /**
    * This method delegates because the formula used is exactly the same.
    * Entire code: <blockquote>{@code return this.multiplyByPowerOf2(InfiniteInteger.valueOf(exponent));}</blockquote>
    *
    * @see #multiplyByPowerOf2(MutableInfiniteInteger)
    * @see #valueOf(long)
    */
   @Override
   public MutableInfiniteInteger multiplyByPowerOf2(final long exponent)
   {
      return this.multiplyByPowerOf2(MutableInfiniteInteger.valueOf(exponent));
   }

   /**
    * Entire code: <blockquote>{@code return this.multiplyByPowerOf2(InfiniteInteger.valueOf(exponent));}</blockquote>
    *
    * @see #multiplyByPowerOf2(MutableInfiniteInteger)
    * @see #valueOf(BigInteger)
    */
   @Override
   public MutableInfiniteInteger multiplyByPowerOf2(final BigInteger exponent)
   {
      return this.multiplyByPowerOf2(MutableInfiniteInteger.valueOf(exponent));
   }

   /**
    * <p>Returns an InfiniteInteger whose value is {@code (this << exponent)}.
    * If the exponent is negative then a right shift is performed instead.
    * Computes <tt>this * 2<sup>exponent</sup></tt>.
    * Note that the nodes are unsigned and this operation ignores sign.
    * Therefore this operation won't change the sign.</p>
    *
    * <p>Examples:<br /><code>
    * InfiniteInteger.valueOf(-10).multiplyByPowerOf2(1) is -20<br />
    * InfiniteInteger.valueOf(100).multiplyByPowerOf2(2) is 400</code></p>
    *
    * <p>This method is not named shiftLeft because the direction left only makes sense for big endian numbers.</p>
    *
    * @param exponent is also the shift distance in bits
    * @return the result including ±∞ and NaN
    * @see #divideByPowerOf2DropRemainder(MutableInfiniteInteger)
    */
   @Override
   public MutableInfiniteInteger multiplyByPowerOf2(final MutableInfiniteInteger exponent)
   {
      if (this.equalValue(0) || exponent.equalValue(0) || !this.isFinite()) return this;
      if (exponent.isNegative) return this.divideByPowerOf2DropRemainder(exponent.copy().abs());

      MutableInfiniteInteger shiftDistanceRemaining = exponent.copy();
      while (isComparisonResult(shiftDistanceRemaining.compareTo(32), GREATER_THAN_OR_EQUAL_TO))
      {
         this.magnitudeHead = DequeNode.Factory.createNodeBefore(0, this.magnitudeHead);
         shiftDistanceRemaining = shiftDistanceRemaining.subtract(32);
      }

      final int smallShiftDistance = shiftDistanceRemaining.intValue();
      if (smallShiftDistance != 0)
      {
         DequeNode<Integer> thisCursor = this.getMagnitudeTail();
         int overflow;

         while (thisCursor != null)
         {
            overflow = BitWiseUtil.getHighestNBits(thisCursor.getData().intValue(), smallShiftDistance);
            //overflow contains what would fall off when shifting left
            overflow >>>= (32 - smallShiftDistance);
            //shift overflow right so that it appears in the least significant place of the following node
            if (overflow != 0 && thisCursor.getNext() == null) DequeNode.Factory.createNodeAfter(thisCursor, overflow);
            else if (overflow != 0) thisCursor.getNext().setData(thisCursor.getNext().getData().intValue() | overflow);

            thisCursor.setData(thisCursor.getData().intValue() << smallShiftDistance);
            thisCursor = thisCursor.getPrev();
         }
      }
      //no need to check leading 0s since it couldn't have gained any

      return this;
   }

   /**
    * This method delegates because the formula used is exactly the same.
    * Entire code: <blockquote>{@code return divide(MutableInfiniteInteger.valueOf(value));}</blockquote>
    *
    * @see #divide(MutableInfiniteInteger)
    * @see #valueOf(long)
    */
   @Override
   public IntegerQuotient<MutableInfiniteInteger> divide(final long value)
   {
      return divide(MutableInfiniteInteger.valueOf(value));
   }

   /**
    * Entire code: <blockquote>{@code return this.divide(InfiniteInteger.valueOf(value));}</blockquote>
    *
    * @see #divide(MutableInfiniteInteger)
    * @see #valueOf(BigInteger)
    */
   @Override
   public IntegerQuotient<MutableInfiniteInteger> divide(final BigInteger value){return this.divide(MutableInfiniteInteger.valueOf(value));}

   /**
    * Returns an IntegerQuotient with fields of the whole and remainder of {@code (this / value)}.
    * Note that ±∞ / ±∞ results in IntegerQuotient(NaN, NaN).
    * X / 0 results in IntegerQuotient(NaN, NaN). ±∞ / X == IntegerQuotient(±∞, NaN);
    * X / ±∞ == IntegerQuotient(0, NaN).
    * <p>
    * Note that this object is not mutated by this operation.
    *
    * @param value the operand to divide this InfiniteInteger by.
    * @return an object with the whole result including ±∞ and NaN and the remainder (which can be NaN but can't be ±∞)
    * @see IntegerQuotient
    */
   @Override
   public IntegerQuotient<MutableInfiniteInteger> divide(final MutableInfiniteInteger value)
   {
      if (this.isNaN() || value.isNaN() || value.equalValue(0))
         return new IntegerQuotient<>(MutableInfiniteInteger.NaN, MutableInfiniteInteger.NaN);
      if (!this.isFinite() && !value.isFinite())
         return new IntegerQuotient<>(MutableInfiniteInteger.NaN, MutableInfiniteInteger.NaN);
      if (!this.isFinite()) return new IntegerQuotient<>(this, MutableInfiniteInteger.NaN);
      if (!value.isFinite()) return new IntegerQuotient<>(new MutableInfiniteInteger(0), MutableInfiniteInteger.NaN);

      if (value.equalValue(1)) return new IntegerQuotient<>(this.copy(), new MutableInfiniteInteger(0));
      if (value.equalValue(-1)) return new IntegerQuotient<>(this.copy().negate(), new MutableInfiniteInteger(0));
      if (this.equals(value))
         return new IntegerQuotient<>(new MutableInfiniteInteger(1), new MutableInfiniteInteger(0));

      MutableInfiniteInteger thisAbs = this.copy().abs(), valueAbs = value.copy().abs();
      //if not equal but abs is equal then answer is -1,0
      if (thisAbs.equals(valueAbs))
         return new IntegerQuotient<>(new MutableInfiniteInteger(-1), new MutableInfiniteInteger(0));
      if (is(thisAbs, LESS_THAN, valueAbs) || this.equalValue(0))
         return new IntegerQuotient<>(new MutableInfiniteInteger(0), thisAbs);

      //shift them down as much as possible
      //I can't use thisAbs.intValue because I need all 32 bits
      MutableInfiniteInteger numberOfShifts = new MutableInfiniteInteger(0);
      while (((int) thisAbs.longValue()) == 0 && ((int) valueAbs.longValue()) == 0)
      {
         //dropping a node is fast and greatly shrinks the numbers
         thisAbs = thisAbs.divideByPowerOf2DropRemainder(32);
         valueAbs = valueAbs.divideByPowerOf2DropRemainder(32);
         //reducing doesn't affect the whole answer
         numberOfShifts = numberOfShifts.add(32);  //but does affect the remainder so track it
      }
      final int lastShift = Math.min(Integer.numberOfTrailingZeros(((int) thisAbs.longValue())),
         Integer.numberOfTrailingZeros(((int) valueAbs.longValue())));
      if (lastShift != 0)
      {
         //passing in 0 would do nothing anyway
         thisAbs = thisAbs.divideByPowerOf2DropRemainder(lastShift);
         valueAbs = valueAbs.divideByPowerOf2DropRemainder(lastShift);
         numberOfShifts = numberOfShifts.add(lastShift);
      }

      final boolean resultIsNegative = (isNegative != value.isNegative);  //!= acts as xor
      //if the remaining division can be done by primitive then delegate
      if (thisAbs.equalValue(thisAbs.longValue()) && valueAbs.equalValue(valueAbs.longValue()))
      {
         long whole = thisAbs.longValue() / valueAbs.longValue();
         final long remainder = thisAbs.longValue() % valueAbs.longValue();
         if (resultIsNegative) whole *= -1;
         return new IntegerQuotient<>(
            new MutableInfiniteInteger(whole),
            new MutableInfiniteInteger(remainder).multiplyByPowerOf2(numberOfShifts)
         );
      }

      //doesn't work for int: https://math.stackexchange.com/questions/333204/binary-long-division-for-polynomials-in-crc-computation
      //ditto: https://math.stackexchange.com/questions/682301/modulo-2-binary-division-xor-not-subtracting-method
      //final IntegerQuotient<MutableInfiniteInteger> integerQuotient = divideBinarySearch(thisAbs, valueAbs);
      //final IntegerQuotient<MutableInfiniteInteger> integerQuotient = longDivide(thisAbs, valueAbs);
      final IntegerQuotient<MutableInfiniteInteger> integerQuotient = binaryDivide(thisAbs, valueAbs);

      MutableInfiniteInteger whole = integerQuotient.getWholeResult();
      if (resultIsNegative) whole = whole.negate();
      return new IntegerQuotient<>(whole, integerQuotient.getRemainder().multiplyByPowerOf2(numberOfShifts));
   }

   private static IntegerQuotient<MutableInfiniteInteger> divideBinarySearch(MutableInfiniteInteger thisAbs,
                                                                             MutableInfiniteInteger valueAbs)
   {
      MutableInfiniteInteger lower = new MutableInfiniteInteger(2);  //above already covered the possible ways whole could be 0 and 1
      MutableInfiniteInteger difference, midway, higher, whole = null;
      higher = ComparableSugar.max(thisAbs, valueAbs);
      higher = higher.copy().divideByPowerOf2DropRemainder(1);

      if (valueAbs.equalValue(1))
         whole = thisAbs;  //this can only occur if valueAbs was shifted down to 1 and thisAbs > Long.Max
      while (whole == null)
      {
         difference = higher.copy().subtract(lower);
         //avoid cutting small numbers in half:
         if (is(difference, LESS_THAN, MutableInfiniteInteger.valueOf(4))) break;  //jump to small loop
         difference = difference.divideByPowerOf2DropRemainder(1);
         midway = difference.add(lower);  //diff not copied because I no longer need it
         final int compareResult = midway.copy().multiply(valueAbs).compareTo(thisAbs);
         if (isComparisonResult(compareResult, EQUAL_TO)) whole = midway;
            //if midway*valueAbs > thisAbs then midway is an upper bound
         else if (isComparisonResult(compareResult, GREATER_THAN)) higher = midway;
         else lower = midway;  //if less than
      }
      //if difference < 4 then just have lower count up (max of 4 times)
      while (whole == null)
      {
         final int compareResult = lower.copy().multiply(valueAbs).compareTo(thisAbs);
         if (isComparisonResult(compareResult, EQUAL_TO)) whole = lower;
         else if (isComparisonResult(compareResult, GREATER_THAN)) whole = lower.subtract(1);
         else lower = lower.add(1);
      }

      final MutableInfiniteInteger remainder = thisAbs.copy().subtract(whole.copy().multiply(valueAbs));

      return new IntegerQuotient<>(whole, remainder);
   }

   /**
    * It works but uses divideBinarySearch and seems slower than it.
    */
   private static IntegerQuotient<MutableInfiniteInteger> longDivide(MutableInfiniteInteger thisAbs,
                                                                     MutableInfiniteInteger valueAbs)
   {
/*
      005325101r21
     ----------
123 | 6(54987444)
     -0 (0)
   ----
      65(4987444)
      -0 (0)
    ----
      654(987444)
     -615 (5)
     ----
       399(87444)
      -369 (3)
      ----
        308(7444)
       -246 (2)
       ----
         627(444)
        -615 (5)
        ----
          124(44)
         -123 (1)
         ----
            14(4)
            -0 (0)
          ----
            144
           -123 (1)
           ----
             21 (r)
*/
      final MutableInfiniteInteger whole = MutableInfiniteInteger.valueOf(0);
      MutableInfiniteInteger carry = MutableInfiniteInteger.valueOf(0);
      DequeNode<Integer> thisCursor = thisAbs.getMagnitudeTail();
      do
      {
         //inherit the new digit from thisAbs
         if (carry.equalValue(0)) carry = MutableInfiniteInteger.valueOf(Integer.toUnsignedLong(thisCursor.getData()));
            //insert a new least significant node
         else carry.magnitudeHead = DequeNode.Factory.createNodeBefore(thisCursor.getData(), carry.magnitudeHead);

         if (is(carry, LESS_THAN, valueAbs))
         {
            whole.magnitudeHead = DequeNode.Factory.createNodeBefore(0, whole.magnitudeHead);
         }
         else
         {
            final IntegerQuotient<MutableInfiniteInteger> partialQuotient = divideBinarySearch(carry, valueAbs);
            //partialQuotient.whole is always 1 node
            final Integer data = partialQuotient.getWholeResult().magnitudeHead.getData();
            whole.magnitudeHead = DequeNode.Factory.createNodeBefore(data, whole.magnitudeHead);
            carry = partialQuotient.getRemainder();
         }
         thisCursor = thisCursor.getPrev();
      } while (thisCursor != null);

      //TODO: remove leading 0s should be a method
      DequeNode<Integer> wholeTail = whole.getMagnitudeTail();
      while (wholeTail.getData().intValue() == 0 && wholeTail != whole.magnitudeHead)
      {
         wholeTail = wholeTail.getPrev();
         wholeTail.getNext().remove();
      }

      return new IntegerQuotient<>(whole, carry);
   }

   /**
    * Significantly faster than divideBinarySearch
    */
   private static IntegerQuotient<MutableInfiniteInteger> binaryDivide(MutableInfiniteInteger thisAbs,
                                                                       MutableInfiniteInteger valueAbs)
   {
/*
yes/no subtract: http://www.binarymath.info/binary-division.php

75/13 = 5 rem 10
1001011/1101 = 101 rem 1010

     0000101
    --------
1101(1001011
 ?1101 no (0)
  ?1101 no (0)
   ?1101 no (0)
    ?1101 no (0)
     -1101 yes (1)
     -----
       10111
      ?1101 no (0)
       -1101 yes (1)
       -----
        1010
        rem 1010

1232/13 = 94 rem 10
10011010000/1101 = 1011110 rem 1010

     00001011110 rem 1010
     -----------
1101(10011010000
 ?1101 no (0)
  ?1101 no (0)
   ?1101 no (0)
    ?1101 no (0)
     -1101 yes (1)
     -----
       110010000
      ?1101 no (0)
       -1101 yes (1)
       -----
        11000000
        -1101 yes (1)
        -----
         1011000
         -1101 yes (1)
         -----
          100100
          -1101 yes (1)
          -----
            1010
           ?1101 no (0)
            rem 1010

10 4294967296/31 = 138547332 r 4
0x = 100000000/1F = 8421084 r 4
0b 100000000000000000000000000000000 / 11111 = 000001000010000100001000010000100 r 0100

      000001000010000100001000010000100
      ---------------------------------
11111(100000000000000000000000000000000
 ?11111 no (0)
  ?11111 no (0)
   ?11111 no (0)
    ?11111 no (0)
     ?11111 no (0)
      -11111 yes (1)
      ------
           1
          ?11111 no (0)...

problem is that I don't know if this will be faster than trial multiplication since it requires iteration over base 2
rather than base int
*/
      DequeNode<Integer> thisCursor = thisAbs.getMagnitudeTail();
      MutableInfiniteInteger carry = MutableInfiniteInteger.valueOf(0);
      MutableInfiniteInteger whole = MutableInfiniteInteger.valueOf(0);
      long extraNodeData = Integer.toUnsignedLong(thisCursor.getData());
      int bitIndex = 32;
      while (true)
      {
         if (bitIndex == 0)
         {
            thisCursor = thisCursor.getPrev();
            if (thisCursor == null) break;
            extraNodeData = Integer.toUnsignedLong(thisCursor.getData());
            bitIndex = 32;
         }
         else
         {
            //TODO: can I refactor this to be less confusing?
            final byte bit = (byte) ((extraNodeData >>> (bitIndex - 1)) & 1);
            carry = carry.multiplyByPowerOf2(1);
            carry.magnitudeHead.setData(carry.magnitudeHead.getData() | bit);
            --bitIndex;

            whole = whole.multiplyByPowerOf2(1);
            if (is(carry, GREATER_THAN_OR_EQUAL_TO, valueAbs))
            {
               whole.magnitudeHead.setData(whole.magnitudeHead.getData() | 1);
               carry = carry.subtract(valueAbs);
            }
         }
      }

      DequeNode<Integer> wholeTail = whole.getMagnitudeTail();
      while (wholeTail.getData().intValue() == 0 && wholeTail != whole.magnitudeHead)
      {
         wholeTail = wholeTail.getPrev();
         wholeTail.getNext().remove();
      }

      return new IntegerQuotient<>(whole, carry);
   }

   /**
    * Aka divideReturnWhole.
    * Entire code: <blockquote>{@code return set(divide(value).getWholeResult());}</blockquote>
    *
    * @see #divide(MutableInfiniteInteger)
    * @see #valueOf(long)
    */
   @Override
   public MutableInfiniteInteger divideDropRemainder(final long value)
   {
      return set(divide(value).getWholeResult());
   }

   /**
    * Aka divideReturnWhole.
    * Entire code: <blockquote>{@code return this.divideDropRemainder(InfiniteInteger.valueOf(value));}</blockquote>
    *
    * @see #divideDropRemainder(MutableInfiniteInteger)
    * @see #valueOf(BigInteger)
    */
   @Override
   public MutableInfiniteInteger divideDropRemainder(final BigInteger value)
   {
      return this.divideDropRemainder(MutableInfiniteInteger.valueOf(value));
   }

   /**
    * Aka divideReturnWhole.
    * Entire code: <blockquote>{@code return set(divide(value).getWholeResult());}</blockquote>
    *
    * @see #divide(MutableInfiniteInteger)
    */
   @Override
   public MutableInfiniteInteger divideDropRemainder(final MutableInfiniteInteger value)
   {
      return set(divide(value).getWholeResult());
   }

   /**
    * This method delegates because the formula used is exactly the same.
    * Entire code: <blockquote>{@code return this.divideByPowerOf2DropRemainder(InfiniteInteger.valueOf(exponent));}</blockquote>
    *
    * @see #divideByPowerOf2DropRemainder(MutableInfiniteInteger)
    * @see #valueOf(long)
    */
   @Override
   public MutableInfiniteInteger divideByPowerOf2DropRemainder(final long exponent)
   {
      return divideByPowerOf2DropRemainder(MutableInfiniteInteger.valueOf(exponent));
   }

   /**
    * Entire code: <blockquote>{@code return this.divideByPowerOf2DropRemainder(InfiniteInteger.valueOf(exponent));}</blockquote>
    *
    * @see #divideByPowerOf2DropRemainder(MutableInfiniteInteger)
    * @see #valueOf(BigInteger)
    */
   @Override
   public MutableInfiniteInteger divideByPowerOf2DropRemainder(final BigInteger exponent)
   {
      return divideByPowerOf2DropRemainder(MutableInfiniteInteger.valueOf(exponent));
   }

   /**
    * <p>Returns an InfiniteInteger whose value is {@code (this >>> exponent)}.
    * If the exponent is negative then a left shift is performed instead.
    * Computes <tt>truncate(this / 2<sup>exponent</sup>)</tt>.
    * Note that the nodes are unsigned and this operation ignores sign.
    * Also note that truncation occurs which means the low numbers are thrown away not rounded.
    * Therefore this operation always 0 fills and won't change the sign (unless the result is 0).</p>
    * <p>Examples:<br /><code>
    * InfiniteInteger.valueOf(-10).divideByPowerOf2DropRemainder(1) is -5<br />
    * InfiniteInteger.valueOf(100).divideByPowerOf2DropRemainder(2) is 25<br />
    * InfiniteInteger.valueOf(3).divideByPowerOf2DropRemainder(1) is 1</code></p>
    *
    * <p>This method is not named shiftRight because the direction right only makes sense for big endian numbers.</p>
    *
    * @param exponent is also the shift distance in bits
    * @return the result including ±∞ and NaN
    * @see #multiplyByPowerOf2(MutableInfiniteInteger)
    */
   @Override
   public MutableInfiniteInteger divideByPowerOf2DropRemainder(final MutableInfiniteInteger exponent)
   {
      if (this.equalValue(0) || exponent.equalValue(0) || !this.isFinite()) return this;
      if (exponent.isNegative) return this.multiplyByPowerOf2(exponent.copy().abs());

      MutableInfiniteInteger shiftDistanceRemaining = exponent.copy();
      while (isComparisonResult(shiftDistanceRemaining.compareTo(32), GREATER_THAN_OR_EQUAL_TO))
      {
         this.magnitudeHead = this.magnitudeHead.getNext();
         if (this.magnitudeHead == null) return set(new MutableInfiniteInteger(0));
         this.magnitudeHead.getPrev().remove();
         shiftDistanceRemaining = shiftDistanceRemaining.subtract(32);
      }

      final int smallShiftDistance = shiftDistanceRemaining.intValue();
      if (smallShiftDistance != 0)
      {
         DequeNode<Integer> thisCursor = this.magnitudeHead;
         int underflow;

         while (thisCursor.getNext() != null)
         {
            thisCursor.setData(thisCursor.getData().intValue() >>> smallShiftDistance);
            underflow = (int) BitWiseUtil.getLowestNBits(thisCursor.getNext().getData().intValue(), smallShiftDistance);
            //underflow contains what would fall off when shifting right
            underflow <<= (32 - smallShiftDistance);
            //shift underflow left so that it appears in the most significant place of the previous node
            if (underflow != 0) thisCursor.setData(thisCursor.getData().intValue() | underflow);
            thisCursor = thisCursor.getNext();
         }
         //last node simply shifts
         thisCursor.setData(thisCursor.getData().intValue() >>> smallShiftDistance);
         if (thisCursor.getData() == 0) thisCursor.remove();
      }

      return this;
   }

   /**
    * <p>Similar to {@code this % value} except the result is always positive. Aka: modulo, modulus, divideDropWhole, remainder.</p>
    * <p>Entire code: <blockquote>{@code return set(divide(value).getRemainder());}</blockquote></p>
    *
    * @see #divide(long)
    */
   @Override
   public MutableInfiniteInteger divideReturnRemainder(final long value)
   {
      return set(divide(value).getRemainder());
   }

   /**
    * <p>Similar to {@code this % value} except the result is always positive. Aka: modulo, modulus, divideDropWhole, remainder.</p>
    * <p>Entire code: <blockquote>{@code return this.divideReturnRemainder(MutableInfiniteInteger.valueOf(value));}</blockquote></p>
    *
    * @see #divideReturnRemainder(MutableInfiniteInteger)
    * @see #valueOf(BigInteger)
    */
   @Override
   public MutableInfiniteInteger divideReturnRemainder(final BigInteger value)
   {
      return this.divideReturnRemainder(MutableInfiniteInteger.valueOf(value));
   }

   /**
    * <p>Similar to {@code this % value} except the result is always positive. Aka: modulo, modulus, divideDropWhole, remainder.</p>
    * <p>Entire code: <blockquote>{@code return set(divide(value).getRemainder());}</blockquote></p>
    *
    * @see #divide(MutableInfiniteInteger)
    */
   @Override
   public MutableInfiniteInteger divideReturnRemainder(final MutableInfiniteInteger value)
   {
      return set(divide(value).getRemainder());
   }

   /**
    * This method delegates because the formula used is exactly the same.
    * Entire code: <blockquote>{@code return power(MutableInfiniteInteger.valueOf(exponent));}</blockquote>
    *
    * @see #power(MutableInfiniteInteger)
    * @see #valueOf(long)
    */
   @Override
   public MutableInfiniteInteger power(final long exponent)
   {
      return power(MutableInfiniteInteger.valueOf(exponent));
   }

   /**
    * Entire code: <blockquote>{@code return this.pow(InfiniteInteger.valueOf(exponent));}</blockquote>
    *
    * @see #power(MutableInfiniteInteger)
    * @see #valueOf(BigInteger)
    */
   @Override
   public MutableInfiniteInteger power(final BigInteger exponent){return this.power(MutableInfiniteInteger.valueOf(exponent));}

   /**
    * Returns an InfiniteInteger whose value is this<sup>exponent</sup>.
    * There are many special cases, for a full table see {@link InfiniteInteger#powerSpecialLookUp(InfiniteInteger, InfiniteInteger) this
    * table}
    * except the power method will return the result instead of null.
    *
    * @param exponent to which this InfiniteInteger is to be raised.
    * @return the result including ±∞ and NaN
    * @throws ArithmeticException if the result would be a fraction (only possible if exponent is negative)
    */
   @Override
   public MutableInfiniteInteger power(final MutableInfiniteInteger exponent)
   {
      final InfiniteInteger tableValue = InfiniteInteger.powerSpecialLookUp(InfiniteInteger.valueOf(this),
         InfiniteInteger.valueOf(exponent));
      if (tableValue != null) return set(tableValue.toMutableInfiniteInteger());

      if (exponent.isNegative)
         throw new ArithmeticException("A negative exponent would result in a non-integer answer. The exponent was: " + exponent);
      if (this.equalValue(2)) return set(MutableInfiniteInteger.valueOf(1).multiplyByPowerOf2(exponent));

      //TODO: study BigInt's pow and copy it
      //but BigInt's div and gcd are too complicated
      MutableInfiniteInteger result = this.copy();
      MutableInfiniteInteger exponentRemaining = exponent.copy().subtract(1);  //already have the first time
      while (!exponentRemaining.equalValue(0))
      {
         result = result.multiply(this);
         exponentRemaining = exponentRemaining.subtract(1);
      }
      return set(result);
   }

   /**
    * Returns an InfiniteInteger whose value is this<sup>this</sup>.
    * This method exists mostly as a testimony that this class really can hold any integer.
    * The result will be much larger than a factorial so it will be a slow execution.
    * For example if this InfiniteInteger is 3 then 3<sup>3</sup> is 27.
    *
    * @return the result including ∞ and NaN
    * @see #power(MutableInfiniteInteger)
    */
   @Override
   public MutableInfiniteInteger selfPower(){return this.power(this);}

   /**
    * Returns an InfiniteInteger whose value is this!.
    * This method exists mostly as a testimony that this class really can hold any integer.
    * Factorial is defined as a number multiplied by all positive integers less than it.
    * So 4! = 4*3*2*1. The special cases of 1! and 0! are 1 but factorial is not defined for
    * negative numbers. If this InfiniteInteger is negative then NaN is returned.
    *
    * @return the result including ∞ and NaN
    * @see #power(MutableInfiniteInteger)
    */
   @Override
   public MutableInfiniteInteger factorial()
   {
      if (this.isNegative || this.isNaN())
         return MutableInfiniteInteger.NaN;  //factorial is not defined for negative numbers
      if (this.equals(MutableInfiniteInteger.POSITIVE_INFINITY)) return this;  //-Infinity is covered above
      if (this.equalValue(0) || this.equalValue(1)) return set(MutableInfiniteInteger.valueOf(1));

      MutableInfiniteInteger result = this;
      MutableInfiniteInteger integerCursor = this.copy().subtract(1);
      while (!integerCursor.equalValue(1))  //don't bother multiplying by 1
      {
         result = result.multiply(integerCursor);
         integerCursor = integerCursor.subtract(1);
      }
      return set(result);
   }

   /**
    * @return true if this InfiniteInteger is prime, false if it is composite
    * @throws ArithmeticException if this is neither prime nor composite
    */
   public boolean isPrime()
   {
      if (this.isNegative || !this.isFinite())
         throw new ArithmeticException("Prime is only defined for integers > 1 and 0");
      if (this.equalValue(0)) return false;
      if (this.equalValue(1))
         throw new ArithmeticException("1 is neither prime nor composite (primality is not defined for 1)");
      if (this.equalValue(2)) return true;
      if (BitWiseUtil.isEven(this.intValue())) return false;

      final InfinitelyLinkedList<PrimeSieve> allSieves = new InfinitelyLinkedList<>();
      MutableInfiniteInteger index = MutableInfiniteInteger.valueOf(3);
      while (is(index, LESS_THAN_OR_EQUAL_TO, this))
      {
         boolean isIndexPrime = true;
         for (final PrimeSieve currentSieve : allSieves)
         {
            if (is(currentSieve.currentValue, EQUAL_TO, this)) return false;
            if (is(currentSieve.currentValue, LESS_THAN, index)) currentSieve.next();
            else if (is(currentSieve.currentValue, EQUAL_TO, index))
            {
               currentSieve.next();
               isIndexPrime = false;
            }
         }
         if (isIndexPrime) allSieves.add(new PrimeSieve(index));
         index = index.add(2);
      }
      return true;
   }

   private static final class PrimeSieve
   {
      private final MutableInfiniteInteger incrementAmount;
      private MutableInfiniteInteger currentValue;

      public PrimeSieve(final MutableInfiniteInteger incrementAmount)
      {
         currentValue = incrementAmount.copy();
         this.incrementAmount = incrementAmount.copy().multiply(2);
      }

      public void next()
      {
         currentValue = currentValue.add(incrementAmount);
      }
   }

   /**
    * This method delegates because the formula used is exactly the same.
    * Entire code: <blockquote>{@code return leastCommonMultiple(MutableInfiniteInteger.valueOf(otherValue));}</blockquote>
    *
    * @see #leastCommonMultiple(MutableInfiniteInteger)
    * @see #valueOf(long)
    */
   public MutableInfiniteInteger leastCommonMultiple(final long otherValue)
   {
      return leastCommonMultiple(MutableInfiniteInteger.valueOf(otherValue));
   }

   /**
    * Entire code: <blockquote>{@code return leastCommonMultiple(MutableInfiniteInteger.valueOf(otherValue));}</blockquote>
    *
    * @see #leastCommonMultiple(MutableInfiniteInteger)
    * @see #valueOf(BigInteger)
    */
   public MutableInfiniteInteger leastCommonMultiple(final BigInteger otherValue)
   {
      return leastCommonMultiple(MutableInfiniteInteger.valueOf(otherValue));
   }

   /**
    * Returns a MutableInfiniteInteger whose value is the <a href="https://en.wikipedia.org/wiki/Least_common_multiple">Least common
    * multiple</a> of
    * {@code this.abs()} and {@code otherValue.abs()}. Returns NaN if either is 0 or is not finite.
    *
    * @param otherValue value with which the LCM is to be computed.
    * @return {@code LCM(abs(this), abs(otherValue))}
    */
   public MutableInfiniteInteger leastCommonMultiple(final MutableInfiniteInteger otherValue)
   {
      final MutableInfiniteInteger thisAbs = this.copy().abs(), otherAbs = otherValue.copy().abs();

      if (!thisAbs.isFinite() || !otherAbs.isFinite()) return MutableInfiniteInteger.NaN;
      if (thisAbs.equalValue(0) || otherAbs.equalValue(0)) return MutableInfiniteInteger.NaN;
      if (thisAbs.equalValue(1)) return otherAbs.copy();
      if (otherAbs.equalValue(1)) return thisAbs.copy();
      if (thisAbs.equals(otherAbs)) return thisAbs.copy();

      class Sieve
      {
         private final MutableInfiniteInteger increment;
         private MutableInfiniteInteger currentValue;

         private Sieve(final MutableInfiniteInteger increment)
         {
            this.increment = increment.copy();  //technically there's no need to defensive copy increment
            currentValue = increment;
         }

         private void next(){currentValue = currentValue.add(increment);}
      }

      final Sieve thisSieve = new Sieve(thisAbs);
      final Sieve otherSieve = new Sieve(otherAbs);
      while (true)
      {
         final int compareResult = thisSieve.currentValue.compareTo(otherSieve.currentValue);
         if (isComparisonResult(compareResult, EQUAL_TO)) return otherSieve.currentValue;
         else if (isComparisonResult(compareResult, GREATER_THAN)) otherSieve.next();
         else thisSieve.next();
      }
   }

   /**
    * This method delegates because the formula used is exactly the same.
    * Entire code: <blockquote>{@code return greatestCommonDivisor(MutableInfiniteInteger.valueOf(otherValue));}</blockquote>
    *
    * @see #greatestCommonDivisor(MutableInfiniteInteger)
    * @see #valueOf(long)
    */
   public MutableInfiniteInteger greatestCommonDivisor(final long otherValue)
   {
      return greatestCommonDivisor(MutableInfiniteInteger.valueOf(otherValue));
   }

   /**
    * Entire code: <blockquote>{@code return greatestCommonDivisor(MutableInfiniteInteger.valueOf(otherValue));}</blockquote>
    *
    * @see #greatestCommonDivisor(MutableInfiniteInteger)
    * @see #valueOf(BigInteger)
    */
   public MutableInfiniteInteger greatestCommonDivisor(final BigInteger otherValue)
   {
      return greatestCommonDivisor(MutableInfiniteInteger.valueOf(otherValue));
   }

   /**
    * Returns a MutableInfiniteInteger whose value is the <a href="http://en.wikipedia.org/wiki/Greatest_common_divisor">greatest common
    * divisor</a> of
    * {@code this.abs()} and {@code otherValue.abs()}. Returns ∞ if
    * {@code this == 0 && otherValue == 0}. Returns NaN if either is not finite.
    *
    * @param otherValue value with which the GCD is to be computed.
    * @return {@code GCD(abs(this), abs(otherValue))}
    */
   public MutableInfiniteInteger greatestCommonDivisor(final MutableInfiniteInteger otherValue)
   {
      MutableInfiniteInteger thisRemaining = this.copy().abs(), otherRemaining = otherValue.copy().abs();

      if (!thisRemaining.isFinite() || !otherRemaining.isFinite()) return MutableInfiniteInteger.NaN;
      if (thisRemaining.equalValue(0) && otherRemaining.equalValue(0)) return MutableInfiniteInteger.POSITIVE_INFINITY;
      if (thisRemaining.equalValue(1) || otherRemaining.equalValue(1)) return MutableInfiniteInteger.valueOf(1);
      if (thisRemaining.equals(otherRemaining)) return thisRemaining.copy();
      if (thisRemaining.equalValue(0)) return otherRemaining.copy();
      if (otherRemaining.equalValue(0)) return thisRemaining.copy();

      MutableInfiniteInteger smallerNumber = thisRemaining, biggerNumber = otherRemaining;
      if (is(this.copy().abs(), GREATER_THAN, otherRemaining.copy().abs()))
      {
         smallerNumber = otherRemaining;
         biggerNumber = thisRemaining;
      }
      //if the lower is a factor of the greater
      if (biggerNumber.copy().divideReturnRemainder(smallerNumber).equalValue(0)) return smallerNumber.copy();

      final MutableInfiniteInteger thisSqrt = thisRemaining.sqrtCeil(), otherSqrt = otherRemaining.sqrtCeil();
      MutableInfiniteInteger divisor = new MutableInfiniteInteger(1);

      //I can't use intValue because I need all 32 bits
      //while each have a trailing 0 digit
      while (((int) thisRemaining.longValue()) == 0 && ((int) otherRemaining.longValue()) == 0)
      {
         thisRemaining = thisRemaining.divideByPowerOf2DropRemainder(32);
         otherRemaining = otherRemaining.divideByPowerOf2DropRemainder(32);
         divisor = divisor.multiplyByPowerOf2(32);
      }
      //I can use intValue because I only need 31 bits (really I only need 1 bit)
      while (BitWiseUtil.isEven(thisRemaining.intValue()) && BitWiseUtil.isEven(otherRemaining.intValue()))
      {
         thisRemaining = thisRemaining.divideByPowerOf2DropRemainder(1);
         otherRemaining = otherRemaining.divideByPowerOf2DropRemainder(1);
         divisor = divisor.multiplyByPowerOf2(1);
      }
      //that covers the fast cases

      MutableInfiniteInteger possibleDivisor = new MutableInfiniteInteger(3);
      while (true)
      {
         //there are no divisors that are > sqrt. likewise for > remaining
         //the sqrt is of the originals and not recalculated for the remaining ones which is why I need to check both
         //it might not be valid to recalculate the sqrt each time which is why I don't
         if (is(possibleDivisor, GREATER_THAN, thisSqrt) || is(possibleDivisor, GREATER_THAN, thisRemaining)) break;
         if (is(possibleDivisor, GREATER_THAN, otherSqrt) || is(possibleDivisor, GREATER_THAN, otherRemaining)) break;

         //if they both can divide without remainder
         final boolean thisDivides = thisRemaining.copy().divideReturnRemainder(possibleDivisor).equalValue(0);
         final boolean otherDivides = otherRemaining.copy().divideReturnRemainder(possibleDivisor).equalValue(0);
         if (thisDivides && otherDivides)
         {
            thisRemaining = thisRemaining.divideDropRemainder(possibleDivisor);
            otherRemaining = otherRemaining.divideDropRemainder(possibleDivisor);
            divisor = divisor.multiply(possibleDivisor);
            //do not increase possibleDivisor because it might divide multiple times
         }
         else possibleDivisor = possibleDivisor.add(2);  //every odd since I know they are no even factors left
      }

      return divisor;
   }

   /**
    * This method finds the ceiling of the square root of this InfiniteInteger.
    * For example if the actual square root is 4.1 the returned value will be 5,
    * if the number is 25 then 5 is returned etc.
    * If ∞ is passed in then ∞ is returned.
    * This method does not mutate and the returned value will be a copy.
    *
    * @return an upper bound for the square root. NaN is returned if this is negative or NaN.
    * @see Math#ceil(double)
    */
   private MutableInfiniteInteger sqrtCeil()
   {
      if (this.isNaN() || this.isNegative) return MutableInfiniteInteger.NaN;
      if (this.equals(MutableInfiniteInteger.POSITIVE_INFINITY)) return this;

      //if fits into signed long then delegate
      if (this.equalValue(this.longValue()))
      {
         return MutableInfiniteInteger.valueOf(((long) Math.ceil(Math.sqrt(this.longValue()))));
      }

      MutableInfiniteInteger higher = this.estimateSqrt();
      if (higher.copy().power(2).equals(this)) return higher;  //if already the exact answer
      MutableInfiniteInteger lower = higher.copy().divideByPowerOf2DropRemainder(1);
      MutableInfiniteInteger difference, midway;

      while (true)
      {
         difference = higher.copy().subtract(lower);
         //avoid cutting small numbers in half:
         if (is(difference, LESS_THAN, MutableInfiniteInteger.valueOf(4))) break;
         difference = difference.divideByPowerOf2DropRemainder(1);
         midway = difference.add(lower);  //diff not copied because I no longer need it
         final int compareResult = midway.copy().power(2).compareTo(this);
         if (isComparisonResult(compareResult, EQUAL_TO)) return midway;
         //if midway^2 > this then midway is an upper bound for the sqrt
         if (isComparisonResult(compareResult, GREATER_THAN)) higher = midway;
         else lower = midway;
      }
      //if difference < 4 then just have lower count up (max of 3 times)
      while (true)
      {
         if (is(lower.copy().power(2), GREATER_THAN_OR_EQUAL_TO, this)) return lower;
         lower = lower.add(1);
      }
      //unreachable
   }

   /**
    * This method is a fast high estimation of the square root of this InfiniteInteger.
    * The number returned will a power of 2 greater than or equal to the actual square root.
    * Therefore worst case scenario the estimation returned will be one less than twice the actual square root
    * (ie {@code worst_estimation == sqrt *2 -1}).
    * The value of this InfiniteInteger can't be negative and must be finite.
    * This method does not mutate and the returned value will be a copy.
    *
    * @return an upper bound for the square root
    */
   private MutableInfiniteInteger estimateSqrt()
   {
      if (this.equalValue(0) || this.equalValue(1)) return this.copy();
      if (is(this, LESS_THAN_OR_EQUAL_TO, MutableInfiniteInteger.valueOf(4))) return MutableInfiniteInteger.valueOf(2);

      //cutting the number of digits in half works for any number base (except base 1) but base 2 has the closest estimation
      //worst case is off by 1 digit therefore base 2 has the smallest error (being off by *2)
      //base 1 would always return thisValue/2 which isn't a good estimation
      MutableInfiniteInteger binaryDigits = MutableInfiniteInteger.valueOf(0);
      DequeNode<Integer> thisCursor = this.magnitudeHead;
      while (thisCursor.getNext() != null)
      {
         binaryDigits = binaryDigits.add(32);
         thisCursor = thisCursor.getNext();
      }
      binaryDigits = binaryDigits.add(32);  //for the last node
      //counting leading 0s functions as expected for unsigned numbers
      binaryDigits = binaryDigits.subtract(Integer.numberOfLeadingZeros(thisCursor.getData().intValue()));
      //subtract the unused bits

      //I can use binaryDigits.intValue because I only need 31 bits (really I only need 1 bit)
      final boolean isExact = BitWiseUtil.isOdd((binaryDigits.intValue()));
      if (isExact) binaryDigits = binaryDigits.add(1);  //make it even by rounding up
      binaryDigits = binaryDigits.divideByPowerOf2DropRemainder(1);

      MutableInfiniteInteger estimation = MutableInfiniteInteger.valueOf(1);
      while (isComparisonResult(binaryDigits.compareTo(32), GREATER_THAN_OR_EQUAL_TO))
      {
         binaryDigits = binaryDigits.subtract(32);
         estimation = estimation.multiplyByPowerOf2(32);
      }
      estimation = estimation.multiplyByPowerOf2(binaryDigits);

      if (isExact && this.isPowerOf2()) estimation = estimation.divideByPowerOf2DropRemainder(1);
      //the estimation will be the actual square root in this case

      return estimation;
   }

   /**
    * Returns the absolute value of this InfiniteInteger.
    *
    * @return itself or the positive version of this
    * @see Math#abs(double)
    */
   @Override
   public MutableInfiniteInteger abs()
   {
      if (!isNegative) return this;  //includes 0, NaN, and +Infinity
      if (this.equals(MutableInfiniteInteger.NEGATIVE_INFINITY)) return MutableInfiniteInteger.POSITIVE_INFINITY;
      isNegative = false;
      return this;
   }

   /**
    * Returns an InfiniteInteger whose value is {@code (0-this)}.
    *
    * @return {@code -this}
    */
   @Override
   public MutableInfiniteInteger negate()
   {
      if (isNaN() || this.equalValue(0)) return this;  //0 is a special case because -0 == 0
      if (this.equals(MutableInfiniteInteger.NEGATIVE_INFINITY)) return MutableInfiniteInteger.POSITIVE_INFINITY;
      if (this.equals(MutableInfiniteInteger.POSITIVE_INFINITY)) return MutableInfiniteInteger.NEGATIVE_INFINITY;
      isNegative = !isNegative;
      return this;
   }

   /**
    * @return -1, 0 or 1 as the value of this number is negative, zero or
    * positive respectively. NaN returns 0.
    */
   @Override
   public byte signum()
   {
      if (isNegative) return -1;
      if (this.equalValue(0) || this.isNaN()) return 0;
      return 1;
   }

   //TODO: add min/max. maybe static (InfInt, InfInt) only?
   //big int also has bitwise operations. gcd. and weird methods

   /**
    * Compares this == NaN.
    *
    * @return true if this InfiniteInteger is the constant for NaN.
    * @see #NaN
    */
   @Override
   public boolean isNaN(){return this == MutableInfiniteInteger.NaN;}

   /**
    * Compares this InfiniteInteger to both positive and negative infinity.
    *
    * @return true if this InfiniteInteger is either of the infinity constants.
    * @see #POSITIVE_INFINITY
    * @see #NEGATIVE_INFINITY
    */
   @Override
   public boolean isInfinite()
   {
      return (this == MutableInfiniteInteger.POSITIVE_INFINITY || this == MutableInfiniteInteger.NEGATIVE_INFINITY);
   }

   /**
    * Compares this InfiniteInteger to ±∞ and NaN (returns false if this is any of them).
    *
    * @return true if this InfiniteInteger is not a special value (ie if this is a finite number).
    * @see #NaN
    * @see #POSITIVE_INFINITY
    * @see #NEGATIVE_INFINITY
    */
   @Override
   public boolean isFinite(){return (!this.isNaN() && !this.isInfinite());}

   /**
    * @throws ArithmeticException if this == NaN
    */
   @Override
   public void signalNaN(){if (isNaN()) throw new ArithmeticException("Not a number.");}

   /**
    * Returns {@code true} if <code>this.abs() == 2<sup>n</sup></code> where {@code n} is any finite non-negative integer.
    * The exception is that 0 ({@code n} is -∞) returns {@code true}.
    *
    * @return {@code true} if this is a power of 2 (including 0 and 1)
    * @see #abs()
    */
   //this method exists because I need it for estimateSqrt(MutableInfiniteInteger)
   public boolean isPowerOf2()
   {
      if (!this.isFinite()) return false;

      DequeNode<Integer> thisCursor;
      for (thisCursor = this.magnitudeHead; thisCursor.getNext() != null; thisCursor = thisCursor.getNext())
      {
         //all nodes that aren't the most significant must be 0
         if (0 != thisCursor.getData().intValue()) return false;
      }

      //and check the most significant node
      return BitWiseUtil.isPowerOf2(thisCursor.getData().intValue());
   }

   /**
    * Compares this MutableInfiniteInteger with the specified object for numeric equality.
    *
    * @param other the value to be compared to this
    * @return true if this MutableInfiniteInteger has the same numeric value as other. false if other is not a number
    */
   @Override
   public boolean equalValue(final Object other)
   {
      if (this == other) return true;
      if (other instanceof Byte) return this.equals(MutableInfiniteInteger.valueOf((Byte) other));
      if (other instanceof Short) return this.equals(MutableInfiniteInteger.valueOf((Short) other));
      if (other instanceof Integer) return this.equals(MutableInfiniteInteger.valueOf((Integer) other));
      if (other instanceof Long) return this.equals(MutableInfiniteInteger.valueOf((Long) other));
      if (other instanceof BigInteger) return this.equals(MutableInfiniteInteger.valueOf((BigInteger) other));
      if (other instanceof InfiniteInteger) return this.equals(MutableInfiniteInteger.valueOf((InfiniteInteger) other));
      if (other instanceof MutableInfiniteInteger) return this.equals(other);
      //null returns false
      //unknown class returns false
      return false;
   }

   /**
    * Compares this MutableInfiniteInteger with the specified value for numeric equality.
    *
    * @param value the value to be compared to this
    * @return true if this MutableInfiniteInteger has the same numeric value as the value parameter
    * @see #longValueExact()
    * @see #compareTo(long)
    */
   @Override
   public boolean equalValue(final long value)
   {
      if (!this.isFinite()) return false;

      if (magnitudeHead.getNext() != null && magnitudeHead.getNext().getNext() != null)
         return false;  //this is larger than max unsigned long (this check does need to be made)
      if (magnitudeHead.getNext() != null && (magnitudeHead.getNext().getData().intValue() & Long.MIN_VALUE) != 0)
         return false;  //this is larger than max signed long

      return (value == this.longValue());
   }

   @Override
   public boolean equals(final Object other)
   {
      if (other == null) return false;
      if (this == other) return true;
      if (!(other instanceof MutableInfiniteInteger)) return false;

      final MutableInfiniteInteger that = (MutableInfiniteInteger) other;
      //these are singletons. if not the same object then it's not equal
      if (!this.isFinite() || !that.isFinite()) return false;
      if (isNegative != that.isNegative) return false;
      DequeNode<Integer> thisCursor = this.magnitudeHead;
      DequeNode<Integer> otherCursor = that.magnitudeHead;
      while (thisCursor != null && otherCursor != null)
      {
         if (!Objects.equals(thisCursor.getData(), otherCursor.getData())) return false;
         thisCursor = thisCursor.getNext();
         otherCursor = otherCursor.getNext();
      }
      return (thisCursor == otherCursor);  //they must both be null (at end)
   }

   /**
    * Compares this MutableInfiniteInteger with the specified other for numeric equality.
    * The natural order is as expected with ±∞ being at either end.
    * With the exception that ∞ &lt; NaN (this is consistent with Float/Double.compareTo).
    *
    * @param other the value to be compared to this
    * @return -1, 0 or 1 if this MutableInfiniteInteger is numerically less than, equal
    * to, or greater than other.
    */
   @Override
   public int compareTo(final MutableInfiniteInteger other)
   {
      if (this == other) return THIS_EQUAL;  //recall that special values are singletons
      if (this == MutableInfiniteInteger.NaN || other == MutableInfiniteInteger.NEGATIVE_INFINITY) return THIS_GREATER;
      if (this == MutableInfiniteInteger.NEGATIVE_INFINITY || other == MutableInfiniteInteger.NaN) return THIS_LESSER;
      //+Infinity is only greater if NaN isn't involved
      if (this == MutableInfiniteInteger.POSITIVE_INFINITY) return THIS_GREATER;
      if (other == MutableInfiniteInteger.POSITIVE_INFINITY) return THIS_LESSER;

      if (isNegative && !other.isNegative) return THIS_LESSER;
      if (!isNegative && other.isNegative) return THIS_GREATER;  //also covers if this.equalValue(0)

      //at this point: they are not the same object, they have the same sign, they are not special values.

      final int magnitudeResult = compareMagnitude(other);
      if (isNegative) return -magnitudeResult;  //if both are negative then the smaller abs is closer to +Infinity
      return magnitudeResult;
   }

   private int compareMagnitude(final MutableInfiniteInteger other)
   {
      //since the lengths can be any integer I first need to compare lengths
      DequeNode<Integer> otherCursor = other.magnitudeHead;
      DequeNode<Integer> thisCursor = this.magnitudeHead;
      //this loop will not execute if they both have 1 node
      //which is correct since they have equal length and are both pointing to last node
      while (thisCursor.getNext() != null || otherCursor.getNext() != null)
      {
         if (thisCursor.getNext() != null && otherCursor.getNext() != null)
         {
            thisCursor = thisCursor.getNext();
            otherCursor = otherCursor.getNext();
         }
         else if (thisCursor.getNext() != null) return THIS_GREATER;
         else return THIS_LESSER;
      }

      //they have the same number of nodes and both cursors are pointing to the most significant (last) node
      int thisData, otherData;
      while (thisCursor != null)
      {
         thisData = thisCursor.getData().intValue();
         otherData = otherCursor.getData().intValue();
         if (thisData != otherData) return Integer.compareUnsigned(thisData, otherData);
         thisCursor = thisCursor.getPrev();
         otherCursor = otherCursor.getPrev();
      }

      //same length and all nodes have the same data
      return THIS_EQUAL;
   }

   /**
    * Compares this InfiniteInteger with the specified other for numeric equality.
    * Even though sorting is not possible this method returns as expected.
    * Entire code: <blockquote>{@code return this.compareTo(InfiniteInteger.valueOf(other));}</blockquote>
    *
    * @param other the value to be compared to this
    * @see #compareTo(MutableInfiniteInteger)
    * @see Comparable#compareTo(Object)
    */
   @Override
   public int compareTo(final BigInteger other){return this.compareTo(MutableInfiniteInteger.valueOf(other));}

   /**
    * Compares this InfiniteInteger with the specified other for numeric equality.
    * Even though sorting is not possible this method returns as expected.
    * Entire code: <blockquote>{@code return this.compareTo(InfiniteInteger.valueOf(other));}</blockquote>
    *
    * @param other the value to be compared to this
    * @see #compareTo(MutableInfiniteInteger)
    * @see Comparable#compareTo(Object)
    */
   @Override
   public int compareTo(final long other){return this.compareTo(MutableInfiniteInteger.valueOf(other));}

   /**
    * Returns the hash code for this InfiniteInteger.
    * Collisions are, in theory, likely when comparing all possible integers
    * with all possible values that can fit into int.
    *
    * @return hash code for this InfiniteInteger.
    */
   @Override
   public int hashCode()
   {
      if (this == MutableInfiniteInteger.NaN) return Integer.MIN_VALUE;  //so that 0 and NaN will not have a collision
      if (this.isInfinite()) return (Integer.MAX_VALUE * this.signum());  //to prevent collision with +/-1
      DequeNode<Integer> cursor = magnitudeHead;
      int hash = Boolean.hashCode(isNegative);
      while (cursor != null)
      {
         hash ^= cursor.getData().intValue();
         cursor = cursor.getNext();
      }
      return hash;
   }

   /**
    * This shows the lowest 20 digits in base 10 so that this number can display something useful to humans
    * when given to a logger or exception. If the number is cut off then it will have a … (after the minus sign).
    * This method will always fit within a string and is reasonably fast. Max long is 19 digits so it won't
    * get cut off.
    *
    * @return String representation of this MutableInfiniteInteger.
    * The format may change arbitrarily.
    * @see #toString(int)
    */
   @Override
   public String toString()
   {
      //return toDebuggingString();
      //These values for the singletons are acceptable for debugging since the number is base 10.
      if (this.equals(MutableInfiniteInteger.POSITIVE_INFINITY)) return "Infinity";
      if (this.equals(MutableInfiniteInteger.NEGATIVE_INFINITY)) return "-Infinity";
      if (this.isNaN()) return "NaN";

      //this check is technically only needed for 0 but should be faster
      if (this.equalValue(this.longValue())) return RadixUtil.toString(this.longValue(), 10);

      return toStringSlow(10, true);
   }

   /**
    * <p>Returns the String representation of this MutableInfiniteInteger in the
    * given radix. The digit-to-character mapping
    * provided by {@link RadixUtil#toString(long, int)} is used, and a minus
    * sign is prepended if appropriate.</p>
    *
    * <p>Note the special values of ∞, -∞, and ∉ℤ (for NaN) which were chosen to avoid collision
    * with any radix. These values are returned for all radix values.</p>
    *
    * @param radix the number base to be used. {@link RadixUtil#toString(long, int)} currently only supports a range of 1 .. 62 (1 and 62
    *              are both inclusive)
    * @return String representation of this MutableInfiniteInteger in the given radix.
    * @throws IllegalArgumentException if radix is illegal
    * @throws WillNotFitException      if this MutableInfiniteInteger can't fit into a string of the given radix
    * @see RadixUtil#toString(long, int)
    */
   public String toString(final int radix)
   {
      RadixUtil.enforceStandardRadix(radix);

      if (this.equals(MutableInfiniteInteger.POSITIVE_INFINITY)) return "∞";
      if (this.equals(MutableInfiniteInteger.NEGATIVE_INFINITY)) return "-∞";
      if (this.isNaN()) return "∉ℤ";

      if (this.equalValue(this.longValue())) return RadixUtil.toString(this.longValue(), radix);
      //This is larger than long so it won't fit into a base 1 string (if > int but < long then above throws).
      //The default toString cuts off so that it always fits.
      if (1 == radix) throw new WillNotFitException(this + " in base 1 would exceed max string length.");

      //all other radix check for exceeding max string as they build because it's easier than doing logBaseX
      if (BitWiseUtil.isPowerOf2(radix)) return toStringPowerOf2(radix);
      else return toStringSlow(radix, false);
   }

   /**
    * optimized for radix powers of 2
    */
   private String toStringPowerOf2(final int radix)
   {
      final List<String> stringList = new InfinitelyLinkedList<>();
      for (DequeNode<Integer> cursor = magnitudeHead; cursor != null; cursor = cursor.getNext())
      {
         final long nodeValue = Integer.toUnsignedLong(cursor.getData());
         //I could check the list size here or after loop since I used InfinitelyLinkedList
         if (stringList.size() == Integer.MAX_VALUE)
            throw new WillNotFitException(this + " in base " + radix + " would exceed max string length.");
         stringList.add(Long.toUnsignedString(nodeValue, radix));
      }

      //get the length of max unsigned int in this radix to know how large each node needs to be
      final int expectedLength = RadixUtil.toString(Integer.toUnsignedLong(-1), radix).length();
      //base 2 is 32 chars
      //base 4 is 16 chars
      //base 8 is 11 chars
      //base 16 is 8 chars
      //base 32 is 7 chars
      if (stringList.size() > Integer.MAX_VALUE / expectedLength)  //overflow conscious
         // Must validate capacity before FriendlyOverflowStringBuilder
         throw new WillNotFitException(this + " in base " + radix + " would exceed max string length.");
      final FriendlyOverflowStringBuilder stringBuilder = new FriendlyOverflowStringBuilder(this + " in base " + radix,
         stringList.size() * expectedLength);
      if (isNegative) stringBuilder.append("-");

      //most significant node isn't padded
      stringBuilder.append(stringList.get(stringList.size() - 1));
      for (int i = stringList.size() - 2; i >= 0; i--)
      {
         stringBuilder.append(leftPad(stringList.get(i), expectedLength));
      }

      return stringBuilder.toString();
   }

   private String leftPad(final String original, final int expectedLength)
   {
      final StringBuilder stringBuilder = new StringBuilder(expectedLength);
      stringBuilder.append(original).reverse();
      while (stringBuilder.length() < expectedLength){stringBuilder.append('0');}
      return stringBuilder.reverse().toString();
   }

   private String toStringSlow(final int radix, final boolean forceFit)
   {
      //this is the one place where FriendlyOverflowStringBuilder can't be used because it is used to make the error message.
      final StringBuilder stringBuilder = new StringBuilder(32);
      MutableInfiniteInteger valueRemaining = this;
      while (!valueRemaining.equalValue(0))
      {
         final IntegerQuotient<MutableInfiniteInteger> integerQuotient = valueRemaining.divide(radix);
         //since radix <= 62 I know that valueRemaining%radix < 62 and thus always fits in int
         final String nodeAsRadix = RadixUtil.toString(integerQuotient.getRemainder().intValue(), radix);
         if (forceFit && stringBuilder.length() == 20)
         {
            //TODO: could be made faster by making a copy of the lowest 3 nodes so that divide isn't overwhelmed
            stringBuilder.append("…");
            break;
         }
         if (Integer.MAX_VALUE - stringBuilder.length() < nodeAsRadix.length())  //overflow conscious
            throw new WillNotFitException(this + " in base " + radix + " would exceed max string length.");
         stringBuilder.append(nodeAsRadix);
         valueRemaining = integerQuotient.getWholeResult();
      }
      if (isNegative && stringBuilder.length() == Integer.MAX_VALUE)
         throw new WillNotFitException(this + " in base " + radix + " would exceed max string length.");
      if (isNegative) stringBuilder.append("-");
      return stringBuilder.reverse().toString();
   }

   String toDebuggingString()
   {
      if (this.equals(MutableInfiniteInteger.POSITIVE_INFINITY)) return "+Infinity";
      if (this.equals(MutableInfiniteInteger.NEGATIVE_INFINITY)) return "-Infinity";
      if (this.isNaN()) return "NaN";

      //Doesn't use FriendlyOverflowStringBuilder because I won't use such huge numbers for debugging this class
      //so to keep this method simple just use StringBuilder (removes a dependency for the sake of debugging).
      final StringBuilder stringBuilder = new StringBuilder();
      if (isNegative) stringBuilder.append("- ");
      else stringBuilder.append("+ ");

      //Since this method is used to debug this class I'll check some invariants.
      //actually a sub-zero node exists during readFromStream (for 1 line)
      if (magnitudeHead.getPrev() != null) throw new IllegalStateException("Bug: sub-zero nodes exist");
      if (magnitudeHead.getNext() == null && magnitudeHead.getData() == 0 && isNegative)
         throw new IllegalStateException("Bug: negative zero found");
      //Don't check for leading 0s (at magnitudeTail) because they can exist temporarily during which this method may be called.

      for (DequeNode<Integer> cursor = magnitudeHead; cursor != null; cursor = cursor.getNext())
      {
         stringBuilder.append(Integer.toHexString(cursor.getData()).toUpperCase());
         stringBuilder.append(", ");  //there will be a trailing ", " but I don't care
      }
      return stringBuilder.toString();
   }

   /**
    * For debugging this class. Should be 0 outside of methods. Inside methods it can be 1. I don't think 2+ is possible
    * so if that happens there is a bug.
    */
   int leadingZeroCount()
   {
      DequeNode<Integer> magnitudeCursor = getMagnitudeTail();
      int leadingZeroCount = 0;
      while (magnitudeCursor.getData().equals(0))
      {
         ++leadingZeroCount;
         magnitudeCursor = magnitudeCursor.getPrev();
      }
      return leadingZeroCount;
   }

   /**
    * In order to maintain the singleton constants they will not be copied.
    * So ±∞ and NaN will return themselves but all others will be copied as expected.
    *
    * @return a copy or a defined singleton
    */
   @Override
   public MutableInfiniteInteger copy()
   {
      if (!this.isFinite()) return this;
      final MutableInfiniteInteger returnValue = new MutableInfiniteInteger(0);
      returnValue.isNegative = this.isNegative;
      DequeNode<Integer> returnCursor = returnValue.magnitudeHead;
      DequeNode<Integer> thisCursor = this.magnitudeHead;

      returnCursor.setData(thisCursor.getData());  //must be outside the loop since the first node already exists
      thisCursor = thisCursor.getNext();
      while (thisCursor != null)
      {
         returnCursor = DequeNode.Factory.createNodeAfter(returnCursor, thisCursor.getData());
         thisCursor = thisCursor.getNext();
      }
      return returnValue;
   }

   /**
    * Mutates this to have the same value as the parameter
    *
    * @return the result which is itself or a defined singleton
    */
   public MutableInfiniteInteger set(final long newValue)
   {
      return this.set(MutableInfiniteInteger.valueOf(newValue));
   }

   /**
    * Mutates this to have the same value as the parameter
    *
    * @return the result which is itself or a defined singleton
    */
   public MutableInfiniteInteger set(final BigInteger newValue)
   {
      return this.set(MutableInfiniteInteger.valueOf(newValue));
   }

   /**
    * Mutates this to have the same value as the parameter.
    * In order to maintain the singleton constants mutation will not
    * occur if this or the parameter are a singleton constant.
    *
    * @return the result which is itself or a defined singleton
    */
   public MutableInfiniteInteger set(final InfiniteInteger newValue)
   {
      return this.set(MutableInfiniteInteger.valueOf(newValue));
   }

   /**
    * Mutates this to have the same value as the parameter (a copy not a live reference).
    * In order to maintain the singleton constants mutation will not
    * occur if this or the parameter are a singleton constant.
    *
    * @return the result which is itself or a defined singleton
    */
   public MutableInfiniteInteger set(final MutableInfiniteInteger newValue)
   {
      if (!newValue.isFinite()) return newValue;  //immutable constants can't be changed or copied.
      if (!this.isFinite()) return this;
      this.isNegative = newValue.isNegative;  //is a primitive boolean so it's immutable
      this.magnitudeHead = newValue.copy().magnitudeHead;
      return this;
   }

   public static MutableInfiniteInteger readFromStream(final ObjectStreamReader reader)
   {
      final MutableInfiniteInteger result = new MutableInfiniteInteger(0);
      DequeNode<Integer> resultCursor = result.magnitudeHead;

      switch (reader.readObject(byte.class))
      {
         case 1:
            return MutableInfiniteInteger.NaN;
         case 2:
            return MutableInfiniteInteger.POSITIVE_INFINITY;
         case 3:
            return MutableInfiniteInteger.NEGATIVE_INFINITY;
         case 4:
            result.isNegative = true;
            break;
         //case 5: isNegative is already false
      }

      int followingNodeCount = Byte.toUnsignedInt(reader.readObject(byte.class));
      while (followingNodeCount != 0)
      {
         resultCursor = DequeNode.Factory.createNodeAfter(resultCursor, reader.readObject(int.class));
         --followingNodeCount;
         if (followingNodeCount == 0) followingNodeCount = Byte.toUnsignedInt(reader.readObject(byte.class));
      }
      result.magnitudeHead = result.magnitudeHead.getNext();
      result.magnitudeHead.getPrev().remove();  //remove the placeholder 0
      return result;
   }

   @Override
   public void writeToStream(final ObjectStreamWriter writer)
   {
      if (this.isNaN()) writer.writeObject((byte) 1);
      else if (this.equals(MutableInfiniteInteger.POSITIVE_INFINITY)) writer.writeObject((byte) 2);
      else if (this.equals(MutableInfiniteInteger.NEGATIVE_INFINITY)) writer.writeObject((byte) 3);
      else if (this.isNegative) writer.writeObject((byte) 4);  //finite negative
      else writer.writeObject((byte) 5);  //finite positive

      if (!this.isFinite()) return;  //They have no nodes so I'm done.

      //TODO: replace with byte and max size (Integer.MAX_VALUE - 8) int array since can write array now
      final int[] someNodes = new int[255];
      DequeNode<Integer> cursor = this.magnitudeHead;
      while (cursor != null)
      {
         int filledCount = 0;
         while (filledCount < 255 && cursor != null)
         {
            someNodes[filledCount] = cursor.getData();
            ++filledCount;
            cursor = cursor.getNext();
         }
         writer.writeObject((byte) filledCount);
         for (int filledIndex = 0; filledIndex < filledCount; ++filledIndex)
         {
            writer.writeObject(someNodes[filledIndex]);
         }
      }
      //Mark that there are no more nodes.
      writer.writeObject((byte) 0);
   }

   private Object writeReplace() throws ObjectStreamException
   {
      magnitudeHead = null;
      throw new NotSerializableException();
   }

   private Object readResolve() throws ObjectStreamException
   {
      magnitudeHead = null;
      throw new NotSerializableException();
   }

   private void writeObject(final ObjectOutputStream out) throws IOException
   {
      magnitudeHead = null;
      throw new NotSerializableException();
   }

   private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException
   {
      magnitudeHead = null;
      throw new NotSerializableException();
   }

   private void readObjectNoData() throws ObjectStreamException
   {
      magnitudeHead = null;
      throw new NotSerializableException();
   }
}
