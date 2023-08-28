package com.github.skySpiral7.java.infinite.numbers;

import java.util.Objects;

/**
 * <p>An immutable bean used to represent the results of integer division.
 * An integer divided by an integer resulting in an integer is not always possible but sometimes required.
 * Therefore observe the definition for integer division and for the remainder.</p>
 *
 * <code>numerator / denominator is defined as numerator = (denominator * wholeResult) ± remainder</code><br />
 * <code>remainder = |numerator| - |(denominator * wholeResult)|</code><br /><br />
 *
 * <ul>
 * <li>The wholeResult may be any integer but 0 &lt;= remainder &lt; denominator (it isn't possible to validate this).</li>
 * <li>If numerator = 0 then remainder = wholeResult = 0.</li>
 * <li>If numerator &lt; denominator then remainder = numerator and wholeResult = 0.</li>
 * <li>If denominator = 0 then remainder = wholeResult = NaN (can't be represented with null)</li>
 * </ul>
 * <br /><br />
 *
 * @param <T> any child class of Number. Although only integers make sense.
 */
public final class IntegerQuotient<T extends Number>
{
   protected T wholeResult;
   protected T remainder;

   /**
    * Note that this constructor is the only way to create this immutable object.
    *
    * @throws NullPointerException if either parameter is null.
    * @see IntegerQuotient
    */
   public IntegerQuotient(T wholeResult, T remainder)
   {
      Objects.requireNonNull(wholeResult);
      Objects.requireNonNull(remainder);
      this.wholeResult = wholeResult;
      this.remainder = remainder;
   }

   /**
    * @return the integer that resulted from the division. It may be zero but not null.
    */
   public T getWholeResult(){return wholeResult;}

   /**
    * @return the integer that remained after the division. It may be zero but not null or negative.
    */
   public T getRemainder(){return remainder;}

   @Override
   public String toString()
   {
      return "whole=" + wholeResult + "; remainder=" + remainder;
   }

}
