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

  int TILE_SIZE = 20;
  int Y_PADDING = 0;
  
  Color HIDDEN_COLOR = Color.getHSBColor(0.25f, 0.62f, 0.60f);
  Color MINE_COLOR = Color.getHSBColor(1f, 0.62f, 0.60f);
  Color OUTLINE_COLOR = Color.DARK_GRAY;
  Color TEXT_COLOR1 = Color.blue;
  Color FLAG_COLOR = Color.YELLOW;

  WorldImage BASE_FILL = new RectangleImage(IConstants.TILE_SIZE, 
      IConstants.TILE_SIZE, OutlineMode.SOLID, IConstants.HIDDEN_COLOR);
  WorldImage OUTLINE = new RectangleImage(IConstants.TILE_SIZE, 
      IConstants.TILE_SIZE, OutlineMode.OUTLINE, IConstants.OUTLINE_COLOR);
  WorldImage BASE_TILE = new OverlayImage(IConstants.OUTLINE, IConstants.BASE_FILL);

  WorldImage FULL_EMPTY_TILE = new OverlayImage(IConstants.OUTLINE, 
      new RectangleImage(IConstants.TILE_SIZE, 
          IConstants.TILE_SIZE, OutlineMode.SOLID, Color.getHSBColor(.07f, 0.4f, 0.8f)));
}

// Represents the Minesweeper game world
class MineWorld extends World {
  int rows;
  int cols;
  int mines;
  int score;

  ArrayList<ACell> cellList;

  // Constructor for testing with custom random
  MineWorld(int cols, int rows, int mines, Random rand, int score) {
    if (mines > (cols * rows)) {
      throw new IllegalArgumentException("Cannot have more mines than tiles on board!");
    }
    else {
      this.cols = cols;
      this.rows = rows;
      this.mines = mines;
      this.cellList = new Utils().buildList(cols, rows, mines, rand);
      this.score = 0;
    }
  }

  // Constructor for testing draw method with a given cell list
  MineWorld(int cols, int rows, int mines, ArrayList<ACell> cellList) {
    if (mines > (cols * rows)) {
      throw new IllegalArgumentException("Cannot have more mines than tiles on board!");
    }
    else {
      this.cols = cols;
      this.rows = rows;
      this.mines = mines;
      this.cellList = cellList;
    }
  }

  // Constructor for real randomized games
  MineWorld(int cols, int rows, int mines) {
    if (mines > (cols * rows)) {
      throw new IllegalArgumentException("Cannot have more mines than tiles on board!");
    }
    else {
      this.cols = cols;
      this.rows = rows;
      this.mines = mines;

      this.cellList = new Utils().buildList(cols, rows, mines, new Random());
    }
  }

  // Overides WorldScene in World, represents the current state of the world
  public WorldScene makeScene() {
    return new Utils().draw(this.cellList, this.cols, this.rows,
        new WorldScene(IConstants.TILE_SIZE * cols, IConstants.TILE_SIZE * rows));
  }


  public void onMouseClicked(Posn pos, String buttonName){ 
    int cellIndex = new Utils().findCellIndex(IConstants.TILE_SIZE, 
        this.cols, this.rows, pos, IConstants.Y_PADDING);
    if (buttonName.equals("LeftButton")) {
      System.out.print(cellIndex);
      this.cellList.get(cellIndex).floodFill(new ArrayList<ACell>());
    }
    else if (buttonName.equals("RightButton")) {
      this.cellList.get(cellIndex).flipFlag();
    }
    
  }

}

// Utils for Arraylist methods and calculation mehods
class Utils {

  public WorldScene draw(ArrayList<ACell> cellList, int cols, 
      int rows, int score, WorldScene ws) {
    int counter = 0;
    WorldImage scoreRect = new RectangleImage (IConstants.TILE_SIZE * cols, 
        IConstants.TILE_SIZE, OutlineMode.OUTLINE ,IConstants.OUTLINE_COLOR);
    WorldImage textImage = new TextImage (score + "", IConstants.TILE_SIZE / 2, 
        IConstants.TEXT_COLOR1);
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

    result = new AboveImage (new OverlayImage(textImage, scoreRect), result);
    ws.placeImageXY(result, IConstants.TILE_SIZE * cols / 2, 
        IConstants.TILE_SIZE * rows / 2);
    
    return ws;
    
  }

