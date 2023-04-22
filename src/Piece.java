import greenfoot.*;
import java.util.*;
public class Piece extends Actor{
    static final int PAWN = 1, KNIGHT = 3, BISHOP = 4, ROOK = 5, QUEEN = 9, KING = 999;
    boolean player;
    /**
     * Generic Piece class tied to a player
     * This class is not meant to be instantiated
     */
    Piece(boolean player, String name){
        if(player){
            setImage("white_"+name+".png");
        } else {
            setImage("black_"+name+".png");
        }
        this.player = player;
    }

    /**
     * Check if a number is inside bounds
     */
    static boolean in(int i){
        return 0 <= i && i <= 7;
    }

    static char toChar(int i){
        if(i==PAWN) return 'p';
        else if(i==-PAWN) return 'P';
        else if(i==KNIGHT) return 'k';
        else if(i==-KNIGHT) return 'K';
        else if(i==BISHOP) return 'b';
        else if(i==-BISHOP) return 'B';
        else if(i==ROOK) return 'r';
        else if(i==-ROOK) return 'R';
        else if(i==QUEEN) return 'q';
        else if(i==-QUEEN) return 'Q';
        else if(i==KING) return 'k';
        else if(i==-KING) return 'K';
        return ' ';
    }
}
