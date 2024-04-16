import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;
import java.util.ArrayDeque;

import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;

// Constants for LightEmAll
interface IConstants {
  int TILE_SIZE = 50;

}

// Represents the World class for LightEmAll
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

  // Constructor for real games with a generated board
  LightEmAll(int width, int height, 
      int powerRow, int powerCol, int radius) { 
    if (width < 1 || height < 1) {
      throw new IllegalArgumentException("Error: Invalid width or height");
    }
    if ((powerRow > height - 1) || (powerCol > width - 1)) {
      throw new IllegalArgumentException("Error: Power station out of bounds");
    }

    this.board = new ArrayListUtils().generateBoard(width, height, new Random());
    this.width = width;
    this.height = height;
    this.powerRow = powerRow;
    this.powerCol = powerCol;
    this.radius = radius;
    this.gameState = "";


    new ArrayListUtils().randomRotateAll(this.board, new Random());
  }

  // Constructor for real "random" games with a given board
  LightEmAll(ArrayList<ArrayList<GamePiece>> board,  ArrayList<GamePiece> nodes, 
      ArrayList<Edge> mst,
      int width, int height, int powerRow, int powerCol, int radius) {
    if (width < 1 || height < 1) {
      throw new IllegalArgumentException("Error: Invalid width or height");
    }
    if ((powerRow > height - 1) || (powerCol > width - 1)) {
      throw new IllegalArgumentException("Error: Power station out of bounds");
    }

    this.board = board;
    this.nodes = nodes;
    this.mst = mst;
    this.width = width;
    this.height = height;
    this.powerRow = powerRow;
    this.powerCol = powerCol;
    this.radius = radius;
    this.gameState = "";


    new ArrayListUtils().randomRotateAll(this.board, new Random());
  }

  // Constructor for testing different states with given random with a generated
  // board
  LightEmAll(ArrayList<ArrayList<GamePiece>> board,  ArrayList<GamePiece> nodes, 
      ArrayList<Edge> mst, int width, int height, int powerRow, int powerCol, 
      int radius, String gameState, Random rand) {
    if (width < 1 || height < 1) {
      throw new IllegalArgumentException("Error: Invalid width or height");
    }
    if ((powerRow > height - 1) || (powerCol > width - 1)) {
      throw new IllegalArgumentException("Error: Power station out of bounds");
    }
    this.board = board;
    this.nodes = nodes;
    this.mst = mst;
    this.width = width;
    this.height = height;
    this.powerRow = powerRow;
    this.powerCol = powerCol;
    this.radius = radius;
    this.gameState = gameState;

    new ArrayListUtils().randomRotateAll(this.board, rand);
  }

  // Constructor for testing different states without random with a generated board
  LightEmAll(ArrayList<ArrayList<GamePiece>> board,  ArrayList<GamePiece> nodes, 
      ArrayList<Edge> mst, int width, int height, int powerRow, int powerCol, 
      int radius, String gameState) {
    if (width < 1 || height < 1) {
      throw new IllegalArgumentException("Error: Invalid width or height");
    }
    if ((powerRow > height - 1) || (powerCol > width - 1)) {
      throw new IllegalArgumentException("Error: Power station out of bounds");
    }
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

    // Draw the win scene
    if (this.gameState.equals("win")) {
      WorldScene winScreen = new WorldScene(width * IConstants.TILE_SIZE, 
          height * IConstants.TILE_SIZE + IConstants.TILE_SIZE);
      winScreen.placeImageXY(new TextImage("You Win!", 
          IConstants.TILE_SIZE / 2, Color.black), 
          IConstants.TILE_SIZE * width / 2, 
          ((IConstants.TILE_SIZE * height) / 2));
      return winScreen;
    }
    // Draw the game scene
    else {
      return new ArrayListUtils().draw(board, width, height, powerRow, powerCol, radius, 
          new WorldScene(width * IConstants.TILE_SIZE, 
              height * IConstants.TILE_SIZE + IConstants.TILE_SIZE));
    }

  }

  // Handles key inputs
  public void onKeyEvent(String key) {
    if (key.equals("left") && this.powerCol != 0) {
      if (this.board.get(powerCol).get(powerRow)
          .leftConnects(this.board.get(powerCol - 1).get(powerRow))) {
        this.powerCol -= 1;
      }
    }
    else if (key.equals("right") && (this.powerCol != this.width - 1)) {
      if (this.board.get(powerCol).get(powerRow)
          .rightConnects(this.board.get(powerCol + 1).get(powerRow))) {
        this.powerCol += 1;
      }
    }
    else if (key.equals("up") && (this.powerRow != 0)) {
      if (this.board.get(powerCol).get(powerRow)
          .topConnects(this.board.get(powerCol).get(powerRow - 1))) {
        this.powerRow -= 1;
      }
    }
    else if (key.equals("down") && (this.powerRow != this.height - 1)) {
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

// Represents a GamePiece on the board
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

  GamePiece(int row, int col, boolean left, boolean right, boolean top, 
      boolean bottom, boolean powered) {
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

  // Produces a WorldImage of a single GamePiece
  WorldImage tileImage(int size, int wireWidth, boolean hasPowerStation) {

    Color wireColor = Color.gray;
    if (powered || hasPowerStation) {
      wireColor = Color.yellow;
    }

    WorldImage image = new OverlayImage(
        new RectangleImage(wireWidth, wireWidth, OutlineMode.SOLID, wireColor),
        new RectangleImage(size, size, OutlineMode.SOLID, Color.DARK_GRAY));
    WorldImage vWire = new RectangleImage(wireWidth, (size + 1) / 2, 
        OutlineMode.SOLID, wireColor);
    WorldImage hWire = new RectangleImage((size + 1) / 2, wireWidth, 
        OutlineMode.SOLID, wireColor);

    if (this.top) { 
      image = new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.TOP, 
          vWire, 0, 0, image); 
    }
    if (this.right) { 
      image = new OverlayOffsetAlign(AlignModeX.RIGHT, AlignModeY.MIDDLE, 
          hWire, 0, 0, image);
    }
    if (this.bottom) {
      image = new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.BOTTOM, 
          vWire, 0, 0, image);
    }
    if (this.left) {
      image = new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.MIDDLE, 
          hWire, 0, 0, image);
    }
    if (hasPowerStation) {
      image = new OverlayImage(
          new OverlayImage(
              new StarImage(size / 3, 7, OutlineMode.OUTLINE, new Color(255, 128, 0)),
              new StarImage(size / 3, 7, OutlineMode.SOLID, new Color(0, 255, 255))),
          image);
    }

    image = new OverlayImage(new RectangleImage(size, size, OutlineMode.OUTLINE, Color.BLACK),
        image);
    return image;
  }

  // Rotates a GamePiece clockwise
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

  // Randomly rotates a GamePiece
  public void randomRotatePiece(Random rand) {
    int numRotations = rand.nextInt(4);

    for (int i = 0; i <= numRotations; i++) {
      this.rotatePiece();
    }
  }

}

