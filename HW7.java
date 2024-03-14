import java.util.function.BiFunction;
import java.util.function.Predicate;

import tester.*;

//represents a list of t
interface IList<T> {

  // returns true if predicate is true on any item in list
  boolean orMap(Predicate<T> pred);

  //combine the items in this list using the given function
  <U> U fold(BiFunction<T, U, U> converter, U initial); 

}

//represents an empty list of t
class MtList<T> implements IList<T> {

  // returns false if predicate is applied to empty list
  public boolean orMap(Predicate<T> pred) {
    return false;
  }

  //combine the items in this list using the given function
  public <U> U fold(BiFunction<T, U, U> converter, U initial) {
    return initial;
  }
}

//represents a list of t
class ConsList<T> implements IList<T> {
  T first;
  IList<T> rest;

  ConsList(T first, IList<T> rest) {
    this.first = first;
    this.rest = rest;
  }

  // returns true if predicate is true on any item in list
  public boolean orMap(Predicate<T> pred) {
    return pred.test(this.first) || this.rest.orMap(pred);
  }

  //combine the items in this list using the given function
  public <U> U fold(BiFunction<T, U, U> converter, U initial) {
    return converter.apply(this.first, this.rest.fold(converter, initial));
  }
}

// Represents a course with a name, prof, and list of students
class Course {
  String name;
  Instructor prof;
  IList<Student> students;

  Course(String name, Instructor prof, IList<Student> students) {
    this.name = name;
    this.prof = prof;
    this.prof.courses = new ConsList<Course>(this, this.prof.courses);
    this.students = students;
  }
  
  Course(String name, Instructor prof) {
    this.name = name;
    this.prof = prof;
    this.prof.courses = new ConsList<Course>(this, this.prof.courses);
    this.students = new MtList<Student>();
  }

  // EFFECT: Updates the course's list of students with the given student
  void enrollHelp(Student s) {
    IList<Student> oldList = this.students;
    this.students = new ConsList<Student>(s, oldList);
  }

}

// Represents an instructor with a name and list of courses they teach
class Instructor {
  String name;
  IList<Course> courses;

  Instructor(String name, IList<Course> courses) {
    this.name = name;
    this.courses = courses;
  }
  
  Instructor(String name) {
    this.name = name;
    this.courses = new MtList<Course>();
  }

  // returns true if this student is in more 
  // than one of the instructor's classes
  public boolean dejavu(Student c) {
    return this.courses.fold(new RepeatingStudent(c), 0) > 1;  
  }

}

class Student {
  String name;
  int id;
  IList<Course> courses;

  Student(String name, int id, IList<Course> courses) {
    this.name = name;
    this.id = id;
    this.courses = courses;
  }
  
  Student(String name, int id) {
    this.name = name;
    this.id = id;
    this.courses = new MtList<Course>();
  }

  // EFFECT: Enrolls a student into a course
  void enroll(Course c) {
    IList<Course> oldList = this.courses;
    this.courses = new ConsList<Course>(c , oldList);
    c.enrollHelp(this);
  }

  // determines whether the given Student is in any of 
  // the same classes as this Student
  public boolean classmates(Student c) {
    return this.courses.orMap(new IsClassmate(c));
  }

  // Checks whether two student IDs are the same
  public boolean sameID(Student s) {
    return this.id == s.id;
  }
}

// determines whether the Student is in the same 
// class as the given student
class IsClassmate implements Predicate<Course> {
  Student s;

  IsClassmate(Student s) {
    this.s = s; 
  }

  public boolean test(Course t) {
    return t.students.orMap(new SameStudent(s));
  }

}

// determines whether the Student is the same 
// as the given student
class SameStudent implements Predicate<Student> {
  Student s;
  
  SameStudent(Student s) {
    this.s = s;
  }

  public boolean test(Student t) {
    return t.sameID(s);
  }
}

// counts how many courses the student is in
class RepeatingStudent implements BiFunction<Course, Integer, Integer> {
  Student c;

  RepeatingStudent(Student c) {
    this.c = c;
  }

  public Integer apply(Course t, Integer sum) {
    if (t.students.orMap(new SameStudent(c))) {
      return sum + 1;
    }
    else {
      return sum;
    }
  }

}


class ExamplesRegistrar {
  //  It should always be the case that any Student who is enrolled in a 
  //  Course should appear in the list of Students for that Course, and the 
  //  Course should likewise appear in the Student’s list of Courses.
  //
  //  It should always be the case that the Instructor for any Course should 
  //  have that Course appear in the Instructor’s list of Courses.

  Student student1;
  Student student2;
  Student student3;
  Student student4;
  Student student5;

  Course computerScience;
  Course music;
  Course math;
  Course art;

  Instructor Ferd;

  Instructor Park;

  Instructor Bach;

  IList<Course> emptyCourseList = new MtList<Course>();
  IList<Course> stuCourseList1;
  IList<Course> stuCourseList2;
  IList<Course> stuCourseList3;
  IList<Course> stuCourseList4;
  IList<Course> stuCourseList5;

