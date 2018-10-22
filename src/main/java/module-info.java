module com.github.skySpiral7.java.infinite {
   requires Java;  //Automatic Module: com.github.SkySpiral7:Java
   //From Java: FileIoUtil, ComparableSugar, DequeNode, Copyable, ListIndexOutOfBoundsException, LinkedList
   //and unused: NumberFormatException, NumericOverflowException
   requires com.github.skySpiral7.java.staticSerialization;  //com.github.SkySpiral7:Java.StaticSerialization

   exports com.github.skySpiral7.java.infinite.dataStructures;
   exports com.github.skySpiral7.java.infinite.exeptions;
   exports com.github.skySpiral7.java.infinite.numbers;
   //hides com.github.skySpiral7.java.infinite.util;
}
