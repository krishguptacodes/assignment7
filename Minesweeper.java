import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;

// Constants class
interface IConstants {
//  int WORLD_SCENE_X = 600;
//  int WORLD_SCENE_Y = 600;

  int TILE_SIZE = 200;

  Color HIDDEN_COLOR = Color.getHSBColor(0.25f, 0.62f, 0.60f);
  Color MINE_COLOR = Color.getHSBColor(1f, 0.62f, 0.60f);
  Color OUTLINE_COLOR = Color.DARK_GRAY;
  Color TEXT_COLOR = Color.BLACK;
}

// Represents the Minesweeper game world
class MineWorld extends World {
  int rows;
  int cols;
  int mines;

  ArrayList<ACell> cellList;

  MineWorld(int cols, int rows, int mines, Random rand) {
    if (mines > (cols * rows)) {
      throw new IllegalArgumentException("Cannot have more mines than tiles on board!");
    }
    else {
      this.cols = cols;
      this.rows = rows;
      this.mines = mines;
      this.cellList = new Utils().buildList(cols, rows, mines, rand);
    }
  }

  MineWorld(int cols, int rows, int mines) {
    this.cols = cols;
    this.rows = rows;
    this.mines = mines;

    this.cellList = new Utils().buildList(cols, rows, mines, new Random());
  }

  // Overides WorldScene in World, represents the current state of the world
  public WorldScene makeScene() {
    return new Utils().draw(this.cellList, this.cols, this.rows,
        new WorldScene(IConstants.TILE_SIZE * cols, IConstants.TILE_SIZE * rows));
  }

  public void onKey() {

  }

}

// Utils for Arraylist methods and calculation mehods
class Utils {

  public WorldScene draw(ArrayList<ACell> cellList, int cols, 
      int rows, WorldScene ws) {
    int counter = 0;
    WorldImage rowAcc = new EmptyImage();
    WorldImage result = new EmptyImage();

    for (int j = 0; j < rows; j++) {
      for (int i = 0; i < cols; i++) {
        rowAcc = new BesideImage(rowAcc, cellList.get(counter).drawHelp());
        counter = counter + 1;
      }
      result = new AboveImage(result, rowAcc);
      rowAcc = new EmptyImage();
    }

    ws.placeImageXY(result, IConstants.TILE_SIZE * cols / 2, 
        IConstants.TILE_SIZE * rows / 2);
    //ws.placeImageXY(cellList.get(0).drawHelp(), 0, counter);
    return ws;
  }

  public ArrayList<ACell> buildList(int cols, int rows, int mines, Random rand) {
    ArrayList<ACell> result = new ArrayList<ACell>();
    ArrayList<Integer> mineSeeds = new Utils()
        .generateMineSeeds(rand, mines, rows * cols);
    int counter = 0;

    for (int j = 0; j < rows; j++) {
      for (int i = 0; i < cols; i++) {
        if (new Utils().containsNumber(mineSeeds, counter)) {
          result.add(new MineCell(new ArrayList<ACell>(), false));
          counter += 1;
        }
        else {
          result.add(new EmptyCell(new ArrayList<ACell>(), false));
          counter += 1;
        }
      }
    }

    for (int i = 0; i < result.size(); i++) {
      int currentCol = new Utils().getCol(cols, rows, i);
      int currentRow = new Utils().getRow(cols, rows, i);

      // add left neighbor
      if (currentCol > 0) {
        result.get(i).addNeighbor(result.get(i - 1));
      }
      // add right neighbor
      if (currentCol < cols - 1) {
        result.get(i).addNeighbor(result.get(i + 1));
      }
      // add top neighbor 
      if (currentRow > 0) {
        result.get(i).addNeighbor(result.get(i - cols));
      }
      // add bottom neighbor 
      if (currentRow < rows - 1) {
        result.get(i).addNeighbor(result.get(i + cols));
      }
      // add top-left neighbor 
      if (currentRow > 0 && currentCol > 0) {
        result.get(i).addNeighbor(result.get(i - cols - 1));
      }
      // add top-right neighbor
      if (currentRow > 0 && currentCol < cols - 1) {
        result.get(i).addNeighbor(result.get(i - cols + 1));
      }
      // add bottom-left neighbor 
      if (currentRow < rows - 1 && currentCol > 0) {
        result.get(i).addNeighbor(result.get(i + cols - 1));
      }
      // add bottom-right neighbor 
      if (currentRow < rows - 1 && currentCol < cols - 1) {
        result.get(i).addNeighbor(result.get(i + cols + 1));
      }
      System.out.print(i);
      System.out.print(result.get(i).neighbors);
    }
    //System.out.print(result.get(1).neighbors.size());

    // System.out.print(result.get(1).neighbors);
    return result;
  }

  public int getCol(int cols, int rows, int i) {
    if((i >= (rows * cols)) || i < 0) {
      return -1;
    }
    else {
      return i % cols;
    }
  }

