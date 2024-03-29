package com.github.skySpiral7.java.infinite.dataStructures;

import com.github.skySpiral7.java.dataStructures.LinkedList;
import com.github.skySpiral7.java.exception.ListIndexOutOfBoundsException;
import com.github.skySpiral7.java.infinite.numbers.InfiniteInteger;
import com.github.skySpiral7.java.iterators.DequeNodeIterator;
import com.github.skySpiral7.java.iterators.DescendingListIterator;
import com.github.skySpiral7.java.pojo.Comparison;
import com.github.skySpiral7.java.pojo.DequeNode;
import com.github.skySpiral7.java.staticSerialization.ObjectStreamReader;
import com.github.skySpiral7.java.staticSerialization.ObjectStreamWriter;
import com.github.skySpiral7.java.staticSerialization.StaticSerializable;
import com.github.skySpiral7.java.util.ComparableSugar;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;

import static com.github.skySpiral7.java.pojo.Comparison.GREATER_THAN;
import static com.github.skySpiral7.java.pojo.Comparison.GREATER_THAN_OR_EQUAL_TO;
import static com.github.skySpiral7.java.pojo.Comparison.LESS_THAN;
import static com.github.skySpiral7.java.pojo.Comparison.LESS_THAN_OR_EQUAL_TO;
import static com.github.skySpiral7.java.util.ComparableSugar.is;
import static com.github.skySpiral7.java.util.ComparableSugar.isComparisonResult;

/**
 * This class is a list without a maximum size unlike ArrayList which has a maximum of approximately 2^31 elements.
 * This data structure requires InfiniteInteger but should otherwise be efficient (as much as it can be which isn't much).
 *
 * @param <E> the data type to be stored
 */
public class InfinitelyLinkedList<E> extends LinkedList<E> implements StaticSerializable
{
   protected InfiniteInteger actualSize;

   public InfinitelyLinkedList()
   {
      size = -1;
      actualSize = InfiniteInteger.ZERO;
   }

   public InfinitelyLinkedList(Collection<? extends E> initialElements)
   {
      this();
      addAll(initialElements);
   }

   public InfinitelyLinkedList(E[] initialElements){this(Arrays.asList(initialElements));}

   @Override
   public boolean offerFirst(E newElement)
   {
      insertNodeAfter(null, newElement);
      return true;
   }

   @Override
   public boolean offerLast(E newElement)
   {
      insertNodeAfter(last, newElement);  //if(this.isEmpty()) then it will insert first
      return true;
   }

   @Override
   protected void insertNodeAfter(DequeNode<E> prev, E data)
   {
      super.insertNodeAfter(prev, data);
      size--;  //undo size++ but it's ok if modCount overflows
      actualSize = actualSize.add(1);
   }

   @Override
   protected E removeNode(DequeNode<E> nodeToRemove)
   {
      E returnValue = super.removeNode(nodeToRemove);
      size++;  //undo size-- but it's ok if modCount overflows
      actualSize = actualSize.subtract(1);
      return returnValue;
   }

   public ListIterator<E> listIterator(InfiniteInteger startingIndex)
   {
      rangeCheckForGet(startingIndex);
      ListIterator<E> returnValue = new DequeNodeIterator.IndexAgnosticValueIterator<E>(getNode(startingIndex));
      return returnValue;
   }

   public InfiniteInteger getActualSize()
   {
      return actualSize;
   }

   @Override
   public int size()
   {
      if (isComparisonResult(actualSize.compareTo(Integer.MAX_VALUE), GREATER_THAN)) return Integer.MAX_VALUE;
      return actualSize.intValue();
   }

   @Override
   protected void rangeCheckForAdd(int index)
   {
      rangeCheckForAdd(InfiniteInteger.valueOf(index));
   }

   @Override
   protected void rangeCheckForGet(int index)
   {
      rangeCheckForGet(InfiniteInteger.valueOf(index));
   }

   protected void rangeCheckForAdd(InfiniteInteger index)
   {
      if (actualSize.equals(index)) return;
      rangeCheckForGet(index);
   }

   protected void rangeCheckForGet(InfiniteInteger index)
   {
      if (is(index, GREATER_THAN_OR_EQUAL_TO, actualSize))
         throw new ListIndexOutOfBoundsException("Index: " + index + ", Size: " + actualSize);
   }

   @Override
   public boolean add(E newElement)
   {
      insertNodeAfter(last, newElement);
      return true;
   }

   @Override
   public void add(int insertionIndex, E newElement)
   {
      insertNodeAfter(getNode(insertionIndex), newElement);
   }

   public void add(InfiniteInteger insertionIndex, E newElement)
   {
      insertNodeAfter(getNode(insertionIndex), newElement);
   }

   @Override
   public boolean addAll(int insertionIndex, Collection<? extends E> newElements)
   {
      return addAll(InfiniteInteger.valueOf(insertionIndex), newElements);
   }

