import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;


// GAME INSTRUCTIONS - should be listed in game, but can be small
// "r" to restart when you win or lose
// "e" to choose easy difficulty in start screen
// "m" to choose medium difficulty in start screen
// "h" to choose hard difficulty in start screen

// Constants class
interface IConstants {

  int TILE_SIZE = 40;
  int Y_PADDING = 1;

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
  String gameState;
  ArrayList<ACell> cellList;

  // Constructor for testing with custom random
  MineWorld(int cols, int rows, int mines, String state, Random rand) {
    if (rows < 1 || cols < 1) {
      throw new IllegalArgumentException("Must have rows and cols greater than 0!");
    }
    if (mines > (cols * rows)) {
      throw new IllegalArgumentException("Cannot have more mines than tiles on board!");
    }
    else {
      this.cols = cols;
      this.rows = rows;
      this.mines = mines;
      this.cellList = new Utils().buildList(cols, rows, mines, rand);
      this.score = 0;
      this.gameState = state;
    }
  }

  // Constructor for testing draw method with a given cell list
  MineWorld(int cols, int rows, int mines, String state, ArrayList<ACell> cellList) {
    if (rows < 1 || cols < 1) {
      throw new IllegalArgumentException("Must have rows and cols greater than 0!");
    }
    if (mines > (cols * rows)) {
      throw new IllegalArgumentException("Cannot have more mines than tiles on board!");
    }
    else {
      this.cols = cols;
      this.rows = rows;
      this.mines = mines;
      this.cellList = cellList;
      this.score = 0;
      this.gameState = state;
    }
  }

  // Constructor for real randomized games
  MineWorld(int cols, int rows, int mines) {
    if (rows < 1 || cols < 1) {
      throw new IllegalArgumentException("Must have rows and cols greater than 0!");
    }
    if (mines > (cols * rows)) {
      throw new IllegalArgumentException("Cannot have more mines than tiles on board!");
    }
    else {
      this.cols = cols;
      this.rows = rows;
      this.mines = mines;
      this.score = 0;
      this.cellList = new Utils().buildList(cols, rows, mines, new Random());
      this.gameState = "start";
    }
  }

  // Overrides WorldScene in World, represents the current state of the world
  public WorldScene makeScene() {
    // Make win scene
    if (this.gameState.equals("win")) {
      WorldScene ws = new WorldScene(IConstants.TILE_SIZE * cols, 
          (IConstants.TILE_SIZE * rows) + IConstants.TILE_SIZE);
      ws.placeImageXY(new TextImage("You Win! (r to restart)", IConstants.TILE_SIZE / 2, 
          IConstants.TEXT_COLOR1), IConstants.TILE_SIZE * cols / 2, 
          ((IConstants.TILE_SIZE * rows) + IConstants.TILE_SIZE) / 2);
      return ws;

    }
    // Make lose scene
    else if (this.gameState.equals("lose")) {
      WorldScene ws = new WorldScene(IConstants.TILE_SIZE * cols, (
          IConstants.TILE_SIZE * rows) + IConstants.TILE_SIZE);
      ws.placeImageXY(new TextImage("You Lose! (r to restart)", IConstants.TILE_SIZE / 2, 
          IConstants.TEXT_COLOR1), IConstants.TILE_SIZE * cols / 2, 
          ((IConstants.TILE_SIZE * rows) + IConstants.TILE_SIZE) / 2);
      return ws;

    }
    // Make start scene
    else if (this.gameState.equals("start")) {
      WorldScene ws = new WorldScene(IConstants.TILE_SIZE * cols, 
          (IConstants.TILE_SIZE * rows) + IConstants.TILE_SIZE);

      int easyMineNum = Math.round((this.rows * this.cols) / 10);
      int mediumMineNum = Math.round((this.rows * this.cols) / 5);
      int hardMineNum = Math.round((this.rows * this.cols) / 3);
      if (easyMineNum == 0) {
        easyMineNum = 1;
      }
      if (mediumMineNum == 0) {
        mediumMineNum = 1;
      }
      WorldImage easyText = new OverlayImage(new TextImage("(e) easy:" 
          + easyMineNum + " mines", 
          IConstants.TILE_SIZE / 3, IConstants.TEXT_COLOR1), 
          new RectangleImage(IConstants.TILE_SIZE * 4, IConstants.TILE_SIZE, 
              OutlineMode.OUTLINE, Color.darkGray));
      WorldImage mediumText = new OverlayImage(new TextImage("(m) medium:" 
          + mediumMineNum + " mines", 
          IConstants.TILE_SIZE / 3, IConstants.TEXT_COLOR1), 
          new RectangleImage(IConstants.TILE_SIZE * 4, IConstants.TILE_SIZE, 
              OutlineMode.OUTLINE, Color.darkGray)); 
      WorldImage hardText = new OverlayImage(new TextImage("(h) hard:" 
          + hardMineNum + " mines", 
          IConstants.TILE_SIZE / 3, IConstants.TEXT_COLOR1), 
          new RectangleImage(IConstants.TILE_SIZE * 4, IConstants.TILE_SIZE, 
              OutlineMode.OUTLINE, Color.darkGray));

      ws.placeImageXY(new AboveImage(new AboveImage(easyText, mediumText), hardText),
          IConstants.TILE_SIZE * cols / 2, // width
          ((IConstants.TILE_SIZE * rows) + IConstants.TILE_SIZE) / 2); // height
      return ws;
    }
    else {
      return new Utils().draw(this.cellList, this.cols, this.rows, this.score,
          new WorldScene(IConstants.TILE_SIZE * cols, (IConstants.TILE_SIZE * rows) 
              + IConstants.TILE_SIZE));
    }
  }