  // Builds an ArrayList of mines and links neighboring mines
  public ArrayList<ACell> buildList(int cols, int rows, int mines, Random rand) {
    ArrayList<ACell> result = new ArrayList<ACell>();
    ArrayList<Integer> mineSeeds = new Utils()
        .generateMineSeeds(rand, mines, rows * cols);
    int counter = 0;

    for (int j = 0; j < rows; j++) {
      for (int i = 0; i < cols; i++) {
        if (new Utils().containsNumber(mineSeeds, counter)) {
          result.add(new MineCell(new ArrayList<ACell>(), true, false));
          counter += 1;
        }
        else {
          result.add(new EmptyCell(new ArrayList<ACell>(), true, false));
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

    }

    return result;
  }

  // Gets the column position of a cell in an ArrayList
  public int getCol(int cols, int rows, int i) {
    if ((i >= (rows * cols)) || i < 0) {
      return -1;
    }
    else {
      return i % cols;
    }
  }

  // Gets the row position of a cell in an ArrayList
  public int getRow(int cols, int rows, int i) {
    if ((i >= (rows * cols)) || i < 0) {
      return -1;
    }
    else {
      return Math.floorDiv(i, cols);
    }
  }

  // Gets the column position of a cell in an ArrayList
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

  // Tests if the given ArrayList has the given int
  public boolean containsNumber(ArrayList<Integer> intList, int findInt) {
    boolean result = false;

    for (int i = 0; i < intList.size(); i++) {
      if (intList.get(i) == findInt) {
        result = true;
      }
    }
    return result;
  }

  // Counts the number of mines in the list of ACells
  public int countMines(ArrayList<ACell> neighbors) {
    int result = 0;

    for (int i = 0; i < neighbors.size(); i++) {
      if (neighbors.get(i).isMine()) {
        result += 1;
      }
    }
    return result;    
  }
  
  public int findCellIndex(int tileSize, int cols, int rows, Posn pos, int padding) {
    int clickedCol = Math.floorDiv(pos.x, tileSize);
    int clickedRow = Math.floorDiv(pos.y, tileSize
        + (padding * tileSize));
    return ((clickedRow * cols) + clickedCol);
  }
}

// Represents an abstract cell class with an ArrayList 
// of neighbors and a boolean flag if the mine is hidden
abstract class ACell {
  ArrayList<ACell> neighbors;
  boolean hidden;
  boolean flagged;

  ACell(ArrayList<ACell> neighbors, boolean hidden, boolean flagged) {
    this.neighbors = neighbors;
    this.hidden = hidden;
    this.flagged = flagged;
  }

  public void flipFlag() { this.flagged = !this.flagged; }

  // floodFill behavior for revealing mines
  public abstract void floodFill(ArrayList<ACell> visited);

  // Returns true if this is a mine
  public abstract boolean isMine();

  // Draws an individual cell
  public abstract WorldImage drawHelp();

  // Effect: adds the given cell to this cell's neighbor list
  public void addNeighbor(ACell neighbor) {
    this.neighbors.add(neighbor);
  }

}

// Represents a cell that has no mine on it
class EmptyCell extends ACell {

  EmptyCell(ArrayList<ACell> neighbors, boolean hidden, boolean flagged) {
    super(neighbors, hidden, flagged);
  }

  // draws the individual empty cell
  public WorldImage drawHelp() {
    //    WorldImage base = new RectangleImage(IConstants.TILE_SIZE, 
    //        IConstants.TILE_SIZE, OutlineMode.SOLID, IConstants.HIDDEN_COLOR);
    //    WorldImage outline = new RectangleImage(IConstants.TILE_SIZE, 
    //        IConstants.TILE_SIZE, OutlineMode.OUTLINE, IConstants.OUTLINE_COLOR);
    //    WorldImage baseTile = new OverlayImage(outline, base);
    int mineNum = new Utils().countMines(this.neighbors);

    if (this.hidden && !this.flagged) {
      return IConstants.BASE_TILE;
    }
    else if (this.hidden && this.flagged) {
      return new OverlayImage(new EquilateralTriangleImage(IConstants.TILE_SIZE / 2, 
          OutlineMode.SOLID, IConstants.FLAG_COLOR), IConstants.BASE_TILE);
    }
    else if (mineNum == 1) {
      return new OverlayImage(new TextImage(mineNum + "", 
          IConstants.TILE_SIZE / 2, IConstants.TEXT_COLOR1), IConstants.FULL_EMPTY_TILE);
    }
    else if (mineNum == 2) {
      return new OverlayImage(new TextImage(mineNum + "", 
          IConstants.TILE_SIZE / 2, Color.green), IConstants.FULL_EMPTY_TILE);
    }
    else if (mineNum > 2) {
      return new OverlayImage(new TextImage(mineNum + "", 
          IConstants.TILE_SIZE / 2, Color.red), IConstants.FULL_EMPTY_TILE);
    }
    else {
      return IConstants.FULL_EMPTY_TILE;
    }

  }

  // Returns true if this is a mine
  public boolean isMine() {
    return false;
  }

  public void floodFill(ArrayList<ACell> visited) {
    if (!this.flagged) {
      this.hidden = false;
    }
    
    if (new Utils().countMines(this.neighbors) < 1 && !this.flagged) {
      visited.add(this);
      for (int i = 0; i < this.neighbors.size(); i++) {
        if (!visited.contains(this.neighbors.get(i))) {
          this.neighbors.get(i).floodFill(visited);
        }
      }
    }

    
  }
}

// Represents a cell with a mine on it
class MineCell extends ACell {
  MineCell(ArrayList<ACell> neighbors, boolean hidden, boolean flagged) {
    super(neighbors, hidden, flagged);
  }

  // Draws the individual cell
  public WorldImage drawHelp() {
    //    WorldImage base = new RectangleImage(IConstants.TILE_SIZE, 
    //        IConstants.TILE_SIZE, OutlineMode.SOLID, IConstants.HIDDEN_COLOR);
    //    WorldImage outline = new RectangleImage(IConstants.TILE_SIZE, 
    //        IConstants.TILE_SIZE, OutlineMode.OUTLINE, IConstants.OUTLINE_COLOR);
    //    WorldImage baseTile = new OverlayImage(outline, base);

    if (this.hidden && !this.flagged) {
      return IConstants.BASE_TILE;
    }
    else if (this.hidden && this.flagged) {
      return new OverlayImage(new EquilateralTriangleImage(IConstants.TILE_SIZE / 2, 
          OutlineMode.SOLID, IConstants.FLAG_COLOR), IConstants.BASE_TILE);
    }
    else {
      return new OverlayImage(new CircleImage(IConstants.TILE_SIZE / 4, 
          OutlineMode.SOLID, IConstants.MINE_COLOR), IConstants.FULL_EMPTY_TILE);
    }
  }

  // Returns true if this is a mine
  public boolean isMine() {
    return true;
  }

  @Override
  public void floodFill(ArrayList<ACell> visited) {
    // TODO zero cases
    
  }

}

class ExamplesMinesweeper {

  ACell cell1;
  ACell cell2;

  ACell flagCell;
  ACell mineCell;
  ACell numberedCell;
  ACell emptyCell;

  ArrayList<ACell> mtList;
  ArrayList<Integer> intList1;
  ArrayList<Integer> intList2;
  ArrayList<Integer> intList3;

  ArrayList<ACell> mtCellList;
  ArrayList<ACell> cellList1;
  ArrayList<ACell> cellList2;
  ArrayList<ACell> cellList2Test;
  ArrayList<ACell> cellList3;
  ArrayList<ACell> cellList4;
  ArrayList<ACell> cellList5;

  MineWorld init1;
  WorldScene mw1;

  WorldScene twoByTwoWorld;

  // Initialize fields
  void initTestConditions() {

    twoByTwoWorld = new WorldScene(2 * IConstants.TILE_SIZE, 2 * IConstants.TILE_SIZE);

    flagCell = new EmptyCell(new ArrayList<ACell>(), true, true);
    mineCell = new MineCell(new ArrayList<ACell>(), false, false);
    numberedCell = new EmptyCell(new ArrayList<ACell>(Arrays.asList(mineCell)), false, false);
    emptyCell = new EmptyCell(new ArrayList<ACell>(), false, false);

    // ArrayList inits
    mtList = new ArrayList<ACell>();
    intList1 = new ArrayList<Integer>(Arrays.asList(11, 1, 6, 3));
    intList2 = new ArrayList<Integer>(Arrays.asList(11, 4, 14, 0, 7, 13, 15));
    intList3 = new ArrayList<Integer>(Arrays.asList(9, 10, 6, 3, 2));

    mtCellList = new ArrayList<ACell>();

    cellList1 = new ArrayList<ACell>(Arrays.asList(new EmptyCell(new ArrayList<ACell>(), 
        true, false),
        new MineCell(new ArrayList<ACell>(), true, false), 
        new EmptyCell(new ArrayList<ACell>(), true, false), 
        new EmptyCell(new ArrayList<ACell>(), true, false), 
        new MineCell(new ArrayList<ACell>(), true, false), 
        new EmptyCell(new ArrayList<ACell>(), true, false), 
        new MineCell(new ArrayList<ACell>(), true, false),
        new EmptyCell(new ArrayList<ACell>(), true, false), 
        new MineCell(new ArrayList<ACell>(), true, false)));

    cellList2 = new ArrayList<ACell>(Arrays.asList(new EmptyCell(new ArrayList<ACell>(), 
        true, false), new EmptyCell(new ArrayList<ACell>(), true, false), 
        new MineCell(new ArrayList<ACell>(), true, false),
        new EmptyCell(new ArrayList<ACell>(), true, false)));

    cellList2.get(0).neighbors.add(cellList2.get(1));
    cellList2.get(0).neighbors.add(cellList2.get(2));
    cellList2.get(0).neighbors.add(cellList2.get(3));

    cellList2.get(1).neighbors.add(cellList2.get(0));
    cellList2.get(1).neighbors.add(cellList2.get(3));
    cellList2.get(1).neighbors.add(cellList2.get(2));

    cellList2.get(2).neighbors.add(cellList2.get(3));
    cellList2.get(2).neighbors.add(cellList2.get(0));
    cellList2.get(2).neighbors.add(cellList2.get(1));

    cellList2.get(3).neighbors.add(cellList2.get(2));
    cellList2.get(3).neighbors.add(cellList2.get(0));
    cellList2.get(3).neighbors.add(cellList2.get(1));


    cell1 = new EmptyCell(mtList, true, false);
    cell2 = new EmptyCell(mtList, true, false);

    cellList3 = new ArrayList<ACell>();

    cellList4 = new ArrayList<ACell>(Arrays.asList(new EmptyCell(new ArrayList<ACell>(), 
        false, false), new EmptyCell(new ArrayList<ACell>(), false, false), 
        new MineCell(new ArrayList<ACell>(), true, true),
        new EmptyCell(new ArrayList<ACell>(), true, false)));

    cellList4.get(0).neighbors.add(cellList4.get(1));
    cellList4.get(0).neighbors.add(cellList4.get(2));
    cellList4.get(0).neighbors.add(cellList4.get(3));

    cellList4.get(1).neighbors.add(cellList4.get(0));
    cellList4.get(1).neighbors.add(cellList4.get(3));
    cellList4.get(1).neighbors.add(cellList4.get(2));

    cellList4.get(2).neighbors.add(cellList4.get(3));
    cellList4.get(2).neighbors.add(cellList4.get(0));
    cellList4.get(2).neighbors.add(cellList4.get(1));

    cellList4.get(3).neighbors.add(cellList4.get(2));
    cellList4.get(3).neighbors.add(cellList4.get(0));
    cellList4.get(3).neighbors.add(cellList4.get(1));

    cellList5 = new ArrayList<ACell>(Arrays.asList(new EmptyCell(new ArrayList<ACell>(), 
        true, true),
        new EmptyCell(new ArrayList<ACell>(), false, false), 
        new EmptyCell(new ArrayList<ACell>(), true, false),
        new MineCell(new ArrayList<ACell>(), true, false)));

    cellList5.get(0).neighbors.add(cellList5.get(1));
    cellList5.get(0).neighbors.add(cellList5.get(2));
    cellList5.get(0).neighbors.add(cellList5.get(3));

    cellList5.get(1).neighbors.add(cellList5.get(0));
    cellList5.get(1).neighbors.add(cellList5.get(3));
    cellList5.get(1).neighbors.add(cellList5.get(2));

    cellList5.get(2).neighbors.add(cellList5.get(3));
    cellList5.get(2).neighbors.add(cellList5.get(0));
    cellList5.get(2).neighbors.add(cellList5.get(1));

    cellList5.get(3).neighbors.add(cellList5.get(2));
    cellList5.get(3).neighbors.add(cellList5.get(0));
    cellList5.get(3).neighbors.add(cellList5.get(1));


    init1 = new MineWorld(2, 2, 1, new Random(1));
    mw1 = new WorldScene(2 * IConstants.TILE_SIZE, 2 * IConstants.TILE_SIZE);
  }

  void testExceptions(Tester t) {
    this.initTestConditions();
    
    t.checkConstructorException(
        new IllegalArgumentException("Cannot have more mines than tiles on board!"),
        "MineWorld",
        10, 10, 101 // More mines than possible
      );
    
  }
  
  void testCountMines(Tester t) {
    this.initTestConditions();
    t.checkExpect(new Utils().countMines(this.mtCellList), 0);
    t.checkExpect(new Utils().countMines(this.cellList1), 4);
    t.checkExpect(new Utils().countMines(this.cellList2), 1);
    t.checkExpect(new Utils().countMines(this.cellList4), 1);

  }

  void testAddNeighbor(Tester t) {
    this.initTestConditions();

    this.cell1.addNeighbor(cell2);
    this.cell2.addNeighbor(cell1);
    t.checkExpect(this.cell1.neighbors.get(0), cell2);
    t.checkExpect(this.cell2.neighbors.get(0), cell1);
  }

  void testBuildList(Tester t) {
    this.initTestConditions();
    this.cellList2Test = new Utils().buildList(2, 2, 1, new Random(1));

    t.checkExpect(this.cellList2Test.get(0).neighbors.size(), 3);
    t.checkExpect(this.cellList2Test.get(1).neighbors.size(), 3);
    t.checkExpect(this.cellList2Test.get(3).neighbors.size(), 3);

    //    for (int i = 0; i < this.cellList2Test.size(); i++) {
    //      t.checkExpect(cellList2Test.get(i), this.cellList2.get(i));
    //    }
    //t.checkExpect(this.cellList2Test, this.cellList2);
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
    t.checkExpect(new MineCell(cellList1, false, false).isMine(), true);
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
  
  void testFindCellIndex(Tester t) {
    this.initTestConditions();

    t.checkExpect(new Utils().findCellIndex(50, 3, 3, new Posn(10, 10), 0), 0);
    t.checkExpect(new Utils().findCellIndex(50, 3, 3, new Posn(120, 51), 0), 5);
    t.checkExpect(new Utils().findCellIndex(50, 10, 10, new Posn(340, 499), 0), 96);
  }

  void testDraw(Tester t) {
    this.initTestConditions();

    WorldImage revealedEmpty = new OverlayImage(new TextImage("1", 
        IConstants.TILE_SIZE / 2, IConstants.TEXT_COLOR1), IConstants.FULL_EMPTY_TILE);
    WorldImage hiddenEmpty = new OverlayImage(IConstants.OUTLINE, IConstants.BASE_FILL);
    WorldImage hiddenFlagged = new OverlayImage(new EquilateralTriangleImage(IConstants
        .TILE_SIZE / 2, OutlineMode.SOLID, IConstants.FLAG_COLOR), IConstants.BASE_TILE);

    WorldImage row1 = new BesideImage(new BesideImage(new EmptyImage(), 
        revealedEmpty), revealedEmpty);
    WorldImage row2 = new BesideImage(new BesideImage(new EmptyImage(), 
        hiddenFlagged), hiddenEmpty);

    WorldImage row3 = new BesideImage(new BesideImage(new EmptyImage(), 
        hiddenFlagged), revealedEmpty);
    WorldImage row4 = new BesideImage(new BesideImage(new EmptyImage(), 
        hiddenEmpty), hiddenEmpty);

    twoByTwoWorld.placeImageXY(new AboveImage(new AboveImage(new EmptyImage(), row1), row2),
        IConstants.TILE_SIZE, IConstants.TILE_SIZE);
    t.checkExpect(new Utils().draw(cellList4, 2, 2, 
        new WorldScene(IConstants.TILE_SIZE * 2, 
            IConstants.TILE_SIZE * 2)), this.twoByTwoWorld);

    this.initTestConditions();
    twoByTwoWorld.placeImageXY(new AboveImage(new AboveImage(new EmptyImage(), row3), row4),
        IConstants.TILE_SIZE, IConstants.TILE_SIZE);
    t.checkExpect(new Utils().draw(cellList5, 2, 2, 
        new WorldScene(IConstants.TILE_SIZE * 2, 
            IConstants.TILE_SIZE * 2)), this.twoByTwoWorld);
  }

  void testDrawHelp(Tester t) {
    this.initTestConditions();

    WorldImage revealedEmpty = new OverlayImage(new TextImage("1", 
        IConstants.TILE_SIZE / 2, IConstants.TEXT_COLOR1), IConstants.FULL_EMPTY_TILE);
    WorldImage hiddenEmpty = new OverlayImage(IConstants.OUTLINE, IConstants.BASE_FILL);
    WorldImage emptyRevealedEmpty = IConstants.FULL_EMPTY_TILE;
    WorldImage hiddenFlagged = new OverlayImage(new EquilateralTriangleImage(IConstants
        .TILE_SIZE / 2, OutlineMode.SOLID, IConstants.FLAG_COLOR), IConstants.BASE_TILE);
    WorldImage revealedMine = new OverlayImage(new CircleImage(IConstants.TILE_SIZE / 4, 
        OutlineMode.SOLID, IConstants.MINE_COLOR), IConstants.FULL_EMPTY_TILE);

    t.checkExpect(this.cell1.drawHelp(), hiddenEmpty); 
    t.checkExpect(this.numberedCell.drawHelp(), revealedEmpty);
    t.checkExpect(this.flagCell.drawHelp(), hiddenFlagged);
    t.checkExpect(this.emptyCell.drawHelp(), emptyRevealedEmpty);
    t.checkExpect(this.mineCell.drawHelp(), revealedMine);
  }

  void testMakeScene(Tester t) {
    this.initTestConditions();

    WorldImage revealedEmpty = new OverlayImage(new TextImage("1", 
        IConstants.TILE_SIZE / 2, IConstants.TEXT_COLOR1), IConstants.OUTLINE);
    WorldImage hiddenEmpty = new OverlayImage(IConstants.OUTLINE, IConstants.BASE_FILL);
    WorldImage hiddenFlagged = new OverlayImage(new EquilateralTriangleImage(IConstants
        .TILE_SIZE / 2, OutlineMode.SOLID, IConstants.FLAG_COLOR), IConstants.BASE_TILE);

    WorldImage row1 = new BesideImage(new BesideImage(new EmptyImage(), 
        hiddenEmpty), hiddenEmpty);
    WorldImage row2 = new BesideImage(new BesideImage(new EmptyImage(), 
        hiddenEmpty), hiddenEmpty);

    this.mw1.placeImageXY(new AboveImage(new AboveImage(new EmptyImage(), row1), row2),
        IConstants.TILE_SIZE, IConstants.TILE_SIZE);

    t.checkExpect(init1.makeScene(), this.mw1);
  }

  void testBigBang(Tester t) {
    this.initTestConditions();
    int columns = 30;
    int rows = 16;
    int mines = 99;

    MineWorld world = new MineWorld(columns, rows, mines, new Random(1));
    int worldWidth = IConstants.TILE_SIZE * columns;
    int worldHeight = IConstants.TILE_SIZE * rows;
    double tickRate = .1;
    world.bigBang(worldWidth, worldHeight, tickRate);
  }

  void atestBigBang2(Tester t) {
    this.initTestConditions();
    int columns = 2;
    int rows = 2;
    int mines = 1;

    MineWorld world = new MineWorld(columns, rows, mines, this.cellList4);
    int worldWidth = IConstants.TILE_SIZE * columns;
    int worldHeight = IConstants.TILE_SIZE * rows;
    double tickRate = .1;
    world.bigBang(worldWidth, worldHeight, tickRate);
  }
}