// Represents an Edge between two nodes
class Edge {
  GamePiece fromNode;
  GamePiece toNode;
  int weight;

  Edge(GamePiece fromNode, GamePiece toNode, int weight) {
    this.fromNode = fromNode;
    this.toNode = toNode;
    this.weight = weight;
  }

}

// Utility class for ArrayLists
class ArrayListUtils {
  public ArrayList<ArrayList<GamePiece>> generateBoard(int width, 
      int height, Random rand) {

    ArrayList<ArrayList<GamePiece>> board = new ArrayList<ArrayList<GamePiece>>();
    HashMap<GamePiece, GamePiece> representatives = new HashMap<GamePiece, GamePiece>();
    ArrayList<Edge> edgesInTree = new ArrayList<Edge>();
    ArrayList<Edge> worklist = new ArrayList<Edge>();

    // Add new gamepieces to fill board
    for (int col = 0; col < width; col++) {
      board.add(new ArrayList<GamePiece>());
      for (int row = 0; row < height; row++) {
        board.get(col)
          .add(new GamePiece(row, col, false, false, false, false, false));
      }
    }

    // Add every edge between neighboring nodes
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        if (i != 0) {
          worklist.add(new Edge(board.get(i).get(j), board.get(i - 1).get(j), rand.nextInt(26)));
        }
        if (i != width - 1) {
          worklist.add(new Edge(board.get(i).get(j), board.get(i + 1).get(j), rand.nextInt(26)));
        }
        if (j != 0) {
          worklist.add(new Edge(board.get(i).get(j), board.get(i).get(j - 1), rand.nextInt(26)));
        }
        if (j != height - 1) {
          worklist.add(new Edge(board.get(i).get(j), board.get(i).get(j + 1), rand.nextInt(26)));
        }
      }
    }

    worklist.sort(new WeightComparator());

    // Initialize every node's representative to itself
    for (ArrayList<GamePiece> column : board) {
      for (GamePiece piece : column) {
        representatives.put(piece, piece);
      }
    }

    // While there's more than one tree
    while (edgesInTree.size() < (width * height) - 1) {
      // Pick the next cheapest edge of the graph
      Edge nextEdge = worklist.remove(0); 

      // Find representatives of X and Y
      GamePiece representativeX = new Utils().find(representatives, nextEdge.fromNode);
      GamePiece representativeY = new Utils().find(representatives, nextEdge.toNode);

      if (representativeX == representativeY) {
        // do nothing
      }
      else {
        edgesInTree.add(nextEdge);

        // Union the representatives of X and Y
        new Utils().union(representatives, representativeX, representativeY);
      }
    }

    for (ArrayList<GamePiece> column : board) {
      for (Edge edge : edgesInTree) {
        new Utils().connectPieces(edge.fromNode, edge.toNode);
      }
    }

    return board;
  }

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

  // Uses breadth first search to power pieces
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

  // Returns valid neighbor GamePieces
  public ArrayList<GamePiece> getConnectedNeighbors(ArrayList<ArrayList<GamePiece>> board, 
      GamePiece from) {
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

  // Randomly rotates all GamePieces on the board
  public void randomRotateAll(ArrayList<ArrayList<GamePiece>> board, 
      Random rand) {
    for (int i = 0; i < board.size(); i++) {
      for (int j = 0; j < board.get(i).size(); j++) {
        board.get(i).get(j).randomRotatePiece(rand);
      }
    }
  }

}

// Utility methods
class Utils {

  // Finds the mouse position in terms of col and row
  public Posn findMousePos(int tileSize, int width, int height, Posn pos) {
    int clickedCol = Math.floorDiv(pos.x, tileSize);
    int clickedRow = Math.floorDiv(pos.y, tileSize) - 1;

    return new Posn(clickedCol, clickedRow);
  }

  // Connects the two game pieces
  public void connectPieces(GamePiece fromNode, GamePiece toNode) {
    // Determine the direction of the connection
    if (fromNode.row == toNode.row - 1) {
      fromNode.bottom = true;
      toNode.top = true;
    } 
    else if (fromNode.row == toNode.row + 1) {
      fromNode.top = true;
      toNode.bottom = true;
    } 
    else if (fromNode.col == toNode.col - 1) {
      fromNode.right = true;
      toNode.left = true;
    } 
    else if (fromNode.col == toNode.col + 1) {
      fromNode.left = true;
      toNode.right = true;
    }
  }

  // Helper method to find the representative of a node
  public GamePiece find(HashMap<GamePiece, GamePiece> representatives, GamePiece node) {
    if (representatives.get(node) == node) {
      return node;
    }
    else {
      return find(representatives, representatives.get(node));
    }
  }

  // Helper method to union two representatives
  public void union(HashMap<GamePiece, GamePiece> representatives, GamePiece x, GamePiece y) {
    GamePiece representativeX = find(representatives, x);
    GamePiece representativeY = find(representatives, y);
    representatives.put(representativeX, representativeY);
  }

}

// Compares Edge's by weight
class WeightComparator implements Comparator<Edge> {
  public int compare(Edge e1, Edge e2) {
    if (e1.weight > e2.weight) {
      return 1;
    }
    if (e1.weight < e2.weight) {
      return -1;
    }
    else {
      return 0;
    }
  }

}

