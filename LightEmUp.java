import java.util.ArrayList;
import java.util.Arrays;

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
    return new ArrayListUtils().draw(board, width, height, powerRow, powerCol, radius, 
        new WorldScene(width * IConstants.TILE_SIZE, height * IConstants.TILE_SIZE));
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



  // ... [ Your GamePiece class contents here]

  // Generate an image of this, the given GamePiece.
  // - size: the size of the tile, in pixels
  // - wireWidth: the width of wires, in pixels
  // - wireColor: the Color to use for rendering wires on this
  // - hasPowerStation: if true, draws a fancy star on this tile to represent the power station
  //
  WorldImage tileImage(int size, int wireWidth, Color wireColor, boolean hasPowerStation) {
    // Start tile image off as a blue square with a wire-width square in the middle,
    // to make image "cleaner" (will look strange if tile has no wire, but that can't be)
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


}

class Edge {

}

class ArrayListUtils {

  // Draws the board of GamePieces
  public WorldScene draw(ArrayList<ArrayList<GamePiece>> board, int width, 
      int height, int powerRow, int powerCol, int radius, WorldScene ws) {
    WorldImage result = new EmptyImage();
    WorldImage colImage = new EmptyImage();

    for (int i = 0; i < board.size(); i++) {
      for (int j = 0; j < board.get(i).size(); j++) {
        if (powerCol == i && powerRow == j) {
          colImage = new AboveImage(colImage, board.get(i).get(j)
              .tileImage(IConstants.TILE_SIZE, 5, Color.yellow, true));
        }
        else {
          colImage = new AboveImage(colImage, board.get(i).get(j)
              .tileImage(IConstants.TILE_SIZE, 5, Color.gray, false));
        }
      }
      result = new BesideImage(result, colImage);
      colImage = new EmptyImage();
    } 

    ws.placeImageXY(result, IConstants.TILE_SIZE * width / 2, IConstants.TILE_SIZE * height / 2);
    return ws;
  }


}


class ExamplesLightEmAll {
  ArrayList<ArrayList<GamePiece>> horizontalBoard;



  void init() {
    
    horizontalBoard = new ArrayList<ArrayList<GamePiece>>();
    // generation of horizontal wire board
    for (int col = 0; col < 8; col++) {
      horizontalBoard.add(new ArrayList<GamePiece>());
      for (int row = 0; row < 9; row++) {
        if (row == 0) {
          horizontalBoard.get(col).add(new GamePiece(row, col, false, false, false, true, false, false));
        }
        else if (row == 8){
          horizontalBoard.get(col).add(new GamePiece(row, col, false, false, true, false, false, false));
        }
        else if (col == 0 && row == 4) {
          horizontalBoard.get(col).add(new GamePiece(row, col, false, true, true, true, false, false));
        }
        else if (col == 7 && row == 4) {
          horizontalBoard.get(col).add(new GamePiece(row, col, true, false, true, true, false, false));
        }
        else if (row == 4) {
          horizontalBoard.get(col).add(new GamePiece(row, col, true, true, true, true, false, false));
        }
        else {
          horizontalBoard.get(col).add(new GamePiece(row, col, false, false, true, true, false, false));
        }
      }
    }

  }


  void testDraw(Tester t) {


  }

  void testDrawPiece(Tester t) {
    this.init();

    
  }

  void testBigBang(Tester t) {
    this.init();

    int width = 8;
    int height = 9;

    LightEmAll world = new LightEmAll(horizontalBoard, null, null, width, height, 0, 0, 5);
    int worldWidth = IConstants.TILE_SIZE * world.width;
    int worldHeight = IConstants.TILE_SIZE * world.height;
    double tickRate = .1;
    world.bigBang(worldWidth, worldHeight, tickRate);
  }
}
























