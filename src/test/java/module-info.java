open module com.github.skySpiral7.java.infinite {
   //copy of prod require:
   requires com.github.skySpiral7.java;  //Module: com.github.SkySpiral7:Java
   //From Java: FileIoUtil, ComparableSugar, DequeNode, Copyable, ListIndexOutOfBoundsException, LinkedList
   //and unused: NumberFormatException, NumericOverflowException
   requires com.github.skySpiral7.java.staticSerialization;  //com.github.SkySpiral7:Java.StaticSerialization

   //test only:
   requires hamcrest.all;
   //requires org.apache.logging.log4j.core;  //not needed for some reason
}
