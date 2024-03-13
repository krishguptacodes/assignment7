abstract class ANode<T> {
  ANode<T> next;
  ANode<T> prev;
  
  ANode() {
    this.next = next;
    this.prev = prev;
  }
}



class Sentinal<T> extends ANode<T> {
  Sentinal() {
    super();
    this.next = this;
    this.prev = this;
  }
}

class Node<T> extends ANode<T> {
  T data;
  Node(T data) {
    super();
    this.next = this;
    this.prev = this;
  }
  
  Node(T data, ANode<T> next, ANode<T> prev) {
    if (next == null|| prev == null) {
      throw new IllegalArgumentException("Neither next or prev can be null");
    }

    this.data = data;
    this.next = next;
    this.prev = prev;
  }
}

class Deque<T> {
  Sentinal<T> header;
  
  Deque() {
    this.header = new Sentinal<T>();
  }
  
  Deque(Sentinal<T> sentinal) { // maybe wrong
    this.header = sentinal;
  }
  
  public int size() {
    return sizeHelper(this.header.next);
  }

  public int sizeHelper(ANode<T> current) {
    if (current == this.header) { 
      return 0;
    } else {
      return 1 + sizeHelper(current.next); 
    }
}
}

class ExamplesDeque {
  Deque<String> deque1; // Empty list
  Deque<String> deque2; // "abc", "bcd", "cde", and "def"
  Deque<String> deque3; // non-lexicographically 

  ExamplesDeque() {
      deque1 = new Deque<>();

      deque2 = new Deque<>();
      new Node<>("abc", deque2.header, deque2.header);  //exception i think
      new Node<>("bcd", deque2.header, deque2.header.prev);
      new Node<>("cde", deque2.header, deque2.header.prev);
      new Node<>("def", deque2.header, deque2.header.prev);

      deque3 = new Deque<>();
      // Example of non-lexicographically ordered list
      new Node<>("apple", deque3.header, deque3.header.prev);
      new Node<>("banana", deque3.header, deque3.header.prev);
      new Node<>("cherry", deque3.header, deque3.header.prev);
      new Node<>("date", deque3.header, deque3.header.prev);
  }
}