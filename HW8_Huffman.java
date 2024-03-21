import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import tester.*;

// Used to represent Huffman coding
class Huffman {
  ABT tree;
  ArrayList<String> letters;

  // Main constructor
  Huffman(ArrayList<String> letters, ArrayList<Integer> frequency) {
    // Throw error if lists are of different lengths
    if (letters.size() != frequency.size()) {
      throw new IllegalArgumentException("Error: lists are of different lengths");
    }
    // Throw error if list is too short
    else if (letters.size() < 2) {
      throw new IllegalArgumentException("Error: length of the list of strings is less than 2");
    }
    else {
      // initialize fields
      ArrayList<ABT> leafList = new ArrayList<ABT>();
      this.letters = letters;
      // Combine letters and frequencies into leafs and add to list
      for (int i = 0; i < letters.size(); i = i + 1) {
        leafList.add(new Leaf(letters.get(i), frequency.get(i)));
      }
      // Sort the list of leafs
      leafList.sort(new leafComparator());
      // Create tree from ArrayList of leafs
      new Utils().createTree(leafList);
      // Initialize tree field as the created tree from the ArrayList
      tree = leafList.get(0);
    }

  }

  // Constructor for testing
  Huffman(ArrayList<String> letters, ABT tree) {
    this.letters = letters;
    this.tree = tree;
  }

  ArrayList<Boolean> encode(String str) {
    ArrayList<Boolean> result = new ArrayList<Boolean>();
    
    for (int i = 0; i < str.length(); i++) {
      result = this.tree.encodeHelp(str.substring(i, i + 1), result);
    }
    
    return result;
  }
  
  String decode(ArrayList<Boolean> encodedList) {
    String result = "";
    while (encodedList.size() > 0) {
      result = this.tree.decodeHelp(encodedList, result);
    }
    return result;
  }
}

abstract class ABT {
  int total;

  ABT(int total) {
    this.total = total;
  }

  public abstract String decodeHelp(ArrayList<Boolean> encodedList, String acc);

  public abstract ArrayList<Boolean> encodeHelp(String str, ArrayList<Boolean> acc);

  public abstract boolean hasLetter(String letter);
  
}

class Node extends ABT{
  ABT left;
  ABT right;

  Node(ABT left, ABT right) {
    super(left.total + right.total);
    this.left = left;
    this.right = right;
  }
  
  public ArrayList<Boolean> encodeHelp(String letter, ArrayList<Boolean> acc) {
    if (this.right.hasLetter(letter)) {
      acc.add(true);
      return this.right.encodeHelp(letter, acc);
    }
    else if (this.left.hasLetter(letter)){
      acc.add(false);
      return this.left.encodeHelp(letter, acc);
    }
    else {
      throw new RuntimeException("Tried to encode "
          + letter
          + " but that is not part of the language.");
    }
  }

  public boolean hasLetter(String letter) {
    return this.left.hasLetter(letter) || this.right.hasLetter(letter);
  }

  @Override
  public String decodeHelp(ArrayList<Boolean> encodedList, String acc) {
    // TODO Auto-generated method stub
    return null;
  }


}

class Leaf extends ABT{
  String letter;
  int freq;

  Leaf(String letter, int freq) {
    super(freq);
    this.letter = letter;
  }


  public ArrayList<Boolean> encodeHelp(String letter, ArrayList<Boolean> acc) {
    return acc;
  }


  public boolean hasLetter(String given) {
    return this.letter.equals(given);
  }


  public String decodeHelp(ArrayList<Boolean> encodedList, String acc) {
    if (encodedList.size() > 0) {
      return decodeHelp(encodedList, acc.concat(this.letter));
    }
    else {
      return acc;
    }
  }



}

class leafComparator implements Comparator<ABT>{

  public int compare(ABT l1, ABT l2) {
    if (l1.total < l2.total) {
      return -1;
    }
    else if (l1.total == l2.total) {
      return 0;
    }
    else {
      return 1;
    }
  }

}

class Utils {
  public void createTree(ArrayList<ABT> arr) {
    while (arr.size() > 1) {

      ABT temp1 = arr.get(0);
      ABT temp2 = arr.get(1);
      ABT newNode = new Node(temp1, temp2);

      arr.add(newNode);
      arr.remove(0);
      arr.remove(0);

      arr.sort(new leafComparator());
    }

  }
}


class ExamplesHuffman {
  ArrayList<String> letters1;
  ArrayList<Integer> frequency1;
  ArrayList<String> letters1Test;
  ArrayList<Integer> frequency1Test;

  ABT tree1;
  Huffman huffman1Test;
  Huffman huffman1;

  void testInit() {
    letters1 = new ArrayList<String>();
    letters1.add("a");
    letters1.add("b");
    letters1.add("c");
    letters1.add("d");
    letters1.add("e");
    letters1.add("f");

    frequency1 = new ArrayList<Integer>();
    frequency1.add(12);
    frequency1.add(45);
    frequency1.add(5);
    frequency1.add(13);
    frequency1.add(9);
    frequency1.add(16);

    letters1Test = new ArrayList<String>();
    letters1Test.add("a");
    frequency1Test = new ArrayList<Integer>();
    frequency1Test.add(12);

    huffman1 = new Huffman(letters1, frequency1);

    tree1 = new Node(new Leaf("b", 45), 
        new Node(new Node(new Leaf("a", 12), new Leaf("d", 13)), 
            new Node(new Node(new Leaf("c", 5), new Leaf("e", 9)), 
                new Leaf("f", 16))));
    huffman1Test = new Huffman(letters1, tree1);

  }

  void testExceptions(Tester t) {
    testInit();
    frequency1.add(2);
    t.checkConstructorException(new IllegalArgumentException("Error: "
        + "lists are of different lengths"), 
        "Huffman", this.letters1, this.frequency1);

    testInit();
    t.checkConstructorException(new IllegalArgumentException("Error: "
        + "length of the list of strings is less than 2"), 
        "Huffman", this.letters1Test, this.frequency1Test);
    
    t.checkConstructorException(new IllegalArgumentException("Error: "
        + "length of the list of strings is less than 2"), 
        "Huffman", this.letters1Test, this.frequency1Test);
  }

  void testConstruction(Tester t) {
    testInit();
    
    t.checkExpect(this.huffman1, this.huffman1);
  }

  void testEncode(Tester t) {
    this.testInit();
    
    t.checkExpect(this.huffman1.encode("b"), 
        new ArrayList<Boolean>(Arrays.asList(false)));
    t.checkExpect(this.huffman1.encode("eba"), 
        new ArrayList<Boolean>(Arrays.asList(true, true, false, true, false, true, false, false)));
    t.checkExpect(this.huffman1.encode("bad"), 
        new ArrayList<Boolean>(Arrays.asList(false, true, false, false, true, false, true)));
  }

}
















