import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.ArrayDeque;

import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;

interface IConstants {
  int TILE_SIZE = 50;


}

class LightEmAll extends World {
  // a list of columns of GamePieces,
  // i.e., represents the board in column-major order
  ArrayList<ArrayList<GamePiece>> board;
  // a list of all nodes
  ArrayList<GamePiece> nodes;
  // a list of edges of the minimum spanning tree
  ArrayList<Edge> mst;
  // the width and height of the board
  int width;
  int height;
  // the current location of the power station,
  // as well as its effective radius
  int powerRow;
  int powerCol;
  int radius;
  String gameState;

  // Constructor for real "random" games
  LightEmAll(ArrayList<ArrayList<GamePiece>> board,  ArrayList<GamePiece> nodes, ArrayList<Edge> mst,
      int width, int height, int powerRow, int powerCol, int radius) {
    this.board = board;
    this.nodes = nodes;
    this.mst = mst;
    this.width = width;
    this.height = height;
    this.powerRow = powerRow;
    this.powerCol = powerCol;
    this.radius = radius;
    this.gameState = "";
    
    
    for (int i = 0; i < this.board.size(); i++) {
      for (int j = 0; j < this.board.get(i).size(); j++) {
        this.board.get(i).get(j).randomRotatePiece(new Random());
      }
    }
  }
  
  // Constructor for testing different states with given random
  LightEmAll(ArrayList<ArrayList<GamePiece>> board,  ArrayList<GamePiece> nodes, ArrayList<Edge> mst,
      int width, int height, int powerRow, int powerCol, int radius, String gameState, Random rand) {
    this.board = board;
    this.nodes = nodes;
    this.mst = mst;
    this.width = width;
    this.height = height;
    this.powerRow = powerRow;
    this.powerCol = powerCol;
    this.radius = radius;
    this.gameState = gameState;
    
    
    for (int i = 0; i < this.board.size(); i++) {
      for (int j = 0; j < this.board.get(i).size(); j++) {
        this.board.get(i).get(j).randomRotatePiece(rand);
      }
    }
  }
  
  // Constructor for testing different states without random
  LightEmAll(ArrayList<ArrayList<GamePiece>> board,  ArrayList<GamePiece> nodes, ArrayList<Edge> mst,
      int width, int height, int powerRow, int powerCol, int radius, String gameState) {
    this.board = board;
    this.nodes = nodes;
    this.mst = mst;
    this.width = width;
    this.height = height;
    this.powerRow = powerRow;
    this.powerCol = powerCol;
    this.radius = radius;
    this.gameState = gameState;
    
  }

  // Builds the current WorldScene
  public WorldScene makeScene() {
    // Power connected wires using BFS
    for (int i = 0; i < this.board.size(); i++) {
      for (int j = 0; j < this.board.get(i).size(); j++) {
        if (new ArrayListUtils()
            .bfs(board, this.board.get(powerCol).get(powerRow), 
                this.board.get(i).get(j))) {
          this.board.get(i).get(j).powered = true;
        }
        else {
          this.board.get(i).get(j).powered = false;
        }
      }
    }
    
    // Check if all lines are powered using BFS
    int currentLit = 0;
    for (int i = 0; i < this.board.size(); i++) {
      for (int j = 0; j < this.board.get(i).size(); j++) {
        if (new ArrayListUtils()
            .bfs(board, this.board.get(powerCol).get(powerRow), 
                this.board.get(i).get(j))) {
          currentLit += 1;
        }
        else {
          currentLit -= 1;
        }
      }
    }
    if (currentLit == width * height) {
      this.gameState = "win";
    }
    
    // Draw the scene
    if (this.gameState.equals("win")) {
      WorldScene winScreen = new WorldScene(width * IConstants.TILE_SIZE, height * IConstants.TILE_SIZE + IConstants.TILE_SIZE);
      winScreen.placeImageXY(new TextImage("You Win!", 
          IConstants.TILE_SIZE / 2, Color.black), 
          IConstants.TILE_SIZE * width / 2, 
          ((IConstants.TILE_SIZE * height) / 2));
      return winScreen;
    }
    else {
      return new ArrayListUtils().draw(board, width, height, powerRow, powerCol, radius, 
          new WorldScene(width * IConstants.TILE_SIZE, height * IConstants.TILE_SIZE + IConstants.TILE_SIZE));
    }
    
  }
  

