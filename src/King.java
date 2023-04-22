import greenfoot.*;
import java.util.*;
public class King extends Piece{
    King(boolean player){
        super(player,"king");
    }

    static HashSet<Move> getMoves(Board b, int x, int y){
        HashSet<Move> output = new HashSet<Move>();
        for(int i=-1;i<=1;i++)
            for(int j=-1;j<=1;j++)
                if(in(x+i) && in(y+j) && i+j!=0)
                    if(b.data[x][y]*b.data[x+i][y+j]<=0)
                        output.add(new Move(x, y, x+i, y+j));
        if(b.data[x][y]>0){
            if(b.wLeftC && b.data[1][7]==0 && b.data[2][7]==0 && b.data[3][7]==0)
                output.add(new Move(4, 7, 1, 7));
            else if(b.wRightC && b.data[5][7]==0 && b.data[6][7]==0)
                output.add(new Move(4, 7, 6, 7));
        } else {
            if(b.bLeftC && b.data[1][0]==0 && b.data[2][0]==0 && b.data[3][0]==0)
                output.add(new Move(4, 0, 1, 0));
            else if(b.bRightC && b.data[5][0]==0 && b.data[6][0]==0)
                output.add(new Move(4, 0, 6, 0));
        }
        return output;
    }
}