   public boolean addAll(InfiniteInteger insertionIndex, Collection<? extends E> newElements)
   {
      rangeCheckForAdd(insertionIndex);
      boolean modified = false;
      Iterator<? extends E> newElementsIterator = newElements.iterator();
      DequeNode<E> insertAfterThisNode = getNode(insertionIndex);
      while (newElementsIterator.hasNext())
      {
         insertNodeAfter(insertAfterThisNode, newElementsIterator.next());
         modified = true;
      }
      return modified;
   }

   @Override
   public boolean addAll(Collection<? extends E> newElementCollection)
   {
      boolean modified = false;
      for (E newElement : newElementCollection)
      {
         if (add(newElement)) modified = true;
      }
      return modified;
   }

   public E get(InfiniteInteger index)
   {
      rangeCheckForGet(index);
      return getNode(index).getData();
   }

   //super.clear will work fine since it calls isEmpty and removeNode

   /**
    * Same as contains except the search starts from the end.
    *
    * @see #contains(Object)
    */
   public boolean containsFromEnd(Object objectToFind)
   {
      Iterator<E> it = descendingIterator();
      if (objectToFind == null)
      {
         while (it.hasNext())
         {
            if (it.next() == null) return true;
         }
      }
      else
      {
         while (it.hasNext())
         {
            if (objectToFind.equals(it.next())) return true;
         }
      }
      return false;
   }

   @Override
   public boolean removeFirstOccurrence(Object elementToRemove)
   {
      Iterator<DequeNode<E>> it = new DequeNodeIterator.IndexAgnosticDequeIterator<E>(first);
      if (elementToRemove == null)
      {
         while (it.hasNext())
         {
            DequeNode<E> thisNode = it.next();
            if (thisNode.getData() == null)
            {
               removeNode(thisNode);
               return true;
            }
         }
      }
      else
      {
         while (it.hasNext())
         {
            DequeNode<E> thisNode = it.next();
            if (elementToRemove.equals(thisNode.getData()))
            {
               removeNode(thisNode);
               return true;
            }
         }
      }
      return false;
   }

   @Override
   public boolean removeLastOccurrence(Object elementToRemove)
   {
      Iterator<DequeNode<E>> it = DescendingListIterator.iterateBackwards(new DequeNodeIterator.IndexAgnosticDequeIterator<E>(last));
      if (elementToRemove == null)
      {
         while (it.hasNext())
         {
            DequeNode<E> thisNode = it.next();
            if (thisNode.getData() == null)
            {
               removeNode(thisNode);
               return true;
            }
         }
      }
      else
      {
         while (it.hasNext())
         {
            DequeNode<E> thisNode = it.next();
            if (elementToRemove.equals(thisNode.getData()))
            {
               removeNode(thisNode);
               return true;
            }
         }
      }
      return false;
   }

   @Override
   public Iterator<E> descendingIterator()
   {
      return DescendingListIterator.iterateBackwards(new DequeNodeIterator.IndexAgnosticValueIterator<E>(last));
   }

   @Override
   public int lastIndexOf(Object objectToFind)
   {
      if (isComparisonResult(actualSize.compareTo(Integer.MAX_VALUE), GREATER_THAN))
         throw new IllegalStateException("The list is larger than an Integer can represent.");
      int index = size;
      if (objectToFind == null)
      {
         for (DequeNode<E> currentNode = last; currentNode != null; currentNode = currentNode.getPrev())
         {
            index--;
            if (currentNode.getData() == null) return index;
         }
      }
      else
      {
         for (DequeNode<E> currentNode = last; currentNode != null; currentNode = currentNode.getPrev())
         {
            index--;
            if (objectToFind.equals(currentNode.getData())) return index;
         }
      }
      return ELEMENT_NOT_FOUND;
   }

   public InfiniteInteger lastActualIndexOf(Object objectToFind)
   {
      InfiniteInteger index = actualSize;
      if (objectToFind == null)
      {
         for (DequeNode<E> currentNode = last; currentNode != null; currentNode = currentNode.getPrev())
         {
            index = index.subtract(1);
            if (currentNode.getData() == null) return index;
         }
      }
      else
      {
         for (DequeNode<E> currentNode = last; currentNode != null; currentNode = currentNode.getPrev())
         {
            index = index.subtract(1);
            if (objectToFind.equals(currentNode.getData())) return index;
         }
      }
      return InfiniteInteger.valueOf(ELEMENT_NOT_FOUND);
   }

   public E remove(InfiniteInteger index)
   {
      DequeNode<E> nodeToRemove = getNode(index);
      E returnValue = nodeToRemove.getData();
      removeNode(nodeToRemove);
      return returnValue;
   }

