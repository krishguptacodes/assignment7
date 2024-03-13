import tester.*;

abstract class ANode<T> {
  ANode<T> next;
  ANode<T> prev;

}

class Sentinel<T> extends ANode<T> {

  Sentinel() {
    this.next = this;
    this.prev = this;
  }

}

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
}

class Deque<T> {
  Sentinel<T> header;

  Deque() {
    this.header = new Sentinel<T>();
  }

  Deque(Sentinel<T> Sentinel) { // maybe wrong
    this.header = Sentinel;
  }

  public int size() {
    return sizeHelper(this.header);
  }

  public int sizeHelper(ANode<T> current) {
    if (current.next == this.header) { 
      return 0;
    } 
    else {
      return 1 + sizeHelper(current.next); 
    }
  }

  public void addAtHead(T val){
    this.addAtHeadHelp(this.header, val);
  }

  public void addAtHeadHelp(Sentinel<T> oldHead, T val) {
    new Node<T>(val, oldHead, oldHead.next);
  }

}

class ExamplesDeque {
  Deque<String> deque1; // Empty list
  Deque<String> deque2; // "abc", "bcd", "cde", and "def"
  Deque<String> deque2Test1; // "abc", "bcd", "cde", and "def"
  Deque<String> deque3; // non-lexicographically 
  Deque<String> deque3Test1; // non-lexicographically 

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
  
  String zzz = "zzz";
  String kiwi = "kiwi";
  

  void init() {
    // Empty Deque
    deque1 = new Deque<String>();

    // Example of lexicographically ordered deque
    sentinel2 = new Sentinel<String>();
    abc = new Node<String>("abc", this.sentinel2, sentinel2); 
    bcd = new Node<String>("bcd", this.abc, sentinel2);
    cde = new Node<String>("cde", bcd, sentinel2);
    def = new Node<String>("def", cde, sentinel2);
    deque2 = new Deque<String>(this.sentinel2);
    
    // Example of lexicographically ordered deque for AddToHead testing
    sentinel2Test1 = new Sentinel<String>();
    zzz1 = new Node<String>("zzz", sentinel2, sentinel2);
    abc1 = new Node<String>("abc", zzz1, sentinel2); 
    bcd1 = new Node<String>("bcd", abc, sentinel2);
    cde1 = new Node<String>("cde", bcd, sentinel2);
    def1 = new Node<String>("def", cde, sentinel2);
    deque2Test1 = new Deque<String>(this.sentinel2);

    // Example of non-lexicographically ordered deque
    sentinel3 = new Sentinel<String>();
    bannana = new Node<String>("bannana", sentinel3, sentinel3);
    cherry = new Node<String>("cherry", bannana, sentinel3);
    date = new Node<String>("date", cherry, sentinel3);
    apple = new Node<String>("apple", date, sentinel3);
    fig = new Node<String>("fig", apple, sentinel3);
    deque3 = new Deque<String>(sentinel3);
    
 // Example of non-lexicographically ordered deque for AddToHead testing
    sentinel3Test1 = new Sentinel<String>();
    kiwi1 = new Node<String>("kiwi", sentinel3, sentinel3);
    bannana1 = new Node<String>("bannana", kiwi1, sentinel3);
    cherry1 = new Node<String>("cherry", bannana, sentinel3);
    date1 = new Node<String>("date", cherry, sentinel3);
    apple1 = new Node<String>("apple", date, sentinel3);
    fig1 = new Node<String>("fig", apple, sentinel3);
    deque3Test1 = new Deque<String>(sentinel3);
    
  }

  void testSize(Tester t) {
    this.init();

    t.checkExpect(deque1.size(), 0);
    t.checkExpect(deque2.size(), 4);
    t.checkExpect(deque3.size(), 5);
  }
  
  void testAddToHead(Tester t) {
    this.init();
    
    deque2.addAtHead(zzz);
    t.checkExpect(deque2, deque2Test1);
  }


}