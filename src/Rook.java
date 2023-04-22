import greenfoot.*;
import java.util.*;
public class Rook extends Piece{
    static int[] xDirs = {1, -1, 0, 0};
    static int[] yDirs = {0, 0, 1, -1};
    Rook(boolean player){
        super(player,"rook");
    }

    static HashSet<Move> getMoves(Board b, int x, int y){
        HashSet<Move> output = new HashSet<Move>();
        for(int a=0;a<4;a++){
            int xSign = xDirs[a];
            int ySign = yDirs[a];
            for(int i=1;i<8;i++){
                if(!in(x+i*xSign) || !in(y+i*ySign))
                    break;
                else if(b.data[x][y]*b.data[x+i*xSign][y+i*ySign]>0)
                    break;
                output.add(new Move(x, y, x+i*xSign, y+i*ySign));
                if(b.data[x][y]*b.data[x+i*xSign][y+i*ySign]<0)
                    break;
            }
        }
        return output;
    }
}