  public void onKeyEvent(String key) {
    if (key.equals("left") && !(this.powerCol == 0)) {
      if (this.board.get(powerCol).get(powerRow)
          .leftConnects(this.board.get(powerCol - 1).get(powerRow))) {
        this.powerCol -= 1;
      }
    }
    else if (key.equals("right") && !(this.powerCol == this.width - 1)) {
      if (this.board.get(powerCol).get(powerRow)
          .rightConnects(this.board.get(powerCol + 1).get(powerRow))) {
        this.powerCol += 1;
      }
    }
    else if (key.equals("up") && !(this.powerRow == 0)) {
      if (this.board.get(powerCol).get(powerRow)
          .topConnects(this.board.get(powerCol).get(powerRow - 1))) {
        this.powerRow -= 1;
      }
    }
    else if (key.equals("down") && !(this.powerRow == this.height - 1)) {
      if (this.board.get(powerCol).get(powerRow)
          .bottomConnects(this.board.get(powerCol).get(powerRow + 1))) {
        this.powerRow += 1;
      }
    }
  }
  
  // Handles mouse inputs
  public void onMouseClicked(Posn pos, String buttonName) { 
    Posn cellIndex = new Utils().findMousePos(IConstants.TILE_SIZE, 
        this.width, this.height, pos);
    
    // Check for left mouse input and if that the player clicks on the board
    if (buttonName.equals("LeftButton") && cellIndex.y >= 0 && cellIndex.x >= 0) {
      this.board.get(cellIndex.x).get(cellIndex.y).rotatePiece();
    }
  }
  
}

class GamePiece {
  // in logical coordinates, with the origin
  // at the top-left corner of the screen
  int row;
  int col;
  // whether this GamePiece is connected to the
  // adjacent left, right, top, or bottom pieces
  boolean left;
  boolean right;
  boolean top;
  boolean bottom;
  boolean powered;

  GamePiece (int row, int col, boolean left, boolean right, boolean top, 
      boolean bottom, boolean powered){
    this.row = row;
    this.col = col;
    this.left = left;
    this.right = right;
    this.top = top;
    this.bottom = bottom;
    this.powered = powered;
  }

  // Checks if given piece can connect to the left game piece
  public boolean leftConnects(GamePiece other) {
    return other.right && this.left;
  }
  // Checks if given piece can connect to the right game piece
  public boolean rightConnects(GamePiece other) {
    return other.left && this.right;
  }
  // Checks if given piece can connect to the top game piece
  public boolean topConnects(GamePiece other) {
    return other.bottom && this.top;
  }
  // Checks if given piece can connect to the bottom game piece
  public boolean bottomConnects(GamePiece other) {
    return other.top && this.bottom;
  }