  public int getRow(int cols, int rows, int i) {
    if((i >= (rows * cols)) || i < 0) {
      return -1;
    }
    else {
      return Math.floorDiv(i, cols);
    }
  }

  public ArrayList<Integer> generateMineSeeds(Random random, int num, int bound) {
    ArrayList<Integer> result = new ArrayList<Integer>();

    while (result.size() < num) {
      int newInt = random.nextInt(bound);

      if (!(new Utils().containsNumber(result, newInt))) {
        result.add(newInt);
      }
    }

    return result;
  }

  public boolean containsNumber(ArrayList<Integer> intList, int findInt) {
    boolean result = false;

    for (int i = 0; i < intList.size(); i++) {
      if (intList.get(i) == findInt) {
        result = true;
      }
    }
    return result;
  }

  public int countMines(ArrayList<ACell> neighbors) {
    int result = 0;

    for(int i = 0; i < neighbors.size(); i++) {
      if (neighbors.get(i).isMine()) {
        result += 1;
      }
    }
    return result;    
  }
}

// Represents an abstract cell class with an ArrayList 
// of neighbors and a boolean flag if the mine is hidden
abstract class ACell {
  ArrayList<ACell> neighbors;
  boolean hidden;

  ACell(ArrayList<ACell> neighbors, boolean hidden) {
    this.neighbors = neighbors;
    this.hidden = hidden;
  }

  public abstract boolean isMine();

  public abstract WorldImage drawHelp();

  // Effect: adds the given cell to this cell's neighbor list
  public void addNeighbor(ACell neighbor) {
    this.neighbors.add(neighbor);
  }

}

// Represents a cell that has no mine on it
class EmptyCell extends ACell {

  EmptyCell(ArrayList<ACell> neighbors, boolean hidden) {
    super(neighbors, hidden);
  }

  public WorldImage drawHelp() {
    WorldImage base = new RectangleImage(IConstants.TILE_SIZE, 
        IConstants.TILE_SIZE, OutlineMode.SOLID, IConstants.HIDDEN_COLOR);
    WorldImage outline = new RectangleImage(IConstants.TILE_SIZE, 
        IConstants.TILE_SIZE, OutlineMode.OUTLINE, IConstants.OUTLINE_COLOR);
    WorldImage baseTile = new OverlayImage(outline, base);
    int mineNum = new Utils().countMines(this.neighbors);

    if (this.hidden) {
      return baseTile;
    }
    else {
      if (mineNum > 0) {
        return new OverlayImage(new TextImage(mineNum + "", 
            IConstants.TILE_SIZE / 2, IConstants.TEXT_COLOR), outline);
      }
      else {
        return outline;
      }
    }
  }

  public boolean isMine() {
    return false;
  }
}

// Represents a cell with a mine on it
class MineCell extends ACell {
  MineCell(ArrayList<ACell> neighbors, boolean hidden) {
    super(neighbors, hidden);
  }

  public WorldImage drawHelp() {
    WorldImage base = new RectangleImage(IConstants.TILE_SIZE, 
        IConstants.TILE_SIZE, OutlineMode.SOLID, IConstants.HIDDEN_COLOR);
    WorldImage outline = new RectangleImage(IConstants.TILE_SIZE, 
        IConstants.TILE_SIZE, OutlineMode.OUTLINE, IConstants.OUTLINE_COLOR);
    WorldImage baseTile = new OverlayImage(outline, base);

    if (this.hidden) {
      return baseTile;
    }
    else {
      return new OverlayImage(new CircleImage(IConstants.TILE_SIZE / 4, 
          OutlineMode.SOLID, IConstants.MINE_COLOR), outline);
    }
  }

  public boolean isMine() {
    return true;
  }
}

class ExamplesMinesweeper {

  ACell cell1;
  ACell cell2;
  ArrayList<ACell> cellList3;

  ArrayList<ACell> mtList;
  ArrayList<Integer> intList1;
  ArrayList<Integer> intList2;
  ArrayList<Integer> intList3;

  ArrayList<ACell> mtCellList;
  ArrayList<ACell> cellList1;
  ArrayList<ACell> cellList2;

  MineWorld init1;
  MineWorld mw1;

