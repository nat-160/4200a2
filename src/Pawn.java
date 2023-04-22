import greenfoot.*;
import java.util.*;
public class Pawn extends Piece{
    Pawn(boolean player){
        super(player,"pawn");
    }
    
    Pawn(boolean player, boolean zaidi){
        super(player,zaidi ? "zaidipawn" : "pawn");
    }

    static HashSet<Move> getMoves(Board b, int x, int y){
        HashSet<Move> output = new HashSet<Move>();
        if(b.data[x][y]>0){
            if(b.data[x][y-1]==0){
                output.add(new Move(x, y, x, y-1));
                if(y==6 && b.data[x][4]==0)
                    output.add(new Move(x, y, x, 4));
            }
            if(in(x-1) && b.data[x-1][y-1]<0 || y==3 && b.enPassant == x-1)
                output.add(new Move(x, y, x-1, y-1));
            if(in(x+1) && b.data[x+1][y-1]<0 || y==3 && b.enPassant == x+1)
                output.add(new Move(x, y, x+1, y-1));
        } else {
            if(b.data[x][y+1]==0){
                output.add(new Move(x, y, x, y+1));
                if(y==1 && b.data[x][3]==0)
                    output.add(new Move(x, y, x, 3));
            }
            if(in(x-1) && b.data[x-1][y+1]>0 || y==4 && b.enPassant == x-1)
                output.add(new Move(x, y, x-1, y+1));
            if(in(x+1) && b.data[x+1][y+1]>0 || y==4 && b.enPassant == x+1)
                output.add(new Move(x, y, x+1, y+1));
        }
        return output;
    }
}
