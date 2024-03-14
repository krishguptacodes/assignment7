import java.util.function.Predicate;

import tester.*;

// Represents an abstract class with a next and prev ANode
abstract class ANode<T> {
  ANode<T> next;
  ANode<T> prev;

  // returns the number of items in the deque
  abstract int sizeHelper();

  // EFFECT: Removes the first node from the deque, returning 
  // the removed node's value
  abstract T removeHeadHelp(Sentinel<T> header);

  // EFFECT: Removes the last node from the deque, returning 
  // the removed node's value
  abstract T removeTailHelp(Sentinel<T> header);

  // produces the first node in this Deque for which 
  // the given predicate returns true, returns header if false
  abstract ANode<T> findHelp(Sentinel<T> header, Predicate<T> pred);

  // Updates the given node's previous node to the given value
  public void updatePrev(ANode<T> n) {
    this.prev = n;
  }

  //Updates the given node's next node to the given value
  public void updateNext(ANode<T> n) {
    this.next = n;
  }

}

// Represents a sentinel at the front/end of a list
class Sentinel<T> extends ANode<T> {

  Sentinel() {
    this.next = this;
    this.prev = this;
  }

  public T removeHeadHelp(Sentinel<T> header) {
    throw new RuntimeException("Cannot remove from empty deque");
  }

  public T removeTailHelp(Sentinel<T> header) {
    throw new RuntimeException("Cannot remove from empty deque");
  }

  // produces the first node in this Deque for which 
  // the given predicate returns true, returns header if false
  ANode<T> findHelp(Sentinel<T> header, Predicate<T> pred) {
    return header;
  }


  int sizeHelper() {
    return 0;
  }

}

// Represents a node in a deque with a prev and next ANode
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

      this.next.updatePrev(this);
      this.prev.updateNext(this);
    }
  }


  // returns the number of items in the deque
  int sizeHelper() {
    return 1 + this.next.sizeHelper();
  }

  // removes the first node in the deque
  public T removeHeadHelp(Sentinel<T> header) {
    header.next = this.next;
    this.next.prev = header;
    return this.data;
  }

  // removes the last node in the deque
  public T removeTailHelp(Sentinel<T> header) {
    header.prev = this.prev;
    this.prev.next = header;
    return this.data;
  }

  // produces the first node in this Deque for which 
  // the given predicate returns true, returns header if false
  public ANode<T> findHelp(Sentinel<T> header, Predicate<T> pred) {
    if (pred.test(this.data)) {
      return this;
    }
    else {
      return this.next.findHelp(header, pred);
    }
  }

}

// Represents a double-ended queue
class Deque<T> {
  Sentinel<T> header;

  Deque() {
    this.header = new Sentinel<T>();
  }

  Deque(Sentinel<T> sentinel) { // maybe wrong
    this.header = sentinel;
  }

  // Returns how many items are in the deque
  public int size() {
    return this.header.next.sizeHelper();
  }

  // EFFECT: Adds the given value to the head of the deque
  public void addAtHead(T val) {
    new Node<T>(val, this.header.next, this.header);
  }

  // EFFECT: Adds the given value to the tail of the deque
  public void addAtTail(T val) {
    new Node<T>(val, this.header, this.header.prev);
  }

  // Removes the first node from the deque, returning 
  // the removed value
  public T removeFromHead() {
    return this.header.next.removeHeadHelp(this.header);
  }

  // Removes the given value to the head of the deque
  public T removeFromTail() {
    return this.header.prev.removeTailHelp(this.header);
  }

  // produces the first node in this Deque for which 
  // the given predicate returns true
  public ANode<T> find(Predicate<T> pred) {
    return this.header.next.findHelp(this.header, pred);
  }
}

// Function object for testing whether the string starts with 
// the character a. Returns true or false. 
class StartsWithA implements Predicate<String> {

  public boolean test(String t) {
    return t.substring(0, 1).equals("a");
  }
}

//Implements the Predicate<String> interface, providing a test method to check 
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

