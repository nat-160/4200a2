

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.*;
import java.util.List;
import javafx.util.Pair;
import javax.swing.*;

//@SuppressWarnings("serial")
public class Board extends JPanel implements MouseListener, MouseMotionListener {
    // Resource location constants for piece images
    private static final String RESOURCES_WBISHOP_PNG = "resources/wbishop.png";
    private static final String RESOURCES_BBISHOP_PNG = "resources/bbishop.png";
    private static final String RESOURCES_WKNIGHT_PNG = "resources/wknight.png";
    private static final String RESOURCES_BKNIGHT_PNG = "resources/bknight.png";
    private static final String RESOURCES_WROOK_PNG = "resources/wrook.png";
    private static final String RESOURCES_BROOK_PNG = "resources/brook.png";
    private static final String RESOURCES_WKING_PNG = "resources/wking.png";
    private static final String RESOURCES_BKING_PNG = "resources/bking.png";
    private static final String RESOURCES_BQUEEN_PNG = "resources/bqueen.png";
    private static final String RESOURCES_WQUEEN_PNG = "resources/wqueen.png";
    private static final String RESOURCES_WPAWN_PNG = "resources/wpawn.png";
    private static final String RESOURCES_BPAWN_PNG = "resources/bpawn.png";

    private static final int DEPTH_LEVEL = 3;

    // Logical and graphical representations of board
    private Square[][] board;
    private GameWindow g;
    
    // List of pieces and whether they are movable
    public LinkedList<Piece> Bpieces;
    public LinkedList<Piece> Wpieces;
    public King Wk;
    public King Bk;

    public List<Square> movable;

    private boolean whiteTurn;

    private Piece currPiece;
    private int currX;
    private int currY;
    
    private CheckmateDetector cmd;

    public Board(GameWindow g) {
        initializeBoard(g);
    }

    public Board(GameWindow g, boolean init) {
        if (init) {
            initializeBoard(g);
        }
        else {
            this.g = g;
            board = new Square[8][8];
            whiteTurn = true;
        }
    }