  // Handles mouse inputs
  public void onMouseClicked(Posn pos, String buttonName) { 

    if (this.gameState.equals("")) {
      int cellIndex = new Utils().findCellIndex(IConstants.TILE_SIZE, 
          this.cols, this.rows, pos, 1);

      if (buttonName.equals("LeftButton")) {
        this.cellList.get(cellIndex).floodFill(new ArrayList<ACell>());
        this.score += 1;
      }
      else if (buttonName.equals("RightButton")) {
        this.cellList.get(cellIndex).flipFlag();
      }
    }

    if (new Utils().isGameWon(this.cellList)) {
      this.gameState = "win";
    }
    if (new Utils().isGameLost(this.cellList)) {
      this.gameState = "lose";
    }
  }

  // Handles key presses
  public void onKeyEvent(String key) {

    int easyMineNum = Math.round((this.rows * this.cols) / 10);
    int mediumMineNum = Math.round((this.rows * this.cols) / 5);
    int hardMineNum = Math.round((this.rows * this.cols) / 3);
    if (easyMineNum == 0) {
      easyMineNum = 1;
    }
    if (mediumMineNum == 0) {
      mediumMineNum = 1;
    }

    if ((this.gameState.equals("win") 
        || this.gameState.equals("lose")) 
        && key.equals("r")) {
      this.cellList = new Utils().buildList(cols, rows, mines, new Random());
      this.gameState = "";
      this.score = 0;
    }
    if (this.gameState.equals("start") && key.equals("e")) {
      this.mines = easyMineNum;
      this.gameState = "";
      this.cellList = new Utils().buildList(cols, rows, mines, new Random());
    }
    else if (this.gameState.equals("start") && key.equals("m")) {
      this.mines = mediumMineNum;
      this.gameState = "";
      this.cellList = new Utils().buildList(cols, rows, mines, new Random());
    }
    else if (this.gameState.equals("start") && key.equals("h")) {
      this.mines = hardMineNum;
      this.gameState = "";
      this.cellList = new Utils().buildList(cols, rows, mines, new Random());
    }
  }

  // Handles key inputs, used for testing with given random
  public void onKeyEventForTesting(String key, Random rand) {

    int easyMineNum = Math.round((this.rows * this.cols) / 10);
    int mediumMineNum = Math.round((this.rows * this.cols) / 5);
    int hardMineNum = Math.round((this.rows * this.cols) / 3);
    if (easyMineNum == 0) {
      easyMineNum = 1;
    }
    if (mediumMineNum == 0) {
      mediumMineNum = 1;
    }

    if ((this.gameState.equals("win") 
        || this.gameState.equals("lose")) 
        && key.equals("r")) {
      this.cellList = new Utils().buildList(cols, rows, mines, rand);
      this.gameState = "";
      this.score = 0;
    }
    if (this.gameState.equals("start") && key.equals("e")) {
      this.mines = easyMineNum;
      this.gameState = "";
      this.cellList = new Utils().buildList(cols, rows, mines, rand);
    }
    else if (this.gameState.equals("start") && key.equals("m")) {
      this.mines = mediumMineNum;
      this.gameState = "";
      this.cellList = new Utils().buildList(cols, rows, mines, rand);
    }
    else if (this.gameState.equals("start") && key.equals("h")) {
      this.mines = hardMineNum;
      this.gameState = "";
      this.cellList = new Utils().buildList(cols, rows, mines, rand);
    }

  }
}

// Utils for Arraylist methods and calculation mehods
class Utils {

