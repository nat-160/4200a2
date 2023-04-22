import greenfoot.*;
import java.util.*;
public class Knight extends Piece{
    Knight(boolean player){
        super(player,"knight");
    }

    static HashSet<Move> getMoves(Board b, int x, int y){
        HashSet<Move> output = new HashSet<Move>();
        for(int i=-2;i<=2;i++)
            for(int j=-2;j<=2;j++)
                if(in(x+i) && in(y+j) && Math.abs(i)+Math.abs(j)==3)
                    if(b.data[x][y]*b.data[x+i][y+j]<=0)
                        output.add(new Move(x, y, x+i, y+j));
        return output;
    }
}