  WorldImage tileImage(int size, int wireWidth, boolean hasPowerStation) {

    Color wireColor = Color.gray;
    if (powered || hasPowerStation) {
      wireColor = Color.yellow;
    }
    
    WorldImage image = new OverlayImage(
        new RectangleImage(wireWidth, wireWidth, OutlineMode.SOLID, wireColor),
        new RectangleImage(size, size, OutlineMode.SOLID, Color.DARK_GRAY));
    WorldImage vWire = new RectangleImage(wireWidth, (size + 1) / 2, OutlineMode.SOLID, wireColor);
    WorldImage hWire = new RectangleImage((size + 1) / 2, wireWidth, OutlineMode.SOLID, wireColor);

    if (this.top) image = new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.TOP, vWire, 0, 0, image);
    if (this.right) image = new OverlayOffsetAlign(AlignModeX.RIGHT, AlignModeY.MIDDLE, hWire, 0, 0, image);
    if (this.bottom) image = new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.BOTTOM, vWire, 0, 0, image);
    if (this.left) image = new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.MIDDLE, hWire, 0, 0, image);
    if (hasPowerStation) {
      image = new OverlayImage(
          new OverlayImage(
              new StarImage(size / 3, 7, OutlineMode.OUTLINE, new Color(255, 128, 0)),
              new StarImage(size / 3, 7, OutlineMode.SOLID, new Color(0, 255, 255))),
          image);
    }
    image = new OverlayImage(new RectangleImage(size, size, OutlineMode.OUTLINE, Color.BLACK), image);
    return image;
  }

  public void rotatePiece() {
    boolean newLeft = false;
    boolean newTop = false;
    boolean newRight = false;
    boolean newBottom = false;
    if (this.left) {
      newTop = true;
    }
    if (this.top) {
      newRight = true;
    }
    if (this.right) {
      newBottom = true;
    }
    if (this.bottom) {
      newLeft = true;
    }
    this.left = newLeft;
    this.right = newRight;
    this.bottom = newBottom;
    this.top = newTop;
  }
  
  public void randomRotatePiece(Random rand) {
    int numRotations = rand.nextInt(4);
    
    for (int i = 0; i <= numRotations; i++) {
      this.rotatePiece();
    }
  }

}

class Edge {
  // For part 2
}

class ArrayListUtils {

  // Draws the board of GamePieces
  public WorldScene draw(ArrayList<ArrayList<GamePiece>> board, int width, 
      int height, int powerRow, int powerCol, int radius, WorldScene ws) {
    WorldImage result = new EmptyImage();
    WorldImage colImage = new EmptyImage();
    WorldImage scoreBar = new RectangleImage(width * IConstants.TILE_SIZE, 
        IConstants.TILE_SIZE, OutlineMode.SOLID, new Color(255, 239, 166));

    for (int i = 0; i < board.size(); i++) {
      for (int j = 0; j < board.get(i).size(); j++) {
        if (powerCol == i && powerRow == j) {
          colImage = new AboveImage(colImage, board.get(i).get(j)
              .tileImage(IConstants.TILE_SIZE, 5, true));
        }
        else {
          colImage = new AboveImage(colImage, board.get(i).get(j)
              .tileImage(IConstants.TILE_SIZE, 5, false));
        }
      }
      result = new BesideImage(result, colImage);
      colImage = new EmptyImage();
    } 

    result = new AboveImage(scoreBar, result);
    ws.placeImageXY(result, IConstants.TILE_SIZE * width / 2, ((IConstants.TILE_SIZE * height)
        + IConstants.TILE_SIZE) / 2);
    return ws;
  }
  
  boolean bfs(ArrayList<ArrayList<GamePiece>> board, GamePiece from, GamePiece to) {
    ArrayDeque<GamePiece> alreadySeen = new ArrayDeque<GamePiece>();
    ArrayDeque<GamePiece> worklist = new ArrayDeque<GamePiece>();
   
    // Initialize the worklist with the from vertex
    worklist.addFirst(from);
    // As long as the worklist isn't empty...
    while (!worklist.isEmpty()) {
      GamePiece next = worklist.removeLast();
      if (next.equals(to)) {
        return true; // Success!
      }
      else if (alreadySeen.contains(next)) {
        // do nothing: we've already seen this one
      }
      else {
        // add all the neighbors of next to the worklist for further processing
        for (GamePiece gp :  new ArrayListUtils().getConnectedNeighbors(board, next)) {
          worklist.addLast(gp);
          gp.powered = true;
        }
        // add next to alreadySeen, since we're done with it
        alreadySeen.addFirst(next);
      }
    }
    // We haven't found the to vertex, and there are no more to try
    return false;
  }


  public ArrayList<GamePiece> getConnectedNeighbors(ArrayList<ArrayList<GamePiece>> board, GamePiece from) {
    ArrayList<GamePiece> result = new ArrayList<GamePiece>();

    if (from.col > 0) {
      if (from.leftConnects(board.get(from.col - 1).get(from.row))) {
        result.add(board.get(from.col - 1).get(from.row));
      }
    }
    
    if (from.col < board.size() - 1) {

      if (from.rightConnects(board.get(from.col + 1).get(from.row))) {

        result.add(board.get(from.col + 1).get(from.row));
      }
    }
    if (from.row > 0) {
      if (from.topConnects(board.get(from.col).get(from.row - 1))) {
        result.add(board.get(from.col).get(from.row - 1));

      }
    }
    if (from.row < board.get(0).size() - 1) {
      if (from.bottomConnects(board.get(from.col).get(from.row + 1))) {
        result.add(board.get(from.col).get(from.row + 1));
      }
    }
    return result;
    
  }

}

