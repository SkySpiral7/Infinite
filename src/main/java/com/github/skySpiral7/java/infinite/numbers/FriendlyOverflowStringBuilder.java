package com.github.skySpiral7.java.infinite.numbers;

import com.github.skySpiral7.java.numbers.WillNotFitException;

/**
 * This class is like StringBuilder except that the error message on string overflow is more helpful.
 * StringBuilder throws OutOfMemoryError with no message. This class throws {@link WillNotFitException} with a message.
 * This is only intended for the toString methods of InfiniteRational etc and thus isn't public and is missing functionality.
 */
class FriendlyOverflowStringBuilder
{
   private final String errorMessage;
   private final StringBuilder stringBuilder;

   /**
    * @param errorMessagePrefix not validated therefore too large will throw an unfriendly error.
    * @param initialCapacity    not validated therefore negative will throw an unfriendly error.
    */
   public FriendlyOverflowStringBuilder(final String errorMessagePrefix, final int initialCapacity)
   {
      //errorMessagePrefix is never long enough to need to throw here
      this.errorMessage = errorMessagePrefix + " would exceed max string length.";
      this.stringBuilder = new StringBuilder(initialCapacity);
   }

   /**
    * @param errorMessagePrefix not validated therefore too large will throw an unfriendly error.
    */
   public FriendlyOverflowStringBuilder(final String errorMessagePrefix)
   {
      //errorMessagePrefix is never long enough to need to throw here
      this.errorMessage = errorMessagePrefix + " would exceed max string length.";
      this.stringBuilder = new StringBuilder();
   }

   public FriendlyOverflowStringBuilder append(final String newString)
   {
      if (Integer.MAX_VALUE - stringBuilder.length() - newString.length() < 0)  //overflow conscious
         throw new WillNotFitException(errorMessage);
      stringBuilder.append(newString);
      return this;
   }

   public FriendlyOverflowStringBuilder append(final char[] newChars)
   {
      if (Integer.MAX_VALUE - stringBuilder.length() - newChars.length < 0)  //overflow conscious
         throw new WillNotFitException(errorMessage);
      stringBuilder.append(newChars);
      return this;
   }

   public FriendlyOverflowStringBuilder append(final char newChar)
   {
      if (Integer.MAX_VALUE == stringBuilder.length()) throw new WillNotFitException(errorMessage);
      stringBuilder.append(newChar);
      return this;
   }

   public int length(){return stringBuilder.length();}

   @Override
   public String toString()
   {
      return stringBuilder.toString();
   }
}