// Examples for tests
class ExamplesLightEmAll {
  WorldScene fourScene;
  WorldScene sixScene;

  ArrayList<ArrayList<GamePiece>> horizontalBoard;
  ArrayList<ArrayList<GamePiece>> verticalBoard;

  ArrayList<ArrayList<GamePiece>> fourBoard;
  ArrayList<ArrayList<GamePiece>> fourBoardRand;
  ArrayList<ArrayList<GamePiece>> sixBoard;
  ArrayList<ArrayList<GamePiece>> sixBoardRand;

  ArrayList<ArrayList<GamePiece>> mBoardTest;

  ArrayList<ArrayList<GamePiece>> kBoardTest;
  ArrayList<ArrayList<GamePiece>> kBoardTest2;
  ArrayList<ArrayList<GamePiece>> kBoardTest3;

  ArrayList<ArrayList<GamePiece>> pieceBoard;
  ArrayList<ArrayList<GamePiece>> pieceBoardRand;

  ArrayList<Edge> edgeList1;
  ArrayList<Edge> edgeList2;

  World pieceWorldRand;
  World fourWorld;
  World fourWorldRand;
  World fourWorldWin;
  World fourWorldStat1;
  World fourWorldStat2;
  World sixWorldRand;
  World mWorld;

  GamePiece gp1;
  GamePiece gp2;
  GamePiece gp3;
  GamePiece gp4;
  GamePiece gp5;
  GamePiece gp6;

  // For testing rotate all
  GamePiece gp1R;
  GamePiece gp2R;
  GamePiece gp3R;
  GamePiece gp4R;

  // For testing draw on a 2x2 board
  GamePiece lWire;
  GamePiece bWire;
  GamePiece rtWire;
  GamePiece rbWire;

  // For testing draw on a 2x2 board
  GamePiece lWireM;
  GamePiece bWireM;
  GamePiece rtWireM;
  GamePiece rbWireM;

  // For testing draw on a 2x2 board
  GamePiece lWireS;
  GamePiece bWireS;
  GamePiece rtWireS;
  GamePiece rbWireS;

  // To be randomized and drawn on a 2x2 board
  GamePiece lWireR;
  GamePiece bWireR;
  GamePiece rtWireR;
  GamePiece rbWireR;

  // To be randomized and drawn on a 2x2 board
  GamePiece lWireK;
  GamePiece bWireK;
  GamePiece rtWireK;
  GamePiece rbWireK;

  // To be randomized and drawn on a 2x2 board
  GamePiece lWireK2;
  GamePiece bWireK2;
  GamePiece rtWireK2;
  GamePiece rbWireK2;

  // To be randomized and drawn on a 2x2 board
  GamePiece Wire1K3;
  GamePiece Wire2K3;
  GamePiece Wire3K3;
  GamePiece Wire4K3;
  GamePiece Wire5K3;
  GamePiece Wire6K3;

  // To be drawn on a 2x2 board
  GamePiece mWire1;
  GamePiece mWire2;
  GamePiece mWire3;
  GamePiece mWire4;

  // For testing draw on a 2x3 board
  GamePiece lWire1ForSix;
  GamePiece lWire2ForSix;
  GamePiece rtWireForSix;
  GamePiece rbWireForSix;  
  GamePiece lbWireForSix;
  GamePiece tWireForSix;

  // For testing draw on a 2x3 board
  GamePiece lWire1ForSixR;
  GamePiece lWire2ForSixR;
  GamePiece rtWireForSixR;
  GamePiece rbWireForSixR;  
  GamePiece lbWireForSixR;
  GamePiece tWireForSixR;

  GamePiece testPieceL;
  GamePiece testPieceR;

  GamePiece fPiece1;
  GamePiece tPiece1;
  GamePiece fPiece1Test;
  GamePiece tPiece1Test;
  GamePiece fPiece2;
  GamePiece tPiece2;
  GamePiece fPiece2Test;
  GamePiece tPiece2Test;
  GamePiece fPiece3;
  GamePiece tPiece3;
  GamePiece fPiece3Test;
  GamePiece tPiece3Test;

  HashMap<GamePiece, GamePiece> hMap1;
  HashMap<GamePiece, GamePiece> hMap2;
  HashMap<GamePiece, GamePiece> hMap3;
  HashMap<GamePiece, GamePiece> hMap3Test;
  HashMap<GamePiece, GamePiece> hMap3Test2;
  HashMap<GamePiece, GamePiece> hMap3Test3;
  ArrayList<Edge> edgeList3;