// Utility methods
class Utils {
  public Posn findMousePos(int tileSize, int width, int height, Posn pos) {
    int clickedCol = Math.floorDiv(pos.x, tileSize);
    int clickedRow = Math.floorDiv(pos.y, tileSize) - 1;
    
    return new Posn(clickedCol, clickedRow);
  }
  
}

class ExamplesLightEmAll {
  WorldScene fourScene;
  WorldScene sixScene;
  
  ArrayList<ArrayList<GamePiece>> horizontalBoard;
  ArrayList<ArrayList<GamePiece>> verticalBoard;
  
  ArrayList<ArrayList<GamePiece>> fourBoard;
  ArrayList<ArrayList<GamePiece>> fourBoardRand;
  ArrayList<ArrayList<GamePiece>> sixBoard;
  
  World fourWorld;
  World fourWorldRand;
  World fourWorldWin;
  
  GamePiece gp1;
  GamePiece gp2;
  GamePiece gp3;
  GamePiece gp4;
  GamePiece gp5;
  GamePiece gp6;
  
  // For testing draw on a 2x2 board
  GamePiece lWire;
  GamePiece bWire;
  GamePiece rtWire;
  GamePiece rbWire;
  
  // To be randomized and drawn on a 2x2 board
  GamePiece lWireR;
  GamePiece bWireR;
  GamePiece rtWireR;
  GamePiece rbWireR;
  
  // For testing draw on a 2x3 board
  GamePiece lWire1ForSix;
  GamePiece lWire2ForSix;
  GamePiece rtWireForSix;
  GamePiece rbWireForSix;  
  GamePiece lbWireForSix;
  GamePiece tWireForSix;
  
  GamePiece testPieceL;
  GamePiece testPieceR;
  
