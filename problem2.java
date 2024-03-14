import java.util.function.Predicate;

import tester.*;

// Represents an abstract node in a double-ended queue (deque).
abstract class ANode<T> {
  ANode<T> next;
  ANode<T> prev;
  
  // EFFECT: how many items are in the deque
  public abstract int sizeHelper();
  
  // EFFECT: Removes the first node from the deque, returning 
  // the removed node's value
  public abstract T removeHeadHelp(Sentinel<T> header);
  
  // EFFECT: Removes the last node from the deque, returning 
  // the removed node's value
  public abstract T removeTailHelp(Sentinel<T> header);
  
  // EFFECT: tales a Predicate<T> and produces the first node in this Deque for 
  // which the given predicate returns true or returns the header node in this Deque
  // if the predicate is not true for any node
  public abstract ANode<T> findHelp(Sentinel<T> header, Predicate<T> pred);
  
}

// Extends ANode<T> and represents a sentinel node in a deque
class Sentinel<T> extends ANode<T> {

  Sentinel() {
    this.next = this;
    this.prev = this;
  }

  // EFFECT: Removes the first node from the deque, returning an Exception for
  // the Sentinal<T> class
  public T removeHeadHelp(Sentinel<T> header) {
    throw new RuntimeException("Cannot remove from empty list");
  }
  
  // EFFECT: Removes the last node from the deque, returning an Exception for
  // the Sentinal<T> class
  public T removeTailHelp(Sentinel<T> header) {
    throw new RuntimeException("Cannot remove from empty list");
  }

  // EFFECT: tales a Predicate<T> and returns the header node
  // if the predicate is not true for any node
  public ANode<T> findHelp(Sentinel<T> header, Predicate<T> pred) {
    return header;
  }

  // EFFECT: how many items are in the deque
  public int sizeHelper() {
    return 0;
  }
}

// representing a standard node containing data in the deque
class Node<T> extends ANode<T> {
  T data;

  Node(T data) {
    this.next = null;
    this.prev = null;
    this.data = data;
  }

  Node(T data, ANode<T> next, ANode<T> prev) {    
    if (next == null || prev == null) {
      throw new IllegalArgumentException("Neither next or prev can be null");
    }
    else {
      this.next = next;
      this.prev = prev;
      this.data = data;

      this.next.prev = this;
      this.prev.next = this;
    }
  }
  
  // EFFECT: how many items are in the deque
  public int sizeHelper() {
    return 1 + this.next.sizeHelper();
  }

  // EFFECT: Removes the first node from the deque, and returning the node that was removed
  public T removeHeadHelp(Sentinel<T> header) {
    header.next = this.next;
    this.next.prev = header;
    return this.data;
  }
  // EFFECT: Removes the last node from the deque, and returning the node that was removed
  public T removeTailHelp(Sentinel<T> header) {
    header.prev = this.prev;
    this.prev.next = header;
    return this.data;
  }

  // EFFECT: tales a Predicate<T> and produces the first node in this Deque for 
  // which the given predicate returns true or returns the header node in this Deque
  // if the predicate is not true for any node
  public ANode<T> findHelp(Sentinel<T> header, Predicate<T> pred) {
    if (pred.test(this.data)) {
      return this;
    }
    else {
      return this.next.findHelp(header, pred);
    }
  }
  
}

//Implements a double-ended queue using a circularly linked list with a sentinel node
class Deque<T> {
  Sentinel<T> header;

  Deque() {
    this.header = new Sentinel<T>();
  }

  Deque(Sentinel<T> Sentinel) { // maybe wrong
    this.header = Sentinel;
  }

  // Returns how many items are in the deque
  public int size() {
    return this.header.next.sizeHelper();
  }
  
  // EFFECT: Adds the given value to the head of the deque
  public void addAtHead(T val){
    new Node<T>(val, this.header.next, this.header);
  }
 
  // EFFECT: Adds the given value to the tail of the deque
  public void addAtTail(T val){
    new Node<T>(val, this.header.prev, this.header);
  }
  
  // EFFECT: Removes the first node from the deque, returning 
  // the removed value
  public T removeFromHead(){
    return this.header.next.removeHeadHelp(this.header);
  }
  
  // EFFECT: Removes the given value to the head of the deque
  public T removeFromTail(){
    return this.header.prev.removeTailHelp(this.header);
  }
  
  // EFFECT: tales a Predicate<T> and produces the first node in this Deque for 
  // which the given predicate returns true or returns the header node in this Deque
  // if the predicate is not true for any node
  public ANode<T> find(Predicate<T> pred) {
    return this.header.next.findHelp(this.header, pred);
  }
}

// Implements the Predicate<String> interface, providing a test method to check 
// if a string starts with the letter "a".
class StartsWithA implements Predicate<String> {

  public boolean test(String t) {
    return t.substring(0, 1).equals("a");
  }
}