  // Draws the given cells on the worldScene
  public WorldScene draw(ArrayList<ACell> cellList, int cols, 
      int rows, int score, WorldScene ws) {
    int counter = 0;
    WorldImage scoreRect = new RectangleImage(IConstants.TILE_SIZE * cols, 
        IConstants.TILE_SIZE, OutlineMode.OUTLINE ,IConstants.OUTLINE_COLOR);
    WorldImage scoreImage = new TextImage("Score: " + score, IConstants.TILE_SIZE / 2, 
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
    result = new AboveImage(new OverlayImage(scoreImage, scoreRect), result);

    ws.placeImageXY(result, IConstants.TILE_SIZE * cols / 2, 
        ((IConstants.TILE_SIZE * rows) + IConstants.TILE_SIZE) / 2);

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

  // Calculates the column position of a cell in an ArrayList
  public int getCol(int cols, int rows, int i) {
    if ((i >= (rows * cols)) || i < 0) {
      return -1;
    }
    else {
      return i % cols;
    }
  }

  // Calculates the row position of a cell in an ArrayList
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

  // Finds the index of the cell the user has clicked
  public int findCellIndex(int tileSize, int cols, int rows, Posn pos, int padding) {
    int clickedCol = Math.floorDiv(pos.x, tileSize);
    int clickedRow = Math.floorDiv(pos.y, tileSize) - padding;
    return ((clickedRow * cols) + clickedCol);
  }

  // Checks if all empty cells are revealed
  public boolean isGameWon(ArrayList<ACell> cellList) {
    boolean result = true;
    for (ACell cell: cellList) {
      if (!cell.isMine() && cell.hidden) {
        result = false;
      }
    }
    return result;
  }

  // Checks if any mine cells are revealed
  public boolean isGameLost(ArrayList<ACell> cellList) {
    boolean result = false;
    for (ACell cell: cellList) {
      if (cell.isMine() && !cell.hidden) {
        result = true;
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
  boolean flagged;

  ACell(ArrayList<ACell> neighbors, boolean hidden, boolean flagged) {
    this.neighbors = neighbors;
    this.hidden = hidden;
    this.flagged = flagged;
  }

  // EFFECT: Flips the boolean value of a flag
  public void flipFlag() { 
    this.flagged = !this.flagged; 
  }

  // EFFECT: floodFill behavior for revealing mines
  public abstract void floodFill(ArrayList<ACell> visited);

  // EFFECT: Returns true if this is a mine
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

  // Flood fill behavior, reveals adjacent empty cells
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

  // Flood fill behavior on a mine, reveals the mine
  public void floodFill(ArrayList<ACell> visited) {
    if (!this.flagged) {
      this.hidden = false;
    }

  }

}

// Examples for minesweeper game
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
  ArrayList<ACell> cellList6;
  ArrayList<ACell> cellList6Test;
  ArrayList<ACell> cellList6Test2;
  ArrayList<ACell> cellList7;
  ArrayList<ACell> cellList7Test;
  ArrayList<ACell> cellList8;
  MineWorld init1;
  MineWorld init6;
  MineWorld init7;
  WorldScene mw1;

  MineWorld winWorld1;
  MineWorld winWorld1Test;
  MineWorld loseWorld1;
  MineWorld loseWorld1Test;
  MineWorld startWorld1;

  WorldScene twoByTwoWorld;

  // Initialize fields
  void initTestConditions() {

    twoByTwoWorld = new WorldScene(2 * IConstants.TILE_SIZE, 3 * IConstants.TILE_SIZE);

    flagCell = new EmptyCell(new ArrayList<ACell>(), true, true);
    mineCell = new MineCell(new ArrayList<ACell>(), false, false);
    numberedCell = new EmptyCell(new ArrayList<ACell>(Arrays.asList(mineCell)), 
        false, false);
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
    cellList2.get(3).addNeighbor(cellList2.get(0));
    cellList2.get(3).addNeighbor(cellList2.get(1));


    cell1 = new EmptyCell(mtList, true, false);
    cell2 = new EmptyCell(mtList, true, false);

    cellList3 = new ArrayList<ACell>();

    cellList4 = new ArrayList<ACell>(Arrays.asList(new EmptyCell(new ArrayList<ACell>(), 
        false, false), new EmptyCell(new ArrayList<ACell>(), false, false), 
        new MineCell(new ArrayList<ACell>(), true, true),
        new EmptyCell(new ArrayList<ACell>(), true, false)));

    cellList4.get(0).addNeighbor(cellList4.get(1));
    cellList4.get(0).addNeighbor(cellList4.get(2));
    cellList4.get(0).addNeighbor(cellList4.get(3));

    cellList4.get(1).addNeighbor(cellList4.get(0));
    cellList4.get(1).addNeighbor(cellList4.get(3));
    cellList4.get(1).addNeighbor(cellList4.get(2));

    cellList4.get(2).addNeighbor(cellList4.get(3));
    cellList4.get(2).addNeighbor(cellList4.get(0));
    cellList4.get(2).addNeighbor(cellList4.get(1));

    cellList4.get(3).addNeighbor(cellList4.get(2));
    cellList4.get(3).addNeighbor(cellList4.get(0));
    cellList4.get(3).addNeighbor(cellList4.get(1));

    cellList5 = new ArrayList<ACell>(Arrays.asList(new EmptyCell(new ArrayList<ACell>(), 
        true, true),
        new EmptyCell(new ArrayList<ACell>(), false, false), 
        new EmptyCell(new ArrayList<ACell>(), true, false),
        new MineCell(new ArrayList<ACell>(), true, false)));

    cellList5.get(0).addNeighbor(cellList5.get(1));
    cellList5.get(0).addNeighbor(cellList5.get(2));
    cellList5.get(0).addNeighbor(cellList5.get(3));

    cellList5.get(1).addNeighbor(cellList5.get(0));
    cellList5.get(1).addNeighbor(cellList5.get(3));
    cellList5.get(1).addNeighbor(cellList5.get(2));

    cellList5.get(2).addNeighbor(cellList5.get(3));
    cellList5.get(2).addNeighbor(cellList5.get(0));
    cellList5.get(2).addNeighbor(cellList5.get(1));

    cellList5.get(3).addNeighbor(cellList5.get(2));
    cellList5.get(3).addNeighbor(cellList5.get(0));
    cellList5.get(3).addNeighbor(cellList5.get(1));

    // For testing floodFill method
    this.cellList6 = new ArrayList<ACell>(Arrays
        .asList(new MineCell(new ArrayList<ACell>(), true, true),
            new EmptyCell(new ArrayList<ACell>(), true, false)));

    this.cellList6.get(0).addNeighbor(cellList6.get(1));
    this.cellList6.get(1).addNeighbor(cellList6.get(0));

    this.cellList6Test = new ArrayList<ACell>(Arrays
        .asList(new MineCell(new ArrayList<ACell>(), true, true),
            new EmptyCell(new ArrayList<ACell>(), false, false)));

    this.cellList6Test.get(0).addNeighbor(cellList6Test.get(1));
    this.cellList6Test.get(1).addNeighbor(cellList6Test.get(0));

    this.cellList6Test2 = new ArrayList<ACell>(Arrays
        .asList(new MineCell(new ArrayList<ACell>(), true, true),
            new EmptyCell(new ArrayList<ACell>(), true, false)));

    this.cellList6Test2.get(0).addNeighbor(this.cellList6Test2.get(1));
    this.cellList6Test2.get(1).addNeighbor(this.cellList6Test2.get(0));

    cellList7 = new ArrayList<ACell>(Arrays.asList(new EmptyCell(new ArrayList<ACell>(), 
        true, false),
        new EmptyCell(new ArrayList<ACell>(), true, false), 
        new EmptyCell(new ArrayList<ACell>(), true, false),
        new EmptyCell(new ArrayList<ACell>(), true, false)));

    cellList7.get(0).addNeighbor(cellList7.get(1));
    cellList7.get(0).addNeighbor(cellList7.get(2));
    cellList7.get(0).addNeighbor(cellList7.get(3));

    cellList7.get(1).addNeighbor(cellList7.get(0));
    cellList7.get(1).addNeighbor(cellList7.get(3));
    cellList7.get(1).addNeighbor(cellList7.get(2));

    cellList7.get(2).addNeighbor(cellList7.get(3));
    cellList7.get(2).addNeighbor(cellList7.get(0));
    cellList7.get(2).addNeighbor(cellList7.get(1));

    cellList7.get(3).addNeighbor(cellList7.get(2));
    cellList7.get(3).addNeighbor(cellList7.get(0));
    cellList7.get(3).addNeighbor(cellList7.get(1));

    cellList7Test = new ArrayList<ACell>(Arrays
        .asList(new EmptyCell(new ArrayList<ACell>(), false, false),
            new EmptyCell(new ArrayList<ACell>(), false, false), 
            new EmptyCell(new ArrayList<ACell>(), false, false),
            new EmptyCell(new ArrayList<ACell>(), false, false)));

    cellList7Test.get(0).addNeighbor(cellList7Test.get(1));
    cellList7Test.get(0).addNeighbor(cellList7Test.get(2));
    cellList7Test.get(0).addNeighbor(cellList7Test.get(3));

    cellList7Test.get(1).addNeighbor(cellList7Test.get(0));
    cellList7Test.get(1).addNeighbor(cellList7Test.get(3));
    cellList7Test.get(1).addNeighbor(cellList7Test.get(2));

    cellList7Test.get(2).addNeighbor(cellList7Test.get(3));
    cellList7Test.get(2).addNeighbor(cellList7Test.get(0));
    cellList7Test.get(2).addNeighbor(cellList7Test.get(1));

    cellList7Test.get(3).addNeighbor(cellList7Test.get(2));
    cellList7Test.get(3).addNeighbor(cellList7Test.get(0));
    cellList7Test.get(3).addNeighbor(cellList7Test.get(1));

    cellList8 = new ArrayList<ACell>(Arrays.asList(new EmptyCell(new ArrayList<ACell>(), 
        false, false),
        new EmptyCell(new ArrayList<ACell>(), true, false), 
        new MineCell(new ArrayList<ACell>(), false, false),
        new EmptyCell(new ArrayList<ACell>(), false, false)));

    //
    this.init1 = new MineWorld(2, 2, 1, "", new Random(1));
    this.init6 = new MineWorld(2, 1, 1, "", this.cellList6);
    this.init7 = new MineWorld(2, 1, 1, "", this.cellList7);
    this.mw1 = new WorldScene(2 * IConstants.TILE_SIZE, 3 * IConstants.TILE_SIZE);

    this.winWorld1 = new MineWorld(2, 2, 1, "win", new Random(1));
    this.winWorld1Test = new MineWorld(2, 2, 1, "win", new Random(1));
    this.loseWorld1 = new MineWorld(2, 2, 1, "lose", new Random(1));
    this.loseWorld1Test = new MineWorld(2, 2, 1, "lose", new Random(1));
    this.startWorld1 = new MineWorld(2, 2, 1, "start", new Random(1));

  }

  void testExceptions(Tester t) {
    this.initTestConditions();

    t.checkConstructorException(
        new IllegalArgumentException("Cannot have more mines than tiles on board!"),
        "MineWorld",
        10, 10, 101);

    t.checkConstructorException(
        new IllegalArgumentException("Must have rows and cols greater than 0!"),
        "MineWorld", 10, -1, 2);


  }

  // Test counting mines
  void testCountMines(Tester t) {
    this.initTestConditions();
    t.checkExpect(new Utils().countMines(this.mtCellList), 0);
    t.checkExpect(new Utils().countMines(this.cellList1), 4);
    t.checkExpect(new Utils().countMines(this.cellList2), 1);
    t.checkExpect(new Utils().countMines(this.cellList4), 1);

  }

  // Test addNeighbor
  void testAddNeighbor(Tester t) {
    this.initTestConditions();

    this.cell1.addNeighbor(cell2);
    this.cell2.addNeighbor(cell1);
    this.emptyCell.addNeighbor(flagCell);
    t.checkExpect(this.cell1.neighbors.get(0), cell2);
    t.checkExpect(this.cell2.neighbors.get(0), cell1);
    t.checkExpect(this.emptyCell.neighbors.get(0), flagCell);
  }

  // Test buildList
  void testBuildList(Tester t) {
    this.initTestConditions();
    this.cellList2Test = new Utils().buildList(2, 2, 1, new Random(1));

    t.checkExpect(this.cellList2Test.get(0).neighbors.size(), 3);
    t.checkExpect(this.cellList2Test.get(1).neighbors.size(), 3);
    t.checkExpect(this.cellList2Test.get(3).neighbors.size(), 3);
  }

  // Test getRow
  void testGetRow(Tester t) {
    this.initTestConditions();

    t.checkExpect(new Utils().getRow(2, 2, 0), 0);
    t.checkExpect(new Utils().getRow(2, 2, 1), 0);
    t.checkExpect(new Utils().getRow(2, 2, 2), 1);
    t.checkExpect(new Utils().getRow(2, 2, 3), 1);
  }

  // Test getCol
  void testGetCol(Tester t) {
    this.initTestConditions();

    t.checkExpect(new Utils().getCol(2, 2, 0), 0);
    t.checkExpect(new Utils().getCol(2, 2, 1), 1);
    t.checkExpect(new Utils().getCol(2, 2, 2), 0);
    t.checkExpect(new Utils().getCol(2, 2, 3), 1);

    t.checkExpect(new Utils().getCol(4, 3, 4), 0);
  }

  // Test isMine method
  void testIsMine(Tester t) {
    this.initTestConditions();

    t.checkExpect(this.cell1.isMine(), false);
    t.checkExpect(this.cell2.isMine(), false);
    t.checkExpect(new MineCell(cellList1, false, false).isMine(), true);
  }

  // Test flip flag
  void testFlipFlag(Tester t) {
    this.initTestConditions();

    t.checkExpect(this.cell1.flagged, false);  
    this.cell1.flipFlag();
    t.checkExpect(this.cell1.flagged, true); 

    t.checkExpect(this.cell2.flagged, false);  
    this.cell2.flipFlag();
    t.checkExpect(this.cell2.flagged, true);  
  }

  // Test generation of mine seeds
  void testGenerateMineSeeds(Tester t) {
    this.initTestConditions();

    t.checkExpect(new Utils().generateMineSeeds(new Random(1), 4, 16), this.intList1);
    t.checkExpect(new Utils().generateMineSeeds(new Random(2), 7, 16), this.intList2);
    t.checkExpect(new Utils().generateMineSeeds(new Random(3), 5, 25), this.intList3);
  }

  // Test containsNumber
  void testContainsNumber(Tester t) {
    this.initTestConditions();

    t.checkExpect(new Utils().containsNumber(this.intList1, 1), true);
    t.checkExpect(new Utils().containsNumber(this.intList1, 27), false);
    t.checkExpect(new Utils().containsNumber(this.intList1, 6), true);
  }

  // Test find cellIndex
  void testFindCellIndex(Tester t) {
    this.initTestConditions();

    t.checkExpect(new Utils().findCellIndex(50, 3, 3, new Posn(10, 10), 0), 0);
    t.checkExpect(new Utils().findCellIndex(50, 3, 3, new Posn(120, 51), 0), 5);
    t.checkExpect(new Utils().findCellIndex(50, 10, 10, new Posn(340, 499), 0), 96);
  }

  // Test gameLost method
  void testIsGameLost(Tester t) {
    this.initTestConditions();

    t.checkExpect(new Utils().isGameLost(this.cellList7), false);
    t.checkExpect(new Utils().isGameLost(this.cellList1), false);
    t.checkExpect(new Utils().isGameLost(this.cellList8), true);
  }

  // Test isGameWon method
  void testIsGameWon(Tester t) {
    this.initTestConditions();

    t.checkExpect(new Utils().isGameWon(this.cellList1), false);
    t.checkExpect(new Utils().isGameWon(this.cellList7), false);
    t.checkExpect(new Utils().isGameWon(this.cellList7Test), true);
  }

  // Test drawing of worldScenes
  void testDraw(Tester t) {
    this.initTestConditions();

    WorldImage revealedEmpty = new OverlayImage(new TextImage("1", 
        IConstants.TILE_SIZE / 2, IConstants.TEXT_COLOR1), IConstants.FULL_EMPTY_TILE);
    WorldImage hiddenEmpty = new OverlayImage(IConstants.OUTLINE, IConstants.BASE_FILL);
    WorldImage hiddenFlagged = new OverlayImage(new EquilateralTriangleImage(IConstants
        .TILE_SIZE / 2, OutlineMode.SOLID, IConstants.FLAG_COLOR), IConstants.BASE_TILE);

    WorldImage scoreRect = new RectangleImage(IConstants.TILE_SIZE * 2, 
        IConstants.TILE_SIZE, OutlineMode.OUTLINE ,IConstants.OUTLINE_COLOR);
    WorldImage scoreImage = new TextImage("Score: " + "0", IConstants.TILE_SIZE / 2, 
        IConstants.TEXT_COLOR1);

    WorldImage row1 = new BesideImage(new BesideImage(new EmptyImage(), 
        revealedEmpty), revealedEmpty);

    WorldImage row2 = new BesideImage(new BesideImage(new EmptyImage(), 
        hiddenFlagged), hiddenEmpty);

    WorldImage row3 = new BesideImage(new BesideImage(new EmptyImage(), 
        hiddenFlagged), revealedEmpty);
    WorldImage row4 = new BesideImage(new BesideImage(new EmptyImage(), 
        hiddenEmpty), hiddenEmpty);

    twoByTwoWorld.placeImageXY(new AboveImage(new OverlayImage(scoreImage, scoreRect), 
        new AboveImage(new AboveImage(new EmptyImage(), row1), row2)),
        IConstants.TILE_SIZE, IConstants.TILE_SIZE + IConstants.TILE_SIZE / 2);

    t.checkExpect(new Utils().draw(cellList4, 2, 2, 
        0, new WorldScene(IConstants.TILE_SIZE * 2, 
            IConstants.TILE_SIZE * 3)), this.twoByTwoWorld);

    this.initTestConditions();
    twoByTwoWorld.placeImageXY(new AboveImage(new OverlayImage(scoreImage, scoreRect), 
        new AboveImage(new AboveImage(new EmptyImage(), row3), row4)),
        IConstants.TILE_SIZE, IConstants.TILE_SIZE + IConstants.TILE_SIZE / 2);
    t.checkExpect(new Utils().draw(cellList5, 2, 2, 0, 
        new WorldScene(IConstants.TILE_SIZE * 2, 
            IConstants.TILE_SIZE * 3)), this.twoByTwoWorld);
  }

  // Test floodFill
  void testFloodFill(Tester t) {
    this.initTestConditions();

    // Test flood fill on an empty cell
    this.cellList6.get(1).floodFill(new ArrayList<ACell>());
    t.checkExpect(this.cellList6, this.cellList6Test);

    this.initTestConditions();

    // Test flood fill on a flagged cell that is flagged
    this.cellList6.get(0).floodFill(new ArrayList<ACell>());
    t.checkExpect(this.cellList6, this.cellList6Test2);

    // Test flood fill on empty cells
    this.cellList7.get(0).floodFill(new ArrayList<ACell>());
    t.checkExpect(this.cellList7, this.cellList7Test);
  }

  // testOnMouseClicked
  void testOnMouseClicked(Tester t) { 
    this.initTestConditions();

    // Testing mouse input for flood filling
    this.init6.onMouseClicked(new Posn(IConstants.TILE_SIZE + 
        IConstants.TILE_SIZE / 2, IConstants.TILE_SIZE + IConstants.TILE_SIZE / 2), 
        "LeftButton");
    t.checkExpect(this.init6.cellList, this.cellList6Test);

    this.initTestConditions();

    // Testing mouse input for flood filling
    this.init6.onMouseClicked(new Posn(IConstants.TILE_SIZE / 2, 
        IConstants.TILE_SIZE + IConstants.TILE_SIZE / 2),
        "LeftButton");
    t.checkExpect(this.init6.cellList, this.cellList6Test2);

    // Testing mouse input for flood filling multiple cells
    this.init7.onMouseClicked(new Posn(IConstants.TILE_SIZE / 2, 
        IConstants.TILE_SIZE + IConstants.TILE_SIZE / 2),
        "LeftButton");
    t.checkExpect(this.init7.cellList, this.cellList7Test);

    // Testing mouse when game is won, nothing should happen
    this.winWorld1.onMouseClicked(new Posn(1, 1), "LeftButton");
    t.checkExpect(this.winWorld1, this.winWorld1Test);

    // Testing mouse when game is lost
    this.loseWorld1.onMouseClicked(new Posn(1, 1), "LeftButton");
    t.checkExpect(this.loseWorld1, this.loseWorld1Test);
  }

  // Test onKey for different world states
  void testOnKey(Tester t) {

    this.initTestConditions();
    this.startWorld1.onKeyEventForTesting("e", new Random(1));
    t.checkExpect(this.startWorld1, this.init1);

    this.initTestConditions();
    this.startWorld1.onKeyEventForTesting("m", new Random(1));
    t.checkExpect(this.startWorld1, this.init1);

    this.initTestConditions();
    this.startWorld1.onKeyEventForTesting("h", new Random(1));
    t.checkExpect(this.startWorld1, this.init1);

    this.initTestConditions();
    this.loseWorld1.onKeyEventForTesting("r", new Random(1));
    t.checkExpect(this.loseWorld1, this.init1);

    this.initTestConditions();
    this.winWorld1.onKeyEventForTesting("r", new Random(1));
    t.checkExpect(this.winWorld1, this.init1);
  }

  // test drawing of single cell
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

  // Test makeScene
  void testMakeScene(Tester t) {
    this.initTestConditions();

    WorldImage hiddenEmpty = new OverlayImage(IConstants.OUTLINE, IConstants.BASE_FILL);
    WorldImage scoreRect = new RectangleImage(IConstants.TILE_SIZE * 2, 
        IConstants.TILE_SIZE, OutlineMode.OUTLINE ,IConstants.OUTLINE_COLOR);
    WorldImage scoreImage = new TextImage("Score: " + "0", IConstants.TILE_SIZE / 2, 
        IConstants.TEXT_COLOR1);
    WorldImage row1 = new BesideImage(new BesideImage(new EmptyImage(), 
        hiddenEmpty), hiddenEmpty);
    WorldImage row2 = new BesideImage(new BesideImage(new EmptyImage(), 
        hiddenEmpty), hiddenEmpty);

    this.mw1.placeImageXY(new AboveImage(new OverlayImage(scoreImage, scoreRect), 
        new AboveImage(new AboveImage(new EmptyImage(), row1), row2)),
        IConstants.TILE_SIZE, IConstants.TILE_SIZE + IConstants.TILE_SIZE / 2);

    t.checkExpect(init1.makeScene(), this.mw1);

    // Test win and lose screens
    this.initTestConditions();
    WorldScene winScreen = new WorldScene(IConstants.TILE_SIZE * 2, 
        (IConstants.TILE_SIZE * 2) + IConstants.TILE_SIZE);
    winScreen.placeImageXY(new TextImage("You Win! (r to restart)", 
        IConstants.TILE_SIZE / 2, IConstants.TEXT_COLOR1), IConstants.TILE_SIZE * 2 / 2, 
        ((IConstants.TILE_SIZE * 2) + IConstants.TILE_SIZE) / 2);
    WorldScene loseScreen = new WorldScene(IConstants.TILE_SIZE * 2, 
        (IConstants.TILE_SIZE * 2) + IConstants.TILE_SIZE);
    loseScreen.placeImageXY(new TextImage("You Lose! (r to restart)", 
        IConstants.TILE_SIZE / 2, 
        IConstants.TEXT_COLOR1), IConstants.TILE_SIZE * 2 / 2, 
        ((IConstants.TILE_SIZE * 2) + IConstants.TILE_SIZE) / 2);

    t.checkExpect(this.winWorld1.makeScene(), winScreen);
    t.checkExpect(this.loseWorld1.makeScene(), loseScreen);

    // test start screen
    WorldScene startScreen = new WorldScene(IConstants.TILE_SIZE * 2, 
        (IConstants.TILE_SIZE * 2) + IConstants.TILE_SIZE);
    WorldImage easyText = new OverlayImage(new TextImage("(e) easy:" + 1 + " mines", 
        IConstants.TILE_SIZE / 3, IConstants.TEXT_COLOR1), 
        new RectangleImage(IConstants.TILE_SIZE * 4, IConstants.TILE_SIZE, 
            OutlineMode.OUTLINE, Color.darkGray));
    WorldImage mediumText = new OverlayImage(new TextImage("(m) medium:" + 1 + " mines", 
        IConstants.TILE_SIZE / 3, IConstants.TEXT_COLOR1), 
        new RectangleImage(IConstants.TILE_SIZE * 4, IConstants.TILE_SIZE, 
            OutlineMode.OUTLINE, Color.darkGray)); 
    WorldImage hardText = new OverlayImage(new TextImage("(h) hard:" + 1 + " mines", 
        IConstants.TILE_SIZE / 3, IConstants.TEXT_COLOR1), 
        new RectangleImage(IConstants.TILE_SIZE * 4, IConstants.TILE_SIZE, 
            OutlineMode.OUTLINE, Color.darkGray));

    startScreen.placeImageXY(new AboveImage(new AboveImage(easyText, 
        mediumText), hardText),
        IConstants.TILE_SIZE , // width
        ((IConstants.TILE_SIZE * 2) + IConstants.TILE_SIZE) / 2); // height

    this.initTestConditions();
    t.checkExpect(this.startWorld1.makeScene(), startScreen);
  }

  // Big bang with given random
  void atestBigBang(Tester t) {
    this.initTestConditions();
    int columns = 30;
    int rows = 16;
    int mines = 99;

    MineWorld world = new MineWorld(columns, rows, mines, "", new Random(1));
    int worldWidth = IConstants.TILE_SIZE * columns;
    int worldHeight = IConstants.TILE_SIZE * rows + IConstants.TILE_SIZE;
    double tickRate = .1;
    world.bigBang(worldWidth, worldHeight, tickRate);
  }

  // Big bang with given list
  void atestBigBang2(Tester t) {
    this.initTestConditions();
    int columns = 2;
    int rows = 2;
    int mines = 1;

    MineWorld world = new MineWorld(columns, rows, mines, "", this.cellList4);
    int worldWidth = IConstants.TILE_SIZE * columns;
    int worldHeight = IConstants.TILE_SIZE * rows + IConstants.TILE_SIZE;
    double tickRate = .1;
    world.bigBang(worldWidth, worldHeight, tickRate);
  }

  // Random big bang
  void testRealBigBang(Tester t) {
    this.initTestConditions();
    int columns = 30;
    int rows = 16;
    int mines = 99;

    MineWorld world = new MineWorld(columns, rows, mines);
    int worldWidth = IConstants.TILE_SIZE * columns;
    int worldHeight = IConstants.TILE_SIZE * rows + IConstants.TILE_SIZE;
    double tickRate = .1;
    world.bigBang(worldWidth, worldHeight, tickRate);
  }
}








