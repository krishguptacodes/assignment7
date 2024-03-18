import java.util.ArrayList;
import java.util.Comparator;

import tester.*;

class Huffman {
  ArrayList<Leaf> leafList; 
  ArrayList<ITree> forest; 
  
  Huffman(ArrayList<String> letters, ArrayList<Integer> frequency) {
    if (letters.size() != frequency.size()) {
      throw new IllegalArgumentException("Error: lists are of different lengths");
    }
    else if (letters.size() < 2) {
      throw new IllegalArgumentException("Error: length of the list of strings is less than 2");
    }
    else {
      for (int i = 0; i < letters.size(); i = i + 1) {
        leafList.add(new Leaf(letters.get(i), frequency.get(i)));
      }
      this.leafList.sort(new leafComparator());
      for (Leaf l : leafList) {
        forest.add(l);
      }
      
    }
  }
}

interface ITree {
  
}

class Node implements ITree{
  int total;
  
  Node(int total) {
    this.total = total;
  }
  
}

class Leaf implements ITree{
  String letter;
  int freq;
  
  Leaf(String letter, int freq) {
    this.letter = letter;
    this.freq = freq;
  }
  
  
}

class leafComparator implements Comparator<Leaf>{

  public int compare(Leaf l1, Leaf l2) {
    if (l1.freq < l2.freq) {
      return -1;
    }
    else if (l1.freq == l2.freq) {
      return 0;
    }
    else {
      return 1;
    }
  }
  
}




class ExamplesHuffman {
  ArrayList<String> letters1;
  ArrayList<Integer> frequency1;

  //Huffman huffman1 = new Huffman(new ArrayList<>)
  
  void testInit(Tester t) {
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
    
    Huffman huffman1 = new Huffman(this.letters1, this.frequency1);
  }
  
  
}