// Implements the Predicate<String> interface, providing a test method to check 
//if a string ends with the letter "y"
class EndsWithY implements Predicate<String> {

  public boolean test(String t) {
    return t.endsWith("y");
  }
}

//Implements the Predicate<Integer> interface, providing a test method to check 
//if an integer is greather than 50
class GreaterThan50 implements Predicate<Integer> {
  public boolean test(Integer t) {
    return t > 50;
  }
}

// Contains tests and examples demonstrating how to use the Deque<T> class.
class ExamplesDeque {
  Deque<String> deque1; // Empty list
  Deque<String> deque1Test1; // Empty list for tests
  Deque<String> deque2; // "abc", "bcd", "cde", and "def"
  Deque<String> deque2Test1; // "abc", "bcd", "cde", and "def"
  Deque<String> deque3; // non-lexicographically 
  Deque<String> deque3Test1; // non-lexicographically 

  Sentinel<String> emptySentinel;
  Sentinel<String> sentinel1Test1;
  ANode<String> hello1;
  
  Sentinel<String> sentinel2;
  ANode<String> abc;
  ANode<String> bcd;
  ANode<String> cde;
  ANode<String> def;

  // Examples for testing add to head
  Sentinel<String> sentinel2Test1;
  ANode<String> abc1;
  ANode<String> bcd1;
  ANode<String> cde1;
  ANode<String> def1;
  ANode<String> zzz1;

  Sentinel<String> sentinel3;
  ANode<String> apple;
  ANode<String> bannana;
  ANode<String> cherry;
  ANode<String> date;
  ANode<String> fig;

  Sentinel<String> sentinel3Test1;
  ANode<String> apple1;
  ANode<String> bannana1;
  ANode<String> cherry1;
  ANode<String> date1;
  ANode<String> fig1;
  ANode<String> kiwi1;

  String hello = "hello";
  String zzz = "zzz";
  String kiwi = "kiwi";
  
  // Integers
  
  Deque<Integer> iDeque1;
  Deque<Integer> iDeque2;  
  Deque<Integer> iDeque1Test1;
  
  Sentinel<Integer> iEmptySentinel;
  ANode<Integer> first;
  
  Sentinel<Integer> iSentinel2;
  ANode<Integer> fir;
  ANode<Integer> sec;
  ANode<Integer> thi;
  ANode<Integer> fort;


  void init() {
    // Empty Deque
    this.deque1 = new Deque<String>();
    this.emptySentinel = new Sentinel<String>();

    this.sentinel1Test1 = new Sentinel<String>();
    this.hello1 = new Node<String>("hello", 
        this.sentinel1Test1, this.sentinel1Test1); 
    this.deque1Test1 = new Deque<String>(this.sentinel1Test1);

    // Example of lexicographically ordered deque
    this.sentinel2 = new Sentinel<String>();
    this.abc = new Node<String>("abc", this.sentinel2, this.sentinel2); 
    this.bcd = new Node<String>("bcd", this.sentinel2, this.abc);
    this.cde = new Node<String>("cde", this.sentinel2, this.bcd);
    this.def = new Node<String>("def", this.sentinel2, this.cde);
    this.deque2 = new Deque<String>(this.sentinel2);

    // Example of lexicographically ordered deque for AddToHead testing
    this.sentinel2Test1 = new Sentinel<String>();
    this.zzz1 = new Node<String>("zzz", this.sentinel2Test1, this.sentinel2Test1);
    this.abc1 = new Node<String>("abc", this.sentinel2Test1, this.zzz1); 
    this. bcd1 = new Node<String>("bcd", this.sentinel2Test1, this.abc1);
    this.cde1 = new Node<String>("cde", this.sentinel2Test1, this.bcd1);
    this.def1 = new Node<String>("def", this.sentinel2Test1, this.cde1);
    this.deque2Test1 = new Deque<String>(this.sentinel2Test1);

    // Example of non-lexicographically ordered deque
    this.sentinel3 = new Sentinel<String>();
    this.bannana = new Node<String>("bannana", this.sentinel3, this.sentinel3);
    this.cherry = new Node<String>("cherry", this.sentinel3, this.bannana);
    this.date = new Node<String>("date", this.sentinel3, this.cherry);
    this.apple = new Node<String>("apple", this.sentinel3, this.date);
    this.fig = new Node<String>("fig", this.sentinel3, this.apple);
    this.deque3 = new Deque<String>(this.sentinel3);

    // Example of non-lexicographically ordered deque for AddToHead testing
    this.sentinel3Test1 = new Sentinel<String>();
    this.kiwi1 = new Node<String>("kiwi", sentinel3Test1, this.sentinel3Test1);
    this.bannana1 = new Node<String>("bannana", this.sentinel3Test1, this.kiwi1);
    this.cherry1 = new Node<String>("cherry", this.sentinel3Test1, this.bannana1);
    this.date1 = new Node<String>("date", this.sentinel3Test1, this.cherry1);
    this.apple1 = new Node<String>("apple", this.sentinel3Test1, this.date1);
    this.fig1 = new Node<String>("fig", this.sentinel3Test1, this.apple1);
    this.deque3Test1 = new Deque<String>(this.sentinel3Test1);
    
    //INTEGERS EXAMPLES
    
    this.iDeque1 = new Deque<Integer>();
    this.iEmptySentinel = new Sentinel<Integer>();
    
    this.first = new Node<Integer>(123, 
        this.iEmptySentinel, this.iEmptySentinel); 
    this.iDeque1Test1 = new Deque<Integer>(this.iEmptySentinel); 
    
    // Example of lexicographically ordered deque
    this.iSentinel2 = new Sentinel<Integer>();
    this.fir = new Node<Integer>(12, this.iSentinel2, this.iSentinel2); 
    this.sec = new Node<Integer>(34, this.iSentinel2, this.fir);
    this.thi = new Node<Integer>(56, this.iSentinel2, this.sec);
    this.fort = new Node<Integer>(78, this.iSentinel2, this.thi);
    this.iDeque2 = new Deque<Integer>(this.iSentinel2);
    
  }

