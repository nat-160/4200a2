import greenfoot.*;
import java.util.*;
import javax.swing.*;
public class Chess extends World{
    int depth = 4;
    Board board = new Board();
    boolean gameOver = false, zaidiMode = false;
    GreenfootSound moveSound = new GreenfootSound("move.mp3");

    /** 
     * Creates new board and displays it
     */
    public Chess(){
        super(8, 8, 100);
        if(depth==4){
            Object[] possibilities = {1, 2, 3, 4, 5, 6, "zaidi mode"};
            Object o = JOptionPane.showInputDialog(
                    new JFrame(),
                    "Select difficulty:",
                    "Chess",
                    JOptionPane.PLAIN_MESSAGE,
                    new ImageIcon(),
                    possibilities,
                    1);
            if(o.equals("zaidi mode")){
                zaidiMode = true;
                depth = -1;
            } else
                depth = (Integer) o;
            System.out.println("Chosen depth: "+depth);
        }
        setBackground("tiles.png");
        display();
    }

    String formatTime(long milliseconds){
        StringBuilder s = new StringBuilder();
        s.append(milliseconds / 60000 % 60);
        s.append('m');
        s.append(milliseconds / 1000 % 60);
        s.append('s');
        s.append(milliseconds % 1000);
        s.append("ms");
        return s.toString();
    }

    /**
     * Handles mouse input and gets computer moves
     */
    public void act(){
        MouseInfo m = Greenfoot.getMouseInfo();
        if(m!=null && m.getButton()==1 && Greenfoot.mouseClicked(null) && !gameOver){
            Actor a = m.getActor();
            if(a!=null){
                Move test = new Move(a.getX(),a.getY(),m.getX(),m.getY());
                //System.out.println("Player attempted to move "+test);
                for(Move move : board.getMoves(true)){
                    if(move.equals(test) && board.isValidMove(test, true)){
                        gameOver = true;
                        board = new Board(board, move);
                        display();
                        moveSound.play();
                        //System.out.println("Moved "+move);
                        if(board.gameOver(false)){
                            System.out.println("The player wins!");
                            break;
                        }
                        System.out.println("Computer has "+board.getChildren(false).size()+" moves");
                        Greenfoot.delay(100);
                        if(zaidiMode)
                            board = maximin(board);
                        else {
                            long startTime = System.currentTimeMillis();
                            board = minimax(board, depth, -999, 999, false).board;
                            long endTime = System.currentTimeMillis();
                            System.out.println("Minimax took "+formatTime(endTime - startTime));
                        }
                        display();
                        moveSound.play();
                        if(board.gameOver(true)){
                            System.out.println("The computer wins!");
                            break;
                        }
                        System.out.println("Player has "+board.getChildren(true).size()+" moves");
                        gameOver = false;
                        break;
                    }
                }
            }
        }
    }

    /**
     * Display Board onto the World
     * Does not update anything!
     */
    void display(){
        removeObjects(getObjects(null));
        for(int x=0;x<8;x++)
            for(int y=0;y<8;y++){
                Actor a = null;
                boolean player = board.data[x][y] > 0;
                int piece = Math.abs(board.data[x][y]);
                if(piece==Piece.PAWN)
                    a = new Pawn(player, zaidiMode);
                else if(piece==Piece.KNIGHT)
                    a = new Knight(player);
                else if(piece==Piece.BISHOP)
                    a = new Bishop(player);
                else if(piece==Piece.ROOK)
                    a = new Rook(player);
                else if(piece==Piece.QUEEN)
                    a = new Queen(player);
                else if(piece==Piece.KING)
                    a = new King(player);
                if(a!=null)
                    addObject(a, x, y);
            }
    }

    /**
     * First legal move, as if the AI set to -1
     * Good for testing
     */
    Board maximin(Board position){
        return position.getChildren(false).toArray(new Board[1])[0];
    }

    /**
     * MiniMax, with alpha-beta pruning
     */
    MinMaxData minimax(Board position, int depth, int alpha, int beta, boolean maximizingPlayer){
        if(depth==0 || position.gameOver(maximizingPlayer))
            return new MinMaxData(position.evaluation(), position);
        if(maximizingPlayer){
            MinMaxData maxEval = new MinMaxData(-999, position);
            for(Move m : position.getChildren(true)){
                Board child = new Board(position, m);
                MinMaxData eval = minimax(child, depth-1, alpha, beta, false);
                if(eval.eval > maxEval.eval)
                    maxEval = new MinMaxData(eval.eval, child);
                alpha = Math.max(alpha, eval.eval);
                if(beta<=alpha) break;
            }
            return maxEval;
        } else {
            MinMaxData minEval = new MinMaxData(999, position);
            for(Move m : position.getChildren(false)){
                Board child = new Board(position, m);
                MinMaxData eval = minimax(child, depth-1, alpha, beta, true);
                if(eval.eval < minEval.eval)
                    minEval = new MinMaxData(eval.eval, child);
                beta = Math.min(beta, eval.eval);
                if(beta<=alpha) break;
            }
            return minEval;
        }
    }

    /*
     * Allows minimax to return 2 values
     */
    private class MinMaxData{
        int eval;
        Board board;
        MinMaxData(int eval, Board board){
            this.eval = eval;
            this.board = board;
        }
    }
}