  void init() {

    testPieceL = new GamePiece(1, 1, true, false, false, false, false); 
    testPieceR = new GamePiece(0, 1, false, true, true, false, false);
    
    // For testing left, right, top, bottom connects
    gp1 = new GamePiece(1, 0, true, false, false, false, false);
    gp2 = new GamePiece(8, 4, false, true, false, false, false);
    gp3 = new GamePiece(1, 1, false, false, true, false, false);
    gp4 = new GamePiece(2, 7, false, false, false, true, false);
    gp5 = new GamePiece(2, 7, true, true, true, true, false);
    gp6 = new GamePiece(0, 0, true, false, true, true, false);

    // For testing draw on a 2x2 board
    lWire = new GamePiece(1, 1, true, false, false, false, true);
    bWire = new GamePiece(0, 1, false, false, false, true, false);
    rtWire = new GamePiece(1, 0, false, true, true, false, true);
    rbWire = new GamePiece(0, 0, false, true, false, true, true);
    
    this.fourBoard = new ArrayList<ArrayList<GamePiece>>();
    fourBoard.add(new ArrayList<GamePiece>());
    fourBoard.add(new ArrayList<GamePiece>());
    fourBoard.get(0).add(this.rbWire);
    fourBoard.get(0).add(this.rtWire);
    fourBoard.get(1).add(this.bWire);
    fourBoard.get(1).add(this.lWire);

    this.fourWorld = new LightEmAll(fourBoard, 
        new ArrayList<GamePiece>(), new ArrayList<Edge>(), 2, 2, 1, 1, 5, "");
    
    this.fourWorldWin = new LightEmAll(fourBoard, 
        new ArrayList<GamePiece>(), new ArrayList<Edge>(), 2, 2, 1, 1, 5, "win");
    
    // To be randomized and drawn on a 2x2 board
    lWireR = new GamePiece(1, 1, true, false, false, false, false);
    bWireR = new GamePiece(1, 0, true, false, false, false, false);
    rtWireR = new GamePiece(0, 1, false, true, true, false, false);
    rbWireR = new GamePiece(0, 0, false, true, false, true, false);
    
    this.fourBoardRand = new ArrayList<ArrayList<GamePiece>>();
    fourBoardRand.add(new ArrayList<GamePiece>());
    fourBoardRand.add(new ArrayList<GamePiece>());
    fourBoardRand.get(0).add(this.rbWireR);
    fourBoardRand.get(0).add(this.rtWireR);
    fourBoardRand.get(1).add(this.bWireR);
    fourBoardRand.get(1).add(this.lWireR);
    
    this.fourWorldRand = new LightEmAll(fourBoardRand, 
        new ArrayList<GamePiece>(), new ArrayList<Edge>(), 2, 2, 1, 1, 5, "", new Random(1));
    
    // For testing draw on a 2x2 board
    lWire1ForSix = new GamePiece(1, 1, true, false, false, false, false);
    lWire2ForSix = new GamePiece(1, 0, true, false, false, false, false);
    rtWireForSix = new GamePiece(0, 1, false, true, true, false, false);
    rbWireForSix = new GamePiece(0, 0, false, true, false, true, false);
    lbWireForSix = new GamePiece(2, 0, true, false, false, true, false);
    tWireForSix = new GamePiece(2, 1, false, false, true, false, false);
    
    this.sixBoard = new ArrayList<ArrayList<GamePiece>>();
    sixBoard.add(new ArrayList<GamePiece>());
    sixBoard.add(new ArrayList<GamePiece>());
    sixBoard.add(new ArrayList<GamePiece>());
    sixBoard.get(0).add(this.rbWireForSix);
    sixBoard.get(0).add(this.rtWireForSix);
    sixBoard.get(1).add(this.lWire2ForSix);
    sixBoard.get(1).add(this.lWire1ForSix);
    sixBoard.get(2).add(this.lbWireForSix);
    sixBoard.get(2).add(this.tWireForSix);

    
    horizontalBoard = new ArrayList<ArrayList<GamePiece>>();
    // generation of horizontal wire board
    for (int col = 0; col < 8; col++) {
      horizontalBoard.add(new ArrayList<GamePiece>());
      for (int row = 0; row < 9; row++) {
        if (row == 0) {
          horizontalBoard.get(col).add(new GamePiece(row, col, false, false, false, true, false));
        }
        else if (row == 8){
          horizontalBoard.get(col).add(new GamePiece(row, col, false, false, true, false, false));
        }
        else if (col == 0 && row == 4) {
          horizontalBoard.get(col).add(new GamePiece(row, col, false, true, true, true, false));
        }
        else if (col == 7 && row == 4) {
          horizontalBoard.get(col).add(new GamePiece(row, col, true, false, true, true, false));
        }
        else if (row == 4) {
          horizontalBoard.get(col).add(new GamePiece(row, col, true, true, true, true, false));
        }
        else {
          horizontalBoard.get(col).add(new GamePiece(row, col, false, false, true, true, false));
        }
      }
    }

    verticalBoard = new ArrayList<ArrayList<GamePiece>>();
    // generation of horizontal wire board
    for (int col = 0; col < 8; col++) {
      verticalBoard.add(new ArrayList<GamePiece>());
      for (int row = 0; row < 9; row++) {
        if (col == 0) {
          verticalBoard.get(col).add(new GamePiece(row, col, false, true, false, false, false));
        }
        else if (col == 7){
          verticalBoard.get(col).add(new GamePiece(row, col, true, false, false, false, false));
        }
        else if (row == 0 && col == 4) {
          verticalBoard.get(col).add(new GamePiece(row, col, true, true, false, true, false));
        }
        else if (row == 8 && col == 4) {
          verticalBoard.get(col).add(new GamePiece(row, col, true, true, true, false, false));
        }
        else if (col == 4) {
          verticalBoard.get(col).add(new GamePiece(row, col, true, true, true, true, false));
        }
        else {
          verticalBoard.get(col).add(new GamePiece(row, col, true, true, false, false, false));
        }
      }
    }
    
    fourScene = new WorldScene(2 * IConstants.TILE_SIZE, (IConstants.TILE_SIZE * 2) + IConstants.TILE_SIZE);
    sixScene = new WorldScene(3 * IConstants.TILE_SIZE, (IConstants.TILE_SIZE * 2) + IConstants.TILE_SIZE);
  }

