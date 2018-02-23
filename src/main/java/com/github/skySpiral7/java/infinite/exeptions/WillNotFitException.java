package com.github.skySpiral7.java.infinite.exeptions;

import java.util.Collection;

import com.github.skySpiral7.java.numbers.NumericOverflowException;

/**
 * This exception is thrown when a class can't contain the intended contents. Examples include all {@link Collection}s,
 * arrays, and {@link String}. This can be thought of as a general OverflowException compared to {@link NumericOverflowException}.
 */
public class WillNotFitException extends RuntimeException
{
   private static final long serialVersionUID = 1L;

   /**
    * Constructs a <code>WillNotFitException</code> with no detail message.
    */
   public WillNotFitException()
   {
      super();
   }

   /**
    * Constructs a <code>NumberFormatException</code> with the
    * specified detail message.
    */
   public WillNotFitException(final String detailMessage)
   {
      super(detailMessage);
   }
}
