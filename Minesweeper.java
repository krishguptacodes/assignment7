import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;

interface IConstants {
  int WORLD_SCENE_X = 150;
  int WORLD_SCENE_Y = 150;

  int TILE_WIDTH = 50;
  int TILE_HEIGHT = 50;
  
  Color HIDDEN_COLOR = Color.GREEN;
  Color OUTLINE_COLOR = Color.DARK_GRAY;
  Color TEXT_COLOR = Color.BLACK;
}

class MineWorld extends World {
  int rows;
  int cols;
  int mines;

  ArrayList<ACell> cellList;

  MineWorld(int cols, int rows, int mines, Random rand) {
    if (mines > (cols * rows)) {
      throw new IllegalArgumentException("Cannot have more mines than tiles on board");
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
        new WorldScene(IConstants.WORLD_SCENE_X, IConstants.WORLD_SCENE_Y));
  }

  public void onKey() {

  }

}

class Utils {

  public WorldScene draw(ArrayList<ACell> cellList, int cols, int rows, WorldScene ws) {
    int counter = 0;
    WorldImage temp = new EmptyImage();
    
    for (int j = 0; j < rows; j++) {
      
      for (int i = 0; i < cols; i++) {
        new BesideImage(cellList.get(counter).drawHelp(), temp);
        counter += 1;
      }
      
      new AboveImage(cellList.get(counter).drawHelp(), temp);
    }
    
    ws.placeImageXY(temp, 0, 0);
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
          result.add(new MineCell(new ArrayList<ACell>(), true));
          counter += 1;
        }
        else {
          result.add(new EmptyCell(new ArrayList<ACell>(), true));
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

  public String countMines(ArrayList<ACell> neighbors) {
    int result = 0;
    
    for(int i = 0; i < neighbors.size(); i++) {
      result = result + neighbors.get(i).countHelp();
    }
    
  }
}

abstract class ACell {
  ArrayList<ACell> neighbors;
  boolean hidden;

  ACell(ArrayList<ACell> neighbors, boolean hidden) {
    this.neighbors = neighbors;
    this.hidden = hidden;
  }

  public abstract int countHelp();

  public abstract WorldImage drawHelp();

  // Effect: adds the given cell to this cell's neighbor list
  public void addNeighbor(ACell neighbor) {
    this.neighbors.add(neighbor);
  }

}

class EmptyCell extends ACell {
  
  EmptyCell(ArrayList<ACell> neighbors, boolean hidden) {
    super(neighbors, hidden);
  }

  public WorldImage drawHelp() {
    WorldImage base = new RectangleImage(IConstants.TILE_WIDTH, 
        IConstants.TILE_WIDTH, OutlineMode.SOLID, IConstants.HIDDEN_COLOR);
    WorldImage outline = new RectangleImage(IConstants.TILE_WIDTH, 
        IConstants.TILE_WIDTH, OutlineMode.OUTLINE, IConstants.OUTLINE_COLOR);
    WorldImage baseTile = new OverlayImage(outline, base);
    
    if (this.hidden) {
      return baseTile;
    }
    else {
      return new OverlayImage(new TextImage(new Utils().countMines(this.neighbors), 
          IConstants.TEXT_COLOR), outline);
    }
  }

  public int countHelp() {
    return 0;
  }
}

class MineCell extends ACell {
  MineCell(ArrayList<ACell> neighbors, boolean hidden) {
    super(neighbors, hidden);
  }
  
  public WorldImage drawHelp() {
    WorldImage base = new RectangleImage(IConstants.TILE_WIDTH, 
        IConstants.TILE_WIDTH, OutlineMode.SOLID, IConstants.HIDDEN_COLOR);
    WorldImage outline = new RectangleImage(IConstants.TILE_WIDTH, 
        IConstants.TILE_WIDTH, OutlineMode.OUTLINE, IConstants.OUTLINE_COLOR);
    WorldImage baseTile = new OverlayImage(outline, base);
    
    if (this.hidden) {
      return baseTile;
    }
    else {
      return new OverlayImage(new CircleImage(IConstants.TILE_WIDTH - 10, 
          OutlineMode.SOLID, IConstants.HIDDEN_COLOR), outline);
    }
  }
  
  public int countHelp() {
    return 1;
  }
}





class ExamplesMinesweeper {
  
  ACell cell1;
  ACell cell2;
  ArrayList<ACell> cellList3;
  
  ArrayList<ACell> mtList;
  ArrayList<Integer> intList1;

  ArrayList<ACell> mtCellList;
  ArrayList<ACell> cellList1;
  ArrayList<ACell> cellList2;
  
  MineWorld init1;
  MineWorld mw1;

  void initTestConditions() {
       
    mtList = new ArrayList<ACell>();
    intList1 = new ArrayList<Integer>(Arrays.asList(11, 1, 6, 3));
    
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
  
  void testAddNeighbor(Tester t) {
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

  void testGenerateMineSeeds(Tester t) {
    this.initTestConditions();

    t.checkExpect(new Utils().generateMineSeeds(new Random(1), 4, 16), this.intList1);
  }

  void testContainsNumber(Tester t) {
    this.initTestConditions();

    t.checkExpect(new Utils().containsNumber(this.intList1, 1), true);
    t.checkExpect(new Utils().containsNumber(this.intList1, 27), false);
    t.checkExpect(new Utils().containsNumber(this.intList1, 6), true);
  }


  void atestBigBang(Tester t) {
    MineWorld world = new MineWorld(0, 0, 0, new Random(1));
    int worldWidth = 600;
    int worldHeight = 400;
    double tickRate = .1;
    world.bigBang(worldWidth, worldHeight, tickRate);
  }
}