  void testLeftConnects(Tester t) {
    this.init();
    t.checkExpect(gp1.leftConnects(gp2), true);
    t.checkExpect(gp2.leftConnects(gp1), false);
    t.checkExpect(gp1.leftConnects(gp3), false);
    t.checkExpect(gp5.leftConnects(gp2), true);
    
    t.checkExpect(this.testPieceL.leftConnects(this.testPieceR), true);
  }

  void testRightConnects(Tester t) {
    this.init();
    t.checkExpect(gp2.rightConnects(gp1), true);
    t.checkExpect(gp2.rightConnects(gp5), true);
    t.checkExpect(gp1.rightConnects(gp2), false);
    t.checkExpect(gp3.rightConnects(gp4), false);
  }

  void testTopConnects(Tester t) {
    this.init();
    t.checkExpect(gp3.topConnects(gp4), true);
    t.checkExpect(gp3.topConnects(gp5), true);
    t.checkExpect(gp4.topConnects(gp3), false);
    t.checkExpect(gp1.topConnects(gp4), false);
    
    t.checkExpect(this.rtWire.topConnects(this.rbWire), true);
  }

  void testBottomConnects(Tester t) {
    this.init();
    t.checkExpect(gp4.bottomConnects(gp3), true);
    t.checkExpect(gp4.bottomConnects(gp5), true);
    t.checkExpect(gp4.bottomConnects(gp1), false);
    t.checkExpect(gp3.bottomConnects(gp4), false);
  }

  void testRotatePiece(Tester t) {
    this.init();

    this.gp1.rotatePiece();
    t.checkExpect(this.gp1, new GamePiece(1, 0, false, false, true, false, false));
    this.gp1.rotatePiece();
    t.checkExpect(this.gp1, new GamePiece(1, 0, false, true, false, false, false));
    this.gp1.rotatePiece();
    t.checkExpect(this.gp1, new GamePiece(1, 0, false, false, false, true, false));

    this.gp6.rotatePiece();
    t.checkExpect(this.gp6, new GamePiece(0, 0, true, true, true, false, false));
  }

  void testfindMousePos(Tester t) {
    this.init();
    
    t.checkExpect(new Utils().findMousePos(50, 8, 9, new Posn(120, 30)), new Posn(2, -1));
    t.checkExpect(new Utils().findMousePos(40, 10, 9, new Posn(125, 30)), new Posn(3, -1));
    t.checkExpect(new Utils().findMousePos(40, 10, 9, new Posn(325, 50)), new Posn(8, 0));
  }
  
  void testRandomRotatePiece(Tester t) {
    this.init();
    this.gp1.randomRotatePiece(new Random(1));
    t.checkExpect(this.gp1, new GamePiece(1, 0, false, false, false, true, false));
    this.gp2.randomRotatePiece(new Random(1));
    t.checkExpect(this.gp2, new GamePiece(8, 4, false, false, true, false, false));
    this.gp3.randomRotatePiece(new Random(1));
    t.checkExpect(this.gp3, new GamePiece(1, 1, true, false, false, false, false));
  }