  IList<Student> emptyStudentList = new MtList<Student>();
  IList<Student> computerScienceList;
  IList<Student> mathList;
  IList<Student> musicList;
  IList<Student> artList;

  void initTestConditions() {
    // initialize students
    this.student1 = new Student("Kevin", 1234, emptyCourseList);
    this.student2 = new Student("Nick", 4321, emptyCourseList);
    this.student3 = new Student("David", 1001, emptyCourseList);
    this.student4 = new Student("Rusheel", 1111, emptyCourseList);
    this.student5 = new Student("Steve", 4201, emptyCourseList);

    // initialize instructors
    this.Ferd = new Instructor("Ferd", emptyCourseList);
    this.Park = new Instructor("Park", emptyCourseList);
    this.Bach = new Instructor("Bach", emptyCourseList);
    
    // initialize courses
    this.computerScience = new Course("Computer Science", Ferd, emptyStudentList);
    this.music = new Course("Music", Bach, emptyStudentList);
    this.math = new Course("Math", Park, emptyStudentList);
    this.art = new Course("Art", Bach, emptyStudentList);

    // modify students
    this.stuCourseList1 = new ConsList<Course>(this.computerScience,
        new ConsList<Course>(this.art, emptyCourseList));
    this.stuCourseList2 = new ConsList<Course>(this.computerScience,
        new ConsList<Course>(this.math, emptyCourseList));
    this.stuCourseList3 = new ConsList<Course>(this.math, this.emptyCourseList);
    this.stuCourseList4 = new ConsList<Course>(this.computerScience,
        new ConsList<Course>(this.music, emptyCourseList));
    this.stuCourseList5 = new ConsList<Course>(this.art, 
        new ConsList<Course>(this.music, this.emptyCourseList));

    // modify courses
    this.computerScienceList = new ConsList<Student>(this.student1, 
        new ConsList<Student>(this.student2, 
            new ConsList<Student>(this.student4, this.emptyStudentList)));
    this.mathList = new ConsList<Student>(this.student2, 
        new ConsList<Student>(this.student3, emptyStudentList));
    this.musicList = new ConsList<Student>(this.student4, 
        new ConsList<Student>(this.student5, this.emptyStudentList));
    this.artList = new ConsList<Student>(this.student1, 
        new ConsList<Student>(this.student5, this.emptyStudentList));


  }

  void testEnroll(Tester t) {
    this.initTestConditions();

    this.student1.enroll(this.art);
    this.student1.enroll(this.computerScience);
    t.checkExpect(this.student1.courses, this.stuCourseList1);

    this.student2.enroll(this.math);
    this.student2.enroll(this.computerScience);
    t.checkExpect(this.student2.courses, this.stuCourseList2);

    this.student3.enroll(this.math);
    t.checkExpect(this.student3.courses, this.stuCourseList3);

    this.student4.enroll(this.music);
    this.student4.enroll(this.computerScience);
    t.checkExpect(this.student4.courses, this.stuCourseList4);

    this.student5.enroll(this.music);
    this.student5.enroll(this.art);
    t.checkExpect(this.student5.courses, this.stuCourseList5);
  }
  
  void testEnrollHelp(Tester t ) {
    this.initTestConditions();
    
    this.art.enrollHelp(this.student5);
    this.art.enrollHelp(this.student1);
    t.checkExpect(this.art.students, this.artList);
    
    this.computerScience.enrollHelp(this.student4);  
    this.computerScience.enrollHelp(this.student2);
    this.computerScience.enrollHelp(this.student1);
    t.checkExpect(this.computerScience.students, this.computerScienceList);
    
    this.music.enrollHelp(this.student5);
    this.music.enrollHelp(this.student4);
    t.checkExpect(this.music.students, this.musicList);
    
    this.math.enrollHelp(this.student3);
    this.math.enrollHelp(this.student2);
    t.checkExpect(this.math.students, this.mathList);
  }

  void testClassmates(Tester t) {
    this.initTestConditions();
    this.testEnroll(t);
    t.checkExpect(this.student1.classmates(this.student2), true);
    t.checkExpect(this.student1.classmates(this.student4), true);
    t.checkExpect(this.student5.classmates(this.student4), true);
    t.checkExpect(this.student2.classmates(this.student5), false);
    t.checkExpect(this.student3.classmates(this.student1), false);
  }

  void testSameID(Tester t) {
    this.initTestConditions();
    t.checkExpect(this.student1.sameID(student1), true);
    t.checkExpect(this.student2.sameID(student1), false);
    t.checkExpect(this.student1.sameID(student4), false);
    t.checkExpect(this.student3.sameID(student3), true);
  }

  boolean testDejavu(Tester t) {
    this.initTestConditions();
    this.testEnroll(t);
    return t.checkExpect(this.Ferd.dejavu(this.student5), false)
        && t.checkExpect(this.Ferd.dejavu(this.student1), false)
        && t.checkExpect(this.Park.dejavu(this.student4), false)
        && t.checkExpect(this.Bach.dejavu(this.student5), true)
        && t.checkExpect(this.Bach.dejavu(this.student1), false);
  }

}