   @Override
   public DequeNode<E> getNode(int index)
   {
      return getNode(InfiniteInteger.valueOf(index));
   }

   public DequeNode<E> getNode(InfiniteInteger index)
   {
      rangeCheckForGet(index);

      if (is(index, LESS_THAN_OR_EQUAL_TO, actualSize.divideByPowerOf2DropRemainder(1)))
      {
         DequeNode<E> currentNode = first;
         for (InfiniteInteger i = InfiniteInteger.ZERO; is(i, LESS_THAN, index); i = i.add(1))
         {currentNode = currentNode.getNext();}
         return currentNode;
      }
      else
      {
         DequeNode<E> currentNode = last;
         for (InfiniteInteger i = actualSize.subtract(1); is(i, GREATER_THAN, index); i = i.subtract(1))
         {currentNode = currentNode.getPrev();}
         return currentNode;
      }
   }

   @Override
   public E set(int index, E newValue)
   {
      return set(InfiniteInteger.valueOf(index), newValue);
   }

   public E set(InfiniteInteger index, E newValue)
   {
      DequeNode<E> nodeToChange = getNode(index);
      E oldValue = nodeToChange.getData();
      nodeToChange.setData(newValue);
      //doesn't increment modCount because there was no structural change
      return oldValue;
   }

   @Override
   public void swap(int indexA, int indexB)
   {
      swap(InfiniteInteger.valueOf(indexA), InfiniteInteger.valueOf(indexB));
   }

   public void swap(InfiniteInteger indexA, InfiniteInteger indexB)
   {
      DequeNode<E> nodeA = getNode(indexA);
      DequeNode<E> nodeB = getNode(indexB);
      E temp = nodeA.getData();
      nodeA.setData(nodeB.getData());
      nodeB.setData(temp);
      //doesn't increment modCount because there was no structural change
   }

   @Override
   public boolean isEmpty()
   {
      return first == null;  //this is faster then checking size
   }

   @Override
   public Object[] toArray()
   {
      if (ComparableSugar.isComparisonResult(actualSize.compareTo(Integer.MAX_VALUE), Comparison.GREATER_THAN))
         throw new IllegalStateException("This list is larger than max array size");
      Object[] result = new Object[actualSize.intValue()];
      int i = 0;
      for (DequeNode<E> cursor = first; cursor != null; cursor = cursor.getNext())
      {
         result[i] = cursor.getData();
         i++;
      }
      return result;
   }

   @Override
   public E[] toArray(Class<E> elementType)
   {
      if (ComparableSugar.isComparisonResult(actualSize.compareTo(Integer.MAX_VALUE), Comparison.GREATER_THAN))
         throw new IllegalStateException("This list is larger than max array size");
      @SuppressWarnings("unchecked") E[] destination = (E[]) java.lang.reflect.Array.newInstance(elementType, actualSize.intValue());
      int i = 0;
      for (DequeNode<E> cursor = first; cursor != null; cursor = cursor.getNext())
      {
         destination[i] = cursor.getData();
         i++;
      }
      return destination;
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T> T[] toArray(T[] destination)
   {
      if (ComparableSugar.isComparisonResult(actualSize.compareTo(Integer.MAX_VALUE), Comparison.GREATER_THAN))
         throw new IllegalStateException("This list is larger than max array size");
      size = actualSize.intValue();
      if (destination.length < size)
         destination = (T[]) java.lang.reflect.Array.newInstance(destination.getClass().getComponentType(), size);
      int i = 0;
      //result exists in order to cause an ArrayStoreException instead of a ClassCastException
      Object[] result = destination;
      for (DequeNode<E> cursor = first; cursor != null; cursor = cursor.getNext())
      {
         result[i] = cursor.getData();
         i++;
      }

      if (destination.length > size) destination[size] = null;

      size = -1;
      return destination;
   }

   @Override
   public LinkedList<E> copy()
   {
      return new InfinitelyLinkedList<E>(this);  //acts as a copy constructor
   }

   public static <T> InfinitelyLinkedList<T> readFromStream(final ObjectStreamReader reader)
   {
      final InfiniteInteger elementCount = reader.readObject(InfiniteInteger.class);
      final InfinitelyLinkedList<T> result = new InfinitelyLinkedList<>();
      for (InfiniteInteger elementIndex = InfiniteInteger.ZERO;
           is(elementIndex, LESS_THAN, elementCount);
           elementIndex = elementIndex.add(1))
      {
         result.add(reader.readObject());
      }
      return result;
   }

   @Override
   public void writeToStream(final ObjectStreamWriter writer)
   {
      writer.writeObject(actualSize);
      for (InfiniteInteger elementIndex = InfiniteInteger.ZERO;
           is(elementIndex, LESS_THAN, this.actualSize);
           elementIndex = elementIndex.add(1))
      {
         writer.writeObject(get(elementIndex));
      }
   }
}