class ExamplesDeque {
  Deque<String> deque1; // Empty list
  Deque<String> deque1Test1; // Empty list for tests
  Deque<String> deque2; // "abc", "bcd", "cde", and "def"
  Deque<String> deque2Test1; // "abc", "bcd", "cde", and "def"
  Deque<String> deque2Test2; // "abc", "bcd", "cde", and "def"
  Deque<String> deque3; // non-lexicographically 
  Deque<String> deque3Test1; // non-lexicographically 
  Deque<String> deque3Test2; // non-lexicographically 

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

  // Examples for testing add to tail
  Sentinel<String> sentinel2Test2;
  ANode<String> abc2;
  ANode<String> bcd2;
  ANode<String> cde2;
  ANode<String> def2;
  ANode<String> zzz2;

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

  Sentinel<String> sentinel3Test2;
  ANode<String> apple2;
  ANode<String> bannana2;
  ANode<String> cherry2;
  ANode<String> date2;
  ANode<String> fig2;
  ANode<String> kiwi2;

  String hello = "hello";
  String zzz = "zzz";
  String kiwi = "kiwi";

  Deque<Integer> intDeque1;
  Deque<Integer> intDeque2;  
  Deque<Integer> intDeque2Test1;  

  Sentinel<Integer> intEmptySentinel;

  Sentinel<Integer> intSentinel2;
  ANode<Integer> one;
  ANode<Integer> two;
  ANode<Integer> three;
  ANode<Integer> four;

  Sentinel<Integer> intSentinel2Test1;
  ANode<Integer> one1;
  ANode<Integer> two1;
  ANode<Integer> three1;
  ANode<Integer> four1;
  ANode<Integer> five1;

  // initialize conditions
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

    // Example of lexicographically ordered deque
    this.sentinel2Test2 = new Sentinel<String>();
    this.abc2 = new Node<String>("abc", this.sentinel2Test2, this.sentinel2Test2); 
    this.bcd2 = new Node<String>("bcd", this.sentinel2Test2, this.abc2);
    this.cde2 = new Node<String>("cde", this.sentinel2Test2, this.bcd2);
    this.def2 = new Node<String>("def", this.sentinel2Test2, this.cde2);
    this.zzz2 = new Node<String>("zzz", this.sentinel2Test2, this.def2);
    this.deque2Test2 = new Deque<String>(this.sentinel2Test2);

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

    // Example of non-lexicographically ordered deque for AddToHead testing
    this.sentinel3Test2 = new Sentinel<String>();
    this.bannana2 = new Node<String>("bannana", 
        this.sentinel3Test2, this.sentinel3Test2);
    this.cherry2 = new Node<String>("cherry", this.sentinel3Test2, this.bannana2);
    this.date2 = new Node<String>("date", this.sentinel3Test2, this.cherry2);
    this.apple2 = new Node<String>("apple", this.sentinel3Test2, this.date2);
    this.fig2 = new Node<String>("fig", this.sentinel3Test2, this.apple2);
    this.kiwi2 = new Node<String>("kiwi", sentinel3Test2, this.fig2);
    this.deque3Test2 = new Deque<String>(this.sentinel3Test2);

    //
    this.intDeque1 = new Deque<Integer>();
    this.intEmptySentinel = new Sentinel<Integer>();

    // Example of an ordered deque with integers
    this.intSentinel2 = new Sentinel<Integer>();
    this.one = new Node<Integer>(12, this.intSentinel2, this.intSentinel2); 
    this.two = new Node<Integer>(34, this.intSentinel2, this.one);
    this.three = new Node<Integer>(56, this.intSentinel2, this.two);
    this.four = new Node<Integer>(78, this.intSentinel2, this.three);
    this.intDeque2 = new Deque<Integer>(this.intSentinel2);