  void initTestConditions() {

    mtList = new ArrayList<ACell>();
    intList1 = new ArrayList<Integer>(Arrays.asList(11, 1, 6, 3));
    intList2 = new ArrayList<Integer>(Arrays.asList(11, 4, 14, 0, 7, 13, 15));
    intList3 = new ArrayList<Integer>(Arrays.asList(9, 10, 6, 3, 2));

    mtCellList = new ArrayList<ACell>();

    cellList1 = new ArrayList<ACell>(Arrays.asList(new EmptyCell(this.mtList, true),
        new MineCell(this.mtList, true), new EmptyCell(this.mtList, true), 
        new EmptyCell(this.mtList, true), new MineCell(this.mtList, true), 
        new EmptyCell(this.mtList, true), new MineCell(this.mtList, true),
        new EmptyCell(this.mtList, true), new MineCell(this.mtList, true)));

    cellList2 = new ArrayList<ACell>(Arrays.asList(new EmptyCell(this.mtList, true),
        new EmptyCell(this.mtList, true), new MineCell(this.mtList, true),
        new EmptyCell(this.mtList, true)));
    cellList2.get(0).addNeighbor(cellList2.get(1));
    cellList2.get(0).addNeighbor(cellList2.get(2));
    cellList2.get(0).addNeighbor(cellList2.get(3));

    cellList2.get(1).addNeighbor(cellList2.get(0));
    cellList2.get(1).addNeighbor(cellList2.get(3));
    cellList2.get(1).addNeighbor(cellList2.get(2));

    cellList2.get(2).addNeighbor(cellList2.get(3));
    cellList2.get(2).addNeighbor(cellList2.get(0));
    cellList2.get(2).addNeighbor(cellList2.get(1));

    cellList2.get(3).addNeighbor(cellList2.get(2));
    cellList2.get(3).addNeighbor(cellList2.get(1));
    cellList2.get(3).addNeighbor(cellList2.get(0));


    //init1 = new MineWorld(3, 3, 4, new Random(1));
    //mw1 = new MineWorld(3, 3, 4, new Random(1));

    cell1 = new EmptyCell(mtList, true);
    cell2 = new EmptyCell(mtList, true);

    cellList3 = new ArrayList<ACell>();

  }

  void testMineWorldConstruct(Tester t) {
    this.initTestConditions();

    //t.checkExpect(this.init1.cellList, this.cellList1);

  }

  void testCountMines(Tester t) {
    this.initTestConditions();


    t.checkExpect(new Utils().countMines(this.mtCellList), 0);
    t.checkExpect(new Utils().countMines(this.cellList1), 4);
    t.checkExpect(new Utils().countMines(this.cellList2), 1);

  }

  void atestAddNeighbor(Tester t) {
    this.initTestConditions();

    cellList3.add(cell2);
    this.cell1.addNeighbor(cell2);

    t.checkExpect(this.cell1.neighbors, cellList3);
  }

  void atestBuildList(Tester t) {
    this.initTestConditions();

    //t.checkExpect(new Utils().buildList(2, 2, 1, new Random(1)), this.cellList2);
  }

  void testGetRow(Tester t) {
    this.initTestConditions();

    t.checkExpect(new Utils().getRow(2, 2, 0), 0);
    t.checkExpect(new Utils().getRow(2, 2, 1), 0);
    t.checkExpect(new Utils().getRow(2, 2, 2), 1);
    t.checkExpect(new Utils().getRow(2, 2, 3), 1);
  }

  void testGetCol(Tester t) {
    this.initTestConditions();

    t.checkExpect(new Utils().getCol(2, 2, 0), 0);
    t.checkExpect(new Utils().getCol(2, 2, 1), 1);
    t.checkExpect(new Utils().getCol(2, 2, 2), 0);
    t.checkExpect(new Utils().getCol(2, 2, 3), 1);

    t.checkExpect(new Utils().getCol(4, 3, 4), 0);
  }
  
  void testIsMine(Tester t) {
    this.initTestConditions();

    t.checkExpect(this.cell1.isMine(), false);
    t.checkExpect(this.cell2.isMine(), false);
    t.checkExpect(new MineCell(cellList1, false).isMine(), true);
  }

  void testGenerateMineSeeds(Tester t) {
    this.initTestConditions();

    t.checkExpect(new Utils().generateMineSeeds(new Random(1), 4, 16), this.intList1);
    t.checkExpect(new Utils().generateMineSeeds(new Random(2), 7, 16), this.intList2);
    t.checkExpect(new Utils().generateMineSeeds(new Random(3), 5, 25), this.intList3);
  }

  void testContainsNumber(Tester t) {
    this.initTestConditions();

    t.checkExpect(new Utils().containsNumber(this.intList1, 1), true);
    t.checkExpect(new Utils().containsNumber(this.intList1, 27), false);
    t.checkExpect(new Utils().containsNumber(this.intList1, 6), true);
  }

  void testDraw(Tester t) {
    
  }
  
  void testMakeScene(Tester t) {
    
  }

  void atestBigBang(Tester t) {
    int columns = 3;
    int rows = 3;
    int mines = 2;
    
    MineWorld world = new MineWorld(columns, rows, mines, new Random(1));
    int worldWidth = IConstants.TILE_SIZE * columns;
    int worldHeight = IConstants.TILE_SIZE * rows;
    double tickRate = .1;
    world.bigBang(worldWidth, worldHeight, tickRate);
  }
}