  void init() {

    this.testPieceL = new GamePiece(1, 1, true, false, false, false, false); 
    this.testPieceR = new GamePiece(0, 1, false, true, true, false, false);

    // For testing left, right, top, bottom connects
    this.gp1 = new GamePiece(1, 0, true, false, false, false, false);
    this.gp2 = new GamePiece(8, 4, false, true, false, false, false);
    this.gp3 = new GamePiece(1, 1, false, false, true, false, false);
    this.gp4 = new GamePiece(2, 7, false, false, false, true, false);
    this.gp5 = new GamePiece(2, 7, true, true, true, true, false);
    this.gp6 = new GamePiece(0, 0, true, false, true, true, false);

    this.gp1R = new GamePiece(1, 0, true, false, false, false, false);
    this.gp2R = new GamePiece(8, 4, false, true, false, false, false);
    this.gp3R = new GamePiece(1, 1, false, false, true, false, false);
    this.gp4R = new GamePiece(2, 7, false, false, false, true, false);

    // For testing draw on a 2x2 board
    this.lWire = new GamePiece(1, 1, true, false, false, false, true);
    this.bWire = new GamePiece(0, 1, false, false, false, true, false);
    this.rtWire = new GamePiece(1, 0, false, true, true, false, true);
    this.rbWire = new GamePiece(0, 0, false, true, false, true, true);

    this.pieceBoard = new ArrayList<ArrayList<GamePiece>>();
    this.pieceBoard.add(new ArrayList<GamePiece>());
    this.pieceBoard.get(0).add(this.gp1);
    this.pieceBoard.get(0).add(this.gp2);

    this.pieceBoardRand = new ArrayList<ArrayList<GamePiece>>();
    this.pieceBoardRand.add(new ArrayList<GamePiece>());
    this.pieceBoardRand.get(0).add(this.gp1R);
    this.pieceBoardRand.get(0).add(this.gp2R);

    this.pieceWorldRand = new LightEmAll(this.pieceBoardRand, 
        new ArrayList<GamePiece>(), new ArrayList<Edge>(), 1, 2, 0, 0, 5, "", new Random(1));

    this.fourBoard = new ArrayList<ArrayList<GamePiece>>();
    this.fourBoard.add(new ArrayList<GamePiece>());
    this.fourBoard.add(new ArrayList<GamePiece>());
    this.fourBoard.get(0).add(this.rbWire);
    this.fourBoard.get(0).add(this.rtWire);
    this.fourBoard.get(1).add(this.bWire);
    this.fourBoard.get(1).add(this.lWire);

    this.fourWorld = new LightEmAll(fourBoard, 
        new ArrayList<GamePiece>(), new ArrayList<Edge>(), 2, 2, 1, 1, 5, "");

    this.fourWorldWin = new LightEmAll(fourBoard, 
        new ArrayList<GamePiece>(), new ArrayList<Edge>(), 2, 2, 1, 1, 5, "win");

    // To be randomized and drawn on a 2x2 board
    this.lWireR = new GamePiece(1, 1, true, false, false, false, true);
    this.bWireR = new GamePiece(0, 1, false, false, false, true, false);
    this.rtWireR = new GamePiece(1, 0, false, true, true, false, true);
    this.rbWireR = new GamePiece(0, 0, false, true, false, true, true);

    this.fourBoardRand = new ArrayList<ArrayList<GamePiece>>();
    this.fourBoardRand.add(new ArrayList<GamePiece>());
    this.fourBoardRand.add(new ArrayList<GamePiece>());
    this.fourBoardRand.get(0).add(this.rbWireR);
    this.fourBoardRand.get(0).add(this.rtWireR);
    this.fourBoardRand.get(1).add(this.bWireR);
    this.fourBoardRand.get(1).add(this.lWireR);

    this.fourWorldRand = new LightEmAll(this.fourBoardRand, 
        new ArrayList<GamePiece>(), new ArrayList<Edge>(), 2, 2, 1, 1, 5, "", new Random(1));

    this.fourWorldStat1 = new LightEmAll(this.fourBoard, 
        new ArrayList<GamePiece>(), new ArrayList<Edge>(), 2, 2, 1, 0, 5, "");
    this.fourWorldStat2 = new LightEmAll(this.fourBoard, 
        new ArrayList<GamePiece>(), new ArrayList<Edge>(), 2, 2, 0, 0, 5, "");

    // To be randomized and drawn on a 2x2 board
    this.mWire1 = new GamePiece(1, 1, true, false, false, false, true);
    this.mWire2 = new GamePiece(0, 1, false, false, false, true, false);
    this.mWire3 = new GamePiece(1, 0, false, true, false, true, true);
    this.mWire4 = new GamePiece(0, 0, false, true, false, true, true);

    this.mBoardTest = new ArrayList<ArrayList<GamePiece>>();
    this.mBoardTest.add(new ArrayList<GamePiece>());
    this.mBoardTest.add(new ArrayList<GamePiece>());
    this.mBoardTest.get(0).add(this.mWire4);
    this.mBoardTest.get(0).add(this.mWire3);
    this.mBoardTest.get(1).add(this.mWire2);
    this.mBoardTest.get(1).add(this.mWire1);

    this.mWorld = new LightEmAll(this.mBoardTest, 
        new ArrayList<GamePiece>(), new ArrayList<Edge>(), 
        2, 2, 1, 1, 5, "");

    // For testing draw on a 2x2 board
    this.lWire1ForSix = new GamePiece(1, 1, true, false, false, false, false);
    this.lWire2ForSix = new GamePiece(1, 0, true, false, false, false, false);
    this.rtWireForSix = new GamePiece(0, 1, false, true, true, false, false);
    this.rbWireForSix = new GamePiece(0, 0, false, true, false, true, false);
    this.lbWireForSix = new GamePiece(2, 0, true, false, false, true, false);
    this.tWireForSix = new GamePiece(2, 1, false, false, true, false, false);

    this.sixBoard = new ArrayList<ArrayList<GamePiece>>();
    this.sixBoard.add(new ArrayList<GamePiece>());
    this.sixBoard.add(new ArrayList<GamePiece>());
    this.sixBoard.add(new ArrayList<GamePiece>());
    this.sixBoard.get(0).add(this.rbWireForSix);
    this.sixBoard.get(0).add(this.rtWireForSix);
    this.sixBoard.get(1).add(this.lWire2ForSix);
    this.sixBoard.get(1).add(this.lWire1ForSix);
    this.sixBoard.get(2).add(this.lbWireForSix);
    this.sixBoard.get(2).add(this.tWireForSix);

    // For testing draw on a 2x2 board
    this.lWire1ForSixR = new GamePiece(1, 1, true, false, false, false, false);
    this.lWire2ForSixR = new GamePiece(1, 0, true, false, false, false, false);
    this.rtWireForSixR = new GamePiece(0, 1, false, true, true, false, false);
    this.rbWireForSixR = new GamePiece(0, 0, false, true, false, true, false);
    this.lbWireForSixR = new GamePiece(2, 0, true, false, false, true, false);
    this.tWireForSixR = new GamePiece(2, 1, false, false, true, false, false);

    this.sixBoardRand = new ArrayList<ArrayList<GamePiece>>();
    this.sixBoardRand.add(new ArrayList<GamePiece>());
    this.sixBoardRand.add(new ArrayList<GamePiece>());
    this.sixBoardRand.add(new ArrayList<GamePiece>());
    this.sixBoardRand.get(0).add(this.rbWireForSixR);
    this.sixBoardRand.get(0).add(this.rtWireForSixR);
    this.sixBoardRand.get(1).add(this.lWire2ForSixR);
    this.sixBoardRand.get(1).add(this.lWire1ForSixR);
    this.sixBoardRand.get(2).add(this.lbWireForSixR);
    this.sixBoardRand.get(2).add(this.tWireForSixR);

    this.sixWorldRand = new LightEmAll(this.sixBoardRand, 
        new ArrayList<GamePiece>(), new ArrayList<Edge>(), 3, 2, 1, 1, 5, "", new Random(1));

    // For testing draw on a 2x2 board
    this.lWireK = new GamePiece(1, 1, true, false, false, false, false);
    this.bWireK = new GamePiece(0, 1, true, false, false, false, false);
    this.rtWireK = new GamePiece(1, 0, false, true, true, false, false);
    this.rbWireK = new GamePiece(0, 0, false, true, false, true, false);

    this.kBoardTest = new ArrayList<ArrayList<GamePiece>>();
    this.kBoardTest.add(new ArrayList<GamePiece>());
    this.kBoardTest.add(new ArrayList<GamePiece>());
    this.kBoardTest.get(0).add(this.rbWireK);
    this.kBoardTest.get(0).add(this.rtWireK);
    this.kBoardTest.get(1).add(this.bWireK);
    this.kBoardTest.get(1).add(this.lWireK);

    // For testing draw on a 2x2 board
    this.lWireK2 = new GamePiece(1, 1, true, false, true, false, false);
    this.bWireK2 = new GamePiece(0, 1, false, false, false, true, false);
    this.rtWireK2 = new GamePiece(1, 0, false, true, true, false, false);
    this.rbWireK2 = new GamePiece(0, 0, false, false, false, true, false);

    this.kBoardTest2 = new ArrayList<ArrayList<GamePiece>>();
    this.kBoardTest2.add(new ArrayList<GamePiece>());
    this.kBoardTest2.add(new ArrayList<GamePiece>());
    this.kBoardTest2.get(0).add(this.rbWireK2);
    this.kBoardTest2.get(0).add(this.rtWireK2);
    this.kBoardTest2.get(1).add(this.bWireK2);
    this.kBoardTest2.get(1).add(this.lWireK2);

    // For testing draw on a 2x2 board
    this.Wire1K3 = new GamePiece(0, 0, false, false, false, true, false);
    this.Wire2K3 = new GamePiece(1, 0, false, true, true, false, false);
    this.Wire3K3 = new GamePiece(0, 1, false, true, false, true, false);
    this.Wire4K3 = new GamePiece(1, 1, true, false, true, false, false);
    this.Wire5K3 = new GamePiece(0, 2, true, false, false, true, false);
    this.Wire6K3 = new GamePiece(1, 2, false, false, true, false, false);

    this.kBoardTest3 = new ArrayList<ArrayList<GamePiece>>();
    this.kBoardTest3.add(new ArrayList<GamePiece>());
    this.kBoardTest3.add(new ArrayList<GamePiece>());
    this.kBoardTest3.add(new ArrayList<GamePiece>());
    this.kBoardTest3.get(0).add(this.Wire1K3);
    this.kBoardTest3.get(0).add(this.Wire2K3);
    this.kBoardTest3.get(1).add(this.Wire3K3);
    this.kBoardTest3.get(1).add(this.Wire4K3);
    this.kBoardTest3.get(2).add(this.Wire5K3);
    this.kBoardTest3.get(2).add(this.Wire6K3);

    this.fPiece1 = new GamePiece(0, 0, false, false, false, false, false);
    this.tPiece1 = new GamePiece(1, 0, false, false, false, false, false);
    this.fPiece1Test = new GamePiece(0, 0, false, false, false, true, false);
    this.tPiece1Test = new GamePiece(1, 0, false, false, true, false, false);

    this.fPiece2 = new GamePiece(0, 0, false, false, false, false, false);
    this.tPiece2 = new GamePiece(0, 1, false, false, false, false, false);
    this.fPiece2Test = new GamePiece(0, 0, false, true, false, false, false);
    this.tPiece2Test = new GamePiece(0, 1, true, false, false, false, false);

    this.fPiece3 = new GamePiece(1, 1, false, false, false, false, false);
    this.tPiece3 = new GamePiece(0, 0, false, false, false, false, false);
    this.fPiece3Test = new GamePiece(1, 1, false, false, true, false, false);
    this.tPiece3Test = new GamePiece(0, 0, false, false, false, true, false);

    this.horizontalBoard = new ArrayList<ArrayList<GamePiece>>();
    // generation of horizontal wire board
    for (int col = 0; col < 8; col++) {
      this.horizontalBoard.add(new ArrayList<GamePiece>());
      for (int row = 0; row < 9; row++) {
        if (row == 0) {
          this.horizontalBoard.get(col)
            .add(new GamePiece(row, col, false, false, false, true, false));
        }
        else if (row == 8) {
          this.horizontalBoard.get(col)
            .add(new GamePiece(row, col, false, false, true, false, false));
        }
        else if (col == 0 && row == 4) {
          this.horizontalBoard.get(col)
            .add(new GamePiece(row, col, false, true, true, true, false));
        }
        else if (col == 7 && row == 4) {
          this.horizontalBoard.get(col)
            .add(new GamePiece(row, col, true, false, true, true, false));
        }
        else if (row == 4) {
          this.horizontalBoard.get(col)
            .add(new GamePiece(row, col, true, true, true, true, false));
        }
        else {
          this.horizontalBoard.get(col)
            .add(new GamePiece(row, col, false, false, true, true, false));
        }
      }
    }

    this.verticalBoard = new ArrayList<ArrayList<GamePiece>>();
    // generation of horizontal wire board
    for (int col = 0; col < 8; col++) {
      this.verticalBoard.add(new ArrayList<GamePiece>());
      for (int row = 0; row < 9; row++) {
        if (col == 0) {
          this.verticalBoard.get(col)
            .add(new GamePiece(row, col, false, true, false, false, false));
        }
        else if (col == 7) {
          this.verticalBoard.get(col)
            .add(new GamePiece(row, col, true, false, false, false, false));
        }
        else if (row == 0 && col == 4) {
          this.verticalBoard.get(col)
            .add(new GamePiece(row, col, true, true, false, true, false));
        }
        else if (row == 8 && col == 4) {
          this.verticalBoard.get(col)
            .add(new GamePiece(row, col, true, true, true, false, false));
        }
        else if (col == 4) {
          this.verticalBoard.get(col)
            .add(new GamePiece(row, col, true, true, true, true, false));
        }
        else {
          this.verticalBoard.get(col)
            .add(new GamePiece(row, col, true, true, false, false, false));
        }
      }
    }

    this.fourScene = new WorldScene(2 * IConstants.TILE_SIZE, (IConstants.TILE_SIZE * 2) 
        + IConstants.TILE_SIZE);
    this.sixScene = new WorldScene(3 * IConstants.TILE_SIZE, (IConstants.TILE_SIZE * 2) 
        + IConstants.TILE_SIZE);

    this.hMap1 = new HashMap<GamePiece, GamePiece>();
    this.hMap1.put(gp1, gp1);
    this.hMap1.put(gp2, gp2);

    this.hMap2 = new HashMap<GamePiece, GamePiece>();
    this.hMap2.put(gp1, gp2);
    this.hMap2.put(gp2, gp2);
    this.hMap2.put(gp3, gp4);
    this.hMap2.put(gp4, gp2);

    this.hMap3 = new HashMap<GamePiece, GamePiece>();
    this.hMap3.put(gp1, gp1);
    this.hMap3.put(gp2, gp2);
    this.hMap3.put(gp3, gp3);
    this.hMap3.put(gp4, gp4);

    this.hMap3Test = new HashMap<GamePiece, GamePiece>();
    this.hMap3Test.put(gp1, gp2);
    this.hMap3Test.put(gp2, gp2);
    this.hMap3Test.put(gp3, gp3);
    this.hMap3Test.put(gp4, gp4);

    this.hMap3Test2 = new HashMap<GamePiece, GamePiece>();
    this.hMap3Test2.put(gp1, gp2);
    this.hMap3Test2.put(gp2, gp3);
    this.hMap3Test2.put(gp3, gp3);
    this.hMap3Test2.put(gp4, gp4);

    this.hMap3Test3 = new HashMap<GamePiece, GamePiece>();
    this.hMap3Test3.put(gp1, gp2);
    this.hMap3Test3.put(gp2, gp3);
    this.hMap3Test3.put(gp3, gp4);
    this.hMap3Test3.put(gp4, gp4);

    this.edgeList1 = new ArrayList<Edge>();
    this.edgeList1.add(new Edge(this.gp1, this.gp2, 5));
    this.edgeList1.add(new Edge(this.gp5, this.gp3, 25));
    this.edgeList1.add(new Edge(this.gp3, this.gp2, 1));

    this.edgeList2 = new ArrayList<Edge>();
    this.edgeList2.add(new Edge(this.gp3, this.gp2, 1));
    this.edgeList2.add(new Edge(this.gp1, this.gp2, 5));
    this.edgeList2.add(new Edge(this.gp5, this.gp3, 25));

    this.edgeList3 = new ArrayList<Edge>();
    this.edgeList3.add(new Edge(this.gp5, this.gp3, 25));
    this.edgeList3.add(new Edge(this.gp1, this.gp2, 5));
    this.edgeList3.add(new Edge(this.gp3, this.gp2, 1));

  }