  void testDraw(Tester t) {
    this.init();
    WorldImage col1 = new AboveImage(this.rbWire.tileImage(IConstants.TILE_SIZE, 5, false), 
        this.rtWire.tileImage(IConstants.TILE_SIZE, 5, false));
    WorldImage col2 = new AboveImage(this.bWire.tileImage(IConstants.TILE_SIZE, 5, false), 
        this.lWire.tileImage(IConstants.TILE_SIZE, 5, true));
    WorldImage mergedCol = new BesideImage(col1, col2);
    WorldImage board = new AboveImage(new RectangleImage(IConstants.TILE_SIZE * 2, 
        IConstants.TILE_SIZE, OutlineMode.SOLID, new Color(255, 239, 166)), mergedCol);
    
    this.fourScene.placeImageXY(board, IConstants.TILE_SIZE, ((IConstants.TILE_SIZE * 2)
        + IConstants.TILE_SIZE) / 2);

    t.checkExpect(new ArrayListUtils().draw(this.fourBoard, 2, 2, 1, 1, 5, 
        new WorldScene(2 * IConstants.TILE_SIZE, IConstants.TILE_SIZE * 3)), this.fourScene);
    
    this.init();
    WorldImage col1a = new AboveImage(this.rbWireForSix.tileImage(IConstants.TILE_SIZE, 5, false), 
        this.rtWireForSix.tileImage(IConstants.TILE_SIZE, 5, false));
    WorldImage col2a = new AboveImage(this.lWire2ForSix.tileImage(IConstants.TILE_SIZE, 5, false), 
        this.lWire1ForSix.tileImage(IConstants.TILE_SIZE, 5, true));
    WorldImage col3 = new AboveImage(this.lbWireForSix.tileImage(IConstants.TILE_SIZE, 5, false), 
        this.tWireForSix.tileImage(IConstants.TILE_SIZE, 5, false)); 
    WorldImage newMergedCol1 = new BesideImage(col1a, col2a);
    WorldImage mergedCol2 = new BesideImage(newMergedCol1, col3);
    WorldImage board2 = new AboveImage(new RectangleImage(IConstants.TILE_SIZE * 3, 
        IConstants.TILE_SIZE, OutlineMode.SOLID, new Color(255, 239, 166)), mergedCol2);
    
    this.sixScene.placeImageXY(board2, (3 * IConstants.TILE_SIZE) / 2, ((IConstants.TILE_SIZE * 2)
        + IConstants.TILE_SIZE) / 2);
    
    t.checkExpect(new ArrayListUtils().draw(this.sixBoard, 3, 2, 1, 1, 5, 
        new WorldScene(3 * IConstants.TILE_SIZE, IConstants.TILE_SIZE * 3)), this.sixScene);
    
    
  }
  
  void testBfs(Tester t) {
    this.init(); 
    
    t.checkExpect(new ArrayListUtils().bfs(this.fourBoard, this.lWire, this.bWire), false); 
    t.checkExpect(new ArrayListUtils().bfs(this.fourBoard, this.lWire, this.rbWire), true);
    t.checkExpect(new ArrayListUtils().bfs(this.fourBoard, this.lWire, this.rtWire), true);
  
  }
  
  void testGetConnectedNeighbors(Tester t) {
    this.init();
    
    t.checkExpect(new ArrayListUtils().getConnectedNeighbors(this.fourBoard, lWire), 
        new ArrayList<GamePiece>(Arrays.asList(this.rtWire)));
    
    t.checkExpect(new ArrayListUtils().getConnectedNeighbors(this.fourBoard, rtWire), 
        new ArrayList<GamePiece>(Arrays.asList(this.lWire, this.rbWire)));
    
    t.checkExpect(new ArrayListUtils().getConnectedNeighbors(this.fourBoard, bWire), 
        new ArrayList<GamePiece>(Arrays.asList()));
  }

  void testDrawPiece(Tester t) {
    this.init();
    
    WorldScene testScene = new WorldScene(500, 500);

    int x = IConstants.TILE_SIZE; 
    int y = IConstants.TILE_SIZE; 

    WorldImage pieceImage1 = this.lWire.tileImage(IConstants.TILE_SIZE, 5, true);
    testScene.placeImageXY(pieceImage1, x, y);
    
    WorldScene expectedScene1 = new WorldScene(500, 500);
    expectedScene1.placeImageXY(pieceImage1, x, y);

    t.checkExpect(this.lWire.drawPiece(testScene, x, y), expectedScene1);

    WorldImage pieceImage2 = this.bWire.tileImage(IConstants.TILE_SIZE, 5, false);
    testScene.placeImageXY(pieceImage2, x * 2, y); 

    WorldScene expectedScene2 = new WorldScene(500, 500);
    expectedScene2.placeImageXY(pieceImage1, x, y);
    expectedScene2.placeImageXY(pieceImage2, x * 2, y);

    t.checkExpect(this.bWire.drawPiece(testScene, x * 2, y), expectedScene2);

  }
  
