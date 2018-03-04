/**
 * State class for a chess game. Outside classes will read and update this state
 * as the game progresses.
 */
public class ChessGameState {
  private boolean whoseTurn; // 0 for black, 1 for white.
  private Piece[][] board;  // Current board state.
  private Pair<Pair<Integer>> lastMove;  // Last move made.
  
  public ChessGameState() {
    board = new Piece[8][8];
  }
  
  public synchronized void update(Pair<Integer> start, Pair<Integer> end) {
    board[end.x][end.y] = board[start.x][start.y];
    board[start.x][start.y] = Piece.NONE;
    whoseTurn = !whoseTurn;
    lastMove = new Pair<Pair<Integer>>(start, end);
  }

  public synchronized boolean getWhoseTurn() {
  return whoseTurn;
  }
  //clone
  public synchronized Piece[][] getBoard() {
    Piece[][] temp = new Piece[board.length][];
    for(int i = 0;i < board.length; i++){
      for(int j = 0; j < board[i].length; j++){
        temp[i][j] = board[i][j];
      }
    }
   return temp;
  }
  //deep clone
  public synchronized Pair<Pair<Integer>> getLastMove() {
//    Pair<Integer> temp1 = new Pair<>(lastMove.x.x,lastMove.x.y);
//    Pair<Integer> temp2 = new Pair<>(lastMove.y.x,lastMove.y.y);
//    Pair<Pair<Integer>> temp = new Pair<Pair<Integer>>(temp1,temp2);
    return lastMove;
  }
  
  public static enum Piece {
    NONE, ROOK, KNIGHT, BISHOP, KING, QUEEN, PAWN;
  }
  
  public class Pair<T> {
    final T x, y;
    public Pair(T x, T y) {
      this.x = x;
      this.y = y;
    }
  }
}