  // testing the sizeHelper method of the ANode<T> class
  void testSizeHelper(Tester t) {
    this.init();
    
    t.checkExpect(this.emptySentinel.sizeHelper(), 0);
    t.checkExpect(this.iSentinel2.sizeHelper(), 0);
    
  }
  
  // testing the removeHeadHelper method of the ANode<T> class
  void removeHeadHelper(Tester t) {
    this.init();
    
  }
  
  // testing the removeTailHelper method of the ANode<T> class
  void removeTailHelper(Tester t) {
    this.init();
    
  }
  
  // testing the findHelper method of the ANode<T> class
  void findHelper(Tester t) {
    this.init();
    
  }
  
  // testing the size method of the Deque<T> class
  void testSize(Tester t) {
    this.init();

    t.checkExpect(this.deque1.size(), 0);
    t.checkExpect(this.deque2.size(), 4);
    t.checkExpect(this.deque3.size(), 5);
    
    this.deque2.addAtHead(this.zzz);
    t.checkExpect(this.deque2.size(), 5);
    t.checkExpect(this.deque2Test1.size(), 5);
  }

  // testing the addToHead method of the Deque<T> class
  void testAddToHead(Tester t) {
    this.init();
    
    this.deque1.addAtHead(hello);
    t.checkExpect(this.deque1, this.deque1Test1);
    
    this.deque2.addAtHead(this.zzz);
    t.checkExpect(this.deque2, this.deque2Test1);
    
    this.deque3.addAtHead(this.kiwi);
    t.checkExpect(this.deque3, this.deque3Test1);
  }
  
  // testing the addToTail method of the Deque<T> class
  void testAddToTail(Tester t) {
    this.init();
    
    this.deque1.addAtTail(hello);
    t.checkExpect(this.deque1, this.deque1Test1);
    
  }
  
  // testing the removeFromHead of the Deque<T> class
  void testRemoveFromHead(Tester t) {
    this.init();
    
    t.checkExpect(this.deque1Test1.removeFromHead(), "hello");
    
    t.checkExpect(this.deque1, this.deque1Test1);
    
  }
  
  // testing the removeFromTail method of the Deque<T> class
  void testRemoveFromTail(Tester t) {
    this.init();
    
    t.checkExpect(this.deque1Test1.removeFromTail(), "hello");
    t.checkExpect(this.deque1, this.deque1Test1);
    
  }
  
  // testing the find method of the Deque<T> class
  void testFind(Tester t) {
    this.init();
    
    //StartsWithA
    t.checkExpect(this.deque1.find(new StartsWithA()), this.emptySentinel);
    t.checkExpect(this.deque1Test1.find(new StartsWithA()), 
        this.sentinel1Test1);  
    
    t.checkExpect(this.deque2.find(new StartsWithA()), this.abc);
    t.checkExpect(this.deque3.find(new StartsWithA()), this.apple);
    
    //EndWithY
    t.checkExpect(this.deque1.find(new EndsWithY()), this.emptySentinel);
    t.checkExpect(this.deque1Test1.find(new EndsWithY()), 
        this.sentinel1Test1);  
    
    t.checkExpect(this.deque3.find(new EndsWithY()), this.cherry);
    
    //GreatherThan50
    t.checkExpect(this.iDeque1.find(new GreaterThan50()), this.iEmptySentinel);
    t.checkExpect(this.iDeque1Test1.find(new GreaterThan50()), 
        this.first); 
    
    t.checkExpect(this.iDeque2.find(new GreaterThan50()), this.thi);
   
  }
}