  // Test constructor exceptions
  void testExceptions(Tester t) {
    this.init();

    t.checkConstructorException(new IllegalArgumentException("Error: Invalid width or height"),
        "LightEmAll", this.fourBoard, new ArrayList<GamePiece>(), 
        new ArrayList<Edge>(), -1, 2, 1, 1, 5);

    this.init();
    t.checkConstructorException(new IllegalArgumentException("Error: Invalid width or height"),
        "LightEmAll", this.fourBoard, new ArrayList<GamePiece>(),
        new ArrayList<Edge>(), 1, -1, 1, 1, 5);

    this.init();
    t.checkConstructorException(new IllegalArgumentException("Error: Power station "
        + "out of bounds"),
        "LightEmAll", this.fourBoard, new ArrayList<GamePiece>(), 
        new ArrayList<Edge>(), 2, 2, 5, 1, 5);

  }

  // Test leftConnects
  void testLeftConnects(Tester t) {
    this.init();
    t.checkExpect(this.gp1.leftConnects(this.gp2), true);
    t.checkExpect(this.gp2.leftConnects(this.gp1), false);
    t.checkExpect(this.gp1.leftConnects(this.gp3), false);
    t.checkExpect(this.gp5.leftConnects(this.gp2), true);

    t.checkExpect(this.testPieceL.leftConnects(this.testPieceR), true);
  }

