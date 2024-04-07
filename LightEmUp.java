import java.util.ArrayList;
import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;

interface IConstants {

  
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
    
  }
  
  public WorldScene makeScene() {
    return new ArrayListUtils().draw(board, width, height, powerRow, powerCol, radius);
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
  // whether the power station is on this piece
  boolean powerStation;
  boolean powered;
  
  GamePiece (int row, int col, boolean left, boolean right, boolean top, 
      boolean bottom, boolean powerStation, boolean powered){
    this.row = row;
    this.col = col;
    this.left = left;
    this.right = right;
    this.top = top;
    this.bottom = bottom;
    this.powerStation = powerStation;
    this.powered = powered;
  }
  
  public WorldImage drawPiece(int size) {
    if (powered) {
      Color wireColor = Color.yellow;
    }
    else {
      Color wireColor = Color.GRAY;
    }
    
    WorldImage tile = new OverlayImage(new RectangleImage(size, size, OutlineMode.OUTLINE, Color.black),
        new RectangleImage(size, size, OutlineMode.SOLID, Color.DARK_GRAY));
    WorldImage horizontalWire = new RectangleImage(size / 2, size / 4, OutlineMode.SOLID, Color.GRAY);
    WorldImage verticalWire = new RectangleImage(size / 4, size / 2, OutlineMode.SOLID, Color.GRAY);
    
    if (left) {
      
    }
    if (right) {
      
    }
    if (bottom) {
      
    }
    if (top) {
      
    }
    
  }
  
}

class ArrayListUtils {
  
  // Draws the board of GamePieces
  public WorldScene draw(ArrayList<ArrayList<GamePiece>> board, int width, int height, int powerRow, int powerCol, int radius) {
    return null;
    
  }
  
  
}
