  void testMakeScene(Tester t) {
    this.init();
    //TODO
  
    WorldImage col1 = new AboveImage(new AboveImage(new EmptyImage(), this.rbWire.tileImage(IConstants.TILE_SIZE, 5, false)), 
        this.rtWire.tileImage(IConstants.TILE_SIZE, 5, false));
    
    WorldImage col2 = new AboveImage(new AboveImage(new EmptyImage(), this.bWire.tileImage(IConstants.TILE_SIZE, 5, false)), 
        this.lWire.tileImage(IConstants.TILE_SIZE, 5, true));
    
    WorldImage mergedCol = new BesideImage(new BesideImage(new EmptyImage(), col1), col2);
    WorldImage board = new AboveImage(new RectangleImage(IConstants.TILE_SIZE * 2, 
        IConstants.TILE_SIZE, OutlineMode.SOLID, new Color(255, 239, 166)), mergedCol);
    
    this.fourScene.placeImageXY(board, IConstants.TILE_SIZE, ((IConstants.TILE_SIZE * 2)
        + IConstants.TILE_SIZE) / 2);
    
    t.checkExpect(this.fourWorld.makeScene(), this.fourScene);
    
    
    WorldImage col3 = new AboveImage(new AboveImage(new EmptyImage(), this.rbWireR.tileImage(IConstants.TILE_SIZE, 5, false)), 
        this.rtWireR.tileImage(IConstants.TILE_SIZE, 5, false));
    
    WorldImage col4 = new AboveImage(new AboveImage(new EmptyImage(), this.bWireR.tileImage(IConstants.TILE_SIZE, 5, false)), 
        this.lWireR.tileImage(IConstants.TILE_SIZE, 5, true));
    
    WorldImage mergedCol2 = new BesideImage(new BesideImage(new EmptyImage(), col3), col4);
    WorldImage board2 = new AboveImage(new RectangleImage(IConstants.TILE_SIZE * 2, 
        IConstants.TILE_SIZE, OutlineMode.SOLID, new Color(255, 239, 166)), mergedCol2);
    
    this.fourScene.placeImageXY(board2, IConstants.TILE_SIZE, ((IConstants.TILE_SIZE * 2)
        + IConstants.TILE_SIZE) / 2);
    
    t.checkExpect(this.fourWorldRand.makeScene(), this.fourScene);
    
    WorldScene winScreen = new WorldScene(2 * IConstants.TILE_SIZE, 2 * IConstants.TILE_SIZE + IConstants.TILE_SIZE);
    winScreen.placeImageXY(new TextImage("You Win!", 
        IConstants.TILE_SIZE / 2, Color.black), 
        IConstants.TILE_SIZE , 
        IConstants.TILE_SIZE);
    
    t.checkExpect(this.fourWorldWin.makeScene(), winScreen);
  }
  
  void atestSeedBigBang(Tester t) {
    this.init();

    int width = 2;
    int height = 2;

    int worldWidth = IConstants.TILE_SIZE * width;
    int worldHeight = (IConstants.TILE_SIZE * height) + IConstants.TILE_SIZE;
    double tickRate = .1;
    this.fourWorld.bigBang(worldWidth, worldHeight, tickRate);
  }

  void atestBigBang(Tester t) {
    this.init();

    int width = 8;
    int height = 9;

    LightEmAll world = new LightEmAll(this.verticalBoard, 
        new ArrayList<GamePiece>(), new ArrayList<Edge>(), width, height, 1, 1, 5);
    int worldWidth = IConstants.TILE_SIZE * world.width;
    int worldHeight = (IConstants.TILE_SIZE * world.height) + IConstants.TILE_SIZE;
    double tickRate = .1;
    world.bigBang(worldWidth, worldHeight, tickRate);
  }
}