  // Test rightConnects
  void testRightConnects(Tester t) {
    this.init();
    t.checkExpect(this.gp2.rightConnects(this.gp1), true);
    t.checkExpect(this.gp2.rightConnects(this.gp5), true);
    t.checkExpect(this.gp1.rightConnects(this.gp2), false);
    t.checkExpect(this.gp3.rightConnects(this.gp4), false);
  }

  // Test topConnects
  void testTopConnects(Tester t) {
    this.init();
    t.checkExpect(this.gp3.topConnects(this.gp4), true);
    t.checkExpect(this.gp3.topConnects(this.gp5), true);
    t.checkExpect(this.gp4.topConnects(this.gp3), false);
    t.checkExpect(this.gp1.topConnects(this.gp4), false);

    t.checkExpect(this.rtWire.topConnects(this.rbWire), true);
  }

  // Test bottomConnects
  void testBottomConnects(Tester t) {
    this.init();
    t.checkExpect(this.gp4.bottomConnects(this.gp3), true);
    t.checkExpect(this.gp4.bottomConnects(this.gp5), true);
    t.checkExpect(this.gp4.bottomConnects(this.gp1), false);
    t.checkExpect(this.gp3.bottomConnects(this.gp4), false);
  }

  // Test rotatePiece
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

  // Test weight comparator
  void testWeightComparator(Tester t) {
    this.init();
    this.edgeList2.sort(new WeightComparator());
    t.checkExpect(this.edgeList2, edgeList2);
    this.edgeList1.sort(new WeightComparator());
    t.checkExpect(this.edgeList1, edgeList2);
    this.edgeList3.sort(new WeightComparator());
    t.checkExpect(this.edgeList3, edgeList2);

  }

