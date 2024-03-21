import java.util.ArrayList;
import java.util.function.Predicate;

import tester.Tester;

class ArrayUtilities {


  // Effect: removes items in the arraylist that fail the predicate
  <T> void removeFailing(ArrayList<T> arr, Predicate<T> pred) {
    int idxVar = 0;
    while (idxVar < arr.size()) {
      if (!pred.test(arr.get(idxVar))) {
        arr.remove(idxVar);
        idxVar = idxVar - 1;
      }
      idxVar = idxVar + 1;
    }
  }

  // Effect: removes items in the arraylist that pass the predicate
  <T> void removePassing(ArrayList<T> arr, Predicate<T> pred) {
    int idxVar = 0;
    while (idxVar < arr.size()) {
      if (pred.test(arr.get(idxVar))) {
        arr.remove(idxVar);
        idxVar = idxVar - 1;
      }
      idxVar = idxVar + 1;
    }
  }

  // Effect: removes items in the list based on the whether the predicate is
  // equal to the given boolean
  <T> void customRemove(ArrayList<T> arr, Predicate<T> pred, boolean keepPassing) {
    int idxVar = 0;
    while (idxVar < arr.size()) {
      if ((pred.test(arr.get(idxVar))) == keepPassing) {
        arr.remove(idxVar);
        idxVar = idxVar - 1;
      }
      idxVar = idxVar + 1;
    }
  }

}

// function object that tests if string starts with a
class StartsWithA implements Predicate<String> {

  public boolean test(String t) {
    return t.substring(0, 1).equals("a");
  }
}

//function object that tests if string length 1
class StringLength1 implements Predicate<String> {

  public boolean test(String t) {
    return t.length() == 1;
  }
}

//function object that tests if int is even
class IsEven implements Predicate<Integer> {
  public boolean test(Integer t) {
    return (t % 2) == 0;
  }
}

//function object that tests if int is greater than 20
class GreaterThan20 implements Predicate<Integer> {
  public boolean test(Integer t) {
    return t > 20;
  }
}



class ExamplesArrayLists {
  ArrayList<String> stringList;
  ArrayList<String> stringListTest1;
  ArrayList<String> stringListTest2;
  ArrayList<String> stringListTest3;
  ArrayList<String> stringListTest4;

  ArrayList<Integer> intList;
  ArrayList<Integer> intListTest1;
  ArrayList<Integer> intListTest2;
  ArrayList<Integer> intListTest3;


  void init() {
    stringList = new ArrayList<String>();
    stringListTest1 = new ArrayList<String>();
    stringListTest2 = new ArrayList<String>();
    stringListTest3 = new ArrayList<String>();
    stringListTest4 = new ArrayList<String>();

    intList = new ArrayList<Integer>();
    intListTest1 = new ArrayList<Integer>();
    intListTest2 = new ArrayList<Integer>();
    intListTest3 = new ArrayList<Integer>();


    this.stringList.add("apple");
    this.stringList.add("bornana");
    this.stringList.add("crabcakes");
    this.stringList.add("dates");
    this.stringList.add("apple");
    this.stringList.add("e");

    this.stringListTest2.add("bornana");
    this.stringListTest2.add("crabcakes");
    this.stringListTest2.add("dates");
    this.stringListTest2.add("e");

    this.stringListTest1.add("apple");
    this.stringListTest1.add("apple");

    this.stringListTest3.add("e");

    this.stringListTest4.add("apple");
    this.stringListTest4.add("bornana");
    this.stringListTest4.add("crabcakes");
    this.stringListTest4.add("dates");
    this.stringListTest4.add("apple");

    this.intList.add(4);
    this.intList.add(40);
    this.intList.add(14);
    this.intList.add(8);
    this.intList.add(21);
    this.intList.add(3);

    this.intListTest1.add(40);
    this.intListTest1.add(21);

    this.intListTest2.add(4);
    this.intListTest2.add(14);
    this.intListTest2.add(8);
    this.intListTest2.add(3);

    this.intListTest3.add(21);

  }

  //Tests for removeFailing on ArrayList of Integers and Strings
  void testRemoveFailing(Tester t) {
    this.init();

    new ArrayUtilities().removeFailing(this.stringList, new StartsWithA());
    t.checkExpect(this.stringList,this.stringListTest1);

    this.init();
    new ArrayUtilities().removeFailing(this.stringList, new StringLength1());
    t.checkExpect(this.stringList,this.stringListTest3);

    new ArrayUtilities().removeFailing(this.intList, new GreaterThan20());
    t.checkExpect(this.intList,this.intListTest1);


  }

  //Tests for removePassing on ArrayList of Integers and Strings
  void testRemovePassing(Tester t) {
    this.init();
    new ArrayUtilities().removePassing(this.stringList, new StartsWithA());
    t.checkExpect(this.stringList,this.stringListTest2);

    this.init();
    new ArrayUtilities().removePassing(this.stringList, new StringLength1());
    t.checkExpect(this.stringList,this.stringListTest4);

    new ArrayUtilities().removePassing(this.intList, new GreaterThan20());
    t.checkExpect(this.intList,this.intListTest2);
  }

  //Tests for customRemove on ArrayList of Integers and Strings
  void testCustomRemove(Tester t) {
    this.init();
    new ArrayUtilities().customRemove(this.stringList, new StartsWithA(), true);
    t.checkExpect(this.stringList,this.stringListTest2);

    this.init();
    new ArrayUtilities().customRemove(this.stringList, new StringLength1(), false);
    t.checkExpect(this.stringList,this.stringListTest3);

    this.init();
    new ArrayUtilities().customRemove(this.stringList, new StringLength1(), true);
    t.checkExpect(this.stringList,this.stringListTest4);

    this.init();
    new ArrayUtilities().customRemove(this.intList, new GreaterThan20(), false);
    new ArrayUtilities().customRemove(this.intList, new IsEven(), true);
    t.checkExpect(this.intList,this.intListTest3);


  }

}