    this.intSentinel2Test1 = new Sentinel<Integer>();
    this.five1 = new Node<Integer>(19, 
        this.intSentinel2Test1, this.intSentinel2Test1);
    this.one1 = new Node<Integer>(12, this.intSentinel2Test1, this.five1); 
    this.two1 = new Node<Integer>(34, this.intSentinel2Test1, this.one1);
    this.three1 = new Node<Integer>(56, this.intSentinel2Test1, this.two1);
    this.four1 = new Node<Integer>(78, this.intSentinel2Test1, this.three1);
    this.intDeque2Test1 = new Deque<Integer>(this.intSentinel2Test1);
  }

  // Testing exceptions
  void testExceptions(Tester t) {
    // test constructing a node with a null for prev or next
    t.checkConstructorException(new IllegalArgumentException(""
        + "Neither next or prev can be null"), "Node", "abc", null, this.abc1);
    t.checkConstructorException(new IllegalArgumentException(""
        + "Neither next or prev can be null"), "Node", "abc", null, null);

    this.init();

    // test removing head or tail from an empty deque
    t.checkException(new RuntimeException("Cannot remove from empty deque"), 
        this.deque1, "removeFromHead");   
    t.checkException(new RuntimeException("Cannot remove from empty deque"), 
        this.deque1, "removeFromTail");

  }

  // test for size of a deque
  void testSize(Tester t) {
    this.init();
    // empty case
    t.checkExpect(this.deque1.size(), 0);
    // non-empty deque cases
    t.checkExpect(this.deque2.size(), 4);
    t.checkExpect(this.deque3.size(), 5);

    this.deque2.addAtHead(this.zzz);
    t.checkExpect(this.deque2.size(), 5);
    t.checkExpect(this.deque2Test1.size(), 5);
  }

  // test helper for size of a deque
  void testSizeHelper(Tester t) {
    this.init();
    // empty case
    t.checkExpect(this.emptySentinel.sizeHelper(), 0);
    // non-empty deque cases
    t.checkExpect(this.abc.sizeHelper(), 4);
    t.checkExpect(this.abc2.sizeHelper(), 5);
    t.checkExpect(this.bannana.sizeHelper(), 5);
    t.checkExpect(this.bannana2.sizeHelper(), 6);
    t.checkExpect(this.one.sizeHelper(), 4);
  }

  // tests for adding node to front of deque
  void testAddToHead(Tester t) {
    this.init();

    // modify by adding at head
    this.deque1.addAtHead(hello);
    // Test mutations
    t.checkExpect(this.deque1, this.deque1Test1);

    // modify by adding at head
    this.deque2.addAtHead(this.zzz);
    // Test mutations
    t.checkExpect(this.deque2, this.deque2Test1);

    // modify by adding at head
    this.deque3.addAtHead(this.kiwi);
    // Test mutations
    t.checkExpect(this.deque3, this.deque3Test1);

    // modify by adding at head
    this.intDeque2.addAtHead(19);
    // Test mutations
    t.checkExpect(this.intDeque2, this.intDeque2Test1);
  }

  // tests for adding node to tail of deque
  void testAddToTail(Tester t) {
    this.init();

    // mutate deque then test if added Node is at tail
    this.deque1.addAtTail(hello);
    t.checkExpect(this.deque1, this.deque1Test1);

    // mutate deque then test if added Node is at tail
    this.deque2.addAtTail(this.zzz);
    t.checkExpect(this.deque2, this.deque2Test2);

    // mutate deque then test if added Node is at tail
    this.deque3.addAtTail(this.kiwi);
    t.checkExpect(this.deque3, this.deque3Test2);    

  }

  // tests for removing node from head of deque
  void testRemoveFromHead(Tester t) {
    this.init();

    // mutate deque then test if head Node is removed
    t.checkExpect(this.deque1Test1.removeFromHead(), "hello");
    t.checkExpect(this.deque1, this.deque1Test1);

    // mutate deque then test if head Node is removed
    t.checkExpect(this.deque2Test1.removeFromHead(), "zzz");
    t.checkExpect(this.deque2, this.deque2Test1);

    // mutate deque then test if head Node is removed
    t.checkExpect(this.deque3Test1.removeFromHead(), "kiwi");
    t.checkExpect(this.deque3, this.deque3Test1);

  }

  // tests helper for removing node to head of deque
  void testRemoveFromHeadHelp(Tester t) {
    this.init();

    // mutate deque then test if head Node is removed
    t.checkExpect(this.hello1
        .removeHeadHelp(sentinel1Test1), "hello");
    t.checkExpect(this.deque1, this.deque1Test1);

    // mutate deque then test if head Node is removed
    t.checkExpect(this.zzz1.removeHeadHelp(sentinel2Test1), "zzz");
    t.checkExpect(this.deque2, this.deque2Test1);

    // mutate deque then test if head Node is removed
    t.checkExpect(this.kiwi1.removeHeadHelp(sentinel3Test1), "kiwi");
    t.checkExpect(this.deque3, this.deque3Test1);

  }

  // tests for removing node from tail of deque
  void testRemoveFromTail(Tester t) {
    this.init();

    // mutate deque then test if tail Node is removed
    t.checkExpect(this.deque1Test1.removeFromTail(), "hello");
    t.checkExpect(this.deque1, this.deque1Test1);

    // mutate deque then test if tail Node is removed
    t.checkExpect(this.deque2Test2.removeFromTail(), "zzz");
    t.checkExpect(this.deque2, this.deque2Test2);

    // mutate deque then test if tail Node is removed
    t.checkExpect(this.deque3Test2.removeFromTail(), "kiwi");
    t.checkExpect(this.deque3, this.deque3Test2);
  }

  // tests helper for removing node from tail of deque
  void testRemoveTailHelp(Tester t) {
    this.init();

    // mutate deque then test if tail Node is removed
    t.checkExpect(this.hello1.removeTailHelp(sentinel1Test1), "hello");
    t.checkExpect(this.deque1, this.deque1Test1);

    // mutate deque then test if tail Node is removed
    t.checkExpect(this.zzz2.removeTailHelp(sentinel2Test2), "zzz");
    t.checkExpect(this.deque2, this.deque2Test2);

    // mutate deque then test if tail Node is removed
    t.checkExpect(this.kiwi2.removeTailHelp(sentinel3Test2), "kiwi");
    t.checkExpect(this.deque3, this.deque3Test2);
  }

  // test find method
  void testFind(Tester t) {
    this.init();

    // Test find on empty deque
    t.checkExpect(this.deque1.find(new StartsWithA()), this.emptySentinel);
    // Test find on non-empty deques
    t.checkExpect(this.deque1Test1.find(new StartsWithA()), 
        this.sentinel1Test1);  
    t.checkExpect(this.deque2.find(new StartsWithA()), this.abc);
    t.checkExpect(this.deque2Test1.find(new StartsWithA()), this.abc1);
    t.checkExpect(this.deque3.find(new StartsWithA()), this.apple);

    //EndWithY
    t.checkExpect(this.deque1.find(new EndsWithY()), this.emptySentinel);
    t.checkExpect(this.deque1Test1.find(new EndsWithY()), 
        this.sentinel1Test1);  

    t.checkExpect(this.deque3.find(new EndsWithY()), this.cherry);

    //GreatherThan50
    t.checkExpect(this.intDeque1.find(new GreaterThan50()), this.intEmptySentinel);

    t.checkExpect(this.intDeque2.find(new GreaterThan50()), this.three);
  }

  // test find method
  void testFindHelp(Tester t) {
    this.init();

    // Test find on empty deque
    t.checkExpect(this.emptySentinel.findHelp(emptySentinel, 
        new StartsWithA()), this.emptySentinel);
    // Test find on non-empty deques
    t.checkExpect(this.hello1.findHelp(this.sentinel1Test1, new StartsWithA()), 
        this.sentinel1Test1);  
    t.checkExpect(this.abc.findHelp(this.sentinel2, new StartsWithA()), this.abc);
    t.checkExpect(this.zzz1.findHelp(this.sentinel2Test1, 
        new StartsWithA()), this.abc1);
    t.checkExpect(this.bannana
        .findHelp(this.sentinel3, new StartsWithA()), this.apple);

    //EndWithY
    t.checkExpect(this.emptySentinel
        .findHelp(emptySentinel, new EndsWithY()), this.emptySentinel);
    t.checkExpect(this.hello1.findHelp(this.sentinel1Test1, new EndsWithY()), 
        this.sentinel1Test1);  

    t.checkExpect(this.bannana
        .findHelp(this.sentinel3, new EndsWithY()), this.cherry);

    //GreatherThan50
    t.checkExpect(this.intEmptySentinel.findHelp(this.intEmptySentinel, 
        new GreaterThan50()), this.intEmptySentinel);

    t.checkExpect(this.one
        .findHelp(this.intSentinel2, new GreaterThan50()), this.three);
  }

}



