  // Test weight comparator
  void testConnectPieces(Tester t) { //TODO
    this.init();

    new Utils().connectPieces(this.fPiece1, this.tPiece1);
    t.checkExpect(this.fPiece1, this.fPiece1Test);
    t.checkExpect(this.tPiece1, this.tPiece1Test);
    new Utils().connectPieces(this.fPiece2, this.tPiece2);
    t.checkExpect(this.fPiece2, this.fPiece2Test);
    t.checkExpect(this.tPiece2, this.tPiece2Test);
    new Utils().connectPieces(this.fPiece3, this.tPiece3);
    t.checkExpect(this.fPiece3, this.fPiece3Test);
    t.checkExpect(this.tPiece3, this.tPiece3Test);
  }

  // Test findMousePos
  void testfindMousePos(Tester t) {
    this.init();

    t.checkExpect(new Utils().findMousePos(50, 8, 9, new Posn(120, 30)), new Posn(2, -1));
    t.checkExpect(new Utils().findMousePos(40, 10, 9, new Posn(125, 30)), new Posn(3, -1));
    t.checkExpect(new Utils().findMousePos(40, 10, 9, new Posn(325, 50)), new Posn(8, 0));
  }

  // Test randomRotatePiece
  void testRandomRotatePiece(Tester t) {
    this.init();
    this.gp1.randomRotatePiece(new Random(1));
    t.checkExpect(this.gp1, new GamePiece(1, 0, false, false, false, true, false));
    this.gp2.randomRotatePiece(new Random(1));
    t.checkExpect(this.gp2, new GamePiece(8, 4, false, false, true, false, false));
    this.gp3.randomRotatePiece(new Random(1));
    t.checkExpect(this.gp3, new GamePiece(1, 1, true, false, false, false, false));
  }

  // test randomRotateAll
  void testRandomRotateAll(Tester t) {
    this.init();
    new ArrayListUtils().randomRotateAll(this.fourBoard, new Random(1));
    t.checkExpect(this.fourBoard, this.fourBoardRand);

    this.init();
    new ArrayListUtils().randomRotateAll(this.pieceBoard, new Random(1));
    t.checkExpect(this.pieceBoard, this.pieceBoardRand); 

    this.init();
    new ArrayListUtils().randomRotateAll(this.sixBoard, new Random(1));
    t.checkExpect(this.sixBoard, this.sixBoardRand); 
  }

  // test tileImage
  void testTileImage(Tester t) {
    this.init();

    WorldImage image = new OverlayImage(
        new RectangleImage(5, 5, OutlineMode.SOLID, Color.gray),
        new RectangleImage(20, 20, OutlineMode.SOLID, Color.DARK_GRAY));
    WorldImage vWire = new RectangleImage(5, (20 + 1) / 2, 
        OutlineMode.SOLID, Color.gray);

    image = new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.BOTTOM, 
        vWire, 0, 0, image);
    image = new OverlayImage(new RectangleImage(20, 20, 
        OutlineMode.OUTLINE, Color.BLACK),
        image);
    t.checkExpect(this.bWire.tileImage(20, 5, false), image); 

    WorldImage image2 = new OverlayImage(
        new RectangleImage(5, 5, OutlineMode.SOLID, Color.yellow),
        new RectangleImage(20, 20, OutlineMode.SOLID, Color.DARK_GRAY));
    WorldImage vWire2 = new RectangleImage(5, (20 + 1) / 2, 
        OutlineMode.SOLID, Color.yellow);
    WorldImage hWire2 = new RectangleImage((20 + 1) / 2, 5, 
        OutlineMode.SOLID, Color.yellow);