    public void initializeBoard(GameWindow g) {
        this.g = g;
        board = new Square[8][8];
        Bpieces = new LinkedList<>();
        Wpieces = new LinkedList<>();
        setLayout(new GridLayout(8, 8, 0, 0));

        this.addMouseListener(this);
        this.addMouseMotionListener(this);

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                int xMod = x % 2;
                int yMod = y % 2;

                if ((xMod == 0 && yMod == 0) || (xMod == 1 && yMod == 1)) {
                    board[x][y] = new Square(this, 1, y, x);
                    this.add(board[x][y]);
                } else {
                    board[x][y] = new Square(this, 0, y, x);
                    this.add(board[x][y]);
                }
            }
        }

        initializePieces();

        this.setPreferredSize(new Dimension(400, 400));
        this.setMaximumSize(new Dimension(400, 400));
        this.setMinimumSize(this.getPreferredSize());
        this.setSize(new Dimension(400, 400));

        whiteTurn = true;

    }

    private void initializePieces() {

        for (int x = 0; x < 8; x++) {
            board[1][x].put(new Pawn(0, board[1][x], RESOURCES_BPAWN_PNG));
            board[6][x].put(new Pawn(1, board[6][x], RESOURCES_WPAWN_PNG));
        }
        
        board[7][3].put(new Queen(1, board[7][3], RESOURCES_WQUEEN_PNG));
        board[0][3].put(new Queen(0, board[0][3], RESOURCES_BQUEEN_PNG));
        
        Bk = new King(0, board[0][4], RESOURCES_BKING_PNG);
        Wk = new King(1, board[7][4], RESOURCES_WKING_PNG);
        board[0][4].put(Bk);
        board[7][4].put(Wk);

        board[0][0].put(new Rook(0, board[0][0], RESOURCES_BROOK_PNG));
        board[0][7].put(new Rook(0, board[0][7], RESOURCES_BROOK_PNG));
        board[7][0].put(new Rook(1, board[7][0], RESOURCES_WROOK_PNG));
        board[7][7].put(new Rook(1, board[7][7], RESOURCES_WROOK_PNG));

        board[0][1].put(new Knight(0, board[0][1], RESOURCES_BKNIGHT_PNG));
        board[0][6].put(new Knight(0, board[0][6], RESOURCES_BKNIGHT_PNG));
        board[7][1].put(new Knight(1, board[7][1], RESOURCES_WKNIGHT_PNG));
        board[7][6].put(new Knight(1, board[7][6], RESOURCES_WKNIGHT_PNG));

        board[0][2].put(new Bishop(0, board[0][2], RESOURCES_BBISHOP_PNG));
        board[0][5].put(new Bishop(0, board[0][5], RESOURCES_BBISHOP_PNG));
        board[7][2].put(new Bishop(1, board[7][2], RESOURCES_WBISHOP_PNG));
        board[7][5].put(new Bishop(1, board[7][5], RESOURCES_WBISHOP_PNG));
        
        
        for(int y = 0; y < 2; y++) {
            for (int x = 0; x < 8; x++) {
                Bpieces.add(board[y][x].getOccupyingPiece());
                Wpieces.add(board[7-y][x].getOccupyingPiece());
            }
        }
        
        cmd = new CheckmateDetector(this /*, Wpieces, Bpieces, wk, bk*/);
    }

    public Square[][] getSquareArray() {
        return this.board;
    }

    public boolean getTurn() {
        return whiteTurn;
    }

    public void setCurrPiece(Piece p) {
        this.currPiece = p;
    }

    public Piece getCurrPiece() {
        return this.currPiece;
    }

    @Override
    public void paintComponent(Graphics g) {
        // super.paintComponent(g);

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Square sq = board[y][x];
                if (sq != null) {
                    sq.paintComponent(g);
                }
            }
        }

        if (currPiece != null) {
            if ((currPiece.getColor() == 1 && whiteTurn)
                    || (currPiece.getColor() == 0 && !whiteTurn)) {
                final Image i = currPiece.getImage();
                if (i != null) {
                    g.drawImage(i, currX, currY, null);
                }
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        currX = e.getX();
        currY = e.getY();

        Square sq = (Square) this.getComponentAt(new Point(e.getX(), e.getY()));

        if (sq.isOccupied()) {
            currPiece = sq.getOccupyingPiece();
            if (currPiece.getColor() == 0 && whiteTurn)
                return;
            if (currPiece.getColor() == 1 && !whiteTurn)
                return;
            sq.setDisplay(false);
        }
        repaint();
    }

    private Board copyBoard() {
        Board newBoard = new Board(g, false);

        int i = 0;
        int j = 0;

        // Reposition the pieces based on Board being copied
        newBoard.Bpieces = new LinkedList<Piece>();
        newBoard.Wpieces = new LinkedList<Piece>();
        for (i = 0; i < 8; i++) {
            for (j = 0; j < 8; j++) {
                newBoard.board[i][j] = new Square(newBoard, 0, j, i);
                Piece piece = this.board[i][j].getOccupyingPiece();
                if (piece != null) {
                    Piece newPiece = piece.copyPiece();
                    newBoard.board[i][j].put(newPiece);
                    if (piece.getColor() == 0) {
                        newBoard.Bpieces.add(newPiece);
                        if (newPiece.getClass().getName().equals("King")) {
                            newBoard.Bk = (King) newPiece;
                        }
                    }
                    else {
                        newBoard.Wpieces.add(newPiece);
                        if (newPiece.getClass().getName().equals("King")) {
                            newBoard.Wk = (King) newPiece;
                        }
                    }
                    if (piece.equals(currPiece)) {
                        newBoard.currPiece = newPiece;
                    }
                }
            }
        }

        newBoard.currX = this.currX;
        newBoard.currY = this.currY;
        newBoard.whiteTurn = this.whiteTurn;

        newBoard.cmd = new CheckmateDetector(newBoard /*, newBoard.Wpieces, newBoard.Bpieces, wk, bk*/);

        return newBoard;
    }

    private Square FindSquare(Square sq) {

        return this.board[sq.getYNum()][sq.getXNum()];
    }

    private Piece FindPiece(Piece chessPiece) {

        int i = 0;
        int j = 0;

        for (i = 0; i < this.board.length; i++) {
            for (j = 0; j < this.board[i].length; j++) {
                Piece piece = this.board[i][j].getOccupyingPiece();
                if (piece != null) {
                    if (piece.getColor() == chessPiece.getColor()) {
                        if (piece.getClass().getName().equals(chessPiece.getClass().getName())) {
                            if ((piece.getPosition().getXNum() == chessPiece.getPosition().getXNum()) &&
                                    (piece.getPosition().getYNum() == chessPiece.getPosition().getYNum())) {
                                return piece;
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    /**
     * Calculate the static evaluation of the board
     * @param turnSelector True for White
     * @return rating for board based on turn
     */
    private int MinMax_CalcVal(boolean turnSelector) {
        if (cmd.blackCheckMated()) return Integer.MAX_VALUE;
        if (cmd.whiteCheckMated()) return Integer.MIN_VALUE;
        int valMinMax = 0;
        for (Piece piece : Wpieces) {
            int pieceVal = pieceValue(piece);
            valMinMax += 100 * pieceVal;
            /*
            for (Square square : piece.getLegalMoves(this)) {
                Piece occPiece = square.getOccupyingPiece();
                if (occPiece != null) {
                    int occVal = pieceValue(occPiece);
                    if (occPiece.getColor() == 0 && pieceVal <= occVal)
                        valMinMax += pieceVal - occVal;
                }
            }

             */
        }
        for (Piece piece : Bpieces) {
            int pieceVal = pieceValue(piece);
            valMinMax -= 100 * pieceVal;
            /*
            for (Square square : piece.getLegalMoves(this)) {
                Piece occPiece = square.getOccupyingPiece();
                if (occPiece != null) {
                    int occVal = pieceValue(occPiece);
                    if (occPiece.getColor() == 1 && pieceVal <= occVal)
                        valMinMax -= pieceVal - occVal;
                }
            }

             */
        }
        // generate some noise, so that no 2 ratings are equal
        return valMinMax + (new Random()).nextInt(100);
    }

    /**
     * Get point value of a particular piece
     * @param p Piece
     * @return Point rating of piece
     */
    private int pieceValue(Piece p) {
        return switch (p.getClass().getName()) {
            case "Bishop", "Knight" -> 3;
            case "Rook" -> 5;
            case "Queen" -> 9;
            case "King" -> 100;
            default -> 1;
        };
    }

    private Pair<Integer, Pair<Piece, Square>> MinMax_SelectPiece(boolean turnSelector, int depthLevel, String prevPos, Stack<String> futureMoves) {
        return MinMax_SelectPiece(turnSelector, depthLevel, prevPos, futureMoves, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Reset all Pawns First Move Status
     */
    private void resetPawnStatus(){
        for (Piece piece: Wpieces)
            if (piece instanceof Pawn)
                if (piece.getPosition().getYNum() == 6)
                    ((Pawn) piece).setMoved(false);
        for (Piece piece : Bpieces)
            if (piece instanceof Pawn)
                if (piece.getPosition().getYNum() == 1)
                    ((Pawn) piece).setMoved(false);
    }

    private class EvalBoard{
        int[][] board;
        EvalBoard(Board old){
            board = new int[8][8];
            Square[][] oldGrid = old.board;
            for(int x=0;x<8;x++){
                for(int y=0;y<8;y++){
                    Piece p = oldGrid[x][y].getOccupyingPiece();
                    if(p != null) {
                        board[x][y] = getValue(p);
                        if (p.getColor()==0) board[x][y] *= -1;
                    }
                    else board[x][y] = 0;
                }
            }
        }
        EvalBoard(EvalBoard old){
            board = new int[8][8];
            for(int x=0;x<8;x++)
                System.arraycopy(old.board[x], 0, board[x], 0, 8);
        }
        int getValue(Piece p){
            return switch(p.getClass().getName()){
                case "Pawn" -> 1;
                case "Knight" -> 3;
                case "Bishop" -> 4;
                case "Rook" -> 6;
                case "Queen" -> 9;
                case "King" -> 100;
                default -> 0;
            };
        }
        boolean gameOver(boolean maximizingPlayer){
            return moves(maximizingPlayer).size() == 0;
        }
        int evaluation(){
            if (inCheck(true)) return Integer.MIN_VALUE;
            if (inCheck(false)) return Integer.MAX_VALUE;
            int sum = 0;
            for(int[] row : board)
                for(int sq : row)
                    sum += sq;
            return sum;
        }
        EvalBoard copyBoard(){
            return new EvalBoard(this);
        }
        boolean turn(Coord piece, Coord square, boolean maximizingPlayer){
            board[square.x][square.y] = board[piece.x][piece.y];
            board[piece.x][piece.y] = 0;
            return true;
        }
        boolean inCheck(boolean maximizingPlayer){
            Coord kingPos = new Coord(-1,-1);
            for (int x = 0; x < 8; x++) {
                for (int y = 0; y < 8; y++) {
                    if (board[x][y] == (maximizingPlayer ? 100 : -100)) {
                        kingPos.x = x;
                        kingPos.y = y;
                    }
                }
            }
            if (kingPos.x==-1 || kingPos.y==-1) return true;
            // check right + down
            int x = kingPos.x+1, y = kingPos.y+1;
            while (x<8 && y<8) {
                switch (board[x][y]) {
                    case 4,9,100:
                        if (!maximizingPlayer) return true;
                        break;
                    case -1,-4,-9,-100:
                        if (maximizingPlayer) return true;
                        break;
                    case 0: break;
                    default: x = 8;
                }
                x++; y++;
            }
            // check right + up
            x = kingPos.x+1; y = kingPos.y-1;
            while (x<8 && y>=0) {
                switch (board[x][y]) {
                    case 1,4,9,100:
                        if (!maximizingPlayer) return true;
                        break;
                    case -4,-9,-100:
                        if (maximizingPlayer) return true;
                        break;
                    case 0: break;
                    default: x = 8;
                }
                x++; y--;
            }
            // check left + down
            x = kingPos.x-1; y = kingPos.y+1;
            while (x>=0 && y<8) {
                switch (board[x][y]) {
                    case 4,9,100:
                        if (!maximizingPlayer) return true;
                        break;
                    case -1,-4,-9,-100:
                        if (maximizingPlayer) return true;
                        break;
                    case 0: break;
                    default: x = -1;
                }
                x--; y++;
            }
            // check left + up
            x = kingPos.x-1; y = kingPos.y-1;
            while (x>=0 && y>=0) {
                switch (board[x][y]) {
                    case 1,4,9,100:
                        if (!maximizingPlayer) return true;
                        break;
                    case -4,-9,-100:
                        if (maximizingPlayer) return true;
                        break;
                    case 0: break;
                    default: x = -1;
                }
                x--; y--;
            }
            // check down
            x = kingPos.x; y = kingPos.y+1;
            while (y<8) {
                switch (board[x][y]) {
                    case 6,9,100:
                        if (!maximizingPlayer) return true;
                        break;
                    case -6,-9,-100:
                        if (maximizingPlayer) return true;
                        break;
                    case 0: break;
                    default: y = 8;
                }
                y++;
            }
            // check up
            y = kingPos.y-1;
            while (y>=0) {
                switch (board[x][y]) {
                    case 6,9,100:
                        if (!maximizingPlayer) return true;
                        break;
                    case -6,-9,-100:
                        if (maximizingPlayer) return true;
                        break;
                    case 0: break;
                    default: y = -1;
                }
                y--;
            }
            // check left
            x = kingPos.x-1; y = kingPos.y;
            while (x>=0) {
                switch (board[x][y]) {
                    case 6,9,100:
                        if (!maximizingPlayer) return true;
                        break;
                    case -6,-9,-100:
                        if (maximizingPlayer) return true;
                        break;
                    case 0: break;
                    default: x = -1;
                }
                x--;
            }
            // check right
            x = kingPos.x+1;
            while (x<8) {
                switch (board[x][y]) {
                    case 6,9,100:
                        if (!maximizingPlayer) return true;
                        break;
                    case -6,-9,-100:
                        if (maximizingPlayer) return true;
                        break;
                    case 0: break;
                    default: x = 8;
                }
                x++;
            }
            // check horsey (there is probably easier way but its only 8 sqs so whatever)
            int horse = (maximizingPlayer ? -3 : 3);
            if (kingPos.x+1<8 && kingPos.y+2<8 && board[kingPos.x+1][kingPos.y+2]==horse) return true;
            if (kingPos.x+2<8 && kingPos.y+1<8 && board[kingPos.x+2][kingPos.y+1]==horse) return true;
            if (kingPos.x+2<8 && kingPos.y-1>=0 && board[kingPos.x+2][kingPos.y-1]==horse) return true;
            if (kingPos.x+1<8 && kingPos.y-2>=0 && board[kingPos.x+1][kingPos.y-2]==horse) return true;
            if (kingPos.x-1>=0 && kingPos.y-2>=0 && board[kingPos.x-1][kingPos.y-2]==horse) return true;
            if (kingPos.x-2>=0 && kingPos.y-1>=0 && board[kingPos.x-2][kingPos.y-1]==horse) return true;
            if (kingPos.x-2>=0 && kingPos.y+1<8 && board[kingPos.x-2][kingPos.y+1]==horse) return true;
            return (kingPos.x-1>=0 && kingPos.y+2<8 && board[kingPos.x-1][kingPos.y+2]==horse);
        }
        boolean validMove(Coord piece, Coord square, boolean maximizingPlayer){
            // same square
            if(piece.x==square.x && piece.y==square.y) return false;
            // out of bounds
            if(square.x<0 || square.x>=8 || square.y<0 || square.y>=8) return false;
            // pawn rules
            if(Math.abs(board[piece.x][piece.y])==1) {
                if (piece.x == square.x) {
                    if (board[square.x][square.y] != 0) return false;
                }
                else if (maximizingPlayer) {
                    if (board[square.x][square.y]>=0) return false;
                }
                else {
                    if (board[square.x][square.y]<=0) return false;
                }
            }
            // cannot move into check
            EvalBoard test = copyBoard();
            test.turn(piece, square, maximizingPlayer);
            if (test.inCheck(maximizingPlayer))
                return false;
            // cannot attack team
            return board[square.x][square.y]*board[piece.x][piece.y]<=0;
        }
        HashSet<Pair<Coord, Coord>> moves(boolean maximizingPlayer){
            HashSet<Pair<Coord,Coord>> moves = new HashSet<>();
            for(int x=0;x<8;x++){
                for(int y=0;y<8;y++){
                    Coord piece = new Coord(x,y);
                    int value = board[x][y];
                    if (!maximizingPlayer) value *= -1;
                    if (value == 1){
                        if (maximizingPlayer) {
                            if (y == 6)
                                if (validMove(piece, new Coord(x, 4), true))
                                    moves.add(new Pair<>(piece, new Coord(x, 4)));
                            else {
                                if (validMove(piece, new Coord(x+1,y-1), true))
                                    moves.add(new Pair<>(piece, new Coord(x+1,y-1)));
                                if (validMove(piece, new Coord(x,y-1), true))
                                    moves.add(new Pair<>(piece, new Coord(x,y-1)));
                                if (validMove(piece, new Coord(x-1,y-1), true))
                                    moves.add(new Pair<>(piece, new Coord(x-1,y-1)));
                            }
                        } else {
                            if (y == 1)
                                if (validMove(piece, new Coord(x, 3), false))
                                    moves.add(new Pair<>(piece, new Coord(x, 3)));
                            else {
                                if (validMove(piece, new Coord(x+1,y+1), true))
                                    moves.add(new Pair<>(piece, new Coord(x+1,y+1)));
                                if (validMove(piece, new Coord(x,y+1), true))
                                    moves.add(new Pair<>(piece, new Coord(x,y+1)));
                                if (validMove(piece, new Coord(x-1,y+1), true))
                                    moves.add(new Pair<>(piece, new Coord(x-1,y+1)));
                            }
                        }
                    }
                    if (value == 3)
                        for(int i=-2;i<=2;i++)
                            for(int j=-2;j<=2;j++)
                                if(Math.abs(i)+Math.abs(j)==3)
                                    if(validMove(piece, new Coord(x+i,y+j), maximizingPlayer))
                                        moves.add(new Pair<>(piece, new Coord(x+i,y+j)));
                    boolean dir1 = true, dir2 = true, dir3 = true, dir4 = true;
                    if (value == 4 || value == 9)
                        for(int i=1;i<=7;i++) {
                            if (validMove(piece, new Coord(x + i, y + i), maximizingPlayer) && dir1)
                                moves.add(new Pair<>(piece, new Coord(x + i, y + i)));
                            else
                                dir1 = false;
                            if (validMove(piece, new Coord(x + i, y - i), maximizingPlayer) && dir2)
                                moves.add(new Pair<>(piece, new Coord(x + i, y - i)));
                            else
                                dir2 = false;
                            if (validMove(piece, new Coord(x - i, y - i), maximizingPlayer) && dir3)
                                moves.add(new Pair<>(piece, new Coord(x - i, y - i)));
                            else
                                dir3 = false;
                            if (validMove(piece, new Coord(x - i, y + i), maximizingPlayer) && dir4)
                                moves.add(new Pair<>(piece, new Coord(x - i, y + i)));
                            else
                                dir4 = false;
                        }
                    dir1 = true; dir2 = true; dir3 = true; dir4 = true;
                    if (value == 6 || value == 9)
                        for(int i=1;i<=7;i++) {
                            if (validMove(piece, new Coord(x + i, y), maximizingPlayer) && dir1)
                                moves.add(new Pair<>(piece, new Coord(x + i, y)));
                            else
                                dir1 = false;
                            if (validMove(piece, new Coord(x - i, y), maximizingPlayer) && dir2)
                                moves.add(new Pair<>(piece, new Coord(x - i, y)));
                            else
                                dir2 = false;
                            if (validMove(piece, new Coord(x, y + i), maximizingPlayer) && dir3)
                                moves.add(new Pair<>(piece, new Coord(x, y + i)));
                            else
                                dir3 = false;
                            if (validMove(piece, new Coord(x, y - i), maximizingPlayer) && dir4)
                                moves.add(new Pair<>(piece, new Coord(x, y - i)));
                            else
                                dir4 = false;
                        }
                    if (value == 100)
                        for(int i=-1;i<=1;i++)
                            for(int j=-1;j<=1;j++)
                                if(validMove(piece, new Coord(x+i,y+j), maximizingPlayer))
                                    moves.add(new Pair<>(piece, new Coord(x+i,y+j)));
                }
            }
            return moves;
        }
    }
    class Coord {
        int x, y;
        Coord (int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
    private Pair<Integer, Pair<Coord, Coord>> MinMax_SelectMove(EvalBoard position, int depth, int alpha, int beta, boolean maximizingPlayer) {
        if (depth == 0 || position.gameOver(maximizingPlayer))
            return new Pair<>(position.evaluation(), new Pair<>(new Coord(10,10), new Coord(10,10)));
        int bestEval = maximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        Pair<Coord, Coord> bestMove = null;
        if (maximizingPlayer) {
            for (Pair<Coord, Coord> move : position.moves(true)) {
                EvalBoard child = position.copyBoard();
                child.turn(move.getKey(), move.getValue(), true);
                int eval = MinMax_SelectMove(child, depth - 1, alpha, beta, false).getKey();
                if (eval >= bestEval) {
                    bestEval = eval;
                    bestMove = move;
                }
                alpha = Math.max(alpha, eval);
                if (beta <= alpha)
                    break;
            }
        } else {
            for (Pair<Coord, Coord> move : position.moves(false)) {
                EvalBoard child = position.copyBoard();
                child.turn(move.getKey(), move.getValue(), false);
                int eval = MinMax_SelectMove(child, depth - 1, alpha, beta, true).getKey();
                if (eval <= bestEval) {
                    bestEval = eval;
                    bestMove = move;
                }
                beta = Math.min(beta, eval);
                if (beta <= alpha)
                    break;
            }
        }
        // This shouldn't happen!
        if(bestMove == null) {
            bestMove = new Pair<>(new Coord(-1, -1), new Coord(-1, -1));
        }
        return new Pair<>(bestEval, bestMove);
    }

    private Pair<Integer, Pair<Piece, Square>> MinMax_SelectPiece(boolean turnSelector, int depthLevel, String prevPos, Stack<String> futureMoves, int alpha, int beta) {
        // Get the Game Tree Depth from UI
        int gameTreeDepth = getUIDepth();
        // Get static eval if limit reached
        if (depthLevel > gameTreeDepth) return new Pair<>(MinMax_CalcVal(turnSelector), null);
        // get list of pieces
        LinkedList<Piece> pieces = turnSelector ? Wpieces : Bpieces;
        // initiate new console output
        Stack<String> bestFutureMoves = new Stack<>();
        // initialize eval depending on turn
        int oppMinMaxVal = turnSelector ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        Piece oppPiece = null;
        Square oppSq = null;
        for (Piece pieceTemp : pieces) {
            if (pieceTemp != null) {
                Stack<String> tempFutureMoves = new Stack<>();
                // Get best square for piece
                Pair<Integer, Pair<Integer, Pair<Integer, Square>>> r =
                        MinMax_SelectSquare(pieceTemp, turnSelector, depthLevel,
                                prevPos + pieceTemp.getPositionName() + "\r\n", tempFutureMoves, alpha, beta);
                // Update alpha & beta, because we are on same level
                int valMinMax = r.getValue().getValue().getKey();
                if (turnSelector) {
                    alpha = Math.max(r.getKey(), valMinMax);
                    beta = r.getValue().getKey();
                } else {
                    alpha = r.getKey();
                    beta = Math.min(r.getValue().getKey(), valMinMax);
                }
                // Break if appropriate
                if (beta <= alpha) break;
                // If the move is an improvement, store it
                Square sqTempNext = r.getValue().getValue().getValue();
                if (((turnSelector) && ((oppSq == null) || (valMinMax > oppMinMaxVal))) || ((!turnSelector) && ((oppSq == null) || (valMinMax < oppMinMaxVal)))) {
                    oppSq = sqTempNext;
                    oppPiece = pieceTemp;
                    oppMinMaxVal = valMinMax;
                    bestFutureMoves.removeAllElements();
                    bestFutureMoves.addAll(tempFutureMoves);
                }
                if (depthLevel == 0) resetPawnStatus();
            }
        }
        // Select a default square, if somehow none was found
        if(oppSq == null) {
            for (Piece p : pieces) {
                if (p.getLegalMoves(this).size() != 0) {
                    oppPiece = p;
                    oppSq = p.getLegalMoves(this).get(0);
                }
            }
        }
        // Set console output
        String oppSqPosName = (oppSq == null) ? "---" : oppSq.getPositionName();
        futureMoves.addAll(bestFutureMoves);
        assert oppPiece != null;
        futureMoves.push(oppPiece.getPositionName() +
                " to " + oppSqPosName +
                " Level: " + Integer.toString(depthLevel) +
                " Val: " + Integer.toString(oppMinMaxVal));

        // Return rating, best piece and best move
        return (new Pair<>(oppMinMaxVal, new Pair<>(oppPiece, oppSq)));
    }

    private int getUIDepth() {
        String strDepth = g.depth.getText();
        int gameTreeDepth = Integer.parseInt(strDepth.substring(17).trim());
        return (gameTreeDepth > 0) ? gameTreeDepth : DEPTH_LEVEL;
    }

    private Pair<Integer, Pair<Integer, Pair<Integer, Square>>> MinMax_SelectSquare(Piece chessPiece, boolean turnSelector, int depthLevel, String prevPos, Stack<String> futureMoves) {
        return MinMax_SelectSquare(chessPiece, turnSelector, depthLevel, prevPos, futureMoves, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    private Pair<Integer, Pair<Integer, Pair<Integer, Square>>> MinMax_SelectSquare(Piece chessPiece, boolean turnSelector, int depthLevel, String prevPos, Stack<String> futureMoves, int alpha, int beta) {
        // Set best found square and evaluation depending on player
        Square nextMoveSq = null;
        int nextMoveMinMax = turnSelector ? Integer.MAX_VALUE : Integer.MIN_VALUE;

        // Instantiate console output
        Stack<String> bestFutureMoves = new Stack<>();

        // Get list of moves to evaluate
        List<Square> possibleMoves = chessPiece.getLegalMoves(this);

        // Find the next square to occupy
        for (Square sq : possibleMoves) {
            // Double check if the square is empty or occupied by an opposing piece (white=1)
            if ((sq.getOccupyingPiece() == null) ||
                    (turnSelector && (sq.getOccupyingPiece().getColor() == 0)) ||
                    (!turnSelector && (sq.getOccupyingPiece().getColor() == 1))
                    )
            {
                // instantiate console output
                Stack<String> tempFutureMoves = new Stack<>();

                // Backup current Move, so it can be undone later
                Piece capturedPiece = sq.isOccupied() ? sq.getOccupyingPiece() : null;
                Square currSq = chessPiece.getPosition();

                // if move is invalid, skip (to evade check)
                boolean success = this.takeTurnEx(chessPiece, sq, turnSelector, prevPos, depthLevel);
                if (!success) continue;

                // Get the best opponent move after current move
                Pair<Integer, Pair<Piece, Square>> r = MinMax_SelectPiece(
                        !turnSelector, depthLevel + 1, prevPos, tempFutureMoves, alpha, beta);
                int valMinMax = r.getKey();

                // Undo the move
                chessPiece.move(currSq);
                if (capturedPiece != null) {
                    if ((capturedPiece.getColor() == 0) && (!Bpieces.contains(capturedPiece))) {
                        Bpieces.add(capturedPiece);
                    }
                    else if ((capturedPiece.getColor() == 1) && (!Wpieces.contains(capturedPiece))) {
                        Wpieces.add(capturedPiece);
                    }
                    capturedPiece.move(sq);
                }
                cmd.update();

                // If a good move has been found, store it
                if ((turnSelector && valMinMax < nextMoveMinMax)
                        || (!turnSelector && valMinMax > nextMoveMinMax)
                        || (valMinMax == Integer.MAX_VALUE
                        || valMinMax == Integer.MIN_VALUE)
                ) {
                    nextMoveSq = sq;
                    nextMoveMinMax = valMinMax;
                    bestFutureMoves.removeAllElements();
                    bestFutureMoves.addAll(tempFutureMoves);
                }
                // Update alpha and beta values
                if (turnSelector) {
                    alpha = Math.max(alpha, nextMoveMinMax);
                } else {
                    beta = Math.min(beta, nextMoveMinMax);
                }
                // Cutoff
                if (beta <= alpha) break;
            }
        }

        futureMoves.addAll(bestFutureMoves);
        return new Pair<>(alpha, new Pair<>(beta, new Pair<>(nextMoveMinMax, nextMoveSq)));
    }

    private boolean EvadeCheck() {
        Stack<String> tempFutureMoves = new Stack<String>();
        // Try to find best square to move the King
        Pair<Integer, Pair<Coord, Coord>> r = MinMax_SelectMove(new EvalBoard(this), getUIDepth(), Integer.MIN_VALUE, Integer.MAX_VALUE, false);
        //Pair<Integer, Pair<Integer, Pair<Integer, Square>>> r = MinMax_SelectSquare(Bk, false, 0, Bk.getPositionName() + "\r\n", tempFutureMoves);
        Square sq = getSquareArray()[r.getValue().getValue().x][r.getValue().getValue().y];

        if (!takeTurnEx(Bk, sq, false, "", 0)) {
            List<Square> kingsMoves = Bk.getLegalMoves(this);

            // If best square is not available pick any available square
            for (Square kingsMove : kingsMoves) {
                sq = kingsMove;
                if (!cmd.testMove(Bk, sq)) continue;
                if (cmd.wMoves.get(sq.hashCode()).isEmpty()) {
                    takeTurnEx(Bk, sq, false, "", 0);
                    return true;
                }
            }
        }
        else {
            return true;
        }


        return false;
    }
    private boolean takeTurnEx(Piece piece, Square sq, boolean turnSelector, String prevPos, int depthLevel) {
        String newText = "";
        boolean success = false;
        if (piece != null) {
            if (piece.getColor() == 0 && turnSelector) {
                newText = prevPos + "Black Piece on White's turn\r\n";
                return false;
            }
            else if (piece.getColor() == 1 && !turnSelector) {
                newText = prevPos + "White Piece on Black's turn\r\n";
                return false;
            }
            else {
                List<Square> legalMoves = piece.getLegalMoves(this);
                movable = cmd.getAllowableSquares(turnSelector);

                if (legalMoves.contains(sq) && movable.contains(sq)
                        && cmd.testMove(piece, sq)) {
                    sq.setDisplay(true);
                    piece.move(sq);
                    cmd.update();
                    success = true;

                    if (g.watchMoves.isSelected()) {
                        int valMinMax = MinMax_CalcVal(turnSelector);
                        newText = prevPos + piece.getPositionName();
                        newText = newText + " Level: " + Integer.toString(depthLevel);
                        newText = newText + " Val: " + Integer.toString(valMinMax) + "\r\n";
                        g.moves.setText(newText);
                        g.moves.update(g.moves.getGraphics());
                    }

                    if (cmd.blackCheckMated()) {
                        newText = newText + "Black Checkmated\r\n";
                    } else if (cmd.whiteCheckMated()) {
                        newText = newText + "White Checkmated\r\n";
                    }
                    else {
                        if (cmd.blackInCheck()) {
                            newText = newText + "Black in Check\r\n";
                        } else if (cmd.whiteInCheck()) {
                            newText = newText + "White in Check\r\n";
                        }
                    }
                }
            }
        }

        if (g.watchMoves.isSelected()) {
            this.update(this.getGraphics());
        }

        return success;
    }

    private void takeTurn(Square sq) {
        String newText = "";
        if (currPiece != null) {
            if (currPiece.getColor() == 0 && whiteTurn) {
                newText = "Black Piece on White's turn\r\n";
            }
            else if (currPiece.getColor() == 1 && !whiteTurn) {
                newText = "White Piece on Black's turn\r\n";
            }
            else {
                List<Square> legalMoves = currPiece.getLegalMoves(this);
                movable = cmd.getAllowableSquares(whiteTurn);

                if (legalMoves.contains(sq) && movable.contains(sq)
                        && cmd.testMove(currPiece, sq)) {
                    sq.setDisplay(true);
                    currPiece.move(sq);
                    cmd.update();

                    newText = currPiece.getPositionName() + "\r\n";

                    if (cmd.blackCheckMated()) {
                        currPiece = null;
                        repaint();
                        this.removeMouseListener(this);
                        this.removeMouseMotionListener(this);
                        g.checkmateOccurred(0);
                        newText = newText + "Black Checkmated\r\n";
                    } else if (cmd.whiteCheckMated()) {
                        currPiece = null;
                        repaint();
                        this.removeMouseListener(this);
                        this.removeMouseMotionListener(this);
                        g.checkmateOccurred(1);
                        newText = newText + "White Checkmated\r\n";
                    }
                    else {
                        boolean bInCheck = cmd.blackInCheck();
                        if (bInCheck) {
                            newText = newText + "Black in Check\r\n";

                            g.gameStatus.setText("Status: Computing");
                            g.buttons.update(g.buttons.getGraphics());

                            if (EvadeCheck()) {
                                currPiece = Bk;
                                whiteTurn = !whiteTurn;
                                newText = newText + "Check evaded\r\n";

                                g.gameStatus.setText("Status: Move to " + currPiece.getPositionName());
                                g.buttons.update(g.buttons.getGraphics());
                            }
                        } else if (cmd.whiteInCheck()) {
                            newText = newText + "White in Check\r\n";
                        }

                        currPiece = null;
                        whiteTurn = !whiteTurn;
                        if (!whiteTurn) {
                            // Let Computer pick the next turn
                            g.gameStatus.setText("Status: Computing");
                            g.buttons.update(g.buttons.getGraphics());

                            Stack<String> futureMoves = new Stack<String>();
                            Pair<Integer, Pair<Coord, Coord>> r = MinMax_SelectMove(new EvalBoard(this),getUIDepth(),Integer.MIN_VALUE, Integer.MAX_VALUE, false);
                            Coord pieceC = r.getValue().getKey();
                            Piece currPiece = getSquareArray()[pieceC.x][pieceC.y].getOccupyingPiece();
                            Coord squareC = r.getValue().getValue();
                            Square currSquare = getSquareArray()[squareC.x][squareC.y];
                            boolean success = takeTurnEx(currPiece, currSquare, whiteTurn, newText, 0);
                            if (!success) System.out.println("Could not move " + pieceC.x + "," + pieceC.y + " to " + squareC.x + "," + squareC.y);
                            whiteTurn = true; // Change the turn back to White

                            //newText = g.moves.getText();
                            newText += "Anticipated Moves:\r\n";
                            String futureMove = futureMoves.isEmpty() ? "" : futureMoves.pop();
                            while (!futureMoves.isEmpty()) {
                                newText += futureMove + "\r\n";
                                futureMove = futureMoves.pop();
                            }

                            g.gameStatus.setText("Status: Move to " + currPiece.getPositionName());
                            g.buttons.update(g.buttons.getGraphics());
                        }
                    }
                } else {
                    currPiece.getPosition().setDisplay(true);
                    currPiece = null;
                    newText = newText + "Invalid Move\r\n";
                }
            }
        }
        else {
            newText = "Null Piece\r\n";
        }

        g.moves.setText(newText);

        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Square sq = (Square) this.getComponentAt(new Point(e.getX(), e.getY()));

        takeTurn(sq);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        currX = e.getX() - 24;
        currY = e.getY() - 24;

        repaint();
    }

    // Irrelevant methods, do nothing for these mouse behaviors
    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

}