    image2 = new OverlayOffsetAlign(AlignModeX.RIGHT, AlignModeY.MIDDLE, 
        hWire2, 0, 0, image2);
    image2 = new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.BOTTOM, 
        vWire2, 0, 0, image2);
    image2 = new OverlayImage(new RectangleImage(20, 20, 
        OutlineMode.OUTLINE, Color.BLACK),
        image2);

    t.checkExpect(this.rbWire.tileImage(20, 5, false), image2); 

  }

  // test draw method
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
    WorldImage col1a = new AboveImage(this.rbWireForSix
        .tileImage(IConstants.TILE_SIZE, 5, false), 
        this.rtWireForSix.tileImage(IConstants.TILE_SIZE, 5, false));
    WorldImage col2a = new AboveImage(this.lWire2ForSix
        .tileImage(IConstants.TILE_SIZE, 5, false), 
        this.lWire1ForSix.tileImage(IConstants.TILE_SIZE, 5, true));
    WorldImage col3 = new AboveImage(this.lbWireForSix
        .tileImage(IConstants.TILE_SIZE, 5, false), 
        this.tWireForSix.tileImage(IConstants.TILE_SIZE, 5, false)); 

    WorldImage newMergedCol1 = new BesideImage(col1a, col2a);
    WorldImage mergedCol2 = new BesideImage(newMergedCol1, col3);
    WorldImage board2 = new AboveImage(new RectangleImage(IConstants.TILE_SIZE * 3, 
        IConstants.TILE_SIZE, OutlineMode.SOLID, new Color(255, 239, 166)), mergedCol2);

    this.sixScene.placeImageXY(board2, (3 * IConstants.TILE_SIZE) / 2, 
        ((IConstants.TILE_SIZE * 2) + IConstants.TILE_SIZE) / 2);

    t.checkExpect(new ArrayListUtils().draw(this.sixBoard, 3, 2, 1, 1, 5, 
        new WorldScene(3 * IConstants.TILE_SIZE, IConstants.TILE_SIZE * 3)), this.sixScene);

  }

  // Test find representatives method
  void testFind(Tester t) {
    this.init();

    t.checkExpect(new Utils().find(this.hMap1, this.gp1), this.gp1);
    t.checkExpect(new Utils().find(this.hMap1, this.gp2), this.gp2);
    t.checkExpect(new Utils().find(this.hMap2, this.gp1), this.gp2);
    t.checkExpect(new Utils().find(this.hMap2, this.gp3), this.gp2);
  }

  // Test find representatives method
  void testUnion(Tester t) {
    this.init();

    new Utils().union(this.hMap3, this.gp1, this.gp2);
    t.checkExpect(this.hMap3, this.hMap3Test);
    new Utils().union(this.hMap3, this.gp2, this.gp3);
    t.checkExpect(this.hMap3, this.hMap3Test2);
    new Utils().union(this.hMap3, this.gp3, this.gp4);
    t.checkExpect(this.hMap3, this.hMap3Test3);
  }

  // Test generateBoard representatives method
  void testGenerateBoard(Tester t) { 
    this.init();

    t.checkExpect(new ArrayListUtils().generateBoard(2, 2, new Random(1)), 
        this.kBoardTest);

    t.checkExpect(new ArrayListUtils().generateBoard(2, 2, new Random(2)), 
        this.kBoardTest2);

    t.checkExpect(new ArrayListUtils().generateBoard(3, 2, new Random(2)), 
        this.kBoardTest3);
  }

  // Test breadth first search
  void testBfs(Tester t) {
    this.init(); 

    // Searching to a disconnected wire
    t.checkExpect(new ArrayListUtils().bfs(this.fourBoard, this.lWire, this.bWire), false); 
    // Searching to a connected wire
    t.checkExpect(new ArrayListUtils().bfs(this.fourBoard, this.lWire, this.rbWire), true);
    // Searching to a connected wire
    t.checkExpect(new ArrayListUtils().bfs(this.fourBoard, this.lWire, this.rtWire), true);
  }

  // Test getConnected Neighbors
  void testGetConnectedNeighbors(Tester t) {
    this.init();

    t.checkExpect(new ArrayListUtils().getConnectedNeighbors(this.fourBoard, lWire), 
        new ArrayList<GamePiece>(Arrays.asList(this.rtWire)));

    t.checkExpect(new ArrayListUtils().getConnectedNeighbors(this.fourBoard, rtWire), 
        new ArrayList<GamePiece>(Arrays.asList(this.lWire, this.rbWire)));

    t.checkExpect(new ArrayListUtils().getConnectedNeighbors(this.fourBoard, bWire), 
        new ArrayList<GamePiece>(Arrays.asList()));
  }

  // test onKeyEvent
  void testOnKeyEvent(Tester t) {
    this.init();

    this.fourWorld.onKeyEvent("left");
    t.checkExpect(this.fourWorld, this.fourWorldStat1);
    this.fourWorld.onKeyEvent("up");
    t.checkExpect(this.fourWorld, this.fourWorldStat2);
    this.fourWorld.onKeyEvent("right");
    t.checkExpect(this.fourWorld, this.fourWorldStat2);
  }

  // test onKeyEvent
  void testOnMouse(Tester t) {
    this.init();

    this.fourWorld.onMouseClicked(new Posn(IConstants.TILE_SIZE / 2, 
        IConstants.TILE_SIZE * 2 + 1), "LeftButton");
    t.checkExpect(this.fourWorld, 
        this.mWorld);
    this.fourWorld.onMouseClicked(new Posn(IConstants.TILE_SIZE + 1, 
        IConstants.TILE_SIZE + 1), "RightButton");
    t.checkExpect(this.fourWorld, 
        this.mWorld);
    this.fourWorld.onMouseClicked(new Posn(0, 
        0), "LeftButton");
    t.checkExpect(this.fourWorld, 
        this.mWorld);
  }

  // Test makeScene
  void testMakeScene(Tester t) {
    this.init();

    WorldImage col1 = new AboveImage(new AboveImage(new EmptyImage(), 
        this.rbWire.tileImage(IConstants.TILE_SIZE, 5, false)), 
        this.rtWire.tileImage(IConstants.TILE_SIZE, 5, false));

    WorldImage col2 = new AboveImage(new AboveImage(new EmptyImage(), 
        this.bWire.tileImage(IConstants.TILE_SIZE, 5, false)), 
        this.lWire.tileImage(IConstants.TILE_SIZE, 5, true));

    WorldImage mergedCol = new BesideImage(new BesideImage(new EmptyImage(), col1), col2);
    WorldImage board = new AboveImage(new RectangleImage(IConstants.TILE_SIZE * 2, 
        IConstants.TILE_SIZE, OutlineMode.SOLID, new Color(255, 239, 166)), mergedCol);

    this.fourScene.placeImageXY(board, IConstants.TILE_SIZE, ((IConstants.TILE_SIZE * 2)
        + IConstants.TILE_SIZE) / 2);

    t.checkExpect(this.fourWorld.makeScene(), this.fourScene);

    this.init();
    WorldScene winScreen = new WorldScene(2 * IConstants.TILE_SIZE, 
        2 * IConstants.TILE_SIZE + IConstants.TILE_SIZE);
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
    this.fourWorldRand.bigBang(worldWidth, worldHeight, tickRate);
  }

  void atestBigBang(Tester t) {
    this.init();

    int width = 8;
    int height = 9;

    LightEmAll world = new LightEmAll(this.verticalBoard, 
        new ArrayList<GamePiece>(), new ArrayList<Edge>(), width, height, 4, 3, 5);
    int worldWidth = IConstants.TILE_SIZE * world.width;
    int worldHeight = (IConstants.TILE_SIZE * world.height) + IConstants.TILE_SIZE;
    double tickRate = .1;
    world.bigBang(worldWidth, worldHeight, tickRate);
  }

  void testBigBangK(Tester t) {
    this.init();

    int width = 10;
    int height = 10;

    LightEmAll world = new LightEmAll(width, height, 0, 0, 5);
    int worldWidth = IConstants.TILE_SIZE * world.width;
    int worldHeight = (IConstants.TILE_SIZE * world.height) + IConstants.TILE_SIZE;
    double tickRate = .1;
    world.bigBang(worldWidth, worldHeight, tickRate);
  }
}














